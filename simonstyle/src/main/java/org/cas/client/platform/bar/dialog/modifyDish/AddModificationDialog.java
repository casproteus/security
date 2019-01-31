package org.cas.client.platform.bar.dialog.modifyDish;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BillListPanel;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;

public class AddModificationDialog extends JDialog implements ActionListener, ListSelectionListener, KeyListener,
        MouseListener, Runnable, ComponentListener {
//    /**
//     * 创建一个 Category 的实例
//     * 
//     * @param prmParent
//     *            父窗体
//     * @param prmCategoryInfo
//     *            逗号分隔的字符串
//     */
//    public AddModificationDialog(JDialog prmParent, String prmCategoryInfo) {
//        super(prmParent, true);
//        initComponent(); // 组件初始化并布局
//        initContent(prmCategoryInfo); // 初始化文本区和列表框数据
//    }

    /**
     * 创建一个 Category 的实例
     * 
     * @param prmParent
     *            父窗体
     * @param prmCategoryInfo
     *            逗号分隔的字符串
     */
    public AddModificationDialog(Frame prmParent, String prmCategoryInfo) {
        super(prmParent, true);
        initComponent(); // 组件初始化并布局
        initContent(prmCategoryInfo); // 初始化文本区和列表框数据
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
        setBounds((CustOpts.SCRWIDTH - 350) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 350, 320); // 对话框的默认尺寸。
        setResizable(true);

        // init--------------------------
        midLabel = new JLabel(BarFrame.consts.AvailableModification()); // "可用类别"标签
        txaCurContent = new JTextArea(); // 加入会滚动的文本区
        topLabel = new JLabel(BarFrame.consts.AddNewModificationItem()); // "项目属于这些类别"标签
        modificationList = new JList();
        btnApply = new JButton(BarFrame.consts.Apply()); // 加至列表按钮
        btnDelete = new JButton(BarFrame.consts.Delete()); // 删除按钮
        resetBTN = new JButton(BarFrame.consts.RESET()); // 重置按钮
        btnOK = new JButton(BarFrame.consts.OK()); // 设置Cancel按钮

        // properties-------------------------
        midLabel.setLabelFor(modificationList);
        topLabel.setLabelFor(txaCurContent);
        modificationList.setCellRenderer(new ModificationListRenderer());
        modificationList.setBackground(null);
        modificationList.setBorder(new LineBorder(Color.GRAY));
        txaCurContent.setBorder(new LineBorder(Color.GRAY));
        midLabel.setDisplayedMnemonic('V');
        topLabel.setDisplayedMnemonic('I');
        btnApply.setMnemonic('A');
        btnDelete.setMnemonic('D');
        resetBTN.setMnemonic('R');
        btnApply.setMargin(new Insets(0, 0, 0, 0));
        btnDelete.setMargin(new Insets(0, 0, 0, 0));
        resetBTN.setMargin(new Insets(0, 0, 0, 0));
        resetBTN.setVisible(false);
        btnApply.setEnabled(false); // 一开始这项为禁止

        // layout---------------------------
        reLayout();

        // build----------------------------
        getContentPane().add(midLabel);
        getContentPane().add(btnApply);
        getContentPane().add(modificationList);
        getContentPane().add(btnDelete);
        getContentPane().add(resetBTN);
        getContentPane().add(txaCurContent);
        getContentPane().add(topLabel);
        getContentPane().add(btnOK);

        // listeners------------------------
        btnApply.addActionListener(this);
        btnDelete.addActionListener(this);
        resetBTN.addActionListener(this);
        btnOK.addActionListener(this);
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
            btnApply.setBounds(btnOK.getX(), txaCurContent.getY() + txaCurContent.getHeight() + CustOpts.VER_GAP,
            		CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
            midLabel.setBounds( topLabel.getX(),
            		txaCurContent.getY() + txaCurContent.getHeight() + CustOpts.VER_GAP,
            		getWidth() - 2 * CustOpts.SIZE_EDGE - 3 * CustOpts.HOR_GAP, CustOpts.BTN_HEIGHT);
        }else {
        	topLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 0, 0);
        	txaCurContent.setBounds(0, 0, 0, 0);
            btnApply.setBounds(0,0,0,0);
            midLabel.setBounds(topLabel.getX(),0,0,0);
        }
        
        
        modificationList.setBounds(midLabel.getX(), midLabel.getY() + midLabel.getHeight() + CustOpts.VER_GAP, // "可用类别"列表框
        		getWidth() - 2 * CustOpts.SIZE_EDGE - 3 * CustOpts.HOR_GAP, 
                btnOK.getY() - 2 * CustOpts.VER_GAP - midLabel.getY() - midLabel.getHeight());
        if(BillListPanel.curDish == null) {
        	btnDelete.setBounds(btnOK.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, btnOK.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        }else {
        	btnDelete.setBounds(btnOK.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, btnOK.getY(), 0, 0);
        }
        resetBTN.setBounds(btnDelete.getX(), btnDelete.getY() + btnDelete.getHeight() + CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
    }

    /** 初始化时使用 */
    private void initContent( String prmCategoryInfo) {
        txaCurContent.setText("null".equalsIgnoreCase(prmCategoryInfo) ? "" : prmCategoryInfo);
        listModel = new DefaultListModel<CheckItem>();

        // 把文本框中字段还原为字符串数组
        ArrayList<String> inputModification = getInputModification();
        ArrayList<String> allModification = getAllModification();

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
                if (notContained) {
                    // 加一个标志为真的
                    listModel.addElement(new CheckItem(inputModification.get(i), true));
                    // 保存到数据库中
                    insertModification(inputModification.get(i));
                }
            }
        }
        
        modificationList.setModel(listModel);
    }
    
    private ArrayList<String> getAllModification() {
        StringBuilder sql = new StringBuilder("SELECT * FROM modification where status = ").append(DBConsts.original);
        ArrayList<String> nameVec = new ArrayList<String>();
        try {

            Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery(sql.toString());
            while (rs.next()) {
            	StringBuilder sb = new StringBuilder(rs.getString("lang1")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang2")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang3")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang4"));
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
     * 返回所用类别已更改标志
     * 
     * @return 是否已更改
     */
    public boolean isModified() {
        return this.modified;
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
	public void actionPerformed(
            ActionEvent e) {
        if (e.getSource() == btnApply)
            addToListClicked();
        else if (e.getSource() == btnDelete)
            deleteClicked();
        else if (e.getSource() == resetBTN)
            resetClicked();
        else if (e.getSource() == btnOK) {
            if(BillListPanel.curDish != null) {
            	String[] notes = this.txaCurContent.getText().split(BarDlgConst.delimiter);
            	ArrayList<String> allModification = getAllModification();
            	StringBuilder fullModifyString = new StringBuilder();
            	StringBuilder onSrcString = new StringBuilder();
            	for(int i = 0; i < notes.length; i++) {
					for (String fullString : allModification) {
						if(notes[i].trim().length() > 0 && fullString.indexOf(notes[i].trim()) > -1) {
							notes[i] = fullString;
							break;
						}
					}
            		fullModifyString.append(notes[i]).append(BarDlgConst.delimiter);
            		
            		String[] langs = notes[i].split(BarDlgConst.semicolon);
        			String lang_Modify = langs.length > LoginDlg.USERLANG ? langs[LoginDlg.USERLANG] : langs[0];
                	if(lang_Modify.length() == 0)
                		lang_Modify = langs[0];
            		onSrcString.append(lang_Modify).append(BarDlgConst.delimiter);
				}
            	
            	BillListPanel.curDish.setModification(fullModifyString.toString());
            	PIMTable table = ((SalesPanel)BarFrame.instance.panels[2]).billPanel.tblBillPanel;
            	int row = table.getSelectedRow();
            	table.setValueAt(onSrcString.toString(), row, 2);
            }
            dispose();
        }
    }

    private boolean insertModification( String modification) {
        StringBuilder sql = new StringBuilder("INSERT INTO modification (lang1, lang2, lang3, lang4, status) VALUES( '");
        String[] langs = modification.split(BarDlgConst.semicolon);
        sql.append(langs.length > 0 ? langs[0] : "");
        sql.append("', '");
        sql.append(langs.length > 1 ? langs[1] : "");
        sql.append("', '");
        sql.append(langs.length > 2 ? langs[2] : "");
        sql.append("', '");
        sql.append(langs.length > 3 ? langs[3] : "");
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
    
    private boolean deleteModification( String modification) {
    	StringBuilder sql = new StringBuilder("DELETE FROM modification WHERE lang1 = '");
        String[] langs = modification.split(BarDlgConst.semicolon);
        sql.append(langs[0]);
        sql.append("';");

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
    
    /**
     *
     */
    private void deleteClicked() {
        int tmpSelectionIndex = modificationList.getSelectedIndex();
        int size = listModel.getSize();
        // 看来字段删除必须在这时处理
        deleteModification(modificationList.getSelectedValue().getName());
        listModel.remove(tmpSelectionIndex);
        itemChanged = true;
        // 如果是最后一个,且列表框中不止一个选项
        if (tmpSelectionIndex == size - 1 && size > 1) {
            // 重设可用字段列表中选中索引,就是前一个
            modificationList.setSelectedIndex(tmpSelectionIndex - 1);
        }
        // 如果是最后一个,且列表框中只有一个选项
        else if (tmpSelectionIndex == size - 1 && size == 1) {
            // 自宫
            btnDelete.setEnabled(false);
        }
        // 正常设置
        else {
            // 重设可用字段列表中选中索引,就是下一个
            modificationList.setSelectedIndex(tmpSelectionIndex);
        }

        // 下面要处理文本区的显示
        setTextOfTextArea();

        // 确定按钮置有效
        if (!btnOK.isEnabled()) {
            btnOK.setEnabled(true);
        }
    }

    /**
     *
     */
    private void resetClicked() {
        // 先全清空
        listModel.removeAllElements();
        // 表示不要再写入磁盘了
        itemChanged = false;

        CASControl.ctrl.getModel().resetCategroyName();
        ArrayList<String> defaultCate = getAllModification();
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

    private ArrayList<String> getListInListComponent() {
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
    
    private void addToListClicked() {
        // 文本框和列表框各取得其中的数组
        ArrayList<String> inputList = getInputModification();
        ArrayList<String> existingList = getListInListComponent();

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
        existingList = getListInListComponent();
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
        btnApply.setEnabled(false);
    }

    /**
     * 设置文本的显示,本方法由列模型自动检测出选中项,组成以逗号为分隔的字符串
     */
    private void setTextOfTextArea() {
        // 用来组装处理文本区中的显示
        StringBuffer sb = new StringBuffer();
        int tmpSize = listModel.size();

        // 用于取值的一个变量
        CheckItem tmpItem;

        // 循环判断列表框中是否有打勾的,有就加入字符串
        for (int i = 0; i < tmpSize; i++) {
            // 要造型的
            tmpItem = listModel.get(i);
            if (tmpItem.isSelected()) {
                sb = sb.append(tmpItem.getName()).append(BarDlgConst.delimiter).append(" ");
            }
        }
        // 要不为空,就要去掉逗号尾巴
        txaCurContent.setText(sb.length() == 0 ? sb.toString() : sb.substring(0, sb.length() - 2));
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
        ArrayList<String> allModification = getAllModification();
        
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
        btnApply.setEnabled(false);
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

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    @Override
	public void valueChanged(
            ListSelectionEvent e) {
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
	public void keyPressed(
            KeyEvent e) {
        // 处理列表框空格键选中
        if (e.getSource() == modificationList && e.getKeyCode() == KeyEvent.VK_SPACE) {
            updateTextArea();
        }

    }

    /**
     *
     */
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
	public void keyReleased(
            KeyEvent e) {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
     * event.
     * 
     * @param e
     *            键盘事件
     */
    @Override
	public void keyTyped(
            KeyEvent e) {
        // 处理文本区的击键
        if (e.getSource() == txaCurContent) // && (e.getKeyChar() != ' ' && e.getKeyChar() != ',' && e.getKeyCode() !=
                                       // e.VK_TAB))//(e.getKeyCode() != e.VK_SPACE && e.getKeyChar() != e.VK_COMMA))
        {
            SwingUtilities.invokeLater(this);
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    @Override
	public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    @Override
	public void mouseEntered(
            MouseEvent e) {

    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    @Override
	public void mouseExited(
            MouseEvent e) {

    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    @Override
	public void mousePressed(
            MouseEvent e) {
        if (e.getSource() == modificationList) {
            updateTextArea();
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    @Override
	public void mouseReleased(
            MouseEvent e) {

    }

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
        ArrayList<String> listInListComponent = getListInListComponent();
        if (curContent == null || curContent.length() == 0) {
            for (int size = listInListComponent.size(), i = 0; i < size; i++)
                listModel.setElementAt(new CheckItem(listInListComponent.get(i).toString(), false), i);
            btnApply.setEnabled(false);
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

        if (hasNewField && !btnApply.isEnabled()) { // 添加到列表按钮置有效
            btnApply.setEnabled(true);
            if (!btnOK.isEnabled()) // 确定按钮置有效
                btnOK.setEnabled(true);
        }
    }

    // 以下为本类的变量声明
    private JButton btnApply; // 几个按钮,在右上方
    private JButton btnDelete;
    private JButton resetBTN;
//    private JButton ok2; // 确定和取消按钮
    private JButton btnOK;
    private JList<CheckItem> modificationList; // 列表框及其模型
    private DefaultListModel<CheckItem> listModel;

    private JLabel midLabel; // "可用类别"标签
    private JLabel topLabel; // "项目属于这些类别"标签

    private JTextArea txaCurContent; // 文本区及其模型
    private boolean modified; // 保存修改标志,以便父对话盒作相应措施
    public static final int OFFSET = -1;// 构建列表框和文本区用的一个常量 以后要去除
    private boolean itemChanged; // 字段增减标志,用于存盘
}
