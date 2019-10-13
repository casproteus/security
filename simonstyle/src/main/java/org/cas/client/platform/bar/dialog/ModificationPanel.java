package org.cas.client.platform.bar.dialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import org.cas.client.platform.bar.i18n.BarDlgConst;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;

public class ModificationPanel extends PIMScrollPane implements ActionListener {
	
	private static ModificationPanel instance = null;
	public static ModificationPanel getInstance() {
		if(instance == null) {
			instance = new ModificationPanel();
		}
		return instance;
	}
//
//    HashMap<Integer, String> selectionsMap = new HashMap<Integer, String>();
//
//	public ModificationPanel() {
//		initComponent();
//	}
//
//	public void initComponent() {
//		validate();
//		repaint();
//	}
//	
//    //keep all selections in a map. key is category idx, value is separated string in top text area.
//	private void initSelectionMap() {
//        ArrayList<String> inputList = getInputModification();//the modifications in above text area.
//        int tabsize = tabbedPane.getTabCount();
//        for(int i = 0; i < tabsize; i++) {
//        	ArrayList<String> allLangNameStrOfCurTab = getAllLangNamesFromDB(i);
//        	StringBuilder selectedNameStrOfCurTab = new StringBuilder();
//        	for (int j = inputList.size() - 1; j >= 0; j--) {
//        		String label = inputList.get(j);
//				if(allLangNameStrOfCurTab.contains(label)) {
//					selectedNameStrOfCurTab.append(BarDlgConst.delimiter);
//					selectedNameStrOfCurTab.append(label);
//					inputList.remove(j);
//				}
//			}
//        	if(selectedNameStrOfCurTab.length() > 1) {
//        		selectionsMap.put(i, selectedNameStrOfCurTab.substring(1));
//        	}
//        }
//	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
	}
	
//	public void initContent(String modification, int i) {
//		// TODO Auto-generated method stub
//		
//	}
}
