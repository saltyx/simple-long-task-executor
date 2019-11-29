package longtimetask.executor.extensionloader;

import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.util.SpringContextUtil;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

@Slf4j
public class ExtensionLoader<T> {

    private final static String EXTENSION_DIRECTORY = "META-INF/extensions/";
    private final Class<?> type;
    private final static ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();
    private final static ConcurrentMap<Class<?>, Object> EXTENSION_INSTANCES = new ConcurrentHashMap<>();
    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");

    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>();
    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();
    private final ConcurrentMap<Class<?>, String> cacheName = new ConcurrentHashMap<>();
    private Map<String, IllegalStateException> exceptions = new ConcurrentHashMap<>();
    private String cachedDefaultName;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    @SuppressWarnings("unchecked")
    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        if (!type.isInterface()) {
            throw new IllegalArgumentException("type should be interface");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("type should be annotated with " + SPI.class.getSimpleName());
        }

        ExtensionLoader<T> extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (extensionLoader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            extensionLoader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return extensionLoader;

    }

    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("extension name can not be blank");
        }
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            holder = new Holder<>();
            cachedInstances.putIfAbsent(name, holder);
        }
        if (holder.get() == null) {
            // double check single instance
            synchronized (holder) {
                if (holder.get() == null) {
                    // create new instance
                    holder.set(createExtension(name));
                }
            }
        }
        return (T) holder.get();
    }

    public T getDefaultExtension() {
        getExtensionClasses();
        if (StringUtils.isBlank(cachedDefaultName)) {
            return null;
        }
        return getExtension(cachedDefaultName);
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw findException(name);
        }
        T instance = (T) EXTENSION_INSTANCES.get(clazz);
        try {
            if (instance == null) {
                EXTENSION_INSTANCES.putIfAbsent(clazz, SpringContextUtil.getBean(clazz) == null ? clazz.newInstance()
                        : SpringContextUtil.getBean(clazz));
                instance = (T) EXTENSION_INSTANCES.get(clazz);
            }

            return instance;
        } catch (Throwable t) {
            log.error("", t);
            throw new IllegalStateException("Extension instance (name:" + name + ", class:" + type + ") could not be" +
                    " instanced", t);
        }
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classMap = cachedClasses.get();
        if (classMap == null) {
            synchronized (cachedClasses) {
                classMap = cachedClasses.get();
                if (classMap == null) {
                    // load classes
                    classMap = loadExtensionClasses();
                    cachedClasses.set(classMap);
                }
            }
        }
        return classMap;
    }

    private Map<String, Class<?>> loadExtensionClasses() {
        cacheDefaultExtensionName();

        Map<String, Class<?>> extensionClasses = new HashMap<>();
        loadDirectory(extensionClasses, type.getName());
        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String type) {
        String filename = ExtensionLoader.EXTENSION_DIRECTORY + type;
        ClassLoader classLoader = findClassLoader();
        try {
            Enumeration<URL> urls;
            if (classLoader != null) {
                urls = classLoader.getResources(filename);
            } else {
                urls = ClassLoader.getSystemResources(filename);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL url = urls.nextElement();
                    loadResource(extensionClasses, classLoader, url);
                }
            }
        } catch (Throwable t) {
            log.error("Exception occurred when loading extension class (interface: " +
                    type + ", description file: " + filename + ").", t);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceURL) {
        try {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) {
                        line = line.substring(0, ci);
                    }
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            if (line.length() > 0) {
                                loadClass(extensionClasses, resourceURL, Class.forName(line, true, classLoader), name);
                            }
                        } catch (Throwable t) {
                            IllegalStateException e = new IllegalStateException("Failed to load extension class (interface: " + type + ", class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
                            exceptions.put(line, e);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            log.error("Exception occurred when loading extension class (interface: " +
                    type + ", class file: " + resourceURL + ") in " + resourceURL, t);
        }
    }

    private void loadClass(Map<String, Class<?>> extensionClasses, java.net.URL resourceURL, Class<?> clazz, String name) throws NoSuchMethodException {
        if (!type.isAssignableFrom(clazz)) {
            throw new IllegalStateException("Error occurred when loading extension class (interface: " +
                    type + ", class line: " + clazz.getName() + "), class "
                    + clazz.getName() + " is not subtype of interface.");
        }

        clazz.getConstructor();
        if (StringUtils.isEmpty(name)) {
            if (name.length() == 0) {
                throw new IllegalStateException("No such extension name for the class " + clazz.getName() + " in the config " + resourceURL);
            }
        }

        String[] names = NAME_SEPARATOR.split(name);
        if (names != null && names.length > 0) {
            for (String n : names) {
                if (cacheName.get(clazz) == null) {
                    cacheName.putIfAbsent(clazz, n);
                }
                cacheName.putIfAbsent(clazz, n);
                saveInExtensionClass(extensionClasses, clazz, n);
            }
        }
    }

    /**
     * put clazz in extensionClasses
     */
    private void saveInExtensionClass(Map<String, Class<?>> extensionClasses, Class<?> clazz, String name) {
        Class<?> c = extensionClasses.get(name);
        if (c == null) {
            extensionClasses.put(name, clazz);
        } else if (c != clazz) {
            String duplicateMsg = "Duplicate extension " + type.getName() + " name " + name + " on " + c.getName() + " and " + clazz.getName();
            log.error(duplicateMsg);
            throw new IllegalStateException(duplicateMsg);
        }
    }

    private IllegalStateException findException(String name) {
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if (entry.getKey().toLowerCase().contains(name.toLowerCase())) {
                return entry.getValue();
            }
        }
        StringBuilder buf = new StringBuilder("No such extension " + type.getName() + " by name " + name);

        int i = 1;
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if (i == 1) {
                buf.append(", possible causes: ");
            }

            buf.append("\r\n(");
            buf.append(i++);
            buf.append(") ");
            buf.append(entry.getKey());
            buf.append(":\r\n");
            buf.append(entry.getValue());
        }
        return new IllegalStateException(buf.toString());
    }

    private static ClassLoader findClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader == null) {
            classLoader = ExtensionLoader.class.getClassLoader();
            if (classLoader == null) {
                classLoader = ClassLoader.getSystemClassLoader();
            }
        }
        return classLoader;
    }

    private void cacheDefaultExtensionName() {
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (defaultAnnotation == null) {
            return;
        }

        String value = defaultAnnotation.value();
        if ((value = value.trim()).length() > 0) {
            String[] names = NAME_SEPARATOR.split(value);
            if (names.length > 1) {
                throw new IllegalStateException("More than 1 default extension name on extension " + type.getName()
                        + ": " + Arrays.toString(names));
            }
            if (names.length == 1) {
                cachedDefaultName = names[0];
            }
        }
    }

}
