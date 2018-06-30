package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class BillFootDialog extends JDialog implements ActionListener, ComponentListener {

    /**
     * 创建一个 Category 的实例
     * 
     * @param prmParent
     *            父窗体
     * @param prmCategoryInfo
     *            逗号分隔的字符串
     */
    public BillFootDialog(Frame prmParent, String prmCategoryInfo) {
        super(prmParent, true);
        initComponent(); // 组件初始化并布局
        initContent(prmCategoryInfo); // 初始化文本区和列表框数据
    }

    /** Invoked when the component's size changes. */
    @Override
	public void componentResized(ComponentEvent e) {
        reLayout();
    }

    /** Invoked when the component's position changes. */
    @Override
	public void componentMoved(ComponentEvent e) {}

    /** Invoked when the component has been made visible. */
    @Override
	public void componentShown(ComponentEvent e) {}

    /** Invoked when the component has been made invisible. */
    @Override
	public void componentHidden(ComponentEvent e) {}

    /** 初始化并布局; */
    private void initComponent() {
        setTitle(BarFrame.consts.BillInfo()); // 设置标题
        getContentPane().setLayout(null);
        setBounds((CustOpts.SCRWIDTH - 350) / 2, (CustOpts.SCRHEIGHT - 320) / 2, 350, 320); // 对话框的默认尺寸。
        setResizable(true);

        // init--------------------------
        txaCurContent = new JTextArea(); // 加入会滚动的文本区
        topLabel = new JLabel(BarFrame.consts.BillInfo());
        btnOK = new JButton(BarFrame.consts.OK()); // 设置Cancel按钮

        // properties-------------------------
        topLabel.setLabelFor(txaCurContent);
        txaCurContent.setBorder(new LineBorder(Color.GRAY));
        topLabel.setDisplayedMnemonic('I');

        // layout---------------------------
        reLayout();

        // build----------------------------
        getContentPane().add(txaCurContent);
        getContentPane().add(topLabel);
        getContentPane().add(btnOK);

        // listeners------------------------
        btnOK.addActionListener(this);
        getContentPane().addComponentListener(this);
    }

    private void reLayout() {
        btnOK.setBounds(getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.BTN_WIDTH - 2 * CustOpts.HOR_GAP, 
        		getHeight() - CustOpts.BTN_HEIGHT - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - 2 * CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);

        if(BillListPanel.curDish == null) {
            topLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
            		btnOK.getX() + btnOK.getWidth(), CustOpts.LBL_HEIGHT);
            txaCurContent.setBounds(topLabel.getX(), topLabel.getY() + CustOpts.LBL_HEIGHT, 
            		getWidth() - 2 * CustOpts.SIZE_EDGE - 3 * CustOpts.HOR_GAP,
                    CustOpts.LBL_HEIGHT * 11);
        }else {
        	topLabel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 0, 0);
        	txaCurContent.setBounds(0, 0, 0, 0);
        }
    }

    /** 初始化时使用 */
    private void initContent( String prmCategoryInfo) {
        txaCurContent.setText("null".equalsIgnoreCase(prmCategoryInfo) ? "" : prmCategoryInfo);

        // 把文本框中字段还原为字符串数组
        ArrayList<String> allModification = getAllModification();
    }
    
    private ArrayList<String> getAllModification() {
        String sql = "SELECT * FROM modification where status = 0";
        ArrayList<String> nameVec = new ArrayList<String>();
        try {

            Statement smt = PIMDBModel.getReadOnlyStatement();
            ResultSet rs = smt.executeQuery(sql);
            while (rs.next()) {
            	StringBuilder sb = new StringBuilder(rs.getString("lang1")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang2")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang3")).append(BarDlgConst.semicolon);
            	sb.append(rs.getString("lang4"));
                nameVec.add(sb.toString());
            }

            // 关闭
            smt.close();
            smt = null;
            rs.close();
            rs = null;

            return nameVec;
            
        } catch (SQLException e) {
            ErrorUtil.write(e);
            return null;
        }
    }
    
    /**
     * 解析一个逗号分隔符处理的字符串 getTextAreaData
     */
    private String[] stringToArray( String string) {
        if (string != null) {
            // 构建字符串分隔器
            StringTokenizer token = new StringTokenizer(string, BarDlgConst.delimiter);
            int size = token.countTokens();
            // 构建相应容量的字符串数组
            String[] indexes = new String[size];
            size = 0;
            // 循环加入,去掉空格的
            while (token.hasMoreTokens()) {
                indexes[size] = token.nextToken().trim();
                size++;
            }
            return indexes;
        } else {
            return null;
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    @Override
	public void actionPerformed(
            ActionEvent e) {
         if (e.getSource() == btnOK) {
            BarOption.setBillFootInfo(txaCurContent.getText());
            dispose();
        }
    }

    private boolean insertModification( String modification) {
        StringBuilder sql = new StringBuilder("INSERT INTO modification (lang1, lang2, lang3, lang4, status) VALUES( '");
        String[] langs = modification.split(BarDlgConst.semicolon);
        sql.append(langs.length > 0 ? langs[0] : "");
        sql.append("', '");
        sql.append(langs.length > 1 ? langs[1] : "");
        sql.append("', '");
        sql.append(langs.length > 2 ? langs[2] : "");
        sql.append("', '");
        sql.append(langs.length > 3 ? langs[3] : "");
        sql.append("', 0);");

        try {
            Statement smt = PIMDBModel.getStatement();
            int rows = smt.executeUpdate(sql.toString());

            // 关闭
            smt.close();
            smt = null;

            return rows != 0;
        } catch (SQLException e) {
        	ErrorUtil.write(e);
            return false;
        }
    }
    
    private boolean updateToModification(String modification){
        String[] langs = modification.split(BarDlgConst.semicolon);
    	StringBuilder sql = new StringBuilder("update modification set lang2 = '");
        sql.append(langs.length > 1 ? langs[1] : "");
        sql.append("', lang3 = '");
        sql.append(langs.length > 2 ? langs[2] : "");
        sql.append("', lang4 = '");
        sql.append(langs.length > 3 ? langs[3] : "");
        sql.append("' where lang1 = '").append(langs[0]).append("';");

        try {
            Statement smt = PIMDBModel.getStatement();
            int rows = smt.executeUpdate(sql.toString());
            // 关闭
            smt.close();
            smt = null;

            return rows != 0;
        } catch (SQLException e) {
        	ErrorUtil.write(e);
            return false;
        }
    }
    
    // 以下为本类的变量声明
    private JButton btnOK;
    private JLabel topLabel; // "项目属于这些类别"标签
    private JTextArea txaCurContent; // 文本区及其模型
}
