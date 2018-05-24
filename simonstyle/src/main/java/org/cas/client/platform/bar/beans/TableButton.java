package org.cas.client.platform.bar.beans;

import javax.swing.JButton;

public class TableButton extends JButton {
    private int id;
    private int type;
    private String openTime;

    public TableButton() {
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
