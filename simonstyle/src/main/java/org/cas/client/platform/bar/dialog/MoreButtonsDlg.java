package org.cas.client.platform.bar.dialog;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

import org.cas.client.platform.bar.BarUtil;
import org.cas.client.platform.bar.action.Cmd_Pay;
import org.cas.client.platform.bar.dialog.statistics.CheckBillDlg;
import org.cas.client.platform.bar.dialog.statistics.ReportDlg;
import org.cas.client.platform.bar.i18n.BarDlgConst0;
import org.cas.client.platform.bar.i18n.BarDlgConst1;
import org.cas.client.platform.bar.i18n.BarDlgConst2;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.FunctionButton;
import org.cas.client.platform.bar.uibeans.MoreButton;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

public class MoreButtonsDlg extends JDialog implements WindowFocusListener{
	
    SalesPanel salesPanel = (SalesPanel)BarFrame.instance.panels[2];
	private ArrayList<JComponent> buttons;

    public MoreButtonsDlg(ArrayList<JComponent> buttons) {
    	super();
    	this.buttons = buttons;
    	setTitle(BarFrame.consts.MORE());
    	setIconImage(CustOpts.custOps.getFrameLogoImage());
        initPanel();
    }

	public void show(MoreButton btnMore) {
		reLayout(btnMore);
		this.setVisible(true);
	}
	
	private void reLayout(MoreButton btnMore) {
		int btnWidth = btnMore.getWidth();
		int btnHeight = btnMore.getHeight();
		
        // command buttons--------------
        int buttonQT = buttons.size();
        int row;
        int col;
        if(buttonQT <= 0) {
        	row = 0;
        	col = 0;
        }else if(buttonQT <= 8) {
        	col = 1;
        	row = buttonQT;
        }else {
        	col = 2;
        	if(buttonQT >= 16){
        		row = 8;
        	}else{
        		row = buttonQT%2 + buttonQT/2;
        	}
        }
        
        for(int i = 0; i < buttonQT; i++) {
        	buttons.get(i).setBounds(CustOpts.HOR_GAP + btnWidth * (i < row ? 0 : 1) + CustOpts.HOR_GAP * (i < row ? 0 : 1),
        			CustOpts.VER_GAP + btnHeight * (i < row ? i : i - row) + CustOpts.VER_GAP * (i < row ? i : i - row),
        			btnWidth, btnHeight);
        }
        
        //dialog		
		int width = btnWidth * col + CustOpts.HOR_GAP * (col + 1) + CustOpts.SIZE_EDGE * 2 + 10;
		int height = btnHeight * row + CustOpts.VER_GAP * (row + 1) + CustOpts.SIZE_EDGE * 2 + 40;
		int x = btnMore.getX() + salesPanel.getRootPane().getParent().getX();
		int y = salesPanel.getRootPane().getHeight() - 80 + salesPanel.getRootPane().getParent().getY();
		setBounds(x - width * (col - 1) + 40, y - height + 80, width, height);
	}
	
	private void initPanel() {
		// 布局---------------
		setLayout(null);
		BarUtil.addFunctionButtons(this.getContentPane(), buttons);
		this.addWindowFocusListener(this);
	}
	
	@Override
	public void windowGainedFocus(WindowEvent e) {}

	@Override
	public void windowLostFocus(WindowEvent e) {
		dispose();
	}
}
