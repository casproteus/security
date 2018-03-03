package org.cas.client.platform.cascontrol.thread;

import org.cas.client.platform.casutil.ErrorUtil;

/**
 * 后台线程。负责管理提前任务队列，执行提醒任务，重复周期性提醒任务， 删除被取消的提醒任务和执行过的非周期性提醒任务
 */

class TimerThread extends Thread {
    /**
     * 构造方法。
     * 
     * @param taskQueue
     *            提醒任务队列。
     */
    TimerThread(TaskQueue prmTaskQueue) {
        taskQueue = prmTaskQueue;
        setName("PIMTasksThread");
    }

    /**
     * 实现父类的run方法
     */
    public void run() {
        try {
            mainLoop();
        } finally {
            // 线程被中止。
            synchronized (taskQueue) {
                // 设置标志
                newTasksMayBeScheduled = false;
                // 删除无用的引用
                taskQueue.clear();
            }
        }
    }

    /**
     * 主循环。
     */
    private void mainLoop() {
        while (true) {
            APIMTimingTask tmpTask;
            boolean taskFired;
            synchronized (taskQueue) {
                // 当前无提醒任务，但可能有提醒任务加入
                while (taskQueue.isEmpty() && newTasksMayBeScheduled) {
                    try {
                        taskQueue.wait();
                    } catch (InterruptedException e) {
                    }
                }
                // 当前无提醒任务，也不会再有提醒任务加入，退出线程。
                if (taskQueue.isEmpty()) {
                    break;
                }
                // 队列非空。查找最近的提醒任务，并作相应操作。
                long currentTime, executionTime;
                // 最近的任务
                tmpTask = taskQueue.getMin();
                synchronized (tmpTask.lock) {
                    // 若为取消的任务，删除，并进入下一轮循环。
                    if (tmpTask.state == APIMTimingTask.CANCELLED) {
                        taskQueue.removeMin();
                        continue; // No action required, poll taskQueue again
                    }
                    // 获得当前的时间和最近的任务执行的时间。
                    currentTime = System.currentTimeMillis();
                    executionTime = tmpTask.nextExecutionTime;
                    // 若为到期任务，设置执行标志。
                    if (taskFired = (executionTime <= currentTime)) {
                        if (tmpTask.cycleTime == 0) {
                            // 非周期性任务
                            taskQueue.removeMin();
                            tmpTask.state = APIMTimingTask.EXECUTED;
                        } else {
                            // 周期性任务，重新布置。
                            taskQueue.rescheduleMin(executionTime + tmpTask.cycleTime);
                        }
                    }
                }
                // synchronized(tmpTask.lock)
                // 若无任务到期，根据间隔时间等待
                // tmpTask hasn't yet fired; wait
                if (!taskFired) {
                    try {
                        taskQueue.wait(executionTime - currentTime);
                    } catch (InterruptedException e) {
                    }
                }
            }
            // synchronized(taskQueue)
            // 任务到期执行。不同步。
            if (taskFired) {
                try {
                    tmpTask.execute();
                } catch (Exception e) {
                    ErrorUtil.write(e);
                }
            }
        }
    }

    /**
     * 当没有提醒任务也不会再有新的提醒任务添加到队列中时，将此标志设为false， 以使线程自然中止。 TODO：比如什么时候呢？
     */
    boolean newTasksMayBeScheduled = true;

    /**
     * 提醒任务队列。 我们在此保存一个引用，而不是使用PIMTimer中的引用使得引用形成循环。 否则Timer用于不会被垃圾收集，这个线程也不会。
     */
    private TaskQueue taskQueue;
}
