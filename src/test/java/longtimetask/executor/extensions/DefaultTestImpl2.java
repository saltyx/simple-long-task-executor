package longtimetask.executor.extensions;

public class DefaultTestImpl2 implements DefaultTest {
    @Override
    public String hi() {
        return DefaultTestImpl2.class.getName();
    }
}
