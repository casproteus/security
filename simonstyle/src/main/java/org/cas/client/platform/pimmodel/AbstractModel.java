package org.cas.client.platform.pimmodel;

import java.io.Serializable;

import javax.swing.event.EventListenerList;

import org.cas.client.platform.ICASModel;
import org.cas.client.platform.pimmodel.event.IPIMModelListener;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;

abstract class AbstractModel implements ICASModel, Serializable {
    /**
     * 初始化中间结构信息
     */
    // protected abstract void initMediumStructure();
    /**
     * 初始化视图信息
     */
    // protected abstract PIMViewInfo initViewInfo(int appType, int appSubType);
    /**
     * 初始化记录
     */
    // protected abstract void generateRecords();
    /**
     * 把一个事件添加到事件监听器列表，该事件表示一个PIMModel的修改
     * 
     * @param: PIMModelListener l
     */
    public void addPIMModelListener(
            IPIMModelListener l) {
        listenerList.add(IPIMModelListener.class, l);
    }

    /**
     * 把一个事件添从事件监听器列表中删除，该事件表示一个PIMModel的修改
     * 
     * @param: PIMModelListener l
     */
    public void removePIMModelListener(
            IPIMModelListener l) {
        listenerList.remove(IPIMModelListener.class, l);
    }

    /**
     * 返回PIMModel更改事件的列表
     * 
     * @return: PIMModelListener[]
     */
    IPIMModelListener[] getPIMModelListeners() {
        return (IPIMModelListener[]) listenerList.getListeners(IPIMModelListener.class);
    }

    /**
     * 发送制定的事件到所有已注册的监听器列表中。
     * 
     * @param: PIMModelEvent model中数据改变后发的事件
     */
    private void firePIMModelChanged(
            PIMModelEvent e) {
        Object[] listeners = listenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IPIMModelListener.class) {
                ((IPIMModelListener) listeners[i + 1]).recordStateChanged(e);
            }
        }
    }

    /**
     * 发送改变记录事件
     */
    protected void firePIMModelDataChanged() {
        firePIMModelChanged(new PIMModelEvent(this));
    }

    /**
     * 发送插入记录事件
     */
    protected void firePIMModelDataInserted() {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.INSERT));
    }

    /**
     * 发送插入记录事件
     * 
     * @param int id 对于数据库来说是表的ID，对于SysSheet来说是SheetID。
     */
    protected void firePIMModelDataInserted(
            int id) {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.INSERT, id));
    }

    /**
     * 发送插入记录事件
     * 
     * @param int id 对于数据库来说是表的ID，对于SysSheet来说是SheetID。
     */
    protected void firePIMModelDataInserted(
            int id,
            PIMRecord[] record) {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.INSERT, id, record));
    }

    /**
     * 发送删除记录事件
     * 
     * @param int id 对于数据库来说是表的ID，对于SysSheet来说是SheetID。
     */
    protected void firePIMModelDataDeleted(
            int id) {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.DELETE, id));
    }

    /**
     * 发送删除记录事件
     * 
     * @param int id 对于数据库来说是表的ID，对于SysSheet来说是SheetID。
     * @param PIMRecord
     *            [] record 被删除的记录数组
     */
    protected void firePIMModelDataDeleted(
            int id,
            PIMRecord[] record) {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.DELETE, id, record));
    }

    /**
     * 发送删除记录事件
     */
    protected void firePIMModelDataDeleted() {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.DELETE));
    }

    /**
     * 发送更新记录事件
     */
    protected void firePIMModelDataUpdated() {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.UPDATE));
    }

    /**
     * 发送更新记录事件
     * 
     * @param int id 对于数据库来说是表的ID，对于SysSheet来说是SheetID。
     * @param PIMRecord
     *            [] record
     */
    protected void firePIMModelDataUpdated(
            int id,
            PIMRecord[] record) {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.UPDATE, id, record));
    }

    /**
     * 发送更新记录事件
     * 
     * @param int prmAppType 对于数据库来说是表的ID，对于SysSheet来说是SheetID。
     */
    protected void firePIMModelDataUpdated(
            int prmAppType) {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.UPDATE, prmAppType));
    }

    protected void fireFieldChangedEvent(
            int id,
            int changeFlag) {
        firePIMModelChanged(new PIMModelEvent(this, changeFlag, id));
    }

    protected void fireFormatChangedEvent() {
        firePIMModelChanged(new PIMModelEvent(this, PIMModelEvent.UPDATE_FORMAT));
    }

    // NOTE:此方法没有被使用,
    // /**
    // * 根据监听器类型返回监听器列表
    // * @param: Class listenerType
    // * @return: EventListener[]
    // */
    // private EventListener[] getListeners (Class listenerType)
    // {
    // return listenerList.getListeners (listenerType);
    // }

    protected EventListenerList listenerList = new EventListenerList();
}
