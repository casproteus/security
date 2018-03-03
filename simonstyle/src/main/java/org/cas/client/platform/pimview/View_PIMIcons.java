package org.cas.client.platform.pimview;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.ViewFormat;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.IPIMCellEditor;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.platform.pimview.pimtable.PIMTableColumn;
import org.cas.client.platform.pimview.util.PIMViewUtil;
import org.cas.client.resource.international.IntlModelConstants;

public class View_PIMIcons extends AbstractPIMView implements Releasable {
    private static View_PIMIcons instance;

    public static View_PIMIcons getInstance() {
        if (instance == null)
            instance = new View_PIMIcons();
        return instance;
    }

    public View_PIMIcons() {
        titlePane = new CommonTitle(0);// 创建标题面板
        titlePane2 = new CommonTitle(0);// 创建标题面板
        panel = new JPanel();
        scrollPane = new PIMScrollPane();

        // 界面上的所有组件构造结束－－－－－－－－－－
        scrollPane.setViewportView(panel);
        add(titlePane);
        add(scrollPane);
        add(titlePane2);
        // 界面搭建立完成－－－－－－－－－－

        // 监听器添加完成－－－－－－－－－－
    }

    // 实现IView的接口-------------------------------------------------------------------
    public void setIconAndTitle(
            Icon prmIcon,
            String prmTitle) {
        titlePane.setPaneTitle(prmIcon, prmTitle);
        titlePane2.setPaneTitle(prmIcon, prmTitle);
    }

    public static int titlePaneHeight = 20;// 默认标题高度

    public void updatePIMUI() {
    }

    public void updateTableInfo(
            int changedType,
            Object changedInfo) {
    }

    public void viewToModel() {
    }// 更新Model/

    public int[] getSelectedRecordIDs() {
        return null;
    }

    public Vector getSelectedRecords() {
        return null;
    }

    public void seleteAllRecords() {
    }

    public void updateFontsAndColor() {
    }

    public void setPageBack(
            boolean prmIsBack) {
    }

    public void closed() {
    }

    // 实现Releaseable接口---------------------------------------------------------------------------
    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     * 
     */
    public void release() {
        if (oldViewInfo != null)
            oldViewInfo.release();
        if (currentViewInfo != null)
            currentViewInfo.release();
        oldViewInfo = null;
        currentViewInfo = null;
        if (panel != null) {
            panel.removeAll();
            panel = null;
        }
        if (scrollPane != null) {
            scrollPane.getViewport().removeAll();
            scrollPane.release();
            scrollPane = null;
        }
        if (lazyFontAndColorPool != null) {
            lazyFontAndColorPool.clear();
            lazyFontAndColorPool = null;
        }
        // TODO:可能与DefaultPIMTableModel中的dispose有重复的地方
        if (contents != null) {
            for (int i = contents.length; --i >= 0;) {
                Object obj = contents[i];
                if (obj != null && obj instanceof Vector)
                    ((Vector) obj).clear();
                obj = null;
            }
            contents = null;
        }
    }

    /** 本方法用于设置View上各个组件的尺寸。 */
    public void setBounds(
            int prmX,
            int prmY,
            int prmWidth,
            int prmHeight) {
        super.setBounds(prmX, prmY, prmWidth, prmHeight);

        if (CustOpts.custOps.isBaseBookHide()) {// 非Book风格。
            if (CustOpts.custOps.isPreviewShown()) {
                int tmpPos = CustOpts.custOps.getSplitHeight();
                if (CustOpts.custOps.isViewTopAndDown()) {
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, tmpPos - titlePaneHeight);
                    getDlg().getContainer().setBounds(0, tmpPos + MainPane.getDividerSize(), prmWidth,
                            prmHeight - tmpPos - MainPane.getDividerSize());
                    getDlg().reLayout();
                } else {
                    scrollPane.setBounds(0, titlePaneHeight, tmpPos, prmHeight - titlePaneHeight);
                    getDlg().getContainer().setBounds(tmpPos + MainPane.getDividerSize(), titlePaneHeight,
                            prmWidth - tmpPos - MainPane.getDividerSize(), prmHeight - titlePaneHeight);
                    getDlg().reLayout();
                }
                titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                titlePane2.setBounds(0, 0, 0, 0);
            } else {
                titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                scrollPane.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);
                titlePane2.setBounds(0, 0, 0, 0);
                if (dialog != null)
                    getDlg().getContainer().setBounds(0, 0, 0, 0);
            }
        } else { // Book风格。
            int tmpPos = CustOpts.custOps.getSplitHeight();
            if (CustOpts.custOps.isPreviewShown()) {
                if (CustOpts.custOps.isViewTopAndDown()) {
                    titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, tmpPos - titlePaneHeight);
                    titlePane2.setBounds(0, tmpPos + MainPane.getDividerSize(), prmWidth, titlePaneHeight);
                    getDlg().getContainer().setBounds(0, tmpPos + MainPane.getDividerSize() + titlePaneHeight,
                            prmWidth, prmHeight - tmpPos - MainPane.getDividerSize() - titlePaneHeight);// 因为总宽度可能为奇数，除以2后由于精度导致一个像素的空白，故用（prmHeight
                                                                                                        // - prmHeight /
                                                                                                        // 2）避免。
                    getDlg().reLayout();
                } else {
                    titlePane.setBounds(0, 0, tmpPos, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, tmpPos, prmHeight - titlePaneHeight);
                    // 因为总宽度可能为奇数，除以2后由于精度导致一个像素的空白，故用（prmHeight - prmHeight / 2）避免。
                    titlePane2.setBounds(tmpPos + MainPane.getDividerSize(), 0,
                            prmWidth - tmpPos - MainPane.getDividerSize(), titlePaneHeight);
                    getDlg().getContainer().setBounds(tmpPos + MainPane.getDividerSize(), titlePaneHeight,
                            prmWidth - tmpPos - MainPane.getDividerSize(), prmHeight - titlePaneHeight);
                    getDlg().getContainer().setPreferredSize(
                            new Dimension(prmWidth - tmpPos - MainPane.getDividerSize(), prmHeight - titlePaneHeight));
                    getDlg().reLayout();
                }
            } else { // 无预览(两侧都是表格）。
                if (CustOpts.custOps.isViewTopAndDown()) {// 上下结构
                    titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);// tmpPos -
                                                                                                    // titlePaneHeight);
                    titlePane2.setBounds(0, 0, 0, 0);// tmpPos + MainPane.getDividerSize(), prmWidth, titlePaneHeight);
                } else { // 左右结构
                    titlePane.setBounds(0, 0, prmWidth, titlePaneHeight);
                    scrollPane.setBounds(0, titlePaneHeight, prmWidth, prmHeight - titlePaneHeight);
                    titlePane2.setBounds(0, 0, 0, 0);// tmpPos + MainPane.getDividerSize(), 0, prmWidth - tmpPos -
                                                     // MainPane.getDividerSize(), titlePaneHeight);
                }
            }
        }
    }

    /** 初始化视图上面应该显示的内容和形式（如列宽等） 该方法被调用之前，setPane和setViewInfo方法必须都是已经设过了的。 */
    public void init(
            boolean prmAppChanged) {
        // 数据内容的更新完成--------
        if (CustOpts.custOps.isPreviewShown()) {
            double tmpScale = CustOpts.custOps.getSplitHeightScale();
            dividerLocation = (int) ((1 - tmpScale) * CustOpts.custOps.getFrameHeight());
            dividerLocation = dividerLocation < 0 ? 0 : dividerLocation;
            // 由于认为分割条的位置在应用切换时候,跳来跳去不好看,所以配置文件中存放了程序中所有应用的共同的分割条位置.
            if (prmAppChanged)// 检查预览面板上的内容是否已准备好了，没有的话先将其初始化好。
                if (dialog != null) {
                    remove(dialog.getContainer());
                    dialog.release();
                    dialog = null;
                }
            getDlg().setContents(null);
        }
        ICASModel tmpModel = CASControl.ctrl.getModel();
        PIMViewInfo tmpViewInfo = getApplication().getActiveViewInfo();
        int tFolderID = tmpViewInfo.getPathID();
        if (tFolderID == 5400) {
            tmpViewInfo.setFolderID(101);
            tmpViewInfo.setFieldNames("0,3,27");
        }
        String[] tmpFieldNames = tmpModel.getFieldNames(tmpViewInfo);
        contents = tmpModel.selectRecords(tmpViewInfo); // 得到准备要显示的内容，TODO：要么对该方法返回的数组做尺寸限制，要么尝试改到绘制的时候再显示。
        lables = new JLabel[contents.length];
        for (int i = 0, len = lables.length; i < len; i++) {
            if (contents[i][2] == null)
                lables[i] =
                        new JLabel((String) contents[i][1],
                                PIMPool.pool.getIcon("/org/cas/client/platform/magicbath/img/Box1.gif"), JLabel.BOTTOM);
            else
                lables[i] =
                        new JLabel((String) contents[i][1],
                                PIMPool.pool.getIcon("/org/cas/client/platform/magicbath/img/Box2.gif"), JLabel.BOTTOM);
            panel.add(lables[i]);
        }

        panel.revalidate();
        panel.validate();
        panel.invalidate();
        panel.repaint();
    }

    /** 返回object数组，表示视图上应该显示的东西，包括图片 */
    private Object[] getDisplayObject(
            String[] fieldNames) {
        return fieldNames;
    }

    public ICASDialog getDlg() {
        if (dialog == null) {
            dialog =
                    MainPane.getApp(
                            (String) CustOpts.custOps.APPNameVec
                                    .get(getApplication().getActiveViewInfo().getAppIndex())).getADialog();
            dialog.getContainer().setBackground(Color.lightGray);
            add(dialog.getContainer());
        }
        return dialog;
    }

    private ICASDialog dialog;// 该对话盒的getContainer()返回的面板将显示于预览区域。
    protected CommonTitle titlePane;// 标题面板
    protected CommonTitle titlePane2;// 标题面板2
    private int dividerLocation;// 保留分隔条位置
    protected static PIMViewInfo oldViewInfo;// 因表格视图切换后的种种情况,保存一个原视图信息的引用是有用的,比如用来判断tableView前后是否服务于同一个应用.
    private JPanel panel;// 主体表格
    private PIMScrollPane scrollPane;// 表格配套的滚动面板
    private Object[][] contents;// 从数据库中得到的记录的所有数据
    private JLabel[] lables;
    // 本池保存当前表格视图所用到的所有字体和前景颜色,然后再分配到每一行去, 以减少所生成的对象数量
    private Hashtable lazyFontAndColorPool = new Hashtable();
}
