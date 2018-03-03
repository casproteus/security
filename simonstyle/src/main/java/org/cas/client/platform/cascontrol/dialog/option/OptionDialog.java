package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;

public class OptionDialog extends JDialog implements ActionListener {
    /**
     * 构建器
     * 
     * @param prmFrame
     *            父窗体
     * @param prmIsModel
     *            是否模式
     */
    public OptionDialog(Frame prmFrame, boolean prmIsModel) {
        super(prmFrame, prmIsModel);
        setTitle(DlgConst.OPTION_DIALOG);

        Dimension size = new Dimension(480, 400);
        width = size.width;
        height = size.height;

        CustOpts.custOps = CustOpts.custOps;

        initDialog();

        setBounds((CustOpts.SCRWIDTH - width) / 2, (CustOpts.SCRHEIGHT - height) / 2, width, height); // 对话框的默认尺寸。
        show();
    }

    /**
     * 初始化对话盒
     */
    private void initDialog() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabMail = new TabMail(this, width, height - -CustOpts.BTN_HEIGHT - 2 * CustOpts.VER_GAP);
        tabCalendar = new TabCalendar(this, width, height - 2 * CustOpts.VER_GAP - CustOpts.BTN_HEIGHT);
        tabLinkMan = new TabLinkMan(this, width, height - 2 * CustOpts.VER_GAP - CustOpts.BTN_HEIGHT);
        tabTask = new TabTask(this, width, height - 2 * CustOpts.VER_GAP - CustOpts.BTN_HEIGHT);
        tabNote = new TabNote(this, width, height - 2 * CustOpts.VER_GAP - CustOpts.BTN_HEIGHT);
        tabOrther = new TabOrther(this, width, height - 2 * CustOpts.VER_GAP - CustOpts.BTN_HEIGHT);
        // tabSecure = new TabSecure(this,width,height - 2*CustOpts.VER_GAP - CustOpts.BTN_HEIGHT);

        tabbedPane.addTab(OptionDlgConst.OPTION_MAIL, tabMail);
        tabbedPane.addTab(OptionDlgConst.OPTION_CALENDAR, tabCalendar);
        // tabbedPane.addTab(OptionDialogConstant.OPTION_CONTACT, tabLinkMan);
        // tabbedPane.addTab(OptionDialogConstant.OPTION_TASK, tabTask);
        // tabbedPane.addTab(OptionDialogConstant.OPTION_DIARY, tabNote);
        // tabbedPane.addTab(OptionDialogConstant.OPTION_OTHER, tabOrther);
        // tabbedPane.addTab("安全",tabSecure);
        tabbedPane.setBounds(0, 0, width, height - 2 * CustOpts.VER_GAP - CustOpts.BTN_HEIGHT);
        getContentPane().add(tabbedPane);

        ok = new JButton(DlgConst.OK);
        getContentPane().add(ok);
        ok.setBounds(width - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, height - CustOpts.BTN_HEIGHT,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel = new JButton(DlgConst.CANCEL);
        getContentPane().add(cancel);
        cancel.setBounds(width - CustOpts.BTN_WIDTH, height - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        ok.addActionListener(this);
    }

    /**
     * 确定动作
     */
    private void okClicked() {
        tabMail.setMailInfo();
        tabCalendar.setCalendarInfo();
        // tabLinkMan.setContactsInfo();
        // tabTask.setTaskInfo();
        // tabNote.setNotInfo();
        // tabOrther.setOtherInfor();
        CustOpts.custOps.saveData();
    }

    /**
     * 处理按钮动作
     * 
     * @param actionEvent
     *            动作事件
     */
    public void actionPerformed(
            java.awt.event.ActionEvent actionEvent) {
        okClicked();
        dispose();
    }

    //
    // public static void main(String [] args)
    // {
    // new OptionDialog(new java.awt.Frame(),false).show();
    // }

    private int width;
    private int height;
    private JButton ok;
    private JButton cancel;
    private TabMail tabMail;
    private TabCalendar tabCalendar;
    private TabLinkMan tabLinkMan;
    private TabTask tabTask;
    private TabNote tabNote;
    private TabOrther tabOrther;
    // private TabSecure tabSecure;
}
