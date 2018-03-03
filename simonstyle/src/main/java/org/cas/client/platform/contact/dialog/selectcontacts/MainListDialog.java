package org.cas.client.platform.contact.dialog.selectcontacts;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.resource.international.ListDialogConstant;
import org.cas.client.resource.international.PaneConsts;



public class MainListDialog extends JDialog implements ActionListener, ChangeListener, ICASDialog
{
	/**
	 * Creates a new instance of MyTable
	 * 构建器
	 * @param parent      : 父窗格
	 * @param prmAction  : 事件
	 * @param prmRecord  : 记录
	 */
	public MainListDialog(Frame parent, ActionListener prmAction, PIMRecord prmRecord)
	{
		super(parent, true);
		pimRecord = prmRecord;
		newFlag = true;
		treePath = CASControl.ctrl.getFolderTree().getSelectedPath(); //刚新建列表对话盒时的路径
		
		actionListener = prmAction;
		setTitle(ListDialogConstant.LIST_TITLE);
		
		initDialog();
	}

	/**
	 *
	 */
	public MainListDialog(JDialog prmDialog, ActionListener prmAction, PIMRecord prmRecord)
	{
		//super(prDialog, false);
		super(prmDialog, true);
		pimRecord = prmRecord;
		actionListener = prmAction;
		setTitle(ListDialogConstant.LIST_TITLE);

		initDialog();
	}
	
	public void reLayout(){};

	/**
	 * 初始化对话盒
	 */
	private void initDialog()
	{
		if (this.pimRecord == null)
		{
			pimRecord = new PIMRecord();
			hashtable = new Hashtable();
		}
		else
		{
			hashtable = pimRecord.getFieldValues();
		}

		//取得对话框的尺寸
		dialogSize = getDialogSize();

		//选项卡
		pane = new JTabbedPane();

		//添加成员面板
		memberPanel =
			new MemberPanel(
				dialogSize.width,
				dialogSize.height - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP - CustOpts.HOR_GAP,
				this,
				hashtable);

		//添加附注面板
		annotationPanel =
			createAnnotationPanel(dialogSize.width, dialogSize.height - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP - CustOpts.HOR_GAP);

		pane.add(memberPanel, ListDialogConstant.LIST_MEMBER);
		pane.add(annotationPanel, ListDialogConstant.LIST_ANNOTATION);
		pane.setBounds(0, 0, dialogSize.width, dialogSize.height - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP);
		getContentPane().add(pane);

		//添加确定
		ok = new JButton(ListDialogConstant.LIST_OK);
		ok.setBounds(dialogSize.width - 2 * CustOpts.BTN_WIDTH - CustOpts.HOR_GAP,
				dialogSize.height - CustOpts.BTN_HEIGHT, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		getContentPane().add(ok);
		ok.addActionListener(this);
		getRootPane().setDefaultButton(ok);
		//取消按钮
		cancel = new JButton(ListDialogConstant.LIST_CANCEL);
		cancel.setBounds(dialogSize.width - CustOpts.BTN_WIDTH, dialogSize.height - CustOpts.BTN_HEIGHT,
				CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
		getContentPane().add(cancel);
		cancel.addActionListener(this);
		pane.addChangeListener(this);

		//设置默认焦点
		memberPanel.getField().grabFocus();
		setBounds((CustOpts.SCRWIDTH - dialogSize.width)/2, (CustOpts.SCRHEIGHT - dialogSize.height)/2, dialogSize.width, dialogSize.height);	//对话框的默认尺寸。
	}

	/**
	 * 返回对话框的尺寸
	 */
	private Dimension getDialogSize()
	{
		return new Dimension(500, 430);
	}

	/**
	 * 创建附注面板
	 * @param w: 面板宽度
	 * @param h: 面板高度
	 */
	private JPanel createAnnotationPanel(int w, int h)
	{
		JPanel annotatPanel = new JPanel();
		annotatPanel.setSize(w, h);
		annotatPanel.setBorder(null);

		//文本域
		noteText = new JTextArea();
		
		Object object = hashtable.get(PIMPool.pool.getKey(ContactDefaultViews.CONTENT));
		if (object != null)
		{
			noteText.setText((String) object);
		}

		//文本域添加至滚动窗格
		PIMScrollPane scroll = new PIMScrollPane(noteText, PIMScrollPane.VERTICAL_SCROLLBAR_ALWAYS, PIMScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scroll.setBounds(5, 5, w - 10, h - 2 * CustOpts.VER_GAP - CustOpts.BTN_HEIGHT - 5);
		scroll.getViewport().setBackground(Color.white);

		annotatPanel.add(scroll);

		return annotatPanel;
	}

	/** Invoked when an action occurs.
	 * 按钮动作事件
	 * @param e 动作事件
	 */
	public void actionPerformed(ActionEvent e)
	{
		/**
		 * 点击确定按钮
		 */
		if (e.getSource() == ok)
		{
			Hashtable tempHashtable = getHashtable();
			Object ob = tempHashtable.get(PIMPool.pool.getKey(ContactDefaultViews.SUBJECT));
			if (ob != null && memberPanel.getField().getText().length() != 0)
			{
				if (newFlag)
				{
					CASTree tree = CASControl.ctrl.getFolderTree();
					
					int tmpIndex = CustOpts.custOps.APPNameVec.indexOf("Contact");
					
					StringBuffer tmpBuffer = new StringBuffer(PaneConsts.ROOTCAPTION);
					tmpBuffer.append(PaneConsts.HEAD_PAGE);
					tmpBuffer.append(", ");
					tmpBuffer.append((String)CustOpts.custOps.APPNameVec.get(tmpIndex));
					tmpBuffer.append(']');
					String tmpBasicFoderStr = tmpBuffer.toString();//得到对于本应用的根本路径。
					CASNode tmpNode = (CASNode)treePath.getLastPathComponent();
					if (treePath.toString().indexOf(tmpBasicFoderStr) == 0) //有联系人结点存在
					{
						if (tree.getNodePath(treePath.getParentPath(), tmpNode.toString()) != null) //结点没被删除
						{
							pimRecord.setInfolderID(tmpNode.getPathID());
						}
						else //show个对话盒,让用户选择一个文件夹
							{
							if(CASUtility.setPathFromDialog(this, pimRecord, ModelCons.TELE_LIST_APP))
							{
								return;
							}
						}
					}
					else //没有联系人结点存在，也弹对话盒让用户选择
					{
						if(CASUtility.setPathFromDialog(this, pimRecord, ModelCons.TELE_LIST_APP))
						{
							return;
						}
					}
				}
				actionListener.actionPerformed(new ActionEvent(this, 0, null));
				release();
			}
			else
			{
				SOptionPane.showErrorDialog(MessageCons.W10510);
				int paneIndex = pane.getSelectedIndex();
				if (paneIndex != 0)
				{
					pane.setSelectedIndex(0);
				}
				//setDefaultFocus(memberPanel.getField());
				memberPanel.getField().grabFocus();
			}
		}
	}

	/**
	 * 返回哈希表
	 */
	private Hashtable getHashtable()
	{
		memberPanel.getRecord();
		hashtable.put(PIMPool.pool.getKey(ContactDefaultViews.TYPE), new Short((short) 1));
		hashtable.put(PIMPool.pool.getKey(ContactDefaultViews.CONTENT), noteText.getText());
		return hashtable;
	}

	//////////////////////////////////////////////////
	/////////   实现IPIMDialog接口中的方法   //////////
	//////////////////////////////////////////////////
	/** 接口中的方法
	 */
	public void release()
	{
		memberPanel = null;
		annotationPanel = null;
		dispose();
	}

	/** Creates a new instance of IPIMDialog
	 * @return 得到要保存的记录
	 */
	public PIMRecord getContents()
	{
		pimRecord.setAppIndex(ModelCons.CONTACT_APP);
		pimRecord.setFieldValues(getHashtable());
		return pimRecord;
	}
	public boolean setContents(PIMRecord prmRecord)
	{
		pimRecord = prmRecord;
		initDialog();
		return true;
	}
	public void makeBestUseOfTime(){}
	public void addAttach(File[] file, Vector actualAttachFiles){}
	public PIMTextPane getTextPane(){return null;}

	/** Invoked when this dialog closing.
	 * This method should be overrided if user has extra execution
	 * while closing the dialog.
	 */
	protected void extraAction()
	{
		release();
	}

	/** Invoked when the target of the listener has changed its state.
	 *
	 * @param e  a ChangeEvent object
	 */
	public void stateChanged(ChangeEvent e)
	{
		int tmpPaneIndex = pane.getSelectedIndex();
		if (tmpPaneIndex == 0)
		{
			memberPanel.getField().grabFocus();
		}
		else if (tmpPaneIndex == 1)
		{
			noteText.grabFocus();
		}
	}
	public Container getContainer()
	{
		return getContentPane();
	}
	/**
	 * called by: SaveContentsAction;
	 * @Note: 本方法将来应该放到接口中去,用于表示作为父(Owner)的对话盒需要子对话盒返回产生的Record.
	 */
	public void addTableContents(int[] prmRecordIds)
	{
		memberPanel.addTableContents(prmRecordIds);
	}

	private JButton ok, cancel;
	private JTabbedPane pane;
	private Dimension dialogSize;
	private PIMRecord pimRecord;
	private Hashtable hashtable;
	private MemberPanel memberPanel;
	private JPanel annotationPanel;
	private JTextArea noteText;
	private ActionListener actionListener;
	private static boolean newFlag;
	private static TreePath treePath;
	public static Hashtable dialogs;// 用于存放对话框引用的Hashtable 
}
