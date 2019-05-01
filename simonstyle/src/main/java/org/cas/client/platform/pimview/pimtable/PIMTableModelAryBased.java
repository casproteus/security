package org.cas.client.platform.pimview.pimtable;

import java.io.Serializable;
import java.util.Vector;

import org.cas.client.platform.casutil.Releasable;

public class PIMTableModelAryBased extends AbstractPIMTableModel implements Serializable, Releasable {
    /**
     * 根据Object[]数组列名和行数来构建一个DefaultPIMTableModel的实例
     * 
     * @param columnNames
     *            列名
     * @param rowCount
     *            行数
     */
    public PIMTableModelAryBased(Object[] columnNames, int rowCount) {
        this(new Object[rowCount][columnNames.length], columnNames);// 转换数据模型
    }

    /**
     * 根据二维Object[]数组和一维Object[]数组列名来构建一个DefaultPIMTableModel的实例
     * 
     * @param data
     *            列名
     * @param columnNames
     *            二维数据模型
     */
    public PIMTableModelAryBased(Object[][] data, Object[] columnNames) {
        setData(data, columnNames);
    }

    /**
     * 根据Vector数据对象和Vector列名来构建一个DefaultPIMTableModel的实例
     * 
     * @param data
     *            数据对象
     * @param columnNames
     *            列名
     */
    public PIMTableModelAryBased(Vector data, Vector columnNames) {
        setData((Object[][]) convertVecToAry(data), convertVecToAry(columnNames));// 转换数据模型
    }

    /**
     * 实现父类(抽象类)中的抽象方法,返回行数 Returns the number of rows in this data table.
     * 
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return data == null ? 0 : data.length;
    }

    /**
     * Returns the number of columns in this data table.
     * 
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columnIdentifiers.length;
    }

    /**
     * 得到列头对象
     * 
     * @called by PIMTable; Header中，返回参数位置的元素。与getColumnName不同，它返回的是Object而非String。
     * @return 列头对象
     * @param column
     *            所在列
     */
    public Object getColumnTitle(
            int column) {
        Object id = null;
        if (column < columnIdentifiers.length)// 索引值不大于列数,
        {
            id = columnIdentifiers[column];
        }
        return id; // 就这么返回了?不如上一个方法写得好
    }

    /**
     * 实现父类(抽象类)中的抽象方法,返回指定单元格中的对象 Returns an attribute value for the cell at <code>row</code> and <code>column</code>.
     *
     * @return the value Object at the specified cell
     * @param row
     *            the row whose value is to be queried
     * @param column
     *            the column whose value is to be queried
     */
    public Object getValueAt(
            int row,
            int column) {
        int tmpDisplayedRow = row;
        if (indexes != null) { // indexes 是父类的受保护对象
            if (row < indexes.length) // 我们从索引表中取行,因为我们的表格是可排序的,
                tmpDisplayedRow = indexes[row];
        }
        if (column < 0) // 没话说,直接返回空
            return null;
        return data[tmpDisplayedRow][column];
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等List结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        if (data != null) {
            for (int i = (data.length - 1); --i >= 0;) {
                Object obj = data[i];
                if (obj != null && obj instanceof Vector)
                    ((Vector) obj).clear();
                obj = null;
            }
            data = null;
        }
        if (columnIdentifiers != null)
            columnIdentifiers = null;
    }

    /**
     * 重载父类(抽象类)中的方法,得到列名的字符串值
     *
     * @param column
     *            所在列
     * @return a name for this column using the string value of the appropriate member in <code>columnIdentifiers</code>
     *         . If <code>columnIdentifiers</code> does not have an entry for this index, returns the default name
     *         provided by the superclass
     */
    public String getColumnName(
            int column) {
        Object id = null;
        if (column < columnIdentifiers.length) // 索引值不大于列数,
            id = columnIdentifiers[column];
        return (id == null) ? super.getColumnName(column) : id.toString(); // 如果未设置就用父类的缺省命名
    }

    /**
     * 重载父类(抽象类)中的方法,我们的PIMTable中的单元格一定是可编辑的 Returns true regardless of parameter values.
     *
     * @param row
     *            the row whose value is to be queried
     * @param column
     *            the column whose value is to be queried
     * @return true
     * @see #setValueAt
     */
    public boolean isCellEditable(
            int row,
            int column) {
        return canEditable;
    }

    /**
     * 实现父类(抽象类)中的抽象方法,设定指定单元格中的对象 Sets the object value for the cell at <code>column</code> and <code>row</code>.
     * <code>aValue</code> is the new value. This method will generate a <code>tableChanged</code> notification.
     *
     * @param aValue
     *            the new value; this can be null
     * @param row
     *            the row whose value is to be changed
     * @param column
     *            the column whose value is to be changed
     */
    public void setValueAt(
            Object prmValue,
            int row,
            int column) {
        int tmpDisplayedRow = row;// indexes 是父类的受保护对象
        // 我们从索引表中取行,因为我们的表格是可排序的,
        if (indexes != null && row < indexes.length)
            tmpDisplayedRow = indexes[row];

        if (tmpDisplayedRow >= getRowCount()) // 没话说,不设了
            return;

        Object[] rowVector = data[tmpDisplayedRow]; // 取得索引行后再设值
        rowVector[column] = prmValue;

        fireTableCellUpdated(tmpDisplayedRow, column); // 激发单元格数据更新事件
    }

    /**
     * 设置新的数据模型和列名 Replaces the value in the <code>dataVector</code> instance variable with the values in the array
     * <code>dataVector</code>. The first index in the <code>Object[][]</code> array is the row index and the second is
     * the column index. <code>columnIdentifiers</code> are the names of the new columns.
     * 
     * @param dataVector
     *            the new data vector
     * @param columnIdentifiers
     *            the names of the columns
     * @see #setData(Vector, Vector)
     */
    public void setData(
            Object[][] prmDataVec,
            Object[] prmColIdentifiers) {
        data = prmDataVec;
        columnIdentifiers = prmColIdentifiers;
        // justifyRows (0, getRowCount ()); //调整行
        fireTableStructureChanged(); // 激发表结构变化事件
    }

    /**
     * 本方法等价于fireTableChanged方法(父类的),仅简单调用一下激发方法即可 Equivalent to <code>fireTableChanged</code>.
     * 
     * @param event
     *            the change event
     */
    public void newDataAvailable(
            PIMTableModelEvent event) {
        fireTableChanged(event);
    }

    /**
     * 操作在数据模型中加一新行 Ensures that the new rows have the correct number of columns. This is accomplished by using the
     * <code>setSize</code> method in <code>Vector</code> which truncates vectors which are too long, and appends
     * <code>null</code>s if they are too short. This method also sends out a <code>tableChanged</code> notification
     * message to all the listeners.
     *
     * @param e
     *            this <code>PIMTableModelEvent</code> describes where the rows were added. If <code>null</code> it
     *            assumes all the rows were newly added
     * @see #getDataVector
     */
    public void newRowsAdded(
            PIMTableModelEvent e) {
        // justifyRows (e.getFirstRow (), e.getLastRow () + 1); //因为只在最后加一行,这行又无数据,简单调整一下行数即可
        fireTableChanged(e); // 激发表格变化事件
    }

    /**
     * 插入行的方法,如果Object数组中没数据,那么这行中的数据是空的 Inserts a row at <code>row</code> in the model. The new row will contain
     * <code>null</code> values unless <code>rowData</code> is specified. Notification of the row being added will be
     * generated.
     *
     * @param row
     *            the row index of the row to be inserted
     * @param rowData
     *            optional data of the row being added
     * @exception ArrayIndexOutOfBoundsException
     *                if the row was invalid
     */
    public void insertRow(
            int prmIndex,
            Object[] prmObj) {
        if (prmIndex > data.length) {
            throw new ArrayIndexOutOfBoundsException(prmIndex + " > " + data.length);
        }

        Object[][] tmpOldData = data;
        data = new Object[tmpOldData.length + 1][tmpOldData[0].length];
        System.arraycopy(tmpOldData, 0, data, 0, tmpOldData.length);

        System.arraycopy(data, prmIndex, data, prmIndex + 1, tmpOldData.length - prmIndex);
        data[prmIndex] = prmObj;

        fireTableRowsInserted(prmIndex, prmIndex);
    }

    /**
     * 删除指定行 Removes the row at <code>row</code> from the model. Notification of the row being removed will be sent to
     * all the listeners.
     *
     * @param row
     *            the row index of the row to be removed
     * @exception ArrayIndexOutOfBoundsException
     *                if the row was invalid
     */
    public void removeRow(
            int index) {
        if (index >= data.length) {
            throw new ArrayIndexOutOfBoundsException(index + " >= " + data.length);
        } else if (index < 0) {
            throw new ArrayIndexOutOfBoundsException(index);
        }

        int j = data.length - index - 1;
        if (j > 0) {
            System.arraycopy(data, index + 1, data, index, j);
        }

        Object[][] oldData = data;
        data = new Object[data.length - 1][oldData[0].length];
        System.arraycopy(oldData, 0, data, 0, data.length);

        fireTableRowsDeleted(index, index);// 源发行删除事件
    }

    /**
     * 在列末加入一列 Adds a column to the model. The new column will have the identifier <code>columnName</code>, which may be
     * null. <code>columnData</code> is the optional vector of data for the column. If it is <code>null</code> the
     * column is filled with <code>null</code> values. Otherwise, the new data will be added to model starting with the
     * first element going to row 0, etc. This method will send a <code>tableChanged</code> notification message to all
     * the listeners.
     *
     * @param columnName
     *            the identifier of the column being added
     * @param columnData
     *            optional data of the column being added
     */
    public void addColumn(
            Object columnName,
            Vector columnData) {
        Object[] tmpOldAry = columnIdentifiers;
        columnIdentifiers = new Object[tmpOldAry.length + 1];
        System.arraycopy(tmpOldAry, 0, columnIdentifiers, 0, tmpOldAry.length);
        columnIdentifiers[columnIdentifiers.length] = columnName; // 在保存列标识的Vector中加入新列的ID

        if (columnData != null) // 如果本列数据不为空
        {
            int columnSize = columnData.size();// 得到该列的中数据的数量(多少行)

            int newColumn = getColumnCount() - 1;// 起始新行的索引
            for (int i = 0; i < columnSize; i++) {
                Object[] row = data[i]; // 得到在数据模型中的索引
                row[newColumn] = columnData.elementAt(i); // 加上去
            }
        } else {
            justifyRows(0, getRowCount());// 没数据就把数据模型调整一下,保证正确
        }

        fireTableStructureChanged();// 最后激发表结构变化事件
    }

    /**
     * 因为表格在视图中只有一个实例,表格模型大体也如此; 必须有设置表格是否可编辑的方法
     * 
     * @param isEditable
     *            是否可编辑
     */
    public void setCellEditable(
            boolean isEditable) {
        canEditable = isEditable;
    }

    /*
     * 子类继承时,有可能传入的参数会有错,构建器调用本方法以保证数据正确
     * @param from 从第几行开始
     * @param to 到第几行结束
     */
    private void justifyRows(
            int from,
            int to) {
        for (int i = from; i < to; i++)// 循环保证维护的数据Vector中每个元素都正确
        {
            if (data[i] == null)// 如果某行为空
            {
                data[i] = new Object[getColumnCount()];// 确保一下
            }
        }
    }

    /*
     * 把一个一维Object数组转为一个Vector Returns a vector that contains the same objects as the array.
     * @param anArray the array to be converted
     * @return the new vector; if <code>anArray</code> is <code>null</code>, returns <code>null</code>
     */
    private static Vector convertToVector(
            Object[] anArray) {
        // 错误处理
        if (anArray == null) {
            return null;
        }
        // 新建一个Vector
        Vector v = new Vector(anArray.length);
        // 循环赋值
        for (int i = 0; i < anArray.length; i++) {
            v.addElement(anArray[i]);
        }
        return v;
    }

    /*
     * 把一个一维Object数组转为一个Vector Returns a vector of vectors that contains the same objects as the array.
     * @param anArray the double array to be converted
     * @return the new vector of vectors; if <code>anArray</code> is <code>null</code>, returns <code>null</code>
     */
    private static Vector convertAryToVec(
            Object[][] anArray) {
        if (anArray == null)// 错误处理
        {
            return null;
        }

        Vector v = new Vector(anArray.length);// 新建一个Vector

        for (int i = 0; i < anArray.length; i++)// 循环赋值
        {
            v.addElement(convertToVector(anArray[i])); // 调用把一维Object数组转为一个Vector 的方法
        }
        return v;
    }

    /*
     * 把一个一维Object数组转为一个Vector Returns a vector of vectors that contains the same objects as the array.
     * @param anArray the double array to be converted
     * @return the new vector of vectors; if <code>anArray</code> is <code>null</code>, returns <code>null</code>
     */
    private Object[] convertVecToAry(
            Vector prmVec) {
        if (prmVec == null)// 错误处理
            return null;

        int tmpLength = prmVec.size();
        Object[] tmpObj = new Object[tmpLength];// 新建一个Ary

        for (int i = 0; i < tmpLength; i++)// 循环赋值
        {
            Object tmpSubAry = prmVec.get(i);
            tmpObj[i] = tmpSubAry instanceof Vector ? convertVecToAry((Vector) tmpSubAry) : tmpSubAry; // 调用把一维Object数组转为一个Vector
                                                                                                       // 的方法
        }
        return tmpObj;
    }

    private Object[][] data;// 保存本模型类中的数据,其中每个元素又为一个Vector (行),每个子Vector中才是真正的数据
    private Object[] columnIdentifiers;// 保存本模型类中的列头标识符,以便于对列工作
    private boolean canEditable = true;// 本组的表格有列是否可编辑的概念
}

//
// /** 在列末加入一列
// * Adds a column to the model. The new column will have the
// * identifier <code>columnName</code>. <code>columnData</code> is the
// * optional array of data for the column. If it is <code>null</code>
// * the column is filled with <code>null</code> values. Otherwise,
// * the new data will be added to model starting with the first
// * element going to row 0, etc. This method will send a
// * <code>tableChanged</code> notification message to all the listeners.
// *
// * @see #addColumn(Object, Vector)
// * @param columnData 数据对象
// * @param columnName 列名 */
// public void addColumn (Object columnName, Object[] columnData)
// {
// addColumn (columnName, convertToVector (columnData));//先转换为Vector后再加上去
// }
// /**将传入的Vector作一个旋转,具体算法我感觉好象有点问题
// * @param v 一个不为空的Vector
// * @param a 大循环起始位置
// * @param b 大循环结束
// * @param shift 循环多少行
// */
// private static void rotate (Vector v, int a, int b, int shift)
// {
// //这个值表示要挪移的行数
// int size = b - a;
// int r = size - shift;
// int g = gcd (size, r);
// for (int i = 0; i < g; i++)
// {
// int to = i;
// //先保存第一个元素
// Object tmp = v.elementAt (a + to);
// //后面的统统往前搬喔
// for (int from = (to + r) % size; from != i; from = (to + r) % size)
// {
// v.setElementAt (v.elementAt (a + from), a + to);
// to = from;
// }
// //把保存的那个放到最后喔
// v.setElementAt (tmp, a + to);
// }
// }
//
// /** 求最小公约数
// * @param i 第一操作数
// * @param j 第二操作数
// * @return 最小公约数
// */
// private static int gcd (int i, int j)
// {
// return (j == 0) ? i : gcd (j, i%j);
// }
//
// /** 判断传入的Vector是否为空,否则返回一个新的Vector
// * @param v 数组容量
// * @return 新的非空数据内容矢量
// */
// private static Vector nonNullVector (Vector v)
// {
// return (v != null) ? v : new Vector ();
// }
// /**设置列标识
// * Replaces the column identifiers in the model. If the number of
// * <code>newIdentifier</code>s is greater than the current number
// * of columns, new columns are added to the end of each row in the model.
// * If the number of <code>newIdentifier</code>s is less than the current
// * number of columns, all the extra columns at the end of a row are
// * discarded. <p>
// *
// * @param newIdentifiers array of column identifiers.
// * If <code>null</code>, set
// * the model to zero columns
// * @see #setNumRows
// */
// private void setColumnIdentifiers (Object[] newIdentifiers)
// {
// setColumnIdentifiers (convertToVector (newIdentifiers));
// }

// /** 设置列数
// * Sets the number of columns in the model. If the new size is greater
// * than the current size, new columns are added to the end of the model
// * with <code>null</code> cell values.
// * If the new size is less than the current size, all columns at index
// * <code>columnCount</code> and greater are discarded.
// *
// * @param columnCount the new number of columns in the model
// *
// * @see #setColumnCount
// */
// public void setColumnCount (int columnCount)
// {
// //将保存列标识的Vector重新设一下列数
// columnIdentifiers.setSize (columnCount);
// //保证一下
// // justifyRows (0, getRowCount ());
// //激发表结构变化事件
// fireTableStructureChanged ();
// }
