package org.cas.client.platform.cascontrol.dialog.customizeview;

/**
 * 数据库查询生成器接口
 */

public interface IQueryBuilder {
    /**
     * 设置类型
     * 
     * @param prmApp
     *            类型
     */
    public void setType(
            int prmApp);

    /**
     * 设置类型，类型数组，不为空
     * 
     * @param prmApps
     *            类型数组
     * @param prmSubApps
     *            子类型数组
     */
    public void setType(
            int[] prmApps,
            int[] prmSubApps);

    /**
     * 是否包含已删除项
     * 
     * @return 是否包含删除的记录，包含为true，否则为false
     */
    public boolean isContainsDel();

    /**
     * 是否选择删除的项目
     */
    public boolean isSelectedDel();

    /**
	 */
    public boolean isContainsSubFolder();

    /**
     * 是否选择删除的项目
     */
    public void setSelectedDel(
            boolean prmIsDel);

    /**
     * 设置是否包含子已删除的文件夹
     * 
     * @param prmIsContainsDel
     *            是否包含删除的记录，如果为包含则为true，否则为false
     */
    public void setContainsDel(
            boolean prmIsContainsDel);

    /**
     * 设置查找的路径
     * 
     * @param prmFolder
     *            查找文件夹路径
     */
    public void setSearchFolders(
            int[][] prmFolder);

    /**
     * 设置查找的路径
     */
    public void setSearchFolder(
            int[] prmFolderIDAry);

    /**
     * 设置是否子目录
     * 
     * @param prmIsContainsSub
     *            是否包含子目录项目
     */
    public void setContainsSubFolder(
            boolean prmIsContainsSub);

    /**
     * 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet();

    /**
     * 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSets();

    /**
     * 得到结果集
     * 
     * @param prmApp
     *            类型
     * @return List 查造的结果集 默认为查找当前的文件夹包括其子文件夹的所有已经删除和未删除的所有的记录
     */
    public Object[][] getResultSet(
            int prmApp);

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
            int prmFolder);

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
            int[] prmFolderID);

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
            int[] prmSubApps);

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
            int[][] prmFolderID);

}
