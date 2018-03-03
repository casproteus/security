package org.cas.client.platform.output.dialog;

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
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascontrol.navigation.CASTree;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.product.ProductDefaultViews;
import org.cas.client.resource.international.DlgConst;
import org.cas.client.resource.international.PaneConsts;

/**联系人对话框
 * <p>微软的TextField的长度限制是228（约）， 本类的限制因为没有规格约束，暂定为912（约）
 */
public class OutputDlg extends JDialog implements ICASDialog, ActionListener, WindowListener, ComponentListener
{
	/** Creates a new instance of ContactDialog
	 * @called by PasteAction 为Copy邮件到联系人应用。
	 * @param prmAction OK时的操作
	 * @param prmRecord 一条记录
	 * @param parent 弹出对话框的Frame
	 */
	public OutputDlg(Frame parent, ActionListener prmAction, PIMRecord prmRecord)
	{
		super(parent, false);
		isPartOfView = (parent == null);
		actionListener = prmAction; //当对话盒采集输入结束后，调该监听的actionperform方法。
		record = prmRecord;
		initDialog();
	}
	/**Creates a new instance of ContactDialog
	 * @param parent 弹出对话框的Frame
	 */
	public OutputDlg(JDialog parent, ActionListener prmAction, PIMRecord prmRecord)
	{
		super(parent, true);
		actionListener = prmAction; //当对话盒采集输入结束后，调该监听的actionperform方法。
		record = prmRecord;
		initDialog();
	}
	/** 为新建通讯组列表，模态对话框。
	 * @param parent 父窗体
	 * @param prmAction OK时的操作
	 */
	public OutputDlg(JDialog parent, ActionListener prmAction)
	{
		super(parent, true);
		actionListener = prmAction; //当对话盒采集输入结束后，调该监听的actionperform方法。
		record = null;
		initDialog();
	}
	
	/*对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局，
	 * 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
	 * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
	 */
	public void reLayout()
	{
		cancel.setBounds(getContainer().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP,
				getContainer().getHeight() - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP,
				CustOpts.BTN_WIDTH,
				CustOpts.BTN_HEIGHT);//关闭
		ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP,
				cancel.getY(),
				CustOpts.BTN_WIDTH,
				CustOpts.BTN_HEIGHT);
		tabPane.setBounds(CustOpts.HOR_GAP,	CustOpts.VER_GAP,
				getContainer().getWidth() - 2 * CustOpts.HOR_GAP,
				(isPartOfView ? getContainer().getHeight() : ok.getY()) - 2 * CustOpts.VER_GAP);
		general.setBounds(0,0,tabPane.getWidth() - CustOpts.HOR_GAP, tabPane.getHeight() - 50);
    	general.reLayout();
    	validate();
	}
	/** Creates a new instance of IPIMDialog
	 * @return 要保存的内容
	 */
	public PIMRecord getContents(){
		general.saveData();
		record.setAppIndex(CustOpts.custOps.APPNameVec.indexOf("Output"));
		return record;
	}
	/**给对话盒赋予内容。
	 * @NOTE：当对话盒作为detailView的预览面板使用时，参数可能为空，为空表示对话盒上内容清空。
	 */
	public boolean setContents(PIMRecord prmRecord){
		record = (prmRecord == null) ? new PIMRecord() : prmRecord;
		hashtable = record.getFieldValues();
		if(hashtable == null){
			hashtable = new Hashtable();
			record.setFieldValues(hashtable);
		}
		general.setContent();//只在已经初始化了的tab页上填内容，其它页上的内容等切换到的时候才在stateChange事件中初始化（初始化时也填内容）
		validate();
		return true;
	}
	public void makeBestUseOfTime(){}
	public void addAttach(File[] file, Vector actualAttachFiles){}
	public PIMTextPane getTextPane(){return null;}
	/** 实现接口中的方法
	 * @called by:emo.pim.pimcontrol.action.pimmenu.file.ContactAciton;
	 */
	public void release(){
		if(newFlag){
			setContents(null);
			return;
		}
		if(record != null){
			int tmpRecID = record.getRecordID();
			if(tmpRecID >= 0 && dialogs != null)	//@NOTE:预览的时候没有不会将对话盒在dialogs中注册,dialogs也未必被实例化.
				dialogs.remove(PIMPool.pool.getKey(tmpRecID));
		}
		ok.removeActionListener(this);
		cancel.removeActionListener(this);
		removeWindowListener(this);
		if (tabPane != null)
			tabPane.removeAll();
		if (general != null){
			general.removeAll();
			general.release();
			general.dlg = null;
			general = null;
		}
		dispose();//对于对话盒，如果不加这句话，就很难释放掉。
		System.gc();//@TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
	}

	/**Invoked when the component's size changes.*/
    public void componentResized(ComponentEvent e){
    	reLayout();
    };

    /**Invoked when the component's position changes.*/    
    public void componentMoved(ComponentEvent e){};

    /**Invoked when the component has been made visible.*/
    public void componentShown(ComponentEvent e){};

    /**Invoked when the component has been made invisible.*/
    public void componentHidden(ComponentEvent e){};

	/** Invoked when an action occurs.
	 * NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
	 * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的
	 * 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
	 * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
	 * @param e 动作事件
	 */
	public void actionPerformed(ActionEvent e){
		if(e.getSource() == ok){
			String tmpTextInField = general.fldAmount.getText();
	    	if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
		    	try{
		    		Integer.valueOf(tmpTextInField);
		    	}catch (NumberFormatException exp){
		    		general.fldAmount.grabFocus();
		    		general.fldAmount.selectAll();
		    		JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
		    		return;
		    	}
	    	tmpTextInField = general.fldArrearage.getText();
	    	if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
		    	try{
		    		Double.valueOf(tmpTextInField);
		    	}catch (NumberFormatException exp){
		    		general.fldArrearage.grabFocus();
		    		general.fldArrearage.selectAll();
		    		JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
		    		return;
		    	}
	    	tmpTextInField = general.fldTotlePrice.getText();
	    	if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
		    	try{
		    		Double.valueOf(tmpTextInField);
		    	}catch (NumberFormatException exp){
		    		general.fldTotlePrice.grabFocus();
		    		general.fldTotlePrice.selectAll();
		    		JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
		    		return;
		    	}

			if (newFlag){
				CASTree tree = CASControl.ctrl.getFolderTree();
				if (tree.getNodePath(treePath.getParentPath(), treePath.getLastPathComponent().toString()) != null) // 结点没被删除
					record.setInfolderID(((CASNode) treePath.getLastPathComponent()).getPathID());
				else {// show个对话盒,让用户选择一个文件夹
					int tmpIndex = CustOpts.custOps.APPNameVec.indexOf("Output");
					int tmpAppNodeID = CASUtility.getAPPNodeID(tmpIndex);//得到对于本应用的根本路径。
					if (CASUtility.setPathFromDialog(this, record, tmpIndex))
						return;// 选择文件夹点的取消按钮,直接退到联系人对话盒
					record.setInfolderID(tmpAppNodeID);
				}
				SwingUtilities.invokeLater(new Runnable(){
					public void run(){
						general.fldCode.requestFocus();
					}
				});
			}else	
				setVisible(false);
			actionListener.actionPerformed(new ActionEvent(this, 0, null));
		}else if(e.getSource() == cancel){
			if(general.isValueChanged()		//具体做法保存之前为检查每个域中的值是否和record中的一致。
				&& JOptionPane.showConfirmDialog(this, DlgConst.SAVEBEFORECLOSE,
						PaneConsts.TITLE, JOptionPane.YES_NO_OPTION) == 0){
				String tmpTextInField = general.fldAmount.getText();
		    	if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
			    	try{
			    		Integer.valueOf(tmpTextInField);
			    	}catch (NumberFormatException exp){
			    		general.fldAmount.grabFocus();
			    		general.fldAmount.selectAll();
			    		JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
			    		return;
			    	}
		    	tmpTextInField = general.fldArrearage.getText();
		    	if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
			    	try{
			    		Double.valueOf(tmpTextInField);
			    	}catch (NumberFormatException exp){
			    		general.fldArrearage.grabFocus();
			    		general.fldArrearage.selectAll();
			    		JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
			    		return;
			    	}
		    	tmpTextInField = general.fldTotlePrice.getText();
		    	if (!tmpTextInField.equals(CASUtility.EMPTYSTR))
			    	try{
			    		Double.valueOf(tmpTextInField);
			    	}catch (NumberFormatException exp){
			    		general.fldTotlePrice.grabFocus();
			    		general.fldTotlePrice.selectAll();
			    		JOptionPane.showMessageDialog(this, DlgConst.FORMATERROR);
			    		return;
			    	}
				if (newFlag){
					CASTree tree = CASControl.ctrl.getFolderTree();
					if (tree.getNodePath(treePath.getParentPath(), treePath.getLastPathComponent().toString()) != null) // 结点没被删除
						record.setInfolderID(((CASNode) treePath.getLastPathComponent()).getPathID());
					else {				// show个对话盒,让用户选择一个文件夹
						int tmpIndex = CustOpts.custOps.APPNameVec.indexOf("Output");
						int tmpAppNodeID = CASUtility.getAPPNodeID(tmpIndex);//得到对于本应用的根本路径。
						if (CASUtility.setPathFromDialog(this, record, tmpIndex))
							return;		// 选择文件夹点的取消按钮,直接退到联系人对话盒
						record.setInfolderID(tmpAppNodeID);
					}
				}
				setVisible(false);
				newFlag = false;
				actionListener.actionPerformed(new ActionEvent(this, 0, null));
			}
			dispose();
		}
	}

	/** Invoked when the Window is set to be the active Window. Only a Frame or
	 * a Dialog can be the active Window. The native windowing system may
	 * denote the active Window or its children with special decorations, such
	 * as a highlighted title bar. The active Window is always either the
	 * focused Window, or the first Frame or Dialog that is an owner of the
	 * focused Window.
	 */
	public void windowActivated(WindowEvent e){}
	/** Invoked when a window has been closed as the result of calling dispose on the window.
	 * @NOTE:当通过左上角Windows菜单，或右上角的叉号关闭时不触发本事件，而触发windowClosing事件。
	 */
	public void windowClosed(WindowEvent e){}
	/** Invoked when the user attempts to close the window from the window's system menu.  If the program does not
	 * explicitly hide or dispose the window while processing this event, the window close operation will be cancelled.
	 * @Note:当通过OK,Cancel按钮关闭时不触发本事件，而触发windowClosed事件。
	 */
	public void windowClosing(WindowEvent e){
		release();
	}
	/** Invoked when a Window is no longer the active Window. Only a Frame or a
	 * Dialog can be the active Window. The native windowing system may denote
	 * the active Window or its children with special decorations, such as a
	 * highlighted title bar. The active Window is always either the focused
	 * Window, or the first Frame or Dialog that is an owner of the focused
	 * Window.
	 */
	public void windowDeactivated(WindowEvent e){}
	/** Invoked when a window is changed from a minimized to a normal state.*/
	public void windowDeiconified(WindowEvent e){}
	/** Invoked when a window is changed from a normal to a
	 * minimized state. For many platforms, a minimized window
	 * is displayed as the icon specified in the window's
	 * iconImage property.
	 * @see java.awt.Frame#setIconImage
	 */
	public void windowIconified(WindowEvent e){}
	/** Invoked the first time a window is made visible. */
	public void windowOpened(WindowEvent e){}
	public Container getContainer(){
		return getContentPane();
	}

    //put & get Value in Hashtable.
	/** 往哈希表中放数据
	 * @param key 键值
	 * @param value 数据
	 */
	public void putValue(Object key, Object value){
		hashtable.put(key, value);
	}
	/** 取数据方法
	 * @param key 键值
	 * @return 数据
	 */
	public Object getValue(Object key){
		return hashtable.get(key);
	}

	private void initDialog(){
		newFlag = record == null || record.getFieldValue(ProductDefaultViews.ID) == null;
		setTitle(OutputDlgConst.TITLE);
		if (newFlag)
			treePath = CASControl.ctrl.getFolderTree().getSelectedPath();//这说明不可以在任意节点下实现任意项目的新建了
		
        setBounds((CustOpts.SCRWIDTH - 560)/2, (CustOpts.SCRHEIGHT - 474)/2, 560, 474);	//对话框的默认尺寸。
		getContentPane().setLayout(null);
		setResizable(true);
		
        //init the contents on the dialog－－－－－－
		if (record == null){ //如果是新建联系人，则实例化hashtable和Record备用。
			record = new PIMRecord();
			hashtable = new Hashtable();
			record.setFieldValues(hashtable);
		}else //如果是编辑联系人，则用传入的hashtable初始化本类的hashtable。
			hashtable = record.getFieldValues();
		
		//初始化－－－－－－－－－－－－－－－－
		tabPane = new JTabbedPane();
		general = new OutputGeneralPanel(this);
		ok = new JButton(newFlag ? DlgConst.UPDATE_BUTTON : DlgConst.OK);
		cancel = new JButton(newFlag ? DlgConst.FINISH_BUTTON : DlgConst.CANCEL);

		//属性设置－－－－－－－－－－－－－－
		tabPane.setFont(CustOpts.custOps.getFontOfDefault());
		ok.setFont(CustOpts.custOps.getFontOfDefault());
		cancel.setFont(ok.getFont());
		ok.setMargin(new Insets(0,0,0,0));
		cancel.setMargin(ok.getMargin());
		getRootPane().setDefaultButton(ok);
		
		//搭建－－－－－－－－－－－－－
		tabPane.addTab(OutputDlgConst.GENERAL, general);
		getContentPane().add(tabPane);
		getContentPane().add(cancel);
		getContentPane().add(ok);

		//初始化Tab页面的第一页
		initGeneralPanel();
		
		//加监听器－－－－－－－－
		ok.addActionListener(this);
		cancel.addActionListener(this);
		addWindowListener(this);
		getContentPane().addComponentListener(this);
		
		//注册－－－－－－－－
		int tmpRecID = record.getRecordID();
		if(tmpRecID >= 0){
			if(dialogs == null)
				dialogs = new Hashtable();
			dialogs.put(PIMPool.pool.getKey(tmpRecID), this);
		}
	}
	
	/**初始化“常规”页上的组件。*/
	private void initGeneralPanel(){
		general.init();
	}

	boolean isPartOfView;	//用于标记本对话盒是独立显示还是作为View的一部分.
	boolean hasClose;		// 标志对话框是否已关闭
	private ActionListener actionListener; //当对话盒采集输入结束后，调该监听的actionperform方法。
	private JButton ok;
	private JButton cancel;
	/** tab选项卡 */
	private JTabbedPane tabPane;
	private OutputGeneralPanel general;
	private Hashtable hashtable;
	private PIMRecord record;
	private TreePath treePath;
	
	boolean newFlag; //新建联系人对话盒的标记
	public static Hashtable dialogs;// 用于存放对话框引用的Hashtable 
}
