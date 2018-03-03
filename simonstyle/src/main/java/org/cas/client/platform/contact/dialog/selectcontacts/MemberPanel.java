package org.cas.client.platform.contact.dialog.selectcontacts;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascontrol.dialog.category.CategoryDialog;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.contact.dialog.ContactDlg;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableModelAryBased;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.ListDialogConstant;


public class MemberPanel extends JPanel implements ActionListener,KeyListener
{
    
    /** Creates a new instance of MemberPanel
     * @param width     : 面板的宽度
     * @param height    : 面板的高度
     * @param prmDialog    : 父对话盒
     * @param prmHashtable : 存放记录的哈希表
     */
    public MemberPanel(int width ,int height,JDialog prmDialog,Hashtable prmHashtable)
    {
        dialog = prmDialog;
        hashtable = prmHashtable;
        setBorder(null);
        setSize(width,height);
        
        /* 名字面板 */
        JPanel namePanel = createNamePanel(width,CustOpts.LBL_HEIGHT+CustOpts.VER_GAP);
        namePanel.setBounds(0,0,namePanel.getWidth(), namePanel.getHeight());
        add(namePanel);
        
        /* 选择成员按钮 */
        selectedMemberButton = createButton(ListDialogConstant.LIST_SELECTED_MEMBER,'M');
        /* 添加新成员按钮 */
        addNewMemberButton = createButton(ListDialogConstant.LIST_ADDNEW_MEMBER,'D');
        /* 删除按钮 */
        removeButton = createButton(ListDialogConstant.LIST_REMOVE,'R');
        /* 更新按钮 */
        JButton updateButton = createButton(ListDialogConstant.LIST_UPDATE,'U');
        
        /* 返回一组按钮中最大宽度 */
        int buttonMaxW = CASUtility.getMaxWidth(new JButton[]
        {
            selectedMemberButton,addNewMemberButton,removeButton,updateButton
        });
        /* 在按钮面板中添加按钮 */
        selectedMemberButton.setBounds(5,namePanel.getY()+CustOpts.LBL_HEIGHT+2*CustOpts.VER_GAP,buttonMaxW,CustOpts.BTN_HEIGHT);
        add(selectedMemberButton);
        addNewMemberButton.setBounds(buttonMaxW+CustOpts.HOR_GAP+5,selectedMemberButton.getY(),buttonMaxW,CustOpts.BTN_HEIGHT);
        add(addNewMemberButton);
        removeButton.setBounds(2*(buttonMaxW+CustOpts.HOR_GAP)+5,selectedMemberButton.getY(),buttonMaxW,CustOpts.BTN_HEIGHT);
        add(removeButton);
        updateButton.setBounds(width-buttonMaxW-5,selectedMemberButton.getY(),buttonMaxW,CustOpts.BTN_HEIGHT);
        add(updateButton);
        
        /* 添加带有表格的滚动窗格 */
        JPanel tablePanel = createTablePanel(width-5,height-(3*CustOpts.LBL_HEIGHT+4*CustOpts.VER_GAP));
        tablePanel.setBounds(0,CustOpts.LBL_HEIGHT + CustOpts.BTN_HEIGHT + 3*CustOpts.VER_GAP,
        		tablePanel.getWidth(), tablePanel.getHeight());
        add(tablePanel);
    }
    
    /**
     * 创建名字面板
     */
    private  JPanel createNamePanel(int w,int h)
    {
        JPanel nameP = new JPanel();
        nameP.setBorder(null);
        nameP.setSize(w,h);
        nameP.setLayout(null);
        //显示名字标签
        JLabel nameLabel = createLabel(ListDialogConstant.LIST_NAME,'N');
        //名字标签宽度
        int labelMaxW = 10+CASDialogKit.getMaxWidth(new JComponent[]
        {nameLabel});
        //输入显示为字段的域
        field = new JTextField(getFieldContents(ContactDefaultViews.SUBJECT));
        field.setBorder(BorderFactory.createLoweredBevelBorder());
        nameLabel.setLabelFor(field);
        nameLabel.setBounds(5,5,labelMaxW,CustOpts.BTN_HEIGHT);
        nameP.add(nameLabel);
        field.setBounds(nameLabel.getX() + labelMaxW, nameLabel.getY(), w-labelMaxW-CustOpts.HOR_GAP-5, CustOpts.BTN_HEIGHT);
        nameP.add(field);
        
        return nameP;
    }
    
    /**
     * 创建表格面板
     */
    private  JPanel createTablePanel(int w,int h)
    {
        JPanel tablePanel = new JPanel();
        tablePanel.setBorder(null);
        tablePanel.setLayout(null);
        tablePanel.setSize(w,h);
        
        //滚动窗格
        PIMScrollPane scroll = createScrollPane(w-10);
        int scrollH = h - CustOpts.LBL_HEIGHT - CustOpts.VER_GAP-10;
        scroll.setBounds(5,5,w-10,scrollH);
        
        //类别按钮
        typeButton = new JButton(DlgConst.CATEGORIES);
        typeButton.setMnemonic('G');
        //类别按钮宽度
        int typeButtonWidth = 10+CASDialogKit.getMaxWidth(new JComponent[]{typeButton});
        typeButton.setBounds(5,scrollH + CustOpts.VER_GAP + 5,typeButtonWidth,CustOpts.BTN_HEIGHT);
        tablePanel.add(typeButton);
        
        //类型文本域
        typeField = new JTextField(CASUtility.EMPTYSTR);
        typeField.setText(getFieldContents(ContactDefaultViews.CATEGORY));
        typeField.setBorder(BorderFactory.createLoweredBevelBorder());
        typeField.setBounds(typeButtonWidth + CustOpts.HOR_GAP + 5, scrollH+CustOpts.VER_GAP+5,
        		w - typeButtonWidth - CustOpts.HOR_GAP - 10, CustOpts.BTN_HEIGHT);
        tablePanel.add(typeField);
        
        tablePanel.add(scroll);
        
        typeButton.addActionListener(this);
        
        return tablePanel;
        
    }
    
    /** 创建滚动窗格
     * @param spWidth 宽度
     * @return 滚动窗格
     */
    public PIMScrollPane createScrollPane(int spWidth)
    {
        /* 在表格中放置两列 */
    	Object[][] tmpAry = new Object[0][0];
        dm = new PIMTableModelAryBased(tmpAry, column);
        dm.setCellEditable(false);
        //如果存在通讯组记录
        if (hashtable != null)
        {
            addTableContents(getReferedFileAsIds());
        }
        
        table = new PIMTable(null,null,null,false);
        table.setModel(dm);
        table.setBorder(null);
        table.setShowGrid(false);
        table.getColumn(imageIcon).setCellRenderer(new IconColumnRenderer());
        int iconW = imageIcon.getIconWidth() + 3;
        table.getColumn(imageIcon).setMaxWidth(iconW);
        table.getColumn(imageIcon).setMinWidth(iconW);
        table.getColumn(imageIcon).setResizable(false); //列头不可移动
        
        /*------------------------------------------------------*/
        //暂时用来处理表格头多一条线的BUG,等完成后去掉
        table.getColumn(ListDialogConstant.LIST_NAME_COLUMN).setMinWidth((spWidth - iconW)/2);
        table.getColumn(ListDialogConstant.LIST_NAME_COLUMN).setPreferredWidth((spWidth - iconW)/2);
        table.getColumn(ListDialogConstant.LIST_NAME_COLUMN).setMaxWidth((spWidth - iconW)/2);
        //暂时用来处理表格头多一条线的BUG,等完成后去掉
        table.getColumnModel().getColumn(2).setMinWidth((spWidth - iconW)/2);
        table.getColumnModel().getColumn(2).setPreferredWidth((spWidth - iconW)/2);
        table.getColumnModel().getColumn(2).setMaxWidth((spWidth - iconW)/2);
        
        /*------------------------------------------------------*/
        
        table.getColumn(ListDialogConstant.LIST_NAME_COLUMN).setResizable(false);
        table.getTableHeader().setReorderingAllowed(false); //不可拖动列
        
        /* 将表格添加到滚动窗格中 */
        PIMScrollPane scroll = new PIMScrollPane(table,PIMScrollPane.VERTICAL_SCROLLBAR_ALWAYS,PIMScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.getViewport().setBackground(Color.white);
        scroll.setBorder(BorderFactory.createLoweredBevelBorder());
        
        table.addKeyListener(this);
        
        return scroll;
    }
    
    /** 创建按钮
     * @param name 按钮标签
     * @param c 快捷键
     * @return 按钮
     */
    public JButton createButton(String name,char c)
    {
        JButton button = new JButton(name);
        button.setMnemonic(c);
        button.addActionListener(this);
        return button;
    }
    
    /** 创建标签
     * @param name 标签名
     * @param c 快捷键
     * @return 标签
     */
    public JLabel createLabel(String name,char c)
    {
        return new JLabel(name,c);
    }
    
    /** 取得表格
     * @return 表格
     */
    public PIMTable getTable()
    {
        return table;
    }
    
    /** 取得哈希表中的内容
     * @param key : 取数据的键值
     * @return 对应的值
     */
    public String getFieldContents(int key)
    {
        return (String)(hashtable.get(PIMPool.pool.getKey(key)));
    }
    
    /**
     * 删除动作
     */
    private void deletAction()
    {
        int tableSelectedRow = table.getSelectedRow();
        int maxSize = table.getRowCount();
        if (tableSelectedRow>=0)
        {
            dm.removeRow(tableSelectedRow);
            if (tableSelectedRow != maxSize-1)
            {
                if (maxSize > 1)
                {
                    table.setRowSelectionInterval(tableSelectedRow,tableSelectedRow);
                    table.setColumnSelectionInterval(0,table.getColumnCount()-1);
                }
            }
            else
            {
                if (maxSize > 1)
                {
                    table.setRowSelectionInterval(tableSelectedRow-1,tableSelectedRow-1);
                    table.setColumnSelectionInterval(0,table.getColumnCount()-1);
                }
            }
        }
    }
    
    /** 监听动作
     * @param e 动作事件
     */
    public void actionPerformed(ActionEvent e)
    {
        Object source = e.getSource();
        //* 添加新成员按钮动作 */
        if (source == addNewMemberButton)
        {
            new ContactDlg(dialog, new SaveContentsAction()).show();
        }
        //* 选择成员按钮动作 */
        if (source == selectedMemberButton)
        {
            member = new SelectedNewMemberDlg(dialog,true,null);
            member.show();
            member.updateCommunicationList(dm);
            if (dm.getRowCount() > 0)
            {
                table.setRowSelectionInterval(0,0);
                table.setColumnSelectionInterval(0,table.getColumnCount()-1);
                //tableSelectedRow = 0;
            }
        }
        //* 删除按钮动作 */
        if (source == removeButton)
        {
            deletAction();
        }
        //类型按钮
        if (source == typeButton)
        {
            //if(categroydialog == null)
            //{
            categroydialog = new CategoryDialog(dialog,typeField.getText());
            categroydialog.show();
            typeField.setText(categroydialog.getCategories());
            //            }
            //            else
            //            {
            //                categroydialog.show(typeField.getText());
            //                typeField.setText(categroydialog.getCategories());
            //            }
            if (categroydialog.isModified())
            {
                typeField.setText(categroydialog.getCategories());
                hashtable.put(PIMPool.pool.getKey(ContactDefaultViews.CATEGORY),categroydialog.getCategories());
            }
        }
    }
    
    /*@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
    @@@@@@@@@@@@@@@@@@    键 盘 事 件 监 听 器  @@@@@@@@@@@@@@@@@@@@@@
    @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@*/
    /** Invoked when a key has been pressed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key pressed event.
     * @param e 键盘事件
     */
    public void keyPressed(KeyEvent e)
    {
        int keycode = e.getKeyCode();
        if (keycode == KeyEvent.VK_DELETE)
        {
            deletAction();
        }
    }
    
    /** Invoked when a key has been released.
     * See the class description for {@link KeyEvent} for a definition of
     * a key released event.
     * @param e 键盘事件
     */
    public void keyReleased(KeyEvent e)
    {
    }
    
    /** Invoked when a key has been typed.
     * See the class description for {@link KeyEvent} for a definition of
     * a key typed event.
     * @param e 键盘事件
     */
    public void keyTyped(KeyEvent e)
    {
    }
    
    //////////////////////////////////////////////////////////////////////
    /** 取得文字文本域的内容
     * @return 内容
     */
    public String getMemberField()
    {
        return field.getText();
    }
    
    /** 返回输入显示为组件
     * @return 显示为组件
     */
    public JTextField getField()
    {
        return field;
    }
    
    /**
     * 返回要发送的一条记录
     */
    public void getRecord()
    {
        if (getMemberField().length() != 0)
        {
            hashtable.put(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT),getMemberField());
            hashtable.put(PIMPool.pool.getKey(ContactDefaultViews.MEMBERLIST),putSelectedRecordId());
            hashtable.put(PIMPool.pool.getKey(ContactDefaultViews.TYPE),PIMPool.pool.getKey(1));
        }
    }
    
    /**
     * 取得PIMRecord的ID号
     */
    private String [] getNativeRecordId()
    {
        String[] recordID = CASControl.ctrl.getModel().getAllContactRecordId();
        return recordID;
    }
    /**
     * 从数据库中取出 字段为 "显示为" 的内容
     */
    private String [] getDisplayAsName()
    {
        String [] tempStr = CASControl.ctrl.getModel().getAllContactDisplayAs();
        return tempStr;
    }
    
    /** 将选取项地ID组合成用逗号分割的字符串
     * @return 经处理的字符串数组
     */
    public String putSelectedRecordId()
    {
        String tmpIdsString = CASUtility.EMPTYSTR;
        int tmpRowCount = table.getRowCount();
        String [] displayAsString = new String[tmpRowCount];
        String [] allDisplayAs = getDisplayAsName();
        String [] recordID = getNativeRecordId();
        int allDisplayAsLn = allDisplayAs.length;
        if (tmpRowCount >0)
        {
            for (int i = 0; i<allDisplayAsLn;i++)
            {
                if (allDisplayAs[i] != null)
                {
                    for (int j=0; j<tmpRowCount;j++)
                    {
                        displayAsString[j] = (String)(dm.getValueAt(j,1));
                        
                        if (allDisplayAs[i].equals(displayAsString[j]))
                        {
                            tmpIdsString += recordID[i]+',';
                            break;
                        }
                    }
                }
            }
            return tmpIdsString.substring(0,tmpIdsString.length()-1);
        }
        else
        {
            return tmpIdsString;
        }
    }
    
    /**
     * call by: addTableContents()
     *        : getContactsType();
     * 取得显示为字段里所有记录的ID
     */
    private int [] getReferedFileAsIds()
    {
        Object tmpIdsobject = hashtable.get(PIMPool.pool.getKey(ContactDefaultViews.MEMBERLIST));
        String stringIDs = (String)tmpIdsobject;
        if (tmpIdsobject != null && stringIDs.length() != 0)
        {
            Vector idsVector = CASUtility.parserStrToVec(stringIDs,',',stringIDs.length());
            int [] ids = new int[idsVector.size()];
            for (int i = 0; i<idsVector.size();i++)
            {
                try
                {
                    ids [i] = Integer.parseInt((String)(idsVector.get(i)));
                }
                catch (NumberFormatException e)
                {}
            }
            return ids;
            
        }
        else
        {
            return new int[0];
        }
    }
    
    /**取得相应ID的PIMRecord
     * 根据记录的Id号添加表格内容
     * called by MainListDialog;
     */
    void addTableContents(int [] prmRecordIds)
    {
        if (prmRecordIds != null && prmRecordIds.length > 0)
        {
            ICASModel pimModel = CASControl.ctrl.getModel();
            String [] displayNames = new String[prmRecordIds.length];
            String [] emailAddress = new String[prmRecordIds.length];
            int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
            int tmpAPpNodeID = CASUtility.getAPPNodeID(tmpAppIndex);
            for (int i = 0; i<prmRecordIds.length;i++)
            {
                PIMRecord tmpRecord = pimModel.selectRecord(tmpAppIndex,prmRecordIds[i],tmpAPpNodeID);
                displayNames[i] = (String)tmpRecord.getFieldValue(ContactDefaultViews.SUBJECT);
                emailAddress[i] = (String)tmpRecord.getFieldValue(ContactDefaultViews.EMAIL);
                dm.insertRow(dm.getRowCount(), new Object[]
                {
                    imageIcon,displayNames[i],emailAddress[i]
                });
            }
        }
    }
    
    /**
     * 取得联系人的类型
     * @return a array of contacts's type
     */
    public int [] getContactsType()
    {
        int [] tmpIds = getReferedFileAsIds();
        int [] tmpTypes = new int[tmpIds.length];
        int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
        int tmpAPpNodeID = CASUtility.getAPPNodeID(tmpAppIndex);
        for (int i = 0; i<tmpIds.length;i++)
        {
            PIMRecord tmpRecord = CASControl.ctrl.getModel().selectRecord(tmpAppIndex,tmpIds[i],tmpAPpNodeID);
            Short typeInteger = (Short)(tmpRecord.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.TYPE)));
            if (typeInteger != null)
            {
                tmpTypes[i] = typeInteger.intValue();
            }
            else
            {
                tmpTypes[i] = 0;
            }
        }
        return tmpTypes;
    }
    
    //private int tableSelectedRow = -1;
    //联系人图标
    private Icon imageIcon = CustOpts.custOps.getContactsIcon(false);
    private CategoryDialog categroydialog;
    private SelectedNewMemberDlg member;
    private JDialog dialog;
    private Hashtable hashtable;
    private JTextField field;
    private JButton removeButton;
    private PIMTable table;
    private JButton typeButton;
    private JTextField typeField;
    private JButton addNewMemberButton ;
    private JButton selectedMemberButton ;
    private PIMTableModelAryBased dm;
    private Object[] column  = new Object[]
    {imageIcon,ListDialogConstant.LIST_NAME_COLUMN,ListDialogConstant.LIST_EMAIL_COLUMN};
}
