package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.bar.net.HttpRequestClient;
import org.cas.client.platform.bar.net.RequestNewOrderThread;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.json.JSONObject;

public class BarFrame extends JFrame implements ICASDialog, ActionListener, WindowListener, ComponentListener {
	private String VERSION = "V0.130-20190207";
    public static BarFrame instance;
    public static BarDlgConst consts = new BarDlgConst0();
    public int curPanel;
    public static NumberPanelDlg numberPanelDlg; 
    public static DiscountDlg discountDlg; 
    public static PayDlg payDlg;
	public static CustomerFrame customerFrame;
	private static GraphicsDevice secondScreen;
    
    public static void main(
            String[] args) {
        CASControl.ctrl.initModel();
        CASControl.ctrl.setMainFrame(new CASMainFrame());
        menuPanel = new MenuPanel();	//have to be after initModel, before new BarFrame().
        instance = new BarFrame();
        numberPanelDlg = new NumberPanelDlg(instance);
        discountDlg = new DiscountDlg(instance);
        payDlg = new PayDlg(instance);
        customerFrame = new CustomerFrame();
        
        //activation check
        String returnStr = validateActivation(null);
        if(!"OK".equals(returnStr)) { 	// if not valid, might because of expired, then give clean the bill head, 
        	if(!"OK".equals(validateActivation(returnStr))) {	// give another valid to show up licence dialog.
        		return;
        	}
        }
        
        if(BarOption.isSingleUser()) {
	        singleUserLoginProcess();
        }else {
        	instance.setVisible(true);
        }
      
        //this thread will start a request thread every 20 seconds to fetch new order from server..
        new RequestNewOrderThread().start();
        
        if(BarOption.isShowCustomerFrame()) {
            GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            for (GraphicsDevice graphicsDevice : gds) {
				if(gd != graphicsDevice) {
					secondScreen = graphicsDevice;
					secondScreen.setFullScreenWindow(customerFrame);
					break;
				}
			}
        }
    }
    
    private static String validateActivation(String activateCode) {
    	if(BarOption.getBillHeadInfo() == null || BarOption.getBillHeadInfo().trim().length() == 0 ) {
    		
    		if(activateCode == null)
    			activateCode = JOptionPane.showInputDialog(null, BarFrame.consts.activeCode());
    		if("asdfas".equals(activateCode)) {
    			if(BarOption.getBillHeadInfo() == null) {
            		BarOption.setBillHeadInfo("AikaPos");
            	}
    			return "OK";
    		}else {
    			authenticate(activateCode);
    			return "OK";
    		}
    	}else {
    		L.d("bill info is:", BarOption.getBillHeadInfo());
    		long timeLeft = checkDaysleft();
    		if(timeLeft > 0) {
    			if (timeLeft < 3024000000l) {   //3024000000L == 35days
                    JOptionPane.showMessageDialog(null, "Application is about to expire! Please re-activate it!");
                }
    			return "OK";
    		}else {
    			BarOption.setBillHeadInfo(null);
    			return activateCode;
    		}
    	}
    }
    
	public static String prepareLicenceJSONString() {
		JSONObject json = new JSONObject();//创建json对象
		json.put("licence", BarOption.getLicense());//使用URLEncoder.encode对特殊和不可见字符进行编码
		String jsonstr = json.toString();//把JSON对象按JSON的编码格式转换为字符串
		return jsonstr;
	}
	
    private static void authenticate(String inputedSN) {
        if (inputedSN == null || inputedSN.length() != 6) {
            JOptionPane.showMessageDialog(null,"Please input correct license code");
            return;
        }

        BarOption.setLicense(inputedSN);
        new HttpRequestClient(HttpRequestClient.SERVER_URL + "/activeAccount", "POST", prepareLicenceJSONString(), 
        		new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String responseString = e.getActionCommand();
						int p = responseString.indexOf("_");
		                if (p < -1) {
		                	JOptionPane.showMessageDialog(null, "Activation failed with error code 520, please contact us at info@ShareTheGoodOnes.com");
		                }else {
		                	String timeLeft = responseString.substring(0, p);
							long time = "none".equals(timeLeft) ? 100 * 365 * 24 * 3600 * 1000 : Long.valueOf(timeLeft);
							if (time > 0) {// if success
								BarOption.setActivateTimeLeft(String.valueOf(time * 24 * 3600 * 1000));
								BarOption.setLastSuccessStr(String.valueOf(new Date().getTime()));
								
								String billHeadInfo = responseString.substring(p + 1);
								p = billHeadInfo.indexOf("_");
								BarOption.setBillHeadInfo(billHeadInfo.substring(0, p > 0 ? p : billHeadInfo.length()));
								if(p > 0) {
									String GST = billHeadInfo.substring(p+1);
									p = GST.indexOf("_");
									BarOption.setGSTAccount(GST.substring(0, p > 0 ? p : GST.length()));
									if(p > 0) {
										BarOption.setQSTAccount(GST.substring(p + 1));
									}
								}
								//@NOTE: can not use JOptionPane.showMessageDialog, because it will be hided by LoginDlg, and stuck there.
								BarFrame.setStatusMes("Application is activated successfully!");
							} else {
								JOptionPane.showMessageDialog(null,
										"Software expired, please contact us at info@ShareTheGoodOnes.com");
							}
		                }
					}
		}).start();
    }
    
    private static long checkDaysleft() {
        //none limitation check, limitation can ba a number(String format), null(not set) or a none(never expired)
        if("none".equals(BarOption.getLimitation())) {         L.d("limitationMode", "none");
            return 3024000000l + 1;
        }
        
        //time of last open, @note:if existing last open time is not valid, then use lastSuccess will not be set.
        long timepassed = 0l;
        String lastsuccessStr = BarOption.getLastSuccessStr();    L.d("lastSuccessStr:",lastsuccessStr);
        try{
            Long lastSuccess= lastsuccessStr == null ? new Date().getTime() : Long.valueOf(lastsuccessStr);
            timepassed = new Date().getTime() - lastSuccess;L.d("timePassed:",timepassed);        //time passed since last open.
        }catch(Exception e){
            L.e("BarFrame ", "the lastsuccessStr is not valid long", e);
        }

        //if timeLeftStr is valid, then it has a chance to turn the timeLeft to be a number bigger than 0.
        long activateTimeLeft = BarOption.getActivateTimeLeft();//Number("number");               L.d("timeLeft(before deduct:", number);
        try {
            //the time left from last calculation, minus time passed. @note: we use abs, so is the time is negative, will still be minused!
        	activateTimeLeft = activateTimeLeft - Math.abs(timepassed);    L.d("timeLeft - timePassed:", activateTimeLeft);

            //update the number and lastsuccess into local cache.
        	BarOption.setLastSuccessStr(String.valueOf(new Date().getTime()));
        	BarOption.setActivateTimeLeft(String.valueOf(activateTimeLeft));  L.d("update new number with:", activateTimeLeft);
        }catch(Exception e){
            L.e("MainActivity", "the left time number can not be pasered into a long", e);
        }

        return activateTimeLeft;
    }
    
    public static void singleUserLoginProcess() {
    	new LoginDlg(instance).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            instance.setVisible(true);
            checkSignIn();
        }else {	//the case that user clicked X button.
        	CASControl.ctrl.exitSystem();
            System.exit(0);
        }
    }

    /**
     * used when single user login, or when multiuser adding table or open table.
     * if already has record, then ignore, if not have record yet. then add one.
     */
	public static void checkSignIn() {
		//first make sure the name is displayed on BarFrame heder area.
    	BarFrame.instance.valOperator.setText(LoginDlg.USERNAME);
    	
		String time = BarOption.df.format(new Date());
		int p = time.indexOf(" ");
		time = time.substring(0, p + 1) + BarOption.getStartTime();
		
		String sql = "Select * from evaluation where EMPLOYEEID = " + LoginDlg.USERID + " and startTime > '" + time
				+ "' and endTime is null";

		Statement smt = PIMDBModel.getStatement();
		try {
			ResultSet rs =  smt.executeQuery(sql);
            rs.next();
			if(rs.getInt("id") > -1);	//if not throwning exception (means there is a record), then do nothing. 
				return;
		}catch(Exception exp) {			//otherwise(if no record yet), generate a record.
			sql = "INSERT INTO evaluation(startTime, EMPLOYEEID, subject) VALUES ('" + BarOption.df.format(new Date())
					+ "', " + LoginDlg.USERID + ", '" + LoginDlg.USERNAME +"')";
			try {
				smt.executeUpdate(sql);
			}catch(Exception exp2) {
				ErrorUtil.write(exp2);
			}
		}
	}
    
    public BarFrame() {
    	initComponent();
        //hide sales and setting pannel.
    }
    
    public void initComponent(){
    	getContentPane().removeAll();
    	setTitle(BarFrame.consts.Title());
        setIconImage(CustOpts.custOps.getFrameLogoImage()); // 设置主窗体的LOGO。

        setBounds(0, 0, CustOpts.SCRWIDTH, CustOpts.SCRHEIGHT - 30); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        setResizable(true);

        // 初始化－－－－－－－－－－－－－－－－
        int tShoestring = 0;
        try {
            tShoestring = Integer.parseInt((String) CustOpts.custOps.getValue(BarFrame.consts.Shoestring()));
        } catch (Exception exp) {
        }
        
        lblOperator = new JLabel(BarFrame.consts.Operator().concat(BarFrame.consts.Colon()));
        valOperator = new JLabel();
        lblCurTable = new JLabel(BarFrame.consts.TABLE().concat(" #"));
        valCurTable = new JLabel();
        lblCurBillIdx = new JLabel(BarFrame.consts.BILL().concat(" #"));
        valCurBillIdx = new JLabel();
        lblStartTime = new JLabel(BarFrame.consts.OPENTIME().concat(BarFrame.consts.Colon()));
        valStartTime = new JLabel();
        
        lblStatus = new JLabel();
		lblVersion = new JLabel(VERSION);
        
        panels[0] = new TablesPanel();
        panels[1] = new BillListPanel();
        panels[2] = new SalesPanel();
        panels[3] = new SettingPanel();

        lblStatus.setBorder(null);
        
        // 搭建－－－－－－－－－－－－－
        add(lblOperator);
        add(valOperator);
        add(lblCurTable);
        add(valCurTable);
        add(lblCurBillIdx);
        add(valCurBillIdx);
        add(lblStartTime);
        add(valStartTime);

        getContentPane().add(lblStatus);
        getContentPane().add(lblVersion);
        
        getContentPane().add(panels[0]);
        getContentPane().add(panels[1]);
        getContentPane().add(panels[2]);
        getContentPane().add(panels[3]);

        // 加监听器－－－－－－－－
        addWindowListener(this);
        getContentPane().addComponentListener(this);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // general.tfdProdNumber.grabFocus();
            }
        });

        switchMode(0);
        reLayout();
    }
    
	public static void setStatusMes(
            String pMes) {
        lblStatus.setText(pMes);
    }
	
    public int switchMode(int i) {
    	BillListPanel.curDish = null;
    	setStatusMes("");
		if (i == 3) {		//setting
			if (!adminAuthentication()) 
				return -1;
			resetStatus();
		}else if(i == 2) {	//sale
			((SalesPanel)panels[i]).billPanel.initContent();
			customerFrame.initContent();
			if(secondScreen != null) {
				secondScreen.setFullScreenWindow(customerFrame);
			}
		}else if(i == 1) {	//bill
			((BillListPanel)panels[i]).initContent();
		}else if(i == 0) {	//table
			resetStatus();
			((TablesPanel)panels[i]).initContent();
		}
		
    	for (JPanel panel : panels)
    		panel.setVisible(false);
		
    	panels[i].setVisible(true);
    	if(i > 1) {	//salespanel and setting pannel need menu panel on it.
    		panels[i].add(menuPanel);
    	}
    	
    	curPanel = i;
    	return 0;
	}
    
	private void resetStatus() {
		if (!BarOption.isSingleUser()) {
			valOperator.setText("");
		}
		valCurTable.setText("");
		valCurBillIdx.setText("");
		valStartTime.setText("");
	}
    
    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true && LoginDlg.USERTYPE == LoginDlg.ADMIN_STATUS) { // 如果用户选择了确定按钮。
        	valOperator.setText(LoginDlg.USERNAME);
            BarFrame.setStatusMes(BarFrame.consts.ADMIN_MODE());
            // @TODO: might need to do some modification on the interface.
            revalidate();
            return true;
        }
        return false;
    }
    
    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        lblCurTable.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCurTable.getPreferredSize().width,
        		lblCurTable.getPreferredSize().height);
        valCurTable.setBounds(lblCurTable.getX() + lblCurTable.getWidth(), CustOpts.VER_GAP, 180 - lblCurTable.getWidth(),
        		lblCurTable.getPreferredSize().height);
        lblCurBillIdx.setBounds(valCurTable.getX() + valCurTable.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCurBillIdx.getPreferredSize().width,
        		lblCurBillIdx.getPreferredSize().height);
        valCurBillIdx.setBounds(lblCurBillIdx.getX() + lblCurBillIdx.getWidth(), CustOpts.VER_GAP, 180 - lblCurBillIdx.getWidth(),
        		lblCurBillIdx.getPreferredSize().height);
        
        lblStartTime.setBounds(getWidth() - lblStartTime.getPreferredSize().width - 200 - CustOpts.HOR_GAP*2 - CustOpts.SIZE_EDGE * 2,
        		 CustOpts.VER_GAP, lblStartTime.getPreferredSize().width, lblOperator.getPreferredSize().height);
        valStartTime.setBounds(lblStartTime.getX() + lblStartTime.getWidth(), lblStartTime.getY(),
        		200 - lblStartTime.getWidth(), lblStartTime.getHeight());
        lblOperator.setBounds(lblStartTime.getX() - 180 - CustOpts.HOR_GAP, CustOpts.VER_GAP, lblOperator.getPreferredSize().width,
                lblOperator.getPreferredSize().height);
        valOperator.setBounds(lblOperator.getX() + lblOperator.getWidth(), CustOpts.VER_GAP, 180 - lblOperator.getWidth(),
                lblOperator.getPreferredSize().height);
        
        // status---------
        lblStatus.setBounds(CustOpts.HOR_GAP, getContainer().getHeight() - CustOpts.LBL_HEIGHT - CustOpts.VER_GAP, 
        		getContainer().getWidth() - CustOpts.HOR_GAP * 2 - 120 -  - CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        lblVersion.setBounds(lblStatus.getX() + lblStatus.getWidth() + CustOpts.VER_GAP, lblStatus.getY(), 100, lblStatus.getHeight());
        for (JPanel panel : panels) {
        	panel.setBounds(0, lblOperator.getY() + lblOperator.getHeight(), 
        			getContainer().getWidth(), lblStatus.getY() - lblOperator.getY() - lblOperator.getHeight());
		}
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
        removeWindowListener(this);
        for (JPanel panel : panels) {
    		panel.removeAll();
    		panel = null;
		}
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(ComponentEvent e) {
        reLayout();
    };

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(ComponentEvent e) {};

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(ComponentEvent e) {};

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(ComponentEvent e) {};

    /**
     * Invoked when an action occurs. NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
     * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
     * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
     * 
     * @param e
     *            动作事件
     */
    @Override
    public void actionPerformed(ActionEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowClosed(WindowEvent e) {}

	static boolean ISCLOSING = false;
    @Override
    public void windowClosing(WindowEvent e) {
		if (ISCLOSING) {
			return;
		}
		ISCLOSING = true; // ignore the second windowClosing event.

        if (CASControl.ctrl.getMainFrame() != null)
            CASControl.ctrl.getMainFrame().dispose();
        dispose();
        CASControl.ctrl.exitSystem(); // 保存所有状态和数据后退出。
    }

    @Override
    public void windowDeactivated(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    boolean hasClose; // 标志对话框是否已关闭

    private JLabel lblCurTable;
    private JLabel lblCurBillIdx;
    private JLabel lblOperator;
    private JLabel lblStartTime;
    
    public JLabel valCurTable;
    public JLabel valCurBillIdx;
    public JLabel valOperator;
    public JLabel valStartTime;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    
    public JPanel[] panels = new JPanel[4];
    public static MenuPanel menuPanel;
    static JLabel lblStatus;
    private JLabel lblVersion;

	public String getCurBillIndex() {
		String curBillIndex = valCurBillIdx.getText();
		if(curBillIndex == null || curBillIndex.trim().length() == 0 || "0".equals(curBillIndex))
			return "1";
		else
			return curBillIndex;
	}
}
