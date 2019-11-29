package longtimetask.executor.extensions;

import longtimetask.executor.extensionloader.SPI;

@SPI("impl1")
public interface DefaultTest {

    String hi();

}
