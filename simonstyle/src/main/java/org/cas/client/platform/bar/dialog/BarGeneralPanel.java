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
import java.util.Vector;

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

import org.cas.client.platform.cascontrol.dialog.category.AddCategoryDlg;
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
import org.cas.client.resource.international.PaneConsts;
import org.hsqldb.index.Index;

//Identity表应该和Employ表合并。
public class BarGeneralPanel extends JPanel implements ComponentListener, KeyListener, ActionListener, FocusListener {
    final int PRICECOLID = 3;
    final int TOTALCOLID = 4;
    final int COUNDCOLID = 2;

    final int USER_STATUS = 1;
    final int ADMIN_STATUS = 2;
    int curSecurityStatus = USER_STATUS;
    
    int categoryBtnPageNum = 0;
    int categoryNumPerPage = 0;

    int menuBtnPageNum = 0;
    int menuNumPerPage = 0;
    
    public static String startTime;
    private CategoryToggle activeToggleButton;
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

    // ActionListner----------------------------------
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == btnPageUpTable) {
        } else if (o == btnPageDownTable) {
        } else if (o == btnPageUpCategory) {
        } else if (o == btnPageDownCategory) {
        } else if (o == btnPageUpMenu) {
        } else if (o == btnPageDownMenu) {
        } else if ( o instanceof CategoryToggle) {
        	CategoryToggle categoryToggle = (CategoryToggle)o;
        	String text = categoryToggle.getText();
        	if(text == null || text.length() == 0) {	//check if it's empty
        		if(curSecurityStatus == ADMIN_STATUS) {		//and it's admin mode, add a Category.
	        		AddCategoryDlg addCategoryDlg = new AddCategoryDlg(BarFrame.instance);
	        		addCategoryDlg.setIndex(categoryToggle.getIndex() + (categoryBtnPageNum * categoryNumPerPage));
	        		addCategoryDlg.setVisible(true);
	        		initCategoryAndMenus();
	        		reLayout();
	        		//add a new category
        		}else {
        			if(adminAuthentication()) {
        				AddCategoryDlg addCategoryDlg = new AddCategoryDlg(BarFrame.instance);
        				addCategoryDlg.setIndex(categoryToggle.getIndex() + (categoryBtnPageNum * categoryNumPerPage));
    	        		addCategoryDlg.setVisible(true);
    	        		initCategoryAndMenus();
    	        		reLayout();
        			}
        		}
        	}else {										//if it's not empty
        		if(!categoryToggle.isSelected()){
        			//TODO: fill menu buttons with menus belong to this category.
	        	}else if(curSecurityStatus == ADMIN_STATUS) {
	        		AddCategoryDlg addCategoryDlg = new AddCategoryDlg(BarFrame.instance);
	        		addCategoryDlg.setText(text);
	        		addCategoryDlg.setIndex(categoryToggle.getIndex() + (categoryBtnPageNum * categoryNumPerPage));
	        		addCategoryDlg.setVisible(true);
	        		initCategoryAndMenus();
	        		reLayout();
	        	}
        	}
        	//TODO: change active toggle button, and update active menus.
        	if(activeToggleButton != null) {
        		activeToggleButton.setSelected(false);
        	}
        	activeToggleButton = categoryToggle;
        	
        }else if (o instanceof JButton) {
        	JButton jButton = (JButton)o;
    		if ( o == btnLine_1_9) { // enter the setting mode.(admin interface)
    			adminAuthentication();
    		} else if (o == btnLine_3_3) { // 盘货,先检查是否存在尚未输入完整信息的产品，如果检查到存
                BarUtility.checkUnCompProdInfo(); // 在这种产品，方法中会自动弹出对话盒要求用户填写详细信息。
                new CheckStoreDlg(BarFrame.instance).setVisible(true);
            } else if (o == btnLine_3_4) {
                new AddStoreDlg(BarFrame.instance).setVisible(true);
            } else if (o == btnLine_3_5) {
                new RefundDlg(BarFrame.instance).setVisible(true);
            } else if (o == btnLine_3_6) {
                hangup();
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
                    BarFrame.instance.setVisible(false);
                    new LoginDlg(null).setVisible(true);
                    if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                        CustOpts.custOps.setUserName(LoginDlg.USERNAME);
                        lblOperator.setText(BarDlgConst.Operator.concat(BarDlgConst.Colon).concat(
                                CustOpts.custOps.getUserName()));
                        reLayout();
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
    // Key Listener--------------------------------
    @Override
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
    }

    private void commKeyProcess(
            KeyEvent e) {
        int tKeyCode = e.getKeyCode();
        if (tKeyCode == 112) {// F1库存
            if (btnLine_3_3.isEnabled())
                actionPerformed(new ActionEvent(btnLine_3_3, 0, null));
        } else if (tKeyCode == 113) {// F2为进货
            if (btnLine_3_4.isEnabled())
                actionPerformed(new ActionEvent(btnLine_3_4, 0, null));
        } else if (tKeyCode == 114) {// F3退货
            if (btnLine_3_5.isEnabled())
                actionPerformed(new ActionEvent(btnLine_3_5, 0, null));
        } else if (tKeyCode == 115) {// F4挂单
            if (btnLine_3_6.isEnabled())
                actionPerformed(new ActionEvent(btnLine_3_6, 0, null));
        } else if (tKeyCode == 116) {// F5统计
            if (btnLine_3_7.isEnabled())
                actionPerformed(new ActionEvent(btnLine_3_7, 0, null));
        }
        // else if(tKeyCode == 119){//F8改汇率
        // if(btnMRate.isEnabled())
        // actionPerformed(new ActionEvent(btnMRate, 0, null));
        // }else if(tKeyCode == 118){//F7改用户
        // if(btnMUser.isEnabled())
        // actionPerformed(new ActionEvent(btnMUser, 0, null));
        // }
        else if (tKeyCode == 117) {// F6系统设置
            if (btnLine_3_8.isEnabled())
                actionPerformed(new ActionEvent(btnLine_3_8, 0, null));
        } else if (tKeyCode == 27) {// ESC 表示取消对话盒区域内容，取消列表中项目，退出系统
            if (((JTextField) e.getSource()).getText().length() > 0)// 如果条码框或者产品名中有内容，就表示清空本条记录。
                resetDlgArea();
            else { // 如果是对话盒区域已经是空的了
                if (getUsedRowCount() != 0) { // 就看列表中是否有记录，有的话，F1就表示情况列表。
                    resetAll();
                } else
                    // 列表中也没有记录的话，就表示退出系统。提醒操作员盘点。
                    actionPerformed(new ActionEvent(btnLine_3_2, 0, null));
            }
        } else if (tKeyCode == 17) {// 按"ctrl"键使光标跳至count.必须先输入数量再输入产品的道理是，扫描枪有可能会带回车，使你没有机会后敲数量。
        } else if (tKeyCode == 38) {
            tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 40) {
            tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
            tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
        } else if (tKeyCode == 8 || tKeyCode == 127) {
            // @NOTE:当Del键或者BackSpace键被按时，如果条码框中没有内容，则直接进行list中记录的删除动作。
            int tRow = tblContent.getSelectedRow();
            if (tRow < 0 || tRow > getUsedRowCount() - 1) { // 没有选中行的话，看看最后一行是第几行，选中它。
                tblContent.setSelectedRow(getUsedRowCount() - 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
            } else { // 有选中行的话，将选中行删除，应收金额相应减少
                Float tPrice = Float.parseFloat((String) tblContent.getValueAt(tRow, 4));
                shouldReceive = shouldReceive - tPrice;
                for (int j = tRow; j < tblContent.getRowCount(); j++)
                    if (j == tblContent.getRowCount() - 1)
                        for (int i = 0; i < tblContent.getColumnCount(); i++)
                            tblContent.setValueAt(null, j, i);
                    else
                        for (int i = 0; i < tblContent.getColumnCount(); i++)
                            tblContent.setValueAt(tblContent.getValueAt(j + 1, i), j, i);
            }
        }
        // 一时想不起来为什么加这么个处理，宏姐说不方便，于是就先注释掉试试再说。
        else if (tKeyCode == ' ') {
            Object tOneKeyOpen = CustOpts.custOps.getValue(BarDlgConst.OneKeyOpen);
            if ("true".equals(tOneKeyOpen) || tOneKeyOpen == null) {
                Object tUniCommand = CustOpts.custOps.getValue(BarDlgConst.UniCommand);
                if ("true".equals(tUniCommand) || tUniCommand == null) {// 如果是采用通用命令（没有钱箱卡），则向端口写数据
                    openMoneyBox();
                } else { // 如果是Windows系统，则直接调用exe格式的开钱箱程序
                    String tSrcPath = tUniCommand.toString();
                    try {
                        Runtime.getRuntime().exec(tSrcPath);
                    } catch (Exception exp) {

                    }
                }
            }
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                }
            });
        }
    }

    @Override
    public void keyReleased(
            KeyEvent e) {
    }

    @Override
    public void keyTyped(
            KeyEvent e) {
    }

    private void resetColWidth() {
        for (int i = 0, len = header.length; i < len; i++) {
            PIMTableColumn tmpCol = tblContent.getColumnModel().getColumn(i);
            tmpCol.setWidth(i == 1 ? getWidth() * 3 / 5 - 243 : 60);
            tmpCol.setPreferredWidth(i == 1 ? getWidth() * 3 / 5 - 243 : 60);
        }
        tblContent.validate();
        tblContent.revalidate();
        tblContent.invalidate();
    }

    // 当对话盒区域内容填写完了以后，通过本方法启动一个线程，实现休眠500ms之后将对话盒区域内容增加到表中。
    // 同时更新tfdShoudReceive的内容。
    private void startWaitThread() {
        setStatusMes(BarDlgConst.NotePordNumber2);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    addContentToList(); // 将对话盒区域内容加入到列表中去。
                    shouldReceive += Float.parseFloat(tfdTotlePrice.getText());
                    // tfdShoudReceive.setText(decimalFormat.format(shouldReceive));
                    resetDlgArea(); // 对话和区域内容复位。
                } catch (Exception e) {
                }
            }
        });
    }

    // 将对话盒区域的内容加入到列表
    private void addContentToList() {
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
        tblContent.setValueAt(Integer.valueOf(prodID), tValidRowCount, 0); // set the code.
        tblContent.setValueAt("replace with real name", tValidRowCount, 1);// set the Name.
        tblContent.setValueAt("replace with real count", tValidRowCount, 2); // set the count.
        tblContent.setValueAt("replace with real count", tValidRowCount, 3); // set the price.
        tblContent.setValueAt(tfdTotlePrice.getText(), tValidRowCount, 4); // set the total price.

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
        lblRSQ = new JLabel(BarDlgConst.RST);
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
        
        //disables
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
        initCategoryAndMenus();
        initTable();
    }
    
    public void initCategoryAndMenus() {
        try {
            Connection connection = PIMDBModel.getConection();
            Statement statement =
                    connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        	//load all the products----------------------------
            ResultSet productRS = statement.executeQuery("select ID, SUBJECT from product where code = '' and deleted != true");
            productRS.afterLast();
            productRS.relative(-1);
            int tmpPos = productRS.getRow();
            prodIDAry = new int[tmpPos];
            prodSubjectAry = new String[tmpPos];
            productRS.beforeFirst();

            tmpPos = 0;
            while (productRS.next()) {
                prodIDAry[tmpPos] = productRS.getInt("ID");
                prodSubjectAry[tmpPos] = productRS.getString("SUBJECT");
                tmpPos++;
            }
            productRS.close();// 关闭
            
            //load all the categorys---------------------------
            ResultSet categoryRS = statement.executeQuery("select ID, NAME from CATEGORY order by DSP_INDEX");
            categoryRS.afterLast();
            categoryRS.relative(-1);
            tmpPos = categoryRS.getRow();
            categoryIdAry = new int[tmpPos];
            categorySubjectAry = new String[tmpPos];
            categoryRS.beforeFirst();

            tmpPos = 0;
            while (categoryRS.next()) {
            	categoryIdAry[tmpPos] = categoryRS.getInt("ID");
            	categorySubjectAry[tmpPos] = categoryRS.getString("NAME");
                tmpPos++;
            }
            categoryRS.close();// 关闭

        } catch (Exception e) {
            ErrorUtil.write(e);
        }
        reInitCategoryAndMenuBtns();
    }

    private void reInitCategoryAndMenuBtns(){
    	 // menu and category buttons must be init after initContent---------
        categoryColumn = (categoryColumn == null || categoryColumn < 4) ? 5 : categoryColumn;
        categoryRow = (categoryRow == null || categoryRow < 1 || categoryRow > 9) ? 3 : categoryRow;
        menuColumn = (menuColumn == null || menuColumn < 1) ? 4 : menuColumn;
        menuRow = (menuRow == null || menuRow < 1) ? 4 : menuRow;

        for (int r = 0; r < categoryRow; r++) {
        	if(r < categoryMatrix.size()) {
	            for (int c = 0; c < categoryColumn; c++) {
	            	if(c < categoryMatrix.get(r).size())
	            		remove(categoryMatrix.get(r).get(c));
	            }
        	}
        }

        for (int r = 0; r < menuRow; r++) {
        	if(r < menuMatrix.size()) {
	            for (int c = 0; c < menuColumn; c++) {
	            	if(c < menuMatrix.get(r).size())
	            		remove(menuMatrix.get(r).get(c));
	            }
        	}
        }
        categoryMatrix.clear();
        menuMatrix.clear();

        int dspIndex = 0;
        dspIndex = 0;
        for (int r = 0; r < categoryRow; r++) {
            ArrayList<CategoryToggle> btnCategoryArry = new ArrayList<CategoryToggle>();
            for (int c = 0; c < categoryColumn; c++) {
            	dspIndex++;
            	CategoryToggle btnCategory = new CategoryToggle(dspIndex);
                btnCategory.setMargin(new Insets(0, 0, 0, 0));
                add(btnCategory);
                btnCategory.addActionListener(this);
                btnCategoryArry.add(btnCategory);
                if(dspIndex <= categorySubjectAry.length) {
            		btnCategory.setText(categorySubjectAry[dspIndex - 1]);
            	}else {
            		btnPageDownCategory.setEnabled(false);
            	}
            }
            categoryMatrix.add(btnCategoryArry);
        }
        categoryNumPerPage = dspIndex;
        
        dspIndex = 0;
        for (int r = 0; r < menuRow; r++) {
            ArrayList<JButton> btnMenuArry = new ArrayList<JButton>();
            for (int c = 0; c < menuColumn; c++) {
            	dspIndex++;
                JButton btnMenu = new JButton("");
                btnMenu.setMargin(new Insets(0, 0, 0, 0));
                add(btnMenu);
                btnMenu.addActionListener(this);
                btnMenuArry.add(btnMenu);
                if(dspIndex <= prodSubjectAry.length) {
                	btnMenu.setText(prodSubjectAry[dspIndex - 1]);
                }else {
                	btnPageDownMenu.setEnabled(false);
                }
            }
            menuMatrix.add(btnMenuArry);
        }
        menuNumPerPage = dspIndex;
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
            	JToggleButton toggleButton = categoryMatrix.get(r).get(c);
            	toggleButton.setBounds(xMenuArea + (categeryBtnWidth + CustOpts.HOR_GAP) * c,
                                srpContent.getY() + (categeryBtnHeight + CustOpts.VER_GAP) * r, categeryBtnWidth,
                                categeryBtnHeight);
            }
        }
        btnPageUpCategory.setBounds(xMenuArea + widthMenuArea, srpContent.getY(), BarDlgConst.SCROLLBAR_WIDTH,
                BarDlgConst.SCROLLBAR_WIDTH * 2);
        btnPageDownCategory.setBounds(btnPageUpCategory.getX(),
                btnPageUpCategory.getY() + btnPageUpCategory.getHeight() + CustOpts.VER_GAP,
                BarDlgConst.SCROLLBAR_WIDTH, BarDlgConst.SCROLLBAR_WIDTH * 2);

        // menugory area--------------
        int menuY = srpContent.getY() + (categeryBtnHeight + CustOpts.VER_GAP) * categoryRow + CustOpts.VER_GAP;
        int menuBtnWidth = (widthMenuArea - CustOpts.HOR_GAP * (menuColumn - 1)) / menuColumn;
        int menuBtnHeight = (int) ((topAreaHeight * (1 - categoryHeight) - CustOpts.VER_GAP * (menuRow)) / menuRow);
        for (int r = 0; r < menuRow; r++) {
            for (int c = 0; c < menuColumn; c++) {
                menuMatrix
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

        resetColWidth();
    }

    public void cancelHangup(
            int pIdx) {
        // 先复位List中的内容
        Object[][] tValues = (Object[][]) hangupVec.get(pIdx);
        tblContent.setDataVector(tValues, header);

        resetColWidth();

        hangupVec.remove(pIdx);

        shouldReceive = 0;
        for (int i = 0; i < tValues.length; i++) {
            shouldReceive += Float.parseFloat((String) tValues[i][4]);
        }
    }

    private void hangup() {
        if (isOnProcess()) {// 如果正在收银,加入挂单库
            int tUsedRow = getUsedRowCount();
            int tColCount = tblContent.getColumnCount();
            Object[][] values = new Object[tUsedRow][tColCount];
            for (int i = 0; i < tUsedRow; i++)
                for (int j = 0; j < tColCount; j++)
                    values[i][j] = tblContent.getValueAt(i, j);
            hangupVec.add(values);
            resetAll();
        } else { // 如果不是正在收银，则显示挂单列表
            new HangupDlg(BarFrame.instance).setVisible(true);
        }
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
        shouldReceive = 0;
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

    private ArrayList<ArrayList<CategoryToggle>> categoryMatrix = new ArrayList<ArrayList<CategoryToggle>>();
    private ArrayList<ArrayList<JButton>> menuMatrix = new ArrayList<ArrayList<JButton>>();

    private PIMTable tblContent;
    private PIMScrollPane srpContent;

    public Vector hangupVec = new Vector();
    private int prodID;
    private float shouldReceive;
    private int[] prodIDAry;
    private String[] prodSubjectAry;
    private int[] categoryIdAry;
    private String[] categorySubjectAry;
    private String[] header = new String[] { BarDlgConst.ProdNumber, BarDlgConst.ProdName, BarDlgConst.Count,
            BarDlgConst.Price, BarDlgConst.Subtotal };
    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    
    //============================================================
    private class CategoryToggle extends JToggleButton{
    	int index = 0;
    	public CategoryToggle(int index) {
    		this.index = index;
    	}
    	public int getIndex() {
    		return index;
    	}
    }
}
