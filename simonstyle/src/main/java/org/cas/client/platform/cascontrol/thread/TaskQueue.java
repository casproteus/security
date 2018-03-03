package org.cas.client.platform.cascontrol.thread;

import org.cas.client.platform.casutil.ErrorUtil;

/**
 * 此类维护一个提醒任务列表，根据执行时间排列先后顺序。 实现中，根据需要，只将最近的提醒任务放在队列的第一个位置，其它任务不排序。
 */

class TaskQueue {
    /**
     * 提醒任务队列。根据任务的数量扩展数组，每次*2。 通过最小排序，保持最近的提醒任务放在队列的第一个位置。
     * <p>
     * tasksAry[1].nextExecutionTime <= tasksAry[i].nextExecutionTime (i >= 2)
     */
    private APIMTimingTask[] tasksAry = new APIMTimingTask[20];

    /**
     * 队列长度，及实际存放的任务数。
     */
    private int size;

    /**
     * 添加一个新任务到队列中。
     * 
     * @param prmTask
     *            待添加的任务。
     * @called by: PIMTimer;
     */
    void add(
            APIMTimingTask prmTask) {
        // 根据任务的数量扩展数组，每次*2
        if (++size == tasksAry.length) {
            APIMTimingTask[] newQueue = new APIMTimingTask[2 * tasksAry.length];
            System.arraycopy(tasksAry, 0, newQueue, 0, size);
            tasksAry = newQueue;
        }
        // 新任务先添加到最后
        tasksAry[size] = prmTask;
    }

    /**
     * 获得最近的任务。
     * 
     * @param 最近的任务
     *            。
     */
    APIMTimingTask getMin() {
        minSort();
        return tasksAry[1];
    }

    /**
     * 删除最近的任务。
     *
     */
    void removeMin() {
        // 因为removeMin总是在getMin后被调用，所以此处实际上此处不需排序。
        // 但为了程序清晰，并在TimerThread改动时不影响程序的正确性，调用minSort。
        minSort();
        removeTask(1);
    }

    /**
     * 删除指定的任务
     * 
     * @param prmTask
     *            待删除的task
     */
    void remove(
            APIMTimingTask prmTask) {
        // 查找并删除
        for (int i = 1; i <= size; i++) {
            if (tasksAry[i] == prmTask) {
                removeTask(i);
                return;
            }
        }
        // 找不到，报错。
        ErrorUtil.write("This task has been canceled or No such task(TaskQueue.remove())");
        // throw new IllegalStateException("This task has been canceled or No such task.");
    }

    /**
     * 将最近任务的执行时间下调。并重新调整队列。
     */
    void rescheduleMin(
            long newTime) {
        tasksAry[1].nextExecutionTime = newTime;
    }

    /**
     * 判断队列是否为空。
     * 
     * @return true 队列为空。 false 队列非空。
     */
    boolean isEmpty() {
        return size == 0;
    }

    /**
     * 清空队列。
     */
    void clear() {
        // Null out task references to prevent memory leak
        for (int i = 1; i <= size; i++) {
            tasksAry[i] = null;
        }

        size = 0;
    }

    /**
     * 排序。将最近的任务放到第一位。
     */
    private void minSort() {
        // 最近的任务的ID
        int minTaskIndex = 1;
        // 查找最近的任务
        for (int i = 2; i <= size; i++) {
            if (tasksAry[i].nextExecutionTime < tasksAry[minTaskIndex].nextExecutionTime) {
                minTaskIndex = i;
            }
        }
        // 将最近的任务放到第一位
        APIMTimingTask minTask = tasksAry[minTaskIndex];
        tasksAry[minTaskIndex] = tasksAry[1];
        tasksAry[1] = minTask;
    }

    /**
     * 删除第i个任务
     * 
     * @param i
     *            待删除的任务在列表中的位置
     */
    private void removeTask(
            int i) {
        tasksAry[i] = tasksAry[size];
        tasksAry[size--] = null;
    }
}
