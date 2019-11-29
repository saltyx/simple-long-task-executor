package longtimetask.executor.thread;

import longtimetask.executor.task.Task;
import longtimetask.executor.extensionloader.SPI;

/**
 * 分配线程数至少大于2，用于任务加载线程和恢复任务线程
 */
@SPI("simple")
public interface ThreadPool {

    /**
     * 提交任务
     * @param task 任务
     */
    void submit(Task task);

    /**
     * 关闭线程池
     */
    void shutdown();

    /**
     * 可用线程数
     * @param currentRunningTask 当前正在运行的任务。注意此参数并不准确
     * @return 可用线程数
     */
    int available(int currentRunningTask);

}
