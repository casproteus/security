package org.cas.client.platform.magicbath.dialog;

import java.awt.Container;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

public class MagicbathDlg extends JDialog implements ICASDialog, ActionListener, WindowListener, ComponentListener {
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     * @param prmAction
     *            OK时的操作
     * @param prmRecord
     *            一条记录
     * @param parent
     *            弹出对话框的Frame
     */
    public MagicbathDlg(Frame parent) {
        super(parent, false);
        isPartOfView = (parent == null);
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    public void reLayout() {
        cancel.setBounds(getContainer().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getContainer().getHeight()
                - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP, isPartOfView ? 0 : CustOpts.BTN_WIDTH, isPartOfView ? 0
                : CustOpts.BTN_HEIGHT);// 关闭
        general.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getContainer().getWidth() - 2 * CustOpts.HOR_GAP,
                (isPartOfView ? getContainer().getHeight() : cancel.getY()) - 2 * CustOpts.VER_GAP);
        general.componentResized(null);
        validate();
    }

    public PIMRecord getContents() {
        return null;
    }

    public boolean setContents(
            PIMRecord prmRecord) {
        return true;
    }

    public void makeBestUseOfTime() {
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    public void release() {
        cancel.removeActionListener(this);
        removeWindowListener(this);
        if (general != null) {
            general.removeAll();
            general = null;
        }
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made invisible. */
    public void componentHidden(
            ComponentEvent e) {
    };

    /**
     * Invoked when an action occurs. NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
     * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
     * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        if (e.getSource() == cancel)
            dispose();
    }

    /**
     * Invoked when the Window is set to be the active Window. Only a Frame or a Dialog can be the active Window. The
     * native windowing system may denote the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
     * is an owner of the focused Window.
     */
    public void windowActivated(
            WindowEvent e) {
    }

    /**
     * Invoked when a window has been closed as the result of calling dispose on the window.
     * 
     * @NOTE:当通过左上角Windows菜单，或右上角的叉号关闭时不触发本事件，而触发windowClosing事件。
     */
    public void windowClosed(
            WindowEvent e) {
    }

    /**
     * Invoked when the user attempts to close the window from the window's system menu. If the program does not
     * explicitly hide or dispose the window while processing this event, the window close operation will be cancelled.
     * 
     * @Note:当通过OK,Cancel按钮关闭时不触发本事件，而触发windowClosed事件。
     */
    public void windowClosing(
            WindowEvent e) {
        release();
    }

    /**
     * Invoked when a Window is no longer the active Window. Only a Frame or a Dialog can be the active Window. The
     * native windowing system may denote the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused Window, or the first Frame or Dialog that
     * is an owner of the focused Window.
     */
    public void windowDeactivated(
            WindowEvent e) {
    }

    /** Invoked when a window is changed from a minimized to a normal state. */
    public void windowDeiconified(
            WindowEvent e) {
    }

    /**
     * Invoked when a window is changed from a normal to a minimized state. For many platforms, a minimized window is
     * displayed as the icon specified in the window's iconImage property.
     * 
     * @see java.awt.Frame#setIconImage
     */
    public void windowIconified(
            WindowEvent e) {
    }

    /** Invoked the first time a window is made visible. */
    public void windowOpened(
            WindowEvent e) {
    }

    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(DlgConst.DlgTitle);

        setBounds((CustOpts.SCRWIDTH - 560) / 2, (CustOpts.SCRHEIGHT - 474) / 2, 560, 474); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        setResizable(true);

        // 初始化－－－－－－－－－－－－－－－－
        general = new MagicbathGeneralPanel();
        cancel = new JButton(DlgConst.FINISH_BUTTON);

        // 属性设置－－－－－－－－－－－－－－
        cancel.setFont(CustOpts.custOps.getFontOfDefault());
        getRootPane().setDefaultButton(cancel);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(general);
        getContentPane().add(cancel);

        // 加监听器－－－－－－－－
        cancel.addActionListener(this);
        addWindowListener(this);
        getContentPane().addComponentListener(this);
    }

    boolean isPartOfView; // 用于标记本对话盒是独立显示还是作为View的一部分.
    boolean hasClose; // 标志对话框是否已关闭
    private JButton cancel;
    private MagicbathGeneralPanel general;
}
