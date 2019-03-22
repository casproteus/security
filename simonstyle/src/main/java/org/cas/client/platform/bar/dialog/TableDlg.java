package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.cas.client.platform.bar.uibeans.TableButton;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;

/**
 * 微软的TextField的长度限制是228（约）， 本类的限制因为没有规格约束，暂定为912（约） // ID I NTEGER IDENTITY PRIMARY KEY // DELETED BIT DEFAULT false //
 * language1 CODE VARCHAR(255) // language2 MNEMONIC VARCHAR(255) // language3 SUBJECT VARCHAR(255) // price PRICE
 * INTEGER // gst FOLDERID INTEGER // qst STORE INTEGER // size COST INTEGER // printer BRAND VARCHAR(255) // CATEGORY
 * CATEGORY VARCHAR(255) // prompmenu UNIT VARCHAR(255) // prompprice CONTENT VARCHAR(255) // promp mofify PRODUCAREA
 * VARCHAR(255)
 */
public class TableDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {

	TableButton btnTable;
	TabbleSettingDlg settingTabbleDlg;
    public TableDlg(TabbleSettingDlg pFrame, TableButton button) {
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
        tfdName.setBounds(lblName.getX() + lblName.getWidth() + CustOpts.HOR_GAP, lblName.getY(), 
        		sptName.getWidth() - lblName.getWidth() - CustOpts.HOR_GAP * 2,  CustOpts.BTN_HEIGHT);
     
        int otherAreaHeight = LoginDlg.USERTYPE >= 2 ? 0 : 200;
        
        // bounds--------
        sptBounds.setBounds(sptName.getX(), tfdName.getY() + tfdName.getHeight() + CustOpts.VER_GAP + otherAreaHeight,
                sptName.getWidth(), CustOpts.SEP_HEIGHT + 2);
        lblX.setBounds(CustOpts.HOR_GAP * 2, sptBounds.getY() + sptBounds.getHeight() + CustOpts.VER_GAP,
        		lblX.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdX.setBounds(lblX.getX() + lblX.getWidth() + CustOpts.HOR_GAP,
        		lblX.getY(), sptName.getWidth()/2 - lblX.getWidth() - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
        lblY.setBounds(tfdX.getX() + tfdX.getWidth() + CustOpts.HOR_GAP, lblX.getY(), 
        		lblY.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdY.setBounds(lblY.getX() + lblY.getWidth() + CustOpts.HOR_GAP, lblY.getY(), 
        		sptName.getWidth()/2 - lblY.getWidth() - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
        
        lblWidth.setBounds(CustOpts.HOR_GAP * 2, lblX.getY() + lblX.getHeight() + CustOpts.VER_GAP,
        		lblWidth.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdWidth.setBounds(lblWidth.getX() + lblWidth.getWidth() + CustOpts.HOR_GAP,
        		lblWidth.getY(), sptName.getWidth()/2 - lblWidth.getWidth() - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
        
        lblHeight.setBounds(tfdWidth.getX() + tfdWidth.getWidth() + CustOpts.HOR_GAP, lblWidth.getY(), 
        		lblHeight.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdHeight.setBounds(lblHeight.getX() + lblHeight.getWidth() + CustOpts.HOR_GAP, lblHeight.getY(), 
        		sptName.getWidth()/2 - lblHeight.getWidth() - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
        
        // other-----------
        sptType.setBounds(sptBounds.getX(), lblWidth.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
        		sptBounds.getWidth(), CustOpts.SEP_HEIGHT + 2);
        lblCategory.setBounds(sptType.getX() + CustOpts.HOR_GAP, sptType.getY() + sptType.getHeight()
                + CustOpts.VER_GAP, lblCategory.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cmbCategory.setBounds(lblCategory.getX() + lblCategory.getWidth() + CustOpts.HOR_GAP, lblCategory.getY(),
                sptName.getWidth() - lblCategory.getX() - lblCategory.getWidth() - CustOpts.HOR_GAP * 4 - 40, CustOpts.BTN_HEIGHT);

        ok.setBounds(getWidth() / 2 - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH,
        		getHeight() - CustOpts.SIZE_EDGE * 2 - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP * 2 - 20,
        		CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getWidth() + ok.getX() + CustOpts.HOR_GAP * 2, ok.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        validate();
    }

    @Override
    public PIMRecord getContents() {return null;}

    @Override
    public boolean setContents(PIMRecord prmRecord) {return true;}

    @Override
    public void makeBestUseOfTime() {}

    @Override
    public void addAttach(File[] file, Vector actualAttachFiles) {}

    @Override
    public PIMTextPane getTextPane() {return null;}

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
            //check name
        	String name = tfdName.getText();
        	if(name.length() < 1 ) {
        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
        		tfdName.grabFocus();
				return;
        	}
        	
        	//check width
    		try {
    			int i = Integer.valueOf(tfdWidth.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
        		tfdWidth.grabFocus();
				return;
    		}
    		
    		//check height
    		try {
    			int i = Integer.valueOf(tfdHeight.getText());
    		}catch(Exception exp) {
    			JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
        		tfdHeight.grabFocus();
				return;
    		}
        	
        	
        	StringBuilder sql = new StringBuilder();
    		if(btnTable.getId() == -1) {	//if has no index means it's new table/
    			int type = cmbCategory.getSelectedIndex();
    			if(settingTabbleDlg == null){
    				type += 100;
    			}
    			sql.append("INSERT INTO DINING_TABLE (name, posX, posY, width, height, type) VALUES ('")
    			.append(name).append("', ").append(tfdX.getText()).append(", ")
    			.append(tfdY.getText()).append(", ").append(tfdWidth.getText())
    			.append(", ").append(tfdHeight.getText()).append(", ")
    			.append(type).append(")");
    		}else {
    			sql.append("Update DINING_TABLE set name = '").append(name).append("', posX = ")
    			.append(tfdX.getText()).append(", posY = ").append(tfdY.getText())
    			.append(", width = ").append(tfdWidth.getText()).append(", height = ")
    			.append(tfdHeight.getText()).append(", type = ").append(cmbCategory.getSelectedIndex())
    			.append(" where id = ").append(btnTable.getId());
    		}
        		
        	try {
        		PIMDBModel.getStatement().executeUpdate(sql.toString());
                dispose();
                if(settingTabbleDlg != null)
                	settingTabbleDlg.initContent();
                else
                	((TablesPanel)BarFrame.instance.panels[0]).initContent();
        	}catch(Exception exp) {
        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
        	}
        } else if (o == cancel) {
            dispose();
        }
    }


    @Override
    public Container getContainer() { return getContentPane(); }

    private void initDialog() {
    	setModal(true);
        setTitle(btnTable.getText().length() > 0 ? btnTable.getText() : BarFrame.consts.TABLE());
        setResizable(false);
        // 初始化－－－－－－－－－－－－－－－－
        sptName = new PIMSeparator(BarFrame.consts.Name());
        lblName = new JLabel(BarFrame.consts.TableName());
        tfdName = new JTextField(btnTable.getText());

        sptBounds = new PIMSeparator(BarFrame.consts.Size());
        lblX = new JLabel("Horizontal");
        tfdX = new JTextField();
        lblY = new JLabel("Vertical");
        tfdY = new JTextField();
        lblWidth = new JLabel("Width");
        tfdWidth = new JTextField();
        lblHeight = new JLabel("Height");
        tfdHeight = new JTextField();

        sptType = new PIMSeparator(OptionDlgConst.OPTION_OTHER);
        lblCategory = new JLabel(BarFrame.consts.Categary());
        cmbCategory = new JComboBox<String>();

        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－

        if(btnTable.getText().length() > 0) {
	        tfdX.setText(String.valueOf(btnTable.getX()));
	        tfdY.setText(String.valueOf(btnTable.getY()));
	        tfdWidth.setText(String.valueOf(btnTable.getWidth()));
	        tfdHeight.setText(String.valueOf(btnTable.getHeight()));
        }else {
	        tfdX.setText("200");
	        tfdY.setText("400");
	        tfdWidth.setText("60");
	        tfdHeight.setText("50");
        }
        String[] typeAry = new String[]{"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L"};
        cmbCategory.setModel(new DefaultComboBoxModel(typeAry));
        cmbCategory.setSelectedIndex(btnTable.getType());
    
    
        ok.setMnemonic('o');
        ok.setMargin(new Insets(0, 0, 0, 0));

        setBounds((CustOpts.SCRWIDTH - 280) / 2, (CustOpts.SCRHEIGHT - 260) / 2,
        		280, LoginDlg.USERTYPE >= 2 ? 260 : 160); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        getRootPane().setDefaultButton(ok);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(sptName);
        getContentPane().add(lblWidth);
        getContentPane().add(tfdWidth);
        getContentPane().add(lblHeight);
        getContentPane().add(tfdHeight);
        getContentPane().add(lblX);
        getContentPane().add(tfdX);
        getContentPane().add(lblY);
        getContentPane().add(tfdY);

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
    private JLabel lblName;
    private JTextField tfdName;
    
    private PIMSeparator sptBounds;
    private JLabel lblWidth;
    private JTextField tfdWidth;
    private JLabel lblHeight;
    private JTextField tfdHeight;
    private JLabel lblX;
    private JTextField tfdX;
    private JLabel lblY;
    private JTextField tfdY;
    
    private PIMSeparator sptType;
    private JLabel lblCategory;
    private JComboBox<String> cmbCategory;

    private JButton ok;
    private JButton cancel;

	@Override
	public void componentResized(ComponentEvent e) {
		reLayout();
	}

	@Override
	public void componentMoved(ComponentEvent e) {}

	@Override
	public void componentShown(ComponentEvent e) {}

	@Override
	public void componentHidden(ComponentEvent e) {}
}
