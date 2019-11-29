package longtimetask.executor;

import longtimetask.executor.exceptionhandler.SimpleExceptionHandlerFactory;
import longtimetask.executor.loader.SimpleLoader;
import longtimetask.executor.loader.TaskRepository;
import longtimetask.executor.task.PersistentTaskData;
import longtimetask.executor.task.SimpleTask;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@ComponentScan(basePackageClasses = {PersistentTaskData.class, TaskRepository.class,
        SimpleTask.class, SimpleLoader.class, SimpleExceptionHandlerFactory.class})
@EnableAutoConfiguration
@EnableJpaAuditing
public class SimpleConfiguration {

}