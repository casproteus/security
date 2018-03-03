package org.cas.client.platform.cascontrol.dialog.csvtool;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IApplication;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;
import org.cas.client.platform.pimview.pimtable.PIMTable;
import org.cas.client.resource.international.ImportDlgConst;
import org.cas.client.resource.international.PaneConsts;

/**
 * 该类有需要改动:一,必须将当前应用做为参数传入进来.因为要事先CSV的多应用共用. 二,必须将参数应用的字段及类型数据取出做遍历分析,找出其中的VARCHAR以外的字段,因为从csv文件导入的字段都是VARCHAR类型的,如果用户将
 * 其匹配到了VARCHAR以外的类型的话,必须在生成Record之前先将其转化为相应的类型,否则插入记录必然出错.不要企图让该工具只支持文本字段, 时间和数字类型是看定有的.boolean类型也是肯定有的.都要能支持才行啊. TODO:
 */
public class CSVImportStep3Dlg extends JDialog implements MouseListener, ActionListener, ComponentListener {
    final int OBJ_STRING_TYPE = 0;
    final int OBJ_OTHERS_TYPE = 1;
    final int DATE_TYPE = 2;
    final int INTEGER_TYPE = 3;
    final int BYTE_TYPE = 4;

    /**
     * @param prmField
     *            为从文件中取得的字段数组
     * @param prmContentRowList
     *            为保存有一行行的数据内容的容器。
     */
    public CSVImportStep3Dlg(Frame prmParent, Object[] prmFieldInFile, ArrayList prmContentRowList, IApplication prmApp) {
        // Title,bounds－－－－－－
        super(prmParent, true);
        setTitle(ImportDlgConst.CVSIMPORT); // CSV导入
        setBounds((CustOpts.SCRWIDTH - 408) / 2, (CustOpts.SCRHEIGHT - 334) / 2, 408, 334); // 对话框的默认尺寸。
        getContentPane().setLayout(null);

        // init the contents on the dialog－－－－－－
        fieldNamesInFile = prmFieldInFile; // fieldNamesInFile:为从文件中取得的字段数组
        contentRowList = prmContentRowList; // contentRowList:为保存记录的容器
        app = prmApp;
        importableFieldIdx = app.getImportableFields();
        mapIndexToStep4 = new int[fieldNamesInFile.length]; // 用于保存组合框中取得的索引
        Arrays.fill(mapIndexToStep4, -1); // @NOTE：全初始化为－1，后面可以判断只要大于－1，表示该字段已经被匹配。
        typesNeedTransform = new Hashtable();
        defvaluesForBoolCol = new Hashtable();
        // 将字段们抓出来挨个分析.
        String[] tmpTypes = prmApp.getAppTypes();
        for (int i = 0, tmpLen = tmpTypes.length; i < tmpLen; i++) {
            if (tmpTypes[i].equals("INTEGER") || tmpTypes[i].equals("BIT") || tmpTypes[i].equals("BINARY")
                    || tmpTypes[i].equals("TIMESTAMP"))
                typesNeedTransform.put(PIMPool.pool.getKey(i), tmpTypes[i]);
        }
        Object[][] temValues = new Object[fieldNamesInFile.length][3];
        for (int i = 0; i < fieldNamesInFile.length; i++) {
            temValues[i][0] = new Object[] { fieldNamesInFile[i], Boolean.FALSE };
            temValues[i][1] = CASUtility.EMPTYSTR;
        }

        // 初始化对话框－－－－－－－－－－－－－－－－
        label = new JLabel(ImportDlgConst.CLICKTHEITEMTOIMPORT); // 顶部的可滚动列表及其说明
        model =
                new FieldsTableModel(temValues, new Object[] { ImportDlgConst.TEXTAREA, ImportDlgConst.COMMATION_AREA }); // "文本域","通讯簿域"
        table = new PIMTable(model, null, null, false);// 显示字段的表格,设置模型
        scrollPane = new PIMScrollPane(table);
        separator = new JSeparator();// 分隔线
        ok = new JButton(ImportDlgConst.OK);
        cancel = new JButton(ImportDlgConst.CANCEL);

        // 属性设置－－－－－－－－－－－－－－
        model.setCellEditable(false);
        table.setHasSorter(false); // 禁止排序;
        table.getColumn(ImportDlgConst.TEXTAREA).setCellRenderer(new TableCheckBoxRenderer()); // 文本域 -- 设置指定列绘制器

        ok.setFont(CustOpts.custOps.getFontOfDefault());
        cancel.setFont(ok.getFont());
        getRootPane().setDefaultButton(ok);

        // 搭建－－－－－－－－－－－－－
        getContentPane().add(label);
        getContentPane().add(scrollPane);
        getContentPane().add(separator);
        getContentPane().add(ok);
        getContentPane().add(cancel);

        // 布局计算－－－－－－－－－－－－－
        reLayout();

        // 加监听器－－－－－－－－
        table.addMouseListener(this);// 为表格添加监听器
        ok.addActionListener(this);
        cancel.addActionListener(this);
        getContentPane().addComponentListener(this);
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        reLayout();
    };

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    };

    /** Invoked when the component has been made invisible. */
    public void componentHidden(
            ComponentEvent e) {
    };

    /** Invoked when the mouse enters a component. */
    public void mouseEntered(
            MouseEvent e) {
    }

    /** Invoked when the mouse exits a component. */
    public void mouseExited(
            MouseEvent e) {
    }

    /** Invoked when a mouse button has been released on a component. */
    public void mouseReleased(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component. 鼠标的单击事件的处理
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        int tmpSelectedIndex = table.getSelectedRow();// 取得所选行的索引

        Object tmpValueInC1 = model.getValueAt(tmpSelectedIndex, 0);// 取得模型中指定行第一列的值

        String tmpCapStr = model.getCheckBoxName(tmpValueInC1);// 取得模型中指定文本域的值

        String tmpMatchedValue = (String) model.getValueAt(tmpSelectedIndex, 1);// 取到第二列中对应项的值

        if (tmpMatchedValue == null || tmpMatchedValue.length() < 1) // 判断第二列中对应项的值的是否为空，空时弹出CSV导入对话框
            askForSettingMapIndex(tmpSelectedIndex, tmpCapStr);
        else {
            Boolean tmpSelection = model.getSelectItem(tmpValueInC1);
            if (tmpSelection.booleanValue())
                model.setValueAt(new Object[] { tmpCapStr, Boolean.FALSE }, tmpSelectedIndex, 0);// 设置指定位置的值
            else
                model.setValueAt(new Object[] { tmpCapStr, Boolean.TRUE }, tmpSelectedIndex, 0);// 设置指定位置的值
        }
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component. 鼠标双击事件
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
        if (e.getClickCount() >= 2) { // 点击的次数大于2
            rowIndex = table.getSelectedRow();// 取得所选行的索引
            Object tmpValue = model.getValueAt(rowIndex, 0);

            String tmpString = model.getCheckBoxName(tmpValue); // 取得模型中指定文本域的值
            askForSettingMapIndex(rowIndex, tmpString);
        }
    }

    /** Invoked when an action occurs. */
    public void actionPerformed(
            ActionEvent e) {
        if (e.getSource() == cancel) {
            setVisible(false);
            dispose();
        } else if (e.getSource() == ok) {
            gatherMatchResult(); // 检查哪些行需要导入，并分别导入到DB表中的哪些列上（分别记入三个实例变量中）

            // 从app对象转变成folderID.
            String tmpAppName = app.getClass().getName();
            int tmpPos = tmpAppName.indexOf("App_");
            int tmpIndex = CustOpts.custOps.APPNameVec.indexOf(tmpAppName.substring(tmpPos + 4));// 得到对于本应用的Index
            int tmpNodeID = CASUtility.getAPPNodeID(tmpIndex); // 得到本应用的NodeID

            // 构建PIMRecod Vector。
            Vector tmpRecordsVec = new Vector(); // 用于保存所有记录
            int tmpRecordCounts = contentRowList.size() - 1; // 遍历文本文件中除了首行外的每一行。
            for (int i = 0; i < tmpRecordCounts; i++) {
                Vector tmpFieldsInFileVec = CASUtility.parserStrToVec( // 将文件中的各个记录行中的字段保存到容其中
                        (String) contentRowList.get(i), '\t', fieldNamesInFile.length);

                for (int j = fieldNamesInFile.length - tmpFieldsInFileVec.size(); j > 0; j--)
                    // 如果当前行内容缺，则用空字串填满。
                    tmpFieldsInFileVec.add("");

                Hashtable tmpHash = new Hashtable();
                addOneRecord(tmpFieldsInFileVec, tmpHash);
                PIMRecord tmpRecord = new PIMRecord();
                tmpRecord.setFieldValues(tmpHash); // @NOTE:必须在将hash设进去以后再设其它值，因为Record中的hash变量尚为null。
                tmpRecord.setInfolderID(tmpNodeID);// 保存字段的记录,
                tmpRecord.setAppIndex(tmpIndex);
                tmpRecordsVec.add(tmpRecord);
            }

            if (tmpRecordsVec != null || !tmpRecordsVec.isEmpty())// 插入记录是否为空，不为空时把导入的所有联系人信息插入数据库中
                if (!CASControl.ctrl.getModel().insertRecords(tmpRecordsVec))
                    ErrorUtil
                            .write("failed in insert a record when selecting a contact(SelectedFieldDialog.confirmInputAction())");// 选择联系人时插入记录失败.");

            dispose();
        }
    }

    protected void extraAction() {
        if (ok != null)
            ok.removeActionListener(this);
        if (table != null) {
            table.removeMouseListener(this);
            table.release();
            table = null;
        }
    }

    Object getFieldName(
            int prmIndex) {
        return fieldNamesInFile[prmIndex];
    }

    private void reLayout() {
        cancel.setBounds(getContentPane().getWidth() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, getHeight()
                - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE - CustOpts.BTN_HEIGHT - CustOpts.VER_GAP,
                CustOpts.BTN_WIDTH, CustOpts.BTN_HEIGHT);// 关闭
        ok.setBounds(cancel.getX() - CustOpts.BTN_WIDTH - CustOpts.HOR_GAP, cancel.getY(), CustOpts.BTN_WIDTH,
                CustOpts.BTN_HEIGHT);

        separator.setBounds(CustOpts.VER_GAP, ok.getY() - CustOpts.SEP_HEIGHT, getWidth() - 3 * CustOpts.VER_GAP,
                CustOpts.SEP_HEIGHT);

        label.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - 3 * CustOpts.HOR_GAP, CustOpts.LBL_HEIGHT);

        scrollPane.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP + CustOpts.LBL_HEIGHT, getWidth() - 3
                * CustOpts.HOR_GAP, getHeight() - CustOpts.LBL_HEIGHT - CustOpts.SIZE_EDGE - CustOpts.SIZE_TITLE
                - CustOpts.BTN_HEIGHT - CustOpts.SEP_HEIGHT - 3 * CustOpts.VER_GAP);// 定位滚动面板
        validate();
    }

    /*
     * 在允许被映射到的，DB列号数组中（即DefaultView的IMPORT_MAP），第几个与传入的DBColumIndex号相符（指向同一个DBtable列）。
     * 即将DBColumnIndex转换为在IMPORT_MAP的位置。
     * @param prmCanInstead 为可代替的属性值
     * @return int 属性值在选择的ComboBox中的索引值
     */
    private int getMatchedIndInMapAry(
            int prmDBColInd) {
        for (int i = 0; i < importableFieldIdx.length; i++)
            // 遍历预先定义好的IMPORT_MAP数组中的每个元素。
            if (importableFieldIdx[i] == prmDBColInd)// 如果存在的话，返回IMPORT_MAP数组中的index号。
                return i;
        return -1; // 否则返回-1
    }

    /*
     * 通过在ComboBox中对应索引，反推导出在Step3的table中的对应位置（行号）
     * @param prmInsteadIndex ComboBox中对应索引项的索引值
     * @return int 在导入的用户一条记录对应数组项的索引值
     */
    private int getIndexInStep3Table(
            int prmIndexInComboBox) {
        if (IndexInStep4Vec == null
                || IndexInStep4Vec.isEmpty() // 如果为空
                || mappedIndexVec == null || mappedIndexVec.isEmpty()
                || mappedIndexVec.size() != IndexInStep4Vec.size()
                || !IndexInStep4Vec.contains(PIMPool.pool.getKey(prmIndexInComboBox)))
            return -1;

        int tmpObjIndex = IndexInStep4Vec.indexOf(PIMPool.pool.getKey(prmIndexInComboBox));// 得到参数在Step4的ComboBox中所处的位置。
        return ((Integer) mappedIndexVec.get(tmpObjIndex)).intValue(); // 得到参数在Step3的Table中对应的行号。
    }

    /*
     * 把选择的属性加到对应属性项中添加有其它类型(Integer or Date)的记录,要转类型，可能格式化的时候出错
     * @note 在转类型时，捕捉到异常，放弃当前项加入数据库
     * @param prmRecord 联系人记录列表 prmHashtable 存放导入数据库的联系人记录的Hashtable
     */
    private void addOneRecord(
            Vector prmFieldsInFileVec,
            Hashtable prmHashtable) {
        for (int i = 0; i < IndexInStep4Vec.size(); i++) {
            int tmpCheckValue = ((Integer) mappedIndexVec.get(i)).intValue();
            int tmpIndexValue = ((Integer) IndexInStep4Vec.get(i)).intValue();
            Object tmpObj = prmFieldsInFileVec.get(tmpCheckValue);
            tmpObj = (tmpObj == null) ? CASUtility.EMPTYSTR : tmpObj;

            // 把一条记录中，除了（String）类型外的其它类型转化为固定的格式，捕捉到异常时，抛弃改元素
            // 是否包含一个列号。该列号通过由参数转化得到，即参数表示的是在IMPORT_MAP数组中占第几位，转换为最终指向的DBTable中的列号。
            if (!typesNeedTransform.containsKey(PIMPool.pool.getKey(importableFieldIdx[tmpIndexValue])))
                prmHashtable.put(PIMPool.pool.getKey(importableFieldIdx[tmpIndexValue]), tmpObj);
            else {
                Object tmpTypedObj = getTheRightType(String.valueOf(tmpObj), tmpIndexValue);// 把字符串类型转化为固定
                if (tmpTypedObj != null) // 的类型(Date、Integer、Short)等，如果为null，则类型转化失败.
                    prmHashtable.put(PIMPool.pool.getKey(importableFieldIdx[tmpIndexValue]), tmpTypedObj);
            }
        }
    }

    /*
     * 处理类型转化
     * @param prmFieldStr 等待被进行正确类型转换的值。
     * @param prmSelectedIndex 内容tmpFieldStr所被映射到的DBTable中的列的Index号，本方法先判断该列是否属 于非字符串的特殊类型，如果是特殊类型的话就对tmpFieldStr进行类型转换。
     * @return Object String类型转化为固定的类型Date或者Integer
     */
    private Object getTheRightType(
            String prmFieldStr,
            int prmSelectedIndex) {
        // 取当前值的类型
        Object tmpTypeStr = typesNeedTransform.get(PIMPool.pool.getKey(importableFieldIdx[prmSelectedIndex]));
        if (tmpTypeStr.equals("TIMESTAMP")) // Date 类型
            return getDateType(prmFieldStr);
        else if (tmpTypeStr.equals("INTEGER")) // Integer 类型
            return getIntegerType(prmFieldStr);
        else if (tmpTypeStr.equals("BIT"))
            return getBooleanType(importableFieldIdx[prmSelectedIndex], prmFieldStr);
        else if (tmpTypeStr.equals("SHORT"))
            return getByteType(prmFieldStr);
        else
            return null;
        // //TODO:可以优化，改为用下面的机制替换，以省缺HashMap的开销。
        // switch importableFieldIdx[tmpSelectedIndex] //如果不是VARCHAR类型的字段，则转换。
        // {
        // case ContactDefaultViewstants.DECORATION_DAY:
        // return parseDateType(tmpFieldStr);
        // break;
        // case ContactDefaultViewstants.BIRTHDAY:
        // PIMPool.pool.getKey(DATE_TYPE);
        // break;
        // case ContactDefaultViewstants.SEX:
        // return parseByteType(tmpFieldStr);
        // break;
        // }
    }

    /*
     * 把字符串类型转化到Date类型
     * @param prmFieldElement 导入的联系人记录中的字段
     * @return Date 如果解析捕捉到异常，则返回空，否则为Date
     */
    private Date getDateType(
            String prmFieldElement) {
        if (prmFieldElement == null || prmFieldElement.length() < 1)// 如果为空，或者为PIMUtility.EMPTYSTR字符串是无法处理格式转化，返回null
            return null;
        Date tmpDate = null;
        try {
            tmpDate = DateFormat.getDateInstance().parse(prmFieldElement);
        } catch (java.text.ParseException pe) {
            return null;
        }
        return tmpDate;
    }

    /*
     * 把字符串类型转化到Short类型
     * @param prmFieldElement 导入的联系人记录中的字段
     * @return Integer 如果解析捕捉到异常，则返回空，否则为Short
     */
    private Byte getByteType(
            String prmFieldElement) {
        if (prmFieldElement == null || prmFieldElement.length() < 1)// 如果为空，或者为PIMUtility.EMPTYSTR字符串是无法处理格式转化，返回null
            return null;

        Byte tmpInt = null;
        try {
            tmpInt = Byte.valueOf(prmFieldElement);
        } catch (NumberFormatException e) {
            return null;
        }
        return tmpInt;
    }

    /*
     * 把字符串类型转化到Integer类型
     * @param prmFieldElement 导入的联系人记录中的字段
     * @return Integer 如果解析捕捉到异常，则返回空，否则为Integer
     */
    private Integer getIntegerType(
            String prmFieldElement) {
        if (prmFieldElement == null || prmFieldElement.length() < 1)// 如果为空，或者为PIMUtility.EMPTYSTR字符串是无法处理格式转化，返回null
            return null;

        Integer tmpInt = null;
        try {
            tmpInt = Integer.valueOf(prmFieldElement);
        } catch (NumberFormatException e) {
            return null;
        }
        return tmpInt;
    }

    /*
     * 把字符串转换为Boolean类型. 1/遇到布尔字段时,先检查该字段是否已经有供与之比较的默认值. 2/如果尚没有,则弹出信息框,请用户指定一个默认值.
     * 3/用户指定了以后,该供比较的默认值被存入defvaluesForBoolCol中. 4/以后给列的字符串将与之比较,不一样的全转化为TRUE,一样的全转化未FALSE.
     */
    private Boolean getBooleanType(
            int prmIndex,
            String prmFieldElement) {
        if (prmFieldElement == null || prmFieldElement.length() < 1)// 如果为空，或者为PIMUtility.EMPTYSTR字符串是无法处理格式转化，返回null
            return null;
        Object tmpDefaultValue = defvaluesForBoolCol.get(PIMPool.pool.getKey(prmIndex));
        if (tmpDefaultValue == null) {
            int tmpAnsw =
                    JOptionPane.showConfirmDialog(this, "CVS导入工具在导入外部数据时遇到一个布尔型字段,请确认该列中是否将<" + prmFieldElement
                            + ">作为默认字段?", PaneConsts.TITLE, JOptionPane.YES_NO_OPTION);
            if (tmpAnsw == 0) {
                defvaluesForBoolCol.put(PIMPool.pool.getKey(prmIndex), prmFieldElement);
                return Boolean.FALSE;
            } else
                return Boolean.TRUE;
        } else
            return tmpDefaultValue.equals(prmFieldElement) ? Boolean.FALSE : Boolean.TRUE;
    }

    /*
     * 将用户的匹配结果汇总到三个Vector中。 mappedIndexVec 用来放置哪些行已被设置映射的容器。 IndexInStep4Vec
     * 用来跟mappedIndexVec对应，表示每个设置了映射的行指向的是step4的Combobox的第几项。 mappedDBColIndexVec
     * 用来放表结构中列的位置序号。如：Step3中的1,2,3行分别选中并映射到Step4的3,7,9行。
     */
    private void gatherMatchResult() {
        int tmpRowCount = model.getRowCount();
        if (tmpRowCount != fieldNamesInFile.length || tmpRowCount < 1)
            return;

        mappedIndexVec = new Vector(); // 用来放置哪些行已被设置映射的容器。
        IndexInStep4Vec = new Vector(); // 用来跟mappedIndexVec对应，表示每个设置了映射的行指向的是step4的Combobox的第几项。
        mappedDBColIndexVec = new Vector();// 用来放表结构中列的位置序号。如：Step3中的1,2,3行分别选中并映射到Step4的3,7,9行。

        // 先检查并记录下Table中哪些行被设为“需要导入”，并映射到Step4的第几行。存到两个变量：mappedIndexVec、IndexInStep4Vec中
        for (int i = 0; i < tmpRowCount; i++) {
            Object tmpValue = model.getValueAt(i, 0); // 取Table表格中各行第零列的值
            tmpValue = model.getSelectItem(tmpValue);
            if (tmpValue != null && ((Boolean) tmpValue).booleanValue())// 如果被选中，则进入
                if (mapIndexToStep4[i] >= 0) { // 如果该选中行被设置了映射，则记录该行的行号和指向的Step4中的ComboBox的行号。
                    mappedIndexVec.add(PIMPool.pool.getKey(i));
                    IndexInStep4Vec.add(PIMPool.pool.getKey(mapIndexToStep4[i]));// 把选中行第二列的属性值加到列表中
                }
        }

        // 如果表中有行既被设置成需要导入，而且又被赋予了映射关系，则将映射到数据库表的列的在数据库中的序号存入applicableCaptionVec
        if (IndexInStep4Vec != null && !IndexInStep4Vec.isEmpty())// 为空表示没有行既被设置成需要导入，又被赋予了映射关系。
            for (int i = 0, tmpLen = IndexInStep4Vec.size(); i < tmpLen; i++) {// 遍历Step3表中每个被选中且有效的行。
                int tmpKey = ((Integer) IndexInStep4Vec.get(i)).intValue(); // 取出行中映射到的目标在Step4中ComboBox中的序号。
                int tmpMap = importableFieldIdx[tmpKey];// 得到ComboBox的model中这个序号的所对应的值，（也是个int值，表示字段在表结构中的序号）
                try {
                    mappedDBColIndexVec.add(PIMPool.pool.getKey(tmpMap));
                } catch (NumberFormatException nfe) {
                    ErrorUtil.write("I don't know When the following exception will be thrown");
                    ErrorUtil.write(nfe);
                    continue;// 选择一个没有的属性的时候则不添加当前的这个属性
                }
            }
    }

    /*
     * 设置表格选择的内容
     * @param prmSelectedIndex:表格索引
     * @param prmCheckBoxName 表格内容
     */
    private void askForSettingMapIndex(
            int prmSelectedIndexIntable,
            String prmCheckBoxName) {
        CSVImportStep4Dlg importContactStep4Dlg = new CSVImportStep4Dlg(this, model, prmSelectedIndexIntable);// 打开选择字段对话框,openSelected
        importContactStep4Dlg.setVisible(true);
        mapIndexToStep4[prmSelectedIndexIntable] = importContactStep4Dlg.getComboBoxIndex();// 从组合框取得的索引值保存到数组中

        if (importContactStep4Dlg.isOkClicked())// 设置指定位置的值
        {
            model.setValueAt(new Object[] { prmCheckBoxName, importContactStep4Dlg.getBoxSelection() },
                    prmSelectedIndexIntable, 0);
        }
        importContactStep4Dlg.dispose();
    }

    IApplication app;
    private JLabel label;
    private PIMScrollPane scrollPane;
    private JSeparator separator;// 分隔线
    private JButton ok, cancel;
    private Vector mappedDBColIndexVec; // 用来放表结构中列的位置序号。如：Step3中的1,2,3行分别选中并映射到Step4的3,7,9行。
                                        // 3,7,9行在Step4的ComboBox的model中分别对应有一个int数字，该数字表示的就是DB表中的列号。
    private int[] dbColIndOfSelDisDoubFields; // 用于存放选中的，与DisplayAs字段等效的字段列号。如：表中1,2,3行分别对应姓，电话，邮件。
                                              // 则该变量中则存放姓和邮件的列号。因为这两个字段属于DisplayAs字段的替身字段。详见
                                              // DefaultView中的DISPLA_AS_DOUBLE数组的定义。
    int[] importableFieldIdx;
    private int[] mapIndexToStep4; // 用于记录本对话盒的表中的每一行所对应到的ImportContactStep4Dlg对话盒中的ConboBox中的第几个。
    private Vector mappedIndexVec; // 用来标记step3的表中哪些行已经被设置了映射，比如其中值如果是1,5,6则表示第一、五、六行已被设置。
    private Vector IndexInStep4Vec; // 用来跟mappedIndexVec对应，表示每个设置了映射的行指向的是step4的Combobox的第几项。
    private Hashtable typesNeedTransform;
    private Hashtable defvaluesForBoolCol;
    private int rowIndex;
    private ArrayList contentRowList;
    private Object[] fieldNamesInFile;
    private PIMTable table;
    private FieldsTableModel model;
}
