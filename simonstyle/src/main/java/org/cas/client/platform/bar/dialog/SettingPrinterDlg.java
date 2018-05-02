package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.cas.client.platform.bar.beans.CategoryToggleButton;
import org.cas.client.platform.bar.beans.MenuButton;
import org.cas.client.platform.bar.beans.TableButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;
import org.hsqldb.lib.StringUtil;

/**
 */
public class SettingPrinterDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {

    public SettingPrinterDlg(BarFrame pFrame) {
    	super(pFrame);
        initDialog();
        reLayout();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        // name------------
    	lblName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 60, CustOpts.BTN_HEIGHT);
        lblIP.setBounds(lblName.getX() + lblName.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP, 120, CustOpts.BTN_HEIGHT);
        lblCategory.setBounds(lblIP.getX() + lblIP.getWidth() + CustOpts.HOR_GAP, CustOpts.VER_GAP, 110, CustOpts.BTN_HEIGHT);
        
        for(int i = 0; i < 6; i++) {
        	tfdName[i].setBounds(lblName.getX(), lblName.getY() + (lblName.getHeight() + CustOpts.VER_GAP * 2) * (1 + i), lblName.getWidth(),CustOpts.BTN_HEIGHT);
        	tfdIP[i].setBounds(lblIP.getX(), tfdName[i].getY(), lblIP.getWidth(),CustOpts.BTN_HEIGHT);
        	cmbCategory[i].setBounds(lblCategory.getX(), tfdIP[i].getY(), lblCategory.getWidth(),CustOpts.BTN_HEIGHT);
        }
        
        ok.setBounds(getWidth() / 2 - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH,
        		cmbCategory[5].getY() + cmbCategory[5].getHeight() + CustOpts.VER_GAP * 3, CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getWidth() + ok.getX() + CustOpts.HOR_GAP * 2, ok.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

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
        ok.removeActionListener(this);
        cancel.removeActionListener(this);

        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
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
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == ok) {
            //@TODO:check inputs
        	for(int i = 0; i < 6; i++) {
        		String sql = 
					"Update Hardware set name = '" + tfdName[i].getText() 
					+ "', ip = '" + tfdIP[i].getText() + "', style = " + cmbCategory[i].getSelectedIndex()
					+ " where id = " + ids[i];
	        	try {
	        		PIMDBModel.getStatement().execute(sql);
	        	}catch(Exception exp) {
	        		ErrorUtil.write(exp);
	        	}
        	}
        	dispose();
        } else if (o == cancel) {
            dispose();
        }
    }


    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
    	setModal(true);
        setTitle(BarDlgConst.Printer);
        setResizable(false);
        // 初始化－－－－－－－－－－－－－－－－
        lblName = new JLabel(BarDlgConst.Name);
        lblIP = new JLabel(BarDlgConst.IPAddress);
        lblCategory = new JLabel(BarDlgConst.Categary);
        tfdName = new JTextField[6];
        tfdIP = new JTextField[6];
        cmbCategory = new JComboBox[6];
        typeAry = new String[2];
        typeAry[0] = "Separate";
        typeAry[1] = "All";
        
        
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－
        ok.setMnemonic('o');
        ok.setMargin(new Insets(0, 0, 0, 0));

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(lblName);
        getContentPane().add(lblIP);
        getContentPane().add(lblCategory);
        for(int i = 0; i < 6; i++) {
        	tfdName[i] = new JTextField();
        	getContentPane().add(tfdName[i]);
        	tfdIP[i] = new JTextField();
        	getContentPane().add(tfdIP[i]);
        	cmbCategory[i] = new JComboBox();
        	cmbCategory[i].setModel(new DefaultComboBoxModel(typeAry));
        	getContentPane().add(cmbCategory[i]);
        }

        getContentPane().add(cancel);
        getContentPane().add(ok);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        cancel.addActionListener(this);
        addComponentListener(this);
        
        setBounds((CustOpts.SCRWIDTH - 280) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 330, 320); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        getRootPane().setDefaultButton(ok);

        //init content
        initContent();
    }

    private void initContent() {
    	 String sql = "select * from hardware where category = 0 and status = 0"; // 是否存在上没有名字的产品？
         try {
             ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
             rs.afterLast();
             rs.relative(-1);
             int tRowCount = rs.getRow();
             if (tRowCount > 0) {
            	 rs.beforeFirst();
                 int tIdx = 0;
                 while (rs.next()) {
                	 ids[tIdx] = rs.getInt("id");
                     tfdName[tIdx].setText(rs.getString("name"));
                     tfdIP[tIdx].setText(rs.getString("ip"));
                     cmbCategory[tIdx].setSelectedIndex(rs.getInt("style"));
                     tIdx++;
                 }
             }
         }catch(Exception exp) {
        	 ErrorUtil.write(exp);
         }
    }

    private JLabel lblName;
    private JLabel lblIP;
    private JLabel lblCategory;
    private JTextField[] tfdName;
    private JTextField[] tfdIP;
    private JComboBox<String>[] cmbCategory;
    String[] typeAry = new String[2];
    int[] ids = new int[6];
    
    private JButton ok;
    private JButton cancel;

	@Override
	public void componentResized(ComponentEvent e) {
		reLayout();
		
	}

	@Override
	public void componentMoved(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}
