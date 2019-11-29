package longtimetask.executor.extensionloader;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface SPI {

    String value() default "";
}
