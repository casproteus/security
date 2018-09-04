package org.cas.client.platform.bar.beans;

import java.awt.Color;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.casutil.CASUtility;

public class TableButton extends JButton {
    private int id = -1;
    private int type;
    private String openTime;

	static String folerPath = CASUtility.getPIMDirPath().concat(System.getProperty("file.separator"));
	
    public TableButton() {
        Color bg = BarOption.getBK("Table");
    	setBackground(bg);
    	setHorizontalAlignment(CENTER);
    }

    public int getId() {
        return id;
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;

		ImageIcon icon = new ImageIcon(folerPath + type+ ".gif");
		Image temp = icon.getImage().getScaledInstance(getWidth(),
				getHeight(), icon.getImage().SCALE_DEFAULT);
		icon = new ImageIcon(temp);
		
    	setIcon(icon);
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

}
