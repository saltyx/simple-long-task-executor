package longtimetask.executor.task;

import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.loader.TaskLoader;
import longtimetask.executor.thread.ThreadPool;
import longtimetask.executor.util.ArrayUtil;

import java.net.URI;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
public class LoaderTask extends InternalTask {

    private final Object signal;
    private TaskLoader taskLoader;
    private String uniqueHost;
    private ThreadPool threadPool;

    public LoaderTask(Object signal, TaskLoader taskLoader, String uniqueHost, ThreadPool threadPool) {
        this.signal = signal;
        this.taskLoader = taskLoader;
        this.uniqueHost = uniqueHost;
        this.threadPool = threadPool;
    }

    @Override
    protected void run(URI params) throws Throwable {
        while (true) {
            loadTask();
        }
    }

    @SuppressWarnings("unchecked")
    protected void loadTask() {
        synchronized (signal) {
            try {
                signal.wait(5000L);
                List<Task> tasks = taskLoader.loadSubmittedTasks(threadPool.available(Task.getRunningTaskSize()));
                if (ArrayUtil.isNotEmpty(tasks)) {
                    for (Task task : tasks) {
                        task.setTaskHost(uniqueHost);
                        threadPool.submit(task);
                    }
                }
            } catch (RejectedExecutionException e) {
                log.warn("task has been rejected. wait for next execution： " + e.getMessage());
            } catch (Throwable t) {
                log.error("loader task throw an exception", t);
            }
        }
    }

    @Override
    protected void beforeTask() {
        Thread.currentThread().setName("loader-task");
        log.info("loader task starting");
    }

    @Override
    protected void afterTask() {

    }
}
