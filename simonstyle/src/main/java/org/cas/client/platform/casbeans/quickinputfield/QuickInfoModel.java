package org.cas.client.platform.casbeans.quickinputfield;

import java.util.Vector;

import javax.swing.DefaultListModel;

//模型的类====================================================================================================
class QuickInfoModel extends DefaultListModel {
    /**
     *
     */
    QuickInfoModel(String[] prmToShowOnPopPaneAry) {
        // 把数据添加到模型中
        for (int i = 0; i < prmToShowOnPopPaneAry.length; i++) {
            addElement(new Object[] { prmToShowOnPopPaneAry[i] });
        }
    }

    /**
     *
     */
    QuickInfoModel(Vector prmVectorSortData) {
        // 把数据添加到模型中
        for (int i = 0; i < prmVectorSortData.size(); i++) {
            addElement(new Object[] { prmVectorSortData.elementAt(i) });
        }
    }

    String getName(
            Object object) {
        Object[] array = (Object[]) object;
        return (String) array[0];
    }
}
