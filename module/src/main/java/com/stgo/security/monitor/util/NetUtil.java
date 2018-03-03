package com.stgo.security.monitor.util;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Vector;

import com.stgo.security.monitor.ProtectionOperator;
import com.stgo.security.monitor.SecurityLog;
import com.stgo.security.monitor.dao.DerbySecurityLogDao;

public class NetUtil extends Thread {

    public static String hostId = "";
    public static NetUtil instance = null;

    private static Vector<SecurityLog> securityLogs = new Vector<SecurityLog>();

    public static final String KEY_ALARM_PRF = "ALARM_";
    public static final String KEY_WARN_PRF = "WARNING_";

    public static final String KEY_SHOP_XML = "KEY_SHOP_XML";
    public static final String KEY_SHOP_LIST = "KEY_SHOP_LIST";
    public static final String KEY_SHOP_ID = "KEY_SHOP_ID";
    public static final String KEY_CUST_LAST_CHAR = "KEY_CUST_LAST_CHAR";

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String EXPORTER_HTTP_METHOD = "GET";

    /**
     * Entry to add status record. the message will be save into local database immediately and will be send to server
     * later in thread.
     * 
     * @param securityLog
     */
    public static void writeLog(
            String label,
            String msg) {
        // SecurityLog securityLog) {
        SecurityLog securityLog = new SecurityLog();
        securityLog.setLabel(label);
        securityLog.setComputer_name(NetUtil.hostId);
        securityLog.setDescription(msg);
        securityLog.setCreated(new Date());
        securityLogs.add(securityLog); // add into queue.
    }

    public static void writeLog(
            SecurityLog securityLog) {
        securityLogs.add(securityLog); // add into queue.
    }

    @Override
    public void run() {
        super.run();
        while (true) {
            // IMPORTANT: put it in top to make sure at the first run, wait enough time so (ProtectionOperator.serverURL
            // can have value).
            AppUtils.sleep(30);
            if (ProtectionOperator.serverURL == null) {
                continue;
            }

            // check the system settings every 5 seconds.
            ProtectionOperator.checkSystemSecurityStatus();
            // report to server (if there's any log not reported yet) every 5 seconds, and check if there's new version
            // by the way.
            String version = DBUtil.getVersion();
            String label = "";
            StringBuilder msg = new StringBuilder("");
            Date time = new Date();
            if (securityLogs.size() > 0) { // if has msg, then add the msg into the content to send.
                SecurityLog securityLog = securityLogs.get(0);
                label = securityLog.getLabel();
                msg.append(securityLog.getLabel()).append("Suspeciouse behavoir found in computer : ")
                        .append(securityLog.getComputer_name())
                        .append("_Some one has changed one of the configurations, detial: ")
                        .append(securityLog.getDescription().replace(" ", "%20"));

                time = securityLog.getCreated();
            }
            // when sending to stgo, label will be used to store internet connected computer id. and label info will be
            // added into msg (if the super administrator think it's improtant, will send out message).
            StringBuilder enrichedURL = new StringBuilder(ProtectionOperator.serverURL) //
                    .append("?version=").append(version) //
                    .append("&hostId=").append(hostId) //
                    .append("&label=").append(label) //
                    .append("&message=").append(msg)//
                    .append("&time=").append(String.valueOf(time.getTime()));//

            sendToSupperAdmin(enrichedURL.toString(), label, msg.toString(), time);
        }
    }

    private static void sendToSupperAdmin(
            String url,
            String label,
            String msg,
            Date time) {
        HttpURLConnection urlConnection = null;
        try {
            url = url.replace(" ", "%20");

            urlConnection = prepareConnection(url);
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (FileUtil.saveDownloadedFile(urlConnection.getInputStream())) {
                    DerbySecurityLogDao.writeLog(hostId, label, "zip file saved to temp folder", time, 0);
                    FileUtil.upgrade();
                }
                // report well, add msg into db with done as status.
                if (securityLogs.size() > 0) {
                    // update status.
                    DerbySecurityLogDao.writeLog(hostId, label, msg, time, 1);
                    securityLogs.remove(0);
                }

                checkDBUnSendRecord();
            } else {
                // not report well, leave the record with status as "not done" in db. remove it from msgs.
                if (securityLogs.size() > 0) {
                    DerbySecurityLogDao.writeLog(hostId, label, msg, time, 0);
                    securityLogs.remove(0);
                }
                DerbySecurityLogDao.writeLog(hostId, label,
                        "WARNING_connection to server not returning unexpected code :" + responseCode, time, 0);

            }
        } catch (Exception e) {
            DerbySecurityLogDao.writeLog(hostId, label,
                    "WARNING_unexpected exception when creating connection to server:" + e.getClass(), time, 0);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();// 使用完关闭TCP连接，释放资源
            }
        }
    }

    private static HttpURLConnection prepareConnection(
            String uri) throws Exception {
        HttpURLConnection urlConnection = (HttpURLConnection) new URL(uri).openConnection();
        // urlConnection.setConnectTimeout(3000);// 连接的超时时间
        // urlConnection.setUseCaches(false);// 不使用缓存
        // urlConnection.setFollowRedirects(false);是static函数，作用于所有的URLConnection对象。
        // urlConnection.setInstanceFollowRedirects(true);// 是成员函数，仅作用于当前函数,设置这个连接是否可以被重定向
        // urlConnection.setReadTimeout(3000);// 响应的超时时间
        urlConnection.setRequestMethod(EXPORTER_HTTP_METHOD);// 设置请求的方式
        urlConnection.addRequestProperty(HEADER_CONTENT_TYPE, "application/octet-stream");
        urlConnection.setDoInput(true);// 设置这个连接是否可以写入数据
        urlConnection.setDoOutput(true);// 设置这个连接是否可以输出数据
        urlConnection.connect();// 连接，从上述至此的配置必须要在connect之前完成，实际上它只是建立了一个与服务器的TCP连接
        return urlConnection;
    }

    // put db unSendRecord back into menory list(securityLogs).
    private static void checkDBUnSendRecord() {
        // TODO:
    }
}
