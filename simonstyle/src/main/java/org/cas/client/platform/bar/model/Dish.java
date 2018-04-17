package org.cas.client.platform.bar.model;

public class Dish {

    public int getId() {
        return id;
    }

    public void setId(
            int id) {
        this.id = id;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(
            int index) {
        this.index = index;
    }

    public String getLanguage1() {
        return language1;
    }

    public void setLanguage1(
            String language1) {
        this.language1 = language1;
    }

    public String getLanguage2() {
        return language2;
    }

    public void setLanguage2(
            String language2) {
        this.language2 = language2;
    }

    public String getLanguage3() {
        return language3;
    }

    public void setLanguage3(
            String language3) {
        this.language3 = language3;
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

    private int id = 0;
    private int index = 0; // display position on screen.
    private String language1; // CODE VARCHAR(255)
    private String language2; // MNEMONIC VARCHAR(255)
    private String language3; // SUBJECT VARCHAR(255)
    private int price; // PRICE INTEGER
    private int gst; // FOLDERID INTEGER
    private int qst; // STORE INTEGER
    private int size; // COST INTEGER
    private String printer; // BRAND VARCHAR(255)
    private String CATEGORY; // CATEGORY VARCHAR(255)
    private String prompPrice; // CONTENT VARCHAR(255)
    private String prompMenu; // UNIT VARCHAR(255)
    private String prompMofify; // /PRODUCAREA VARCHAR(255)
}
