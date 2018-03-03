package org.cas.client.platform.pimview;

import java.awt.event.KeyEvent;
import java.util.Vector;

import javax.swing.Icon;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.pimcard.CardViewComponent;
import org.cas.client.platform.pimview.pimcard.CardViewPanel;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;

public class View_PIMThumbnails extends AbstractPIMView implements Releasable {
    private static View_PIMThumbnails instance;

    public static View_PIMThumbnails getInstance() {
        if (instance == null)
            instance = new View_PIMThumbnails();
        return instance;
    }

    /**
     * Creates a new instance of PIMCardView
     * 
     * @param application
     *            应用接口
     */
    private View_PIMThumbnails() {
        cardPane = new CardViewPanel();// 800, 300, contents, info);
        scrollPane =
                new PIMScrollPane(cardPane, PIMScrollPane.VERTICAL_SCROLLBAR_NEVER,
                        PIMScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        titlePane = new CommonTitle(0);// 创建标题面板

        add(titlePane);
        add(scrollPane);
    }

    //
    // private void dfasdfsd()
    // {
    // model = PIMControl.ctrl.getModel();
    // currentViewInfo = application.getActiveViewInfo();
    // displayIndexes = model.getFieldNameIndex(currentViewInfo);
    // headerTitles = model.getFieldNames(currentViewInfo);
    //
    // contents = application.getViewContents();
    //
    // CardInfo info = new CardInfo();
    // info.setDisplayIndexes(displayIndexes);
    // info.setDisplayFieldNames(headerTitles);
    // info.setShowEmptyField(false);
    // }
    /**
     * 返回视图类型
     * 
     * @return int
     */
    public int getViewType() {
        return ModelCons.CARD_VIEW;
    }

    /**
     * 初始化
     */
    public void init(
            boolean prmAppChanged) {
        if (cardPane != null) {
            cardPane.setView(this);
            cardPane.clearSelection();
            cardPane.layoutCardView();
            if (cardPane.getCardComponents().size() != 0) {
                cardPane.selectCard((CardViewComponent) cardPane.getCardComponents().get(0));
            } else {
                cardPane.setLastAnchorCard(null);
            }
            scrollPane.revalidate();
            cardPane.repaint();
            cardPane.requestFocus();
            cardPane.requestDefaultFocus();
        }
    }

    /**
     * 更新表格信息,目前只对表格有效
     * 
     * @param changedType
     *            视图类型
     * @param changedInfo
     *            视图信息
     */
    public void updateTableInfo(
            int changedType,
            Object changedInfo) {
    }

    /** 更新Model */
    public void viewToModel() {
    }

    /**
     * 本方法在视图更新和字体对话盒操作后调用,以处理所有字体和前景色
     */
    public void updateFontsAndColor() {
    }

    /**
     * 自我更新一下
     */
    public void updatePIMUI() {

    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的ID的方法
     * 
     * @return 所有选中的记录的ID
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public int[] getSelectedRecordIDs() {
        Vector selectedCards = cardPane.getSelectedCards();
        if (selectedCards == null || selectedCards.size() == 0) {
            return new int[0];
        } else {
            int[] ids = new int[selectedCards.size()];
            for (int i = 0; i < selectedCards.size(); i++) {
                CardViewComponent comp = (CardViewComponent) selectedCards.elementAt(i);
                int recordID = comp.getRecordID();
                ids[i] = recordID;
            }
            return ids;
        }
    }

    /**
     * 应控制的要求,需要有得到视图中选中的所有记录的方法
     * 
     * @return 所有选中的记录
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public Vector getSelectedRecords() {
        int[] tmpSelectedIDS = getSelectedRecordIDs();
        if (tmpSelectedIDS == null || tmpSelectedIDS.length == 0) {
            return new Vector(0);
        } else {
            ICASModel model = CASControl.ctrl.getModel();
            int tmpPathID = getApplication().getActiveViewInfo().getPathID();
            for (int tmpSize = CustOpts.custOps.APPNameVec.size(), i = 0; i < tmpSize; i++) {
                return model.selectRecords(getApplication().getActiveViewInfo().getAppIndex(), tmpSelectedIDS,
                        tmpPathID);
            }
            return null;
        }
    }

    /**
     * 应控制的要求,需要有选取视图中的所有记录的方法
     * 
     * @called by : MainPane; and Control is CutAction & CopyAction
     */
    public void seleteAllRecords() {
        cardPane.selectAll();
    }

    /**
     * 目前卡片视图接受键盘焦点处理有问题,本方法用于临时处理使得正确,以后要去掉 called by : ContactPane
     * 
     * @param e
     *            键盘事件
     */
    public void processKeyEvent(
            KeyEvent e) {
        cardPane.processKeyEvent(e);
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        if (cardPane != null) {
            cardPane.release();
            cardPane = null;
        }
    }

    public void setBounds(
            int prmX,
            int prmY,
            int prmWidth,
            int prmHeight) {
        super.setBounds(prmX, prmY, prmWidth, prmHeight);
        titlePane.setBounds(0, 0, prmWidth, 20);
        scrollPane.setBounds(0, 20, prmWidth, prmHeight - 20);
    }

    public void setIconAndTitle(
            Icon prmIcon,
            String prmTitle) {
        titlePane.setPaneTitle(prmIcon, prmTitle);
    }

    public void setIconAndTitle(
            String prmTitle1,
            String prmTitle2) {
    }

    /**
     * called by PIMApplication,when BaseBookPane的书边上被点击时,调此方法:对Table视图实现上下翻页,对文本视图实现按日期翻页,卡片 视图实现左右翻页.
     */
    public void setPageBack(
            boolean prmIsBack) {
    }

    /**
     */
    public void closed() {
    }

    /**
     * 标题面板
     */
    protected CommonTitle titlePane;
    /**
     * 当前视图信息
     */
    private PIMViewInfo currentViewInfo;

    /**
     * 卡片视图组件
     */
    private CardViewPanel cardPane;
    /**
     * 配套的滚动面板
     */
    private PIMScrollPane scrollPane;
    /**
     * 数据内容
     */
    private Object[][] contents;
    /**
     * 要显示的索引
     */
    private int[] displayIndexes;
    /**
     * 在卡片中显示的字段名
     */
    private String[] headerTitles;
}
