package org.cas.client.platform.pimmodel.event;

import java.util.EventListener;

/**
 * 该类维护项目中记录变化的监听器
 */

public interface IPIMModelListener extends EventListener {
    /** 当对象的某些属性有所改变时调用此方法,通知感兴趣的监听器. */
    void recordStateChanged(
            PIMModelEvent event);
}
