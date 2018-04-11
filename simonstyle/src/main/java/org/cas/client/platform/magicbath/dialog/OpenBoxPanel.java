package org.cas.client.platform.magicbath.dialog;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.magicbath.MagicbathTiedView;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;

public class OpenBoxPanel extends JTabbedPane implements ComponentListener, ActionListener {
    public OpenBoxPanel() {
        // init-----------
        general = new JPanel();
        lblSeleCate = new JLabel(MagicbathDlgConst.SeleCate);
        cbxBath = new JCheckBox(MagicbathDlgConst.BodyBath);
        cbxFoot = new JCheckBox(MagicbathDlgConst.FootBath);
        lblSeleSex = new JLabel(MagicbathDlgConst.SeleSex);
        cbxMale = new JCheckBox(MagicbathDlgConst.Male);
        cbxFemale = new JCheckBox(MagicbathDlgConst.Female);
        lblSeleNumber = new JLabel(MagicbathDlgConst.SeleBoxNumber);
        cmbBoxNumber = new JComboBox();
        ok = new JButton(MagicbathDlgConst.AddIn);

        // properties----------------
        general.setLayout(null);
        lblSeleCate.setOpaque(true);
        lblSeleNumber.setOpaque(true);
        lblSeleSex.setOpaque(true);
        lblSeleCate.setHorizontalAlignment(JLabel.CENTER);
        lblSeleNumber.setHorizontalAlignment(JLabel.CENTER);
        lblSeleSex.setHorizontalAlignment(JLabel.CENTER);
        lblSeleCate.setBackground(Color.LIGHT_GRAY);
        lblSeleNumber.setBackground(Color.LIGHT_GRAY);
        lblSeleSex.setBackground(Color.LIGHT_GRAY);
        lblSeleCate.setBorder(new LineBorder(Color.GRAY));
        lblSeleNumber.setBorder(new LineBorder(Color.GRAY));
        lblSeleSex.setBorder(new LineBorder(Color.GRAY));
        lblSeleNumber.setLabelFor(cmbBoxNumber);
        cbxBath.setOpaque(false);
        cbxFoot.setOpaque(false);
        cbxMale.setOpaque(false);
        cbxFemale.setOpaque(false);

        // relayout------------
        general.setPreferredSize(new Dimension(getWidth(), CustOpts.BTN_HEIGHT * 2 + CustOpts.VER_GAP * 3));
        relayout();

        // build----------------
        general.add(lblSeleCate);
        general.add(cbxBath);
        general.add(cbxFoot);
        general.add(lblSeleSex);
        general.add(cbxMale);
        general.add(cbxFemale);
        general.add(lblSeleNumber);
        general.add(cmbBoxNumber);
        general.add(ok);
        addTab(MagicbathDlgConst.OpenBox, general);

        // listener-------------
        general.addComponentListener(this);
        cbxBath.addActionListener(this);
        cbxFoot.addActionListener(this);
        cbxMale.addActionListener(this);
        cbxFemale.addActionListener(this);
        ok.addActionListener(this);

        // init contents-------------
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initBoxNumbers(null, null);
            }
        });
    }

    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        relayout();
    }

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(
            ComponentEvent e) {
    }

    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object tSource = e.getSource();
        if (tSource == ok) {
            String tmpBoxNumber = (String) cmbBoxNumber.getSelectedItem();
            if (tmpBoxNumber == null || tmpBoxNumber.length() == 0) {
                JOptionPane.showMessageDialog(this, MagicbathDlgConst.MsgInputBoxNumber);
                return;
            }
            if (!cbxBath.isSelected() && !cbxFoot.isSelected()) {
                JOptionPane.showMessageDialog(this, MagicbathDlgConst.MsgSelectCate);
                return;
            }
            // if(!cbxMale.isSelected() && !cbxFemale.isSelected()){
            // JOptionPane.showMessageDialog(this, BathConst.MsgSelectSex);
            // return;
            // }
            openBox(tmpBoxNumber); // to modify the time filed of the correspond record.
            // first init the comboBoxes.
            initBoxNumbers(null, null);
            MagicbathGeneralPanel.pAddService.initBoxNumbers();
            MagicbathGeneralPanel.pCalculate.initBoxNumbers();
            // second add service record.
            if (cbxBath.isSelected())
                MagicbathGeneralPanel.pAddService.addService("", "桑拿", tmpBoxNumber, 1);
            // last reset the states of the CheckBoxes.
            cbxBath.setSelected(false); // to reset the generalPane.
            cbxFoot.setSelected(false);
            cbxMale.setSelected(false);
            cbxFemale.setSelected(false);
        } else if (tSource == cbxBath) {
            cbxFoot.setSelected(false);
            initBoxNumbers(cbxBath.isSelected() ? MagicbathDlgConst.BodyBath : null, null);
            // cbxMale.isSelected() ? BathConst.Male : (cbxFemale.isSelected() ? BathConst.Female : null));
        } else if (tSource == cbxFoot) {
            cbxBath.setSelected(false);
            initBoxNumbers(cbxFoot.isSelected() ? MagicbathDlgConst.FootBath : null, null);
            // cbxMale.isSelected() ? BathConst.Male : (cbxFemale.isSelected() ? BathConst.Female : null));
        } else if (tSource == cbxMale) {
            cbxFemale.setSelected(false);
            // initBoxNumbers(cbxBath.isSelected() ? BathConst.BodyBath : (cbxFoot.isSelected() ? BathConst.FootBath :
            // null),
            // cbxMale.isSelected() ? BathConst.Male : null);
        } else if (tSource == cbxFemale) {
            cbxMale.setSelected(false);
            // initBoxNumbers(cbxBath.isSelected() ? BathConst.BodyBath : (cbxFoot.isSelected() ? BathConst.FootBath :
            // null),
            // cbxFemale.isSelected() ? BathConst.Female : null);
        }
    }

    public void openBox(
            String pBoxNumber) {
        for (int i = 0; i < subjectAry.length; i++)
            if (subjectAry[i].equals(pBoxNumber)) {
                PIMRecord tmpRec =
                        CASControl.ctrl.getModel().selectRecord(CustOpts.custOps.APPNameVec.indexOf("Contact"),
                                idAry[i], 100); // to select a record from DB.
                Object tObj = Calendar.getInstance().getTime();
                tmpRec.setFieldValue(ContactDefaultViews.ANNIVERSARY, tObj); // modify the Time info of the record.
                CASControl.ctrl.getModel().updateRecord(tmpRec, true); // save the record back to DB.
                MagicbathTiedView.getInstance().resetState(pBoxNumber, tObj);
                break;
            }
    }

    public void initBoxNumbers(
            String pCate,
            String pSex) {
        String sql = "select ID, SUBJECT from contact where ";
        if (pCate != null) {
            sql = sql.concat("category like '").concat(pCate).concat("' ");
        }
        if (pSex != null) {
            if (pCate != null)
                sql = sql.concat("and ");
            sql = sql.concat("Nname like '").concat(pSex).concat("' ");
        }
        if (pCate != null || pSex != null)
            sql = sql.concat("and ");
        sql = sql.concat("anniversary is null and DELETED != true");
        try {
            ResultSet rs =
                    PIMDBModel.getConection()
                            .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                            .executeQuery(sql);
            ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            idAry = new int[tmpPos];
            subjectAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                idAry[tmpPos] = rs.getInt("ID");
                subjectAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        cmbBoxNumber.setModel(new DefaultComboBoxModel(subjectAry));
        cmbBoxNumber.setSelectedIndex(-1);
    }

    private void relayout() {
        int tSwitchWidth = 520;
        lblSeleCate.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cbxBath.setBounds(lblSeleCate.getX() + lblSeleCate.getWidth() + CustOpts.HOR_GAP, lblSeleCate.getY(),
                cbxBath.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxFoot.setBounds(cbxBath.getX() + cbxBath.getWidth(), cbxBath.getY(), cbxFoot.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        lblSeleSex.setBounds(cbxFoot.getX() + cbxFoot.getWidth() + CustOpts.HOR_GAP * 2, cbxFoot.getY(),
                getWidth() > tSwitchWidth ? CustOpts.BTN_WIDTH : 0, CustOpts.BTN_HEIGHT);
        cbxMale.setBounds(lblSeleSex.getX() + lblSeleSex.getWidth() + CustOpts.HOR_GAP, lblSeleSex.getY(),
                getWidth() > tSwitchWidth ? cbxMale.getPreferredSize().width : 0, CustOpts.BTN_HEIGHT);
        cbxFemale.setBounds(cbxMale.getX() + cbxMale.getWidth(), cbxMale.getY(),
                getWidth() > tSwitchWidth ? cbxFemale.getPreferredSize().width : 0, CustOpts.BTN_HEIGHT);
        lblSeleNumber.setBounds(getWidth() > tSwitchWidth ? cbxFemale.getX() + cbxFemale.getWidth() + CustOpts.HOR_GAP
                : cbxFoot.getX() + cbxFoot.getWidth() + CustOpts.HOR_GAP, cbxFemale.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        cmbBoxNumber.setBounds(lblSeleNumber.getX() + lblSeleNumber.getWidth() + CustOpts.HOR_GAP,
                lblSeleNumber.getY(),
                getWidth() - 2 * CustOpts.HOR_GAP - lblSeleNumber.getX() - lblSeleNumber.getWidth(),
                CustOpts.BTN_HEIGHT);
        ok.setBounds(getWidth() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, lblSeleCate.getY() + lblSeleCate.getHeight()
                + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
    }

    private JPanel general;
    private JLabel lblSeleCate;
    private JCheckBox cbxBath;
    private JCheckBox cbxFoot;
    private JLabel lblSeleSex;
    private JCheckBox cbxMale;
    private JCheckBox cbxFemale;
    private JLabel lblSeleNumber;
    private JComboBox cmbBoxNumber;
    private JButton ok;

    private int[] idAry;
    private String[] subjectAry;
}
