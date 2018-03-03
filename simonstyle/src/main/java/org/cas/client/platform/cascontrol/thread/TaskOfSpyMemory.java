package org.cas.client.platform.cascontrol.thread;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;

/**
 * 在ModelSetter中实例化此类并进行内存监视，最初的设计思想： 定期对内存监视，如果内存占用已经超过 一定的比例（指的使用的内存和jvm的内存总和的比例），就进行强制的垃圾收集的动作。
 * 对gc的调用说明：sun内部测试gc的时候有三种使用方式： 1）、连续调用10次gc与System.runFinalization() 2）、连续调用3次gc与System.runFinalization()，使线程sleep
 * 5000ms，在调用一次gc，在sleep 5000ms 3）、仅调用一次gc 测试结果表明仅调用一次可以满足需要，若有需要，可以采用调用多次并sleep当前Thread的方法试验。
 * 若有更好的内存处理方法，或者不需要处理内存的问题，请删除此类。
 */

class TaskOfSpyMemory extends APIMTimingTask {
    /**
     * 创建MemoryMonitorTask对象
     */
    TaskOfSpyMemory() {
    }

    /**
     * 任务到期执行的动作。
     */
    void execute() {
        // Runtime rt = Runtime.getRuntime();
        // long total = rt.totalMemory();
        // long free = rt.freeMemory();
        // A.s(" begin free :"+free);
        // A.s(" begin total :"+total);
        // A.s(" 开始内存使用率 ："+(double)(total - free)/(double)total);
        // long time = System.currentTimeMillis();
        // if ((double)(total - free)/(double)total > 0.9)
        // {
        // for (int i = 9; －－i >= 0;)
        // {
        System.gc();
        System.runFinalization();
        // }
        if ((CASMainFrame) CASControl.ctrl.getMainFrame() == null) {
            return;
        }
        // ((PIMMainFrame)PIMControl.ctrl.getPIMFrame()).getAdvancedBar().refreshMemoryInfo();
        // Runtime runtime = Runtime.getRuntime();
        // long totalMemory = runtime.totalMemory();
        // long freeMemory = runtime.freeMemory();
        // int value = (int)((((double)(totalMemory - freeMemory))/(double)totalMemory)*100);
        // A.s(" value :"+value);
        // PIMProgressBar memoryProgressBar =
        // ((PIMMainFrame)PIMControl.ctrl.getMainFrame()).getBarManager().getAdvancedBar().getMemoryProgressBar();
        // memoryProgressBar.setValue(value);
        // String progressTip = "(Used)"+(((double)(totalMemory -
        // freeMemory))/1000)+"K/(Total)"+((double)totalMemory/1000)+'K';
        // memoryProgressBar.setToolTipText(progressTip);
        // A.s(" time :"+(System.currentTimeMillis() - time));
        // total = rt.totalMemory();
        // free = rt.freeMemory();
        // A.s(" after free :"+free);
        // A.s(" after total :"+total);
        // A.s(" 收集后内存使用率 ："+(double)(total - free)/(double)total);
    }

    void setStatus(
            boolean status) {
    }
}
