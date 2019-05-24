package org.cas.client.platform.bar.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.util.Date;

import javax.swing.JOptionPane;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.CmdBtnsDlg;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.uibeans.FunctionToggleButton;
import org.cas.client.platform.bar.uibeans.ISButton;
import org.cas.client.platform.bar.uibeans.SamActionListener;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class Cmd_Other implements SamActionListener {

	private static Cmd_Other instance;
	private Cmd_Other() {}
	public static Cmd_Other getInstance() {
		if(instance == null)
			instance = new Cmd_Other();
		return instance;
	}
	
	private ISButton sourceBtn;
	
	public ISButton getSourceBtn() {
		return sourceBtn;
	}
	@Override
	public void setSourceBtn(ISButton sourceBtn) {
		this.sourceBtn = sourceBtn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		FunctionToggleButton o = (FunctionToggleButton)e.getSource();
		
		String giftCardNumber  = JOptionPane.showInputDialog(null, BarFrame.consts.Account());
		if(giftCardNumber == null || giftCardNumber.length() == 0)
			return;
		
		StringBuilder sql = new StringBuilder("SELECT * from hardware where category = 2 and name = '").append(giftCardNumber)
				.append("' and (status is null or status = 0)");
		try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            if(tmpPos == 0) {	//if there's no this coupon number in database, then warning and return.
            	JOptionPane.showMessageDialog(BarFrame.instance, BarFrame.consts.InvalidCoupon());
            	return;
            }else {			//if the number is OK.
            	//get out every field of first matching record.
            	rs.beforeFirst();
                tmpPos = 0;
                rs.next();
                int id = rs.getInt("id");
                int category = rs.getInt("style");
                String productCode = rs.getString("IP");
                int value = rs.getInt("langType");
                if(value <=0) {
                    JOptionPane.showMessageDialog(null, BarFrame.consts.InvalidCoupon());
                    return;
                }
                //show up the payDialog, waiting for user to input money, after confirm, the money should be deduct from the account of this card
                BarFrame.payDlg.maxInput = (float)(value / 100.0);
                BarFrame.setStatusMes(BarFrame.consts.CurrentBalanceMsg() + BarFrame.payDlg.maxInput);
                Cmd_Pay.getInstance().actionPerformed(new ActionEvent(o, 0, ""));
                
                if (BarFrame.payDlg.inputedContent != null && BarFrame.payDlg.inputedContent.length() > 0) {
                    float newBalance = (float)(value / 100.0) - Float.valueOf(BarFrame.payDlg.inputedContent);
	                sql = new StringBuilder("update hardware set status = ").append(DBConsts.expired)
                    		.append(" where id = ").append(id);
                    PIMDBModel.getStatement().executeUpdate(sql.toString());
					sql = new StringBuilder("INSERT INTO Hardware (name, category, langType, ip, style, status) VALUES ('").append(giftCardNumber)
					    .append("', 2, ").append(Math.round(newBalance * 100)).append(", '")
					    .append(BarOption.df.format(new Date())).append("', ").append(LoginDlg.USERID).append(", ")
					    .append(DBConsts.original).append(")");
					PIMDBModel.getStatement().executeUpdate(sql.toString());
                }
            }
		}catch(Exception exp) {
			L.e("Redeem Coupon", "exception happend when redeem coupon: " + sql, exp);
		}
    	
	}
}
