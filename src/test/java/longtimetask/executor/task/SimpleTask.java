package longtimetask.executor.task;

import lombok.Getter;
import lombok.Setter;
import longtimetask.executor.TaskExecutorTest;
import longtimetask.executor.loader.TaskRepository;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class SimpleTask extends Task {

    @Getter
    @Setter
    private PersistentTaskData taskData;

    private TaskRepository taskRepository;

    public void setTaskRepository(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public SimpleTask(PersistentTaskData taskData) {
        this.taskData = taskData;
        try {
            super.setParams(new URI(taskData.getTaskUrl()));
        } catch (URISyntaxException e) {
           throw new IllegalArgumentException("url syntax not correct");
        }
    }

    @Override
    protected void run(URI params) throws Throwable {
        TaskExecutorTest.QUEUE.offer(new FutureTask<>(() -> {
            taskData.setTaskStatus("processing");
            taskRepository.save(taskData);
            TimeUnit.SECONDS.sleep(5L);
            taskData.setTaskStatus("success");
            taskRepository.save(taskData);
            return null;
        }));

    }

    @Override
    protected void beforeTask() {

    }

    @Override
    protected void afterTask() {

    }
}
