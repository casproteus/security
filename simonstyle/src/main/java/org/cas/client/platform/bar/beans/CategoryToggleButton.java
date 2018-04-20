package org.cas.client.platform.bar.beans;

import javax.swing.JToggleButton;

public class CategoryToggleButton extends JToggleButton {
    int index = 0;

    public CategoryToggleButton(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
