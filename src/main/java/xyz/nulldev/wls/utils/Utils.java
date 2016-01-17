package xyz.nulldev.wls.utils;

/**
 * Project: WebLinkedServer
 * Created: 16/01/16
 * Author: nulldev
 */
public class Utils {

    static final String[] SIZE_UNITS
            = {"B", "K", "M", "G", "T"};
    public static String formatSize(long size) {
        int i;
        for(i = 0; size >= 1024 && i < 4; i++) {
            size /= 1024;
        }
        return (Math.round(size * 100) / 100) + " " + SIZE_UNITS[i];
    }
}
