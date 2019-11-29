package longtimetask.executor.task;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.exceptionhandler.ExceptionHandlerFactory;
import longtimetask.executor.extensionloader.ExtensionLoader;
import longtimetask.executor.util.UriUtil;
import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
@ToString
@EqualsAndHashCode
public abstract class Task implements Runnable {

    private final static LinkedBlockingQueue<Task> RUNNING_TASKS = new LinkedBlockingQueue<>();

    @Getter
    @Setter
    protected URI params;

    protected abstract void run(URI params) throws Throwable;
    protected abstract void beforeTask();
    protected abstract void afterTask();

    @SuppressWarnings("unchecked")
    @Override
    public void run() {
        try {
            beforeTask();
            RUNNING_TASKS.add(this);
            run(params);
            afterTask();
        } catch (Throwable t) {
            log.error("exception occurs ", t);
            if (StringUtils.isBlank(UriUtil.getParameter(params, "exceptionHandlerFactory"))) {
                ExtensionLoader.getExtensionLoader(ExceptionHandlerFactory.class)
                        .getExtension(UriUtil.getParameter(params, "exceptionHandlerFactory"))
                        .getExceptionHandler(params).handleException(this, t);
            } else {
                throw new IllegalStateException("can not find exception handler for " + this, t);
            }
        } finally {
            RUNNING_TASKS.remove(this);
        }
    }

    public void setTaskHost(String uniqueHost) {
        params = UriUtil.setHost(params, uniqueHost);
    }

    public boolean equalToHost(String targetHost) {
        return StringUtils.equals(params.getHost(), targetHost);
    }

    public static int getRunningTaskSize() {
        return RUNNING_TASKS.size();
    }

    public static boolean containProcessingTask(Task task) {
        if (task == null) {
            return false;
        }
        return RUNNING_TASKS.contains(task);
    }
}
