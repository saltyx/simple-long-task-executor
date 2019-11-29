package longtimetask.executor;

import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.task.SimpleTask;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.FutureTask;

@Slf4j
public class TaskExecutorTest extends PersistentTaskTest {

    public static final BlockingQueue<FutureTask<List<SimpleTask>>> QUEUE = new ArrayBlockingQueue<>(100);

    private TaskExecutor executor;

    @Before
    public void setUp() {
        executor = new TaskExecutor("simple", "1");
        executor.start();
        executor.notifyTask();
    }

    @Test
    public void test() throws InterruptedException {
        while (true) {
            if (QUEUE.isEmpty()) {
                continue;
            }
            QUEUE.take().run();
        }
    }

    @After
    public void shutdown() {
        executor.shutdown();
    }

}
