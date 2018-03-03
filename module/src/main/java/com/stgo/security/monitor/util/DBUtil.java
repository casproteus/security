package com.stgo.security.monitor.util;

import java.util.ArrayList;
import java.util.List;

import com.stgo.security.monitor.dao.DerbyOptionStatusDao;

public class DBUtil {

    public static String getUserName() {
        return "sharethegoodones";
    }

    public static String getSN() {
        return "stgo";
    }

    public static String getVersion() {
        return DerbyOptionStatusDao.getVersion();
    }

    /**
     * version is actually "LastUpdadteTime", It's the the Flag to determine if should upgrade. if time earlier than the
     * time on server, then should upgrade. on test server, the time is zip file upload time, when client side asking if
     * have new update, it will use local "last upgrade time", if not set yet, then return now. so first time upgrade
     * will down load the zip whenever it's uploaded. when it's down loaded, on client side will do upgrade, then save
     * the server side upload time(or file last modified data--if possible) as "last upgrade time"
     * 
     * @param time
     */
    public static void setVersion(
            String time) {
        DerbyOptionStatusDao.setVersion(time);
    }

    /**
     * 
     * @return
     */
    public static String getLastUpdateTime() {
        return "";
    }

    public static boolean isAutoRecognizeUsed() {
        return false;
    }

    public static boolean isReadOnlyUsed() {
        return true;
    }

    public static boolean isHidenRelaventFileUsed() {
        return false;
    }

    public static boolean isRunningAsServer() {
        return true;
    }

    public static List<String> getClientUrlStrs() {
        List<String> urls = new ArrayList<String>();
        return urls;
    }
}
