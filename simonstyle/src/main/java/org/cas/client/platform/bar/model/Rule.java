package org.cas.client.platform.bar.model;

import org.cas.client.platform.bar.dialog.BarFrame;

public class Rule {

	private int id;
	private int dspIdx;
	private String ruleName;
	private String content;
	private int action;
	private int status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDspIdx() {
		return dspIdx;
	}
	public void setDspIdx(int dspIdx) {
		this.dspIdx = dspIdx;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public float getActionPrice() {
		if(action >= 60) {
			return action;
		}else {
			//get the price of the dishes
			String[] ids = content.split(",");
			int totalPrice = 0;
			for(String id : ids) {
				for(Dish dish : BarFrame.menuPanel.getDishAry()) {
					if(id.equals(String.valueOf(dish.getId()))) {
						totalPrice += dish.getPrice();
						break;
					}
				}
			}
			//then calculate the discount price.
			return Math.round(Float.valueOf(totalPrice * action) / 100f);
		}
	}

}
