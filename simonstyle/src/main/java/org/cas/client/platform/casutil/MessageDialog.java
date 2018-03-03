/*
 * ErrorDialog.java
 *
 * Created on 2003年12月23日, 下午4:17
 */
package org.cas.client.platform.casutil;

import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

/**
 * 一个带CheckBox的信息框。运行用户设置成以后不再提示.@TODO:应该考虑合并到PIMOptionPane中.
 */

public class MessageDialog extends JDialog implements ActionListener {
    /**
     * 显示一个有checkbox的ErrorDialog，并有返回值
     *
     * @called by : CheckInboxForContactAction.actionPerformed()
     */
    public static int showMessageDialog(
            Frame parent,
            String prmPrompMessage,
            String prmCheckMess) {
        new MessageDialog(parent, prmPrompMessage, prmCheckMess).setVisible(true);
        if (isCheckSelected)
            errorResult *= 10;
        return errorResult;
    }

    /** 使用窗体作为 父容器。 */
    public MessageDialog(Frame parent, String prmPrompMessage) {
        super(parent, false);
        initDialog(PaneConsts.TITLE, prmPrompMessage, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                SOptionPane.DEFAULT_FOCUS_1 | SOptionPane.NO_HELP); // 初始化并显示对话框
    }

    /**
     * 使用窗体作为 父容器。
     * 
     * @param parent
     *            父窗体。
     * @param prmMessage
     *            提示信息
     * @param prmCheckMess
     *            checkbox的提示信息
     */
    public MessageDialog(Frame parent, String prmPrompMessage, String prmCheckMess) {
        super(parent, true);
        checkBoxMess = prmCheckMess;
        haveCheckbox = true;
        isCheckSelected = false;
        initDialog(PaneConsts.TITLE, prmPrompMessage, SOptionPane.CONFIRM_MESSAGE, SOptionPane.STYLE_YES_NO,
                SOptionPane.DEFAULT_FOCUS_1 | SOptionPane.NO_HELP);
        // checkBox = new JCheckBox(prmCheckMess, false, 'E', this, this);
        setVisible(true);
    }

    /**
     * 初始化对话框的标题、信息图标、信息等。
     * 
     * @param prmTitle
     *            对话框标题。
     * @param message
     *            对话框的信息文本。
     * @param prmButtonStyle
     *            按钮选择类型。
     * @param messageFlag
     *            对话框的一些标志。
     */
    private void initDialog(
            String prmTitle,
            String message,
            int messageType,
            int prmButtonStyle,
            int messageFlag) {
        this.messageFlag = messageFlag;
        // 设置对话框标题，若为空，使用默认标题。
        setTitle(prmTitle != null ? prmTitle : PaneConsts.TITLE);
        metrics = getFontMetrics(CustOpts.custOps.getFontOfDefault());
        // 字体的高度，代码没有改变原来的做法。实际上用getHeight即可。
        int fontHeight = metrics.getAscent() + metrics.getDescent() + metrics.getLeading();
        // 放置图标的组件
        JLabel dialogLabel = new JLabel();
        // 避免以后判断空指针。
        if (message == null)
            message = CASUtility.EMPTYSTR;
        // 文本显示的最大宽度
        int position = getPosition(message, prmButtonStyle);
        // 文本区域宽度
        int areaWidth = position + CustOpts.HOR_GAP;
        // 最终显示的文本
        String string = getAreaContents(message, position);
        if (ErrorLabel.isSpecial()) {
            areaWidth += CustOpts.HOR_GAP;
        }
        // 检查文本的行数，以回车为分隔，至少为一行。
        int lines = 1;
        // i 为字符在字符串中的位置，length为字符串的长度。
        for (int i = 0, length = string.length(); i < length; i++)
            // 假如为回车，则增加一行
            if (string.charAt(i) == '\n')
                lines++;
        // 文本及图标区域的高度
        int areaHeight = Math.max(lines * fontHeight + 4, DlgConst.IMAGE_SIZE);
        // 文本及图标区域的宽度
        int width = areaWidth + (SOptionPane.MIN_AREA_X + CustOpts.HOR_GAP + 2);
        // 包含了按钮或的高度
        int height = areaHeight + (CustOpts.VER_GAP * 2 + CustOpts.BTN_HEIGHT);
        if (haveCheckbox) {
            checkBox = new JCheckBox(checkBoxMess, false);
            checkBox.setMnemonic('E');
            checkBox.setBounds(SOptionPane.MIN_AREA_X, height - CustOpts.LBL_HEIGHT - CustOpts.BTN_HEIGHT, width,
                    CustOpts.LBL_HEIGHT);
            height += CustOpts.VER_GAP;
            getContentPane().add(checkBox);
        }
        // 设置信息的图标
        dialogLabel.setIcon(getMessageIcon(messageType));
        // 创建按钮
        setButtonStyle(prmButtonStyle, width, height);
        dialogLabel.setBounds(0, fontHeight * Math.max(lines, 2) / 2 - DlgConst.IMAGE_SIZE / 2 + 4,
                DlgConst.IMAGE_SIZE, DlgConst.IMAGE_SIZE);
        // 创建文本标签组件
        ErrorLabel areaLabel = new ErrorLabel(string);
        areaLabel.setBounds(SOptionPane.MIN_AREA_X + (areaWidth - position) / 2, CustOpts.VER_GAP - 2, areaWidth + 4,
                areaHeight);
        // 添加组件
        getContentPane().add(areaLabel);
        getContentPane().add(dialogLabel);
        // 初始化对话框
        setBounds((CustOpts.SCRWIDTH - width) / 2, (CustOpts.SCRHEIGHT - height) / 2, width, height); // 对话框的默认尺寸。
        // 默认选择为“取消”
        errorResult = SOptionPane.CANCEL;
        isErrorShowing = true;
    }

    /**
     * 通过按钮选择类型返回所有按钮及间隙的总宽度。
     * 
     * @param prmButtonStyle
     *            按钮选择类型。
     * @return 所有按钮及间隙的总宽度。
     */
    private int getButtonsWidth(
            int prmButtonStyle) {
        switch (prmButtonStyle) {
            case SOptionPane.STYLE_OK_CANCEL:
            case SOptionPane.STYLE_OK_HELP:
            case SOptionPane.STYLE_RETRY_CANCEL:
            case SOptionPane.STYLE_YES_NO:
                // 两个一般按钮，一个间隙
                return CustOpts.BTN_WIDTH * 2 + CustOpts.HOR_GAP;

            case SOptionPane.STYLE_CONTINUE_END:
            case SOptionPane.STYLE_DISABLE_ENABLE:
            case SOptionPane.STYLE_FIX_IGNORE:
            case SOptionPane.STYLE_UPDATE_UNUPDATE:
                // 两个大的按钮，一个间隙
                return SOptionPane.BIG_BUTTON_WIDTH * 2 + CustOpts.HOR_GAP;

            case SOptionPane.STYLE_YES_NO_CANCEL:
            case SOptionPane.STYLE_OK_CANCEL_HELP:
            case SOptionPane.STYLE_YES_NO_HELP:
            case SOptionPane.STYLE_ABORT_RETRY_IGNORE:
                // 三个一般的按钮，两个间隙
                return CustOpts.BTN_WIDTH * 3 + CustOpts.HOR_GAP * 2;

            case SOptionPane.STYLE_YES_YESTOALL_NO_CANCEL:
                // 三个一般按钮，一个大按钮，三个间隙
                return CustOpts.BTN_WIDTH * 3 + SOptionPane.BIG_BUTTON_WIDTH + CustOpts.HOR_GAP * 3;

                // case PIMOptionPane.STYLE_OK:
            default:
                // 一个一般的按钮，没有间隙
                return CustOpts.BTN_WIDTH;
        }
    }

    /**
     * 判断在何处应该切分字符串。
     * 
     * @param str
     *            对话框信息内容。
     * @return prmButtonStyle 按钮选择类型。
     */
    private int getPosition(
            String str,
            int prmButtonStyle) {
        // 得到所有按钮及其之间的间隙的宽度。
        int minWidth = getButtonsWidth(prmButtonStyle);
        // 判断是否本身多行。
        int point = str.indexOf('\n');
        if (point < 0) {
            // 不知道算法. 为何 2.5 ？
            int width = (int) (metrics.stringWidth(str) / 2.5);
            // 处理只有一个按钮的特例。
            if (minWidth == CustOpts.BTN_WIDTH) {
                if (width < CustOpts.BTN_WIDTH + 2 * CustOpts.HOR_GAP) {
                    width = CustOpts.BTN_WIDTH + 2 * CustOpts.HOR_GAP;
                }
            }
            width += minWidth;
            // 不能超过规定宽度
            if (width > SOptionPane.MAX_WIDTH) {
                return SOptionPane.MAX_WIDTH;
            }
            return width;
        }
        // 下面得到显示最长的子串的显示宽度。
        int maxWidth = 0;
        // start 为一行子串在原字符串的位置。
        for (int start = 0; point >= 0;) {
            // 一行的最大显示宽度
            maxWidth = Math.max(maxWidth, metrics.stringWidth(str.substring(start, point)));
            // 开始为下一个位置
            start = point + 1;
            point = str.indexOf('\n', start);
            // 若为最后以后，计算以后的所有字符的显示宽度。
            if (point < 0) {
                maxWidth = Math.max(maxWidth, metrics.stringWidth(str.substring(start)));
            }
        }
        // 不能超过规定宽度
        if (maxWidth > SOptionPane.MAX_WIDTH) {
            return SOptionPane.MAX_WIDTH;
        }
        // 不能小于最小宽度。
        if (maxWidth < minWidth) {
            return minWidth;
        }
        return maxWidth;
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
        // 分切后余下的文本，初始为原始文本
        String remainText = str;
        // 分切后的结果（文本）
        StringBuffer result = null;
        // 分切的原始文本的开始位置
        int start = 0;
        // 分切动作直到余下的文本长度为0
        while (remainText.length() > 0) {
            // 此算法不详。
            String breakString = ErrorLabel.getBreakString(remainText, metrics, cut);
            if (breakString == null) {
                break;
            }
            start += breakString.length();
            if (result == null) {
                result = new StringBuffer();
            }
            result.append(breakString);
            // 假如不是因为换行，则需要添加换行
            if (!ErrorLabel.hasEnter()) {
                result.append('\n');
            }
            remainText = str.substring(start);
        }
        // 假如结果为空，表示没有分切，使用原来的文本。
        return result == null ? str : result.toString();
    }

    /**
     * 创建按钮，并设置一些属性。
     * 
     * @param index
     *            按钮的索引。
     * @param text
     *            按钮的文本。
     * @param mnemonic
     *            按钮的助记符，'\0'表示无助记符。
     * @param width
     *            按钮的宽度，0 表示正常按钮宽度。
     */
    private void create(
            int index,
            String text,
            char mnemonic,
            int width) {
        // 宽度为0， 设置为正常宽度。
        if (width == 0) {
            width = CustOpts.BTN_WIDTH;
        }
        // 创建按钮
        JButton button = new JButton(text);
        button.setMnemonic(mnemonic);
        button.setBounds(this.x, this.y, width, CustOpts.BTN_HEIGHT);
        getContentPane().add(button);
        // 设置下一个按钮的x位置，当前位置加按钮宽度和间隙
        this.x += width + CustOpts.HOR_GAP;
        // 保存按钮到对应的位置
        this.buttons[index] = button;
        // 判断是否为CANCEL按钮
        if (index == SOptionPane.INDEX_CANCEL) {
            // 赋值给cancel变量
            this.cancel = button;
        } else {
            // 判断是否为OK按钮
            if (index == SOptionPane.INDEX_OK) {
                // 当允许设置按钮状态，且功能无效时这时按钮为灰。
                if ((this.messageFlag & SOptionPane.CHECK_STATE) != 0 && this.buttonState == SOptionPane.BUTTON_UNABLE) {
                    button.setEnabled(false);
                }
                // 赋值给ok变量
                this.ok = button;
            }
            // 按钮添加动作监听器
            button.addActionListener(this);
        }
        // 假如默认按钮不是第一个，则设置相应位置的按钮为默认按钮。
        int defaultButton = this.messageFlag % SOptionPane.MAX_BUTTONS;
        if (defaultButton > 0 && defaultButton == this.count) {
            getRootPane().setDefaultButton(button);
        }
        // 按钮计数更新
        this.count++;
    }

    /**
     * 创建按钮。
     * 
     * @param prmButtonStyle
     *            按钮类型。
     * @param width
     *            对话框上面板的宽度。
     * @param height
     *            按钮底边的位置。
     */
    private void setButtonStyle(
            int prmButtonStyle,
            int width,
            int height) {
        // 最左边的按钮x方向的起始位置。
        this.x = (width - getButtonsWidth(prmButtonStyle)) / 2;
        // 按钮的y方向的位置。
        this.y = height - CustOpts.BTN_HEIGHT;
        // 所有可能的按钮的数组
        this.buttons = new JButton[SOptionPane.COUNT_OF_INDEX];
        // 仅 OK 按钮
        // '\0' 表示无助记符， 0 表示为正常按钮宽度。
        // OK 按钮
        if (haveCheckbox) {
            create(SOptionPane.INDEX_YES, DlgConst.YES, '\0', 0);
            create(SOptionPane.INDEX_NO, DlgConst.NO, '\0', 0);
        } else {
            create(SOptionPane.INDEX_OK, DlgConst.OK, '\0', 0);
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

    /** 按钮激发动作。 */
    public void actionPerformed(
            ActionEvent e) {
        Object button = e.getSource(); // 激发动作的按钮
        if (button == checkBox)
            isCheckSelected = checkBox.isSelected();

        for (int index = 0; index < SOptionPane.COUNT_OF_INDEX; index++)
            // index 作为按钮数组的下标
            if (button == this.buttons[index]) { // 判断是哪个按钮
                errorResult = index; // 设置返回值，这里返回值与按钮的索引是一致的。
                if (isErrorShowing)
                    dispose(); // 避免多次调用。不过这里可能有问题。
                isErrorShowing = false;
                return; // 不继续判断，直接返回。
            }
    }

    /**
     * 对话框关闭时，做一些清除动作。
     */
    protected void extraAction() {
        // 清除所有按钮对象的引用
        this.buttons = null;
        if (checkBox != null) {
            checkBox.removeActionListener(this);
            checkBox = null;
        }
        // 清除信息的对象引用
        this.metrics = null;
    }

    // public static void main(String afgs[])
    // {
    // new MessageDialog(null, "ssssssssssssss");
    // }

    public static int errorResult;// 对话框的返回值
    private static boolean isErrorShowing; // 信息对话框是否在显示标志。
    private JButton ok, cancel;
    private JButton[] buttons;// 存放可能的按钮的一个数组.
    private int x;// 最左边的按钮x方向的起始位置。
    private int y; // 按钮的y方向的位置。
    private int count; // 按钮的计数.
    private int buttonState;// 第一个按钮的状态，仅在允许设置时有效.
    private FontMetrics metrics; // 对话框的FontMetrics。
    private int messageFlag; // 对话框的标志.
    private int messageIndex;// 普通对话框的索引号。
    private boolean haveCheckbox;
    private static boolean isCheckSelected;
    private String checkBoxMess;
    private JCheckBox checkBox;
}
