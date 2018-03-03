package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;

/** This class provides default implementations for the JFC <code>Action</code> */
public abstract class SAction implements Action {
    // 不知道什么意思?
    /**
     * 用于自定义工具条外部设置Action。
     */
    protected java.awt.event.ActionListener otherAction;

    /**
     * The key of initial name of this action.
     */
    public static final String INIT_NAME = "I";

    /**
     * The key of Other name of this action.
     */
    // public static final String OTHER_NAME = "O";
    /**
     * The key of enabled property.
     */
    public static final String ENABLED = "enabled";

    /**
     * The key of color property.
     */
    public static final String COLOR = "color";

    /**
     * The key of tooltip property.
     */
    public static final String TIP = "ToolTipText";

    /**
     * The key of pressed property.
     */
    public static final String PRESSED = "pressed";

    /** external flag. */
    public int flag;

    /** internal status. */
    public int status;

    /**
     * Specifies whether action is enabled; the default is true.
     */
    protected boolean enabled = true;

    /** 当前选择的索引 */
    protected char selectedIndex;

    /**
     * Contains the array of key bindings.
     */
    protected Object[] table;

    /**
     * If any <code>PropertyChangeListeners</code> have been registered, the Object listener may be this listenr or
     * array of PropertyChangeListeners.
     */
    protected Object listener;

    /** 用于快捷键的动作事件 */
    protected static ActionEvent itemEvent;

    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    public SAction() {
    }

    /**
     * Defines an <code>Action</code> object with a flag.
     * 
     * @param flag
     *            the external flag for this action.
     */
    public SAction(int flag) {
        this.flag = flag;
    }

    /**
     * Sets the <code>Value</code> associated with the specified key.
     * 
     * @param key
     *            the <code>String</code> that identifies the stored object
     * @param newValue
     *            the <code>Object</code> to store using this key
     * @see Action#putValue
     */
    public void putValue(
            String key,
            Object newValue) {
        if (key == null) {
            key = NAME;
            newValue = getValue(newValue == null ? INIT_NAME : (String) newValue);
        }
        // 处理特殊的字体名称组件
        else if (key == "recentItem") {
            if (newValue != null) {
                // 最近列表的索引
                int index = getKey("recentList") + 1;
                if (index > 0) {
                    // 最近列表的索引
                    Object[] recentList = (Object[]) table[index];
                    if (recentList != null) {
                        // 最近列表的索引的长度
                        int length = recentList.length;
                        // 长度计数
                        for (int i = 0; i < length; i++) {
                            if (newValue.equals(recentList[i])) {
                                if (i != 0) {
                                    // 仅改变位置
                                    System.arraycopy(recentList, 0, recentList, 1, i);
                                    recentList[0] = newValue;
                                    setValue(key, newValue);
                                }
                                return;
                            }
                        }
                    }
                }
            }
            return;
        }
        Object oldValue = setValue(key, newValue);
        if (oldValue != this) {
            firePropertyChange(key, oldValue, newValue);
        }
    }

    /**
     * Enables or disables the action.
     * 
     * @param value
     *            true to enable the action, false to disable it.
     */
    public void setEnabled(
            boolean value) {
        if (enabled != (((status = value ? status & (~1) : status | 1) & 0xFFFF8003) == 0)) {
            firePropertyChange(ENABLED, value ? Boolean.FALSE : Boolean.TRUE, (enabled = value) ? Boolean.TRUE
                    : Boolean.FALSE);
        }
    }

    /**
     * Returns true if the action is enabled.
     *
     * @return true if the action is enabled, false otherwise
     * @see Action#isEnabled
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Adds a <code>PropertyChangeListener</code> to the listener list. The listener is registered for all properties.
     * <p>
     * A <code>PropertyChangeEvent</code> will get fired in response to setting a bound property, e.g.
     * <code>setFont</code>, <code>setBackground</code>, or <code>setForeground</code>. Note that if the current
     * component is inheriting its foreground, background, or font from its container, then no event will be fired in
     * response to a change in the inherited property.
     *
     * @param listener
     *            The <code>PropertyChangeListener</code> to be added
     */
    public void addPropertyChangeListener(
            PropertyChangeListener listener) {
        addListener(listener);
    }

    /**
     * Removes a <code>PropertyChangeListener</code> from the listener list. This removes a
     * <code>PropertyChangeListener</code> that was registered for all properties.
     *
     * @param listener
     *            the <code>PropertyChangeListener</code> to be removed
     */
    public void removePropertyChangeListener(
            PropertyChangeListener listener) {
        removeListener(listener);
    }

    /**
     * Press Esc to undo this action.
     */
    public void reset() {
    }

    /**
     * 返回动作的选择状态。
     * 
     * @return 动作的选择状态。
     */
    public boolean isActionSelected() {
        return selectedIndex > 0;
    }

    /**
     * 返回动作的选择索引。
     * 
     * @return 动作的选择索引。
     */
    public int getSelectedIndex() {
        return selectedIndex;
    }

    /**
     * Gets the <code>Object</code> associated with the specified key.
     * 
     * @param key
     *            a string containing the specified <code>key</code>
     * @return the binding <code>Object</code> stored with this key; if there are no keys, it will return
     *         <code>null</code>
     * @see Action#getValue
     */
    public Object getValue(
            String key) {
        int index = getKey(key);
        return index >= 0 ? table[index + 1] : null;
    }

    /**
     * 设置属性值，不反映到组件。
     * 
     * @param key
     *            属性键值。
     * @param value
     *            属性的值。
     * @return 原来的值，this表示无效设置。
     */
    public Object setValue(
            String key,
            Object value) {
        int index = getKey(key);
        Object oldValue = index < 0 ? null : table[index + 1];
        // Remove the entry for key if newValue is null
        // else put in the newValue for key.
        if (value == null) {
            if (index < 0) {
                return this;
            }
            int length = table.length - 2;
            if (length <= 0) {
                table = null;
            } else {
                Object[] temp = new Object[length];
                if (index > 0) {
                    System.arraycopy(table, 0, temp, 0, index);
                }
                if (length > index) {
                    System.arraycopy(table, index + 2, temp, index, length - index);
                }
                table = temp;
            }
        } else if (table == null) {
            table = new Object[] { key, value };
        } else if (index >= 0) {
            if (value == oldValue) {
                return this;
            }
            table[index + 1] = value;
        } else {
            int length = table.length;
            Object[] temp = new Object[length + 2];
            System.arraycopy(table, 0, temp, 0, length);
            temp[length] = key;
            (table = temp)[length + 1] = value;
        }
        return oldValue;
    }

    /**
     * Get the index of key in table,if not found return -1
     * 
     * @param key
     *            a string containing the specified <code>key</code>
     * @return the index of key; if = -1,not found key in table.
     */
    private int getKey(
            String key) {
        Object[] table;
        if ((table = this.table) != null) {
            for (int i = 0; i < table.length; i += 2) {
                // original is (key.equals(table[i])),key must use the EAction Constants
                if (key == table[i]) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Set the eanbled status of some bit. Only called by Main Control.
     * 
     * @param flag
     *            the flag of set or reset bit.
     */
    public void checkStatus(
            int flag) {
        status = flag < 0 ? status & flag : status | flag;
    }

    /**
     * Set the eanbled status of some bit. Only called by Main Control.
     * 
     * @param flag
     *            the flag of set or reset bit.
     */
    public void setEnabled(
            int flag) {
        boolean value = ((status = flag < 0 ? status & flag : status | flag) & 0xFFFF8003) == 0;
        if (value != enabled) {
            firePropertyChange(ENABLED, value ? Boolean.FALSE : Boolean.TRUE, (enabled = value) ? Boolean.TRUE
                    : Boolean.FALSE);
        }
    }

    /**
     * Set the Action inner status without change the component. Only called by Main Control.
     * 
     * @param action
     *            the action to be set.
     * @param flag
     *            the enable or disable flag.
     */
    public static void setEnabled(
            SAction action,
            int flag) {
        if (action != null) {
            action.setEnabled(flag);
        }
    }

    /**
     * Set the Action inner status without change the component. Only called by Main Control.
     * 
     * @param action
     *            the action to be set.
     * @param enabled
     *            the enable or disable flag.
     */
    public static void setEnabled(
            SAction action,
            boolean enabled) {
        if (action != null) {
            action.setEnabled(enabled);
        }
    }

    /**
     * clear all flag
     * 
     * @param actions
     *            the actions to checked.
     * @param mask
     *            the status mask flag.
     */
    public static void clear(
            SAction[] actions,
            int mask) {
        for (int i = actions.length; i-- > 0;) {
            SAction action = actions[i];
            if (action != null) {
                action.status &= mask;
            }
        }
    }

    // Start of Optimization,code（02/13/2002）－by WuLiangqiao（User084）
    /**
     * set all flag
     * 
     * @param actions
     *            the actions to checked.
     * @param mask
     *            the status mask flag.
     */
    public static void set(
            SAction[] actions,
            int mask) {
        for (int i = actions.length; i-- > 0;) {
            SAction action = actions[i];
            if (action != null) {
                action.status |= mask;
            }
        }
    }

    // End of Optimization.

    /**
     * Set flag according the control string.
     * 
     * @param actions
     *            the actions to checked.
     * @param control
     *            the control string.
     * @param mask
     *            the status mask flag.
     */
    public static void setMask(
            SAction[] actions,
            String control,
            int mask) {
        for (int i = control.length(); i-- > 0;) {
            SAction action = actions[control.charAt(i)];
            if (action != null) {
                action.status |= mask;
            }
        }
    }

    /**
     * Set or reset flag according the control string.
     * 
     * @param actions
     *            the actions to checked.
     * @param control
     *            the control string.
     * @param mask
     *            the status mask flag.
     */
    public static void checkMask(
            SAction[] actions,
            String control,
            int mask) {
        for (int i = control.length(); i-- > 0;) {
            SAction action = actions[control.charAt(i)];
            if (action != null) {
                action.status = mask < 0 ? action.status & mask : action.status | mask;
            }
        }
    }

    /**
     * Enter a status such as EDIT,SELECT_PICTURE,SELECT_OBJECT
     * 
     * @param actions
     *            the actions to checked.
     * @param status
     *            status flag
     * @param set
     *            the flag for refresh the status.
     */
    public static void enter(
            SAction[] actions,
            int status,
            int mask) {
        for (int i = actions.length; i-- > 0;) {
            SAction action = actions[i];
            if (action != null) {
                action.status = (action.flag & status) == 0 ? action.status & (~mask) : action.status | mask;
            }
        }
    }

    /**
     * Set the special bit for special status.
     * 
     * @param actions
     *            the actions to checked.
     * @param status
     *            the status flag.
     * @param mask
     *            if mask < 0 , exit the status; else enter the status.
     */
    public static void checkSpecial(
            SAction[] actions,
            int status,
            int mask) {
        for (int i = actions.length; i-- > 0;) {
            SAction action = actions[i];
            if (action != null && (action.flag & status) != 0) {
                action.status = mask < 0 ? action.status & mask : action.status | mask;
            }
        }
    }

    /**
     * Enables or disables the actions. Only called by Main Control.
     * 
     * @param actions
     *            the actions to checked.
     */
    public static void fireActionStatus(
            SAction[] actions) {
        for (int i = actions.length; i-- > 0;) {
            SAction action = actions[i];
            boolean value;
            if (action != null && ((value = (action.status & 0xFFFF8003) == 0)) != action.enabled) {
                action.firePropertyChange(ENABLED, value ? Boolean.FALSE : Boolean.TRUE,
                        (action.enabled = value) ? Boolean.TRUE : Boolean.FALSE);
            }
        }
    }

    /**
     * Supports reporting bound property changes. This method can be called when a bound property has changed and it
     * will send the appropriate <code>PropertyChangeEvent</code> to any registered <code>PropertyChangeListeners</code>
     * .
     */
    public void firePropertyChange(
            String name,
            Object old,
            Object value) {
        Object item = this.listener;
        Object[] listeners;
        int length;
        if (item instanceof Object[]) {
            listeners = (Object[]) item;
            length = listeners.length;
        } else {
            if (item == null) {
                return;
            }
            listeners = null;
            length = 1;
        }
        PropertyChangeEvent evt = null;
        for (int i = length; i-- > 0;) {
            if (listeners != null) {
                item = listeners[i];
            }
            if (item instanceof WeakReference) {
                Object key = ((WeakReference) item).get();
                if (key == null) {
                    removeListener(item);
                    continue;
                }
                if (name == ENABLED) {
                    ((JComponent) key).setEnabled(value == Boolean.TRUE);
                } else if (name == SMALL_ICON) {
                    if (key instanceof AbstractButton) {
                        ((AbstractButton) key).setIcon((Icon) value);
                    }
                }
            } else if (item instanceof PropertyChangeListener) {
                ((PropertyChangeListener) item).propertyChange(evt != null ? evt : (evt =
                        new PropertyChangeEvent(this, name, old, value)));
            }
        }
    }

    /**
     * 添加一个动作的监听器。
     * 
     * @param object
     *            监听器，可能是组件，可能是属性监听器。
     */
    public synchronized void addListener(
            Object object) {
        // 处理数组的情况。
        if (this.listener instanceof Object[]) {
            // 原来的监听器数组
            Object[] old = (Object[]) this.listener;
            // 监听器个数
            int length = old.length;
            // i为监听器计数
            for (int i = 0; i < length; i++) {
                if (old[i] == object) {
                    return;
                }
            }
            // 新的监听器数组
            Object[] listeners = new Object[length + 1];
            System.arraycopy(old, 0, listeners, 0, length);
            listeners[length] = object;
            this.listener = listeners;
        } else if (this.listener == null) {
            // 为单独的监听器对象
            this.listener = object;
        } else {
            // 变为组件，不需要判断是否已存在
            this.listener = new Object[] { this.listener, object };
        }
    }

    /**
     * 移除该动作的监听器。
     * 
     * @param object
     *            监听器，可以时组件，也可以是属性监听器。
     */
    public synchronized void removeListener(
            Object object) {
        // 某个监听器
        Object item = this.listener;
        // 监听器的数组
        Object[] listeners;
        // 监听器的个数
        int length;
        if (item instanceof Object[]) {
            listeners = (Object[]) item;
            length = listeners.length;
        } else {
            // 没有任何监听器，返回
            if (item == null) {
                return;
            }
            // 单个监听器
            listeners = null;
            length = 1;
        }
        // 特殊标志，表示是否为组件
        boolean special = object instanceof JComponent;
        // i 为监听器的计数
        for (int i = 0; i < length; i++) {
            if (listeners != null) {
                item = listeners[i];
            }
            // 特殊的，需要取出真正的对象
            if (special && item instanceof WeakReference) {
                item = ((WeakReference) item).get();
            }
            if (item == object) {
                if (length <= 2) {
                    // 清空或者变成单独的对象
                    this.listener = length == 1 ? null : listeners[1 - i];
                } else {
                    // 仍然是对象数组
                    if (i < --length) {
                        listeners[i] = listeners[length];
                    }
                    System.arraycopy(listeners, 0, listener = new Object[length], 0, length);
                }
                return;
            }
        }
    }

    /**
     * 用于自定义工具条外部设置Action。
     * 
     * @param a
     *            外部Action。
     */
    public void setAction(
            java.awt.event.ActionListener a) {
        otherAction = a;
    }
}
