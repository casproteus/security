package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.util.ArrayList;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.util.ViewConstants;
import org.cas.client.resource.international.IntlModelConstants;

/**
 * 本类提供一些静态方法，用来负责对数据库的一些数据的处理
 */

class ParseUtil {

    /** 创建一个 ParseUtil 的实例 */
    private ParseUtil() {
    }

    /**
     * 解析当前视图的字段信息
     * 
     * @return 经处理后的字段数组
     */
    public static ArrayList parseFields() {
        int tmpPathID = CustOpts.custOps.getActivePathID();
        PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(tmpPathID);
        return parseFields(tmpViewInfo);
    }

    /**
     * 解析字段信息,传入值不得小于0
     * 
     * @called by: FieldsDialog
     * @param prmActiveAppType
     *            视图主应用类型
     * @param prmActiveViewType
     *            视图子应用类型
     * @return 经处理后的字段数组
     */
    public static ArrayList parseFields(
            int prmActiveAppType,
            int prmActiveViewType) {
        PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(CustOpts.custOps.getActivePathID());
        return parseFields(tmpViewInfo);
    }

    /**
     * 解析字段信息
     * 
     * @called by: FilterDialog
     * @param prmViewInfo
     *            视图信息
     * @return 经处理后的字段数组
     */
    public static ArrayList parseFields(
            PIMViewInfo prmViewInfo) {
        if (prmViewInfo == null) {
            return null;
        }
        PIMViewInfo tmpViewInfo = (PIMViewInfo) prmViewInfo.clone();
        int tmpActiveAppType = tmpViewInfo.getAppIndex();
        String tmpFieldsStr = tmpViewInfo.getFieldNames();
        if (tmpFieldsStr == null) {
            return null;
        }
        Vector tmpFieldsVec = new Vector();
        int tmpStrLen;
        tmpStrLen = tmpFieldsStr.length();
        for (int tmpDot = ',', tmpStartPos = 0, tmpPos = 0; tmpStartPos < tmpStrLen; tmpPos++, tmpStartPos = tmpPos) {
            // 得到当前位置之后的第一个逗号的位置。
            tmpPos = tmpFieldsStr.indexOf(tmpDot, tmpPos);
            // 如果当前位置后面没有逗号了，就把当前位置设为字符串末尾。
            if (tmpPos < 0) {
                tmpPos = tmpStrLen;
            }
            // 将起始位置和当前位置之间的字符串加入Vector。
            tmpFieldsVec.add(tmpFieldsStr.substring(tmpStartPos, tmpPos));
        }
        Object[] currentIDindexes = tmpFieldsVec.toArray();
        int[] indexAry = new int[currentIDindexes.length];
        ArrayList fields = new ArrayList();
        // 目前只有联系人和邮件这两部分的字段
        try {
            for (int i = 0; i < currentIDindexes.length; i++) {
                indexAry[i] = Integer.parseInt((String) currentIDindexes[i]);
                if (tmpActiveAppType == ModelCons.OUTBOX_APP || tmpActiveAppType == ModelCons.INBOX_APP
                        || tmpActiveAppType == ModelCons.SENDED_APP || tmpActiveAppType == ModelCons.DELETED_ITEM_APP) {
                    currentIDindexes[i] = IntlModelConstants.EMAIL_FLDS[indexAry[i]];
                    fields.add(IntlModelConstants.EMAIL_FLDS[indexAry[i]]);
                } else if (tmpActiveAppType == ModelCons.CONTACT_APP) {
                    currentIDindexes[i] = IntlModelConstants.CONTACTS_FIELD[indexAry[i]];
                    fields.add(IntlModelConstants.CONTACTS_FIELD[indexAry[i]]);
                } else if (tmpActiveAppType == ModelCons.DIARY_APP) {
                    currentIDindexes[i] = IntlModelConstants.DIARY_FLDS[indexAry[i]];
                    fields.add(IntlModelConstants.DIARY_FLDS[indexAry[i]]);

                } else if (tmpActiveAppType == ModelCons.TASK_APP) {
                    currentIDindexes[i] = IntlModelConstants.TASK_FLDS[indexAry[i]];
                    fields.add(IntlModelConstants.TASK_FLDS[indexAry[i]]);
                } else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
                    currentIDindexes[i] = IntlModelConstants.APPOINTMENT_FLDS[indexAry[i]];
                    fields.add(IntlModelConstants.APPOINTMENT_FLDS[indexAry[i]]);
                }
            }
        } catch (Exception ex) {
            // ex.printStackTrace();
        }

        return fields;
    }

    /**
     * 解析当前视图的字段宽度信息
     * 
     * @return 经处理后的宽度数组
     */
    public static int[] parseFieldWidths() {
        int tmpPathID = CustOpts.custOps.getActivePathID();
        PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(tmpPathID);
        return parseFieldWidths(tmpViewInfo);
    }

    /**
     * 解析字段宽度信息
     * 
     * @called by: FieldsDialog
     * @param prmActiveAppType
     *            视图主应用类型
     * @param prmActiveViewType
     *            视图子应用类型
     * @return 经处理后的宽度数组
     */
    public static int[] parseFieldWidths(
            int prmActiveAppType,
            int prmActiveViewType) {
        PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(CustOpts.custOps.getActivePathID());
        return parseFieldWidths(tmpViewInfo);
    }

    /**
     * 解析字段宽度信息
     * 
     * @param prmViewInfo
     *            视图信息
     * @return 经处理后的宽度数组
     */
    public static int[] parseFieldWidths(
            PIMViewInfo prmViewInfo) {
        if (prmViewInfo == null) {
            return null;
        }
        PIMViewInfo tmpViewInfo = (PIMViewInfo) prmViewInfo.clone();

        String tmpFieldsWidth = tmpViewInfo.getFieldWidths();
        Vector tmpFieldsVec = new Vector();
        int tmpStrLen;
        tmpStrLen = tmpFieldsWidth.length();

        for (int tmpDot = ',', tmpStartPos = 0, tmpPos = 0; tmpStartPos < tmpStrLen; tmpPos++, tmpStartPos = tmpPos) {
            tmpPos = tmpFieldsWidth.indexOf(tmpDot, tmpPos); // 得到当前位置之后的第一个逗号的位置。
            if (tmpPos < 0) // 如果当前位置后面没有逗号了，就把当前位置设为字符串末尾。
            {
                tmpPos = tmpStrLen;
            }
            tmpFieldsVec.add(tmpFieldsWidth.substring(tmpStartPos, tmpPos)); // 将起始位置和当前位置之间的字符串加入Vector。
        }
        Object[] tmpFieldWidths = tmpFieldsVec.toArray();

        int[] widthAry = new int[tmpFieldWidths.length];
        try {
            for (int i = 0, count = tmpFieldWidths.length; i < count; i++) {
                widthAry[i] = Integer.parseInt((String) tmpFieldWidths[i]);
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }

        return widthAry;
    }

    /**
     * 解析当前视图的排序信息 一个字段带一个升降序标志，0升1降
     * 
     * @return 排序信息数组
     */
    public static Object[] parseSortCritia() {
        int tmpPathID = CustOpts.custOps.getActivePathID();
        PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(tmpPathID);
        return parseSortCritia(tmpViewInfo);
    }

    /**
     * 解析当前视图的排序信息
     * 
     * @param prmActiveAppType
     *            视图主应用类型
     * @param prmActiveViewType
     *            视图子应用类型
     * @return 排序信息数组
     */
    public static Object[] parseSortCritia(
            int prmActiveAppType,
            int prmActiveViewType) {
        PIMViewInfo tmpViewInfo = CASControl.ctrl.getViewInfo(CustOpts.custOps.getActivePathID());
        return parseSortCritia(tmpViewInfo);
    }

    /**
     * 解析排序信息
     * 
     * @called by: CustomizeViewDialog; SortDialog
     * @param prmViewInfo
     *            视图信息
     * @return 排序信息数组
     */
    public static Object[] parseSortCritia(
            PIMViewInfo prmViewInfo) {
        if (prmViewInfo == null) {
            return null;
        }
        PIMViewInfo tmpViewInfo = (PIMViewInfo) prmViewInfo.clone();
        int tmpActiveAppType = tmpViewInfo.getAppIndex();
        // 显示哪些字段。
        String tmpSortCritiaStr = tmpViewInfo.getSortCritia();
        Vector tmpSortCritiaVec = new Vector();

        int tmpStrLen;
        // 这个条件判断表示中有排序标志
        Object[] tmpSortCritia = new Object[0];
        if (tmpSortCritiaStr != null) {
            tmpStrLen = tmpSortCritiaStr.length();
            for (int tmpDot = ',', tmpStartPos = 0, tmpPos = 0; tmpStartPos < tmpStrLen; tmpPos++, tmpStartPos = tmpPos) {
                // 得到当前位置之后的第一个逗号的位置。
                tmpPos = tmpSortCritiaStr.indexOf(tmpDot, tmpPos);
                // 如果当前位置后面没有逗号了，就把当前位置设为字符串末尾。
                if (tmpPos < 0) {
                    tmpPos = tmpStrLen;
                }
                // 将起始位置和当前位置之间的字符串加入Vector。全部是数据索引
                tmpSortCritiaVec.add(tmpSortCritiaStr.substring(tmpStartPos, tmpPos));
            }
            tmpSortCritia = tmpSortCritiaVec.toArray();
            int index = 0;
            // 至此，headerTitles和widthAry中放的是用String表示的int值，把他们变成int值分别存入indexAry和
            // widthAry数组，并在headerTitles中存入相应的显示值。
            // 我是这么认为的：排序标志为两个两个成对的，第一个是字段，第二个是升降序标志0升1降
            try {
                for (int i = 0; i < tmpSortCritia.length; i += 2) {
                    index = Integer.parseInt((String) tmpSortCritia[i]);
                    // tmpSortCritia[i] = ModelDBConstants.EMAILFLDS[indexAry[i]];
                    if (tmpActiveAppType == ModelCons.OUTBOX_APP || tmpActiveAppType == ModelCons.INBOX_APP
                            || tmpActiveAppType == ModelCons.SENDED_APP
                            || tmpActiveAppType == ModelCons.DELETED_ITEM_APP) {
                        tmpSortCritia[i] = IntlModelConstants.EMAIL_FLDS[index];
                    } else if (tmpActiveAppType == ModelCons.CONTACT_APP) {
                        tmpSortCritia[i] = IntlModelConstants.CONTACTS_FIELD[index];
                    } else if (tmpActiveAppType == ModelCons.DIARY_APP) {
                        tmpSortCritia[i] = IntlModelConstants.DIARY_FLDS[index];
                    } else if (tmpActiveAppType == ModelCons.TASK_APP) {
                        tmpSortCritia[i] = IntlModelConstants.TASK_FLDS[index];
                    } else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
                        tmpSortCritia[i] = IntlModelConstants.APPOINTMENT_FLDS[index];
                    }
                }
            } catch (Exception ex) {
                // ex.printStackTrace();
            }
        } // end if
        return tmpSortCritia;
    }

    /**************************************************************************/
    /***************** 下面的方法用来组装(反解析)要存入数据库的字段 *************/
    /**************************************************************************/

    /**
     * 组装字段
     * 
     * @param prmViewInfo
     *            视图信息
     * @param prmFields
     *            显示字段
     * @return 逗号分隔的字符串
     */
    public static String imparseFields(
            PIMViewInfo prmViewInfo,
            Object[] prmFields) {
        int tmpActiveAppType = prmViewInfo.getAppIndex();
        Object[] allFields = null;
        String fieldsStr = CASUtility.EMPTYSTR;
        if (tmpActiveAppType == ModelCons.OUTBOX_APP || tmpActiveAppType == ModelCons.INBOX_APP
                || tmpActiveAppType == ModelCons.SENDED_APP || tmpActiveAppType == ModelCons.DELETED_ITEM_APP) {
            allFields = IntlModelConstants.EMAIL_FLDS;
        } else if (tmpActiveAppType == ModelCons.CONTACT_APP) {
            allFields = IntlModelConstants.CONTACTS_FIELD;
        } else if (tmpActiveAppType == ModelCons.DIARY_APP) {
            allFields = IntlModelConstants.DIARY_FLDS;
        } else if (tmpActiveAppType == ModelCons.TASK_APP) {
            allFields = IntlModelConstants.TASK_FLDS;
        } else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
            allFields = IntlModelConstants.APPOINTMENT_FLDS;
        }
        for (int i = 0; i < prmFields.length; i++) {
            // 这个循环用来取字段的索引号
            for (int j = 0; j < allFields.length; j++) {
                if (prmFields[i].toString().equals(allFields[j].toString())) {
                    fieldsStr = fieldsStr + j + ',';
                    break;
                }
            } // end for j
        } // end for i
        return fieldsStr.substring(0, fieldsStr.length() - 1);
    }

    /**
     * 组装字段宽度
     * 
     * @param prmViewInfo
     *            视图信息
     * @param prmFields
     *            显示字段的宽度
     * @return 逗号分隔的字符串
     */
    public static String imparseFieldWidths(
            PIMViewInfo prmViewInfo,
            Object[] prmFields) {
        int tmpActiveAppType = prmViewInfo.getAppIndex();
        int[] allWidths = null;
        Object[] allFields = null;
        String fieldsWidthStr = CASUtility.EMPTYSTR;
        if (tmpActiveAppType == ModelCons.OUTBOX_APP || tmpActiveAppType == ModelCons.INBOX_APP
                || tmpActiveAppType == ModelCons.SENDED_APP || tmpActiveAppType == ModelCons.DELETED_ITEM_APP) {
            allWidths = ViewConstants.EMAIL_FIELD_WIDTHS;
            allFields = IntlModelConstants.EMAIL_FLDS;
        } else if (tmpActiveAppType == ModelCons.CONTACT_APP) {
            allWidths = ViewConstants.CONTACTS_FIELD_WIDTHS;
            allFields = IntlModelConstants.CONTACTS_FIELD;
        } else if (tmpActiveAppType == ModelCons.DIARY_APP) {
            allWidths = ViewConstants.DIARY_FLDS_WIDTHS;
            allFields = IntlModelConstants.DIARY_FLDS;
        } else if (tmpActiveAppType == ModelCons.TASK_APP) {
            allWidths = ViewConstants.TASK_FLDS_WIDTHS;
            allFields = IntlModelConstants.TASK_FLDS;
        } else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
            allWidths = ViewConstants.CALENDAR_FLDS_WIDTHS;
            allFields = IntlModelConstants.APPOINTMENT_FLDS;
        }
        // 这是未增减的字段和与之一一对应的宽度
        String[] originFields = CASControl.ctrl.getModel().getFieldNames(prmViewInfo);
        int[] originWidths = CASControl.ctrl.getModel().getFieldWidths(prmViewInfo);
        ArrayList tmpOriginFieldslst = new ArrayList(originFields.length);
        for (int k = 0, count = originFields.length; k < count; k++) {
            tmpOriginFieldslst.add(originFields[k]);
        }
        // 这个循环才是一次一个新字段处理
        for (int i = 0; i < prmFields.length; i++) {
            // 下面用来加宽度，最好的办法是在数据库中先遍历出宽度(用户的设定)
            // 这个局部变量用来标志在数据库中没有找到对应宽度
            boolean tmpNoFound = true;
            // 在老字段中搜索
            for (int j = 0, count = originFields.length; j < count; j++) {
                if (originFields[j].equals(prmFields[i].toString())) {
                    tmpNoFound = false;
                    fieldsWidthStr = fieldsWidthStr + originWidths[j] + ',';
                    break;
                }
            }
            if (tmpNoFound) {
                // 这个循环用来取字段的索引号
                for (int j = 0; j < allFields.length; j++) {
                    // 传入的字段和所有字段比,有相同的就根据其索引来得到该字段的宽度信息
                    if (prmFields[i].toString().equals(allFields[j].toString())) {
                        fieldsWidthStr = fieldsWidthStr + allWidths[j] + ',';
                        break;
                    }
                } // end for j
            }

        } // end for i
        return fieldsWidthStr.substring(0, fieldsWidthStr.length() - 1);
    }

    /**
     * 组装排序
     * 
     * @param prmViewInfo
     *            视图信息
     * @param prmSortCritia
     *            排序信息
     * @return 逗号分隔的字符串
     */
    public static String imparseSortCritia(
            PIMViewInfo prmViewInfo,
            Object[] prmSortCritia) {
        return null;
    }

}
