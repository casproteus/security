package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.text.ParseException;
import java.util.ArrayList;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.FilterInfo;
import org.cas.client.resource.international.CustViewConsts;

/**
 * 联系人相关信息的查询的查询
 */

class WhereFactory {

    /** Creates a new instance of CreateFilterWhere */
    public WhereFactory(FilterInfo prmFilterPool) {
        filterPool = prmFilterPool;
    }

    /**
     * 设置视图信息
     */
    public void setViewInfo(
            PIMViewInfo prmViewInfo) {
        viewInfo = prmViewInfo;
    }

    /**
     * 返回视图信息
     */
    public PIMViewInfo getViewInfo() {
        return viewInfo;
    }

    /**
     * 设置过滤信息对象
     */
    public void setFilterInfo(
            FilterInfo prmFilterPool) {
        filterPool = prmFilterPool;
    }

    /**
     * @return FilterInfo 视图过滤信息
     */
    public FilterInfo getFilterInfo() {
        return filterPool;
    }

    /**
     * 初始化日期工具类
     */
    private void lazyInitKit() {
        if (dateKit == null) {
            dateKit = new QueryDateKit();
        }
    }

    /**
     * 解析查询条件等信息
     */
    private void parseAdvanceCondition() {
        ArrayList tmpFieldList = filterPool.getFieldList(), // 字段
        tmpValueList = filterPool.getValueList(), // 值
        tmpTypesList = filterPool.getTypesList(), // 类型
        tmpCondiList = filterPool.getCondiList(); // 条件

        String[] prmField = CASUtility.getFieldAry(searchApp);
        for (int i = tmpFieldList.size() - 1; i >= 0; i--) // 建立条件语句
        {
            value = (String) tmpValueList.get(i);
            atype = ((Integer) tmpTypesList.get(i)).intValue();
            condi = ((Integer) tmpCondiList.get(i)).intValue();
            field = prmField[((Integer) tmpFieldList.get(i)).intValue()];
            // 建立条件语句
            buildConditionSql();
        }
    }

    /**
     * 解析类型
     */
    private void buildConditionSql() {
        String tmpList = null;
        switch (atype) {
            case CustViewConsts.STRING_TYPE:
                tmpList = stringTypeCondi();
                break;

            case CustViewConsts.NUM_TYPE:
                tmpList = numTypeCondi();
                break;

            case CustViewConsts.BOOL_TYPE:
                tmpList = boolTypeCondi();
                break;

            case CustViewConsts.TIME_TYPE:
                try {
                    tmpList = timeTypeCondi();
                } catch (ParseException pe) {
                    tmpList = null; // @NOTE: 如果去掉这个条件，即扩大了结果集的范围，用户要的结果也在其中
                }
                break;

            default:
                break;
        }
        addedEnginery(tmpList);
    }

    /**
     * 时间类型的条件
     */
    private String timeTypeCondi() throws java.text.ParseException {
        String tmpTimeCondi = null, tmpC = CustViewConsts.TIME_CONDITION[condi];
        int tmpBegDay, tmpBegYear, tmpEndDay, tmpEndYear;

        lazyInitKit(); // 初始化日期工具类

        if (tmpC.equals(CustViewConsts.TIME_VA_ANYTIME)) // 任何时间
        {
            tmpTimeCondi = null;
        } else if (tmpC.equals(CustViewConsts.TIME_VA_YESTODAY)) // 昨天
        {
            dateKit.setDate(dateKit.getYesterday());
            tmpTimeCondi = timeEquals(dateKit.getBegDay(), dateKit.getBegYear());
        } else if (tmpC.equals(CustViewConsts.TIME_VA_TODAY)) // 今天
        {
            dateKit.setDate(dateKit.getToday());
            tmpTimeCondi = timeEquals(dateKit.getBegDay(), dateKit.getBegYear());
        } else if (tmpC.equals(CustViewConsts.TIME_CON_TOMORROW)) // 明天
        {
            dateKit.setDate(dateKit.getTomorrow());
            tmpTimeCondi = timeEquals(dateKit.getBegDay(), dateKit.getBegYear());
        } else if (tmpC.equals(CustViewConsts.TIME_VA_SEVEN)) // 上7天
        {
            dateKit.setBoundDate(dateKit.getLastSeven(), dateKit.getToday());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.TIME_CON_NEXT_SEVEN)) // 下7天
        {
            dateKit.setBoundDate(dateKit.getToday(), dateKit.getNextSeven());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.TIME_VA_LASTWEEK)) // 上周
        {
            dateKit.setBoundDate(dateKit.getLastWeek());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.TIME_VA_THISWEEK)) // 本周
        {
            dateKit.setBoundDate(dateKit.getCurrWeek());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.TIME_CON_NEXT_WEEK)) // 下周
        {
            dateKit.setBoundDate(dateKit.getNextWeek());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.TIME_VA_LASTMONTH)) // 上个月
        {
            dateKit.setBoundDate(dateKit.getLastMonth());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.TIME_VA_THISMONTH)) // 本月
        {
            dateKit.setBoundDate(dateKit.getCurrMonth());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.TIME_CON_NEXT_MONTH)) // 下月
        {
            dateKit.setBoundDate(dateKit.getNextMonth());
            tmpTimeCondi = timeBetween();
        } else if (tmpC.equals(CustViewConsts.CON_IN)) {
            dateKit.setDate(java.text.DateFormat.getDateInstance().parse(value));
            tmpTimeCondi = timeEquals(dateKit.getBegDay(), dateKit.getBegYear());

        } else if (tmpC.equals(CustViewConsts.TIME_CON_NOEARLY)) // 不早于
        {
            dateKit.setDate(java.text.DateFormat.getDateInstance().parse(value));
            tmpTimeCondi = timeNotEarly(dateKit.getBegDay(), dateKit.getBegYear());
        } else if (tmpC.equals(CustViewConsts.TIME_CON_NOLATE)) // 不晚于
        {
            dateKit.setDate(java.text.DateFormat.getDateInstance().parse(value));
            tmpTimeCondi = timeNotLater(dateKit.getBegDay(), dateKit.getBegYear());
        } else if (tmpC.equals(CustViewConsts.CON_BETWEEN)) // 介于
        {
            // TODO:介于的时候在查找的时候，视图上该如何显示是个问题，所以暂时不解决，等待规格
            tmpBegDay = 21;
            tmpBegYear = 2004;

            tmpEndDay = 21;
            tmpEndYear = 2004;
            tmpTimeCondi = timeBetween(tmpBegDay, tmpEndDay, tmpBegYear, tmpEndYear);
        } else if (tmpC.equals(CustViewConsts.CON_EXIST)) // 存在
        {
            tmpTimeCondi = isExist();
        } else if (tmpC.equals(CustViewConsts.CON_NO_EXIST)) // 不存在
        {
            tmpTimeCondi = isNotExist();
        }
        return tmpTimeCondi;
    }

    /**
     * 日期条件语句
     */
    private String timeEquals(
            int prmDayInt,
            int prmYearInt) {
        StringBuffer tmpBuf = new StringBuffer();
        tmpBuf.append(field).append(" IS NOT NULL AND ");
        tmpBuf.append(" DAYOFYEAR(").append(field).append(") = ").append(prmDayInt);
        tmpBuf.append(" AND ");
        tmpBuf.append(" YEAR(").append(field).append(") = ").append(prmYearInt);
        return tmpBuf.toString();
    }

    /**
     * 日期条件语句
     */
    private String timeBetween() {
        return timeBetween(dateKit.getBegDay(), dateKit.getEndDay(), dateKit.getBegYear(), dateKit.getEndYear());
    }

    /**
     * 日期条件语句
     */
    private String timeBetween(
            int prmBegDay,
            int prmEndDay,
            int prmBegYear,
            int prmEndYear) {
        StringBuffer tmpBuf = new StringBuffer();

        tmpBuf.append(field).append(" IS NOT NULL AND ");
        tmpBuf.append(" DAYOFYEAR(").append(field);
        tmpBuf.append(") BETWEEN ").append(prmBegDay).append(" AND ").append(prmEndDay);
        tmpBuf.append(" AND ");

        tmpBuf.append(" YEAR(").append(field);
        tmpBuf.append(") BETWEEN ").append(prmBegYear).append(" AND ").append(prmEndYear);

        return tmpBuf.toString();
    }

    /**
     * 日期条件
     */
    private String timeNotEarly(
            int prmBegDay,
            int prmBegYear) {
        StringBuffer tmpBuf = new StringBuffer();
        tmpBuf.append(field).append(" IS NOT NULL AND ");
        tmpBuf.append('(');
        tmpBuf.append(" YEAR(").append(field).append(") > ").append(prmBegYear);
        tmpBuf.append(" OR ");
        tmpBuf.append('(');
        tmpBuf.append(" DAYOFYEAR(").append(field).append(") >= ").append(prmBegDay);
        tmpBuf.append(" AND ");
        tmpBuf.append(" YEAR(").append(field).append(") = ").append(prmBegYear);
        tmpBuf.append(')');
        tmpBuf.append(')');
        return tmpBuf.toString();
    }

    /**
     * 日期条件
     */
    private String timeNotLater(
            int prmBegDay,
            int prmBegYear) {
        StringBuffer tmpBuf = new StringBuffer();
        tmpBuf.append(field).append(" IS NOT NULL AND ");
        tmpBuf.append('(');
        tmpBuf.append(" YEAR(").append(field).append(") < ").append(prmBegYear);
        tmpBuf.append(" OR ");
        tmpBuf.append('(');
        tmpBuf.append(" DAYOFYEAR(").append(field).append(") <= ").append(prmBegDay);
        tmpBuf.append(" AND ");
        tmpBuf.append(" YEAR(").append(field).append(") = ").append(prmBegYear);
        tmpBuf.append(')');
        tmpBuf.append(')');
        return tmpBuf.toString();
    }

    /**
     * 数字类型的条件
     */
    private String numTypeCondi() {
        String tmpC = CustViewConsts.NUM_CONDITION[condi];
        String tmpNumCondi = null;
        if (tmpC.equals(CustViewConsts.CON_EQUAL)) // 等于
        {
            tmpNumCondi = " = ";
        } else if (tmpC.equals(CustViewConsts.CON_NO_EQUAL)) // 等于
        {
            tmpNumCondi = " <> ";
        } else if (tmpC.equals(CustViewConsts.NUM_CON_AT_MORE)) // 至多
        {
            tmpNumCondi = " <= ";
        } else if (tmpC.equals(CustViewConsts.NUM_CON_AT_LESS)) // 至少
        {
            tmpNumCondi = " >= ";
        } else if (tmpC.equals(CustViewConsts.NUM_CON_MORE)) // 多于
        {
            tmpNumCondi = " > ";
        } else if (tmpC.equals(CustViewConsts.NUM_CON_LESS)) // 少于
        {
            tmpNumCondi = " < ";
        } else if (tmpC.equals(CustViewConsts.CON_BETWEEN)) // 介于
        {
            // @TODO: 数字介于要修改界面, 这里要做修改
        }

        if (tmpNumCondi != null) {
            return field.concat(tmpNumCondi).concat(value);
        } else if (tmpC.equals(CustViewConsts.CON_EXIST)) // 存在
        {
            tmpNumCondi = isExist();
        } else if (tmpC.equals(CustViewConsts.CON_NO_EXIST)) // 不存在
        {
            tmpNumCondi = isNotExist();
        }

        return tmpNumCondi;
    }

    /**
     * 字符串类型的条件
     */
    private String stringTypeCondi() {
        String tmpC = CustViewConsts.STR_CONDITION[condi];
        String tmpStrCondi = null;
        if (tmpC.equals(CustViewConsts.STR_CON_CONTAIN)) // 包含
        {
            tmpStrCondi = field.concat(" LIKE '%").concat(value).concat("%' ");
        } else if (tmpC.equals(CustViewConsts.STR_CON_NOCONTAIN)) {
            tmpStrCondi = field.concat(" NOT LIKE '%").concat(value).concat("%' ");
        } else if (tmpC.equals(CustViewConsts.STR_CON_NONENULL)) // 非空
        {
            tmpStrCondi = field.concat(" IS NOT NULL "); // @NOTE: 值没有用
        } else if (tmpC.equals(CustViewConsts.STR_CON_NULL)) // 为空
        {
            tmpStrCondi = field.concat(" IS NULL ");
        } else if (tmpC.equals(CustViewConsts.STR_CON_YES)) {
            tmpStrCondi = field.concat(" = '").concat(value).concat("' "); // @NOTE: 值没有用
        }
        return tmpStrCondi.toString();
    }

    /**
     * 布尔条件
     */
    private String boolTypeCondi() {
        String tmpC = CustViewConsts.BOOL_CONDITION[condi];
        String tmpBoolCondi = null;
        boolean isYes = value.equals(CustViewConsts.BOOL_VA_YES);
        if (tmpC.equals(CustViewConsts.CON_EQUAL)) // 等于
        {
            tmpBoolCondi = isYes ? " = TRUE " : " != TRUE ";
        } else if (tmpC.equals(CustViewConsts.CON_NO_EQUAL)) // 不等于
        {
            tmpBoolCondi = isYes ? " != TRUE " : " = TRUE ";
        } else if (tmpC.equals(CustViewConsts.CON_EXIST)) // 存在
        {
            tmpBoolCondi = isYes ? null : " IS NULL "; // @NOTE: null表示不添加这个条件
        } else if (tmpC.equals(CustViewConsts.CON_NO_EXIST)) // 不存在
        {
            tmpBoolCondi = isYes ? " IS NULL " : null; // @NOTE: null表示不添加这个条件
        }

        return (tmpBoolCondi == null) ? null : field.concat(tmpBoolCondi);
    }

    // *第一选项卡的条件的实现@TODO : ******************************************************//
    // @TODO:
    // *************************************************************************//
    /**
     * 是否存在的条件语句
     */
    private String isExist() {
        return field.concat(" IS NOT NULL ");
    }

    /**
     * 不存在
     */
    private String isNotExist() {
        return field.concat(" IS NULL ");
    }

    /**
     * 添加语句的引擎
     */
    private void addedEnginery(
            String prmSql) {
        addedEnginery(prmSql, true);
    }

    /**
     * 添加语句的引擎
     */
    private void addedEnginery(
            String prmSql,
            boolean prmIsAnd) {
        if (prmSql == null) // 返回
        {
            return;
        }

        if (query.length() > 0) {
            if (prmIsAnd) {
                query.append(" AND ");
            } else {
                query.append(" OR ");
            }
        }

        query.append(prmSql);
    }

    /**
     * 得到查询的字符串
     */
    public String getWhere(
            PIMViewInfo prmViewInfo) {
        // 实例化查询where语句
        query = new StringBuffer();
        viewInfo = prmViewInfo;
        searchApp = prmViewInfo.getAppIndex(); // 得到查找的类型

        parseAdvanceCondition(); // 组织高级过滤的条件

        return query.toString();
    }

    private int atype;
    private int condi;
    private String field;
    private String value;

    private int searchApp;
    private StringBuffer query; // 查询语句
    private FilterInfo filterPool; // 过滤信息对象
    private QueryDateKit dateKit; // 日期处理的工具类
    private PIMViewInfo viewInfo; // 视图信息

}
