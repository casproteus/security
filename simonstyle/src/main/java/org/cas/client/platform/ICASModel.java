package org.cas.client.platform;

import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.IDbDataSource;
import org.cas.client.platform.pimmodel.datasource.ViewFormat;
import org.cas.client.platform.pimmodel.event.IPIMModelListener;

/** PIM的model必须实现的接口。 */

public interface ICASModel extends Releasable {
    // insert-----------------------------------------------------------
    /**
     * 插入一条记录
     * 
     * @param: PIMRecord 记录
     * @param: prmIsRefresh 是否刷新视图
     * @return: boolean
     */
    public boolean insertRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh);

    /**
     * 插入多个记录
     * 
     * @param: List records
     * @param: boolean
     */
    public boolean insertRecords(
            List prmRecords);

    // delete-----------------------------------------------------------
    /**
     * 删除一条记录
     * 
     * @param: IRecord record
     * @return: boolean
     */
    public boolean deleteRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh);

    /**
     * 删除多条记录
     * 
     * @param: List records
     * @return: boolean
     */
    public boolean deleteRecords(
            List prmRecords);

    // restore----------------------------------------------------------
    /**
     * 恢复被删除的记录
     */
    public boolean restoreDeletedRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh);

    /**
     * 恢复被删除的记录组
     */
    public boolean restoreDeletedRecords(
            List prmRecords);

    // permanentlyDelete---------------------------------------------------
    /**
     * 彻底删除多条记录
     * 
     * @param: List records
     * @return: boolean
     */
    public boolean permanentlyDeleteRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh,
            boolean isDelAttach);

    /**
     * 彻底删除多条记录
     * 
     * @param: List records
     * @return: boolean
     */
    public boolean permanentlyDeleteRecords(
            List prmRecords,
            boolean isDelAttach);

    // update---------------------------------------------------------------
    /**
     * 修改一条记录
     * 
     * @param: PIMRecord record
     * @return: boolean
     */
    public boolean updateRecord(
            PIMRecord prmRecord,
            boolean prmIsRefresh);

    /**
     * 修改多条记录
     * 
     * @param: List records
     * @return: boolean
     */
    public boolean updateRecords(
            List prmRecords);

    // select---------------------------------------------------------------
    /**
     * 根据id和表名（或者其他的各种中间结构），返回某一条记录
     * 
     * @NOTE：此方法不但可以返回各应用对应的数据库表中的记录，也可以用来选择已删除项中的记录。当根据prmAppType判断 
     *                                                               出来当前应用是deleteItems时，这时的INFOLDER,应该为删除前记录的INFOLDER,而不再表示记录所在的数据库表
     *                                                               。
     * @NOTE:在已删除的记录中有INFOLDER这个属性,直接取到这个INFOLDER属性即可。
     * @TODO:将来要将prmAppType参数移至参数中的最后一位，并改名：prmIsDeleted,暂时没有改，因为model取出了记录组装 
     *                                                                        成PIMRecord后需要赋一个app给它，很多地方目前仍是根据这个属性做事，无次属性就需要根据infolder属性来解析了
     *                                                                        ， 担心性能会受影响。
     * @param int appType 类型
     * @param int recordID 记录的ID号
     * @param int prmInfolder 为删除前记录的INFOLDER路径
     * @return PIMRecord
     */
    public PIMRecord selectRecord(
            int prmAppType,
            int prmRecordID,
            int prmFolderID);

    /**
     * 根据表名（或者其他中间结构），遍历某一些记录
     * 
     * @NOTE:注意此方法不可以取已删除项中几个应用的记录,但是可以取单个应用的多条记录
     * @param int appType 类型
     * @param int[]recordIDs 记录的ID号
     * @param prmFolderID
     *            记录删除前的ID号
     * @return PIMRecord 记录
     */
    public Vector selectRecords(
            int prmAppType,
            int[] prmRecordIDs,
            int prmFolderID);

    /**
     * 根据具体中间结构名，遍历某一些记录
     * 
     * @param int appType
     * @param string
     *            [] where语句条件
     * @return Vector 返回记录列表
     */
    public Vector selectRecords(
            int prmApp,
            int prmFolderID,
            String prmSql);

    /**
     * 返回所有的记录
     * 
     * @param PIMViewInfo
     *            viewInfo
     * @return List
     */
    public Object[][] selectRecords(
            PIMViewInfo prmViewInfo);

    /**
     * 得到一个返回中的vector包括PIMRecord的Vector
     * 
     * @param PIMViewInfo
     *            viewInfo
     * @param String
     *            [] prmWhereSql 字符串条件
     * @return List
     */
    public Vector getAllRecord(
            PIMViewInfo prmViewInfo,
            String prmWhereSql);

    // Listener-----------------------------------------------------------------
    /**
     * 把一个事件添加到事件监听器列表，该事件表示一个PIMModel的修改
     * 
     * @param: PIMModelListener l
     */
    public void addPIMModelListener(
            IPIMModelListener prmListener);

    /**
     * 把一个事件添从事件监听器列表中删除，该事件表示一个PIMModel的修改
     * 
     * @param: PIMModelListener l
     */
    public void removePIMModelListener(
            IPIMModelListener prmListener);

    // 对ViewInfo的处理----------------------------------------------------------------------------
    /**
     * 返回视图信息
     * 
     * @param int appType
     * @param int appSubType
     * @return PIMViewInfo
     */
    public PIMViewInfo getViewInfo(
            int prmFolderID);

    /**
     * 设置视图信息
     * 
     * @param PMViewInfo
     *            viewInfo
     * @return boolean
     */
    public boolean updateViewInfo(
            PIMViewInfo prmViewInfo);

    /**
     * 将指定的viewinfo删除
     * 
     * @param: prmViewInfo 需要删除的viewinfo
     * @return: 是否删除成功
     */
    public boolean deleteViewInfo(
            PIMViewInfo prmViewInfo);

    /**
     * 设置视图的缺省值
     * 
     * @param: prmAppType 需要恢复缺省值的视图的类型索引
     * @param: prmSubAppType 需要恢复缺省值的视图的子类型索引
     */
    public void resetViewInfo(
            int prmAppType,
            int prmSubAppType,
            int prmFolderID);

    /**
     * 通过给定的PIMViewInfo得到其中的字段名称
     * 
     * @return String[] 字段名称
     */
    public String[] getFieldNames(
            PIMViewInfo prmViewInfo);

    /**
     * 通过给定的PIMViewInfo得到其中的字段宽度，每个字段与getFieldsFromViewInfo 得到的字段对应
     *
     * @return int[] 字段宽度
     */
    public int[] getFieldWidths(
            PIMViewInfo prmViewInfo);

    /**
     * 通过给定的PIMViewInfo得到其中的字
     * 
     * @return int[] 名字的索引
     */
    public int[] getFieldNameIndex(
            PIMViewInfo prmViewInfo);

    // 修改数据库结构--------------------------------------------------------
    /**
     * 添加新的字段
     * 
     * @param: PIMField field
     * @return: boolean
     */
    // public boolean addNewField(PIMField field);
    /**
     * 删除字段 注意该字段必须是用户自定义字段才可被删除。
     * 
     * @param: PIMField field
     * @return: boolean
     */
    // public boolean deleteField(PIMField field);
    /**
     * 修改已存在的字段 注意该字段必须是用户自定义字段才可被修改。
     * 
     * @param: PIMField field
     * @return: boolean
     */
    // public boolean modifyField(PIMField field);
    // 中介数据------------------------------------------------------------------
    /**
     * 保存数据，这些数据都实现了IDbDataSource接口
     * 
     * @param source
     *            需要保存的数据
     * @param event
     *            当数据发生变化是要发什么事件通知视图
     * @return boolean 保存动作是否成功
     */
    public boolean saveDataSource(
            IDbDataSource prmSource,
            int prmEve);

    /**
     * 更新数据，这些数据都实现了IDbDataSource接口
     *
     * @param source
     *            需要更新的数据
     * @param event
     *            当数据发生变化是要发什么事件通知视图
     * @return boolean 更新动作是否成功
     */
    public boolean updateDataSource(
            IDbDataSource prmSource,
            int prmEve);

    /**
     * 删除数据，这些数据都实现了IDbDataSource接口
     *
     * @param source
     *            需要删除的数据
     * @param event
     *            当数据发生变化是要发什么事件通知视图
     * @return boolean 删除动作是否成功
     */
    public boolean deleteDataSource(
            IDbDataSource prmSource,
            int prmEve);

    /**
     * 取得需要的数据，通过数据类型和条件
     *
     * @param dataType
     *            数据类型，与ModelConstants中的常量对应
     * @param condition
     *            要取得数据需要的条件。如想取得app＝2，supApp ＝ 0的PIMViewInfo信息 应传入new int[]{2,0};
     */
    public IDbDataSource getDataSource(
            int prmDataType,
            int[] prmCondition);

    /**
     * 得到所有的记录信息，condiation可以为空
     */
    public List getAllDataSource(
            int dataType,
            int[] condition);

    // 类别---------------------------------------------------------------------
    /**
     * 从数据库中得到所有的类别名字
     */
    public String[] getAllCategoryName();    
    
    /**
     * 从数据库中得到所有的类别名字
     */
    public String[] getCategoryNamesByType(int type);

    /**
     * 把需要加入的名字加入数据库中
     */
    public boolean addCategroyName(
            String prmCate, int type);

    /**
     * 将指定的名字从库中删除
     */
    public boolean deleteCategroyName(
            String prmCate);

    /**
     * 将列别重置
     */
    public boolean resetCategroyName();

    // ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
    // 联系人------------------------------------------------------------
    /**
     * 返回所有的联系人中的ID字段
     * 
     * @return int[]
     */
    public String[] getAllContactRecordId();

    /**
     * 返回所有的联系人中的表示为字段
     * 
     * @return String[]
     */
    public String[] getAllContactDisplayAs();

    /**
     * 得到联系人相关的字段: (1)ID字段 (2)FieldAs字段 (3)CommGroup通讯组 (4)EMail (5)EMail_1 (6)EMail_2 (7)INFOLDER表路径
     * 说明:INFOLDER路径不用从数据库取,根据数据库表的名字得到
     */
    public Vector getContactQuickInfo();

    /**
     * 取得联系人记录中是否是通讯组列表的字段,组成数组返回,除了是删除项的不去,其他的全取
     *
     * @return int[] 是否是通讯组的字段值组成的数组
     */
    public int[] getAllCommGroupAttrList();

    // 邮件----------------------------------------------------------------
    /**
     * 返回所有的联系人中的邮箱字段
     * 
     * @return String[]
     */
    public String[][] getAllEmailAddress();

    /**
     * 插入多条mail记录
     *
     * @para: recordList 需要插入的记录的列表
     * @para: modelPara 决定model需要做什么事情，参照emo.pim.util.ModelConstants中的声明
     * @return: boolean 插入是否成功
     */
    public Vector insertMailRecords(
            List prmRecordList);

    /**
     * 得到默认帐号
     */
    public PIMRecord getDefaultAccount();

    /**
     * 返回当前ACCOUNT（帐号）表中符合条件的帐号记录 默认情况下.状态非以下三种时，返回ACCOUNT表中所有的帐号
     * 
     * @param prmAccountState
     *            三种状态：ModelConstants.ACCOUNT_ALL (所有帐号) ModelConstants.ACCOUNT_VALID (有效帐号)
     *            ModelConstants.ACCOUNT_INVALID (无效帐号)
     * 
     * @return 返回当前ACCOUNT（帐号）表中符合条件的帐号记录,根据param不同可以分别返回所有帐号、有效帐号、无效帐号列表
     * @see emo.pim.pimutil.ModelCons
     */
    public Vector getMailAccount(
            int prmAccountState);

    /**
     * 添加一个邮件规则到model中
     */
    // public boolean addMailRule(MailRuleContainer ruleContianer);
    /**
     * 更新一个已有的邮件规则
     */
    // public boolean updateMailRule(MailRuleContainer ruleContianer);
    /**
     * 得到所有的邮件规则列表
     */
    // public List getAllMailRule();
    /**
     * 删除一个邮件规则
     */
    // public boolean deleteMailRule(int series);
    /**
     * 将邮件地址列表添加到“垃圾发件人”列表或“成人内容发件人”列表中
     * 
     * @param mailList
     *            被添加的邮件地址列表
     * @param isJunk
     *            true : 添加到“垃圾发件人”列表中 false: 添加到“成人内容发件人”列表中
     */
    public void addToJunkList(
            String[] prmMailList,
            boolean prmIsJunk);

    /**
     * 标记邮件为已读/未读
     * 
     * @param appType
     *            被标记邮件的应用类型
     * @param ids
     *            被标记邮件的id，若为null，则标记所有邮件。
     * @param hasRead
     *            true : 标记为已读 false: 标记为未读
     */
    public void markAsRead(
            int appType,
            int prmFolderID,
            int[] prmIDs,
            boolean prmHasRead,
            int icon,
            boolean isRefresh);

    /**
     * 此方法得到发件箱中的所有记录而不用viewInfo中提供的过虑条件,因为在发送的时候所有的邮件都要发送
     */
    public Vector getOutboxRecords();

    /**
     * 得到指定应用的未读记录的数目
     *
     * @param: prmAppType 指定应用的索引
     * @param: prmFolderPath 取得哪个文件夹下的新记录的条数
     */
    public int getNewItemCount(
            int prmAppType,
            int prmFolderID);

    // 日历-------------------------------------------------------------
    /**
     * 通过起始和FolderPath搜索一个约会记录
     */
    public PIMRecord[] getDayRecords(
            Date prmStartDate,
            int prmFolderID);

    // ____________________________________________________________________________________

    // ViewFormat---------------------------------------------------------
    /**
     * 得到视图绘制时需要的ViewFormat
     *
     * @param: prmAppType 选择的应用类型
     * @param: prmSupAppType 选择的应用子类型
     * @param: prmModeType 选择的样式类型，随应用的不同而不同，如在邮件中有已读、未读、垃圾等
     * @param: prmFolderPath 此viewForat所在的文件夹选项
     * @return: ViewFormat 保存Format的类,暂时只有font的信息
     */
    public ViewFormat getViewFormat(
            int prmAppType,
            int prmSubAppType,
            int prmModeType,
            int prmFolderID);

    /**
     * 更新ViewFormat的信息
     *
     * @param prmFormat
     *            需要更新的ViewFormat
     * @return boolean 更新是否成功
     */
    public boolean updateViewFormat(
            ViewFormat prmFormat);

    /**
     * 将一条新的ViewInfo放入数据库,当新建文件夹的时候需要
     *
     * @param: prmViewInfo 需要添加的viewinfo
     * @return: boolean 是否添加成功
     */
    public boolean addViewInfo(
            PIMViewInfo prmViewInfo);

    /**
     * 刷新，将被替代
     */
    public void refreshView(
            int prmApp);

    /**
     * 删除记录时刷新
     * 
     * @手动刷新视图
     * @param prmRefreshApp
     *            刷新的app类型 prmRecord 记录
     */
    public void deleteRefreshView(
            int prmRefreshApp,
            PIMRecord[] prmRecord);

    /**
     * 更新记录是刷新视图
     * 
     * @手动刷新视图
     * @param prmRefreshApp
     *            刷新的app类型 prmRecord 记录
     */
    public void updateRefreshView(
            int prmRefreshApp,
            PIMRecord[] prmRecord);

    /**
     * 插入记录是刷新视图
     * 
     * @手动刷新视图
     * @param prmRefreshApp
     *            刷新的app类型 prmRecord 记录
     */
    public void insertRefreshView(
            int prmRefreshApp,
            PIMRecord[] prmRecord);

    /**
     */
    public int getAppNextID(
            int prmApp);

    // 数据库内容导入导出--------------------------------------------------------
    /**
     * 导出数据到一个文件 Called by:
     */
    public void exportInfo();

    /**
     * 导入数据到一个文件
     */
    public void importInfo();

    // 安全与认证----------------------------------------------------------------
    /**
     * 得到联系人的证书
     * 
     * @param prmEmailAddress
     *            邮件地址列表
     * @return Hashtable 邮件地址对应的证书的hashtabl, key: 邮件地址， value: 证书列表，为byte[](注意：需要造型)
     */
    public Vector getCertificate(
            Vector prmEmailAddress);

    /**
     * 得到所有的证书,和私钥
     * 
     * @param prmMailAddress
     *            为用户帐号的邮件地址
     * @return Vector vector中第一个元素为私钥,第二个元素为证书,如果证书或者私钥为null,则返回null
     */
    public Vector getPrivateKeyAndCer(
            String prmMailAddress);

    /**
     * 得到单个的证书,和私钥
     * 
     * @param prmMailAddress
     *            为用户帐号的邮件地址
     * @return Vector vector中第一个元素为私钥,第二个元素为证书,如果证书或者私钥为null,则返回null
     */
    public Vector getSingalPrivateKeyAndCer(
            String prmMailAddress);

    // 在程序启动和退出时可能需要调用以下方法-------------------------------------------------
    /**
     * @return boolean
     */
    public boolean compactDataFile();

    /**
     * 检查当前输入的用户名和密码是否有效，如果数据库表中没有用户名和密码记录时，则会在用户第一次输入时保存用户输入的用户名和密码。
     * 
     * 用户名不能为null, 也不能为""字符, 密码不能为null,但是可以为""字符.【用户名和密码大小写敏感】 用户名和密码最大长度都为15个[char]，eio中定义最大为15『位』。超过长度只匹配前15个char
     * 
     *
     * @param prmUserName
     *            用户名
     * @param prmPassword
     *            密码
     * @return int 0 : 通过 （数据库中无用户和密码信息时，第一次输入的用户名和密码时，次方法都会返回 0：通过） 1 : 无此帐号 2 : 密码不符合
     */
    public int certificate(
            String prmUserName,
            String prmPassword);

    /**
     * 数据库是否连接正常，因为有些动作执行较早，可能在数据库初始化之前，所以此类动作执行前需要调本方法询问。
     * 
     * @return true if the database is connected well.
     */
    public boolean isConnectedWell();
}
// /**
// *插入一条mail记录
// *@para: record 需要插入的记录
// *@para: modelPara 决定model需要做什么事情，参照emo.pim.util.ModelConstants中的声明
// *@return: boolean 插入是否成功
// */
// public boolean insertMailRecord(PIMRecord prmRecord);
