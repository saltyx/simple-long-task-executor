package longtimetask.executor.extensionloader;

public class Holder<T> {

    private T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return this.value;
    }

}
