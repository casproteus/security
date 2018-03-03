package org.cas.client.platform.pimview;

import java.util.Vector;

import javax.swing.Icon;

import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.pimmodel.PIMViewInfo;

/**
 * 该类用来布局标题面板
 */

public interface IView {
    // **************************************************************************
    /**
     * 表示视图的排序信息发生变化
     */
    int SORTOR_CHANGED = 0;

    /**
     * 表示视图列的宽度发生变化
     */
    int COLUMN_WIDTH_CHANGED = 1;

    /**
     * 表示视图中的列发生移动(备用)
     */
    int COLUMN_ORDER_CHANGED = 2;

    /**
     * 设置应用，该实例将被作为视图及其内部JTable等组件的鼠标键盘事件的监听器使用。
     * 
     * @param application
     *            应用接口
     */
    void setApplication(
            IApplication application);

    /**
     * 返回应用
     * 
     * @return IApplication
     */
    IApplication getApplication();

    /**
     * 设置视图规格数据
     * 
     * @param application
     *            应用接口
     */
    void setViewInfo(
            PIMViewInfo application);

    /**
     * 更新Model
     */
    void viewToModel();

    /**
     * 自我更新一下
     */
    void updatePIMUI();

    /**
     * 用来处理表格头中的鼠标点击事件, 在某些应用视图为 PIMTable 表格时,表格头的鼠标动作,会激发排序,列宽度尺寸等 发生变化,表格会在适当时候调用本方法以保存该项信息
     * 
     * @param changedType
     *            见本类的几个常量
     * @param changedInfo
     *            封装变化的信息 在排序时,代表一个 int [2], 第一个为表格头上的排序列的索引,第二个为表示升降序标 志, 因为表格头目前尚未提供鼠标操作后得到排序信息的方法暂如此.其他几种变化目前似乎 用这个参数
     * @called by 如HeaderListener(目前)
     */
    public void updateTableInfo(
            int changedType,
            Object changedInfo);

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的ID的方法
     * 
     * @return 所有选中的记录的ID
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    int[] getSelectedRecordIDs();

    // @NOTE：将来可能得考虑去掉，因为不可能所有的视图都需要该方法。
    public void setIconAndTitle(
            Icon prmIcon,
            String prmTitle);

    // public void setIconAndTitle2(String prmString1, String prmString2);
    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的方法
     * 
     * @return 所有选中的记录
     * @called by : MainPane; and Control is CutAction & CopyAction
     * @deprecated:因为视图对应的是数据库中的View，而View通常只是table中记录的字集。
     */
    Vector getSelectedRecords();

    /**
     * 应控制的要求,需要有选取视图中的所有记录的方法
     * 
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    void seleteAllRecords();

    /**
     * 本方法在视图更新和字体对话盒操作后调用,以处理所有字体和前景色
     */
    void updateFontsAndColor();

    /**
     * 本方法用于设置View上各个组件的尺寸。
     * 
     * @param prmX
     * @param prmY
     * @param prmWidth
     * @param prmHeight
     */
    void setBounds(
            int prmX,
            int prmY,
            int prmWidth,
            int prmHeight);

    /**
     * called by PIMApplication,when BaseBookPane的书边上被点击时,调此方法:对Table视图实现上下翻页,对文本视图实现按日期翻页,卡片 视图实现左右翻页.
     */
    void setPageBack(
            boolean prmIsBack);

    /**
     * 主窗口被关闭的时候将被从IApplicationPane的closed方法中调到.
     */
    void closed();
}
