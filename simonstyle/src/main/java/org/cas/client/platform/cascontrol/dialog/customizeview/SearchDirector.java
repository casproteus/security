package org.cas.client.platform.cascontrol.dialog.customizeview;

import org.cas.client.platform.pimmodel.PIMViewInfo;

/**
 * 数据库查询导向器
 */
public class SearchDirector {
    /**
     * Creates a new instance of SearchDirector
     * 
     * @param prmQueryBuilder
     *            查寻对象生成器
     */
    public SearchDirector(IQueryBuilder prmQueryBuilder) {
        query = prmQueryBuilder;
    }

    /**
     * 设置QueryBuilder
     * 
     * @param prmQueryBuilder
     *            查寻对象生成器
     */
    public void setBuilder(
            IQueryBuilder prmQueryBuilder) {
        query = prmQueryBuilder;
    }

    /**
     * 得到这个Builder
     * 
     * @return 返回当前的查询对象生成器
     */
    public IQueryBuilder getBuilder() {
        return query;
    }

    /**
     * 设置要查找的是否为已删除的记录
     * 
     * @param prmIsDel
     *            如果prmIsDel为true则查询已经删除的记录，否则查询未删除的记录
     */
    public void setSelectedDel(
            boolean prmIsDel) {
        query.setSelectedDel(prmIsDel);
    }

    /**
     * 设置是否包含已经删除的记录
     * 
     * @param prmIsContainsDel
     *            如果prmIsContainsDel为true，则查询的记录中包括已经删除的记录， 否则查询的记录中不包括已经删除的记录
     */
    public void setContainsDel(
            boolean prmIsContainsDel) {
        query.setContainsDel(prmIsContainsDel);
    }

    /**
     * 是否包含子目录
     * 
     * @param prmIsContainsSub
     *            如果prmIsContainsSub为true，则查找当前文件夹的包括其子文件夹的所有的记录 否则只查询当前的文件夹
     */
    public void setContainSubFolder(
            boolean prmIsContainsSub) {
        query.setContainsSubFolder(prmIsContainsSub);
    }

    /**
     * 得到结果集
     * 
     * @param prmViewInfo
     *            视图信息
     * @return List 查造的结果集 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet(
            PIMViewInfo prmViewInfo) {
        query.setType(prmViewInfo.getAppIndex()); // 设置查询的类型
        query.setSearchFolder(new int[] { prmViewInfo.getPathID() });
        return query.getResultSet(); // 得到查询的结果集
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
        return query.getResultSet(prmApp);
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
            String prmFolder) {
        return query.getResultSet();// prmApp, prmSubApp, prmFolder);
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
            String[] prmFolder) {
        return query.getResultSet();// prmApp, prmSubApp, prmFolder);
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
        return query.getResultSet(prmApps, prmSubApps);
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
            String[][] prmFolder) {
        return query.getResultSet();// prmApps, prmSubApps, prmFolder);
    }

    private IQueryBuilder query; // 查询的对象
}
