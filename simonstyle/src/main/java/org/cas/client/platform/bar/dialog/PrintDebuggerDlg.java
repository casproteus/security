package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;

import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.resource.international.DlgConst;

public class PrintDebuggerDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener {
    public PrintDebuggerDlg(JFrame pParent) {
        super(pParent, false);
        initDialog();
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
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    public void componentMoved(
            ComponentEvent e) {
    };

    public void componentShown(
            ComponentEvent e) {
    };

    public void componentHidden(
            ComponentEvent e) {
    };

    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        // 初始化－－－－－－－－－－－－－－－－
        lblCommand = new JLabel(BarFrame.consts.PrintCommand);
        cmbCommand = new JComboBox();
        tfdPara1 = new JTextField();
        tfdPara2 = new JTextField();
        tfdPara3 = new JTextField();
        tfdPara4 = new JTextField();
        tfdPara5 = new JTextField();

        tblContent = new PIMTable();
        srpContent = new PIMScrollPane(tblContent);

        lblParameters = new JLabel(BarFrame.consts.parameters);
        btnAdd = new JButton(BarFrame.consts.Add);
        btnDelete = new JButton(DlgConst.DELETE);
        btnExcute = new JButton(BarFrame.consts.Test);
        btnClose = new JButton(DlgConst.FINISH_BUTTON);

        // properties
        setTitle(BarFrame.consts.PrintDebugger);
        setBounds((CustOpts.SCRWIDTH - 500) / 2, (CustOpts.SCRHEIGHT - 400) / 2, 500, 400); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        cmbCommand.setModel(new DefaultComboBoxModel(commandAry));
        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(20);
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        // border----------
        tblContent.setBorder(tfdPara3.getBorder());
        // forcus-------------
        tfdPara3.setFocusable(false);
        tfdPara4.setFocusable(false);
        tblContent.setFocusable(false);

        btnExcute.setMargin(new Insets(0, 0, 0, 0));
        btnClose.setMargin(btnExcute.getInsets());
        btnAdd.setMargin(btnExcute.getInsets());
        btnDelete.setMargin(btnExcute.getInsets());

        // 搭建－－－－－－－－－－－－－
        add(lblParameters);
        add(lblCommand);
        add(cmbCommand);
        add(tfdPara1);
        add(tfdPara2);
        add(tfdPara5);
        add(tfdPara3);
        add(tfdPara4);
        add(srpContent);

        add(btnAdd);
        add(btnDelete);
        add(btnExcute);
        add(btnClose);
        // 加监听器－－－－－－－－
        getContentPane().addComponentListener(this);
        cmbCommand.addActionListener(this);
        btnExcute.addActionListener(this);
        btnClose.addActionListener(this);
        btnAdd.addActionListener(this);
        btnDelete.addActionListener(this);
        cmbCommand.addKeyListener(this);
        tfdPara1.addKeyListener(this);
        tfdPara2.addKeyListener(this);
        tfdPara3.addKeyListener(this);
        tfdPara4.addKeyListener(this);
        tfdPara5.addKeyListener(this);
        // initContents--------------
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initTable();
                tfdPara1.grabFocus();
            }
        });
    }

    private char getCharPara(
            JTextField pParaFild) {
        String tSt = pParaFild.getText();
        if (tSt.length() == 0) {
            JOptionPane.showMessageDialog(this, BarFrame.consts.MsgMissPara);
            return '郝';
        } else
            return tSt.charAt(0);
    }

    private int getIntegerPara(
            JTextField pParaFild) {
        String tStr = pParaFild.getText();
        int n = 2;
        try {
            n = Integer.parseInt(tStr);
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(this, BarFrame.consts.MsgMissPara);
        }
        return Integer.valueOf(n);
    }

    // ActionListner----------------------------------

    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnExcute) {
            executeTest();
        } else if (o == btnClose)
            dispose();
        else if (o == btnAdd) {
            Vector tVec = new Vector();
            int tSeleIdx = cmbCommand.getSelectedIndex();
            tVec.add(cmbCommand.getSelectedItem());
            tVec.add(BarFrame.consts.commandTips[tSeleIdx]);
            switch (tSeleIdx) {
                case 0:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(64));
                    break;
                case 1:
                    tVec.add(new Integer(9));
                    tVec.add(new Integer(getCharPara(tfdPara1)));
                    break;
                case 2:
                    tVec.add(new Integer(13));
                    break;
                case 3:
                    tVec.add(new Integer(10));
                    break;
                case 4:
                    // tVec.add(new Integer(27));
                    // tVec.add(new Integer(74));
                    // tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(Integer.valueOf(getCharPara(tfdPara1)));
                    break;
                case 5:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(100));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 6:
                    tVec.add(new Integer(28));
                    tVec.add(new Integer(83));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    break;
                case 7:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(32));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 8:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(50));
                    break;
                case 9:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(51));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 10:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(33));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 11:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(37));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 12:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(38));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    tVec.add(getIntegerPara(tfdPara3));
                    tVec.add(getIntegerPara(tfdPara4));
                    tVec.add(getIntegerPara(tfdPara5));
                    break;
                case 13:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(63));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 14:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(45));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 15:
                    tVec.add(new Integer(28));
                    tVec.add(new Integer(45));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 16:
                    tVec.add(new Integer(29));
                    tVec.add(new Integer(76));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    break;
                case 17:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(36));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    break;
                case 18:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(68));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    tVec.add(getIntegerPara(tfdPara3));
                    tVec.add(getIntegerPara(tfdPara4));
                    tVec.add(getIntegerPara(tfdPara5));
                    tVec.add(Integer.valueOf(0));
                    break;
                case 19:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(92));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    break;
                case 20:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(97));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 21:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(69));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 22:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(71));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 23:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(99));
                    tVec.add(new Integer(53));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 24:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(123));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 25:
                    tVec.add(new Integer(29));
                    tVec.add(new Integer(86));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    break;
                case 26:
                    tVec.add(new Integer(29));
                    tVec.add(new Integer(87));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    break;
                case 27:
                    tVec.add(new Integer(28));
                    tVec.add(new Integer(33));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
                case 28:
                    tVec.add(new Integer(27));
                    tVec.add(new Integer(42));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    tVec.add(getIntegerPara(tfdPara3));
                    tVec.add(getIntegerPara(tfdPara4));
                    tVec.add(getIntegerPara(tfdPara5));
                    break;
                case 29:
                    tVec.add(new Integer(28));
                    tVec.add(new Integer(50));
                    tVec.add(getIntegerPara(tfdPara1));
                    tVec.add(getIntegerPara(tfdPara2));
                    tVec.add(getIntegerPara(tfdPara3));
                    tVec.add(getIntegerPara(tfdPara4));
                    tVec.add(getIntegerPara(tfdPara5));
                    break;
                case 30:
                    tVec.add(new Integer(28));
                    tVec.add(new Integer(87));
                    tVec.add(getIntegerPara(tfdPara1));
                    break;
            }
            addContentToList(tVec);
            resetDlgArea();
        } else if (o == btnDelete) {
            int tRow = tblContent.getSelectedRow();
            if (tRow < 0) { // 没有选中行的话，看看最后一行是第几行，选中它。
                tblContent.setSelectedRow(getUsedRowCount() - 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
            } else { // 有选中行的话，将选中行删除，应收金额相应减少
                for (int j = tRow; j < tblContent.getRowCount(); j++)
                    if (j == tblContent.getRowCount() - 1)
                        for (int i = 0; i < tblContent.getColumnCount(); i++)
                            tblContent.setValueAt(null, j, i);
                    else
                        for (int i = 0; i < tblContent.getColumnCount(); i++)
                            tblContent.setValueAt(tblContent.getValueAt(j + 1, i), j, i);
            }
        }
    }

    // keyListener
    public void keyTyped(
            KeyEvent e) {
    }

    public void keyPressed(
            KeyEvent e) {
        if (e.getKeyCode() == 112) {
            JDialog a = new JDialog(this);
            JTextPane tTextPane = new JTextPane();
            JScrollPane tScrollPane = new JScrollPane(tTextPane);
            a.getContentPane().add(tScrollPane);
            tTextPane.setText(BarFrame.consts.commandTips[cmbCommand.getSelectedIndex()]);
            a.setBounds(200, 100, 400, 300);
            a.setVisible(true);
        }
    }

    public void keyReleased(
            KeyEvent e) {
    }

    // 将对话盒区域的内容加入到列表
    private void addContentToList(
            Vector pList) {
        int tRowCount = tblContent.getRowCount(); // add content to the table.
        int tColCount = tblContent.getColumnCount();
        int tValidRowCount = getUsedRowCount(); // get the used RowCount
        if (tRowCount == tValidRowCount) { // no line is empty, add a new Line.
            Object[][] tValues = new Object[tRowCount + 1][tColCount];
            for (int r = 0; r < tRowCount; r++)
                for (int c = 0; c < tColCount; c++)
                    tValues[r][c] = tblContent.getValueAt(r, c);
            tblContent.setDataVector(tValues, header);
            resetColWidth();
        }
        for (int i = 0, len = pList.size(); i < len; i++) {
            tblContent.setValueAt(pList.get(i), tValidRowCount, i);
        }
    }

    void executeTest() {
        CommPortIdentifier tPortIdty;
        try {
            Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
            if (tPorts == null)
                JOptionPane.showMessageDialog(this, "no comm ports found!");
            else
                while (tPorts.hasMoreElements()) {
                    tPortIdty = (CommPortIdentifier) tPorts.nextElement();
                    if (tPortIdty.getName().equals("LPT1")) {
                        if (!tPortIdty.isCurrentlyOwned()) {
                            ParallelPort tParallelPort = (ParallelPort) tPortIdty.open("ParallelBlackBox", 2000);
                            DataOutputStream tOutStream = new DataOutputStream(tParallelPort.getOutputStream());
                            for (int i = 0, len = getUsedRowCount(); i < len; i++)
                                for (int j = 2, colCount = getUsedColumn(i); j < colCount; j++) {
                                    tOutStream.write(((Integer) tblContent.getValueAt(i, j)).intValue());
                                }
                            tOutStream.flush();
                            tOutStream.close();
                            tParallelPort.close();
                        }
                    }
                }
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getUsedColumn(
            int pRowID) {
        for (int i = 0; i < 1000; i++) {
            Object o = tblContent.getValueAt(pRowID, i);
            if (o == null)
                return i;
        }
        return -1;
    }

    private void resetColWidth() {
        for (int i = 0, len = header.length; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(60);
            tmpCol.setPreferredWidth(60);
        }
        validate();
        tblContent.revalidate();
        invalidate();
    }

    private void initTable() {
        Object[][] tValues = new Object[1][header.length];
        tblContent.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void reLayout() {
        lblCommand.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCommand.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        cmbCommand.setBounds(lblCommand.getX() + lblCommand.getWidth(), lblCommand.getY(),
                getWidth() - lblCommand.getX() - lblCommand.getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.SIZE_EDGE * 2
                        - CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnAdd.setBounds(cmbCommand.getX() + cmbCommand.getWidth() + CustOpts.HOR_GAP, cmbCommand.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnDelete.setBounds(btnAdd.getX(), btnAdd.getY() + btnAdd.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        lblParameters.setBounds(lblCommand.getX(), lblCommand.getY() + lblCommand.getHeight() + CustOpts.VER_GAP,
                lblParameters.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPara1.setBounds(lblParameters.getX() + lblParameters.getWidth(), lblParameters.getY(), cmbCommand.getWidth()
                / 5 - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        tfdPara2.setBounds(tfdPara1.getX() + tfdPara1.getWidth() + CustOpts.HOR_GAP, tfdPara1.getY(),
                tfdPara1.getWidth(), CustOpts.BTN_HEIGHT);
        tfdPara3.setBounds(tfdPara2.getX() + tfdPara2.getWidth() + CustOpts.HOR_GAP, tfdPara2.getY(),
                tfdPara2.getWidth(), CustOpts.BTN_HEIGHT);
        tfdPara4.setBounds(tfdPara3.getX() + tfdPara3.getWidth() + CustOpts.HOR_GAP, tfdPara3.getY(),
                tfdPara3.getWidth(), CustOpts.BTN_HEIGHT);
        tfdPara5.setBounds(tfdPara4.getX() + tfdPara4.getWidth() + CustOpts.HOR_GAP, tfdPara4.getY(),
                cmbCommand.getWidth() - tfdPara1.getWidth() * 4 - CustOpts.HOR_GAP * 4, CustOpts.BTN_HEIGHT);

        srpContent.setBounds(CustOpts.HOR_GAP, tfdPara5.getY() + tfdPara5.getHeight() + CustOpts.VER_GAP, getWidth()
                - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2,
                getHeight() - CustOpts.VER_GAP * 3 - CustOpts.BTN_HEIGHT - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE
                        - tfdPara5.getY() - tfdPara5.getHeight());
        btnExcute.setBounds(srpContent.getWidth() + srpContent.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH * 2,
                srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        btnClose.setBounds(btnExcute.getWidth() + btnExcute.getX() + CustOpts.HOR_GAP, btnExcute.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

        resetColWidth();
    }

    private int getUsedRowCount() {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++)
            if (tblContent.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblContent.getRowCount();
    }

    private void resetDlgArea() {
        cmbCommand.setSelectedIndex(-1);
        tfdPara1.setText("");
        tfdPara2.setText("");
        tfdPara3.setText("");
        tfdPara4.setText("");
        tfdPara5.setText("");
    }

    JLabel lblCommand;
    JLabel lblParameters;

    JComboBox cmbCommand;
    JTextField tfdPara1;
    JTextField tfdPara2;
    JTextField tfdPara3;
    JTextField tfdPara4;
    JTextField tfdPara5;

    PIMTable tblContent;
    PIMScrollPane srpContent;

    private JButton btnAdd;
    private JButton btnDelete;
    private JButton btnExcute;
    private JButton btnClose;

    private String[] commandAry = BarFrame.consts.CommandAry;

    private String[] header = new String[] { BarFrame.consts.PrintCommand, BarFrame.consts.Note, "A", "B", "C", "D", "E", "F",
            "G", "H", "I", "J" };
}
