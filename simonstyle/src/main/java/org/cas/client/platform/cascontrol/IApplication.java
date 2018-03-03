package org.cas.client.platform.cascontrol;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.event.PIMModelEvent;
import org.cas.client.platform.pimview.FieldDescription;
import org.cas.client.platform.pimview.IView;

/** 该类用来定义PIM的各种应用的面板应该具有的功能。TODO：有冗余，考虑去掉不需要用的方法。 */
public interface IApplication {
    String[] getAppFields(); // 应用包含哪些数据库字段。

    String[] getAppTypes(); // 应用的数据库字段分别是些什么类型。

    String[] getAppTexts(); // 应用的各个字段的别名分别是什么。

    void showDialog(
            Frame parent,
            ActionListener prmAction,
            PIMRecord prmRecord,
            boolean prmIsMeeting,
            boolean prmDrag); // 要求每个加入到本系统的应用实现该方法，使在该应用中选中的记录能够在该对话盒中显示，对话盒上的getContainer()返回的

    ICASDialog getADialog(); // 返回用于新建该应用的内容的对话盒实例。该实例可能用于预览等方面。

    JToolBar[] getStaticBars(); // 返回需要一直显示在界面上的工具条,如邮件中的新建,接收/发送等.

    JPanel[] getStaticStateBars();

    JToolBar[] getDynamicBars(); // 返回只在进入应用时显示在界面上的工具条,如邮件中的回复,转发等.

    JPanel[] getDynamicStateBars();

    JMenuItem getCreateMenu(); // 用于构建右键菜单：返回新建菜单项。

    JMenuItem getCreateMenu(
            Vector prmSelectedRecVec); // 用于构建右键菜单：根据选中的记录，返回新建菜单项。

    String[] getRecommendColAry(); // 返回用于在查找对话盒中显示的一些文本类型的列名.方便用户做简单查找.

    String[] getImportDispStr(); // 返回应用所支持的可导入内容的字符串数组。

    int[] getImportableFields(); // 返回应用中可以供导入的字段.

    String getImportIntrStr(
            Object prmKey); // 返回可导入内容对饮的说明文字。

    boolean execImport(
            Object prmKey); // 返回导入选项对应的Action。

    Action getAction(
            Object prmActionName); // 根据传入的Action名，返回改Action实例。

    int getStatus(
            Object prmActionName); // 根据传入的Action名，返回改Action对应的状态属性，该属性将被用来跟系统当前属性作对比，从而知道Action当前是否可用。

    IView getTiedView();

    Icon getAppIcon(
            boolean prmIsBig);

    // 对视图样式信息和视图数据信息的处理---------------------------------------//TODO：下一步考虑不提供访问视图信息的能力。
    PIMViewInfo getActiveViewInfo(); // 各种应用都保有一个对当前视图风格的引用，TODO：下一步可以考虑改该属性为不许外部访问。

    void initInfoInDB(); // 每个希望加入到PIM系统的应用都必须实现该方法，使系统在ViewInfo系统表中为其初始化ViewInfo。

    Object[][] processContent(
            Object[][] pContents,
            Object[] pFieldNames);

    // 没有必要对数据多设置这一层维护，视图直接根据ViewInfo或View得到model中的需要显示的Contents Object[][] getViewContents();
    // //返回当前视图的数据内容,因为外界动作使得视图切换时都传入入视图信息和数据内容.本接口应有从中读取该信息的能力,故加此方法
    // 关于邮件菜单的处理------------------------------------------------------
    void showPopupMenu(
            Component comp,
            int x,
            int y); // 根据上下文显示弹出式菜单

    // 鼠标点击事件的处理------------------------------------------------------
    void processMouseDoubleClickAction(
            Component comp,
            int x,
            int y,
            int prmSeleID);// 根据上下文面板在鼠标双击时要执行的动作: 比如弹出一个对话盒等

    void processMouseClickAction(
            Component comp); // 根据上下文棉板在鼠标单击时要行的动作:(例如显示预览)

    // 更新和改换视图的能力----------------------------------------------------
    void updateContent(
            PIMModelEvent e); // 更新视图中的显示数据

    void refreshView(
            PIMViewInfo newViewInfo); // 更新完整的视图信息

    /**
     * 本方法由各个应用实现，用以提供应用中数据库各个列的内容在显示时需要用到的绘制器和编辑器。
     * 尤其某些字段的显示需要特殊处理。如：在Table视图中显示数据库中的"“张三”<SanZhang@emo3.com>"时，要显示成："张三".
     * 
     * @param prmHeadName
     *            : 列头上显示的字符串。
     * @param prmIsEditable
     *            : 是否允许编辑。
     * @param prmEditor
     *            ：传入方法体中，等待被赋值的编辑器。
     * @param prmRenderer
     *            :传入方法体中，等待被赋值的绘制器。
     * @return ：true- 列宽不可以调整。
     */
    FieldDescription getFieldDescription(
            String prmHeadName,
            boolean prmIsEditable);

    // 对视图中数据的操作------------------------------------------------------
    void seleteAllRecord(); // 选取视图中的所有记录。

    int[] getSelectRecordsID(); // 取得被选中的记录。

    Vector getSelectRecords(); // 取得被选中的记录。

    void setPageBack(
            boolean prmIsBack); // 翻页功能,对于非书本视图可以实现成滚屏效果

    void closed(
            boolean prmNeedSave); // 视图关闭时应有保存尚未保存的数据的功能
}
