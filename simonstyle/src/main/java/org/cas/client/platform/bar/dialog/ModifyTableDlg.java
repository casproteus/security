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
 * 微软的TextField的长度限制是228（约）， 本类的限制因为没有规格约束，暂定为912（约） // ID I NTEGER IDENTITY PRIMARY KEY // DELETED BIT DEFAULT false //
 * language1 CODE VARCHAR(255) // language2 MNEMONIC VARCHAR(255) // language3 SUBJECT VARCHAR(255) // price PRICE
 * INTEGER // gst FOLDERID INTEGER // qst STORE INTEGER // size COST INTEGER // printer BRAND VARCHAR(255) // CATEGORY
 * CATEGORY VARCHAR(255) // prompmenu UNIT VARCHAR(255) // prompprice CONTENT VARCHAR(255) // promp mofify PRODUCAREA
 * VARCHAR(255)
 */
public class ModifyTableDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {

	TableButton btnTable;
	SettingTabbleDlg settingTabbleDlg;
    public ModifyTableDlg(SettingTabbleDlg pFrame, TableButton button) {
    	super(pFrame);
    	settingTabbleDlg = pFrame;
    	if(button == null) {
    		button =  new TableButton();
    		button.setId(-1);
    	}
    	btnTable = button;
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
        sptName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP
               - CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT + 2);

        lblName.setBounds(CustOpts.HOR_GAP * 2, sptName.getY() + sptName.getHeight() + CustOpts.VER_GAP,
                lblName.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdName.setBounds(lblName.getX() + lblName.getWidth() + CustOpts.HOR_GAP, lblName.getY(), 80,  CustOpts.BTN_HEIGHT);
     
        // bounds--------
        sptBounds.setBounds(sptName.getX(), tfdName.getY() + tfdName.getHeight() + CustOpts.VER_GAP,
                sptName.getWidth(), CustOpts.SEP_HEIGHT + 2);
        lblLocations[0].setBounds(CustOpts.HOR_GAP * 2, sptBounds.getY() + sptBounds.getHeight() + CustOpts.VER_GAP,
                lblLocations[0].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLocations[0].setBounds(lblLocations[0].getX() + lblLocations[0].getWidth() + CustOpts.HOR_GAP,
                lblLocations[0].getY(), 
                sptName.getWidth()/2 - lblLocations[0].getWidth() - CustOpts.HOR_GAP * 2 - 40,
                CustOpts.BTN_HEIGHT);

        lblLocations[1].setBounds(lblLocations[0].getX(), lblLocations[0].getY() + lblLocations[0].getHeight()
                + CustOpts.VER_GAP, lblLocations[1].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLocations[1].setBounds(lblLocations[1].getX() + lblLocations[1].getWidth() + CustOpts.HOR_GAP,
                lblLocations[1].getY(), tfdLocations[0].getWidth(), CustOpts.BTN_HEIGHT);

        lblLocations[2].setBounds(tfdLocations[0].getX() + tfdLocations[0].getWidth() + CustOpts.HOR_GAP,
        		lblLocations[0].getY(), lblLocations[2].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLocations[2].setBounds(lblLocations[2].getX() + lblLocations[2].getWidth() + CustOpts.HOR_GAP,
                lblLocations[2].getY(), sptName.getWidth()/2 - lblLocations[2].getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        
        lblLocations[3].setBounds(lblLocations[2].getX(), lblLocations[2].getY() + lblLocations[2].getHeight()
                + CustOpts.VER_GAP, lblLocations[3].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLocations[3].setBounds(lblLocations[3].getX() + lblLocations[3].getWidth() + CustOpts.HOR_GAP,
                lblLocations[3].getY(), sptName.getWidth()/2 - lblLocations[3].getWidth() - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        
        // other-----------
        sptType.setBounds(sptBounds.getX(), tfdLocations[3].getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
        		sptBounds.getWidth(), CustOpts.SEP_HEIGHT + 2);
        lblCategory.setBounds(sptType.getX() + CustOpts.HOR_GAP, sptType.getY() + sptType.getHeight()
                + CustOpts.VER_GAP, lblCategory.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cmbCategory.setBounds(lblCategory.getX() + lblCategory.getWidth() + CustOpts.HOR_GAP, lblCategory.getY(),
                sptName.getWidth() - lblCategory.getWidth() - lblName.getPreferredSize().width - CustOpts.HOR_GAP
                        * 4 - 40, CustOpts.BTN_HEIGHT);


        ok.setBounds(getWidth() / 2 - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH,
        		cmbCategory.getY() + cmbCategory.getHeight() + CustOpts.VER_GAP * 3, CustOpts.BTN_WIDTH,
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
        cmbCategory.removeActionListener(this);
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
            //check inputs
        	String name = tfdName.getText();
        	if(name.length() < 1 ) {
        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
        		tfdName.grabFocus();
				return;
        	}else {
        		if(tfdLocations[0].getText().length() < 1) {
	        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
	        		tfdLocations[0].grabFocus();
					return;
	        	}else if(tfdLocations[1].getText().length() < 1) {
	        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
	        		tfdLocations[1].grabFocus();
					return;
	        	}else if(tfdLocations[2].getText().length() < 1) {
	        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
	        		tfdLocations[2].grabFocus();
					return;
	        	}else if(tfdLocations[3].getText().length() < 1) {
	        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
	        		tfdLocations[3].grabFocus();
					return;
	        	}else {
	        		try {
	        			int i = Integer.valueOf(tfdLocations[0].getText());
	        		}catch(Exception exp) {
	        			JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
		        		tfdLocations[0].grabFocus();
						return;
	        		}
	        		try {
	        			int i = Integer.valueOf(tfdLocations[1].getText());
	        		}catch(Exception exp) {
	        			JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
		        		tfdLocations[1].grabFocus();
						return;
	        		}
	        		try {
	        			int i = Integer.valueOf(tfdLocations[2].getText());
	        		}catch(Exception exp) {
	        			JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
		        		tfdLocations[2].grabFocus();
						return;
	        		}
	        		try {
	        			int i = Integer.valueOf(tfdLocations[3].getText());
	        		}catch(Exception exp) {
	        			JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
		        		tfdLocations[3].grabFocus();
						return;
	        		}
	        	}
        	}
        	
        	
        	String sql = btnTable.getId() == -1 ?				//if has no index means it's new table/
        			"INSERT INTO DINING_TABLE (name, posX, posY, width, height, type) VALUES ('"
					+ name + "', " + tfdLocations[0].getText() + ", " + tfdLocations[1].getText() + ", "
					+ tfdLocations[2].getText() + ", " + tfdLocations[3].getText() + ", " + cmbCategory.getSelectedItem() + ")"
					:
					"Update DINING_TABLE set name = '" + name 
					+ "', posX = " + tfdLocations[0].getText() + ", posY = " + tfdLocations[1].getText()
					+ ", width = " + tfdLocations[2].getText() + ", height = " + tfdLocations[3].getText()
					+ ", type = " + cmbCategory.getSelectedItem() + " where id = " + btnTable.getId();
        	try {
        		PIMDBModel.getStatement().execute(sql);
                dispose();
                if(settingTabbleDlg != null)
                	settingTabbleDlg.initTableBtns();
                else
                	((TablesPanel)BarFrame.instance.panels[BarFrame.instance.curPanel]).initContent();
        	}catch(Exception exp) {
        		ErrorUtil.write(exp);
        	}
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
        setTitle(btnTable.getText());
        setResizable(false);
        // 初始化－－－－－－－－－－－－－－－－
        sptName = new PIMSeparator(BarDlgConst.Name);
        lblName = new JLabel(BarDlgConst.Name);
        tfdName = new JTextField(btnTable.getText());

        sptBounds = new PIMSeparator(BarDlgConst.Size);
        lblLocations = new JLabel[4];
        tfdLocations = new JTextField[4];
        lblLocations[0] = new JLabel("X");
        tfdLocations[0] = new JTextField();
        lblLocations[1] = new JLabel("Y");
        tfdLocations[1] = new JTextField();
        lblLocations[2] = new JLabel("Width");
        tfdLocations[2] = new JTextField();
        lblLocations[3] = new JLabel("Height");
        tfdLocations[3] = new JTextField();

        sptType = new PIMSeparator(OptionDlgConst.OPTION_OTHER);
        lblCategory = new JLabel(BarDlgConst.Categary);
        cmbCategory = new JComboBox<String>();

        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－
        if(btnTable.getText().length() > 0) {
	        tfdLocations[0].setText(String.valueOf(btnTable.getX()));
	        tfdLocations[1].setText(String.valueOf(btnTable.getY()));
	        tfdLocations[2].setText(String.valueOf(btnTable.getWidth()));
	        tfdLocations[3].setText(String.valueOf(btnTable.getHeight()));
        }else {
	        tfdLocations[0].setText("200");
	        tfdLocations[1].setText("400");
	        tfdLocations[2].setText("120");
	        tfdLocations[3].setText("60");
        }
        String[] typeAry = new String[2];

        typeAry[0] = "0";
        typeAry[1] = "1";
        cmbCategory.setModel(new DefaultComboBoxModel(typeAry));
        cmbCategory.setSelectedItem(btnTable.getType());
    
    
        ok.setMnemonic('o');
        ok.setMargin(new Insets(0, 0, 0, 0));

        setBounds((CustOpts.SCRWIDTH - 280) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 280, 250); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        getRootPane().setDefaultButton(ok);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(sptName);
        getContentPane().add(lblLocations[0]);
        getContentPane().add(tfdLocations[0]);
        getContentPane().add(lblLocations[1]);
        getContentPane().add(tfdLocations[1]);
        getContentPane().add(lblLocations[2]);
        getContentPane().add(tfdLocations[2]);
        getContentPane().add(lblLocations[3]);
        getContentPane().add(tfdLocations[3]);

        getContentPane().add(sptBounds);

        getContentPane().add(sptType);
        getContentPane().add(lblCategory);
        getContentPane().add(cmbCategory);
        getContentPane().add(lblName);
        getContentPane().add(tfdName);

        getContentPane().add(cancel);
        getContentPane().add(ok);

        // 加监听器－－－－－－－－
        cmbCategory.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        addComponentListener(this);
    }


    private PIMSeparator sptName;
    private JLabel[] lblLocations;
    private JTextField[] tfdLocations;

    private PIMSeparator sptBounds;

    private PIMSeparator sptType;
    private JLabel lblCategory;
    private JComboBox<String> cmbCategory;
    private JLabel lblName;
    private JTextField tfdName;

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
