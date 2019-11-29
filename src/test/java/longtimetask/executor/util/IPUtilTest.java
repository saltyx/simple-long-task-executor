package longtimetask.executor.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.net.UnknownHostException;

@Slf4j
public class IPUtilTest {

    @Test
    public void test() throws UnknownHostException {
        log.info("{}", IPUtil.getHostName());
    }

}
