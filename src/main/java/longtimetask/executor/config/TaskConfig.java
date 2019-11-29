package longtimetask.executor.config;

import longtimetask.executor.util.SpringContextUtil;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = SpringContextUtil.class)
public class TaskConfig {

}
