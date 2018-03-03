package org.cas.client.platform.pimview.pimtable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Vector;

/**
 * 拖放转移器。
 */
class PIMDNDTransfer implements Transferable {
    /**
     * 保存源的引用
     */
    private Vector data;
    /**
     * 自定义的数据类型
     */
    public static final DataFlavor PIM_RECORD_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType,
            "PIMRecord");
    /**
     * 拖放转移所支持的数据类型.
     */
    private DataFlavor[] supportedFlavor = { PIM_RECORD_FLAVOR };

    /**
     * Creates a new instance of NodeTransfer
     * 
     * @param prmTable
     */
    PIMDNDTransfer(Vector vector) {
        data = vector;
    }

    /**
     * Returns an object which represents the data to be transferred. The class of the object returned is defined by the
     * representation class of the flavor.
     *
     * @see DataFlavor#getRepresentationClass
     * @param flavor
     *            the requested flavor for the data
     * @exception IOException
     *                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException
     *                if the requested data flavor is not supported.
     * @return 可转移的数据.
     */
    public Object getTransferData(
            DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (isDataFlavorSupported(flavor)) {
            return data;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

    /**
     * Returns an array of DataFlavor objects indicating the flavors the data can be provided in. The array should be
     * ordered according to preference for providing the data (from most richly descriptive to least descriptive).
     * 
     * @return an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors() {
        return supportedFlavor;
    }

    /**
     * Returns whether or not the specified data flavor is supported for this object.
     * 
     * @param flavor
     *            the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(
            DataFlavor flavor) {
        return Arrays.asList(supportedFlavor).contains(flavor);
    }

}
