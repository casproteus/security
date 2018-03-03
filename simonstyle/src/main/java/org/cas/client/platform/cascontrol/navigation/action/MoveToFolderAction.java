package org.cas.client.platform.cascontrol.navigation.action;

import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.menuaction.SAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;



/**@TODO: 此 Action 需 要 整 理 --------
 */

public class MoveToFolderAction extends SAction
{
    /** Creates a new instance of MoveToAction
     * 构建器: 移动记录到文件夹
     */
    public MoveToFolderAction()
    {
        super(IStatCons.RECORD_SELECTED);
    }

    /**Invoked when an action occurs.
     * 移动记录动作
     */
    public void actionPerformed(ActionEvent e)
    {
        Vector tmpSeleRecVec = CASControl.ctrl.getSelectRecords(); //取得选择的记录
        if (tmpSeleRecVec != null && tmpSeleRecVec.size() > 0)//检查有效性以便决定是否显示路径选择对话盒。
        {
            MoveFolderDialog movedialog = new MoveFolderDialog(CASControl.ctrl.getMainFrame());
            movedialog.show();
            int tmpSelPathID = movedialog.getSelectedPathID();   //取得要移动的目标位置
            moveRecords(tmpSeleRecVec,tmpSelPathID);
        }
    }
    
	/**
	 * @called by: EFolderTree.java 和本类actionperformed（）;
	 * 本方法中再次对传入的recVec，selectPath等进行了有效性判断，和actionperformed中的判断重复，是因为有可能从Tree组件的drop方法
	 * 中直接进入本方法。而actionperform中的判断是因为要知道有效性以便决定是否显示对话盒。
	 * 从当前应用的视图（目前只实现了表格视图，TODO：卡片视图是否也应该支持？）拖放记录到导航树的节点中。
	 */
	public static void moveRecords(Vector prmRecVec, int prmSelectedPathID)
	{
		if (prmRecVec == null || prmSelectedPathID <= 0)	//传入参数无效
			return;

		int tmpSize = prmRecVec.size(); //移动记录的数目如果小于1也返回。
		if (tmpSize < 1)
			return;
		
		int tmpSourceApp = CustOpts.custOps.getActiveAppType(); //当前应用必然是“源”了。
		int tmpTargetApp = CASUtility.getAppIndexByFolderID(prmSelectedPathID);
				
		//----------------------------------------------
		//==============  内部拖动  ===============
		//将Record从数据空中永久删除，并根据原Record构造一批路径属性不同的Record插回到数据库中，不需要弹对话盒。
		//----------------------------------------------
		if (tmpTargetApp == tmpSourceApp || tmpTargetApp == -1)
		{
			//@NOTE:假设规格不会变：源记录都是在在同一张表中。
			PIMRecord tmpRec = (PIMRecord) prmRecVec.get(0);
			if (tmpRec.getInfolderID() == prmSelectedPathID)
				//@TODO:报错：目标路径与源路径相同，无法移动。
				return;  //当前的元素不用做移动操作,因为在同一个INFOLDER中
			else
				for (int i = 0; i < tmpSize; i ++){
					tmpRec = (PIMRecord)prmRecVec.get(i);
					tmpRec.setInfolderID(prmSelectedPathID); //设置待移动的目标路径
				}
		
			CASControl.ctrl.getModel().updateRecords(prmRecVec);	//添加记录

			//@TODO：加入undo事件
//			undoRedoAction.addEdit(getMoveUndoEdit(prmTargetVector));
			return;
		}
		//------------------------------------------------//
		//========== 从已删除项之外的应用拖动 =============//
		//------------------------------------------------//
//		if (PIMUtility.isMail(tmpSourceApp))	//从邮件向各个应用拖放。
//		{
//			if (PIMUtility.isMail(tmpTargetApp))	//邮件－－邮件
//			{
//				PIMControl.ctrl.getModel().permanentlyDeleteRecords(prmRecVec, false);
//				for (int size = prmRecVec.size(), i = 0; i < size; i ++)
//				{
//					PIMRecord tmpRecord = (PIMRecord) prmRecVec.get(i);
//					tmpRecord.setAppIndex(tmpTargetApp);
//					tmpRecord.setInfolder(prmSelectedPath.toString());
//				}
//				
//				PIMControl.ctrl.getModel().insertRecords(prmRecVec);//添加记录
//				//@TODO:加入undo事件
////				undoRedoAction.addEdit(getInsertUndoEdit(newRecords));
//			}
//			else if (tmpTargetApp == ModelConstants.CALENDAR_APP)
//			{
//				dragFromeMailToCalendar(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.TASK_APP)
//			{
//				dragFromeMailToTask(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.CONTACT_APP)
//			{
//				dragFromeMailToContact(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到
//				//该文件夹中记录的转换。请新建一条记录，
//				//然后通过复制后粘贴将您想要的内容导入新记录。
//				ErrorDialog.showErrorDialog(MessageCons.W10634);
//			}
//			else if (tmpTargetApp == ModelConstants.DELETED_ITEM_APP) //目标类型是已删除项
//			{
//				//执行删除操作
//				PIMControl.ctrl.getModel().deleteRecords(prmRecVec);
//				//添加undo/redo事件
//				int size = prmRecVec.size();
//				int[] tmpIds = new int[size];
//				for (int i = 0; i < size; i++)
//				{
//					tmpIds[i] = ((PIMRecord)(prmRecVec.get(i))).getRecordID();
//				}
//				PIMRecord tmpRecord = (PIMRecord)(prmRecVec.get(0));
//				int tmpFolderID = tmpRecord.getInfolderID();
//				int tmpAppType = tmpRecord.getAppIndex();
//				UndoRedoAction.getUndoAction().addEdit(
//				ModelUndoRedoMethod.getDefaultModelUndoMethod().getDeleteUndoEdit(tmpIds, tmpFolderID, tmpAppType));
//			}
//		}
//		else if (tmpSourceApp == ModelConstants.CALENDAR_APP)
//		{
//			if (tmpTargetApp == ModelConstants.TASK_APP)
//			{
//				dragFromeCalendarToTask(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.CONTACT_APP)
//			{
//				dragFromeCalendarToContact(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (PIMUtility.isMail(tmpTargetApp))
//			{
//				dragFromeCalendarToMail(prmRecVec, prmSelectedPath.toString(), tmpTargetApp);
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到该
//				//文件夹中记录的转换。请新建一条记录，然后
//				//通过复制后粘贴将您想要的内容导入新记录。
//				ErrorDialog.showErrorDialog(MessageCons.W10634);
//			}
//			else if (tmpTargetApp == ModelConstants.DELETED_ITEM_APP) //目标类型是已删除项
//			{
//				//执行删除操作
//				PIMControl.ctrl.getModel().deleteRecords(prmRecVec);
//				//添加undo/redo事件
//				int size = prmRecVec.size();
//				int[] tmpIds = new int[size];
//				for (int i = 0; i < size; i++)
//				{
//					tmpIds[i] = ((PIMRecord)(prmRecVec.get(i))).getRecordID();
//				}
//				PIMRecord tmpRecord = (PIMRecord)(prmRecVec.get(0));
//				String tmpFolder = tmpRecord.getInfolder();
//				int tmpAppType = tmpRecord.getAppIndex();
//				UndoRedoAction.getInstance().addEdit(
//				ModelUndoRedoMethod.getDefaultModelUndoMethod().getDeleteUndoEdit(tmpIds, tmpFolder, tmpAppType));
//			}
//		}
//		else if (tmpSourceApp == ModelConstants.TASK_APP)
//		{
//			if (tmpTargetApp == ModelConstants.CALENDAR_APP)
//			{
//				dragFromeTaskToCalendar(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.CONTACT_APP)
//			{
//				dragFromeTaskToContact(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (PIMUtility.isMail(tmpTargetApp))
//			{
//				dragFromeTaskToMail(prmRecVec, prmSelectedPath.toString(), tmpTargetApp);
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到该文
//				//件夹中记录的转换。请新建一条记录，然后通过
//				//复制后粘贴将您想要的内容导入新记录。
//				ErrorDialog.showErrorDialog(MessageCons.W10634);
//			}
//			else if (tmpTargetApp == ModelConstants.DELETED_ITEM_APP) //目标类型是已删除项
//			{
//				//执行删除操作
//				PIMControl.ctrl.getModel().deleteRecords(prmRecVec);
//				//添加undo/redo事件
//				int size = prmRecVec.size();
//				int[] tmpIds = new int[size];
//				for (int i = 0; i < size; i++)
//				{
//					tmpIds[i] = ((PIMRecord)(prmRecVec.get(i))).getRecordID();
//				}
//				PIMRecord tmpRecord = (PIMRecord)(prmRecVec.get(0));
//				String tmpFolder = tmpRecord.getInfolder();
//				int tmpAppType = tmpRecord.getAppIndex();
//				UndoRedoAction.getInstance().addEdit(
//				ModelUndoRedoMethod.getDefaultModelUndoMethod().getDeleteUndoEdit(tmpIds, tmpFolder, tmpAppType));
//				//UndoRedoAction.getInstance().addEdit(ModelUndoRedoMethod.getDefaultModelUndoMethod().getDeleteUndoEdit(prmTargetVector));
//			}
//		}
//		else if (tmpSourceApp == ModelConstants.CONTACT_APP)
//		{
//			if (tmpTargetApp == ModelConstants.CALENDAR_APP)
//			{
//				dragFromeContactToCalendar(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.TASK_APP)
//			{
//				dragFromeContactToTask(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (PIMUtility.isMail(tmpTargetApp))
//			{
//				dragFromeContactToMail(prmRecVec, prmSelectedPath.toString(), tmpTargetApp);
//			}
//			else if (tmpTargetApp == ModelConstants.DELETED_ITEM_APP) //目标类型是已删除项
//			{
//				//执行删除操作
//				PIMControl.ctrl.getModel().deleteRecords(prmRecVec);
//				//添加undo/redo事件
//				int size = prmRecVec.size();
//				int[] tmpIds = new int[size];
//				for (int i = 0; i < size; i++)
//				{
//					tmpIds[i] = ((PIMRecord)(prmRecVec.get(i))).getRecordID();
//				}
//				PIMRecord tmpRecord = (PIMRecord)(prmRecVec.get(0));
//				String tmpFolder = tmpRecord.getInfolder();
//				int tmpAppType = tmpRecord.getAppIndex();
//				UndoRedoAction.getInstance().addEdit(
//				ModelUndoRedoMethod.getDefaultModelUndoMethod().getDeleteUndoEdit(tmpIds, tmpFolder, tmpAppType));
//				//UndoRedoAction.getInstance().addEdit(ModelUndoRedoMethod.getDefaultModelUndoMethod().getDeleteUndoEdit(prmTargetVector));
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到该文件
//				//夹中记录的转换。请新建一条记录，然后通过复制
//				//后粘贴将您想要的内容导入新记录。
//				ErrorDialog.showErrorDialog(MessageCons.W10634);
//			}
//		}
//		else if (tmpSourceApp == ModelConstants.DIARY_APP)
//		{
//			//对不起，系统目前不支持从您选中的记录到该文件夹中
//			//记录的转换。请新建一条记录，然后通过复制后粘贴将
//			//您想要的内容导入新记录。
//			ErrorDialog.showErrorDialog(MessageCons.W10634);
//		}
	}
//	//-------------------    TODO :   ---------------------
//	//==== 拖动方法需要和移动Action进行统一,对拖动进行封装管理 ======
//
//	//=============================================================
//	//         --------     拖 动 方 法     --------
//	//=============================================================
//	/**
//	 * 从邮件拖动到日历
//	 * @NOTE：参数应该已经经过有效性判断。
//	 */
//	private static void dragFromeMailToCalendar(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords.size() > 1)
//		{
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getRecordString(prmRecords));
//			tmpRecord.setAppIndex(ModelConstants.CALENDAR_APP);
//			tmpRecord.setFieldValues(hashtable);
//			MainPane.getApp("Appointment").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord, false,false);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object caption = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord newRecord = new PIMRecord();
//			newRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.CAPTION), caption);
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getRecordString(prmRecords));
//			newRecord.setAppIndex(ModelConstants.CALENDAR_APP);
//			newRecord.setFieldValues(hashtable);
//			MainPane.getApp("Appointment").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), newRecord, false,false);
//		}
//	}
//
//	/**
//	 * 从邮件拖动到任务
//	 */
//	private static void dragFromeMailToTask(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getRecordString(prmRecords));
//			tmpRecord.setAppIndex(ModelConstants.TASK_APP);
//			tmpRecord.setFieldValues(hashtable);
//			MainPane.getApp("Task").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord, false,false);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object caption = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord newRecord = new PIMRecord();
//			newRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getRecordString(prmRecords));
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.CAPTION), caption);
//			newRecord.setAppIndex(ModelConstants.TASK_APP);
//			newRecord.setFieldValues(hashtable);
//			MainPane.getApp("Task").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), newRecord, false,false);
//		}
//	}
//
//	/**
//	 * 从邮件拖动到联系人
//	 */
//	private static void dragFromeMailToContact(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getRecordString(prmRecords));
//			tmpRecord.setAppIndex(ModelConstants.CONTACT_APP);
//			tmpRecord.setFieldValues(hashtable);
//			new ContactDialog.showContactDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord,false);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object address = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.ADDRESSER);
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			if (address != null)
//			{
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.EMAIL), PIMUtility.commaStrToPlainAddr(address.toString()));
//			}
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getRecordString(prmRecords));
//			tmpRecord.setAppIndex(ModelConstants.CONTACT_APP);
//			tmpRecord.setFieldValues(hashtable);
//			ContactDialog.showContactDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord,false);
//		}
//	}
//
//	/**
//	 * 从日历拖动到任务
//	 */
//	private static void dragFromeCalendarToTask(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			//TODO:需要包装COMMENT
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object caption = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			//TODO:需要包装COMMENT
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.CAPTION), caption);
//			tmpRecord.setAppIndex(ModelConstants.TASK_APP);
//			tmpRecord.setFieldValues(hashtable);
//			MainPane.getApp("Task").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord, false,false);
//		}
//	}
//
//	/**
//	 * 从日历拖动到联系人
//	 */
//	private static void dragFromeCalendarToContact(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			//TODO:需要包装COMMENT
//		}
//		else if (prmRecords.size() == 1)
//		{
//			//PIMRecord oldRecord = (PIMRecord)prmRecords.get(0);
//			Object address = null;
//			Vector accountVector = PIMControl.ctrl.getModel().getMailAccount(ModelConstants.ACCOUNT_VALID);
//			if (accountVector != null && accountVector.size() > 0)
//			{
//				for (int i = 0; i < accountVector.size(); i++)
//				{
//					PIMRecord accountRec = (PIMRecord) accountVector.get(i);
//					Integer isDef = (Integer) accountRec.getFieldValue(ModelDBConstants.IS_DEFAULT_MAIL);
//					if (isDef.intValue() == 1)
//					{
//						address = accountRec.getFieldValue(ModelDBConstants.SMTPEMAILADDRESS);
//						break;
//					}
//				}
//			}
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			if (address != null)
//			{
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.EMAIL), PIMUtility.commaStrToPlainAddr(address.toString()));
//			}
//			tmpRecord.setAppIndex(ModelConstants.CONTACT_APP);
//			tmpRecord.setFieldValues(hashtable);
//			//TODO:需要包装COMMENT
//			ContactDialog.showContactDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord,false);
//		}
//	}
//
//	/**
//	 * 从日历拖动到邮件
//	 */
//	private static void dragFromeCalendarToMail(Vector prmRecords, String prmInfolder, int prmTargetApp)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			//TODO:需要包装COMMENT
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object caption = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.CAPTION), caption);
//			tmpRecord.setAppIndex(prmTargetApp);
//			tmpRecord.setFieldValues(hashtable);
//			//TODO:需要包装COMMENT
//			MailFrame.showMailFrame(tmpRecord,pimMailContext, MailUtility.CONVERT, null);
//		}
//	}
//
//	/**
//	 * 从任务拖动到邮件
//	 */
//	private static void dragFromeTaskToMail(Vector prmRecords, String prmInfolder, int prmTargetApp)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getTaskString(prmRecords));
//			tmpRecord.setAppIndex(prmTargetApp);
//			tmpRecord.setFieldValues(hashtable);
//			//TODO:需要包装COMMENT
//			MailFrame.showMailFrame(tmpRecord,pimMailContext, MailUtility.CONVERT, null);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object caption = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getTaskString(prmRecords));
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.CAPTION), caption);
//			tmpRecord.setAppIndex(prmTargetApp);
//			tmpRecord.setFieldValues(hashtable);
//			//TODO:需要包装COMMENT
//			MailFrame.showMailFrame(tmpRecord,pimMailContext, "convert", null);
//		}
//	}
//
//	/**
//	 * 从联系人拖动到邮件
//	 */
//	private static void dragFromeContactToMail(Vector prmRecords, String prmInfolder, int prmTargetApp)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 0)
//		{
//			String address = PIMUtility.EMPTYSTR;
//			for (int i = 0; i < prmRecords.size(); i++)
//			{
//				PIMRecord tmpRecord = (PIMRecord) prmRecords.get(i);
////				Object addressObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL);
////				if (addressObj != null)
////				{
////					address += addressObj.toString() + ';';
////				}
//				Object tmpEmailObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL);
//                if (tmpEmailObj == null)
//                {
//                    tmpEmailObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL_2);
//                    if (tmpEmailObj == null)
//                    {
//                        tmpEmailObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL_3);
//                    }
//                }
//                if(tmpEmailObj == null)    //emialAddress 仍然为空的情况.
//                {
//                    Object tmpValue = tmpRecord.getFieldValue(ModelDBConstants.TYPE);
//                    if (tmpValue instanceof Short && ((Short)tmpValue).intValue() == 1) //虽然email仍然为null,但如果该条项目是通讯组列表,也视为有效.
//                    {
//                        int tmpIDs[] = PIMUtility.stringToArray((String)tmpRecord.getFieldValue(ModelDBConstants.MEMBER_LIST));
//                        for (int j = 0; j < tmpIDs.length; j++)
//                        {
//                            tmpRecord = PIMControl.ctrl.getModel().selectRecord(ModelConstants.CONTACT_APP, tmpIDs[j],tmpRecord.getInfolder());
//                            tmpEmailObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL);
//                            if (tmpEmailObj == null)
//                            {
//                                tmpEmailObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL_2);
//                                if (tmpEmailObj == null)
//                                {
//                                    tmpEmailObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL_3);
//                                }
//                            }
//                            if (tmpEmailObj != null)
//                            {
//                            	String caption = (String)tmpRecord.getFieldValue(ModelDBConstants.CAPTION);
//                            	caption = caption.indexOf(",") == -1 ? caption : caption.replace(',','.');
//                            	address = address.concat(PIMUtility.DOUBlEQUOTATION)
//                                .concat(caption).concat(PIMUtility.DOUBlEQUOTATION)
//                                .concat(PIMUtility.LEFTSHARPBRACKET).concat((String)tmpEmailObj).concat(PIMUtility.RIGHTSHARPBRACKET).concat(PIMUtility.COMMA);
//                            }
//                        }
//                    }
//                    else    //说明:选中联系人不含有电子邮件地址信息,跳过,并设置标记,使在秀邮件窗体前报错误信息框.
//                    {
//                    	ErrorDialog.showErrorDialog(MessageCons.W10540);
//                    }
//                }
//                else
//                {
//                	String caption = (String)tmpRecord.getFieldValue(ModelDBConstants.CAPTION);
//                	caption = caption.indexOf(",") == -1 ? caption : caption.replace(',','.');
//                	address = address.concat(PIMUtility.DOUBlEQUOTATION)
//                    .concat(caption).concat(PIMUtility.DOUBlEQUOTATION)
//                    .concat(PIMUtility.LEFTSHARPBRACKET).concat((String)tmpEmailObj).concat(PIMUtility.RIGHTSHARPBRACKET).concat(PIMUtility.COMMA);
//                }
//			}
//			PIMRecord newRecord = new PIMRecord();
//			newRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			if (address.length() > 0)
//			{
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.RECIPIENT), address.substring(0, address.length() - 1));
//			}
//			newRecord.setAppIndex(prmTargetApp);
//			newRecord.setFieldValues(hashtable);
//			//TODO:需要包装COMMENT
//			MailFrame.showMailFrame(newRecord,pimMailContext, "convert", null);
//		}
////		else if (prmRecords.size() == 1)
////		{
////			Object caption = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
////			PIMRecord tmpRecord = new PIMRecord();
////			tmpRecord.setInfolder(prmInfolder);
////			Hashtable hashtable = new Hashtable();
////			hashtable.put(pool.getIntegerKey(ModelDBConstants.CAPTION), caption);
////			tmpRecord.setAppIndex(prmTargetApp);
////			tmpRecord.setFieldValues(hashtable);
////			//TODO:需要包装COMMENT
////			MailFrame.showMailFrame(tmpRecord,pimMailContext, "convert", null);
////		}
//	}
//
//	/**
//	 * 从联系人拖动到任务
//	 */
//	private static void dragFromeContactToTask(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			String address = PIMUtility.EMPTYSTR;
//			String displayAs = PIMUtility.EMPTYSTR;
//			for (int i = 0; i < prmRecords.size(); i++)
//			{
//				PIMRecord tmpRecord = (PIMRecord) prmRecords.get(i);
//				Object addressObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL);
//				Object display_as = tmpRecord.getFieldValue(ModelDBConstants.CAPTION);
//				if (display_as != null)
//				{
//					address += addressObj.toString() + ';';
//					String display_asStr = display_as.toString().indexOf(",") == -1 ? display_as.toString() : display_as.toString().replace(',', '.');
//					displayAs += '\"' + display_asStr + "\"<>" + ',';
//				}
//			}
//			PIMRecord newRecord = new PIMRecord();
//			newRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			if (displayAs.length() > 0)
//			{
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.CONTACT), displayAs.substring(0, displayAs.length() - 1));
////				hashtable.put(pool.getIntegerKey(ModelDBConstants.ADDRESSEE), address.substring(0, address.length() - 1));
//			}
//			newRecord.setAppIndex(ModelConstants.TASK_APP);
//			newRecord.setFieldValues(hashtable);
//			MainPane.getApp("Task").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), newRecord, false,false);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object address = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.EMAIL);
//			Object display_as = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			if (address != null)
//			{
////				hashtable.put(pool.getIntegerKey(ModelDBConstants.ADDRESSEE), address);
//			}
//			if (display_as != null)
//			{
//				String display_asStr = display_as.toString().indexOf(",") == -1 ? display_as.toString() : display_as.toString().replace(',', '.');
//				display_asStr = '\"' + display_asStr + "\"<>";
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.CONTACT), display_asStr);
//			}
//			tmpRecord.setAppIndex(ModelConstants.TASK_APP);
//			tmpRecord.setFieldValues(hashtable);
//			MainPane.getApp("Task").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord, false,false);
//		}
//	}
//
//	/**
//	 * 从联系人拖动到约会
//	 */
//	private static void dragFromeContactToCalendar(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			String address = PIMUtility.EMPTYSTR;
//			String displayAs = PIMUtility.EMPTYSTR;
//			for (int i = 0; i < prmRecords.size(); i++)
//			{
//				PIMRecord tmpRecord = (PIMRecord) prmRecords.get(i);
//				Object addressObj = tmpRecord.getFieldValue(ModelDBConstants.EMAIL);
//				Object display_as = tmpRecord.getFieldValue(ModelDBConstants.CAPTION);
//				if (display_as != null)
//				{
//					address += addressObj.toString() + ';';
//					displayAs += display_as.toString() + ';';
//				}
//			}
//
//			PIMRecord newRecord = new PIMRecord();
//			newRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			if (displayAs.length() > 0)
//			{
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.CONTACT), displayAs.substring(0, displayAs.length() - 1));
////				hashtable.put(pool.getIntegerKey(ModelDBConstants.ADDRESSEE), address.substring(0, address.length() - 1));
//			}
////			hashtable.put(pool.getIntegerKey(ModelDBConstants.CALENDAR_CALL_ACTOR), Boolean.TRUE);
//			newRecord.setAppIndex(ModelConstants.CALENDAR_APP);
//			newRecord.setFieldValues(hashtable);
//			MainPane.getApp("Appointment").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), newRecord, false,false);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			PIMRecord tmpRecord = (PIMRecord) prmRecords.get(0);
//			Object address = tmpRecord.getFieldValue(ModelDBConstants.EMAIL);
//			Object display_as = tmpRecord.getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord newRecord = new PIMRecord();
//			newRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			if (address != null)
//			{
////				hashtable.put(pool.getIntegerKey(ModelDBConstants.ADDRESSEE), address);
//			}
//			if (display_as != null)
//			{
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.CONTACT), display_as.toString());
//			}
////			hashtable.put(pool.getIntegerKey(ModelDBConstants.CALENDAR_CALL_ACTOR), Boolean.TRUE);
//			newRecord.setAppIndex(ModelConstants.CALENDAR_APP);
//			newRecord.setFieldValues(hashtable);
//			MainPane.getApp("Appointment").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), newRecord, false,false);
//		}
//	}
//
//	/**
//	 * 从任务拖动到联系人
//	 */
//	private static void dragFromeTaskToContact(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getTaskString(prmRecords));
//			tmpRecord.setAppIndex(ModelConstants.CONTACT_APP);
//			tmpRecord.setFieldValues(hashtable);
//			ContactDialog.showContactDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord,false);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object address = null;
//			Vector accountVector = PIMControl.ctrl.getModel().getMailAccount(ModelConstants.ACCOUNT_VALID);
//			if (accountVector != null && accountVector.size() > 0)
//			{
//				for (int i = 0; i < accountVector.size(); i++)
//				{
//					PIMRecord accountRec = (PIMRecord) accountVector.get(i);
//					Boolean isDef = (Boolean) accountRec.getFieldValue(ModelDBConstants.IS_DEFAULT_MAIL);
//					if (isDef.booleanValue())
//					{
//						address = accountRec.getFieldValue(ModelDBConstants.SMTPEMAILADDRESS);
//						break;
//					}
//				}
//			}
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getTaskString(prmRecords));
//			if (address != null)
//			{
//				hashtable.put(pool.getIntegerKey(ModelDBConstants.ADDRESSEE), address);
//			}
//			tmpRecord.setAppIndex(ModelConstants.CONTACT_APP);
//			tmpRecord.setFieldValues(hashtable);
//			ContactDialog.showContactDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord,false);
//		}
//	}
//
//	/**
//	 * 从任务拖动到约会
//	 */
//	private static void dragFromeTaskToCalendar(Vector prmRecords, String prmInfolder)
//	{
//		if (prmRecords == null)
//		{
//			return;
//		}
//		if (prmRecords.size() > 1)
//		{
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getTaskString(prmRecords));
//			tmpRecord.setAppIndex(ModelConstants.CALENDAR_APP);
//			tmpRecord.setFieldValues(hashtable);
//			MainPane.getApp("Appointment").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord, false,false);
//		}
//		else if (prmRecords.size() == 1)
//		{
//			Object caption = ((PIMRecord) prmRecords.get(0)).getFieldValue(ModelDBConstants.CAPTION);
//			PIMRecord tmpRecord = new PIMRecord();
//			tmpRecord.setInfolder(prmInfolder);
//			Hashtable hashtable = new Hashtable();
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.CAPTION), caption);
//			hashtable.put(pool.getIntegerKey(ModelDBConstants.COMMENT), PIMUtility.getTaskString(prmRecords));
//			tmpRecord.setAppIndex(ModelConstants.CALENDAR_APP);
//			tmpRecord.setFieldValues(hashtable);
//			MainPane.getApp("Appointment").showDialog(PIMControl.ctrl.getMainFrame(), new SaveContentsAction(), tmpRecord, false,false);
//		}
//	}
//
//    private static PIMMailContext pimMailContext = PIMMailContext.getInstance();
}
////----------------------------------------------//
////==============  从已删除项拖动  ===============//
////----------------------------------------------//
//if (tmpSourceApp == ModelConstants.DELETED_ITEM_APP)
//{
//	boolean containsError = true;
//	for (int size = prmRecVec.size(), i = 0; i < size; i ++)
//	{
//		PIMRecord tmpRecord = (PIMRecord) prmRecVec.get(i);
//		String infolder = tmpRecord.getInfolder();
//		int tmpType = PIMUtility.getAppType(infolder); //得到记录原来的类型
//	
//		if (infolder.equalsIgnoreCase(prmSelectedPath)) //类型与目标应用一致
//		{
//			PIMControl.ctrl.getModel().restoreDeletedRecord(tmpRecord, true);
//		} //类型与目标应用都属邮件
//	
//		else if (PIMUtility.isMail(tmpType))
//		{
//			if (PIMUtility.isMail(tmpTargetApp))
//			{
//				PIMControl.ctrl.getModel().permanentlyDeleteRecord(tmpRecord, true, false);
//				tmpRecord.setAppIndex(tmpTargetApp);
//				tmpRecord.setInfolder(prmSelectedPath);
//				//添加记录
//				PIMControl.ctrl.getModel().insertRecord(tmpRecord,false);
//			}
//			else if (tmpTargetApp == ModelConstants.CALENDAR_APP)
//			{
//				dragFromeMailToCalendar(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.TASK_APP)
//			{
//				dragFromeMailToTask(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.CONTACT_APP)
//			{
//				dragFromeMailToContact(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到该
//				//文件夹中记录的转换。请新建一条记录，然后
//				//通过复制后粘贴将您想要的内容导入新记录。
//				if (containsError)
//				{
//					ErrorDialog.showErrorDialog(MessageCons.W10634);
//					containsError = false;
//				}
//				
//			}
//		}
//		else if (tmpType == ModelConstants.CALENDAR_APP)
//		{
//		
//			if (tmpTargetApp == ModelConstants.CALENDAR_APP)
//			{
//				PIMControl.ctrl.getModel().permanentlyDeleteRecord(tmpRecord, true, false);
//				tmpRecord.setAppIndex(tmpTargetApp);
//				tmpRecord.setInfolder(prmSelectedPath);
//				//添加记录
//				PIMControl.ctrl.getModel().insertRecord(tmpRecord, false);
//			}	
//			else if (tmpTargetApp == ModelConstants.TASK_APP)
//			{
//				dragFromeCalendarToTask(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.CONTACT_APP)
//			{
//				dragFromeCalendarToContact(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (PIMUtility.isMail(tmpTargetApp))
//			{
//				dragFromeCalendarToMail(prmRecVec, prmSelectedPath.toString(), tmpTargetApp);
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到该文件
//				//夹中记录的转换。请新建一条记录，然后通过复制
//				//后粘贴将您想要的内容导入新记录。
//				if (containsError)
//				{
//					ErrorDialog.showErrorDialog(MessageCons.W10634);
//					containsError = false;
//				}
//			}
//		}
//		else if (tmpType == ModelConstants.TASK_APP)
//		{
//			if (tmpTargetApp == ModelConstants.TASK_APP)
//			{
//				PIMControl.ctrl.getModel().permanentlyDeleteRecord(tmpRecord, true, false);
//				tmpRecord.setAppIndex(tmpTargetApp);
//				tmpRecord.setInfolder(prmSelectedPath);
//				//添加记录
//				PIMControl.ctrl.getModel().insertRecord(tmpRecord, false);
//			}
//			else if (tmpTargetApp == ModelConstants.CALENDAR_APP)
//			{
//				dragFromeTaskToCalendar(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.CONTACT_APP)
//			{
//				dragFromeTaskToContact(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (PIMUtility.isMail(tmpTargetApp))
//			{
//				dragFromeTaskToMail(prmRecVec, prmSelectedPath.toString(), tmpTargetApp);
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到该文件
//				//夹中记录的转换。请新建一条记录，然后通过复制
//				//后粘贴将您想要的内容导入新记录。
//				if (containsError)
//				{
//					ErrorDialog.showErrorDialog(MessageCons.W10634);
//					containsError = false;
//				}
//			}
//		}
//		else if (tmpType == ModelConstants.CONTACT_APP)
//		{
//			if (tmpTargetApp == ModelConstants.CONTACT_APP)
//			{
//				PIMControl.ctrl.getModel().permanentlyDeleteRecord(tmpRecord, true, false);
//				tmpRecord.setAppIndex(tmpTargetApp);
//				tmpRecord.setInfolder(prmSelectedPath);
//				//添加记录
//				PIMControl.ctrl.getModel().insertRecord(tmpRecord, false);
//			}
//			else if (tmpTargetApp == ModelConstants.CALENDAR_APP)
//			{
//				dragFromeContactToCalendar(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (tmpTargetApp == ModelConstants.TASK_APP)
//			{
//				dragFromeContactToTask(prmRecVec, prmSelectedPath.toString());
//			}
//			else if (PIMUtility.isMail(tmpTargetApp))
//			{
//				dragFromeContactToMail(prmRecVec, prmSelectedPath.toString(), tmpTargetApp);
//			}
//			else if (tmpTargetApp == ModelConstants.DIARY_APP)
//			{
//				//对不起，系统目前不支持从您选中的记录到该
//				//文件夹中记录的转换。请新建一条记录，然后
//				//通过复制后粘贴将您想要的内容导入新记录。
//				if (containsError)
//				{
//					ErrorDialog.showErrorDialog(MessageCons.W10634);
//					containsError = false;
//				}
//			}
//		}
//	}
//	return;
//}
