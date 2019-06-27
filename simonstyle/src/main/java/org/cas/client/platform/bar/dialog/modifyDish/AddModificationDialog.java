package org.cas.client.platform.bar.dialog.modifyDish;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.Scrollbar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.security.KeyStore.Entry;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.DlgConst;
import org.hsqldb.lib.StringUtil;

public class AddModificationDialog extends JDialog implements ActionListener, ListSelectionListener, KeyListener,
        MouseListener, Runnable, ComponentListener, ChangeListener {

    int oldIndex = 0;
    HashMap<Integer, String> selections = new HashMap<Integer, String>();
    
    /**
     * @param prmCategoryInfo  逗号分隔的字符串
     */
    public AddModificationDialog(Frame prmParent, String prmCategoryInfo) {
        super(prmParent, true);
        initComponent(); // 组件初始化并布局
        initContent(prmCategoryInfo, 0); // 初始化文本区和列表框数据
    }

    /** Invoked when the component's size changes. */
    @Override
	public void componentResized(ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    @Override
	public void componentMoved(ComponentEvent e) {}

    /** Invoked when the component has been made visible. */
    @Override
	public void componentShown(ComponentEvent e) {}

    /** Invoked when the component has been made invisible. */
    @Override
	public void componentHidden(ComponentEvent e) {}

    /** 初始化并布局; */
    private void initComponent() {
        setTitle(BarFrame.consts.MODIFY()); // 设置标题
        getContentPane().setLayout(null);
        setBounds((CustOpts.SCRWIDTH - 600) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 600, 320); // 对话框的默认尺寸。
        setResizable(true);

        // init--------------------------
        midLabel = new JLabel(BarFrame.consts.AvailableModification()); // "可用类别"标签
        txaCurContent = new JTextArea(); // 加入会滚动的文本区
        topLabel = new JLabel(BarFrame.consts.AddNewModificationItem()); // "项目属于这些类别"标签
        modificationList = new JList();
        scrPane = new PIMScrollPane(modificationList);
        tabbedPane = new JTabbedPane();
        btnApplyToList = new JButton(BarFrame.consts.ApplyToList()); // 加至列表按钮
        btnApplyToCategory = new JButton(BarFrame.consts.ApplyToCategory());
        lblDspIdx = new JLabel(BarFrame.consts.DSPINDEX());
        lblPrice = new JLabel(BarFrame.consts.Price());
        valDspIdx = new JTextField();
        valPrice = new JTextField();
        btnApply =  new JButton(BarFrame.consts.Apply());
        btnDelete = new JButton(BarFrame.consts.Delete());
        resetBTN = new JButton(BarFrame.consts.RESET());
        btnOK = new JButton(BarFrame.consts.OK());

        // properties-------------------------
        midLabel.setLabelFor(modificationList);
        topLabel.setLabelFor(txaCurContent);
        modificationList.setCellRenderer(new ModificationListRenderer());
        modificationList.setBackground(null);
        modificationList.setBorder(new LineBorder(Color.GRAY));
        txaCurContent.setBorder(new LineBorder(Color.GRAY));
        midLabel.setDisplayedMnemonic('V');
        topLabel.setDisplayedMnemonic('I');
        btnApplyToList.setMnemonic('A');
        btnApplyToCategory.setMnemonic('p');
        btnApply.setMnemonic('D');
        btnDelete.setMnemonic('D');
        resetBTN.setMnemonic('R');
        btnApplyToList.setMargin(new Insets(0, 0, 0, 0));
        btnApplyToCategory.setMargin(new Insets(0, 0, 0, 0));
        btnDelete.setMargin(new Insets(0, 0, 0, 0));
        btnApply.setMargin(new Insets(0, 0, 0, 0));
        resetBTN.setMargin(new Insets(0, 0, 0, 0));
        resetBTN.setVisible(false);
        btnApplyToList.setEnabled(false); // 一开始这项为禁止
        btnApplyToCategory.setEnabled(false); // 一开始这项为禁止
        valDspIdx.setEditable(BillListPanel.curDish == null );
        valPrice.setEditable(BillListPanel.curDish == null );
        
        // layout---------------------------
        reLayout();

        // build----------------------------
        getContentPane().add(midLabel);
        getContentPane().add(btnApplyToList);
        getContentPane().add(btnApplyToCategory);
        tabbedPane.add("    ", scrPane);
        getContentPane().add(tabbedPane);
        getContentPane().add(lblDspIdx);
        getContentPane().add(valDspIdx);
        getContentPane().add(lblPrice);
        getContentPane().add(valPrice);
        getContentPane().add(btnApply);
        getContentPane().add(btnDelete);
        getContentPane().add(resetBTN);
        getContentPane().add(txaCurContent);
        getContentPane().add(topLabel);
        getContentPane().add(btnOK);

        // listeners------------------------
        btnApplyToList.addActionListener(this);
        btnApplyToCategory.addActionListener(this);
        btnApply.addActionListener(this);
        btnDelete.addActionListener(this);
        resetBTN.addActionListener(this);
        btnOK.addActionListener(this);
        
        tabbedPane.addChangeListener(this);
        modificationList.addListSelectionListener(this);
        
        modificationList.addMouseListener(this);
        
        txaCurContent.addKeyListener(this);
        modificationList.addKeyListener(this);
        
        getContentPane().addComponentListener(this);
    }

    private void reLayout() {
        btnOK.setBounds(getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.BTN_WIDTH - 2 * CustOpts.HOR_GAP, 
        		getHeight() - CustOpts.BTN_HEIGHT - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - 2 * CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

        if(BillListPanel.curDish == null) {
            topLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
            		btnOK.getX() + btnOK.getWidth(), CustOpts.LBL_HEIGHT);
            txaCurContent.setBounds(topLabel.getX(), topLabel.getY() + CustOpts.LBL_HEIGHT, 
            		getWidth() - 2 * CustOpts.SIZE_EDGE - 3 * CustOpts.HOR_GAP,
                    CustOpts.LBL_HEIGHT * 3);
            int width = 120;
            btnApplyToList.setBounds(btnOK.getX() + btnOK.getWidth() - width, txaCurContent.getY() + txaCurContent.getHeight() + CustOpts.VER_GAP,
            		width, CustOpts.BTN_HEIGHT);
            btnApplyToCategory.setBounds(btnApplyToList.getX() - CustOpts.HOR_GAP - width, btnApplyToList.getY(),
            		width, CustOpts.BTN_HEIGHT);
            midLabel.setBounds( topLabel.getX(),
            		txaCurContent.getY() + txaCurContent.getHeight() + CustOpts.VER_GAP,
            		getWidth() - 2 * CustOpts.SIZE_EDGE - 3 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        }else {
        	topLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 0, 0);
        	txaCurContent.setBounds(0, 0, 0, 0);
            btnApplyToList.setBounds(0,0,0,0);
            btnApplyToCategory.setBounds(0,0,0,0);
            midLabel.setBounds(topLabel.getX(),0,0,0);
        }
        
        tabbedPane.setBounds(midLabel.getX(), midLabel.getY() + midLabel.getHeight() + CustOpts.VER_GAP, // "可用类别"列表框
        		getWidth() - 2 * CustOpts.SIZE_EDGE - 3 * CustOpts.HOR_GAP, 
                btnOK.getY() - 2 * CustOpts.VER_GAP - midLabel.getY() - midLabel.getHeight());
        
        lblDspIdx.setBounds(tabbedPane.getX(), btnOK.getY(),
        		lblDspIdx.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        valDspIdx.setBounds(lblDspIdx.getX() + lblDspIdx.getWidth() + CustOpts.HOR_GAP, lblDspIdx.getY(),
        		30, CustOpts.BTN_HEIGHT);
        
        lblPrice.setBounds(valDspIdx.getX() + valDspIdx.getWidth() + CustOpts.HOR_GAP, valDspIdx.getY(),
        		lblPrice.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        valPrice.setBounds(lblPrice.getX() + lblPrice.getWidth() + CustOpts.HOR_GAP, lblPrice.getY(),
        		60, CustOpts.BTN_HEIGHT);
       
        
        if(BillListPanel.curDish == null) {
            btnApply.setBounds(valPrice.getX() + valPrice.getWidth() + CustOpts.HOR_GAP, valPrice.getY(),
            		CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        	btnDelete.setBounds(btnApply.getX() + btnApply.getWidth() + CustOpts.HOR_GAP, btnOK.getY(),
        			CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        }else {
            btnApply.setBounds(valPrice.getX() + valPrice.getWidth() + CustOpts.HOR_GAP, valPrice.getY(), 0, 0);
        	btnDelete.setBounds(btnApply.getX() + btnApply.getWidth() + CustOpts.HOR_GAP, btnOK.getY(), 0, 0);
        }
//        resetBTN.setBounds(btnDelete.getX(), btnDelete.getY() + btnDelete.getHeight() + CustOpts.VER_GAP,
//                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
    }

    /** 初始化时使用 */
    private void initContent( String prmCategoryInfo, int idx) {
    	//must set content first, so the selections can be initialized base on it. 
    	//and set the content again in the last line, when got idea which belongs to this page.
        txaCurContent.setText("null".equalsIgnoreCase(prmCategoryInfo) ? "" : prmCategoryInfo);	
        listModel = new DefaultListModel<CheckItem>();

        initTabbedPaneContent();
        initSelectionMap();
        txaCurContent.setText(selections.get(idx));
    }

	private void initSelectionMap() {
        ArrayList<String> inputList = getInputModification();
        int tabsize = tabbedPane.getTabCount();
        for(int i = 0; i < tabsize; i++) {
        	ArrayList<String> allLabelOfCurTab = getAllModification(i);
        	StringBuilder stringForCurTab = new StringBuilder();
        	for (int j = inputList.size() - 1; j >= 0; j--) {
        		String label = inputList.get(j);
				if(allLabelOfCurTab.contains(label)) {
					stringForCurTab.append(label);
					stringForCurTab.append(BarDlgConst.delimiter);
					inputList.remove(j);
				}
			}
        	selections.put(i, stringForCurTab.toString());
        }
        
        
	}

	private void initTabbedPaneContent() {
		tabbedPane.removeAll();
        tabbedPane.add("    ", scrPane);
        StringBuilder sql = new StringBuilder("select * from modification where type < 0 order by type desc");
        try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.beforeFirst();
            while (rs.next()) {
            	switch (LoginDlg.USERLANG) {
				case 0:
					tabbedPane.add(rs.getString("lang1"), new PIMScrollPane());
					break;
				case 1:
					tabbedPane.add(rs.getString("lang2"), new PIMScrollPane());
					break;
				case 2:
					tabbedPane.add(rs.getString("lang3"), new PIMScrollPane());
					break;
				default:
					break;
				}
            }
		}catch(Exception exp) {
			L.e("AddModificationDlg", "exception when change output back to original bill" + sql, exp);
		}
	}
    
    private ArrayList<String> getAllModification(int idx) {
        StringBuilder sql = new StringBuilder("SELECT * FROM modification where status = ").append(DBConsts.original);
        if(idx == 0 ) {
        	sql.append(" and type is null or type = 0");
        }else if(idx > 0){
        	sql.append(" and type = ").append(idx);
        }else {
        	//append no type condition if the idx is -1;
        }
        sql.append(" order by lang5");
        
        ArrayList<String> nameVec = new ArrayList<String>();
        try {

            Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery(sql.toString());
            while (rs.next()) {
            	StringBuilder sb = new StringBuilder(rs.getString("lang1")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang2")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang3")).append(BarDlgConst.semicolon);
                nameVec.add(sb.toString());
            }

            // 关闭
            smt.close();
            smt = null;
            rs.close();
            rs = null;

            return nameVec;
            
        } catch (SQLException e) {
            ErrorUtil.write(e);
            return null;
        }
    }

    /**
     * 解析一个逗号分隔符处理的字符串 getTextAreaData
     */
    private String[] stringToArray( String string) {
        if (string != null) {
            // 构建字符串分隔器
            StringTokenizer token = new StringTokenizer(string, BarDlgConst.delimiter);
            int size = token.countTokens();
            // 构建相应容量的字符串数组
            String[] indexes = new String[size];
            size = 0;
            // 循环加入,去掉空格的
            while (token.hasMoreTokens()) {
                indexes[size] = token.nextToken().trim();
                size++;
            }
            return indexes;
        } else {
            return null;
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    @Override
	public void actionPerformed(ActionEvent e) {
    	Object o = e.getSource();
        if (o == btnApplyToList)
            applyToListClicked();
        else if (o == btnApplyToCategory)
        	addToCategoryClicked();
        else if (o == btnDelete)
            deleteClicked();
        else if (o == btnApply)
        	applyProperties();
        else if (o == resetBTN)
            resetClicked();
        else if (o == btnOK) {
            if(BillListPanel.curDish != null) {
            	StringBuilder modifications = new StringBuilder(txaCurContent.getText());
            	for (java.util.Map.Entry<Integer, String> entry : selections.entrySet()) {
            		if(entry.getKey() != tabbedPane.getSelectedIndex()) {
            			if(modifications.length() > 0 && !modifications.toString().endsWith(BarDlgConst.delimiter)) {
            				modifications.append(BarDlgConst.delimiter);
            			}
            			modifications.append(entry.getValue());
            		}
				}
            	
            	String[] notes = modifications.toString().split(BarDlgConst.delimiter);
            	ArrayList<String> allModification = getAllModification(-1);
            	StringBuilder fullModifyString = new StringBuilder();
            	StringBuilder onSrcString = new StringBuilder();
            	for(int i = 0; i < notes.length; i++) {
//            		//now this for lood should be not necessary anymore, because the content in txaCurContent is always full string.
//					for (String fullStringOfLabel : allModification) {
//						if(notes[i].trim().length() > 0 && fullStringOfLabel.indexOf(notes[i].trim()) > -1) {
//							notes[i] = fullStringOfLabel;
//							break;
//						}
//					}
            		fullModifyString.append(notes[i].trim()).append(BarDlgConst.delimiter);
            		
            		String[] langs = notes[i].split(BarDlgConst.semicolon);
        			String lang_OnSrc = langs.length > LoginDlg.USERLANG ? langs[LoginDlg.USERLANG].trim() : langs[0].trim();
                	if(lang_OnSrc.length() == 0)
                		lang_OnSrc = langs[0];
            		onSrcString.append(lang_OnSrc).append(BarDlgConst.delimiter);
				}
            	
            	//update the display on table
            	PIMTable table = ((SalesPanel)BarFrame.instance.panels[2]).billPanel.table;
            	int row = table.getSelectedRow();
            	String oldContent = (String)table.getValueAt(row, 2);
            	
            	int idx = oldContent.indexOf("-" + BarOption.getMoneySign());
            	if(idx > -1) {
            		String oldDiscount = oldContent.substring(idx); //no matter discount in the end or begging.
            		idx = oldDiscount.indexOf(BarDlgConst.delimiter);
            		if(idx > -1) {
            			oldContent = oldContent.substring(0, idx);
            			oldDiscount = oldDiscount.substring(0, idx);
            		}
            		onSrcString.append(oldDiscount);
            	}
            	table.setValueAt(onSrcString.toString(), row, 2);
            	
            	//money part calculating
            	float oldPriceInLabel = 0.0f;
            	float newPriceInLabel = 0.0f;	//new labels added money
            	if(BillListPanel.curDish.getModification() != null) {
            		oldPriceInLabel = BarUtil.calculateLabelsPrices(BillListPanel.curDish.getModification().split(BarDlgConst.delimiter));
            	}
            	newPriceInLabel = BarUtil.calculateLabelsPrices(notes);	//new labels added money
            	int newtotal = (int)(BillListPanel.curDish.getTotalPrice() - oldPriceInLabel * 100 + newPriceInLabel * 100);
            	
            	//update the orderAry and database.
            	BillListPanel.curDish.setModification(fullModifyString.toString());
            	
            	BillListPanel.curDish.setTotalPrice(newtotal);
             	
             	int outputID = BillListPanel.curDish.getOutputID();
             	if(outputID >= 0) {
             		String sql = "update output set toltalprice = " + BillListPanel.curDish.getTotalPrice() + " where id = " + outputID;
             		try {
             			PIMDBModel.getStatement().executeUpdate(sql);
             		}catch(Exception exp) {
    					L.e("AddModificationDlg", " exception when update total price for output!" + sql, exp);
             		}
             	}
             	
             	//updte table display
             	table.setValueAt(BarOption.getMoneySign() + BarUtil.formatMoney(newtotal/100.0), row, 3);
             	((SalesPanel)BarFrame.instance.panels[2]).billPanel.updateTotalArea();
            }
            dispose();
        }
    }

	private void applyProperties() {
    	//validate values;
    	CheckItem seleItem = modificationList.getSelectedValue();
    	if(seleItem == null) {
    		JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.AtLeastOneShouldBeSelected());
    		return;
    	}
    		
    	try {
    		Float.parseFloat(valPrice.getText().length() > 0 ? valPrice.getText() : "0.00");
    		Float.parseFloat(valDspIdx.getText().length() > 0 ? valDspIdx.getText() : "0");
    	}catch(Exception e) {
    		JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidInput());
    		return;
    	}
    	
    	//update
		StringBuilder sql = new StringBuilder("UPDATE modification set lang5 = '").append(valDspIdx.getText()).append("', ")
				.append(" lang6 = '").append(valPrice.getText());
		if(tabbedPane.getSelectedIndex() == 0) {
			sql.append("' where type is null or type = 0");
		}else {
			sql.append("' where type = ").append((tabbedPane.getSelectedIndex()));
		}
		sql.append(" and lang1 = '");
		
		String lang1 = seleItem.getName();
		int i = lang1.indexOf(BarDlgConst.semicolon);
		lang1 = lang1.substring(0, i);
		sql.append(lang1).append("'");
		
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}catch(Exception e) {
			L.e("AddModification ", "Exception when updating a category into modification.", e);
		}
	}

	private boolean insertModification( String modification) {
        StringBuilder sql = new StringBuilder("INSERT INTO modification (lang1, lang2, lang3, type, status) VALUES( '");
        String[] langs = modification.split(BarDlgConst.semicolon);
        sql.append(langs.length > 0 ? langs[0] : "");
        sql.append("', '");
        sql.append(langs.length > 1 ? langs[1] : "");
        sql.append("', '");
        sql.append(langs.length > 2 ? langs[2] : "");
        sql.append("', '");
        sql.append(tabbedPane.getSelectedIndex());
        sql.append("', 0);");

        try {
            Statement smt = PIMDBModel.getStatement();
            int rows = smt.executeUpdate(sql.toString());

            // 关闭
            smt.close();
            smt = null;

            return rows != 0;
        } catch (SQLException e) {
        	ErrorUtil.write(e);
            return false;
        }
    }
    
    private boolean updateToModification(String modification){
        String[] langs = modification.split(BarDlgConst.semicolon);
    	StringBuilder sql = new StringBuilder("update modification set lang2 = '");
        sql.append(langs.length > 1 ? langs[1] : "");
        sql.append("', lang3 = '");
        sql.append(langs.length > 2 ? langs[2] : "");
        sql.append("', lang4 = '");
        sql.append(langs.length > 3 ? langs[3] : "");
        sql.append("' where lang1 = '").append(langs[0]).append("';");

        try {
            Statement smt = PIMDBModel.getStatement();
            int rows = smt.executeUpdate(sql.toString());
            // 关闭
            smt.close();
            smt = null;

            return rows != 0;
        } catch (SQLException e) {
        	ErrorUtil.write(e);
            return false;
        }
    }
    
    private boolean deleteModification( String modification, int idx) {
    	StringBuilder sql = new StringBuilder("DELETE FROM modification WHERE lang1 = '");
        String[] langs = modification.split(BarDlgConst.semicolon);
        sql.append(langs[0]);
        sql.append("' and type = ").append(idx);

        try {
            Statement smt = PIMDBModel.getStatement();
            int rows = smt.executeUpdate(sql.toString());
            // 关闭
            smt.close();
            smt = null;

            return rows != 0;
        } catch (SQLException e) {
        	ErrorUtil.write(e);
            return false;
        }
    }
    
    private boolean deleteCategory(int idx) {
    	if(idx > 0) {
    		idx = idx * -1;
    	}
    	StringBuilder sql = new StringBuilder("DELETE FROM modification where type = ").append(idx);
        try {
            PIMDBModel.getStatement().executeUpdate(sql.toString());
            
            sql = new StringBuilder("update modification set type = type + 1 where type < " + idx);	//update the other category
            PIMDBModel.getStatement().executeUpdate(sql.toString());

            sql = new StringBuilder("update modification set type = type - 1 where type > " + (-1 * idx));	//update modification of other category.
            PIMDBModel.getStatement().executeUpdate(sql.toString());
            
            return true;
        } catch (SQLException e) {
        	ErrorUtil.write(e);
            return false;
        }
    }
    
    private ArrayList<String> getInputModification() {
        // 把文本框中字段还原为字符串数组
        String[] usedFields = stringToArray(txaCurContent.getText());
        ArrayList<String> lstModificationSelected = new ArrayList<String> ();
        // 文本框中字段放入一个 ArrayList 中以备用
        // 去掉过滤信息
        if (usedFields != null && usedFields.length > 0) {
            for (int i = 0; i < usedFields.length; i++) {
                if (usedFields[i].length() == 0) // 是空串不用加
                    continue;
                lstModificationSelected.add(usedFields[i]);
            }
        }
        lstModificationSelected.trimToSize();
        return lstModificationSelected;
    }
    
    private ArrayList<String> getModificationsInListComponent(){
    	// 现在列表框中的字段
        Object[] modificationInListComponent = listModel.toArray();

    	ArrayList<String> modelArr = new ArrayList<String>(modificationInListComponent.length);
        // 用列表框模型中的数据的名字加入 list
        for (int i = 0; i < modificationInListComponent.length; i++) {
            modelArr.add(((CheckItem) modificationInListComponent[i]).getName());
        }
        return modelArr;
    }
    
    private void deleteClicked() {
    	if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.AllSelectedItemWillBeDeleted(), DlgConst.DlgTitle,
                JOptionPane.YES_NO_OPTION) != 0) {
			return;
		}
    	
    	ListModel<CheckItem> model = modificationList.getModel();
    	if(model.getSize() > 0) {
	    	for(int i = model.getSize() - 1; i >= 0; i--) {
	    		CheckItem checkItem = model.getElementAt(i);
	    		if(checkItem.isSelected()) {
	    			int tmpSelectionIndex = i;
	    	        
	    	        // 看来字段删除必须在这时处理
	    	        deleteModification(checkItem.getName(), tabbedPane.getSelectedIndex());
	    	        listModel.remove(tmpSelectionIndex);
	    	        itemChanged = true;
	
	    	        // 下面要处理文本区的显示
	    	        setTextOfTextArea();
	
	    	        // 确定按钮置有效
	    	        if (!btnOK.isEnabled()) {
	    	            btnOK.setEnabled(true);
	    	        }
	    		}
	    	}
	    	if(!itemChanged) {
		    	JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.AtLeastOneShouldBeSelected());
		    	return;
	    	}
    	}else {//delete the group.
    		int selectedTabIdx = tabbedPane.getSelectedIndex();
    		deleteCategory(selectedTabIdx);
    		tabbedPane.remove(selectedTabIdx);
    	}
    }

    private void resetClicked() {
        // 先全清空
        listModel.removeAllElements();
        // 表示不要再写入磁盘了
        itemChanged = false;

        CASControl.ctrl.getModel().resetCategroyName();
        ArrayList<String> defaultCate = getAllModification(tabbedPane.getSelectedIndex());
        // 再把系统默认的数据加入
        for (int i = 0, count = defaultCate.size(); i < count; i++) {
            listModel.addElement(new CheckItem(defaultCate.get(i), false));
        }
        // 设置默认选项
        modificationList.setSelectedIndex(0);
        // TODO: 从数据库中读取信息并装配文本区和列表框
        txaCurContent.setText(CASUtility.EMPTYSTR);

        // 确定按钮置有效
        if (!btnOK.isEnabled()) {
            btnOK.setEnabled(true);
        }
        // 删除按钮置有效
        if (!btnDelete.isEnabled()) {
            btnDelete.setEnabled(true);
        }
    }

    private ArrayList<String> getListInCurrentListComponent() {
        Object[] aryListModelData = listModel.toArray();

        // 构造一个 ArrayList 存放列表框中的数据中的字符串以便于判断包含关系
        ArrayList<String> lstListModelArr = new ArrayList<String>(aryListModelData.length);

        CheckItem tmpCheckItem;

        // 用列表框模型中的数据的名字加入 list
        for (int i = 0; i < aryListModelData.length; i++) {
            tmpCheckItem = (CheckItem) aryListModelData[i];
            lstListModelArr.add(tmpCheckItem.getName());
        }
        return lstListModelArr;
    }

    private int findIndexByLang1(ArrayList<String> existingList, String content) {
    	String strToCompare = content;
    	int p = strToCompare.indexOf(BarDlgConst.semicolon);
    	if(p > 0)
    		strToCompare = strToCompare.substring(0, p);
    	
    	for(int i = 0; i < existingList.size(); i++) {
			if(existingList.get(i).startsWith(strToCompare))
				return i;
		}
    	return -1;	
    }
    
    private void applyToListClicked() {
        // 文本框和列表框各取得其中的数组
        ArrayList<String> inputList = getInputModification();
        ArrayList<String> existingList = getListInCurrentListComponent();

        // 表示列表框中字段有更改,但不保存 ? 
        itemChanged = true;

        // 循环判断模型中是否包含文本区中的字符串们
        for (int i = 0; i < inputList.size(); i++) {
            // 不包含才加入列表框模型
        	String strInput = inputList.get(i).toString();
        	int index = findIndexByLang1(existingList, strInput);
            if (index == -1) {
            	insertModification(strInput);
                listModel.addElement(new CheckItem(strInput, true));
            }else {
            	if(!existingList.get(index).equals(strInput)) {
            		listModel.setElementAt(new CheckItem(inputList.get(i), true), index);
            		updateToModification(strInput);
            	}
            }
        }
        // 下面要判断原有模型中的字符串在文本区中有没有,If no, set unselected， If yes, set slected
        existingList = getListInCurrentListComponent();
        for (int i = 0; i < existingList.size(); i++) {
            // 文本区中不包含便将它设置标志为假
            if (!inputList.contains(existingList.get(i).toString())) {
                listModel.setElementAt(new CheckItem(existingList.get(i).toString(), false), i);
            } else {
                listModel.setElementAt(new CheckItem(existingList.get(i).toString(), true), i);
            }
        }
        setTextOfTextArea();

        // 确定按钮置有效
        if (!btnOK.isEnabled()) {
            btnOK.setEnabled(true);
        }

        // 本按钮置无效
        btnApplyToList.setEnabled(false);
        btnApplyToCategory.setEnabled(false);
    }
    
    private void addToCategoryClicked(){
		String[] langs = StringUtil.split(txaCurContent.getText(), BarDlgConst.semicolon);
    	String title = tabbedPane.getTitleAt(tabbedPane.getSelectedIndex());
    	if(title.equals("    ")) {
    		//insert
    		StringBuilder sql = new StringBuilder("INSERT INTO modification (lang1, lang2, lang3, type, status) VALUES ('")
    				.append(langs[0]).append("', '");
    		if(langs.length > 1) {
    			sql.append(langs[1]);
    		}
    		sql.append("', '");
    		if(langs.length > 2) {
    			sql.append(langs[2]);
    		}
    		sql.append("', ");
    		sql.append((tabbedPane.getTabCount()) * -1).append(", 0)");
    		try {
    			PIMDBModel.getStatement().executeUpdate(sql.toString());
    		}catch(Exception e) {
    			L.e("AddModification ", "Exception when insert a category into modification.", e);
    		}
    	}else {
    		//update
    		StringBuilder sql = new StringBuilder("UPDATE modification set lang1 = '").append(langs[0]).append("', ").append(" lang2 = '");
    		if(langs.length > 1) {
    			sql.append(langs[1]);
    		}
    		sql.append("', lang3 = '");
    		if(langs.length > 2) {
    			sql.append(langs[2]);
    		}
    		sql.append("' where type = ").append((tabbedPane.getSelectedIndex()) * -1);
    		try {
    			PIMDBModel.getStatement().executeUpdate(sql.toString());
    		}catch(Exception e) {
    			L.e("AddModification ", "Exception when updating a category into modification.", e);
    		}
    	}
    	initContent("null", tabbedPane.getSelectedIndex());
    }
    /**
     * 设置文本的显示,本方法由列模型自动检测出选中项,组成以逗号为分隔的字符串
     */
    private void setTextOfTextArea() {
        // 用来组装处理文本区中的显示
        StringBuilder sb = new StringBuilder();
        int tmpSize = listModel.size();

        // 用于取值的一个变量
        CheckItem tmpItem;

        // 循环判断列表框中是否有打勾的,有就加入字符串
        for (int i = 0; i < tmpSize; i++) {
            // 要造型的
            tmpItem = listModel.get(i);
            if (tmpItem.isSelected()) {
            	String langs = tmpItem.getName();
            	if(langs.indexOf(BarDlgConst.semicolon) < 0) {
            		langs = getAllLanguage(langs);
            	}
                sb = sb.append(langs).append(BarDlgConst.delimiter).append(" ");
            }
        }
        // 要不为空,就要去掉逗号尾巴
        txaCurContent.setText(sb.length() == 0 ? sb.toString() : sb.substring(0, sb.length() - 2));
    }

    private String getAllLanguage(String lang) {
    	StringBuilder sql = new StringBuilder("select * from modification where (type ");
    	if(tabbedPane.getSelectedIndex() == 0) {
    		sql.append("is null or type = 0)");
    	}else {
    		sql.append("= ").append(tabbedPane.getSelectedIndex()).append(")");
    	}
    	sql.append(" and status = ").append(DBConsts.original)
    		.append(" and lang").append(LoginDlg.USERLANG + 1).append(" = '").append(lang).append("'");
        try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.beforeFirst();
            while (rs.next()) {
            	return new StringBuilder(rs.getString("lang1")).append(BarDlgConst.semicolon)
            		.append(rs.getString("lang2")).append(BarDlgConst.semicolon)
					.append(rs.getString("lang3")).append(BarDlgConst.semicolon).toString();
            }
		}catch(Exception exp) {
			L.e("AddModificationDlg", "exception when querying full langs with sql " + sql, exp);
		}
        L.e("AddModificationDlg", "find no modification record!" + sql, null);
        return null;
	}

	/**
     * 重载,以后要去掉
     * 
     * @param modification
     *            逗号分隔的字符串
     */
    public void show(String modification) {
        txaCurContent.setText(modification);
        // 一开始这项为禁止
        btnOK.setEnabled(false);

        // 初始化列表框数据
        // 把文本框中字段还原为字符串数组
        String[] usedFields = stringToArray(modification);
        ArrayList<String> inputModification = getInputModification();
        ArrayList<String> allModification = getAllModification(tabbedPane.getSelectedIndex());
        
        // 反向操作,如果用户的输入导致新的字段的产生,在列表框模型中要加入,并存盘

        if (usedFields != null && usedFields.length > 0) {
            // 这个循环是加字段
            for (int i = 0; i < usedFields.length; i++) {
                if (!allModification.contains(usedFields[i])) {
                    // 加一个标志为真的
                    listModel.addElement(new CheckItem(usedFields[i], true));
                    // 保存到数据库中
                    CASControl.ctrl.getModel().addCategroyName(usedFields[i], i);
                }
            }

            // 这个循环是处理以下这个情况:如果用户在类别按钮旁的文本区中自己减了一个
            for (int i = 0; i < allModification.size(); i++) {
                if (!inputModification.contains(allModification.get(i).toString())) {
                    // 去掉一个标志为真的
                    listModel.setElementAt(new CheckItem(allModification.get(i).toString(), false), i);
                } else {
                    listModel.setElementAt(new CheckItem(allModification.get(i).toString(), true), i);
                }
            }
        }
        // 一开始这项为禁止
        btnApplyToList.setEnabled(false);
        btnApplyToCategory.setEnabled(false);
        setVisible(true);
    }

    /**
     * 得到用于表示类别的字符串
     * 
     * @called by: getCategories; TaskDialog; DetailPane; MemberPanel; ContactGeneralPanel
     * @return 逗号分隔的字符串
     */
    public String getCategories() {
        return txaCurContent.getText();
    }

	@Override
	public void stateChanged(ChangeEvent e) {
		JTabbedPane tabbedPane = (JTabbedPane)e.getSource();
		//reinit the content.
		PIMScrollPane scrPane = (PIMScrollPane)tabbedPane.getSelectedComponent();
		if(scrPane == null) {
			return;
		}
		
		int idx = tabbedPane.getSelectedIndex();

		selections.put(oldIndex, txaCurContent.getText());
		oldIndex = idx;
		txaCurContent.setText(selections.get(idx));
		
		// 把文本框中字段还原为字符串数组
        ArrayList<String> inputModification = getInputModification();
        ArrayList<String> allModification = getAllModification(idx);
        listModel.clear();
        // 加入列表框模型
        for (int i = 0, count = allModification.size(); i < count; i++) {
        	String[] langs = allModification.get(i).split(BarDlgConst.semicolon);
        	int langIdx = LoginDlg.USERLANG;
        	String lang_Modify = langs.length > langIdx ? langs[langIdx] : langs[0];
        	if(lang_Modify.length() == 0)
        		lang_Modify = langs[0];
        	
            // 置检查标志
            listModel.addElement(new CheckItem(BillListPanel.curDish == null ? allModification.get(i) : lang_Modify, inputModification.contains(allModification.get(i))));
        }
        
        // 反向操作,如果用户的输入导致新的字段的产生,在列表框模型中要加入,并存盘
        if (inputModification != null && inputModification.size() > 0) {
            for (int i = 0; i < inputModification.size(); i++) {
            	boolean notContained = true;
            	for (String string : allModification) {
					if(string.indexOf(inputModification.get(i)) > -1) {
						notContained = false;
						break;
					}
				}
//                if (notContained) {
//                    // 加一个标志为真的
//                    listModel.addElement(new CheckItem(inputModification.get(i), true));
//                    // 保存到数据库中
//                    insertModification(inputModification.get(i));
//                }
            }
        }
        
        modificationList.setModel(listModel);
        scrPane.getViewport().add(modificationList);
        SwingUtilities.invokeLater(this);
	}
	
    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    @Override
	public void valueChanged(ListSelectionEvent e) {
        /*
         * 主要来处理文本区中的显示 if(e.getSource() == categoryList) { StringBuffer sb = new StringBuffer(); int tmpSize =
         * categoryListModel.size(); CheckItem tmpItem; for(int i = 0; i < tmpSize ; i++) { tmpItem =
         * (CheckItem)categoryList.getSelectedValue(); if(tmpItem.isSelected()) { sb =
         * sb.append(tmpItem.getName()).append(this.delimiter); } } textAreaData = sb.toString();
         * if(textAreaData.length() >= 0) { //textAreaData = textAreaData.substring(0,textAreaData.length()-
         * delimiter.length()); } textArea.setText(textAreaData); } //
         */
    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
     * pressed event.
     * 
     * @param e
     *            键盘事件
     */
    @Override
	public void keyPressed(KeyEvent e) {
        if (e.getSource() == modificationList && e.getKeyCode() == KeyEvent.VK_SPACE) {        // 处理列表框空格键选中
            updateTextArea();
            updateProperties();
        }
    }

    private void updateTextArea() {
        // 处理列表本身的选中状态
        int index = modificationList.getSelectedIndex();
        if (index != -1) {
            CheckItem seleItem = modificationList.getSelectedValue();
            listModel.set(index, new CheckItem(seleItem.getName(), !seleItem.isSelected()));

            // 以列表框的模型来设置文本区中的显示
            setTextOfTextArea();
            // 确定按钮置有效
            if (!btnOK.isEnabled()) {
                btnOK.setEnabled(true);
            }
        }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
     * released event.
     * 
     * @param e
     *            键盘事件
     */
    @Override
	public void keyReleased(KeyEvent e) {}

    @Override
	public void keyTyped(KeyEvent e) {
        // 处理文本区的击键
        if (e.getSource() == txaCurContent) { 	// && (e.getKeyChar() != ' ' && e.getKeyChar() != ',' && e.getKeyCode() !=
        										// e.VK_TAB))//(e.getKeyCode() != e.VK_SPACE && e.getKeyChar() != e.VK_COMMA))
            SwingUtilities.invokeLater(this);
        }
    }

    @Override
	public void mouseClicked(MouseEvent e) {}

    @Override
	public void mouseEntered(MouseEvent e) {}

    @Override
	public void mouseExited(MouseEvent e) {}

    @Override
	public void mousePressed(MouseEvent e) {
        if (e.getSource() == modificationList) {
            updateTextArea();
            updateProperties();
        }
    }

    private void updateProperties() {
    	String lang1 = modificationList.getSelectedValue().getName();
    	int idx = lang1.indexOf(BarDlgConst.semicolon);
    	if(idx > 0) {
        	lang1 = lang1.substring(0, idx);
    	}
    	idx = tabbedPane.getSelectedIndex();
    	StringBuilder sql = new StringBuilder("SELECT * FROM modification where status = ").append(DBConsts.original);
        if(idx == 0 ) {
        	sql.append(" and type is null or type = 0");
        }else if(idx > 0){
        	sql.append(" and type = ").append(idx);
        }else {
        	//append no type condition if the idx is -1;
        }
        sql.append(" and lang1 = '").append(lang1).append("'");
        
        try {
	        ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
	        rs.beforeFirst();
	        while (rs.next()) {
	        	valDspIdx.setText(rs.getString("lang5"));
				valPrice.setText(rs.getString("lang6"));
	        }
        }catch(Exception e) {
        	L.e("AddModification ", "Exception happend when update the value in propertiy fields with searched out records." + sql, e);
        }
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    @Override
	public void run() {
        String curContent = txaCurContent.getText(); // 要检查文本区中是否有新字段产生,如果有便要使'添至列表'按钮激活 把文本框中字段还原为字符串数组
        ArrayList<String> listInListComponent = getListInCurrentListComponent();
        if (curContent == null || curContent.length() == 0) {
            for (int i = 0; i < listInListComponent.size(); i++) {
                listModel.setElementAt(new CheckItem(listInListComponent.get(i).toString(), false), i);
            }
            btnApplyToList.setEnabled(false);
            btnApplyToCategory.setEnabled(false);
            btnOK.setEnabled(false);
            return;
        }

        ArrayList<String> textArr = getInputModification();
        
        boolean hasNewField = false; // 定义一个布尔值用来表示有新字段产生
        for (int i = 0; i < textArr.size(); i++)
            if (!listInListComponent.contains(textArr.get(i).toString())) { // 列表框模型中不包含才有
                hasNewField = true;
                break;
            }

        if (hasNewField && !btnApplyToList.isEnabled()) { // 添加到列表按钮置有效
            btnApplyToList.setEnabled(true);
            if (!btnOK.isEnabled()) // 确定按钮置有效
                btnOK.setEnabled(true);
        }
        btnApplyToCategory.setEnabled(textArr.size() == 1);
    }

    // 以下为本类的变量声明
    private JButton btnApplyToList; // 几个按钮,在右上方
    private JButton btnApplyToCategory; // 几个按钮,在右上方
    private JButton btnDelete;
    private JButton resetBTN;
//    private JButton ok2; // 确定和取消按钮
    private JButton btnOK;
    private JList<CheckItem> modificationList; // 列表框及其模型
    private PIMScrollPane scrPane;
    private JTabbedPane tabbedPane;
    private DefaultListModel<CheckItem> listModel;

    private JLabel midLabel; // "可用类别"标签
    private JLabel topLabel; // "项目属于这些类别"标签

    private JTextArea txaCurContent; // 文本区及其模型
    private boolean modified; // 保存修改标志,以便父对话盒作相应措施
    public static final int OFFSET = -1;// 构建列表框和文本区用的一个常量 以后要去除
    private boolean itemChanged; // 字段增减标志,用于存盘
    
    private JLabel lblDspIdx;
    private JLabel lblPrice;
    private JTextField valDspIdx;
    private JTextField valPrice;
    private JButton btnApply;
    
    
    public boolean isModified() {
        return this.modified;
    }
}
