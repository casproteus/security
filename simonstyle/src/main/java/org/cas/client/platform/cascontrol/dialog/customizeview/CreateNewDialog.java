package org.cas.client.platform.cascontrol.dialog.customizeview;

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
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.cas.client.platform.casbeans.group.PIMButtonGroup;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.resource.international.CustViewConsts;

class CreateNewDialog extends JDialog implements ICASDialog, ActionListener {
    /**
     * 创建一个 CreateNewDialog 的实例
     * 
     * @param prmDialog
     *            父窗体
     * @param prmViewInfo
     *            视图信息
     */
    public CreateNewDialog(Dialog prmDialog, PIMViewInfo prmViewInfo) {
        super(prmDialog, true);
        // PIMControl.ctrl.getMainFrame();
        this.parentDialog = prmDialog;
        this.viewInfo = prmViewInfo;
        // 设置标题
        setTitle(CustViewConsts.CREATE_NEW_VIEW);
        setSize(getDialogSize());
        // 将内部组件初始化并布局
        initComponent();
        // 加上所有监听器
        addAllListeners();
        setBounds((CustOpts.SCRWIDTH - getDialogSize().width) / 2, (CustOpts.SCRHEIGHT - getDialogSize().height) / 2,
                getDialogSize().width, getDialogSize().height); // 对话框的默认尺寸。
    }

    public void reLayout() {
    };

    /**
     * 返回对话盒的尺寸
     * 
     * @return 对话盒尺寸
     */
    public Dimension getDialogSize() {
        return new Dimension(WIDTH, HEIGHT);
    }

    /**
     * 初始化组件加布局;
     */
    private void initComponent() {
        Dimension dlgSize = getDialogSize();

        // 设置OK按钮
        ok = new JButton(CustViewConsts.OK);
        ok.setBounds(dlgSize.width - ok.getPreferredSize().width * 2 - CustOpts.HOR_GAP,
                dlgSize.height - ok.getPreferredSize().height, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(ok);
        // 等该功能完善后打开
        ok.setEnabled(false);

        // 设置Cancel按钮
        cancel = new JButton(CustViewConsts.CANCEL);
        cancel.setBounds(dlgSize.width - cancel.getPreferredSize().width, dlgSize.height
                - cancel.getPreferredSize().height, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);
        getContentPane().add(cancel);
        // 这两个值用来给其他组件定位 ,都得通过他们计算
        int X_Coordinate = 0;
        int Y_Coordinate = 0;

        // 设置最上面的标签
        topLabel = new JLabel(CustViewConsts.NEW_VIEW_NAME);
        newViewName = new JTextField(CustViewConsts.FIELD_TEXT);// 设置文本框
        topLabel.setDisplayedMnemonic('N');
        topLabel.setLabelFor(newViewName);
        topLabel.setBounds(X_Coordinate, Y_Coordinate, topLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);

        // Y_Coordinate += CustOpts.LABEL_HEIGHT + CustOpts.VER_GAP;

        newViewName.setBounds(topLabel.getX(), topLabel.getY() + topLabel.getHeight() + CustOpts.VER_GAP,
                dlgSize.getSize().width, CustOpts.BTN_HEIGHT);
        getContentPane().add(topLabel);
        getContentPane().add(newViewName);

        // 设置列表框上的标签
        Y_Coordinate += CustOpts.LBL_HEIGHT + newViewName.getSize().height + CustOpts.VER_GAP * 2;
        viewTypeLabel = new JLabel(CustViewConsts.VIEW_TYPE_LABEL);
        applyTypeList = new JList(CustViewConsts.VIEW_TYPES);
        viewTypeLabel.setDisplayedMnemonic('V');
        viewTypeLabel.setLabelFor(applyTypeList);
        viewTypeLabel
                .setBounds(X_Coordinate, Y_Coordinate, viewTypeLabel.getPreferredSize().width, CustOpts.LBL_HEIGHT);

        // Y_Coordinate += CustOpts.LABEL_HEIGHT + CustOpts.VER_GAP;
        // 设置列表框上
        int listHeight = CustOpts.LBL_HEIGHT * 4;
        applyTypeList.setBounds(X_Coordinate, Y_Coordinate + CustOpts.LBL_HEIGHT + CustOpts.VER_GAP,
                dlgSize.getSize().width, listHeight);
        getContentPane().add(viewTypeLabel);
        getContentPane().add(applyTypeList);
        // 设置应用范围面板
        Y_Coordinate += CustOpts.LBL_HEIGHT + listHeight + CustOpts.VER_GAP * 2;
        applyTypePanel = new JPanel();// CustomizeViewConstants.APPLY_SCOPE);
        applyTypePanel.setBounds(X_Coordinate, Y_Coordinate, dlgSize.width,
                dlgSize.height - Y_Coordinate - cancel.getSize().height - CustOpts.VER_GAP);
        getContentPane().add(applyTypePanel);

        // 这两个值用来给应用范围面板上的组件定位
        int X_Coordinate_inner = CustOpts.HOR_GAP;
        int Y_Coordinate_inner = CustOpts.VER_GAP * 3;

        // 设置任何人都可看到该文件夹按钮
        everybodyCanWatch = new JRadioButton(CustViewConsts.EVERYBODY_CAN_WATCH, true);
        everybodyCanWatch.setMnemonic('T');
        everybodyCanWatch.setBounds(X_Coordinate_inner, Y_Coordinate_inner, everybodyCanWatch.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        applyTypePanel.add(everybodyCanWatch);
        everybodyCanWatch.setSelected(true);

        // 设置只有本人能够看到该文件夹按钮
        Y_Coordinate_inner += everybodyCanWatch.getSize().height + CustOpts.VER_GAP;
        onlyMySelfCanWatch = new JRadioButton(CustViewConsts.ONLY_MYSELF_CAN_WATCH, false);
        onlyMySelfCanWatch.setMnemonic('F');
        onlyMySelfCanWatch.setBounds(X_Coordinate_inner, Y_Coordinate_inner,
                onlyMySelfCanWatch.getPreferredSize().width, CustOpts.BTN_HEIGHT);
        applyTypePanel.add(onlyMySelfCanWatch);

        // 设置应用全部文件夹按钮
        Y_Coordinate_inner += onlyMySelfCanWatch.getSize().height + CustOpts.VER_GAP;
        allFolder = new JRadioButton(CustViewConsts.ALL_FOLDERS, false);
        allFolder.setMnemonic('A');
        allFolder.setBounds(X_Coordinate_inner, Y_Coordinate_inner, allFolder.getPreferredSize().width,
                CustOpts.BTN_HEIGHT);
        applyTypePanel.add(allFolder);

        // 建一个按钮组
        PIMButtonGroup buttonGroup = new PIMButtonGroup();
        // 把这三个单选按钮关联
        buttonGroup.add(everybodyCanWatch);
        buttonGroup.add(onlyMySelfCanWatch);
        buttonGroup.add(allFolder);
    }

    /**
     * 给所有组件加上监听器
     */
    private void addAllListeners() {
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
    }

    /**
     * 实现接口中的方法
     */
    public void release() {
    }

    /**
     * 实现接口中的方法 ,本例返回空
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
     * 宽度,以后去掉
     */
    public static final int WIDTH = 240;
    // 高度
    /**
     * 高度,以后去掉
     */
    public static final int HEIGHT = 300;

    // 构建列表框用的一个常量
    /** 偏移,以后去掉 */
    public static final int OFFSET = -1;

    private JButton ok;
    private JButton cancel;
    // 最上面的标签
    private JLabel topLabel;
    // 给用户输入视图名称
    private JTextField newViewName;

    // 中间的标签
    private JLabel viewTypeLabel;
    // 显示新建视图的类型
    private JList applyTypeList;

    // 下面的应用类型选择面板
    private JPanel applyTypePanel;
    // 应用类型选择面板上的几个应用类型按钮
    private JRadioButton everybodyCanWatch;
    private JRadioButton onlyMySelfCanWatch;
    private JRadioButton allFolder;
}
