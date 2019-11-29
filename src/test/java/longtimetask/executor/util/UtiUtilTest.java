package longtimetask.executor.util;

import org.junit.Assert;
import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class UtiUtilTest {

    @Test
    public void test_GetUriParam() throws URISyntaxException {
        URI uri = new URI("test://path?key1=value1");
        Assert.assertEquals("value1", UriUtil.getParameter(uri, "key1"));
    }

    @Test
    public void test_UriParamNotExist() throws URISyntaxException {
        URI uri = new URI("test://path?key1=value1");
        Assert.assertNull(UriUtil.getParameter(uri, "key2"));
    }

    @Test
    public void test_ChangeUriHost() throws URISyntaxException {
        URI uri = new URI("test://target-host/path1/path2/path3?key=value&key1=value1&key2=value2#fragment");
        Assert.assertEquals("test://target-host111/path1/path2/path3?key=value&key1=value1&key2=value2#fragment",
                uri.toString().replaceFirst("://(.*)+", "://target-host111") + uri.getPath() +"?"
                        + uri.getQuery() + "#"+uri.getFragment());

    }
}
