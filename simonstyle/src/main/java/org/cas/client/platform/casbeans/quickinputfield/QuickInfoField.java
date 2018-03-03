package org.cas.client.platform.casbeans.quickinputfield;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Panel;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.pimmodel.PIMRecord;

/**
 * 由于本控件上的popUpPane的限制，QuickInfoPane只能加在三种面板上： JFrame，JDialog，JPanel及其子类上。
 */

public class QuickInfoField extends JTextField implements KeyListener, MouseListener, FocusListener, DocumentListener,
        Runnable, Cloneable, Releasable {
    final int ADDITION_SEP = 5;
    final int INSERT = 0;
    final int MODIFY = 1;
    final int REMOVE_UPDATE = 0;
    final int INSERT_UPDATE = 1;
    final int NULL_VK_MASK = -1;
    final int MIN_EMAIL_LENGTH = 3;
    final int MAILCONTALLOWED = 3;

    /**
     * Creates a new instance of PIMEmail
     * 
     * @NOTE: 传入的容器可能是JFrame，或JDialog，或JPanel。
     * @param pmrContainer
     *            父级容器
     */
    public QuickInfoField(Container pmrContainer) {
        container = pmrContainer;
        undo = new TextFieldUndoMethod();

        // 准备用于存放每次键盘输入后将弹出的匹配项的Vector及其实际加入文本域中内容的Vector。
        matchedItemsVec = new Vector(); // 存储要显示的邮件地址
        // 从fileAsVec和mailAddrVec中挑出一个，作为当用户回车或鼠标点击list某项时插入到field中的内容。
        replaceWithStrVec = new Vector();

        // 准备用于存用户实际输入内容对应项的Vec。
        selectedIDAddrVec = new Vector();
        selectedMailAddrVec = new Vector();
        selectedFileAsesVec = new Vector();

        // 开始准备界面上的组件-------------------------------------------------------
        jList = new JList();
        popUpPanel = new Panel(new BorderLayout()); // 弹出面板NOTE：必须是重量级组件，为了保证显示在前面。
        popUpPanel.setSize(280, 100);
        popUpPanel.setVisible(false);

        jList.setCellRenderer(new QuickInfoRender()); // 设置绘制器

        // 准备将从field中弹出的popupPane。
        popUpPanel.add(new JScrollPane(jList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);

        // @NOTE: 将popUpPanel加到QuickInfoPane所在的面板上。此处限制了QuickInfoPane只能加在三种面板上。
        // @NOTE: 因为JFrame和JDialog的公共父类并没有这两个子类都有的getContentPane方法，只好分别造型。
        if (pmrContainer instanceof JFrame) {
            ((JFrame) pmrContainer).getContentPane().add(popUpPanel);
        } else if (pmrContainer instanceof JDialog) {
            ((JDialog) pmrContainer).getContentPane().add(popUpPanel);
        } else if (pmrContainer instanceof JPanel) {
            pmrContainer.add(popUpPanel);
        }
        // UI组装完毕－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        textDoc = this.getDocument();
        textDoc.addDocumentListener(this);
        addFocusListener(this);
        addKeyListener(this); // 加入键盘事件
        addMouseListener(this); // 添加鼠标监听，用于地址信息的区域选取

        jList.addMouseListener(this);
    }

    /**
     * 改方法用于对QuickInfoField的用户和用户输入内容进行比较匹配的数组进行初始化
     * 
     * @called by: TaskDialog; AppointmentDialog;
     */
    public void initArys() {
        // 开始初始化jList用到的数据～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～～
        ICASModel tmpModel = CASControl.ctrl.getModel();
        allIDAry = tmpModel.getAllContactRecordId(); // 暂时用来存储一下全部ID字段。
        allFileAsAry = tmpModel.getAllContactDisplayAs();// 暂时用来存储一下全部的表示为字段
        int[] tmpAllIsGroup = tmpModel.getAllCommGroupAttrList();// 存储全部的是否为通讯组列表为字段.
        String[][] tmpAllAddressAry = tmpModel.getAllEmailAddress();// 存储全部的邮件地址字段

        // 得到数组的长度，并进行一个有效性校验。（判断是否两个一致，不一致则报错，并临时取一个最小值）
        // NOTE:如果真的出了错误将导致严重后果:邮件发错了人!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        aryLenth = allFileAsAry.length;
        if (aryLenth != tmpAllAddressAry.length || aryLenth != allIDAry.length) {
            // 写入错误日记。
            ErrorUtil
                    .write("the length of contacts ary in ContactTable is not same with file as ary! (QuickInfoField.initArys())");
            aryLenth = (aryLenth > tmpAllAddressAry.length) ? tmpAllAddressAry.length : aryLenth; // 这个补救措施没有大用.鉴于目前不知这种出错可能是否存在,先不整理.
            aryLenth = (aryLenth > allIDAry.length) ? tmpAllAddressAry.length : aryLenth;
        }
        // 至此得到不含重复的表示为或者ID的数组的长度,
        // @NOTE:该长度不是最终的"用于和输入内容比较匹配的数组"的长度-----------------------------------------

        // 累计最长的"表示为"字符串所占区域的长度（赋给一个最小限制长度10）。
        int tmpGapStrBeforeMailAddr = 10;
        for (int i = 0, tmpActualLength; i < aryLenth; i++) {
            tmpActualLength = getNeededLengthForShow(allFileAsAry[i]);
            if (tmpActualLength > tmpGapStrBeforeMailAddr) {
                tmpGapStrBeforeMailAddr = tmpActualLength;
            }
        }
        // 用一个全部是空格的字符串来表示这个长度。以备后面从该字串中取出若干补足各fileAs项长度的不足。
        StringBuffer temSepBuffer = new StringBuffer();
        for (int i = 0; i < tmpGapStrBeforeMailAddr + ADDITION_SEP; i++) {
            temSepBuffer.append(CASUtility.SPACE);
        }
        String tmpSepString = temSepBuffer.toString();
        // 至此得到标识着最大长度的(全部由空格组成的)字符串---------------------

        // 准备真正的allIDAry/allFileAsAry/allEmailAddrAry和strToShowOnPopPane的内容-------------------------
        aryLenth = aryLenth * MAILCONTALLOWED;// 先准备三倍于记录数的长度,后面要通过临时数组进行瘦身的.
        String[] tmpAllEmailAddrAry = new String[aryLenth];
        String[] tmpStrToShowOnPopPaneAry = new String[aryLenth];
        String[] tmpAllIDAry = new String[aryLenth];
        String[] tmpAllFileAsAry = new String[aryLenth];
        int i = 0;
        for (int j = 0; j < allFileAsAry.length; j++) {
            if (tmpAllIsGroup[j] == 1) // 该条记录是通讯组的情况.
            {
                tmpAllIDAry[i] = allIDAry[j];
                tmpAllFileAsAry[i] = allFileAsAry[j];
                tmpAllEmailAddrAry[i] = tmpAllAddressAry[j][0];
                tmpStrToShowOnPopPaneAry[i] =
                        tmpAllFileAsAry[i].concat(
                                tmpSepString.substring(0, tmpSepString.length()
                                        - getNeededLengthForShow(tmpAllFileAsAry[i]))).concat(tmpAllEmailAddrAry[i]);
                i++;
            } else // 该条记录不是通讯组(即普通联系人)的情况.
            {
                // NOTE:此处可以以循环表达,之所以没有用是为了省掉循环用到的临时变量.
                if (tmpAllAddressAry[j][0].length() > MIN_EMAIL_LENGTH) {
                    tmpAllIDAry[i] = allIDAry[j];
                    tmpAllFileAsAry[i] = allFileAsAry[j];
                    tmpAllEmailAddrAry[i] = tmpAllAddressAry[j][0];
                    tmpStrToShowOnPopPaneAry[i] =
                            tmpAllFileAsAry[i].concat(
                                    tmpSepString.substring(0, tmpSepString.length()
                                            - getNeededLengthForShow(tmpAllFileAsAry[i])))
                                    .concat(tmpAllEmailAddrAry[i]);
                    i++;
                }
                if (tmpAllAddressAry[j][1].length() > MIN_EMAIL_LENGTH) {
                    tmpAllIDAry[i] = allIDAry[j];
                    tmpAllFileAsAry[i] = allFileAsAry[j];
                    tmpAllEmailAddrAry[i] = tmpAllAddressAry[j][1];
                    tmpStrToShowOnPopPaneAry[i] =
                            tmpAllFileAsAry[i].concat(
                                    tmpSepString.substring(0, tmpSepString.length()
                                            - getNeededLengthForShow(tmpAllFileAsAry[i])))
                                    .concat(tmpAllEmailAddrAry[i]);
                    i++;
                }
                if (tmpAllAddressAry[j][1].length() > MIN_EMAIL_LENGTH) {
                    tmpAllIDAry[i] = allIDAry[j];
                    tmpAllFileAsAry[i] = allFileAsAry[j];
                    tmpAllEmailAddrAry[i] = tmpAllAddressAry[j][1];
                    tmpStrToShowOnPopPaneAry[i] =
                            tmpAllFileAsAry[i].concat(
                                    tmpSepString.substring(0, tmpSepString.length()
                                            - getNeededLengthForShow(tmpAllFileAsAry[i])))
                                    .concat(tmpAllEmailAddrAry[i]);
                    i++;
                }
            }
        }
        // 开始瘦身~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
        aryLenth = i; // 此时i的值应该恰好是长度.
        allIDAry = new String[aryLenth];
        allFileAsAry = new String[aryLenth];
        allEmailAddrAry = new String[aryLenth];
        strToShowOnPopPaneAry = new String[aryLenth];
        System.arraycopy(tmpAllIDAry, 0, allIDAry, 0, aryLenth);
        System.arraycopy(tmpAllFileAsAry, 0, allFileAsAry, 0, aryLenth);
        System.arraycopy(tmpAllEmailAddrAry, 0, allEmailAddrAry, 0, aryLenth);
        System.arraycopy(tmpStrToShowOnPopPaneAry, 0, strToShowOnPopPaneAry, 0, aryLenth);
        // 至此得到allIDAry/allFileAsAry/allEmailAddrAry/strToShowOnPopPane的内容------------------------------------------------------------------------------------

        // jList.setModel (new QuickInfoModel (strToShowOnPopPaneAry)); //设置模式 //目前第一版不准备在列表上显示图标。
    }

    /*
     * 计算字符串显示时的长度，双字节字符占两格
     */
    private int getNeededLengthForShow(
            String prmStr) {
        final int tmpLength = prmStr.length();
        int tmpActualLength = tmpLength;
        for (int i = 0; i < tmpLength; i++) {
            if (prmStr.charAt(i) > 127) {
                tmpActualLength++;
            }
        }
        return tmpActualLength;
    }

    /*
     *
     */
    private boolean isCurentWordAMatchedOne(
            int prmCaretPos) {
        int tmpIndex = getCurentWordIndex(prmCaretPos);
        Object tmpValue = (selectedFileAsesVec.size() > tmpIndex) ? selectedFileAsesVec.get(tmpIndex) : null;
        return (tmpValue != null && ((String) tmpValue).length() > 0);
    }

    /*
     *
     */
    private void selectCurrentWord(
            int prmCaretPos,
            boolean isForward) {
        String tmpContent = getText();
        int tmpStart = getLastSemiPos(prmCaretPos);
        int tmpEnd = tmpContent.indexOf(';', tmpStart + 1);
        tmpEnd = (tmpEnd == -1) ? tmpContent.length() : tmpEnd + 1;
        if (isForward) {
            select(tmpStart + 1, tmpEnd);
        } else {
            // @NOTE:因为select方法的结果必然是由前向后的选中，而这里需要由后往前的选中。只好用moveCaretPosition方法。
            setCaretPosition(tmpEnd);
            moveCaretPosition(tmpStart + 1);
        }
    }

    /*
     *
     */
    private int getLastSemiPos(
            int prmCaretPos) {
        String tmpStr = getText().substring(0, prmCaretPos);
        int tmpPos = tmpStr.indexOf(';');
        for (int i = tmpPos; i != -1;) {
            i = tmpStr.indexOf(';', tmpPos + 1);
            if (i != -1) {
                tmpPos = i;
            }
        }
        return tmpPos;
    }

    /*
     * 改方法判断焦点所在的词的位置号。@NOTE：本方法不是返回选中的词的位置号，只关心光标位置。
     */
    private int getCurentWordIndex(
            int prmCaretPos) {
        String tmpStr = getText().substring(0, prmCaretPos);

        int tmpIndex = 0;
        for (int tmpPos = tmpStr.indexOf(';'); tmpPos != -1; tmpIndex++) {
            tmpPos = tmpStr.indexOf(';', tmpPos + 1);
        }
        return tmpIndex;
    }

    /*
     *
     */
    private String getWordAtPos(
            int prmCaretPos) {
        int tmpLastSemiPos = getLastSemiPos(prmCaretPos);
        int tmpNextSemiPos = getText().indexOf(';', tmpLastSemiPos + 1);
        // 表示没有，只有一个
        if (tmpNextSemiPos == -1) {
            tmpNextSemiPos = getText().length();
        }
        return CASUtility.clearHeadSpace(getText().substring(tmpLastSemiPos + 1, tmpNextSemiPos));
    }

    /*
     *
     */
    private void preparePopupPane() {
        // 清空
        matchedItemsVec.removeAllElements();
        replaceWithStrVec.removeAllElements();

        // 得到目前已有的内容是是什么。
        int tmpCurCarePos = getCaretPosition();
        int tmpLastSemiPos = getLastSemiPos(tmpCurCarePos); // 判断是不是在输入第一个地址。
        String tmpContent = this.getText().substring(0, tmpCurCarePos);

        // @NOTE:(tmpLastSemiPos != -1)表示找到了“；”，即不是在输入第一个地址-----------------------------------
        String tmpEditingStr =
                (tmpLastSemiPos != -1) ? tmpContent.substring(tmpLastSemiPos + 1, tmpCurCarePos) : tmpContent;
        tmpEditingStr = CASUtility.clearHeadSpace(tmpEditingStr);
        // 当前正在编辑的词的光标前的全部净内容的长度。用户敲回车或鼠标选择jList中的一项时会用到
        caretPosInEditingMail = tmpEditingStr.length();

        // 以下负责将匹配的内容显示在popupPane上=============================================================================
        popUpPanel.setVisible(false);
        if (tmpCurCarePos == 0 || tmpEditingStr.trim().endsWith(";") || tmpEditingStr.trim().length() < 1) // 若文本框为空或上一个地址已输入结束，且尚未输入新的内容。
        {
            return;
        }
        Vector tmpItemsIncludInput = new Vector();
        Vector tmpReplaWithStrVec = new Vector();
        String tmpCurValue = null; // 用于暂存Vector中的每一个元素的值的临时变量。
        for (int i = 0; i < aryLenth; i++) {
            // 先比较每个fileAs值是否和输入匹配。
            tmpCurValue = allFileAsAry[i];
            if (tmpCurValue.length() >= caretPosInEditingMail) {
                if (tmpEditingStr.equalsIgnoreCase(tmpCurValue.substring(0, caretPosInEditingMail))) {
                    matchedItemsVec.addElement(strToShowOnPopPaneAry[i]);
                    replaceWithStrVec.addElement(allFileAsAry[i]);
                    continue;
                } else if (tmpCurValue.toLowerCase().indexOf(tmpEditingStr.toLowerCase()) > 0) // 如果不匹配，则看有没有包含关系，如果有，则装入另一个Vector，跟在PopupPane的尾部显示。
                {
                    // 加到另一个Vector中，并显示再POP上。
                    tmpItemsIncludInput.addElement(strToShowOnPopPaneAry[i]);
                    tmpReplaWithStrVec.addElement(allFileAsAry[i]);
                    // NOTE:这里不可用continue；因为即使有包含关系仍然需要判断与地址的匹配关系。
                }
            }

            // 再比较每个mailAddress值是否和输入匹配。
            tmpCurValue = allEmailAddrAry[i];
            if (tmpCurValue.length() >= caretPosInEditingMail) {
                if (tmpEditingStr.equalsIgnoreCase(tmpCurValue.substring(0, caretPosInEditingMail))) {
                    matchedItemsVec.addElement(strToShowOnPopPaneAry[i]);
                    replaceWithStrVec.addElement(allEmailAddrAry[i]);
                } else if (tmpCurValue.toLowerCase().indexOf(tmpEditingStr.toLowerCase()) > 0) // 如果不匹配，则看有没有包含关系，如果有，则装入另一个Vector，跟在PopupPane的尾部显示。
                {
                    // 加到另一个Vector中，并显示再POP上。
                    tmpItemsIncludInput.addElement(strToShowOnPopPaneAry[i]);
                    tmpReplaWithStrVec.addElement(allEmailAddrAry[i]);
                }
            }
        }
        matchedItemsVec.addAll(tmpItemsIncludInput); // 将包含输入内容的项，追加到matchedItemsVec中，并显示到POP上。
        replaceWithStrVec.addAll(tmpReplaWithStrVec); // 将包含输入内容的项的具体替换字符串也追加到replaceWithStrVec上。

        if (matchedItemsVec.size() != 0) // 如果有匹配。－－－－－－－
        {
            // 重新绘制列表框
            jList.setModel(new QuickInfoModel(matchedItemsVec));
            // 默认选中的为第一项
            jList.setSelectedIndex(0);
            // 设置jScrollPane出现的位置
            int tmpX = this.getX() - 5 + 6 * tmpCurCarePos;
            if (getWidth() - popUpPanel.getWidth() >= 0) {
                if (tmpX > getWidth() + getX() - popUpPanel.getWidth()) // 有一中可能性，如果getWidth() -
                                                                        // popUpPanel.getWidth();则出错了，
                {
                    tmpX = getWidth() + getX() - popUpPanel.getWidth();
                }
            } else {
                //
                int tmpMaxX = this.getWidth(); // 如果这个JScrollPane的位置的X坐标大于本TextField的最右端的坐标，则显示不可已向右移动的，X坐标定在最右端的坐标
                int tmpConMax = container.getWidth() - popUpPanel.getWidth();
                tmpX = getX() + Math.min(tmpX - getX(), Math.min(tmpMaxX, tmpConMax));// 目的是为了不让JScrollPane越界
            }
            int tmpY = getY() + getHeight();
            if (tmpY + popUpPanel.getHeight() > container.getY() + container.getHeight()) // @NOTE: 这里还需要修改
            {
                tmpY = this.getY() - popUpPanel.getHeight() + 2;
            }
            popUpPanel.setBounds(tmpX, tmpY - 2, 280, 100);
            popUpPanel.setVisible(true);
        }
    }

    /*
     * 负责用选定的内容替换编辑域中的内容。
     */
    private void replaceContentFromPop() {
        // 先让pane不可见
        popUpPanel.setVisible(false);

        // 准备 selectedIDAddrVec;selectedMailAddrVec;selectedFileAsesVec已备外部取用
        // @NOTE:必须在setText()之前调用，否则里面的caretPos会改变出错-------------------------------------
        repairSelectedVecs(matchedItemsVec.elementAt(jList.getSelectedIndex()).toString(), true, MODIFY);

        int tmpCaretPos = this.getCaretPosition(); // 记下当前光标位置，便于查找当前正在编辑的词，和替换完后重设光标落点。
        // 得到用于替换正在编辑的词的Str。
        String tmpReplaceWithStr = replaceWithStrVec.elementAt(jList.getSelectedIndex()).toString();

        // 先准备好正在编辑的地址之前的所有已编辑好的地址的字符串。
        int tmpSemiPos = getLastSemiPos(tmpCaretPos);
        String tmpMailsBefEditingMailStr =
                tmpSemiPos != -1 ? getText().substring(0, tmpSemiPos + 1).concat(CASUtility.SPACE)
                        : CASUtility.EMPTYSTR;

        int tmpAddedCharCount;
        // 如果光标在文本域中所有已有文字之后，则只追加一个分号。
        if (tmpCaretPos >= getText().length()) {
            this.setText(tmpMailsBefEditingMailStr.concat(tmpReplaceWithStr).concat("; "));
            tmpAddedCharCount = 2;
        }
        // 如果光标在文本域中已有文字的中间某处，则回车后光标所在的词被替换掉。
        else {
            String tmpStrBehindCaret = getText().substring(tmpCaretPos); // 先取出光标后所有文字
            // 因为被删掉的必然是个已匹配的项，那么后面一个词之前如果已存在一个分隔用的“ ”，就直接加“;"，否则要用"; "作为后面文字的开头。

            int tmpCurMailEndPos = tmpStrBehindCaret.indexOf(";"); // 先得到这个剩下的字串中第一个地址的结束位置。
            // 光标之后有分号，则分号之前的文字需要被替换掉。
            if (tmpCurMailEndPos != -1) {
                tmpStrBehindCaret = tmpStrBehindCaret.substring(tmpCurMailEndPos);
                tmpAddedCharCount = 1;
            }
            // 没有分号，说明光标之后全部应被替换掉。
            else {
                tmpStrBehindCaret = "; ";
                tmpAddedCharCount = 2;
            }
            // 至此光标之后需要保留的内容(也即从第一个分号到最后的所有内容)已知道，并存于tmpStrBehindCaret中------------------------------------------------

            setText(tmpMailsBefEditingMailStr + tmpReplaceWithStr + tmpStrBehindCaret);
        }

        // 设置光标位置 @NOTE:因为按规定，从PopupPane上替换被替换的词会被末尾为“； ”分号加空格的词替换，所以加2----------
        setCaretPosition(getText().indexOf(";", tmpCaretPos) + tmpAddedCharCount);
    }

    /*
     * 准备 selectedIDAddrVec;selectedMailAddrVec;selectedFileAsesVec已备外部取用
     * @para prmReplaceWithStr: 用于替换的字串，在pop显示时是replaceWithVec中的一 个元素，没有显示时是当前编辑的词（两个分号或空格或空格和分号之间）。
     * @NOTE:prmReplaceWithStr不可为null！！！！
     */
    private void repairSelectedVecs(
            String prmReplaceWithStr,
            boolean prmMatched,
            int prmMode) {
        String tmpID = null, tmpFileAs = null, tmpMail = null; // 准备三个String，用于暂存数据。

        if (prmMatched) // 如果和popuppane中的某一项匹配。//@NOTE:对于通讯组列表的处理也在这里.
        {
            for (int i = 0; i < aryLenth; i++) // 得到选中的项是所有项中的（而不是匹配并显示在popuppane上的）第几项。
            {
                // 先比较每个fileAs值是否和输入匹配。
                if (prmReplaceWithStr.startsWith(allFileAsAry[i] + ' ')
                        && prmReplaceWithStr.endsWith(' ' + allEmailAddrAry[i])
                        && prmReplaceWithStr
                                .substring(allFileAsAry[i].length(),
                                        prmReplaceWithStr.length() - allEmailAddrAry[i].length()).trim().length() == 0) {
                    tmpID = allIDAry[i];
                    tmpFileAs = allFileAsAry[i];
                    tmpMail = allEmailAddrAry[i];
                    break;
                }
            }
            // 为通讯组列表加一个判断:如果是通讯组列表,则在tmpMail字段中存通讯组成员中的信息.@NOTE:故意在循环外判断,减少判断次数.
            int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
            int tmpNodeID = CASUtility.getAppIndexByFolderID(tmpAppIndex);
            PIMRecord tmpContacRec =
                    CASControl.ctrl.getModel().selectRecord(tmpAppIndex, Integer.parseInt(tmpID), tmpNodeID);
            // if (new
            // Short((short)1).equals(tmpContacRec.getFieldValue(PIMPool.pool.getKey(ContactDefaultViews.TYPE))))
            // //tmpID对应的Record是通讯组列表)
            // {
            // //取出tmpID对应的Record的成员字段值.
            // tmpMail =
            // (String)tmpContacRec.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.MEMBER_LIST));
            // }
        } else {
            tmpID = CASUtility.EMPTYSTR;
            tmpFileAs = CASUtility.EMPTYSTR;
            tmpMail = prmReplaceWithStr;
        }

        // 得到当前编辑的词是第几个词。也是对应于外部将抓取的Vec的第几位。
        int tmpIndex = getCurentWordIndex(getCaretPosition());

        if (tmpMail == null || tmpMail.length() < 1) {
            if (selectedIDAddrVec != null && !selectedIDAddrVec.isEmpty()) {
                selectedFileAsesVec.removeElementAt(tmpIndex);
                selectedIDAddrVec.removeElementAt(tmpIndex);
                selectedMailAddrVec.removeElementAt(tmpIndex);
            }
        } else if (prmMode == MODIFY) {
            if (tmpMail.length() < 1) {

                if (selectedMailAddrVec.size() <= 1) {
                    selectedIDAddrVec.removeAllElements();
                    selectedFileAsesVec.removeAllElements();
                    selectedMailAddrVec.removeAllElements();
                } else {
                    selectedIDAddrVec.removeElementAt(tmpIndex);
                    selectedFileAsesVec.removeElementAt(tmpIndex);
                    selectedMailAddrVec.removeElementAt(tmpIndex);
                }
            } else {
                if (tmpIndex < selectedIDAddrVec.size()) {
                    selectedIDAddrVec.setElementAt(tmpID, tmpIndex);
                    selectedFileAsesVec.setElementAt(tmpFileAs, tmpIndex);
                    selectedMailAddrVec.setElementAt(tmpMail, tmpIndex);
                } else {
                    selectedIDAddrVec.addElement(tmpID);
                    selectedFileAsesVec.addElement(tmpFileAs);
                    selectedMailAddrVec.addElement(tmpMail);
                }
            }
        }

        else if (prmMode == INSERT) {
            selectedIDAddrVec.insertElementAt(tmpID, tmpIndex);
            selectedFileAsesVec.insertElementAt(tmpFileAs, tmpIndex);
            selectedMailAddrVec.insertElementAt(tmpMail, tmpIndex);

            // TODO:抓出所有光标后的词。
            selectedIDAddrVec.setElementAt(tmpID, tmpIndex + 1);
            selectedFileAsesVec.setElementAt(tmpFileAs, tmpIndex + 1);
            selectedMailAddrVec.setElementAt(tmpMail, tmpIndex + 1);
        }
        // @NOTE：删除暂时放在DELETE键事件中处理，不在此处考虑。
    }

    // 鼠标事件处理============================================================================================================
    /**
     * 鼠标监听器中的方法
     * 
     * @param mouseEvent
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == jList) {
            replaceContentFromPop();
        }
    }

    /**
     * 鼠标监听器中的方法
     * 
     * @param mouseEvent
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent mouseEvent) {
        if (mouseEvent.getSource() == this) {
            int tmpCarePos = getCaretPosition();
            if (isCurentWordAMatchedOne(tmpCarePos)) {
                selectCurrentWord(tmpCarePos, true);
            } else if (getSelectionStart() != getSelectionEnd()) // 杜绝多选的情况
            {
                select(tmpCarePos, tmpCarePos);
            }
        }
    }

    /**
     * 鼠标监听器中的方法
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * 鼠标监听器中的方法
     * 
     * @param mouseEvent
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent mouseEvent) {
    }

    /**
     * 鼠标监听器中的方法
     * 
     * @param mouseEvent
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent mouseEvent) {
    }

    /**
     * @return boolean 键盘事件是否是此组件触发的
     */
    public boolean isKeyEvent() {
        return isKeyEvent;
    }

    // 重载父类的方法
    protected void processKeyEvent(
            java.awt.event.KeyEvent e) {
        isKeyEvent = popUpPanel.isShowing();
        super.processKeyEvent(e);
        isKeyEvent = false;
    }

    // 键盘事件处理============================================================================================================
    /**
     * 处理回车事件，这种事件标志着一个联系人输入完成。
     * 
     * @param keyEvent
     *            the command line arguments
     */
    public void keyPressed(
            KeyEvent keyEvent) {
        setClickedKey(NULL_VK_MASK, NULL_VK_MASK);
        int tmpKey = keyEvent.getKeyCode();
        if (tmpKey == KeyEvent.VK_ENTER) // 回车键的处理----------------------------------------------
        {
            // 若列表显示（即输入的内容不在列表中有匹配项）--------------------
            if (popUpPanel.isShowing()) {
                replaceContentFromPop();
            }
            // 如果输入的内容不在列表中时，打回车发生的动作-------------------
            else {
                String tmpContent = getText();
                if (!tmpContent.trim().endsWith(";") && tmpContent.trim().length() != 0) // 文本域的最后字符不为“；”且文本域不为空
                {
                    setText(tmpContent + ';'); // 加上“；”
                } else // 选中全部。
                {
                    selectAll();
                }
            }
        } // 回车键的事件结束

        else if (tmpKey == KeyEvent.VK_DELETE) // 删除键的处理----------------------------------------------
        {

            setClickedKey(tmpKey, NULL_VK_MASK);
            String tmpStr = getText();
            // @NOTE:此处用getSelectionStart而不用getCaretPos，因为考虑到
            // 选中一个词时，光标的位置其实是在词的外部了。而用getSelectionStart恰可避免。
            int tmpCaretPos = getSelectionStart(); // getCaretPosition();
            if (getText().equals(getSelectedText())) {
                selectedFileAsesVec.removeAllElements();
                selectedIDAddrVec.removeAllElements();
                selectedMailAddrVec.removeAllElements();
            } else if (isCurentWordAMatchedOne(tmpCaretPos)) {
                // Vecs中要少一个元素了。
                int tmpIndex = getCurentWordIndex(tmpCaretPos);
                selectedFileAsesVec.remove(tmpIndex);
                selectedIDAddrVec.remove(tmpIndex);
                selectedMailAddrVec.remove(tmpIndex);
            } else if (tmpStr.substring(tmpCaretPos).startsWith(";")) // 如果将被删的字符是“;”,让这个动作落空，什么也不干。
            {
                setCaretPosition(tmpStr.length());
            }
            // 将在一个非匹配项中点delete。而且不是在最末尾部。
            // @NOTE；之所以在这处理删除键，因为release时东西已被删掉了，判断起来麻烦。

            else if (tmpCaretPos < tmpStr.length()) {
                int tmpLastSemiPos = getLastSemiPos(tmpCaretPos);
                String tmpEditingStrBeforCare = tmpStr.substring(tmpLastSemiPos + 1, tmpCaretPos);
                tmpEditingStrBeforCare = CASUtility.clearHeadSpace(tmpEditingStrBeforCare);
                int tmpNextSemiPos = tmpStr.indexOf(';', tmpCaretPos);
                if (tmpNextSemiPos == -1) {
                    tmpNextSemiPos = tmpStr.length();
                }
                tmpStr = tmpStr.substring(tmpCaretPos + 1, tmpNextSemiPos);
                repairSelectedVecs(tmpEditingStrBeforCare.concat(tmpStr), false, MODIFY);
            }
        }

        else if (tmpKey == KeyEvent.VK_BACK_SPACE) // 后退键的处理----------------------------------------------
        {
            setClickedKey(tmpKey, NULL_VK_MASK);
            int tmpCarePos = getCaretPosition();
            if (tmpCarePos > 0 && getText().substring(tmpCarePos - 1).startsWith(";")) {
                setCaretPosition(0);
            }
        } else if (tmpKey == KeyEvent.VK_LEFT || tmpKey == KeyEvent.VK_RIGHT) // 左右键的处理----------------------------------------------
        {
            return;
        } else if (tmpKey == KeyEvent.VK_Z && keyEvent.isControlDown()) {
            // 设置CTRL+Z事件已经响应
            setClickedKey(tmpKey, KeyEvent.CTRL_MASK);
            // 处理undo操作
            this.undoEvent();
        } else if (tmpKey == KeyEvent.VK_Y && keyEvent.isControlDown()) {
            // 设置CTRL+Y事件已经响应
            setClickedKey(tmpKey, KeyEvent.CTRL_MASK);
            // 处理redo操作
            this.redoEvent();
        } else // 其他键的处理----------------------------------------------
        {
            // @NOTE:此处用getSelectionStart而不用getCaretPos，因为考虑到
            // 选中一个词时，光标的位置其实是在词的外部了。而用getSelectionStart恰可避免。
            int tmpCaretPos = getSelectionStart(); // getCaretPosition();
            if (getText().equals(getSelectedText())) {
                selectedFileAsesVec.removeAllElements();
                selectedIDAddrVec.removeAllElements();
                selectedMailAddrVec.removeAllElements();
            } else if (isCurentWordAMatchedOne(tmpCaretPos)) {
                // Vecs中要少一个元素了。
                // @note 这里需要处理
                int tmpIndex = getCurentWordIndex(tmpCaretPos);
                selectedFileAsesVec.set(tmpIndex, CASUtility.EMPTYSTR);
                selectedIDAddrVec.set(tmpIndex, CASUtility.EMPTYSTR);
                selectedMailAddrVec.set(tmpIndex, CASUtility.EMPTYSTR);

                if (tmpCaretPos == 0) {
                    setText(getText().substring(0, tmpCaretPos).concat(";")
                            .concat(getText().substring(getSelectionEnd())));
                    setCaretPosition(tmpCaretPos);
                } else {
                    setText(getText().substring(0, tmpCaretPos).concat(" ;")
                            .concat(getText().substring(getSelectionEnd())));
                    setCaretPosition(tmpCaretPos + 1);
                }
            }
        }
    }

    // 按键释放时~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * 按键释放时
     * 
     * @param keyEvent
     *            键盘事件
     */
    public void keyReleased(
            KeyEvent keyEvent) {
        setClickedKey(NULL_VK_MASK, NULL_VK_MASK);
        int tmpKey = keyEvent.getKeyCode();
        char tmpChar = keyEvent.getKeyChar();
        int tmpCaretPos = getCaretPosition();

        if (tmpKey == KeyEvent.VK_DOWN) // 向下箭头键的处理----------------------------------------------
        {
            int tmpCurSelecIndex = jList.getSelectedIndex();
            // TODO：mailAddrVec有问题，应该用当前满足匹配条件的记录集合的Vec的Size！
            if (tmpCurSelecIndex < matchedItemsVec.size() - 1) {
                jList.setSelectedIndex(tmpCurSelecIndex + 1);
                SwingUtilities.invokeLater(this);
            }
        }

        else if (tmpKey == KeyEvent.VK_UP) // 向上箭头键的处理----------------------------------------------
        {
            int tmpCurSelecIndex = jList.getSelectedIndex();
            if (tmpCurSelecIndex > 0) {
                jList.setSelectedIndex(tmpCurSelecIndex - 1);
                SwingUtilities.invokeLater(this);
            }
        }
        // 至此已经排除了上下键--------------------------------------------------

        else if (tmpKey == KeyEvent.VK_LEFT || tmpKey == KeyEvent.VK_RIGHT) {
            if (isCurentWordAMatchedOne(tmpCaretPos)) {
                selectCurrentWord(tmpCaretPos, tmpKey == KeyEvent.VK_RIGHT);
            } else if (getSelectionStart() != getSelectionEnd()) // 杜绝多选的情况
            {
                select(tmpCaretPos, tmpCaretPos);
            }
            preparePopupPane();
        }
        // 至此已经排除了上下左右键--------------------------------------------------

        else if (tmpKey == KeyEvent.VK_DELETE || tmpKey == KeyEvent.VK_ENTER) // 删除键的以在preessed中处理----------------------------------------------
        {
            return;
        }

        else if (tmpKey == KeyEvent.VK_HOME) // HOME键的处理----------------------------------------------
        {
            // 允许全选，但不允许部分选。也不允许
            if (getText().equals(getSelectedText())) {
                return;
            } else if (isCurentWordAMatchedOne(tmpCaretPos)) {
                selectCurrentWord(tmpCaretPos, true);
            } else {
                select(tmpCaretPos, tmpCaretPos);
            }
        } else if (tmpKey == KeyEvent.VK_BACK_SPACE && tmpCaretPos == 0) {
            if (getText().length() < 1) {
                if (selectedFileAsesVec != null && selectedFileAsesVec.size() > 0) {
                    selectedFileAsesVec.removeAllElements();
                    selectedIDAddrVec.removeAllElements();
                    selectedMailAddrVec.removeAllElements();
                }
            }
            return;
        }

        else if (tmpKey == KeyEvent.VK_CONTROL || tmpKey == KeyEvent.VK_SHIFT) {
            return;
        }
        // 至此已经排除了其他特殊键--------------------------------------------------
        else if (tmpKey == KeyEvent.VK_Z && keyEvent.isControlDown()) {
            preparePopupPane();
        } else if (tmpKey == KeyEvent.VK_Y && keyEvent.isControlDown()) {
            preparePopupPane();
        } else {
            preparePopupPane();
            if (tmpChar == ';' || tmpChar == '；' || tmpChar == ',' || tmpChar == '，' || tmpChar == '。' // 有些键要被视为敲入了回车键。
                    || tmpChar == CustOpts.BIAS || tmpChar == ':' || tmpChar == '<' || tmpChar == '>' || tmpChar == '?') {
                if (!(tmpCaretPos == getText().length())) // 在最末尾。
                {
                    String tmpContent = getText();
                    String tmpShow =
                            tmpContent.substring(0, tmpCaretPos - 1).concat("; ")
                                    .concat(tmpContent.substring(tmpCaretPos));
                    String tmpGetText =
                            tmpContent.substring(0, tmpCaretPos - 1).concat(";")
                                    .concat(tmpContent.substring(tmpCaretPos));
                    // 取到下一个字符。判断是否为“;”
                    char tmpCh = tmpGetText.charAt(tmpCaretPos);
                    // 如果下个字符为空,在";"的前面加上一个“;”号，危险动作
                    if (tmpCh == ';') {
                        setText(tmpShow);
                        int tmpIndex = getCurentWordIndex(tmpCaretPos - 1);
                        String tmpWordAtPos = getWordAtPos(tmpCaretPos - 1);
                        // 在插入前的“;”前面没有字符串
                        // NOTE 如果前面的一个时“ ”（空格）？？
                        if (tmpWordAtPos == null || tmpWordAtPos.length() < 1) {
                            selectedFileAsesVec.insertElementAt(CASUtility.EMPTYSTR, tmpIndex);
                            selectedIDAddrVec.insertElementAt(CASUtility.EMPTYSTR, tmpIndex);
                            selectedMailAddrVec.insertElementAt(CASUtility.EMPTYSTR, tmpIndex);
                        } else {
                            selectedFileAsesVec.insertElementAt(CASUtility.EMPTYSTR, tmpIndex);
                            selectedIDAddrVec.insertElementAt(CASUtility.EMPTYSTR, tmpIndex);
                            selectedMailAddrVec.insertElementAt(tmpWordAtPos, tmpIndex);

                            // 全都设置为空
                            selectedFileAsesVec.set(tmpIndex + 1, CASUtility.EMPTYSTR);
                            selectedIDAddrVec.set(tmpIndex + 1, CASUtility.EMPTYSTR);
                            selectedMailAddrVec.set(tmpIndex + 1, CASUtility.EMPTYSTR);
                        }
                    } else {
                        setText(tmpShow);
                        int tmpIndex = getCurentWordIndex(tmpCaretPos - 1); // CaretPos = 2; tmpIndex = 1
                        // size = 1;
                        selectedFileAsesVec.set(tmpIndex, CASUtility.EMPTYSTR);
                        selectedIDAddrVec.set(tmpIndex, CASUtility.EMPTYSTR);
                        selectedMailAddrVec.set(tmpIndex, getWordAtPos(tmpCaretPos - 1));

                        selectedFileAsesVec.insertElementAt(CASUtility.EMPTYSTR, tmpIndex + 1);
                        selectedIDAddrVec.insertElementAt(CASUtility.EMPTYSTR, tmpIndex + 1);
                        selectedMailAddrVec.insertElementAt(getWordAtPos(tmpCaretPos), tmpIndex + 1);
                    }
                } else {
                    // 不需要添加，不需要在Vector中加什么?
                    // 有一情况为“;;;”即所有的元素都为";"的是否也满足这种情况的
                    if (tmpCaretPos == 1) {
                        selectedFileAsesVec.add(CASUtility.EMPTYSTR);
                        selectedIDAddrVec.add(CASUtility.EMPTYSTR);
                        selectedMailAddrVec.add(CASUtility.EMPTYSTR);
                    } else {
                        // 查找在插入位置的前一个字符是否为";"
                        StringBuffer tmpGetText = new StringBuffer(getText());
                        if (tmpGetText.length() > 0) {
                            tmpGetText.setCharAt(tmpGetText.length() - 1, ';'); // 把最后的一个元素改为“;”
                        }
                        char tmpCh = tmpGetText.charAt(tmpCaretPos - 2);

                        if (tmpCh == ';') {
                            selectedFileAsesVec.add(CASUtility.EMPTYSTR);
                            selectedIDAddrVec.add(CASUtility.EMPTYSTR);
                            selectedMailAddrVec.add(CASUtility.EMPTYSTR);
                        }
                    }
                }
            }
            // @TODO: CTRL＋ V要特殊处理。但不是用这个判断。
            /*
             * else if (tmpKey == keyEvent.VK_PASTE) { //@NOTE: 要高鹏处理粘贴板。同步处理好selectedVecs }
             */
            else {
                // 如果当前的文本为空的时候
                String tmpWord = getWordAtPos(tmpCaretPos);
                if (tmpWord.trim().length() > 0) {
                    repairSelectedVecs(tmpWord, false, MODIFY);
                }
            }
        }
    }

    /**
     * 列表的事件
     * 
     * @param keyEvent
     *            键盘事件
     */
    public void keyTyped(
            KeyEvent keyEvent) {
    }

    // 焦点事件--------------------------------------------------------------------------------------------------
    /**
     * 处理焦点事件
     * 
     * @param focusEvent
     *            焦点事件
     */
    public void focusLost(
            FocusEvent focusEvent) {
        if (focusEvent.getSource() == this) {
            popUpPanel.setVisible(false);
        }
    }

    /**
     * 处理焦点事件
     * 
     * @param focusEvent
     *            焦点事件
     */
    public void focusGained(
            FocusEvent focusEvent) {
    }

    // ---------------------------------------------------------------------------------------------------------

    // 添加右键地址区域选取功能
    /**
     * 检查地址信息的格式，并统计范围
     */
    public void check() {
        // 清空地址标志
        if (addressRanges == null) {
            addressRanges = new Vector();
        } else {
            addressRanges.clear();
        }
        // 取得文本信息和长度
        StringBuffer text = new StringBuffer(getText().trim());
        // 获得第一个联系人的信息
        // 前一个分隔符的位置
        int lastSepLocation = -1;
        // 后一个分隔符的位置
        int nextSepLocation;

        while ((lastSepLocation < text.length()) && text.length() != 0) {
            // 删除分隔符后的空格
            while ((lastSepLocation + 1 < text.length()) && (text.charAt(lastSepLocation + 1) == ' ')) {
                text.delete(lastSepLocation + 1, lastSepLocation + 2);
            }
            // 获得下一个分隔符的位置
            nextSepLocation = text.indexOf(";", lastSepLocation + 1);
            if (nextSepLocation == -1) {
                nextSepLocation = text.length();
            }
            // 删除分隔符前的空格
            while (text.charAt(nextSepLocation - 1) == ' ') {
                text.delete(nextSepLocation - 1, nextSepLocation);
                nextSepLocation--;
            }
            // 保存地址区域的信息，若为空信息则删除。
            if (lastSepLocation == nextSepLocation - 1) {
                text.delete(lastSepLocation, nextSepLocation);
            } else {
                if (lastSepLocation != -1) {
                    text.insert(lastSepLocation + 1, ' ');
                    lastSepLocation++;
                    nextSepLocation++;
                }
                addressRanges.addElement(new AddressRange(lastSepLocation + 1, nextSepLocation));
                lastSepLocation = nextSepLocation;
            }
            // 获得下一段地址信息的起始
        } // while

        setText(text.toString());
    }

    /**
     * @called by: emo.pim.pimview.dialog.contacts.GeneralPane.java;
     */
    String getIDStr() {
        String tmpIDStr = null;
        for (int i = 0, tmpSize = selectedMailAddrVec.size();;) {
            tmpIDStr += (String) selectedIDAddrVec.get(i);
            if (++i < tmpSize) {
                tmpIDStr += ',';
            } else {
                break;
            }
        }
        return tmpIDStr;
    }

    /**
     * 设置内容
     * 
     * @param prmContent
     *            要显示的内容
     */

    public void setContents(
            String prmContent) {
        // 传入为空则不做处理.
        if (prmContent == null || prmContent.length() < 1) {
            return;
        }
        alarmed = false; // 将alarm标志复位，以便在setConten方法被调之后检查Alarm是否被置位，如果被置说明传入过非法邮件地址（但是否弹过提示信息还要看isParentAEDialog的标志状态）
        String tmpItem = prmContent; // 用来在遍历中暂存以逗号连接的每一个元素.
        int tmpPos = 0;
        int i;
        int j;
        // 遍历出每一个逗号,取出期间的元素(必定是符合""<>格式的),并将其进行分解.
        while ((i = prmContent.indexOf(CASUtility.COMMA, tmpPos)) != -1) {
            tmpItem = prmContent.substring(tmpPos, i); // 得到了一个联系人元素.
            tmpItem = tmpItem.trim(); // 确保两端没有空格(因为有的邮件客户在回复的时候会使地址中多出空格,如OutLookExpress).

            j = tmpItem.indexOf(CASUtility.DOUBlEQUOTATION, 1);
            if (j < 1) // 容错处理，防止QuickInfoField发生错乱，末尾留有逗号。
            {
                // TODO:WriteToLog();
                break;
            }
            selectedFileAsesVec.add(tmpItem.substring(1, j)); // 将PIMUtility.EMPTYSTR间的内容存入selectedFileAsesVec
            // 将<>间的内容存入selectedMailAddrVec,之前要确保两端没有空格(因为有的邮件客户在回复的时候会使地址中多出空格,如OutLookExpress).
            tmpItem = tmpItem.substring(j + 1).trim();
            if (!(tmpItem.startsWith(CASUtility.LEFTSHARPBRACKET) && tmpItem.endsWith(CASUtility.RIGHTSHARPBRACKET)
                    && tmpItem.indexOf('@') > 1 && tmpItem.length() > 4)) {
                if (!alarmed) {
                    alarmed = true; // 如果邮件地址不合法，则确保alarmed标志被设置为true，在第一次发现不合法，且不是用于联系人文本域时，弹错误信息。
                    if (!actAsLinkManField) {
                        // 程序发现联系人栏中存在格式错误的邮件地址,该含有无效邮件地址的联系人将被自动忽略.请检查修正后重新加入该联系人.
                        SOptionPane.showErrorDialog(MessageCons.W10421);
                    }
                }
                if (actAsLinkManField) // 如果是联系人域actAsLinkManField标志为true，则不会弹错误信息，内容照加。
                {
                    selectedMailAddrVec.add(CASUtility.EMPTYSTR);
                    selectedIDAddrVec.add(CASUtility.EMPTYSTR);
                } else // 如果是联系人域actAsLinkManField标志为false，则会弹一次错误信息，该条内容被忽略。
                {
                    selectedFileAsesVec.remove(selectedFileAsesVec.lastElement());
                }
            } else {
                selectedMailAddrVec.add(tmpItem.substring(1, tmpItem.length() - 1));
                selectedIDAddrVec.add(CASUtility.EMPTYSTR);
            }
            tmpPos = i + 1;
        }

        tmpItem = prmContent.substring(tmpPos).trim(); // 取出剩下的内容并去掉空格.
        if (tmpItem.length() > 0) // 此判断针对传入的参数末尾带有一个逗号的情况,此时的处理是忽略,而不报错.
        { // 对循环无法遍历到的最末尾的一个元素做相同的解析处理.
            j = tmpItem.indexOf('\"', 1); // 有没有引号.
            if (j != -1) // 有的话取出表示为内容.
            {
                selectedFileAsesVec.add(tmpItem.substring(1, j));
            } else // 没有引号的话,表示为以空字串填充.
            {
                selectedFileAsesVec.add(CASUtility.EMPTYSTR);
                if (tmpItem.startsWith(CASUtility.DOUBlEQUOTATION)) // 容错:因为是从1位置搜索有没有引号的,所以要看一下0位置是什么字符,万一是引号则将其舍弃..
                {
                    tmpItem = tmpItem.substring(1);
                }
            }
            tmpItem = tmpItem.substring(j + 1).trim(); // 如果找到引号了从引号的后面开始截取,如果没有找到,则j为-1,从0位置截取.
            if (!(tmpItem.startsWith(CASUtility.LEFTSHARPBRACKET) && tmpItem.endsWith(CASUtility.RIGHTSHARPBRACKET)
                    && tmpItem.indexOf('@') > 1 && tmpItem.length() > 4)) {
                if (!alarmed) {
                    alarmed = true; // 如果邮件地址不合法，则确保alarmed标志被设置为true，在第一次发现不合法，且不是用于联系人文本域时，弹错误信息。
                    if (!actAsLinkManField) { // 程序发现联系人栏中存在格式错误的邮件地址,该含有无效邮件地址的联系人将被自动忽略.请检查修正后重新加入该联系人.
                        SOptionPane.showErrorDialog(MessageCons.W10421);
                    }
                }
                if (actAsLinkManField) // 如果是联系人域actAsLinkManField标志为true，则不会弹错误信息，内容照加
                {
                    selectedMailAddrVec.add(CASUtility.EMPTYSTR);
                    selectedIDAddrVec.add(CASUtility.EMPTYSTR);
                } else // 如果是联系人域actAsLinkManField标志为false，则会弹一次错误信息，该条内容被忽略。
                {
                    selectedFileAsesVec.remove(selectedFileAsesVec.lastElement());
                }
            } else {
                selectedMailAddrVec.add(tmpItem.substring(1, tmpItem.length() - 1));
                selectedIDAddrVec.add(CASUtility.EMPTYSTR);
            }
        }
        // 使QuickInfoField上作相应的显示.
        String tmpStrForShow = CASUtility.EMPTYSTR;
        for (i = 0; i < selectedMailAddrVec.size(); i++) // 示如果没有表示为的，则用邮件名来表示
        {
            String tmpStr = (String) selectedFileAsesVec.get(i);
            if (tmpStr == null || tmpStr.length() < 1) {
                tmpStr = selectedMailAddrVec.get(i).toString();
            }
            tmpStrForShow = tmpStrForShow.concat(tmpStr).concat("; ");
        }
        setText(tmpStrForShow);
    }

    /**
     * 把邮件选择的收件人的值设置到文本框中
     * 
     * @param prmContactInfs
     *            选择的联系人的列表格式为“收件人，EmailAddr”
     * @param prmIndexVec
     *            修改已选择的删除了的联系人的索引列表
     * @called by: EmailHeader.java
     */
    public void setContents(
            Vector prmContactInfs,
            Vector prmIndexVec) {

        // 删除上次添加的联系人信息，因为在这次操作中间已经被删除
        if ((prmIndexVec != null) && (prmIndexVec.size() > 0) && (prmIndexVec.size() == selectedFileAsesVec.size()))

        {
            selectedFileAsesVec.removeAllElements();
            selectedMailAddrVec.removeAllElements();
            selectedIDAddrVec.removeAllElements();
        }
        // 判断上次选择的联系人信息有无改变
        else if (prmIndexVec != null && !prmIndexVec.isEmpty()) {
            // 如果有改变
            if (prmIndexVec.size() != selectedFileAsesVec.size()) {
                int tmpPos;
                for (int i = 0; i < prmIndexVec.size(); i++) {
                    tmpPos = Integer.parseInt((prmIndexVec.get(i).toString()));
                    selectedFileAsesVec.remove(tmpPos);
                    selectedMailAddrVec.remove(tmpPos);
                    selectedIDAddrVec.remove(tmpPos);
                }
            }
        }
        // 判断联系人信息是否为空
        if (prmContactInfs != null && !prmContactInfs.isEmpty()) {

            int tmpBeg = 0, tmpEnd = 0;
            String tmpContact, tmpEmailAddr, tmpId;
            Iterator tmpContactIte = prmContactInfs.iterator();
            while (tmpContactIte != null && tmpContactIte.hasNext()) {
                String tmpInf = tmpContactIte.next().toString();

                int tmpBegPos = tmpInf.indexOf(',');
                // 格式不符合时返回
                if (tmpBegPos == -1) {
                    continue;
                }
                tmpBeg = tmpInf.indexOf(',');
                tmpEnd = tmpInf.lastIndexOf(',');
                // 只有一个","
                if (tmpBeg == tmpEnd) {
                    continue;
                }
                tmpContact = tmpInf.substring(0, tmpBeg);
                tmpEmailAddr = tmpInf.substring(tmpBeg + 1, tmpEnd);
                tmpId = tmpInf.substring(tmpEnd + 1);
                try {
                    Integer.parseInt(tmpId);
                } catch (NumberFormatException e) {
                    tmpId = CASUtility.EMPTYSTR;
                }
                if (tmpContact == null) {
                    tmpContact = CASUtility.EMPTYSTR;
                }
                if (tmpEmailAddr == null) {
                    tmpEmailAddr = CASUtility.EMPTYSTR;
                }
                if (tmpId == null) {
                    tmpId = CASUtility.EMPTYSTR;
                }
                selectedFileAsesVec.add(tmpContact);
                selectedMailAddrVec.add(tmpEmailAddr);
                selectedIDAddrVec.add(tmpId);
            }
        }
        // 使QuickInfoField上作相应的显示.
        String tmpStrForShow = CASUtility.EMPTYSTR;
        for (int i = 0; i < selectedFileAsesVec.size(); i++) {
            String tmpCons = (String) selectedFileAsesVec.get(i);
            // 如果联系人为空的时候，显示邮件地址
            if (tmpCons == null || tmpCons.length() < 1) {
                tmpCons = selectedMailAddrVec.get(i).toString();
            }
            tmpStrForShow = tmpStrForShow.concat(tmpCons).concat(";");
        }
        ;
        setText(tmpStrForShow);
    }

    /**
     * 实现接口中的方法
     * 
     * @return 返回文本框内容
     */
    public String getContents() {
        String tmpIDStr = CASUtility.EMPTYSTR;
        int tmpSize = selectedMailAddrVec.size(); // 选中的联系人(含通讯组)的数目.
        if (tmpSize > 0) {
            String tmpMailStr = null;
            for (int i = 0;;) {
                tmpMailStr = (String) selectedMailAddrVec.get(i);
                if (tmpMailStr.indexOf('@') > -1) // 如果是普通联系人
                {
                    tmpIDStr += '\"' + (String) selectedFileAsesVec.get(i) + "\"<" + tmpMailStr + '>';
                }
                // else //如果是通讯组列表, 则进入子循环,将每一个成员的ID取出来,放入数组.
                // {
                // int tmpRecordID = 0;
                // try //检查收件人中发现有无效的收件人地址!
                // {
                // tmpRecordID = Integer.parseInt((String)selectedIDAddrVec.get(i));
                // }
                // catch(Exception e)
                // {
                // //TODO:报错:"收件人中发现有无效的收件人地址!"
                // break;
                // }
                //
                // //为通讯组列表加一个判断:如果是通讯组列表,则在tmpMail字段中存通讯组成员中的信息.@NOTE:故意在循环外判断,减少判断次数.
                // int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
                // int tmpNodeID = PIMUtility.getAppIndexByFolderID(tmpAppIndex);
                // PIMRecord tmpContacRec = PIMControl.ctrl.getModel().selectRecord(tmpAppIndex, tmpRecordID,
                // tmpNodeID);
                //
                // if (!new
                // Short((short)1).equals(tmpContacRec.getFieldValue(PIMPool.pool.getKey(ContactDefaultViews.TYPE))))
                // //如果tmpID对应的Record不是通讯组列表)
                // {
                // //TODO: writeErrorLog();
                // i++;
                // continue;
                // }
                // //取出tmpID对应的Record的成员字段值,并转化成int数组.
                // int tmpIDs[] = PIMUtility.stringToArray(
                // (String)tmpContacRec.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.MEMBER_LIST)));
                //
                // for (int ii = 0; ;) //然后着个取其表示为和emialAddress.
                // {
                // tmpContacRec = PIMControl.ctrl.getModel().selectRecord(tmpAppIndex, tmpIDs[ii],tmpNodeID);
                //
                // //抓出该条记录的email字段内容.
                // String tmpEmail =
                // (String)tmpContacRec.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.EMAIL));
                // if (tmpEmail == null ||tmpEmail.length() < 1)
                // {
                // tmpEmail =
                // (String)tmpContacRec.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.EMAIL_2));
                // if (tmpEmail == null ||tmpEmail.length() < 1)
                // {
                // tmpEmail =
                // (String)tmpContacRec.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.EMAIL_3));
                // }
                // }
                //
                // //判断是否有邮件地址,没有的话报错.并在确定后跳过
                // if (tmpEmail == null ||tmpEmail.length() < 1)
                // {
                // //TODO: 报错!"有个成员没有邮件地址,该成员将无法收到邮件,请核对发件箱本邮件收件人字段中缺少的收件人,]
                // //并在为其添加邮件地址信息后补发邮件.
                // continue;
                // }
                //
                // tmpIDStr += '\"' +
                // (String)tmpContacRec.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.CAPTION))
                // + "\"<" + tmpEmail + '>';
                //
                // if (++ii < tmpIDs.length)
                // {
                // tmpIDStr += ',';
                // }
                // else
                // {
                // break;
                // }
                // }
                // }

                if (++i < tmpSize) {
                    tmpIDStr += ',';
                } else {
                    break;
                }
            }
        }
        return tmpIDStr;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        jList.ensureIndexIsVisible(jList.getSelectedIndex());
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * @called by: TaskDialog;AppointMentDialog; 根PIMRecord出始化联系人域时，设置作为联系人的QuickInfoField组件，是不剔除邮件地址不合法的联系人，也不弹警告信息。
     */
    public void setIsActAsLinkManField(
            boolean prmIsActAsLinkManField) {
        actAsLinkManField = prmIsActAsLinkManField;
    }

    // 变量------------------------------------------------------------------------------------------------------------
    private Vector addressRanges;
    private int clickedKey = NULL_VK_MASK; // 键盘响应对应的键
    private int clickedMskKey = NULL_VK_MASK;
    private Panel popUpPanel; // 弹出内容的底层容器。
    private JList jList;
    private Container container;
    static String[] allIDAry;
    static String[] allFileAsAry; // 存储全部的表示为字段
    static String[] allEmailAddrAry;
    static String[] strToShowOnPopPaneAry; // 存储可能显示的用户表示为和邮件地址组和的全集。

    static int aryLenth;

    private Vector matchedItemsVec; // 存储要显示的邮件地址击list某项时插入到field中的内容。
    private Vector replaceWithStrVec; // 从fileAsVec和mailAddrVec中挑出一个，作为当用户回车或鼠标点

    private Vector selectedIDAddrVec;
    private Vector selectedMailAddrVec;
    private Vector selectedFileAsesVec;

    // private int mailEndPosBefCaret; //分号的位置
    private int caretPosInEditingMail; // 用来标示分号后字符的长度

    private Document textDoc;
    private TextFieldUndoMethod undo;
    private boolean alarmed;
    private boolean actAsLinkManField;
    private boolean isKeyEvent;

    // =======================================================================================================================================
    /**
     * 处理地址范围
     */
    public static class AddressRange {
        //
        /**
         * 构建器
         * 
         * @param start
         *            开始,
         * @param end
         *            结束
         */
        public AddressRange(int start, int end) {
            this.start = start;
            this.end = end;
        }

        //
        /**
         * 判断内容的包含
         * 
         * @param location
         *            定位行
         * @return 是否包含
         */
        public boolean include(
                int location) {
            return ((start <= location) && (location < end));
        }

        final int start;
        final int end;
    }

    /**
     * undo、redo操作后，设置本地的三个列表
     * 
     * @param prmContent
     *            当前QuickInfoField文本框中的内容
     */
    private void setNativeContent(
            String prmContent) {
        // 清空所有联系人
        selectedFileAsesVec.removeAllElements();
        selectedIDAddrVec.removeAllElements();
        selectedMailAddrVec.removeAllElements();
        // 如果为空
        if (prmContent == null && prmContent.length() < 1) {
            return;
        }
        // 取出每一个联系人
        Vector tmpContactsVec = new Vector();
        StringTokenizer tmpKen = new StringTokenizer(prmContent, ";");
        while (tmpKen != null && tmpKen.hasMoreTokens()) {
            tmpContactsVec.add(tmpKen.nextToken());
        }
        // 没有联系人,删除所有联系人
        if (tmpContactsVec != null && !tmpContactsVec.isEmpty()) {
            // 搜索selectedMailAddrVec列表中是否有当前联系人
            for (int i = 0; i < tmpContactsVec.size(); i++) {
                String tmpObjStr = String.valueOf(tmpContactsVec.get(i)).trim();
                // 如果字符串为“”字符时，把它作为没有当前联系人，在selectedFileAsesVec中删除它
                if (tmpObjStr != null && tmpObjStr.length() < 1) {
                    continue;
                } else {
                    setSelectedVec(tmpObjStr);
                }
            }
        }
    }

    /**
     * 通过匹配联系人表示为和联系人邮件来设置映象列表
     * 
     * @param prmConatact
     *            单个联系人的邮件或者表示为字符串
     */
    private void setSelectedVec(
            String prmContact) {
        // 如果为空的时候不给匹配
        if (prmContact == null || prmContact.length() < 1) {
            return;
        }
        int tmpIndex = -1;
        // 如果所有FieldAs为空
        if (allFileAsAry != null && allFileAsAry.length > 0) {
            // 如果一个人的FieldAs和别的EmailAddr相同怎么办？
            for (int i = 0; i < allFileAsAry.length; i++) {
                if (allFileAsAry[i].equals(prmContact.trim()) || allEmailAddrAry[i].equals(prmContact.trim())) {
                    tmpIndex = i;
                    break;
                }
            }
        }
        // 邮件或者表示为匹配
        if (tmpIndex >= 0) {
            selectedFileAsesVec.add(allFileAsAry[tmpIndex]);
            selectedIDAddrVec.add(allIDAry[tmpIndex]);
            selectedMailAddrVec.add(allEmailAddrAry[tmpIndex]);
        } else {
            selectedFileAsesVec.add(CASUtility.EMPTYSTR);
            selectedIDAddrVec.add(CASUtility.EMPTYSTR);
            selectedMailAddrVec.add(prmContact.trim()); // 把邮件设置为当前字符串
        }
    }

    /**
     * undo操作事件
     */
    private void undoEvent() {
        String str = undo.getUndoItem();
        if (str != null) {
            // 强制刷新，否则会覆盖第一个键
            // NOTE 在撤销至文本框为空的时，添加一个当键盘按下时不会响应insertUpdate或者removeUpdate
            if (str.length() < 1) {
                setText(CASUtility.SPACE);
            }
            setText(str);
            // 设置当前的三个列表
            setNativeContent(str);
        }
    }

    /**
     * redo操作事件
     */
    private void redoEvent() {
        String str = undo.getRedoItem();
        if (str != null) {
            // 强制刷新，否则会覆盖第一个键
            // NOTE 在撤销至文本框为空的时，添加一个当键盘按下时不会响应insertUpdate或者removeUpdate
            if (str.length() < 1) {
                setText(CASUtility.SPACE);
            }
            setText(str);
            // 设置当前的三个列表
            setNativeContent(str);
        }
    }

    /**
     * 在删除或者插入事件响应时，添加上一次操作时的QuickInfoField中的内容
     * 
     * @param prmUpdate
     *            插入或删除事件的标志，insert为1，remove为0
     */
    private void addEditItem(
            int prmUpdate,
            int prmOffset,
            int prmLength) {
        // 如果为CTRL+Ｚ或者为CTRL+YDe时候
        if ((getClickedMask() == KeyEvent.CTRL_MASK)
                && (getClickedKey() == KeyEvent.VK_Z || getClickedKey() == KeyEvent.VK_Y)) {
            setClickedKey(NULL_VK_MASK, NULL_VK_MASK);
            return;
        } else if (undo.isUndoed() || undo.isRedoed()) {
            // 如果没有做
            undo.setUndo(false);
            undo.setRedo(false);
            // 不做键盘的添加动作
            return;
        } else {
            // 取当前的文本,删除的时候文本为空。插入的时候文本不为空
            String tmpText = getText();
            if (prmUpdate == REMOVE_UPDATE) {
                int tmpKeyMap = getClickedKey();
                if ((tmpText.length() < 1 && tmpKeyMap == KeyEvent.VK_BACK_SPACE && prmOffset == 0 && prmLength == 1)
                        || (tmpText.length() < 1 && tmpKeyMap == KeyEvent.VK_DELETE)) {
                    undo.addEditItem(tmpText);
                    setClickedKey(NULL_VK_MASK, NULL_VK_MASK);
                } else if (tmpText.length() > 0) {
                    undo.addEditItem(tmpText);
                }
            } else {
                undo.addEditItem(tmpText);
            }
        }
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e
     *            the document event
     */
    public void changedUpdate(
            DocumentEvent e) {
    }

    /**
     * Gives notification that there was an insert into the document. The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e
     *            the document event
     */
    public void insertUpdate(
            DocumentEvent e) {

        addEditItem(INSERT_UPDATE, e.getOffset(), e.getLength());
    }

    /**
     * Gives notification that a portion of the document has been removed. The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e
     *            the document event
     */
    public void removeUpdate(
            DocumentEvent e) {
        addEditItem(REMOVE_UPDATE, e.getOffset(), e.getLength());
    }

    /**
     * 设置键盘响应的键值
     * 
     * @param prmClickedKey
     *            键值
     */
    private void setClickedKey(
            int prmClickedKey,
            int prmMaskClicked) {
        clickedKey = prmClickedKey;
        clickedMskKey = prmMaskClicked;
    }

    /**
     * 取键盘事件中响应的键
     * 
     * @return int 键值
     */
    private int getClickedKey() {
        return clickedKey;
    }

    /**
     * 取键盘事件中响应的键
     * 
     * @return int 键值
     */
    private int getClickedMask() {
        return clickedMskKey;
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        removeKeyListener(this);
        removeFocusListener(this);
        removeMouseListener(this);
        jList.removeMouseListener(this);
        if (textDoc != null) {
            textDoc.removeDocumentListener(this);
        }
    }

}
