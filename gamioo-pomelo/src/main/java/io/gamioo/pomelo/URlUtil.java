package io.gamioo.pomelo;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URLEncoder;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author Allen Jiang
 */
public class URlUtil {
    private static final Logger logger = LogManager.getLogger(URlUtil.class);

    public static String buildUrl(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        sb.append(url);
        for (Entry<String, Object> entry : params.entrySet()) {
            if (i == 0) {
                sb.append("?");
            } else {
                sb.append("&");
            }
            i++;
            try {
                Object value = entry.getValue();
                if (value != null && StringUtils.isNotEmpty(String.valueOf(value))) {
                    sb.append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(value), "UTF-8"));
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
        return sb.toString();
    }
}
