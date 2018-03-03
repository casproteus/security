package org.cas.client.platform.cascontrol.menuaction;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.contact.dialog.ContactDlg;
import org.cas.client.platform.pimmodel.PIMRecord;

public class PasteAction extends SAction {
    /** Creates a new instance of PasteAction */
    public PasteAction() {
        super(IStatCons.HAVE_PASTE);
        // super(PIMActionFlag.STATUS_MAIL | PIMActionFlag.STATUS_HAVE_PASTE);
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        if (getPasteType() == CASControl.NULL) {
            return;
        }
        // 获得系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 取得剪贴板上的内容
        Transferable contents = clipboard.getContents(null);
        Vector records;
        try {
            // 使用PIMRecordSelect.PIMRecordFlavor取得数据
            // TODO：此处应该改为先判断，然后取数据。
            records = (Vector) contents.getTransferData(PIMRecordSelect.pimRecordFlavor);
        } catch (UnsupportedFlavorException evt) {
            // 不支持的Copy/Paste格式不进行Paste
            return;
        } catch (IOException ioe) {
            return;
        }

        if ((records != null) && (records.size() > 0))// 判断存在Paste的内容
        {
            final int activeAppType = CustOpts.custOps.getActiveAppType(); // 当前只支持邮件的Copy/Paste
            PIMRecord tmpRecord = (PIMRecord) records.elementAt(0);
            final int recordType = tmpRecord.getAppIndex();
            if (CASUtility.isMail(recordType)) // 被粘贴的内容是邮件
            {
                if (CASUtility.isMail(activeAppType)) // 粘贴到邮件应用
                {
                    if (getPasteType() == CASControl.CUT) // 移动
                    {
                        // 验证存在需要移动的记录
                        if ((records != null) && (records.size() > 0)) {
                            // 取得起始应用
                            final int tmpOldFolderID =
                                    ((PIMRecord) (((PIMRecord) (records.elementAt(0))))).getInfolderID();
                            final int tmpNewFolderID = CustOpts.custOps.getActivePathID();
                            // 若起始应用未变，即原地移动，则直接返回
                            if (tmpOldFolderID == tmpNewFolderID) {
                                setPasteType(CASControl.NULL);
                                return;
                            }
                            // @TODO:暂时屏蔽，等待整理。移动
                            // MoveUndoRedoEdit.changeApp(model, records, tmpNewApp);
                            // 加入undo事件
                            // UndoRedoAction.getUndoAction().addEdit(ModelUndoRedoMethod.getDefaultModelUndoMethod().getMoveUndoEdit(records,
                            // oldApp, tmpNewApp));
                            final int size = records.size();
                            for (int i = 0; i < size; i++) {
                                PIMRecord record = (PIMRecord) ((PIMRecord) records.elementAt(i)).clone();
                                record.setAppIndex(CustOpts.custOps.getActiveAppType());
                                record.setInfolderID(CustOpts.custOps.getActivePathID());
                                CASControl.ctrl.getModel().insertRecord(record, true);
                            }
                            CASControl.ctrl.getModel().deleteRecords(records);
                        }
                        // 移动后设置状态为无内容Paste，防止用户再次移动或复制
                        setPasteType(CASControl.NULL);
                    } else // 复制
                    {
                        // 更改记录的应用类型
                        final int size = records.size();
                        for (int i = 0; i < size; i++) {
                            ((PIMRecord) records.elementAt(i)).setAppIndex(CustOpts.custOps.getActiveAppType());
                            ((PIMRecord) records.elementAt(i)).setInfolderID(CustOpts.custOps.getActivePathID());
                        }
                        // 添加记录
                        CASControl.ctrl.getModel().insertRecords(records);

                        // 加入undo事件
                        // UndoRedoAction.getUndoAction().addEdit(ModelUndoRedoMethod.getDefaultModelUndoMethod().getPasteUndoEdit(records));
                        // setPasteType(NULL);
                    }
                } else if (activeAppType == ModelCons.CONTACT_APP) // 粘贴到联系人
                {
                    // 从邮件中取得内容
                    PIMRecord newRecord = CASUtility.getContact(tmpRecord);
                    // 显示联系人窗口
                    if (newRecord != null) {// TODO:Infolder怎么处理???
                        new ContactDlg(CASControl.ctrl.getMainFrame(), new SaveContentsAction(), newRecord).show();
                    }
                }
            }
        }
    }

    /**
     * 获得Paste的种类，包括Cut，Copy和无内容Paste。
     * 
     * @return Paste的种类
     */
    public int getPasteType() {
        return CASControl.pasteType;
    }

    /**
     * 设置Paste的种类，包括Cut，Copy和无内容Paste。
     * 
     * @param pasteType
     *            Paste的种类
     */
    public void setPasteType(
            int prmPasteType) {
        CASControl.pasteType = prmPasteType;
    }
}
