package org.cas.client.platform.cascontrol.dialog.customizeview;

import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimmodel.datasource.FilterInfo;
import org.cas.client.resource.international.PaneConsts;

/**
 * 数据库高级筛选实现类
 */

class SearchQuery extends ConcreteQueryBuilder {

    /** Creates a new instance of FilterQuery */
    public SearchQuery(FilterInfo prmSearchPool, WhereFactory prmFactory) {
        queryPool = prmSearchPool;
        whereFactory = prmFactory;
    }

    /**
     * 设置过滤器
     */
    public void setSearch(
            FilterInfo prmSearchPool) {
        queryPool = prmSearchPool;
    }

    /**
     * 设置查询条件的where语句
     */
    public void setFactory(
            WhereFactory prmFactory) {
        whereFactory = prmFactory;
    }

    /**
     * 取到默认的查询语句
     * 
     * @return String[] 返回查询语句条件语句
     */
    public String getQueryWhere(
            PIMViewInfo prmViewInfo) {
        return whereFactory.getWhere(prmViewInfo); // 要查找的试图类型
    }

    /**
     * 根据条件生成数据库查询条件
     */
    protected void queryConfig(
            int prmApp) {
        findPosition = queryPool.getFilterPosition(); // 得到查询查找的位置,注意在查找的时候,
        setContainsSubFolder(queryPool.isContainsSubFolders());

        // 如果查找邮件,而查找路径只选择了"收件箱",则发件箱,和已发送项中的邮件是不用查询的,所以可以退出当前的查找
        if (findPosition == null || findPosition.length() < 1) // 如果查找的路径为空,则查找当前应用的所有的记录
        {
            findPosition = PaneConsts.HEAD_PAGE;
        }

        if (findPosition.startsWith(PaneConsts.DELETE_ITEM)) // 处理查找已删除项
        {
            setSelectedDel(true); // 选择已删除项的记录
            setContainsSubFolder(true); // 设置包括子文件夹
            setSearchFolder(new int[] { CASUtility.getAPPNodeID((prmApp)) }); // 得到记录删除前的INFOLDER路径,设置查找的路径
        } else if (findPosition.equals(PaneConsts.HEAD_PAGE)) // 查找咨询管理,只有所有查找所有项目的时候,才可以查找咨询管理
        { // 此时会查找所有的记录,包括已删除项和包括所有的应用的文件和子文件夹路径
            setSelectedDel(false); // 不查找已删除项
            setSearchFolder(null); // 设置查找路径为默认的路径
            // setContainsSubFolder(true); //包含子文件夹
            setContainsDel(true); // 包含已删除项
        } else {
            setSelectedDel(false); // 不查找已删除项
            // setSearchFolder(new int[]{PIMUtility.getFolderPath(findPosition)}); //设置查找路径为默认的路径
            // setContainsSubFolder(false); //包含子文件夹
            setContainsDel(false); // 包含已删除项
        }
    }

    // NOTE:此方法没有被使用
    // /** 解析查找路径
    // * @但是目前只支持单个目录的查找，所以此方法暂时无用
    // */
    // private String[] parseFindPosition()
    // {
    // StringTokenizer tmpToken = new StringTokenizer(findPosition, PIMUtility.COMMA);
    //
    // //解析查找路径
    // ArrayList tmpList = new ArrayList();
    // while (tmpToken != null && tmpToken.hasMoreTokens())
    // {
    // tmpList.add((String) tmpToken.nextToken());
    // }
    //
    // //实例化查找数组
    // int tmpSize = tmpList.size();
    // String [] tmpFolder = new String[tmpSize];
    //
    // //得到查找路径数组
    // for (int i = tmpSize - 1; i >= 0; i--)
    // {
    // tmpFolder[i] = PIMUtility.getFolderPath((String) tmpList.get(i));
    // }
    //
    // return tmpFolder;
    // }

    private String findPosition; // 查找的路径
    private FilterInfo queryPool; // 过滤信息对象
    private WhereFactory whereFactory;
}
