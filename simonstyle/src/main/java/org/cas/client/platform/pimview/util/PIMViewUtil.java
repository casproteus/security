package org.cas.client.platform.pimview.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.pimview.pimtable.IPIMCellEditor;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.Item;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pimview.pimtable.PIMTableHeader;
import org.cas.client.resource.international.IntlModelConstants;
import org.cas.client.resource.international.MailOptionConstant;
import org.cas.client.resource.international.PIMTableConstants;

public class PIMViewUtil {
    /**
     * 返回表格头中编辑器缺省位置
     * 
     * @param Object
     *            [] displayObjects
     * @return int
     */
    public static int getDefaultEditorLoc(
            Object[] displayObjects) {
        int location = -1;
        int length = displayObjects.length;
        for (int i = 1; i < length; i++) {
            if (displayObjects[i] instanceof String) {
                location = i;
                break;
            }
        }

        return location;
    }

    public static int getCharWidth(
            FontMetrics fontMatrics,
            char c) {
        return fontMatrics.charWidth(c);
    }

    public static int getColumnWidth(
            FontMetrics fontMatrics,
            String text) {
        return fontMatrics.stringWidth(text);
    }

    // 关于关键字段是否有值的处理========================================================================================================
    /**
     * @NOTE:以下两个方法代码极其相似,但不能合并,因为1/联系人的表示为有一个根据四个字段组合表示为字串的动作, 2/寻找主题字段时,不同的应用要与不同的资源文件变量比较. 判断任务的＂主题＂是否有值，否则记录不予存盘
     *                                                          TODO: 返回值的意义需要说明.
     * @return -1:全部字段都是null,(对于BasicPIMTableUI表示表头中没有任何内容,意味着"应该允许退出,不弹警告,但也不存盘.") 0:
     *         主题字段为null,其他字段有内容,(对于BasicPIMTableHeaderUI意味着弹警告). 1: 主题字段不为null.(对于BasicPIMTableHeaderUI意味着存盘).
     * @called by: BasicPIMTableHeaderUI;
     */
    public static int isSubjectAsHasValue(
            final PIMTableHeader prmTableHeader) {
        int oldColumn = -1;
        // 我觉得先要保存现有编辑器的值
        if (prmTableHeader.getTable().hasEditor() && prmTableHeader.isEditing()) {
            oldColumn = prmTableHeader.getEditingColumn();

            IPIMCellEditor cellEditor = prmTableHeader.getCellEditor();
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
            prmTableHeader.removeEditor();
        }
        String valueOfTopic = null;

        int topicIndex = 0;
        // 我觉得还要利用这个循环来了解是否所有列都为空,如是,要返回真, 但是不能存盘.
        boolean allIsNull = true;
        // 本循环将会找到"主题" 的位置和其值
        for (int i = 0, tmpCount = prmTableHeader.getColumnModel().getColumnCount(); i < tmpCount; i++) {
            Object tmpOb = prmTableHeader.getColumnModel().getColumn(i).getIdentifier();
            // 主题一定是String
            if (tmpOb instanceof String) {
                // 表示找到"主题"
                if (tmpOb.equals(IntlModelConstants.TASK_FLDS[ModelDBCons.CAPTION])) {
                    topicIndex = i;
                    Object tmpTopic = prmTableHeader.getValueAt(i);
                    // 保存该字段在快速编辑栏中的值
                    if (tmpTopic != null && tmpTopic instanceof String) {
                        valueOfTopic = (String) tmpTopic;
                    }
                } // end if (找到"主题")
            }

            // 以下是借用的,检查是否所有列都为空，是的话就表示用户没有输入任何数据
            Object tmpValue = prmTableHeader.getValueAt(i);
            // 暂时如此,几个类别都要处理
            if (tmpValue != null && i != 0 && tmpValue instanceof String && ((String) tmpValue).trim().length() != 0) {
                allIsNull = false;
            }
        } // end for
        if (allIsNull) {
            return -1;
        }

        // 无值就要跳回主题
        if (valueOfTopic == null || valueOfTopic.trim().length() == 0) {
            Toolkit.getDefaultToolkit().beep();
            prmTableHeader.setEditingColumn(topicIndex);
            prmTableHeader.setSelectedColumnIndex(topicIndex);
            // 使表格头的编辑器跳回主题一列
            if (!prmTableHeader.editCellAt(1, topicIndex)) {
                return 1;
            }
            prmTableHeader.getEditorComponent().requestFocus();
            // TODO:要增加滚动处理
            if (prmTableHeader.getTable().getAutoscrolls()) {
                Rectangle rect = prmTableHeader.getHeaderRect(1, topicIndex, false);
                if (rect != null) {
                    prmTableHeader.scrollRectToVisible(rect);
                    prmTableHeader.getTable().scrollRectToVisible(rect);
                }
            }
            return 0;
        }
        // 要恢复编辑原状
        else {
            if (oldColumn != -1) {
                prmTableHeader.setEditingColumn(oldColumn);
                prmTableHeader.setSelectedColumnIndex(oldColumn);
                if (!prmTableHeader.editCellAt(1, oldColumn)) {
                    // return 1;
                }
                prmTableHeader.getEditorComponent().requestFocus();
                prmTableHeader.getEditorComponent().requestFocus();
            }
            return 1;
        }
    }

    /**
     * 判断联系人的＂表示为＂是否有值，否则记录不予存盘
     * 
     * @called by: BasicPIMTableUI; PIMTableHeader; PIMTableView; BasicPIMTableHeaderUI.
     */
    public static int isDisplayAsHasValue(
            final PIMTableHeader prmTableHeader) {
        int oldColumn = -1;
        // 我觉得先要保存现有编辑器的值
        if (prmTableHeader.getTable().hasEditor() && prmTableHeader.isEditing()) {
            oldColumn = prmTableHeader.getEditingColumn();
            //
            // 对表示为字段进行处理
            processDisplayAsField(prmTableHeader);

            IPIMCellEditor cellEditor = prmTableHeader.getCellEditor();
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
            prmTableHeader.removeEditor();
        }
        String valueOfDisplayAs = null;
        int tmpDisplayAsIndex = 0;
        // 我觉得还要利用这个循环来了解是否所有列都为空,如是,要返回真
        boolean allIsNull = true;
        // 本循环将会找到"显示为" 的位置和其值
        for (int i = 0, tmpCount = prmTableHeader.getColumnModel().getColumnCount(); i < tmpCount; i++) {
            Object tmpOb = prmTableHeader.getColumnModel().getColumn(i).getIdentifier();
            if (tmpOb instanceof String) {
                // 表示找到"显示为"
                if (tmpOb.equals(IntlModelConstants.CONTACTS_FIELD[ModelDBCons.CAPTION])) {
                    tmpDisplayAsIndex = i;
                    Object tmpFileAs = prmTableHeader.getValueAt(i);
                    // 保存该字段在快速编辑栏中的值
                    if (tmpFileAs != null && tmpFileAs instanceof String) {
                        valueOfDisplayAs = (String) tmpFileAs;
                    }
                    // break;
                } // end if (找到"显示为")
            }

            // 以下是借用的
            Object tmpValue = prmTableHeader.getValueAt(i);
            // 暂时如此,几个类别都要处理
            if (tmpValue != null && i != 0 && tmpValue instanceof String && ((String) tmpValue).trim().length() != 0) {
                // A.s(PIMUtility.EMPTYSTR+ i + "have value");
                allIsNull = false;
            }
        } // end for
        if (allIsNull) {
            return -1;
        }
        if (valueOfDisplayAs == null || valueOfDisplayAs.trim().length() == 0) {
            Toolkit.getDefaultToolkit().beep();
            prmTableHeader.setEditingColumn(tmpDisplayAsIndex);
            prmTableHeader.setSelectedColumnIndex(tmpDisplayAsIndex);
            if (!prmTableHeader.editCellAt(1, tmpDisplayAsIndex)) {
                return 1;
            }
            prmTableHeader.getEditorComponent().requestFocus();
            // TODO:要增加滚动处理
            if (prmTableHeader.getTable().getAutoscrolls()) {
                Rectangle rect = prmTableHeader.getHeaderRect(1, tmpDisplayAsIndex, false);
                if (rect != null) {
                    prmTableHeader.scrollRectToVisible(rect);
                    prmTableHeader.getTable().scrollRectToVisible(rect);
                }
            }
            return 0;
        } else {
            if (oldColumn != -1) {
                prmTableHeader.setEditingColumn(oldColumn);
                prmTableHeader.setSelectedColumnIndex(oldColumn);
                if (!prmTableHeader.editCellAt(1, oldColumn)) {
                    // return 1;
                }
                prmTableHeader.getEditorComponent().requestFocus();
                prmTableHeader.getEditorComponent().requestFocus();
            }
            return 1;
        }
    } // end veto

    /**
     * 判断联系人表格体的＂表示为＂是否有值，否则记录不予存盘
     */
    public static boolean isDisplayAsHasValue(
            final PIMTable prmTable) {
        int oldColumn = -1;
        int oldRow = -1;
        // 我觉得先要保存现有编辑器的值
        if (prmTable.hasEditor() && prmTable.isEditing()) {
            oldColumn = prmTable.getEditingColumn();
            oldRow = prmTable.getEditingRow();

            // 对表示为字段进行处理
            processDisplayAsField(prmTable.getTableHeader());

            IPIMCellEditor cellEditor = prmTable.getCellEditor();
            if (cellEditor != null) {
                cellEditor.stopCellEditing();
            }
            prmTable.removeEditor();
        }
        String valueOfDisplayAs = null;
        int tmpDisplayAsIndex = 0;
        // 本循环将会找到"显示为" 的位置和其值
        for (int i = 0, tmpCount = prmTable.getColumnModel().getColumnCount(); i < tmpCount; i++) {
            Object tmpOb = prmTable.getColumnModel().getColumn(i).getIdentifier();
            if (tmpOb instanceof String) {
                // 表示找到"显示为"
                if (tmpOb.equals(IntlModelConstants.CONTACTS_FIELD[ModelDBCons.CAPTION])) {
                    tmpDisplayAsIndex = i;
                    Object tmpFileAs = prmTable.getValueAt(oldRow, i);
                    // 保存该字段在快速编辑栏中的值
                    if (tmpFileAs != null && tmpFileAs instanceof String) {
                        valueOfDisplayAs = (String) tmpFileAs;
                    }
                } // end if (找到"显示为")
            }
        } // end for
        if (valueOfDisplayAs == null || valueOfDisplayAs.trim().length() == 0) {
            Toolkit.getDefaultToolkit().beep();
            prmTable.setEditingColumn(tmpDisplayAsIndex);
            prmTable.setEditingRow(oldRow);
            prmTable.editCellAt(oldRow, tmpDisplayAsIndex, null);
            prmTable.getEditorComponent().requestFocus();
            prmTable.revalidate();
            prmTable.validate();
            prmTable.invalidate();
            // TODO:要增加滚动处理
            if (prmTable.getAutoscrolls()) {
                prmTable.scrollToRect(oldRow, tmpDisplayAsIndex);
            }
            return false;
        } else {
            if (oldColumn != -1) {
                prmTable.setEditingColumn(oldColumn);
                prmTable.setEditingRow(oldRow);
                prmTable.editCellAt(oldRow, oldColumn, null);
                prmTable.getEditorComponent().requestFocus();
                prmTable.getEditorComponent().requestFocus();
            }
            return true;
        }
    } // end=====================================================================================================================

    // 关于联系人中的表示为字段的处理===================================================================================================
    /**
     * @param prmTableHeader
     *            表格头引用
     * @NOTE: 根据规格,表示为字段是不可能隐藏的.所以在以表视图显示时,必定显示在界面上.
     * @NOTE: 在快速编辑栏中必定为新建联系人的动作,所以不用考虑编辑联系人时,如果表示为字段有内容不去改变表示为的内容 的情况.
     */
    public static void processDisplayAsField(
            PIMTableHeader prmTableHeader) {
        // 取当前编辑列的列头表示
        IPIMTableColumnModel tmpColumnModel = prmTableHeader.getColumnModel();
        Object tmpEditingIdentifier = tmpColumnModel.getColumn(prmTableHeader.getEditingColumn()).getIdentifier();
        Object tmpIdentifier = null;

        // 如果是姓氏/名字/单位/职务中的一个.
        // @NOTE:PIMTableHeader中取出的列表标题不会是null.
        if (tmpEditingIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.FNAME])
                || tmpEditingIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.NAME])
                || tmpEditingIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.COMPANY])
                || tmpEditingIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.TITLE])) {
            // 用来保存找到后的字符串
            String tmpLastName = null;
            String tmpFirstName = null;
            String tmpUnit = null;
            String tmpBusiness = null;
            // 保存找到"显示为"的索引,用于对该位置进行赋(重新组合后的)值.
            int tmpDisplayAsIndex = -1;

            int tmpCount = tmpColumnModel.getColumnCount();// 得到当前共有多少类显示.
            boolean isEditingColumn = false;
            // 本循环将会找到其他几个字段是否在表格中
            for (int i = 0; i < tmpCount; i++) {
                tmpIdentifier = tmpColumnModel.getColumn(i).getIdentifier();

                if (tmpEditingIdentifier == tmpIdentifier) {
                    isEditingColumn = true;
                }
                if (tmpIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.FNAME])) {
                    tmpLastName =
                            isEditingColumn ? (String) ((JTextField) prmTableHeader.getEditorComponent()).getText()
                                    : (String) prmTableHeader.getValueAt(i);
                    isEditingColumn = false;
                    continue;
                }
                if (tmpIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.NAME])) {
                    tmpFirstName =
                            isEditingColumn ? (String) ((JTextField) prmTableHeader.getEditorComponent()).getText()
                                    : (String) prmTableHeader.getValueAt(i);
                    isEditingColumn = false;
                    continue;
                }
                if (tmpIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.COMPANY])) {
                    tmpUnit =
                            isEditingColumn ? (String) ((JTextField) prmTableHeader.getEditorComponent()).getText()
                                    : (String) prmTableHeader.getValueAt(i);
                    isEditingColumn = false;
                    continue;
                }
                if (tmpIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ContactDefaultViews.TITLE])) {
                    tmpBusiness =
                            isEditingColumn ? (String) ((JTextField) prmTableHeader.getEditorComponent()).getText()
                                    : (String) prmTableHeader.getValueAt(i);
                    isEditingColumn = false;
                    continue;
                }
                // 表示找到有"显示为"
                else if (tmpIdentifier.equals(IntlModelConstants.CONTACTS_FIELD[ModelDBCons.CAPTION])) {
                    tmpDisplayAsIndex = i; // 纪录FileAs的位置,用于后面为其设置组合后的内容.
                    continue;
                }
            }

            // NOTE:在添加联系人记录时，只添加了“表示为字段”，单击回车键时，会调用此方法
            // 而且如果在显示为字段后一个字段为姓氏/名字/单位/职务中的一个时，会进入此循环并设置“表示为”字段。
            // 因为getPreparedFileAsStr()方法的缺陷，导致即使“显示为”字段有值，也会因为姓氏/名字/单位/职务的值为空，而把显示为字段的值设置为空。
            // 所以要判断如果tmpFileAs == null时则不设置“表示为”字段。

            // 在"显示为"字段中设值
            if (tmpDisplayAsIndex != -1) {
                String tmpFileAs = getPreparedFileAsStr(tmpLastName, tmpFirstName, tmpUnit, tmpBusiness);
                if (tmpFileAs != null) {
                    prmTableHeader.setValueAt(tmpFileAs, tmpDisplayAsIndex);
                }
            }
        } // end 处理如果是 "姓氏"这一字段
    }

    /*
     * 本方法根据用户在Option对话框中做的自定义设置,由几个参数组合出一个FileAs值.
     */
    private static String getPreparedFileAsStr(
            String prmLastName,
            String prmFirstName,
            String prmUnit,
            String prmBusiness) {
        ArrayList tmpComposedFileAsList = new ArrayList();
        int[] tmpShowedFileAsMap = new int[11];
        for (int i = 0; i < 11; i++) {
            tmpShowedFileAsMap[i] = -1;
        }
        // 准备各字符串是否为有效的标志----------------------------
        boolean tmpIsLastNameValid = (prmLastName != null && prmLastName.length() > 0);
        boolean tmpIsFirstNameValid = (prmFirstName != null && prmFirstName.length() > 0);
        boolean tmpIsUnitValid = (prmUnit != null && prmUnit.length() > 0);
        boolean tmpIsSuffixValid = (prmBusiness != null && prmBusiness.length() > 0);

        if (tmpIsLastNameValid && tmpIsFirstNameValid) {
            if (tmpIsSuffixValid) {
                tmpComposedFileAsList.add(prmLastName.concat(CASUtility.COMMA).concat(prmBusiness)
                        .concat(CASUtility.SPACE).concat(prmFirstName));
                tmpShowedFileAsMap[0] = tmpComposedFileAsList.size() - 1;
                tmpComposedFileAsList
                        .add(prmLastName.concat(prmFirstName).concat(CASUtility.SPACE).concat(prmBusiness));
                tmpShowedFileAsMap[1] = tmpComposedFileAsList.size() - 1;
            }
            if (tmpIsUnitValid) {
                tmpComposedFileAsList.add(prmFirstName.concat(CASUtility.COMMA).concat(prmLastName)
                        .concat(CASUtility.LEFT_BRACKET).concat(prmUnit).concat(CASUtility.RIGHT_BRACKET));
                tmpShowedFileAsMap[2] = tmpComposedFileAsList.size() - 1;
                tmpComposedFileAsList.add(prmFirstName.concat(prmLastName).concat(CASUtility.LEFT_BRACKET)
                        .concat(prmUnit).concat(CASUtility.RIGHT_BRACKET));
                tmpShowedFileAsMap[3] = tmpComposedFileAsList.size() - 1;
                tmpComposedFileAsList.add(prmUnit.concat(CASUtility.LEFT_BRACKET).concat(prmFirstName)
                        .concat(CASUtility.COMMA).concat(prmLastName).concat(CASUtility.RIGHT_BRACKET));
                tmpShowedFileAsMap[4] = tmpComposedFileAsList.size() - 1;
            }
            tmpComposedFileAsList.add(prmLastName.concat(prmFirstName));
            tmpShowedFileAsMap[5] = tmpComposedFileAsList.size() - 1;
            tmpComposedFileAsList.add(prmFirstName.concat(prmLastName));
            tmpShowedFileAsMap[6] = tmpComposedFileAsList.size() - 1;
        }
        if (tmpIsLastNameValid) // 名字字符串为有效的。
        {
            if (tmpIsUnitValid) {
                tmpComposedFileAsList.add(prmUnit.concat(CASUtility.SPACE).concat(prmLastName));
                tmpShowedFileAsMap[7] = tmpComposedFileAsList.size() - 1;
            }
            tmpComposedFileAsList.add(prmLastName);
            tmpShowedFileAsMap[8] = tmpComposedFileAsList.size() - 1;
        }
        if (tmpIsFirstNameValid) // 姓字符串为有效的。
        {
            tmpComposedFileAsList.add(prmFirstName);
            tmpShowedFileAsMap[9] = tmpComposedFileAsList.size() - 1;
        }
        if (tmpIsUnitValid) {
            tmpComposedFileAsList.add(prmUnit);
            tmpShowedFileAsMap[10] = tmpComposedFileAsList.size() - 1;
        }

        int tmpRealIndex = tmpShowedFileAsMap[CustOpts.custOps.getDisplayAsOrder()];
        if (tmpComposedFileAsList.size() > 0) {
            return (String) tmpComposedFileAsList.get(tmpRealIndex == -1 ? 0 : tmpRealIndex);
        }
        return null;
    }

    /**
     * 本方法使用递归来求得快速编辑栏编辑栏上第一个可进行文本输入的编辑器所在的 列的索引,如实在没有就取第一个可进行编辑的列的索引
     * 
     * @param prmTableHeader
     *            传入的表格头
     * @return 一个可进行文本输入的编辑器所在的列的索引
     */
    public static int getFirstTextableColumn(
            PIMTableHeader prmTableHeader) {
        // Class tmpClass = null;
        // try
        // {
        // tmpClass = Class.forName("org.cas.client.platform.pimview.pmTable.PIMTable$GenericEditor");
        // }
        // catch (ClassNotFoundException e)
        // {
        // //e.printStackTrace();
        // }

        int firstCanKeyInputColumn = -1;
        int firstCanEditorableColumn = -1;
        // if(!tmpColumn.getEditorEnable() || tmpColumn.getMaxWidth() == 0)
        if (prmTableHeader != null && prmTableHeader.getTable().hasEditor()) {
            PIMTable tmpTable = prmTableHeader.getTable();

            for (int i = 1, tmpCount = tmpTable.getModel().getColumnCount(); i < tmpCount; i++) {
                PIMTableColumn tmpTableColumn = tmpTable.getColumnModel().getColumn(i);
                IPIMCellEditor cellEditor = prmTableHeader.getCellEditor(i);
                boolean CannotEditorFlag = !tmpTableColumn.getEditorEnable() || tmpTableColumn.getMaxWidth() == 0;

                // 可编辑的列
                if (!CannotEditorFlag) {
                    firstCanEditorableColumn = i;
                }
                if (!CannotEditorFlag && cellEditor instanceof PIMTable.GenericEditor) {
                    firstCanKeyInputColumn = i;
                    break;
                }
            } // end for
            if (firstCanEditorableColumn < firstCanKeyInputColumn) {
                return firstCanEditorableColumn;
            } else {
                return firstCanKeyInputColumn;
            }
        }
        return firstCanKeyInputColumn;
    }

    /**
     * 本方法用于将从Table视图中取出的值（一般都是String类型）转换成不同的类型，便于存入数据库中。
     * 
     * @called by: BasicPIMTableHeaderUI；PIMTableHeader；PIMTableView；
     */
    public static Object getValueForSaveToDB(
            int prmAppType,
            int prmFieldNameInt,
            Object prmValue) {
        if (prmValue == null) {
            return prmValue;
        }// 如果传入为空，则直接返回-------------------------

        // 首先处理无论是哪种应用，都需要考虑转换的类型。＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        if (prmFieldNameInt == ModelDBCons.FLAGSTATUS) // 标记状态：选中项变成String－－－－－－－
        {
            if (prmValue instanceof Item) {
                return PIMPool.pool.getKey(((Item) prmValue).getName().equalsIgnoreCase(
                        PIMTableConstants.FLAGS_STATUS_CONSTANTS[1]) ? 1 : 2);
            } else {
                return PIMPool.pool.getKey(0);
            }
        }

        // 如果是联系人应用，那么要考虑如下几种需要转换的类型。＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        if (prmAppType == ModelCons.CONTACT_APP) {
            if (prmFieldNameInt == ContactDefaultViews.BIRTHDAY || // 三个日期.生日,纪念日,提醒时间，返回Date类型－－－－－－－－－－－
                    prmFieldNameInt == ContactDefaultViews.ANNIVERSARY || prmFieldNameInt == ModelDBCons.FOLOWUPENDTIME) {
                if (prmValue instanceof Date) {
                    return (Date) prmValue;
                } else if (prmValue instanceof String) {
                    if (CASUtility.getFormatedTime((String) prmValue, 0) != null) {
                        // A.s("Ok no problems I can parse it");
                    }
                    return null; // new Date(72,5,14);
                }
            } else if (prmFieldNameInt == ContactDefaultViews.SEX) // 性别:转换成整型－－－－－－－－－－－－－－－－－－－－－－－－－－
            {
                // 表示保存后未修改的状态
                if (prmValue instanceof Integer) {
                    return prmValue;
                } else {
                    String sexString = prmValue.toString();
                    if (sexString.equals(PIMTableConstants.SEX_ITEMS[1])) {
                        return new Byte((byte) 1);
                    } else if (sexString.equals(PIMTableConstants.SEX_ITEMS[2])) {
                        return new Byte((byte) 2);
                    } else if (sexString.length() > 0) {
                        return new Byte((byte) 0);
                    }
                    return null;
                }
            } else if (prmFieldNameInt == ModelDBCons.FOLLOWUPCOMPLETE // 是否需要提醒：转换成布尔值－－－－－－－－－－－－－－－－－
                    || prmFieldNameInt == ModelDBCons.DELETED_FLAG) {
                if (prmValue.toString().equals("1") || prmValue.toString().equals(PIMPool.BOOLEAN_TRUE)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
            // ID:照片:联系人:所在文件夹:类型:通讯组列表:Icon等字段被设成只读了，返回null(设成null可以避免再被存一遍?)－－－－－－－－－－－－－－－－
            if (prmFieldNameInt == ContactDefaultViews.PHOTO || prmFieldNameInt == ModelDBCons.CONTACT
                    || prmFieldNameInt == ContactDefaultViews.ID || prmFieldNameInt == ModelCons.folderIDIdx
                    || prmFieldNameInt == ContactDefaultViews.TYPE || prmFieldNameInt == ContactDefaultViews.MEMBERLIST
                    || prmFieldNameInt == ModelDBCons.ICON) {
                return null;
            }
        }
        // 如果是联系人应用，那么要考虑如下几种需要转换的类型。＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
        else if (prmAppType == ModelCons.TASK_APP) {
            if (prmFieldNameInt == ModelDBCons.IMPORTANCE) // 重要性:组合框变成Short------------------------------------------------
            {
                if (prmValue instanceof Item) {
                    String tmpValue = ((Item) prmValue).getName();
                    int id = 1;
                    if (tmpValue.equalsIgnoreCase(MailOptionConstant.IMPORTANT[0])) {
                        id = 0;
                    } else if (tmpValue.equalsIgnoreCase(MailOptionConstant.IMPORTANT[2])) {
                        id = 2;
                    }
                    return new Short((short) id);
                } else {
                    return new Short((short) 1);
                }
            } else if (prmFieldNameInt == ModelDBCons.STATUS) // 状态:状态组合框变成整数-------------------------------------------------------------
            {
                String name = (String) prmValue;
                int id = -1;
                if (name.equalsIgnoreCase(PIMTableConstants.TASK_STATUS_CONSTANTS[0])) {
                    id = 0;
                } else if (name.equalsIgnoreCase(PIMTableConstants.TASK_STATUS_CONSTANTS[2])) {
                    id = 2;
                } else if (name.equalsIgnoreCase(PIMTableConstants.TASK_STATUS_CONSTANTS[2])) {
                    id = 2;
                } else if (name.equalsIgnoreCase(PIMTableConstants.TASK_STATUS_CONSTANTS[3])) {
                    id = 3;
                } else if (name.equalsIgnoreCase(PIMTableConstants.TASK_STATUS_CONSTANTS[4])) {
                    id = 4;
                }
                return id == -1 ? null : PIMPool.pool.getKey(id);
            } else if (prmFieldNameInt == ModelDBCons.BEGIN_TIME
                    || prmFieldNameInt == ModelDBCons.END_TIME
                    || // 六个日期,转换成Date型－－－－－－－－－－－－－－－－－－－－－－
                    prmFieldNameInt == ModelDBCons.FINISH_DATE || prmFieldNameInt == ModelDBCons.MODIFY_TIME
                    || prmFieldNameInt == ModelDBCons.AWOKE_DATE || prmFieldNameInt == ModelDBCons.CREATE_TIME
                    || prmFieldNameInt == ModelDBCons.FOLOWUPENDTIME || prmFieldNameInt == ModelDBCons.RECIEVEDATE) {
                if (prmValue instanceof Date) {
                    return (Date) prmValue;
                } else if (prmValue instanceof String) {
                    if (CASUtility.getFormatedTime((String) prmValue, 0) != null) {
                        // A.s("Ok no problems I can parse it");
                    }
                    return null; // new Date(72,5,14);
                }
            } else if (prmFieldNameInt == ModelDBCons.FINISH_FLAG) // 已完成标志－－－－－－－－－－－－－－－－－－－－－－－－－－－
            {
                String name = prmValue.toString();
                if (name.equals(PIMPool.BOOLEAN_TRUE) || name.equals("1")) {
                    return new Short((short) 1);
                } else if (name.length() == 0) {
                    return null;
                } else {
                    return new Short((short) 0);
                }
            } else if (prmFieldNameInt == ModelDBCons.NEEDTOBESEND || // 是否已分派标志，返回Short----------------------------------------
                    prmFieldNameInt == ModelDBCons.FOLLOWUPCOMPLETE || prmFieldNameInt == ModelDBCons.DELETED_FLAG) {
                if (prmValue.toString().equals("1") || prmValue.toString().equals(PIMPool.BOOLEAN_TRUE)) {
                    return Boolean.TRUE;
                } else {
                    return Boolean.FALSE;
                }
            }
            if (prmFieldNameInt == ModelDBCons.CONTACT // 联系人字段,提醒时间字段，收件人,任务大小，Icon不予以处理
                    || prmFieldNameInt == ModelDBCons.AWOKE_TIME
                    || prmFieldNameInt == ModelDBCons.ADDRESSEE
                    || prmFieldNameInt == ModelDBCons.SIZE
                    || prmFieldNameInt == ContactDefaultViews.MEMBERLIST
                    || prmFieldNameInt == ModelDBCons.ICON) {
                return null;
            }
        }
        // 如果不需要任何特殊变换，返回字符串－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－
        // if (prmValue.toString().trim().length() != 0) //@NOTE:空字串的情况不应该返回null，应该返回空字串，
        // { //因为如果空字串对于本方法的返回值来说是有意义的，他表示“当前行的某个字段的值
        return prmValue.toString(); // 由某值变成了PIMUtility.EMPTYSTR（空字串），如果返回null，对于方法来说，表示的是"当前行的该字段的值并没有被
        // } //编辑过。
    }

    /**
     * 弹出式菜单的弹出点与屏幕上点的转换
     */
    public static Point calculatePopmenuPoint(
            Component comp,
            int x,
            int y,
            JPopupMenu popupMenu) {
        Dimension popSize = popupMenu.getPreferredSize();
        Point point = new Point(x, y);
        // 组件上的点转换成屏幕上的点
        SwingUtilities.convertPointToScreen(point, comp);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // 屏幕上的X点加上弹出式菜单的宽度如超出屏幕就要减去一个菜单的宽度(弹出在左边).
        int locateX = point.x + popSize.width > screenSize.width ? x - popSize.width : x;
        // 屏幕上的Y点加上弹出式菜单的高度如超出屏幕就要减去一个菜单的高度(弹出在上边).
        int locateY = point.y + popSize.height > screenSize.height ? y - popSize.height : y;
        return new Point(locateX, locateY);
    }
}
// //关于ICON字段值的定义
// public static int NORMALMAILICON = 10; //--邮件
// public static int TASKMAILICON = 11; //--任务
// public static int CONTACTSMAILICON = 12;//--联系人
// /**
// * =========================================
// * 试验IMAP协议，
// * 其中头为"文件夹", "未读", "总计", "同步设置"
// * =========================================
// * @called by:已删除项
// * @param pmTable
// * @param prmHeadNames
// * @param prmHasEditor
// */
// private static void processFolderSpecialFields(PIMTable pmTable, String[] prmHeadNames, boolean prmHasEditor, boolean
// isIMAP)
// {
// IPIMTableColumnModel tmpColumnModel = pmTable.getColumnModel();
// int tmpCount = tmpColumnModel.getColumnCount();
// int tmpWidth = 0;
// PIMTableColumn tmpTableColumn = null;
// for (int i = 0; i < tmpCount; i ++)
// {
// tmpTableColumn = tmpColumnModel.getColumn(i);
// tmpWidth = tmpTableColumn.getPreferredWidth();
// //文件夹（有图有文）
// if (prmHeadNames[i] == IntlModelConstants.LOCALFOLDER_FLDS[17])
// {
// tmpTableColumn.setMaxWidth(tmpWidth);
// tmpTableColumn.setMinWidth(tmpWidth);
// tmpTableColumn.setCellRenderer(new IMAPIconRenderer());
// if (prmHasEditor)
// {
// tmpTableColumn.setEditorEnable(false);
// }
// }
// //未读信件数量
// else if (prmHeadNames[i] == IntlModelConstants.LOCALFOLDER_FLDS[18])
// {
// tmpTableColumn.setMaxWidth(tmpWidth);
// tmpTableColumn.setMinWidth(tmpWidth);
// //TODO:这一列的绘制器要处理的实际是任务的主题
// tmpTableColumn.setCellRenderer(new DefaultPIMTableCellRenderer());
// if (prmHasEditor)
// {
// tmpTableColumn.setEditorEnable(false);
// }
// }
// //信件数量总计
// else if (prmHeadNames[i] == IntlModelConstants.LOCALFOLDER_FLDS[19])
// {
// tmpTableColumn.setMaxWidth(tmpWidth);
// tmpTableColumn.setMinWidth(tmpWidth);
// //TODO:这一列的绘制器要处理的实际是任务的主题
// tmpTableColumn.setCellRenderer(new DefaultPIMTableCellRenderer());
// if (prmHasEditor)
// {
// tmpTableColumn.setEditorEnable(false);
// }
// }
// if (isIMAP)
// {
// //同步设置情况（不要同步，所有邮件，新邮件，邮件标题）
// if (prmHeadNames[i] == IntlModelConstants.LOCALFOLDER_FLDS[20])
// {
// tmpTableColumn.setMaxWidth(tmpWidth);
// tmpTableColumn.setMinWidth(tmpWidth);
// tmpTableColumn.setCellRenderer(new CheckBoxCellRenderer("", true, ' '));
// if (prmHasEditor)
// {
// tmpTableColumn.setCellEditor(new DefaultPIMCellEditor(new PIMCheckBoxEditor("", true, ' ')));
// }
// }
// }
// }
// }

// /**本方法是用来给没有编辑器的日历面板上的表格用的
// * call by: emo.pim.pimview.calendar.CalendarLeftSubPane
// */
// public static void processSpecialFields(int appType,PIMTableColumn prmTableColumn,int displayIndex)
// {
// if (appType == ModelConstants.TASK_APP)
// {
// //"图标"
// if (displayIndex == ModelDBConstants.ICON)
// {
// int tmpWidth = ViewConstants.TASK_FLDS_WIDTHS[displayIndex];
// prmTableColumn.setMaxWidth(tmpWidth);
// prmTableColumn.setMinWidth(tmpWidth);
// //TODO: 任务有没有图标?几种?
// prmTableColumn.setCellRenderer(new TaskIconRenderer());
//
// }
// //下面是完成
// else if (displayIndex == ModelDBConstants.FINISH_FLAG)
// {
// int tmpWidth = ViewConstants.TASK_FLDS_WIDTHS[displayIndex];
// prmTableColumn.setMaxWidth(tmpWidth);
// prmTableColumn.setMinWidth(tmpWidth);
// prmTableColumn.setCellRenderer(new CheckBoxCellRenderer());
// }
// //下面是时间类的
// else if (displayIndex == ModelDBConstants.END_TIME ||
// displayIndex == ModelDBConstants.BEGIN_TIME ||
// displayIndex == ModelDBConstants.FINISH_DATE ||
// displayIndex == ModelDBConstants.MODIFY_TIME ||
// displayIndex == ModelDBConstants.AWOKE_DATE ||
// displayIndex == ModelDBConstants.CREATE_TIME)
// {
// prmTableColumn.setCellRenderer(new DateRenderer());
// }
// }
// else if (appType == ModelConstants.CONTACT_APP)
// {
// if (displayIndex == ModelDBConstants.ICON)
// {
// int tmpWidth = ViewConstants.CONTACTS_FIELD_WIDTHS[displayIndex];
// prmTableColumn.setMaxWidth(tmpWidth);
// prmTableColumn.setMinWidth(tmpWidth);
// prmTableColumn.setCellRenderer(new ContactsIconRenderer());
// }
// }
// }

// /**
// * 获得容器的窗体
// * @param c 查找父窗体的容器
// * @NOTE：将来肯可能日历中会用到。
// */
// private Frame getFrame(Container c)
// {
// Frame frame = null;
// while ((c = c.getParent()) != null)
// {
// if (c instanceof Frame)
// {
// frame = (Frame)c;
// }
// }
// return frame;
// }

// /**
// *
// */
// public static PIMTable getATable(PIMViewInfo currentViewInfo, Object[][] contents, IPIMModel model)
// {
// int[] displayIndexes = model.getFieldNameIndex(currentViewInfo);
// int[] displayWidths = model.getFieldWidths(currentViewInfo);
// Object[] headerTitles = model.getDisplayObject(currentViewInfo);
//
// int columns = headerTitles.length;
// int rows = contents.length;
// int dataColumnLength = contents.length;
//
// Object[][] tableContents = null;
// if (dataColumnLength == columns)
// {
// tableContents = contents;
// }
// else if (dataColumnLength > columns)
// {
// tableContents = new Object[rows][columns];
// for (int i = 0; i < columns; i ++)
// {
// System.arraycopy(contents[i], 0, tableContents[i],0, columns);
// }
// }
// else
// {
// tableContents = new Object[rows][columns];
// }
// PIMTable pimTable = new PIMTable();
// PIMTableModelVecBased tableModel = new PIMTableModelVecBased(tableContents, headerTitles);
// pimTable.setModel(tableModel);
//
// //设置每列宽度。
// IPIMTableColumnModel tableColumnModel = pimTable.getColumnModel();
// for (int i = 0; i < columns; i++)
// {
// tableColumnModel.getColumn(i).setPreferredWidth(displayWidths[i]);
// }
//
// tableColumnModel.getColumn(0).setMinWidth(0);
// tableColumnModel.getColumn(0).setMaxWidth(0);
//
// processSpecialFields(pimTable, displayIndexes);
//
// return pimTable;
// }

// /** 处理一些特殊的字段
// * @param PIMTable pmTable
// * @param int[] displayIndexes
// */
// private static void processSpecialFields(PIMTable pmTable, int[] displayIndexes)
// {
// IPIMTableColumnModel tmpColumnModel = pmTable.getColumnModel();
// int tmpWidth = 0;
// PIMTableColumn tmpTableColumn = null;
// //几种特殊的绘制器和编辑器
// for (int i = 0 ,tmpCount = displayIndexes.length; i< tmpCount ;i++)
// {
// tmpTableColumn = tmpColumnModel.getColumn(i);
// tmpWidth = tmpTableColumn.getPreferredWidth();
// tmpTableColumn.setMaxWidth(tmpWidth);
// tmpTableColumn.setMinWidth(tmpWidth);
// //人头
// if (displayIndexes[i] == ModelDBConstants.ICON)
// {
// tmpTableColumn.setEditorEnable(false);
// tmpTableColumn.setCellRenderer(new ContactsIconRenderer());
// }
// // //附件
// // else if(displayIndexes[i] == ModelDBConstants.ATTACHMENT)
// // {
// // tmpTableColumn.setEditorEnable(false);
// // tmpTableColumn.setCellRenderer(new AttachmentRenderer());
// // }
// //标志状态
// else if (displayIndexes[i] == ModelDBConstants.FLAGSTATUS)
// {
// tmpTableColumn.setCellRenderer(new ImageIconPIMTableCellRenderer());
// }
// }
// }

//
// /** 设置字段的宽度
// * @param PIMTable pmTable
// * @param int[] displayWidths
// */
// public static void setFieldWidths(PIMTable pmTable, int[] displayWidths)
// {
// IPIMTableColumnModel tableColumnModel = pmTable.getColumnModel();
// int columns = displayWidths.length;
// for (int i = 0; i < columns; i++)
// {
// tableColumnModel.getColumn(i).setPreferredWidth(displayWidths[i]);
// }
// tableColumnModel.getColumn(0).setMinWidth(0);
// tableColumnModel.getColumn(0).setMaxWidth(0);
// }
//
// /** 取出记录列表中的值转化为二维数组
// * @param Vector prmContents :包括视图显示的字段和内部使用的字段例如READED等字段的二维Vector.
// * @param int[] prmDisplayIndexes:各列所对应的字段名的数字表示.
// * @return Object[][]
// *
// * 解决每次都取出所有的数据，现在每次只取该显示的字段和一些特使的字段例如READED等字段
// * @called by: emo.pim.pimview.PIMTableView.java
// */
// public static Object[][] getDisplayRecordArray(Object[][] prmContents, int prmDisplayIndexesLength)
// {
// if (prmContents == null)
// {
// return null;
// }
// int tmpRows = prmContents.length; //得到传入prmContents中包含记录的条数.
// int tmpCols = prmDisplayIndexesLength; //得到传入的内容所包含的记录显示多少个字段.
// Object[][] tmpDisplay = new Object[tmpRows][tmpCols];
//
// Object tmpField = null;
// for (int i = 0; i < tmpRows; i ++)
// {
// Object[] tmpFieldsVec = prmContents[i];
// for (int j = 0; j < tmpCols; j ++)
// {
// tmpField = tmpFieldsVec[j];
// tmpDisplay[i][j] = tmpField;
// }
// }
// return tmpDisplay;
// }
//
// public static Object[][] getDisplayRecords(Vector contents, int app)
// {
// int len, size = contents.size();
// Object[][] display = null;
// if (size == 0)
// {
// return new Object[0][0];
// }
// else
// {
// len = ((Vector) contents.get(0)).size();
// display = new Object[size][len];
// }
//
// //对已删除项做特殊处理,最后一位为App类型
// if (app == ModelConstants.DELETED_ITEM_APP)
// {
// len --;
// }
//
// Vector tmp = null;
// for (int i = 0; i < size; i ++)
// {
// tmp = (Vector) contents.get(i);
// for (int j = 0; j < len; j ++)
// {
// display[i][j] = tmp.get(j);
// }
// }
// return display;
// }
