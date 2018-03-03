package org.cas.client.platform.cascontrol.dialog.option;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;

class EmailOptionDialog extends JDialog implements ActionListener {
    /**
     * Creates a new instance of EmailOption
     * 
     * @param prmDialog
     *            父窗体
     * @param prmIsModel
     *            是否模式
     * @param prmCustomOptions用户自定义
     */
    public EmailOptionDialog(JDialog prmDialog, boolean prmIsModel) {
        super(prmDialog, prmIsModel);

        setTitle(OptionDlgConst.OPTION_EMAIL_OPTION);

        Dimension size = new Dimension(400, 400);
        dialog = prmDialog;
        width = size.width;
        height = size.height;

        initDialog();

        setBounds((CustOpts.SCRWIDTH - width) / 2, (CustOpts.SCRHEIGHT - height) / 2, width, height); // 对话框的默认尺寸。
    }

    /**
     * 初始化对话盒
     */
    private void initDialog() {
        int paneHeight = height - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP;
        tabpnlMain1 = new JTabbedPane();
        tabEmailManage = new TabEmailManage(this, width, paneHeight);
        tabEmailAdvance = new TabEmailAdvance(this, width, paneHeight);
        tabEmailTrack = new TabEmailTrack(this, width, paneHeight);

        tabpnlMain1.addTab(OptionDlgConst.OPTION_EMAIL_DISPOSE, tabEmailManage);
        tabpnlMain1.addTab(OptionDlgConst.OPTION_ADVANCE, tabEmailAdvance);
        tabpnlMain1.addTab(OptionDlgConst.OPTION_TRACK, tabEmailTrack);

        tabpnlMain1.setBounds(0, 0, width, paneHeight);

        getContentPane().add(tabpnlMain1);

        ok = new JButton(DlgConst.OK);
        ok.setBounds(width - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, height - CustOpts.BTN_HEIGHT,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel = new JButton(DlgConst.CANCEL);
        cancel.setBounds(width - CustOpts.BTN_WIDTH, height - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        ok.addActionListener(this);
        getContentPane().add(ok);
        getContentPane().add(cancel);
    }

    /**
     * 设置电子邮件信息
     */
    void setInfo() {
        tabEmailAdvance.setMailAdvanceInfo();
        tabEmailManage.setMailDealInfo();
        tabEmailTrack.setTrackInfo();
    }

    /**
     * 在点击对话盒右上角的关闭按钮或按下"ESCAPE"键,会使对话盒销毁,此过程会调用 此方法,所以重载此方法,如父容器是对话盒,只是不显示而不销毁.
     */
    protected void realClose() {
        if (getParent() instanceof JDialog) {
            setVisible(false);
        } else {
            dispose();
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        Object objSource = e.getSource();
        if (objSource == ok) {
            setInfo();
            setVisible(false);
        }
    }

    /**
     * 本对话盒ID
     */
    public static int id;
    private JDialog dialog;
    private JButton ok;
    private JButton cancel;
    private int width;
    private int height;
    private JTabbedPane tabpnlMain1;
    private TabEmailAdvance tabEmailAdvance;
    private TabEmailManage tabEmailManage;
    private TabEmailTrack tabEmailTrack;
}
