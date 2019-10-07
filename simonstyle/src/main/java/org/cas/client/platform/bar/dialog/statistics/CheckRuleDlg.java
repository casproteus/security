package org.cas.client.platform.bar.dialog.statistics;

import java.awt.Color;
import java.awt.Container;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.SalesPanel;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.uibeans.ArrowButton;
import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.cascontrol.dialog.ICASDialog;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.DefaultPIMTableCellRenderer;
import org.cas.client.platform.pimview.pimtable.IPIMTableColumnModel;
import org.cas.client.platform.pimview.pimtable.PIMTable;

public class CheckRuleDlg extends JDialog implements ICASDialog, ActionListener, ComponentListener, KeyListener {
    
	private boolean isEditingBillID;
	
    /**
     * Creates a new instance of ContactDialog
     * 
     * @called by PasteAction 为Copy邮件到联系人应用。
     */
    public CheckRuleDlg(JFrame pParent) {
        super(pParent, true);
        initDialog();
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(
            KeyEvent e) {
        Object o = e.getSource();
        switch (e.getKeyCode()) {
            case 37:
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn() - 1);
                break;
            case 38:
                tblContent.setSelectedRow(tblContent.getSelectedRow() - 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
                break;
            case 39:
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn() + 1);
                break;
            case 40:
                tblContent.setSelectedRow(tblContent.getSelectedRow() + 1);
                tblContent.scrollToRect(tblContent.getSelectedRow(), tblContent.getSelectedColumn());
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {}

    /*
     * 对话盒的布局独立出来，为了在对话盒尺寸发生改变后，界面各元素能够重新布局， 使整体保持美观。尤其在Linux系列的操作系统上，所有的对话盒都必须准备好应对用户的拖拉改变尺寸。
     * @NOTE:因为setBounds方法本身不会触发事件导致重新布局，所以本方法中设置Bounds之后调用了reLayout。
     */
    @Override
    public void reLayout() {
        srpContent.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, 
        		getWidth() - CustOpts.SIZE_EDGE * 2 - CustOpts.HOR_GAP * 2, 
                getHeight() - CustOpts.SIZE_TITLE - CustOpts.SIZE_EDGE - CustOpts.VER_GAP * 3
                - CustOpts.BTN_WIDTH_NUM);
        
        int btnWidth = 80;
        btnDeLete.setBounds(CustOpts.HOR_GAP, btnDown.getY(),
                btnWidth, CustOpts.BTN_WIDTH_NUM);
        label.setBounds(btnDeLete.getX() + btnDeLete.getWidth() + CustOpts.HOR_GAP, btnDeLete.getY(), label.getPreferredSize().width, btnDeLete.getPreferredSize().height);
        btnUp.setBounds(getWidth() - btnWidth * 2 - CustOpts.HOR_GAP * 4, srpContent.getY() + srpContent.getHeight() + CustOpts.VER_GAP,
                btnWidth, CustOpts.BTN_WIDTH_NUM);
        btnDown.setBounds(btnUp.getX() + btnUp.getWidth() + CustOpts.HOR_GAP, btnUp.getY(),
                btnWidth, CustOpts.BTN_WIDTH_NUM);
        
        
        IPIMTableColumnModel tTCM = tblContent.getColumnModel();
        tTCM.getColumn(0).setPreferredWidth(40);	//BarFrame.consts.TIME
        tTCM.getColumn(1).setPreferredWidth(230);	//BarFrame.consts.Table,
        tTCM.getColumn(2).setPreferredWidth(360);	//BarFrame.consts.Bill
        tTCM.getColumn(3).setPreferredWidth(60);	//BarFrame.consts.Total
        tTCM.getColumn(4).setPreferredWidth(60);	//BarFrame.consts.Discount
        validate();
    }

    @Override
    public PIMRecord getContents() {return null;}

    @Override
    public boolean setContents( PIMRecord prmRecord) {return true;}

    @Override
    public void makeBestUseOfTime() {}

    @Override
    public void addAttach(File[] file, Vector actualAttachFiles) {}

    @Override
    public PIMTextPane getTextPane() {return null;}

    @Override
    public void release() {
        btnUp.removeActionListener(this);
        btnDown.removeActionListener(this);
        btnDeLete.removeActionListener(this);

        dispose();// 对于对话盒，如果不加这句话，就很难释放掉。
        System.gc();// @TODO:不能允许私自运行gc，应该改为象收邮件线程那样低优先级地自动后台执行，可以从任意方法设置立即执行。
    }

    @Override
    public void componentResized(ComponentEvent e) {
        reLayout();
    };

    @Override
    public void componentMoved(ComponentEvent e) {};

    @Override
    public void componentShown(ComponentEvent e) {};

    @Override
    public void componentHidden(ComponentEvent e) {};

    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();

    	int selectedRow = tblContent.getSelectedRow();
    	int tValidRowCount = getUsedRowCount();
    	if(selectedRow < 0 || selectedRow > tValidRowCount - 1) {
    		JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
    		ErrorUtil.write("Unexpected row number when calling removeAtSelection : " + selectedRow);
    		return;
    	}
    	
        if (o == btnUp) {
        	StringBuilder sql = new StringBuilder("update CustomizedRule set dspIdx = dspIdx -1 where ruleName = '").append(tblContent.getValueAt(selectedRow, 1))
    	        	.append("' and content = '").append(tblContent.getValueAt(selectedRow, 2)).append("'");
        	try {
		    	PIMDBModel.getStatement().executeUpdate(sql.toString());
			}catch(Exception exp) {
				L.e("counter mode returning... ", "error happend when deleting an rule with sql:" + sql, exp);
			}	
        }else if(o == btnDown) {
        	StringBuilder sql = new StringBuilder("update CustomizedRule set dspIdx = dspIdx + 1 where ruleName = '").append(tblContent.getValueAt(selectedRow, 1))
    	        	.append("' and content = '").append(tblContent.getValueAt(selectedRow, 2)).append("'");
        	try {
		    	PIMDBModel.getStatement().executeUpdate(sql.toString());
			}catch(Exception exp) {
				L.e("counter mode returning... ", "error happend when deleting an rule with sql:" + sql, exp);
			}
        }else if(o == btnDeLete) {
        	if(tblContent.getSelectedRow() < 0) {
        		JOptionPane.showConfirmDialog(null, BarFrame.consts.OnlyOneShouldBeSelected());
        		return;
        	}
        	StringBuilder sql = new StringBuilder("delete from CustomizedRule where ruleName = '").append(tblContent.getValueAt(selectedRow, 1))
	        	.append("' and content = '").append(tblContent.getValueAt(selectedRow, 2)).append("'");
        	try {
		    	PIMDBModel.getStatement().executeUpdate(sql.toString());
			}catch(Exception exp) {
				L.e("counter mode returning... ", "error happend when deleting an rule with sql:" + sql, exp);
			}		
        }
        BarFrame.menuPanel.initRules();
        this.initContent();
    }
	
    private int getUsedRowCount() {
        for (int i = 0, len = tblContent.getRowCount(); i < len; i++)
            if (tblContent.getValueAt(i, 2) == null)
                return i; // 至此得到 the used RowCount。
        return tblContent.getRowCount();
    }
    
    @Override
    public Container getContainer() {
        return getContentPane();
    }

    private void initDialog() {
        setTitle(BarFrame.consts.Rule());
        setModal(false);

        // 初始化－－－－－－－－－－－－－－－－
        tblContent = new PIMTable();// 显示字段的表格,设置模型
        srpContent = new PIMScrollPane(tblContent);

        btnDeLete = new JButton(BarFrame.consts.Delete());
        label = new JLabel("Disc less than 0.6 will be considered as persentage.");
        btnUp = new ArrowButton("↑");
        btnDown = new ArrowButton("↓");
        // properties
        btnUp.setMnemonic('A');
        btnUp.setMargin(new Insets(0, 0, 0, 0));
        btnDown.setMnemonic('P');
        btnDown.setMargin(new Insets(0, 0, 0, 0));
        
        tblContent.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tblContent.setAutoscrolls(true);
        tblContent.setRowHeight(30);
        tblContent.setBorder(new JTextField().getBorder());
        tblContent.setFocusable(false);

        JLabel tLbl = new JLabel();
        tLbl.setOpaque(true);
        tLbl.setBackground(Color.GRAY);
        srpContent.setCorner(JScrollPane.LOWER_RIGHT_CORNER, tLbl);

        // 布局---------------
        setBounds((CustOpts.SCRWIDTH - 780) / 2, (CustOpts.SCRHEIGHT - 500) / 2, 780, 500); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(srpContent);

        getContentPane().add(btnDeLete);
        getContentPane().add(label);
        getContentPane().add(btnUp);
        getContentPane().add(btnDown);
        // 加监听器－－－－－－－－
        btnUp.addActionListener(this);
        btnUp.addKeyListener(this);
        btnDown.addActionListener(this);
        btnDown.addKeyListener(this);
        btnDeLete.addActionListener(this);
        btnDeLete.addKeyListener(this);
        
        getContentPane().addComponentListener(this);
    }

    public void initContent() {
    	//NOTE: can not use the rules in menuPane as model, because in dialog will also display the deactived rules infuture. so might be different from the ary in menuPane.
        StringBuilder sql = new StringBuilder("select * from CustomizedRule where status != -100 order by dspidx");
        fillTableAreaWithResultSet(sql);
        reLayout();
    }

	private void fillTableAreaWithResultSet(StringBuilder sql) {
		Object[][] tValues = null;
		try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            tValues = new Object[tmpPos][header.length];
            rs.beforeFirst();
            tmpPos = 0;
            while (rs.next()) {
                tValues[tmpPos][0] = rs.getInt("dspIdx");;
                tValues[tmpPos][1] = rs.getString("ruleName");
                tValues[tmpPos][2] = rs.getString("content");
                tValues[tmpPos][3] = BarUtil.formatMoney(rs.getInt("action") / 100.0);
                tValues[tmpPos][4] = rs.getInt("status");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        tblContent.setDataVector(tValues, header);
        DefaultPIMTableCellRenderer tCellRender = new DefaultPIMTableCellRenderer();
        tCellRender.setOpaque(true);
        tCellRender.setBackground(Color.LIGHT_GRAY);
        tblContent.getColumnModel().getColumn(1).setCellRenderer(tCellRender);
	}
    
    private void initPordAndEmploy() {
        String sql = "select ID, UserName from UserIdentity";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            employIdAry = new int[tmpPos + 1];
            employNameAry = new String[tmpPos + 1];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                employIdAry[tmpPos] = rs.getInt("ID");
                employNameAry[tmpPos] = rs.getString("UserName");
                tmpPos++;
            }
            employIdAry[tmpPos] = -1;
            employNameAry[tmpPos] = "-";
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }

        // for the service.
        sql = "select ID, SUBJECT from Product";
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            prodIdAry = new int[tmpPos];
            prodNameAry = new String[tmpPos];
            rs.beforeFirst();

            tmpPos = 0;
            while (rs.next()) {
                prodIdAry[tmpPos] = rs.getInt("ID");
                prodNameAry[tmpPos] = rs.getString("SUBJECT");
                tmpPos++;
            }
            rs.close();// 关闭
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
    }

    private String[] header = new String[] {
    		BarFrame.consts.DSPINDEX(),
    		BarFrame.consts.Name(),
    		BarFrame.consts.comment(),
    		BarFrame.consts.Discount(),
    		BarFrame.consts.Status()
    };

    int[] employIdAry;
    String[] employNameAry;
    int[] prodIdAry;
    String[] prodNameAry;
    
    PIMTable tblContent;
    PIMScrollPane srpContent;

    private JButton btnDeLete;
    private JLabel label;
    private JButton btnUp;
    private JButton btnDown;
}
