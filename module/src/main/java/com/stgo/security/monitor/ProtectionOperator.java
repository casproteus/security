package com.stgo.security.monitor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.StreamingOutput;

import com.stgo.security.monitor.dao.DerbyOptionStatusDao;
import com.stgo.security.monitor.dao.DerbySecurityLogDao;
import com.stgo.security.monitor.util.DBUtil;
import com.stgo.security.monitor.util.FileUtil;
import com.stgo.security.monitor.util.NetUtil;
import com.stgo.security.monitor.util.WinRegistry;
import com.stgo.security.monitor.util.WinUtil;

public class ProtectionOperator {

    // initialized in xml.
    private DerbySecurityLogDao derbySecurityLogDao;
    private DerbyOptionStatusDao derbyOptionStatusDao;
    public static String unzipFolderPath;
    public static String tempFolderPath;
    public static String serverURL;

    private static int AUTO_RECOGNIZE = 0x00000001;
    private static int READ_ONLY = 0x00000010;
    private static int HIDDEN_FILES = 0x00000100;

    /**
     * constructor when this class initialized, the thread start to work.
     */
    public ProtectionOperator() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            NetUtil.hostId = inetAddress.getHostAddress() + "_" + inetAddress.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if (NetUtil.instance == null) { // make sure the server started.
            NetUtil.instance = new NetUtil();
            NetUtil.instance.start();
        }
    }

    // ================================acting as server, response client's report====================
    /**
     * Processing client error log.
     * 
     * @param version
     * @param label
     * @param msg
     * @param time
     * @return
     */
    public StreamingOutput logStatus(
            final String version,
            final String hostID,
            final String label,
            final String msg,
            String time) {

        // if not all good, then log the issue into db. and then send to stgo)
        if (msg != null && msg.length() > 0) {
            // save to db.
            Date createdDate = new Date(Long.valueOf(time));
            DerbySecurityLogDao.writeLog(hostID, label, msg, createdDate, 0);

            // continue report to stgo. stgo will decide email admin or remote control the machine.
            SecurityLog securityLog = new SecurityLog();
            securityLog.setLabel(String.valueOf(version));
            securityLog.setComputer_name(label);
            securityLog.setDescription(msg);
            securityLog.setCreated(createdDate);

            NetUtil.writeLog(securityLog);
        }

        // if the version are different with server, then upgrade.
        return String.valueOf(DBUtil.getVersion()).equals(version) ? null : FileUtil.convertFileToStreammingOutput(
                tempFolderPath, "system_security_monitor.zip");
    }

    // ================================acting as client, response server's command====================
    // when API receive command from server to update configuration , will reach here.
    public void updateOptionStatus(
            int status) {
        // TODO:
    }

    // When API receive command from server to report current configuration, will reach here.
    public List<OptionStatus> returnCurrentOptionStatus() {
        List<OptionStatus> optionStatus = DerbyOptionStatusDao.queryForList("Select * from OptionStatus", null);
        return optionStatus;
    }

    // When API receive command from server to stop current configuration, will reach here.
    // timeLimit == -1 means do not set up any more. while it will still set up when next time start up computer. or
    // manually call setup.
    public void cancelUSBWriteProtection(
            String name,
            String sn,
            int timeLimit,
            int function_id) {
        // verify identity
        if (!DBUtil.getUserName().equals(name) || !DBUtil.getSN().equals(sn)) {
            NetUtil.writeLog(NetUtil.KEY_WARN_PRF,
                    "tried to cancelUSBWriteProtection with wrong username and password.");
            return;
        }

        if ((function_id & AUTO_RECOGNIZE) != 0 && DBUtil.isAutoRecognizeUsed()) {
            closeUSBKeyAutoRecognize(false);
        }
        if ((function_id & READ_ONLY) != 0 && DBUtil.isReadOnlyUsed()) {
            makeUSBReadOnly(false);
        }
        if ((function_id & HIDDEN_FILES) != 0 && DBUtil.isHidenRelaventFileUsed()) {
            hideRelaventFiles(false);
        }

        // passed, start to cancel
        try {
            String value = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, // HKEY
                    "Software\\Microsoft\\Windows\\CurrentVersion\\Policies", "DisableRegistryTools");

            // waiting for operation
            if (timeLimit == 0) { // by default, reset after 1 minuts.
                timeLimit = 1;
            } else if (timeLimit < 0) { // -1 means do not set up any more. it will set up when next time start up
                // computer.
                return;
            }

            Thread.sleep(timeLimit * 60 * 1000);

        } catch (Exception e) {
            setupUSBWriteProtection();
        }
    }

    // When API receive command from server to upload the non-uploaded logs, will reach here.
    public List<SecurityLog> checkSecurityLog() {
        List<SecurityLog> optionStatus = derbySecurityLogDao.queryForList("Select * from SecurityLog", null, null);
        return optionStatus;
    }

    // =======================acting as coworker of NetUtil, check system security
    // status=================================
    public static void checkSystemSecurityStatus() {
        checkRegister();
        checkManagedApp();
    }

    private static void checkRegister() {
        setupUSBWriteProtection();
        checkIfRegisterOpenAble();
    }

    private static void checkManagedApp() {
        // BarFrame.main(null);
    }

    // ----------------------first type of check----------------------
    private static void setupUSBWriteProtection() {
        if (DBUtil.isAutoRecognizeUsed()) {
            closeUSBKeyAutoRecognize(true);
        }
        if (DBUtil.isReadOnlyUsed()) {
            makeUSBReadOnly(true);
        }
        if (DBUtil.isHidenRelaventFileUsed()) {
            hideRelaventFiles(true);
        }
    }

    private static void closeUSBKeyAutoRecognize(
            boolean flag) {
        try {
            // check current status
            String newValue =
                    WinUtil.getRegKey("Start", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\USBSTOR");
            if (!newValue.substring(3).equals(flag ? "4" : "3")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Current USBSTOR is " + newValue + ". it need to be set to "
                        + (flag ? "4" : "3"));
            }

            // set to desired value
            WinUtil.setValueForRegKey("HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\USBSTOR", "Start",
                    flag ? "4" : "3", "REG_DWORD");

            // check seeting resutl
            newValue =
                    WinUtil.getRegKey("Start", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Services\\USBSTOR");
            if (!newValue.substring(3).equals(flag ? "4" : "3")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Failed to set USBSTOR to " + (flag ? "4" : "3"));
            }
        } catch (Exception e) {
            NetUtil.writeLog(NetUtil.KEY_WARN_PRF, e.getClass().getName());
        }
    }

    private static void makeUSBReadOnly(
            boolean flag) {
        try {
            // check current status
            String newValue =
                    WinUtil.getRegKey("WriteProtect", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\StorageDevicePolicies");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_ALARM_PRF, "Current WriteProtect is " + newValue
                        + ". it need to be set to " + (flag ? "1" : "0"));
            }

            // set to desired value
            WinUtil.setValueForRegKey("HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\StorageDevicePolicies",
                    "WriteProtect", flag ? "1" : "0", "REG_DWORD");

            // check seeting resutl
            newValue =
                    WinUtil.getRegKey("WriteProtect", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\StorageDevicePolicies");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_ALARM_PRF, "Failed to set WriteProtect to " + (flag ? "1" : "0"));
            }
        } catch (Exception e) {
            NetUtil.writeLog(NetUtil.KEY_ALARM_PRF, e.getClass().getName());
        }
    }

    private static void hideRelaventFiles(
            boolean flag) {
        try {
            if (flag) {
                // @TODO: got exception when running the script in this way, it's ok when I try in dos. Will try use
                // java code to do it if it's necessary.
                WinUtil.execDosCommand("copy c:/windows/inf/usbstor.inf c:/windows/usbstor.inf /y >nul");
                WinUtil.execDosCommand("copy c:\\windows\\inf\\usbstor.pnf c:\\windows\\usbstor.pnf /y >nul");
                WinUtil.execDosCommand("del c:\\windows\\inf\\usbstor.pnf /q/f >nul");
                WinUtil.execDosCommand("del c:\\windows\\inf\\usbstor.inf /q/f >nul");
            } else {
                WinUtil.execDosCommand("copy %Windir%\\usbstor.inf %Windir%\\inf\\usbstor.inf /y >nul");
                WinUtil.execDosCommand("copy %Windir%\\usbstor.pnf %Windir%\\inf\\usbstor.pnf /y >nul");
            }
        } catch (Exception e) {
            NetUtil.writeLog(NetUtil.KEY_WARN_PRF, e.getClass().getName());
        }
    }

    private void forbiddenEnterSafeMode(
            boolean flag) {
        // forbidden user to enter safe mode
        try {
            String newValue =
                    WinUtil.getRegKey("Minimal", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\SafeBoot");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Current Minimal is " + newValue + ". it need to be set to "
                        + (flag ? "1" : "0"));
            }
            newValue =
                    WinUtil.getRegKey("Network", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\SafeBoot");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Current Network is " + newValue + ". it need to be set to "
                        + (flag ? "1" : "0"));
            }

            WinUtil.setValueForRegKey("HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\SafeBoot", "Minimal",
                    flag ? "1" : "0", "REG_DWORD");
            WinUtil.setValueForRegKey("HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\SafeBoot", "Network",
                    flag ? "1" : "0", "REG_DWORD");

            newValue =
                    WinUtil.getRegKey("Minimal", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\SafeBoot");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Failed to set Minimal to " + (flag ? "1" : "0"));
            }
            newValue =
                    WinUtil.getRegKey("Network", "REG_DWORD",
                            "HKEY_LOCAL_MACHINE\\SYSTEM\\CurrentControlSet\\Control\\SafeBoot");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Failed to set Network to " + (flag ? "1" : "0"));
            }
        } catch (Exception e) {
            NetUtil.writeLog(NetUtil.KEY_WARN_PRF, e.getClass().getName());
        }
    }

    private void forbiddenEnterRegEditor(
            boolean flag) {
        // anti use regedit to change the settings back. @NOTE: when this command run, no reg command can work
        // anymore.
        try {
            // check current status
            String newValue =
                    WinUtil.getRegKey("DisableRegistryTools", "REG_DWORD",
                            "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Current DisableRegistryTools is " + newValue
                        + ". it need to be set to " + (flag ? "1" : "0"));
            }

            // set to desired value
            WinUtil.setValueForRegKey("HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies",
                    "DisableRegistryTools", flag ? "1" : "0", "REG_DWORD");

            // check seeting resutl
            newValue =
                    WinUtil.getRegKey("DisableRegistryTools", "REG_DWORD",
                            "HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Policies");
            if (!newValue.substring(3).equals(flag ? "1" : "0")) {
                NetUtil.writeLog(NetUtil.KEY_WARN_PRF, "Failed to set DisableRegistryTools to " + (flag ? "1" : "0"));
            }
        } catch (Exception e) {
            NetUtil.writeLog(NetUtil.KEY_WARN_PRF, e.getClass().getName());
        }
    }

    // ------------------------second type of check--------------------------------
    private static void checkIfRegisterOpenAble() {

    }

    // ================================getters and setters==========================
    /**
     * Returns the <code>derbySecurityLogDao</code> value.
     *
     * @return The <code>derbySecurityLogDao</code>.
     */
    public DerbySecurityLogDao getDerbySecurityLogDao() {
        return derbySecurityLogDao;
    }

    /**
     * Sets the <code>derbySecurityLogDao</code> value.
     *
     * @param derbySecurityLogDao
     *            The <code>derbySecurityLogDao</code> to set.
     */
    public void setDerbySecurityLogDao(
            DerbySecurityLogDao derbySecurityLogDao) {
        this.derbySecurityLogDao = derbySecurityLogDao;
    }

    /**
     * Returns the <code>derbyOptionStatusDao</code> value.
     *
     * @return The <code>derbyOptionStatusDao</code>.
     */
    public DerbyOptionStatusDao getDerbyOptionStatusDao() {
        return derbyOptionStatusDao;
    }

    /**
     * Sets the <code>derbyOptionStatusDao</code> value.
     *
     * @param derbyOptionStatusDao
     *            The <code>derbyOptionStatusDao</code> to set.
     */
    public void setDerbyOptionStatusDao(
            DerbyOptionStatusDao derbyOptionStatusDao) {
        this.derbyOptionStatusDao = derbyOptionStatusDao;
    }

    /**
     * Returns the <code>unzipFolderPath</code> value.
     *
     * @return The <code>unzipFolderPath</code>.
     */
    public String getUnzipFolderPath() {
        return unzipFolderPath;
    }

    /**
     * Sets the <code>unzipFolderPath</code> value.
     *
     * @param unzipFolderPath
     *            The <code>unzipFolderPath</code> to set.
     */
    public void setUnzipFolderPath(
            String unzipFolderPath) {
        ProtectionOperator.unzipFolderPath = unzipFolderPath;
    }

    /**
     * Returns the <code>tempFolderPath</code> value.
     *
     * @return The <code>tempFolderPath</code>.
     */
    public String getTempFolderPath() {
        return tempFolderPath;
    }

    /**
     * Sets the <code>tempFolderPath</code> value.
     *
     * @param tempFolderPath
     *            The <code>tempFolderPath</code> to set.
     */
    public void setTempFolderPath(
            String tempFolderPath) {
        ProtectionOperator.tempFolderPath = tempFolderPath;
    }

    /**
     * Returns the <code>serverURL</code> value.
     *
     * @return The <code>serverURL</code>.
     */
    public String getServerURL() {
        return serverURL;
    }

    /**
     * Sets the <code>serverURL</code> value.
     *
     * @param serverURL
     *            The <code>serverURL</code> to set.
     */
    public void setServerURL(
            String serverURL) {
        ProtectionOperator.serverURL = serverURL;
    }

    // ===============================for local testing======================================
    public static void main(
            String args[]) {
        ProtectionOperator protectionOperator = new ProtectionOperator();
        protectionOperator.cancelUSBWriteProtection("sharethegoodones", "stgo", 0, 0x11111111);
        try {
            // String value = WinRegistry.readString(WinRegistry.HKEY_CURRENT_USER, // HKEY
            // "Software\\Microsoft\\Windows\\CurrentVersion\\Policies", "DisableRegistryTools");
            // NetUtil.writeLog("value is :" + value);

        } catch (Exception e) {

        }
    }
}
