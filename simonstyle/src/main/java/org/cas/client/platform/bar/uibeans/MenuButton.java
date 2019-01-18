package org.cas.client.platform.bar.uibeans;

import java.awt.Color;

import javax.swing.JButton;

import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.model.Dish;

public class MenuButton extends JButton {
    int dspIndex = 0;
    private Dish dish;

    public MenuButton(int dspIndex) {
    	Color bg = BarOption.getBK("Dish");
    	if(bg == null) {
    		bg = new Color(204,255,102);
    	}
    	setBackground(bg);
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
