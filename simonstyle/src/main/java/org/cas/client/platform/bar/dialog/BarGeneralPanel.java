package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;

import javax.comm.CommPortIdentifier;
import javax.comm.ParallelPort;
import javax.comm.PortInUseException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.cas.client.platform.bar.beans.CategoryToggle;
import org.cas.client.platform.bar.beans.MenuButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pos.dialog.statistics.Statistic;
import org.cas.client.platform.refund.dialog.RefundDlg;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

//Identity表应该和Employ表合并。
public class BarGeneralPanel extends JPanel implements ComponentListener, ActionListener, FocusListener {
    private final int PRICECOLID = 3;
    private final int TOTALCOLID = 4;
    private final int COUNDCOLID = 2;

    private final int USER_STATUS = 1;
    private final int ADMIN_STATUS = 2;
    private int curSecurityStatus = USER_STATUS;

    private int curCategoryPage = 0;
    private int categoryNumPerPage = 0;

    private int curMenuPageNum = 0;
    private int curMenuPerPage = 0;

    private int[] categoryIdAry;
    String[][] categoryNameMetrix;
    
    private int[] prodIDAry;
    String[][] menuNameMetrix;// the struction must be [3][index]. it's more convenient than [index][3]
    String[][] onScrMenuNameMetrix;// it's sub set of all menuNameMetrix
    
    
    private Dish[] dishAry;
    private Dish[] onScrDishAry;
    
    public static String startTime;
    CategoryToggle activeCategoryButton;

    public BarGeneralPanel() {
        initComponent();
    }

    // ComponentListener-----------------------------
    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
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
    public void focusGained(
            FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    @Override
    public void focusLost(
            FocusEvent e) {
    }

    // ActionListner-------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnPageUpTable) {
            btnPageDownTable.setEnabled(true);
            if (true) {
                btnPageUpTable.setEnabled(false);
            }
        } else if (o == btnPageDownTable) {
            btnPageUpTable.setEnabled(true);
            if (true) {
                btnPageDownTable.setEnabled(false);
            }
        } else if (o == btnPageUpCategory) {
            curCategoryPage--;
            // adjust status
            btnPageDownCategory.setEnabled(true);
            if (curCategoryPage == 0) {
                btnPageUpCategory.setEnabled(false);
            }

            reInitCategoryAndMenuBtns();
            reLayout();
        } else if (o == btnPageDownCategory) {
            curCategoryPage++;
            // adjust status
            btnPageUpCategory.setEnabled(true);
            if (curCategoryPage * categoryNumPerPage > categoryNameMetrix.length) {
                btnPageDownCategory.setEnabled(false);
            }

            reInitCategoryAndMenuBtns();
            reLayout();
        } else if (o == btnPageUpMenu) {
            curMenuPageNum--;
            btnPageDownMenu.setEnabled(true);
            if (curMenuPageNum == 0) {
                btnPageUpMenu.setEnabled(false);
            }
            reInitCategoryAndMenuBtns();
            reLayout();
        } else if (o == btnPageDownMenu) {
            curMenuPageNum++;
            btnPageUpMenu.setEnabled(true);
            if (curMenuPageNum * curMenuPerPage > menuNameMetrix.length) {
                btnPageDownMenu.setEnabled(false);
            }
            reInitCategoryAndMenuBtns();
            reLayout();
        }
        // category buttons------------------------------------
        else if (o instanceof CategoryToggle) {
            CategoryToggle categoryToggle = (CategoryToggle) o;
            String text = categoryToggle.getText();
            if (text == null || text.length() == 0) { // check if it's empty
                if (curSecurityStatus == ADMIN_STATUS) { // and it's admin mode, add a Category.
                    CategoryDlg addCategoryDlg = new CategoryDlg(BarFrame.instance);
                    addCategoryDlg.setIndex(categoryToggle.getIndex());
                    addCategoryDlg.setVisible(true);
                } else {
                    if (adminAuthentication()) {
                        CategoryDlg addCategoryDlg = new CategoryDlg(BarFrame.instance);
                        addCategoryDlg.setIndex(categoryToggle.getIndex());
                        addCategoryDlg.setVisible(true);
                    }
                }
            } else { // if it's not empty
                if (!text.equals(activeCategoryButton.getText())) {
                    //change active toggle button, and update active menus.
                    if (activeCategoryButton != null) {
                        activeCategoryButton.setSelected(false);
                    }
                    activeCategoryButton = categoryToggle;
                    initCategoryAndDishes();	//fill menu buttons with menus belong to this category.
                    reLayout();
                } else if (curSecurityStatus == ADMIN_STATUS) {
                    CategoryDlg categoryDlg = new CategoryDlg(BarFrame.instance);
                    categoryDlg.setIndex(categoryToggle.getIndex());
                    categoryDlg.setVisible(true);
                }
            }
        }
        // menu buttons---------------
        else if (o instanceof MenuButton) {
            MenuButton menuButton = (MenuButton) o;
            String text = menuButton.getText();
            if (text == null || text.length() == 0) { // check if it's empty
                if (curSecurityStatus == ADMIN_STATUS) { // and it's admin mode, add a Category.
                    new DishDlg(BarFrame.instance, menuButton.getDspIndex()).setVisible(true);
                } else {
                    if (adminAuthentication())
                        new DishDlg(BarFrame.instance, menuButton.getDspIndex()).setVisible(true);
                }
            } else { // if it's not empty
                if (curSecurityStatus == ADMIN_STATUS) {
                    new DishDlg(BarFrame.instance, menuButton.getDish()).setVisible(true);
                } else {
                    // add into table.
                	addContentToList(menuButton.getDish());
                }
            }
        } else if (o instanceof JButton) {
            JButton jButton = (JButton) o;
            if (o == btnLine_1_9) { // enter the setting mode.(admin interface)
                adminAuthentication();
            } else if (o == btnLine_3_3) { // 盘货,先检查是否存在尚未输入完整信息的产品，如果检查到存
                BarUtility.checkUnCompProdInfo(); // 在这种产品，方法中会自动弹出对话盒要求用户填写详细信息。
                new CheckStoreDlg(BarFrame.instance).setVisible(true);
            } else if (o == btnLine_3_4) {
                new AddStoreDlg(BarFrame.instance).setVisible(true);
            } else if (o == btnLine_3_5) {
                new RefundDlg(BarFrame.instance).setVisible(true);
            } else if (o == btnLine_3_6) {
            } else if (o == btnLine_3_7) {
                int tType = Integer.parseInt(CustOpts.custOps.getUserType());
                if (tType > 0) {// 如果当前登陆用户是个普通员工，则显示普通登陆对话盒。等待再次登陆
                    new LoginDlg(BarFrame.instance).setVisible(true);// 结果不会被保存到ini
                    if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                        int tLevel = Integer.parseInt(LoginDlg.USERTYPE);
                        if (tLevel <= 0)// 进一步判断，如果新登陆是经理，弹出对话盒
                            new Statistic(BarFrame.instance).setVisible(true);
                    }
                } else
                    // 如果当前的用户已经是管理员了，则弹出对话盒？
                    new Statistic(BarFrame.instance).setVisible(true);
            } else if (o == btnLine_3_8) { // Logout
                if (curSecurityStatus == ADMIN_STATUS) {
                    curSecurityStatus--;
                    // @TODO: might need to do some modification on the interface.
                    setStatusMes(BarDlgConst.USE_MODE);
                } else {
                    BarFrame.instance.dispose();
                    new LoginDlg(null).setVisible(true);
                    if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                        CustOpts.custOps.setUserName(LoginDlg.USERNAME);
                        CustOpts.custOps.setUserLang(LoginDlg.USERLANG);
                        lblOperator.setText(BarDlgConst.Operator.concat(BarDlgConst.Colon).concat(
                                CustOpts.custOps.getUserName()));
                        BarFrame.instance = new BarFrame();
                        BarFrame.instance.setVisible(true);
                    }
                }
            } else if (o == btnLine_3_9) {
                new BarOptionDlg(BarFrame.instance).setVisible(true);
            }
        }
    }

    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("admin".equalsIgnoreCase(CustOpts.custOps.getUserName())) {
                curSecurityStatus++;
                setStatusMes(BarDlgConst.ADMIN_MODE);
                // @TODO: might need to do some modification on the interface.
                revalidate();
                return true;
            }
        }
        return false;
    }

    private void resetColWidth(int tableWidth) {
        PIMTableColumn tmpCol1 = tblContent.getColumnModel().getColumn(0);
        tmpCol1.setWidth(40);
        tmpCol1.setPreferredWidth(40);
        PIMTableColumn tmpCol3 = tblContent.getColumnModel().getColumn(2);
        tmpCol3.setWidth(40);
        tmpCol3.setPreferredWidth(40);
        PIMTableColumn tmpCol4 = tblContent.getColumnModel().getColumn(3);
        tmpCol4.setWidth(40);
        tmpCol4.setPreferredWidth(40);
        PIMTableColumn tmpCol5 = tblContent.getColumnModel().getColumn(4);
        tmpCol5.setWidth(40);
        tmpCol5.setPreferredWidth(40);

        PIMTableColumn tmpCol2 = tblContent.getColumnModel().getColumn(1);
        tmpCol2.setWidth(tableWidth - tmpCol1.getWidth() - tmpCol3.getWidth() - tmpCol4.getWidth() - tmpCol5.getWidth() - 3);
        tmpCol2.setPreferredWidth(tmpCol2.getWidth());
        
        tblContent.validate();
        tblContent.revalidate();
        tblContent.invalidate();
    }

    // 将对话盒区域的内容加入到列表
    private void addContentToList(Dish dish) {
        int tRowCount = tblContent.getRowCount(); // add content to the table.
        int tColCount = tblContent.getColumnCount();
        int tValidRowCount = getUsedRowCount(); // get the used RowCount
        if (tRowCount == tValidRowCount) { // no line is empty, add a new Line.
            Object[][] tValues = new Object[tRowCount + 1][tColCount];
            for (int r = 0; r < tRowCount; r++)
                for (int c = 0; c < tColCount; c++)
                    tValues[r][c] = tblContent.getValueAt(r, c);
            tblContent.setDataVector(tValues, header);
            resetColWidth(srpContent.getWidth());
        }else {
        	tRowCount--;
        }
        tblContent.setValueAt(tRowCount + 1, tValidRowCount, 0); // set the code.
        tblContent.setValueAt(dish.getLanguage(CustOpts.custOps.getUserLang()), tValidRowCount, 1);// set the Name.
        tblContent.setValueAt(dish.getSize() > 1 ? dish.getSize() : "", tValidRowCount, 2); // set the count.
        tblContent.setValueAt(1, tValidRowCount, 3); // set the count.
        tblContent.setValueAt(dish.getPrice()/100f, tValidRowCount, 4); // set the price.

        enableBtns(false);
        setStatusMes(BarDlgConst.NotePordNumber3);
    }

    private void openMoneyBox() {
        int[] ccs = new int[5];
        ccs[0] = 27;
        ccs[1] = 112;
        ccs[2] = 0;
        ccs[3] = 80;
        ccs[4] = 250;

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
                            for (int i = 0; i < 5; i++)
                                tOutStream.write(ccs[i]);
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

    private void printInvoice(
            String pDate) {
    }

    private void initComponent() {
        int tShoestring = 0;
        try {
            tShoestring = Integer.parseInt((String) CustOpts.custOps.getValue(BarDlgConst.Shoestring));
        } catch (Exception exp) {
        }
        startTime = Calendar.getInstance().getTime().toLocaleString();
        lblOperator = new JLabel(BarDlgConst.Operator.concat(BarDlgConst.Colon).concat(CustOpts.custOps.getUserName()));
        lblShoestring =
                new JLabel(BarDlgConst.LeftMoney.concat(BarDlgConst.Colon)
                        .concat(decimalFormat.format(tShoestring / 100.0)).concat(BarDlgConst.Unit));
        lblStartTime = new JLabel(BarDlgConst.StartTime.concat(BarDlgConst.Colon).concat(startTime));// @Todo:以后改为从服务器上获取。
        lblSubTotle = new JLabel(BarDlgConst.Subtotal);
        lblTSQ = new JLabel(BarDlgConst.QST);
        lblRSQ = new JLabel(BarDlgConst.GST);
        lblTotlePrice = new JLabel(BarDlgConst.Total);

        tfdTotlePrice = new JTextField();
        tfdCustomer = new JTextField();

        btnLine_3_1 = new JButton(BarDlgConst.SEND);
        btnLine_3_2 = new JButton(BarDlgConst.PAY);
        btnLine_3_3 = new JButton(BarDlgConst.PRINT_BILL);
        btnLine_3_4 = new JButton(BarDlgConst.QUICK_OPEN);
        btnLine_3_5 = new JButton(BarDlgConst.VOID_ALL);
        btnLine_3_6 = new JButton(BarDlgConst.MODIFY);
        btnLine_3_7 = new JButton(BarDlgConst.DISC_VOLUMN);
        btnLine_3_8 = new JButton(BarDlgConst.Logout);
        btnLine_3_9 = new JButton(BarDlgConst.MORE);

        btnLine_2_1 = new JButton("");
        btnLine_2_2 = new JButton("");
        btnLine_2_3 = new JButton("");
        btnLine_2_4 = new JButton(BarDlgConst.MASTER);
        btnLine_2_5 = new JButton(BarDlgConst.VOID_ITEM);
        btnLine_2_6 = new JButton(BarDlgConst.PRICE);
        btnLine_2_7 = new JButton(BarDlgConst.DISC_ITEM);
        btnLine_2_8 = new JButton(BarDlgConst.SPLIT_BILL);
        btnLine_2_9 = new JButton("");

        btnLine_1_1 = new JButton(BarDlgConst.EXACT_CASH);
        btnLine_1_2 = new JButton(BarDlgConst.CASH);
        btnLine_1_3 = new JButton(BarDlgConst.DEBIT);
        btnLine_1_4 = new JButton(BarDlgConst.VISA);
        btnLine_1_5 = new JButton(BarDlgConst.CANCEL_ALL);
        btnLine_1_6 = new JButton(BarDlgConst.QTY);
        btnLine_1_7 = new JButton(BarDlgConst.FAST_DISCOUNT);
        btnLine_1_8 = new JButton(BarDlgConst.EQUL_BILL);
        btnLine_1_9 = new JButton(BarDlgConst.SETTINGS);

        btnPageUpTable = new JButton("↑");
        btnPageDownTable = new JButton("↓");
        btnPageUpCategory = new JButton("↑");
        btnPageDownCategory = new JButton("↓");
        btnPageUpMenu = new JButton("↑");
        btnPageDownMenu = new JButton("↓");

        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);
        lblStatus = new JLabel();

        // properties
        setLayout(null);

        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(20);
        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);
        Font tFont = PIMPool.pool.getFont((String) CustOpts.custOps.hash2.get(PaneConsts.DFT_FONT), Font.PLAIN, 40);

        // Margin-----------------
        btnLine_3_2.setMargin(new Insets(0, 0, 0, 0));
        btnLine_3_1.setMargin(btnLine_3_2.getInsets());
        btnLine_3_3.setMargin(btnLine_3_2.getInsets());
        btnLine_3_6.setMargin(btnLine_3_2.getInsets());
        btnLine_3_7.setMargin(btnLine_3_2.getInsets());
        btnLine_3_8.setMargin(btnLine_3_2.getInsets());
        btnLine_3_4.setMargin(btnLine_3_2.getInsets());
        btnLine_3_5.setMargin(btnLine_3_2.getInsets());
        btnPageUpTable.setMargin(btnLine_3_2.getInsets());
        btnPageDownTable.setMargin(btnLine_3_2.getInsets());
        btnPageUpCategory.setMargin(btnLine_3_2.getInsets());
        btnPageDownCategory.setMargin(btnLine_3_2.getInsets());
        btnPageUpMenu.setMargin(btnLine_3_2.getInsets());
        btnPageDownMenu.setMargin(btnLine_3_2.getInsets());

        // border----------
        tblContent.setBorder(null);
        lblStatus.setBorder(null);
        // forcus-------------
        tfdTotlePrice.setFocusable(false);
        tfdCustomer.setFocusable(false);

        btnLine_3_2.setFocusable(false);
        btnLine_3_3.setFocusable(false);
        btnLine_3_6.setFocusable(false);
        // btnMUser.setFocusable(false);
        // btnMRate.setFocusable(false);
        btnLine_3_7.setFocusable(false);
        btnLine_3_8.setFocusable(false);
        btnLine_3_4.setFocusable(false);
        btnLine_3_5.setFocusable(false);

        tblContent.setFocusable(false);

        // disables
        btnPageUpCategory.setEnabled(false);
        btnPageUpMenu.setEnabled(false);
        btnPageUpTable.setEnabled(false);

        // built
        add(lblOperator);
        add(lblShoestring);
        add(lblStartTime);

        add(lblSubTotle);
        add(lblTSQ);
        add(lblRSQ);
        add(lblTotlePrice);

        add(tfdTotlePrice);
        add(tfdCustomer);

        add(btnLine_3_1);
        add(btnLine_3_2);
        add(btnLine_3_3);
        add(btnLine_3_4);
        add(btnLine_3_5);
        add(btnLine_3_6);
        add(btnLine_3_7);
        add(btnLine_3_8);
        add(btnLine_3_9);

        // add(btnLine_2_1);
        // add(btnLine_2_2);
        // add(btnLine_2_3);
        add(btnLine_2_4);
        add(btnLine_2_5);
        add(btnLine_2_6);
        add(btnLine_2_7);
        add(btnLine_2_8);
        add(btnLine_2_9);

        add(btnLine_1_1);
        add(btnLine_1_2);
        add(btnLine_1_3);
        add(btnLine_1_4);
        add(btnLine_1_5);
        add(btnLine_1_6);
        add(btnLine_1_7);
        add(btnLine_1_8);
        add(btnLine_1_9);

        add(btnPageUpTable);
        add(btnPageDownTable);
        add(btnPageUpCategory);
        add(btnPageDownCategory);
        add(btnPageUpMenu);
        add(btnPageDownMenu);

        add(srpContent);
        add(lblStatus);

        // add listener
        addComponentListener(this);

        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        // tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）
        btnPageUpTable.addActionListener(this);
        btnPageDownTable.addActionListener(this);
        btnPageUpCategory.addActionListener(this);
        btnPageDownCategory.addActionListener(this);
        btnPageUpMenu.addActionListener(this);
        btnPageDownMenu.addActionListener(this);

        btnLine_3_1.addActionListener(this);
        btnLine_3_2.addActionListener(this);
        btnLine_3_3.addActionListener(this);
        btnLine_3_4.addActionListener(this);
        btnLine_3_5.addActionListener(this);
        btnLine_3_6.addActionListener(this);
        btnLine_3_7.addActionListener(this);
        btnLine_3_8.addActionListener(this);
        btnLine_3_9.addActionListener(this);

        btnLine_2_1.addActionListener(this);
        btnLine_2_2.addActionListener(this);
        btnLine_2_3.addActionListener(this);
        btnLine_2_4.addActionListener(this);
        btnLine_2_5.addActionListener(this);
        btnLine_2_6.addActionListener(this);
        btnLine_2_7.addActionListener(this);
        btnLine_2_8.addActionListener(this);
        btnLine_2_9.addActionListener(this);

        btnLine_1_1.addActionListener(this);
        btnLine_1_2.addActionListener(this);
        btnLine_1_3.addActionListener(this);
        btnLine_1_4.addActionListener(this);
        btnLine_1_5.addActionListener(this);
        btnLine_1_6.addActionListener(this);
        btnLine_1_7.addActionListener(this);
        btnLine_1_8.addActionListener(this);
        btnLine_1_9.addActionListener(this);

        // initContents--------------
        initCategoryAndDishes();
        initTable();
    }

    public void initCategoryAndDishes() {
        try {
            Connection connection = PIMDBModel.getConection();
            Statement statement =
                    connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // load all the categorys---------------------------
            ResultSet categoryRS = statement.executeQuery("select ID, LANG1, LANG2, LANG3 from CATEGORY order by DSP_INDEX");
            categoryRS.afterLast();
            categoryRS.relative(-1);
            int tmpPos = categoryRS.getRow();
            categoryIdAry = new int[tmpPos];
            categoryNameMetrix = new String[3][tmpPos];
            categoryRS.beforeFirst();

            tmpPos = 0;
            while (categoryRS.next()) {
                categoryIdAry[tmpPos] = categoryRS.getInt("ID");
                
                categoryNameMetrix[0][tmpPos] = categoryRS.getString("LANG1");
                categoryNameMetrix[1][tmpPos] = categoryRS.getString("LANG2");
                categoryNameMetrix[2][tmpPos] = categoryRS.getString("LANG3");
                
                tmpPos++;
            }
            categoryRS.close();// 关闭

            // load all the dishes----------------------------
            ResultSet productRS =
                    statement
                            .executeQuery("select ID, CODE, MNEMONIC, SUBJECT, PRICE, FOLDERID, STORE,  COST, BRAND, CATEGORY, CONTENT, UNIT, PRODUCAREA, INDEX from product where deleted != true order by index");
            productRS.afterLast();
            productRS.relative(-1);
            tmpPos = productRS.getRow();
            prodIDAry = new int[tmpPos];
            menuNameMetrix = new String[3][tmpPos];
            dishAry = new Dish[tmpPos];
            productRS.beforeFirst();
            
            //compose the record into dish objects--------------
            tmpPos = 0;
            while (productRS.next()) { // @NOTE: don't load all the content, because menu can be many
                prodIDAry[tmpPos] = productRS.getInt("ID");
                menuNameMetrix[0][tmpPos] = productRS.getString("CODE");
                menuNameMetrix[1][tmpPos] = productRS.getString("MNEMONIC");
                menuNameMetrix[2][tmpPos] = productRS.getString("SUBJECT");

                dishAry[tmpPos] = new Dish();
                dishAry[tmpPos].setId(prodIDAry[tmpPos]);
                dishAry[tmpPos].setLanguage(0, menuNameMetrix[0][tmpPos]);
                dishAry[tmpPos].setLanguage(1, menuNameMetrix[1][tmpPos]);
                dishAry[tmpPos].setLanguage(2, menuNameMetrix[2][tmpPos]);
                dishAry[tmpPos].setPrice(productRS.getInt("PRICE"));
                dishAry[tmpPos].setGst(productRS.getInt("FOLDERID"));
                dishAry[tmpPos].setQst(productRS.getInt("STORE"));
                dishAry[tmpPos].setSize(productRS.getInt("COST"));
                dishAry[tmpPos].setPrinter(productRS.getString("BRAND"));
                dishAry[tmpPos].setCATEGORY(productRS.getString("CATEGORY"));
                dishAry[tmpPos].setPrompPrice(productRS.getString("CONTENT"));
                dishAry[tmpPos].setPrompMenu(productRS.getString("UNIT"));
                dishAry[tmpPos].setPrompMofify(productRS.getString("PRODUCAREA"));
                dishAry[tmpPos].setDspIndex(productRS.getInt("INDEX"));
                tmpPos++;
            }
            productRS.close();// 关闭

        } catch (Exception e) {
            ErrorUtil.write(e);
        }
        reInitCategoryAndMenuBtns();
    }

    // menu and category buttons must be init after initContent---------
    private void reInitCategoryAndMenuBtns() {
        // validate rows and columns first(in case they are changed into bad value)--------
        categoryColumn = (categoryColumn == null || categoryColumn < 4) ? 5 : categoryColumn;
        categoryRow = (categoryRow == null || categoryRow < 1 || categoryRow > 9) ? 3 : categoryRow;
        categoryNumPerPage = categoryColumn * categoryRow;

        menuColumn = (menuColumn == null || menuColumn < 1) ? 4 : menuColumn;
        menuRow = (menuRow == null || menuRow < 1) ? 4 : menuRow;
        curMenuPerPage = menuColumn * menuRow;

        // clean current catogory and menus from both screen and metrix if have---------------
        for (int r = 0; r < categoryRow; r++) {
            if (r < onSrcCategoryMatrix.size()) {
                for (int c = 0; c < categoryColumn; c++) {
                    if (c < onSrcCategoryMatrix.get(r).size())
                        remove(onSrcCategoryMatrix.get(r).get(c));
                }
            }
        }
        for (int r = 0; r < menuRow; r++) {
            if (r < onSrcMenuBtnMatrix.size()) {
                for (int c = 0; c < menuColumn; c++) {
                    if (c < onSrcMenuBtnMatrix.get(r).size())
                        remove(onSrcMenuBtnMatrix.get(r).get(c));
                }
            }
        }
        onSrcCategoryMatrix.clear();
        onSrcMenuBtnMatrix.clear();

        // create new buttons and add onto the screen (no layout yet)------------
        int dspIndex = curCategoryPage * categoryNumPerPage;
        for (int r = 0; r < categoryRow; r++) {
            ArrayList<CategoryToggle> btnCategoryArry = new ArrayList<CategoryToggle>();
            for (int c = 0; c < categoryColumn; c++) {
                dspIndex++;
                CategoryToggle btnCategory = new CategoryToggle(dspIndex);
                btnCategory.setMargin(new Insets(0, 0, 0, 0));
                add(btnCategory);
                btnCategory.addActionListener(this);
                btnCategoryArry.add(btnCategory);
                if (dspIndex <= categoryNameMetrix[0].length) {
                    btnCategory.setText(categoryNameMetrix[CustOpts.custOps.getUserLang()][dspIndex - 1]);
                    if (activeCategoryButton != null
                            && categoryNameMetrix[CustOpts.custOps.getUserLang()][dspIndex - 1].equalsIgnoreCase(activeCategoryButton.getText())) {
                        btnCategory.setSelected(true);
                    }
                } else {
                    btnPageDownCategory.setEnabled(false);
                }
            }
            onSrcCategoryMatrix.add(btnCategoryArry);
        }

        // if no activeCategory, use the first one on screen.
        if (activeCategoryButton == null) {
            activeCategoryButton = onSrcCategoryMatrix.get(0).get(0);
            activeCategoryButton.setSelected(true);
        }

        // initialize on screen menus===============================================================
        //find out menus matching to current category and current lang
        onScrMenuNameMetrix = new String[3][menuNameMetrix[0].length];
        onScrDishAry = new Dish[menuNameMetrix[0].length];
        
        int onscrMenuIndex = 0;
        for (int i = 0; i < dishAry.length; i++) {
			if(dishAry[i].getCATEGORY().equals(activeCategoryButton.getText())) {
				
				onScrMenuNameMetrix[0][onscrMenuIndex] = menuNameMetrix[0][i];
				onScrMenuNameMetrix[1][onscrMenuIndex] = menuNameMetrix[1][i];
				onScrMenuNameMetrix[2][onscrMenuIndex] = menuNameMetrix[2][i];
				
				onScrDishAry[onscrMenuIndex] = dishAry[i];
				//make sure the display index are lined
				if(dishAry[i].getDspIndex() != onscrMenuIndex + 1) {
					try {
		                Connection conn = PIMDBModel.getConection();
		                Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

			            StringBuilder sql = new StringBuilder("UPDATE product SET INDEX = ").append(onscrMenuIndex + 1)
			                            	.append(" where ID = ").append(dishAry[i].getId());
			            smt.executeUpdate(sql.toString());
			            smt.close();
		                smt = null;
		            }catch(Exception exp) {
		                exp.printStackTrace();
		            }
					dishAry[i].setDspIndex(onscrMenuIndex + 1);
				}
				
				onscrMenuIndex++;
			}
		}
        
        dspIndex = curMenuPageNum * curMenuPerPage;
        for (int r = 0; r < menuRow; r++) {
            ArrayList<MenuButton> btnMenuArry = new ArrayList<MenuButton>();
            for (int c = 0; c < menuColumn; c++) {
                MenuButton btnMenu = new MenuButton(dspIndex + 1);
                btnMenu.setMargin(new Insets(0, 0, 0, 0));
                add(btnMenu);
                btnMenu.addActionListener(this);
                btnMenuArry.add(btnMenu);
                if (dspIndex < onscrMenuIndex) {
                    btnMenu.setText(onScrMenuNameMetrix[CustOpts.custOps.getUserLang()][dspIndex]);// TODO: replace 0 with
                    btnMenu.setDish(onScrDishAry[dspIndex]);// TODO: replace 0 with
                } else {
                    btnPageDownMenu.setEnabled(false);
                }

                dspIndex++;
            }
            onSrcMenuBtnMatrix.add(btnMenuArry);
        }
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
        int panelWidth = getWidth();
        int panelHeight = getHeight();
        lblOperator.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblOperator.getPreferredSize().width,
                lblOperator.getPreferredSize().height);
        int tHalfHeight = (panelHeight - lblOperator.getY() - lblOperator.getHeight()) / 2;
        int tBtnWidht = (panelWidth - CustOpts.HOR_GAP * 10) / 9;
        int tBtnHeight = panelHeight / 10;
        int tGap = tHalfHeight / 11;
        int tVGap = tGap * 2 / 3;
        int tCompH = tGap + tGap - tVGap;
        int tFieldWidth1 = panelWidth / 2;

        lblStartTime.setBounds(panelWidth - lblStartTime.getPreferredSize().width - CustOpts.HOR_GAP,
                lblOperator.getY(), lblStartTime.getPreferredSize().width, lblOperator.getHeight());
        lblShoestring.setBounds(
                lblOperator.getX()
                        + lblOperator.getWidth()
                        + (lblStartTime.getX() - lblOperator.getX() - lblOperator.getWidth() - lblShoestring
                                .getPreferredSize().width) / 2, lblOperator.getY(),
                lblShoestring.getPreferredSize().width, lblOperator.getHeight());

        // status---------
        lblStatus.setBounds(CustOpts.HOR_GAP, panelHeight - CustOpts.LBL_HEIGHT - CustOpts.VER_GAP, panelWidth
                - CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);

        // command buttons--------------
        // line 3
        btnLine_3_1
                .setBounds(CustOpts.HOR_GAP, lblStatus.getY() - tBtnHeight - CustOpts.VER_GAP, tBtnWidht, tBtnHeight);
        btnLine_3_2.setBounds(btnLine_3_1.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_3_3.setBounds(btnLine_3_2.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_3_4.setBounds(btnLine_3_3.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_3_5.setBounds(btnLine_3_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_3_6.setBounds(btnLine_3_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_3_7.setBounds(btnLine_3_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_3_8.setBounds(btnLine_3_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_3_9.setBounds(btnLine_3_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_3_1.getY(), tBtnWidht,
                tBtnHeight);

        // line 2
        btnLine_2_1.setBounds(CustOpts.HOR_GAP, btnLine_3_1.getY() - tBtnHeight - CustOpts.VER_GAP, tBtnWidht,
                tBtnHeight);
        btnLine_2_2.setBounds(btnLine_2_1.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_3.setBounds(btnLine_2_2.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_4.setBounds(btnLine_2_3.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_5.setBounds(btnLine_2_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_6.setBounds(btnLine_2_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_7.setBounds(btnLine_2_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_8.setBounds(btnLine_2_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_2_9.setBounds(btnLine_2_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_2_1.getY(), tBtnWidht,
                tBtnHeight);
        // line 1
        btnLine_1_1.setBounds(CustOpts.HOR_GAP, btnLine_2_1.getY() - tBtnHeight - CustOpts.VER_GAP, tBtnWidht,
                tBtnHeight * 2 + CustOpts.VER_GAP);
        btnLine_1_2.setBounds(btnLine_1_1.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight * 2 + CustOpts.VER_GAP);
        btnLine_1_3.setBounds(btnLine_1_2.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight * 2 + CustOpts.VER_GAP);
        btnLine_1_4.setBounds(btnLine_1_3.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_5.setBounds(btnLine_1_4.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_6.setBounds(btnLine_1_5.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_7.setBounds(btnLine_1_6.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_8.setBounds(btnLine_1_7.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);
        btnLine_1_9.setBounds(btnLine_1_8.getX() + tBtnWidht + CustOpts.HOR_GAP, btnLine_1_1.getY(), tBtnWidht,
                tBtnHeight);

        // TOP part============================
        int topAreaHeight = btnLine_1_1.getY() - 3 * CustOpts.VER_GAP - lblOperator.getY() - lblOperator.getHeight();
        // table area-------------
        Double tableWidth = (Double) CustOpts.custOps.hash2.get("TableWidth");
        tableWidth = (tableWidth == null || tableWidth < 0.2) ? 0.4 : tableWidth;
        srpContent.setBounds(CustOpts.HOR_GAP, lblOperator.getY() + lblOperator.getHeight() + CustOpts.VER_GAP,
                (int) (panelWidth * tableWidth) - BarDlgConst.SCROLLBAR_WIDTH, topAreaHeight
                        - BarDlgConst.SubTotal_HEIGHT);

        btnPageUpTable.setBounds(CustOpts.HOR_GAP + srpContent.getWidth(), srpContent.getY() + srpContent.getHeight()
                - BarDlgConst.SCROLLBAR_WIDTH * 4 - CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH,
                BarDlgConst.SCROLLBAR_WIDTH * 2);
        btnPageDownTable.setBounds(btnPageUpTable.getX(), btnPageUpTable.getY() + btnPageUpTable.getHeight()
                + CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH * 2);

        // sub total-------
        lblTSQ.setBounds(srpContent.getX(), srpContent.getY() + srpContent.getHeight(), srpContent.getWidth() / 4,
                BarDlgConst.SubTotal_HEIGHT * 1 / 3);
        lblRSQ.setBounds(lblTSQ.getX() + lblTSQ.getWidth(), lblTSQ.getY(), lblTSQ.getWidth(), lblTSQ.getHeight());
        lblSubTotle.setBounds(lblRSQ.getX() + lblRSQ.getWidth(), lblTSQ.getY(), lblRSQ.getWidth() * 2,
                lblTSQ.getHeight());
        lblTotlePrice.setBounds(lblSubTotle.getX(), lblSubTotle.getY() + lblSubTotle.getHeight(),
                lblSubTotle.getWidth(), BarDlgConst.SubTotal_HEIGHT * 2 / 3);

        // category area--------------
        int xMenuArea = srpContent.getX() + srpContent.getWidth() + CustOpts.HOR_GAP + BarDlgConst.SCROLLBAR_WIDTH;
        int widthMenuArea =
                (panelWidth - srpContent.getWidth() - CustOpts.HOR_GAP * 3) - BarDlgConst.SCROLLBAR_WIDTH * 2;
        Double categoryHeight = (Double) CustOpts.custOps.hash2.get("categoryHeight");
        categoryHeight = (categoryHeight == null || categoryHeight < 0.2) ? 0.4 : categoryHeight;

        int categeryBtnWidth = (widthMenuArea - CustOpts.HOR_GAP * (categoryColumn - 1)) / categoryColumn;
        int categeryBtnHeight =
                (int) ((topAreaHeight * categoryHeight - CustOpts.VER_GAP * (categoryRow - 1)) / categoryRow);

        for (int r = 0; r < categoryRow; r++) {
            for (int c = 0; c < categoryColumn; c++) {
                JToggleButton toggleButton = onSrcCategoryMatrix.get(r).get(c);
                toggleButton.setBounds(xMenuArea + (categeryBtnWidth + CustOpts.HOR_GAP) * c, srpContent.getY()
                        + (categeryBtnHeight + CustOpts.VER_GAP) * r, categeryBtnWidth, categeryBtnHeight);
            }
        }
        btnPageUpCategory.setBounds(xMenuArea + widthMenuArea, srpContent.getY(), BarDlgConst.SCROLLBAR_WIDTH,
                BarDlgConst.SCROLLBAR_WIDTH * 2);
        btnPageDownCategory.setBounds(btnPageUpCategory.getX(),
                btnPageUpCategory.getY() + btnPageUpCategory.getHeight() + CustOpts.VER_GAP,
                BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH * 2);

        // menu area--------------
        int menuY = srpContent.getY() + (categeryBtnHeight + CustOpts.VER_GAP) * categoryRow + CustOpts.VER_GAP;
        int menuBtnWidth = (widthMenuArea - CustOpts.HOR_GAP * (menuColumn - 1)) / menuColumn;
        int menuBtnHeight = (int) ((topAreaHeight * (1 - categoryHeight) - CustOpts.VER_GAP * (menuRow)) / menuRow);
        for (int r = 0; r < menuRow; r++) {
            for (int c = 0; c < menuColumn; c++) {
                onSrcMenuBtnMatrix
                        .get(r)
                        .get(c)
                        .setBounds(xMenuArea + (menuBtnWidth + CustOpts.HOR_GAP) * c,
                                menuY + (menuBtnHeight + CustOpts.VER_GAP) * r, menuBtnWidth, menuBtnHeight);
            }
        }
        btnPageUpMenu.setBounds(btnPageUpCategory.getX(), srpContent.getY() + topAreaHeight
                - BarDlgConst.SCROLLBAR_WIDTH * 4 - CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH,
                BarDlgConst.SCROLLBAR_WIDTH * 2);
        btnPageDownMenu.setBounds(btnPageUpMenu.getX(), btnPageUpMenu.getY() + btnPageUpMenu.getHeight()
                + CustOpts.VER_GAP, BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH * 2);

        resetColWidth(srpContent.getWidth());
    }

    private boolean isOnProcess() {
        return getUsedRowCount() > 0;
    }

    private int getUsedRowCount() {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++)
            if (tblContent.getValueAt(i, 0) == null)
                return i; // 至此得到 the used RowCount。
        return tblContent.getRowCount();
    }

    private void resetAll() {
        resetDlgArea();
        resetListArea();
    }

    private void resetDlgArea() {
        tfdTotlePrice.setText("");
        tfdCustomer.setText("");
    }

    private void resetListArea() {
        int tRowCount = tblContent.getRowCount();
        int tColCount = tblContent.getColumnCount();
        for (int j = 0; j < tRowCount; j++)
            for (int i = 0; i < tColCount; i++)
                tblContent.setValueAt(null, j, i);
    }

    private void enableBtns(
            boolean pIsEnable) {
        btnLine_3_1.setEnabled(pIsEnable);
        btnLine_3_2.setEnabled(pIsEnable);
        btnLine_3_3.setEnabled(pIsEnable);
        btnLine_3_4.setEnabled(pIsEnable);
        btnLine_3_5.setEnabled(pIsEnable);
    }

    public static void setStatusMes(
            String pMes) {
        lblStatus.setText(pMes);
    }

    private JLabel lblOperator;
    private JLabel lblShoestring;
    private JLabel lblStartTime;

    private JLabel lblSubTotle;
    private JLabel lblTSQ;
    private JLabel lblRSQ;
    private JLabel lblTotlePrice;

    static JLabel lblStatus;

    private JTextField tfdTotlePrice;
    private JTextField tfdCustomer;

    private JButton btnLine_3_1;
    private JButton btnLine_3_2;
    private JButton btnLine_3_3;
    private JButton btnLine_3_4;
    private JButton btnLine_3_5;
    private JButton btnLine_3_6;
    private JButton btnLine_3_7;
    private JButton btnLine_3_8;
    private JButton btnLine_3_9;

    private JButton btnLine_2_1;
    private JButton btnLine_2_2;
    private JButton btnLine_2_3;
    private JButton btnLine_2_4;
    private JButton btnLine_2_5;
    private JButton btnLine_2_6;
    private JButton btnLine_2_7;
    private JButton btnLine_2_8;
    private JButton btnLine_2_9;

    private JButton btnLine_1_1;
    private JButton btnLine_1_2;
    private JButton btnLine_1_3;
    private JButton btnLine_1_4;
    private JButton btnLine_1_5;
    private JButton btnLine_1_6;
    private JButton btnLine_1_7;
    private JButton btnLine_1_8;
    private JButton btnLine_1_9;

    private JButton btnPageUpTable;
    private JButton btnPageDownTable;
    private JButton btnPageUpCategory;
    private JButton btnPageDownCategory;
    private JButton btnPageUpMenu;
    private JButton btnPageDownMenu;

    Integer categoryColumn = (Integer) CustOpts.custOps.hash2.get("categoryColumn");
    Integer categoryRow = (Integer) CustOpts.custOps.hash2.get("categoryRow");
    Integer menuColumn = (Integer) CustOpts.custOps.hash2.get("menuColumn");
    Integer menuRow = (Integer) CustOpts.custOps.hash2.get("menuRow");

    ArrayList<ArrayList<CategoryToggle>> onSrcCategoryMatrix = new ArrayList<ArrayList<CategoryToggle>>();
    private ArrayList<ArrayList<MenuButton>> onSrcMenuBtnMatrix = new ArrayList<ArrayList<MenuButton>>();

    private PIMTable tblContent;
    private PIMScrollPane srpContent;
    private String[] header = new String[] { BarDlgConst.ProdNumber, BarDlgConst.ProdName, BarDlgConst.Size, BarDlgConst.Count,
            BarDlgConst.Price};
    
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");

}
