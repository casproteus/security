package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelCons;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.ViewFormat;
import org.cas.client.resource.international.CustViewConsts;

class SetFormatDialog extends JDialog implements ICASDialog, ActionListener, ListSelectionListener// ,FontInterface
{

    /**
     * 创建一个 FormatDialog 的实例
     * 
     * @param prmDialog
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     */
    public SetFormatDialog(Dialog prmDialog, PIMViewInfo prmViewInfo) {
        super(prmDialog, true);
        // 保存引用
        this.parentDialog = prmDialog;
        this.viewInfo = prmViewInfo;
        // 设置标题
        setTitle(CustViewConsts.SET_FORMAT_TITLE);
        // 设置对话盒尺寸
        setSize(getDialogSize());
        // 初始化List中的数据并装载数据库的缺省字体
        initFontInfo();
        // 组件初始化并布局
        initComponent();
        // 给组件加上监听器
        addAllListeners();
        // 根据视图信息初始化字体对话盒
        selectFontStyle(0);
        setBounds((CustOpts.SCRWIDTH - getDialogSize().width) / 2, (CustOpts.SCRHEIGHT - getDialogSize().height) / 2,
                getDialogSize().width, getDialogSize().height); // 对话框的默认尺寸。
    }

    public void reLayout() {
    };

    /**
     * 返回本对话盒的尺寸
     * 
     * @return 对话盒尺寸
     */
    public Dimension getDialogSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * 给所有的组件加上监听器
     */
    private void addAllListeners() {
        // 先加一个吧
        fontButton.addActionListener(this);
        formatList.addListSelectionListener(this);
        ok.addActionListener(this);
        cancel.addActionListener(this);
    }

    /**
     * 初始化List中的数据并装载数据库的缺省字体
     */
    private void initFontInfo() {
        // 得到当前的应用类型
        int tmpActiveAppType = viewInfo.getAppIndex();

        // 取邮件视图的所有字段
        // 目前好象是三个应用
        if (tmpActiveAppType == ModelCons.OUTBOX_APP || tmpActiveAppType == ModelCons.INBOX_APP
                || tmpActiveAppType == ModelCons.SENDED_APP || tmpActiveAppType == ModelCons.DELETED_ITEM_APP) {
            // 这数据在本包下
            listdata = CustViewConsts.EMAIL_FONTS;
        }
        // 取联系人视图的所有字段
        else if (tmpActiveAppType == ModelCons.CONTACT_APP) {
            listdata = CustViewConsts.CONTACT_FONTS;
        }
        // 取任务视图的所有字段
        else if (tmpActiveAppType == ModelCons.TASK_APP) {
            listdata = CustViewConsts.CONTACT_FONTS;
        }
        // 取日历视图的所有字段
        else if (tmpActiveAppType == ModelCons.CALENDAR_APP) {
            listdata = CustViewConsts.CALENDAR_FONTS;
        }
        // 其他几个不管

        int formatCounts = listdata.length;
        viewFormats = new ViewFormat[formatCounts];
        originViewFormats = new ViewFormat[formatCounts];
        ICASModel model = CASControl.ctrl.getModel();
        for (int i = 0; i < formatCounts; i++) {
            originViewFormats[i] = model.getViewFormat(tmpActiveAppType, 1, i, viewInfo.getPathID());
            viewFormats[i] = (ViewFormat) originViewFormats[i].clone();
        }
    }

    /**
     * 初始化对话盒中的组件并布局
     */
    private void initComponent() {
        Dimension dlgSize = getDialogSize();

        // 设置OK按钮
        ok = new JButton(CustViewConsts.OK);
        ok.setBounds(dlgSize.width - ok.getPreferredSize().width * 2 - CustOpts.HOR_GAP,
                dlgSize.height - ok.getPreferredSize().height, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);

        // 设置Cancel按钮
        cancel = new JButton(CustViewConsts.CANCEL);
        cancel.setBounds(dlgSize.width - cancel.getPreferredSize().width, dlgSize.height
                - cancel.getPreferredSize().height, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);

        // 这两个变量用来给组件定位,所有的组件都根据它来计算
        int X_Coordinate = 0;
        int Y_Coordinate = 0;

        // 设置可选项的标签
        JLabel topLabel = new JLabel(CustViewConsts.MODE);
        topLabel.setBounds(X_Coordinate, Y_Coordinate, topLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(topLabel);

        // 设置可选项的标签
        Y_Coordinate += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;
        // 初定列表框的高度和宽度
        int tmpListWidth = dlgSize.width;
        // dlgSize.height - Y_Coordinate - ok.getSize().height - CustOpts.HOR_GAP - CustOpts.VER_GAP;
        // 初定列表框的高度
        int tmpListHeight = CustOpts.LBL_HEIGHT * 6;

        // 设置选项的列表框
        formatList = new JList(listdata);
        formatList.setBounds(X_Coordinate, Y_Coordinate, tmpListWidth, tmpListHeight);
        getContentPane().add(formatList);

        Y_Coordinate += tmpListHeight + CustOpts.VER_GAP;
        // 设置预览面板的标签
        JLabel preView = new JLabel(CustViewConsts.PREVIEW);
        preView.setBounds(X_Coordinate + BUTTON_WIDTH + CustOpts.HOR_GAP, Y_Coordinate,
                preView.getPreferredSize().width, CustOpts.LBL_HEIGHT);
        getContentPane().add(preView);

        Y_Coordinate += CustOpts.LBL_HEIGHT + CustOpts.VER_GAP;

        // 设置字体按钮
        fontButton = new JButton(CustViewConsts.FONTS);
        fontButton.setMnemonic('F');
        fontButton.setBounds(X_Coordinate, Y_Coordinate, BUTTON_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(fontButton);

        /*
         * JLabel previewLabel = new JLabel(); previewLabel.setBackground(Color.white); int previewWidth =
         * getDialogSize().width - CustOpts.HOR_GAP*2 - CustOpts.HOR_GAP - BUTTON_WIDTH; int previewHeight =
         * getDialogSize().height - Y_Coordinate - cancel.getPreferredSize().height - CustOpts.HOR_GAP -
         * CustOpts.HOR_GAP; previewLabel.setPreferredSize(new Dimension(previewWidth,previewHeight));
         * previewLabel.added(getContentPane(),X_Coordinate + BUTTON_WIDTH + CustOpts.HOR_GAP,Y_Coordinate);
         * previewLabel.setOpaque(true); //
         */
        // 设置预览面板
        previewPanel = new PreviewPanel();
        previewPanel.setBackground(Color.white);
        // 预览面板的宽度和高度
        int previewWidth = getDialogSize().width - CustOpts.HOR_GAP - BUTTON_WIDTH;
        int previewHeight = getDialogSize().height - Y_Coordinate - cancel.getPreferredSize().height - CustOpts.HOR_GAP;
        // 加上去吧
        previewPanel.setBounds(X_Coordinate + BUTTON_WIDTH + CustOpts.HOR_GAP, Y_Coordinate, previewWidth,
                previewHeight);
        getContentPane().add(previewPanel);
    }

    private void selectFontStyle(
            int index) {
        // if (currentSeleItemFontAttr == null)
        // {
        // currentSeleItemFontAttr = new FontAttribute(FontConstants.SPREADSHEET);
        // previewPanel.setFontAttribute(currentSeleItemFontAttr);
        // }
        // currentSeleItemFontAttr.setFontName(viewFormats[index].getFontName());
        // currentSeleItemFontAttr.setFontStyle(viewFormats[index].getFontStyle());
        // currentSeleItemFontAttr.setFontSize(viewFormats[index].getFontSize());
        // currentSeleItemFontAttr.setFontColor(viewFormats[index].getFontColor());
        // currentSeleItemFontAttr.setStrikethrough(viewFormats[index].isHaveStrikethrough() ? 2 : 0);
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
        // 字体按钮按下
        if (e.getSource() == fontButton) {
            // //弹出字体对话盒
            // //天,他们WP是这样创建字体对话盒实例的
            // new FontDialog(this,true,this,FontConstants.SPREADSHEET);
            // previewPanel.repaint();
            // viewFormats[selectedID].setFontName(currentSeleItemFontAttr.getFontName());
            // viewFormats[selectedID].setFontStyle(currentSeleItemFontAttr.getFontStyle());
            // viewFormats[selectedID].setFontSize(currentSeleItemFontAttr.getFontSize());
            // viewFormats[selectedID].setFontColor(currentSeleItemFontAttr.getFontColor());
            // viewFormats[selectedID].setHaveStrikethrough(currentSeleItemFontAttr.getStrikethrough() == 2 );
        } else if (e.getSource() == cancel) {
            dispose();
        } else if (e.getSource() == ok) {
            for (int i = 0; i < viewFormats.length; i++) {
                if (!originViewFormats[i].equals(viewFormats[i])) {
                    CASControl.ctrl.getModel().updateViewFormat(viewFormats[i]);
                }
            }
            dispose();
        }
    }

    /**
     * 实现接口中的方法
     */
    public void release() {
    }

    /**
     * 实现接口中的方法,本例返回空
     * 
     * @return NULL
     */
    public PIMRecord getContents() {
        return null;
    }

    public boolean setContents(
            PIMRecord prmRecord) {
        return false;
    }

    public void makeBestUseOfTime() {
    }

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    public void valueChanged(
            ListSelectionEvent e) {
        if (e.getSource() == formatList) {
            selectedID = formatList.getSelectedIndex();
            selectFontStyle(selectedID);
            previewPanel.repaint();
        }
    }

    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    public PIMTextPane getTextPane() {
        return null;
    }

    public Container getContainer() {
        return getContentPane();
    }

    // 保存父对话盒的引用
    private Dialog parentDialog;
    // 保存当前视图信息的引用
    private PIMViewInfo viewInfo;

    // 保存本对话盒的尺寸
    /**
     * 本对话盒宽度
     */
    public static final int WIDTH = 350;
    // 高度
    /**
     * 本对话盒高度
     */
    public static final int HEIGHT = 250;

    // 大型按钮的宽度
    /**
     * 以后要去掉
     */
    public static final int BUTTON_WIDTH = 100;
    // 构建列表框用的一个常量
    /**
     * 以后要去掉
     */
    public static final int OFFSET = -1;

    // 当前可选用的格式列表框
    private JList formatList;
    // 字体按钮
    private JButton fontButton;
    private JButton ok;
    private JButton cancel;
    // 预览面板
    private PreviewPanel previewPanel;

    // 保存列表框的数据模型
    private String[] listdata;

    /**
     * 保存当前所有字体列表
     */
    private ViewFormat[] viewFormats;
    private ViewFormat[] originViewFormats;
    /**
     * 当前选中索引
     */
    private int selectedID;
}
