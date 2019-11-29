package longtimetask.executor;

import lombok.extern.slf4j.Slf4j;
import longtimetask.executor.config.TaskConfig;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest(classes = {SimpleConfiguration.class, TaskConfig.class})
@Slf4j
public class PersistentTaskTest {

}
