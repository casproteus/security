package org.cas.client.platform.bar.net;

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
import org.cas.client.platform.bar.dialog.TablesPanel;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.model.Printer;
import org.cas.client.platform.bar.net.bean.MainOrder;
import org.cas.client.platform.bar.net.bean.Material;
import org.cas.client.platform.bar.net.bean.TextContent;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.cascontrol.dialog.logindlg.LoginDlg;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.pimmodel.PIMDBModel;

import flexjson.JSONDeserializer;

public class RequestNewOrderThread extends Thread implements ActionListener{
	
	static String url;
	String jsonStr;
	List<MainOrder> mainOrders;
	List<Material> materials;
	HashMap<String, Integer> qtMap;
	List<TextContent> menus;
	List<TextContent> serviceTexts;
	 
    @Override
    public void run() {
    	
    	reInitURLString();
		jsonStr = BarFrame.prepareLicenceJSONString();
		
    	while(true) {
    		try {
    			Thread.sleep(20000);
    		}catch(Exception e) {
    			//do nothing.
    		}
    		if(url != null && url.trim().length() > 1 && !"null".equals(url.trim().toLowerCase())) {
    			new HttpRequestClient(url, "POST", jsonStr, this).start();
    		}
    	}
    }

    
	public static void reInitURLString() {
		url = NetUtil.validateURL(BarOption.getServerHost());	//if not valid, the url will be return directly.
		if(url != null && url.trim().length() > 1 && !"null".equals(url.trim().toLowerCase())) {	//so need to check again. 
			if(!url.endsWith("/")) {
				url = url + "/";
			}
			
			String storeName = BarOption.getBillHeadInfo();
	    	int p = storeName.indexOf(":");
	    	if(p > 0) {
	    		storeName = storeName.substring(0, p).trim();
	    	}
	    	
			url =  url + storeName + "/requestNewOrders";
		}
	}

	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		List<String> tList = new JSONDeserializer<List<String>>()
				.use(null, ArrayList.class).use("values", String.class).deserialize(e.getActionCommand());
		//reinit the four instance properties with new received json string.
	    mainOrders = new JSONDeserializer<List<MainOrder>>()
	    		.use(null, ArrayList.class).use("values", MainOrder.class).deserialize(tList.get(0));

	    materials = new JSONDeserializer<List<Material>>().use(null, ArrayList.class)
	            .use("values", Material.class).deserialize(tList.get(1));


        //because the qt (4X) will stands for 4 material, so need to merge it before creating output. 
        qtMap = conbineMainOrders(materials);
        
	    menus = new JSONDeserializer<List<TextContent>>().use(null, ArrayList.class)
	            .use("values", TextContent.class).deserialize(tList.get(2));
	    
	    serviceTexts = new JSONDeserializer<List<TextContent>>().use(null, ArrayList.class)
	            .use("values", TextContent.class).deserialize(tList.get(3));

        String openTime = BarOption.df.format(new Date());

	    for (int i = 0; i < mainOrders.size(); i++) {
	        MainOrder mainOrder = mainOrders.get(i);
	        //prepare tableID and billIndex
	        String tableID = mainOrder.sizeTable;
	        String billIndex = "1";			//@default value, will change if tableID contains "_".
	        int p = tableID.indexOf("_");
	        if(p > 0) {
	        	billIndex = tableID.substring(p+1);
	        	tableID = tableID.substring(0, p);
	        }
	        //table
	        makeSureTableExistsAndOpened(tableID, openTime);
	        
	        //bills
	        int billId = generateBill(openTime, tableID, billIndex, mainOrder.payCondition);
            if(billId < 0) {
            	L.e("Request new orders", "Failed to create a bill with createtime:" + openTime + " tableID:" + tableID + " billIndex:" + " price:" + mainOrder.payCondition, null);
            	return;
            }
            
            //outputs
            List<Dish> dishes = generateOutputs(mainOrder, openTime, tableID, billIndex, billId, materials);
            
            //send these output to kitchen.
            PrintService.exePrintOrderList(dishes, tableID, billIndex, "customer", false);
            
            //send request to update the status to be 50.
        	new HttpRequestClient(prepareUpdateOrderStatusURL(BarOption.getServerHost(), mainOrder.id, 50), "POST", BarFrame.prepareLicenceJSONString(), null).start();
	    }
	}

	public int generateBill(String createtime, String tableID, String billIndex, String total) {
		//create a bill
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
		        .append(0).append(", '")		//emoployid--0 is the id of automactically generated Robot employee when first time use.
		        .append("").append("', '")					//Comment
		        .append(createtime).append("')");				//opentime
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		   	sql = new StringBuilder("Select id from bill where createtime = '").append(createtime).append("' and billIndex = '").append(billIndex).append("'");
		    ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
		    rs.beforeFirst();
		    rs.next();

			return rs.getInt("id");
		 }catch(Exception exp) {
			ErrorUtil.write(exp);
			return -1;
		 }
	}

	public List<Dish> generateOutputs(MainOrder mainOrder, String createtime, String tableID, String billIndex,
			int billId, List<Material> materials) {
		
		List<Dish> dishes = new ArrayList<Dish>();
		//create output record for each materials of this mainOrder. @materials has been cleaned.
		for (int j = 0; j < materials.size(); j++) {
			Material material = materials.get(j);
			if(material.mainOrder.id == mainOrder.id) {	//I prefer this way rather than generate a clean materials list as parameter, because faster and more stable.
		    	String location = material.location;
		    	String portionName = material.portionName;	//dish name
		    	Integer num = qtMap.get(material.portionName + (material.remark == null ? "" : material.remark));	//@NOTE we supposed the dish name should not duplicate.
		    	int price = material.dencity == null ? 0 : Math.round(Float.valueOf(material.dencity.substring(1).trim()) * 100);
		    	
		    	//make sure product exist.
		    	Dish dish = synchronizeToLocalDB(location, portionName, price, material.menFu);
		    	
		    	dish.setNum(num);
		    	dish.setModification(material.remark);
				createOutputRecord(createtime, tableID, billIndex, billId, dishes, material, num, price, dish);
			}
		}
		return dishes;
	}

	public void createOutputRecord(String createtime, String tableID, String billIndex, int billId,
			List<Dish> dishes, Material material, int num, int price, Dish dish) {
		StringBuilder sql;
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
		            .append(material.remark == null ? "" : material.remark).append("', ")				//content
		            .append(LoginDlg.USERID).append(", '")		//emoployid
		            .append(createtime).append("', ")	//opentime
		            .append(billId).append(")");	//category ->billId
			smt.executeUpdate(sql.toString());
			dishes.add(dish);
		} catch (Exception exp) {
			ErrorUtil.write(exp);
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
			String key = material.portionName + (material.remark == null ? "" : material.remark);
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
	private Dish synchronizeToLocalDB(String location, String portionName, int price, String menFu) {
		String[] strAry = location.split("_");
		
		String category = makeSureCategoryExist(location, Integer.parseInt(strAry[0]), Integer.parseInt(strAry[1]));
		return makeSureDishExist(category, location, Integer.parseInt(strAry[3]), portionName, price, menFu);
	}

	//creata a table record if it's not exist yet. and make it opened. @NOTE: is the status is already opened, then report error.
	private void makeSureTableExistsAndOpened(String table, String openTime) {
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
	        		BarFrame.instance.openATable(table, openTime);
	        	}
	        }
	        ((TablesPanel)BarFrame.instance.panels[0]).initContent();
		}catch(Exception exp) {
			L.e("downloading new orders", "makeSureTableExistsAndOpened", exp);
		}
	}

	//create a new opened table
	private void createNewOpenedTable(String table) {
		int idx = Integer.valueOf(table);
		int row = idx / 10 + 1;
		int col = idx % 10;
		
		StringBuilder sql = new StringBuilder("INSERT INTO DINING_TABLE (name, posX, posY, width, height, type, status, openTime) VALUES ('")
			.append(table).append("', ")	//name
			.append(col * 90 + CustOpts.HOR_GAP).append(", ")	//posX
			.append(row * 90 + CustOpts.VER_GAP).append(", ")	//posY
			.append(90).append(", ")	//width
			.append(90).append(", ")	//height
			.append(1).append(", ")		//type
			.append(1).append(", '")
			.append(BarOption.df.format(new Date())).append("')");		//status
		
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}catch(Exception exp) {
			ErrorUtil.write(exp);
		}
	}

	private String makeSureCategoryExist(String location, int menuIdx, int idx) {
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
            	return synchronizeLocalCategory(menuIdx, idx, rs);
            }
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        return "";
	}

	//location's format is supposed to be like "3_1_0_1"
	private String synchronizeLocalCategory(int menuIdx, int subMenuIdx, ResultSet rs) throws SQLException {
		String location = menuIdx + "_" + subMenuIdx;
		HashMap<String, String> contentMap = new HashMap<>(); 
		for(TextContent menu : menus) {
			String posInPage = menu.posInPage;
			if(posInPage.endsWith("_menu_" + location)) {
				contentMap.put(posInPage, menu.content);
			}
		}
		//values in web
		String name1 = contentMap.get("en_menu_" + location);
		String name2 = contentMap.get("fr_menu_" + location);
		String name3 = contentMap.get("zh_menu_" + location);
		name1 = name1 == null ? "" : name1;
		name2 = name2 == null ? "" : name2;
		name3 = name3 == null ? "" : name3;
		//values in local
		String lName1 = rs.getString("LANG1");
		String lName2 = rs.getString("LANG2");
		String lName3 = rs.getString("LANG3");
		
		if(!name1.equals(lName1) || !name2.equals(lName2) || !name3.equals(lName3)) {
			StringBuilder sql = new StringBuilder("Update category set LANG1 = '").append(name1)
					.append("', LANG2 = '").append(name2)
					.append("', LANG3 = '").append(name3).append("' where dsp_Index = ").append(subMenuIdx);
		
			PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			BarFrame.menuPanel.initComponent();
		}
		return name1;
	}

	//create the No. idx category
	private String createNewCategory(int idx) {
		HashMap<String, String> titleMap = new HashMap<>(); 
		for(TextContent textContent : menus) {
			if(textContent.getCategoryIdx() == idx) {
				textContent.putTitle(titleMap);
			}
		}
        StringBuilder sql = new StringBuilder("INSERT INTO Category(LANG1, LANG2, LANG3, DSP_INDEX) VALUES('")
            		.append(titleMap.get("en")).append("', '")
            		.append(titleMap.get("fr")).append("', '")
            		.append(titleMap.get("zh")).append("', ")
            		.append(idx).append(")");
        try {
	        PIMDBModel.getStatement().executeUpdate(sql.toString());
	        BarFrame.menuPanel.initComponent();
	        return titleMap.get("en");
        }catch(Exception exp) {
        	return "";
        }
	}

	//the dencity must be a null or $1
	private Dish makeSureDishExist(String category, String location, int menuIdx, String portionName, int price, String menFu) {
		//ID, CODE, MNEMONIC, SUBJECT, PRICE, FOLDERID, STORE,  COST, BRAND, CATEGORY, CONTENT, UNIT, PRODUCAREA, INDEX
		StringBuilder sql = new StringBuilder("select * from product where deleted != true and index = ").append(menuIdx).append(" and category = '").append(category).append("'");

        try {
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
			rs.relative(-1);
            int tmpPos = rs.getRow();
            if(tmpPos == 0) {
            	return createNewDish(location, price, menFu, category);
            }else {
            	//check if the properties are all same, if not, update local to make sure local are same with server.
        		String localPrinterIdx = synchronizeLocalProduct(location, price, menFu, rs);
            	return new Dish(rs.getInt("id"), category, portionName, rs.getString("MNEMONIC"), rs.getString("SUBJECT"), localPrinterIdx);
            }
        } catch (SQLException e) {
            ErrorUtil.write(e);
        }
        return null;
	}

	private String synchronizeLocalProduct(String location, int price, String menFu, ResultSet rs) throws SQLException {
		HashMap<String, String> contentMap = new HashMap<>(); 
		for(TextContent textContent : serviceTexts) {
			String posInPage = textContent.posInPage;
			if(posInPage.contains(location)) {
				if(posInPage.endsWith("_description")) {	//if it's name of dish.
					contentMap.put(posInPage, NetUtil.fetchProductName(textContent.content, BarOption.getMoneySign()));
				}
			}
		}
		//values in web
		String name1 = contentMap.get("en_service_" + location + "_description");
		String name2 = contentMap.get("fr_service_" + location + "_description");
		String name3 = contentMap.get("zh_service_" + location + "_description");
		name1 = name1 == null ? "" : name1;
		name2 = name2 == null ? "" : name2;
		name3 = name3 == null ? "" : name3;
		
		//values in local
		String lName1 = rs.getString("CODE");
		String lName2 = rs.getString("MNEMONIC");
		String lName3 = rs.getString("SUBJECT");
		int lPrice = rs.getInt("PRICE");
		String lPrinter = rs.getString("BRAND");
		
		if(!name1.equals(lName1) || !name2.equals(lName2) || !name3.equals(lName3) 
				|| price != lPrice || !menFu.equals(lPrinter)) {
			StringBuilder sql = new StringBuilder("Update Product set CODE = '").append(name1)
					.append("', MNEMONIC = '").append(name2)
					.append("', SUBJECT = '").append(name3)
					.append("', PRICE = ").append(price)
					.append(", BRAND = '").append(menFu)
					.append("' where deleted != true and index = ").append(rs.getInt("INDEX")).append(" and category = '").append(rs.getString("CATEGORY")).append("'");
		
			PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			BarFrame.menuPanel.initComponent();
		}
		return menFu;
	}

	//create the new dish in the given category.
	private Dish createNewDish(String location, int price, String seletedPrinterIdStr, String category) {
		//we need to find out the 3 name of the product and the first category string of the product.
		HashMap<String, String> contentMap = new HashMap<>(); 
		for(TextContent textContent : serviceTexts) {
			String posInPage = textContent.posInPage;
			if(posInPage.contains(location)) {
				if(posInPage.endsWith("_description")) {	//if it's name of dish.
					contentMap.put(posInPage, NetUtil.fetchProductName(textContent.content, BarOption.getMoneySign()));
				}
			}
		}

		String name1 = contentMap.get("en_service_" + location + "_description");
		String name2 = contentMap.get("fr_service_" + location + "_description");
		String name3 = contentMap.get("zh_service_" + location + "_description");
		seletedPrinterIdStr = convertPrinterIPtoIdx(seletedPrinterIdStr);
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
	        BarFrame.menuPanel.initComponent();
	        
	        sql = new StringBuilder("Select id from product where CODE = '")
	        		.append(name1).append("' and category = '").append(category).append("'");
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
            rs.beforeFirst();
            rs.next();
            return new Dish(rs.getInt("id"), category, name1, name2, name3, seletedPrinterIdStr);
        }catch(Exception exp) {
        	L.e("RequestNewOrders", "exception when creating new Dish.", exp);
        }
        return null;
	}

	private String convertPrinterIPtoIdx(String seletedPrinterIdStr) {
		
		String[] ipAryFromWeb = seletedPrinterIdStr.split(",");
		if(ipAryFromWeb.length < 1) {
			return seletedPrinterIdStr;
		}
		int nonEmptyQt = BarFrame.menuPanel.getPrinters().length;
		int idxOfFirstEmpty = 0;
		for (int i = BarFrame.menuPanel.getPrinters().length - 1; i >= 0; i--) {
			Printer printer = BarFrame.menuPanel.getPrinters()[i];
			if(printer.getIp() == null || printer.getIp().length() == 0) {
				nonEmptyQt--;
				idxOfFirstEmpty = i;
			}
		}
		
		for(int i = 0; i < ipAryFromWeb.length; i++) {
			if(ipAryFromWeb[i].length() > 0) {	// some are just "", ignore this kind of element.
				boolean findedMatching = false;
				for (int idxOfLocalPrinter = 0; idxOfLocalPrinter < 6; idxOfLocalPrinter++) {	
					Printer localPrinter  = BarFrame.menuPanel.getPrinters()[idxOfLocalPrinter];
					if(ipAryFromWeb[i].equals(localPrinter.getIp())) {
						ipAryFromWeb[i] = String.valueOf(idxOfLocalPrinter);
						findedMatching = true;
						break;
					}
				}
				if(!findedMatching) {
					if(nonEmptyQt < 6) {
						updateLocalPrinter("P"+(idxOfFirstEmpty + 1), 0, 0, ipAryFromWeb[i], 0, 0, idxOfFirstEmpty);
						BarFrame.menuPanel.initPrinters();
						ipAryFromWeb[i] = String.valueOf(idxOfFirstEmpty + 1);
					}else {
						ipAryFromWeb[i] = String.valueOf(1);
						BarFrame.setStatusMes("Error! Web printer ip do not match any of local 6 printer ip! Printed on main printer temperally.");
						L.e("requestNewOrders", "web printer ip do not match any of local 6 printer IP.", null);
					}
				}
			}
		}
		
		StringBuilder sb = new StringBuilder(ipAryFromWeb[0]);
		for (int i = 1; i < ipAryFromWeb.length; i++) {
			if(ipAryFromWeb[i].length() > 0) {
				sb.append(",").append(ipAryFromWeb[i]);
			}
		}
		return sb.toString();
	}

	private void updateLocalPrinter(String name, int category, int langType, String ip, int style, int status, int id) {
		String[] numStrs = ip.split("\\.");
		if(numStrs.length != 4) {
			L.e("updating localprinters", "non-ip parameter found", null);
		}
		StringBuilder sql = new StringBuilder("Update Hardware set name = '").append(name)
				.append("', category = ").append(category).append(", langType = ").append(langType)
				.append(", ip = '").append(ip).append("', style = ").append(style).append(", status = ").append(status)
				.append(" where id = ").append(id);
		try {
			PIMDBModel.getStatement().executeUpdate(sql.toString());
		}catch(Exception e) {
			L.e("RequestNewOrders", "exception when creating an Printer record", e);
		}
	}
}
