package org.cas.client.resource.international;

/***/
public interface ErrorConstant
{
    /* Not input name for new node */
     String FOLDER_NAME_ERROR = "Please Input a name.";
    /* duplicated node */
     String FOLDER_NAME_DUP = "A folder with this name already exists. Please use another name.";
    /* moving node is failed */
     String FOLDER_MOVE_ERROR = "The folder is a special folder, and it can not be moved.";

    /* time's order is error */
     String TIME_SET_ERROR = "The end time can not occurs before the start time";
    
     String DRAGGED_TO_ROOT_ERROR = "Sorry! It can't be dragged to here ,please choose others!";
     //ErrorDialog 中用到的国际化信息
     String SELECTED_ITEMS = "选定的项目";
}
