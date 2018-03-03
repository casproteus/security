package org.cas.client.platform.pimview.pimtable;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringBufferInputStream;
import java.io.StringReader;

import javax.swing.plaf.UIResource;

import org.cas.client.platform.casutil.CASUtility;

/**
 * 这个类用来实现表格中的数据转移
 */

class BasicPIMTransferable implements Transferable, UIResource {
    /** 保存普通文本风格的数据 */
    protected String plainData;
    /** 保存网页格式文本风格的数据 */
    protected String htmlData;
    /** 定义网页格式文本风格 */
    private static DataFlavor[] htmlFlavors;
    /** 定义字符串风格 */
    private static DataFlavor[] stringFlavors;
    /** 定义普通文本风格 */
    private static DataFlavor[] plainFlavors;

    /**
     * 静态初始化块,将以上支持的风格初始化
     */
    static {
        try {
            // 数组长度为3
            htmlFlavors = new DataFlavor[3];
            // 三种解析方式
            htmlFlavors[0] = new DataFlavor("text/html;class=java.lang.String");
            htmlFlavors[1] = new DataFlavor("text/html;class=java.io.Reader");
            htmlFlavors[2] = new DataFlavor("text/html;charset=unicode;class=java.io.InputStream");

            // 数组长度为3
            plainFlavors = new DataFlavor[3];
            // 三种解析方式
            plainFlavors[0] = new DataFlavor("text/plain;class=java.lang.String");
            plainFlavors[1] = new DataFlavor("text/plain;class=java.io.Reader");
            plainFlavors[2] = new DataFlavor("text/plain;charset=unicode;class=java.io.InputStream");

            // 数组长度为2
            stringFlavors = new DataFlavor[2];
            // MIME邮件风格
            stringFlavors[0] = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=java.lang.String");
            // 字符串风格
            stringFlavors[1] = DataFlavor.stringFlavor;

        } catch (ClassNotFoundException cle) {
            System.err.println("error initializing javax.swing.plaf.basic.BasicTranserable");
        }
    }

    /**
     * 构建器,传入参数为两种风格的数据
     * 
     * @param plainData
     *            文本数据
     * @param htmlData
     *            网页数据
     */
    BasicPIMTransferable(String plainData, String htmlData) {
        this.plainData = plainData;
        this.htmlData = htmlData;
    }

    /**
     * 得到本类所支持的可转移的数据风格 Returns an array of DataFlavor objects indicating the flavors the data can be provided in. The
     * array should be ordered according to preference for providing the data (from most richly descriptive to least
     * descriptive).
     * 
     * @return an array of data flavors in which this data can be transferred
     */
    public DataFlavor[] getTransferDataFlavors() {
        // 先找扩展的复杂数据风格是否支持
        DataFlavor[] richerFlavors = getRicherFlavors();
        int nRicher = (richerFlavors != null) ? richerFlavors.length : 0;
        // 再找本类定义的数据风格是否支持
        int nHTML = (isHTMLSupported()) ? htmlFlavors.length : 0;
        int nPlain = (isPlainSupported()) ? plainFlavors.length : 0;
        int nString = (isPlainSupported()) ? stringFlavors.length : 0;
        // 总共支持类型数量
        int nFlavors = nRicher + nHTML + nPlain + nString;
        // 根据数量建所支持的风格数组
        DataFlavor[] flavors = new DataFlavor[nFlavors];

        // fill in the array
        int nDone = 0;
        // 在风格数组中加入扩展的复杂数据风格类型
        if (nRicher > 0) {
            System.arraycopy(richerFlavors, 0, flavors, nDone, nRicher);
            nDone += nRicher;
        }
        // 在风格数组中加入网页风格类型
        if (nHTML > 0) {
            System.arraycopy(htmlFlavors, 0, flavors, nDone, nHTML);
            nDone += nHTML;
        }
        // 在风格数组中加入普通文本数据风格类型
        if (nPlain > 0) {
            System.arraycopy(plainFlavors, 0, flavors, nDone, nPlain);
            nDone += nPlain;
        }
        // 在风格数组中加入字符串数据风格类型
        if (nString > 0) {
            System.arraycopy(stringFlavors, 0, flavors, nDone, nString);
            nDone += nString;
        }
        return flavors;
    }

    /**
     * 判断某种数据风格是否支持 Returns whether or not the specified data flavor is supported for this object.
     * 
     * @param flavor
     *            the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    public boolean isDataFlavorSupported(
            DataFlavor flavor) {
        // 得到本类所支持的风格种类的引用
        DataFlavor[] flavors = getTransferDataFlavors();
        // 遍历
        for (int i = 0; i < flavors.length; i++) {
            // 比较,相等为真,否则为假
            if (flavors[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 从数据风格中得到所需数据 Returns an object which represents the data to be transferred. The class of the object returned is
     * defined by the representation class of the flavor.
     *
     * @see DataFlavor#getRepresentationClass
     * @return 须转移的数据
     * @param flavor
     *            the requested flavor for the data
     * @exception IOException
     *                if the data is no longer available in the requested flavor.
     * @exception UnsupportedFlavorException
     *                if the requested data flavor is not supported.
     */
    public Object getTransferData(
            DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        // 如支持就从中取数据
        if (isRicherFlavor(flavor)) {
            return getRicherData(flavor);
        } else if (isHTMLFlavor(flavor)) // 判断是否为网页风格所支持
        {
            // 如支持就从中取数据
            String data = getHTMLData();
            // 如没数据就为设为空字符串
            data = (data == null) ? CASUtility.EMPTYSTR : data;
            // 以下几个判断为提供网页数据的表现类(在视图中绘出)
            // 先看是否可直接用字符串表示出来
            if (String.class.equals(flavor.getRepresentationClass())) {
                return data;
            } else if (Reader.class.equals(flavor.getRepresentationClass())) {
                // 次看是否可用读字符的流 Reader 表示出来
                return new StringReader(data);
            } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
                // 再看是否可用字符串输入缓冲流表示出来
                return new StringBufferInputStream(data);
            }
            // fall through to unsupported
        } else if (isPlainFlavor(flavor)) // 判断是否为普通文本风格所支持
        {
            // 如支持就从中取数据
            String data = getPlainData();
            // 如没数据就为设为空字符串
            data = (data == null) ? CASUtility.EMPTYSTR : data;
            // 先看是否可直接用字符串表示出来
            if (String.class.equals(flavor.getRepresentationClass())) {
                return data;
            } else if (Reader.class.equals(flavor.getRepresentationClass())) {
                // 次看是否可用读字符的流 Reader 表示出来
                return new StringReader(data);
            } else if (InputStream.class.equals(flavor.getRepresentationClass())) {
                // 再看是否可用字符串输入缓冲流表示出来
                return new StringBufferInputStream(data);
            }
            // fall through to unsupported

        } else if (isStringFlavor(flavor)) // 判断是否为字符串所支持
        {
            // 如支持就从中取数据
            String data = getPlainData();
            // 如没数据就为设为空字符串
            data = (data == null) ? CASUtility.EMPTYSTR : data;
            return data;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    // --- richer subclass flavors ----------------------------------------------
    /**
     * 判断某种风格是否为扩展的复杂文本风格所支持
     * 
     * @param flavor
     *            风格类型
     * @return 是否支持
     */
    protected boolean isRicherFlavor(
            DataFlavor flavor) {
        // 得到复杂文本风格风格种类的引用
        DataFlavor[] richerFlavors = getRicherFlavors();
        // 得到其支持的种类数
        int nFlavors = (richerFlavors != null) ? richerFlavors.length : 0;
        // 遍历
        for (int i = 0; i < nFlavors; i++) {
            // 比较,相等为真,否则为假
            if (richerFlavors[i].equals(flavor)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回更复杂性的风格类型 一些子类具有比本类中所定义的两种文本风格更为复杂的风格,如果本方法返回为空 这些风格将被本类中所定义的两种文本风格所替代. Some subclasses will have flavors that
     * are more descriptive than HTML or plain text. If this method returns a non-null value, it will be placed at the
     * start of the array of supported flavors.
     * 
     * @return 支持的复杂文本类型
     */
    protected DataFlavor[] getRicherFlavors() {
        return null;
    }

    /**
     * 返回更复杂性的风格类型中的数据 一些子类具有比本类中所定义的两种文本风格更为复杂的风格,如果本方法返回为空 这些风格返回的数据将被本类中所定义的两种文本风格所替代.
     * 
     * @param flavor
     *            风格炻
     * @throws UnsupportedFlavorException
     *             不支持的异常
     * @return 复杂风格类型数据
     */
    protected Object getRicherData(
            DataFlavor flavor) throws UnsupportedFlavorException {
        return null;
    }

    // --- html flavors ----------------------------------------------------------

    /**
     * 判断某种风格是否为网页风格所支持 Returns whether or not the specified data flavor is an HTML flavor that is supported.
     * 
     * @param flavor
     *            the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    protected boolean isHTMLFlavor(
            DataFlavor flavor) {
        // 得到网页风格种类的引用
        DataFlavor[] flavors = htmlFlavors;
        // 遍历
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(flavor)) {
                // 有就返回真
                return true;
            }
        }
        // 否则为假
        return false;
    }

    /**
     * 判断网页风格是否被支持,如在构建时提供的相应参数不为空就表示支持 Should the HTML flavors be offered? If so, the method getHTMLData should be
     * implemented to provide something reasonable.
     * 
     * @return 是否支持
     */
    protected boolean isHTMLSupported() {
        return htmlData != null;
    }

    /**
     * 提供网页格式数据 Fetch the data in a text/html format
     * 
     * @return 网页数据
     */
    protected String getHTMLData() {
        return htmlData;
    }

    // --- plain text flavors ----------------------------------------------------

    /**
     * 判断某种风格是否为普通文本风格所支持 Returns whether or not the specified data flavor is an plain flavor that is supported.
     * 
     * @param flavor
     *            the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    protected boolean isPlainFlavor(
            DataFlavor flavor) {
        // 得到普通文本风格种类的引用
        DataFlavor[] flavors = plainFlavors;
        // 遍历
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(flavor)) {
                // 有就返回真
                return true;
            }
        }
        // 否则为假
        return false;
    }

    /**
     * 判断是否有普通文本的数据 这个方法名起得似乎有点使人迷惑 Should the plain text flavors be offered? If so, the method getPlainData should be
     * implemented to provide something reasonable.
     * 
     * @return 是否支持
     */
    protected boolean isPlainSupported() {
        return plainData != null;
    }

    /**
     * 提供纯文本风格的数据 Fetch the data in a text/plain format.
     * 
     * @return 纯文本数据
     */
    protected String getPlainData() {
        return plainData;
    }

    // --- string flavorss --------------------------------------------------------

    /**
     * 判断某种风格是否为字符串风格所支持 Returns whether or not the specified data flavor is a String flavor that is supported.
     * 
     * @param flavor
     *            the requested flavor for the data
     * @return boolean indicating whether or not the data flavor is supported
     */
    protected boolean isStringFlavor(
            DataFlavor flavor) {
        // 得到普通文本风格种类的引用
        DataFlavor[] flavors = stringFlavors;
        // 遍历
        for (int i = 0; i < flavors.length; i++) {
            if (flavors[i].equals(flavor)) {
                // 有就返回真
                return true;
            }
        }
        // 否则为假
        return false;
    }
}
