package org.cas.client.platform.bar.uibeans;

import java.awt.Color;
import java.awt.Image;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.dialog.TableDlg;
import org.cas.client.platform.cascustomize.CustOptsConsts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.resource.international.PaneConsts;

public class TableButton extends JButton {
	
    public static Color colorSelected = new Color(200, 200, 200);
    public static Color colorDefault = new Color(255, 255, 255);
    
    private int id = -1;
    private int type;
    private String openTime;

	static String folerPath = CASUtility.getPIMDirPath();
	
	List<Integer> typeList = Arrays.asList(TableDlg.typeAry);
	
    public TableButton() {
    	setBorder(null);
    	setFont(BarOption.lessBigFont);
        Color bg = BarOption.getBK("Table");
    	setBackground(bg);
    	setHorizontalTextPosition(CENTER);
    }

    public int getId() {
        return id;
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		if(type < 0) {
			type = -1 * type;
		}
		this.type = type;
		
		if(type != 0) {
			Image temp = PIMPool.pool.getImage(PaneConsts.IAMGE_PATH.concat("T".concat(String.valueOf(type)).concat(".png")));
	        ImageIcon icon = new ImageIcon(temp);
			
	    	setIcon(icon);
	    	int p = typeList.indexOf(type);
	    	setSize(TableDlg.widthAry[p], TableDlg.heightAry[p]);
		}
	}

	public void setId(int index) {
		this.id = index;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public void open() {
		setBackground(colorSelected);
		String openTime = BarOption.df.format(new Date());
		setOpenTime(openTime);
		
		BarFrame.instance.openATable(getText(), openTime);
		BarFrame.instance.setCurBillID(BarFrame.instance.createAnEmptyBill(getText(), openTime, 0));
	}

}
