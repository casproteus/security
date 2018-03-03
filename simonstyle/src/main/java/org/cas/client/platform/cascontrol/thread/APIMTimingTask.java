package org.cas.client.platform.cascontrol.thread;

public abstract class APIMTimingTask {
    /**
     * 此任务未被布置。
     */
    static final int VIRGIN = 0;

    /**
     * 任务已被布置。 若为非周期性任务，还代表未执行。
     */
    static final int SCHEDULED = 1;

    /**
     * 仅对非周期性任务有意义，表示已执行但未取消。
     */
    static final int EXECUTED = 2;

    /**
     * 任务被取消。一般通过调用TimerTask.cancel。
     */
    static final int CANCELLED = 3;

    /**
     * The state of this task, chosen from the constants below. 任务状态。初始化为VIRGIN
     */
    int state = VIRGIN;

    /**
     * 同步锁。用于访问控制。
     */
    final Object lock = new Object();

    /**
     * 下一次执行的时间。System.currentTimeMillis格式。 对于周期性任务，每次执行后对要调整。
     */
    long nextExecutionTime;

    /**
     * 重复周期，单位毫秒。
     */
    long cycleTime;

    /**
     * 取消当前任务。
     * <p>
     * 在一个周期性任务的run方法中调用此方法，可以保证此任务不会在执行。
     * <p>
     * 该方法可能被多次调用，但第二次及之后的调用无效。
     * 
     * @return true 方法调用前任务已布置但未执行 false 方法未布置，已执行，或已取消。
     */
    boolean cancel() {
        synchronized (lock) {
            boolean tmpResult = (state == SCHEDULED);
            state = CANCELLED;
            return tmpResult;
        }
    }

    // abstract method-----------------------------------------------
    /**
     * 任务到期执行的动作。
     */
    abstract void execute();

    abstract void setStatus(
            boolean status);
}
