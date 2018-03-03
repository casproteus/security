package org.cas.client.platform.cascontrol.thread;

import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.RemindDialog;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Player;
import org.cas.client.platform.pimmodel.PIMRecord;

class TaskOfNoticeTask extends APIMTimingTask {

    /** Creates a new instance of TaskTimerTask */
    TaskOfNoticeTask(int prmID, int prmType, int prmFolderID) {
        id = prmID;
        type = prmType;
        infolder = prmFolderID;
    }

    /**
     * 任务到期执行的动作。
     */
    void execute() {
        PIMRecord record = CASControl.ctrl.getModel().selectRecord(type, id, infolder);
        if (record != null) {
            // TODO:为了节约内存，addRemindContent()方法参数改为Vector,省去PIMRecord的new操作
            Vector tmpVec = new Vector();
            tmpVec.addElement(PIMPool.pool.getKey(type));
            tmpVec.addElement(PIMPool.pool.getKey(id));
            tmpVec.addElement(PIMPool.pool.getKey(infolder));
            tmpVec.addElement(record.getFieldValue(ModelDBCons.CAPTION));
            tmpVec.addElement(record.getFieldValue(ModelDBCons.RECIEVEDATE));
            RemindDialog.getInstance().addRemindContent(tmpVec);
            RemindDialog.getInstance().setVisible(true);
            String soundPath = CASUtility.EMPTYSTR;
            if (record.getAppIndex() == ModelCons.TASK_APP) {
                soundPath = (String) record.getFieldValue(ModelDBCons.SOUND);
            } else if (record.getAppIndex() == ModelCons.CALENDAR_APP) {
                soundPath = (String) record.getFieldValue(ModelDBCons.CALENDAR_REMIND_SOUND);
            } else if (record.getAppIndex() == ModelCons.CONTACT_APP) {
                soundPath = "/org/cas/client/resource/img/presentation/sound/ClangingMetal.wav";
            }
            if (soundPath.length() > 0) {
                new Player(soundPath).play();
            }
        }
    }

    void setStatus(
            boolean status) {
    }

    private int id, type;
    private int infolder;
}
