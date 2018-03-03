package org.cas.client.platform.cascontrol.dialog;

import java.awt.Container;
import java.awt.Window;
import java.io.File;
import java.util.Vector;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimmodel.PIMRecord;

/**
 * 操作记录的对话盒要实现的接口
 */

public interface ICASDialog extends Releasable {
    PIMRecord getContents(); // Creates a new instance of IPIMDialog

    boolean setContents(
            PIMRecord prmRecord);

    Window getOwner(); // 定义该极其可能与对话盒父类重复的方法,为的是使Action能够判断对话盒的拥有着,
                       // 如果是拥有者是一个对话盒,那么很可能父对话需要得到子对话盒产生的纪录

    void makeBestUseOfTime(); // 本方法用于规定一个机制：当任何一个PIM对话盒打开后，如果有一个较空余的时间，可以启动线程（model的
                              // release方法中负责在线程中增加一个收内存的任务）来回收由于连接没有断掉而产生的未被回收的内存。

    Container getContainer();

    void reLayout();

    void addAttach(
            File[] file,
            Vector actualAttachFiles);

    PIMTextPane getTextPane();
}
