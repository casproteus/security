package org.cas.client.platform.bar.dialog;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.CategoryDialogConstants;
import org.cas.client.resource.international.DlgConst;

public class CategoryDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
	MenuPanel menuPanel;
    int dspIndex;

    public CategoryDlg(BarFrame pParent) {
        super(pParent, true);
        menuPanel = pParent.menuPanel;
        initDialog();
    }

    public void setIndex(
            int dspIndex) {
        this.dspIndex = dspIndex;
        this.general.tfdDspIndex.setText(String.valueOf(dspIndex));
        if(dspIndex <= menuPanel.categoryNameMetrix[0].length) {
        	this.general.tfdCategoryNames[0].setText(menuPanel.categoryNameMetrix[0][dspIndex - 1]);
        	this.general.tfdCategoryNames[1].setText(menuPanel.categoryNameMetrix[1][dspIndex - 1]);
        	this.general.tfdCategoryNames[2].setText(menuPanel.categoryNameMetrix[2][dspIndex - 1]);
        }
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
    	delete.setBounds(getContainer().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, 
        		getContainer().getHeight() - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP, 
        		CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        cancel.setBounds(delete.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, delete.getY(), 
        		CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), 
        		CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        general.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getContainer().getWidth() - 2 * CustOpts.HOR_GAP,
                (ok.getY()) - 2 * CustOpts.VER_GAP);
        general.componentResized(null);
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
        delete.removeActionListener(this);
        
        if (general != null) {
            general.removeAll();
            general = null;
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
        Object o = e.getSource();
        if (o == ok) {
            String text = general.tfdCategoryNames[0].getText();

            // name check ----------------------------------
            if (text == null || text.length() < 1) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                general.tfdCategoryNames[0].grabFocus();
                return;
            } 

            if (isCategoryNameModified(0)) {
                for (int i = 0; i < menuPanel.dishNameMetrix[0].length; i++) {
                    if (i != dspIndex - 1 && text.equalsIgnoreCase(menuPanel.dishNameMetrix[0][i])) {
                        JOptionPane.showMessageDialog(this, BarFrame.consts.DuplicatedInput());
                        general.tfdCategoryNames[0].grabFocus();
                        return;
                    }
                }
            }

            if (isCategoryNameModified(1)) {
            	text = general.tfdCategoryNames[1].getText();
                if (text != null && !"".equals(text))//language2 is allowed to be empty.
                    for (int i = 0; i < menuPanel.dishNameMetrix[1].length; i++) {
                        if (i != dspIndex - 1 && text.equalsIgnoreCase(menuPanel.dishNameMetrix[1][i])) {
                            JOptionPane.showMessageDialog(this, BarFrame.consts.DuplicatedInput());
                            general.tfdCategoryNames[1].grabFocus();
                            return;
                        }
                    }
            } 

            if (isCategoryNameModified(2)) {
            	text = general.tfdCategoryNames[2].getText();
                if (text != null && !"".equals(text))//language3 is allowed to be empty.
                    for (int i = 0; i < menuPanel.dishNameMetrix[2].length; i++) {
                        if (i != dspIndex - 1 && text.equalsIgnoreCase(menuPanel.dishNameMetrix[2][i])) {
                            JOptionPane.showMessageDialog(this, BarFrame.consts.DuplicatedInput());
                            general.tfdCategoryNames[2].grabFocus();
                            return;
                        }
                    }
            }
            
            try {
                Statement smt = PIMDBModel.getStatement();

                //adjust affected category's Index
                int newIndex = Integer.valueOf(general.tfdDspIndex.getText()); // display must be a integer
                if (newIndex > dspIndex) {
                	for (int i = dspIndex + 1; i <= newIndex; i++) { // make index smaller
                    	String sql = "UPDATE Category SET DSP_INDEX = ".concat(String.valueOf(i - 1))
                        	.concat(" where DSP_INDEX = ").concat(String.valueOf(i)).concat("");
                        smt.executeUpdate(sql.toString());
                    }
                } else if (newIndex < dspIndex){
                    for (int i = dspIndex - 1; i >= newIndex; i--) { // make index bigger @NOTE: have adjust from top to down.
                    	String sql = "UPDATE Category SET DSP_INDEX = ".concat(String.valueOf(i + 1))
                        	.concat(" where DSP_INDEX = ").concat(String.valueOf(i)).concat("");
                        smt.executeUpdate(sql.toString());
                    }
                }
                
              //start to save to db-----	if name was not null, it's an update, otherwise, an insert
                String sql = dspIndex <= menuPanel.categoryNameMetrix[0].length 
                		&& menuPanel.categoryNameMetrix[0][dspIndex - 1] != null 
                		? 
                		"UPDATE Category SET LANG1 = '".concat(general.tfdCategoryNames[0].getText())
                    	.concat("', LANG2 = '").concat(general.tfdCategoryNames[1].getText())
                    	.concat("', LANG3 = '").concat(general.tfdCategoryNames[2].getText())
                    	.concat("', DSP_INDEX = ").concat(general.tfdDspIndex.getText())
                    	.concat(" where LANG1 = '").concat(menuPanel.categoryNameMetrix[0][dspIndex - 1]).concat("'")
                    	:
                    	"INSERT INTO Category(LANG1, LANG2, LANG3, DSP_INDEX) VALUES('"
                    		.concat(general.tfdCategoryNames[0].getText()).concat("', '")
                    		.concat(general.tfdCategoryNames[1].getText()).concat("', '")
                    		.concat(general.tfdCategoryNames[2].getText()).concat("', ")
                    		.concat(general.tfdDspIndex.getText()).concat(")")	;

                smt.executeUpdate(sql.toString());
                smt.close();
                smt = null;
                menuPanel.initCategoryAndDishes();
                menuPanel.reLayout();
                dispose();
            } catch (Exception exception) {
            	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                exception.printStackTrace();
                return;
            }
        } else if (o == cancel) {
            dispose();
        } else if (o == delete) {
        	String category = menuPanel.categoryNameMetrix[0][dspIndex - 1];
        	if(category == null)
        		return;
        	
        	if (JOptionPane.showConfirmDialog(this, BarFrame.consts.COMFIRMDELETEACTION2(), BarFrame.consts.Operator(),
                    JOptionPane.YES_NO_OPTION) != 0)// 确定删除吗？
                return;
                        
            try {
                Statement smt = PIMDBModel.getStatement();

                // insert the product record into db.==========================
                StringBuilder sql = new StringBuilder("delete from product where CATEGORY = '").append(category).append("'");
                smt.executeUpdate(sql.toString());
                
                //start to save to db-----	if name was not null, it's an update, otherwise, an insert
                sql = new StringBuilder("delete from Category where LANG1 = '").append(category).append("'");
                    	
                smt.executeUpdate(sql.toString());
                smt.close();
                smt = null;
                menuPanel.initCategoryAndDishes();
                menuPanel.reLayout();
                dispose();
            } catch (Exception exception) {
            	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                exception.printStackTrace();
                return;
            }
        }
    }
    
    private boolean isCategoryNameModified(int lang) {

        boolean isNotInitYet = dspIndex - 1 >= menuPanel.categoryNameMetrix[lang].length;
        
        String oldText = isNotInitYet ? null : menuPanel.categoryNameMetrix[lang][dspIndex - 1];
        boolean isEmptyBefore = oldText == null || oldText.length() == 0;
        
        String newText = general.tfdCategoryNames[lang].getText();
        boolean isEmptyNow = newText == null || newText.length() == 0;

        if ((isNotInitYet || isEmptyBefore) && isEmptyNow) { // if empty before, and empty now, return false
            return false;
        } else if ((isNotInitYet || isEmptyBefore) && !isEmptyNow) {// if empty before, not empty now, return true
            return true;
        } else if (isEmptyNow) { // if not empty before, empty now, return true
            return true;
        } else { // if not empty before, not empty now, compare!
            return !newText.equals(oldText);
        }
    }
    
    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(CategoryDialogConstants.CATEGORYEditorTITLE);
        setModal(true);
        setResizable(false);

        // 初始化－－－－－－－－－－－－－－－－
        general = new GeneralPanel();
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);
        delete = new JButton(DlgConst.DELETE);

        // 属性设置－－－－－－－－－－－－－－
        ok.setFocusable(false);
        cancel.setFocusable(false);
        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getMargin());
        delete.setMargin(ok.getMargin());
        getRootPane().setDefaultButton(ok);
        // 布局---------------
        int tHight = general.getPreferredSize().height + CustOpts.BTN_HEIGHT + 2 * CustOpts.VER_GAP + CustOpts.SIZE_EDGE
                        + CustOpts.SIZE_TITLE;
        setBounds((CustOpts.SCRWIDTH - 260) / 2, (CustOpts.SCRHEIGHT - tHight) / 2, 260, tHight); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        // 搭建－－－－－－－－－－－－－
        getContentPane().add(general);
        getContentPane().add(ok);
        getContentPane().add(cancel);
        getContentPane().add(delete);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        cancel.addActionListener(this);
        delete.addActionListener(this);
        
        getContentPane().addComponentListener(this);
    }

    private JButton ok;
    private JButton cancel;
    private JButton delete;
    private GeneralPanel general;

    // ==========================================================================
    private class GeneralPanel extends JPanel implements ComponentListener {

        public GeneralPanel() {
            initConponent();
        }

        /** Invoked when the component's size changes. */
        @Override
        public void componentResized(
                ComponentEvent e) {
            reLayout();
        }

        /** Invoked when the component's position changes. */
        @Override
        public void componentMoved(
                ComponentEvent e) {
        }

        /** Invoked when the component has been made visible. */
        @Override
        public void componentShown(
                ComponentEvent e) {
        }

        /** Invoked when the component has been made invisible. */
        @Override
        public void componentHidden(
                ComponentEvent e) {
        }

        private void initConponent() {
        	lblCategoryNames =  new JLabel[3];
            lblCategoryNames[0] = new JLabel(BarFrame.consts.Language1());
            lblCategoryNames[1] = new JLabel(BarFrame.consts.Language2());
            lblCategoryNames[2] = new JLabel(BarFrame.consts.Language3());
            lblPosition = new JLabel(BarFrame.consts.DSPINDEX());

            tfdCategoryNames = new JTextField[3];
            tfdCategoryNames[0] = new JTextField();
            tfdCategoryNames[1] = new JTextField();
            tfdCategoryNames[2] = new JTextField();
            tfdDspIndex = new JTextField();

            // properties
            setLayout(null);

            initContent();
            reLayout();
            // built
            add(lblCategoryNames[0]);
            add(lblCategoryNames[1]);
            add(lblCategoryNames[2]);
            add(tfdCategoryNames[0]);
            add(tfdCategoryNames[1]);
            add(tfdCategoryNames[2]);
            add(lblPosition);
            add(tfdDspIndex);
            addComponentListener(this);
        }

        private void initContent() {
            String sql = "select ID, LANG1, LANG2, LANG3, DSP_INDEX from CATEGORY where DSP_INDEX >= 0 order by DSP_INDEX";
            try {
                ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
                ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

                rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                idAry = new int[tmpPos];
                categoryNameMetrix = new String[3][tmpPos];
                indexAry = new int[tmpPos];
                rs.beforeFirst();
                tmpPos = 0;
                while (rs.next()) {
                    idAry[tmpPos] = rs.getInt("id");
                    categoryNameMetrix[0][tmpPos] = rs.getString("LANG1");
                    categoryNameMetrix[1][tmpPos] = rs.getString("LANG2");
                    categoryNameMetrix[2][tmpPos] = rs.getString("LANG3");
                    indexAry[tmpPos] = rs.getInt("DSP_INDEX");
                    tmpPos++;
                }
                rs.close();// 关闭
            } catch (SQLException e) {
                ErrorUtil.write(e);
            }
        }

        /** 本方法用于设置View上各个组件的尺寸。 */
        public void reLayout() {
        	
            int prmWidth = getWidth();
            
           lblCategoryNames[0].setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCategoryNames[0].getPreferredSize().width,CustOpts.BTN_HEIGHT);
           tfdCategoryNames[0].setBounds(lblCategoryNames[0].getX() + lblCategoryNames[0].getWidth() + CustOpts.HOR_GAP, lblCategoryNames[0].getY(), 
                    prmWidth - lblCategoryNames[0].getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
            
           lblCategoryNames[1].setBounds(CustOpts.HOR_GAP, lblCategoryNames[0].getY() + lblCategoryNames[0].getHeight() + CustOpts.VER_GAP, 
        		   lblCategoryNames[0].getPreferredSize().width,CustOpts.BTN_HEIGHT);
           tfdCategoryNames[1].setBounds(lblCategoryNames[1].getX() + lblCategoryNames[1].getWidth() + CustOpts.HOR_GAP, lblCategoryNames[1].getY(), 
                    prmWidth - lblCategoryNames[1].getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
            
           lblCategoryNames[2].setBounds(CustOpts.HOR_GAP, lblCategoryNames[1].getY() + lblCategoryNames[1].getHeight() + CustOpts.VER_GAP, 
        		   lblCategoryNames[0].getPreferredSize().width,CustOpts.BTN_HEIGHT);
           tfdCategoryNames[2].setBounds(lblCategoryNames[2].getX() + lblCategoryNames[2].getWidth() + CustOpts.HOR_GAP, lblCategoryNames[2].getY(), 
                    prmWidth - lblCategoryNames[2].getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
            
            lblPosition.setBounds(lblCategoryNames[2].getX(), lblCategoryNames[2].getY() + lblCategoryNames[2].getHeight() + CustOpts.VER_GAP,
                    lblCategoryNames[2].getWidth(), CustOpts.BTN_HEIGHT);
            
            tfdDspIndex.setBounds(tfdCategoryNames[2].getX(), lblPosition.getY(), tfdCategoryNames[2].getWidth(), CustOpts.BTN_HEIGHT);

            setPreferredSize(new Dimension(getWidth(), tfdDspIndex.getY() + tfdDspIndex.getHeight() + CustOpts.VER_GAP));
        }

        JLabel[] lblCategoryNames;
        JLabel lblPosition;

        JTextField[] tfdCategoryNames;
        JTextField tfdDspIndex;

        int[] idAry;
        String[][] categoryNameMetrix;
        int[] indexAry;
    }
}
