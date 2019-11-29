package longtimetask.executor.extensions;

public class DefaultTestImpl1 implements DefaultTest {
    @Override
    public String hi() {
        return DefaultTestImpl1.class.getName();
    }
}
