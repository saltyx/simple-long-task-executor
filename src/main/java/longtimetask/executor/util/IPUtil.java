package longtimetask.executor.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class IPUtil {
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            throw new IllegalStateException("UnknownHostName");
        }
    }
}
