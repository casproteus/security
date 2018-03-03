package org.cas.client.platform.cascontrol.menuaction;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.commonmenu.PIMActionName;

public class CopyAction extends SAction {

    /**
     * Creates a new instance of CopyAction
     * 
     * @param 粘贴类型
     */
    public CopyAction(int pasteType) {
        super();
        this.pasteType = pasteType;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        // 获得系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 初始化加入到剪贴板中的复制内容
        Vector selectRecords = CASControl.ctrl.getSelectRecords();
        if (selectRecords != null && selectRecords.size() > 0) {
            PIMRecordSelect records = null;
            if (pasteType == CASControl.CUT) {
                // Vector tmpSelected = new Vector();
                // for (int size = selectRecords.size(), i = 0; i < size; i++)
                // {
                // tmpSelected.add(((PIMRecord)selectRecords.get(i)).clone());
                // }
                records = new PIMRecordSelect(selectRecords);
            } else {
                records = new PIMRecordSelect(selectRecords);
            }
            // 将复制的内容加入到剪贴板
            clipboard.setContents(records, records);
            // 通知PasteAction当前的Paste是移动(Cut)还是复制(Copy)
            ((PasteAction) (PIMActionName.ACTIONS[PIMActionName.ID_EDIT_PASTE])).setPasteType(pasteType);
            // if (pasteType == PasteAction.CUT)
            // {
            // PIMControl.ctrl.getModel().deleteRecords(selectRecords);
            // }
        }
    }

    private int pasteType;
}
