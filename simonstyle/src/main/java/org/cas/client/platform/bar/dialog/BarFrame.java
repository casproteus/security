package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMRecord;

public class BarFrame extends JFrame implements ICASDialog, ActionListener, WindowListener, ComponentListener {

    public static BarFrame instance;

    public static void main(
            String[] args) {
        if (instance == null) {
        	new LoginDlg(instance).setVisible(true);
            if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            	CustOpts.custOps.setUserName(LoginDlg.USERNAME);
            }else {
            	return;
            }
            instance = new BarFrame();
        }
        instance.setVisible(true);
    }

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
    public BarFrame() {
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        general.setBounds(0, 0, getContainer().getWidth(), getContainer().getHeight());
        general.componentResized(null);
        validate();
    }

    @Override
    public PIMRecord getContents() {
        return null;
    }

    @Override
    public boolean setContents(
            PIMRecord prmRecord) {
        return true;
    }

    @Override
    public void makeBestUseOfTime() {
    }

    @Override
    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    @Override
    public PIMTextPane getTextPane() {
        return null;
    }

    @Override
    public void release() {
        removeWindowListener(this);
        if (general != null) {
            general.removeAll();
            general = null;
        }
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made invisible. */
    @Override
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
    @Override
    public void actionPerformed(
            ActionEvent e) {
    }

    @Override
    public void windowActivated(
            WindowEvent e) {
    }

    @Override
    public void windowClosed(
            WindowEvent e) {
    }

    @Override
    public void windowClosing(
            WindowEvent e) {
        setVisible(false);
        if (CASControl.ctrl.getMainFrame() != null)
            CASControl.ctrl.getMainFrame().dispose(); // 先隐藏视图，为的是给用户感觉关闭得比较快。
        dispose();
        try {
            CASControl.ctrl.exitSystem(); // 保存所有状态和数据后退出。
        } catch (Exception exp) {
            ErrorUtil.write(exp);
            // System.exit(0);
        }
        instance = null;
    }

    @Override
    public void windowDeactivated(
            WindowEvent e) {
    }

    @Override
    public void windowDeiconified(
            WindowEvent e) {
    }

    @Override
    public void windowIconified(
            WindowEvent e) {
    }

    @Override
    public void windowOpened(
            WindowEvent e) {
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarDlgConst.Title);
        setIconImage(CustOpts.custOps.getFrameLogoImage()); // 设置主窗体的LOGO。

        setBounds(0, 0, CustOpts.SCRWIDTH, CustOpts.SCRHEIGHT); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        setResizable(true);

        // 初始化－－－－－－－－－－－－－－－－
        general = new BarGeneralPanel();

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(general);

        // 加监听器－－－－－－－－
        addWindowListener(this);
        getContentPane().addComponentListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                //general.tfdProdNumber.grabFocus();
            }
        });
    }

    boolean hasClose; // 标志对话框是否已关闭
    public BarGeneralPanel general;
}
