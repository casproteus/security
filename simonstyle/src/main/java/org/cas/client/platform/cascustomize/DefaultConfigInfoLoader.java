package org.cas.client.platform.cascustomize;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Hashtable;

import org.apache.commons.io.FileUtils;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;

class DefaultConfigInfoLoader {
    final private String LINK_STRING = "=";
    final private String ENTER_STRING = ";";

    /** 构造器 */
    DefaultConfigInfoLoader() {
    }

    /** 取得PIM的config文件的路径, @Called by ConfigFileWriter */
    static String getConfigFilePath() {
        return CASUtility.getPIMDirPath().concat(System.getProperty("file.separator")).concat("PIMConfig.ini");
    }

    /**
     * @called by:FileCustomOptions;
     * @param prmHash
     *            所有用户CustOptimized信息通过本方法调入hash中。
     */
    void loadCustmizedData(
            Hashtable prmHash) {
        // 将磁盘中文件中的信息读入字符串tmpFileText。----------------------------------------
        String tmpConfigFilePath = getConfigFilePath();
        System.out.println("tmpConfigFilePath is : " + tmpConfigFilePath);
        String tmpFileText = "";
        try {
            tmpFileText = FileUtils.readFileToString(new File(tmpConfigFilePath), "UTF-8");
            // FileInputStream fileInput = new FileInputStream(tmpConfigFilePath);
            //
            // byte[] buffer = new byte[(int) new File(tmpConfigFilePath).length()]; // 建立用户初始化设置文件对象。
            // fileInput.read(buffer);
            // fileInput.close();
            // tmpFileText = new String(buffer); // 将用户初始化设置文件信息读入字符串。
        } catch (IOException e) {
            return;
        }
        System.out.println("tmpFileText(UTF-8) is : " + tmpFileText);
        // 将字符串中的信息存入Hashtable。----------------------------------------
        int tmpLINKPos;
        int tmpENTERPos = 0;
        int tmpTextLen = tmpFileText.length();

        if (tmpFileText.endsWith("}"))
            tmpFileText = tmpFileText.substring(0, tmpTextLen - 1);
        // @NOTE:不可以去掉前面的“{”号，因为每次取Key的时候是从前一个“，”号位置＋＋开始取值，
        // 去掉“{”号会导致如“Vertical”变成了“ertical”的错误。
        // if (tmpFileText.startsWith("{"))
        // tmpFileText = tmpFileText.substring(1);
        // 至此去掉字符串两端的“{”和“}”--------------------------

        tmpFileText = tmpFileText.trim(); // 去掉字符串两端其他无意义的字符。
        tmpTextLen = tmpFileText.length();

        String tmpKey = null;
        String tmpValue = null;
        for (; tmpENTERPos < tmpTextLen && tmpENTERPos > -1;) {
            tmpLINKPos = tmpFileText.indexOf(LINK_STRING, tmpENTERPos); // 得到等于号的位置。
            if (tmpLINKPos <= tmpENTERPos) // 如果等号位置不在回车位置之前，说明不对了，或到头了。
                return;
            tmpKey = tmpFileText.substring(++tmpENTERPos, tmpLINKPos).trim(); // 得到key值（等于号前面的值）。

            tmpENTERPos = tmpFileText.indexOf(ENTER_STRING, tmpLINKPos); // 得到回车符得位置。
            if (tmpENTERPos <= tmpLINKPos) // 如果回车号位置不在等号位置之前，说明不对了，或到头了。
                tmpENTERPos = tmpTextLen; // 此处不可直接返回，因为不该漏掉最后一个value。
                                          // @NOTE:可能需要判断是否是等于，如果最后一个符号是“＝”会不会出错？
            tmpValue = tmpFileText.substring(++tmpLINKPos, tmpENTERPos).trim();
            System.out.println("开始塞值---------------------");
            System.out.println("key:" + tmpKey);
            System.out.println("tmpValue" + tmpValue);
            prmHash.put(tmpKey, tmpValue);
        }
    }

    /**
     * @called by:FileCustomOptions; hash2中的内容包括两项：供验证主应用是否有被添加删除的AppNameStr；和菜单工具条的结构信息。
     * @param prmHash
     *            需要load的信息在prmHash中存储着
     */
    void loadSystemData(
            Hashtable prmHash) {
        // 将磁盘中文件中的信息读入字符串tmpFileText。----------------------------------------
        // String tmpFileText = "";
        // mpSream.try {
        // System.out.println("URL:" + getClass().getResource("/config.ini"));
        // InputStream tmpSream = getClass().getResourceAsStream("/config.ini");
        // BufferedReader tmpBufReader = new BufferedReader(new InputStreamReader(tmpSream));
        // String tS = "";
        // while ((tS = tmpBufReader.readLine()) != null) { // 将用户初始化设置文件信息读入字符串。
        // tmpFileText = tmpFileText.concat(tS).concat("\n");
        // }
        // tmpBufReader.close();
        // tclose();
        // } catch (Exception e) {
        // ErrorUtil.write("fail: " + e.toString());
        // return;
        // }

        String tmpFileText =
                "{\r\n"
                        + "APP_Captions=客户关系类/人力资源类/产品与服务/销售类/库存类/退货类;\r\n"
                        + "APP_Names=Contact/Employee/Product/Output/Input/Refund;\r\n"
                        + "Menus=文件(F)/编辑(E)/视图(V)/工具(T)/帮助(H);\r\n"
                        + "Mnemonic=F/E/V/T/H;\r\n"
                        + "文件(F)=添加记录(N)/打开选中(O).../-/文件夹(F)/-/导入和导出(T).../-/退出(X);\r\n"
                        + "文件(F)_Mnemonic=N/O/F/T/X;\r\n"
                        + "文件(F)_ActionID=N/OpenAction/FolderPropertyAction/ImportExportAction/FileExitAction;\r\n"
                        + "添加记录(N)=销售记录(S)/进货记录(I)/产品资料(P)/联系人(C)/职员信息(E);\r\n"
                        + "添加记录(N)_Mnemonic= S/I/P/C/E;\r\n"
                        + "添加记录(N)_ActionID=NewOutputAction/NewInputAction/NewProductAction/NewContactAction/NewEmployeeAction;\r\n"
                        + "文件夹(F)=新建文件夹(N)/重命名(R)/删除文件夹(D);\r\n"
                        + "文件夹(F)_Mnemonic=N/R/D;\r\n"
                        + "文件夹(F)_ActionID=FolderNewAction/FolderRenameAction/FolderDelAction;\r\n"
                        + "编辑(E)=全选(A)/删除(D)/-/查找(F)/-/类别(C);\r\n"
                        + "编辑(E)_Mnemonic=A/D/F/C;\r\n"
                        + "编辑(E)_ActionID=SelectAllAction/DeleteAction/FindAction/CategoriesAction;\r\n"
                        + "视图(V)=上一个(R)/下一个(X)/-/旋转视图(C)/-/显示&隐藏导航面板(P)/显示&隐藏书本(B)/显示&隐藏预览(N)/-/更换背景(F)/自定义背景(M)...;\r\n"
                        + "视图(V)_Mnemonic=R/X/C/P/B/N/F/M;\r\n"
                        + "视图(V)_ActionID=LastFolderAction/NextFolderAction/CircumViewAction/PaneAction/HideBookAction/PreviewAction/NextMainBGAction/CustomizeBGAction;\r\n"
                        + "工具(T)=数据维护(M);\r\n" + "工具(T)_Mnemonic=M;\r\n" + "工具(T)_ActionID=ModifyData;\r\n"
                        + "帮助(H)=视频讲解(I)/在线注册(R).../-/关于(A)...;\r\n" + "帮助(H)_Mnemonic=I/R/A;\r\n"
                        + "帮助(H)_ActionID=VedioExplain/OnlineRegister/AboutAction;\r\n" + "DefaultFont=宋体;\r\n"
                        + "}\r\n" + "";
        // 将字符串中的信息存入Hashtable。----------------------------------------
        int tmpLINKPos;
        int tmpENTERPos = 0;
        int tmpTextLen = tmpFileText.length();

        if (tmpFileText.endsWith("}")) {
            tmpFileText = tmpFileText.substring(0, tmpTextLen - 1);
        }
        // @NOTE:不可以去掉前面的“{”号，因为每次取Key的时候是从前一个“，”号位置＋＋开始取值，
        // 去掉“{”号会导致如“Vertical”变成了“ertical”的错误。
        if (tmpFileText.startsWith("﻿{")) {
            tmpFileText = tmpFileText.substring(1);
        } // 至此去掉字符串两端的“{”和“}”

        tmpFileText = tmpFileText.trim(); // 去掉字符串两端其他无意义的字符。
        tmpTextLen = tmpFileText.length();

        String tmpKey = null;
        String tmpValue = null;
        for (; tmpENTERPos < tmpTextLen && tmpENTERPos > -1;) {
            tmpLINKPos = tmpFileText.indexOf(LINK_STRING, tmpENTERPos); // 得到等于号的位置。
            if (tmpLINKPos <= tmpENTERPos) // 如果等号位置不在回车位置之前，
                return; // 说明不对了，或到头了。

            tmpKey = tmpFileText.substring(++tmpENTERPos, tmpLINKPos).trim(); // 得到key值（等于号前面的值）。

            tmpENTERPos = tmpFileText.indexOf(ENTER_STRING, tmpLINKPos); // 得到回车符得位置。
            if (tmpENTERPos <= tmpLINKPos) // 如果回车号位置不在等号位置之前，说明不对了，或到头了。
                tmpENTERPos = tmpTextLen; // NOTE:可能需要判断是否是等于，如果最后一个符号是“＝”会不会出错？
                                          // 此处不可直接返回，因为不该漏掉最后一个value。
            tmpValue = tmpFileText.substring(++tmpLINKPos, tmpENTERPos).trim();
            prmHash.put(tmpKey, tmpValue);
        }
    }

    /**
     * @called by:FileCustomOptions; 根据传入的应用名，构造路径字符串，然后读入信息到参数hash表中。
     * @param prmHash
     *            需要load的信息在prmHash中存储着
     */
    void loadAppData(
            String prmAppName,
            Hashtable prmHash) {
        String tmpFileText = "";
        try {
            URL tmpURL =
                    getClass().getResource(
                            "/org/cas/client/platform/".concat(prmAppName.toLowerCase()).concat("/").concat(prmAppName)
                                    .concat(".ini"));
            String absolutPath = tmpURL.getFile();
            if (absolutPath.indexOf(".jar!") == -1) {
                tmpFileText = FileUtils.readFileToString(new File(absolutPath), "UTF-8");
            } else {
                InputStream is =
                        this.getClass().getResourceAsStream(
                                "/org/cas/client/platform/".concat(prmAppName.toLowerCase()).concat("/")
                                        .concat(prmAppName).concat(".ini"));
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String s = "";
                while ((s = br.readLine()) != null)
                    sb.append(s);
                tmpFileText = sb.toString();
            }
            // ==================================

            // InputStream tmpSream = getClass().getResourceAsStream("/org/cas/client/platform/"
            // .concat(prmAppName.toLowerCase()).concat("/").concat(prmAppName).concat(".ini"), "UTF-8");
            // BufferedReader tmpBufReader = new BufferedReader(new InputStreamReader(tmpSream));
            // String tS = "";
            // while ((tS = tmpBufReader.readLine()) != null) { // 将用户初始化设置文件信息读入字符串。
            // tmpFileText = tmpFileText.concat(tS).concat("\n");
            // }
            // tmpBufReader.close();
            // tmpSream.close();
        } catch (IOException e) {
            ErrorUtil.write("fail: " + e.toString());
            return;
        }

        // 将字符串中的信息存入Hashtable。----------------------------------------
        int tmpLINKPos;
        int tmpENTERPos = 0;
        int tmpTextLen = tmpFileText.length();

        if (tmpFileText.endsWith("}"))
            tmpFileText = tmpFileText.substring(0, tmpTextLen - 1);
        // @NOTE:不可以去掉前面的“{”号，因为每次取Key的时候是从前一个“，”号位置＋＋开始取值，
        // 去掉“{”号会导致如“Vertical”变成了“ertical”的错误。
        // if (tmpFileText.startsWith("{"))
        // tmpFileText = tmpFileText.substring(1);
        // 至此去掉字符串两端的“{”和“}”--------------------------

        tmpFileText = tmpFileText.trim(); // 去掉字符串两端其他无意义的字符。
        tmpTextLen = tmpFileText.length();

        String tmpKey = null;
        String tmpValue = null;
        for (; tmpENTERPos < tmpTextLen && tmpENTERPos > -1;) {
            tmpLINKPos = tmpFileText.indexOf(LINK_STRING, tmpENTERPos); // 得到等于号的位置。
            if (tmpLINKPos <= tmpENTERPos) // 如果等号位置不在回车位置之前，说明不对了，或到头了。
                return;
            tmpKey = tmpFileText.substring(++tmpENTERPos, tmpLINKPos).trim();// 得到key值（等于号前面的值）。

            tmpENTERPos = tmpFileText.indexOf(ENTER_STRING, tmpLINKPos);// 得到回车符得位置。
            if (tmpENTERPos <= tmpLINKPos) // 如果回车号位置不在等号位置之前，说明不对了，或到头了。
                tmpENTERPos = tmpTextLen; // 此处不可直接返回，因为不该漏掉最后一个value。
            // @NOTE:可能需要判断是否是等于，如果最后一个符号是“＝”会不会出错？
            tmpValue = tmpFileText.substring(++tmpLINKPos, tmpENTERPos).trim();
            prmHash.put(tmpKey, tmpValue);
        }
    }
}

/*----------------------------------------------------------
 private void loadData(String[] prmDataAry)
 {

 int temIndex = 0;               //得到prmDataArray在INFORMATION_LIST链表中的位置。如果一直到找完没有找到匹配项，则返回。
 for (; temIndex < INFORMATION_LIST.length; ++temIndex)
 {
 if (INFORMATION_LIST[temIndex] == prmDataAry)
 {
 break;
 }
 }
 if (temIndex >= INFORMATION_LIST.length)
 {
 return;
 }

 String temOptionTitle = prmDataAry[0];  //得到参数中的标题在文本文件(PIMConfig.ini)中的位置，如果没有这个标题则返回。
 int temStartOffset = fileText.indexOf(temOptionTitle);
 if (temStartOffset == -1)
 {
 return;
 }

 int temEndOffset = fileText.length();           //定义一个变量，并指向文档内容字符串末尾的位置。
 if (temIndex != (INFORMATION_LIST.length - 1))  //如果参数在INFORMATION_LIST链表中的位置不是最后一个,而且下一项信息的标。
 {                                               //题在文本文件(PIMConfig.ini)中存在......
 temEndOffset = fileText.indexOf(INFORMATION_LIST[temIndex + 1][0]); //使指针指向下一项信息的标题。
 if (temEndOffset == -1)
 {
 temEndOffset = fileText.length();        //没有的话，指针仍然指向结尾处。
 }
 }

 String temSectionText = fileText.substring(temStartOffset, temEndOffset);   //取出Config文件中与参数相关的全部信息。

 String temKey;
 int offset, crOffset, strLen;
 for (int i = 1; i < prmDataAry.length; ++i)             //prmDataAry的首位置是特殊符号，所以从1开始？
 {
 temKey = prmDataAry[i] + LINK_STRING;
 strLen = temKey.length();
 offset = temSectionText.indexOf(temKey);
 if (offset == -1)
 {
 continue;
 }
 offset += strLen;
 crOffset = temSectionText.indexOf(ENTER_STRING, offset);
 if (crOffset == -1)
 {
 continue;
 }
 setHashValue(prmDataAry[i], temSectionText.substring(offset, crOffset));
 }
 }

 private void setHashValue(String key, String value)
 {
 int offset = value.indexOf(INTEGER_TYPE);
 if (offset != -1)
 {
 offset = INTEGER_TYPE.length();
 value = value.substring(offset);
 try
 {
 int integerValue = Integer.parseInt(value);
 hash.put(key, new Integer(integerValue));
 }
 catch (NumberFormatException e)
 {
 }
 return;
 }

 offset = value.indexOf(STRING_TYPE);
 if (offset != -1)
 {
 offset = STRING_TYPE.length();
 value = value.substring(offset);
 hash.put(key, value);
 return;
 }

 offset = value.indexOf(BOOLEAN_TYPE);
 if (offset != -1)
 {
 offset = BOOLEAN_TYPE.length();
 value = value.substring(offset);
 if (value.equals(BOOLEAN_TRUE))
 {
 hash.put(key, Boolean.TRUE);
 }
 else if (value.equals(BOOLEAN_FALSE))
 {
 hash.put(key, Boolean.FALSE);
 }
 return;
 }
 }*/

/**
 * Load the navigator tree.
 * 
 * void loadNavigateTree() { loadData(NAVIGATE_TABLE); }
 * 
 * /** Load the PIM screen layout information.
 * 
 * void loadLayoutInformation() { loadData(LAYOUT_LIST); }
 * 
 * /** Load the options.
 * 
 * void loadOptionInformation() { loadData(OPTION_LIST); }
 * 
 * /** Load the day view information.
 * 
 * void loadDayViewInformation() { loadData(DAY_VIEW_LIST); }
 * 
 * /** Load the week view information.
 * 
 * void loadWeekViewInformation() { loadData(WEEK_VIEW_LIST); }
 * 
 * /** Load the month view information.
 * 
 * void loadMonthViewInformation() { loadData(MONTH_VIEW_LIST); }
 */
