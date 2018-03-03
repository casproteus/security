/* ThreadActionsFacade.java
 *
 * Created on 2004年4月19日, 下午4:46
 */

package org.cas.client.platform.cascontrol.thread;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JFrame;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.AppointmentConstant;

/**
 */
public class ThreadActionsFacade {
    private static ThreadActionsFacade instance;

    /** @called by: emo.pim.PIMControl.java; */
    public static ThreadActionsFacade getInstance() {
        if (instance == null)
            instance = new ThreadActionsFacade();
        return instance;
    }

    /** Creates a new instance of ThreadActionsFacade */
    private ThreadActionsFacade() {
        // startMailTask(); //定时发收邮件
        // startRemindTask(PIMTimer.getInstance(),ModelConstants.TASK_APP); //任务提醒
        // startRemindTask(PIMTimer.getInstance(),ModelConstants.CALENDAR_APP); //约会提醒
        // startRemindTask(PIMTimer.getInstance(),ModelConstants.CONTACT_APP); //联系人后续标记
        // startSpyMemoTask(); //启动内存监视器，
    }

    /**
     * 在线程中加入定时发收邮件的任务.
     * 
     * @will called by 新建帐号的Action.
     */
    public void startMailTask() {
        Vector tmpMailAccVec = CASControl.ctrl.getModel().getMailAccount(ModelCons.ACCOUNT_VALID);
        if (tmpMailAccVec != null && tmpMailAccVec.size() > 0) {
            int tmpWaitTime = CustOpts.custOps.getSpantime();
            boolean isStart = CustOpts.custOps.getCheckMail();
            if (isStart && tmpWaitTime > 0) {
                // 添加定时邮件收发任务
                taskOfReceiveMail = taskOfReceiveMail == null ? new TaskOfReceiveMail(true) : taskOfReceiveMail;
                // taskOfSendMail = new TaskOfSendMail(false);

                // 启动半分钟后开始,邮件提醒
                // PIMTimer.getInstance().schedule(taskOfSendMail, 1000 * 30, tmpWaitTime * 1000 * 60);
                CASTimer.getInstance().schedule(taskOfReceiveMail, tmpWaitTime * 1000 * 60, tmpWaitTime * 1000 * 60);
            }
        }
    }

    /*
     * 确定提醒任务
     * @param prmTimer 定时器
     * @param prmType 记录类型
     */
    private void startRemindTask(
            CASTimer prmTimer,
            int prmType) {
        ICASModel tmpModel = CASControl.ctrl.getModel();

        Vector tmpVector = tmpModel.getAllRecord(tmpModel.getViewInfo(CASUtility.getAPPNodeID(prmType)), null);
        if (tmpVector == null || tmpVector.size() == 0)
            return;

        for (int i = tmpVector.size() - 1; i >= 0; i--) {
            PIMRecord tmpRecord = (PIMRecord) tmpVector.get(i);
            Hashtable tmpHash = tmpRecord.getFieldValues();
            Boolean tmpNeedRemind = null;
            Calendar tmpCalendar = null;

            if (prmType == ModelCons.TASK_APP) { // 任务提醒
                tmpNeedRemind = (Boolean) tmpHash.get(PIMPool.pool.getKey(ModelDBCons.NEED_AWOKE));
                Date tmpDate = (Date) tmpHash.get(PIMPool.pool.getKey(ModelDBCons.AWOKE_DATE));
                if (tmpDate == null)
                    return;
                tmpCalendar = Calendar.getInstance();
                tmpCalendar.setTime(tmpDate);
            } else if (prmType == ModelCons.CALENDAR_APP) { // 约会提醒
                tmpNeedRemind = (Boolean) tmpHash.get(PIMPool.pool.getKey(ModelDBCons.CALENDAR_NEED_REMIND));
                String tmpAdvanceStr = (String) tmpHash.get(PIMPool.pool.getKey(ModelDBCons.CALENDAR_REMIND_TIME));
                long tmpAdvance = getAdvanceTime(tmpAdvanceStr);
                tmpCalendar = Calendar.getInstance();
                long tmpCurrent = tmpCalendar.getTimeInMillis();
                tmpCalendar.setTimeInMillis(tmpCurrent - tmpAdvance);
            } else if (prmType == ModelCons.CONTACT_APP) { // 联系人后续标记提醒
                Hashtable contactHash = tmpRecord.getFieldValues();
                Object tmpObj = contactHash.get(PIMPool.pool.getKey(ModelDBCons.FOLLOWUPCOMPLETE));
                if (tmpObj == null)
                    return;
                if (((Boolean) tmpObj).booleanValue()) {
                    tmpObj = contactHash.get(PIMPool.pool.getKey(ModelDBCons.FOLOWUPENDTIME));
                    if (tmpObj == null)
                        return;
                    Date date = (Date) tmpObj;
                    tmpCalendar = Calendar.getInstance();
                    tmpCalendar.setTime(date);
                }
            }

            if (tmpNeedRemind != null && tmpNeedRemind.booleanValue()) {
                APIMTimingTask taskTimerTask =
                        new TaskOfNoticeTask(tmpRecord.getRecordID(), prmType, tmpRecord.getInfolderID());
                prmTimer.schedule(taskTimerTask, tmpCalendar);
            }
        }
    }

    private void startSpyMemoTask() {
        // 暂时定为1分钟进行一次gc的动作，运行一段时间，若发现造成使用的粘滞，需要修改间隔并在MemoryMonitorTask()中判断内存的使用率
        taskOfSpyMemory = new TaskOfSpyMemory();
        CASTimer.getInstance().schedule(taskOfSpyMemory, 1000 * 60, 1000 * 60);
    }

    /*
     * 取得提前时间
     * @param 时间字符串
     * @return 提前时间
     */
    private long getAdvanceTime(
            String prmString) {
        String init = CASUtility.EMPTYSTR;
        long advance = 0;
        int minPos = prmString.indexOf(AppointmentConstant.MINUTE);
        int hourPos = prmString.indexOf(AppointmentConstant.HOUR);
        int dayPos = prmString.indexOf(AppointmentConstant.DAY);
        if (minPos != -1) {
            prmString = prmString.substring(0, minPos).trim();
            init = AppointmentConstant.MINUTE;
        } else if (hourPos != -1) {
            prmString = prmString.substring(0, hourPos).trim();
            init = AppointmentConstant.HOUR;
        } else if (dayPos != -1) {
            prmString = prmString.substring(0, dayPos).trim();
            init = AppointmentConstant.DAY;
        } else {
            prmString = prmString.trim();
            init = AppointmentConstant.MINUTE;
        }
        try {
            advance = Long.parseLong(prmString);
            if (init == AppointmentConstant.MINUTE)
                advance = advance * 60 * 1000;
            else if (init == AppointmentConstant.HOUR)
                advance = advance * 60 * 60 * 1000;
            else if (init == AppointmentConstant.DAY)
                advance = advance * 24 * 60 * 60 * 1000;
            else
                advance = advance * 60 * 1000;
        } catch (Exception e) {
            // /*****/A.s("存储提前时间错误");
        }

        return advance;
    }

    /**
     * @called by: SendAndReceiveAllAciton.java； 立即执行收发信动作。
     * @TODO:不应该再new一个新的任务，应该调ThreadAction的方法，使原来的定期任务时间调整至立即执行
     */
    public void sendAndReceiveAll() {
        taskVec.clear();
        TaskOfSendMail tmpSendMail = new TaskOfSendMail(true);
        taskVec.add(tmpSendMail);
        CASTimer.getInstance().schedule(tmpSendMail, 0);
        TaskOfReceiveMail receiveMail = new TaskOfReceiveMail(false);
        taskVec.add(receiveMail);
        CASTimer.getInstance().schedule(receiveMail, 0); // @?为什么是1000(1秒),如果也是0或1会出错吗?
    }

    /** @called by: SendAllAction.java; */
    public void sendAll() {
        // 将发送任务放到线程只执行
        taskVec.clear();
        TaskOfSendMail tmpSendMail = new TaskOfSendMail(true);
        taskVec.add(tmpSendMail);
        CASTimer.getInstance().schedule(tmpSendMail, 0);
    }

    /** called by: ReceiveAllAction.java */
    public void receiveAll() {
        taskVec.clear();
        TaskOfReceiveMail receiveMail = new TaskOfReceiveMail(false);
        taskVec.add(receiveMail);
        CASTimer.getInstance().schedule(receiveMail, 0);
    }

    /**
     * 布置提醒任务，在prmDelaySecons毫秒后执行。
     *
     * @param prmTask
     *            待布置的提醒任务
     * @param prmDelaySecons
     *            提醒任务执行前等待的时间。单位毫秒。
     * @throws IllegalArgumentException
     *             如果 <tt>prmDelaySecons</tt> 是负的，或者 <tt>prmDelaySecons + System.currentTimeMillis()</tt> 是负的。
     * @throws IllegalStateException
     *             如果后台线程管理器被cancel。
     */
    public void schedule(
            APIMTimingTask prmTask,
            long prmDelaySecons) {
        CASTimer.getInstance().schedule(prmTask, prmDelaySecons);
    }

    /** @Called by: TaskDialog.java */
    public void addTaskRemind(
            int prmID,
            Calendar prmRemindCalendar) {
        int tmpAppIndex = CustOpts.custOps.APPNameVec.indexOf("Task");
        int tmpNodeID = CASUtility.getAPPNodeID(tmpAppIndex);
        CASTimer.getInstance().schedule(new TaskOfNoticeTask(prmID, tmpAppIndex, tmpNodeID), prmRemindCalendar);
    }

    /**
     * 中止后台线程管理器，忽略所有已布置的提醒任务。
     * 
     * @called by: PIMControl;
     *         <p>
     *         该方法可能被多次调用，但第二次及之后的调用无效。
     */
    public static void cancel() {
        if (instance != null)
            CASTimer.getInstance().cancel();
    }

    /**
     * 取消提醒任务。
     * 
     * @param prmTask
     *            待取消的任务。
     * @throws IllegalStateException
     *             task被cancel了或根本没有布置。
     */
    public void cancelTask(
            APIMTimingTask prmTask) {
        CASTimer.getInstance().cancelTask(prmTask);
    }

    /** @called by: TaskDialog.java */
    public void snooze(
            JFrame prmFrame,
            long prmTime) {
        schedule(new TaskOfSnooze(prmFrame), prmTime);
    }

    /** @called by: AccountDlg. */
    public APIMTimingTask getMailReciveTask() {
        return taskOfReceiveMail;
    }

    /** @called by: AccountDlg. */
    public APIMTimingTask getMailSendTask() {
        return taskOfSendMail;
    }

    public Vector getMailTask() {
        return taskVec;
    }

    /**
     * 切换应用的时候需要收集垃圾（内容较多的时候和频繁切换的时候作用明显），但在视图显示之前 进行垃圾收集会导致响应变慢，如果在视图切换之后在进行垃圾收集可能情况会好一点
     * 基于这样的考虑，定义此方法，此方法将原来的垃圾收集线程停止，并重新设定一个线程 执行垃圾收集，此新线程在指定的时间执行第一次，以后每次在一定的时间间隔执行，可能会 解决以上提到的问题
     */
    public void resetMemoryTask(
            long prmPeriod) {
        if (taskOfSpyMemory == null) // 由于办类在线程中初始化,所以如果刚秀出界面就点切换,那么调到本方法时,本变量可能仍为空.
            return;

        taskOfSpyMemory.state = TaskOfSpyMemory.SCHEDULED;
        CASTimer.getInstance().schedule(taskOfSpyMemory, prmPeriod, 1000 * 60);
    }

    private APIMTimingTask taskOfReceiveMail;
    private APIMTimingTask taskOfSendMail;
    private APIMTimingTask taskOfSpyMemory;
    private Vector taskVec = new Vector();
}
