package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

public class PayDlg extends JDialog implements ActionListener, ComponentListener, WindowListener{
	
	private JToggleButton btnSource;
	//flag
	boolean isAllContentSelected;
	
	float originalReceived;
	
    public PayDlg(BarFrame pParent) {
        super(pParent, true);
        initComponent();
    }
    
    public void updateBill(int billId) {
    	StringBuilder sb = new StringBuilder();
    	
		String curTitle = getTitle();
		//calculate the received of diffent kind
		if(curTitle.equals(BarFrame.consts.EnterCashPayment())){
			String cashReceived = reflectNewInput(valCashReceived.getText());
			sb.append("update bill set cashReceived = ").append(cashReceived).append(" where id = ").append(billId);
		} else if(curTitle.equals(BarFrame.consts.EnterDebitPayment())) {
			String debitReceived = reflectNewInput(valDebitReceived.getText());
			sb.append("update bill set debitReceived = ").append(debitReceived).append(" where id = ").append(billId);
		} else if(curTitle.equals(BarFrame.consts.EnterVisaPayment())) {
			String visaReceived = reflectNewInput(valVisaReceived.getText());
			sb.append("update bill set visaReceived = ").append(visaReceived).append(" where id = ").append(billId);
		} else if(curTitle.equals(BarFrame.consts.EnterMasterPayment())) {
			String masterReceived = reflectNewInput(valMasterReceived.getText());
			sb.append("update bill set masterReceived = ").append(masterReceived).append(" where id = ").append(billId);
		}
		
		try {
			PIMDBModel.getStatement().executeUpdate(sb.toString());
		}catch(Exception e) {
			ErrorUtil.write(e);
		}
    }

	private String reflectNewInput(String existingAmount) {
		Float existingMoney = 0f;
		Float newAddedMoney = 0f;
		
		try {
			existingMoney = Float.valueOf(existingAmount);
		}catch(Exception e) {
			//do nothing.
		}
		
		try {
			newAddedMoney = Float.valueOf(tfdNewReceived.getText());
		}catch(Exception e) {
			//do nothing.
		}
		
		return String.valueOf((int)(existingMoney * 100 + newAddedMoney * 100));
	}
    
    public static void exactMoney(int billId, String pay) {
    	StringBuilder sb = new StringBuilder("update bill set " + pay + "Received = ")
    	.append((int)(Float.valueOf(valTotal.getText()) * 100))
    	//.append(", DebitReceived = 0, VisaReceived = 0, MasterReceived = 0")
    	.append(", status = -1 where id = ").append(billId);
   		try {
			PIMDBModel.getStatement().executeUpdate(sb.toString());
		}catch(Exception e) {
			ErrorUtil.write(e);
		}
   		if(SalesPanel.isLastBillOfCurTable())
			SalesPanel.resetCurTableDBStatus();
    }
    
	public void initContent(BillPanel billPanel) {
		int billId = billPanel.getBillId();
    	StringBuilder sb = new StringBuilder("select * from bill where id = " + billId);
    	try {
    		ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sb.toString());
            rs.next();
            Float total = Float.valueOf((float) (rs.getInt("total") / 100.0));
        	valTotal.setText(total.toString());
        	Float cashReceived = Float.valueOf((float) (rs.getInt("cashReceived") / 100.0));
            valCashReceived.setText(cashReceived.toString());
        	Float debitReceived = Float.valueOf((float) (rs.getInt("debitReceived") / 100.0));
            valDebitReceived.setText(debitReceived.toString());
            Float visaReceived = Float.valueOf((float) (rs.getInt("visaReceived") / 100.0));
            valVisaReceived.setText(visaReceived.toString());
            Float masterReceived = Float.valueOf((float) (rs.getInt("masterReceived") / 100.0));
            valMasterReceived.setText(masterReceived.toString());
            
            float left = ((int)((total * 100 - cashReceived * 100 - debitReceived * 100 - visaReceived * 100 - masterReceived * 100))) / 100f;
            valLeft.setText(String.valueOf(left));
            
//            if(BarFrame.consts.EnterCashPayment.equals(getTitle())) {
//                newReceived.setText(valCashReceived.getText());
//                valCashReceived.setVisible(false);
//            }else if(BarFrame.consts.EnterDebitPayment.equals(getTitle())) {
//                newReceived.setText(valDebitReceived.getText());
//                valDebitReceived.setVisible(false);
//            }else if(BarFrame.consts.EnterVisaPayment.equals(getTitle())) {
//                newReceived.setText(valVisaReceived.getText());
//                valVisaReceived.setVisible(false);
//            }else if(BarFrame.consts.EnterMasterPayment.equals(getTitle())) {
//                newReceived.setText(valMasterReceived.getText());
//                valMasterReceived.setVisible(false);
//            }
    	}catch(Exception e) {
    		ErrorUtil.write(e);
    	}
    }
    
    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    public void reLayout() {

    	lblCashReceived.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 
    			lblCashReceived.getPreferredSize().width, lblTotal.getPreferredSize().height);
        valCashReceived.setBounds(lblCashReceived.getX() + lblCashReceived.getWidth(), lblCashReceived.getY(),
        		80 - lblCashReceived.getWidth(), lblCashReceived.getHeight());

    	lblDebitReceived.setBounds(CustOpts.HOR_GAP, lblCashReceived.getY() + lblCashReceived.getHeight() + CustOpts.VER_GAP, 
    			lblDebitReceived.getPreferredSize().width, lblDebitReceived.getPreferredSize().height);
        valDebitReceived.setBounds(lblDebitReceived.getX() + lblDebitReceived.getWidth(), lblDebitReceived.getY(),
        		80 - lblDebitReceived.getWidth(), lblDebitReceived.getHeight());

    	lblVisaReceived.setBounds(lblCashReceived.getX() + 80 + CustOpts.HOR_GAP, CustOpts.VER_GAP, 
    			lblVisaReceived.getPreferredSize().width, lblVisaReceived.getPreferredSize().height);
        valVisaReceived.setBounds(lblVisaReceived.getX() + lblVisaReceived.getWidth(), lblVisaReceived.getY(),
        		80 - lblVisaReceived.getWidth(), lblVisaReceived.getHeight());

    	lblMasterReceived.setBounds(lblVisaReceived.getX(), lblDebitReceived.getY(), 
    			lblMasterReceived.getPreferredSize().width, lblMasterReceived.getPreferredSize().height);
        valMasterReceived.setBounds(lblMasterReceived.getX() + lblMasterReceived.getWidth(), lblMasterReceived.getY(),
        		100 - lblMasterReceived.getWidth(), lblMasterReceived.getHeight());
        
        tfdNewReceived.setBounds(lblMasterReceived.getX() + 100 + CustOpts.HOR_GAP, CustOpts.VER_GAP, 160, 40);
        
    	lblTotal.setBounds(tfdNewReceived.getX() + tfdNewReceived.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP,
    			lblTotal.getPreferredSize().width, lblTotal.getPreferredSize().height);
        valTotal.setBounds(lblTotal.getX() + lblTotal.getWidth(), lblTotal.getY(), 100 - lblTotal.getWidth(), lblTotal.getHeight());
        lblLeft.setBounds(lblTotal.getX(), lblMasterReceived.getY(), 
        		lblLeft.getPreferredSize().width, lblLeft.getPreferredSize().height);
        valLeft.setBounds(lblLeft.getX() + lblLeft.getWidth(), lblLeft.getY(), 100 - lblLeft.getWidth(), lblLeft.getHeight());
        
        btn10.setBounds(CustOpts.HOR_GAP,  tfdNewReceived.getY() + tfdNewReceived.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        btn20.setBounds(btn10.getX() + btn10.getWidth() + CustOpts.HOR_GAP, btn10.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        btn30.setBounds(btn10.getX(), btn10.getY() + btn10.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        btn40.setBounds(btn20.getX(), btn30.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        btn50.setBounds(btn30.getX(), btn30.getY() + btn30.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        btn100.setBounds(btn40.getX(), btn50.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        btnExact.setBounds(btn50.getX(), btn50.getY() + btn50.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM * 2 + CustOpts.HOR_GAP, CustOpts.BTN_WIDTH_NUM);
        
        num1.setBounds(btn20.getX() + btn20.getWidth() + CustOpts.HOR_GAP * 2,  tfdNewReceived.getY() + tfdNewReceived.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num2.setBounds(num1.getX() + num1.getWidth() + CustOpts.HOR_GAP, num1.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num3.setBounds(num2.getX() + num2.getWidth() + CustOpts.HOR_GAP, num1.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num4.setBounds(num1.getX(), num1.getY() + num1.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num5.setBounds(num4.getX() + num4.getWidth() + CustOpts.HOR_GAP, num4.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num6.setBounds(num5.getX() + num5.getWidth() + CustOpts.HOR_GAP, num5.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num7.setBounds(num4.getX(), num4.getY() + num1.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num8.setBounds(num7.getX() + num7.getWidth() + CustOpts.HOR_GAP, num7.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num9.setBounds(num8.getX() + num8.getWidth() + CustOpts.HOR_GAP, num8.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        num0.setBounds(num7.getX(), num7.getY() + num7.getHeight() + CustOpts.VER_GAP, CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        point.setBounds(num0.getX() + num0.getWidth() + CustOpts.HOR_GAP, num0.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        if(point.isVisible())
        	back.setBounds(point.getX() + point.getWidth() + CustOpts.HOR_GAP, point.getY(), CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM);
        else
        	back.setBounds(num0.getX() + num0.getWidth() + CustOpts.HOR_GAP, num0.getY(), CustOpts.BTN_WIDTH_NUM * 2 + CustOpts.HOR_GAP, CustOpts.BTN_WIDTH_NUM);
    	ok.setBounds(num3.getX() + num3.getWidth() + CustOpts.HOR_GAP, num3.getY(),
    			CustOpts.BTN_WIDTH_NUM, CustOpts.BTN_WIDTH_NUM * 4 + CustOpts.VER_GAP * 3);

        validate();
    }

    /**
     * Invoked when an action occurs. NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
     * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
     * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
     * 
     * @param e
     *            动作事件
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String curContent = tfdNewReceived.getText();
        
        Object o = e.getSource();
        if (o == ok) {
        	//check content format
        	try {
        		Float.valueOf(curContent);
        	}catch(Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
        		return;
        	}
        	
        	int billId = ((SalesPanel)BarFrame.instance.panels[2]).billPanel.getBillId();
        	boolean billClosed = false;
        	//check if left moeny is 0. 
        	int left = (int)(Float.valueOf(valLeft.getText()) * 100);
        	if( left > 0) {
	        	if(JOptionPane.showConfirmDialog(this, BarFrame.consts.reCeivedMoneyNotEnough(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) == 0) {
	        		//user selected to close the bill. update the bill to closed
	        		billClosed = closeCurrentBill();
	        		//if selected yes, then update the table status.
	        		if(SalesPanel.isLastBillOfCurTable()) {
	        			SalesPanel.resetCurTableDBStatus();
	        		}
	        	}
        	}else if(left <= 0) {
        		billClosed = closeCurrentBill();
            	this.setVisible(false);
            	if(left < 0) {	//if it's equal to 0, then do not display the change dialog.
            		new ChangeDlg(BarFrame.instance, BarOption.getMoneySign()
            				+ new DecimalFormat("#0.00").format((0 - left)/100f)).setVisible(true); //it's a non-modal dialog.
            		//if the last pay was with cash, then might need cash back (no change, paid with a "50" bill)
            		if(getTitle().equals(BarFrame.consts.EnterCashPayment())){
            			updateBill(billId, "cashback", left);
            		}else {
                		updateBill(billId, "TIP", 0 - left);	//otherwise, tread as tip.
            		}
            	}
        		if(SalesPanel.isLastBillOfCurTable()) {
        			SalesPanel.resetCurTableDBStatus();
        		}
        	}
        	//no matter it's closed or not, we need to update the pay info of the bill.
    		updateBill(billId);
        	resetContent();
        	this.setVisible(false);

        	PrintService.openDrawer();
        	//let's qa decide if we should go back to table interface.
        	if(left <= 0 || billClosed) {
        		BillPanel bp = ((SalesPanel)BarFrame.instance.panels[2]).billPanel;
        		PrintService.exePrintInvoice(bp, bp.orderedDishAry, getTitle().equals(BarFrame.consts.EnterCashPayment()));
        		BarFrame.instance.switchMode(0);
        	}
        	
        } else if(o == btnExact) {//update bill and display change 0.00;
        	String strPay = "other";
    		String curTitle = getTitle();
    		//calculate the received of diffent kind
    		if(curTitle.equals(BarFrame.consts.EnterCashPayment())){
    			strPay = "cash";
    		} else if(curTitle.equals(BarFrame.consts.EnterDebitPayment())) {
    			strPay = "debit";
    		} else if(curTitle.equals(BarFrame.consts.EnterVisaPayment())) {
    			strPay = "visa";
    		} else if(curTitle.equals(BarFrame.consts.EnterMasterPayment())) {
    			strPay = "master";
    		}
        	exactMoney(((SalesPanel)BarFrame.instance.panels[2]).billPanel.getBillId(), strPay);
        	resetContent();
        	this.setVisible(false);

    		BillPanel bp = ((SalesPanel)BarFrame.instance.panels[2]).billPanel;
    		PrintService.exePrintInvoice(bp, bp.orderedDishAry, true);
    		PrintService.openDrawer();
        	BarFrame.instance.switchMode(0);
        	
        } else {
	        if(isAllContentSelected)
	        	curContent = "";
	        
	        if (o == back) {
	            if (curContent != null && curContent.length() > 0) {
	                tfdNewReceived.setText(curContent.substring(0, curContent.length() - 1));
	                updateLeft();
	            }
	        } else if(o == btn10) {
	        	increaseReceived(10);
	        } else if(o == btn20) {
	        	increaseReceived(20);
	        } else if(o == btn30) {
	        	increaseReceived(30);
	        } else if(o == btn40) {
	        	increaseReceived(40);
	        } else if(o == btn50) {
	        	increaseReceived(50);
	        } else if(o == btn100) {
	        	increaseReceived(100);
	        } else if (o == num1) {
	        	concatReceived("1");
	        } else if (o == num2) {
	        	concatReceived("2");
	        } else if (o == num3) {
	        	concatReceived("3");
	        } else if (o == num4) {
	        	concatReceived("4");
	        } else if (o == num5) {
	        	concatReceived("5");
	        } else if (o == num6) {
	            tfdNewReceived.setText(curContent.concat("6"));
	            updateLeft();
	        } else if (o == num7) {
	            tfdNewReceived.setText(curContent.concat("7"));
	            updateLeft();
	        } else if (o == num8) {
	            tfdNewReceived.setText(curContent.concat("8"));
	            updateLeft();
	        } else if (o == num9) {
	            tfdNewReceived.setText(curContent.concat("9"));
	            updateLeft();
	        } else if (o == num0) {
	            tfdNewReceived.setText(curContent.concat("0"));
	            updateLeft();
	        } else if(o == point) {
	            tfdNewReceived.setText(curContent.concat("."));
	        }
        }
        isAllContentSelected = false;
    }

	public static void updateBill(int billId, String fieldName, int value) {
		StringBuilder sb = new StringBuilder("update bill set ").append(fieldName).append(" = ").append(value).append(" where id = ").append(billId);
		
		try {
			PIMDBModel.getStatement().executeUpdate(sb.toString());
		}catch(Exception e) {
			ErrorUtil.write(e);
		}
	}

	private boolean closeCurrentBill() {
		int billID = ((SalesPanel)BarFrame.instance.panels[2]).billPanel.getBillId();
		try {
			//do not modify the status of output. because it's a normal output, shouldn't be deleted.
			//String sql ="update output set deleted = 100 where category = " + billID;
			//PIMDBModel.getStatement().executeUpdate(sql);
			
			String sql = "update bill set status = -1 where id = " + billID;
			PIMDBModel.getStatement().executeUpdate(sql);
			return true;
		}catch(Exception exp) {
			L.e("PayDlg", "unexpected error occured whenn updating bill status.", exp);
		}
		return false;
	}
    
    private void increaseReceived(int amount) {
    	Float received = 0.0f;
    	try{
    		received = Float.valueOf(tfdNewReceived.getText());
    	}catch(Exception e) {}
    	received += amount;
    	tfdNewReceived.setText(String.valueOf(received));
        updateLeft();
    }
    
    private void concatReceived(String num){
        tfdNewReceived.setText(tfdNewReceived.getText().concat(num));
        updateLeft();
    }
    
	@Override
	public void componentResized(ComponentEvent e) {}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {
		if(btnSource != null)
			btnSource.setSelected(false);
	}

	public void updateLeft() {
		Float total = Float.valueOf(valTotal.getText());
		Float received = 0f;
		
		try{ 
			received += Float.valueOf(valCashReceived.getText()) + Float.valueOf(valDebitReceived.getText());
			received += Float.valueOf(valVisaReceived.getText()) + Float.valueOf(valMasterReceived.getText());
			received += Float.valueOf(tfdNewReceived.getText());
		}catch(Exception e) {}
		
		valLeft.setText(String.valueOf((int)(total * 100 - received * 100)/100f));
		BarFrame.customerFrame.updateChange(0 - (int)(total * 100 - received * 100));
	}
	
    void initComponent() {
    	getContentPane().removeAll();
    	
        setResizable(false);
        setModal(false);
        //setAlwaysOnTop(true);

        // 初始化－－－－－－－－－－－－－－－－

        lblCashReceived = new JLabel(BarFrame.consts.CASH() + " : " + BarOption.getMoneySign());
        valCashReceived = new JLabel("");
        lblDebitReceived = new JLabel(BarFrame.consts.DEBIT() + " : " + BarOption.getMoneySign());
        valDebitReceived = new JLabel("");
        lblVisaReceived = new JLabel(BarFrame.consts.VISA() + " : " + BarOption.getMoneySign());
        valVisaReceived = new JLabel("");
        lblMasterReceived = new JLabel(BarFrame.consts.MASTER() + " : " + BarOption.getMoneySign());
        valMasterReceived = new JLabel("");
        
        tfdNewReceived = new JTextField();
        
        lblTotal = new JLabel(BarFrame.consts.Total() + " : " + BarOption.getMoneySign());
        valTotal = new JLabel("");
        lblLeft = new JLabel(BarFrame.consts.Left() + " : " + BarOption.getMoneySign());
        valLeft = new JLabel("");
        
        ok = new JButton("<html><h1 style='text-align: center; padding-bottom: 5px; color:#18F507;'>✔</h1></html>");
        back = new JButton("←");
        
        btn10 = new JButton(BarOption.getMoneySign() + "10");
        btn20 = new JButton(BarOption.getMoneySign() + "20");
        btn30 = new JButton(BarOption.getMoneySign() + "30");
        btn40 = new JButton(BarOption.getMoneySign() + "40");
        btn50 = new JButton(BarOption.getMoneySign() + "50");
        btn100 = new JButton(BarOption.getMoneySign() + "100");
        btnExact = new JButton(BarFrame.consts.EXACT());
        
        num1 = new JButton("1");
        num2 = new JButton("2");
        num3 = new JButton("3");
        num4 = new JButton("4");
        num5 = new JButton("5");
        num6 = new JButton("6");
        num7 = new JButton("7");
        num8 = new JButton("8");
        num9 = new JButton("9");
        num0 = new JButton("0");
        point = new JButton(".");

        // 属性设置－－－－－－－－－－－－－－
        // ok.setFont(CustOpts.custOps.getFontOfDefault());
        back.setMargin(new Insets(0, 0, 0, 0));
        ok.setMargin(new Insets(0, 0, 0, 0));
        btn10.setMargin(back.getMargin());
        btn20.setMargin(back.getMargin());
        btn30.setMargin(back.getMargin());
        btn40.setMargin(back.getMargin());
        btn50.setMargin(back.getMargin());
        btn100.setMargin(back.getMargin());
        btnExact.setMargin(back.getMargin());
        
        num1.setMargin(back.getMargin());
        num2.setMargin(back.getMargin());
        num3.setMargin(back.getMargin());
        num4.setMargin(back.getMargin());
        num5.setMargin(back.getMargin());
        num6.setMargin(back.getMargin());
        num7.setMargin(back.getMargin());
        num8.setMargin(back.getMargin());
        num9.setMargin(back.getMargin());
        num0.setMargin(back.getMargin());
        point.setMargin(back.getMargin());
        
        tfdNewReceived.setFont(BarOption.bigFont);
        tfdNewReceived.setBackground(Color.WHITE);
        
        
        // 布局---------------
        int tHight = CustOpts.BTN_HEIGHT + CustOpts.BTN_WIDTH_NUM * 4 + 5 * CustOpts.VER_GAP
                        + CustOpts.SIZE_EDGE + CustOpts.SIZE_TITLE + 20;
        int tWidth = (CustOpts.BTN_WIDTH_NUM + CustOpts.HOR_GAP) * 6 + CustOpts.SIZE_EDGE * 2 + CustOpts.HOR_GAP + 5;
        setBounds((CustOpts.SCRWIDTH - tWidth) / 2, (CustOpts.SCRHEIGHT - tHight) / 2, tWidth, tHight); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        reLayout();
        
        // 搭建－－－－－－－－－－－－－
        getContentPane().add(lblCashReceived);
        getContentPane().add(valCashReceived);
        getContentPane().add(lblDebitReceived);
        getContentPane().add(valDebitReceived);
        getContentPane().add(lblVisaReceived);
        getContentPane().add(valVisaReceived);
        getContentPane().add(lblMasterReceived);
        getContentPane().add(valMasterReceived);
        
        getContentPane().add(tfdNewReceived);
        
        getContentPane().add(lblTotal);
        getContentPane().add(valTotal);
        getContentPane().add(lblLeft);
        getContentPane().add(valLeft);
        
        getContentPane().add(ok);
        getContentPane().add(back);

        getContentPane().add(btn10);
        getContentPane().add(btn20);
        getContentPane().add(btn30);
        getContentPane().add(btn40);
        getContentPane().add(btn50);
        getContentPane().add(btn100);
        getContentPane().add(btnExact);
        
        getContentPane().add(num1);
        getContentPane().add(num2);
        getContentPane().add(num3);
        getContentPane().add(num4);
        getContentPane().add(num5);
        getContentPane().add(num6);
        getContentPane().add(num7);
        getContentPane().add(num8);
        getContentPane().add(num9);
        getContentPane().add(num0);
        getContentPane().add(point);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        back.addActionListener(this);
        num1.addActionListener(this);
        num2.addActionListener(this);
        num3.addActionListener(this);
        num4.addActionListener(this);
        num5.addActionListener(this);
        num6.addActionListener(this);
        num7.addActionListener(this);
        num8.addActionListener(this);
        num9.addActionListener(this);
        num0.addActionListener(this);
        point.addActionListener(this);

        btn10.addActionListener(this);
        btn20.addActionListener(this);
        btn30.addActionListener(this);
        btn40.addActionListener(this);
        btn50.addActionListener(this);
        btn100.addActionListener(this);
        btnExact.addActionListener(this);
        addComponentListener(this);
        addWindowListener(this);
        
        // init Contents
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                tfdNewReceived.grabFocus();
            }
        });
        
        reLayout();
    }

	public void setFloatSupport(boolean setFloatSupport) {
		point.setVisible(setFloatSupport);
	}
	
	private JButton num1;
    private JButton num2;
    private JButton num3;
    private JButton num4;
    private JButton num5;
    private JButton num6;
    private JButton num7;
    private JButton num8;
    private JButton num9;
    private JButton num0;
    private JButton point;
    private JButton back;
    private JButton ok;

    private JButton btn10;
    private JButton btn20;
    private JButton btn30;
    private JButton btn40;
    private JButton btn50;
    private JButton btn100;
    private JButton btnExact;

    
    private JLabel lblCashReceived;
    public JLabel valCashReceived;
    private JLabel lblDebitReceived;
    public JLabel valDebitReceived;
    private JLabel lblVisaReceived;
    public JLabel valVisaReceived;
    private JLabel lblMasterReceived;
    public JLabel valMasterReceived;

    public JTextField tfdNewReceived;

    private JLabel lblTotal;
    public static JLabel valTotal;
    private JLabel lblLeft;
    public JLabel valLeft;

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {
		if(tfdNewReceived.getText().length() > 0)
			JOptionPane.showMessageDialog(this, BarFrame.consts.UnSavedContentWillBeLost());
		
		resetContent();
	}

	private void resetContent() {
		valCashReceived.setText("0.0");
		valDebitReceived.setText("0.0");
		valVisaReceived.setText("0.0");
		valMasterReceived.setText("0.0");
		
		valTotal.setText("0.0");
		valLeft.setText("0.0");
		
		tfdNewReceived.setText("");
	}
	
	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
