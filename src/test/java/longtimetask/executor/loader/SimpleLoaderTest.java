package longtimetask.executor.loader;

import longtimetask.executor.PersistentTaskTest;
import longtimetask.executor.task.PersistentTaskData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SimpleLoaderTest extends PersistentTaskTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private SimpleLoader simpleLoader;

    PersistentTaskData data, data1, data2;

    @Before
    public void setUp() {
        data = new PersistentTaskData();
        data.setTaskStatus("submitted");
        data.setTaskUrl("test://unknown?exceptionHandler=simple");
        taskRepository.save(data);

        data1 = new PersistentTaskData();
        data1.setTaskStatus("submitted");
        data1.setTaskUrl("test://unknown?exceptionHandler=simple");
        taskRepository.save(data1);

        data2 = new PersistentTaskData();
        data2.setTaskStatus("processing");
        data2.setTaskUrl("test://unknown?exceptionHandler=simple");
        taskRepository.save(data2);
    }

    @Test
    public void test_QuerySubmittedTasks() {
        assertThat(taskRepository.findAllByTaskStatus("processing")).contains(data2);

        assertThat(taskRepository.findByTaskStatusOrderByCreatedDateAsc("submitted", PageRequest.of(0, 10))
                .get().collect(Collectors.toList())).contains(data, data1);
    }

    @Test
    public void test_SimpleLoader() {
        assertThat(simpleLoader.loadAllProcessingTasks().get(0).getTaskData()).isEqualTo(data2);
    }

    @Test
    public void test_SpringBeanAndNewInstance() throws IllegalAccessException, InstantiationException {
        Assert.assertEquals(simpleLoader, SimpleLoader.class.newInstance());
    }

}
