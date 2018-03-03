package org.cas.client.platform.casbeans.calendar;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableHeader;

public class CalendarCombo extends JComboBox implements PopupMenuListener, MouseListener, FocusListener,
        ComponentListener, Runnable {
    /** Creates a new instance of ColorCombo */
    public CalendarCombo() {
        super();
        calendarComboBoxUI = new CalendarComboBoxUI(this);
        setUI(calendarComboBoxUI);
        setRenderer(new CalendarRenderer());
        putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
        setBackground(Color.white);
        model = new CalendarComboModel(this);
        setModel(model);
        setEditor(new CalendarTextEditor(1));
        // setEditable(true);
        // model.setSelectedItem (null);
        // setSelectedItem (null);//model.getElementAt (0));
        addPopupMenuListener(this);
        // addFocusListener(this);
        // addComponentListener(this);
        // addKeyListener(this);
        calendarComboBoxUI.getArrowButton().addMouseListener(this);
        // calendarComboBoxUI.getArrowButton().addFocusListener(this);
        textField = (JTextField) getEditor();
        // textField.addKeyListener(this);
        if (getParent() instanceof PIMTable || getParent() instanceof PIMTableHeader) {
            setFocusTraversalKeysEnabled(false);
            ((JTextField) getEditor()).setFocusTraversalKeysEnabled(false);
            textField.addComponentListener(this);
            addFocusListener(this);
        } else {
            setFocusTraversalKeysEnabled(true);
            ((JTextField) getEditor()).setFocusTraversalKeysEnabled(true);
            textField.addFocusListener(this);
            addFocusListener(this);
        }
    }

    /**
     * Creates a new instance of ColorCombo
     * 
     * @param usedForEditor
     *            不用作表格中的编辑器时,参数usedForEditor 传入为 false
     */
    public CalendarCombo(boolean usedForEditor) {
        super();
        this.usedForEditor = usedForEditor;
        calendarComboBoxUI = new CalendarComboBoxUI(this);
        setUI(calendarComboBoxUI);
        setRenderer(new CalendarRenderer());
        putClientProperty("JComboBox.lightweightKeyboardNavigation", "Lightweight");
        setBackground(Color.white);
        model = new CalendarComboModel(this);
        setModel(model);
        setEditor(new CalendarTextEditor(1));
        setEditable(true);
        addPopupMenuListener(this);
        calendarComboBoxUI.getArrowButton().addMouseListener(this);
        // calendarComboBoxUI.getArrowButton().addFocusListener(this);
        textField = (JTextField) getEditor();
        // textField.addKeyListener(this);
        if (usedForEditor) {
            setFocusTraversalKeysEnabled(false);
            ((JTextField) getEditor()).setFocusTraversalKeysEnabled(false);
            textField.addComponentListener(this);
            addFocusListener(this);
        } else {
            setFocusTraversalKeysEnabled(true);
            ((JTextField) getEditor()).setFocusTraversalKeysEnabled(true);
            textField.addFocusListener(this);
            addFocusListener(this);
        }
    }

    /**
     * 设置一个格式化的日期文本,在组合框中显示出来
     * 
     * @param prmFormatTimeText
     *            格式化的日期文本
     */
    public void setTimeText(
            String prmFormatTimeText) {
        // ((JTextField)getEditor()).setText(prmFormatTimeText);
        setSelectedItem(prmFormatTimeText);
    }

    /**
     * 得到一个格式化的日期文本,目前是处理键盘输入的字符串
     * 
     * @return 格式化的日期文本
     */
    public String getTimeText() {
        return ((JTextField) getEditor()).getText();
    }

    // /** Set the title label of the component.
    // * @param label the title label.
    // */
    // public void setTitleLabel(JLabel label)
    // {
    // super.setTitleLabel(label);
    // ((JTextField) getEditor()).setTitleLabel(label);
    // }
    /**
     * @param event
     *            键盘事件
     * @return 是否由父类来处理
     */
    public boolean canTransfered(
            KeyEvent event) {
        int code = event.getKeyCode();
        int modify = event.getModifiers();
        if (((modify != KeyEvent.ALT_MASK) && (code == KeyEvent.VK_ENTER)) || code == KeyEvent.VK_TAB) {
            return true;
        }
        return false;
    }

    /**
     * @param e
     *            处理键盘事件
     */
    // *
    public void processKeyEvent(
            KeyEvent e) {
        Container parent = getParent();
        if (canTransfered(e) && parent instanceof PIMTable) {
            ((PIMTable) parent).processKeyEvent(e);
        } else if (canTransfered(e) && parent instanceof PIMTableHeader) {
            ((PIMTableHeader) parent).processKeyEvent(e);
        }
        super.processKeyEvent(e);
    }

    // */
    /**
     * 本方法必须超载,以便于加入我们的 model
     *
     * @param anObject
     *            the list object to select; use <code>null</code> to clear the selection
     * @beaninfo preferred: true description: Sets the selected item in the JComboBox.
     */
    public void setSelectedItem(
            Object anObject) {
        getModel().setSelectedItem(anObject);

        // 下面的方法是要使弹出面板显示出所需的年月日
        // TODO:目前似乎无效
        if (anObject != null && anObject instanceof Date) {
            Date tmpDate = (Date) anObject;
            ((CalendarComboBoxUI) getUI()).getPopupComponent().getCalendarComboPane()
                    .setWeek(tmpDate.getYear() + 1900, tmpDate.getMonth() + 1, tmpDate.getDate());
        }
    }

    /**
     * 得到选中项
     * 
     * @return 选中项
     */
    public Object getSelectedItem() {
        return model.getSelectedItem();
    }

    /**
     * 在本组的目前表格时,组合框内容变化会调用本方法,而且编辑器代表会捕获这个动作 的语义事件,从而将本编辑器组件从表格(头)中移除,这是我们所不期望的,所以要终结 本方法的执行
     */
    protected void fireActionEvent() {
        Container parent = getParent();
        if (parent != null && (parent instanceof PIMTable || parent instanceof PIMTableHeader)) {
            return;
        } else {
            super.fireActionEvent();
        }
    }

    /**
     * This method is called when the popup menu is canceled
     * 
     * @param e
     *            弹出事件源
     */
    public void popupMenuCanceled(
            PopupMenuEvent e) {
    }

    /**
     * This method is called before the popup menu becomes invisible Note that a JPopupMenu can become invisible any
     * time
     * 
     * @param e
     *            弹出事件源
     */
    public void popupMenuWillBecomeInvisible(
            PopupMenuEvent e) {
    }

    /**
     * This method is called before the popup menu becomes visible
     * 
     * @param e
     *            弹出事件源
     */
    public void popupMenuWillBecomeVisible(
            PopupMenuEvent e) {
        // calendarComboBoxUI.getPopupComponent().getCalendarComboPane();
        // 一旦要显示出,就加用一个定时器去探测弹出面板是否生成,以便给日期选择区加上鼠标监听器
        calendarComboBoxUI.getPopupComponent().getTimer().start();
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        updateCompVisible();
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        updateCompVisible();
    }

    private void updateCompVisible() {
        // 目前似乎只有这个办法可以保证一点就弹
        // TODO:再弹一次应消失这个问题要解决
        // 任务对话盒中的提醒为无效的时候，点击还会弹出日期的选择框，加入判断避免出现此情况
        if (!calendarComboBoxUI.getPopupComponent().isVisible() && isEnabled()) {
            calendarComboBoxUI.getPopupComponent().setVisible(true);
        } else {
            calendarComboBoxUI.getPopupComponent().setVisible(false);
        }
    }

    //
    // /** Invoked when a key has been pressed.
    // * @param e the key event.
    // */
    // public void keyPressed(KeyEvent e)
    // {
    // int key = e.getKeyCode();
    // if (key == e.VK_ENTER && e.getSource() == getEditor()) //(key == e.VK_ESCAPE || key == e.VK_ENTER))
    // {
    // JTextField tmoEditor = (JTextField)getEditor();
    // String tmpStr = tmoEditor.getText();
    // String [] dayData = null;
    // if(tmpStr.indexOf(CustOpts.BIAS)> 0)
    // {
    // dayData = stringToArray(tmpStr,"/");
    // }
    // else if(tmpStr.indexOf('-')> 0)
    // {
    // dayData = stringToArray(tmpStr,"-");
    // }
    // else
    // {
    // return ;
    // }
    // int [] tmpDays = new int[3];
    // if (dayData.length >= 3)
    // try
    // {
    // tmpDays[0] = Integer.parseInt(dayData[0]);
    // tmpDays[1] = Integer.parseInt(dayData[1]);
    // if(dayData[2].charAt(1) >= '0' && dayData[2].charAt(1) <= '9')
    // {
    // tmpDays[2] = Integer.parseInt(dayData[2].substring(0,1));
    // }
    // else
    // {
    // tmpDays[2] = Integer.parseInt(dayData[2].substring(0,0));
    // }
    //
    // }
    // catch (Exception ex )
    // {
    // ex.printStackTrace();
    // }
    // Date tmpDate = new Date(tmpDays[0],tmpDays[1] -1 ,tmpDays[2]);
    // }
    // 因为本组件的UI和标准的不一致,不超掉会出异常
    // //super.keyPressed(e);
    // }
    /**
     * 解析一个逗号分隔符处理的字符串 getTextAreaData
     * 
     * @return 处理后的字符串数组
     * @param prmString
     *            一个逗号分隔符处理的字符串
     * @param pimDelimiter
     *            分隔符
     */
    public String[] stringToArray(
            String prmString,
            String pimDelimiter) {
        if (prmString != null) {
            // 构建字符串分隔器
            StringTokenizer token = new StringTokenizer(prmString, pimDelimiter);
            int size = token.countTokens();
            // 构建相应容量的字符串数组
            String[] indexes = new String[size];
            size = 0;
            // 循环加入,去掉空格的
            while (token.hasMoreTokens()) {
                indexes[size] = token.nextToken().trim();
                size++;
            }
            return indexes;
        } else {
            return null;
        }
    }

    /**
     * Invoked when a component gains the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusGained(
            FocusEvent e) {
        if (e.getSource() == this) {
            SwingUtilities.invokeLater(this);
        }
    }

    /**
     * Invoked when a component loses the keyboard focus.
     * 
     * @param e
     *            焦点事件
     */
    public void focusLost(
            FocusEvent e) {
        CalendarCombo tmpCombo;
        Object tmpSour = e.getSource();
        if (tmpSour instanceof CalendarCombo) {
            tmpCombo = (CalendarCombo) tmpSour;
        } else {
            tmpCombo = (CalendarCombo) ((JTextField) tmpSour).getParent();
        }

        if (tmpCombo == null) {
            return;
        }
        if (tmpCombo.getParent() instanceof PIMTable || tmpCombo.getParent() instanceof PIMTableHeader) {
        } else {
            // model.forceFireItemChangedEvent();
        }
        // */
        // model.forceFireItemChangedEvent();
    }

    /**
     * Invoked when the component has been made invisible.
     * 
     * @param e
     *            组件变化事件源
     */
    public void componentHidden(
            ComponentEvent e) {
        // ***/A.s("8-Q make pigs dashing !!! componentHidden");//*/
    }

    /**
     * Invoked when the component's position changes.
     * 
     * @param e
     *            组件变化事件源
     */
    public void componentMoved(
            ComponentEvent e) {
        // ***/A.s("8-Q make pigs dashing !!! componentMoved");//*/
    }

    /**
     * Invoked when the component's size changes.
     * 
     * @param e
     *            组件变化事件源
     */
    public void componentResized(
            ComponentEvent e) {
        if (e.getSource() == getEditor() && this.getParent() instanceof PIMTable
                || this.getParent() instanceof PIMTableHeader) {
            SwingUtilities.invokeLater(this);
        }
        // ***/A.s("8-Q make pigs dashing !!! componentResized");//*/
    }

    /**
     * Invoked when the component has been made visible.
     * 
     * @param e
     *            组件变化事件源
     */
    public void componentShown(
            ComponentEvent e) {
        // ***/A.s("8-Q make pigs dashing !!! componentShown");//*/
    }

    /**
     * 设置小时分钟字符串
     * 
     * @param prmTime
     *            表示小时分钟的字符串,如"10:30"
     */
    public void setTime(
            String prmTime) {
        time = prmTime;
    }

    /**
     * 得到小时分钟
     * 
     * @return 表示小时分钟的字符串,如"10:30"
     */
    public String getTime() {
        return time;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
     */
    public void keyPressed(
            KeyEvent e) {
        // XXX Auto-generated method stub
        // super.keyPressed(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
     */
    public void keyReleased(
            KeyEvent e) {
        // XXX Auto-generated method stub
        // super.keyReleased(e);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
     */
    public void keyTyped(
            KeyEvent e) {
        // XXX Auto-generated method stub
        // super.keyTyped(e);
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used to create a thread, starting the thread
     * causes the object's <code>run</code> method to be called in that separately executing thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may take any action whatsoever.
     *
     * @see java.lang.Thread#run()
     */
    public void run() {
        ((JTextField) getEditor()).requestFocus();
    }

    boolean usedForEditor;// 是否用于表格编辑器组件
    private CalendarComboModel model;// 本组件的数据模型
    private CalendarComboBoxUI calendarComboBoxUI;// 本组件的UI
    private String time;// 小时和分钟的字符串
    JTextField textField;// 定制的编辑器

}
