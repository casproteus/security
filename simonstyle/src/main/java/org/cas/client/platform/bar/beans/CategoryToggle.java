package org.cas.client.platform.bar.beans;

import javax.swing.JToggleButton;

public class CategoryToggle extends JToggleButton {
    int index = 0;

    public CategoryToggle(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
