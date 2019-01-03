package org.cas.client.platform.bar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.dialog.BarOption;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.netbeans.MainOrder;
import org.cas.client.platform.bar.netbeans.Material;
import org.cas.client.platform.bar.netbeans.NetUtil;
import org.cas.client.platform.bar.netbeans.TextContent;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

import flexjson.JSONDeserializer;

public class RequestNewOrderThread extends Thread implements ActionListener{
	
	String url;
	String jsonStr;
	List<MainOrder> mainOrders;
	List<Material> materials;
	List<TextContent> menus;
	List<TextContent> serviceTexts;
	 
	public RequestNewOrderThread(String url) {

		url = NetUtil.validateURL(url);
		
		if(!url.endsWith("/")) {
			url = url + "/";
		}
		this.url =  url + "requestNewOrders";
		jsonStr = BarFrame.prepareLicenceJSONString();
	}
	
    @Override
    public void run() {
    	while(true) {
    		try {
    			Thread.sleep(20000);
    		}catch(Exception e) {
    			//do nothing.
    		}
    		new HttpRequestClient(url, "POST", jsonStr, this).start();
    	}
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		List<String> tList = new JSONDeserializer<List<String>>()
				.use(null, ArrayList.class).use("values", String.class).deserialize(e.getActionCommand());
	
	    mainOrders = new JSONDeserializer<List<MainOrder>>()
	    		.use(null, ArrayList.class).use("values", MainOrder.class).deserialize(tList.get(0));

	    materials = new JSONDeserializer<List<Material>>().use(null, ArrayList.class)
	            .use("values", Material.class).deserialize(tList.get(1));

	    menus = new JSONDeserializer<List<TextContent>>().use(null, ArrayList.class)
	            .use("values", TextContent.class).deserialize(tList.get(2));
	    
	    serviceTexts = new JSONDeserializer<List<TextContent>>().use(null, ArrayList.class)
	            .use("values", TextContent.class).deserialize(tList.get(3));
	    

	    for (int i = 0; i < mainOrders.size(); i++) {
	        MainOrder mainOrder = mainOrders.get(i);
	        //create a bill
	        String createtime = BarOption.df.format(new Date());
	        String tableID = mainOrder.sizeTable;
	        String billIndex = "1";			//@default value, will change if tableID contains "_".
	        int p = tableID.indexOf("_");
	        if(p > 0) {
	        	billIndex = tableID.substring(p+1);
	        	tableID = tableID.substring(0, p);
	        }
	        String total = mainOrder.payCondition;
			StringBuilder sql = new StringBuilder(
		            "INSERT INTO bill(createtime, tableID, BillIndex, total, discount, tip, otherreceived, cashback, EMPLOYEEID, Comment, opentime) VALUES ('")
					.append(createtime).append("', '")			//createtime
		            .append(tableID).append("', '")				//tableID
		            .append(billIndex).append("', ")			//BillIndex
		            .append(total == null ? 0 : (int)(Float.parseFloat(total.substring(1)) * 100)).append(", ")	 //remove the $ ahead.
		            .append(0).append(", ")	//discount
		            .append(0).append(", ")	//tip
		            .append(0).append(", ")	//otherreceived
		            .append(0).append(", ")	//cashback
		            .append(LoginDlg.USERID).append(", '")		//emoployid
		            .append("").append("', '")					//Comment
		            .append(createtime).append("')");				//opentime
			try {
				PIMDBModel.getStatement().executeUpdate(sql.toString());
			   	sql = new StringBuilder("Select id from bill where createtime = '").append(createtime).append("' and billIndex = ").append(billIndex);
	            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
	            rs.beforeFirst();
	            rs.next();
	            int billId = rs.getInt("id");
	            //because the qt (4X) will stands for 4 material, so need to merge it before creating output. 
	            HashMap<String, Integer> qtMap = conbineMainOrders(materials);
	            List<Dish> dishes = new ArrayList();;
	            //create output record for each materials of this mainOrder. @materials has been cleaned.
	            for (int j = 0; j < materials.size(); j++) {
	            	Material material = materials.get(j);
	            	if(material.mainOrder.id == mainOrder.id) {
	                	String location = material.location;
	                	String portionName = material.portionName;	//dish name
	                	int num = qtMap.get(material.portionName + material.remark);	//@NOTE we supposed the dish name should not duplicate.
	                	int price = material.dencity == null ? 0 : (int)(Float.valueOf(material.dencity.substring(1).trim()) * 100);
	                	//make sure product exist.
	                	Dish dish = synchronizeToLocalDB(tableID, location, portionName, price, material.menFu);
	                	dish.setNum(num);
	                	dish.setModification(material.remark);
	            		Statement smt = PIMDBModel.getStatement();
	            		try {
	            			sql = new StringBuilder(
	            		    	"INSERT INTO output(SUBJECT, CONTACTID, PRODUCTID, AMOUNT, TOLTALPRICE, DISCOUNT, CONTENT, EMPLOYEEID, TIME, category) VALUES ('")
	            					.append(tableID).append("', ")	//subject ->table id
	            		            .append(billIndex).append(", ")			//contactID ->bill id
	            		            .append(dish.getId()).append(", ")	//product id
	            		            .append(num).append(", ")	//amount
	            		            .append(price).append(", ")	//totalprice int
	            		            .append(0).append(", '")	//discount
	            		            .append(material.remark).append("', ")				//content
	            		            .append(LoginDlg.USERID).append(", '")		//emoployid
	            		            .append(createtime).append("', ")	//opentime
	            		            .append(billId).append(")");	//category ->billId
	            			smt.executeUpdate(sql.toString());
	            			dishes.add(dish);
	            		} catch (Exception exp) {
	            			ErrorUtil.write(exp);
	            		}
	            	}
	            }
	            //send these output to kitchen.
	            PrintService.exePrintOrderList(dishes, tableID, billIndex, "customer", false);
	            //send request to updat the status.
	        	new HttpRequestClient(prepareUpdateOrderStatusURL(BarOption.getServerHost(), mainOrder.id, 50), "POST", BarFrame.prepareLicenceJSONString(), null).start();
			 }catch(Exception exp) {
				ErrorUtil.write(exp);
			 }
			
			
			
	//        String loginName = instance.getContactPerson().getLoginname();
	//        instance.setContactPerson(userMap.get(loginName));
	//        mainOrderMap.put(instance.getId(), instance);
	//        instance.setPerson(person);
	    }
	    
	
	    for (int i = 0; i < materials.size(); i++) {
	        System.out.println(i);
	        Material material = materials.get(i);
	//        instance.setMainOrder(mainOrderMap.get(instance.getMainOrder().getId()));
	//        instance.setPerson(person);
	    }
    
	}
	
	private String prepareUpdateOrderStatusURL(String url, long mainOrderId, int targetStatus) {
		url = NetUtil.validateURL(url);
		StringBuilder sb = new StringBuilder(url);
		if(!url.endsWith("/")) {
			sb.append("/");
		}
		sb.append("updateMainOrderStatus/");
		sb.append(mainOrderId).append("/").append(targetStatus);
		return sb.toString();
	}
	
	//combine the material list. remove the duplicated ones, and add the qt into the map value (use name+comment as key).
	private HashMap<String, Integer> conbineMainOrders(List<Material> materials) {
		HashMap<String, Integer> qtMap = new HashMap<String, Integer>();
		for(int i = materials.size() - 1; i >= 0; i--) {
			Material material = materials.get(i);
			String key = material.portionName + material.remark;
			if(qtMap.containsKey(key)) {
				qtMap.put(key, qtMap.get(key) + 1);//increase the qt.
				materials.remove(i);			//remove the none-first one from list.
			}else {
				qtMap.put(key, 1);	//put into it and set the start qt 1.
			}
		}
		return qtMap;
	}

	//check the category and dish, make sure they exist.
	private Dish synchronizeToLocalDB(String table, String location, String portionName, int price, String menFu) {
		String[] strAry = location.split("_");
		int idx = Integer.parseInt(strAry[1]);
		makeSureTableExistsAndOpened(table);
		String category = makeSureCategoryExist(idx);
		return makeSureDishExist(category, location, Integer.parseInt(strAry[3]), portionName, price, menFu);
	}

	//creata a table record if it's not exist yet. and make it opened. @NOTE: is the status is already opened, then report error.
	private void makeSureTableExistsAndOpened(String table) {
		StringBuilder sql = new StringBuilder("select status from dining_Table where name = '")
				.append(table).append("'");
		try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
	        rs.relative(-1);
	        int tmpPos = rs.getRow();
	        if(tmpPos == 0) {
	        	createNewOpenedTable(table);
	        }else {
	        	if(rs.getInt("status") != 1) {//if already exist, make the status to be 1;
	        		sql = new StringBuilder("update dining_Table set status = 1 where name = ")
	        				.append(table).append("'");
	        		PIMDBModel.getStatement().executeUpdate(sql.toString());
	        	}
	        }
		}catch(Exception exp) {
			L.e("downloading new orders", "makeSureTableExistsAndOpened", exp);
		}
	}

	//create a new opened table
	private void createNewOpenedTable(String table) {
		StringBuilder sql = new StringBuilder("INSERT INTO DINING_TABLE (name, posX, posY, width, height, type, status) VALUES ('")
			.append(table).append("', ")	//name
			.append(10).append(", ")	//posX
			.append(10).append(", ")	//posY
			.append(50).append(", ")	//width
			.append(50).append(", ")	//height
			.append(1).append(", ")		//type
			.append(1).append(")");		//status
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}catch(Exception exp) {
			ErrorUtil.write(exp);
		}
	}

	//@NOTE: here we don not do update, if web/pos changed text, we should do a menu/automatic synchronization.
	private String makeSureCategoryExist(int idx) {
		//if less than category exist, then add category to 3.
        String sql = "select ID, LANG1, LANG2, LANG3, DSP_INDEX from CATEGORY where DSP_INDEX = " + idx;
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            

            rs.afterLast();
            rs.relative(-1);
            int tmpPos = rs.getRow();
            if(tmpPos == 0) {
            	return createNewCategory(idx);
            }else {
            	return rs.getString("LANG1");
            }
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        return "";
	}

	//create the No. idx category
	private String createNewCategory(int idx) {
		HashMap<String, String> titleMap = new HashMap<>(); 
		for(TextContent textContent : menus) {
			if(textContent.getCategoryIdx() == idx) {
				textContent.getTitle(titleMap);
			}
		}
        StringBuilder sql = new StringBuilder("INSERT INTO Category(LANG1, LANG2, LANG3, DSP_INDEX) VALUES('")
            		.append(titleMap.get("en")).append("', '")
            		.append(titleMap.get("fr")).append("', '")
            		.append(titleMap.get("zh")).append("', ")
            		.append(idx).append(")");
        try {
	        PIMDBModel.getStatement().executeUpdate(sql.toString());
	        return titleMap.get("en");
        }catch(Exception exp) {
        	return "";
        }
	}

	//the dencity must be a null or $1
	private Dish makeSureDishExist(String category, String location, int menuIdx, String portionName, int price, String menFu) {
		//ID, CODE, MNEMONIC, SUBJECT, PRICE, FOLDERID, STORE,  COST, BRAND, CATEGORY, CONTENT, UNIT, PRODUCAREA, INDEX
		StringBuilder sql = new StringBuilder("select * from product where deleted != true and CODE = '")
				.append(portionName).append("' and price = ").append(price)
				.append(" and category = '").append(category).append("'");

        try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
			rs.relative(-1);
            int tmpPos = rs.getRow();
            if(tmpPos == 0) {
            	return createNewDish(location, price, menFu, category);
            }else {
            	return new Dish(rs.getInt("id"), category, portionName, rs.getString("MNEMONIC"), rs.getString("SUBJECT"), menFu);
            }
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        return null;
	}

	//create the new dish in the given category.
	private Dish createNewDish(String location, int price, String seletedPrinterIdStr, String category) {
		//we need to find out the 3 name of the product and the first category string of the product.
		HashMap<String, String> contentMap = new HashMap<>(); 
		for(TextContent textContent : serviceTexts) {
			String posInPage = textContent.posInPage;
			if(posInPage.contains(location)) {
				if(posInPage.endsWith("_description")) {	//if it's name of dish.
					String[] strs = posInPage.split("_");
					contentMap.put(posInPage, NetUtil.fetchProductName(textContent.content, BarOption.getMoneySign()));
				}
			}
		}

		String name1 = contentMap.get("en_service_" + location + "_description");
		String name2 = contentMap.get("fr_service_" + location + "_description");
		String name3 = contentMap.get("zh_service_" + location + "_description");
		StringBuilder sql = new StringBuilder(
                 "INSERT INTO Product(CODE, MNEMONIC, SUBJECT, PRICE, FOLDERID, store, Cost,  BRAND, CATEGORY, INDEX, CONTENT, Unit, PRODUCAREA) VALUES ('")
                 .append(name1).append("', '")//CODE
                 .append(name2).append("', '")//MNEMONIC
                 .append(name3).append("', ")//SUBJECT
                 .append(price).append(", ")	//PRICE
                 .append(1).append(", ")		//FOLDERID---if need gst?
                 .append(1).append(", ")		//store---if need qst?
                 .append(0).append(", '")		//Cost--size
                 .append(seletedPrinterIdStr).append("', '")	//BRAND --printer string
                 .append(category).append("', ")		//category
                 .append(Integer.valueOf(location.substring(location.lastIndexOf("_") + 1))).append(", '")//INDEX
                 .append("false").append("', '")//CONTENT---cbxPricePomp
                 .append("false").append("', '")//Unit---cbxMenuPomp
                 .append("false").append("')");//PRODUCAREA---cbxModifyPomp
        try {
	        PIMDBModel.getStatement().executeUpdate(sql.toString());
	        sql = new StringBuilder("Select id from product where CODE = '")
	        		.append(name1).append("' and category = '").append(category).append("'");
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.beforeFirst();
            rs.next();
            return new Dish(rs.getInt("id"), category, name1, name2, name3, seletedPrinterIdStr);
        }catch(Exception exp) {
        }
        return null;
	}
}
