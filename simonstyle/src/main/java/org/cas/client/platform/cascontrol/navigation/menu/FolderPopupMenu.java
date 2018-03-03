package org.cas.client.platform.cascontrol.navigation.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascontrol.menuaction.FolderDelAction;
import org.cas.client.platform.cascontrol.menuaction.FolderNewAction;
import org.cas.client.platform.cascontrol.menuaction.FolderRenameAction;
import org.cas.client.platform.cascontrol.navigation.action.FolderEmptyAction;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.MenuConstant;

/**本类实现了导航树上的右键菜单，本右键菜单可以本设置成三种模式：
 * 一、常规模式：供所有的插件应用选用，这种模式提供的功能为一般性的导航树功能，如：新建，删除，重命名，移动文件夹，在文件夹中查找项目，
 * 		定制文件夹下的视图的显示等。插件应用可以提供自己的右键菜单，系统只有在向应用请求右键菜单得到null时，才显示本模式的菜单。
 * 二、联系人模式：由于联系人为PIM的系统提供的。所以其右键菜单也用此类予以实现。比常规模式多出几个菜单项如：发送邮件（前提是发现邮件
 * 		模块已经安装）。
 * 三、回收站模式：如果是在回收站下的文件夹上右键，则弹出菜单用该模式，其特点是没有比常规模式多出了恢复菜单项。
 */

public class FolderPopupMenu extends JPopupMenu implements ActionListener
{
    public static final int NORMAL_ITEM = 0; //常规菜单
    public static final int DELETE_ITEM = 1; //删除菜单
    public static final int CONTACT_ITEM = 2;//联系人菜单
    
    public static final int CAN_NEW_FOLDER = 0;  	//可以新建文件夹
    public static final int CANNOT_NEW_FOLDER = 1;  //不能新建文件夹
    /** Creates new FolderPopup
     * @param 应用类型
     */
    public FolderPopupMenu(String prmAppType)
    {
        this(prmAppType,NORMAL_ITEM);
    }
    
    /** Creates new FolderPopup
     * @param prmApptType 应用类型
     * @param prmType: 弹出菜单类型
     */
    private FolderPopupMenu(String prmAppType, int prmMode)
    {
        appType = prmMode;
        
        if (prmMode == NORMAL_ITEM)
            setMenuItem(menuItem, mnemonics);
        else if (prmMode == DELETE_ITEM)
            setMenuItem(deleteMenuItem, mnemonics);
    }
    
    /**Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
		String tmpMenuText = ((JMenuItem)e.getSource()).getText();
		int tmpIndex = -1;
		for(int i = 0; i < menuItem.length; i++)
			if(menuItem[i].equals(tmpMenuText))
			{
				tmpIndex = i;
				break;
			}
		if(tmpIndex < 0)
		{
			ErrorUtil.write("found no matching ActionName for the popupMenu of NavigationTree.");
			return;
		}
		
		String tmpActionName = actionNames[tmpIndex];	
		    	
    	CASControl.ctrl.getAction(tmpActionName).actionPerformed(null);
    }

    /**设置弹出菜单项
     */
    private void setMenuItem(String[] prmMenuItem, char[] prmMnemonics)
    {
        for (int i = 0; i < prmMenuItem.length; ++i)
        {
            if (prmMenuItem[i].length() == 0)
            {
                addSeparator();
            }
            else
            {
                JMenuItem tmpMenuItem = PIMPool.pool.getAMenuItem();
				tmpMenuItem.setText(prmMenuItem[i]);				//此句必须显示的调用一下(奇怪的时道重命名一项的时候text为空)
	    		tmpMenuItem.setMnemonic(prmMnemonics[i]);
	    		tmpMenuItem.addActionListener(this);
	    		add(tmpMenuItem);
            }
        }
    }
    
    /**Set the dynamic content of popup menu.
     * 动态调整PopMenu，
     * 在“移动”“拷贝”“删除”“重命名”后添加相应的文件夹名称
     * @param prmNodeName the dynamic content，文件夹名称
     */
    public void setModifyName(String prmNodeName)
    {
        int[] tmpNormalIndex = {0, 1};//{2, 3, 5, 6};
        int[] tmpDeleteIndex = {2, 3};//{3, 4, 6, 7};
        int[] tmpContactIndex = {0, 1};
        int[] tmpIndex = null;
        if (appType == NORMAL_ITEM)
        {
            tmpIndex = tmpNormalIndex;
        }
        else if (appType == DELETE_ITEM)
        {
            tmpIndex = tmpDeleteIndex;
        }
        else if (appType == CONTACT_ITEM)
        {
        	tmpIndex = tmpContactIndex;
        }
        
        int tmpLen = tmpIndex.length;
        String[] tmpSrc = new String[tmpLen];
        for (int i = 0; i < tmpLen; ++i)
        {
            if (appType == NORMAL_ITEM)
            {
                tmpSrc[i] = menuItem[tmpIndex[i]];
                menuItem[tmpIndex[i]] += '"' + prmNodeName + '"';   //动态添加文件夹名称
                if (tmpIndex[i] != 1)
                {
                    menuItem[tmpIndex[i]] = menuItem[tmpIndex[i]];
                }
            }
            else if (appType == DELETE_ITEM)
            {
                tmpSrc[i] = deleteMenuItem[tmpIndex[i]];
                deleteMenuItem[tmpIndex[i]] += '"' + prmNodeName + '"';   //动态添加文件夹名称
                if (tmpIndex[i] != 3)
                {
                    deleteMenuItem[tmpIndex[i]] = deleteMenuItem[tmpIndex[i]];
                }
            }
            else if (appType == CONTACT_ITEM)
            {
            	tmpSrc[i] = contactMenuItem[tmpIndex[i]];
            	contactMenuItem[tmpIndex[i]] += '"' + prmNodeName + '"';  //动态添加文件夹名称
            	if (tmpIndex[i] != 1)
            	{
            		contactMenuItem[tmpIndex[i]] = contactMenuItem[tmpIndex[i]];
            	}
            }
        }
        //暂时没有必要,故屏蔽(008).      if (appType == DELETE_ITEM)
        //        {
        //            tmpSrc[1] = deleteMenuItem[1];
        //            deleteMenuItem[1] += '"' + prmNodeName + '"' + MenuConstant.EMPTY_FOLDER;   //动态添加文件夹名称
        //        }
        
        //重置菜单项
        removeAll();
        if (appType == NORMAL_ITEM)
        {
            setMenuItem(menuItem, mnemonics);
            for (int i = 0; i < tmpLen; ++i)
            {
                menuItem[tmpIndex[i]] = tmpSrc[i];
            }
        }
        else if (appType == DELETE_ITEM)
        {
            setMenuItem(deleteMenuItem, mnemonics);
            for (int i = 0; i < tmpLen; ++i)
            {
                deleteMenuItem[tmpIndex[i]] = tmpSrc[i];
            }
        }
        else if (appType == CONTACT_ITEM)
        {
        	setMenuItem(contactMenuItem, mnemonics);
        	for (int i = 0; i < tmpLen; ++i)
        	{
        		contactMenuItem[tmpIndex[i]] = tmpSrc[i];
        	}
        }
        if (appType == DELETE_ITEM)
        {
            deleteMenuItem[1] = tmpSrc[1];
        }
    }
    
    /**Set some menu enable or disable
     * @param isEnable  true:  menu is enable
     *                  false: menu is disable
     */
    public void setItemStatus(boolean isEnable, int prmCanNewFolder)
    {
//    	if (appType == DELETE_ITEM)
//        {
//            ((JMenuItem)getMenuComponent(3)).setEnabled(isEnable);
//            ((JMenuItem)getMenuComponent(4)).setEnabled(isEnable);
//            ((JMenuItem)getMenuComponent(2)).setEnabled(isEnable);
//            ((JMenuItem)getMenuComponent(3)).setEnabled(isEnable);
//            if (prmCanNewFolder == CAN_NEW_FOLDER)
//            {
//                ((JMenuItem)getMenuComponent(4)).setEnabled(true);
//            }
//            else if (prmCanNewFolder == CANNOT_NEW_FOLDER)
//            {
//                ((JMenuItem)getMenuComponent(4)).setEnabled(false);
//            }
//        }
//    	else if (appType == NORMAL_ITEM)
//        {
//            ((JMenuItem)getMenuComponent(2)).setEnabled(isEnable); //移动
//            ((JMenuItem)getMenuComponent(3)).setEnabled(isEnable); //复制
//            ((JMenuItem)getMenuComponent(0)).setEnabled(isEnable); //删除
//            ((JMenuItem)getMenuComponent(1)).setEnabled(isEnable); //重命名
//            
//            if (prmCanNewFolder == CAN_NEW_FOLDER)
//            {
//                ((JMenuItem)getMenuComponent(2)).setEnabled(true); //新建文件夹
//            }
//            else if (prmCanNewFolder == CANNOT_NEW_FOLDER)
//            {
//                ((JMenuItem)getMenuComponent(2)).setEnabled(false); //新建文件夹
//            }
//        }
//    	else if (appType == CONTACT_ITEM)
//    	{
//    		((JMenuItem)getMenuComponent(0)).setEnabled(isEnable); //删除
//            ((JMenuItem)getMenuComponent(1)).setEnabled(isEnable); //重命名
//            if (prmCanNewFolder == CAN_NEW_FOLDER)
//            {
//                ((JMenuItem)getMenuComponent(2)).setEnabled(true); //新建文件夹
//            }
//            else if (prmCanNewFolder == CANNOT_NEW_FOLDER)
//            {
//                ((JMenuItem)getMenuComponent(2)).setEnabled(false); //新建文件夹
//            }
//    	}
    }
   
    private String[] menuItem = {
        MenuConstant.FILE_NEW_FOLDER,
        MenuConstant.FILE_RENAME_FOLDER,
//        MenuConstant.FILE_FOLDER_MOVE,
        MenuConstant.FILE_FOLDER_DEL,
//        PIMUtility.EMPTYSTR,
//        MenuConstant.EDIT_FIND
    }; //菜单项
    private char[] mnemonics = {
    	'e',
    	'r',
//    	'v',
    	'd',
//    	' ',
//    	'f'
    };
    private String[] actionNames = {
    		"FolderNewAction",
    		"FolderRenameAction",
//    		"FolderMoveAction",
    		"FolderDelAction",
//    		"",
//    		""
    };
    
    private String[] deleteMenuItem =
    {
        MenuConstant.EMPTY,
//        MenuConstant.FOLDER_FIND,
        CASUtility.EMPTYSTR,
//        MenuConstant.FOLDER_MOVE,
//        PaneConstant.COPY,
//        PIMUtility.EMPTYSTR,
        MenuConstant.FILE_FOLDER_DEL,
        MenuConstant.FILE_RENAME_FOLDER,
        MenuConstant.FILE_NEW_FOLDER,
//        PIMUtility.EMPTYSTR,
//        MenuConstant.FOLDER_MARK
    }; //删除菜单项
   
    private String[] contactMenuItem = 
    {
    		MenuConstant.FILE_FOLDER_DEL,
            MenuConstant.FILE_RENAME_FOLDER,
            MenuConstant.FILE_NEW_FOLDER
    }; //联系人和系统联系人菜单项
    

    
    private Action[] deleteItemAction =
    {
        new FolderEmptyAction(),
        null,
//        null,
        
//        new FolderMoveAction(),
//        new FolderCopyAction(),
//        null,
        
        new FolderDelAction(IStatCons.EDITING),
        new FolderRenameAction(),
        new FolderNewAction(),
//        null,
//        
//        null,
    };//删除亚菜单项动作
   
    private Action[] contactItemAction = 
    {
    		new FolderDelAction(IStatCons.EDITING),
            new FolderRenameAction(),
            new FolderNewAction()
    }; //联系人和系统联系人菜单项动作
    
    private int appType;
}
