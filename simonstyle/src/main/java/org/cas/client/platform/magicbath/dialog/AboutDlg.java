package org.cas.client.platform.magicbath.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.CustViewConsts;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

public class AboutDlg extends JDialog implements ActionListener {
    private static AboutDlg instance = null;

    public static AboutDlg getInstance(
            JFrame pFrame) {
        if (instance == null)
            instance = new AboutDlg(pFrame);
        return instance;
    }

    private AboutDlg(JFrame pFrame) {
        super(pFrame);
        setTitle(DlgConst.DlgTitle);
        setBounds((CustOpts.SCRWIDTH - 430) / 2, (CustOpts.SCRHEIGHT - 330) / 2, 430, 330); // 对话框的默认尺寸。
        initComponents();
    }

    public void actionPerformed(
            ActionEvent e) {
        setVisible(false);
        instance = null;
        dispose();
    }

    private void initComponents() {
        // init--------------------
        lblImg = new JLabel(PIMPool.pool.getIcon(PaneConsts.IAMGE_PATH.concat("Logo.gif")));
        lblProductName = new JLabel(PaneConsts.TITLE);
        lblVersion = new JLabel(MagicbathDlgConst.Version);
        lblCopyRight = new JLabel(MagicbathDlgConst.CopyRight);
        lblGivento = new JLabel(MagicbathDlgConst.Givento);
        lblUserName = new JLabel(System.getProperty("user.name"));
        lblLicenceNumber = new JLabel(MagicbathDlgConst.LicenceNumber.concat(getSN()));
        separater = new PIMSeparator("");
        lblNotice1 = new JLabel(MagicbathDlgConst.Notice1);
        lblNotice2 = new JLabel(MagicbathDlgConst.Notice2);
        lblNotice3 = new JLabel(MagicbathDlgConst.Notice3);
        lblMail = new JLabel(MagicbathDlgConst.Mail);
        lblQQ = new JLabel(MagicbathDlgConst.QQ);
        ok = new JButton(CustViewConsts.OK);

        // propeties-------------
        getContentPane().setLayout(null);

        // layout--------------
        reLayout();

        // build up--------------
        getContentPane().add(lblImg);
        getContentPane().add(lblProductName);
        getContentPane().add(lblVersion);
        getContentPane().add(lblCopyRight);
        getContentPane().add(lblGivento);
        getContentPane().add(lblUserName);
        getContentPane().add(lblLicenceNumber);
        getContentPane().add(separater);
        getContentPane().add(lblNotice1);
        getContentPane().add(lblNotice2);
        getContentPane().add(lblNotice3);
        getContentPane().add(lblMail);
        getContentPane().add(lblQQ);
        getContentPane().add(ok);

        // listeners------------------
        ok.addActionListener(this);
    }

    private String getSN() {
        String tSN = null;
        String sql = "select VERSION from SYSTEMINFO where ID = 1";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.beforeFirst();
            while (rs.next()) {
                tSN = rs.getString("VERSION");
                break;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        return tSN;
    }

    private void reLayout() {
        lblImg.setBounds(CustOpts.HOR_GAP + 6, CustOpts.VER_GAP + 20, lblImg.getPreferredSize().width,
                lblImg.getPreferredSize().height);
        lblProductName.setBounds(CustOpts.HOR_GAP + 80, CustOpts.VER_GAP, lblProductName.getPreferredSize().width,
                CustOpts.LBL_HEIGHT);
        lblVersion.setBounds(lblProductName.getX(), lblProductName.getY() + lblProductName.getHeight(),
                lblVersion.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblCopyRight.setBounds(lblVersion.getX(), lblVersion.getY() + lblVersion.getHeight(),
                lblCopyRight.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblGivento.setBounds(lblCopyRight.getX(), lblCopyRight.getY() + lblCopyRight.getHeight() + CustOpts.LBL_HEIGHT,
                lblGivento.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblUserName.setBounds(lblGivento.getX(), lblGivento.getY() + lblGivento.getHeight(),
                lblUserName.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblLicenceNumber.setBounds(lblUserName.getX(), lblUserName.getY() + lblUserName.getHeight(),
                lblLicenceNumber.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        separater.setBounds(CustOpts.VER_GAP, lblLicenceNumber.getY() + lblLicenceNumber.getHeight(), getWidth()
                - CustOpts.HOR_GAP * 3, CustOpts.LBL_HEIGHT);
        lblNotice1.setBounds(CustOpts.HOR_GAP, lblLicenceNumber.getY() + lblLicenceNumber.getHeight()
                + CustOpts.LBL_HEIGHT, lblNotice1.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblNotice2.setBounds(lblNotice1.getX(), lblNotice1.getY() + lblNotice1.getHeight(),
                lblNotice2.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblNotice3.setBounds(lblNotice2.getX(), lblNotice2.getY() + lblNotice2.getHeight(),
                lblNotice3.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblMail.setBounds(lblNotice3.getX(), lblNotice3.getY() + lblNotice3.getHeight(),
                lblMail.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        lblQQ.setBounds(lblMail.getX(), lblMail.getY() + lblMail.getHeight(), lblQQ.getPreferredSize().width,
                CustOpts.LBL_HEIGHT);
        ok.setBounds(lblNotice3.getWidth() - CustOpts.BTN_WIDTH, lblQQ.getY() + CustOpts.LBL_HEIGHT,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
    }

    private JLabel lblImg;
    private JLabel lblProductName;
    private JLabel lblVersion;
    private JLabel lblCopyRight;
    private JLabel lblGivento;
    private JLabel lblUserName;
    private JLabel lblLicenceNumber;
    private PIMSeparator separater;
    private JLabel lblNotice1;
    private JLabel lblNotice2;
    private JLabel lblNotice3;
    private JLabel lblMail;
    private JLabel lblQQ;
    private JButton ok;
}
