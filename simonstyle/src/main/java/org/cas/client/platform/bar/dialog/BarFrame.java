package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;

public class BarFrame extends JFrame implements ICASDialog, ActionListener, WindowListener, ComponentListener {

    public static BarFrame instance;
    public static BarDlgConst consts = new BarDlgConst0();
    public int curPanel;
    public static NumberPanelDlg numberPanelDlg; 
    public static PayCashDlg payCashDlg;
    
    public static void main(
            String[] args) {
        CASControl.ctrl.initModel();
        CASControl.ctrl.setMainFrame(new CASMainFrame());
        menuPanel = new MenuPanel();	//have to be after initModel, before new BarFrame().
        instance = new BarFrame();
        numberPanelDlg = new NumberPanelDlg(instance);
        payCashDlg = new PayCashDlg(instance);
        
        if(BarOption.isSingleUser()) {
	        singleUserLoginProcess();
        }else {
        	instance.setVisible(true);
        }
    }
    
    public static void singleUserLoginProcess() {
    	new LoginDlg(instance).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            instance.setVisible(true);
            
            //do sign in automatically
            String sql = "INSERT INTO evaluation(startTime, EMPLOYEEID) VALUES ('" + BarOption.df.format(new Date())
				+ "', " + LoginDlg.USERID + ")";
			try {
				PIMDBModel.getStatement().executeQuery(sql);
			}catch(Exception exp) {
				ErrorUtil.write(exp);
			}
        }else {	//the case that user clicked X button.
            System.exit(0);
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
        lblCurTable = new JLabel(BarFrame.consts.TABLE().concat(BarFrame.consts.Colon()));
        valCurTable = new JLabel();
        lblCurBill = new JLabel(BarFrame.consts.BILL().concat(BarFrame.consts.Colon()));
        valCurBill = new JLabel();
        lblStartTime = new JLabel(BarFrame.consts.OPENTIME().concat(BarFrame.consts.Colon()));
        valStartTime = new JLabel();
        
        lblStatus = new JLabel();
		lblVersion = new JLabel("V0.17-20180611");
        
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
        add(lblCurBill);
        add(valCurBill);
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
		if (i == 3) {
			if (!adminAuthentication()) 
				return -1;
			resetStatus();
		}else if(i == 2) {
			((SalesPanel)panels[i]).billPanel.initContent();
		}else if(i == 1) {
			((BillListPanel)panels[i]).initContent();
		}else if(i == 0) {
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
		if (BarOption.isSingleUser()) {
			valOperator.setText(LoginDlg.USERNAME);
		}else {
			valOperator.setText("");
		}
		valCurTable.setText("");
		valCurBill.setText("");
		valStartTime.setText("");
	}
    
    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("admin".equalsIgnoreCase(LoginDlg.USERNAME)) {
            	valOperator.setText(LoginDlg.USERNAME);
                BarFrame.setStatusMes(BarFrame.consts.ADMIN_MODE());
                // @TODO: might need to do some modification on the interface.
                revalidate();
                return true;
            }
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
        lblCurBill.setBounds(valCurTable.getX() + valCurTable.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCurBill.getPreferredSize().width,
        		lblCurBill.getPreferredSize().height);
        valCurBill.setBounds(lblCurBill.getX() + lblCurBill.getWidth(), CustOpts.VER_GAP, 180 - lblCurBill.getWidth(),
        		lblCurBill.getPreferredSize().height);
        
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
        		getContainer().getWidth() - CustOpts.HOR_GAP * 2 - 100 -  - CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
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
    }

    @Override
    public void windowActivated(
            WindowEvent e) {
    }

    @Override
    public void windowClosed(
            WindowEvent e) {
    }

	static boolean ISCLOSING = false;
    @Override
    public void windowClosing(
            WindowEvent e) {
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
    public void windowDeactivated(
            WindowEvent e) {
    }

    @Override
    public void windowDeiconified(
            WindowEvent e) {
    }

    @Override
    public void windowIconified(
            WindowEvent e) {
    }

    @Override
    public void windowOpened(
            WindowEvent e) {
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    boolean hasClose; // 标志对话框是否已关闭

    private JLabel lblCurTable;
    private JLabel lblCurBill;
    private JLabel lblOperator;
    private JLabel lblStartTime;
    
    public JLabel valCurTable;
    public JLabel valCurBill;
    public JLabel valOperator;
    public JLabel valStartTime;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    
    public JPanel[] panels = new JPanel[4];
    public static MenuPanel menuPanel;
    static JLabel lblStatus;
    private JLabel lblVersion;
}
