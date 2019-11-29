package longtimetask.executor.loader;

import longtimetask.executor.extensionloader.SPI;
import longtimetask.executor.task.Task;

import java.util.List;

/**
 * 任务状态分为 SUBMITTED -> PROCESSING -> FINISHED
 */
@SPI
public interface TaskLoader<T extends Task> {

    /**
     * 加载处于提交状态的任务
     * @param n 数量
     * @return 任务
     */
    List<T> loadSubmittedTasks(int n);

    /**
     * 加载所有处于处理中状态的任务
     * @return 任务
     */
    List<T> loadAllProcessingTasks();

}
