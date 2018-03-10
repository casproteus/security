package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.cascustomize.CustOptsConsts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

public class ChangeRateDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, FocusListener {
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public ChangeRateDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    public void focusGained(
            FocusEvent e) {
    }

    public void focusLost(
            FocusEvent e) {
        Object o = e.getSource();
        if (o == tfdLeft0) {
            try {
                tfdRight0.setText(String.valueOf(1 / Float.parseFloat(tfdLeft0.getText())));
            } catch (Exception exp) {
                tfdLeft0.setText(String.valueOf(1 / Float.parseFloat(tfdRight0.getText())));
            }
        } else if (o == tfdLeft1) {
            try {
                tfdRight1.setText(String.valueOf(1 / Float.parseFloat(tfdLeft1.getText())));
            } catch (Exception exp) {
                tfdLeft1.setText(String.valueOf(1 / Float.parseFloat(tfdRight1.getText())));
            }
        } else if (o == tfdLeft2) {
            try {
                tfdRight2.setText(String.valueOf(1 / Float.parseFloat(tfdLeft2.getText())));
            } catch (Exception exp) {
                tfdLeft2.setText(String.valueOf(1 / Float.parseFloat(tfdRight2.getText())));
            }
        } else if (o == tfdLeft3) {
            try {
                tfdRight3.setText(String.valueOf(1 / Float.parseFloat(tfdLeft3.getText())));
            } catch (Exception exp) {
                tfdLeft3.setText(String.valueOf(1 / Float.parseFloat(tfdRight3.getText())));
            }
        } else if (o == tfdLeft4) {
            try {
                tfdRight4.setText(String.valueOf(1 / Float.parseFloat(tfdLeft4.getText())));
            } catch (Exception exp) {
                tfdLeft4.setText(String.valueOf(1 / Float.parseFloat(tfdRight4.getText())));
            }
        } else if (o == tfdLeft5) {
            try {
                tfdRight5.setText(String.valueOf(1 / Float.parseFloat(tfdLeft5.getText())));
            } catch (Exception exp) {
                tfdLeft5.setText(String.valueOf(1 / Float.parseFloat(tfdRight5.getText())));
            }
        } else if (o == tfdLeft6) {
            try {
                tfdRight6.setText(String.valueOf(1 / Float.parseFloat(tfdLeft6.getText())));
            } catch (Exception exp) {
                tfdLeft6.setText(String.valueOf(1 / Float.parseFloat(tfdRight6.getText())));
            }
        } else if (o == tfdLeft7) {
            try {
                tfdRight7.setText(String.valueOf(1 / Float.parseFloat(tfdLeft7.getText())));
            } catch (Exception exp) {
                tfdLeft7.setText(String.valueOf(1 / Float.parseFloat(tfdRight7.getText())));
            }
        } else if (o == tfdRight0) {
            try {
                tfdLeft0.setText(String.valueOf(1 / Float.parseFloat(tfdRight0.getText())));
            } catch (Exception exp) {
                tfdRight0.setText(String.valueOf(1 / Float.parseFloat(tfdLeft0.getText())));
            }
        } else if (o == tfdRight1) {
            try {
                tfdLeft1.setText(String.valueOf(1 / Float.parseFloat(tfdRight1.getText())));
            } catch (Exception exp) {
                tfdRight1.setText(String.valueOf(1 / Float.parseFloat(tfdLeft1.getText())));
            }
        } else if (o == tfdRight2) {
            try {
                tfdLeft2.setText(String.valueOf(1 / Float.parseFloat(tfdRight2.getText())));
            } catch (Exception exp) {
                tfdRight2.setText(String.valueOf(1 / Float.parseFloat(tfdLeft2.getText())));
            }
        } else if (o == tfdRight3) {
            try {
                tfdLeft3.setText(String.valueOf(1 / Float.parseFloat(tfdRight3.getText())));
            } catch (Exception exp) {
                tfdRight3.setText(String.valueOf(1 / Float.parseFloat(tfdLeft3.getText())));
            }
        } else if (o == tfdRight4) {
            try {
                tfdLeft4.setText(String.valueOf(1 / Float.parseFloat(tfdRight4.getText())));
            } catch (Exception exp) {
                tfdRight4.setText(String.valueOf(1 / Float.parseFloat(tfdLeft4.getText())));
            }
        } else if (o == tfdRight5) {
            try {
                tfdLeft5.setText(String.valueOf(1 / Float.parseFloat(tfdRight5.getText())));
            } catch (Exception exp) {
                tfdRight5.setText(String.valueOf(1 / Float.parseFloat(tfdLeft5.getText())));
            }
        } else if (o == tfdRight6) {
            try {
                tfdLeft6.setText(String.valueOf(1 / Float.parseFloat(tfdRight6.getText())));
            } catch (Exception exp) {
                tfdRight6.setText(String.valueOf(1 / Float.parseFloat(tfdLeft6.getText())));
            }
        } else if (o == tfdRight7) {
            try {
                tfdLeft7.setText(String.valueOf(1 / Float.parseFloat(tfdRight7.getText())));
            } catch (Exception exp) {
                tfdRight7.setText(String.valueOf(1 / Float.parseFloat(tfdLeft7.getText())));
            }
        }
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    public void reLayout() {
        // 最最左边的标签"1元="
        lblLeftUnit0.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblLeftUnit0.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        lblLeftUnit1.setBounds(CustOpts.HOR_GAP, lblLeftUnit0.getY() + lblLeftUnit0.getHeight() + CustOpts.VER_GAP,
                lblLeftUnit0.getWidth(), CustOpts.BTN_HEIGHT);
        lblLeftUnit2.setBounds(CustOpts.HOR_GAP, lblLeftUnit1.getY() + lblLeftUnit1.getHeight() + CustOpts.VER_GAP,
                lblLeftUnit1.getWidth(), CustOpts.BTN_HEIGHT);
        lblLeftUnit3.setBounds(CustOpts.HOR_GAP, lblLeftUnit2.getY() + lblLeftUnit2.getHeight() + CustOpts.VER_GAP,
                lblLeftUnit2.getWidth(), CustOpts.BTN_HEIGHT);
        lblLeftUnit4.setBounds(CustOpts.HOR_GAP, lblLeftUnit3.getY() + lblLeftUnit3.getHeight() + CustOpts.VER_GAP,
                lblLeftUnit3.getWidth(), CustOpts.BTN_HEIGHT);
        lblLeftUnit5.setBounds(CustOpts.HOR_GAP, lblLeftUnit4.getY() + lblLeftUnit4.getHeight() + CustOpts.VER_GAP,
                lblLeftUnit4.getWidth(), CustOpts.BTN_HEIGHT);
        lblLeftUnit6.setBounds(CustOpts.HOR_GAP, lblLeftUnit5.getY() + lblLeftUnit5.getHeight() + CustOpts.VER_GAP,
                lblLeftUnit5.getWidth(), CustOpts.BTN_HEIGHT);
        lblLeftUnit7.setBounds(CustOpts.HOR_GAP, lblLeftUnit6.getY() + lblLeftUnit6.getHeight() + CustOpts.VER_GAP,
                lblLeftUnit6.getWidth(), CustOpts.BTN_HEIGHT);
        // 左边的TextField
        tfdLeft0.setBounds(lblLeftUnit0.getX() + lblLeftUnit0.getWidth(), lblLeftUnit0.getY(), 100,
                lblLeftUnit0.getHeight());
        tfdLeft1.setBounds(tfdLeft0.getX(), lblLeftUnit1.getY(), 100, lblLeftUnit1.getHeight());
        tfdLeft2.setBounds(tfdLeft1.getX(), lblLeftUnit2.getY(), 100, lblLeftUnit2.getHeight());
        tfdLeft3.setBounds(tfdLeft2.getX(), lblLeftUnit3.getY(), 100, lblLeftUnit3.getHeight());
        tfdLeft4.setBounds(tfdLeft3.getX(), lblLeftUnit4.getY(), 100, lblLeftUnit4.getHeight());
        tfdLeft5.setBounds(tfdLeft4.getX(), lblLeftUnit5.getY(), 100, lblLeftUnit5.getHeight());
        tfdLeft6.setBounds(tfdLeft5.getX(), lblLeftUnit6.getY(), 100, lblLeftUnit6.getHeight());
        tfdLeft7.setBounds(tfdLeft6.getX(), lblLeftUnit7.getY(), 100, lblLeftUnit7.getHeight());
        // 左边的外币单位
        lblForeignLeft0.setBounds(tfdLeft0.getX() + tfdLeft0.getWidth(), tfdLeft0.getY(),
                lblForeignLeft0.getPreferredSize().width, tfdLeft0.getHeight());
        lblForeignLeft1.setBounds(tfdLeft1.getX() + tfdLeft1.getWidth(), tfdLeft1.getY(),
                lblForeignLeft1.getPreferredSize().width, tfdLeft1.getHeight());
        lblForeignLeft2.setBounds(tfdLeft2.getX() + tfdLeft2.getWidth(), tfdLeft2.getY(),
                lblForeignLeft2.getPreferredSize().width, tfdLeft2.getHeight());
        lblForeignLeft3.setBounds(tfdLeft3.getX() + tfdLeft3.getWidth(), tfdLeft3.getY(),
                lblForeignLeft3.getPreferredSize().width, tfdLeft3.getHeight());
        lblForeignLeft4.setBounds(tfdLeft4.getX() + tfdLeft4.getWidth(), tfdLeft4.getY(),
                lblForeignLeft4.getPreferredSize().width, tfdLeft4.getHeight());
        lblForeignLeft5.setBounds(tfdLeft5.getX() + tfdLeft5.getWidth(), tfdLeft5.getY(),
                lblForeignLeft5.getPreferredSize().width, tfdLeft5.getHeight());
        lblForeignLeft6.setBounds(tfdLeft6.getX() + tfdLeft6.getWidth(), tfdLeft6.getY(),
                lblForeignLeft6.getPreferredSize().width, tfdLeft6.getHeight());
        lblForeignLeft7.setBounds(tfdLeft7.getX() + tfdLeft7.getWidth(), tfdLeft7.getY(),
                lblForeignLeft7.getPreferredSize().width, tfdLeft7.getHeight());
        // 最最右边的人民币单位："元"
        lblRightUnit0.setBounds(getWidth() - CustOpts.HOR_GAP * 3 - lblRightUnit0.getPreferredSize().width,
                lblLeftUnit0.getY(), lblRightUnit0.getPreferredSize().width, lblLeftUnit0.getHeight());
        lblRightUnit1.setBounds(lblRightUnit0.getX(), lblLeftUnit1.getY(), lblRightUnit0.getWidth(),
                lblLeftUnit1.getHeight());
        lblRightUnit2.setBounds(lblRightUnit1.getX(), lblLeftUnit2.getY(), lblRightUnit1.getWidth(),
                lblLeftUnit2.getHeight());
        lblRightUnit3.setBounds(lblRightUnit2.getX(), lblLeftUnit3.getY(), lblRightUnit2.getWidth(),
                lblLeftUnit3.getHeight());
        lblRightUnit4.setBounds(lblRightUnit3.getX(), lblLeftUnit4.getY(), lblRightUnit3.getWidth(),
                lblLeftUnit4.getHeight());
        lblRightUnit5.setBounds(lblRightUnit4.getX(), lblLeftUnit5.getY(), lblRightUnit4.getWidth(),
                lblLeftUnit5.getHeight());
        lblRightUnit6.setBounds(lblRightUnit5.getX(), lblLeftUnit6.getY(), lblRightUnit5.getWidth(),
                lblLeftUnit6.getHeight());
        lblRightUnit7.setBounds(lblRightUnit6.getX(), lblLeftUnit7.getY(), lblRightUnit6.getWidth(),
                lblLeftUnit7.getHeight());
        // 右边的TextField
        tfdRight0.setBounds(lblRightUnit0.getX() - 100, lblRightUnit0.getY(), 100, lblRightUnit0.getHeight());
        tfdRight1.setBounds(lblRightUnit1.getX() - 100, lblRightUnit1.getY(), 100, lblRightUnit1.getHeight());
        tfdRight2.setBounds(lblRightUnit2.getX() - 100, lblRightUnit2.getY(), 100, lblRightUnit2.getHeight());
        tfdRight3.setBounds(lblRightUnit3.getX() - 100, lblRightUnit3.getY(), 100, lblRightUnit3.getHeight());
        tfdRight4.setBounds(lblRightUnit4.getX() - 100, lblRightUnit4.getY(), 100, lblRightUnit4.getHeight());
        tfdRight5.setBounds(lblRightUnit5.getX() - 100, lblRightUnit5.getY(), 100, lblRightUnit5.getHeight());
        tfdRight6.setBounds(lblRightUnit6.getX() - 100, lblRightUnit6.getY(), 100, lblRightUnit6.getHeight());
        tfdRight7.setBounds(lblRightUnit7.getX() - 100, lblRightUnit7.getY(), 100, lblRightUnit7.getHeight());
        // 右边的外币单位
        lblForeignRight0.setBounds(tfdRight0.getX() - lblForeignRight0.getPreferredSize().width, tfdRight0.getY(),
                lblForeignRight0.getPreferredSize().width, tfdRight0.getHeight());
        lblForeignRight1.setBounds(tfdRight1.getX() - lblForeignRight1.getPreferredSize().width, tfdRight1.getY(),
                lblForeignRight1.getPreferredSize().width, tfdRight1.getHeight());
        lblForeignRight2.setBounds(tfdRight2.getX() - lblForeignRight2.getPreferredSize().width, tfdRight2.getY(),
                lblForeignRight2.getPreferredSize().width, tfdRight2.getHeight());
        lblForeignRight3.setBounds(tfdRight3.getX() - lblForeignRight3.getPreferredSize().width, tfdRight3.getY(),
                lblForeignRight3.getPreferredSize().width, tfdRight3.getHeight());
        lblForeignRight4.setBounds(tfdRight4.getX() - lblForeignRight4.getPreferredSize().width, tfdRight4.getY(),
                lblForeignRight4.getPreferredSize().width, tfdRight4.getHeight());
        lblForeignRight5.setBounds(tfdRight5.getX() - lblForeignRight5.getPreferredSize().width, tfdRight5.getY(),
                lblForeignRight5.getPreferredSize().width, tfdRight5.getHeight());
        lblForeignRight6.setBounds(tfdRight6.getX() - lblForeignRight6.getPreferredSize().width, tfdRight6.getY(),
                lblForeignRight6.getPreferredSize().width, tfdRight6.getHeight());
        lblForeignRight7.setBounds(tfdRight7.getX() - lblForeignRight7.getPreferredSize().width, tfdRight7.getY(),
                lblForeignRight7.getPreferredSize().width, tfdRight7.getHeight());

        cancel.setBounds(getWidth() - CustOpts.HOR_GAP * 3 - CustOpts.BTN_WIDTH, getHeight() - CustOpts.SIZE_TITLE
                - CustOpts.SIZE_EDGE - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        ok.setBounds(cancel.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
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
        ok.removeActionListener(this);
        cancel.removeActionListener(this);
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
        Object o = e.getSource();
        if (o == ok) {// 更新新的汇率进入ini文件。
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[1], tfdLeft0.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[2], tfdLeft1.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[3], tfdLeft2.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[4], tfdLeft3.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[5], tfdLeft4.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[6], tfdLeft5.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[7], tfdLeft6.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.MoneyUnit[8], tfdLeft7.getText());
            dispose();
            setVisible(false);
        } else if (o == cancel) {
            dispose();
            setVisible(false);
        }
    }

    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarDlgConst.MRate);
        setResizable(false);
        setBounds((CustOpts.SCRWIDTH - 380) / 2, (CustOpts.SCRHEIGHT - 300) / 2, 380, 300); // 对话框的默认尺寸。

        // 初始化－－－－－－－－－－－－－－－－
        lblLeftUnit0 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));
        lblLeftUnit1 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));
        lblLeftUnit2 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));
        lblLeftUnit3 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));
        lblLeftUnit4 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));
        lblLeftUnit5 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));
        lblLeftUnit6 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));
        lblLeftUnit7 = new JLabel("1".concat(BarDlgConst.Unit).concat(" = "));

        lblRightUnit0 = new JLabel(BarDlgConst.Unit);
        lblRightUnit1 = new JLabel(BarDlgConst.Unit);
        lblRightUnit2 = new JLabel(BarDlgConst.Unit);
        lblRightUnit3 = new JLabel(BarDlgConst.Unit);
        lblRightUnit4 = new JLabel(BarDlgConst.Unit);
        lblRightUnit5 = new JLabel(BarDlgConst.Unit);
        lblRightUnit6 = new JLabel(BarDlgConst.Unit);
        lblRightUnit7 = new JLabel(BarDlgConst.Unit);

        lblForeignLeft0 = new JLabel(BarDlgConst.MoneyUnit[1]);
        lblForeignLeft1 = new JLabel(BarDlgConst.MoneyUnit[2]);
        lblForeignLeft2 = new JLabel(BarDlgConst.MoneyUnit[3]);
        lblForeignLeft3 = new JLabel(BarDlgConst.MoneyUnit[4]);
        lblForeignLeft4 = new JLabel(BarDlgConst.MoneyUnit[5]);
        lblForeignLeft5 = new JLabel(BarDlgConst.MoneyUnit[6]);
        lblForeignLeft6 = new JLabel(BarDlgConst.MoneyUnit[7]);
        lblForeignLeft7 = new JLabel(BarDlgConst.MoneyUnit[8]);

        lblForeignRight0 = new JLabel("1".concat(BarDlgConst.MoneyUnit[1]).concat(" = "));
        lblForeignRight1 = new JLabel("1".concat(BarDlgConst.MoneyUnit[2]).concat(" = "));
        lblForeignRight2 = new JLabel("1".concat(BarDlgConst.MoneyUnit[3]).concat(" = "));
        lblForeignRight3 = new JLabel("1".concat(BarDlgConst.MoneyUnit[4]).concat(" = "));
        lblForeignRight4 = new JLabel("1".concat(BarDlgConst.MoneyUnit[5]).concat(" = "));
        lblForeignRight5 = new JLabel("1".concat(BarDlgConst.MoneyUnit[6]).concat(" = "));
        lblForeignRight6 = new JLabel("1".concat(BarDlgConst.MoneyUnit[7]).concat(" = "));
        lblForeignRight7 = new JLabel("1".concat(BarDlgConst.MoneyUnit[8]).concat(" = "));

        tfdLeft0 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[1]));
        tfdLeft1 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[2]));
        tfdLeft2 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[3]));
        tfdLeft3 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[4]));
        tfdLeft4 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[5]));
        tfdLeft5 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[6]));
        tfdLeft6 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[7]));
        tfdLeft7 = new JTextField((String) CustOpts.custOps.getValue(BarDlgConst.MoneyUnit[8]));

        tfdRight0 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft0.getText())));
        tfdRight1 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft1.getText())));
        tfdRight2 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft2.getText())));
        tfdRight3 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft3.getText())));
        tfdRight4 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft4.getText())));
        tfdRight5 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft5.getText())));
        tfdRight6 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft6.getText())));
        tfdRight7 = new JTextField(String.valueOf(1 / Float.parseFloat(tfdLeft7.getText())));

        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // properties
        ok.setMnemonic('o');
        cancel.setMnemonic('c');

        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getMargin());
        getRootPane().setDefaultButton(ok);

        // 布局---------------
        getContentPane().setLayout(null);
        reLayout();

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(ok);
        getContentPane().add(cancel);
        getContentPane().add(lblLeftUnit0);
        getContentPane().add(lblLeftUnit1);
        getContentPane().add(lblLeftUnit2);
        getContentPane().add(lblLeftUnit3);
        getContentPane().add(lblLeftUnit4);
        getContentPane().add(lblLeftUnit5);
        getContentPane().add(lblLeftUnit6);
        getContentPane().add(lblLeftUnit7);
        getContentPane().add(lblRightUnit0);
        getContentPane().add(lblRightUnit1);
        getContentPane().add(lblRightUnit2);
        getContentPane().add(lblRightUnit3);
        getContentPane().add(lblRightUnit4);
        getContentPane().add(lblRightUnit5);
        getContentPane().add(lblRightUnit6);
        getContentPane().add(lblRightUnit7);
        getContentPane().add(lblForeignLeft0);
        getContentPane().add(lblForeignLeft1);
        getContentPane().add(lblForeignLeft2);
        getContentPane().add(lblForeignLeft3);
        getContentPane().add(lblForeignLeft4);
        getContentPane().add(lblForeignLeft5);
        getContentPane().add(lblForeignLeft6);
        getContentPane().add(lblForeignLeft7);
        getContentPane().add(lblForeignRight0);
        getContentPane().add(lblForeignRight1);
        getContentPane().add(lblForeignRight2);
        getContentPane().add(lblForeignRight3);
        getContentPane().add(lblForeignRight4);
        getContentPane().add(lblForeignRight5);
        getContentPane().add(lblForeignRight6);
        getContentPane().add(lblForeignRight7);
        getContentPane().add(tfdLeft0);
        getContentPane().add(tfdLeft1);
        getContentPane().add(tfdLeft2);
        getContentPane().add(tfdLeft3);
        getContentPane().add(tfdLeft4);
        getContentPane().add(tfdLeft5);
        getContentPane().add(tfdLeft6);
        getContentPane().add(tfdLeft7);
        getContentPane().add(tfdRight0);
        getContentPane().add(tfdRight1);
        getContentPane().add(tfdRight2);
        getContentPane().add(tfdRight3);
        getContentPane().add(tfdRight4);
        getContentPane().add(tfdRight5);
        getContentPane().add(tfdRight6);
        getContentPane().add(tfdRight7);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);
        tfdLeft0.addFocusListener(this);
        tfdLeft1.addFocusListener(this);
        tfdLeft2.addFocusListener(this);
        tfdLeft3.addFocusListener(this);
        tfdLeft4.addFocusListener(this);
        tfdLeft5.addFocusListener(this);
        tfdLeft6.addFocusListener(this);
        tfdLeft7.addFocusListener(this);
        tfdRight0.addFocusListener(this);
        tfdRight1.addFocusListener(this);
        tfdRight2.addFocusListener(this);
        tfdRight3.addFocusListener(this);
        tfdRight4.addFocusListener(this);
        tfdRight5.addFocusListener(this);
        tfdRight6.addFocusListener(this);
        tfdRight7.addFocusListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tfdLeft0.grabFocus();
            }
        });
    }

    private JLabel lblLeftUnit0;
    private JLabel lblLeftUnit1;
    private JLabel lblLeftUnit2;
    private JLabel lblLeftUnit3;
    private JLabel lblLeftUnit4;
    private JLabel lblLeftUnit5;
    private JLabel lblLeftUnit6;
    private JLabel lblLeftUnit7;

    private JLabel lblRightUnit0;
    private JLabel lblRightUnit1;
    private JLabel lblRightUnit2;
    private JLabel lblRightUnit3;
    private JLabel lblRightUnit4;
    private JLabel lblRightUnit5;
    private JLabel lblRightUnit6;
    private JLabel lblRightUnit7;

    private JLabel lblForeignLeft0;
    private JLabel lblForeignLeft1;
    private JLabel lblForeignLeft2;
    private JLabel lblForeignLeft3;
    private JLabel lblForeignLeft4;
    private JLabel lblForeignLeft5;
    private JLabel lblForeignLeft6;
    private JLabel lblForeignLeft7;

    private JLabel lblForeignRight0;
    private JLabel lblForeignRight1;
    private JLabel lblForeignRight2;
    private JLabel lblForeignRight3;
    private JLabel lblForeignRight4;
    private JLabel lblForeignRight5;
    private JLabel lblForeignRight6;
    private JLabel lblForeignRight7;

    private JTextField tfdLeft0;
    private JTextField tfdLeft1;
    private JTextField tfdLeft2;
    private JTextField tfdLeft3;
    private JTextField tfdLeft4;
    private JTextField tfdLeft5;
    private JTextField tfdLeft6;
    private JTextField tfdLeft7;

    private JTextField tfdRight0;
    private JTextField tfdRight1;
    private JTextField tfdRight2;
    private JTextField tfdRight3;
    private JTextField tfdRight4;
    private JTextField tfdRight5;
    private JTextField tfdRight6;
    private JTextField tfdRight7;

    private JButton ok;
    private JButton cancel;
}
