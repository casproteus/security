package org.cas.client.platform.contact.dialog.selectcontacts;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.cas.client.platform.cascontrol.dialog.CASDialogKit;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.resource.international.ListDialogConstant;



public class SearchUserDialog extends JDialog implements ActionListener
{
    private Dimension dialogSize;
    /** Creates a new instance of SearchUserDialog
     * @param f 父窗体
     * @param model 是否模式
     */

    public SearchUserDialog(JDialog f,boolean model)
    {
        super(f,model);
        setTitle(ListDialogConstant.SEARCH_USER_TITLE);

        dialogSize = getSearchUserSize();
        w = dialogSize.width;
        h = dialogSize.height;

        initSearchDialog();

        //控制对话框显示的位置及大小
        setBounds((CustOpts.SCRWIDTH - w)/2, (CustOpts.SCRHEIGHT - h)/2, w, h);	//对话框的默认尺寸。
    }

    /**
     * 初始化
     */
    private  void initSearchDialog()
    {
        JLabel scopeLabel = createLabel(ListDialogConstant.SEARCH_RANGE,'K');
        box = createComboBox(data,150);
        box.addActionListener(this);
        JButton webNodeButton = createButton(ListDialogConstant.WEB_NODE,'W');
        JButton startSearch = createButton(ListDialogConstant.START_SEARCH,'F');
        JButton stopButton = createButton(ListDialogConstant.STOP_SEARCH,'P');
        clearAll = createButton(ListDialogConstant.CELEAR_ALL,'L');
        clearAll.addActionListener(this);
        JLabel iconLabel = new JLabel();
        cancel = createButton(ListDialogConstant.CLOSE_DIALOG,'C');

        int scopeLabelMaxW = 10+CASDialogKit.getMaxWidth(new JComponent[]
        {scopeLabel});
        buttonMaxWidth = 10+CASDialogKit.getMaxWidth(new JComponent[]
        {webNodeButton,startSearch,stopButton,clearAll,iconLabel,cancel});
        int boxW = w - scopeLabelMaxW - 2*CustOpts.HOR_GAP - buttonMaxWidth -10;
        scopeLabel.setBounds(5,5,scopeLabelMaxW,CustOpts.LBL_HEIGHT);
        box.setBounds(5+scopeLabelMaxW+CustOpts.HOR_GAP,5,boxW,CustOpts.LBL_HEIGHT);
        int webNodeButtonX = box.getX()+boxW+CustOpts.HOR_GAP;
        webNodeButton.setBounds(webNodeButtonX,5,buttonMaxWidth,CustOpts.BTN_HEIGHT);
        scopeLabel.setLabelFor(box);

        startSearch.setBounds(webNodeButtonX,webNodeButton.getY()+2*CustOpts.BTN_HEIGHT,buttonMaxWidth,CustOpts.BTN_HEIGHT);
        stopButton.setBounds(webNodeButtonX,startSearch.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP,buttonMaxWidth,CustOpts.BTN_HEIGHT);
        clearAll.setBounds(webNodeButtonX,stopButton.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP,buttonMaxWidth,CustOpts.BTN_HEIGHT);
        //int iconW = searchIcon.getIconWidth();
        //int iconH = searchIcon.getIconHeight();
        //iconLabel.setBounds(webNodeButtonX+(buttonMaxWidth-iconW)/2,clearAll.getY()+BUTTON_HEIGHT+VERTICAL_GAP,iconW,iconH);
        //iconLabel.setIcon(searchIcon);
        cancel.setBounds(webNodeButtonX,h - CustOpts.BTN_HEIGHT - CustOpts.HOR_GAP,buttonMaxWidth,CustOpts.BTN_HEIGHT);

        //general
        paneH = h -  CustOpts.LBL_HEIGHT - CustOpts.VER_GAP - CustOpts.HOR_GAP - 5;
        tabbedPane = new JTabbedPane();
        tabbedPane.add(returnUserPanel(),ListDialogConstant.USER_OF_DIALOG);
        tabbedPane.setBounds(5,5+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,w - 10 - CustOpts.HOR_GAP - buttonMaxWidth,paneH);

        //advance
        pane = new JTabbedPane();
        pane.add(returnUserPanel(),ListDialogConstant.USER_OF_DIALOG);
        pane.add(returnAdvancePanel(),ListDialogConstant.ADVANCE_OF_DIALGO);
        pane.setBounds(5,5+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,w - 10 - CustOpts.HOR_GAP - buttonMaxWidth,paneH);
        pane.setVisible(false);

        getContentPane().add(scopeLabel);
        getContentPane().add(box);
        getContentPane().add(webNodeButton);
        getContentPane().add(startSearch);
        getContentPane().add(stopButton);
        getContentPane().add(clearAll);
        getContentPane().add(iconLabel);
        getContentPane().add(cancel);
        getContentPane().add(pane);
        getContentPane().add(tabbedPane);

    }

    /**
     *  用户面板
     */
    private JPanel returnUserPanel()
    {
        JPanel epanel = new JPanel();
        epanel.setLayout(null);
        epanel.setBorder(null);

        JLabel nameLabel = createLabel(ListDialogConstant.SEARCH_NAME,'N');
        JTextField nameField = createTextField();
        JLabel emailLabel = createLabel(ListDialogConstant.LIST_EMAIL_ADDRESS,'E');
        JTextField emailField = createTextField();

        JLabel addressLabel = createLabel(ListDialogConstant.SEARCH_ADDRESS,'S');
        JTextField addressField = createTextField();
        JLabel telephoneLabel = createLabel(ListDialogConstant.TELEPHONE_USER,'H');
        JTextField telephoneField = createTextField();
        JLabel otherLabel = createLabel(ListDialogConstant.OTHER_CONTENT,'O');
        JTextField otherField = createTextField();

        int labelMaxWidth = 10+CASDialogKit.getMaxWidth(new JComponent[]{nameLabel,emailLabel,addressLabel,telephoneLabel,otherLabel});

        //add label to epanel(panel)
        nameLabel.setBounds(5,5,labelMaxWidth,CustOpts.LBL_HEIGHT);
        emailLabel.setBounds(5,nameLabel.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,labelMaxWidth,CustOpts.LBL_HEIGHT);

        //add label to ep(panel)
        ep = new JPanel();
        ep.setBorder(null);
        ep.setLayout(null);

        int epWidth = w - buttonMaxWidth - CustOpts.HOR_GAP - 15; //pane.getWidth();
        ep.setBounds(0,emailLabel.getY()+CustOpts.LBL_HEIGHT,epWidth,paneH-CustOpts.LBL_HEIGHT  + CustOpts.BTN_HEIGHT-5);
        addressLabel.setBounds(5,CustOpts.VER_GAP,labelMaxWidth,CustOpts.LBL_HEIGHT);
        telephoneLabel.setBounds(5,addressLabel.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,labelMaxWidth,CustOpts.LBL_HEIGHT);
        otherLabel.setBounds(5,telephoneLabel.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,labelMaxWidth,CustOpts.LBL_HEIGHT);

        int fieldWidth = epWidth - labelMaxWidth - CustOpts.HOR_GAP - 15;
        int fieldX = addressLabel.getX()+CustOpts.HOR_GAP+labelMaxWidth;
        //add field to epanel(panel)
        nameField.setBounds(fieldX,5,fieldWidth,CustOpts.LBL_HEIGHT);
        emailField.setBounds(fieldX,nameLabel.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,fieldWidth,CustOpts.LBL_HEIGHT);

        //add field to ep(panel);
        addressField.setBounds(fieldX,CustOpts.VER_GAP,fieldWidth,CustOpts.LBL_HEIGHT);
        telephoneField.setBounds(fieldX,addressLabel.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,fieldWidth,CustOpts.LBL_HEIGHT);
        otherField.setBounds(fieldX,telephoneLabel.getY()+CustOpts.LBL_HEIGHT+CustOpts.VER_GAP,fieldWidth,CustOpts.LBL_HEIGHT);

        //label 和 textField 帮定
        nameLabel.setLabelFor(nameField);
        emailLabel.setLabelFor(emailField);
        addressLabel.setLabelFor(addressField);
        telephoneLabel.setLabelFor(telephoneField);
        otherLabel.setLabelFor(otherField);

        ep.add(addressLabel);
        ep.add(telephoneLabel);
        ep.add(otherLabel);
        ep.add(addressField);
        ep.add(telephoneField);
        ep.add(otherField);

        epanel.add(nameLabel);
        epanel.add(emailLabel);
        epanel.add(nameField);
        epanel.add(emailField);
        epanel.add(ep);

        return epanel;

    }

    /**
     *  高级面板
     */
    private JPanel returnAdvancePanel()
    {

        int width = w - buttonMaxWidth - CustOpts.HOR_GAP - 15;
        int height = h - 2*CustOpts.BTN_HEIGHT - CustOpts.VER_GAP - CustOpts.HOR_GAP;
        JPanel aep = new JPanel();
        aep.setPreferredSize(new Dimension(width,height));
        aep.setOpaque(false);
        aep.setBorder(new TitledBorder(new EtchedBorder(), ListDialogConstant.PANEL_TITLE,
        		4, 2,CustOpts.custOps.getFontOfDefault()));
        
        int boxWidth = (width - 2*CustOpts.HOR_GAP - 15)/3;
        int boxH = CustOpts.LBL_HEIGHT+CustOpts.VER_GAP;

        //
        JComboBox nameBox = createComboBox(nameList,80);
        nameBox.setBounds(5,boxH,boxWidth,CustOpts.BTN_HEIGHT);
        nameBox.setSelectedItem(ListDialogConstant.NAME);

        //
        JComboBox containBox = createComboBox(containList,80);
        containBox.setBounds(nameBox.getX()+boxWidth+CustOpts.HOR_GAP,boxH,boxWidth,CustOpts.BTN_HEIGHT);
        containBox.setSelectedItem(ListDialogConstant.INCLUDE);

        //
        field = createTextField();
        field.setBounds(containBox.getX()+boxWidth+CustOpts.HOR_GAP,boxH,boxWidth,CustOpts.BTN_HEIGHT);

        //
        addButton = createButton(ListDialogConstant.ADD,'A');
        deletButton = createButton(ListDialogConstant.REMOVE,'E');
        int buttonMaxW = 10+CASDialogKit.getMaxWidth(new JComponent[]{addButton,deletButton});
        int buttonX = width - buttonMaxW - 10;
        int buttonY = field.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP;
        addButton.setBounds(buttonX,buttonY,buttonMaxW,CustOpts.BTN_HEIGHT);
        deletButton.setBounds(buttonX,addButton.getY()+CustOpts.BTN_HEIGHT+CustOpts.VER_GAP,buttonMaxW,CustOpts.BTN_HEIGHT);
        addButton.addActionListener(this);
        deletButton.addActionListener(this);

        DefaultListModel m = new DefaultListModel();
        list = new JList(m);
        int scrollPaneWidth = width - buttonMaxW - CustOpts.HOR_GAP - 15;
        PIMScrollPane scrollPane = new PIMScrollPane(list,PIMScrollPane.VERTICAL_SCROLLBAR_ALWAYS,PIMScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        scrollPane.setBounds(5,buttonY,scrollPaneWidth,height - addButton.getY() - CustOpts.LBL_HEIGHT);

        scrollPane.getViewport().setBackground(Color.white);
        scrollPane.setBorder(BorderFactory.createLoweredBevelBorder());

        aep.add(nameBox);
        aep.add(containBox);
        aep.add(field);
        aep.add(scrollPane);
        aep.add(addButton);
        aep.add(deletButton);

        return aep;
    }

    /**
     * 创建按钮
     */
    private  JButton createButton(String name,char ch) //,Container c,int x,int y,int w,EListener listener)
    {
        JButton button = new JButton(name);
        button.setMnemonic(ch); //,c,x,y,w,listener);
        return button;
    }

    /**
     * 创建标签
     */
    private  JLabel createLabel(String name,char c)
    {
        return new JLabel(name,c);
    }

    /**
     * 创建文本域
     */
    private  JTextField createTextField()
    {
        return new JTextField();
    }

    /**
     * 创建 ComboBox
     */
    private JComboBox createComboBox(Object [] l,int w)
    {
    	JComboBox tmpComb = new JComboBox(l);
    	tmpComb.setSize(w, CustOpts.BTN_HEIGHT);
        return tmpComb;
    }

    /**
     * 初始化对话框的尺寸
     */
    private Dimension getSearchUserSize()
    {
        return new Dimension(400,200);
    }

    ////////////////////////////////////////////////////////////////
    /////////////    ------   监 听 动 作  ------    ///////////////
    ////////////////////////////////////////////////////////////////
    /** 监听动作
     * @param e 动作事件
     */
    public void actionPerformed(ActionEvent e)
    {
        /* 监听comboBox 选择查找类型 */
        if (e.getSource().equals(box))
        {
            //取得comboBox项的索引值
            int tempIndex = box.getSelectedIndex();
            /* 如果取得的项不是第一个
            显示高级面板
            隐藏contacts特有的组件
             */
            if (tempIndex != 0)
            {
                tabbedPane.setVisible(false);
                pane.setVisible(true);
                ep.setVisible(false);
                isAdvance = true;
            }
            /* 隐藏高级面板
            将contacts中特有的项显示
             */
            else
            {
                pane.setVisible(false);
                tabbedPane.setVisible(true);
                ep.setVisible(true);
                isAdvance = false;
            }
        }

        /* 如果高级面板在显示状态 */
        if (isAdvance == true)
        {
            //
            DefaultListModel model = (DefaultListModel)(list.getModel());
            /* 添加按钮监听列表
            从输入地的域中取得字符串
            加到列表中
             */
            if (e.getSource().equals(addButton))
            {
                String tempStr = field.getText(); //+nameBox.getSelectedItem()+containBox.getSelectedItem();
                if (tempStr.length() != 0)
                {
                    model.addElement(tempStr);
                }
            }
            /* 删除按钮
            将列表中配选择的数据删除
             */
            if (e.getSource().equals(deletButton))
            {
                Object tempO = list.getSelectedValue();
                if (tempO!=null)
                {
                    model.removeElement(tempO);
                }
            }
            /* 删除全部按钮动作 */
            if (e.getSource().equals(clearAll))
            {
                int count = model.getSize();
                if (count > 0)
                {
                    model.removeAllElements();
                }
            }
        }
    }

    /////////////////////////////////////////////////////
    ////~~~~~~~~~~~~    全 局 变 量    ~~~~~~~~~~~~~~////
    /////////////////////////////////////////////////////
    /** 组合框中的元素
     */
    private Object [] data = new Object[]
    {
        "contacts","Yahoo! People Search",
        "Active Directory",
        "Bigfoot Internet 目录服务",
        "InfoSpace Internet 目录服务",
        "InfoSpace 商业目录服务",
        "Switchboard Internet 目录服务",
        "VeriSign Internet 目录服务",
        "WhoWhere Internet 目录服务"
    };

    private Object [] containList = new Object[]
    {
        ListDialogConstant.INCLUDE,ListDialogConstant.START,ListDialogConstant.END,ListDialogConstant.PRONOUNCE
    };

    private Object [] nameList = new Object[]
    {
        ListDialogConstant.NAME,ListDialogConstant.COMMANY,ListDialogConstant.LASTNAME,ListDialogConstant.FIRSTNAME,ListDialogConstant.LIST_EMAIL_COLUMN
    };

    private int w ;
    private int h ;
    private int paneH;
    private int buttonMaxWidth;
    private boolean isAdvance ;
    private JButton cancel;
    private JButton addButton;
    private JButton deletButton ;
    private JButton clearAll ;
    private JList list ;
    private JTextField field;
    private JComboBox box ;
    private JPanel ep ;
    private JTabbedPane pane ;
    private JTabbedPane tabbedPane ;
    //private ImageIcon searchIcon = new ImageIcon(getClass().getResource("search1.jpg"));

}
