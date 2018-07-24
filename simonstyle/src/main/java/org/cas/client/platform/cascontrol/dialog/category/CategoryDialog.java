package org.cas.client.platform.cascontrol.dialog.category;

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
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.CategoryDialogConstants;

public class CategoryDialog extends JDialog implements ActionListener, ListSelectionListener, KeyListener,
        MouseListener, Runnable, ComponentListener {
	/**
	 * to indicate the category is used for what. e.g. 
	 * if type == -1 then it's used for employee.
	 * if type >= 0, means it's for dish.
	 */
	private int type = 0;	
	
    /**
     * 创建一个 Category 的实例
     * 
     * @param prmParent
     *            父窗体
     * @param prmCategoryInfo
     *            逗号分隔的字符串
     */
    public CategoryDialog(JDialog prmParent, String prmCategoryInfo) {
        super(prmParent, true);
        textAreaData = prmCategoryInfo;
        initComponent(); // 组件初始化并布局
    }

    /**
     * 创建一个 Category 的实例
     * 
     * @param prmParent
     *            父窗体
     * @param prmCategoryInfo
     *            逗号分隔的字符串
     */
    public CategoryDialog(Frame prmParent, String prmCategoryInfo) {
        super(prmParent, true);
        textAreaData = prmCategoryInfo;
        initComponent(); // 组件初始化并布局
    }

    /**
     * 创建一个 Category 的实例
     * 
     * @param prmParent
     *            父窗体
     * @param prmCategoryInfo
     *            逗号分隔的字符串
     */
    public CategoryDialog(JDialog prmParent, String prmCategoryInfo, int type) {
        super(prmParent, true);
        textAreaData = prmCategoryInfo;
        this.type = type;
        initComponent(); // 组件初始化并布局
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

    /** 初始化并布局; */
    private void initComponent() {
        setTitle(CategoryDialogConstants.CATEGORYTITLE); // 设置标题
        getContentPane().setLayout(null);
        setBounds((CustOpts.SCRWIDTH - 250) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 250, 320); // 对话框的默认尺寸。
        setResizable(true);

        // init--------------------------
        midLabel = new JLabel(CategoryDialogConstants.MIDDLE_LABEL); // "可用类别"标签
        textArea = new JTextArea(); // 加入会滚动的文本区
        topLabel = new JLabel(CategoryDialogConstants.TOP_LABEL); // "项目属于这些类别"标签
        categoryList = new JList();
        addToListBTN = new JButton(CategoryDialogConstants.ADD_TO_LIST); // 加至列表按钮
        deleteBTN = new JButton(CategoryDialogConstants.DELETE_BTN); // 删除按钮
        resetBTN = new JButton(CategoryDialogConstants.RESET_BTN); // 重置按钮
        ok = new JButton(CategoryDialogConstants.OK_BTN); // 设置OK按钮
        cancel = new JButton(CategoryDialogConstants.CANCEL_BTN); // 设置Cancel按钮

        // properties-------------------------
        midLabel.setLabelFor(categoryList);
        topLabel.setLabelFor(textArea);
        categoryList.setCellRenderer(new CategoryListRenderer());
        midLabel.setDisplayedMnemonic('V');
        topLabel.setDisplayedMnemonic('I');
        addToListBTN.setMnemonic('A');
        deleteBTN.setMnemonic('D');
        resetBTN.setMnemonic('R');
        textArea.setText(textAreaData);
        addToListBTN.setMargin(new Insets(0, 0, 0, 0));
        deleteBTN.setMargin(new Insets(0, 0, 0, 0));
        resetBTN.setMargin(new Insets(0, 0, 0, 0));
        addToListBTN.setEnabled(false); // 一开始这项为禁止
        ok.setEnabled(false); // 一开始这项为禁止

        // layout---------------------------
        reLayout();

        // build----------------------------
        getContentPane().add(midLabel);
        getContentPane().add(addToListBTN);
        getContentPane().add(categoryList);
        getContentPane().add(deleteBTN);
        getContentPane().add(resetBTN);
        getContentPane().add(textArea);
        getContentPane().add(topLabel);
        getContentPane().add(ok);
        getContentPane().add(cancel);

        // listeners------------------------
        addToListBTN.addActionListener(this);
        deleteBTN.addActionListener(this);
        resetBTN.addActionListener(this);
        cancel.addActionListener(this);
        ok.addActionListener(this);
        categoryList.addListSelectionListener(this);
        categoryList.addMouseListener(this);
        textArea.addKeyListener(this);
        categoryList.addKeyListener(this);
        getContentPane().addComponentListener(this);

        // cotents--------------------------
        initData(); // 初始化文本区和列表框数据
        categoryList.setModel(categoryListModel);
    }

    private void reLayout() {
        cancel.setBounds(getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getHeight()
                - CustOpts.BTN_HEIGHT - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        topLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, cancel.getX() - CustOpts.HOR_GAP * 2,
                CustOpts.LBL_HEIGHT);
        textArea.setBounds(topLabel.getX(), topLabel.getY() + CustOpts.LBL_HEIGHT, topLabel.getWidth(),
                CustOpts.LBL_HEIGHT * 3);
        midLabel.setBounds(textArea.getX(), textArea.getY() + textArea.getHeight() + CustOpts.VER_GAP,
                textArea.getWidth(), CustOpts.LBL_HEIGHT);
        categoryList.setBounds(midLabel.getX(), midLabel.getY() + midLabel.getHeight(), // "可用类别"列表框
                midLabel.getWidth(), cancel.getY() - CustOpts.VER_GAP - midLabel.getY() - midLabel.getHeight());
        addToListBTN.setBounds(cancel.getX(), CustOpts.LBL_HEIGHT, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        deleteBTN.setBounds(addToListBTN.getX(), categoryList.getY(), CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        resetBTN.setBounds(deleteBTN.getX(), deleteBTN.getY() + deleteBTN.getHeight() + CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
    }

    /** 初始化时使用 */
    private void initData() {
        categoryListModel = new DefaultListModel();

        // 把文本框中字段还原为字符串数组
        String[] usedFields = stringToArray(textAreaData);
        ArrayList usedArr = new ArrayList();

        // 放入一个 ArrayList 中以备用
        if (usedFields != null && usedFields.length > 0) {
            usedArr = new ArrayList(usedFields.length);
            for (int i = 0; i < usedFields.length; i++) {
                usedArr.add(usedFields[i]);
            }
        }
        
        String[] categoryFields = CASControl.ctrl.getModel().getCategoryNamesByType(type);

        // 加入列表框模型
        for (int i = 0, count = categoryFields.length; i < count; i++) {
            boolean checked = false;
            // 置检查标志
            if (usedArr.contains(categoryFields[i])) {
                checked = true;
            }
            categoryListModel.addElement(new CheckItem(categoryFields[i], checked));
        }
        // 反向操作,如果用户的输入导致新的字段的产生,在列表框模型中要加入,并存盘

        if (usedFields != null && usedFields.length > 0) {
            ArrayList tmpModelArr = new ArrayList(categoryFields.length);
            for (int i = 0; i < categoryFields.length; i++) {
                tmpModelArr.add(categoryFields[i]);
            }

            for (int i = 0; i < usedFields.length; i++) {
                if (!tmpModelArr.contains(usedFields[i])) {
                    // 加一个标志为真的
                    categoryListModel.addElement(new CheckItem(usedFields[i], true));
                    // 保存到数据库中
                    CASControl.ctrl.getModel().addCategroyName(usedFields[i], type);
                }
            }
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
    private String[] stringToArray(
            String string) {
        if (string != null) {
            // 构建字符串分隔器
            StringTokenizer token = new StringTokenizer(string, delimiter);
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
        if (e.getSource() == addToListBTN)
            addToListClicked();
        else if (e.getSource() == deleteBTN)
            deleteClicked();
        else if (e.getSource() == resetBTN)
            resetClicked();
        else if (e.getSource() == ok) {
            okClicked();
            dispose();
        } else if (e.getSource() == cancel)
            dispose();
    }

    /***/
    private void okClicked() {
        textAreaData = textArea.getText();
        modified = true;
        // 要将变化了的项目字段保存到数据库
        if (itemChanged) {
            // 将要在数据模型中遍历,找不到以上两者的某一值,便要加入数据库
            String[] categoryFields = CASControl.ctrl.getModel().getAllCategoryName();
            ArrayList categoryDBFieldsArr = new ArrayList();
            // 用列表框模型中的数据的名字加入 list
            for (int i = 0; i < categoryFields.length; i++)
                categoryDBFieldsArr.add(categoryFields[i]);

            // 现在列表框中的字段
            Object[] modelData = categoryListModel.toArray();

            CheckItem tmpCheckItem = null;
            // 构造一个 ArrayList 存放列表框中的数据中的字符串以便于判断包含关系
            ArrayList modelArr = new ArrayList(modelData.length);
            // 用列表框模型中的数据的名字加入 list
            for (int i = 0; i < modelData.length; i++) {
                tmpCheckItem = (CheckItem) modelData[i];
                modelArr.add(tmpCheckItem.getName());
            }

            // 把文本框中字段还原为字符串数组
            textAreaData = textArea.getText();
            String[] usedFields = stringToArray(textAreaData);
            ArrayList usedArr = new ArrayList();
            // 文本框中字段放入一个 ArrayList 中以备用
            // 去掉过滤信息
            if (usedFields != null && usedFields.length > 0) {
                for (int i = 0; i < usedFields.length; i++) {
                    usedArr.add(usedFields[i]);
                }
            }
            usedArr.trimToSize();

            // 准备工作做好了,进行进行保存新字段类别操作
            // 这是先处理文本区中的字段
            for (int i = 0; i < usedArr.size(); i++) {
                if (!categoryDBFieldsArr.contains(usedArr.get(i).toString())) {
                    // 要加入数据库和这个 ArrayList
                    CASControl.ctrl.getModel().addCategroyName(usedArr.get(i).toString(), type);

                    categoryDBFieldsArr.add(usedArr.get(i).toString());
                }
            }
            // 再处理列表框中的字段
            for (int i = 0; i < modelArr.size(); i++) {
                if (!categoryDBFieldsArr.contains(modelArr.get(i).toString())) {
                    // 要加入数据库和这个 ArrayList
                    CASControl.ctrl.getModel().addCategroyName(modelArr.get(i).toString(), type);
                    categoryDBFieldsArr.add(modelArr.get(i).toString());
                }
            }
        }
    }

    /**
     *
     */
    private void deleteClicked() {
        int tmpSelectionIndex = categoryList.getSelectedIndex();
        int size = categoryListModel.getSize();
        // 看来字段删除必须在这时处理
        CASControl.ctrl.getModel().deleteCategroyName(((CheckItem) categoryList.getSelectedValue()).getName());
        categoryListModel.remove(tmpSelectionIndex);
        itemChanged = true;
        // 如果是最后一个,且列表框中不止一个选项
        if (tmpSelectionIndex == size - 1 && size > 1) {
            // 重设可用字段列表中选中索引,就是前一个
            categoryList.setSelectedIndex(tmpSelectionIndex - 1);
        }
        // 如果是最后一个,且列表框中只有一个选项
        else if (tmpSelectionIndex == size - 1 && size == 1) {
            // 自宫
            deleteBTN.setEnabled(false);
        }
        // 正常设置
        else {
            // 重设可用字段列表中选中索引,就是下一个
            categoryList.setSelectedIndex(tmpSelectionIndex);
        }

        // 下面要处理文本区的显示
        setTextOfTextArea();

        // 确定按钮置有效
        if (!ok.isEnabled()) {
            ok.setEnabled(true);
        }
    }

    /**
     *
     */
    private void resetClicked() {
        // 先全清空
        categoryListModel.removeAllElements();
        // 表示不要再写入磁盘了
        itemChanged = false;

        CASControl.ctrl.getModel().resetCategroyName();
        String[] defaultCate = CASControl.ctrl.getModel().getAllCategoryName();
        // 再把系统默认的数据加入
        for (int i = 0, count = defaultCate.length; i < count; i++) {
            categoryListModel.addElement(new CheckItem(defaultCate[i], false));
        }
        // 设置默认选项
        categoryList.setSelectedIndex(0);
        // TODO: 从数据库中读取信息并装配文本区和列表框
        textAreaData = CASUtility.EMPTYSTR;
        textArea.setText(textAreaData);

        // 确定按钮置有效
        if (!ok.isEnabled()) {
            ok.setEnabled(true);
        }
        // 删除按钮置有效
        if (!deleteBTN.isEnabled()) {
            deleteBTN.setEnabled(true);
        }
    }

    private ArrayList textFieldDateToArr(
            String str) {
        String[] items = stringToArray(str);

        // 构造一个 ArrayList 存放列表框中的数据中的字符串以便于判断包含关系
        ArrayList textArr = new ArrayList(); // modelData.length);

        for (int i = 0; i < items.length; i++) {
            // 是空串我个人认为不用加
            if (items[i].length() == 0) {
                continue;
            }

            textArr.add(items[i]);
        }
        textArr.trimToSize();
        return textArr;
    }

    private ArrayList getListFieldsArr() {
        Object[] modelData = categoryListModel.toArray();

        // 构造一个 ArrayList 存放列表框中的数据中的字符串以便于判断包含关系
        ArrayList modelArr = new ArrayList(modelData.length);

        CheckItem tmpCheckItem;

        // 用列表框模型中的数据的名字加入 list
        for (int i = 0; i < modelData.length; i++) {
            tmpCheckItem = (CheckItem) modelData[i];
            modelArr.add(tmpCheckItem.getName());
        }
        return modelArr;
    }

    /**
     *
     */
    private void addToListClicked() {
        // 文本框和列表框各取得其中的数组
        textAreaData = textArea.getText();
        ArrayList textArr = textFieldDateToArr(textAreaData);
        ArrayList modelData = getListFieldsArr();

        // 表示列表框中字段有更改,但不保存
        itemChanged = true;

        // 循环判断模型中是否包含文本区中的字符串们
        for (int i = 0; i < textArr.size(); i++) {
            // 不包含才加入列表框模型
            if (!modelData.contains(textArr.get(i).toString())) {
                categoryListModel.addElement(new CheckItem(textArr.get(i).toString(), true));
            }
        }

        // 下面要判断原有模型中的字符串在文本区中有没有,没有置 false
        for (int i = 0; i < modelData.size(); i++) {
            // 文本区中不包含便将它设置标志为假
            if (!textArr.contains(modelData.get(i).toString())) {
                categoryListModel.setElementAt(new CheckItem(modelData.get(i).toString(), false), i);
            } else {
                categoryListModel.setElementAt(new CheckItem(modelData.get(i).toString(), true), i);
            }
        }
        setTextOfTextArea();

        // 确定按钮置有效
        if (!ok.isEnabled()) {
            ok.setEnabled(true);
        }

        // 本按钮置无效
        addToListBTN.setEnabled(false);
    }

    /**
     * 设置文本的显示,本方法由列模型自动检测出选中项,组成以逗号为分隔的字符串
     */
    private void setTextOfTextArea() {
        // 用来组装处理文本区中的显示
        StringBuffer sb = new StringBuffer();
        int tmpSize = categoryListModel.size();

        // 用于取值的一个变量
        CheckItem tmpItem;

        // 循环判断列表框中是否有打勾的,有就加入字符串
        for (int i = 0; i < tmpSize; i++) {
            // 要造型的
            tmpItem = (CheckItem) categoryListModel.get(i);
            if (tmpItem.isSelected()) {
                sb = sb.append(tmpItem.getName()).append(delimiter);
            }
        }
        textAreaData = sb.toString();
        // 要不为空,就要去掉逗号尾巴
        if (textAreaData.length() != 0) {
            textAreaData = textAreaData.substring(0, textAreaData.length() - delimiter.length());
        }
        textArea.setText(textAreaData);
    }

    /**
     * 重载,以后要去掉
     * 
     * @param PrmCategories
     *            逗号分隔的字符串
     */
    public void show(
            String PrmCategories) {
        textAreaData = PrmCategories;
        textArea.setText(textAreaData);
        // 一开始这项为禁止
        ok.setEnabled(false);

        // 初始化列表框数据
        // 把文本框中字段还原为字符串数组
        String[] usedFields = stringToArray(textAreaData);
        ArrayList usedArr = textFieldDateToArr(textAreaData);

        String[] categoryFields = CASControl.ctrl.getModel().getAllCategoryName();

        ArrayList tmpModelArr = new ArrayList(categoryFields.length);
        for (int i = 0; i < categoryFields.length; i++) {
            tmpModelArr.add(categoryFields[i]);

        }
        // 反向操作,如果用户的输入导致新的字段的产生,在列表框模型中要加入,并存盘

        if (usedFields != null && usedFields.length > 0) {
            // 这个循环是加字段
            for (int i = 0; i < usedFields.length; i++) {
                if (!tmpModelArr.contains(usedFields[i])) {
                    // 加一个标志为真的
                    categoryListModel.addElement(new CheckItem(usedFields[i], true));
                    // 保存到数据库中
                    CASControl.ctrl.getModel().addCategroyName(usedFields[i], type);
                }
            }

            // 这个循环是处理以下这个情况:如果用户在类别按钮旁的文本区中自己减了一个
            for (int i = 0; i < tmpModelArr.size(); i++) {
                if (!usedArr.contains(tmpModelArr.get(i).toString())) {
                    // 去掉一个标志为真的
                    categoryListModel.setElementAt(new CheckItem(tmpModelArr.get(i).toString(), false), i);
                } else {
                    categoryListModel.setElementAt(new CheckItem(tmpModelArr.get(i).toString(), true), i);
                }
            }
        }
        // 一开始这项为禁止
        addToListBTN.setEnabled(false);
        setVisible(true);
    }

    /**
     * 得到用于表示类别的字符串
     * 
     * @called by: getCategories; TaskDialog; DetailPane; MemberPanel; ContactGeneralPanel
     * @return 逗号分隔的字符串
     */
    public String getCategories() {
        return textAreaData;
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
        if (e.getSource() == categoryList && e.getKeyCode() == KeyEvent.VK_SPACE) {
            updateTextArea();
        }

    }

    /**
     *
     */
    private void updateTextArea() {
        // 处理列表本身的选中状态
        int index = categoryList.getSelectedIndex();
        if (index != -1) {
            CheckItem seleItem = (CheckItem) categoryList.getSelectedValue();
            categoryListModel.set(index, new CheckItem(seleItem.getName(), !seleItem.isSelected()));

            // 以列表框的模型来设置文本区中的显示
            setTextOfTextArea();
            // 确定按钮置有效
            if (!ok.isEnabled()) {
                ok.setEnabled(true);
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
        if (e.getSource() == textArea) // && (e.getKeyChar() != ' ' && e.getKeyChar() != ',' && e.getKeyCode() !=
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
        if (e.getSource() == categoryList) {
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
        textAreaData = textArea.getText(); // 要检查文本区中是否有新字段产生,如果有便要使'添至列表'按钮激活 把文本框中字段还原为字符串数组
        ArrayList modelData = getListFieldsArr();
        if (textAreaData == null || textAreaData.length() == 0) {
            for (int size = modelData.size(), i = 0; i < size; i++)
                categoryListModel.setElementAt(new CheckItem(modelData.get(i).toString(), false), i);
            addToListBTN.setEnabled(false);
            ok.setEnabled(false);
            return;
        }

        ArrayList textArr = textFieldDateToArr(textAreaData);

        boolean hasNewField = false; // 定义一个布尔值用来表示有新字段产生
        for (int i = 0; i < textArr.size(); i++)
            if (!modelData.contains(textArr.get(i).toString())) { // 列表框模型中不包含才有
                hasNewField = true;
                break;
            }

        if (hasNewField && !addToListBTN.isEnabled()) { // 添加到列表按钮置有效
            addToListBTN.setEnabled(true);
            if (!ok.isEnabled()) // 确定按钮置有效
                ok.setEnabled(true);
        }
    }

    // 以下为本类的变量声明
    private JButton addToListBTN; // 几个按钮,在右上方
    private JButton deleteBTN;
    private JButton resetBTN;
    private JButton ok; // 确定和取消按钮
    private JButton cancel;
    private JList categoryList; // 列表框及其模型
    private DefaultListModel categoryListModel;

    private JLabel midLabel; // "可用类别"标签
    private JLabel topLabel; // "项目属于这些类别"标签

    private JTextArea textArea; // 文本区及其模型
    private String textAreaData;
    private boolean modified; // 保存修改标志,以便父对话盒作相应措施
    public static final int OFFSET = -1;// 构建列表框和文本区用的一个常量 以后要去除
    private static final String delimiter = ","; // 分隔符
    private boolean itemChanged; // 字段增减标志,用于存盘
}
