package org.cas.client.platform.bar.dialog;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.uibeans.CategoryToggleButton;
import org.cas.client.platform.bar.uibeans.MenuButton;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;
import org.cas.client.resource.international.DlgConst;

//Identity表应该和Employ表合并。
public class SalesPanel extends JPanel implements ComponentListener, FocusListener {

	String[][] categoryNameMetrix;
    ArrayList<ArrayList<CategoryToggleButton>> onSrcCategoryTgbMatrix = new ArrayList<ArrayList<CategoryToggleButton>>();
    CategoryToggleButton tgbActiveCategory;
    
    //Dish is more complecated than category, it's devided by category first, then divided by page.
    String[][] dishNameMetrix;// the struction must be [3][index]. it's more convenient than [index][3]
    String[][] onScrDishNameMetrix;// it's sub set of all menuNameMetrix
    private ArrayList<ArrayList<MenuButton>> onSrcMenuBtnMatrix = new ArrayList<ArrayList<MenuButton>>();

    //for print
    public static String SUCCESS = "0";
    public static String ERROR = "2";
    
    //tempral flags
    public boolean partialPaid;	//for indicating that money is partial paid, when leaving sales panel, will give a notice!
    
    public SalesPanel() {
        initComponent();
    }

    // ComponentListener-----------------------------
    /** Invoked when the component's size changes. */
    @Override
    public void componentResized(
            ComponentEvent e) {
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

    @Override
    public void focusGained(FocusEvent e) {
        Object o = e.getSource();
        if (o instanceof JTextField)
            ((JTextField) o).selectAll();
    }

    @Override
    public void focusLost(FocusEvent e) {}

	public void discountBill(float discount) {
		if(!billPanel.checkStatus()) {
			return;
		}
		billPanel.discount = discount > billPanel.subTotal ? billPanel.subTotal : discount;
		billPanel.updateTotleArea();
		
		billPanel.createAndPrintNewOutput();
		billPanel.billPricesUpdateToDB();
	}

	//Todo: Maybe it's safe to delete this method, because I think no need to touch ouputs.
//	private void reopenOutput() {
//		//convert the status of relevant output.
//		StringBuilder sql = new StringBuilder("update output set deleted = ").append(DBConsts.original)
//				.append(" where deleted = ").append(DBConsts.voided)
//				.append(" and category = ").append(billPanel.billID);
//		try {
//			PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
//		}catch(Exception exp) {
//			L.e("SalesPane", "Exception happenned when converting output's status to 0", exp);
//		}
//	}

	public static void updateBill(int billId, String fieldName, int value) {
		StringBuilder sb = new StringBuilder("update bill set ").append(fieldName).append(" = ").append(value).append(" where id = ").append(billId);
		
		try {
			PIMDBModel.getStatement().executeUpdate(sb.toString());
		}catch(Exception e) {
			ErrorUtil.write(e);
		}
	}
	
	public void discountADish(int value, Dish mostExpensiveDish) throws SQLException {
		if(!billPanel.checkStatus()) {
			return;
		}
		int outputID = mostExpensiveDish.getOutputID();
		if(outputID >= 0) {
			StringBuilder sql = new StringBuilder("update output set discount = ").append(value)
					.append(", toltalprice = ").append(Math.round(mostExpensiveDish.getTotalPrice() - value))
					.append(" where id = ").append(outputID);
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}
		
		billPanel.updateTotleArea();
		billPanel.createAndPrintNewOutput();
		billPanel.billPricesUpdateToDB();
	}
	
	public void removeItem() {
		if(BillListPanel.curDish == null) {//check if there's an item selected.
			JOptionPane.showMessageDialog(this, BarFrame.consts.OnlyOneShouldBeSelected());
			return;
		}
		if(BillListPanel.curDish.getOutputID() >= 0) {//check if it's send
			if(JOptionPane.showConfirmDialog(BarFrame.instance, BarFrame.consts.COMFIRMDELETEACTION(), DlgConst.DlgTitle, JOptionPane.YES_NO_OPTION) != 0)
				return;
			//clean output from db.
			//No need to do it, will be called in method removeFromSelection() BillListPanel.curDish.changeOutputStatus(DBConsts.deleted);
			//send cancel message to kitchen
			BillListPanel.curDish.setCanceled(true);	//set the dish with cancelled flag, so when it's printout, will with "!!!!!".
			billPanel.sendDishToKitchen(BillListPanel.curDish, true);
			//clean from screen.
			billPanel.removeFromSelection(billPanel.table.getSelectedRow());
			//update bill info, must be after the screen update, because will get total from screen.
			BillPanel.updateBillRecordPrices(billPanel);
		}else {
			//only do clean from screen, because the output not generated yet, and will not affect the toltal in bill.
			billPanel.removeFromSelection(billPanel.table.getSelectedRow());
		}
	}

//    public static void resetCurTable(){
//    	try {
//            //clean all empty bill (match table id and opentime, status is null, while doesn't exist in any output.).
//            //if there's an output was deleted from this bill, this bill is still considered as empty.
//            //if there's an output was completed 10
////            sql = new StringBuilder("update bill set status = ").append(DBConsts.deleted)
////            		.append(" WHERE bill.id IN ( SELECT id FROM bill WHERE tableID = ").append(BarFrame.instance.valCurTable.getText())
////    				.append(" and OPENTIME = '").append(BarFrame.instance.valStartTime.getText())
////    				.append("' and status IS NULL OR status = ").append(DBConsts.original)
////    				.append(") AND NOT EXISTS (SELECT category FROM OUTPUT WHERE (deleted IS null or deleted = ").append(DBConsts.completed)
////    				.append(" AND time = '").append(BarFrame.instance.valStartTime.getText())
////    				.append("' and SUBJECT = '").append(BarFrame.instance.valCurTable.getText()).append("')");
//            
//            //no need to be complex, all ortiginal status bills of this table should be cleaned.
//            //close table
//            BarFrame.instance.closeATable(BarFrame.instance.cmbCurTable.getSelectedItem().toString(),
//            		BarFrame.instance.valStartTime.getText());
//    	}catch(Exception exp) {
//    		ErrorUtil.write(exp);
//    	}
//    }
    
    void reLayout() {
        // command buttons--------------
        int top = BarUtil.layoutCommandButtons(this, CommandBtnDlg.groupedButtons[2]);
        if(top < 0) {
        	return;
        }
        // TOP part============================
        int topAreaHeight = top - 3 * CustOpts.VER_GAP;

        billPanel.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP,
                (int) (getWidth() * (1 - BarOption.getMenuAreaPortion())), topAreaHeight);
        
        // menu area--------------
        int xMenuArea = billPanel.getX() + billPanel.getWidth() + CustOpts.HOR_GAP;
        int widthMenuArea = getWidth() - billPanel.getWidth() - CustOpts.HOR_GAP * 2 - CustOpts.SIZE_EDGE;

        BarFrame.menuPanel.setBounds(xMenuArea, billPanel.getY(), widthMenuArea, topAreaHeight);
        BarFrame.menuPanel.reLayout();

        billPanel.resetColWidth(billPanel.getWidth());
    }

    void initComponent() {
    	removeAll();	//when it's called by setting panel(changed colors...), it will be called to refresh.
        
        billPanel = new BillPanel(this);
        // properties
        Color bg = BarOption.getBK("Sales");
    	if(bg == null) {
    		bg = new Color(216,216,216);
    	}
		setBackground(bg);
        setLayout(null);
        
        // built
        BarUtil.addFunctionButtons(this, CommandBtnDlg.groupedButtons[2]);
        
        add(billPanel);
        // add listener
        addComponentListener(this);

        // 因为考虑到条码经常由扫描仪输入，不一定是靠键盘，所以专门为他加了DocumentListener，通过监视内容变化来自动识别输入完成，光标跳转。
        // tfdProdNumber.getDocument().addDocumentListener(this); // 而其它组件如实收金额框不这样做为了节约（一个KeyListener接口全搞定）
		reLayout();
    }

    public BillPanel billPanel;
}
