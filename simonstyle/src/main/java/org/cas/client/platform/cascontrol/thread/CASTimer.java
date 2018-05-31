package org.cas.client.platform.cascontrol.thread;

import java.util.Calendar;

import org.cas.client.platform.casutil.ErrorUtil;

/**
 * PIM后台线程管理器。使用线程管理PIM中的周期性事件和定时提醒任务。 PIM只需要一个后台线程管理器，故此类为单件。
 */

class CASTimer {
    static CASTimer instance = new CASTimer(); // Singlton

    static CASTimer getInstance() {
        if (instance == null) // PIMTimer被Cancel
            instance = new CASTimer();

        if (!instance.thread.isAlive()) { // 若当前线程已停止，
            instance.thread.setPriority(Thread.MIN_PRIORITY); // 重新开始
            instance.thread.newTasksMayBeScheduled = true;
            // TODO:此处需要增加安全保证,防止返回了实例后,实例的方法执行中发现线程没有启动而导致错误.
            try {
            	instance.thread.start();
            }catch(Exception e) {}
        }
        return instance;
    }

    /*
     * 构造方法。singleton，构造方法为private。 因为在PIM退出时，线程同时退出，所以不支持将线程声明为daemon。
     * @see Thread
     * @see #cancel()
     */
    private CASTimer() {
        // TODO：设置后台线程的优先级，具体由规格确定。
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.start();
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
    void schedule(
            APIMTimingTask prmTask,
            long prmDelaySecons) {
        if (prmDelaySecons < 0)
            ErrorUtil.write("0007", new Exception("Negative prmDelaySecons"));// throw new
                                                                              // IllegalArgumentException("Negative prmDelaySecons.");
        sched(prmTask, System.currentTimeMillis() + prmDelaySecons, 0);
    }

    /**
     * 布置提醒任务在一定时间执行。如果执行的时间已过，则布置后立即执行。
     *
     * @param prmTask
     *            待布置的提醒任务
     * @param time
     *            提醒任务执行的时间。
     * @throws IllegalArgumentException
     *             如果 <tt>dtime.getTimeInMillis</tt> 是负的。
     * @throws IllegalStateException
     *             如果后台线程管理器被cancel。
     */
    void schedule(
            APIMTimingTask prmTask,
            Calendar time) {
        sched(prmTask, time.getTimeInMillis(), 0);
    }

    /**
     * 布置周期性提醒任务。
     *
     * @param prmTask
     *            待布置的提醒任务
     * @param prmDelaySecons
     *            提醒任务执行前等待的时间。单位毫秒。
     * @param prmPeriod
     *            周期性任务的执行周期。单位毫秒。
     * @throws IllegalArgumentException
     *             如果 <tt>prmDelaySecons</tt> 是负的，或者 <tt>prmDelaySecons + System.currentTimeMillis()</tt> 是负的。
     * @throws IllegalStateException
     *             如果后台线程管理器被cancel。
     */
    void schedule(
            APIMTimingTask prmTask,
            long prmDelaySecons,
            long prmPeriod) {
        if (prmDelaySecons < 0)
            ErrorUtil.write("0008", new Exception("Negative prmDelaySecons"));
        if (prmPeriod <= 0)
            ErrorUtil.write("0009", new Exception("Non-positive cycleTime"));

        sched(prmTask, System.currentTimeMillis() + prmDelaySecons, prmPeriod);
    }

    /**
     * 布置周期性提醒任务。
     *
     * @param prmTask
     *            待布置的提醒任务
     * @param time
     *            提醒任务首次执行的时间。单位毫秒。
     * @param prmPeriod
     *            周期性任务的执行周期。单位毫秒。
     * @throws IllegalArgumentException
     *             如果 <tt>time.getTimeInMillis()</tt> 是负的。
     * @throws IllegalStateException
     *             如果后台线程管理器被cancel。
     */
    void schedule(
            APIMTimingTask prmTask,
            Calendar time,
            long prmPeriod) {
        if (prmPeriod <= 0)
            ErrorUtil.write("0010", new Exception("Non-positive cycleTime"));

        sched(prmTask, time.getTimeInMillis(), prmPeriod);
    }

    /**
     * 取消提醒任务。
     * 
     * @param prmTask
     *            待取消的任务。
     * @throws IllegalStateException
     *             task被cancel了或根本没有布置。
     */
    void cancelTask(
            APIMTimingTask prmTask) {
        prmTask.cancel();
        sched(prmTask, Long.MAX_VALUE, 0);
    }

    /**
     * 布置提醒任务在prmCycleTime时间执行，prmNextExecTime毫秒重复一次。 如果prmNextExecTime > 0，提醒任务作为周期性任务布置。 如果prmNextExecTime =
     * 0，提醒任务作为一次性任务布置。 time符合Calendar.getTime()格式。 此方法检查后台线程管理器状态，任务状态，并初始化执行时间，但不涉及周期。
     *
     * @throws IllegalStateException
     *             如果后台线程管理器被cancel。
     */
    private void sched(
            APIMTimingTask prmTask,
            long prmNextExecTime,
            long prmCycleTime) {
        synchronized (taskQueue) {
            if (!thread.newTasksMayBeScheduled) // 检查后台线程管理器是否被cancel了。
                ErrorUtil.write("0011", new Exception("Timer already cancelled."));

            synchronized (prmTask.lock) {
                prmTask.nextExecutionTime = prmNextExecTime; // 设置任务的属性
                prmTask.cycleTime = prmCycleTime; // 可能是原来的笔误，现在修正

                if (prmTask.state == APIMTimingTask.VIRGIN) { // 根据任务的状态作不同的动作
                    prmTask.state = APIMTimingTask.SCHEDULED; // 新任务，添加到队列中
                    taskQueue.add(prmTask); // 将任务添加到任务列表中
                } else if (prmTask.state == APIMTimingTask.SCHEDULED) {
                    // 已布置任务，重新排序
                } else if (prmTask.state == APIMTimingTask.CANCELLED)
                    taskQueue.remove(prmTask); // 删除已有记录
            }

            if (taskQueue.getMin() == prmTask) // 如果当前添加的任务是最近的任务
                taskQueue.notify();
        }
    }

    /**
     * 中止后台线程管理器，忽略所有已布置的提醒任务。
     * 
     * @called by: PIMControl;
     *         <p>
     *         该方法可能被多次调用，但第二次及之后的调用无效。
     */
    void cancel() {
        synchronized (taskQueue) {
            thread.newTasksMayBeScheduled = false;
            taskQueue.clear();
            taskQueue.notify(); // In case taskQueue was already empty.
        }
    }

    private TaskQueue taskQueue = new TaskQueue(); // 任务列表。与内部线程共享。通过不同的schedule方法添加任务，执行时间通过cancelTask删除。
    private TimerThread thread = new TimerThread(taskQueue);// 用于定时执行任务的线程
}
