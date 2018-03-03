package org.cas.client.platform.cascontrol.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.cas.client.platform.casbeans.calendar.CalendarCombo;
import org.cas.client.platform.casbeans.group.PIMButtonGroup;
import org.cas.client.platform.casbeans.group.PIMButtonGroupListener;
import org.cas.client.platform.casbeans.textfield.LimitedIntTextField;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.PaneConsts;
import org.cas.client.resource.international.TaskDialogConstant;

public class AgainCycleDialog extends JDialog implements PIMButtonGroupListener, ActionListener, KeyListener,
        ItemListener, ComponentListener {
    /**
     * Creates a new instance of AgainCycleDialog
     * 
     * @param prmDialog
     *            :
     * @param prmIsTask
     *            :
     * @param prmHashtable
     *            :
     */
    public AgainCycleDialog(JDialog prmDialog, boolean prmIsTask, Hashtable prmHashtable) {
        isTask = prmIsTask;
        contentTable = prmHashtable;

        setTitle(PaneConsts.REPETITIONDEFINE);// "设定重复周期"
        setBounds((CustOpts.SCRWIDTH - 400) / 2, (CustOpts.SCRHEIGHT - 300) / 2, 400, 300); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 实例化各个组件====================================
        rivetDatePanel = new JPanel();
        ok = new JButton(TaskDialogConstant.OK);
        cancel = new JButton(TaskDialogConstant.CANCEL);

        modePanel = new JPanel();
        detailPanel = new JPanel();
        repeatPanel = new JPanel();

        dateGroup = new PIMButtonGroup();
        againBoundGroup = new PIMButtonGroup();

        dayRadioButton = new JRadioButton(TaskDialogConstant.ACCORD_DAY, false); // 按天
        weekRadioButton = new JRadioButton(TaskDialogConstant.ACCORD_WEEK, false);// 按周
        monthRadioButton = new JRadioButton(TaskDialogConstant.ACCORD_MONTH, false); // 按月
        yearRadioButton = new JRadioButton(TaskDialogConstant.ACCORD_YEAR, false); // 按年
        separator = new JSeparator(JSeparator.VERTICAL);

        startLabel = new JLabel(TaskDialogConstant.START);
        calendarstartCombo = new CalendarCombo();
        endDateButton = new JRadioButton(TaskDialogConstant.NONE_END_DATE, false);
        againButton = new JRadioButton(TaskDialogConstant.ANAIN, false);
        limitNumberField = new LimitedIntTextField(100);
        numberLabel = new JLabel(TaskDialogConstant.END_TIMES);
        endWithButton = new JRadioButton(TaskDialogConstant.END_WITH, false);
        endTimeCombo = new CalendarCombo();

        // 属性设置====================================
        modePanel.setLayout(null);
        detailPanel.setLayout(null);
        repeatPanel.setLayout(null);
        rivetDatePanel.setLayout(null);
        detailPanel.setBorder(null);
        dayRadioButton.setMnemonic('D');
        weekRadioButton.setMnemonic('W');
        monthRadioButton.setMnemonic('M');
        yearRadioButton.setMnemonic('Y');
        endWithButton.setMnemonic('B');
        endDateButton.setMnemonic('O');
        againButton.setMnemonic('F');
        startLabel.setDisplayedMnemonic('S');
        startLabel.setLabelFor(calendarstartCombo);
        repeatPanel.setBorder(new TitledBorder(new EtchedBorder(), TaskDialogConstant.AGAIN_BOUND, 4, 2,
                CustOpts.custOps.getFontOfDefault()));
        rivetDatePanel.setBorder(new TitledBorder(new EtchedBorder(), TaskDialogConstant.RIVET_DATE_MODE, 4, 2,
                CustOpts.custOps.getFontOfDefault()));

        // 布局计算============================================================================
        reLayout();

        // 内容显示============================================================================
        Object tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.MODE_INDEX));
        modeIndex = tmpObj == null ? 0 : ((Byte) tmpObj).byteValue();
        dateGroup.setSelectIndex(modeIndex);
        if (modeIndex == 0) {
            initDayPanel();
        } else if (modeIndex == 1) {
            initWeekPanel();
        } else if (modeIndex == 2) {
            initMonthPanel();
        } else if (modeIndex == 3) {
            initYearPanel();
        }

        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.START_DATE));
        Date startDate = tmpObj == null ? currentDate : ((Date) tmpObj);
        calendarstartCombo.setSelectedItem(startDate);
        //
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.END_INDEX));
        int endIndex = tmpObj == null ? 0 : ((Byte) tmpObj).byteValue();
        //
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.REPEAT_NUMBER));
        int repeatNum = tmpObj == null ? 1 : ((Integer) tmpObj).intValue();

        limitNumberField.setText(Integer.toString(endIndex == 1 ? repeatNum : 10));
        //
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.END_DATE));
        Date endDate = tmpObj == null ? currentDate : ((Date) tmpObj);

        endTimeCombo.setSelectedItem(endDate);

        // 搭建============================================================
        dateGroup.add(dayRadioButton);
        dateGroup.add(weekRadioButton);
        dateGroup.add(monthRadioButton);
        dateGroup.add(yearRadioButton);
        modePanel.add(dayRadioButton);
        modePanel.add(weekRadioButton);
        modePanel.add(monthRadioButton);
        modePanel.add(yearRadioButton);
        modePanel.add(separator);
        rivetDatePanel.add(modePanel);
        rivetDatePanel.add(detailPanel);

        repeatPanel.add(startLabel);
        repeatPanel.add(calendarstartCombo);
        repeatPanel.add(endDateButton);
        repeatPanel.add(againButton);
        repeatPanel.add(numberLabel);
        repeatPanel.add(endWithButton);
        repeatPanel.add(endTimeCombo);
        repeatPanel.add(limitNumberField);
        againBoundGroup.add(endDateButton);
        againBoundGroup.add(againButton);
        againBoundGroup.add(endWithButton);
        againBoundGroup.setSelectIndex(endIndex);

        getContentPane().add(rivetDatePanel);
        getContentPane().add(repeatPanel);
        getContentPane().add(ok);
        getContentPane().add(cancel);

        // 加监听器========================================================
        ok.addActionListener(this);
        cancel.addActionListener(this);
        limitNumberField.addKeyListener(this);
        ((JTextField) (endTimeCombo.getEditor())).addKeyListener(this);
        endTimeCombo.addItemListener(this);
        againBoundGroup.addEButtonGroupListener(this);
        dateGroup.addEButtonGroupListener(this);
        getContentPane().addComponentListener(this);
    }

    /**
     * Invoked when selection changed.
     * 
     * @param group
     *            the button group whose selection changed.
     * @param select
     *            the group selected index.
     */
    public void selected(
            PIMButtonGroup group,
            int select) {
        if (group == dateGroup) {
            if (select == 0) {
                detailPanel.removeAll();
                initDayPanel();
            } else if (select == 1) {
                detailPanel.removeAll();
                initWeekPanel();
            } else if (select == 2) {
                detailPanel.removeAll();
                initMonthPanel();
            } else if (select == 3) {
                detailPanel.removeAll();
                initYearPanel();
            }
            detailPanel.updateUI();
        } else if (group == dayButtonGroup) {
            if (select == 0 && isTask) {
                tastFinishTextField.setText("1");
                tastFinishTextField.updateUI();
            } else if (select == 1) {
                dayIntTextField.setText("1");
                dayIntTextField.updateUI();
                if (isTask) {
                    tastFinishTextField.setText("1");
                    tastFinishTextField.updateUI();
                }
            } else if (select == 2) {
                dayIntTextField.setText("1");
                dayIntTextField.updateUI();
            }
        } else if (group == weekButtonGroup) {
            if (select == 0 && isTask) {
                startWeekField.setText("1");
                startWeekField.updateUI();
            } else if (select == 1) {
                spanWeekTextField.setText("1");
                spanWeekTextField.updateUI();
                int week = currentDate.getDay();
                monday.setSelected(week == 1);
                tuesday.setSelected(week == 2);
                wednsday.setSelected(week == 3);
                thursday.setSelected(week == 4);
                firday.setSelected(week == 5);
                sateday.setSelected(week == 6);
                sunday.setSelected(week == 7);
                // detailPanel.updateUI();
            }
        } else if (group == monthButtonGroup) {
            needItemEvent = false;
            if (select == 0) {
                numMonField.setText("1");
                numMonField.updateUI();
                monthCombo.setSelectedIndex(0);
                int week = currentDate.getDay();
                int comboIndex = week == 7 ? 3 : week + 3;
                weekCombo.setSelectedIndex(comboIndex);
                int date = currentDate.getDate();
                monthCombo.setSelectedIndex(((date % 7) == 0) ? (date / 7 - 1) : (date / 7));
                if (isTask) {
                    numMonthStart.setText("1");
                    numMonthStart.updateUI();
                }
            } else if (select == 1) {
                perMonthField.setText("1");
                perMonthField.updateUI();
                limitDayField.setText(Integer.toString(currentDate.getDate()));
                limitDayField.updateUI();
                if (isTask) {
                    numMonthStart.setText("1");
                    numMonthStart.updateUI();
                }
            } else if (select == 2) {
                perMonthField.setText("1");
                perMonthField.updateUI();
                limitDayField.setText(Integer.toString(currentDate.getDate()));
                limitDayField.updateUI();
                numMonField.setText("1");
                numMonField.updateUI();
                monthCombo.setSelectedIndex(0);
                int week = currentDate.getDay();
                int comboIndex = week == 7 ? 3 : week + 3;
                weekCombo.setSelectedIndex(comboIndex);
                int date = currentDate.getDate();
                monthCombo.setSelectedIndex(((date % 7) == 0) ? (date / 7 - 1) : (date / 7));
            }
            needItemEvent = true;
        } else if (group == yearButtonGroup) {
            needItemEvent = false;
            if (select == 0) {
                monthyearComboBox.setSelectedIndex(currentDate.getMonth());
                int week = currentDate.getDay();
                int comboIndex = week == 7 ? 3 : week + 3;
                weekCombo1.setSelectedIndex(comboIndex);
                int date = currentDate.getDate();
                monthCombo1.setSelectedIndex(((date % 7) == 0) ? (date / 7 - 1) : (date / 7));
                if (isTask) {
                    limitYear.setText("1");
                    limitYear.updateUI();
                }
            } else if (select == 1) {
                monthYearCombo.setSelectedIndex(currentDate.getMonth());
                limitedDay.setText(Integer.toString(currentDate.getDate()));
                limitedDay.updateUI();
                if (isTask) {
                    limitYear.setText("1");
                    limitYear.updateUI();
                }
            } else if (select == 2) {
                monthYearCombo.setSelectedIndex(currentDate.getMonth());
                limitedDay.setText(Integer.toString(currentDate.getDate()));
                limitedDay.updateUI();
                monthyearComboBox.setSelectedIndex(currentDate.getMonth());
                int week = currentDate.getDay();
                int comboIndex = week == 7 ? 3 : week + 3;
                weekCombo1.setSelectedIndex(comboIndex);
                int date = currentDate.getDate();
                monthCombo1.setSelectedIndex(((date % 7) == 0) ? (date / 7 - 1) : (date / 7));
            }
            needItemEvent = true;
        } else if (group == againBoundGroup) {
            needItemEvent = false;
            if (select == 0) {
                limitNumberField.setText("10");
                endTimeCombo.setSelectedItem(currentDate);
            } else if (select == 1) {
                endTimeCombo.setSelectedItem(currentDate);
            } else if (select == 2) {
                limitNumberField.setText("10");
            }
            needItemEvent = true;
        }
    }

    /**
     * Invoked when the component's size changes.
     */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /**
     * Invoked when the component's position changes.
     */
    public void componentMoved(
            ComponentEvent e) {
    };

    /**
     * Invoked when the component has been made visible.
     */
    public void componentShown(
            ComponentEvent e) {
    };

    /**
     * Invoked when the component has been made invisible.
     */
    public void componentHidden(
            ComponentEvent e) {
    };

    /**
     * When OK clicked What happened
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        if (e.getSource() == ok) {
            /*
             * if (dateGroup.getSelectIndex() == 1 && weekButtonGroup.getSelectIndex() == 0 && !(monday.isSelected() ||
             * tuesday.isSelected() || wednsday.isSelected() || thursday.isSelected() || firday.isSelected() ||
             * sateday.isSelected() || sunday.isSelected())) {
             * emo.pim.util.ErrorDialog.showErrorDialog(MessageCons.C10758); return; } if (false) {
             * ErrorDialog.showErrorDialog(MessageCons.C10759); //必须输入一个值 }
             */
            int result = checkValue();
            if (result == 1) {
                SOptionPane.showErrorDialog(MessageCons.C10758);
                return;
            } else if (result == 2) {
                SOptionPane.showErrorDialog(MessageCons.C10759); // 必须输入一个合法值
                return;
            }
            putHashtableValue(contentTable);
            dispose();
        }
    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
     * pressed event.
     */
    public void keyPressed(
            KeyEvent e) {
        Object source = e.getSource();
        int keyCode = e.getKeyCode();
        if (source == endTimeCombo.getEditor()) {
            needItemEvent = false;
            againBoundGroup.setSelectIndex(2);
            selected(againBoundGroup, 2);
            needItemEvent = true;
        }
        if (!(keyCode >= KeyEvent.VK_0 && keyCode <= KeyEvent.VK_9)) {
            return;
        }
        if (source == dayIntTextField) {
            dayButtonGroup.setSelectIndex(0);
            selected(dayButtonGroup, 0);
        } else if (source == tastFinishTextField) {
            dayButtonGroup.setSelectIndex(2);
            selected(dayButtonGroup, 2);
        } else if (source == spanWeekTextField) {
            weekButtonGroup.setSelectIndex(0);
            selected(weekButtonGroup, 0);
        } else if (source == startWeekField) {
            weekButtonGroup.setSelectIndex(1);
            selected(weekButtonGroup, 1);
        } else if (source == perMonthField || source == limitDayField) {
            monthButtonGroup.setSelectIndex(0);
            selected(monthButtonGroup, 0);
        } else if (source == numMonField) {
            monthButtonGroup.setSelectIndex(1);
            selected(monthButtonGroup, 1);
        } else if (source == numMonthStart) {
            monthButtonGroup.setSelectIndex(2);
            selected(monthButtonGroup, 2);
        } else if (source == limitedDay) {
            yearButtonGroup.setSelectIndex(0);
            selected(yearButtonGroup, 0);
        } else if (source == limitYear) {
            yearButtonGroup.setSelectIndex(2);
            selected(yearButtonGroup, 2);
        } else if (source == limitNumberField) {
            againBoundGroup.setSelectIndex(1);
            selected(againBoundGroup, 1);
        }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
     * released event.
     */
    public void keyReleased(
            KeyEvent e) {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
     * event.
     */
    public void keyTyped(
            KeyEvent e) {
    }

    /**
     * Invoked when an item has been selected or deselected by the user. The code written for this method performs the
     * operations that need to occur when an item is selected (or deselected).
     */
    public void itemStateChanged(
            ItemEvent e) {
        if (!needItemEvent) {
            return;
        }
        Object source = e.getSource();
        if ((source == monthCombo || source == weekCombo) && monthButtonGroup != null) {
            monthButtonGroup.setSelectIndex(1);
            selected(monthButtonGroup, 1);
        } else if (source == monthYearCombo && yearButtonGroup != null) {
            yearButtonGroup.setSelectIndex(0);
            selected(yearButtonGroup, 0);
        } else if ((source == monthyearComboBox || source == monthCombo1 || source == weekCombo1)
                && yearButtonGroup != null) {
            yearButtonGroup.setSelectIndex(1);
            selected(yearButtonGroup, 1);
        } else if (source == endTimeCombo) {
            if (againBoundGroup != null && e.getStateChange() == ItemEvent.SELECTED) {
                againBoundGroup.setSelectIndex(2);
                selected(againBoundGroup, 2);
                endWithButton.grabFocus();
            }
        }
    }

    /**
     * 重载EDialog方法，打断连接，为gc做准备
     */
    public void extraAction() {
        ok.removeActionListener(this);
        if (dateGroup != null) {
            dateGroup.removeEButtonGroupListener(this);
        }
        if (dayButtonGroup != null) {
            dayButtonGroup.removeEButtonGroupListener(this);
            dayIntTextField.removeKeyListener(this);
            if (isTask) {
                tastFinishTextField.removeKeyListener(this);
            }
        }
        if (weekButtonGroup != null) {
            weekButtonGroup.removeEButtonGroupListener(this);
            spanWeekTextField.removeKeyListener(this);
            if (isTask) {
                startWeekField.removeKeyListener(this);
            }
        }
        if (monthButtonGroup != null) {
            monthButtonGroup.removeEButtonGroupListener(this);
            perMonthField.removeKeyListener(this);
            limitDayField.removeKeyListener(this);
            numMonField.removeKeyListener(this);
            monthCombo.removeItemListener(this);
            weekCombo.removeItemListener(this);
            if (isTask) {
                numMonthStart.removeKeyListener(this);
            }
        }
        if (yearButtonGroup != null) {
            yearButtonGroup.removeEButtonGroupListener(this);
            limitedDay.removeKeyListener(this);
            monthYearCombo.removeItemListener(this);
            monthyearComboBox.removeItemListener(this);
            monthCombo1.removeItemListener(this);
            weekCombo1.removeItemListener(this);
            if (isTask) {
                limitYear.removeKeyListener(this);
            }
        }
        if (againBoundGroup != null) {
            againBoundGroup.removeEButtonGroupListener(this);
            limitNumberField.removeKeyListener(this);
        }
    }

    /**
     * 保存数据
     */
    public void putHashtableValue(
            Hashtable prmHashTable) {
        byte tmpModeIndex = (byte) dateGroup.getSelectIndex();
        prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.MODE_INDEX), new Byte(tmpModeIndex));
        if (tmpModeIndex == 0) {
            byte dayIndex = (byte) dayButtonGroup.getSelectIndex();
            prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX), new Byte(dayIndex));
            if (dayIndex == 0) {
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(dayIntTextField.getValue()));
            } else if (dayIndex == 2) {
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(tastFinishTextField.getValue()));
            }
        }
        if (tmpModeIndex == 1) {
            // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.MODE_INDEX),new Byte(1));
            byte weekIndex = (byte) weekButtonGroup.getSelectIndex();
            prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX), new Byte(weekIndex));
            if (weekIndex == 0) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x1));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(spanWeekTextField.getValue()));
                byte weekCheckBoxValue = 0;
                // sunday
                if (sunday.isSelected()) {
                    weekCheckBoxValue = (byte) (weekCheckBoxValue | 0x40);
                }
                // monday
                if (monday.isSelected()) {
                    weekCheckBoxValue = (byte) (weekCheckBoxValue | 0x1);
                }
                // tuesday
                if (tuesday.isSelected()) {
                    weekCheckBoxValue = (byte) (weekCheckBoxValue | 0x2);
                }
                // wednsday
                if (wednsday.isSelected()) {
                    weekCheckBoxValue = (byte) (weekCheckBoxValue | 0x4);
                }
                // thursday
                if (thursday.isSelected()) {
                    weekCheckBoxValue = (byte) (weekCheckBoxValue | 0x8);
                }
                // firday
                if (firday.isSelected()) {
                    weekCheckBoxValue = (byte) (weekCheckBoxValue | 0x10);
                }
                // sateday
                if (sateday.isSelected()) {
                    weekCheckBoxValue = (byte) (weekCheckBoxValue | 0x20);
                }
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.WEEK_FLAGS), new Byte(weekCheckBoxValue));
            } else if (weekIndex == 1) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x2));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(startWeekField.getValue()));
            }
        }
        if (tmpModeIndex == 2) {
            // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.MODE_INDEX),new Byte(2));
            byte monthIndex = (byte) monthButtonGroup.getSelectIndex();
            prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX), new Byte(monthIndex));
            if (monthIndex == 0) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x1));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(perMonthField.getValue()));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.SECOND_NUMBER),
                        PIMPool.pool.getKey(limitDayField.getValue()));
            } else if (monthIndex == 1) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x2));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(numMonField.getValue()));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.SECOND_NUMBER),
                        PIMPool.pool.getKey(monthCombo.getSelectedIndex()));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.THIRD_NUMBER),
                        PIMPool.pool.getKey(weekCombo.getSelectedIndex()));
            } else if (monthIndex == 2) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x4));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(numMonthStart.getValue()));
            }
        }
        if (tmpModeIndex == 3) {
            // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.MODE_INDEX),new Byte(3));
            byte yearIndex = (byte) yearButtonGroup.getSelectIndex();
            prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX), new Byte(yearIndex));
            if (yearIndex == 0) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x1));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(monthYearCombo.getSelectedIndex()));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.SECOND_NUMBER),
                        PIMPool.pool.getKey(limitedDay.getValue()));
            } else if (yearIndex == 1) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x2));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(monthyearComboBox.getSelectedIndex()));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.SECOND_NUMBER),
                        PIMPool.pool.getKey(monthCombo1.getSelectedIndex()));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.THIRD_NUMBER),
                        PIMPool.pool.getKey(weekCombo1.getSelectedIndex()));
            } else if (yearIndex == 2) {
                // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.EVERY_MODE_INDEX),new Byte((byte)0x4));
                prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER),
                        PIMPool.pool.getKey(limitYear.getValue()));
            }
        }
        prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.START_DATE), (Date) (calendarstartCombo.getSelectedItem()));
        byte endIndex = (byte) againBoundGroup.getSelectIndex();
        prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.END_INDEX), new Byte(endIndex));
        // if (endIndex == 0)
        // {
        // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.END_INDEX),new Byte((byte)0x1));
        // }
        // if (endIndex == 1)
        // {
        // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.END_INDEX),new Byte((byte)0x2));
        // }
        // if (endIndex == 2)
        // {
        // prmHashTable.put(PIMPool.pool.getIntegerKey(ModelDBConstants.END_INDEX),new Byte((byte)0x4));
        // }
        prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.REPEAT_NUMBER),
                PIMPool.pool.getKey(limitNumberField.getValue()));
        prmHashTable.put(PIMPool.pool.getKey(ModelDBCons.END_DATE), (Date) (endTimeCombo.getSelectedItem()));
    }

    /**
	 */
    CalendarCombo getStartCalendarCombo() {
        return calendarstartCombo;
    }

    /**
	 */
    CalendarCombo getEndCalendarCombo() {
        return endTimeCombo;
    }

    private void reLayout() {
        cancel.setBounds(getContentPane().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getHeight()
                - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        // 上左区域,用于选择日/周/月/年.
        int raidoButtonWidth =
                10 + CASDialogKit.getMaxWidth(new JComponent[] { dayRadioButton, weekRadioButton, monthRadioButton,
                        yearRadioButton });
        dayRadioButton.setBounds(CustOpts.HOR_GAP, 0, raidoButtonWidth, CustOpts.BTN_HEIGHT);
        weekRadioButton.setBounds(dayRadioButton.getX(),
                dayRadioButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, raidoButtonWidth, CustOpts.BTN_HEIGHT);
        monthRadioButton.setBounds(weekRadioButton.getX(), weekRadioButton.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, raidoButtonWidth, CustOpts.BTN_HEIGHT);
        yearRadioButton.setBounds(monthRadioButton.getX(), monthRadioButton.getY() + CustOpts.BTN_HEIGHT
                + CustOpts.VER_GAP, raidoButtonWidth, CustOpts.BTN_HEIGHT);

        // 上左区域和上右区域中间的分割条.
        separator.setBounds(yearRadioButton.getX() + raidoButtonWidth + CustOpts.HOR_GAP, dayRadioButton.getY(),
                CustOpts.SEP_HEIGHT, yearRadioButton.getY() + CustOpts.BTN_HEIGHT);

        // 上部的三个面板的布局(modePanel和detailPanel是放在rivetDatePanel上的.detailPanel上的内容与左部的redioBox联动.
        rivetDatePanel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - 3 * CustOpts.HOR_GAP,
                yearRadioButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP);
        modePanel.setBounds(CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT, separator.getX() + CustOpts.SEP_HEIGHT,
                yearRadioButton.getY() + CustOpts.BTN_HEIGHT);
        detailPanel.setBounds(modePanel.getX() + modePanel.getWidth(), CustOpts.LBL_HEIGHT, rivetDatePanel.getWidth()
                - modePanel.getX() - modePanel.getWidth() - CustOpts.HOR_GAP, modePanel.getHeight());

        // 底部面板及上面的组件.
        repeatPanel.setBounds(CustOpts.HOR_GAP, rivetDatePanel.getY() + rivetDatePanel.getHeight(),
                rivetDatePanel.getWidth(), ok.getY() - rivetDatePanel.getY() - rivetDatePanel.getHeight()
                        - CustOpts.VER_GAP);

        startLabel.setBounds(CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT, startLabel.getPreferredSize().width,
                CustOpts.LBL_HEIGHT);
        calendarstartCombo.setBounds(startLabel.getX() + startLabel.getWidth() + CustOpts.HOR_GAP, startLabel.getY(),
                100, CustOpts.LBL_HEIGHT);

        endDateButton.setBounds(repeatPanel.getWidth() / 2 + CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT,
                endDateButton.getPreferredSize().width, CustOpts.BTN_HEIGHT);

        againButton.setBounds(endDateButton.getX(), endDateButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                againButton.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        limitNumberField.setBounds(againButton.getX() + againButton.getWidth(), againButton.getY(), 30,
                CustOpts.LBL_HEIGHT);
        numberLabel.setBounds(limitNumberField.getX() + limitNumberField.getWidth() + CustOpts.HOR_GAP,
                limitNumberField.getY(), numberLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);

        endWithButton.setBounds(againButton.getX(), againButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                endWithButton.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        endTimeCombo.setBounds(endWithButton.getX() + endWithButton.getWidth() + CustOpts.HOR_GAP,
                endWithButton.getY(), 100, CustOpts.LBL_HEIGHT);
    }

    /*
     * 按天面板
     * @param prmWidth : 面板宽度
     * @param prmHeight: 面板高度
     */
    private void initDayPanel() {
        // 初始化组件 ---------------------------------------------
        JRadioButton perDayButton = new JRadioButton(TaskDialogConstant.PER, false);// 每多少天
        perDayButton.setMnemonic('V');
        int perDayButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perDayButton });
        perDayButton.setBounds(0, 0, perDayButtonWidth, CustOpts.BTN_HEIGHT);
        detailPanel.add(perDayButton);
        Object tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER));
        int firstLimitDays = tmpObj == null ? 1 : ((Integer) tmpObj).intValue();
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX));
        byte detailModeIndex = tmpObj == null ? 0 : ((Byte) tmpObj).byteValue();
        dayIntTextField = new LimitedIntTextField(1000);
        if (detailModeIndex == 0) {
            dayIntTextField.setText(Integer.toString(firstLimitDays));
        } else {
            dayIntTextField.setText("1");
        }
        dayIntTextField.setBounds(perDayButtonWidth, 0, 30, CustOpts.LBL_HEIGHT);
        detailPanel.add(dayIntTextField);
        dayIntTextField.addKeyListener(this);
        JLabel dayLabel = new JLabel(TaskDialogConstant.DAY);
        dayLabel.setBounds(perDayButtonWidth + dayIntTextField.getWidth() + CustOpts.HOR_GAP, 0,
                detailPanel.getPreferredSize().width - perDayButtonWidth - dayIntTextField.getWidth(),
                CustOpts.LBL_HEIGHT);
        detailPanel.add(dayLabel);

        // 每个工作日
        JRadioButton perworkDayButton = new JRadioButton(TaskDialogConstant.PER_WORK_DAY, false);
        perworkDayButton.setMnemonic('E');
        int perworkDayButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perworkDayButton });
        // A.s("perworkDayButtonWidth : "+perworkDayButtonWidth);
        perworkDayButton.setBounds(0, perDayButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                perworkDayButtonWidth, CustOpts.BTN_HEIGHT);
        detailPanel.add(perworkDayButton);

        // 每当任务完成后第 X 天后重新开始
        JRadioButton perstartDayButton = new JRadioButton(TaskDialogConstant.WHEN_TASK_FINISHED, false);
        perstartDayButton.setMnemonic('G');
        // A.s(" isTask  : "+isTask);
        if (isTask) {
            int perstartDayButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perstartDayButton });
            perstartDayButton.setBounds(0, perworkDayButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    perstartDayButtonWidth, CustOpts.BTN_HEIGHT);
            detailPanel.add(perstartDayButton);
            tastFinishTextField = new LimitedIntTextField(1000);
            // tmpObj = contentTable.get(PIMPool.pool.getIntegerKey(ModelDBConstants.THIRD_NUMBER));
            // int thirdLimitDays = tmpObj == null ? 1 : ((Integer)tmpObj).intValue();
            if (detailModeIndex == 2) {
                tastFinishTextField.setText(Integer.toString(firstLimitDays));
            } else {
                tastFinishTextField.setText("1");
            }
            tastFinishTextField.setBounds(perstartDayButtonWidth, perstartDayButton.getY(), 30, CustOpts.LBL_HEIGHT);
            detailPanel.add(tastFinishTextField);
            tastFinishTextField.addKeyListener(this);
            // 天后重新开始
            JLabel startDayLabel = new JLabel(TaskDialogConstant.DAYS_START_AGAIN);
            startDayLabel.setBounds(perstartDayButtonWidth + tastFinishTextField.getWidth() + CustOpts.HOR_GAP,
                    perstartDayButton.getY(), detailPanel.getPreferredSize().width - perstartDayButtonWidth
                            - tastFinishTextField.getWidth(), CustOpts.LBL_HEIGHT);
            detailPanel.add(startDayLabel);
        }
        dayButtonGroup = new PIMButtonGroup();
        dayButtonGroup.add(perDayButton);
        dayButtonGroup.add(perworkDayButton);
        if (isTask) {
            dayButtonGroup.add(perstartDayButton);
        }
        dayButtonGroup.addEButtonGroupListener(this);
        dayButtonGroup.setSelectIndex(detailModeIndex);
    }

    /*
     * 按周面板
     * @param prmWidth : 面板宽度
     * @param prmHeight: 面板高度
     */
    private void initWeekPanel() {
        // ---------------------------------------------
        // 初始化组件
        //
        JRadioButton spanWeek = new JRadioButton(TaskDialogConstant.AGAIN_SPAN_IS, false);
        spanWeek.setMnemonic('C');
        int spanWeekWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { spanWeek });
        Object tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER));
        int firstLimitWeeks = tmpObj == null ? 1 : ((Integer) tmpObj).intValue();
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX));
        byte detailModeIndex = tmpObj == null ? 0 : ((Byte) tmpObj).byteValue();
        spanWeekTextField = new LimitedIntTextField(100);
        if (detailModeIndex == 0) {
            spanWeekTextField.setText(Integer.toString(firstLimitWeeks));
        } else {
            spanWeekTextField.setText("1");
        }
        // 周后的
        JLabel spanWeekLabel = new JLabel(TaskDialogConstant.AFTER_WEEK);
        spanWeek.setBounds(0, 0, spanWeekWidth, CustOpts.BTN_HEIGHT);
        detailPanel.add(spanWeek);
        spanWeekTextField.setBounds(spanWeekWidth, 0, 30, CustOpts.LBL_HEIGHT);
        detailPanel.add(spanWeekTextField);
        spanWeekTextField.addKeyListener(this);
        spanWeekLabel.setBounds(spanWeekWidth + spanWeekTextField.getWidth() + CustOpts.HOR_GAP, 0, getWidth()
                - (spanWeekWidth + spanWeekTextField.getWidth() + CustOpts.HOR_GAP), CustOpts.BTN_HEIGHT);
        detailPanel.add(spanWeekLabel);

        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.WEEK_FLAGS));
        byte weekFlag = tmpObj == null ? 0 : ((Byte) tmpObj).byteValue();
        if (weekFlag == 0) {
            weekFlag = (byte) (0x1 << (currentDate.getDay() - 1));
        }
        sunday = new JCheckBox();
        sunday.setText(TaskDialogConstant.SUNDAY);
        sunday.setSelected((weekFlag & 0x40) != 0);
        monday = new JCheckBox();
        monday.setText(TaskDialogConstant.MONDAY);
        monday.setSelected((weekFlag & 0x1) != 0);
        tuesday = new JCheckBox();
        tuesday.setText(TaskDialogConstant.TUESDAY);
        tuesday.setSelected((weekFlag & 0x2) != 0);
        wednsday = new JCheckBox();
        wednsday.setText(TaskDialogConstant.WEDNESDAY);
        wednsday.setSelected((weekFlag & 0x4) != 0);
        thursday = new JCheckBox();
        thursday.setText(TaskDialogConstant.THUSDAY);
        thursday.setSelected((weekFlag & 0x8) != 0);
        firday = new JCheckBox();
        firday.setText(TaskDialogConstant.FIRDAY);
        firday.setSelected((weekFlag & 0x10) != 0);
        sateday = new JCheckBox();
        sateday.setText(TaskDialogConstant.SATURDAY);
        sateday.setSelected((weekFlag & 0x20) != 0);
        int checkBoxWidth =
                10 + CASDialogKit.getMaxWidth(new JComponent[] { sunday, monday, tuesday, wednsday, thursday, firday,
                        sateday });
        sunday.setBounds(CustOpts.HOR_GAP, spanWeek.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, checkBoxWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(sunday);
        monday.setBounds(sunday.getX() + checkBoxWidth + CustOpts.HOR_GAP, sunday.getY(), checkBoxWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(monday);
        tuesday.setBounds(monday.getX() + checkBoxWidth + CustOpts.HOR_GAP, monday.getY(), checkBoxWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(tuesday);
        wednsday.setBounds(tuesday.getX() + checkBoxWidth + CustOpts.HOR_GAP, tuesday.getY(), checkBoxWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(wednsday);
        thursday.setBounds(CustOpts.HOR_GAP, sunday.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, checkBoxWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(thursday);
        firday.setBounds(thursday.getX() + checkBoxWidth + CustOpts.HOR_GAP, thursday.getY(), checkBoxWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(firday);
        sateday.setBounds(firday.getX() + checkBoxWidth + CustOpts.HOR_GAP, thursday.getY(), checkBoxWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(sateday);

        JRadioButton perTastRadioButton = new JRadioButton(TaskDialogConstant.WHEN_TASK_FINISHED, false);
        perTastRadioButton.setMnemonic('G');
        if (isTask) {
            //
            int perTastWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perTastRadioButton });
            perTastRadioButton.setBounds(0, sateday.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, perTastWidth,
                    CustOpts.BTN_HEIGHT);
            detailPanel.add(perTastRadioButton);
            startWeekField = new LimitedIntTextField(1000);
            if (detailModeIndex == 1) {
                startWeekField.setText(Integer.toString(firstLimitWeeks));
            } else {
                startWeekField.setText("1");
            }
            startWeekField.setBounds(perTastWidth, perTastRadioButton.getY(), 30, CustOpts.LBL_HEIGHT);
            detailPanel.add(startWeekField);
            startWeekField.addKeyListener(this);
            JLabel startLabel = new JLabel(TaskDialogConstant.WEEK_START_AGAIN);
            startLabel.setBounds(startWeekField.getX() + startWeekField.getWidth() + CustOpts.HOR_GAP,
                    perTastRadioButton.getY(), startLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
            detailPanel.add(startLabel);

        }
        weekButtonGroup = new PIMButtonGroup();
        weekButtonGroup.add(spanWeek);
        if (isTask) {
            weekButtonGroup.add(perTastRadioButton);
        }
        weekButtonGroup.addEButtonGroupListener(this);
        weekButtonGroup.setSelectIndex(detailModeIndex);
        // return detailPanel;
    }

    /*
     * 按月面板
     * @param prmWidth : 面板宽度
     * @param prmHeight: 面板高度
     */
    private void initMonthPanel() {
        // ----------------------------------------------------
        //
        // 第每月每天
        needItemEvent = false;
        Object tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX));
        byte detailModeIndex = tmpObj == null ? 0 : ((Byte) tmpObj).byteValue();
        boolean isFirst = detailModeIndex == 0;
        boolean isSecond = detailModeIndex == 1;
        boolean isThird = detailModeIndex == 2;
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER));
        int firstNum = tmpObj == null ? 1 : ((Integer) tmpObj).intValue();
        JRadioButton perMonthButton = new JRadioButton(TaskDialogConstant.PER, false);
        perMonthButton.setMnemonic('V');
        int perMonthWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perMonthButton });
        perMonthButton.setBounds(0, 0, perMonthWidth, CustOpts.BTN_HEIGHT);
        detailPanel.add(perMonthButton);
        perMonthField = new LimitedIntTextField(100);
        perMonthField.addKeyListener(this);
        perMonthField.setText(isFirst ? Integer.toString(firstNum) : "1");
        perMonthField.setBounds(perMonthWidth, 0, 30, CustOpts.LBL_HEIGHT);
        detailPanel.add(perMonthField);
        JLabel monthNumberLabel = new JLabel(TaskDialogConstant.MONTH_START);
        int monthNumberLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { monthNumberLabel });
        monthNumberLabel.setBounds(perMonthField.getX() + perMonthField.getWidth() + CustOpts.HOR_GAP,
                perMonthButton.getY(), monthNumberLabelWidth, CustOpts.LBL_HEIGHT);
        detailPanel.add(monthNumberLabel);
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.SECOND_NUMBER));
        int secondNum = tmpObj == null ? 1 : ((Integer) tmpObj).intValue();
        limitDayField = new LimitedIntTextField(1000);
        limitDayField.addKeyListener(this);
        limitDayField.setText(isFirst ? Integer.toString(secondNum) : Integer.toString(currentDate.getDate()));
        limitDayField.setBounds(monthNumberLabel.getX() + monthNumberLabelWidth, monthNumberLabel.getY(), 30,
                CustOpts.LBL_HEIGHT);
        detailPanel.add(limitDayField);
        JLabel dayLabel = new JLabel(TaskDialogConstant.DAY);
        dayLabel.setBounds(limitDayField.getX() + limitDayField.getWidth() + CustOpts.HOR_GAP, limitDayField.getY(),
                detailPanel.getPreferredSize().width - limitDayField.getX() - limitDayField.getWidth() - 2
                        * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
        detailPanel.add(dayLabel);

        // 第每月第几个星期
        JRadioButton perNumberMonthButton = new JRadioButton(TaskDialogConstant.PER_WEEK, false);
        perNumberMonthButton.setMnemonic('E');
        int numberButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perNumberMonthButton });
        perNumberMonthButton.setBounds(0, perMonthButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                numberButtonWidth, CustOpts.BTN_HEIGHT);
        detailPanel.add(perNumberMonthButton);
        numMonField = new LimitedIntTextField(100);
        numMonField.addKeyListener(this);
        numMonField.setText(isSecond ? Integer.toString(firstNum) : "1");
        numMonField.setBounds(numberButtonWidth, perNumberMonthButton.getY(), 30, CustOpts.LBL_HEIGHT);
        detailPanel.add(numMonField);
        JLabel numMonLabel = new JLabel(TaskDialogConstant.MONTHS_TAL);
        int numMonLabelWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { numMonLabel });
        numMonLabel.setBounds(numMonField.getX() + numMonField.getWidth() + CustOpts.HOR_GAP,
                perNumberMonthButton.getY(), numMonLabelWidth, CustOpts.LBL_HEIGHT);
        detailPanel.add(numMonLabel);
        // tmpObj = contentTable.get(PIMPool.pool.getIntegerKey(ModelDBConstants.SECOND_NUMBER));
        // int comboIndex = (tmpObj == null || detailModeIndex != 1) ? 0 : ((Integer)tmpObj).intValue();
        monthCombo = new JComboBox(TaskDialogConstant.MONTH_COMBO_DATA);
        monthCombo.addItemListener(this);
        monthCombo.setSelectedIndex(isSecond ? secondNum : 0);
        monthCombo.setBounds(numMonLabel.getX() + numMonLabelWidth, numMonLabel.getY(), 80, CustOpts.BTN_HEIGHT);
        detailPanel.add(monthCombo);
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.THIRD_NUMBER));
        int thirdNum = tmpObj == null ? 0 : ((Integer) tmpObj).intValue();
        weekCombo = new JComboBox(TaskDialogConstant.WEEK_COMBO_DATA);
        weekCombo.addItemListener(this);
        weekCombo.setSelectedIndex(isSecond ? thirdNum : 0);
        weekCombo.setBounds(monthCombo.getX() + monthCombo.getWidth() + CustOpts.HOR_GAP, monthCombo.getY(), 100,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(weekCombo);
        if (detailModeIndex != 1) {
            int week = currentDate.getDay();
            int comboIndex = week == 7 ? 3 : week + 3;
            weekCombo.setSelectedIndex(comboIndex);
            int date = currentDate.getDate();
            monthCombo.setSelectedIndex(((date % 7) == 0) ? (date / 7 - 1) : (date / 7));
        }
        //
        JRadioButton perTastButton = new JRadioButton(TaskDialogConstant.WHEN_TASK_FINISHED, false);
        perTastButton.setMnemonic('G');
        if (isTask) {
            int tastButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perTastButton });
            perTastButton.setBounds(0, perNumberMonthButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP,
                    tastButtonWidth, CustOpts.BTN_HEIGHT);
            detailPanel.add(perTastButton);
            numMonthStart = new LimitedIntTextField(1000);
            numMonthStart.addKeyListener(this);
            numMonthStart.setText(isThird ? Integer.toString(firstNum) : "1");
            numMonthStart.setBounds(tastButtonWidth, perTastButton.getY(), 30, CustOpts.LBL_HEIGHT);
            detailPanel.add(numMonthStart);
            JLabel againStartLabel = new JLabel(TaskDialogConstant.START_AGAIN_MONTHS);
            againStartLabel.setBounds(numMonthStart.getX() + numMonthStart.getWidth() + CustOpts.HOR_GAP,
                    numMonthStart.getY(),
                    detailPanel.getPreferredSize().width - numMonthStart.getX() - numMonthStart.getWidth()
                            - CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
            detailPanel.add(againStartLabel);
        }

        monthButtonGroup = new PIMButtonGroup();
        monthButtonGroup.add(perMonthButton);
        monthButtonGroup.add(perNumberMonthButton);
        if (isTask) {
            monthButtonGroup.add(perTastButton);
        }
        monthButtonGroup.addEButtonGroupListener(this);
        monthButtonGroup.setSelectIndex(detailModeIndex);
        needItemEvent = true;
    }

    /*
     * 按年面板
     * @param prmWidth : 面板宽度
     * @param prmHeight: 面板高度
     */
    private void initYearPanel() {
        // ------------------------------------------------
        //
        needItemEvent = false;
        // 每年
        Object tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.EVERY_MODE_INDEX));
        byte detailModeIndex = tmpObj == null ? 0 : ((Byte) tmpObj).byteValue();
        boolean isFirst = detailModeIndex == 0;
        boolean isSecond = detailModeIndex == 1;
        boolean isThird = detailModeIndex == 2;
        JRadioButton perYearButton = new JRadioButton(TaskDialogConstant.PER_YEAR1, false);
        perYearButton.setMnemonic('V');
        int yearButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perYearButton });
        perYearButton.setBounds(0, 0, yearButtonWidth, CustOpts.BTN_HEIGHT);
        detailPanel.add(perYearButton);
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.FIRST_NUMBER));
        int firstNum = tmpObj == null ? 0 : ((Integer) tmpObj).intValue();
        monthYearCombo = new JComboBox(TaskDialogConstant.MONTH_DATA);
        monthYearCombo.addItemListener(this);
        monthYearCombo.setSelectedIndex(isFirst ? firstNum : currentDate.getMonth());
        monthYearCombo.setBounds(yearButtonWidth, perYearButton.getY(), 80, CustOpts.BTN_HEIGHT);
        detailPanel.add(monthYearCombo);
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.SECOND_NUMBER));
        int secondNum = tmpObj == null ? 1 : ((Integer) tmpObj).intValue();
        limitedDay = new LimitedIntTextField(100);
        limitedDay.addKeyListener(this);
        limitedDay.setText(Integer.toString(isFirst ? secondNum : currentDate.getDate()));
        limitedDay.setBounds(monthYearCombo.getX() + monthYearCombo.getWidth() + CustOpts.HOR_GAP,
                perYearButton.getY(), 30, CustOpts.LBL_HEIGHT);
        detailPanel.add(limitedDay);
        //
        JRadioButton perYe = new JRadioButton(TaskDialogConstant.PER_YEAR2, false);
        perYe.setMnemonic('E');
        int perYeWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { perYe });
        perYe.setBounds(0, perYearButton.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, perYeWidth,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(perYe);
        monthyearComboBox = new JComboBox(TaskDialogConstant.MONTH_DATA);
        monthyearComboBox.addItemListener(this);
        monthyearComboBox.setSelectedIndex(isSecond ? firstNum : currentDate.getMonth());
        monthyearComboBox.setBounds(perYeWidth, perYe.getY(), 80, CustOpts.BTN_HEIGHT);
        detailPanel.add(monthyearComboBox);
        // 的
        JLabel adjLabel = new JLabel(TaskDialogConstant.DE);
        adjLabel.setBounds(monthyearComboBox.getX() + monthyearComboBox.getWidth() + CustOpts.HOR_GAP, perYe.getY(),
                adjLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        detailPanel.add(adjLabel);
        monthCombo1 = new JComboBox(TaskDialogConstant.MONTH_COMBO_DATA);
        monthCombo1.addItemListener(this);
        monthCombo1.setSelectedIndex(isSecond ? secondNum : 0);
        monthCombo1.setBounds(adjLabel.getX() + adjLabel.getWidth() + CustOpts.HOR_GAP, adjLabel.getY(), 80,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(monthCombo1);
        tmpObj = contentTable.get(PIMPool.pool.getKey(ModelDBCons.THIRD_NUMBER));
        int thirdNum = tmpObj == null ? 0 : ((Integer) tmpObj).intValue();
        weekCombo1 = new JComboBox(TaskDialogConstant.WEEK_COMBO_DATA);
        weekCombo1.addItemListener(this);
        weekCombo1.setSelectedIndex(isSecond ? thirdNum : 0);
        weekCombo1.setBounds(monthCombo1.getX() + monthCombo1.getWidth() + CustOpts.HOR_GAP, perYe.getY(), 80,
                CustOpts.BTN_HEIGHT);
        detailPanel.add(weekCombo1);
        if (!isSecond) {
            // 设置当前的星期是第几个星期几
            int week = currentDate.getDay();
            int comboIndex = week == 7 ? 3 : week + 3;
            weekCombo1.setSelectedIndex(comboIndex);
            int date = currentDate.getDate();
            monthCombo1.setSelectedIndex(((date % 7) == 0) ? (date / 7 - 1) : (date / 7));
        }
        // 每当人物完成后第
        JRadioButton radiaoButton = null;// = new JRadioButton(TaskDialogConstant.WHEN_TASK_FINISHED,false,'G');
        if (isTask) {
            radiaoButton = new JRadioButton(TaskDialogConstant.WHEN_TASK_FINISHED, false);
            radiaoButton.setMnemonic('G');
            int radiaoButtonWidth = 10 + CASDialogKit.getMaxWidth(new JComponent[] { radiaoButton });
            radiaoButton.setBounds(0, perYe.getY() + CustOpts.BTN_HEIGHT + CustOpts.VER_GAP, radiaoButtonWidth,
                    CustOpts.BTN_HEIGHT);
            detailPanel.add(radiaoButton);
            limitYear = new LimitedIntTextField(1000);
            limitYear.addKeyListener(this);
            limitYear.setText(Integer.toString(isThird ? firstNum : 1));
            limitYear.setBounds(radiaoButtonWidth, radiaoButton.getY(), 30, CustOpts.LBL_HEIGHT);
            detailPanel.add(limitYear);
            JLabel numMonthLabel = new JLabel(TaskDialogConstant.START_AGAIN_MONTHS);
            numMonthLabel.setBounds(limitYear.getX() + limitYear.getWidth() + CustOpts.HOR_GAP, radiaoButton.getY(),
                    detailPanel.getPreferredSize().width - limitYear.getX() - limitYear.getWidth() - 2
                            * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);
            detailPanel.add(numMonthLabel);
        }
        //
        yearButtonGroup = new PIMButtonGroup();
        yearButtonGroup.add(perYearButton);
        yearButtonGroup.add(perYe);
        if (isTask) {
            yearButtonGroup.add(radiaoButton);
        }
        yearButtonGroup.addEButtonGroupListener(this);
        yearButtonGroup.setSelectIndex(detailModeIndex);
        needItemEvent = true;
    }

    /*
     * 此方法在确定时候对用户输入的值做有效性检查，如果有无效值，弹出警告对话框提示
     * @return 0 :数据合法，可以保存 1 :选中的按周，却没有选定是星期几 2 :选中的需要数据的地方，却没有数据或者数据为0
     */
    private int checkValue() {
        Object start = calendarstartCombo.getSelectedItem();
        // 循环中的开始日期不能为空
        if (start == null || !(start instanceof Date)) {
            calendarstartCombo.grabFocus();
            calendarstartCombo.getEditor().selectAll();
            return 2;
        }
        int result = 0;
        int modeIndex = dateGroup.getSelectIndex();
        if (modeIndex == 0) {
            int selectIndex = dayButtonGroup.getSelectIndex();
            result =
                    ((selectIndex == 0 && dayIntTextField.getValue() <= 0) || (selectIndex == 2 && tastFinishTextField
                            .getValue() <= 0)) ? 2 : 0;
        } else if (modeIndex == 1) {
            result = checkWeekMode();
        } else if (modeIndex == 2) {
            // result = checkMonthMode();
            int selectIndex = monthButtonGroup.getSelectIndex();
            result =
                    ((selectIndex == 0 && (perMonthField.getValue() <= 0 || limitDayField.getValue() <= 0))
                            || (selectIndex == 1 && numMonField.getValue() <= 0) || (selectIndex == 2 && numMonthStart
                            .getValue() <= 0)) ? 2 : 0;
        } else if (modeIndex == 3) {
            // result = checkYearMode();
            int selectIndex = yearButtonGroup.getSelectIndex();
            result =
                    ((selectIndex == 0 && limitedDay.getValue() <= 0) || (selectIndex == 2 && limitYear.getValue() <= 0)) ? 2
                            : 0;
        }
        if (result == 0) {
            // result = checkRepeatContent();
            result = (againBoundGroup.getSelectIndex() == 1 && limitNumberField.getValue() <= 0) ? 2 : 0;
            limitNumberField.grabFocus();
        }
        if (result == 0) {
            // 判断是否结束日期在开始日期之前，如果是，是无效的循环，提醒用户
            if (againBoundGroup.getSelectIndex() == 2) {
                Object end = endTimeCombo.getSelectedItem();
                if (end == null) {
                    result = 2;
                    endTimeCombo.grabFocus();
                    endTimeCombo.getEditor().selectAll();
                } else {
                    if (end instanceof Date) {
                        Date tmpStartDate = (Date) start;
                        Date tmpEndDate = (Date) end;
                        if (tmpEndDate.before(tmpStartDate)) {
                            result = 2;
                            endTimeCombo.grabFocus();
                            endTimeCombo.getEditor().selectAll();
                        }
                    } else {
                        result = 2;
                        endTimeCombo.grabFocus();
                        endTimeCombo.getEditor().selectAll();
                    }
                }
            }
        }
        return result;
    }

    private int checkWeekMode() {
        int selectIndex = weekButtonGroup.getSelectIndex();
        if (selectIndex == 0) {
            if (spanWeekTextField.getValue() <= 0) {
                return 2;
            }
            // 不能用else if，二者没有关系
            if (!(monday.isSelected() || tuesday.isSelected() || wednsday.isSelected() || thursday.isSelected()
                    || firday.isSelected() || sateday.isSelected() || sunday.isSelected())) {
                return 1;
            }
        } else if (selectIndex == 1 && startWeekField.getValue() <= 0) {
            return 2;
        }
        return 0;
    }

    private JRadioButton dayRadioButton; // 按天
    private JRadioButton weekRadioButton;// 按周
    private JRadioButton monthRadioButton; // 按月
    private JRadioButton yearRadioButton; // 按年
    private JSeparator separator;
    private JLabel startLabel;
    private JRadioButton endDateButton;
    private JRadioButton againButton;
    private JLabel numberLabel;
    private JButton ok, cancel;
    private PIMButtonGroup dateGroup;
    private PIMButtonGroup dayButtonGroup;
    private PIMButtonGroup weekButtonGroup;
    private PIMButtonGroup monthButtonGroup;
    private PIMButtonGroup yearButtonGroup;
    private PIMButtonGroup againBoundGroup;
    private JPanel rivetDatePanel, modePanel, detailPanel, repeatPanel;
    private LimitedIntTextField dayIntTextField;
    private LimitedIntTextField tastFinishTextField;
    private LimitedIntTextField spanWeekTextField;
    private LimitedIntTextField startWeekField;
    private LimitedIntTextField perMonthField;
    private LimitedIntTextField limitDayField;
    private LimitedIntTextField numMonField;
    private LimitedIntTextField numMonthStart;
    private LimitedIntTextField limitedDay;
    private LimitedIntTextField limitYear;
    private LimitedIntTextField limitNumberField;
    private JComboBox monthCombo;
    private JComboBox weekCombo;
    private JComboBox monthYearCombo;
    private JComboBox monthyearComboBox;
    private JComboBox monthCombo1;
    private JComboBox weekCombo1;
    private CalendarCombo calendarstartCombo;
    private CalendarCombo endTimeCombo;
    private JCheckBox sunday;
    private JCheckBox monday;
    private JCheckBox tuesday;
    private JCheckBox wednsday;
    private JCheckBox thursday;
    private JCheckBox firday;
    private JCheckBox sateday;
    private Hashtable contentTable;
    private boolean isTask;
    private byte modeIndex;
    private Date currentDate = new Date(System.currentTimeMillis());
    private boolean needItemEvent = true;
    private JRadioButton endWithButton;
}
