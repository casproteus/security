package org.cas.client.platform.bar.beans;

import java.sql.Timestamp;

import javax.swing.JButton;
import javax.swing.JToggleButton;

public class TableButton extends JButton {
    private int id;
    private int type;
    private Timestamp openTime;

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

	public Timestamp getOpenTime() {
		return openTime;
	}

	public void setOpenTime(Timestamp openTime) {
		this.openTime = openTime;
	}

}
