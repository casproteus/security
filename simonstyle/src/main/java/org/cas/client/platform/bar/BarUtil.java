package org.cas.client.platform.bar;

import java.awt.Component;
import java.awt.Container;
import java.io.DataOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.cas.client.platform.bar.dialog.BarFrame;
import org.cas.client.platform.bar.model.DBConsts;
import org.cas.client.platform.bar.model.Dish;
import org.cas.client.platform.bar.print.PrintService;
import org.cas.client.platform.bar.uibeans.MoreButton;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.L;
import org.cas.client.platform.casutil.TaoEncrypt;
import org.cas.client.platform.pimmodel.PIMDBModel;

import gnu.io.CommPortIdentifier;
import gnu.io.ParallelPort;
import gnu.io.PortInUseException;

public class BarUtil {
	
    public static DecimalFormat formatter = new DecimalFormat("#0.00");
    public static String[] sepLines;
    public static String SEP_STR1 = "=";
    public static String SEP_STR2 = "-";
	
	//not sure the command still available. I am currently using Command.OPEN_CASHIER
	public static void openMoneyBox() {
		int[] ccs = new int[5];
		ccs[0] = 27;
		ccs[1] = 112;
		ccs[2] = 0;
		ccs[3] = 80;
		ccs[4] = 250;

		CommPortIdentifier tPortIdty;
		try {
			Enumeration tPorts = CommPortIdentifier.getPortIdentifiers();
			if (tPorts == null)
				JOptionPane.showMessageDialog(BarFrame.instance, "no comm ports found!");
			else
				while (tPorts.hasMoreElements()) {
					tPortIdty = (CommPortIdentifier) tPorts.nextElement();
					if (tPortIdty.getName().equalsIgnoreCase("com1")) {
						if (!tPortIdty.isCurrentlyOwned()) {
							ParallelPort tParallelPort = (ParallelPort) tPortIdty.open("serialBlackBox", 2000);
							DataOutputStream tOutStream = new DataOutputStream(tParallelPort.getOutputStream());
							for (int i = 0; i < 5; i++)
								tOutStream.write(ccs[i]);
							tOutStream.flush();
							tOutStream.close();
							tParallelPort.close();
						}
					}
				}
		} catch (PortInUseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
    // 先检查是否存在尚未输入完整信息的产品，如果检查到存在这种产品，方法中会自动弹出对话盒要求用户填写详细信息。
    public static void checkUnCompProdInfo() {
        String sql = "select * from product where subject = '' and DELETED != true"; // 是否存在上没有名字的产品？
        try {
            ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql);
            rs.afterLast();
            rs.relative(-1);
            int tRowCount = rs.getRow();
            if (tRowCount > 0) {
                int[] tIDAry = new int[tRowCount];
                String[] tCodeAry = new String[tRowCount];
                int[] tPriceAry = new int[tRowCount];
                String[] tMnemonicAry = new String[tRowCount];
                String[] tSubjectAry = new String[tRowCount];
                int[] tStoreAry = new int[tRowCount];
                String[] tUnitAry = new String[tRowCount];
                String[] tCategoryAry = new String[tRowCount];
                int[] tCostAry = new int[tRowCount];
                String[] tContentAry = new String[tRowCount];

                rs.beforeFirst();
                int tIdx = 0;
                while (rs.next()) {
                    tIDAry[tIdx] = rs.getInt("ID");
                    tCodeAry[tIdx] = rs.getString("Code");
                    tPriceAry[tIdx] = rs.getInt("Price");
                    tMnemonicAry[tIdx] = rs.getString("Mnemonic");
                    tSubjectAry[tIdx] = rs.getString("Subject");
                    tStoreAry[tIdx] = rs.getInt("Store");
                    tUnitAry[tIdx] = rs.getString("Unit");
                    tCategoryAry[tIdx] = rs.getString("Category");
                    tCostAry[tIdx] = rs.getInt("Cost");
                    tContentAry[tIdx] = rs.getString("Content");
                    tIdx++;
                }
//                for (int i = 0; i < tRowCount; i++) {
//                    new MenuItem(BarFrame.instance, tIDAry[i], tCodeAry[i], tPriceAry[i], tMnemonicAry[i],
//                            tSubjectAry[i], tStoreAry[i], tUnitAry[i], tCategoryAry[i], tCostAry[i], tContentAry[i])
//                            .setVisible(true);
//
//                }
            }
            rs.close();// 关闭
        } catch (SQLException exp) {// 如果没有匹配是否会到该代码块？
            ErrorUtil.write(exp);
        }
    }

    public static String formatMoney(double number) {
    	return formatter.format(number);
    }
    
    public static boolean isNumber(
            int pKeyCode) {
        return (pKeyCode >= 48 && pKeyCode <= 58) || (pKeyCode >= 96 && pKeyCode <= 106);
    }

    // 返回Option中设置的，条码的位数。
    public static int getProdCodeLen() {
        String tValue = (String) CustOpts.custOps.getValue(BarFrame.consts.ProdCodeLength());
        int tProdCodeLen = 100;
        try {
            tProdCodeLen = Integer.parseInt(tValue);
        } catch (Exception exp) {
        }
        return tProdCodeLen;
    }

    public static int getPreferedWidth() {
		int tWidth = 42;
        try {
            tWidth = Integer.valueOf((String)CustOpts.custOps.getValue( "width"));
        }catch(Exception e){
        	//do nothing, if no with property set, width will keep default value.
        }
		return tWidth;
	}


    public static String generateString(int length, String character){
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < length; i++){
            sb.append(character);
        }
        return sb.toString();
    }
    
    public static int getLengthOfString(String content){
        int length = content.length();
        int realWidth = length;
        for(int i = 0; i < length; i++) {
            char c = content.charAt(i);
            if(c >=19968 && c <= 171941) {
                realWidth++;
            }
        }
        return realWidth;
    }
    
	public static boolean isMoreThanOneBill() {
		try {
			StringBuilder sql = new StringBuilder("SELECT DISTINCT contactID from output where SUBJECT = '").append(BarFrame.instance.cmbCurTable.getSelectedItem().toString())
					.append("' and (deleted is null or deleted = ").append(DBConsts.original)
					.append(" and time = '").append(BarFrame.instance.valStartTime.getText()).append("' order by contactID");
			ResultSet rs = PIMDBModel.getReadOnlyStatement().executeQuery(sql.toString());
			rs.afterLast();
			rs.relative(-1);
			int num = rs.getRow();
	
			if (num < 2) {	//more than 1 record, means splitted.
				return false;
			} else {
				return true;
			}
		}
		catch(Exception e) {
			return false;
		}
	}
	
	private void setRefInfoComment(int billId) {
		StringBuilder sb = new StringBuilder("update bill set comment = '").append(PrintService.REF_TO + billId).append("' where id = ").append(billId);
		try {
			PIMDBModel.getStatement().executeUpdate(sb.toString());
		}catch(Exception exp) {
			ErrorUtil.write(exp);
		}
	}

	//remove html tags e.g.<html><center>Bill<br>FOOT INFO</center></html>
	public static String getPlainTextOut(String string) {
		int p = string.indexOf("<html>");
		if(p >= 0) {
			string = string.substring(p + 6);
			p = string.indexOf("</html>");
			if(p >= 0) {
				string = string.substring(0, p);
			}
			p = string.indexOf("<center>");
			if(p >= 0) {
				string = string.substring(p + 8);
			}
			p = string.indexOf("</center>");
			if(p >= 0) {
				string = string.substring(0, p);
			}
			p = string.indexOf("<br>");
			if(p > 0) {
				string = string.substring(0, p) + " " + string.substring(p + 4);
			}
			p = string.indexOf("<br>");
			if(p > 0) {
				string = string.substring(0, p);
			}
		}
		return string;
	}

    public static String canadianPennyRound(String substring) {
		Float price = Float.valueOf(substring.trim());
		int cent = (int)(price * 100);
		int lastNum = cent % 10;
		if(lastNum < 3) {
			cent = cent - lastNum;
		}else if(lastNum > 7) {
			cent = cent - lastNum + 10;
		}else {
			cent = cent - lastNum + 5;
		}
		
		return BarUtil.formatMoney(cent/100f);
	}
    
	public static String getSeperatorLine(int index, int tWidth) {
		//seperator
		if(sepLines == null) {
			sepLines = new String[2];
	        String sep_str1 = (String)CustOpts.custOps.getValue("sep_str1");
	        if(sep_str1 == null || sep_str1.length() == 0){
	            sep_str1 = SEP_STR1;
	        }
	        sepLines[0] = BarUtil.generateString(tWidth, sep_str1);

	        String sep_str2 = (String)CustOpts.custOps.getValue("sep_str2");
	        if(sep_str2 == null || sep_str2.length() == 0){
	            sep_str2 = SEP_STR2;
	        }

	        sepLines[1] = BarUtil.generateString(tWidth, sep_str2);
		}
		if(index > sepLines.length) {
			L.e("PrintService", "Unexpect index when getting item from sepLines with Index: " + index
					+ "the width of sepLines is " + sepLines.length, null);
		}
		return sepLines[index];
	}
	

	public static void cleanDB() {
		try {
			//delete bill;
			StringBuilder sql = new StringBuilder("delete from bill");
    		PIMDBModel.getStatement().executeUpdate(sql.toString());
    		//delete output
    		sql = new StringBuilder("delete from output");
    		PIMDBModel.getStatement().executeUpdate(sql.toString());
    		//update the dining_table
    		sql = new StringBuilder("update dining_table set status = 0");
    		PIMDBModel.getStatement().executeUpdate(sql.toString());
    	}catch(Exception exp) {
    		L.e("Report", " exception when trying to delete records from db", exp);
    	}
		
	}

	public static String encrypt(String key, String substring) {
		if(key.equals("superPassword")) {
			substring = TaoEncrypt.encryptPassword(substring);
		}
		return substring;
	}

	public static List<Dish> generateAnEmptyDish() {
		List<Dish> dishes = new ArrayList<Dish>();
		Dish dish = new Dish();
		dishes.add(dish);
		return dishes;
	}
	
	public static void addFunctionButtons(Container panel, ArrayList<JComponent> buttons) {
		int max = buttons.size();
        if(max > 20) {
        	MoreButton btnMore = new MoreButton(BarFrame.consts.MORE());
        	for(int i = 19; i < buttons.size(); i++) {
        		btnMore.addButton(buttons.get(i));
        	}
        	buttons.add(19, btnMore);
        	max = 20;
        }
        
        for(int i = 0; i < max ; i++) {
        	panel.add((Component)buttons.get(i));
		}
	}

	public static int layoutCommandButtons(JPanel panel, ArrayList<JComponent> buttons) {
		if(buttons == null)
			return -1;
		
        int panelWidth = panel.getWidth();
        int panelHeight = panel.getHeight();

        // command buttons--------------
        int butonQT = buttons.size();
        
        int row;
        int col;
        if(butonQT <= 0) {
        	row = 0;
        	col = 0;
        }else if(butonQT <= 10) {
        	row = 1;
        	col = butonQT;
        }else {
        	row = 2;
        	if(butonQT >= 20){
        		col = 10;
        	}else{
        		col = butonQT%2 + butonQT/2;
        	}
        }
        
        int tBtnWidht = col == 0? 100 : (panelWidth - CustOpts.HOR_GAP * (col + 1)) / (col);
        int tBtnHeight = (BarFrame.instance.getHeight() - 80) / 10;

        int max = butonQT > 20 ? 20 : butonQT;
        for(int i = 0; i < max; i++) {
        	buttons.get(i).setBounds(CustOpts.HOR_GAP + tBtnWidht * (i < col ? i : i - col) + CustOpts.HOR_GAP * (i < col ? i : i - col),
        			panelHeight - tBtnHeight * (i < col && max > col ? 2 : 1) - CustOpts.VER_GAP * (i < col && max > col ? 2 : 1),
        			tBtnWidht, tBtnHeight);
        }
		return buttons != null && buttons.size() > 0 ? buttons.get(0).getY() : -1;
	}
}
