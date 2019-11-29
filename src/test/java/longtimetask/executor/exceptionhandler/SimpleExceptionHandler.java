package longtimetask.executor.exceptionhandler;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.TaskExecutorTest;
import longtimetask.executor.loader.TaskRepository;
import longtimetask.executor.task.SimpleTask;

import java.util.concurrent.FutureTask;

@Slf4j
public class SimpleExceptionHandler implements ExceptionHandler<SimpleTask> {

    @Getter
    @Setter
    private TaskRepository taskRepository;

    public SimpleExceptionHandler(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public void handleException(final SimpleTask task, Throwable t) {
        TaskExecutorTest.QUEUE.offer(new FutureTask<>(() -> {
            task.getTaskData().setTaskStatus("error");
            taskRepository.save(task.getTaskData());
            return null;
        }));
    }

    @Override
    public void recover(final SimpleTask task) {
        TaskExecutorTest.QUEUE.offer(new FutureTask<>(() -> {
            log.info("recover {} to submitted", task);
            task.getTaskData().setTaskStatus("submitted");
            taskRepository.save(task.getTaskData());
            return null;
        }));
    }
}
