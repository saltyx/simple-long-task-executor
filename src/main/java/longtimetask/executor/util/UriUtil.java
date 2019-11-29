package longtimetask.executor.util;

import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public class UriUtil {

    private final static Pattern PARAMETER_PATTERN = Pattern.compile("\\s*[&]+\\s*");

    public static String getParameter(URI uri, String expectedKey) {
        String query = uri.getQuery();
        String[] pairs = PARAMETER_PATTERN.split(query);
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String key = pair.substring(0, pair.indexOf('='));
                if (StringUtils.isBlank(key)) {
                    continue;
                }
                key = key.trim();
                if (StringUtils.equals(key, expectedKey)) {
                    return pair.substring(pair.indexOf('=')+1);
                }
            }
        }
        return null;
    }

    public static URI setHost(URI uri, String host) {
        String uriStr = uri.toString();
        uriStr = uriStr.replaceFirst("://(.*)+", "://" + host);
        if (StringUtils.isNotBlank(uri.getPath())) {
            uriStr = uriStr + uri.getPath();
        }

        if (StringUtils.isNotBlank(uri.getQuery())) {
            uriStr = uriStr + uri.getQuery();
        }

        if (StringUtils.isNotBlank(uri.getFragment())) {
            uriStr = uriStr + uri.getFragment();
        }

        try {
            return new URI(uriStr);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("uri syntax error", e);
        }
    }

}
