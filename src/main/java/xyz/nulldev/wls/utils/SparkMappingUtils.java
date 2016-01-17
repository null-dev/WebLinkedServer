package xyz.nulldev.wls.utils;

import spark.Route;
import spark.Spark;

/**
 * Project: X10
 * Created: 10/11/15
 * Author: nulldev
 */
public class SparkMappingUtils {
    public static void lenientGetMap(String url, Route route) {
        String withoutSlash = url;
        //Trim off slash if necessary
        if(url.endsWith("/")) {
            withoutSlash = url.substring(0, url.length()-1);
        }
        Spark.get(withoutSlash, route);
        Spark.get(withoutSlash+"/", route);
    }

    public static void lenientPostMap(String url, Route route) {
        String withoutSlash = url;
        //Trim off slash if necessary
        if(url.endsWith("/")) {
            withoutSlash = url.substring(0, url.length()-1);
        }
        Spark.post(withoutSlash, route);
        Spark.post(withoutSlash+"/", route);
    }
}
