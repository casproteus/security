package org.cas.client.platform.bar.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pos.dialog.modifyuser.MUserSwichDlg;
import org.cas.client.platform.pos.dialog.modifyuser.ModifyPasswordDlg;
import org.cas.client.resource.international.DlgConst;

import com.jpos.POStest.POStestGUI;

public class BarOption extends JDialog implements ICASDialog, ActionListener, ComponentListener {
	public static final int MaxQTY = 10000;
	public static Font bigFont = new Font("Arial", Font.PLAIN, 48);
	
	public static final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	public static void setBK(Color color, String key) {
		StringBuilder sb = new StringBuilder();
		sb.append(color.getRed()).append(",").append(color.getGreen()).append(",").append(color.getBlue());
		CustOpts.custOps.setKeyAndValue(key, sb.toString());
	}
	
	public static Color getBK(String key) {
		String colorStr = (String)CustOpts.custOps.getValue(key);
		if(colorStr != null) {
			try {
				String[] colors = colorStr.split(",");
				return new Color(Integer.valueOf(colors[0]), Integer.valueOf(colors[1]), Integer.valueOf(colors[2]));
			}catch(Exception e) {
				return null;
			}
		}
		return null;
	}

    public static String getOwnerInfo() {
    	StringBuilder sb = new StringBuilder(BarOption.getStoreName()).append("\n");
    	sb.append(BarOption.getAddress()).append("\n");
    	sb.append(BarOption.getCityAndProvince()).append("\n");
    	sb.append(BarOption.getPostCode()).append("\n");
    	sb.append(BarOption.getTel()).append("\n");
    	sb.append(BarOption.getWebSite()).append("\n");
    	return sb.toString();
    }

    private static Object getWebSite() {
		// TODO Auto-generated method stub
		return "www.sharethegoodones.com/nudo";
	}

	private static Object getTel() {
		// TODO Auto-generated method stub
		return "514 552 5771";
	}

	private static Object getPostCode() {
		// TODO Auto-generated method stub
		return "H4L 3E8";
	}

	private static Object getCityAndProvince() {
		// TODO Auto-generated method stub
		return "Montreal, Quebec";
	}

	private static Object getAddress() {
		// TODO Auto-generated method stub
		return "75 cardinal";
	}

	public static String getStoreName() {
		// TODO Auto-generated method stub
		return "NUDO";
	}
//	public static void setMaxRooBackWorkHour(int workhour) {
//		CustOpts.custOps.setKeyAndValue("MaxRooBackWorkHour", workhour);
//	}
//	public static int getMaxRollBackWorkHour() {
//		return CustOpts.custOps.getValue("MaxRooBackWorkHour") == null ? 
//				17 * 60 * 60 * 1000 : Integer.valueOf((String)CustOpts.custOps.getValue("MaxRooBackWorkHour"));
//	}
	
//	public static boolean isPrintBillWhenPay() {
//    	return CustOpts.custOps.getValue("isPrintBillWhenPay") == null ? 
//    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isPrintBillWhenPay"));
//    }
//    public static void setIsPrintBillWhenPay(Boolean isPrintBillWhenPay) {
//    	CustOpts.custOps.setKeyAndValue("isPrintBillWhenPay", isPrintBillWhenPay);
//    }
	
	public static boolean isDisDishIDInKitchen(){
		return CustOpts.custOps.getValue("isDisDishIDInKitchen") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isDisDishIDInKitchen"));
	}
	public static void setIsDisplayBillInKitchen(boolean isDisDishIDInKitchen){
		CustOpts.custOps.setKeyAndValue("isDisDishIDInKitchen", isDisDishIDInKitchen);
	}

	public static boolean isDisplayBillInKitchen(){
		return CustOpts.custOps.getValue("isDisplayBillInKitchen") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isDisplayBillInKitchen"));
	}
	public static void setDisplayBillInKitchen(boolean displayBillInKitchen){
		CustOpts.custOps.setKeyAndValue("displayBillInKitchen", displayBillInKitchen);
	}

	public static boolean isDoNotDisplayWaiterInKitchen(){
		return CustOpts.custOps.getValue("isDoNotDisplayWaiterInKitchen") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isDoNotDisplayWaiterInKitchen"));
	}
	
	public static void setDoNotDisplayWaiterInKitchen(boolean isDoNotDisplayWaiterInKitchen){
		CustOpts.custOps.setKeyAndValue("isDoNotDisplayWaiterInKitchen", isDoNotDisplayWaiterInKitchen);
	}
	
    public static String getMoneySign() {
    	return CustOpts.custOps.getValue("moneySign") == null ? 
    			"$" : String.valueOf(CustOpts.custOps.getValue("moneySign"));
    }
    public static void setMoneySign(String moneySign) {
    	CustOpts.custOps.setKeyAndValue("moneySign", moneySign);
    }
	    
    public static boolean isDisCountBeforeTax() {
    	return CustOpts.custOps.getValue("isDisCountBeforeTax") == null ? 
    			false : Boolean.valueOf((String)CustOpts.custOps.getValue("isDisCountBeforeTax"));
    }
    public static void setIsDisCountBeforeTax(Boolean isDisCountBeforeTax) {
    	CustOpts.custOps.setKeyAndValue("isDisCountBeforeTax", isDisCountBeforeTax);
    }
    
    public static int getBillPageRow() {
    	return CustOpts.custOps.getValue("BillPanel_Row") == null ? 
    			1 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Row"));
    }
    public static void setBillPageRow(String row) {
    	CustOpts.custOps.setKeyAndValue("BillPanel_Row", row);
    }
    
    public static int getBillPageCol() {
    	return CustOpts.custOps.getValue("BillPanel_Col") == null ? 
    			4 : Integer.valueOf((String)CustOpts.custOps.getValue("BillPanel_Col"));
    }
    public static void setBillPageCol(String col) {
    	CustOpts.custOps.setKeyAndValue("BillPanel_Col", col);
    }
    
    public static int getCategoryRow() {
    	return CustOpts.custOps.getValue("Category_Row") == null ? 
    			3 : Integer.valueOf((String)CustOpts.custOps.getValue("Category_Row"));
    }
    public static void setCategoryRow(String row) {
    	CustOpts.custOps.setKeyAndValue("Category_Row", row);
    }
    
    public static int getCategoryCol() {
    	return CustOpts.custOps.getValue("Category_Col") == null ? 
    			5 : Integer.valueOf((String)CustOpts.custOps.getValue("Category_Col"));
    }
    public static void setCategoryCol(String col) {
    	CustOpts.custOps.setKeyAndValue("Category_Col", col);
    }
    
    public static int getDishRow() {
    	return CustOpts.custOps.getValue("Dish_Row") == null ? 
    			4 : Integer.valueOf((String)CustOpts.custOps.getValue("Dish_Row"));
    }
    public static void setDishRow(String row) {
    	CustOpts.custOps.setKeyAndValue("Dish_Row", row);
    }
    
    public static int getDishCol() {
    	return CustOpts.custOps.getValue("Dish_Col") == null ? 
    			4 : Integer.valueOf((String)CustOpts.custOps.getValue("Dish_Col"));
    }
    public static void setDishCol(String col) {
    	CustOpts.custOps.setKeyAndValue("Dish_Col", col);
    }

    public static boolean isSingleUser() {
    	return "true".equals(CustOpts.custOps.getValue("SingleUserMode"));
    }
    public static void setSingleUser(String is) {
    	CustOpts.custOps.setKeyAndValue("SingleUserMode", is);
    }
    
    public static void setFastFoodMode(boolean isFastFoodMode) {
    	CustOpts.custOps.setKeyAndValue("FastFoodMode", isFastFoodMode);
    }
    public static boolean isFastFoodMode() {
    	return CustOpts.custOps.getValue("FastFoodMode") == null ? 
    			false : (Boolean)CustOpts.custOps.getValue("FastFoodMode");
    }
    
    public static void setStartTime(String startTimeOfDay) {
    	CustOpts.custOps.setKeyAndValue("StartTimeOfDay", startTimeOfDay);
    }
    public static String getStartTime() {
    	return CustOpts.custOps.getValue("StartTimeOfDay") == null ? 
    			"00:00:00" : String.valueOf(CustOpts.custOps.getValue("StartTimeOfDay"));
    }
    
    public BarOption(JFrame pFrame) {
        super(pFrame, false);
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        lblProdCodeLength.setBounds(CustOpts.HOR_GAP * 2, CustOpts.VER_GAP, lblProdCodeLength.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdEncodeStyle.setBounds(
                lblProdCodeLength.getX() + lblProdCodeLength.getWidth(),
                lblProdCodeLength.getY(),
                getWidth() / 2 - CustOpts.HOR_GAP - CustOpts.SIZE_EDGE - lblProdCodeLength.getX()
                        - lblProdCodeLength.getWidth(), CustOpts.BTN_HEIGHT);
        lblFontSize.setBounds(tfdEncodeStyle.getX() + tfdEncodeStyle.getWidth() + CustOpts.HOR_GAP,
                lblProdCodeLength.getY(), lblFontSize.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdFontSize.setBounds(lblFontSize.getX() + lblFontSize.getWidth(), lblFontSize.getY(), getWidth()
                - CustOpts.HOR_GAP * 2 - CustOpts.SIZE_EDGE - lblFontSize.getX() - lblFontSize.getWidth(),
                CustOpts.BTN_HEIGHT);

        sptMoneyBox
                .setBounds(CustOpts.HOR_GAP, lblProdCodeLength.getY() + lblProdCodeLength.getHeight()
                        + CustOpts.VER_GAP, getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2,
                        CustOpts.SEP_HEIGHT + 2);
        checbox.setBounds(lblProdCodeLength.getX(), sptMoneyBox.getY() + sptMoneyBox.getHeight() + CustOpts.VER_GAP,
                checbox.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        btnOpenCmd.setBounds(lblFontSize.getX(), checbox.getY(), btnOpenCmd.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdOpenCmd.setBounds(btnOpenCmd.getX() + btnOpenCmd.getWidth() + CustOpts.HOR_GAP, btnOpenCmd.getY(),
                getWidth() - CustOpts.HOR_GAP * 3 - CustOpts.SIZE_EDGE - btnOpenCmd.getX() - btnOpenCmd.getWidth(),
                CustOpts.BTN_HEIGHT);
        cbxUseMoneyBox.setBounds(checbox.getX(), checbox.getY() + checbox.getHeight() + CustOpts.VER_GAP,
                cbxUseMoneyBox.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxOneKeyOpenBox.setBounds(btnOpenCmd.getX(), cbxUseMoneyBox.getY(), cbxOneKeyOpenBox.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);

        sptPrint.setBounds(sptMoneyBox.getX(), cbxOneKeyOpenBox.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sptMoneyBox.getWidth(), CustOpts.SEP_HEIGHT + 2);
        cbxUsePrinter.setBounds(cbxUseMoneyBox.getX(), sptPrint.getY() + sptPrint.getHeight() + CustOpts.VER_GAP,
                cbxUsePrinter.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        lblTiltle.setBounds(cbxOneKeyOpenBox.getX(), cbxUsePrinter.getY(), lblTiltle.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdTitle.setBounds(lblTiltle.getX() + lblTiltle.getWidth() + CustOpts.HOR_GAP, lblTiltle.getY(),
                cbxOneKeyOpenBox.getX() + cbxOneKeyOpenBox.getWidth() - lblTiltle.getX()
                        - lblTiltle.getPreferredSize().width - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        lblThankWord.setBounds(cbxOneKeyOpenBox.getX(), lblTiltle.getY() + lblTiltle.getHeight() + CustOpts.VER_GAP,
                lblThankWord.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdThankWord.setBounds(
                lblThankWord.getX() + lblThankWord.getWidth() + CustOpts.HOR_GAP,
                lblThankWord.getY(),
                cbxOneKeyOpenBox.getX() + cbxOneKeyOpenBox.getWidth() - lblThankWord.getX()
                        - lblThankWord.getPreferredSize().width - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);

        sptOther.setBounds(sptPrint.getX(), tfdThankWord.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sptPrint.getWidth(), CustOpts.SEP_HEIGHT + 2);
        btnMRate.setBounds(cbxUsePrinter.getX(), sptOther.getY() + sptOther.getHeight() + CustOpts.VER_GAP,
                lblProdCodeLength.getWidth() + tfdEncodeStyle.getWidth(), CustOpts.BTN_HEIGHT);
        btnMUser.setBounds(lblThankWord.getX(), btnMRate.getY(), lblFontSize.getWidth() + tfdFontSize.getWidth(),
                CustOpts.BTN_HEIGHT);
        btnDspServer.setBounds(btnMRate.getX(), btnMRate.getY() + btnMRate.getHeight() + CustOpts.VER_GAP,
                btnMRate.getWidth(), CustOpts.BTN_HEIGHT);
        btnDspSuperTool.setBounds(btnDspServer.getX(), btnDspServer.getY() + btnDspServer.getHeight()
                + CustOpts.VER_GAP, btnDspServer.getWidth(), CustOpts.BTN_HEIGHT);
        btnDspPrintTool.setBounds(btnMUser.getX(), btnDspServer.getY(),
                lblFontSize.getWidth() + tfdFontSize.getWidth(), CustOpts.BTN_HEIGHT);

        ok.setBounds(btnDspPrintTool.getX(), btnDspPrintTool.getY() + btnDspPrintTool.getHeight() + CustOpts.VER_GAP,
                btnDspPrintTool.getWidth(), CustOpts.BTN_HEIGHT);
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
        btnDspServer.removeActionListener(this);
        btnDspSuperTool.removeActionListener(this);
        btnDspPrintTool.removeActionListener(this);
        ok.removeActionListener(this);
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
        if (o == btnDspServer) {
            CASControl.ctrl.showMainFrame(); // it will show login dialog first.
        } else if (o == this.btnDspSuperTool) {
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
        } else if (o == btnDspPrintTool) {
//            new PrintDebuggerDlg(BarFrame.instance).setVisible(true);
        } else if (o == ok) {
            int tSize = 12;
            try {
                tSize = Integer.parseInt(tfdFontSize.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdFontSize.grabFocus();
                tfdFontSize.selectAll();
                return;
            }
//            CustOpts.custOps.setKeyAndValue(BarFrame.consts.EncodeStyle, tfdEncodeStyle.getText());
//
//            CustOpts.custOps.setKeyAndValue(BarFrame.consts.UniCommand,
//                    checbox.isSelected() ? "true" : tfdOpenCmd.getText());
//            CustOpts.custOps.setKeyAndValue(BarFrame.consts.UseMoenyBox, cbxUseMoneyBox.isSelected() ? "true" : "false");
//            CustOpts.custOps.setKeyAndValue(BarFrame.consts.OneKeyOpen, cbxOneKeyOpenBox.isSelected() ? "true" : "false");
//
//            CustOpts.custOps.setKeyAndValue(BarFrame.consts.UsePrinter, cbxUsePrinter.isSelected() ? "true" : "false");
//            CustOpts.custOps.setKeyAndValue(BarFrame.consts.PrintTitle, tfdTitle.getText());
//            CustOpts.custOps.setKeyAndValue(BarFrame.consts.Thankword, tfdThankWord.getText());

            CustOpts.custOps.setFontSize(tSize);
            dispose();
        } else if (o == checbox) {
            btnOpenCmd.setEnabled(!checbox.isSelected());
            tfdOpenCmd.setEnabled(!checbox.isSelected());
        } else if (o == cbxUseMoneyBox) {
            // 开个线程检查是否有连接。
        } else if (o == cbxOneKeyOpenBox) {
            // 开个线程检查是否有连接。
        } else if (o == cbxUsePrinter) {
            // 开个线程检查是否有连接。
        } else if (o == btnOpenCmd) {
            JFileChooser tmpFileChooser = new JFileChooser();
            tmpFileChooser.setAcceptAllFileFilterUsed(true);
            if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
                return;
            File tCmdFile = tmpFileChooser.getSelectedFile();
            if (tCmdFile != null) {
                tfdOpenCmd.setText(tCmdFile.getAbsolutePath());
            }
        } else if (o == btnMRate) {
//            new ChangeRateDlg(BarFrame.instance).setVisible(true);
        } else if (o == btnMUser) {
            int tType = Integer.parseInt(CustOpts.custOps.getUserType());
            if (tType > 0) {// 如果当前登陆用户是个普通员工，则显示普通登陆对话盒。等待再次登陆
                new LoginDlg(BarFrame.instance).setVisible(true);// 结果不会被保存到ini
                if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
                    if (LoginDlg.USERTYPE < 2) {// 进一步判断，如果新登陆不是为经理，弹出该密码对话盒（显示一个只有名字，密码，验证三项的LoginDlg）。
                        new ModifyPasswordDlg(BarFrame.instance, LoginDlg.USERNAME).setVisible(true);
                    } else
                        // 如果新登陆为经理，则弹出SwitchDlg，
                        new MUserSwichDlg(BarFrame.instance).setVisible(true);// 否则显示一个SwitchDlg
                }
            } else
                // 如果当前的用户已经是管理员了，则弹出对话盒询问：添加？修改？还是删除？
                new MUserSwichDlg(BarFrame.instance).setVisible(true);
        }
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
//        setTitle(BarFrame.consts.Option);
//        setResizable(false);
//        // 初始化－－－－－－－－－－－－－－－－
//        lblProdCodeLength = new JLabel(BarFrame.consts.EncodeStyle);
//        tfdEncodeStyle = new JTextField((String) CustOpts.custOps.getValue(BarFrame.consts.EncodeStyle));
//        lblFontSize = new JLabel(BarFrame.consts.Size);
//        tfdFontSize = new JTextField(String.valueOf(CustOpts.custOps.getFontSize()));
//        checbox = new JCheckBox(BarFrame.consts.UniCommand);
//        btnOpenCmd = new JButton(BarFrame.consts.SpecialCommand);
//        tfdOpenCmd = new JTextField();
//
//        sptMoneyBox = new PIMSeparator(BarFrame.consts.MONEYBOX);
//        cbxUseMoneyBox = new JCheckBox(BarFrame.consts.UseMoenyBox);
//        cbxOneKeyOpenBox = new JCheckBox(BarFrame.consts.OneKeyOpen);
//        cbxUsePrinter = new JCheckBox(BarFrame.consts.UsePrinter);
//
//        sptPrint = new PIMSeparator(BarFrame.consts.PrinterSupport);
//        lblTiltle = new JLabel(BarFrame.consts.PrintTitle);
//        tfdTitle = new JTextField();
//        lblThankWord = new JLabel(BarFrame.consts.Thankword);
//        tfdThankWord = new JTextField();
//
//        sptOther = new PIMSeparator(OptionDlgConst.OPTION_OTHER);
//        btnDspServer = new JButton(BarFrame.consts.DspServer);
//        btnDspSuperTool = new JButton(BarFrame.consts.DspSuperTool);
//        btnDspPrintTool = new JButton(BarFrame.consts.DspPrintTool);
//
//        btnMUser = new JButton(BarFrame.consts.MUser);
//        btnMRate = new JButton(BarFrame.consts.MRate);

        ok = new JButton(DlgConst.OK);

        // 属性设置－－－－－－－－－－－－－－
        ok.setMnemonic('o');
        btnDspServer.setMargin(new Insets(0, 0, 0, 0));
        btnOpenCmd.setMargin(btnDspServer.getMargin());
        btnDspSuperTool.setMargin(btnDspServer.getMargin());
        btnDspPrintTool.setMargin(btnDspSuperTool.getMargin());
        btnMUser.setMargin(btnDspServer.getInsets());
        btnMRate.setMargin(btnDspServer.getInsets());
        ok.setMargin(btnDspServer.getMargin());

        setBounds((CustOpts.SCRWIDTH - 280) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 280, 320); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        getRootPane().setDefaultButton(ok);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(lblProdCodeLength);
        getContentPane().add(tfdEncodeStyle);
        getContentPane().add(lblFontSize);
        getContentPane().add(tfdFontSize);
        getContentPane().add(sptMoneyBox);
        getContentPane().add(checbox);
        getContentPane().add(btnOpenCmd);
        getContentPane().add(tfdOpenCmd);
        getContentPane().add(cbxUseMoneyBox);
        getContentPane().add(cbxOneKeyOpenBox);
        getContentPane().add(sptPrint);
        getContentPane().add(cbxUsePrinter);
        getContentPane().add(lblTiltle);
        getContentPane().add(tfdTitle);
        getContentPane().add(lblThankWord);
        getContentPane().add(tfdThankWord);

        getContentPane().add(sptOther);
        getContentPane().add(btnDspServer);
        // getContentPane().add(btnDspSuperTool);
        getContentPane().add(btnDspPrintTool);

        getContentPane().add(btnMUser);
        getContentPane().add(btnMRate);

        getContentPane().add(ok);

        // 加监听器－－－－－－－－
        btnDspServer.addActionListener(this);
        btnDspSuperTool.addActionListener(this);
        btnDspPrintTool.addActionListener(this);
        ok.addActionListener(this);
        checbox.addActionListener(this);
        cbxUseMoneyBox.addActionListener(this);
        cbxOneKeyOpenBox.addActionListener(this);
        btnOpenCmd.addActionListener(this);
        btnMUser.addActionListener(this);
        btnMRate.addActionListener(this);
        getContentPane().addComponentListener(this);

        // Content
//        Object tIsUniOpenCmd = CustOpts.custOps.getValue(BarFrame.consts.UniCommand);
//        checbox.setSelected(tIsUniOpenCmd == null || tIsUniOpenCmd.equals("true"));
//        if (!checbox.isSelected())
//            tfdOpenCmd.setText(tIsUniOpenCmd.toString());
//        else {
//            btnOpenCmd.setEnabled(false);
//            tfdOpenCmd.setEnabled(false);
//        }
//        Object tUseMoneyBox = CustOpts.custOps.getValue(BarFrame.consts.UseMoenyBox);
//        cbxUseMoneyBox.setSelected(tUseMoneyBox == null || tUseMoneyBox.equals("true"));
//        Object tOneKeyOpenBox = CustOpts.custOps.getValue(BarFrame.consts.OneKeyOpen);
//        cbxOneKeyOpenBox.setSelected(tOneKeyOpenBox == null || tOneKeyOpenBox.equals("true"));
//        Object tUsePrinter = CustOpts.custOps.getValue(BarFrame.consts.UsePrinter);
//        cbxUsePrinter.setSelected(tUsePrinter == null || tUsePrinter.equals("true"));
//
//        tfdTitle.setText((String) CustOpts.custOps.getValue(BarFrame.consts.PrintTitle));
//        tfdThankWord.setText((String) CustOpts.custOps.getValue(BarFrame.consts.Thankword));
    }

    private PIMSeparator sptMoneyBox;
    private PIMSeparator sptPrint;
    private PIMSeparator sptOther;
    private JLabel lblProdCodeLength;
    private JTextField tfdEncodeStyle;
    private JLabel lblFontSize;
    private JTextField tfdFontSize;
    private JCheckBox checbox;
    private JButton btnOpenCmd;
    private JTextField tfdOpenCmd;
    private JCheckBox cbxUseMoneyBox;
    private JCheckBox cbxOneKeyOpenBox;
    private JCheckBox cbxUsePrinter;
    private JLabel lblTiltle;
    private JTextField tfdTitle;
    private JLabel lblThankWord;
    private JTextField tfdThankWord;
    private JButton btnDspServer;
    private JButton btnDspSuperTool;
    private JButton btnDspPrintTool;
    private JButton btnMRate;
    private JButton btnMUser;
    private JButton ok;
}
