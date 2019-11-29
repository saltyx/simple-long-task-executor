package longtimetask.executor.extensions;

import longtimetask.executor.extensionloader.ExtensionLoader;

import longtimetask.executor.extensions.*;
import org.junit.Test;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.*;

public class ExtensionTaskLoaderTest {

    @Test
    public void test_normal() {
        Test1 test1 = ExtensionLoader.getExtensionLoader(Test1.class).getExtension("impl1");
        assertTrue(test1 instanceof Test1Impl);
        Test1 test2 = ExtensionLoader.getExtensionLoader(Test1.class).getExtension("impl2");
        assertTrue(test2 instanceof Test1Impl2);

    }

    @Test
    public void test_multiName() {
        Test2 test2 = ExtensionLoader.getExtensionLoader(Test2.class).getExtension("impl1");
        Test2 test2_1 = ExtensionLoader.getExtensionLoader(Test2.class).getExtension("impl2");
        assertTrue(test2 instanceof Test2Impl1);
        assertTrue(test2_1 instanceof Test2Impl1);
        assertEquals(test2, test2_1);
    }

    @Test
    public void test_NoSuchExtension() {
        try {
            ExtensionLoader.getExtensionLoader(Test1.class).getExtension("impl3");
            fail();
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), containsString("No such extension guice.test.extensions.Test1 by name impl3"));
        }
    }

    @Test
    public void test_DefaultExtension() {
        DefaultTest defaultTest = ExtensionLoader.getExtensionLoader(DefaultTest.class).getDefaultExtension();
        assertTrue(defaultTest instanceof DefaultTestImpl1);
    }

}
