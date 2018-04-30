package org.cas.client.platform.bar.beans;

import javax.swing.JButton;
import javax.swing.JToggleButton;

public class TableButton extends JButton {
    private int index;
    private int type;

    public TableButton() {
    }

    public int getIndex() {
        return index;
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
