package com.stgo.security.monitor.util;

public class AppUtils {

    public static void sleep(
            int time) {
        try {
            Thread.sleep(time * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
