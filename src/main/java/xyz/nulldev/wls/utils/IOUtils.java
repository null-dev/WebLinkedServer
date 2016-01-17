package xyz.nulldev.wls.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class IOUtils {
    static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    static HashMap<String, String> cachedResource = new HashMap<>();

    public static String getResourceAsString(String resource) throws IOException {
        if(!resource.startsWith("/")) {
            resource = "/" + resource;
        }
        //Try cache
        String output = cachedResource.get(resource);
        //Check if cache hit
        if(output == null) {
            InputStream stream = IOUtils.class.getResourceAsStream(resource);
            try (final BufferedReader br
                         = new BufferedReader(new InputStreamReader(stream, DEFAULT_CHARSET))) {
                output = br.lines().collect(Collectors.joining("\n"));
            }
            //Cache resource
            cachedResource.put(resource, output);
        }
        return output;
    }
}
