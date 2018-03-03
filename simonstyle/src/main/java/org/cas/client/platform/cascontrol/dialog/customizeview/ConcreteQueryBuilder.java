package org.cas.client.platform.cascontrol.dialog.customizeview;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASModel;
import org.cas.client.platform.cascontrol.navigation.CASNode;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.FilterInfo;

/**
 * 数据库查询生成器的抽象类
 */

public class ConcreteQueryBuilder implements IQueryBuilder {
    /* Creates a new instance of FilterResultApplication */
    public ConcreteQueryBuilder() {
        decorate = new StringBuffer();
        model = CASControl.ctrl.getModel();
    }

    /**
     * 设置类型
     * 
     * @param prmApp
     *            类型
     */
    public void setType(
            int prmApp) {
        app = prmApp;
    }

    /**
     * 设置类型
     * 
     * @param prmApps
     *            [] 类型数组
     * @param prmSubApps
     *            [] 子类型数组
     */
    public void setType(
            int[] prmApps,
            int[] prmSubApps) {
        appTypes = prmApps;
        subAppTypes = prmSubApps;
    }

    /**
     * 设置是否包含子已删除的文件夹
     * 
     * @param prmIsContainsDel
     *            设置是否包含已删除的记录，如果包含则可以查询到数据库中已经删除的记录
     */
    public void setContainsDel(
            boolean prmIsContainsDel) {
        isContainsDel = prmIsContainsDel;
    }

    /**
     * 是否包含已删除项
     * 
     * @return boolean 是否已经设置为已删除标记
     */
    public boolean isContainsDel() {
        return isContainsDel;
    }

    /**
     * 是否选择删除的项目
     */
    public boolean isSelectedDel() {
        return isDel;
    }

    /**
     * 是否包含子文件夹的项目
     */
    public boolean isContainsSubFolder() {
        return isContainsSub;
    }

    /**
     * 是否选择删除的项目
     */
    public void setSelectedDel(
            boolean prmIsDel) {
        isDel = prmIsDel;
    }

    /**
     * 设置查找的路径
     * 
     * @param prmFolder
     *            查找文件夹路径
     */
    public void setSearchFolders(
            int[][] prmFolder) {
        folders = prmFolder;
    }

    /**
     * 设置查找的路径
     */
    public void setSearchFolder(
            int[] prmFolderIDAry) {
        folder = prmFolderIDAry;
    }

    /**
     * 设置是否子目录
     * 
     * @param prmIsContainsSub
     *            是否包含子目录项目
     */
    public void setContainsSubFolder(
            boolean prmIsContainsSub) {
        isContainsSub = prmIsContainsSub;
    }

    /**
     * 得到数据库查询结果集
     * 
     * @return List 返回数据库查询的结果，列表中为Vector列表
     * @针对已删除项,默认INFOLDER为"[PIM, 资讯管理, 已删除项]",则可以得到子INFOLDER路径为 [PIM, 资讯管理, 已删除项, APPOINTMENT] [PIM, 资讯管理, 已删除项, TASK]
     *                           [PIM, 资讯管理, 已删除项, CONTACT] [PIM, 资讯管理, 已删除项, DIARY] [PIM, 资讯管理, 已删除项, INBOX] [PIM,
     *                           资讯管理, 已删除项, OUTBOX] [PIM, 资讯管理, 已删除项, SENDEDITEM]
     */
    public Object[][] getResultSet() {
        ArrayList recList = new ArrayList();
        queryConfig(app); // 构造记录,把所有的记录添加到recList中
        constructRecords(recList, app, folder);
        return listToArray(recList);
    }

    /**
     * 得到数据库查询结果集
     * 
     * @return List 返回数据库查询的结果，列表中为Vector列表
     */
    public Object[][] getResultSets() {
        ArrayList recArrays = new ArrayList();
        int len = appTypes.length;

        if (folders == null) // 判断folders是否为空,为空则实例化
        {
            folders = new int[len][];
        }
        // 依次取到所有的类型的记录
        for (int i = 0; i < len; i++) {
            folder = folders[i];
            queryConfig(appTypes[i]);
            constructRecords(recArrays, appTypes[i], folder);
        }

        return listToArray(recArrays);
    }

    /**
     * 得到结果集
     * 
     * @param prmApp
     *            类型
     * @return List 查造的结果集 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet(
            int prmApp) {
        setType(prmApp);
        return getResultSet();
    }

    /**
     * 得到结果集
     * 
     * @param prmApp
     *            类型
     * @param prmSubApp
     *            子类型
     * @param prmFolder
     *            查找的路径
     * @return List 查造的结果集 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet(
            int prmApp,
            int prmSubApp,
            int prmFolder) {
        setType(prmApp);
        setSearchFolder(new int[] { prmFolder });
        return getResultSet();
    }

    /**
     * 得到结果集
     * 
     * @param prmApp
     *            类型
     * @param prmSubApp
     *            子类型
     * @param prmFolder
     *            [] 查找的文件夹路径数组
     * @return List 查造的结果集 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet(
            int prmApp,
            int prmSubApp,
            int[] prmFolderID) {
        setType(prmApp);
        setSearchFolder(prmFolderID);
        return getResultSet();
    }

    /**
     * 得到结果集
     * 
     * @param prmApp
     *            [] 类型数组
     * @param prmSubApp
     *            [] 子类型数组
     * @return List 查造的结果集 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet(
            int[] prmApps,
            int[] prmSubApps) {
        setType(prmApps, prmSubApps);
        return getResultSets();
    }

    /**
     * 得到结果集
     * 
     * @param prmApps
     *            [] 类型数组
     * @param prmSubApps
     *            [] 子类型数组
     * @param prmFolder
     *            [] 查找的文件夹路径数组
     * @return List 查造的结果集， 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet(
            int[] prmApps,
            int[] prmSubApps,
            int[][] prmFolderID) {
        setType(prmApps, prmSubApps);
        setSearchFolders(prmFolderID);
        return getResultSets();
    }

    public Object[][] listToArray(
            ArrayList arrayList) {
        int size = arrayList.size();
        if (arrayList == null || size == 0) {
            return null;
        }

        int length = 0, pos = 0;
        for (int i = size - 1; i >= 0; i--) {
            length += ((Object[][]) arrayList.get(i)).length;
        }

        Object[][] values = new Object[length][];
        length = 0;

        for (int i = 0; i < size; i++) {
            pos += length;

            Object[][] element = (Object[][]) arrayList.get(i);
            length = element.length;
            System.arraycopy(element, 0, values, pos, length);
        }
        return values;
    }

    /**
     * 可以对查找进行设置,钩子方法,用户可以通过此方法设置查找
     */
    protected void queryConfig(
            int app) {
    }

    /**
     * 钩子方法，可以通过设置查询的where条件来得到查询的条件
     */
    protected String getQueryWhere(
            PIMViewInfo prmViewInfo) {
        return null;
    }

    /**
     * 取到某应用类型的文件夹对应的表的记录
     * 
     * @param prmApp
     *            应用类型
     * @param prmFolderID
     *            文件夹路径
     * @return 查找的记录
     */
    protected Object[][] getAppRecords(
            int prmApp,
            int prmFolderID) {
        // 根据app和subApp类型和INFOLDER字段构造ViewInfo，由ViewInfo来得到记录
        viewInfo = model.getViewInfo(prmFolderID);
        if (viewInfo == null) {
            return null;
        }
        // 装饰查询的SQL语句
        decorateCustomize(getQueryWhere(viewInfo));
        String sql = decorate.toString();
        decorate.setLength(0); // 清空decorate中的查询语句

        FilterInfo filterBak = viewInfo.getFilterInfo();
        filterInfo.setFilterString(sql);
        Object[][] values = model.selectRecords(viewInfo);
        viewInfo.setFilterInfo(filterBak);

        return values;
    }

    /**
     * 取到已删除项的记录,对于已删除项的记录,除了要设置查询的条件以外,还要设置查找记录删除前所在的表(由INFOLDER字段确定)
     * 
     * @param prmSubFolder
     *            查找的文件夹路径,这些路径都属于同一个类型的.
     * @param prmFolderID
     *            对应的查找的回收站表的INFOLDER
     * @return 查找的记录
     */
    protected Object[][] getDeleteItemRecords(
            ArrayList prmSubFolder,
            int prmFolderID) {
        // 得到已删除项的viewInfo
        viewInfo = model.getViewInfo(prmFolderID);
        // 装饰查询的SQL语句
        if (viewInfo == null) {
            return null;
        }
        decorateCustomize(getQueryWhere(viewInfo));
        // 装饰删除的记录对应的INFOLDER查找是要查找INFOLDER路径
        decorateDeleteCondition(prmSubFolder);
        String sql = decorate.toString();
        decorate.setLength(0); // 清空decorate中的查询语句
        FilterInfo filterBak = viewInfo.getFilterInfo();
        filterInfo.setFilterString(sql);
        Object[][] values = model.selectRecords(viewInfo);
        viewInfo.setFilterInfo(filterBak);

        return values;
    }

    /*
     * @param prmApp 应用类型
     * @param prmSubApp 得到子应用类型
     * @param prmFolder[] //待查询的当前应用的文件夹和子文件夹,注意此处的文件夹和子文件夹是可以选择的
     * @return 所有的筛选的记录
     */
    private void constructRecords(
            ArrayList prmRecList,
            int prmApp,
            int[] prmFolderIDs) {
        int[] tmpFolder = (prmFolderIDs == null) ? new int[] { CASUtility.getAPPNodeID(prmApp) } : prmFolderIDs;// 判断路径,如果路径不存在则取到默认的根目录的路径
        int[] typeFolders = getInFolders(prmApp);// 当前数据库中prmApp应用类型对应的所有的数据库表对应的INFOLDER路径

        ArrayList sub = new ArrayList(), set = new ArrayList();// 实例化当前路径的子路径

        for (int len = tmpFolder.length, i = 0; i < len; i++) {
            if (isContainsSub) {
                sub.addAll(getSubFolders(tmpFolder[i], typeFolders));// 当前文件夹及其子文件夹路径,包含重复的子文件夹路径
            } else {
                sub.add(PIMPool.pool.getKey(tmpFolder[i]));// 添加此文件夹
            }
        }
        // 所有的文件夹对应的数据库表,遍历表得到所有的表的记录
        Iterator ite = sub.iterator();
        while (ite.hasNext()) {
            Object element = ite.next();
            if (set.contains(element)) {
                continue;
            } else {
                set.add(element);
            }
            if (!isDel) // 选择非已删除项的记录
            {
                prmRecList.add(getAppRecords(prmApp, ((Integer) element).intValue()));
            }
        }

        // 处理包括已删除项的记录,如果没有文件夹路径,则不用查找已删除项的记录
        if (set.size() > 0 && (isContainsDel || isDel)) {
            // set中包含了已删除项的路径，取set中路径对应表的记录
            Object[][] tmpDeleteItemRecords = getDeleteItemRecords(set, CASUtility.getAPPNodeID(prmApp));

            if (tmpDeleteItemRecords != null) {
                prmRecList.add(tmpDeleteItemRecords);
            }
        }
    }

    /**
     * 返回应用对应的子文件夹路径，如果应用类型大于已删除项应用类型则得不到对应的文件夹路径
     * 
     * @param prmAppType
     *            应用类型
     * @return String[] 当前应用的所有的文件夹路径
     * 
     *         对于已删除项的子文件夹则返回所有的
     */
    private int[] getInFolders(
            int prmAppType) {
        Vector tmpVec = CustOpts.custOps.APPCapsVec;
        CASNode tmpAppNode = CASControl.ctrl.getFolderTree().getRootNode();
        while (tmpAppNode != null) // 遍历树结点
        {
            if (tmpVec.indexOf(tmpAppNode.getUserObject()) == prmAppType) // 得到结点的字串在CapVec的Index并判断与参数匹配否
            {
                break;
            }
            tmpAppNode = (CASNode) tmpAppNode.getNextNode();
        }// 至此得到了参数对应的应用级别的节点－－－－－－－－－－－－－－－－－－－－－－－－－－

        ArrayList tmpList = new ArrayList();
        CASNode tmpChileNode = (CASNode) tmpAppNode.getNextNode();
        String tmpParentStr = tmpAppNode.getPath().toString();
        tmpParentStr = tmpParentStr.substring(0, tmpParentStr.length() - 1);
        while (tmpChileNode != null) {
            if (tmpAppNode.toString().indexOf(tmpParentStr) >= 0)
                tmpList.add(tmpAppNode);
            else
                break;
        }

        int tmpSize = tmpList.size();
        int[] tmpFolders = new int[tmpSize];
        for (int i = 0; i < tmpSize; i++) {
            tmpFolders[i] = ((CASNode) tmpList.get(i)).getPathID();
        }
        return tmpFolders;
    }

    private List getSubFolders(
            int prmFolder,
            int[] prmTypeFolders) {
        int len = prmTypeFolders.length;
        // String tmpFolder = prmFolder.substring(0, prmFolder.length() - 1); //截去路径[PIM, 咨询管理, 收件箱]后面的半个']'
        // tmpFolder = tmpFolder.concat(","); //并在"[PIM, 咨询管理, 收件箱"字符后面添加一个','号为"[PIM, 咨询管理, 收件箱,"

        ArrayList folderList = new ArrayList(len);
        for (int i = 0; i < len; i++) {
            // if (prmTypeFolders[i].startsWith(tmpFolder) || prmFolder.equals(prmTypeFolders[i]))
            // //遍历子文件夹,包括其相同的INFOLDER路径
            // {
            // folderList.add(prmTypeFolders[i]);
            // }
        }

        return folderList;
    }

    /**
     * 装饰钩子方法中取到的查询的where语句
     */
    protected void decorateCustomize(
            String prmWhere) {
        // 添加用户实现的where语句
        if (prmWhere == null || prmWhere.length() < 1) {
            return;
        }

        if (decorate == null) {
            decorate = new StringBuffer(); // 实例化条件字符串
        }

        // 判断是否已经装饰过，如果已经有条件要添加一个"AND"
        if (decorate.length() > 0) {
            decorate.append(" AND ");
        }
        // 添加用户实现的条件语句
        decorate.append(prmWhere);

    }

    /**
     * 本方法在查询已删除中的记录时使用,因为一个已删除项对应多个数据库表, 所以在查找一个表的已删除记录时要找到对应的回收站表,并根据INFOLDER路径查找对应的记录
     * 
     * @param prmSubFolder
     *            记录删除前的INFOLDER信息
     */
    protected void decorateDeleteCondition(
            ArrayList prmSubFolder) {
        if (decorate == null) {
            decorate = new StringBuffer(); // 实例化条件字符串
        }
        // 判断是否已经装饰过，如果已经有条件要添加一个"AND"
        if (decorate.length() > 0) {
            decorate.append(" AND ");
        }
        // 选择记录的INFOLDER字段信息
        decorate.append(" INFOLDER IN ('").append(prmSubFolder.get(0)).append('\'');
        for (int size = prmSubFolder.size(), i = 1; i < size; i++) {
            decorate.append(", '").append(prmSubFolder.get(i)).append('\'');
        }
        decorate.append(") ");
    }

    protected FilterInfo filterInfo = new FilterInfo();
    protected int app; // 类型
    protected int[] folder; // 路径
    protected int[][] folders; // 路径
    protected StringBuffer decorate; // 装饰记录的where条件
    protected ICASModel model;
    protected PIMViewInfo viewInfo; // 视图信息
    protected int[] appTypes; // 要查询的类型数组
    protected int[] subAppTypes; // 要查询的子类行的数组
    protected boolean isDel; // 是否选择已经删除的项目
    protected boolean isContainsDel = true; // 是否包含已经删除的项
    protected boolean isContainsSub = true; // 是否包含子目录文件夹
}
