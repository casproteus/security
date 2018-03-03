package org.cas.client.platform.casutil;

/**
 * 用来初始化pim系统在database中的各个系统表的缺省值,
 * 
 * @NOTE: 本类中的数组不仅仅用于初始化数据库的各个系统表的结构.其元素的顺序关系(即在数组中的位置)还要严格对应 各个字段的Int值.因为PIMDBModel在插入一个记录时,要
 *        1/根据DefaultDBInfo中的String表示的字段的index值对应到IntModelConstants中字段名数组的各个相应元素. 2/显示在Table视图上.
 *        3/在Table中快速输入时要建立一个Record,其中HashTable中各个字段的Key,就是来自DefaultDBInfo中String的描述.
 *        4/String的描述根据的是ModelDBConstants的定义.
 *        5/而要命的是:PIMDBModel要根据这个Index值去抓该字段对应的在数据库中的类型!而且没有去做一次循环比较,看该字段在数据库中对应哪个字段, 完全根据大家在各自数组中的位置来互相识别. 一句话:
 *        还是为了性能牺牲维护的做法.
 */

public interface TmpConstants {
    public static final String CONTACT = "CONTACT"; // 联系人
    public static final String CC = "CC"; // 抄送人
    public static final String BCC = "BCC"; // 暗送人
    public static final String RECIPIENT = "RECIPIENT"; // 收件人
    public static final String ADDRESSER = "ADDRESSER"; // 发件人
    public static final String ADDRESSEE = "ADDRESSEE"; // 收件人
    public static final String DEL = "DELETED_FLAG"; // 删除标志
    public static final String READED = "READED"; // 记录是否已读标志位
}
