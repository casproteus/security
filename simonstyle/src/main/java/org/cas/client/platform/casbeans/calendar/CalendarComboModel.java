package org.cas.client.platform.casbeans.calendar;

import java.util.Date;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.JTextField;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.EDate;
import org.cas.client.platform.casutil.EDaySet;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.MonthConstant;

class CalendarComboModel extends AbstractListModel implements ComboBoxModel {
    /**
     * Creates a new instance of ColorComboModel
     * 
     * @param combo
     *            传入引用
     */
    public CalendarComboModel(CalendarCombo combo) {
        // 保存一下 combo
        combobox = combo;
    }

    /**
     * Returns the value at the specified index.
     * 
     * @param index
     *            the requested index
     * @return the value at <code>index</code>
     */
    public Object getElementAt(
            int index) {
        return processSelectionItem();
    }

    /**
     * Returns the selected item
     * 
     * @return The selected item or <code>null</code> if there is no selection
     */
    public Object getSelectedItem() {
        return processSelectionItem();
    }

    /**
     * 处理选中项,主要是字符串和日期
     * 
     * @return 选中的日期
     */
    private Object processSelectionItem() {
        // 为空或为日期型就正常处理
        if (selectionItem == null || selectionItem instanceof Date) {
            return selectionItem;
        }
        // 字符型要试着解析一下
        else if (selectionItem instanceof String) {
            String tmpStr = (String) selectionItem;
            String[] dayData = null;
            // 处理"/"可解析标志
            if (tmpStr.indexOf(CustOpts.BIAS) > 0) {
                dayData = combobox.stringToArray(tmpStr, "/");
            }
            // 处理"-"可解析标志
            else if (tmpStr.indexOf('-') > 0) {
                dayData = combobox.stringToArray(tmpStr, "-");
            }
            // 没有就返回空
            else {
                return null;
            }
            int[] tmpDays = new int[3];
            // 有三组才进行
            if (dayData.length >= 3) {
                try {
                    // 必须一个一个处理
                    tmpDays[0] = Integer.parseInt(dayData[0]);
                    tmpDays[1] = Integer.parseInt(dayData[1]);
                    // 最后一个的第二字符更特殊
                    // 大于10
                    if (dayData[2].charAt(1) >= '0' && dayData[2].charAt(1) <= '9') {
                        tmpDays[2] = Integer.parseInt(dayData[2].substring(0, 2));
                    }
                    // 小于10;
                    else {
                        tmpDays[2] = Integer.parseInt(dayData[2].substring(0, 1));
                    }
                } catch (Exception ex) {
                    // ex.printStackTrace();
                }
                // 能过就返回日期型
                Date tmpDate = new Date(tmpDays[0] - 1900, tmpDays[1] - 1, tmpDays[2]);

                return tmpDate;
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     * Returns the length of the list.
     * 
     * @return the length of the list
     */
    public int getSize() {
        return 1;
    }

    /**
     * 强制派发事件
     */
    void forceFireItemChangedEvent() {
        // 日期型就格式化一下
        if (selectionItem != null && selectionItem instanceof Date) {
            Date tmpDate = (Date) selectionItem;
            // 生成文本给编辑器用
            StringBuffer sb =
                    new StringBuffer().append(tmpDate.getYear() + 1900).append('-').append(tmpDate.getMonth() + 1)
                            .append('-').append(tmpDate.getDate()).append(' ');
            String text = (sb.append('(').append(MonthConstant.WEEKDAYS[tmpDate.getDay()]).append(')').toString());

            // 下面是给弹出的日期选择区用的
            EDate[] tmpEDate = new EDate[1];
            tmpEDate[0] = new EDate(tmpDate.getYear() + 1900, tmpDate.getMonth(), tmpDate.getDate());
            EDaySet nowSet = new EDaySet(tmpEDate);
            // 可能会有一个空指针异常,不过我后来没有查出,先分一下,便于下次出现时工作
            CalendarComboBoxUI tmpUI = (CalendarComboBoxUI) combobox.getUI();
            // 2003.10.28 我查出来了
            if (tmpUI == null) {
                return;
            }
            CalendarBasicPopup popup = tmpUI.getPopupComponent();
            if (popup.isVisible()) {
                popup.getCalendarComboPane().setDaySet(nowSet);
            }

            fireContentsChanged(this, 0, 0);
            ((JTextField) combobox.getEditor()).setText(text);
        }
        // 是字符串型就不管
        else if (selectionItem != null && selectionItem instanceof String) {
            String tmpStr = (String) selectionItem;

            // 当然,能处理还是处理一下
            // 有以下两种情况便可进行
            if (tmpStr.indexOf(CustOpts.BIAS) > 0 || tmpStr.indexOf('-') > 0) {
                String[] dayData = null;
                // 可解析标志
                boolean canParser = false;
                // 处理"/"可解析标志
                if (tmpStr.indexOf(CustOpts.BIAS) > 0) {
                    dayData = combobox.stringToArray(tmpStr, "/");
                    canParser = true;
                }
                // 处理"-"可解析标志
                else if (tmpStr.indexOf('-') > 0) {
                    dayData = combobox.stringToArray(tmpStr, "-");
                    canParser = true;
                }
                int[] tmpDays = new int[3];
                // 有三组才进行
                if (canParser && dayData.length >= 3) {
                    try {
                        // 必须一个一个处理
                        tmpDays[0] = Integer.parseInt(dayData[0]);
                        tmpDays[1] = Integer.parseInt(dayData[1]);
                        // 最后一个的第二字符更特殊
                        // 大于10
                        if (dayData[2].charAt(1) >= '0' && dayData[2].charAt(1) <= '9') {
                            tmpDays[2] = Integer.parseInt(dayData[2].substring(0, 2));
                        }
                        // 小于10;
                        else {
                            tmpDays[2] = Integer.parseInt(dayData[2].substring(0, 1));
                        }
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                    }
                    Date tmpDate = new Date(tmpDays[0] - 1900, tmpDays[1] - 1, tmpDays[2]);
                    selectionItem = tmpDate;
                    // 生成文本给编辑器用
                    StringBuffer sb =
                            new StringBuffer().append(tmpDate.getYear() + 1900).append('-')
                                    .append(tmpDate.getMonth() + 1).append('-').append(tmpDate.getDate()).append(' ');
                    String text =
                            (sb.append('(').append(MonthConstant.WEEKDAYS[tmpDate.getDay()]).append(')').toString());

                    // 下面是给弹出的日期选择区用的
                    EDate[] tmpEDate = new EDate[1];
                    tmpEDate[0] = new EDate(tmpDate.getYear() + 1900, tmpDate.getMonth(), tmpDate.getDate());
                    EDaySet nowSet = new EDaySet(tmpEDate);
                    // 这样便可设置一下:
                    ((CalendarComboBoxUI) combobox.getUI()).getPopupComponent().getCalendarComboPane()
                            .setDaySet(nowSet);
                    fireContentsChanged(this, 0, 0);
                    // 编辑器的处理
                    ((JTextField) combobox.getEditor()).setText(text);
                    return;
                }
            }
            // ((JTextField)combobox.getEditor()).setText((String)anItem);
            fireContentsChanged(this, 0, 0);
            ((JTextField) combobox.getEditor()).setText((String) tmpStr);
        }
    }

    /**
     * Set the selected item. The implementation of this method should notify all registered
     * <code>ListDataListener</code>s that the contents have changed.
     *
     * @param anItem
     *            the list object to select or <code>null</code> to clear the selection
     */
    public void setSelectedItem(
            Object anItem) {
        // 日期型就格式化一下
        if (anItem != null && anItem instanceof Date) {
            // 先放一下
            selectionItem = anItem;

            Date tmpDate = (Date) anItem;
            // 生成文本给编辑器用
            StringBuffer sb =
                    new StringBuffer().append(tmpDate.getYear() + 1900).append('-').append(tmpDate.getMonth() + 1)
                            .append('-').append(tmpDate.getDate()).append(CASUtility.SPACE);
            String text =
                    (sb.append(CASUtility.LEFT_BRACKET).append(MonthConstant.WEEKDAYS[tmpDate.getDay()])
                            .append(CASUtility.RIGHT_BRACKET).toString());

            // 下面是给弹出的日期选择区用的
            EDate[] tmpEDate = new EDate[1];
            tmpEDate[0] = new EDate(tmpDate.getYear() + 1900, tmpDate.getMonth(), tmpDate.getDate());
            EDaySet nowSet = new EDaySet(tmpEDate);

            ((CalendarComboBoxUI) combobox.getUI()).getPopupComponent().getCalendarComboPane().setDaySet(nowSet);

            // ((JTextField)combobox.getEditor()).setText(text);
            fireContentsChanged(this, 0, 0);
            ((JTextField) combobox.getEditor()).setText(text);
        }

        // 是字符串型就不管
        else if (anItem != null && anItem instanceof String) {
            selectionItem = anItem;
            String tmpStr = (String) selectionItem;

            // 当然,能处理还是处理一下
            // 有以下两种情况便可进行
            if (tmpStr.indexOf(CustOpts.BIAS) > 0 || tmpStr.indexOf('-') > 0) {
                String[] dayData = null;
                // 可解析标志
                boolean canParser = false;
                // 处理"/"可解析标志
                if (tmpStr.indexOf(CustOpts.BIAS) > 0) {
                    dayData = combobox.stringToArray(tmpStr, "/");
                    canParser = true;
                }
                // 处理"-"可解析标志
                else if (tmpStr.indexOf('-') > 0) {
                    dayData = combobox.stringToArray(tmpStr, "-");
                    canParser = true;
                }
                int[] tmpDays = new int[3];
                // 有三组才进行
                if (canParser && dayData.length >= 3) {
                    try {
                        // 必须一个一个处理
                        tmpDays[0] = Integer.parseInt(dayData[0]);
                        tmpDays[1] = Integer.parseInt(dayData[1]);
                        // 最后一个的第二字符更特殊
                        // 大于10
                        if (dayData[2].charAt(1) >= '0' && dayData[2].charAt(1) <= '9') {
                            tmpDays[2] = Integer.parseInt(dayData[2].substring(0, 2));
                        }
                        // 小于10;
                        else {
                            tmpDays[2] = Integer.parseInt(dayData[2].substring(0, 1));
                        }
                    } catch (Exception ex) {
                        // ex.printStackTrace();
                    }
                    Date tmpDate = new Date(tmpDays[0] - 1900, tmpDays[1] - 1, tmpDays[2]);
                    selectionItem = tmpDate;
                    // 生成文本给编辑器用
                    StringBuffer sb =
                            new StringBuffer().append(tmpDate.getYear() + 1900).append('-')
                                    .append(tmpDate.getMonth() + 1).append('-').append(tmpDate.getDate())
                                    .append(CASUtility.SPACE);
                    String text =
                            (sb.append(CASUtility.LEFT_BRACKET).append(MonthConstant.WEEKDAYS[tmpDate.getDay()])
                                    .append(CASUtility.RIGHT_BRACKET).toString());

                    // 下面是给弹出的日期选择区用的
                    EDate[] tmpEDate = new EDate[1];
                    tmpEDate[0] = new EDate(tmpDate.getYear() + 1900, tmpDate.getMonth(), tmpDate.getDate());
                    EDaySet nowSet = new EDaySet(tmpEDate);
                    // 这样便可设置一下:
                    ((CalendarComboBoxUI) combobox.getUI()).getPopupComponent().getCalendarComboPane()
                            .setDaySet(nowSet);
                    if (!(tmpDate.equals(getSelectedItem()))) {
                        fireContentsChanged(this, 0, 0);
                    }
                    // 编辑器的处理
                    ((JTextField) combobox.getEditor()).setText(text);
                    return;
                }
            }
            // ((JTextField)combobox.getEditor()).setText((String)anItem);
            fireContentsChanged(this, 0, 0);
            ((JTextField) combobox.getEditor()).setText((String) anItem);
        }
    }

    /**
     * 本例中就一个可选项
     */
    private Object selectionItem;
    /**
     * 本组件的引用
     */
    private CalendarCombo combobox;
}
