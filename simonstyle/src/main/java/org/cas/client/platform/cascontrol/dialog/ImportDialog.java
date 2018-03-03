package org.cas.client.platform.cascontrol.dialog;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.resource.international.ImportDlgConst;

/**
 * 导入导出对话盒(导入联系人地址簿) 本对话盒应该改为开放式，允许第三方开发自己的导入功能。 TODO:资源文件不能再从借口中取，静态变量释放不掉的。因为它是类的属性，对像的属性可以随着对象的释放被回收掉。而类一经装载是不放掉的。
 * NOTE:本对话盒可以作为新人学习的典范。用于熟悉对话盒UI标准，动态布局标准，和开发插件标准的实现。
 * NOTE:导入跟菜单不同，菜单项需要统一协调屏幕上的布局，所以要求二次开发用户将文本写到文件中去，而导入对话盒中允许导入的各项只须显示到
 * 本对话盒中即可，所以不需要要求二次开发者将内容象菜单一样写入config文件。另外，菜单有预设的状态信息，用来标识改菜单项在哪些情况下有效
 * 而导入对话盒中的所有项都不应该设计状态信息，因该在任何状态下都是有效的。所以各应用不需要象菜单那样初始化其状态信息。同样，取其对应的
 * Action的时候也可以直接用Text作为key换取Action即可，不需要象菜单那样复杂（这也是没有通过getAction接口取Action的原因，getAction 一般根据传入的类名，通过反射得到相应的Acton的实例的）。
 */
public class ImportDialog extends JDialog implements ListSelectionListener, ActionListener, ComponentListener {
    /**
     * Creates a new instance of ImportDialog
     * 
     * @param prmParent
     *            为父窗格
     */
    public ImportDialog(Frame prmParent) {
        super(prmParent, true);
        setTitle(ImportDlgConst.IMPORTDIALOG_TITLE); // 通讯簿导入工具
        setBounds((CustOpts.SCRWIDTH - 408) / 2, (CustOpts.SCRHEIGHT - 334) / 2, 408, 334); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        // 实例化各个组件----------------------------------------------------------------------------------------------
        label = new JLabel(ImportDlgConst.CHOOSE_OPERATE); // 顶部的可滚动列表及其说明
        list = new JList(CASControl.ctrl.getImportDisStr()); // 通过总控向所有既已安装的应用索取“需要支持导入”的项目内容。
        scrollPane = new JScrollPane(list);

        introducePanel = new JPanel(); // 中间的带有标题面板的"说明信息"区域
        area = new JTextArea(); // 显示文本(动态取显示内容)。

        separator = new JSeparator(); // 底部的分隔线和next、cancel按钮
        ok = new JButton(ImportDlgConst.NEXT); // ok按钮上显示的是“下一步”
        cancel = new JButton(ImportDlgConst.CLOSE);

        // 分别作属性设置----------------------------------------------------------------------------------------------
        label.setDisplayedMnemonic('C');
        label.setFont(CustOpts.custOps.getFontOfDefault());
        label.setLabelFor(list);

        list.setFont(CustOpts.custOps.getFontOfDefault());
        list.addListSelectionListener(this);
        list.setSelectedIndex(0);
        scrollPane.setAutoscrolls(true);// 默认为false。

        area.setOpaque(false);// 多行标签设置为透明
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setRequestFocusEnabled(false);
        area.setEditable(false); // 设置是否可编辑

        introducePanel.setOpaque(false);
        introducePanel.setBorder(new TitledBorder(new EtchedBorder(), ImportDlgConst.EXPLAIN_INFO, 4, 2,
                CustOpts.custOps.getFontOfTableHeader()));
        introducePanel.setLayout(new BorderLayout());

        ok.setFont(CustOpts.custOps.getFontOfDefault());
        cancel.setFont(ok.getFont());
        getRootPane().setDefaultButton(ok);

        // 搭建－－－－－－－－－－－－－－－－－－－－－
        introducePanel.add(area);
        getContentPane().add(ok);
        getContentPane().add(cancel);
        getContentPane().add(separator);
        getContentPane().add(introducePanel);
        getContentPane().add(scrollPane);
        getContentPane().add(label);

        // 加监听器----------------------------------------------------------------------------------------
        ok.addActionListener(this);
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     */
    private void reLayout() {
        cancel.setBounds(getContentPane().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getHeight()
                - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);
        separator.setBounds(CustOpts.VER_GAP, ok.getY() - CustOpts.SEP_HEIGHT, getWidth() - 3 * CustOpts.VER_GAP,
                CustOpts.SEP_HEIGHT);

        int tmpListHight =
                (getHeight() - CustOpts.LBL_HEIGHT - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - CustOpts.BTN_HEIGHT // rootPane的高度去掉List的说明标签高度先
                                                                                                                    // 再去掉底部的Button的高度
                        - CustOpts.SEP_HEIGHT // 再去掉JSeparator的高度
                - 3 * CustOpts.VER_GAP) / 2; // 再去掉一共3个间隙，剩下的就是List和Area的高度，除以2即得到列表的高度。

        introducePanel.setBounds(CustOpts.HOR_GAP, separator.getY() - tmpListHight - CustOpts.VER_GAP, getWidth() - 3
                * CustOpts.HOR_GAP, tmpListHight);

        scrollPane.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP + CustOpts.LBL_HEIGHT, getWidth() - 3
                * CustOpts.HOR_GAP, tmpListHight);

        label.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - 3 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);

        validate(); // 确保所有组件被重绘，否则尤其JList容易绘制不即时。
    }

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    public void valueChanged(
            ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) // @NOTE:在选择列表中的某一项时，该方法会被调用两次。所以
        { // 用该判断去掉一次（因为发现两次传入的e只有这各属性不同)。
            Object tmpKey = list.getSelectedValue(); // 之所以选择不是“adjusting”的（即加‘！’号）,是因为用代
            area.setText(CASControl.ctrl.getImportIntrStr(tmpKey)); // 码setIndex(n)设置选中时该属性是false的。
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
     * Invoked when an action occurs. 确定动作 打开文件选取器
     */
    public void actionPerformed(
            ActionEvent e) {
        if (e.getSource() == cancel) {
            setVisible(false);
            dispose();
        } else if (e.getSource() == ok) {
            Object tmpKey = list.getSelectedValue();
            CASControl.ctrl.execImport(tmpKey);
        }
    }

    /**
     * 确保无论以何种方法关闭窗口，都要调dispose方法，否则对话盒实例将被一直JNI引用，无法释放内存。
     * 
     * @see #setDefaultCloseOperation
     */
    protected void processWindowEvent(
            WindowEvent e) {
        super.processWindowEvent(e);

        if (e.getID() == WindowEvent.WINDOW_CLOSING)
            dispose();
    }

    private JLabel label;
    private JList list;
    private JScrollPane scrollPane;
    private JTextArea area;
    private JPanel introducePanel;
    private JSeparator separator;
    private JButton ok;
    private JButton cancel;
}
