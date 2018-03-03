/*
 * PIMAboutDlg.java
 *
 * Created on 2003年11月27日, 下午4:34
 */

package org.cas.client.platform.cascontrol.dialog;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

/**
 */
public class CASAboutDlg extends JDialog implements ActionListener {
    private static String VERSIONNUMBER = "DMYX0128";
    private static int CLEARANCE = 10;

    /** Creates a new instance of PIMAboutDlg */
    public CASAboutDlg(Frame prmParent, boolean prmModel) {
        super(prmParent, prmModel);
        label = new JLabel(PaneConsts.TITLE);
        label2 = new JLabel(PaneConsts.HELP_DIALOG_CONTENT2);
        cancel = new JButton(DlgConst.OK);

        label.setBounds(0, CLEARANCE, label.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(label);

        label2.setBounds(0, CLEARANCE + CustOpts.LBL_HEIGHT, label2.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(label2);
        cancel.setBounds(115, 66, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);
        cancel.addActionListener(this);

        setTitle(PaneConsts.HELP_DIALOG_TITLE);
        setBounds((CustOpts.SCRWIDTH - 300) / 2, (CustOpts.SCRHEIGHT - 90) / 2, 300, 90); // 对话框的默认尺寸。
    }

    public void actionPerformed(
            ActionEvent e) {
        dispose();
    }

    public static String getVersion() {
        return VERSIONNUMBER;
    }

    private JButton cancel;
    private JLabel label;
    private JLabel label2;
}
