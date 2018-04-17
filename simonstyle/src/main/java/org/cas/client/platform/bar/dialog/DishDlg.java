package org.cas.client.platform.bar.dialog;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pos.dialog.modifyuser.MUserSwichDlg;
import org.cas.client.platform.pos.dialog.modifyuser.ModifyPasswordDlg;
import org.cas.client.platform.product.ProductDefaultViews;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.OptionDlgConst;
import org.cas.client.resource.international.PaneConsts;

import com.jpos.POStest.POStestGUI;

/**
 * 微软的TextField的长度限制是228（约）， 本类的限制因为没有规格约束，暂定为912（约）
 */
public class DishDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
	//the position of current menu
	private int index;
	//all the name of the menu, used to validate the new input of the name.
	//@NOTE: if the category is changed, please rember to update the nameMetrix according to the new category.
	private String[][] nameMetrix;
	//for initializing the status of category combobox.
	private String activeCategory;
    private int[] categoryIdAry;
    private String[] categorySubjectAry;
    
	public void setIndex(int index) {
		this.index = index;
	}

	public void setNameMetrix(String[][] nameMetrix) {
		this.nameMetrix = nameMetrix;
	}
	
    public DishDlg(JFrame pFrame) {
        super(pFrame, false);
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
    	//name------------
        sptName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
        		(getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP)/2 - CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT + 2);
        
        lblLanguages[0].setBounds(CustOpts.HOR_GAP * 2, sptName.getY() + sptName.getHeight() + CustOpts.VER_GAP, lblLanguages[0].getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        tfdLanguages[0].setBounds(lblLanguages[0].getX() + lblLanguages[0].getWidth() + CustOpts.HOR_GAP,lblLanguages[0].getY(),
                sptName.getWidth() - lblLanguages[0].getWidth()  - CustOpts.HOR_GAP*2, CustOpts.BTN_HEIGHT);
        
        lblLanguages[1].setBounds(lblLanguages[0].getX(),lblLanguages[0].getY() + lblLanguages[0].getHeight() + CustOpts.VER_GAP,
        		lblLanguages[1].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLanguages[1].setBounds(lblLanguages[1].getX() + lblLanguages[1].getWidth() + CustOpts.HOR_GAP, lblLanguages[1].getY(), tfdLanguages[0].getWidth(), CustOpts.BTN_HEIGHT);
        
        lblLanguages[2].setBounds(lblLanguages[1].getX(), lblLanguages[1].getY() + lblLanguages[1].getHeight() + CustOpts.VER_GAP,
        		lblLanguages[2].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLanguages[2].setBounds(lblLanguages[2].getX() + lblLanguages[2].getWidth() + CustOpts.HOR_GAP, lblLanguages[2].getY(), tfdLanguages[0].getWidth(), CustOpts.BTN_HEIGHT);
        //price---------
        sptPrice.setBounds(sptName.getX() + sptName.getWidth() + CustOpts.HOR_GAP, sptName.getY(),
        		sptName.getWidth(), CustOpts.SEP_HEIGHT + 2);
        lblPrice.setBounds(sptPrice.getX() + CustOpts.HOR_GAP, sptPrice.getY() + sptPrice.getHeight() + CustOpts.VER_GAP,
        		lblPrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPrice.setBounds(lblPrice.getX() + lblPrice.getWidth() + CustOpts.HOR_GAP, lblPrice.getY(),
        		sptPrice.getWidth() - lblPrice.getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
        cbxQST.setBounds(tfdPrice.getX(), lblPrice.getY() + lblPrice.getHeight() + CustOpts.VER_GAP * 2, CustOpts.BTN_WIDTH,CustOpts.BTN_HEIGHT);
        cbxGST.setBounds(cbxQST.getX() + cbxQST.getWidth() + CustOpts.HOR_GAP * 3, cbxQST.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        //size--------
        sptSize.setBounds(sptName.getX(), tfdLanguages[2].getY() + tfdLanguages[2].getHeight() + CustOpts.VER_GAP,
        		sptName.getWidth(), CustOpts.SEP_HEIGHT + 2);
        rdbSize1.setBounds(sptSize.getX() + CustOpts.HOR_GAP, sptSize.getY() + sptSize.getHeight() + CustOpts.VER_GAP,
        		(sptSize.getWidth() - CustOpts.HOR_GAP * 2)/3 - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        rdbSize2.setBounds(rdbSize1.getX() + rdbSize1.getWidth() + CustOpts.HOR_GAP, rdbSize1.getY(), rdbSize1.getWidth(), CustOpts.BTN_HEIGHT);
        rdbSize3.setBounds(rdbSize2.getX() + rdbSize1.getWidth() + CustOpts.HOR_GAP, rdbSize1.getY(), rdbSize1.getWidth(), CustOpts.BTN_HEIGHT);
        rdbSize4.setBounds(rdbSize1.getX(), rdbSize1.getY() + rdbSize1.getHeight() + CustOpts.VER_GAP, rdbSize1.getWidth(), CustOpts.BTN_HEIGHT);
        rdbSize5.setBounds(rdbSize2.getX(), rdbSize4.getY(), rdbSize1.getWidth(), CustOpts.BTN_HEIGHT);
        rdbSize6.setBounds(rdbSize3.getX(), rdbSize4.getY(), rdbSize1.getWidth(), CustOpts.BTN_HEIGHT);
        //printers--------
        sptPrinter.setBounds(sptPrice.getX(), sptSize.getY(), sptPrice.getWidth(), CustOpts.SEP_HEIGHT + 2);
        cbxPrinter1.setBounds(sptPrinter.getX() + CustOpts.HOR_GAP, sptPrinter.getY() + sptPrinter.getHeight() + CustOpts.VER_GAP,
        		(sptPrinter.getWidth() - CustOpts.HOR_GAP * 2)/3 - CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        cbxPrinter2.setBounds(cbxPrinter1.getX() + cbxPrinter1.getWidth() + CustOpts.HOR_GAP, cbxPrinter1.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter3.setBounds(cbxPrinter2.getX() + cbxPrinter1.getWidth() + CustOpts.HOR_GAP, cbxPrinter1.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter4.setBounds(cbxPrinter1.getX(), cbxPrinter1.getY() + cbxPrinter1.getHeight() + CustOpts.VER_GAP, cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter5.setBounds(cbxPrinter2.getX(), cbxPrinter4.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinter6.setBounds(cbxPrinter3.getX(), cbxPrinter4.getY(), cbxPrinter1.getWidth(), CustOpts.BTN_HEIGHT);
        //other-----------
        sptOther.setBounds(sptSize.getX(), cbxPrinter6.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sptPrinter.getWidth() * 2, CustOpts.SEP_HEIGHT + 2);
        lblCategory.setBounds(sptOther.getX() + CustOpts.HOR_GAP, sptOther.getY() + sptOther.getHeight() + CustOpts.VER_GAP,
        		lblCategory.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cmbCategory.setBounds(lblCategory.getX() + lblCategory.getWidth() + CustOpts.HOR_GAP, lblCategory.getY(),
        		sptName.getWidth() - lblCategory.getWidth() - CustOpts.HOR_GAP * 2, CustOpts.BTN_HEIGHT);
        cbxPricePomp.setBounds(cbxPrinter4.getX(), cmbCategory.getY(), cbxPricePomp.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        cbxMenuPomp.setBounds(cbxPrinter5.getX(), cbxPricePomp.getY(),
        		cbxMenuPomp.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cbxModifyPomp.setBounds(cbxPrinter6.getX(), cbxPricePomp.getY(),
        		cbxModifyPomp.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        
        ok.setBounds(getWidth()/2 - CustOpts.HOR_GAP  - CustOpts.BTN_WIDTH,
        		cbxMenuPomp.getY() + cbxMenuPomp.getHeight() + CustOpts.VER_GAP * 3, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cancel.setBounds(ok.getWidth() + ok.getX() + CustOpts.HOR_GAP * 2, ok.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        
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

    //if before is "", then it's consiered as not empty before.
    private boolean isMenuNameModified(int lang) {
    	
    	boolean isNotInitYet = index - 1 >= nameMetrix[lang].length;
    	String oldText = isNotInitYet ? null : nameMetrix[lang][index-1];
    	boolean isEmptyBefore = oldText == null || oldText.length() == 0;
    	String newText = tfdLanguages[lang].getText();
    	boolean isEmptyNow = newText == null || newText.length() == 0;
    	
    	if((isNotInitYet || isEmptyBefore) && isEmptyNow) {		//if empty before, and empty now, return false
    		return false;
    	}else if((isNotInitYet || isEmptyBefore) && !isEmptyNow) {//if empty before, not empty now, return true
    		return true;
    	}else if(isEmptyNow) {									  //if not empty before, empty now, return true
    		return true;
    	}else {													  //if not empty before, not empty now, compare!
    		return !newText.equals(oldText);
    	}
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
        	// validate input=================
        	//name check @NOTE: index is start from 1!!!
        	if(tfdLanguages[0].getText() == null || tfdLanguages[0].getText().length() < 1) {
        		JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
        		tfdLanguages[0].grabFocus();
        		return;
        	}else if(isMenuNameModified(0)) {
        		for(int i = 0; i < nameMetrix[0].length; i++) {
        			if(i != index && tfdLanguages[0].getText().equalsIgnoreCase(nameMetrix[0][i])) {
        				JOptionPane.showMessageDialog(this, BarDlgConst.DuplicatedInput);
                		tfdLanguages[0].grabFocus();
                		return;
        			}
        		}
        	}else if(isMenuNameModified(1)) {
        		if(!"".equals(tfdLanguages[1].getText()))
	        		for(int i = 0; i < nameMetrix[1].length; i++) {
	        			if(i != index && tfdLanguages[1].getText().equalsIgnoreCase(nameMetrix[1][i])) {
	        				JOptionPane.showMessageDialog(this, BarDlgConst.DuplicatedInput);
	                		tfdLanguages[1].grabFocus();
	                		return;
	        			}
	        		}
        	}else if(isMenuNameModified(2)) {
        		if(!"".equals(tfdLanguages[2].getText()))
	        		for(int i = 0; i < nameMetrix[2].length; i++) {
	        			if(i != index && tfdLanguages[2].getText().equalsIgnoreCase(nameMetrix[2][i])) {
	        				JOptionPane.showMessageDialog(this, BarDlgConst.DuplicatedInput);
	                		tfdLanguages[2].grabFocus();
	                		return;
	        			}
	        		}
        	}
        	
        	//price check
            float tPrice;
            try {
                tPrice = Float.parseFloat(tfdPrice.getText());
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdPrice.grabFocus();
                tfdPrice.selectAll();
                return;
            }
            
            //TODO update the product record into db.
            
            dispose();
        } else if(o == cancel){
        	dispose();
        } else if (o == cbxQST) {
            tfdPrice.setEnabled(!cbxQST.isSelected());
        } else if (o == cbxGST) {
            // 开个线程检查是否有连接。
        } else if (o == cbxPricePomp) {
            // 开个线程检查是否有连接。
        } else if (o == cbxPrinter1) {
            // 开个线程检查是否有连接。
        }
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarDlgConst.Menu);
        setResizable(false);
        // 初始化－－－－－－－－－－－－－－－－
        sptName = new PIMSeparator(BarDlgConst.Name);
        lblLanguages = new JLabel[3];
        tfdLanguages = new JTextField[3];
        lblLanguages[0] = new JLabel(BarDlgConst.Language1);
        tfdLanguages[0] = new JTextField();
        lblLanguages[1] = new JLabel(BarDlgConst.Language2);
        tfdLanguages[1] = new JTextField();
        lblLanguages[2] = new JLabel(BarDlgConst.Language3);
        tfdLanguages[2] = new JTextField();

        sptPrice = new PIMSeparator(BarDlgConst.PRICE);
        lblPrice = new JLabel(BarDlgConst.PRICE);
        tfdPrice = new JTextField();
        cbxGST = new JCheckBox(BarDlgConst.GST);
        cbxQST = new JCheckBox(BarDlgConst.QST);

        sptSize = new PIMSeparator(BarDlgConst.Size);
        rdbSize1 = new JRadioButton(BarDlgConst.Size1);
        rdbSize2 = new JRadioButton(BarDlgConst.Size2);
        rdbSize3 = new JRadioButton(BarDlgConst.Size3);
        rdbSize4 = new JRadioButton(BarDlgConst.Size4);
        rdbSize5 = new JRadioButton(BarDlgConst.Size5);
        rdbSize6 = new JRadioButton(BarDlgConst.Size6);
        ButtonGroup group = new ButtonGroup();
        group.add(rdbSize1);
        group.add(rdbSize2);
        group.add(rdbSize3);
        group.add(rdbSize4);
        group.add(rdbSize5);
        group.add(rdbSize6);
        
        sptPrinter = new PIMSeparator(BarDlgConst.PRINTER);
        cbxPrinter1 = new JCheckBox(BarDlgConst.Printer1);
        cbxPrinter2 = new JCheckBox(BarDlgConst.Printer2);
        cbxPrinter3 = new JCheckBox(BarDlgConst.Printer3);
        cbxPrinter4 = new JCheckBox(BarDlgConst.Printer4);
        cbxPrinter5 = new JCheckBox(BarDlgConst.Printer5);
        cbxPrinter6 = new JCheckBox(BarDlgConst.Printer6);
        
        
        sptOther = new PIMSeparator(OptionDlgConst.OPTION_OTHER);
        cbxPricePomp = new JCheckBox(BarDlgConst.PricePomp);
        cbxMenuPomp = new JCheckBox(BarDlgConst.MenuPomp);
        cbxModifyPomp = new JCheckBox(BarDlgConst.ModifyPomp);
        lblCategory = new JLabel(BarDlgConst.Categary);
        cmbCategory = new JComboBox<String>();

        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－
        ok.setMnemonic('o');
        ok.setMargin(new Insets(0, 0, 0, 0));

        setBounds((CustOpts.SCRWIDTH - 280) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 680, 300); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        getRootPane().setDefaultButton(ok);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(sptName);
        getContentPane().add(lblLanguages[0]);
        getContentPane().add(tfdLanguages[0]);
        getContentPane().add(lblLanguages[1]);
        getContentPane().add(tfdLanguages[1]);
        getContentPane().add(lblLanguages[2]);
        getContentPane().add(tfdLanguages[2]);
        
        getContentPane().add(sptPrice);
        getContentPane().add(lblPrice);
        getContentPane().add(tfdPrice);
        getContentPane().add(cbxQST);
        getContentPane().add(cbxGST);
        
        getContentPane().add(sptSize);
        getContentPane().add(rdbSize1);
        getContentPane().add(rdbSize2);
        getContentPane().add(rdbSize3);
        getContentPane().add(rdbSize4);
        getContentPane().add(rdbSize5);
        getContentPane().add(rdbSize6);
        
        getContentPane().add(sptPrinter);
        getContentPane().add(cbxPrinter1);
        getContentPane().add(cbxPrinter2);
        getContentPane().add(cbxPrinter3);
        getContentPane().add(cbxPrinter4);
        getContentPane().add(cbxPrinter5);
        getContentPane().add(cbxPrinter6);

        getContentPane().add(sptOther);
        getContentPane().add(lblCategory);
        getContentPane().add(cmbCategory);
        getContentPane().add(cbxPricePomp);
        getContentPane().add(cbxMenuPomp);
        getContentPane().add(cbxModifyPomp);

        getContentPane().add(cancel);
        getContentPane().add(ok);

        // 加监听器－－－－－－－－
        cmbCategory.addActionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
        cbxQST.addActionListener(this);
        cbxGST.addActionListener(this);
        cbxPricePomp.addActionListener(this);
        getContentPane().addComponentListener(this);

        // Content
        cbxQST.setSelected(true);
        cbxGST.setSelected(true);
        rdbSize1.setSelected(true);
        cbxPrinter1.setSelected(true);
        initCategory();
    }
    
    public void initCategory() {
        try {
            Connection connection = PIMDBModel.getConection();
            Statement statement =
                    connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            
            //load all the categorys---------------------------
            ResultSet categoryRS = statement.executeQuery("select ID, NAME from CATEGORY order by DSP_INDEX");
            categoryRS.afterLast();
            categoryRS.relative(-1);
            int tmpPos = categoryRS.getRow();
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
        
        if(categorySubjectAry.length > 0) {
            cmbCategory.setModel(new DefaultComboBoxModel(categorySubjectAry));
        }
    }

	public void setActiveCategory(String activeCategory) {
		this.activeCategory = activeCategory;
		if(activeCategory != null) {
			cmbCategory.setSelectedItem(activeCategory);
		}
	}

	private PIMSeparator sptName;
    private JLabel[] lblLanguages;
    private JTextField[] tfdLanguages;

    private PIMSeparator sptPrice;
    private JLabel lblPrice;
    private JTextField tfdPrice;
    private JCheckBox cbxGST;
    private JCheckBox cbxQST;

    private PIMSeparator sptSize;
    private JRadioButton rdbSize1;
    private JRadioButton rdbSize2;
    private JRadioButton rdbSize3;
    private JRadioButton rdbSize4;
    private JRadioButton rdbSize5;
    private JRadioButton rdbSize6;
    
    private PIMSeparator sptPrinter;
    private JCheckBox cbxPrinter1;
    private JCheckBox cbxPrinter2;
    private JCheckBox cbxPrinter3;
    private JCheckBox cbxPrinter4;
    private JCheckBox cbxPrinter5;
    private JCheckBox cbxPrinter6;

    private PIMSeparator sptOther;
    private JLabel lblCategory;
    private JComboBox<String> cmbCategory;
    private JCheckBox cbxPricePomp;
    private JCheckBox cbxMenuPomp;
    private JCheckBox cbxModifyPomp;
    
    private JButton ok;
    private JButton cancel;
}
