package longtimetask.executor.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import longtimetask.executor.task.Task;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SimpleThreadPool implements ThreadPool {

    private ExecutorService taskExecutor;

    public SimpleThreadPool() {
        taskExecutor = new ThreadPoolExecutor(50, 50,
                0, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1),
                new ThreadFactoryBuilder().setNameFormat("pool-task-%d").build(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    @Override
    public void submit(Task task) {
        taskExecutor.submit(task);
    }

    @Override
    public void shutdown() {
        taskExecutor.shutdown();
    }

    @Override
    public int available(int currentRunningTask) {
        return 50 - currentRunningTask;
    }
}
