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
import java.util.Calendar;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.bar.beans.TableButton;
import org.cas.client.platform.bar.model.User;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMRecord;

public class BarFrame extends JFrame implements ICASDialog, ActionListener, WindowListener, ComponentListener {

    public static BarFrame instance;
    int curPanel;
    JLabel lblCurBill;
    static NumberPanelDlg numberPanelDlg; 
    
    public static String startTime;
    
    public static void main(
            String[] args) {
        CASControl.ctrl.initModel();
        instance = new BarFrame();
        numberPanelDlg = new NumberPanelDlg(instance);
        
        if(isSingleUser()) {
	        new LoginDlg(instance).setVisible(true);
	        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
	            instance.setVisible(true);
	        }else {	//the case that user clicked X button.
	            System.exit(0);
	        }
        }else {
        	instance.setVisible(true);
        }
    }

    public static boolean isSingleUser() {
    	return CustOpts.custOps.getValue("SingleUserMode") != null;
    }
    
    public BarFrame() {
        setTitle(BarDlgConst.Title);
        setIconImage(CustOpts.custOps.getFrameLogoImage()); // 设置主窗体的LOGO。

        setBounds(0, 0, CustOpts.SCRWIDTH, CustOpts.SCRHEIGHT - 30); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        setResizable(true);

        // 初始化－－－－－－－－－－－－－－－－
        int tShoestring = 0;
        try {
            tShoestring = Integer.parseInt((String) CustOpts.custOps.getValue(BarDlgConst.Shoestring));
        } catch (Exception exp) {
        }
        lblShoestring =
                new JLabel(BarDlgConst.LeftMoney.concat(BarDlgConst.Colon)
                        .concat(decimalFormat.format(tShoestring / 100.0)).concat(BarDlgConst.Unit));
        
        startTime = Calendar.getInstance().getTime().toLocaleString();
        lblOperator = new JLabel(BarDlgConst.Operator.concat(BarDlgConst.Colon));
        valOperator = new JLabel();
        lblCurTable = new JLabel(BarDlgConst.Table.concat(BarDlgConst.Colon));
        valCurTable = new JLabel();
        lblBill = new JLabel(BarDlgConst.Bill.concat(BarDlgConst.Colon));
        lblCurBill = new JLabel();
        
        lblShoestring = new JLabel();
                        
        lblStartTime = new JLabel();

        lblStatus = new JLabel();
        menuPanel = new MenuPanel();
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
        add(lblBill);
        add(lblCurBill);
        add(lblShoestring);
        add(lblStartTime);
        
        getContentPane().add(lblStatus);
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
        
        //hide sales and setting pannel.
        switchMode(0);
    }
    
	public static void setStatusMes(
            String pMes) {
        lblStatus.setText(pMes);
    }
	
    public int switchMode(int i) {
		if (i == 3) {
			if (!adminAuthentication()) 
				return -1;
		}else if(i == 2) {
			((SalesPanel)panels[i]).billPanel.initTable();
		}else if(i == 1) {
			((BillListPanel)panels[i]).initContent();
		}else if(i == 0) {
			resetStatus();
			((TablesPanel)panels[i]).initTableBtns();
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
		if (isSingleUser()) {
			valOperator.setText(LoginDlg.USERNAME);
		}else {
			valOperator.setText("");
		}
		valCurTable.setText("");
		lblCurBill.setText("");

		lblStartTime.setText(BarDlgConst.StartTime.concat(BarDlgConst.Colon).concat(startTime));
	}
    
    private boolean adminAuthentication() {
        new LoginDlg(null).setVisible(true);
        if (LoginDlg.PASSED == true) { // 如果用户选择了确定按钮。
            if ("admin".equalsIgnoreCase(LoginDlg.USERNAME)) {
            	valOperator.setText(LoginDlg.USERNAME);
                BarFrame.setStatusMes(BarDlgConst.ADMIN_MODE);
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
        lblOperator.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblOperator.getPreferredSize().width,
                lblOperator.getPreferredSize().height);
        valOperator.setBounds(lblOperator.getX() + lblOperator.getWidth(), CustOpts.VER_GAP, 180 - lblOperator.getWidth(),
                lblOperator.getPreferredSize().height);
        lblCurTable.setBounds(valOperator.getX() + valOperator.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCurTable.getPreferredSize().width,
        		lblCurTable.getPreferredSize().height);
        valCurTable.setBounds(lblCurTable.getX() + lblCurTable.getWidth(), CustOpts.VER_GAP, 180 - lblCurTable.getWidth(),
        		lblCurTable.getPreferredSize().height);
        lblBill.setBounds(valCurTable.getX() + valCurTable.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP, lblBill.getPreferredSize().width,
        		lblBill.getPreferredSize().height);
        lblCurBill.setBounds(lblBill.getX() + lblBill.getWidth(), CustOpts.VER_GAP, 180 - lblBill.getWidth(),
        		lblBill.getPreferredSize().height);
        lblStartTime.setBounds(getWidth() - lblStartTime.getPreferredSize().width - CustOpts.HOR_GAP*2 - CustOpts.SIZE_EDGE * 2,
                lblOperator.getY(), lblStartTime.getPreferredSize().width, lblOperator.getPreferredSize().height);
        lblShoestring.setBounds(
                lblOperator.getX()
                        + lblOperator.getWidth()
                        + (lblStartTime.getX() - lblOperator.getX() - lblOperator.getWidth() - lblShoestring
                                .getPreferredSize().width) / 2, lblOperator.getY(),
                lblShoestring.getPreferredSize().width, lblOperator.getHeight());
        
        // status---------
        lblStatus.setBounds(CustOpts.HOR_GAP, getContainer().getHeight() - CustOpts.LBL_HEIGHT - CustOpts.VER_GAP, 
        		getContainer().getWidth() - CustOpts.HOR_GAP * 2, CustOpts.LBL_HEIGHT);
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

    @Override
    public void windowClosing(
            WindowEvent e) {
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

    private JLabel lblOperator;
    JLabel valOperator;
    private JLabel lblCurTable;
    JLabel valCurTable;
    private JLabel lblBill;
    private JLabel lblShoestring;
    private JLabel lblStartTime;

    private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
    
    JPanel[] panels = new JPanel[4];
    MenuPanel menuPanel;
    static JLabel lblStatus;
}
