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

import org.cas.client.platform.bar.beans.CategoryToggle;
import org.cas.client.platform.bar.beans.MenuButton;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
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
public class DishDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
    public boolean ADDED;
    private int prodID = -1;
    //@NOTE this is the position on screen, could be different than the index in dish
    //e.g. index in dish could be not lined.
    private int dspIndex;
    private Dish dish;

    // all the name of the menu, used to validate the new input of the name.
    // @NOTE: if the category is changed, please rember to update the nameMetrix according to the new category.
    // @NOTE: this is the model Metrix, not the onscreen metrix, it's using metrix because each dish has 3 names.

    // for initializing the status of category combobox.
    private String activeCategory;
    private int[] categoryIdAry;
    private String[] categorySubjectAry;
    
    private BarGeneralPanel barGeneralPanel;
    
    public DishDlg(BarFrame pFrame, int dspIndex) {
    	super(pFrame, false);
        this.barGeneralPanel = pFrame.general;
        this.dspIndex = dspIndex;
        initDialog();
    }

    public DishDlg(BarFrame pFrame, Dish dish) {
        super(pFrame, false);
        this.barGeneralPanel = pFrame.general;
        this.dish = dish;
    	this.prodID = dish.getId();
        this.dspIndex = dish.getDspIndex();
        initDialog();
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        // name------------
        sptName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, (getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP)
                / 2 - CustOpts.HOR_GAP, CustOpts.SEP_HEIGHT + 2);

        lblLanguages[0].setBounds(CustOpts.HOR_GAP * 2, sptName.getY() + sptName.getHeight() + CustOpts.VER_GAP,
                lblLanguages[0].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLanguages[0].setBounds(lblLanguages[0].getX() + lblLanguages[0].getWidth() + CustOpts.HOR_GAP,
                lblLanguages[0].getY(), sptName.getWidth() - lblLanguages[0].getWidth() - CustOpts.HOR_GAP * 2,
                CustOpts.BTN_HEIGHT);

        lblLanguages[1].setBounds(lblLanguages[0].getX(), lblLanguages[0].getY() + lblLanguages[0].getHeight()
                + CustOpts.VER_GAP, lblLanguages[1].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLanguages[1].setBounds(lblLanguages[1].getX() + lblLanguages[1].getWidth() + CustOpts.HOR_GAP,
                lblLanguages[1].getY(), tfdLanguages[0].getWidth(), CustOpts.BTN_HEIGHT);

        lblLanguages[2].setBounds(lblLanguages[1].getX(), lblLanguages[1].getY() + lblLanguages[1].getHeight()
                + CustOpts.VER_GAP, lblLanguages[2].getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdLanguages[2].setBounds(lblLanguages[2].getX() + lblLanguages[2].getWidth() + CustOpts.HOR_GAP,
                lblLanguages[2].getY(), tfdLanguages[0].getWidth(), CustOpts.BTN_HEIGHT);
        // price---------
        sptPrice.setBounds(sptName.getX() + sptName.getWidth() + CustOpts.HOR_GAP, sptName.getY(), sptName.getWidth(),
                CustOpts.SEP_HEIGHT + 2);
        lblPrice.setBounds(sptPrice.getX() + CustOpts.HOR_GAP, sptPrice.getY() + sptPrice.getHeight()
                + CustOpts.VER_GAP, lblPrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdPrice.setBounds(lblPrice.getX() + lblPrice.getWidth() + CustOpts.HOR_GAP, lblPrice.getY(),
                sptPrice.getWidth() - lblPrice.getWidth() - CustOpts.HOR_GAP * 3, CustOpts.BTN_HEIGHT);
        cbxQST.setBounds(tfdPrice.getX(), lblPrice.getY() + lblPrice.getHeight() + CustOpts.VER_GAP * 2,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        cbxGST.setBounds(cbxQST.getX() + cbxQST.getWidth() + CustOpts.HOR_GAP * 3, cbxQST.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        // size--------
        sptSize.setBounds(sptName.getX(), tfdLanguages[2].getY() + tfdLanguages[2].getHeight() + CustOpts.VER_GAP,
                sptName.getWidth(), CustOpts.SEP_HEIGHT + 2);
        rdbSizes[0].setBounds(sptSize.getX() + CustOpts.HOR_GAP, sptSize.getY() + sptSize.getHeight()
                + CustOpts.VER_GAP, (sptSize.getWidth() - CustOpts.HOR_GAP * 2) / 3 - CustOpts.HOR_GAP,
                CustOpts.BTN_HEIGHT);
        rdbSizes[1].setBounds(rdbSizes[0].getX() + rdbSizes[0].getWidth() + CustOpts.HOR_GAP, rdbSizes[0].getY(),
                rdbSizes[0].getWidth(), CustOpts.BTN_HEIGHT);
        rdbSizes[2].setBounds(rdbSizes[1].getX() + rdbSizes[0].getWidth() + CustOpts.HOR_GAP, rdbSizes[0].getY(),
                rdbSizes[0].getWidth(), CustOpts.BTN_HEIGHT);
        rdbSizes[3].setBounds(rdbSizes[0].getX(), rdbSizes[0].getY() + rdbSizes[0].getHeight() + CustOpts.VER_GAP,
                rdbSizes[0].getWidth(), CustOpts.BTN_HEIGHT);
        rdbSizes[4].setBounds(rdbSizes[1].getX(), rdbSizes[3].getY(), rdbSizes[0].getWidth(), CustOpts.BTN_HEIGHT);
        rdbSizes[5].setBounds(rdbSizes[2].getX(), rdbSizes[3].getY(), rdbSizes[0].getWidth(), CustOpts.BTN_HEIGHT);
        // printers--------
        sptPrinter.setBounds(sptPrice.getX(), sptSize.getY(), sptPrice.getWidth(), CustOpts.SEP_HEIGHT + 2);
        cbxPrinters[0].setBounds(sptPrinter.getX() + CustOpts.HOR_GAP, sptPrinter.getY() + sptPrinter.getHeight()
                + CustOpts.VER_GAP, (sptPrinter.getWidth() - CustOpts.HOR_GAP * 2) / 3 - CustOpts.HOR_GAP,
                CustOpts.BTN_HEIGHT);
        cbxPrinters[1].setBounds(cbxPrinters[0].getX() + cbxPrinters[0].getWidth() + CustOpts.HOR_GAP,
                cbxPrinters[0].getY(), cbxPrinters[0].getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinters[2].setBounds(cbxPrinters[1].getX() + cbxPrinters[0].getWidth() + CustOpts.HOR_GAP,
                cbxPrinters[0].getY(), cbxPrinters[0].getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinters[3].setBounds(cbxPrinters[0].getX(), cbxPrinters[0].getY() + cbxPrinters[0].getHeight()
                + CustOpts.VER_GAP, cbxPrinters[0].getWidth(), CustOpts.BTN_HEIGHT);
        cbxPrinters[4].setBounds(cbxPrinters[1].getX(), cbxPrinters[3].getY(), cbxPrinters[0].getWidth(),
                CustOpts.BTN_HEIGHT);
        cbxPrinters[5].setBounds(cbxPrinters[2].getX(), cbxPrinters[3].getY(), cbxPrinters[0].getWidth(),
                CustOpts.BTN_HEIGHT);
        // other-----------
        sptOther.setBounds(sptSize.getX(), cbxPrinters[5].getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                sptPrinter.getWidth() * 2, CustOpts.SEP_HEIGHT + 2);
        lblCategory.setBounds(sptOther.getX() + CustOpts.HOR_GAP, sptOther.getY() + sptOther.getHeight()
                + CustOpts.VER_GAP, lblCategory.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        cmbCategory.setBounds(lblCategory.getX() + lblCategory.getWidth() + CustOpts.HOR_GAP, lblCategory.getY(),
                sptName.getWidth() - lblCategory.getWidth() - lblDspIndex.getPreferredSize().width - CustOpts.HOR_GAP
                        * 4 - 40, CustOpts.BTN_HEIGHT);

        lblDspIndex.setBounds(cmbCategory.getX() + cmbCategory.getWidth() + CustOpts.HOR_GAP, cmbCategory.getY(),
                lblDspIndex.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        tfdDspIndex.setBounds(lblDspIndex.getX() + lblDspIndex.getWidth() + CustOpts.HOR_GAP, lblDspIndex.getY(), 40,
                CustOpts.BTN_HEIGHT);

        cbxPricePomp.setBounds(cbxPrinters[3].getX(), cmbCategory.getY(), cbxPricePomp.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        cbxMenuPomp.setBounds(cbxPrinters[4].getX(), cbxPricePomp.getY(), cbxMenuPomp.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        cbxModifyPomp.setBounds(cbxPrinters[5].getX(), cbxPricePomp.getY(), cbxModifyPomp.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);

        ok.setBounds(getWidth() / 2 - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH,
                cbxMenuPomp.getY() + cbxMenuPomp.getHeight() + CustOpts.VER_GAP * 3, CustOpts.BTN_WIDTH,
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

    private boolean isMenuNameModified(int lang) {

        boolean isNotInitYet = dspIndex - 1 >= barGeneralPanel.onScrMenuNameMetrix[lang].length;
        
        String oldText = isNotInitYet ? null : barGeneralPanel.onScrMenuNameMetrix[lang][dspIndex - 1];
        boolean isEmptyBefore = oldText == null || oldText.length() == 0;
        
        String newText = tfdLanguages[lang].getText();
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
            //category check ----------
        	//@Note: must be done first--if category changed, the array to compare/adjust will be different.
        	String newCategory = cmbCategory.getSelectedItem().toString();
        	if(!newCategory.equals(activeCategory)) {
        		barGeneralPanel.activeCategoryButton.setSelected(false);
        		//find new button.
        		for (ArrayList<CategoryToggle> btns : barGeneralPanel.onSrcCategoryMatrix) {
					for(CategoryToggle btn : btns) {
						if (newCategory.equals(btn.getText())){
			        		barGeneralPanel.activeCategoryButton = btn;
						}
					}
				}
        	}
        	
            // name check ----------------------------------
            if (tfdLanguages[0].getText() == null || tfdLanguages[0].getText().length() < 1) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdLanguages[0].grabFocus();
                return;
            } 

            if (isMenuNameModified(0)) {
                for (int i = 0; i < barGeneralPanel.menuNameMetrix[0].length; i++) {
                    if (i != dspIndex && tfdLanguages[0].getText().equalsIgnoreCase(barGeneralPanel.menuNameMetrix[0][i])) {
                        JOptionPane.showMessageDialog(this, BarDlgConst.DuplicatedInput);
                        tfdLanguages[0].grabFocus();
                        return;
                    }
                }
            }

            if (isMenuNameModified(1)) {
            	String text = tfdLanguages[1].getText();
                if (text != null && !"".equals(text))//language2 is allowed to be empty.
                    for (int i = 0; i < barGeneralPanel.menuNameMetrix[1].length; i++) {
                        if (i != dspIndex && text.equalsIgnoreCase(barGeneralPanel.menuNameMetrix[1][i])) {
                            JOptionPane.showMessageDialog(this, BarDlgConst.DuplicatedInput);
                            tfdLanguages[1].grabFocus();
                            return;
                        }
                    }
            } 

            if (isMenuNameModified(2)) {
            	String text = tfdLanguages[2].getText();
                if (text != null && !"".equals(text))//language3 is allowed to be empty.
                    for (int i = 0; i < barGeneralPanel.menuNameMetrix[2].length; i++) {
                        if (i != dspIndex && text.equalsIgnoreCase(barGeneralPanel.menuNameMetrix[2][i])) {
                            JOptionPane.showMessageDialog(this, BarDlgConst.DuplicatedInput);
                            tfdLanguages[2].grabFocus();
                            return;
                        }
                    }
            }

            // price check----------------------------------
            String priceText = tfdPrice.getText();
            float tPrice;
            try {
                tPrice = Float.parseFloat(priceText);
                int tPosOfPoint = priceText.indexOf("."); // 若格式也没有问题，精度是否符合要求。
                if (tPosOfPoint != -1 && priceText.substring(tPosOfPoint + 1).length() > 2) {
                    JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                    tfdPrice.selectAll();
                    tfdPrice.grabFocus();
                    return;
                }
            } catch (Exception exp) {
                JOptionPane.showMessageDialog(this, DlgConst.InvalidInput);
                tfdPrice.grabFocus();
                tfdPrice.selectAll();
                return;
            }
            
            try {
                Connection conn = PIMDBModel.getConection();
                Statement smt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

                //dspIndex check----------------------------
	            int newIndex = Integer.valueOf(tfdDspIndex.getText()); // display must be a integer
                // index modified, need to modify affected categories
            	if (newIndex > dspIndex) {
                    for (int i = dspIndex + 1; i <= newIndex; i++) { // make index smaller
                    	StringBuilder sql = new StringBuilder("UPDATE product SET INDEX = ").append(String.valueOf(i - 1))
                        	.append(" where INDEX = ").append(i)
                        	.append(" and category = '").append(newCategory).append("'");
                        smt.executeUpdate(sql.toString());
                    }
                } else if (newIndex < dspIndex){
                    for (int i = dspIndex - 1; i >= newIndex; i--) { // make index bigger @NOTE: have adjust from top to down.
                    	StringBuilder sql = new StringBuilder("UPDATE product SET INDEX = ").append(String.valueOf(i + 1))
                        	.append(" where INDEX = ").append(i)
                        	.append(" and category = '").append(newCategory).append("'");
                        smt.executeUpdate(sql.toString());
                    }
                }
                
                // insert the product record into db.==========================
                if (isCreatingNewDish()) {
                    StringBuilder sql = new StringBuilder(
                        "INSERT INTO Product(CODE, MNEMONIC, SUBJECT, PRICE, FOLDERID, store, Cost,  BRAND, CATEGORY, INDEX, CONTENT, Unit, PRODUCAREA) VALUES ('")
                        .append(tfdLanguages[0].getText()).append("', '")
                        .append(tfdLanguages[1].getText()).append("', '")
                        .append(tfdLanguages[2].getText()).append("', ")
                        .append(String.valueOf((int) tPrice * 100)).append(", ")
                        .append(cbxGST.isSelected() ? "1" : "0").append(", ")
                        .append(cbxQST.isSelected() ? "1" : "0").append(", ")
                        .append(getSelectedSize()).append(", '")
                        .append(getSeletedPrinterString()).append("', '")
                        .append(newCategory).append("', ")
                        .append(newIndex).append(", '")
                        .append(cbxPricePomp.isSelected() ? "true" : "false").append("', '")
                        .append(cbxMenuPomp.isSelected() ? "true" : "false").append("', '")
                        .append(cbxModifyPomp.isSelected() ? "true" : "false").append("')");
                    smt.executeUpdate(sql.toString());

                    sql = new StringBuilder("Select id from product where code = '")
                        .append(tfdLanguages[0].getText()).append("' and PRICE = ")
                        .append(String.valueOf((int) tPrice * 100)).append(" and MNEMONIC = '")
                        .append(tfdLanguages[1].getText()).append("' and SUBJECT = '")
                        .append(tfdLanguages[2].getText()).append("' and store = ")
                        .append(cbxGST.isSelected() ? "1" : "0").append(" and UNIT = '")
                        .append(cbxMenuPomp.isSelected() ? "true" : "false").append("' and CATEGORY = '")
                        .append(newCategory).append("' and Cost = ")
                        .append(getSelectedSize()).append(" and CONTENT = '")
                        .append(cbxPricePomp.isSelected() ? "true" : "false").append("'");
                    ResultSet rs = smt.executeQuery(sql.toString());
                    rs.beforeFirst();
                    rs.next();
                    prodID = rs.getInt("id");
                    rs.close();
                    ADDED = true;
                } else {
                    StringBuilder sql = new StringBuilder("update product set code = '").append(tfdLanguages[0].getText())
	                    .append("', MNEMONIC = '").append(tfdLanguages[1].getText())
	                    .append("', SUBJECT = '").append(tfdLanguages[2].getText())
	                    .append("', PRICE = ").append(String.valueOf((int) tPrice * 100))
	                    .append(", FOLDERID = ").append(cbxGST.isSelected() ? "1" : "0")
	                    .append(", store = ").append(cbxQST.isSelected() ? "1" : "0")
	                    .append(", Cost = ").append(getSelectedSize())
	                    .append(", BRAND = '").append(getSeletedPrinterString())
	                    .append("', CATEGORY = '").append(newCategory)
	                    .append("', INDEX = ").append(newIndex)
	                    .append(", CONTENT = '").append(cbxPricePomp.isSelected() ? "true" : "false")
	                    .append("', UNIT = '").append(cbxMenuPomp.isSelected() ? "true" : "false")
	                    .append("', PRODUCAREA = '").append(cbxModifyPomp.isSelected() ? "true" : "false")
	                    .append("' where ID = ").append(String.valueOf(prodID));

                    smt.executeUpdate(sql.toString());
                    ADDED = true;
                }
                smt.close();
                smt = null;
            }catch(Exception exp) {
            	JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
                exp.printStackTrace();
            }

            barGeneralPanel.initCategoryAndDishes();
            barGeneralPanel.reLayout();
            dispose();
        } else if (o == cancel) {
            dispose();
        }
    }

    private boolean isCreatingNewDish() {
    	//currently the onScrAry is as long ad whole ary. so this will not work.
    	//so, check only if the original language1 is empty or not.
        return dspIndex - 1 >= barGeneralPanel.onScrMenuNameMetrix[0].length 
        		|| barGeneralPanel.onScrMenuNameMetrix[0][dspIndex - 1] == null
        		|| barGeneralPanel.onScrMenuNameMetrix[0][dspIndex - 1].length() < 1;
    }

    private String getSeletedPrinterString() {
        StringBuilder selectedPrinters = new StringBuilder();
        for(int i = 0; i < cbxPrinters.length; i++) {
            if (cbxPrinters[i].isSelected())
                selectedPrinters.append(i).append(",");
        }
        return selectedPrinters.toString();
    }

    private int getSelectedSize() {
    	for(int i = 0; i < rdbSizes.length; i++) {
            if (rdbSizes[i].isSelected())
                return i;
    	}
    	return 0;
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
    	setModal(true);
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
        rdbSizes = new JRadioButton[6];
        rdbSizes[0] = new JRadioButton(BarDlgConst.Size1);
        rdbSizes[1] = new JRadioButton(BarDlgConst.Size2);
        rdbSizes[2] = new JRadioButton(BarDlgConst.Size3);
        rdbSizes[3] = new JRadioButton(BarDlgConst.Size4);
        rdbSizes[4] = new JRadioButton(BarDlgConst.Size5);
        rdbSizes[5] = new JRadioButton(BarDlgConst.Size6);
        ButtonGroup group = new ButtonGroup();
        group.add(rdbSizes[0]);
        group.add(rdbSizes[1]);
        group.add(rdbSizes[2]);
        group.add(rdbSizes[3]);
        group.add(rdbSizes[4]);
        group.add(rdbSizes[5]);

        sptPrinter = new PIMSeparator(BarDlgConst.PRINTER);
        cbxPrinters = new JCheckBox[6];
        cbxPrinters[0] = new JCheckBox(BarDlgConst.Printer1);
        cbxPrinters[1] = new JCheckBox(BarDlgConst.Printer2);
        cbxPrinters[2] = new JCheckBox(BarDlgConst.Printer3);
        cbxPrinters[3] = new JCheckBox(BarDlgConst.Printer4);
        cbxPrinters[4] = new JCheckBox(BarDlgConst.Printer5);
        cbxPrinters[5] = new JCheckBox(BarDlgConst.Printer6);

        sptOther = new PIMSeparator(OptionDlgConst.OPTION_OTHER);
        cbxPricePomp = new JCheckBox(BarDlgConst.PricePomp);
        cbxMenuPomp = new JCheckBox(BarDlgConst.MenuPomp);
        cbxModifyPomp = new JCheckBox(BarDlgConst.ModifyPomp);
        lblCategory = new JLabel(BarDlgConst.Categary);
        cmbCategory = new JComboBox<String>();
        lblDspIndex = new JLabel(BarDlgConst.DSPINDEX);
        tfdDspIndex = new JTextField();

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
        getContentPane().add(rdbSizes[0]);
        getContentPane().add(rdbSizes[1]);
        getContentPane().add(rdbSizes[2]);
        getContentPane().add(rdbSizes[3]);
        getContentPane().add(rdbSizes[4]);
        getContentPane().add(rdbSizes[5]);

        getContentPane().add(sptPrinter);
        getContentPane().add(cbxPrinters[0]);
        getContentPane().add(cbxPrinters[1]);
        getContentPane().add(cbxPrinters[2]);
        getContentPane().add(cbxPrinters[3]);
        getContentPane().add(cbxPrinters[4]);
        getContentPane().add(cbxPrinters[5]);

        getContentPane().add(sptOther);
        getContentPane().add(lblCategory);
        getContentPane().add(cmbCategory);
        getContentPane().add(lblDspIndex);
        getContentPane().add(tfdDspIndex);
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
        if (dish == null) {
            cbxQST.setSelected(true);
            cbxGST.setSelected(true);
            rdbSizes[0].setSelected(true);
            cbxPrinters[0].setSelected(true);
        } else {
            tfdLanguages[0].setText(dish.getLanguage1());
            tfdLanguages[1].setText(dish.getLanguage2());
            tfdLanguages[2].setText(dish.getLanguage3());
            tfdPrice.setText(String.valueOf(Float.valueOf(dish.getPrice()) / 100));
            cbxGST.setSelected(dish.getGst() == 1);
            cbxQST.setSelected(dish.getQst() == 1);
            int size = dish.getSize();
            rdbSizes[size].setSelected(true);

            String printer = dish.getPrinter();
            String[] printerStrs = StringUtil.split(printer, ",");
            for (String string : printerStrs) {
                try {
                    int i = Integer.valueOf(string);
                    cbxPrinters[i].setSelected(true);
                } catch (Exception e) {
                }
            }

            cbxPricePomp.setSelected("true".equals(dish.getPrompPrice()));
            cbxMenuPomp.setSelected("true".equals(dish.getPrompMenu()));
            cbxModifyPomp.setSelected("true".equals(dish.getPrompMofify()));
        }
        activeCategory = barGeneralPanel.activeCategoryButton.getText();
        tfdDspIndex.setText(String.valueOf(dspIndex));
        
        initCategory();
    }

    public void initCategory() {
        try {
            Connection connection = PIMDBModel.getConection();
            Statement statement =
                    connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            // load all the categorys---------------------------
            ResultSet categoryRS = statement.executeQuery("select ID, LANG1, LANG2, LANG3 from CATEGORY order by DSP_INDEX");
            categoryRS.afterLast();
            categoryRS.relative(-1);
            int tmpPos = categoryRS.getRow();
            categoryIdAry = new int[tmpPos];
            categorySubjectAry = new String[tmpPos];
            categoryRS.beforeFirst();

            tmpPos = 0;
            while (categoryRS.next()) {
                categoryIdAry[tmpPos] = categoryRS.getInt("ID");
                categorySubjectAry[tmpPos] = categoryRS.getString("LANG" + (CustOpts.custOps.getUserLang() + 1));
                tmpPos++;
            }
            categoryRS.close();// 关闭

        } catch (Exception e) {
            ErrorUtil.write(e);
        }

        if (categorySubjectAry.length > 0) {
            cmbCategory.setModel(new DefaultComboBoxModel(categorySubjectAry));
            if (activeCategory != null) {
                cmbCategory.setSelectedItem(activeCategory);
            }
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
    private JRadioButton[] rdbSizes;

    private PIMSeparator sptPrinter;
    private JCheckBox[] cbxPrinters;

    private PIMSeparator sptOther;
    private JLabel lblCategory;
    private JComboBox<String> cmbCategory;
    private JLabel lblDspIndex;
    private JTextField tfdDspIndex;

    private JCheckBox cbxPricePomp;
    private JCheckBox cbxMenuPomp;
    private JCheckBox cbxModifyPomp;

    private JButton ok;
    private JButton cancel;
}
