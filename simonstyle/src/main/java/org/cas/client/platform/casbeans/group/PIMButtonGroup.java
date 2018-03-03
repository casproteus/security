package org.cas.client.platform.casbeans.group;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JToggleButton;

public class PIMButtonGroup extends MouseAdapter implements ActionListener {

    /**
     * create button group without parameter.
     */
    public PIMButtonGroup() {
    }

    /**
     * create group for count buttons ,and has double clicked listener okListener.
     * 
     * @param count
     *            count of buttons.
     * @param dialog
     *            the dialog contain this group.
     * @param ok
     *            the button to be activated when double click.
     * @param okListener
     *            ok button's action listener.
     * @param groupListener
     *            When selection changed invoke the method select() in this interface.
     */
    public PIMButtonGroup(int count, ActionListener okListener, PIMButtonGroupListener groupListener) {
        this(null, groupListener);
        buttons = new JToggleButton[size = count];
    }

    /**
     * create group for buttons ,and has double clicked listener okListener.
     * 
     * @param buttons
     *            the button array.
     * @param dialog
     *            the dialog contain this group.
     * @param ok
     *            the button to be activated when double click.
     * @param groupListener
     *            When selection changed invoke the method select() in this interface.
     */
    public PIMButtonGroup(JToggleButton[] buttons, PIMButtonGroupListener groupListener) {
        addEButtonGroupListener(groupListener);
        select = -1;
        if (buttons != null) {
            this.buttons = buttons;
            size = buttons.length;
            while (count < size) {
                addButton(buttons[count]);
            }
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            ActionEvent object.
     */
    public void actionPerformed(
            ActionEvent e) {
        JToggleButton button = (JToggleButton) e.getSource();
        if (!button.isEnabled()) {
            return;
        }
        if (select < 0 || button != buttons[select]) {
            for (int i = 0; i < count; i++) {
                if (button == buttons[i]) {
                    select(i, true);
                }
            }
            return;
        }
        select(button);
        button.setSelected(true);
        setSelectIndex(focus);
        if (special) {
            Object listener = groupListener;
            if (listener != null) {
                ((PIMButtonGroupListener) listener).selected(null, select);
            }
        }
    }

    /**
     * Invoked when the mouse released.
     * 
     * @param e
     *            MouseEvent object.
     */
    public void mouseReleased(
            MouseEvent e) {
        JToggleButton button;
        if (e.getModifiers() == e.BUTTON1_MASK &&
        // End of BUG#10398.
                (button = (JToggleButton) e.getSource()).isEnabled()) {
            if (select < 0) {
                select(button);
                setSelectIndex(focus);
            } else if (button != buttons[select]) {
                select(select, true);
            }
        }
    }

    /**
     * Adds a PIMButtonGroupListener to the group.
     * 
     * @param groupListener
     *            the listener to be added.
     */
    public void addEButtonGroupListener(
            PIMButtonGroupListener groupListener) {
        this.groupListener = groupListener;
    }

    /**
     * Removes a PIMButtonGroupListener from the group.
     * 
     * @param listener
     *            the listener to be removed.
     */
    public void removeEButtonGroupListener(
            PIMButtonGroupListener groupListener) {
        this.groupListener = null;
    }

    /**
     * Add a RadioButton to group.
     * 
     * @param button
     *            the JToggleButton to be added.
     */
    public void add(
            JToggleButton button) {
        if (count >= size) {
            JToggleButton[] newButton = new JToggleButton[size + 4];
            for (int i = 0; i < count; i++) {
                newButton[i] = buttons[i];
            }
            size += 4;
            buttons = newButton;
        }
        addButton(buttons[count] = button);
    }

    /**
     * set the selection index number,from 0 to count-1.
     * 
     * @param index
     *            index number.
     */
    public void setSelectIndex(
            int index) {
        if (index < count) {
            if ((select = index) >= 0) {
                focus = index;
            }
        }
        for (int i = 0; i < count; i++) {
            JToggleButton b = buttons[i];
            boolean selected = i == select;
            b.setSelected(selected);
            if (b instanceof Groupable) {
                ((Groupable) b).setFocusFlag(selected || select < 0 && i == focus);
            }
        }
    }

    /**
     * return the selected button's index number ,from 0 to count-1.
     * 
     * @return index of selected button.
     */
    public int getSelectIndex() {
        return select;
    }

    /**
     * enable or disable the group of Radibutton.
     * 
     * @param enable
     *            if false to disable the button group.
     */
    public void setEnabled(
            boolean enable) {
        for (int i = 0; i < count; i++) {
            buttons[i].setEnabled(enable);
        }
        if (enable) {
            setSelectIndex(select);
        }
    }

    /**
     * Invoke when Up/Dwon/Left/Right diection key pressed.
     * 
     * @param dir
     *            if dir is -1,Up/Left key pressed ; else is 1 ,Down/Right key pressed.
     */
    public void checkKey(
            int dir) {
        int now = 0;
        if (select >= 0) {
            int other = now = same ? select : focus;
            do {
                now += dir;
                if (now >= count) {
                    now = 0;
                } else if (now < 0) {
                    now = count - 1;
                }
            } while (!buttons[now].isEnabled() && other != now);
        }
        select(now, same);
    }

    /**
     * Enable or disable the special Group Listener.
     * 
     * @param enable
     *            the flag of special listener.
     */
    public void setSpecial(
            boolean enable) {
        special = enable;
    }

    private void addButton(
            JToggleButton button) {
        int count = this.count;
        int current = button.isSelected() ? count : select;
        if (button instanceof Groupable) {
            Groupable groupButton = (Groupable) button;
            same |= groupButton.isSelectedFocus();
            groupButton.setFocusFlag(count == 0 || current == count);
            groupButton.setGroup(this);
            button = buttons[focus];
            if (current == count && count != 0 && button instanceof Groupable) {
                ((Groupable) button).setFocusFlag(false);
            }
        }
        if (current != select) {
            select = focus = current;
        }
        this.count = count + 1;
    }

    /*
     * Change current selection to now.
     */
    private void select(
            int now,
            boolean real) {
        if (real) {
            setSelectIndex(now);
            Object listener = groupListener;
            if (listener != null) {
                ((PIMButtonGroupListener) listener).selected(this, select);
            }
        } else {
            select(buttons[now]);
        }
        buttons[focus].requestFocus();
    }

    protected void select(
            JToggleButton button) {
        boolean hasFocus = false;
        for (int i = 0; i < count; i++) {
            JToggleButton b = buttons[i];
            if (b == button) {
                focus = i;
            } else if (b instanceof Groupable) {
                if (b.hasFocus()) {
                    ((Groupable) b).setFocusFlag(false);
                    hasFocus = true;
                }
            }
        }
        if (hasFocus) {
            ((Groupable) button).setFocusFlag(true);
            button.requestFocus();
        }
    }

    private boolean same;
    private boolean special;
    private int count;
    private int select;
    private int size;
    private int focus;
    Object groupListener;
    JToggleButton[] buttons;
}
