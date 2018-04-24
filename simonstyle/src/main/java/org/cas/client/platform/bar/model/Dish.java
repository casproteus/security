package org.cas.client.platform.bar.model;

public class Dish {
	public Dish clone(){
		Dish dish = new Dish();
		dish.setCATEGORY(CATEGORY);
		dish.setDiscount(discount);
		dish.setDspIndex(dspIndex);
		dish.setGst(gst);
		dish.setQst(qst);
		dish.setId(id);
		dish.setLanguage(0, language[0]);
		dish.setLanguage(1, language[1]);
		dish.setLanguage(2, language[2]);
		dish.setModification(modification);
		dish.setNum(num);
		dish.setOutputID(outputID);
		dish.setPrice(price);
		dish.setPrinter(printer);
		dish.setPrompMenu(prompMenu);
		dish.setPrompMofify(prompMofify);
		dish.setPrompPrice(prompPrice);
		return dish;
	}
    public int getId() {
        return id;
    }

    public void setId(
            int id) {
        this.id = id;
    }

    public int getDspIndex() {
        return dspIndex;
    }

    public void setDspIndex(
            int index) {
        this.dspIndex = index;
    }

    public String getLanguage(int i) {
        return language[i];
    }

    public void setLanguage(int i,
            String language) {
        this.language[i] = language;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(
            int price) {
        this.price = price;
    }

    public int getGst() {
        return gst;
    }

    public void setGst(
            int gst) {
        this.gst = gst;
    }

    public int getQst() {
        return qst;
    }

    public void setQst(
            int qst) {
        this.qst = qst;
    }

    public int getSize() {
        return size;
    }

    public void setSize(
            int size) {
        this.size = size;
    }

    public String getPrinter() {
        return printer;
    }

    public void setPrinter(
            String printer) {
        this.printer = printer;
    }

    public String getCATEGORY() {
        return CATEGORY;
    }

    public void setCATEGORY(
            String cATEGORY) {
        CATEGORY = cATEGORY;
    }

    public String getPrompPrice() {
        return prompPrice;
    }

    public void setPrompPrice(
            String prompPrice) {
        this.prompPrice = prompPrice;
    }

    public String getPrompMenu() {
        return prompMenu;
    }

    public void setPrompMenu(
            String prompMenu) {
        this.prompMenu = prompMenu;
    }

    public String getPrompMofify() {
        return prompMofify;
    }

    public void setPrompMofify(
            String prompMofify) {
        this.prompMofify = prompMofify;
    }

    public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getDiscount() {
		return discount;
	}

	public void setDiscount(int discount) {
		this.discount = discount;
	}

	public int getOutputID() {
		return outputID;
	}

	public void setOutputID(int outputID) {
		this.outputID = outputID;
	}

	public String getModification() {
		return modification;
	}

	public void setModification(String modification) {
		this.modification = modification;
	}

	private int id = -1;
    private int dspIndex = 0; // display position on screen.
    private String language[] = new String[3]; // CODE VARCHAR(255), MNEMONIC VARCHAR(255),SUBJECT VARCHAR(255)
    private int price; // PRICE INTEGER
    private int gst; // FOLDERID INTEGER
    private int qst; // STORE INTEGER
    private int size; // COST INTEGER
    private String printer; // BRAND VARCHAR(255) comma separated ip string.
    private String CATEGORY; // CATEGORY VARCHAR(255)
    private String prompPrice; // CONTENT VARCHAR(255)
    private String prompMenu; // UNIT VARCHAR(255)
    private String prompMofify; // /PRODUCAREA VARCHAR(255)
    //none saving fields-----------------------------------------
    private int num = 1;
    private int discount;
    private int outputID = -1;
    private String modification;
    
}
