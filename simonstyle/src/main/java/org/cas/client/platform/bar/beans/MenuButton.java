package org.cas.client.platform.bar.beans;

import javax.swing.JButton;

public class MenuButton extends JButton {
    int index = 0;

    public MenuButton(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
