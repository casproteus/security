package org.cas.client.platform.pimview.pimtable;

import java.io.Serializable;
import java.util.Vector;

import org.cas.client.platform.casutil.Releasable;

/**
 * 缺省的 PIMTable 类的数据模型,本模型具有数据操纵、处理能力
 */

public class PIMTableModelVecBased extends AbstractPIMTableModel implements Serializable, Releasable {
    /**
     * 根据Object[]数组列名和行数来构建一个DefaultPIMTableModel的实例
     * 
     * @param columnNames
     *            列名
     * @param rowCount
     *            行数
     */
    public PIMTableModelVecBased(Object[] columnNames, int rowCount) {
        this(convertToVector(columnNames), new Vector(rowCount));// 转换数据模型
    }

    /**
     * 根据二维Object[]数组和一维Object[]数组列名来构建一个DefaultPIMTableModel的实例
     * 
     * @param data
     *            列名
     * @param columnNames
     *            二维数据模型
     */
    public PIMTableModelVecBased(Object[][] data, Object[] columnNames) {
        this(convertToVector(data), convertToVector(columnNames));
    }

    /**
     * 根据Vector数据对象和Vector列名来构建一个DefaultPIMTableModel的实例
     * 
     * @param data
     *            数据对象
     * @param columnNames
     *            列名
     */
    public PIMTableModelVecBased(Vector data, Vector columnNames) {
        setData(data, columnNames);// 转换数据模型
    }

    /**
     * 实现父类(抽象类)中的抽象方法,返回行数 Returns the number of rows in this data table.
     * 
     * @return the number of rows in the model
     */
    public int getRowCount() {
        return data.size();
    }

    /**
     * Returns the number of columns in this data table.
     * 
     * @return the number of columns in the model
     */
    public int getColumnCount() {
        return columnIdentifiers.size();
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
        if (column < columnIdentifiers.size()) // 索引值不大于列数,
        {
            id = columnIdentifiers.elementAt(column);
        }
        return (id == null) ? super.getColumnName(column) : id.toString(); // 如果未设置就用父类的缺省命名
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
        if (column < columnIdentifiers.size())// 索引值不大于列数,
        {
            id = columnIdentifiers.elementAt(column);
        }
        return id; // 就这么返回了?不如上一个方法写得好
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
        int rowIndex = row;
        // indexes 是父类的受保护对象
        if (indexes != null) {
            // 我们从索引表中取行,因为我们的表格是可排序的,
            if (row < indexes.length) {
                rowIndex = indexes[row];
            }
        }
        // 没话说,直接返回空
        if (column < 0) {
            return null;
        }
        // 取得索引行后再取值
        Vector rowVector = (Vector) data.elementAt(rowIndex);
        return rowVector.elementAt(column);
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
            Object aValue,
            int row,
            int column) {
        int rowIndex = row;
        // indexes 是父类的受保护对象
        // 我们从索引表中取行,因为我们的表格是可排序的,
        if (indexes != null && row < indexes.length) {
            rowIndex = indexes[row];
        }
        // 没话说,不设了
        if (rowIndex >= getRowCount()) {
            return;
        }
        // 取得索引行后再设值
        Vector rowVector = (Vector) data.elementAt(rowIndex);
        rowVector.setElementAt(aValue, column);
        // 激发单元格数据更新事件
        fireTableCellUpdated(rowIndex, column);
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
            for (int i = (data.size() - 1); --i >= 0;) {
                Object obj = data.get(i);
                if (obj != null && obj instanceof Vector) {
                    ((Vector) obj).clear();
                }
                obj = null;
            }
            data.clear();
            data = null;
        }
        if (columnIdentifiers != null) {
            columnIdentifiers.clear();
            columnIdentifiers = null;
        }
    }

    /**
     * 设置新的数据模型和列名 Replaces the current <code>dataVector</code> instance variable with the new Vector of rows,
     * <code>dataVector</code>. <code>columnIdentifiers</code> are the names of the new columns. The first name in
     * <code>columnIdentifiers</code> is mapped to column 0 in <code>dataVector</code>. Each row in
     * <code>dataVector</code> is adjusted to match the number of columns in <code>columnIdentifiers</code> either by
     * truncating the <code>Vector</code> if it is too long, or adding <code>null</code> values if it is too short.
     * <p>
     * Note that passing in a <code>null</code> value for <code>dataVector</code> results in unspecified behavior, an
     * possibly an exception.
     *
     * @param data
     *            the new data vector
     * @param columnIdentifiers
     *            the names of the columns
     * @see #getDataVector
     */
    public void setData(
            Vector prmDataVector,
            Vector prmColumnIdentifiers) {
        data = nonNullVector(prmDataVector); // 保证不为空
        columnIdentifiers = nonNullVector(prmColumnIdentifiers); // 保证不为空
        justifyRows(0, getRowCount()); // 调整行
        fireTableStructureChanged(); // 激发表结构变化事件
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
            Object[][] dataVector,
            Object[] columnIdentifiers) {
        // 先统统转为Vector后再调用上一个方法
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
        justifyRows(e.getFirstRow(), e.getLastRow() + 1); // 因为只在最后加一行,这行又无数据,简单调整一下行数即可

        fireTableChanged(e); // 激发表格变化事件
    }

    /**
     * 本方法等价于fireTableChanged方法(父类的),仅简单调用一下激发方法即可 Equivalent to <code>fireTableChanged</code>.
     *
     * @param event
     *            the change event
     *
     */
    public void rowsRemoved(
            PIMTableModelEvent event) {
        fireTableChanged(event);
    }

    /**
     * 插入行的方法,如果Vector中没数据,那么这行中的数据是空的 Inserts a row at <code>row</code> in the model. The new row will contain
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
            int row,
            Vector rowData) {
        data.insertElementAt(rowData, row);// 在指定行处插入
        justifyRows(row, row + 1); // 确保一下数据的正确
        fireTableRowsInserted(row, row);
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
            int row,
            Object[] rowData) {
        insertRow(row, convertToVector(rowData));// 很简单,先转换为Vector后再插入
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
            int row) {
        // 删除
        data.removeElementAt(row);
        // 源发行删除事件
        fireTableRowsDeleted(row, row);
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
        columnIdentifiers.addElement(columnName); // 在保存列标识的Vector中加入新列的ID

        if (columnData != null) // 如果本列数据不为空
        {
            int columnSize = columnData.size();// 得到该列的中数据的数量(多少行)

            if (columnSize > getRowCount()) // 如大于当前行就设一下新行数
            {
                data.setSize(columnSize);
            }

            justifyRows(0, getRowCount()); // 保证一下

            int newColumn = getColumnCount() - 1;// 起始新行的索引
            for (int i = 0; i < columnSize; i++) {
                Vector row = (Vector) data.elementAt(i); // 得到在数据模型中的索引
                row.setElementAt(columnData.elementAt(i), newColumn); // 加上去
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
        // Sometimes the DefaultPIMTableModel is subclassed
        // instead of the AbstractPIMTableModel by mistake.
        // Set the number of rows for the case when getRowCount
        // is overridden.
        // 设置行数
        data.setSize(getRowCount());
        // 循环保证维护的数据Vector中每个元素都正确
        for (int i = from; i < to; i++) {
            if (data.elementAt(i) == null)// 如果某行为空
            {
                data.setElementAt(new Vector(), i);// 确保一下
            }
            // 把该行的长度设置一下
            ((Vector) data.elementAt(i)).setSize(getColumnCount()); // dataVector.elementAt不一定是Vector
        }
    }

    /**
     * 判断传入的Vector是否为空,否则返回一个新的Vector
     * 
     * @param v
     *            数组容量
     * @return 新的非空数据内容矢量
     */
    private static Vector nonNullVector(
            Vector v) {
        return (v != null) ? v : new Vector();
    }

    /**
     * 把一个一维Object数组转为一个Vector Returns a vector that contains the same objects as the array.
     * 
     * @param anArray
     *            the array to be converted
     * @return the new vector; if <code>anArray</code> is <code>null</code>, returns <code>null</code>
     */
    protected static Vector convertToVector(
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

    /**
     * 把一个一维Object数组转为一个Vector Returns a vector of vectors that contains the same objects as the array.
     * 
     * @param anArray
     *            the double array to be converted
     * @return the new vector of vectors; if <code>anArray</code> is <code>null</code>, returns <code>null</code>
     */
    protected static Vector convertToVector(
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

    /**
     * 将传入的Vector作一个旋转,具体算法我感觉好象有点问题
     * 
     * @param v
     *            一个不为空的Vector
     * @param a
     *            大循环起始位置
     * @param b
     *            大循环结束
     * @param shift
     *            循环多少行
     */
    private static void rotate(
            Vector v,
            int a,
            int b,
            int shift) {
        // 这个值表示要挪移的行数
        int size = b - a;
        int r = size - shift;
        int g = gcd(size, r);
        for (int i = 0; i < g; i++) {
            int to = i;
            // 先保存第一个元素
            Object tmp = v.elementAt(a + to);
            // 后面的统统往前搬喔
            for (int from = (to + r) % size; from != i; from = (to + r) % size) {
                v.setElementAt(v.elementAt(a + from), a + to);
                to = from;
            }
            // 把保存的那个放到最后喔
            v.setElementAt(tmp, a + to);
        }
    }

    /**
     * 求最小公约数
     * 
     * @param i
     *            第一操作数
     * @param j
     *            第二操作数
     * @return 最小公约数
     */
    private static int gcd(
            int i,
            int j) {
        return (j == 0) ? i : gcd(j, i % j);
    }

    private Vector data;// 保存本模型类中的数据,其中每个元素又为一个Vector (行),每个子Vector中才是真正的数据
    private Vector columnIdentifiers;// 保存本模型类中的列头标识符,以便于对列工作
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
// public void addColumn(Object columnName, Object[] columnData)
// {
// addColumn(columnName, convertToVector(columnData));//先转换为Vector后再加上去
// }
//
// /** 本方法不好,请用<code>setRowCount</code> 方法替代
// * Obsolete as of Java 2 platform v1.3. Please use <code>setRowCount</code> instead.
// * @param rowCount 行数
// * Sets the number of rows in the model. If the new size is greater
// * than the current size, new rows are added to the end of the model
// * If the new size is less than the current size, all
// * rows at index <code>rowCount</code> and greater are discarded. <p>
// *
// * @param rowCount the new number of rows
// * @see #setRowCount
// */
// /** 设置行数,如新行数大于当前行数,新加的几行放到最后,
// * 小于的话最后几行就被删除
// * Sets the number of rows in the model. If the new size is greater
// * than the current size, new rows are added to the end of the model
// * If the new size is less than the current size, all
// * rows at index <code>rowCount</code> and greater are discarded. <p>
// *
// * @param rowCount 行数
// * @see #setColumnCount
// */
// public void setRowCount (int rowCount)
// {
// int old = getRowCount();//保存一下原有行数
//
// if (old == rowCount)//没加新行就直接返回
// return;
//
// dataVector.setSize (rowCount);//扩容行数
//
// if (rowCount <= old)//少于原有行数,说明最后几行被删除了
// {
// fireTableRowsDeleted (rowCount, old-1);
// }
//
// else //下面的等同于添加新行的方法
// {
// justifyRows (old, rowCount);
// fireTableRowsInserted (old, rowCount-1);
// }
// }
//
// /** 在列末加入一列
// * Adds a column to the model. The new column will have the
// * identifier <code>columnName</code>, which may be null. This method
// * will send a
// * <code>tableChanged</code> notification message to all the listeners.
// * This method is a cover for <code>addColumn(Object, Vector)</code> which
// * uses <code>null</code> as the data vector.
// *
// * @param columnName the identifier of the column being added
// */
// public void addColumn (Object columnName)
// {
// addColumn(columnName, (Vector)null);
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
// justifyRows (0, getRowCount ());
// //激发表结构变化事件
// fireTableStructureChanged ();
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
// public void setColumnIdentifiers (Object[] newIdentifiers)
// {
// setColumnIdentifiers (convertToVector (newIdentifiers));
// }
// /** 将连续的几行到指定行
// * Moves one or more rows from the inlcusive range <code>start</code> to
// * <code>end</code> to the <code>to</code> position in the model.
// * After the move, the row that was at index <code>start</code>
// * will be at index <code>to</code>.
// * This method will send a <code>tableChanged</code> notification
// * message to all the listeners. <p>
// *
// * <pre>
// * Examples of moves:
// * <p>
// * 1. moveRow(1,3,5);
// * a|B|C|D|e|f|g|h|i|j|k - before
// * a|e|f|g|h|B|C|D|i|j|k - after
// * <p>
// * 2. moveRow(6,7,1);
// * a|b|c|d|e|f|G|H|i|j|k - before
// * a|G|H|b|c|d|e|f|i|j|k - after
// * <p>
// * </pre>
// *
// * @param start 开始行 the starting row index to be moved
// * @param end 结束行 the ending row index to be moved
// * @param to 到第几行 the destination of the rows to be moved
// * @exception ArrayIndexOutOfBoundsException if any of the elements
// * would be moved out of the table's range
// *
// */
// public void moveRow (int start, int end, int to)
// {
// int shift = to - start;//得到偏移offset
// int first, last;
// if (shift < 0)//小于零(向上移)
// {
// first = to; //大循环起始位置
// last = end;//大循环结束
// }
// else//大于零(向下移)
// {
// first = start;//大循环起始位置
// last = to + end - start;//大循环结束
// }
// //那就移动吧,乾坤大挪移
// //有没有问题? 要不要加 1 ?
// rotate (data, first, last + 1, shift);
// fireTableRowsUpdated (first, last);//激发行更新事件
// }
//
// /** 设置列标识
// * Replaces the column identifiers in the model. If the number of
// * <code>newIdentifier</code>s is greater than the current number
// * of columns, new columns are added to the end of each row in the model.
// * If the number of <code>newIdentifier</code>s is less than the current
// * number of columns, all the extra columns at the end of a row are
// * discarded. <p>
// *
// * @see #setNumRows
// * @param columnIdentifiers vector of column identifiers. If
// * <code>null</code>, set the model
// * to zero columns */
// private void setColumnIdentifiers (Vector columnIdentifiers)
// {
// setData (data, columnIdentifiers);
// }
