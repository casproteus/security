package org.cas.client.platform.bar.beans;

import javax.swing.JToggleButton;

import org.cas.client.platform.bar.dialog.BarOption;

public class CategoryToggleButton extends JToggleButton {
    int index = 0;

    public CategoryToggleButton(int index) {
        setBackground(BarOption.getBK("Category"));
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
