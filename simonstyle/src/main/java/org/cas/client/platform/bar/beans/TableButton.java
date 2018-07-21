package org.cas.client.platform.bar.beans;

import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class TableButton extends JButton {
    private int id = -1;
    private int type;
    private String openTime;

    public TableButton() {
    	setBackground(BarOption.getBK("Table"));
    }

    public int getId() {
        return id;
    }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
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
