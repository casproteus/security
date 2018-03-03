package org.cas.client.platform.cascontrol.dialog.adduserdlg;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.cascustomize.CustOpts;

public class AddUserGeneralPanel extends JPanel implements ComponentListener {

    public AddUserGeneralPanel() {
        initConponent();
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    public void componentHidden(
            ComponentEvent e) {
    }

    private void initConponent() {
        lblUserName = new JLabel(AddUserDlgConst.UserName);
        lblPassword = new JLabel(AddUserDlgConst.Password);
        lblMakesure = new JLabel(AddUserDlgConst.Makesure);

        tfdUserName = new JTextField();
        tfdPassword = new JTextField();
        tfdMakesure = new JTextField();

        // properties
        setLayout(null);

        // built
        add(lblUserName);
        add(lblPassword);
        add(lblMakesure);

        add(tfdUserName);
        add(tfdPassword);
        add(tfdMakesure);

        // add listener
        addComponentListener(this);
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void reLayout() {
        int prmWidth = getWidth();
        lblUserName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblUserName.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        lblPassword.setBounds(lblUserName.getX(), lblUserName.getY() + lblUserName.getHeight() + CustOpts.VER_GAP,
                lblUserName.getWidth(), CustOpts.BTN_HEIGHT);
        lblMakesure.setBounds(lblPassword.getX(), lblPassword.getY() + lblPassword.getHeight() + CustOpts.VER_GAP,
                lblPassword.getWidth(), CustOpts.BTN_HEIGHT);
        tfdUserName.setBounds(lblUserName.getX() + lblUserName.getWidth() + CustOpts.HOR_GAP, lblUserName.getY(),
                prmWidth - lblUserName.getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
        tfdPassword.setBounds(tfdUserName.getX(), lblPassword.getY(), tfdUserName.getWidth(), CustOpts.BTN_HEIGHT);
        tfdMakesure.setBounds(tfdPassword.getX(), lblMakesure.getY(), tfdPassword.getWidth(), CustOpts.BTN_HEIGHT);
    }

    JLabel lblUserName;
    JLabel lblPassword;
    JLabel lblMakesure;

    JTextField tfdUserName;
    JTextField tfdPassword;
    JTextField tfdMakesure;
}
