package org.cas.client.platform.casutil;

import java.awt.Dialog;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

/** 信息框。 */
public class SOptionPane extends JDialog implements ActionListener {
    public static final byte FOR_GENERAL = 0; // 普通使用。
    public static final byte FOR_MACRO = 1; // Macro 使用
    public static final byte FOR_CUSTOMIZE = 2; // 自定义使用
    public static final int DEFAULT_FOCUS_1 = 0; // 设置第一个按钮为默认按钮。
    public static final int DEFAULT_FOCUS_2 = 1; // 设置第二个按钮为默认按钮。
    public static final int DEFAULT_FOCUS_3 = 2; // 设置第三个按钮为默认按钮。
    public static final int DEFAULT_FOCUS_4 = 3; // 设置第四个按钮为默认按钮。
    public static final int MAX_BUTTONS = 4; // 按钮最多个数。

    public static final int NO_ADDITION = 0; // 没有附加信息，固定信息
    public static final int ONE_ADDITION = 8; // 一个附加信息
    public static final int TWO_MERGE_ADDITION = 0x0010;// 两个附加信息，但实际要合并。
    public static final int TWO_ADDITION = 0x0020; // 两个附加信息。
    public static final int HAS_ADDITION = ONE_ADDITION | TWO_MERGE_ADDITION | TWO_ADDITION; // 有附加信息
    public static final int CHECK_STATE = 0x0040; // 需要检查按钮状态。
    public static final int NO_HELP = 0x0080; // 不使用Help
    public static final int DEFAULT_FRAME = 0x0100; // 父窗体为空时，以主窗体为默认
    public static final int CREATE_DIALOG = 0x0200; // 父对话框为空时，创建Dialog

    public static final int LOG_INDEX = 626; // 错误日志对话框
    public static final int INDEX_015 = 15; // 15 索引
    public static final int INDEX_311 = 311; // 311 索引
    public static final int INDEX_046 = 46; // 46 索引
    public static final int INDEX_582 = 582; // 582 索引

    // 以下为按钮选择常量，对应与‘1’～‘9’，‘a’～‘e’
    public static final int STYLE_OK = 1; // 仅为OK 按钮类型
    public static final int STYLE_OK_CANCEL = 2; // OK CANCEL 按钮类型
    public static final int STYLE_OK_HELP = 3; // OK HELP 按钮类型
    public static final int STYLE_RETRY_CANCEL = 4; // RETRY_CANCEL 按钮类型
    public static final int STYLE_YES_NO = 5; // YES_NO 按钮类型
    public static final int STYLE_YES_NO_CANCEL = 6; // YES_NO_CANCEL 按钮类型
    public static final int STYLE_OK_CANCEL_HELP = 7; // OK_CANCEL_HELP 按钮类型
    public static final int STYLE_YES_NO_HELP = 8; // YES_NO_HELP 按钮类型
    public static final int STYLE_YES_YESTOALL_NO_CANCEL = 9; // YES_YESTOALL_NO_CANCEL 按钮类型
    public static final int STYLE_DISABLE_ENABLE = 10; // DISABLE_ENABLE 按钮类型
    public static final int STYLE_CONTINUE_END = 11; // CONTINUE_END 按钮类型
    public static final int STYLE_FIX_IGNORE = 12; // FIX_IGNORE 按钮类型
    public static final int STYLE_UPDATE_UNUPDATE = 13; // UPDATE_UNUPDATE 按钮类型
    public static final int STYLE_ABORT_RETRY_IGNORE = 14; // ABORT_RETRY_IGNORE 按钮类型

    // 以下为信息框的返回值以及按钮的索引，两者为相同的值
    public static final int INDEX_OK = 0; // OK 按钮的序号及返回值
    public static final int INDEX_NO = 1; // NO 按钮的序号及返回值
    public static final int INDEX_CANCEL = 2; // CANCEL 按钮的序号及返回值
    public static final int INDEX_ABORT = 3; // ABORT 按钮的序号及返回值
    public static final int INDEX_YES = 4; // YES 按钮的序号及返回值
    public static final int INDEX_RETRY = 5; // 重试 按钮的序号及返回值
    public static final int INDEX_YES_TO_ALL = 6; // YES_TO_ALL 按钮的序号及返回值
    public static final int INDEX_IGNORE = 7; // IGNORE 按钮的序号及返回值
    public static final int INDEX_CONTINUE = 8; // CONTINUE 按钮的序号及返回值
    public static final int INDEX_END = 9; // END 按钮的序号及返回值
    public static final int INDEX_DISABLE = 10; // DISABLE 按钮的序号及返回值
    public static final int INDEX_ENABLE = 11; // ENABLE 按钮的序号及返回值
    public static final int INDEX_HELP = 12; // HELP 按钮的序号及返回值
    public static final int INDEX_UPDATE = 13; // UPDATE 按钮的序号及返回值
    public static final int INDEX_UNUPDATE = 14; // UNUPDATE 按钮的序号及返回值
    public static final int INDEX_FIXED = 15; // FIXED 按钮的序号及返回值
    public static final int COUNT_OF_INDEX = 16; // 按钮索引的个数

    // 以下常量为自定义方式的信息框使用
    public static final int OK_OPTION = -1; // 按钮类型：仅OK按钮
    public static final int OK_CANCEL_OPTION = -2; // 按钮类型：OK、CANCEL按钮
    public static final int OK_HELP_OPTION = -3; // 按钮类型：OK、HELP按钮
    public static final int RETRY_CANCEL_OPTION = -4; // 按钮类型：RETRY、CANCEL按钮
    public static final int YES_NO_OPTION = -5; // 按钮类型：YES、NO按钮
    public static final int YES_NO_CANCEL_OPTION = -6; // 按钮类型：YES、NO、CANCEL按钮
    public static final int OK_CANCEL_HELP_OPTION = -7; // 按钮类型：OK、CANCEL、HELP按钮
    public static final int YES_NO_HELP_OPTION = -8; // 按钮类型：YES、NO、HELP按钮
    public static final int YES_YESTOALL_NO_CANCEL_OPTION = -9; // 按钮类型：YES、YESTOALL、NO、CANCEL按钮
    public static final int MACRO_OPTION = -10; // 按钮类型：DISABLE_ENABLE 按钮
    public static final int MACRO_DEBUG = -11; // 按钮类型：为MACRO的DEBUG使用
    public static final int BUTTON_UNABLE = 0; // 按钮不可用状态

    public static final int MIN_AREA_X = 38; // 最小的宽度
    public static final int BIG_BUTTON_WIDTH = 90; // 大的按钮的宽度
    public static final int MAX_WIDTH = 500; // 最大宽度
    public static final int CASH_SIZE = 10; // 信息内容的缓存最大个数。

    // 所有常量将被删除，请使用MessageCons的常量。
    public static final int ERROR_MESSAGE = -1; // 错误信息类型
    public static final int WARNING_MESSAGE = -2; // 警告信息类型
    public static final int CONFIRM_MESSAGE = -3; // 确认信息类型
    public static final int QUESTION_MESSAGE = -4; // 疑问信息类型

    public static final int OK_ONLY = 1; // 只显示【确定】按钮
    public static final int OK_CANCEL = 2; // 显示【确定】及【取消】按钮
    public static final int RETRY_CANCEL = 4; // 显示【重试】及【取消】按钮
    public static final int YES_NO = 8; // 显示【是】【否】按钮
    public static final int YES_NO_CANCEL = 0x10; // 显示【是】【否】及【取消】按钮
    public static final int YES_YESTOALL_NO_CANCEL = 0x20; // 显示【是】【全是】【否】及【取消】按钮
    public static final int CONTINUE_END = 0x40; // 显示【继续】及【结束】按钮
    public static final int DISABLE_ENABLE = 0x80; // 显示【取消宏】及【启用宏】按钮
    public static final int ABORT_RETRY_IGNORE = 0x100; // 显示【终止】【重试】及【忽略】按钮
    public static final int UPDATE_UNUPDATE = 0x200; // 显示【更新】及【不更新】按钮
    public static final int FIX_IGNORE = 0x400; // 显示【调整】和【忽略】按钮

    public static final int CRITICAL = 0x10000; // 显示Critical Message图标
    public static final int QUESTION = 0x20000; // 显示Warning Query 图标
    public static final int EXCLAMATION = 0x4000; // 显示Warning Message图标
    public static final int INFORMATION = 0x80000; // 显示Information Message图标

    public static final int DEFAULT_BUTTON1 = 0x100000; // 第一个按钮为默认值
    public static final int DEFAULT_BUTTON2 = 0x200000; // 第二个按钮为默认值
    public static final int DEFAULT_BUTTON3 = 0x400000; // 第三个按钮为默认值
    public static final int DEFAULT_BUTTON4 = 0x800000; // 第四个按钮为默认值

    public static int errorResult; // 对话框的返回值

    public static final int OK = 0; // 用户按下确定按钮
    public static final int NO = OK + 1; // 用户按下否按钮
    public static final int CANCEL = NO + 1; // 用户按下取消按钮
    public static final int ABORT = CANCEL + 1; // 用户按下放弃按钮
    public static final int YES = ABORT + 1; // 用户按下是按钮
    public static final int RETRY = YES + 1; // 用户按下重试按钮
    public static final int YESTOALL = RETRY + 1; // 用户按下全是按钮
    public static final int IGNORE = YESTOALL + 1; // 用户按下忽略按钮
    public static final int CONTINUE = IGNORE + 1; // 用户按下继续按钮
    public static final int END = CONTINUE + 1; // 用户按下结束按钮
    public static final int DISABLE = END + 1; // 用户按下停用宏按钮
    public static final int ENABLE = DISABLE + 1; // 用户按下使用宏按钮
    public static final int HELP = ENABLE + 1; // 用户按下帮助按钮
    public static final int UPDATE = HELP + 1; // 用户按下更新按钮
    public static final int UNUPDATE = UPDATE + 1; // 用户按下不更新按钮
    public static final int FIXED = UNUPDATE + 1; // 用户按下调整按钮

    private static int cashCount; // 缓存中的数据个数.
    private static char[] indexCash; // 缓存对应的索引数组.
    private static byte[] lengthInfo; // 各索引的信息内容在文件中的长度（2个字节）。
    private static byte[][] contentCash; // 各信息内容的缓存数组，容量为CASH_SIZE(10)。

    private static boolean isErrorShowing; // 是否有信息对话框正在显示。
    private static boolean flag; // 某标志，表示信息框停秀.直到被设为FALSE才能继续显示信息框.
    private byte callMode; // 调用的方式,0 - 普通，1－ Macro，2 － 自定义

    /** 按钮激发动作。@param e 按钮的动作事件。 */
    public void actionPerformed(
            ActionEvent e) {
        Object tmpBtn = e.getSource(); // 激发动作的按钮
        for (int i = 0; i < COUNT_OF_INDEX; i++) { // index 作为按钮数组的下标
            if (tmpBtn == buttons[i]) { // 判断是哪个按钮
                errorResult = i; // 设置返回值，这里返回值与按钮的索引是一致的。
                isErrorShowing = false;
                setVisible(false);
                dispose();
                break; // 不继续判断，直接返回。
            }
        }
    }

    /**
     * 显示错误信息框。父窗体为主窗体。固定信息内容。
     * 
     * @param str
     *            信息内容。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            int prmMesIndex) {
        return show(null, prmMesIndex, NO_ADDITION | DEFAULT_FRAME, null, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。固定信息内容。
     * 
     * @param parent
     *            父窗体或对话框.
     * @param messageIndex
     *            信息内容的索引号。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Object parent,
            int messageIndex) {
        return show(parent, messageIndex, NO_ADDITION, null, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。两个附加信息合并。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param messageIndex
     *            信息内容的索引号。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附件信息内容2。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Object parent,
            int messageIndex,
            String str1,
            String str2) {
        return show(parent, messageIndex, TWO_MERGE_ADDITION, str1, str2, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。一个附加信息。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param messageIndex
     *            信息内容的索引号。
     * @param str1
     *            附加信息内容1。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Object parent,
            int messageIndex,
            String str1) {
        return show(parent, messageIndex, ONE_ADDITION, str1, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。一个附加信息。需要检查按钮状态。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param messageIndex
     *            信息内容的索引号。
     * @param str1
     *            附加信息内容1。
     * @param state
     *            按钮选择状态。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Object parent,
            int messageIndex,
            String str1,
            int state) {
        return show(parent, messageIndex, ONE_ADDITION | CHECK_STATE, str1, null, state);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两或三段信息。二个附加信息。 两段信息可能需要合并。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param messageIndex
     *            信息内容的索引号。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附加信息内容2。
     * @param isDivide
     *            true表示三段信息，false两段信息，附加信息合并。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Object parent,
            int messageIndex,
            String str1,
            String str2,
            boolean isDivide) {
        return show(parent, messageIndex, isDivide ? TWO_ADDITION : TWO_MERGE_ADDITION, str1, str2, 0);
    }

    /**
     * 显示错误信息框。父窗体为主窗体。两段信息。两个附加信息合并。
     * 
     * @param messageIndex
     *            信息内容的索引号。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附件信息内容2。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            int messageIndex,
            String str1,
            String str2) {
        return show(null, messageIndex, TWO_MERGE_ADDITION | DEFAULT_FRAME, str1, str2, 0);
    }

    /**
     * 显示错误信息框。父窗体为主窗体。两或三段信息。二个附加信息。 两段信息可能需要合并。
     * 
     * @param messageIndex
     *            信息内容的索引号。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附加信息内容2。
     * @param isDivide
     *            true表示三段信息，false两段信息，附加信息合并。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            int messageIndex,
            String str1,
            String str2,
            boolean isDivide) {
        return show(null, messageIndex, isDivide ? TWO_ADDITION | DEFAULT_FRAME : TWO_MERGE_ADDITION | DEFAULT_FRAME,
                str1, str2, 0);
    }

    /**
     * 显示错误信息框。父窗体为主窗体。固定信息内容。
     * 
     * @param str
     *            信息内容。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            String str) {
        return show(null, str, NO_ADDITION | DEFAULT_FRAME, null, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传为主窗体。两段信息。两个附加信息合并。
     * 
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附件信息内容2。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            String str,
            String str1,
            String str2) {
        return show(null, str, TWO_MERGE_ADDITION | DEFAULT_FRAME, str1, str2, 0);
    }

    /**
     * 显示错误信息框。父窗体为主窗体。两或三段信息。二个附加信息。 两段信息可能需要合并。
     * 
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附加信息内容2。
     * @param isDivide
     *            true表示三段信息，false两段信息，附加信息合并。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            String str,
            String str1,
            String str2,
            boolean isDivide) {
        return show(null, str, isDivide ? TWO_ADDITION | DEFAULT_FRAME : TWO_MERGE_ADDITION | DEFAULT_FRAME, str1,
                str2, 0);
    }

    /**
     * 第二种对话框类型，自定义方式。 显示错误信息框。父窗体为主窗体。指定信息类型、按钮选择类型和显示的内容以及对话框标题。
     * 
     * @param title
     *            对话框标题。
     * @param messageType
     *            信息类型（confirm，error， question， warning）
     * @param optionType
     *            按钮选择类型。
     * @param message
     *            显示的信息内容。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            String title,
            int messageType,
            int optionType,
            String message) {
        SOptionPane dialog = getDialog(null, DEFAULT_FRAME);// 创建信息对话框。
        dialog.callMode = FOR_CUSTOMIZE;
        int buttonStyle = changeStyle(optionType);// 转换按钮选择类型
        dialog.initDialog(title, message, messageType, buttonStyle, 0);
        dialog.setVisible(true);
        return errorResult;
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。固定信息内容。
     * 
     * @param parent
     *            父窗体或对话框.
     * @param str
     *            信息内容。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Frame parent,
            String str) {
        return show(parent, str, NO_ADDITION, null, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。一个附加信息。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Frame parent,
            String str,
            String str1) {
        return show(parent, str, ONE_ADDITION, str1, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。两个附加信息合并。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附件信息内容2。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Frame parent,
            String str,
            String str1,
            String str2) {
        return show(parent, str, TWO_MERGE_ADDITION, str1, str2, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两或三段信息。二个附加信息。 两段信息可能需要合并。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附加信息内容2。
     * @param isDivide
     *            true表示三段信息，false两段信息，附加信息合并。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Frame parent,
            String str,
            String str1,
            String str2,
            boolean isDivide) {
        return show(parent, str, isDivide ? TWO_ADDITION : TWO_MERGE_ADDITION, str1, str2, 0);
    }

    /**
     * 第三种对话框类型,该方法为Macro提供对话框的显示。
     * 
     * @param parent
     *            对话框的容器。 null 的主窗体。
     * @param prompt
     *            对话框显示的内容。
     * @param buttonStyle
     *            对话框的类型。
     * @param title
     *            对话框的标题。 null 统一图标
     */
    public static int showErrorDialog(
            Frame parent,
            String prompt,
            int buttonStyle,
            String title) {
        return getDialog(parent, DEFAULT_FRAME).showMessage(prompt, buttonStyle, title);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。一个附加信息。 需要检查按钮状态。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param state
     *            按钮选择状态。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Frame parent,
            String str,
            String str1,
            int state) {
        return show(parent, str, ONE_ADDITION | CHECK_STATE, str1, null, state);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。固定信息内容。
     * 
     * @param parent
     *            父窗体或对话框.
     * @param str
     *            信息内容。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Dialog parent,
            String str) {
        return show(parent, str, NO_ADDITION, null, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。一个附加信息。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Dialog parent,
            String str,
            String str1) {
        return show(parent, str, ONE_ADDITION, str1, null, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。两个附加信息合并。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附件信息内容2。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Dialog parent,
            String str,
            String str1,
            String str2) {
        return show(parent, str, TWO_MERGE_ADDITION, str1, str2, 0);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两或三段信息。二个附加信息。 两段信息可能需要合并。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param str2
     *            附加信息内容2。
     * @param isDivide
     *            true表示三段信息，false两段信息，附加信息合并。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Dialog parent,
            String str,
            String str1,
            String str2,
            boolean isDivide) {
        return show(parent, str, isDivide ? TWO_ADDITION : TWO_MERGE_ADDITION, str1, str2, 0);
    }

    /**
     * 第三种对话框类型, 该方法为Macro提供对话框的显示。
     * 
     * @param parent
     *            对话框的容器。 null 一个不希望的容器。
     * @param prompt
     *            对话框显示的内容。
     * @param buttonStyle
     *            对话框的类型。
     * @param title
     *            对话框的标题。 null 统一图标
     */
    public static int showErrorDialog(
            Dialog parent,
            String prompt,
            int buttonStyle,
            String title) {
        return getDialog(parent, CREATE_DIALOG).showMessage(prompt, buttonStyle, title);
    }

    /**
     * 显示错误信息框。父窗体为传入参数的窗体或对话框。两段信息。一个附加信息。 需要检查按钮状态。
     * 
     * @param parent
     *            父窗体或对话框。
     * @param str
     *            信息内容。
     * @param str1
     *            附加信息内容1。
     * @param state
     *            按钮选择状态。
     * @return 返回选择信息。
     */
    public static int showErrorDialog(
            Dialog parent,
            String str,
            String str1,
            int state) {
        return show(parent, str, ONE_ADDITION | CHECK_STATE, str1, null, state);
    }

    /**
     * 这个方法设置一个标志，若这个标志为True， 则任何调用showDialog的时候都不要显示对话框。
     * 
     * @param flag
     *            若这个标志为False，则按照正常情况进行。
     */
    public static void setSpecialFlag(
            boolean flag) {
        SOptionPane.flag = flag;
    }

    /**
     * 这个方法取得是否需要弹对话框的标志。 这是为了在一系列流程中间，若很多地方需要设置这个标志的话， 就需要取得它原来的标志，然后设置所需要的标志。 结束后再把原来的标志设置回去。
     * 
     * @return 该特殊标志。
     */
    public static boolean getSpecialFlag() {
        return SOptionPane.flag;
    }

    /** 使用窗体作为 父容器。 */
    private SOptionPane(Frame parent) {
        super(parent, true);
    }

    /** 使用对话框作为 父容器。 */
    private SOptionPane(Dialog parent) {
        super(parent, true);
    }

    /**
     * 创建对话框实例。
     * 
     * @param prmParent
     *            父窗体和对话框。
     * @param prmMesFlag
     *            信息的一些标志。
     * @return 对话框实例。
     */
    private static SOptionPane getDialog(
            Object prmParent,
            int prmMesFlag) {
        Toolkit.getDefaultToolkit().beep(); // 发声
        if (prmParent == null) {
            if ((prmMesFlag & DEFAULT_FRAME) != 0)// 判断是否使用主窗体和创建父对话框。
                prmParent = CASControl.ctrl.getMainFrame();
            else if ((prmMesFlag & CREATE_DIALOG) != 0)// 判断是否创建父对话框
                prmParent = new JDialog();
        }

        if (prmParent instanceof Dialog) // 根据父容器的不同，创建不同的类型的对话框。
            return new SOptionPane((Dialog) prmParent); // 父为对话框
        else
            return new SOptionPane((Frame) prmParent); // 父为窗体
    }

    /*
     * 处理信息框。
     * @param parent 父窗体或父对话框。
     * @param str 信息框的类型字串。
     * @param messageFlag 相关的一些标志。
     * @param addition1 附件的信息1。
     * @param addition2 附件的信息2。
     * @param state 按钮检查状态， 仅需检查是其作用
     */
    private static int show(
            Object parent,
            String str,
            int messageFlag,
            String addition1,
            String addition2,
            int state) {
        int index = 0;// 把字符串转换为整型的索引。
        try {
            index = Integer.parseInt(str.substring(2));
        } catch (Exception e) {
            // 不处理
        }
        return show(parent, index, messageFlag, addition1, addition2, state);
    }

    /*
     * 处理信息框。
     * @param parent 父窗体或父对话框。
     * @param messageIndex 信息框的索引。
     * @param messageFlag 相关的一些标志。
     * @param addition1 附件的信息1。
     * @param addition2 附件的信息2。
     * @param prmState 按钮选择状态。
     */
    private static int show(
            Object prmParent,
            int prmMesIndex,
            int prmMesFlag,
            String prmAddition1,
            String prmAddition2,
            int prmState) {
        SOptionPane tmpDlg = getDialog(prmParent, prmMesFlag);// 创建需要的对话框
        tmpDlg.callMode = FOR_GENERAL; // 设置为普通调用
        if ((prmMesFlag & CHECK_STATE) != 0) // 需要检查按钮状态。
            tmpDlg.buttonState = prmState;

        tmpDlg.initContent(prmMesIndex); // 将Index置换为内容。
        tmpDlg.messageIndex = prmMesIndex;

        String tmpContent = null; // 信息的最终文本内容
        StringBuffer tmpMessage = tmpDlg.message1; // 组合信息文本内容的中间结果
        if (tmpMessage == null) { // 发生错误
            if (lengthInfo == null) // 不存在信息文件
                tmpContent = DlgConst.NO_FILE; // 报告相关资源被改动,该功能无法使用.
            else
                // 有资源,但没有匹配索引号的情况
                tmpContent = DlgConst.NO_KEY.concat(Integer.toString(prmMesIndex));
            tmpDlg.messageChar = 'e'; // 为错误类型信息框，仅一个OK按钮。
            tmpDlg.optionChar = '1';
        } else { // 顺利,则合成最终的信息文本
            if ((prmMesFlag & HAS_ADDITION) != 0 && prmAddition1 != null) {
                tmpMessage.append(prmAddition1);
                StringBuffer tmpLastMessage = tmpDlg.message2; // 组成信息内容的最后一个文本。
                if ((prmMesFlag & TWO_MERGE_ADDITION) != 0) {
                    if (prmAddition2 != null) // 两个附加信息但需要合并。
                        tmpMessage.append(prmAddition2.trim());
                } else if ((prmMesFlag & TWO_ADDITION) != 0) {
                    if (tmpLastMessage != null) { // 两个附加信息且不需要合并。
                        tmpMessage.append(tmpLastMessage);
                        if (prmAddition2 != null) // 添加第二个附加文本。
                            tmpMessage.append(prmAddition2.trim());
                    }
                }
                if (tmpDlg.message3 != null) // 有第三段信息
                    tmpMessage.append(tmpDlg.message3); // 添加最后一个信息
            }
            tmpContent = tmpMessage.toString(); // 转换成字符串
        }

        int tmpBtnStyle = tmpDlg.optionChar - '0'; // 把按钮选择字符转换为按钮选择类型。
        if (tmpBtnStyle > 9) // '1'~'9'为1～9，'a'~'f'为10-15.
            tmpBtnStyle += '0' - 'a' + 10; // 原来为字符，a对应10。

        int tmpMesType = tmpDlg.messageChar; // 把信息类型字符转换为信息类型。
        if (tmpMesType == 'c')
            tmpMesType = CONFIRM_MESSAGE; // 确认类型，字符为c
        else if (tmpMesType == 'e')
            tmpMesType = ERROR_MESSAGE; // 错误类型，字符为e
        else if (tmpMesType == 'q')
            tmpMesType = QUESTION_MESSAGE; // 疑问类型，字符为q
        else
            tmpMesType = WARNING_MESSAGE; // 警告类型，字符应该为w

        tmpDlg.initDialog(null, tmpContent, tmpMesType, tmpBtnStyle, prmMesFlag); // 初始化对话框。
        tmpDlg.showScreenHelp();
        tmpDlg.setVisible(true);

        return errorResult; // 返回选择结果
    }

    /*
     * 把选择类型转换为按钮选择类型。以后常量定义为一致，则不需要转换。
     * @param optionTyle 选择类型。
     * @return 按钮选择类型。
     */
    private static int changeStyle(
            int optionType) {
        switch (optionType) {
            case OK_OPTION: // 为 OK 按钮
                return STYLE_OK;

            case OK_CANCEL_OPTION: // 为 OK_CANCE 按钮
                return STYLE_OK_CANCEL;

            case OK_HELP_OPTION: // 为 OK_HELP 按钮
                return STYLE_OK_HELP;

            case RETRY_CANCEL_OPTION: // 为 RETRY_CANCEL 按钮
                return STYLE_RETRY_CANCEL;

            case YES_NO_OPTION: // 为 YES_NO 按钮
                return STYLE_YES_NO;

            case YES_NO_CANCEL_OPTION: // 为 YES_NO_CANCEL 按钮
                return STYLE_YES_NO_CANCEL;

            case OK_CANCEL_HELP_OPTION: // 为 OK_CANCEL_HELP 按钮
                return STYLE_OK_CANCEL_HELP;

            case YES_NO_HELP_OPTION: // 为 YES_NO_HELP 按钮
                return STYLE_YES_NO_HELP;

            case YES_YESTOALL_NO_CANCEL_OPTION: // 为 YES_YESTOALL_NO_CANCEL 按钮
                return STYLE_YES_YESTOALL_NO_CANCEL;

            case MACRO_OPTION: // 为 DISABLE_ENABLE 按钮
                return STYLE_DISABLE_ENABLE;

            case MACRO_DEBUG: // 为 CONTINUE_END 按钮
                return STYLE_CONTINUE_END;

            default: // 默认为 OK 按钮
                return STYLE_OK;
        }
    }

    /**
     * 初始化对话框的标题、信息图标、信息等。
     * 
     * @param prmTitle
     *            对话框标题。
     * @param prmMes
     *            对话框的信息文本。
     * @param prmMesType
     *            信息/警告/等类型.
     * @param prmBtnStyle
     *            按钮选择类型。
     * @param prmMesFlag
     *            对话框的一些标志。
     */
    private void initDialog(
            String prmTitle,
            String prmMes,
            int prmMesType,
            int prmBtnStyle,
            int prmMesFlag) {
        // 参数保存,变量初始化,及除错-----------
        messageFlag = prmMesFlag;
        metrics = getFontMetrics(CustOpts.custOps.getFontOfDefault());
        prmMes = prmMes != null ? prmMes : CASUtility.EMPTYSTR; // 避免以后判断空指针。

        // 属性-----------------------------
        setTitle(prmTitle != null ? prmTitle : PaneConsts.TITLE); // 设置对话框标题，若为空，使用默认标题。

        int tmpPos = getMesWidth(prmMes, prmBtnStyle); // 文本布局显示需要的最大宽度
        String tmpFinalStr = getAreaContents(prmMes, tmpPos); // 最终显示的文本
        // 至此得到了经重新组织后的文字内容----------------------------

        int tmpLines = 1; // 检查文本的行数，以回车为分隔，至少为一行。
        for (int i = 0, len = tmpFinalStr.length(); i < len; i++)
            if (tmpFinalStr.charAt(i) == '\n') // 回车的个数+1=行数.
                tmpLines++;
        int tmpFontHeight = metrics.getAscent() + metrics.getDescent() + metrics.getLeading(); // 字体的高度。
        int tmpAreaH = Math.max(tmpLines * tmpFontHeight + 4, DlgConst.IMAGE_SIZE);// 文本及图标区域的高度
        int tmpHeight = tmpAreaH + (CustOpts.VER_GAP * 2 + CustOpts.BTN_HEIGHT); // 包含了按钮后的高度
        int tmpAreaW = tmpPos + (ErrorLabel.isSpecial() ? CustOpts.HOR_GAP * 2 : CustOpts.HOR_GAP); // 文本区域宽度
        int tmpWidth = tmpAreaW + (MIN_AREA_X + CustOpts.HOR_GAP + 2); // 文本加图标区域后的宽度
        // 至此得到了文本加上图标所占区间的高宽-------------------------

        int w = tmpWidth + 2 * CustOpts.HOR_GAP;
        int h = tmpHeight + CustOpts.SIZE_TITLE + CustOpts.VER_GAP * 2;
        setBounds((CustOpts.SCRWIDTH - w) / 2, (CustOpts.SCRHEIGHT - h) / 2, w, h); // 对话框的默认尺寸。
        setLayout(null);

        JLabel tmpDlgLab = new JLabel(); // 放置图标的组件
        tmpDlgLab.setIcon(getMessageIcon(prmMesType)); // 设置信息的图标
        tmpDlgLab.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, DlgConst.IMAGE_SIZE, DlgConst.IMAGE_SIZE); // 设置信息的图标

        ErrorLabel tmpAreaLabel = new ErrorLabel(tmpFinalStr); // 创建文本标签组件
        tmpAreaLabel.setBounds(MIN_AREA_X + (tmpAreaW - tmpPos) / 2, CustOpts.VER_GAP, tmpAreaW + 4, tmpAreaH);

        add(tmpAreaLabel); // 添加组件
        add(tmpDlgLab);
        setButtonStyle(prmBtnStyle, tmpWidth, tmpHeight); // 创建按钮

        errorResult = CANCEL; // 默认选择为“取消”
        isErrorShowing = true;
    }

    /**
     * 通过按钮选择类型返回所有按钮加上他们之间的间隙的所占的总宽度。
     * 
     * @param buttonStyle
     *            按钮选择类型。
     * @return 所有按钮及间隙的总宽度。
     */
    private int getButtonsWidth(
            int buttonStyle) {
        switch (buttonStyle) {
            case STYLE_OK_CANCEL:
            case STYLE_OK_HELP:
            case STYLE_RETRY_CANCEL:
            case STYLE_YES_NO:
                return CustOpts.BTN_WIDTH * 2 + CustOpts.HOR_GAP; // 两个一般按钮，一个间隙

            case STYLE_CONTINUE_END:
            case STYLE_DISABLE_ENABLE:
            case STYLE_FIX_IGNORE:
            case STYLE_UPDATE_UNUPDATE:
                return BIG_BUTTON_WIDTH * 2 + CustOpts.HOR_GAP; // 两个大的按钮，一个间隙

            case STYLE_YES_NO_CANCEL:
            case STYLE_OK_CANCEL_HELP:
            case STYLE_YES_NO_HELP:
            case STYLE_ABORT_RETRY_IGNORE:
                return CustOpts.BTN_WIDTH * 3 + CustOpts.HOR_GAP * 2; // 三个一般的按钮，两个间隙

            case STYLE_YES_YESTOALL_NO_CANCEL: // 三个一般按钮，一个大按钮，三个间隙
                return CustOpts.BTN_WIDTH * 3 + BIG_BUTTON_WIDTH + CustOpts.HOR_GAP * 3;

            default:
                return CustOpts.BTN_WIDTH; // 一个一般的按钮，没有间隙
        }
    }

    /**
     * 判断在何处应该切分字符串。
     * 
     * @param prmContent
     *            对话框信息内容。
     * @param buttonStyle
     *            按钮选择类型。
     */
    private int getMesWidth(
            String prmContent,
            int prmBtnStyle) {
        int tmpMinWidth = getButtonsWidth(prmBtnStyle); // 所有按钮加上其间的间隙的宽度为信息框的宽度的下限。

        int tmpPos = prmContent.indexOf('\n'); // 判断信息是否本身就是多行的。
        if (tmpPos < 0) { // 参数信息为单行的算法:
            int tmpWidth = (int) (metrics.stringWidth(prmContent) / 2.5); // 奇怪的算法!!
            if (tmpMinWidth == CustOpts.BTN_WIDTH) // 处理只有一个按钮的特例情况。
                if (tmpWidth < CustOpts.BTN_WIDTH + 2 * CustOpts.HOR_GAP)
                    tmpWidth = CustOpts.BTN_WIDTH + 2 * CustOpts.HOR_GAP;
            tmpWidth += tmpMinWidth;
            return tmpWidth > MAX_WIDTH ? MAX_WIDTH : tmpWidth; // 不能超过规定宽度
        } else { // 参数信息为多行的算法:
            int tmpMaxWidth = 0; // 遍历每一行,得到显示最长的子串的显示宽度。
            int tmpCurPos = 0;
            while (tmpPos >= 0) {
                tmpMaxWidth = Math.max(tmpMaxWidth, metrics.stringWidth(prmContent.substring(tmpCurPos, tmpPos)));
                tmpCurPos = tmpPos + 1; // 定为到回车后,便于继续搜索.
                tmpPos = prmContent.indexOf('\n', tmpCurPos);
            }
            tmpMaxWidth = Math.max(tmpMaxWidth, metrics.stringWidth(prmContent.substring(tmpCurPos)));
            // 至此得到各行信息的最大宽度.
            return tmpMaxWidth > MAX_WIDTH ? MAX_WIDTH : (tmpMaxWidth < tmpMinWidth ? tmpMinWidth : tmpMaxWidth);
            // 不能超过上界 不能超过下界.
        }
    }

    /**
     * 把文本按照显示切成多行，在需要的地方添加换行。
     * 
     * @param str
     *            原始文本。
     * @param cut
     *            文本显示最大宽度。
     * @return 转换后的文本。
     */
    private String getAreaContents(
            String str,
            int cut) {
        String remainText = str; // 分切后余下的文本，初始为原始文本
        StringBuffer result = null; // 分切后的结果（文本）
        int start = 0; // 分切的原始文本的开始位置
        while (remainText.length() > 0) { // 分切动作直到余下的文本长度为0
            String breakString = ErrorLabel.getBreakString(remainText, metrics, cut);// 此算法不详。
            if (breakString == null)
                break;
            start += breakString.length();
            if (result == null)
                result = new StringBuffer();
            result.append(breakString);

            if (!ErrorLabel.hasEnter()) // 假如不是因为换行，则需要添加换行
                result.append('\n');
            remainText = str.substring(start);
        }

        return result == null ? str : result.toString();// 假如结果为空，表示没有分切，使用原来的文本。
    }

    /*
     * 创建按钮，并设置一些属性。
     * @param prmIndex 按钮的索引。
     * @param prmText 按钮的文本。
     * @param prmMnemonic 按钮的助记符，'\0'表示无助记符。
     * @param prmWidth 按钮的宽度，0 表示正常按钮宽度。
     */
    private void create(
            int prmIndex,
            String prmText,
            char prmMnemonic,
            int prmWidth) {
        if (prmWidth == 0) // 宽度为0， 设置为正常宽度。
            prmWidth = CustOpts.BTN_WIDTH;

        JButton tmpBtn = new JButton(prmText); // 创建按钮
        tmpBtn.setMnemonic(prmMnemonic);
        tmpBtn.setBounds(x, y, prmWidth, 20);
        tmpBtn.setMargin(new Insets(0, 0, 0, 0));
        add(tmpBtn);
        tmpBtn.addActionListener(this);

        x += prmWidth + CustOpts.HOR_GAP; // 设置下一个按钮的x位置，当前位置加按钮宽度和间隙
        buttons[prmIndex] = tmpBtn;
        if (prmIndex == INDEX_CANCEL) // 判断是否为CANCEL按钮
            cancel = tmpBtn; // 赋值给cancel变量
        else {
            if (prmIndex == INDEX_OK) { // 判断是否为OK按钮
                if ((this.messageFlag & CHECK_STATE) != 0 && this.buttonState == BUTTON_UNABLE) // 当允许设置按钮状态，且功能无效时这时按钮为灰。
                    tmpBtn.setEnabled(false);
                ok = tmpBtn;// 赋值给ok变量
            }
            tmpBtn.addActionListener(this); // 按钮添加动作监听器
        }
        // int defaultButton = this.messageFlag % MAX_BUTTONS;// 假如默认按钮不是第一个，则设置相应位置的按钮为默认按钮。
        // if (defaultButton > 0 && defaultButton == this.count)
        // setDefaultFocus(button);
        this.count++; // 按钮计数更新
    }

    /**
     * 创建按钮。
     * 
     * @param buttonStyle
     *            按钮类型。
     * @param width
     *            对话框上面板的宽度。
     * @param height
     *            按钮底边的位置。
     */
    private void setButtonStyle(
            int buttonStyle,
            int width,
            int height) {
        x = (width - getButtonsWidth(buttonStyle)) / 2; // 最左边的按钮x方向的起始位置。
        y = height - CustOpts.BTN_HEIGHT; // 按钮的y方向的位置。
        buttons = new JButton[COUNT_OF_INDEX]; // 所有可能的按钮的数组
        switch (buttonStyle) {
            case STYLE_OK: // 仅 OK 按钮 // '\0' 表示无助记符， 0 表示为正常按钮宽度。
                create(INDEX_OK, DlgConst.OK, 'O', 0); // OK 按钮
                break;

            case STYLE_OK_CANCEL: // OK + Cancel 按钮
                create(INDEX_OK, DlgConst.OK, 'O', 0); // OK 按钮
                create(INDEX_CANCEL, DlgConst.CANCEL, 'C', 0); // CANCEL 按钮
                break;

            case STYLE_OK_HELP: // OK + Help 按钮
                create(INDEX_OK, DlgConst.OK, 'O', 0);
                create(INDEX_HELP, DlgConst.HELP_BUTTON, 'H', 0); // Help 按钮
                break;

            case STYLE_RETRY_CANCEL: // Retry + Cancel 按钮 // 目前不统一，应该统一。 普通使用时， Retry实际使用为 OK。
                create(callMode == FOR_GENERAL ? INDEX_OK : // RETRY 按钮
                        INDEX_RETRY, DlgConst.RETRY_BUTTON, 'R', 0);
                create(INDEX_CANCEL, DlgConst.CANCEL, 'C', 0); // CANCEL 按钮
                break;

            case STYLE_YES_NO: // Yes + No 按钮 // 目前不统一，应该统一。 普通使用时， Yes实际使用为 OK。
                create(callMode == FOR_GENERAL ? INDEX_OK : INDEX_YES, DlgConst.YES, 'Y', 0); // YES 按钮
                create(INDEX_NO, DlgConst.NO, 'N', 0); // No 按钮
                break;

            case STYLE_YES_NO_CANCEL: // Yes + No + Cancel 按钮 // 目前不统一，应该统一。 普通使用时， Retry实际使用为 OK。
                create(callMode == FOR_GENERAL ? INDEX_OK : INDEX_YES, DlgConst.YES, 'Y', 0); // Yes 按钮
                create(INDEX_NO, DlgConst.NO, 'N', 0); // No 按钮
                create(INDEX_CANCEL, DlgConst.CANCEL, 'C', 0); // Cancel 按钮
                break;

            case STYLE_OK_CANCEL_HELP: // OK + Cancel + Help 按钮
                create(INDEX_OK, DlgConst.OK, 'O', 0); // OK 按钮
                create(INDEX_CANCEL, DlgConst.CANCEL, 'C', 0); // CANCEL 按钮
                create(INDEX_HELP, DlgConst.HELP_BUTTON, 'H', 0); // HELP 按钮
                break;

            case STYLE_YES_NO_HELP: // Yes + No + Help 按钮 // 这里Yes实际使用 OK
                create(INDEX_OK, DlgConst.YES, 'Y', 0); // Yes 按钮
                create(INDEX_NO, DlgConst.NO, 'N', 0); // NO 按钮
                create(INDEX_HELP, DlgConst.HELP_BUTTON, 'H', 0); // Help 按钮
                break;

            case STYLE_YES_YESTOALL_NO_CANCEL: // Yes + YesToAll + No + Cancel 按钮 // 目前不统一，应该统一。 普通使用时， Yes实际使用为 OK。
                create(callMode == FOR_GENERAL ? INDEX_OK : INDEX_YES, DlgConst.YES, 'Y', 0); // Yes 按钮
                create(INDEX_YES_TO_ALL, DlgConst.YESTOALL_BUTTON, 'A', BIG_BUTTON_WIDTH); // Yes To ALL 使用大按钮
                create(INDEX_NO, DlgConst.NO, 'N', 0); // No 按钮
                create(INDEX_CANCEL, DlgConst.CANCEL, 'C', 0); // CANCEL 按钮
                break;

            case STYLE_DISABLE_ENABLE: // Disable + Enable 按钮// 目前不统一，应该统一。
                create(callMode == FOR_CUSTOMIZE ? // 普通和MACRO使用时， Disable实际使用 YES。
                INDEX_DISABLE
                        : INDEX_YES, DlgConst.DISABLE_BUTTON, 'D', BIG_BUTTON_WIDTH);
                create(callMode == FOR_CUSTOMIZE ? // 目前不统一，应该统一。 普通和MACRO使用时， Enable实际使用 NO。
                INDEX_ENABLE
                        : INDEX_NO, DlgConst.ENABLE_BUTTON, 'E', BIG_BUTTON_WIDTH);
                break;

            case STYLE_CONTINUE_END: // Continue + End 按钮 // 目前不统一，应该统一。
                create(callMode == FOR_CUSTOMIZE ? INDEX_CONTINUE : INDEX_OK, DlgConst.CONTINUE_BUTTON, 'C',
                        BIG_BUTTON_WIDTH); // 普通和MACRO使用时， Contunue实际使用 OK。
                create(callMode == FOR_CUSTOMIZE ? INDEX_END : INDEX_NO, DlgConst.END_BUTTON, 'E', BIG_BUTTON_WIDTH); // 目前不统一，应该统一。
                                                                                                                      // 普通和MACRO使用时，
                                                                                                                      // End实际使用
                                                                                                                      // NO。
                break;

            case STYLE_FIX_IGNORE: // Fix + Ignore 按钮
                create(INDEX_FIXED, DlgConst.FIX_BUTTON, 'F', BIG_BUTTON_WIDTH); // Fixed 按钮
                create(INDEX_IGNORE, DlgConst.IGNORE_BUTTON, 'I', BIG_BUTTON_WIDTH); // Ignore 按钮
                break;

            case STYLE_UPDATE_UNUPDATE: // Update + Unupdate 按钮
                create(INDEX_UPDATE, DlgConst.UPDATE_BUTTON, 'U', BIG_BUTTON_WIDTH); // Update 按钮
                create(INDEX_UNUPDATE, DlgConst.UNUPDATE_BUTTON, 'N', BIG_BUTTON_WIDTH); // Unupdate 按钮
                break;

            case STYLE_ABORT_RETRY_IGNORE: // Abort + Retry + Ignore 按钮
                create(INDEX_ABORT, DlgConst.ABORT_BUTTON, 'A', 0); // Abort 按钮
                create(INDEX_RETRY, DlgConst.RETRY_BUTTON, 'R', 0); // Retry 按钮
                create(INDEX_IGNORE, DlgConst.IGNORE_BUTTON, 'I', 0); // Ignore 按钮
                break;

            default:
                return;
        }
    }

    /**
     * 返回信息框使用的图标。以后应该使用图标库中的图标。
     * 
     * @param messageType
     *            信息类型, ERROR_MESSAGE,WARNING_MESSAGE,CONFIRM_MESSAGE,QUESTION_MESSAGE。
     * @return 信息框使用的图标.
     */
    private Icon getMessageIcon(
            int messageType) {
        switch (messageType) {
            case SOptionPane.ERROR_MESSAGE: // 错误图标
                return PIMPool.pool.getIcon("/org/cas/client/resource/img/pim/Error.gif");

            case SOptionPane.WARNING_MESSAGE: // 警告图标。
                return PIMPool.pool.getIcon("/org/cas/client/resource/img/pim/Warning.gif");

            case SOptionPane.CONFIRM_MESSAGE: // 确认图标。
                return PIMPool.pool.getIcon("/org/cas/client/resource/img/pim/Confirm.gif");

            default: // 不是上述类型，无图标。
                return null;
        }
    }

    /*
     * 显示MACRO调用的信息对话框。
     * @param parompt 信息文本内容。
     * @param buttonStyle 信息及按钮类型。
     * @param title 对话框标题。
     * @return 选择结果。
     */
    private int showMessage(
            String prmPrompt,
            int prmButtonStyle,
            String prmTitle) {
        callMode = FOR_MACRO;

        int defaultButton = DEFAULT_FOCUS_1; // 转换默认按钮。默认为第一个按钮
        if ((prmButtonStyle & DEFAULT_BUTTON2) != 0)
            defaultButton = DEFAULT_FOCUS_2; // 默认为第二个按钮
        else if ((prmButtonStyle & DEFAULT_BUTTON3) != 0)
            defaultButton = DEFAULT_FOCUS_3; // 默认为第三个按钮
        else if ((prmButtonStyle & DEFAULT_BUTTON4) != 0)
            defaultButton = DEFAULT_FOCUS_4; // 默认为第四个按钮

        int messageType = WARNING_MESSAGE; // 转换信息类型。默认为警告类型。
        if ((prmButtonStyle & CRITICAL) != 0)
            messageType = ERROR_MESSAGE; // 错误类型
        else if ((prmButtonStyle & QUESTION) != 0)
            messageType = QUESTION_MESSAGE; // 疑问类型
        else if ((prmButtonStyle & INFORMATION) != 0)
            messageType = CONFIRM_MESSAGE; // 确认类型

        int style = STYLE_OK; // 转换按钮类型，以后常量定义一致，只有简单转换。
        if ((prmButtonStyle & OK_CANCEL) != 0)
            style = STYLE_OK_CANCEL; // 为OK_CANCEL按钮
        else if ((prmButtonStyle & RETRY_CANCEL) != 0)
            style = STYLE_RETRY_CANCEL; // 为RETRY_CANCE按钮
        else if ((prmButtonStyle & YES_NO) != 0)
            style = STYLE_YES_NO; // 为YES_NO按钮
        else if ((prmButtonStyle & YES_NO_CANCEL) != 0)
            style = STYLE_YES_NO_CANCEL; // 为YES_NO_CANCEL按钮
        else if ((prmButtonStyle & YES_YESTOALL_NO_CANCEL) != 0)
            style = STYLE_YES_YESTOALL_NO_CANCEL; // 为YES_YESTOALL_NO_CANCEL按钮
        else if ((prmButtonStyle & CONTINUE_END) != 0)
            style = STYLE_CONTINUE_END; // 为CONTINUE_END按钮
        else if ((prmButtonStyle & DISABLE_ENABLE) != 0)
            style = STYLE_DISABLE_ENABLE; // 为DISABLE_ENABLE按钮
        else if ((prmButtonStyle & ABORT_RETRY_IGNORE) != 0)
            style = STYLE_ABORT_RETRY_IGNORE; // 为ABORT_RETRY_IGNORE按钮
        else if ((prmButtonStyle & UPDATE_UNUPDATE) != 0)
            style = STYLE_UPDATE_UNUPDATE; // 为UPDATE_UNUPDATE按钮
        else if ((prmButtonStyle & FIX_IGNORE) != 0)
            style = STYLE_FIX_IGNORE; // 为FIX_IGNORE按钮

        initDialog(prmTitle, prmPrompt, messageType, style, defaultButton | NO_HELP);// 初始化并显示对话框
        setVisible(true);

        return errorResult; // 返回选择结果
    }

    /*
     * 对话框打开时调用帮助相同。
     */
    private void showScreenHelp() {
        int index = this.messageIndex;// 信息的索引号
        if (index != 0) {
            StringBuffer buffer = new StringBuffer(6); // 存放错误信息，错误号由6个字符组成
            buffer.append(this.messageChar).append(this.optionChar);
            for (int divider = 1000; divider > 0; divider /= 10)
                // 把索引变为4个字符。 divider为被除数，分别为1000，100，10，1
                buffer.append((char) (index / divider + '0')); // +'0' 把数值转换为字符
            String[] help = new String[2];// 设置帮助主题。由Ebean统一处理。
            help[0] = "errorm/".concat(buffer.toString()); // "errorm/" 为帮助关键词
        }
    }

    /**
     * 初始化信息索引对应的信息类型、按钮选择类型、信息内容。
     * 
     * @param prmIndex
     *            信息的索引号。
     */
    private void initContent(
            int prmIndex) {
        if (indexCash == null) {
            indexCash = new char[CASH_SIZE]; // 初始化缓存。
            contentCash = new byte[CASH_SIZE][];
            try {
                byte[] info = read("/org/cas/client/resource/errorFile.idx", 0, 0); // 缓冲区用来存放读入信息的长度信息。
                if (getChar(info, 0) * 2 + 2 == info.length)// 检验长度正确性
                    lengthInfo = info;
            } catch (Exception e) { // 不需要处理
            }
        }

        for (int i = 0; i < cashCount; i++)
            // 检查索引号是否在缓存中 i为缓存的计数及索引
            if (indexCash[i] == prmIndex) {
                processBytes(contentCash[i], i, prmIndex); // 已在缓存的处理。
                return;
            }

        if (lengthInfo == null || prmIndex <= 0 || prmIndex * 2 >= lengthInfo.length) // 缓存及索引号检查合法性。
            return;
        int tmpSize = getChar(lengthInfo, prmIndex * 2); // 取得该索引的信息内容长度，为0表示不存在。
        if (tmpSize == 0)
            return;

        int tmpOffset = 0; // 计算该索引信息内容的在数据文件的偏移位置。
        for (int i = 1; i < prmIndex; i++)
            // i 为索引的计数。
            tmpOffset += getChar(lengthInfo, i * 2);

        processBytes(read("/org/cas/client/resource/errorFile.dat", tmpOffset, tmpSize), cashCount, prmIndex);// 读入信息内容并处理。/client/resource/errorFile.dat
                                                                                                              // 为数据文件路径
    }

    /**
     * 处理从文件读出或从缓存取出的信息内容，放到缓存最前面， 并且解析为信息类型字符、按钮选择字符、信息文本内容。
     * 
     * @param prmBuffer
     *            信息内容，从文件读出或从缓存取出。
     * @param prmOrder
     *            在缓存的位置，等于目前缓存内容个数是表示不在缓存中。
     * @param prmIndex
     *            信息框的索引号。
     */
    private void processBytes(
            byte[] prmBuffer,
            int prmOrder,
            int prmIndex) {
        if (prmBuffer == null)
            return;

        if (prmOrder != 0) {
            if (prmOrder == CASH_SIZE) // 相等表示缓存已满，最多移动CASH_SIZE - 1个
                prmOrder = CASH_SIZE - 1;
            System.arraycopy(contentCash, 0, contentCash, 1, prmOrder); // 把前面的内容往后移动一个位置
            System.arraycopy(indexCash, 0, indexCash, 1, prmOrder);
        }

        contentCash[0] = prmBuffer; // 内容放到缓存的最前面
        indexCash[0] = (char) prmIndex;

        if (prmOrder == cashCount && cashCount < CASH_SIZE) // 不是缓存中的数据且缓存未满，缓存内容个数加1
            cashCount++;

        messageChar = (char) prmBuffer[0]; // 取出信息类型字符（c/e/q/w)
        char optionChar = (char) prmBuffer[1]; // 取出按钮选择类型字符
        this.optionChar = (char) (optionChar & 0x7F); // 真正的类型字符，Bit7为有无子串的标志
        try {
            int tmpSize = prmBuffer.length; // 缓冲区的字节长度。
            if ((optionChar & 0x80) != 0) { // 判断是否含有多个字串
                int count = prmBuffer[2]; // 子串个数。
                int offset = 3; // 从第3个开始为各子串：子串长度 ，子串内容。
                for (int i = 0; i < count; i++) { // i为子串的计数
                    int charCount = getChar(prmBuffer, offset); // 取出字串长度
                    getStringBuffer(prmBuffer, i, offset + 2, charCount); // 取出字符串
                    offset += charCount * 2 + 2; // 每个字符占2个字节。长度2字节
                }
            } else
                getStringBuffer(prmBuffer, 0, 2, (tmpSize - 2) / 2); // 取出字符串，字符个数为信息内容长度－信息及按钮类型的一半
        } catch (Exception e) { // 避免出现意外。
        }
    }

    /**
     * 从信息内容中取出信息文本。
     * 
     * @param buffer
     *            信息内容。
     * @param messageOrder
     *            字符串的序号。
     * @param offset
     *            字符串在信息内容的偏移位置。
     * @param count
     *            字符串的个数。
     * @return 字符串缓冲区。
     */
    private void getStringBuffer(
            byte[] buffer,
            int messageOrder,
            int offset,
            int count) {
        StringBuffer str = new StringBuffer(count); // 存放文本的字符串缓冲。
        for (int i = 0; i < count; i++)
            // i 为字符串个数的计数及索引。
            str.append((char) getChar(buffer, offset + i * 2));

        if (messageOrder == 0) // 根据字符串的序号，保存到不同的变量。
            this.message1 = str; // 第一段信息
        else if (messageOrder == 1)
            this.message2 = str; // 第二段信息
        else
            this.message3 = str; // 第三段信息
    }

    /**
     * 根据缓冲区和偏移取出字符型（2字节）的整数。
     * 
     * @param buffer
     *            缓冲区。
     * @param offset
     *            该整数的偏移位置。
     * @return 字符整型。
     */
    private int getChar(
            byte[] buffer,
            int offset) {
        return (buffer[offset] << 8) + (buffer[offset + 1] & 255);
    }

    /**
     * 读入指定文件和偏移长度的字节。
     * 
     * @param fileName
     *            文件名。
     * @param offset
     *            开始读的文件偏移。
     * @param length
     *            读入的长度，为0时表示读入全部。
     * @return 读入的字节数组，null表示发生了错误。
     */
    private byte[] read(
            String fileName,
            int offset,
            int length) {
        InputStream file = getClass().getResourceAsStream(fileName); // 对应文件的文件流。
        if (file == null)
            return null;
        try {
            if (length == 0)
                length = file.available();// 需要读入全部内容。
            else if (offset != 0)
                file.skip(offset);

            byte[] buffer = new byte[length]; // 读入内容的缓冲区。
            offset = 0;

            while (length > 0) { // 读入内容。
                int readCount = file.read(buffer, offset, length); // 读入内容的字节数
                if (readCount == -1)
                    break;
                offset += readCount;
                length -= readCount;
            }
            return buffer; // 返回读入的内容
        } catch (Exception e) { // 发生了错误。
            return null;
        } finally {
            try {
                file.close(); // 关闭文件流
            } catch (Exception e) {
                // 这里不需要处理任何事情。
            }
        }
    }

    private JButton[] buttons; // 存放可能的按钮的一个数组.
    private int x; // 最左边的按钮x方向的起始位置。
    private int y; // 按钮的y方向的位置。
    private int count; // 按钮的计数.
    private int buttonState; // 第一个按钮的状态，仅在允许设置时有效.
    private FontMetrics metrics; // 对话框的FontMetrics。

    private char messageChar; // 普通对话框的信息类型，“e，c，w，q ”之一
    private char optionChar; // 普通对话框的按钮选择信息，'1'-'9','a-f'之一
    private StringBuffer message1; // 普通对话框的第一个信息内容。
    private StringBuffer message2; // 普通对话框的第二个信息内容。
    private StringBuffer message3; // 普通对话框的第三个信息内容。
    private int messageFlag; // 对话框的标志.
    private int messageIndex; // 普通对话框的索引号。

    private JButton ok;
    private JButton cancel;
    private static Icon errorIcon;
    private static Icon confirmIcon;
    private static Icon warningIcon;
}
