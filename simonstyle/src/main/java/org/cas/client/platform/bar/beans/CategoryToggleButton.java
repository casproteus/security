package org.cas.client.platform.bar.beans;

import java.awt.Color;

import javax.swing.JToggleButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class CategoryToggleButton extends JToggleButton {
    int index = 0;

    public CategoryToggleButton(int index) {
		Color bg = BarOption.getBK("Category");
    	if(bg == null) {
    		bg = new Color(169,209,141);
    	}
        setBackground(bg);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
