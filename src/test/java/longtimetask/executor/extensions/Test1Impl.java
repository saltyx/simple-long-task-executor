package longtimetask.executor.extensions;

public class Test1Impl implements Test1 {

    @Override
    public String hi() {
        return Test1Impl.class.getName();
    }
}
