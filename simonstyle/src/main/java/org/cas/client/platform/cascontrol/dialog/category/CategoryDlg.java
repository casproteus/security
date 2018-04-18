package org.cas.client.platform.cascontrol.dialog.category;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.bar.dialog.BarDlgConst;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarGeneralPanel;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.CategoryDialogConstants;
import org.cas.client.resource.international.DlgConst;

public class CategoryDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener {
	BarGeneralPanel parentPanel;
    String name;
    int index;

    public CategoryDlg(BarFrame pParent) {
        super(pParent, true);
        parentPanel = pParent.general;
        initDialog();
    }

    public void setText(
            String name) {
        this.name = name;
        this.general.tfdCategoryName.setText(name);
    }

    public void setIndex(
            int index) {
        this.index = index;
        this.general.dspIndex.setText(String.valueOf(index));
    }

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        cancel.setBounds(getContainer().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getContainer().getHeight()
                - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP, CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);// 关闭
        general.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getContainer().getWidth() - 2 * CustOpts.HOR_GAP,
                (ok.getY()) - 2 * CustOpts.VER_GAP);
        general.componentResized(null);
        validate();
    }

    @Override
    public PIMRecord getContents() {
        return null;
    }

    @Override
    public boolean setContents(
            PIMRecord prmRecord) {
        return true;
    }

    @Override
    public void makeBestUseOfTime() {
    }

    @Override
    public void addAttach(
            File[] file,
            Vector actualAttachFiles) {
    }

    @Override
    public PIMTextPane getTextPane() {
        return null;
    }

    @Override
    public void release() {
        ok.removeActionListener(this);
        cancel.removeActionListener(this);
        if (general != null) {
            general.removeAll();
            general = null;
        }
        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /** Invoked when the component's position changes. */
    @Override
    public void componentMoved(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made visible. */
    @Override
    public void componentShown(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made invisible. */
    @Override
    public void componentHidden(
            ComponentEvent e) {
    };

    /**
     * Invoked when an action occurs. NOTE:PIM的绝大多数用于新建和编辑的对话盒，对于确定事件的处理，采用如下规则：
     * 即：先出发监听器事件，监听器根据IPIMDialog接口的方法getContent（）取出对话盒中的 记录。监听器负责将记录存入Model，监听器最后负责将对话盒释放。
     * 目的是让所有对话盒只认识一个叫Record的东西，不认识别的。
     * 
     * @param e
     *            动作事件
     */
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == ok) {
            String tCategory = general.tfdCategoryName.getText();

            if (!tCategory.equalsIgnoreCase(name)) { // if changed, need to check if new value duplicated with others.
                for (int i = 0; i < general.categoryNameAry.length; i++) {
                    if (tCategory.equalsIgnoreCase(general.categoryNameAry[i])) {
                        JOptionPane.showMessageDialog(this, DlgConst.CategoryNameInUse);
                        general.tfdCategoryName.setText("");
                        general.tfdCategoryName.grabFocus();
                        return;
                    }
                }
            }

            String sql =
                    "INSERT INTO Category(NAME, DSP_INDEX) VALUES('".concat(general.tfdCategoryName.getText())
                            .concat("', ").concat(general.dspIndex.getText()).concat(")");

            try {
                Connection conn = PIMDBModel.getConection();
                Statement smt = conn.createStatement();

                int newIndex = Integer.valueOf(general.dspIndex.getText()); // display must be a integer
                if (newIndex != index) { // index modified, need to modify affected categories
                    if (newIndex > index) {
                        for (int i = index + 1; i <= newIndex; i++) { // make index smaller
                            sql =
                                    "UPDATE Category SET DSP_INDEX = ".concat(String.valueOf(i - 1))
                                            .concat(" where DSP_INDEX = ").concat(String.valueOf(i)).concat("");
                            smt.executeUpdate(sql.toString());
                        }
                    } else {
                        for (int i = newIndex; i < index; i++) { // make index bigger
                            sql =
                                    "UPDATE Category SET DSP_INDEX = ".concat(String.valueOf(i + 1))
                                            .concat(" where DSP_INDEX = ").concat(String.valueOf(i)).concat("");
                            smt.executeUpdate(sql.toString());
                        }
                    }
                }

                if (name != null) {
                    sql =
                            "UPDATE Category SET NAME = '".concat(general.tfdCategoryName.getText())
                                    .concat("', DSP_INDEX = ").concat(general.dspIndex.getText())
                                    .concat(" where NAME = '").concat(name).concat("'");
                }

                smt.executeUpdate(sql.toString());
                smt.close();
                smt = null;
                parentPanel.initCategoryAndMenus();
                parentPanel.reLayout();
                dispose();
            } catch (Exception exception) {
                exception.printStackTrace();
                return;
            }
        } else if (o == cancel) {
            dispose();
        }
    }

    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(CategoryDialogConstants.CATEGORYEditorTITLE);
        setModal(true);
        setResizable(false);

        // 初始化－－－－－－－－－－－－－－－－
        general = new GeneralPanel();
        ok = new JButton(DlgConst.OK);
        cancel = new JButton(DlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－
        ok.setFocusable(false);
        cancel.setFocusable(false);
        ok.setMnemonic('o');
        cancel.setMnemonic('c');
        ok.setMargin(new Insets(0, 0, 0, 0));
        cancel.setMargin(ok.getMargin());
        getRootPane().setDefaultButton(ok);
        // 布局---------------
        int tHight =
                general.getPreferredSize().height + CustOpts.BTN_HEIGHT + 2 * CustOpts.VER_GAP + CustOpts.SIZE_EDGE
                        + CustOpts.SIZE_TITLE;
        setBounds((CustOpts.SCRWIDTH - 260) / 2, (CustOpts.SCRHEIGHT - tHight) / 2, 260, tHight); // 对话框的默认尺寸。
        getContentPane().setLayout(null);
        // 搭建－－－－－－－－－－－－－
        getContentPane().add(general);
        getContentPane().add(ok);
        getContentPane().add(cancel);

        // 加监听器－－－－－－－－
        ok.addActionListener(this);
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);
    }

    private JButton ok;
    private JButton cancel;
    private GeneralPanel general;

    // ==========================================================================
    private class GeneralPanel extends JPanel implements ComponentListener {

        public GeneralPanel() {
            initConponent();
        }

        /** Invoked when the component's size changes. */
        @Override
        public void componentResized(
                ComponentEvent e) {
            reLayout();
        }

        /** Invoked when the component's position changes. */
        @Override
        public void componentMoved(
                ComponentEvent e) {
        }

        /** Invoked when the component has been made visible. */
        @Override
        public void componentShown(
                ComponentEvent e) {
        }

        /** Invoked when the component has been made invisible. */
        @Override
        public void componentHidden(
                ComponentEvent e) {
        }

        private void initConponent() {
            lblCategoryName = new JLabel(CategoryDialogConstants.NAME);
            lblPosition = new JLabel(BarDlgConst.DSPINDEX);

            tfdCategoryName = new JTextField();
            dspIndex = new JTextField();

            // properties
            setLayout(null);

            initContent();
            reLayout();
            // built
            add(lblCategoryName);
            add(tfdCategoryName);
            add(lblPosition);
            add(dspIndex);
            addComponentListener(this);
        }

        private void initContent() {
            String sql = "select ID, NAME, DSP_INDEX from CATEGORY";
            try {
                ResultSet rs =
                        PIMDBModel.getConection()
                                .createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY)
                                .executeQuery(sql);
                ResultSetMetaData rd = rs.getMetaData(); // 得到结果集相关信息

                rs.afterLast();
                rs.relative(-1);
                int tmpPos = rs.getRow();
                idAry = new int[tmpPos];
                categoryNameAry = new String[tmpPos];
                indexAry = new int[tmpPos];
                rs.beforeFirst();
                tmpPos = 0;
                while (rs.next()) {
                    idAry[tmpPos] = rs.getInt("id");
                    categoryNameAry[tmpPos] = rs.getString("NAME");
                    indexAry[tmpPos] = rs.getInt("DSP_INDEX");
                    tmpPos++;
                }
                rs.close();// 关闭
            } catch (SQLException e) {
                ErrorUtil.write(e);
            }
        }

        /** 本方法用于设置View上各个组件的尺寸。 */
        public void reLayout() {
            int prmWidth = getWidth();
            lblCategoryName.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, lblCategoryName.getPreferredSize().width,
                    CustOpts.BTN_HEIGHT);
            tfdCategoryName.setBounds(lblCategoryName.getX() + lblCategoryName.getWidth() + CustOpts.HOR_GAP,
                    lblCategoryName.getY(), prmWidth - lblCategoryName.getWidth() - CustOpts.HOR_GAP * 3,
                    CustOpts.BTN_HEIGHT);
            lblPosition.setBounds(lblCategoryName.getX(), lblCategoryName.getY() + lblCategoryName.getHeight()
                    + CustOpts.VER_GAP, lblCategoryName.getWidth(), CustOpts.BTN_HEIGHT);
            dspIndex.setBounds(tfdCategoryName.getX(), lblPosition.getY(), tfdCategoryName.getWidth(),
                    CustOpts.BTN_HEIGHT);

            setPreferredSize(new Dimension(getWidth(), dspIndex.getY() + dspIndex.getHeight() + CustOpts.VER_GAP));
        }

        JLabel lblCategoryName;
        JLabel lblPosition;

        JTextField tfdCategoryName;
        JTextField dspIndex;

        int[] idAry;
        String[] categoryNameAry;
        int[] indexAry;
    }
}
