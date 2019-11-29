package longtimetask.executor.extensions;

public class Test2Impl1 implements Test2 {
    @Override
    public String hi() {
        return Test2.class.getName();
    }
}
