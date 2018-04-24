package org.cas.client.platform.bar.model;

public class Category {
	private int ID;
	private int dspIndex;
	private String language[] = new String[3];

	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	
	public int getDspIndex() {
		return dspIndex;
	}
	public void setDspIndex(int dspIndex) {
		this.dspIndex = dspIndex;
	}
	public String[] getLanguage() {
		return language;
	}
	public void setLanguage(String language[]) {
		this.language = language;
	}
	
}
