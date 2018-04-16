package org.cas.client.platform.bar.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pos.dialog.modifyuser.MUserSwichDlg;
import org.cas.client.platform.pos.dialog.modifyuser.ModifyPasswordDlg;
import org.cas.client.platform.product.ProductDefaultViews;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;
import org.cas.client.resource.international.PaneConsts;

import com.jpos.POStest.POStestGUI;

/**
 * 微软的TextField的长度限制是228（约）， 本类的限制因为没有规格约束，暂定为912（约）
 */
public class DishDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
	private int index;
	public void setIndex(int index) {
		this.index = index;
	}
	
    public DishDlg(JFrame pFrame) {
        super(pFrame, false);
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
    	//name------------
        sptName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
        		getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2, CustOpts.SEP_HEIGHT + 2);
        lblLanguage1.setBounds(CustOpts.HOR_GAP * 2, sptName.getY() + sptName.getHeight() + CustOpts.VER_GAP, lblLanguage1.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdLanguage1.setBounds(lblLanguage1.getX() + lblLanguage1.getWidth() + CustOpts.HOR_GAP,lblLanguage1.getY(),
                (getWidth() - lblLanguage1.getX() * 2)/3 - CustOpts.HOR_GAP*2 - lblLanguage1.getWidth(), CustOpts.BTN_HEIGHT);
        lblLanguage2.setBounds(tfdLanguage1.getX() + tfdLanguage1.getWidth() + CustOpts.HOR_GAP,
                lblLanguage1.getY(), lblLanguage2.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLanguage2.setBounds(lblLanguage2.getX() + lblLanguage2.getWidth() + CustOpts.HOR_GAP, lblLanguage2.getY(), tfdLanguage1.getWidth(), CustOpts.BTN_HEIGHT);
        
        lblLanguage3.setBounds(tfdLanguage2.getX() + tfdLanguage2.getWidth() + CustOpts.HOR_GAP,
                lblLanguage2.getY(), lblLanguage3.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLanguage3.setBounds(lblLanguage3.getX() + lblLanguage3.getWidth() + CustOpts.HOR_GAP, lblLanguage3.getY(), tfdLanguage2.getWidth() + CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        //price---------
        sptPrice.setBounds(CustOpts.HOR_GAP, lblLanguage1.getY() + lblLanguage1.getHeight()+ CustOpts.VER_GAP,
        		getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2, CustOpts.SEP_HEIGHT + 2);
        lblPrice.setBounds(lblLanguage1.getX(), sptPrice.getY() + sptPrice.getHeight() + CustOpts.VER_GAP,
        		lblPrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPrice.setBounds(lblPrice.getX() + lblPrice.getWidth() + CustOpts.HOR_GAP, lblPrice.getY(),
        		lblLanguage1.getWidth() + tfdLanguage1.getWidth() - lblPrice.getWidth(), CustOpts.BTN_HEIGHT);
        checQST.setBounds(tfdLanguage2.getX(), lblPrice.getY(), checQST.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        cbxGST.setBounds(lblLanguage3.getX(), lblPrice.getY(), cbxGST.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        //size--------
        sptSize.setBounds(sptPrice.getX(), cbxGST.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sptPrice.getWidth(), CustOpts.SEP_HEIGHT + 2);
        cbxSize1.setBounds(lblPrice.getX(), sptSize.getY() + sptSize.getHeight() + CustOpts.VER_GAP,
        		(getWidth() - CustOpts.HOR_GAP * 2)/6 - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        cbxSize2.setBounds(cbxSize1.getX() + cbxSize1.getWidth() + CustOpts.HOR_GAP, cbxSize1.getY(), cbxSize1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxSize3.setBounds(cbxSize2.getX() + cbxSize1.getWidth() + CustOpts.HOR_GAP, cbxSize1.getY(), cbxSize1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxSize4.setBounds(cbxSize3.getX() + cbxSize1.getWidth() + CustOpts.HOR_GAP, cbxSize1.getY(), cbxSize1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxSize5.setBounds(cbxSize4.getX() + cbxSize1.getWidth() + CustOpts.HOR_GAP, cbxSize1.getY(), cbxSize1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxSize6.setBounds(cbxSize5.getX() + cbxSize1.getWidth() + CustOpts.HOR_GAP, cbxSize1.getY(), cbxSize1.getWidth(), CustOpts.BTN_HEIGHT);
        //printers--------
        sptPrinter.setBounds(sptSize.getX(), cbxSize6.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sptPrice.getWidth(), CustOpts.SEP_HEIGHT + 2);
        cbxPrinter1.setBounds(lblPrice.getX(), sptPrinter.getY() + sptPrinter.getHeight() + CustOpts.VER_GAP,
        		(getWidth() - CustOpts.HOR_GAP * 2)/6 - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        cbxPrinter2.setBounds(cbxPrinter1.getX() + cbxPrinter1.getWidth() + CustOpts.HOR_GAP, cbxPrinter1.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter3.setBounds(cbxPrinter2.getX() + cbxPrinter1.getWidth() + CustOpts.HOR_GAP, cbxPrinter1.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter4.setBounds(cbxPrinter3.getX() + cbxPrinter1.getWidth() + CustOpts.HOR_GAP, cbxPrinter1.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter5.setBounds(cbxPrinter4.getX() + cbxPrinter1.getWidth() + CustOpts.HOR_GAP, cbxPrinter1.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter6.setBounds(cbxPrinter5.getX() + cbxPrinter1.getWidth() + CustOpts.HOR_GAP, cbxPrinter1.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);

        sptOther.setBounds(sptPrinter.getX(), cbxPrinter6.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sptPrinter.getWidth(), CustOpts.SEP_HEIGHT + 2);
        cmbCategory.setBounds(cbxPrinter1.getX(), sptOther.getY() + sptOther.getHeight() + CustOpts.VER_GAP,
                lblLanguage1.getWidth() + tfdLanguage1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPricePomp.setBounds(cmbCategory.getX(), cmbCategory.getY(), lblLanguage3.getWidth() + tfdLanguage3.getWidth(),
                CustOpts.BTN_HEIGHT);
        cbxMenuPomp.setBounds(cbxPricePomp.getX(), cbxPricePomp.getY() + cbxPricePomp.getHeight() + CustOpts.VER_GAP,
        		cbxPricePomp.getWidth(), CustOpts.BTN_HEIGHT);

        ok.setBounds(getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.SIZE_EDGE - CustOpts.BTN_WIDTH * 2,
        		cbxMenuPomp.getY() + cbxMenuPomp.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getWidth() + ok.getX() + CustOpts.HOR_GAP, ok.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
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
        cbxMenuPomp.removeActionListener(this);
        cmbCategory.removeActionListener(this);
        ok.removeActionListener(this);
        cancel.removeActionListener(this);

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
        Object o = e.getSource();
        if (o == cbxMenuPomp) {
            CASControl.ctrl.showMainFrame(); // it will show login dialog first.
        } else if (o == this.cmbCategory) {
            for (int i = 0; i < 100000000; i++) {
                String tStr = String.valueOf(480 / 100.0);
                if (tStr.equals("4.79")) {
                    ErrorUtil.write(String.valueOf(480 / 100.0));
                }
            }
            POStestGUI gui = new POStestGUI();
            JFrame frame = new JFrame("POStest");
            frame.getContentPane().add(gui, BorderLayout.CENTER);
            frame.setSize(700, 500);
            frame.setVisible(true);
        } else if (o == ok) {
            int tSize = 12;
            try {
                tSize = Integer.parseInt(tfdLanguage2.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdLanguage2.grabFocus();
                tfdLanguage2.selectAll();
                return;
            }
            CustOpts.custOps.setKeyAndValue(BarDlgConst.EncodeStyle, tfdLanguage1.getText());

            CustOpts.custOps.setKeyAndValue(BarDlgConst.UniCommand,
                    checQST.isSelected() ? "true" : tfdPrice.getText());
            CustOpts.custOps.setKeyAndValue(BarDlgConst.UseMoenyBox, cbxGST.isSelected() ? "true" : "false");
            CustOpts.custOps.setKeyAndValue(BarDlgConst.OneKeyOpen, cbxPricePomp.isSelected() ? "true" : "false");
            CustOpts.custOps.setFontSize(tSize);
            dispose();
        } else if (o == checQST) {
            tfdPrice.setEnabled(!checQST.isSelected());
        } else if (o == cbxGST) {
            // 开个线程检查是否有连接。
        } else if (o == cbxPricePomp) {
            // 开个线程检查是否有连接。
        } else if (o == cbxPrinter1) {
            // 开个线程检查是否有连接。
        }
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(DlgConst.ProductInfo);
        setResizable(false);
        // 初始化－－－－－－－－－－－－－－－－
        sptName = new PIMSeparator(BarDlgConst.Name);
        lblLanguage1 = new JLabel(DlgConst.Language1);
        tfdLanguage1 = new JTextField();
        lblLanguage2 = new JLabel(DlgConst.Language2);
        tfdLanguage2 = new JTextField();
        lblLanguage3 = new JLabel(DlgConst.Language3);
        tfdLanguage3 = new JTextField();

        sptPrice = new PIMSeparator(BarDlgConst.PRICE);
        lblPrice = new JLabel(BarDlgConst.PRICE);
        tfdPrice = new JTextField();
        cbxGST = new JCheckBox(BarDlgConst.GST);
        checQST = new JCheckBox(BarDlgConst.QST);

        sptSize = new PIMSeparator(BarDlgConst.Size);
        cbxSize1 = new JCheckBox(BarDlgConst.Size1);
        cbxSize2 = new JCheckBox(BarDlgConst.Size2);
        cbxSize3 = new JCheckBox(BarDlgConst.Size3);
        cbxSize4 = new JCheckBox(BarDlgConst.Size4);
        cbxSize5 = new JCheckBox(BarDlgConst.Size5);
        cbxSize6 = new JCheckBox(BarDlgConst.Size6);
        
        sptPrinter = new PIMSeparator(BarDlgConst.PRINTER);
        cbxPrinter1 = new JCheckBox(BarDlgConst.Printer1);
        cbxPrinter2 = new JCheckBox(BarDlgConst.Printer2);
        cbxPrinter3 = new JCheckBox(BarDlgConst.Printer3);
        cbxPrinter4 = new JCheckBox(BarDlgConst.Printer4);
        cbxPrinter5 = new JCheckBox(BarDlgConst.Printer5);
        cbxPrinter6 = new JCheckBox(BarDlgConst.Printer6);
        
        
        sptOther = new PIMSeparator(OptionDlgConst.OPTION_OTHER);
        cbxPricePomp = new JCheckBox(BarDlgConst.PricePomp);
        cbxMenuPomp = new JCheckBox(BarDlgConst.MenuPomp);
        cmbCategory = new JButton(BarDlgConst.DspSuperTool);

        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－
        ok.setMnemonic('o');
        cbxMenuPomp.setMargin(new Insets(0, 0, 0, 0));
        cmbCategory.setMargin(cbxMenuPomp.getMargin());
        ok.setMargin(cbxMenuPomp.getMargin());

        setBounds((CustOpts.SCRWIDTH - 280) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 680, 320); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        getRootPane().setDefaultButton(ok);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(sptName);
        getContentPane().add(lblLanguage1);
        getContentPane().add(tfdLanguage1);
        getContentPane().add(lblLanguage2);
        getContentPane().add(tfdLanguage2);
        getContentPane().add(lblLanguage3);
        getContentPane().add(tfdLanguage3);
        
        getContentPane().add(sptPrice);
        getContentPane().add(lblPrice);
        getContentPane().add(tfdPrice);
        getContentPane().add(checQST);
        getContentPane().add(cbxGST);
        
        getContentPane().add(sptSize);
        getContentPane().add(cbxSize1);
        getContentPane().add(cbxSize2);
        getContentPane().add(cbxSize3);
        getContentPane().add(cbxSize4);
        getContentPane().add(cbxSize5);
        getContentPane().add(cbxSize6);
        
        getContentPane().add(sptPrinter);
        getContentPane().add(cbxPrinter1);
        getContentPane().add(cbxPrinter2);
        getContentPane().add(cbxPrinter3);
        getContentPane().add(cbxPrinter4);
        getContentPane().add(cbxPrinter5);
        getContentPane().add(cbxPrinter6);

        getContentPane().add(sptOther);
        getContentPane().add(cmbCategory);
        getContentPane().add(cbxPricePomp);
        getContentPane().add(cbxMenuPomp);

        getContentPane().add(cancel);
        getContentPane().add(ok);

        // 加监听器－－－－－－－－
        cbxMenuPomp.addActionListener(this);
        cmbCategory.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        checQST.addActionListener(this);
        cbxGST.addActionListener(this);
        cbxPricePomp.addActionListener(this);
        getContentPane().addComponentListener(this);

        // Content
        Object tIsUniOpenCmd = CustOpts.custOps.getValue(BarDlgConst.UniCommand);
        checQST.setSelected(tIsUniOpenCmd == null || tIsUniOpenCmd.equals("true"));
        if (!checQST.isSelected())
            tfdPrice.setText(tIsUniOpenCmd.toString());
        
        Object tUseMoneyBox = CustOpts.custOps.getValue(BarDlgConst.UseMoenyBox);
        cbxGST.setSelected(tUseMoneyBox == null || tUseMoneyBox.equals("true"));
        Object tOneKeyOpenBox = CustOpts.custOps.getValue(BarDlgConst.OneKeyOpen);
        cbxPricePomp.setSelected(tOneKeyOpenBox == null || tOneKeyOpenBox.equals("true"));
        Object tUsePrinter = CustOpts.custOps.getValue(BarDlgConst.UsePrinter);
    }
    
    private PIMSeparator sptName;
    private JLabel lblLanguage1;
    private JTextField tfdLanguage1;
    private JLabel lblLanguage2;
    private JTextField tfdLanguage2;
    private JLabel lblLanguage3;
    private JTextField tfdLanguage3;

    private PIMSeparator sptPrice;
    private JLabel lblPrice;
    private JTextField tfdPrice;
    private JCheckBox cbxGST;
    private JCheckBox checQST;

    private PIMSeparator sptSize;
    private JCheckBox cbxSize1;
    private JCheckBox cbxSize2;
    private JCheckBox cbxSize3;
    private JCheckBox cbxSize4;
    private JCheckBox cbxSize5;
    private JCheckBox cbxSize6;
    
    private PIMSeparator sptPrinter;
    private JCheckBox cbxPrinter1;
    private JCheckBox cbxPrinter2;
    private JCheckBox cbxPrinter3;
    private JCheckBox cbxPrinter4;
    private JCheckBox cbxPrinter5;
    private JCheckBox cbxPrinter6;

    private PIMSeparator sptOther;
    private JCheckBox cbxPricePomp;
    private JCheckBox cbxMenuPomp;
    private JButton cmbCategory;
    
    private JButton ok;
    private JButton cancel;
}
