package org.cas.client.resource.international;

/***/
public interface PIMtaskFunction
{
    /*按钮的文本"发送任务状态报告[S]..."*/
    String  PIM_JBUTTON_SEND="发送任务状态报告[S]...";
    /*标签："开始日期[R]:“，"状态[K]:"和"截止日期[D]:"*/
    String  PIM_JLABEL_START="开始日期[R]:";
    String  PIM_JLABEL_END="截止日期[D]:";    
    String  PIM_JLABEL_STATUS="状态[K]:";
    /*按钮文本"重复周期[U]..."*/
    String  PIM_JBUTTON_REPAT="重复周期[U]...";
    /*标签："优先级[Y]:"和"完成率[L] "*/
    String  PIM_JLABEL_PRI="优先级[Y]:";
    String  PIM_JLABEL_FINISH="完成率[L] ";
    /*完成率的JTEXTFIELD并且默认文本为"0%"*/
    String  PIM_JTEXTFIELD_FINISH="0%";
    /*标签：”播放声音“*/
    String  PIM_JLABEL_PLAY="播放声音[P]";
    /*”提醒“复选框*/
    String  PIM_JCHECKBOX_AWOKE="提醒[M]:";
    /*对话盒TITLE为"任务周期"，此对话盒由”重复周期“按钮控制*/
    String  PIM_JDIALOG_TITLE="任务周期";
    /*主窗体上的"发送 "，"取消 "，"确定 "按钮，其中"发送 "只有在分配任务时才显示*/
    String  PIM_JBUTTON_PRINT_TEXT="发送 ";
    String  PIM_JBUTTON_QUIT="取消 ";
    String  PIM_JBUTTON_SURE="确定 ";
    /*主窗体的标题*/
    String  PIM_JFRAME_TITLE="无任务--标题";
    /*“任务”和“细节”选项卡*/
    String  PIM_JPANEL_TASK_TITLE="任务";
    String  PIM_JPANEL_DETAIL_TITLE="细节";
    /*标签："该邮件没有发送"*/
    String  PIM_JLABEL_NOSEND="该邮件没有发送";
    /*按钮的文本"收件人"*/
    String  PIM_JBUTTON_ACCEPT="收件人";
    /*"wq@emo3.com"收件人的地址，是JTEXTFIELD_ACCEPT的默认文本*/
    String  PIM_JTEXTFIELD_ACCEPT="wq@emo3.com";
    /*标签："主题[J]:"及主题为"撰写演讲搞"*/
    String  PIM_JLABEL_SUBJECT="主题[J]:";
    String  PIM_JTEXTFIELD_SUBJECT="撰写演讲搞";
    /*复选框"分配任务[N]"*/
    String  PIM_JCHECKBOX_TASK="分配任务[N]";
    /*标签："所有者"及所有人"王强"文本框*/
    String  PIM_JLABEL_OWN_TITLE="所有者";
    String  PIM_JTEXTFIELD_OWN_TEXT="王强";
    /*复选框的标题*/
    String  PIM_JCHECKBOX_SAVE_TEXT="在我的任务列表中保存此任务的更新副本";
    String  PIM_JCHECKBOX_SEND_TEXT="此任务完成后给我发送状态报告[M]:";
    /*”任务“选项卡上的按钮*/
    String  PIM_JBUTTON_LINKMAN_TEXT="联系人[C]... ";
    /*“细节”选项卡上的标签*/
    String  PIM_JLABEL_FR_TEXT="完成日期[M]:";
    String  PIM_JLABEL_WORK1_TEXT="工作总量[W]:";
    /*”细节“选项卡上的文本框，标签*/
    String  PIM_JTEXTFIELD_WORK1_TEXT="0小时";
    String  PIM_JLABEL_LICHENG_TEXT="里程[G]:";
    String  PIM_JLABEL_WORK2_TEXT="工作总量[W]:";
    String  PIM_JTEXTFIELD_WORK2_TEXT="0小时";
    String  PIM_JLABEL_XINXI_TEXT="记帐信息[B]:";
    String PIM_JLABEL_PLACE_TEXT="单位[P]:";
    String  PIM_JLABEL_LIST_TEXT="更新列表[L]:";
    /*”细节“选项卡上的按钮*/
    String PIM_JBUTTON_CREAT_TEXT="创建未分配的副本[Y] ";
    /*”任务周期“对话盒的内容*/
    /*单选按钮"按天[D]"，"按周[W]"，"按月[M]"，"按年[Y]"*/
    String PIM_JRADIO_DAY_TEXT="按天[D]";
    String PIM_JRADIO_WEEK_TEXT="按周[W]";
    String PIM_JRADIO_MONTH_TEXT="按月[M]";
    String PIM_JRADIO_YEAR_TEXT="按年[Y]";
    /*单选按钮"重复间隔为[C]"及包含的时间选择复选框*/
    String PIM_JRADIO_REPAT_TEXT="重复间隔为[C]";
    String PIM_JLABEL_REPAT_TEXT="周后的";
    String PIM_JCHECKBOX_SUN_TEXT="星期日";
    String PIM_JCHECKBOX_MON_TEXT="星期一";
    String PIM_JCHECKBOX_TUES_TEXT="星期二";
    String PIM_JCHECKBOX_WED_TEXT="星期三";
    String PIM_JCHECKBOX_THURS_TEXT="星期四";
    String PIM_JCHECKBOX_FRI_TEXT="星期五";
    String PIM_JCHECKBOX_SAT_TEXT="星期六";
    /*单选按钮"每当任务完成后的第[G]"及它对应的标签："周重新开始"，*/
    String PIM_JRADIO_FINISH_TEXT="每当任务完成后的第[G]";
    String PIM_JLABEL_ANEW_TEXT="周重新开始";
    /*”任务周期“对话盒中的"确定"，"取消"，"删除周期[R]"按钮*/
    String PIM_JBUTTON_DSURE_TEXT="确定";
    String PIM_JBUTTON_DQUIT_TEXT="取消";
    String PIM_JBUTTON_DELETE_TEXT="删除周期[R]";
    /*”重复范围“中的标签："开始[S]:"*/
    String PIM_JLABEL_ASTART_TEXT="开始[S]:";
    /*“重复范围”中的单选按钮"无结束日期[O]""重复[F]"*/
    String PIM_JRADIO_NOF_TEXT="无结束日期[O]";
    String PIM_JRADIO_REPATF_TEXT="重复[F]";
    /*“重复”单选按钮相对应的标签*/
    String PIM_JLABEL_REPATF_TEXT="次后结束";
    /*“重复范围”中的单选按钮"结束于[B]"*/
    String PIM_JRADIO_FINISHD_TEXT="结束于[B]";
    /*”按天“单选按钮包含的信息*/
    /*”按天“定期模式中的信息*/
    String PIM_JRADIO_EVERYDAY_TEXT="每[V]";
    String PIM_JTEXTFIELD_EVERYDAY_TEXT="1";
    String PIM_JLABEL_EVERYDAY_TEXT="天";
    String PIM_JRADIO_EVERYWORK_TEXT="每个工作日[E]";
    String PIM_JRADIO_EVERYF_TEXT="每当任务完成后第[G]";
    String PIM_JTEXTFIELD_EVF_TEXT="1";
    String PIM_JLABEL_EVF_TEXT="天重新开始";
    /*“按月”单选按钮包含的信息*/
    String PIM_JRADIO_MEVERY="每[A]";
    String PIM_JTEXTFIELD_AM="1";
    String PIM_JLABEL_AM="个月的第";
    String PIM_JTEXTFIELD_AD_TEXT="5";
    String PIM_JLABEL_AD_TEXT="天";
    String PIM_JRADIO_MEVERYE_TEXT="每[E]";
    String PIM_JTEXTFIELD_EM_TEXT="1";
    String  PIM_JLABEL_EM_TEXT="个月的";
    String PIM_COMBOBOX_ED1_TEXT="第一个";
    String PIM_JRADIOMEVERYG_TEXT="每当任务完成后第[G]";
    String PIM_JTEXTFIELD_GM_TEXT="1";
    String PIM_JLABEL_GM_TEXT="个月的";
    /*“按年”单选按钮包含的信息*/
    String PIM_JRADIO_EVERYV_TEXT="每年[V]";
    String PIM_JRADIO_YEARE_TEXT="每年[E]";
    String PIM_JLABEL_YEARE_TEXT="的";
    String PIM_JRADIO_YEARG_TEXT="每当任务完成后第[G]";
    String PIM_JTEXTFIELD_YEARG_TEXT="1";
    String PIM_JLABEL_YEARG_TEXT="年重新开始  ";
    /*”任务“选项卡上的”开始日期“组合框，并设置它的一些项目*/
    String PIM_COMBOBOX_ITEM_NULL="无";
    String PIM_COMBOBOX_ITEM_TIME="2003-3-6";
    String PIM_COMBOBOX_ITEM_DATE="2003-4-6";
     /*”任务“选项卡上的”结束日期“组合框，并设置它的一些项目*/
    String PIM_COMBOBOXEND_ITEM_NULL="无";
    String PIM_COMBOBOXEND_ITEM_TIME="2003-3-6";
    String PIM_COMBOBOXEND_ITEM_DATE="2003-4-6";
     /*”任务“选项卡上的”状态“组合框，并设置它的一些项目*/
    String PIM_COMBOBOX_STATUS_START="未开始";
    String PIM_COMBOBOX_STATUS_DOING="进行中";
    String PIM_COMBOBOX_STATUS_FINISH="已完成";
    String PIM_COMBOBOX_STATUS_WAITING="正在等待其他人";
    String PIM_COMBOBOX_STATUS_DELAY="已推迟";
     /*”任务“选项卡上的”播放声音“组合框，并设置它的一些项目*/
    String PIM_COMBOBOX_PLAY_NO_SOUND="无声音";
     /*”细节“选项卡上的”完成日期“组合框，并设置它的一些项目*/
    String PIM_COMBOBOX_FR_NULL="无";
    String PIM_COMBOBOX_FR_TIME="2003-4-2";
    /*"任务周期"对话盒中的“开始”组合框*/
    String PIM_COMBOBOX_ASTART_DATE="2003-3-5(星期三)";
    String PIM_COMBOBOX_ASTART_TIME="2003-3-6";
    String PIM_COMBOBOX_ASTART_TIME1="2003-4-6";
     /*"任务周期"对话盒中的“结束于”组合框*/
    String PIM_COMBOBOX_FINISH_TIME="2003-5-7(星期三)";
     /*"任务周期"对话盒中的“按年”中的组合框*/
    String PIM_COMBOBOX_ED2_TIME="星期三";
    String PIM_COMBOBOX_YEAR_V="三月";
    String PIM_JTEXTFIELD_YEAR_V="5";
    String PIM_COMBOBOX_YEAR_E1="三月";
    String PIM_COMBOBOX_FIRST="第一个";
    String PIM_COMBOBOX_ITEM_WEEK="星期三";
    /* 当单击”确定“按钮时弹出的消息提示*/
    String PIM_JOPTIONPANE_MESSAGE="因为该任务已完成，所以它的提醒被关闭";
    /*"细节"选项卡中单击”创建未分配的副本“按钮弹出的信息*/
    String PIM_JOPTIONPANE_CONFIRM_DIALOG_MESSAGE="如果创建了该任务的未分配的副本，该副本将属于你个人，而且将不再接收到关于你所分配任务的更新版本";
     /*当单击”发送“按钮弹出的信息*/
    String PIM_JOPTIONPANE_CONFIFM_MESSAGE="已经将这个任务要求，定址到多个收件人，该任务的副本将不被更新";
    /*“完成率”文本框*/
    String PIM_JTEXTFIELD_FINISHED="100%";
    /*窗体标题一定含有次常亮*/
    String PIM_JFRAME_APPEND="任务";
    /*当邮件地址为自己的时，弹出的消息*/
    String PIM_JOPTIONPANE_SENDSELF_MESSAGE="不得向自己发送任务要求";
    String PIM_STR_EDN="小时";
}
