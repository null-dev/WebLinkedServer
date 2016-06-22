package xyz.nulldev.wls.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

/**
 * Project: X10
 * Created: 10/11/15
 * Author: nulldev
 */
public class StringUtils {
    public static boolean isEmptyOrNull(String s) {
        return s == null || s.isEmpty();
    }

    public static String encodeToURLParams(Map<String, String> params) {
        StringBuilder output = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> param : params.entrySet()) {
            if(first) {
                first = false;
                output.append("?");
            } else {
                output.append("&");
            }
            try {
                output.append(URLEncoder.encode(param.getKey(), "UTF-8"));
                output.append('=');
                output.append(URLEncoder.encode(param.getValue(), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return output.toString();
    }
}
