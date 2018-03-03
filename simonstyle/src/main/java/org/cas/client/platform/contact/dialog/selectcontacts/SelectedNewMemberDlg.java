package org.cas.client.platform.contact.dialog.selectcontacts;

import java.awt.BorderLayout;
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
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.casbeans.PIMSeparator;
import org.cas.client.platform.cascontrol.menuaction.SaveContentsAction;
import org.cas.client.platform.cascontrol.menuaction.UpdateContactAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.contact.dialog.ContactDlg;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableModelAryBased;
import org.cas.client.platform.pimview.pimtable.PIMTableModelVecBased;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.ListDialogConstant;


/**@TODO:改类代码需要整理,很多方法用到model,可以改为维护一个model的引用.
 */

public class SelectedNewMemberDlg extends JDialog
	implements ActionListener, MouseListener, KeyListener, ComponentListener{
	
	public SelectedNewMemberDlg(JDialog prmDialog, String prmListModelString){
		this(prmDialog, true, prmListModelString);
		isActAsLinkManDlg = true;
	}

	/** Creates a new instance of SelectedNewMemberDlg
	 * @param prmDialog  : 父对话盒
	 * @param prmIsModel : 是否为模态
	 * @param prmListModelString : 用逗号分隔的显示在列表的显示为字段
	 * @called by: NewRuleDialog; CantactGeneralDialog; DetailPane; Memberpanel; TaskDialog; AppointmentDialog; MailOptionDialog;
	 */
	public SelectedNewMemberDlg(JDialog prmDialog, boolean prmIsModel, String prmListModelString){
		super(prmDialog, prmIsModel);
		listModelString = prmListModelString;
		
		initVec();
		idsVector = new Vector(); 						//保存被选记录ID
		communicationListDataVector = new Vector(); 	//用于保存通讯组列表
		
		initDialog();
	}

	/** Creates a MailFrame中的收件人或抄送人或密送人按钮被点击时调此构造器。
	 * @called by: EmailHeader;
	 * @param prmParent : 父对话盒
	 * @param prmCeceiverString : 用逗号分隔的收件人字符串
	 * @param prmCopySendString : 用逗号分隔的抄送字符串
	 * @param prmBackDoorString : 用逗号分隔的密件抄送字符串
	 */
	public SelectedNewMemberDlg(Frame prmParent, String prmCeceiverString, String prmCopySendString,
			String prmBackDoorString){
		super(prmParent, true);
		toString = prmCeceiverString;
		ccString = prmCopySendString;
		bccString = prmBackDoorString;
		
		initVec();
		idsVector = new Vector();//保存被选记录ID
		
		initReceiverDialog(); //   初始化
		
		isCalledFromEmailHeader = true; //设置是否是从emailHeader调到的标志，被设置说明要考虑三个QuickInfoField（发送，抄送，密送）；
	}

	/** Invoked when the component's size changes. */
    public void componentResized(ComponentEvent e){
    	reLayout();
    }

    /**Invoked when the component's position changes. */    
    public void componentMoved(ComponentEvent e){}

    /**Invoked when the component has been made visible. */
    public void componentShown(ComponentEvent e){}

    /**Invoked when the component has been made invisible. */
    public void componentHidden(ComponentEvent e){}
    
	/** 初始化*/
	private void initDialog(){
		setTitle(ListDialogConstant.LIST_SELECTED_NEW_MEMBER); //设置标题
		setBounds((CustOpts.SCRWIDTH - 500) / 2, (CustOpts.SCRHEIGHT - 410) / 2, 500, 410); //对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		//实例化------------------------------------------------
		addButton = new JButton(ListDialogConstant.ADDTO);
		Vector tColNames = new Vector();//表格列名称
		tColNames.add(imageIcon);
		tColNames.add(ListDialogConstant.DIAPLAY_AS);
		tColNames.add(ListDialogConstant.EMAIL_ADDRESS);
		tColNames.add("ID");
		tableModel = new PIMTableModelVecBased(getDataVector(), tColNames);//表格模型
		nativeTable = new PIMTable(null, null, null, false);
		scroll = new PIMScrollPane(nativeTable, PIMScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				PIMScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);//表格滚动窗格
		borderPane = new JPanel();
		contacts = new JButton(DlgConst.CONTACTS);
		property = new JButton(ListDialogConstant.PROPERTY);
		addToList = new JLabel(ListDialogConstant.ADDTO_LIST);//列表标签
		receiverListLabel = new JLabel(ListDialogConstant.RECEIVER_LIST_NAME);//收件人列表
		listModel = new DefaultListModel();	//列表模型
		if(listModelString != null){
			selectedReceiverVec = CASUtility.parserStrToVec(listModelString, ',', listModelString.length());
			for(int i = 0; i < selectedReceiverVec.size(); i++)
				listModel.addElement(selectedReceiverVec.get(i));
		}
		selectedMemberList = new JList(listModel);
		listSeledMemberPane = new JScrollPane(selectedMemberList);
		separator = new PIMSeparator(CASUtility.EMPTYSTR);//分隔线
		ok = new JButton(ListDialogConstant.LIST_OK);
		cancel = new JButton(ListDialogConstant.LIST_CANCEL);

		//属性------------------------------------
		addButton.setMargin(new Insets(4,4,4,4));
		addButton.setMnemonic('A'); //添加按钮
		contacts.setMnemonic('W'); //联系人按钮
		property.setMnemonic('R'); //属性按钮
		tableModel.setCellEditable(false);
		
		nativeTable.setAutoResizeMode(PIMTable.AUTO_RESIZE_ALL_COLUMNS);
		nativeTable.setModel(tableModel);
		nativeTable.setShowGrid(false);
		nativeTable.getTableHeader().setReorderingAllowed(false);
		nativeTable.getColumn(ListDialogConstant.DIAPLAY_AS).setResizable(false); //列头不可移动
		nativeTable.getColumn(imageIcon).setCellRenderer(new IconColumnRenderer());
		nativeTable.getColumn(imageIcon).setMinWidth(imageIcon.getIconWidth() + 3);
		nativeTable.getColumn(imageIcon).setMaxWidth(imageIcon.getIconWidth() + 3);
		nativeTable.getColumn(imageIcon).setPreferredWidth(imageIcon.getIconWidth() + 3);

		nativeTable.getColumn(ListDialogConstant.EMAIL_ADDRESS).setResizable(false); //列头不可移动
		nativeTable.getColumn(ListDialogConstant.DIAPLAY_AS).setMinWidth(80);
		nativeTable.getColumn(ListDialogConstant.DIAPLAY_AS).setPreferredWidth(80);
		nativeTable.getColumn(ListDialogConstant.EMAIL_ADDRESS).setMinWidth(120);
		nativeTable.getColumn(ListDialogConstant.EMAIL_ADDRESS).setPreferredWidth(120);
		nativeTable.getColumn("ID").setMinWidth(0);
		nativeTable.getColumn("ID").setPreferredWidth(0);
		nativeTable.getColumn("ID").setMaxWidth(0);
		nativeTable.getColumn("ID").setPreferredWidth(0);
		
		scroll.getViewport().setBackground(Color.white); //设置滚动窗格背景色
		borderPane.setBorder(BorderFactory.createEtchedBorder());
		borderPane.setLayout(new BorderLayout());
		getRootPane().setDefaultButton(ok);
		
		//布局------------------------------------
		reLayout();

		//搭建------------------------------------
		getContentPane().add(addButton);
		//      contacts.added(panel,0,contactsH,contactsMaxW,this);
		//      property.added(panel,contactsMaxW + CustOpts.HOR_GAP,contactsH,contactsMaxW,this);
		getContentPane().add(addToList);
		getContentPane().add(receiverListLabel);
		getContentPane().add(listSeledMemberPane);
		getContentPane().add(separator);
		getContentPane().add(ok);
		getContentPane().add(cancel);
		borderPane.add(scroll);
		getContentPane().add(borderPane);
		//监听器------------------------------------
		nativeTable.addMouseListener(this); //添加表格监听器
		ok.addActionListener(this);
		cancel.addActionListener(this);
		addNewMemberListeners();//添加监听器
		addComponentListener(this);
	}

	/**初始化收件人对话盒*/
	private void initReceiverDialog(){
		setTitle(ListDialogConstant.RECEIVER);
		setBounds((CustOpts.SCRWIDTH - 500) / 2,(CustOpts.SCRHEIGHT - 410) / 2, 500,410); //对话框的默认尺寸。
		getContentPane().setLayout(null);
		
		//实例化-------------------------------
		toButton = new JButton(ListDialogConstant.RECEIVER_BUTTON);
		ccButton = new JButton(ListDialogConstant.COPY_SEND);
		bccButton = new JButton(ListDialogConstant.BACKDOOR_COPY_SEND);
		Vector columnNames = new Vector();//表格列名称
		columnNames.add(imageIcon);
		columnNames.add(ListDialogConstant.DIAPLAY_AS);
		columnNames.add(ListDialogConstant.EMAIL_ADDRESS);
		columnNames.add("ID");
		tableModel = new PIMTableModelVecBased(getDataVector(), columnNames);	//表格模型
		nativeTable = new PIMTable(null, null, null, false);
		scroll = new PIMScrollPane(nativeTable, PIMScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				PIMScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);//表格滚动窗格
		borderPane = new JPanel();
		contacts = new JButton(ListDialogConstant.CONTACTS); //联系人按钮
		property = new JButton(ListDialogConstant.PROPERTY);//属性按钮
		receiverLabel = new JLabel(ListDialogConstant.MAIL_RECEIVER);//列表标签
		receiverListLabel = new JLabel(ListDialogConstant.RECEIVER_LIST_NAME);//收件人列表
		
		receiverListModel = new DefaultListModel();//列表模型
		if(toString != null){
			selectedReceiverVec = CASUtility.parserStrToVec(toString, ',', toString.length());
			for(int i = 0; i < selectedReceiverVec.size(); i++)
				receiverListModel.addElement(selectedReceiverVec.get(i));
		}
		receiverList = new JList(receiverListModel);
		tmpSCRPane = new JScrollPane(receiverList);
		
		copySendListModel = new DefaultListModel();
		if(ccString != null){
			selectedCopySendVec = CASUtility.parserStrToVec(ccString, ',', ccString.length());
			for(int i = 0; i < selectedCopySendVec.size(); i++)
				copySendListModel.addElement(selectedCopySendVec.get(i));
		}
		copySendList = new JList(copySendListModel);
		tmpPaneCopy = new JScrollPane(copySendList);
		
		backDoorListModel = new DefaultListModel();
		if(bccString != null){
			selectedBackDoorVec = CASUtility.parserStrToVec(bccString, ',', bccString.length());
			for(int i = 0; i < selectedBackDoorVec.size(); i++)
				backDoorListModel.addElement(selectedBackDoorVec.get(i));
		}
		backDoorList = new JList(backDoorListModel);
		tmpbackDoorPane = new JScrollPane(backDoorList);
		separator = new PIMSeparator(CASUtility.EMPTYSTR);//分隔线
		ok = new JButton(ListDialogConstant.LIST_OK);
		cancel = new JButton(ListDialogConstant.LIST_CANCEL);
		
		//属性------------------------------------
		toButton.setMnemonic('M'); //收件人按钮
		ccButton.setMnemonic('C'); //抄送按钮
		bccButton.setMnemonic('B');//密件抄送按钮
		contacts.setMnemonic('W');
		property.setMnemonic('R');
		toButton.setMargin(new Insets(4,4,4,4)); 
		ccButton.setMargin(toButton.getMargin());
		bccButton.setMargin(toButton.getMargin());
		contacts.setMargin(toButton.getMargin());
		property.setMargin(toButton.getMargin());
		tableModel.setCellEditable(false);
		nativeTable.setAutoResizeMode(PIMTable.AUTO_RESIZE_ALL_COLUMNS);
		nativeTable.setModel(tableModel);
		nativeTable.setShowGrid(false);
		nativeTable.getTableHeader().setReorderingAllowed(false);
		nativeTable.getColumn(ListDialogConstant.DIAPLAY_AS).setResizable(false); //列头不可移动
		nativeTable.getColumn(imageIcon).setCellRenderer(new IconColumnRenderer());
		nativeTable.getColumn(imageIcon).setMinWidth(imageIcon.getIconWidth() + 3);
		nativeTable.getColumn(imageIcon).setMaxWidth(imageIcon.getIconWidth() + 3);
		nativeTable.getColumn(imageIcon).setPreferredWidth(imageIcon.getIconWidth() + 3);
		nativeTable.getColumn(ListDialogConstant.EMAIL_ADDRESS).setResizable(false); //列头不可移动
		//nativeTable.getColumn(OPERATION_TELEPHONE).setResizable(false);//列头不可移动
		//nativeTable.getColumn(HOMEPHONE).setResizable(false);//列头不可移动
		nativeTable.getColumn(ListDialogConstant.DIAPLAY_AS).setMinWidth(80);
		nativeTable.getColumn(ListDialogConstant.DIAPLAY_AS).setPreferredWidth(80);
		nativeTable.getColumn(ListDialogConstant.EMAIL_ADDRESS).setMinWidth(120);
		nativeTable.getColumn(ListDialogConstant.EMAIL_ADDRESS).setPreferredWidth(120);
		nativeTable.getColumn("ID").setMinWidth(0);
		nativeTable.getColumn("ID").setPreferredWidth(0);
		nativeTable.getColumn("ID").setMaxWidth(0);
		nativeTable.getColumn("ID").setPreferredWidth(0);
		scroll.getViewport().setBackground(Color.white);//设置滚动窗格背景色
		borderPane.setBorder(BorderFactory.createEtchedBorder());
		borderPane.setLayout(new BorderLayout());
		
		//布局-----------------------------------
		reLayout();
		
		//搭建-----------------------------
		getContentPane().add(toButton);
		getContentPane().add(ccButton);
		getContentPane().add(bccButton);
		//        contacts.added(panel,0,contactsH,contactsMaxW,this);
		//        property.added(panel,contactsMaxW + CustOpts.HOR_GAP,contactsH,contactsMaxW,this);
		getContentPane().add(receiverLabel);
		getContentPane().add(receiverListLabel);
		getContentPane().add(tmpSCRPane);
		getContentPane().add(tmpPaneCopy);
		getContentPane().add(tmpbackDoorPane);
		getContentPane().add(separator);
		getContentPane().add(ok);
		getContentPane().add(cancel);
		borderPane.add(scroll);
		getContentPane().add(borderPane);

		//监听----------------------------------------------
		nativeTable.addMouseListener(this); //添加表格监听器
		ok.addActionListener(this);
		cancel.addActionListener(this);
		addReceiverListeners();//添加监听器
		addComponentListener(this);
	}

	private void reLayout(){
		cancel.setBounds(getWidth() - CustOpts.HOR_GAP*2 - CustOpts.BTN_WIDTH,
				getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP - CustOpts.BTN_HEIGHT,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(),
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		separator.setBounds(CustOpts.HOR_GAP, ok.getY() - CustOpts.VER_GAP - CustOpts.SEP_HEIGHT,
				getWidth() - CustOpts.HOR_GAP*3, CustOpts.SEP_HEIGHT);
		
		receiverListLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
				receiverListLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
		
		if(addToList != null){ 	//普通联系人选择器
			addToList.setBounds((getWidth() - CustOpts.HOR_GAP * 3)/2 + CustOpts.HOR_GAP*2 + CustOpts.BTN_WIDTH,
					receiverListLabel.getY(),
					addToList.getPreferredSize().width, CustOpts.LBL_HEIGHT);
			addButton.setBounds(addToList.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH,
					addToList.getY() + CustOpts.LBL_HEIGHT,
					CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
			
			borderPane.setBounds(receiverListLabel.getX(), receiverListLabel.getY() + CustOpts.LBL_HEIGHT,
					(getWidth() - CustOpts.HOR_GAP*3)/2 - CustOpts.HOR_GAP,
					separator.getY() - addButton.getY() - CustOpts.VER_GAP/2);
			
			listSeledMemberPane.setBounds(addToList.getX(), borderPane.getY(),
					getWidth() - CustOpts.HOR_GAP * 3 - addToList.getX(),
					borderPane.getHeight());
			
		}else{					//邮件联系人选择器
			receiverLabel.setBounds((getWidth() - CustOpts.HOR_GAP * 3)/2 + CustOpts.HOR_GAP*2 + CustOpts.BTN_WIDTH,
					receiverListLabel.getY(),
					receiverLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
			toButton.setBounds(receiverLabel.getX() - CustOpts.HOR_GAP - CustOpts.BTN_WIDTH,
					receiverLabel.getY() + CustOpts.LBL_HEIGHT,
					CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
			borderPane.setBounds(receiverListLabel.getX(), receiverListLabel.getY() + CustOpts.LBL_HEIGHT,
					(getWidth() - CustOpts.HOR_GAP*3)/2 - CustOpts.HOR_GAP,
					separator.getY() - toButton.getY() - CustOpts.VER_GAP/2);
			tmpSCRPane.setBounds(receiverLabel.getX(), toButton.getY(),
					getWidth() - CustOpts.HOR_GAP * 3 - receiverLabel.getX(),
					(borderPane.getHeight() - 2*CustOpts.VER_GAP)/3);
			ccButton.setBounds(toButton.getX(), tmpSCRPane.getY() + tmpSCRPane.getHeight() + CustOpts.VER_GAP, 
					toButton.getWidth(), CustOpts.BTN_HEIGHT);
			tmpPaneCopy.setBounds(tmpSCRPane.getX(), ccButton.getY(), tmpSCRPane.getWidth(), tmpSCRPane.getHeight());
			bccButton.setBounds(ccButton.getX(), tmpPaneCopy.getY() + tmpPaneCopy.getHeight() + CustOpts.VER_GAP,
					ccButton.getWidth(), CustOpts.BTN_HEIGHT);
			tmpbackDoorPane.setBounds(tmpPaneCopy.getX(), bccButton.getY(),
					tmpPaneCopy.getWidth(),
					borderPane.getY() + borderPane.getHeight() - bccButton.getY());
		}
		
		validate();
	}
	
	/**初始化列表*/
	private void initVec(){
		receiverVec = new Vector();				//联系人和联系人地址列表
		copySendVec = new Vector();
		backDoorVec = new Vector();
		showReceiverVec = new Vector();			//显示选择联系人信息列表
		showCopySendVec = new Vector();
		showBackDoorVec = new Vector();
		receiverIds = new Vector();				//保存通讯组ID
		copySendIds = new Vector();
		backDoorIds = new Vector();
		selectedReceiverVec = new Vector();		//已经选择的联系人列表
		selectedCopySendVec = new Vector();
		selectedBackDoorVec = new Vector();
		receiverIndexVec = new Vector();		//已经选择的联系人列表修改后的索引值
		copySendIndexVec = new Vector();
		backDoorIndexVec = new Vector();
	}

	/**添加成员列表监听器*/
	private void addNewMemberListeners(){
		selectedMemberList.addKeyListener(this);

		addButton.addActionListener(this);
		//        contacts.addActionListener(this);
		//        property.addActionListener(this);
		ok.addActionListener(this);
	}

	/**添加收件人监听器*/
	private void addReceiverListeners(){
		receiverList.addKeyListener(this);
		copySendList.addKeyListener(this);
		backDoorList.addKeyListener(this);

		toButton.addActionListener(this);
		ccButton.addActionListener(this);
		bccButton.addActionListener(this);
		//        contacts.addActionListener(this);
		//        property.addActionListener(this);
		ok.addActionListener(this);
		cancel.addActionListener(this);
	}

	/**取得PIMRecord的ID号*/
	private String[] getNativeRecordId(){
		String[] recordID = CASControl.ctrl.getModel().getAllContactRecordId();
		return recordID;
	}

	/** 从数据库中取出 字段为 "显示为" 的内容 */
	private String[] getDisplayAsName(){
		String[] tempStr = CASControl.ctrl.getModel().getAllContactDisplayAs();
		return tempStr;
	}

	/**
	 * 从数据库中取出 字段为 "EMail 地址"
	 */
	private String[] getEMailAddress(){
		emailAddress = CASControl.ctrl.getModel().getAllEmailAddress();

		if(emailAddress != null){
			int rowCount = emailAddress.length;
			String[] tempStr = new String[rowCount];
			for(int i = 0; i < rowCount; i++){
				tempStr[i] = emailAddress[i][0];
			}
			return tempStr;
		}else{
			return null;
		}
	}

	/** 得到所有的用户
	 * @return 所有的用户
	 */
	public String[] getAllUserName(){
		return this.getDisplayAsName();
	}

	/**取得每条数据前面地图标*/
	private Icon[] getColumnIcon(){
		String[][] emailAddress1 = CASControl.ctrl.getModel().getAllEmailAddress();
		int[] tmpTypes = CASControl.ctrl.getModel().getAllCommGroupAttrList();
		if(emailAddress1 != null){
			Icon[] iconArray = new Icon[emailAddress1.length];
			for(int i = 0; i < emailAddress1.length; i++){
				if(tmpTypes[i] == 1){
					iconArray[i] = image;
				}else{
					iconArray[i] = imageIcon;
				}
			}
			return iconArray;
		}else{
			return null;
		}
	}

	/**本地表格中的数据*/
	private Vector getDataVector(){
		Vector dataVector = new Vector();
		String[] displayName = getDisplayAsName();
		String[] addressEmail = getEMailAddress();
		Icon[] imageIcons = getColumnIcon();
		int[] ids = getIntId();

		int recordsCount = displayName.length;
		Vector[] recordVector = new Vector[displayName.length];
		for(int i = 0; i < recordsCount; i++){
			if(emailAddress != null){
				recordVector[i] = new Vector();
				recordVector[i].add(imageIcons[i]);
				recordVector[i].add(displayName[i]);
				recordVector[i].add(addressEmail[i]);
				recordVector[i].add(PIMPool.pool.getKey(ids[i]));
				dataVector.add(recordVector[i]);
			}
		}
		return dataVector;
	}

	/**将所有的ID转换为 int 型 * 返回保存ID的数组*/
	private int[] getIntId(){
		String[] tempRecordId = getNativeRecordId();
		int[] tempIntRecordId = new int[tempRecordId.length];
		for(int i = 0; i < tempRecordId.length; i++){
			try{
				tempIntRecordId[i] = Integer.parseInt(tempRecordId[i]);
			}catch (NumberFormatException e){
			}
		}
		return tempIntRecordId;
	}

	//
	private int getRecordType(PIMRecord prmRecord){
		//取得相应PIMRecord的类型
		Short tmpShort = (Short) (prmRecord.getFieldValues().get(PIMPool.pool.getKey(ContactDefaultViews.TYPE)));
		if(tmpShort != null){
			return tmpShort.intValue();
		}else{
			return 0;
		}
	}

	/** 添加到列表事件  */
	private void addSeleRecsToRightList(Vector prmShowVec, Vector prmVec){
		String[] tmpDisplayAsString = getSelectedDisplayAsFields(); //得到选中记录的表示为字段内容数组；
		int[] tmpSelectedIds = getSelectedIds(); //得到选中记录的ID字段内容数组；
		String tmpWarningStr = CASUtility.EMPTYSTR;
		ICASModel tmpModel = CASControl.ctrl.getModel();
		int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
		int tmpAppNodeId = CASUtility.getAPPNodeID(tmpAppIndex);
		for(int i = 0; i < tmpSelectedIds.length; i++){
			if(!idsVector.contains(PIMPool.pool.getKey(tmpSelectedIds[i]))) //如果通过ID的对比发现，右侧的列表中不含有该选中项，才做处理，否则跳过。
			{
				PIMRecord tmpRecord = tmpModel.selectRecord(tmpAppIndex, tmpSelectedIds[i], tmpAppNodeId); //从model中找到该记录。
				//对该记录的三个邮件项做判断，如果三个都是无效的，则根据其是否是通讯组，做不同处理。
				if((tmpRecord.getFieldValue(ContactDefaultViews.EMAIL) == null || tmpRecord.getFieldValue(
						ContactDefaultViews.EMAIL).toString().length() == 0)){
					if(getRecordType(tmpRecord) == 1) //如果当前的选择的是用户组，则进一步对其中包含的联系人进行判断。
					{
						if(!isContainsContact(listModel, tmpDisplayAsString[i])){
							listModel.addElement(tmpDisplayAsString[i]);
							setAddContactsGroup(prmShowVec, prmVec, tmpDisplayAsString[i], tmpSelectedIds[i]);
						}
					}else if(isActAsLinkManDlg){
						if(!isContainsContact(listModel, tmpDisplayAsString[i])){
							idsVector.add(PIMPool.pool.getKey(tmpSelectedIds[i]));
							listModel.addElement(tmpDisplayAsString[i]);
							setAddContactsPersonal(prmShowVec, prmVec, tmpDisplayAsString[i], tmpSelectedIds[i]);
						}
					}else{
						tmpWarningStr = tmpWarningStr.concat(tmpDisplayAsString[i]).concat(",");
					}
				}else{
					if(!isContainsContact(listModel, tmpDisplayAsString[i])){
						idsVector.add(PIMPool.pool.getKey(tmpSelectedIds[i]));
						listModel.addElement(tmpDisplayAsString[i]);
						setAddContactsPersonal(prmShowVec, prmVec, tmpDisplayAsString[i], tmpSelectedIds[i]);
					}
				}
			}
		}
		if(tmpWarningStr.length() > 0){
			//选中的记录中有一条或多条联系人记录不含有邮件地址信息，为避免将来发送邮件时产生遗漏，请先为其补充邮件地址信息，再加入通讯组。
			SOptionPane.showErrorDialog(MessageCons.W10648);
		}

		selectedMemberList.setSelectedIndex(listModel.size() - 1);
		selectedMemberList.ensureIndexIsVisible(listModel.size() - 1);
	}

	//判断列表中有无此联系人信息
	private boolean isContainsContact(DefaultListModel prmListModel, String prmContactStr){
		if(prmContactStr == null || prmContactStr.length() < 1){
			return false;
		}else if(prmListModel == null || prmListModel.size() < 1){
			return false;
		}else{
			return prmListModel.contains(prmContactStr);
		}
	}

	/**添加记录到通讯组列表*/
	private void addRecordToCommunicationList(){
		if(idsVector != null){
			int tmpListModelSize = idsVector.size();
			int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
			int tmpAppNodeId = CASUtility.getAPPNodeID(tmpAppIndex);
			for(int i = 0; i < tmpListModelSize; i++){
				Integer tmpIdsInteger = (Integer) idsVector.get(i);
				int tmpId = tmpIdsInteger.intValue();
				PIMRecord pimRecord = CASControl.ctrl.getModel().selectRecord(tmpAppIndex, tmpId, tmpAppNodeId);
				String displayAsString = (String) pimRecord.getFieldValues().get(
						PIMPool.pool.getKey(ContactDefaultViews.SUBJECT));
				String tmpAddressesString = (String) pimRecord.getFieldValues().get(
						PIMPool.pool.getKey(ContactDefaultViews.EMAIL));
				//用于保存通讯组列表记录
				Vector communicationListRecordVector = new Vector();
				if(getRecordType(pimRecord) != 1){
					communicationListRecordVector.add(imageIcon);
				}else{
					communicationListRecordVector.add(image);
				}
				communicationListRecordVector.add(displayAsString); //tmpDisplayAsString[tmpId]);
				communicationListRecordVector.add(tmpAddressesString);
				communicationListDataVector.add(communicationListRecordVector);
			}
		}
	}

	/**更新通讯阻列表
	 * @param prmModel : 需要更新的表格模型
	 */
	void updateCommunicationList(PIMTableModelAryBased prmModel){
		if(isOkClick){
			addRecordToCommunicationList(); //取得数据
			int tempVectorSize = communicationListDataVector.size(); //取得容器的长度
			int modelRowCount = prmModel.getRowCount();
			//过滤communicationListDataVector中的数据
			//将不存在的东西加入表格
			//遍历添加数据的容器
			for(int k = 0; k < tempVectorSize; k++){
				Vector rowVector = (Vector) communicationListDataVector.get(k); //取得指定索引的容器
				String rowVectorDisplayName = (String) (rowVector.get(1));

				boolean[] haveDifferentDate = new boolean[modelRowCount];//用来保存是否存在重复数据的数组
				if(modelRowCount > 0) //如果模型行数大于则遍历容器
				{
					for(int m = 0; m < modelRowCount; m++) //遍历模型
					{
						String tmpDisplayName = (String) (prmModel.getValueAt(m, 1));
						if(!tmpDisplayName.equals(rowVectorDisplayName)) //将容器中的数据盒表格模型中的数据比较
						{
							haveDifferentDate[m] = true;
						}else{
							haveDifferentDate[m] = false;
						}
					}
					boolean have = true;
					for(int n = 0; n < modelRowCount; n++){
						have &= haveDifferentDate[n];
					}
					if(have){
						prmModel.insertRow(prmModel.getRowCount(), rowVector.toArray());
					}
				}else //模型中数据为空,直接添加至模型
				{
					prmModel.insertRow(prmModel.getRowCount(), rowVector.toArray());
				}
			}
		}
	}

	//==========================================================================

	/**取得被选记录的ID*/
	private int[] getSelectedIds(){
		int[] tableSelectedRows = nativeTable.getSelectedRows();
		int[] tmpSelectedIds = new int[tableSelectedRows.length];
		for(int i = 0; i < tableSelectedRows.length; i++){
			Integer idInteger = (Integer) nativeTable.getValueAt(tableSelectedRows[i], 3);
			tmpSelectedIds[i] = idInteger.intValue();
		}
		return tmpSelectedIds;
	}

	/**
	 * 返回显示为地段
	 */
	public String[] getSelectedDisplayAsFields(){
		int[] tableSelectedRows = nativeTable.getSelectedRows();
		String[] namesString = new String[tableSelectedRows.length];
		for(int i = 0; i < tableSelectedRows.length; i++){
			namesString[i] = (String) nativeTable.getValueAt(tableSelectedRows[i], 1);
		}
		return namesString;
	}

	/////////////////////////////////////////////////////////////
	/////////////////   动  作  监  听  器    ///////////////////
	/////////////////////////////////////////////////////////////
	/** 按钮动作
	 * @param e 动作事件
	 */
	public void actionPerformed(ActionEvent e){
		Object source = e.getSource(); //取得事件源
		if(source == cancel){ //如果是cancel按钮被点，则需要释放内存。@TODO: cancel事件也应该捕捉，但目前不知道办法。
			clearObject();
			dispose();
		}else if(source == addButton){ //如果是添加到列表按钮监听器,将从表格中取到的字段为"显示为"的内容添加到列表中
			addSeleRecsToRightList(showReceiverVec, receiverVec);
		}else if(source == ok){ //确定按钮监听器
			isOkClick = true;
			dispose();
		}else if(source == contacts){ // 联系人按钮* 添加新的联系人记录
			ContactDlg contactDialog = new ContactDlg(this, new SaveContentsAction());
			contactDialog.show();
			PIMRecord newPIMRecord = contactDialog.getContents();
			contactDialog.release();
			if(newPIMRecord != null){
				int newId = newPIMRecord.getRecordID();
				if(newId != -1){
					String tmpDisplayAsString = (String) newPIMRecord.getFieldValues().get(
							PIMPool.pool.getKey(ContactDefaultViews.SUBJECT));
					String tmpAddressesString = (String) newPIMRecord.getFieldValues().get(
							PIMPool.pool.getKey(ContactDefaultViews.EMAIL));
					tableModel.insertRow(0, new Object[] { imageIcon, tmpDisplayAsString, tmpAddressesString,
							PIMPool.pool.getKey(newId) });
					nativeTable.setRowSelectionInterval(0, 0);
					selectedRow = 0;
				}
			}
		}else if(source == property){ // 属性按钮 * 根据选择的记录打开属性信息
			if(selectedRow >= 0){
				Object idsObject = nativeTable.getValueAt(selectedRow, 3);
				int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
				int tmpAppNodeId = CASUtility.getAPPNodeID(tmpAppIndex);
				PIMRecord selectedPimRecord = CASControl.ctrl.getModel().selectRecord(tmpAppIndex,
						((Integer) idsObject).intValue(), tmpAppNodeId);

				Object typeObject = selectedPimRecord.getFieldValue(ContactDefaultViews.TYPE);
				if((typeObject == null) || ((Short) typeObject).intValue() == 0)
					new ContactDlg(this, new UpdateContactAction(), selectedPimRecord).show();
				else
					new MainListDialog(SelectedNewMemberDlg.this, new UpdateContactAction(), selectedPimRecord)
							.show();
			}else
				new ContactDlg(this, new UpdateContactAction(), null).show();
		}else if(source == toButton){ // 收件人按钮 * 添加收件人
			if(selectedRow >= 0)
				addSeleRecsToRightList(receiverList, receiverListModel, showReceiverVec, receiverVec);
		}else if(source == ccButton){ //抄送按钮
			if(selectedRow >= 0)
				addSeleRecsToRightList(copySendList, copySendListModel, showCopySendVec, copySendVec);
		}else if(source == bccButton){ //密件抄送按钮
			if(selectedRow >= 0)
				addSeleRecsToRightList(backDoorList, backDoorListModel, showBackDoorVec, backDoorVec);
		}
	}

	/****************************************************************/
	//  ================   鼠 标 监 听 器   ================= //
	//------------------------------------------------------- //
	/** Invoked when the mouse button has been clicked (pressed
	 * and released) on a component.
	 * @param e 鼠标事件源
	 */
	public void mouseClicked(MouseEvent e){
		int mouseClickCount = e.getClickCount();
		if(mouseClickCount >= 2){
			selectedRow = nativeTable.getSelectedRow();
			if(selectedRow != -1){
				if(isCalledFromEmailHeader) //使收件人对话盒
				{
					addSeleRecsToRightList(receiverList, receiverListModel, showReceiverVec, receiverVec);
				}else //选择成员对话盒
				{
					addSeleRecsToRightList(showReceiverVec, receiverVec);
				}
			}
		}
	}

	/** Invoked when a mouse button has been pressed on a component.
	 * @param e 鼠标事件源
	 */
	public void mousePressed(MouseEvent e){
		selectedRow = nativeTable.getSelectedRow();
	}

	/** Invoked when the mouse enters a component.
	 * @param e 鼠标事件源
	 */
	public void mouseEntered(MouseEvent e){}

	/** Invoked when the mouse exits a component.
	 * @param e 鼠标事件源
	 */
	public void mouseExited(MouseEvent e){}

	/** Invoked when a mouse button has been released on a component.
	 * @param e 鼠标事件源
	 */
	public void mouseReleased(MouseEvent e){}

	/*****************************************************************/
	//  =================   键 盘 监 听 器   ================= //
	//-------------------------------------------------------- //
	/** Invoked when a key has been pressed.
	 * See the class description for {@link KeyEvent} for a definition of
	 * a key pressed event.
	 * @param e 键盘事件
	 */
	public void keyPressed(KeyEvent e){
		Object source = e.getSource();
		int keycode = e.getKeyCode();
		if(keycode == KeyEvent.VK_DELETE){
			if(source == receiverList){
				deletAction(receiverList, receiverListModel, showReceiverVec, receiverVec, selectedReceiverVec,
						receiverIndexVec);
			}else if(source == copySendList){
				deletAction(copySendList, copySendListModel, showCopySendVec, copySendVec, selectedCopySendVec,
						copySendIndexVec);
			}else if(source == backDoorList){
				deletAction(backDoorList, backDoorListModel, showBackDoorVec, backDoorVec, selectedBackDoorVec,
						backDoorIndexVec);
			}else if(source == selectedMemberList){
				deletAction(selectedMemberList, listModel, showReceiverVec, receiverVec, selectedReceiverVec,
						receiverIndexVec);
			}
		}
	}

	/** Invoked when a key has been released.
	 * See the class description for {@link KeyEvent} for a definition of
	 * a key released event.
	 * @param e 键盘事件
	 */
	public void keyReleased(KeyEvent e){}

	/** Invoked when a key has been typed.
	 * See the class description for {@link KeyEvent} for a definition of
	 * a key typed event.
	 * @param e 键盘事件
	 */
	public void keyTyped(KeyEvent e){}

	//--------------- 以下 call by : 联系人按钮 --------------------
	/** 返回用逗号分隔的联系人字符串
	 * @return 经处理后的字符串 */

	public String getContactsString(){
		String tmpContactsString = listModelString;
		if(isOkClick){
			int tmpReceivers = listModel.size();
			tmpContactsString = CASUtility.EMPTYSTR;
			for(int i = 0; i < tmpReceivers; i++){
				tmpContactsString += (String) listModel.getElementAt(i) + ',';
			}
			if(tmpReceivers > 0){
				return tmpContactsString.substring(0, tmpContactsString.length() - 1);
			}else{
				return tmpContactsString;
			}
		}else{
			return tmpContactsString;
		}
	}

	//--------------- 此方法 call by : 邮件对话盒 --------------------
	/** 返回用逗号分隔的收件人字符串
	 * @return 经处理后的字符串
	 */
	public String getReceiversString(){
		String tmpReceiveString = toString;
		if(isOkClick){
			tmpReceiveString = CASUtility.EMPTYSTR;
			int tmpReceivers = receiverListModel.size();
			for(int i = 0; i < tmpReceivers; i++){
				tmpReceiveString += (String) receiverListModel.getElementAt(i) + ',';
			}
			if(tmpReceivers > 0){
				return tmpReceiveString.substring(0, tmpReceiveString.length() - 1);
			}else{
				return tmpReceiveString;
			}
		}else{
			return tmpReceiveString;
		}
	}

	/** 返回用逗号分隔的抄送字符串
	 * @return 经处理后的字符串
	 */
	public String getCopySendsString(){
		String tmpCopySendsString = ccString;
		if(isOkClick){
			tmpCopySendsString = CASUtility.EMPTYSTR;
			int tmpCopySends = copySendListModel.size();
			for(int i = 0; i < tmpCopySends; i++){
				tmpCopySendsString += (String) copySendListModel.getElementAt(i) + ',';
			}
			if(tmpCopySends > 0){
				return tmpCopySendsString.substring(0, tmpCopySendsString.length() - 1);
			}else{
				return tmpCopySendsString;
			}
		}else{
			return tmpCopySendsString;
		}
	}

	/** 返回用逗号分隔的密件抄送字符串
	 * @return 经处理后的字符串
	 */
	public String getBackDoorsString(){
		String tmpBackDoorsString = bccString;
		if(isOkClick){
			tmpBackDoorsString = CASUtility.EMPTYSTR;
			int tmpBackDoors = backDoorListModel.size();
			for(int i = 0; i < tmpBackDoors; i++){
				tmpBackDoorsString += (String) backDoorListModel.getElementAt(i) + ',';
			}
			if(tmpBackDoors > 1){
				return tmpBackDoorsString.substring(0, tmpBackDoorsString.length() - 1);
			}else{
				return tmpBackDoorsString;
			}
		}else{
			return tmpBackDoorsString;
		}
	}

	/** 删除动作
	 * 点击Delect时的动作
	 * @param prmSourceList : 列表事件源
	 * @param prmSourceListModel : 列表模型
	 */
	private void deletAction(JList prmSourceList, DefaultListModel prmListModel, Vector prmShowVector,
			Vector prmVector, Vector prmSelectedVec, Vector prmIndexVec){
		int listIndex = prmSourceList.getSelectedIndex();
		int tmpListModelSize = prmListModel.size();
		if(listIndex >= 0){
			Object tmpSelectionObject = prmListModel.getElementAt(listIndex);
			prmListModel.removeElementAt(listIndex);
			if(listIndex >= prmSelectedVec.size())	//删除ModelList列表中的元素
				setDeletedContact(prmShowVector, prmVector, listIndex - prmSelectedVec.size());
			else if(listIndex < prmSelectedVec.size()){
				prmSelectedVec.remove(listIndex);
				prmIndexVec.add(PIMPool.pool.getKey(listIndex));
			}
			if(listIndex != tmpListModelSize - 1){
				if(tmpListModelSize > 1)
					prmSourceList.setSelectedIndex(listIndex);
			}else if(tmpListModelSize > 1)
					prmSourceList.setSelectedIndex(listIndex - 1);

			/**********************************************
			 * NOTE : 如果是选择新成员对话盒则执行以下代码
			 **********************************************/
			if(!isCalledFromEmailHeader){
				if(idsVector != null && listIndex < idsVector.size())//这里应该删除idsVector中的ID
					idsVector.remove(listIndex);
				if(!prmListModel.contains(tmpSelectionObject)){
					int tmpListVectorSize = communicationListDataVector.size();
					String tmpString = (String) tmpSelectionObject;
					Vector tmpVector;
					for(int i = 0; i < tmpListVectorSize; i++){
						tmpVector = (Vector) communicationListDataVector.get(i);
						String tmpDisplayAs = (String) tmpVector.get(1);
						if(tmpDisplayAs.equals(tmpString))
							communicationListDataVector.remove(tmpVector);
					}
				}
			}
		}
	}

	/** 添加动作
	 * @param prmSourceList : 列表事件源
	 * @param prmSourceListModel : 列表模型
	 */
	private void addSeleRecsToRightList(JList prmSourceList, DefaultListModel prmListModel, Vector prmShowVector,
			Vector prmVector){
		String[] tempDisplayAsFields = getSelectedDisplayAsFields();
		int[] tmpIds = getSelectedIds();
		ICASModel tmpModel = CASControl.ctrl.getModel();
		String tmpWarningStr = CASUtility.EMPTYSTR;

		int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
		int tmpAppNodeId = CASUtility.getAPPNodeID(tmpAppIndex);

		for(int i = 0; i < tmpIds.length; i++){
			PIMRecord tmpRecord = tmpModel.selectRecord(tmpAppIndex, tmpIds[i], tmpAppNodeId);
			if(tmpRecord.getFieldValue(ContactDefaultViews.EMAIL) == null){
				//判断当前的选择的是否为用户组
				if(getRecordType(tmpRecord) == 1){
					prmListModel.addElement(tempDisplayAsFields[i]);
					setAddContactsGroup(prmShowVector, prmVector, tempDisplayAsFields[i], tmpIds[i]);
				}else
					tmpWarningStr += tempDisplayAsFields[i] + ','; //如果不为用户组
			}else{
				prmListModel.addElement(tempDisplayAsFields[i]);
				//加载收件人和IDs到Vector中
				setAddContactsPersonal(prmShowVector, prmVector, tempDisplayAsFields[i], tmpIds[i]);
			}
		}
		if(tmpWarningStr.length() > 0)
			//选中的记录中有一条或多条联系人记录不含有邮件地址信息，为避免将来发送邮件时产生遗漏，请先为其补充邮件地址信息，再加入通讯组。
			SOptionPane.showErrorDialog(MessageCons.W10648);
		
		prmSourceList.setSelectedIndex(prmListModel.size() - 1);
		prmSourceList.ensureIndexIsVisible(prmListModel.size() - 1);
	}

	/**在列表中删除时，做删除操作 
	 * @param prmShowContactVec 显示已经选择的联系人列表
	 *        prmVector 联系人信息列表，格式为“Contact，EmailAddr，ID”
	 *        prmIndex 已选择的被删除的联系人记录的索引值
	 */
	private void setDeletedContact(Vector prmShowContactVec, Vector prmVector, int prmIndex){
		if((prmShowContactVec == null || prmShowContactVec.isEmpty())
				|| (prmVector == null || prmVector.isEmpty()))
			return;
		if(prmIndex >= 0){
			prmShowContactVec.removeElementAt(prmIndex);
			prmVector.removeElementAt(prmIndex);
		}
	}

	/**添加工作组的信息
	 * @param prmShowContactVec 显示已经选择的联系人列表
	 *        prmVector 联系人信息列表，格式为“Contact，EmailAddr，ID”
	 *        prmContact 联系人显示为(FieldAs)字符串
	 *        prmId 当前联系人记录ID
	 * @return boolean 如果联系人为空，返回false，否则返回ture
	 */
	private boolean setAddContactsGroup(Vector prmShowContactVec, Vector prmVector, String prmContact, int prmId){
		StringBuffer tmpFormatStr = new StringBuffer();
		if(prmContact == null || prmContact.length() < 1)
			return false;
		else
			prmShowContactVec.add(prmContact);
		//联系人
		tmpFormatStr.append(prmContact);
		tmpFormatStr.append(',');
		//EMail（因为为空字符，所以没有加入）
		tmpFormatStr.append(',');
		//联系人ID
		tmpFormatStr.append(prmId);
		prmVector.add(tmpFormatStr.toString());
		return true;
	}

	/**添加单个联系人信息
	 * @param prmShowContactVec 显示已经选择的联系人列表
	 *        prmVector 联系人信息列表，格式为“Contact，EmailAddr，ID”
	 *        prmContact 联系人显示为(FieldAs)字符串
	 *        prmId 当前联系人记录ID
	 * @boolean 联系人为空或者邮件为空，返回false
	 */
	private boolean setAddContactsPersonal(Vector prmShowContactVec, Vector prmVector, String prmContact, int prmId){
		if(prmContact == null || prmContact.length() < 1)
			return false;//判断"表示为"是否为空，这里不能为空

		ICASModel tmpModel = CASControl.ctrl.getModel();
		int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
		int tmpAppNodeId = CASUtility.getAPPNodeID(tmpAppIndex);
		String tmpAvaEmail = getAvailabilityEmail(tmpModel.selectRecord(tmpAppIndex, prmId, tmpAppNodeId));
		StringBuffer tmpFormatStr = new StringBuffer();
		//邮件为空时不添加联系人信息
		if(tmpAvaEmail == null || tmpAvaEmail.length() < 1){ //如果地址无效且不是作为联系人
			if(!isActAsLinkManDlg)
				return false;
			else
				tmpAvaEmail = CASUtility.EMPTYSTR;
		}
		prmContact = prmContact.indexOf(",") == -1 ? prmContact : prmContact.replace(',', '.');
		prmShowContactVec.add(prmContact);
		//联系人
		tmpFormatStr.append(prmContact);
		tmpFormatStr.append(',');

		//EMailAddr
		tmpFormatStr.append(tmpAvaEmail);
		tmpFormatStr.append(',');

		//联系人ID
		tmpFormatStr.append(prmId);
		prmVector.add(tmpFormatStr.toString());
		return true;
	}

	/** 取默认可用的邮件,如果邮件1不可用,则取邮件2,如果邮件2不可用,则取邮件3,否则返回PIMUtility.EMPTYSTR;
	 *  @param prmRecord 联系人数据表中的一条记录
	 *  @return String 联系人可用的邮件，如果不存在返回空
	 */
	private String getAvailabilityEmail(PIMRecord prmRecord){
		Object tmpAvailabEmail;
		tmpAvailabEmail = prmRecord.getFieldValue(ContactDefaultViews.EMAIL);
		if(tmpAvailabEmail != null){
			return tmpAvailabEmail.toString();
		}else{
			return CASUtility.EMPTYSTR;
		}
	}

	//清除对象
	private void clearObject(){
		showReceiverVec = null;
		showCopySendVec = null;
		showBackDoorVec = null;
		//
		receiverIds = null;
		copySendIds = null;
		backDoorIds = null;
		//
		receiverVec = null;
		copySendVec = null;
		backDoorVec = null;
		//
		selectedReceiverVec = null;
		selectedCopySendVec = null;
		selectedBackDoorVec = null;
		//
		receiverIndexVec = null;
		copySendIndexVec = null;
		backDoorIndexVec = null;
	}

	/** 返回一个Vector 已选择的收件人被删除的索引值列表
	 * @return Vector 已选择的收件人被删除的索引值列表
	 * @called by: EMailHeader.java
	 */
	public Vector getReceiverIndexVec(){
		return (isOkClick == true) ? receiverIndexVec : null;
	}

	/** 返回已选择的抄送人被删除的索引值列表
	 * @return Vector 已选择的抄送人被删除的索引值列表
	 * @called by: EMailHeader.java
	 */
	public Vector getCopySendIndexVec(){
		return (isOkClick == true) ? copySendIndexVec : null;
	}

	/** 返回 一个已选择的暗送人被删除的索引值列表
	 * @return Vector 已选择的暗送人被删除的索引值列表
	 * @called by: EMailHeader.java
	 */
	public Vector getBackDoorIndexVec(){
		return (isOkClick == true) ? backDoorIndexVec : null;
	}

	/** 返回邮件收件人和收件人地址
	 * @return Vector 邮件收件人和收件人地址,格式为 receiver,email
	 * @called by: EmailHeader.java
	 */
	public Vector getReceiverInfsVector(){
		return getInfsVector(receiverVec);
	}

	/** 返回邮件抄送人和抄送人地址
	 * @return Vector 邮件抄送人和抄送人地址,格式为 copysend,email
	 * @called by: EmailHeader.java
	 */
	public Vector getCopySendInfsVector(){
		return getInfsVector(copySendVec);
	}

	/** 返回邮件密送人和密送人地址
	 * @return Vector 邮件密送人和密送人地址,格式为 backdoor,email
	 * @called by: EmailHeader.java
	 */
	public Vector getBackDoorInfsVector(){
		return getInfsVector(backDoorVec);
	}

	//返回列表
	private Vector getInfsVector(Vector prmVector){
		if(isOkClick == false)
			return null;
		if(prmVector == null || prmVector.isEmpty())
			return null;
		else
			return prmVector;
	}

	/***/
	protected void extraAction(){
		if(addButton != null)
			addButton.removeActionListener(this);
		if(contacts != null)
			contacts.removeActionListener(this);
		if(property != null)
			property.removeActionListener(this);
		if(ok != null)
			ok.removeActionListener(this);
		if(toButton != null)
			toButton.removeActionListener(this);
		if(ccButton != null)
			ccButton.removeActionListener(this);
		if(bccButton != null)
			bccButton.removeActionListener(this);
		if(cancel != null)
			cancel.removeActionListener(this);
		if(selectedMemberList != null)
			selectedMemberList.removeKeyListener(this);
		if(receiverList != null)
			receiverList.removeKeyListener(this);
		if(copySendList != null)
			copySendList.removeKeyListener(this);
		if(backDoorList != null)
			backDoorList.removeKeyListener(this);
		if(nativeTable != null){
			nativeTable.removeMouseListener(this);
			nativeTable.release();
			nativeTable = null;
		}
	}

	///////////////////////////////////////////////////
	/////////   ----  全  局  变  量  ------   //////////
	//////////////////////////////////////////////////
	private JButton ok;
	private JButton cancel;
	private PIMScrollPane scroll;//表格滚动窗格
	private JPanel borderPane;
	private JLabel addToList;//列表标签
	private JLabel receiverListLabel;//收件人列表
	private JLabel receiverLabel;//列表标签
	private PIMSeparator separator;//分隔线
	private int selectedRow = -1;
	private Icon imageIcon = CustOpts.custOps.getContactsIcon(false);	//联系人图标
	private Icon image = CustOpts.custOps.getContactGroupIcon();	//通讯组图标
	private JButton addButton;
	private JButton contacts;
	private JButton property;
	private PIMTable nativeTable;
	private PIMTableModelVecBased tableModel;
	private JList selectedMemberList;
	private JScrollPane listSeledMemberPane;
	private DefaultListModel listModel;
	private String listModelString;
	
	private JScrollPane tmpSCRPane;
	private JScrollPane tmpPaneCopy;
	private JScrollPane tmpbackDoorPane;
	
	
	private boolean isOkClick;//父对话盒是否为通讯组的标志
	private String[][] emailAddress;//保存邮电地址的数组
	private Vector communicationListDataVector;//用于保存通讯组列表
	//联系人和联系人地址列表
	private Vector receiverVec;
	private Vector copySendVec;
	private Vector backDoorVec;
	//显示选择联系人信息列表
	private Vector showReceiverVec;
	private Vector showCopySendVec;
	private Vector showBackDoorVec;
	//保存通讯组ID
	private Vector receiverIds;
	private Vector copySendIds;
	private Vector backDoorIds;
	//已经选择的联系人列表
	private Vector selectedReceiverVec;
	private Vector selectedCopySendVec;
	private Vector selectedBackDoorVec;
	//已经选择的联系人列表修改后的索引值
	private Vector receiverIndexVec;
	private Vector copySendIndexVec;
	private Vector backDoorIndexVec;
	
	private Vector idsVector;
	private boolean isCalledFromEmailHeader; 	// 判断是否是从MailFrame上调出来的标志。
	private boolean isActAsLinkManDlg; 			// 判断是否是从选择联系人按钮上弹出来的对话盒的标志。
	//收件人对话盒-----------------------
	private String toString;//
	private String ccString;
	private String bccString;
	private JButton toButton; //收件人按钮
	private JButton ccButton; //抄送按钮
	private JButton bccButton; //密件抄送按钮
	
	private DefaultListModel receiverListModel;//列表模型
	private DefaultListModel copySendListModel;
	private DefaultListModel backDoorListModel;
	//列表
	private JList receiverList;
	private JList copySendList;
	private JList backDoorList;
}
