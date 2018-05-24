package org.cas.client.platform.bar.dialog.statistics;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pos.dialog.CheckStoreDlg;
import org.cas.client.platform.pos.dialog.PosDlgConst;
import org.cas.client.resource.international.DlgConst;

public class Statistic  extends JDialog implements ICASDialog, ActionListener, ComponentListener{

	public Statistic(JFrame pFrame){
		super(pFrame, true);
		parent = pFrame;
		initDialog();
	}
	
	public void reLayout(){
		int tWidth = 80;
		sptInfo.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
				getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2, CustOpts.SEP_HEIGHT + 2);
		btnProduct.setBounds(sptInfo.getX() + CustOpts.HOR_GAP, sptInfo.getY() + sptInfo.getHeight() + CustOpts.VER_GAP,
				tWidth, CustOpts.BTN_HEIGHT);
		btnCustomer.setBounds(btnProduct.getX() + btnProduct.getWidth() + CustOpts.HOR_GAP, btnProduct.getY(),
				tWidth, CustOpts.BTN_HEIGHT);
		btnSupplier.setBounds(btnCustomer.getX() + btnCustomer.getWidth() + CustOpts.HOR_GAP, btnCustomer.getY(),
				tWidth, CustOpts.BTN_HEIGHT);
		btnEmployee.setBounds(btnSupplier.getX() + btnSupplier.getWidth() + CustOpts.HOR_GAP, btnSupplier.getY(),
				tWidth, CustOpts.BTN_HEIGHT);
		
		sptRec.setBounds(sptInfo.getX(), btnProduct.getY() + btnProduct.getHeight() + CustOpts.VER_GAP,
				sptInfo.getWidth(), CustOpts.SEP_HEIGHT + 2);
		btnSale.setBounds(sptRec.getX() + CustOpts.HOR_GAP, sptRec.getY() + sptRec.getHeight() + CustOpts.VER_GAP,
				tWidth, CustOpts.BTN_HEIGHT);
		btnInput.setBounds(btnSale.getX() + btnSale.getWidth() + CustOpts.HOR_GAP, btnSale.getY(),
				tWidth, CustOpts.BTN_HEIGHT);
		btnRefund.setBounds(btnInput.getX() + btnInput.getWidth() + CustOpts.HOR_GAP, btnInput.getY(),
				tWidth, CustOpts.BTN_HEIGHT);
		btnEvaluation.setBounds(btnRefund.getX() + btnRefund.getWidth() + CustOpts.HOR_GAP, btnRefund.getY(),
				tWidth, CustOpts.BTN_HEIGHT);
		
		sptProf.setBounds(sptRec.getX(), btnSale.getY() + btnSale.getHeight() + CustOpts.VER_GAP,
				sptRec.getWidth(), CustOpts.SEP_HEIGHT + 2);
		btnProfit.setBounds(sptProf.getX() + CustOpts.HOR_GAP, sptProf.getY() + sptProf.getHeight() + CustOpts.VER_GAP,
				btnProfit.getPreferredSize().width, CustOpts.BTN_HEIGHT);
		cancel.setBounds(getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH, 
				btnProfit.getY(),CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

    	validate();
	}
	public PIMRecord getContents(){return null;}
	public boolean setContents(PIMRecord prmRecord){return true;}
	public void makeBestUseOfTime(){}
	public void addAttach(File[] file, Vector actualAttachFiles){}
	public PIMTextPane getTextPane(){return null;}
	
	public void release(){
		btnProduct.removeActionListener(this);
		btnCustomer.removeActionListener(this);
		btnSupplier.removeActionListener(this);
		btnEmployee.removeActionListener(this);
		btnSale.removeActionListener(this);
		btnInput.removeActionListener(this);
		btnRefund.removeActionListener(this);
		btnEvaluation.removeActionListener(this);
		btnProfit.removeActionListener(this);
		cancel.removeActionListener(this);
		dispose();//对于对话盒，如果不加这句话，就很难释放掉。
		System.gc();//@TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
	}

    public void componentResized(ComponentEvent e){
    	reLayout();
    };
    public void componentMoved(ComponentEvent e){};
    public void componentShown(ComponentEvent e){};
    public void componentHidden(ComponentEvent e){};

	public void actionPerformed(ActionEvent e){
		Object o = e.getSource();
		if(o == btnProduct)
			new CheckStoreDlg(parent).setVisible(true);
		else if(o == btnCustomer)
			new CustomerListDlg(parent).setVisible(true);
		else if(o == btnSupplier)
			new SupplierListDlg(parent).setVisible(true);
		else if(o == btnEmployee)
			new EmployeeListDlg(parent).setVisible(true);
		else if(o == btnSale)
			new SaleListDlg(parent).setVisible(true);
		else if(o == btnInput)
			new AddStoreListDlg(parent).setVisible(true);
		else if(o == btnRefund)
			new RefundListDlg(parent).setVisible(true);
		else if(o == btnEvaluation)
			new CheckInOutListDlg(parent).setVisible(true);
		else if(o == btnProfit)
			new ProfitDlg(parent).setVisible(true);
		else if(o == cancel)
			dispose();
	}
	
	public Container getContainer(){
		return getContentPane();
	}
    
	private void initDialog(){
        setTitle(PosDlgConst.Static);
		setResizable(false);
		
		//初始化－－－－－－－－－－－－－－－－
		btnProduct = new JButton(PosDlgConst.MerchandiseInfo);//商品资料
		btnCustomer = new JButton(PosDlgConst.CustomerInfo);//客户资料
		btnSupplier = new JButton(PosDlgConst.SupplierInfo);//供货商资料
		btnEmployee = new JButton(PosDlgConst.EmployeeInfo);//员工资料
		btnSale = new JButton(PosDlgConst.SaleRecs);		//销售记录
		btnInput = new JButton(PosDlgConst.InputRecs);		//进货记录
		btnRefund = new JButton(PosDlgConst.RefundRecs);	//退货记录
		btnEvaluation = new JButton(PosDlgConst.WorkRecs);//工作记录
		btnProfit = new JButton(PosDlgConst.ProfitStatChange);	//盈利情况
		cancel = new JButton(DlgConst.FINISH_BUTTON);			//关闭
		sptInfo = new PIMSeparator(PosDlgConst.BasicData);
		sptRec = new PIMSeparator(PosDlgConst.BasicRecs);
		sptProf = new PIMSeparator(PosDlgConst.Statistic);
		
		//属性设置－－－－－－－－－－－－－－
		btnProduct.setMargin(new Insets(0,0,0,0));
		btnCustomer.setMargin(btnProduct.getMargin());
		btnSupplier.setMargin(btnProduct.getMargin());
		btnEmployee.setMargin(btnProduct.getMargin());
		btnSale.setMargin(btnProduct.getMargin());
		btnInput.setMargin(btnProduct.getMargin());
		btnRefund.setMargin(btnProduct.getMargin());
		btnEvaluation.setMargin(btnProduct.getMargin());
		btnProfit.setMargin(btnProduct.getMargin());
		cancel.setMargin(btnProduct.getMargin());

		setBounds((CustOpts.SCRWIDTH - 380)/2, (CustOpts.SCRHEIGHT - 180)/2, 380, 180);	//对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		//搭建－－－－－－－－－－－－－
		getContentPane().add(sptInfo);
		getContentPane().add(btnProduct);
		getContentPane().add(btnCustomer);
		getContentPane().add(btnSupplier);
		getContentPane().add(btnEmployee);
		getContentPane().add(sptRec);
		getContentPane().add(btnSale);
		getContentPane().add(btnInput);
		getContentPane().add(btnRefund);
		getContentPane().add(btnEvaluation);
		getContentPane().add(sptProf);
		getContentPane().add(btnProfit);
		getContentPane().add(cancel);
		
		//加监听器－－－－－－－－
		btnProduct.addActionListener(this);
		btnCustomer.addActionListener(this);
		btnSupplier.addActionListener(this);
		btnEmployee.addActionListener(this);
		btnSale.addActionListener(this);
		btnInput.addActionListener(this);
		btnRefund.addActionListener(this);
		btnEvaluation.addActionListener(this);
		btnProfit.addActionListener(this);
		cancel.addActionListener(this);
		getContentPane().addComponentListener(this);
	}
	private JFrame parent;
	private JButton btnProduct;
	private JButton btnCustomer;
	private JButton btnSupplier;
	private JButton btnEmployee;
	private JButton btnSale;
	private JButton btnInput;
	private JButton btnRefund;
	private JButton btnEvaluation;
	private JButton btnProfit;
	private JButton cancel;
	private PIMSeparator sptInfo;
	private PIMSeparator sptRec;
	private PIMSeparator sptProf;
}
