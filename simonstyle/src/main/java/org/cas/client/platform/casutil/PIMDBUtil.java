package org.cas.client.platform.casutil;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.util.ModelConstants2;
import org.cas.client.resource.international.PaneConsts;

public class PIMDBUtil {
    public static final String COMMA = " , "; // 共享对象逗号
    public static final String ASTERISK = " * "; // 星号

    private static File directory;

    /**
     *
     */
    public static File getPIMTempDirectory() {
        return directory;
    }

    /**
     * 建目录
     */
    public static void createPIMTempDirectory() {
        // 建目录
        if (directory == null) {
            directory = new File(CASUtility.getPIMMailDirPath() + "tmp" + System.getProperty("file.separator"));
            directory.mkdir();
        }
    }

    /**
     * Delete Temp directory
     */
    public static void deletePIMTempDirectory() {
        if (directory != null) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    files[i].delete();
                }
            }
            directory.delete();
        }
    }

    /**
     * 解析字符串为int数组，字符串由数字组成，','分隔，无空格
     */
    /*
     * static int[] getFieldIDs(String fieldNames) { int[] fieldID = null; if (fieldNames == null || fieldNames.length()
     * == 0) { return null; } String temp = fieldNames; int index = temp.indexOf(","); Vector v = new Vector(); while
     * (index >= 0) { v.addElement(temp.substring(0, index)); temp = temp.substring(index + 1); index =
     * temp.indexOf(","); } v.addElement(temp); fieldID = new int[v.size()]; for (int i = 0; i < v.size(); i ++) {
     * fieldID[i] = Integer.parseInt((String)v.elementAt(i)); } return fieldID; } /**
     */
    /*
     * static Integer[] getFieldIDObj(String fieldNames) { int[] fieldID = getFieldIDs(fieldNames); int length =
     * fieldID.length; Integer[] fieldIDObj = new Integer[length]; for (int i = 0; i < length; i ++) { fieldIDObj[i] =
     * new Integer(fieldID[i]); } return fieldIDObj; } /**
     */
    /*
     * static String[] getDisplayNames(String fieldNames) { int[] fieldID = getFieldIDs(fieldNames); int length =
     * fieldID.length; String[] fieldName = new String[length]; for (int i = 0; i < length; i ++) { fieldName[i] =
     * ModelDBConstants.CONTACTS[fieldID[i]].toString(); } return fieldName; } /**
     */
    /*
     * static Hashtable getDisplayFields() { return null; } /** 把序列化对象转化为byte[]
     * @param Object obj序列化对象
     * @return byte[] 对象序列化后得到的byte数组
     */
    public static byte[] encodeSerializeObjectToByteArray(
            Object obj) {
        if (!(obj instanceof Serializable)) {
            return null;
        }
        byte[] b = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            b = baos.toByteArray();

            oos.flush();
            oos.close();
            baos.flush();
            baos.close();
        } catch (IOException ie) {
            ie.printStackTrace();
        }

        return b;
    }

    /**
     * 把byte[]转化为序列化对象
     * 
     * @param byte[] 需要转化为对象的byte数组
     * @return Object 被转化后的对象
     */
    public static Object decodeByteArrayToSerializeObject(
            byte[] ba) {
        if ((ba == null) || (ba.length == 0)) {
            return null;
        }
        Object object = null;
        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(ba);
            ObjectInputStream ois = new ObjectInputStream(bais);

            object = ois.readObject();

            ois.close();
            bais.close();
        } catch (IOException ie) {
            // ie.printStackTrace();
        } catch (ClassNotFoundException cnfe) {
            // cnfe.printStackTrace();
        }

        return object;
    }

    /**
     * 把图片转化为byte[]
     * 
     * @param: fileName 要转换成byte数组的文件名字
     * @return: byte[] 转换后的byte数组
     */
    public static byte[] encodeImageToByteArray(
            String fileName) {
        byte[] b = null;
        try {
            FileInputStream fis = new FileInputStream(fileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            int length = bis.available();
            b = new byte[length];
            while ((bis.read(b)) != -1)
                ;

            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return b;
    }

    /**
     * 把序图片转化为byte[]
     * 
     * @param icon
     *            需要转换成byte数组的ImageIcon对象
     * @return byte[] 转换后的byte数组
     */
    static byte[] encodeImageIconToByteArray(
            ImageIcon icon) {
        byte[] b = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(icon);
            b = baos.toByteArray();

            oos.flush();
            oos.close();
            baos.flush();
            baos.close();
        } catch (IOException ie) {
            // ie.printStackTrace();
        }

        return b;
    }

    /**
     * 把byte[]转化为图片，路径？
     * 
     * @param: byte[] 需要转换成ImageIcon对象的byte数组
     * @return: Image 转换后的ImageIcon对象
     */
    static ImageIcon decodeByteArrayToImageIcon(
            byte[] bArray) {
        ImageIcon icon = null;

        Toolkit kit = Toolkit.getDefaultToolkit();

        if (bArray != null)
            icon = new ImageIcon(kit.createImage(bArray));

        return icon;
    }

    /**
     * 把byte[]转化为图片，路径？
     * 
     * @param: bArray 需要转换成Image对象的byte数组
     * @return: Image 通过byte数组转换后的Image对象
     */
    static Image decodeByteArrayToImage(
            byte[] bArray) {
        Image image = null;
        Toolkit kit = Toolkit.getDefaultToolkit();
        if (bArray != null)
            image = kit.createImage(bArray);

        return image;
    }

    /**
     * 把byte[]转化为图片，路径？
     * 
     * @param: byte[] 需要转换成对象并存成文件的byte数组
     * @param: recordID 记录的id
     * @return: String 转换后的文件名
     */
    public static String decodeByteArrayToImage(
            byte[] bArray,
            int recordID) {
        String fileName = null;
        if (bArray != null)
            try {
                // fileName = getPIMURL() + System.getProperty("file.separator") + recordID + ".jpg";
                fileName = File.createTempFile("TMP", ".png", getPIMTempDirectory()).toString();

                FileOutputStream fos = new FileOutputStream(fileName);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(bArray);
                bos.flush();
                bos.close();
                bos = null;
                fos.flush();
                fos.close();
                fos = null;
            } catch (IOException ie) {
                // ie.printStackTrace();
            }

        return fileName;
    }

    /**
     * 得到各个应用中用到的表的名字
     *
     * @param: appType 应用的类型
     * @return: String 得到应用对应的数据库表的名字
     */
    public static String getSystemTableName(
            int appType) {
        if (appType > 0)
            return ModelConstants2.SYSTEMTABLE_NAME_LIST[appType];
        else
            return ModelConstants2.SYSTEMTABLE_NAME_LIST[appType * -1];
    }

    /**
     * 通过表名得到应用类型
     *
     * @param: tableName 表名
     * @return: int 应用的索引
     */
    public static int getAppTableIndex(
            String tableName) {
        int tmpIndex = CustOpts.custOps.APPNameVec.indexOf(tableName);
        if (tmpIndex >= 0)
            return tmpIndex;
        else
            for (int i = 0; i < ModelConstants2.SYSTEMTABLE_NAME_LIST.length; i++)
                if (ModelConstants2.SYSTEMTABLE_NAME_LIST[i].equals(tableName))
                    return i * -1;
        return tmpIndex;
    }

    /**
     * 根据数据库的表的名字得到对应的INFOLDER路径
     * 
     * @param prmTableName
     *            数据库表的名字
     * @return INFOLDER路径
     */
    public static String converTableNameToFolder(
            String prmTableName) {
        if (BarUtil.empty(prmTableName)) // 判断当前的数据库表的名字是否可用
            return null;

        StringBuffer tmpFolder = new StringBuffer();
        tmpFolder.append(PaneConsts.ROOTCAPTION).append(PaneConsts.HEAD_PAGE).append(", ");

        StringTokenizer ken = new StringTokenizer(prmTableName, "_");
        if (ken != null && ken.hasMoreTokens())
            tmpFolder.append(getAppName(ken.nextToken()));
        while (ken.hasMoreTokens())
            tmpFolder.append(", ").append(ken.nextToken());
        tmpFolder.append(CASUtility.NODEPATHEND);

        return tmpFolder.toString(); // 返回当前的数据库表对应的INFOLDER路径
    }

    /**
     * 根据数据库表的名字"MAIL"得到应用的名字"收件箱"
     * 
     * @param prmTableAppName
     *            数据库表的名字
     * @return 应用的名字
     */
    public static String getAppName(
            String prmTableAppName) {
        String appName = null;
        int tmpIndex = CustOpts.custOps.APPNameVec.indexOf(prmTableAppName);
        if (tmpIndex > 0)
            appName = (String) CustOpts.custOps.APPCapsVec.get(tmpIndex);
        return appName;
    }

    /** 根据数据库表的名字得到对应的数据库表的字段和相应类型在DefaultDBInfo的 */
    public static int getFieldAndTypeIndex(
            String prmTableName) {
        String tmpStr = prmTableName;
        if (tmpStr == null || tmpStr.length() < 1) // 判断数据库的表的名字
            return -1;

        int tmpPos = tmpStr.indexOf("_");
        if (tmpPos > 0) { // 得到数据库表在TABLE_NAME_LIST中的索引值 得到数据库的表的名字--如果是
            Thread.dumpStack(); // 类似Contacts_Fields的表名，处理后得到Contacts.
            tmpStr = tmpStr.substring(0, tmpPos);
        }

        tmpPos = CustOpts.custOps.APPNameVec.indexOf(tmpStr);
        if (tmpPos >= 0)
            return tmpPos;

        for (int i = 0; i < ModelConstants2.SYSTEMTABLE_NAME_LIST.length; i++)
            if (ModelConstants2.SYSTEMTABLE_NAME_LIST[i].equals(tmpStr)) {
                tmpPos = i;
                break;
            }
        return tmpPos * -1;
    }

    /**
     * 通过数据类型得到表名,这些类型都大于7
     *
     * @param: dataType 数据类型
     * @return String 数据库的表名
     */
    public static String getTableName(
            int dataType) {
        String tableName = null;
        if (dataType == ModelCons.VIEW_INFO_DATA)
            tableName = ModelConstants2.VIEWINFO_TABLE_NAME;
        else if (dataType == ModelCons.MAIL_RULE_DATA)
            tableName = ModelDBCons.MAIL_RULE_TABLE_NAME;
        else if (dataType == ModelCons.ACCOUNT_INFO_DATA)
            tableName = ModelDBCons.ACCOUNT_TABLE_NAME1;
        else if (dataType == ModelCons.VIEW_FORMAT_DATA)
            tableName = ModelDBCons.VIEW_FORMAT_TABLE_NAME;
        return tableName;
    }

    /**
     * 通过表名得到数据类型的索引
     *
     * @param tableName
     *            表名
     * @return int 数据类型的索引
     */
    public static int getDataType(
            String tableName) {
        int type = -1;
        if (ModelConstants2.VIEWINFO_TABLE_NAME.equalsIgnoreCase(tableName))
            type = ModelCons.VIEW_INFO_DATA;
        else if (ModelDBCons.MAIL_RULE_TABLE_NAME.equalsIgnoreCase(tableName))
            type = ModelCons.MAIL_RULE_DATA;
        else if (ModelDBCons.ACCOUNT_TABLE_NAME1.equalsIgnoreCase(tableName))
            type = ModelCons.ACCOUNT_INFO_DATA;
        else if (ModelDBCons.VIEW_FORMAT_TABLE_NAME.equals(tableName))
            type = ModelCons.VIEW_FORMAT_DATA;
        return type;
    }

    /**
     * 通过数据类型得到对应的类名
     *
     * @param: prmDataType 数据类型
     * @return: String 类的名字
     */
    public static String getClassName(
            int prmDataType) {
        String tmpClassName = null;
        if (prmDataType == ModelCons.VIEW_INFO_DATA)
            tmpClassName = "org.cas.client.platform.pimmodel.PIMViewInfo";
        else if (prmDataType == ModelCons.MAIL_RULE_DATA)
            tmpClassName = "org.cas.client.platform.pimmodel.datasource.MailRuleContainer";
        else if (prmDataType == ModelCons.ACCOUNT_INFO_DATA)
            tmpClassName = "org.cas.client.platform.pimmodel.datasource.AccountInfo";
        else if (prmDataType == ModelCons.VIEW_FORMAT_DATA)
            tmpClassName = "org.cas.client.platform.pimmodel.datasource.ViewFormat";
        return tmpClassName;
    }

    /**
     * 改名数据库表中的一张表以后,涉及修改的一系列的数据库表 例如:INBOX_123_jin表修改为INBOX_111_jin 后要涉及修改的数据库的表的名字如 INBOX_123_jin INBOX_123_jin1
     * INBOX_123_jin_jin1等等数据库表的名字
     */
    public static Hashtable renamedTableName(
            String[] prmTabNames,
            String prmOldTableName,
            String prmNewTableName) {
        ArrayList aList = new ArrayList(); // 写一个触发器使得所有的数据库的表的名字随之改变
        ArrayList bList = new ArrayList();
        StringTokenizer ken = new StringTokenizer(prmOldTableName, "_");
        while (ken != null && ken.hasMoreTokens())
            aList.add(ken.nextToken());
        ken = new StringTokenizer(prmNewTableName, "_");
        while (ken != null && ken.hasMoreTokens())
            bList.add(ken.nextToken());

        int index = -1; // 匹配找到两个字符串的不同之处
        String a = CASUtility.EMPTYSTR, b = CASUtility.EMPTYSTR;
        for (int size = aList.size(), i = 0; i < size; i++) {
            a = (String) aList.get(i);
            b = (String) bList.get(i);
            if (!a.equals(b)) // 注意区分大小写
                index = i;
        }

        if (index == -1)// 判断是否找到不同的,改名失败
            return null;

        StringBuffer sub = new StringBuffer();
        for (int i = 0; i < index; i++)
            // 组装成数据库表名INBOX_
            sub.append(aList.get(i)).append("_");

        a = sub.toString().concat(a); // 组合成数据库表名INBOX_123
        b = sub.toString().concat(b);

        int begPos = a.length(); // 改名
        Hashtable nameHash = new Hashtable();
        for (int size = prmTabNames.length, i = 0; i < size; i++)
            if (prmTabNames[i].equals(a.toUpperCase()) || prmTabNames[i].startsWith(a.toUpperCase().concat("_"))) {
                String subFol = prmTabNames[i].substring(begPos);
                nameHash.put(prmTabNames[i], b.toUpperCase().concat(subFol));
            }

        return nameHash;
    }

    /**
     * 删除数据库表
     * 
     * @param prmtableName
     *            要删除的数据库表的名字
     * @param String
     *            [] 数据库表的名字
     * @return String[] 涉及删除的数据库的表的名字
     */
    public static String[] deleteTableNames(
            String[] prmAllTable,
            String prmTableName) {
        ArrayList name = new ArrayList();
        for (int size = prmAllTable.length, i = 0; i < size; i++)
            if (prmAllTable[i].startsWith(prmTableName))
                name.add(prmAllTable[i]);
        return (String[]) name.toArray(new String[0]);
    }

    /**
     * 得到子文件夹数据库表名对应的主应用的数据库的表的名字
     */
    public static String getMainTableName(
            String prmSubTableName) {
        int pos = prmSubTableName.indexOf("_");
        return pos < 0 ? prmSubTableName : prmSubTableName.substring(0, pos);
    }

    /**
     * 得到对应的删除的目的表的名字
     * 
     * @param prmApp
     *            类型
     * @return String 回收张数据库表名 NOTE:只用应用类型值7才有对应的删除的数据库表,其余的类型都没有对应的回收 日历 CALENDAR_APP = 0 任务 TASK_APP = 1
     *         联系人CONTACT_APP = 2 日记DIARY_APP = 3 收件箱INBOX_APP = 4 发件箱OUTBOX_APP = 5 已发送邮件SENDED_APP = 6
     */
    public static String getRecycleTableName(
            int prmApp) {
        String temp = (String) CustOpts.custOps.APPNameVec.get(prmApp);
        return temp.concat("_RECYCLE");
    }

    /**
     * 取到已删除项的INFOLDER路径，根据引用类型
     * 
     * @param prmAppType
     * @return
     */
    public static String getRecycleFolder(
            int prmAppType) {
        return converTableNameToFolder((String) CustOpts.custOps.APPNameVec.get(prmAppType));
    }

    // public static void main(String[] args)
    // {
    // getUpdateInfolders(new String[]
    // {"[PIM, 资讯管理, 收件箱, 123456]", "[PIM, 资讯管理, 收件箱, 123, 345]", "[PIM, 资讯管理, 收件箱, 123, 345, 234]"},
    // "[PIM, 资讯管理, 收件箱, 123]",
    // "[PIM, 资讯管理, 收件箱, jin]"
    // );
    // A.s(convertFolderToRecycleName("[PIM, 资讯管理, 已删除项]"));
    // }
}
// /**
// * 根据INFOLDER字段得到对应的回收站表的名字
// * @param prmFolder 记录所在的FOLDER_PATH
// * @return 返回对应回收站表的名字
// */
// public static String convertFolderToRecycleName(String prmFolder)
// {
// StringBuffer tab = new StringBuffer();
// tab.append(prmFolder);
// tab.deleteCharAt(prmFolder.length() - 1); //删除最后的一个']'
//
// StringTokenizer ken = new StringTokenizer(tab.toString(), ",");
// String tmpAppName = null;
// if (ken.countTokens() < 3)
// {
// throw new IllegalArgumentException("Illegal FOLDER PATH :" + prmFolder);
// }
// int count = 0;
// for (int size = prmFolder.length(), i = 0; i < size; i++)
// {
// if (prmFolder.charAt(i) == ',')
// {
// count++;
// }
// }
// if (count == 2)
// {
// ken.nextToken();
// ken.nextToken();
// int tmpIndex = CustOpts.custOps.APPCapsVec.indexOf(ken.nextToken().trim());
// tmpAppName = (String)CustOpts.custOps.APPNameVec.get(tmpIndex);
//
// }
// else if (count >= 3)
// {
// ken.nextToken();
// ken.nextToken();
// ken.nextToken();
// int tmpIndex = CustOpts.custOps.APPCapsVec.indexOf(ken.nextToken().trim());
// tmpAppName = (String)CustOpts.custOps.APPNameVec.get(tmpIndex);
// }
// /**
// * =====================================
// * 此处为本地联系人的特殊处理，正常版本应去掉
// * =====================================
// */
// if (tmpAppName.equals(ModelDBConstants.CLIENT_CONTACT_TABLE_NAME))
// {
// tmpAppName = ModelDBConstants.CONTACT_TABLE_NAME;
// }
// return tmpAppName == null ? null : tmpAppName.concat("_RECYCLE");
// }
