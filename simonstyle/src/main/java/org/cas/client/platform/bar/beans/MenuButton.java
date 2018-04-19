package org.cas.client.platform.bar.beans;

import javax.swing.JButton;

import org.cas.client.platform.bar.model.Dish;

public class MenuButton extends JButton {
    int dspIndex = 0;
    private Dish dish;

    public MenuButton(int dspIndex) {
        this.dspIndex = dspIndex;
    }

    public int getDspIndex() {
        return dspIndex;
    }

	public Dish getDish() {
		return dish;
	}

	public void setDish(Dish dish) {
		this.dish = dish;
	}
}
