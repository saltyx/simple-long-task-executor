package longtimetask.executor.task;

import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.exceptionhandler.ExceptionHandlerFactory;
import longtimetask.executor.extensionloader.ExtensionLoader;
import longtimetask.executor.loader.TaskLoader;
import longtimetask.executor.util.ArrayUtil;
import longtimetask.executor.util.UriUtil;

import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class RecoverTask extends InternalTask {

    private TaskLoader taskLoader;
    private String uniqueHost;

    public RecoverTask(TaskLoader taskLoader, String uniqueHost) {
        this.taskLoader = taskLoader;
        this.uniqueHost = uniqueHost;
    }

    @Override
    protected void run(URI params) throws Throwable {
        while (true) {
            recoverTasks();
        }
    }

    @SuppressWarnings("unchecked")
    protected void recoverTasks() {
        try {
            TimeUnit.SECONDS.sleep(5L);
            List<Task> tasks = taskLoader.loadAllProcessingTasks();
            if (ArrayUtil.isNotEmpty(tasks)) {
                for (Task task : tasks) {
                    // 排除本机正在运行的程序 和 不是本机运行的
                    if (Task.containProcessingTask(task) || !task.equalToHost(uniqueHost)) {
                        continue;
                    }
                    if (UriUtil.getParameter(task.getParams(), "exceptionHandlerFactory") == null) {
                        log.error("recover failed. can not find exception handler for " + task);
                    } else {
                        ExtensionLoader.getExtensionLoader(ExceptionHandlerFactory.class)
                                .getExtension(UriUtil.getParameter(task.getParams(), "exceptionHandlerFactory"))
                                .getExceptionHandler(task.getParams()).recover(task);
                    }
                }
            }
        } catch (Throwable t) {
            log.error("recover task error", t);
        }

    }

    @Override
    protected void beforeTask() {
        Thread.currentThread().setName("recover-task");
        log.info("recover task starting");
    }

    @Override
    protected void afterTask() {

    }
}
