package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlgConst;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;

//@NOTE：如果是售货时候，发现某个商品库中没有，而且这个商品又没有条形码，该怎么办呢？－－－解决方案是条码框中敲入一个专用(表示无码商品)的条码
//然后敲回车，这样，就会弹出本对话盒，从而操作员可以添加该商品了。
//@NOTE:本类初始化时会检查，如果传入的条形码与扫描仪输入的条码位数不符合，则会自动忽略传入的条码－－反正这种条码输入数据库中也没有用，因为扫描仪
//永远不会遇到一个与之匹配的项的。
public class MerchandiseDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, FocusListener,
        KeyListener {
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public MerchandiseDlg(JFrame pParent, String pProdNumber) {
        super(pParent, true);
        setTitle(BarDlgConst.AddProd);
        initDialog(pProdNumber);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tfdPrice.grabFocus();
            }
        });
    }

    public MerchandiseDlg(JFrame pParent, int pID, String pCode, int pPrice, String pMnemonic, String pSubject,
            int pStore, String pUnit, String pCategory, int pCost, String pContent) {
        super(pParent, true);
        setTitle(BarDlgConst.ModifyMerchanInfo);
        initDialog(pCode);
        prodID = pID;
        tfdPrice.setText(String.valueOf(pPrice / 100.00));
        tfdPinyin.setText(pMnemonic);
        tfdProdName.setText(pSubject);
        tfdStorCount.setText(String.valueOf(pStore));
        tfdPackage.setText(pUnit);
        tfdType.setText(pCategory);
        tfdCost.setText(String.valueOf(pCost / 100.00));
        tfdRemark.setText(pContent);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                tfdProdName.grabFocus();
            }
        });
    }

    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    public void focusLost(
            FocusEvent e) {
    }

    public void keyTyped(
            KeyEvent e) {
    }

    public void keyPressed(
            KeyEvent e) {
    }

    public void keyReleased(
            KeyEvent e) {
        Object o = e.getSource();
        if (o == tfdPrice) {
            String tValue = (String) tfdPrice.getText();
            int tDotPosition = tValue.indexOf('.');
            if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3) {// 位数满了，自动结算
                tfdPinyin.grabFocus();
                tfdPinyin.selectAll();
            }
        } else if (o == tfdCost) {
            String tValue = (String) tfdCost.getText();
            int tDotPosition = tValue.indexOf('.');
            if (tDotPosition >= 0 && tValue.length() - tDotPosition >= 3) {// 位数满了，自动结算
                tfdRemark.grabFocus();
            }
        }
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    public void reLayout() {
        int tWidth = 260;
        lblProdCode.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblProdCode.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdProdCode.setBounds(lblProdCode.getX() + lblProdCode.getWidth() + CustOpts.HOR_GAP, lblProdCode.getY(),
                tWidth - lblProdCode.getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP * 5, CustOpts.BTN_HEIGHT);
        ok.setBounds(tfdProdCode.getX() + tfdProdCode.getWidth() + CustOpts.HOR_GAP, lblProdCode.getY(),
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭

        lblPrice.setBounds(lblProdCode.getX(), lblProdCode.getY() + lblProdCode.getHeight() + CustOpts.VER_GAP,
                lblProdCode.getWidth(), CustOpts.BTN_HEIGHT);
        tfdPrice.setBounds(tfdProdCode.getX(), lblPrice.getY(), tfdProdCode.getWidth()
                - lblUnit.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblUnit.setBounds(tfdPrice.getX() + tfdPrice.getWidth(), lblPrice.getY(), lblUnit.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getX(), lblPrice.getY(), ok.getWidth(), CustOpts.BTN_HEIGHT);

        lblPinyin.setBounds(lblPrice.getX(), lblPrice.getY() + lblPrice.getHeight() + CustOpts.VER_GAP,
                lblPrice.getWidth(), CustOpts.BTN_HEIGHT);
        tfdPinyin.setBounds(tfdPrice.getX(), lblPinyin.getY(), tfdPrice.getWidth() + lblUnit.getWidth(),
                CustOpts.BTN_HEIGHT);
        lblProdName.setBounds(lblPinyin.getX(), lblPinyin.getY() + lblPinyin.getHeight() + CustOpts.VER_GAP,
                lblPinyin.getWidth(), CustOpts.BTN_HEIGHT);
        tfdProdName.setBounds(tfdPinyin.getX(), lblProdName.getY(), tfdPinyin.getWidth(), CustOpts.BTN_HEIGHT);
        lblStorCount.setBounds(lblProdName.getX(), lblProdName.getY() + lblProdName.getHeight() + CustOpts.VER_GAP,
                lblProdName.getWidth(), CustOpts.BTN_HEIGHT);
        tfdStorCount.setBounds(tfdProdName.getX(), lblStorCount.getY(), tfdProdName.getWidth(), CustOpts.BTN_HEIGHT);
        lblPackage.setBounds(lblStorCount.getX(), lblStorCount.getY() + lblStorCount.getHeight() + CustOpts.VER_GAP,
                lblStorCount.getWidth(), CustOpts.BTN_HEIGHT);
        tfdPackage.setBounds(tfdStorCount.getX(), lblPackage.getY(), tfdStorCount.getWidth(), CustOpts.BTN_HEIGHT);
        lblType.setBounds(lblPackage.getX(), lblPackage.getY() + lblPackage.getHeight() + CustOpts.VER_GAP,
                lblPackage.getWidth(), CustOpts.BTN_HEIGHT);
        tfdType.setBounds(tfdPackage.getX(), lblType.getY(), tfdPackage.getWidth(), CustOpts.BTN_HEIGHT);
        lblCost.setBounds(lblType.getX(), lblType.getY() + lblType.getHeight() + CustOpts.VER_GAP, lblType.getWidth(),
                CustOpts.BTN_HEIGHT);
        tfdCost.setBounds(tfdType.getX(), lblCost.getY(), tfdType.getWidth() - lblUnit2.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        lblUnit2.setBounds(tfdCost.getX() + tfdCost.getWidth(), tfdCost.getY(), lblUnit2.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        lblRemark.setBounds(lblCost.getX(), lblCost.getY() + lblCost.getHeight() + CustOpts.VER_GAP,
                lblCost.getWidth(), CustOpts.BTN_HEIGHT);
        tfdRemark.setBounds(tfdCost.getX(), lblRemark.getY(), tfdCost.getWidth() + lblUnit2.getWidth(),
                CustOpts.BTN_HEIGHT);

        int tHight =
                tfdRemark.getY() + tfdRemark.getHeight() + CustOpts.VER_GAP + CustOpts.SIZE_EDGE + CustOpts.SIZE_TITLE;
        setBounds((CustOpts.SCRWIDTH - tWidth) / 2, (CustOpts.SCRHEIGHT - tHight) / 2, tWidth, tHight - 4); // 对话框的默认尺寸。

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
        if (o == ok) { // 检查一下是否有正确输入价格和助记信息或者品名信息。
            String tPrice = tfdPrice.getText(); // 价格是否有输入。
            if (tPrice.length() < 1) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdPrice.grabFocus();
                return;
            }

            float tFloatPrice; // 如果价格有输入，格式是否可以转为Float。
            try {
                tFloatPrice = Float.parseFloat(tPrice);
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                tfdPrice.selectAll();
                tfdPrice.grabFocus();
                return;
            }

            int tPosOfPoint = tPrice.indexOf("."); // 若格式也没有问题，精度是否符合要求。
            if (tPosOfPoint != -1 && tPrice.substring(tPosOfPoint + 1).length() > 2) {
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                tfdPrice.selectAll();
                tfdPrice.grabFocus();
                return;
            }

            if (tfdPinyin.getText().length() < 1 && tfdProdName.getText().length() < 1) { // 品名和助记至少要有一项有内容
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdPinyin.grabFocus();
                return;
            }

            String tCount = tfdStorCount.getText();
            if (tCount.length() > 0) { // 库存量如果有内容，要保证其格式不能错
                try {
                    Integer.parseInt(tCount);
                } catch (Exception exp) {
                    JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                    tfdStorCount.selectAll();
                    tfdStorCount.grabFocus();
                    return;
                }
            }

            float tFloatCost; // 成本若无输入，则既为0.
            try {
                tFloatCost = Float.parseFloat(tfdCost.getText());
            } catch (Exception exp) {
                tFloatCost = 0;
            }// 检查结束--------------------------------

            // 开始将新纪录存入数据库中。
            prodCode = tfdProdCode.getText();
            price = String.valueOf(CASUtility.getPriceByCent(tFloatPrice));
            pinyin = tfdPinyin.getText();
            prodName = tfdProdName.getText();
            storCount = tCount.equals("") ? "0" : tCount;
            pack = tfdPackage.getText();
            setProdType(tfdType.getText());
            cost = String.valueOf(CASUtility.getPriceByCent(tFloatCost));
            remark = tfdRemark.getText();
            if (prodID == -1) { // 如果是增加产品的情况--------------------------------------
                String sql =
                        "INSERT INTO Product(CODE, PRICE, MNEMONIC, SUBJECT, store, Unit, CATEGORY, Cost, CONTENT, FOLDERID) VALUES ('"
                                .concat(prodCode).concat("', ").concat(price).concat(", '").concat(pinyin)
                                .concat("', '").concat(prodName).concat("', ").concat(storCount).concat(", '")
                                .concat(pack).concat("', '").concat(getProdType()).concat("', ").concat(cost)
                                .concat(", '").concat(remark).concat("', 5103)");
                try {
                    Connection conn = PIMDBModel.getConection();
                    Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
                    smt.executeUpdate(sql);

                    sql =
                            "Select id from product where code = '".concat(prodCode).concat("' and PRICE = ")
                                    .concat(price).concat(" and MNEMONIC = '").concat(pinyin)
                                    .concat("' and SUBJECT = '").concat(prodName).concat("' and store = ")
                                    .concat(storCount).concat(" and UNIT = '").concat(pack)
                                    .concat("' and CATEGORY = '").concat(getProdType()).concat("' and Cost = ")
                                    .concat(cost).concat(" and CONTENT = '").concat(remark).concat("'");
                    ResultSet rs = smt.executeQuery(sql);
                    rs.beforeFirst();
                    rs.next();
                    prodID = rs.getInt("id");
                    rs.close();
                    smt.close();
                    smt = null;
                    ADDED = true;
                    dispose();
                } catch (SQLException exp) {
                    exp.printStackTrace();
                }
            } else { // 修改产品的情况----------------------------------------------
                String sql =
                        "update product set code = '".concat(prodCode).concat("', PRICE = ").concat(price)
                                .concat(", MNEMONIC = '").concat(pinyin).concat("', SUBJECT = '").concat(prodName)
                                .concat("', store = ").concat(storCount).concat(", UNIT = '").concat(pack)
                                .concat("', CATEGORY = '").concat(getProdType()).concat("', Cost = ").concat(cost)
                                .concat(", CONTENT = '").concat(remark).concat("' where ID = ")
                                .concat(String.valueOf(prodID));

                try {
                    Statement smt = PIMDBModel.getConection().createStatement();
                    smt.executeUpdate(sql);
                    smt.close();
                    smt = null;
                    ADDED = true;
                    dispose();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            BarFrame.instance.general.initComponents();// 更新PosGeneralPanel中的品名组件内容。
        } else if (o == cancel) {
            dispose();
        }
    }

    public Container getContainer() {
        return getContentPane();
    }

    public void enableProdCode(
            boolean pEnabled) {
        tfdProdCode.setEnabled(pEnabled);
    }

    public void enableStorCount(
            boolean pEnabled) {
        tfdStorCount.setEnabled(pEnabled);
    }

    private void initDialog(
            String pProdNumber) {
        setResizable(false);

        // 初始化－－－－－－－－－－－－－－－－
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        lblProdCode = new JLabel(BarDlgConst.ProdNumber);
        lblPrice = new JLabel(BarDlgConst.Price);
        lblUnit = new JLabel(BarDlgConst.Unit);
        lblPinyin = new JLabel(BarDlgConst.Pinyin);
        lblProdName = new JLabel(BarDlgConst.ProdName);
        lblStorCount = new JLabel(BarDlgConst.Store);
        lblPackage = new JLabel(BarDlgConst.Package);
        lblType = new JLabel(BarDlgConst.Type);
        lblCost = new JLabel(BarDlgConst.Cost);
        lblUnit2 = new JLabel(BarDlgConst.Unit);
        lblRemark = new JLabel(BarDlgConst.Note);

        tfdProdCode = new JTextField((pProdNumber.equals("****") || pProdNumber.equals("××××")) ? "" : pProdNumber);
        tfdPrice = new JTextField();
        tfdPinyin = new JTextField();
        tfdProdName = new JTextField();
        tfdStorCount = new JTextField();
        tfdPackage = new JTextField();
        tfdType = new JTextField();
        tfdCost = new JTextField();
        tfdRemark = new JTextField();

        // properties
        if (pProdNumber.equals("****"))
            tfdProdCode.setEnabled(false);
        ok.setMnemonic('o');
        cancel.setMnemonic('c');

        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getMargin());

        getRootPane().setDefaultButton(ok);

        tfdPrice.setNextFocusableComponent(tfdPinyin);
        tfdPinyin.setNextFocusableComponent(tfdProdName);
        // 布局---------------
        getContentPane().setLayout(null);
        reLayout();

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(ok);
        getContentPane().add(cancel);
        getContentPane().add(lblProdCode);
        getContentPane().add(tfdProdCode);
        getContentPane().add(lblPrice);
        getContentPane().add(lblUnit);
        getContentPane().add(tfdPrice);
        getContentPane().add(lblPinyin);
        getContentPane().add(tfdPinyin);
        getContentPane().add(lblProdName);
        getContentPane().add(tfdProdName);
        getContentPane().add(lblStorCount);
        getContentPane().add(tfdStorCount);
        getContentPane().add(lblPackage);
        getContentPane().add(tfdPackage);
        getContentPane().add(lblType);
        getContentPane().add(tfdType);
        getContentPane().add(lblCost);
        getContentPane().add(tfdCost);
        getContentPane().add(lblUnit2);
        getContentPane().add(lblRemark);
        getContentPane().add(tfdRemark);
        // 加监听器－－－－－－－－
        tfdProdCode.addFocusListener(this);
        tfdPrice.addFocusListener(this);
        tfdPinyin.addFocusListener(this);
        tfdProdName.addFocusListener(this);
        tfdStorCount.addFocusListener(this);
        tfdPackage.addFocusListener(this);
        tfdType.addFocusListener(this);
        tfdCost.addFocusListener(this);

        tfdPrice.addKeyListener(this);
        tfdCost.addKeyListener(this);

        ok.addActionListener(this);
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);

        ADDED = false;
    }

    public int getProdID() {
        return prodID;
    }

    public String getProdCode() {
        return prodCode;
    }

    public String getCost() {
        return String.valueOf(Integer.parseInt(cost) / 100.0);
    }

    public String getPrice() {
        return String.valueOf(Integer.parseInt(price) / 100.0);
    }

    public String getPinyin() {
        return pinyin;
    }

    public String getProdName() {
        return prodName;
    }

    public String getStorCount() {
        return storCount;
    }

    public String getPackage() {
        return pack;
    }

    public String getRemark() {
        return remark;
    }

    public String getProdType() {
        return prodType;
    }

    public void setProdType(
            String prodType) {
        this.prodType = prodType;
    }

    public boolean ADDED;
    private int prodID = -1;
    private String prodCode;
    private String price;
    private String pinyin;
    private String prodName;
    private String storCount;
    private String pack;
    private String prodType;
    private String remark;
    private String cost;

    private JButton ok;
    private JButton cancel;

    private JLabel lblProdCode;
    private JLabel lblPrice;
    private JLabel lblUnit;
    private JLabel lblPinyin;
    private JLabel lblProdName;
    private JLabel lblStorCount;
    private JLabel lblPackage;
    private JLabel lblType;
    private JLabel lblCost;
    private JLabel lblUnit2;
    private JLabel lblRemark;

    private JTextField tfdProdCode;
    private JTextField tfdPrice;
    private JTextField tfdPinyin;
    private JTextField tfdProdName;
    private JTextField tfdStorCount;
    private JTextField tfdPackage;
    private JTextField tfdType;
    private JTextField tfdCost;
    private JTextField tfdRemark;
}
