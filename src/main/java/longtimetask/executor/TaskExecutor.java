package longtimetask.executor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.extensionloader.ExtensionLoader;
import longtimetask.executor.loader.TaskLoader;
import longtimetask.executor.task.LoaderTask;
import longtimetask.executor.task.RecoverTask;
import longtimetask.executor.thread.ThreadPool;
import longtimetask.executor.util.IPUtil;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

@Slf4j
public class TaskExecutor implements InitializingBean, DisposableBean {

    private TaskLoader taskLoader;
    private ThreadPool threadPool;
    private final Object object = new Object();

    @Getter
    private final String uniqueHost;

    public TaskExecutor(String thread, String loader, String uniqueId) {
        this.taskLoader = ExtensionLoader.getExtensionLoader(TaskLoader.class).getExtension(loader);
        this.threadPool = ExtensionLoader.getExtensionLoader(ThreadPool.class).getExtension(thread);
        this.uniqueHost = IPUtil.getHostName() + ":" + uniqueId;
    }

    public TaskExecutor(String loader, String uniqueId) {
        this.taskLoader = ExtensionLoader.getExtensionLoader(TaskLoader.class).getExtension(loader);
        this.threadPool = ExtensionLoader.getExtensionLoader(ThreadPool.class).getDefaultExtension();
        this.uniqueHost = IPUtil.getHostName() + "-" + uniqueId;
    }

    public void start() {
        final LoaderTask loaderTask = new LoaderTask(object, taskLoader, uniqueHost, threadPool);
        final RecoverTask recoverTask = new RecoverTask(taskLoader, uniqueHost);
        this.threadPool.submit(recoverTask);
        this.threadPool.submit(loaderTask);
    }

    public void notifyTask() {
        synchronized (object) {
            object.notifyAll();
        }
    }

    public void shutdown() {
        if (threadPool != null) {
            threadPool.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.start();
    }

    @Override
    public void destroy() throws Exception {
        this.shutdown();
    }
}
