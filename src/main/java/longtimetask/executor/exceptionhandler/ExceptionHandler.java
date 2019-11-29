package longtimetask.executor.exceptionhandler;

import longtimetask.executor.task.Task;

/**
 * 异常处理器
 */
public interface ExceptionHandler<T extends Task> {
    /**
     * 处理异常
     * @param t 异常
     */
    void handleException(T task, Throwable t);

    /**
     * 恢复任务
     * @param task 任务
     */
    void recover(T task);
}
