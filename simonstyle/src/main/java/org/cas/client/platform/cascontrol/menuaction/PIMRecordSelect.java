package org.cas.client.platform.cascontrol.menuaction;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;

import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;

public class PIMRecordSelect implements Transferable, ClipboardOwner {
    static {
        try {
            pimRecordFlavor =
                    new DataFlavor(Class.forName("org.cas.client.platform.pimmodel.PIMRecord"), "You are arrested!");
        } catch (ClassNotFoundException e) {
            ErrorUtil.write("Class of PIMRecord is not found!(PIMRecordSelect.static()");
        }
    }

    /**
     * Creates a new instance of PIMRecordSelect
     * 
     * @param 操作记录
     */
    public PIMRecordSelect(Vector records) {
        this.records = records;
    }

    /**
     * Returns an object which represents the data to be transferred. The class of the object returned is defined by the
     * representation class of the flavor.
     *
     * @param prmFlavor
     *            the requested flavor for the data
     * @see DataFlavor#getRepresentationClass
     * @exception IOException
     *                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException
     *                if the requested data flavor is not supported.
     */
    public Object getTransferData(
            DataFlavor prmFlavor) throws UnsupportedFlavorException {
        if (prmFlavor.equals(DataFlavor.stringFlavor)) {
            if (records == null) {
                return "#ERROR";
            }

            // 判断是否为邮件应用
            // TODO:此处有问题,值判断了第一条记录,如果在已删除项中,只判断一条是错误的
            if (CASUtility.isMail(((PIMRecord) (records.elementAt(0))).getAppIndex())) {
                StringBuffer message = new StringBuffer("发件人\t主题\t接收时间\n");
                final int size = records.size();
                for (int i = 0; i < size; i++) {
                    PIMRecord record = (PIMRecord) records.elementAt(i);
                    message.append(record.getFieldValue(ModelDBCons.ADDRESSER)).append("\t")
                            .append(record.getFieldValue(ModelDBCons.CAPTION)).append("\t")
                            .append(record.getFieldValue(ModelDBCons.FOLOWUPENDTIME)).append("\n");
                }
                return message.toString();
            } else {
                return records.size() + " #ERROR\n";
            }

        } else if (prmFlavor.equals(pimRecordFlavor)) {
            return records;
        } else {
            ErrorUtil.write("Error Occured! PIMRecordSelect.getTranferData()");
            return "#ERROR";
            // throw new UnsupportedFlavorException(prmFlavor);
        }
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data can be provided in. The array should be
     * ordered according to preference for providing the data (from most richly descriptive to least descriptive).
     * 
     * @return an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    /**
     * Returns whether or not the specified data flavor is supported for this object.
     * 
     * @param prmFlavor
     *            the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(
            DataFlavor prmFlavor) {
        for (int i = 0; i < flavors.length; i++) {
            if (prmFlavor.equals(flavors[i])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Notifies this object that it is no longer the owner of the contents of the clipboard.
     * 
     * @param clipboard
     *            the clipboard that is no longer owned
     * @param contents
     *            the contents which this owner had placed on the clipboard
     */
    public void lostOwnership(
            Clipboard clipboard,
            Transferable contents) {
    }

    Vector records;

    static DataFlavor pimRecordFlavor;

    private DataFlavor[] flavors = { DataFlavor.stringFlavor, pimRecordFlavor };
}
