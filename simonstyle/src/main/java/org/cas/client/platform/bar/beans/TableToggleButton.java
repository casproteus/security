package org.cas.client.platform.bar.beans;

import javax.swing.JToggleButton;

public class TableToggleButton extends JToggleButton {
    private int index;
    private int billCount;
    private int type;

    public TableToggleButton() {
    }

    public int getIndex() {
        return index;
    }

	public int getBillCount() {
		return billCount;
	}

	public void setBillCount(int billCount) {
		this.billCount = billCount;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
