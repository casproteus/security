package org.cas.client.platform.cascontrol;

/**
 * 本类是一个对照表，拿任何一个用int值所表示的拥有4X8=32个状态开关的“状态集”与本类比较，可以： 1、与本类中的某个变量相与－－可以知道该状态集中的某个状态是否处于“高电位”（即是否打开）状态；
 * 2、与本类中的某个变量相或－－可以使该状态集的相应的开关被置位（即置为高电位，或设成打开状态）。
 * 
 * @TODO:用三十二位来表示三十二中基本状态,可能会远远不够.将来应该尽量用long或double?当然也可以用下面的方法来扩展（user008）: 每个应用应该可以定义自己的状态.即:系统自身的菜单,于系统的状态值相比.
 *                                                                            应用自身的菜单与应用维护的状态值比较,也可与系统维护的状态值比较.
 *                                                                            系统菜单状态不应该受应用的状态影响
 *                                                                            .应用的菜单确经常会受系统状态的影响,但不应该受到其它应用的状态值的影响.
 *                                                                            必须留一位扩展用,如STATUS_OPEN
 */
public interface IStatCons {
    int NEVER = 0x00000000;// 如果某个菜单被设为该值,那么该菜单在系统处于任何状态时,都将无效.即该菜单暂时不准备开放测试.
    int ALWAYS = ~NEVER;// 如果某个菜单被设为该值,那么该菜单在系统处于任何状态时,都有效.适用于一些外挂工具.

    int OPEN = 0x00000001;// 该位表示与当前应用不是菜单项对应的应用时,该菜单项是否有效.若该位被置,则即使当前应用不是菜单项所属应用
                          // 菜单项仍然应该参与和系统状态的相与计算后才能知道有效状态,否则只要与当前应用不符,即设为无效.
    int RECORD_SELECTED = OPEN << 1;

    int FOLDER_SELECTED = RECORD_SELECTED << 1;// 有文件夹被选中

    int USER_DEFINED = FOLDER_SELECTED << 1;

    int HAVE_ATTACH = USER_DEFINED << 1;// 带附件@NTOE:该系统状态设置和使用必然和选中相关,即必然同时选中.但是此处不可以
                                        // 破坏标志的单纯性.即每个标记只能有一个置位. 只能在menuItem或系统使用时进行组合.
    int HAVE_UNDO = HAVE_ATTACH << 1;

    int HAVE_REDO = HAVE_UNDO << 1;

    int HAVE_PASTE = HAVE_REDO << 1;

    int BEEN_READ = HAVE_PASTE << 1;

    int UNREAD = BEEN_READ << 1;

    int IS_MARK = UNREAD << 1;

    int PRVIEW = IS_MARK << 1;

    int SELECT_ALL = PRVIEW << 1;

    int CAN_FIND = SELECT_ALL << 1;

    int HAS_ACCOUNT = CAN_FIND << 1;// 无帐号时禁用

    int ALL_APP = HAS_ACCOUNT << 1;

    int EDITING = ALL_APP << 1;

    int NEED_ALL_MATCH = OPEN << 31;
}
