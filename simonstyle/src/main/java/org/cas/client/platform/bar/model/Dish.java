package org.cas.client.platform.bar.model;


import java.sql.Statement;
import java.util.ArrayList;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.pimmodel.PIMDBModel;


public class Dish {
	
	//create new outputs.
	public static void split(ArrayList<Dish> selectdDishAry, int splitAmount, String billID) {
		for(int i = 0; i < selectdDishAry.size(); i++) {
			Dish dish = selectdDishAry.get(i);
			split(dish, splitAmount, billID);
		}
	}

	public static void split(Dish dish, int splitAmount, String billID) {
		int num = dish.getNum();			//current amount
		//first pick out the number on 100,0000 and 10000 position
		int pK = num /(BarOption.MaxQTY * 100);
		if(num > BarOption.MaxQTY * 100) {
			num = num %(BarOption.MaxQTY * 100);
		}
		int pS = (int)num /BarOption.MaxQTY;
		if(num > BarOption.MaxQTY) {
			num = num % BarOption.MaxQTY;
		}
		//calculate the new rate of dividing price, and the new num to update into the db.
		float priceChange = (float)num / splitAmount;	//default division rate.
		int pX = splitAmount * BarOption.MaxQTY;
		if(pS > 0) {	//while if it's already a float, then reset the division rate.
			priceChange = priceChange / pS;
			num += pS * BarOption.MaxQTY ;		//if more than one time division, put the number on higher position.
			pX *= 100;
		}
		if(pK > 0) {
			priceChange = priceChange / pK;
			num += pK * BarOption.MaxQTY * 100;
			pX *= 100;
		}
		num += pX;
		
		if(billID == null) {		//updating the original one.
			Statement smt = PIMDBModel.getStatement();
			try {
				smt.execute("update output set amount = " + num
						+ ", TOLTALPRICE = " + (dish.getPrice() - dish.getDiscount()) * priceChange + " where id = " + dish.getOutputID());
			} catch (Exception exp) {
				ErrorUtil.write(exp);
			}
		}else {						//creating a splited one.
			dish.setNum(num);
			createSplitedOutput(dish, billID, priceChange);
		}
	}

	public static void createOutput(Dish dish, String billID) {
		createSplitedOutput(dish, billID, dish.getNum());
	}
	
	public static void createSplitedOutput(Dish dish, String billID, float priceChange) {
		int num = dish.getNum();
		Statement smt = PIMDBModel.getStatement();
		try {
			StringBuilder sql = new StringBuilder(
		            "INSERT INTO output(SUBJECT, CONTACTID, PRODUCTID, AMOUNT, TOLTALPRICE, DISCOUNT, CONTENT, EMPLOYEEID, TIME) VALUES ('")
		            .append(BarFrame.instance.valCurTable.getText()).append("', ")	//subject ->table id
		            .append(billID).append(", ")			//contactID ->bill id
		            .append(dish.getId()).append(", ")	//productid
		            .append(dish.getNum()).append(", ")	//amount
		            .append((dish.getPrice() - dish.getDiscount()) * priceChange).append(", ")	//totalprice int
		            .append(dish.getDiscount() * dish.getNum()).append(", '")	//discount
		            .append(dish.getModification()).append("', ")				//content
		            .append(LoginDlg.USERID).append(", '")		//emoployid
		            .append(dish.getOpenTime()).append("') ");
		        smt.executeUpdate(sql.toString());
		
		} catch (Exception exp) {
			ErrorUtil.write(exp);
		}
	}
	
	public static void delete(Dish dish) {
		if(dish.getOutputID() < 0) {
			return;
		}
		
		Statement smt = PIMDBModel.getStatement();
		try {
			smt.execute("update output set deleted = 100 where id = " + dish.getOutputID());
		} catch (Exception exp) {
			ErrorUtil.write(exp);
		}
	}
	
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
		dish.setBillIndex(billIndex);
		dish.setOpenTime(openTime);
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
    	if("null".equalsIgnoreCase(language[i]) || language[i] == null || language[i].length() == 0) {
    		return language[0];
    	}
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

    //should return 0 or 1....
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

	public String getBillIndex() {
		return billIndex;
	}

	public void setBillIndex(String billIndex) {
		this.billIndex = billIndex;
	}

	public String getOpenTime() {
		return openTime;
	}

	public void setOpenTime(String openTime) {
		this.openTime = openTime;
	}

	public int getBillID() {
		return billID;
	}

	public void setBillID(int billID) {
		this.billID = billID;
	}

	public boolean isCanceled() {
		return isCanceled;
	}

	public void setCanceled(boolean isCanceled) {
		this.isCanceled = isCanceled;
	}

	public int getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(int totalPrice) {
		this.totalPrice = totalPrice;
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
    private String billIndex;
    private String openTime;
    private int billID;
    private boolean isCanceled;
    private int totalPrice;
}
