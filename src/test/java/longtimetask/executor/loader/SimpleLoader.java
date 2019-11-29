package longtimetask.executor.loader;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.TaskExecutorTest;
import longtimetask.executor.task.PersistentTaskData;
import longtimetask.executor.task.SimpleTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.InetAddress;
import java.util.List;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Data
@Service
@Slf4j
public class SimpleLoader implements TaskLoader<SimpleTask> {

    private TaskRepository repository;

    @Autowired
    public void setRepository(TaskRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        FutureTask<List<SimpleTask>> task = new FutureTask<>(() -> {
            PersistentTaskData data = new PersistentTaskData();
            data.setTaskStatus("submitted");
            data.setTaskUrl("test://unknown?exceptionHandlerFactory=simple");
            repository.save(data);

            PersistentTaskData data1 = new PersistentTaskData();
            data1.setTaskStatus("submitted");
            data1.setTaskUrl("test://unknown?exceptionHandlerFactory=simple");
            repository.save(data1);

            PersistentTaskData data2 = new PersistentTaskData();
            data2.setTaskStatus("processing");
            data2.setTaskUrl("test://"+InetAddress.getLocalHost().getHostName()+"-1?exceptionHandlerFactory=simple");
            repository.save(data2);
            log.info("============================save tasks");
            return null;

        });

        TaskExecutorTest.QUEUE.offer(task);


    }

    @Override
    public List<SimpleTask> loadSubmittedTasks(final int n) {
        assert repository != null;

        FutureTask<List<SimpleTask>> futureTask = new FutureTask<>(() -> {
            List<SimpleTask> tasks = repository.findByTaskStatusOrderByCreatedDateAsc("submitted",
                    PageRequest.of(0, n)).get().map(taskData -> {
                SimpleTask simpleTask = new SimpleTask(taskData);
                simpleTask.setTaskRepository(repository);
                return simpleTask;
            }).collect(Collectors.toList());
            log.info("load tasks ===> {}", tasks);
            return tasks;
        });

        TaskExecutorTest.QUEUE.offer(futureTask);

        try {
            List<SimpleTask> results = futureTask.get();
            log.info("need load {} tasks, actual {}", n, results.size());
            return results;
        } catch (Throwable e) {
            throw new IllegalStateException("future get error", e);
        }
    }

    @Override
    public List<SimpleTask> loadAllProcessingTasks() {
        assert repository != null;
        FutureTask<List<SimpleTask>> futureTask = new FutureTask<>(() -> {
            List<SimpleTask> tasks = repository.findAllByTaskStatus("processing").stream().map(taskData -> {
                SimpleTask simpleTask = new SimpleTask(taskData);
                simpleTask.setTaskRepository(repository);
                return simpleTask;
            }).collect(Collectors.toList());
            log.info("load processing tasks ===> {}", tasks);
            return tasks;
        });

        TaskExecutorTest.QUEUE.offer(futureTask);
        try {
            List<SimpleTask> results = futureTask.get();
            log.info("need load processing , actual {}", results.size());
            return results;
        } catch (Throwable e) {
            throw new IllegalStateException("future get error", e);
        }
    }
}
