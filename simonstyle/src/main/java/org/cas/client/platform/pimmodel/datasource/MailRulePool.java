package org.cas.client.platform.pimmodel.datasource;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

public class MailRulePool implements Serializable {
    /** Creates a new instance of MailRulePool */
    public MailRulePool() {
    }

    //
    public void setAccountOption(
            int option) {
        accountOption = option;
    }

    //
    public int getAccountOption() {
        return accountOption;
    }

    //
    public void setAccount(
            String account) {
        accounts = account;
    }

    //
    public String getAccount() {
        return accounts;
    }

    //
    public void setMyNameIndex(
            int index) {
        myNameIndex = index;
    }

    //
    public int getMyNameIndex() {
        return myNameIndex;
    }

    //
    public void setMyNameOption(
            int index) {
        myNameOption = index;
    }

    //
    public int getMyNameOption() {
        return myNameOption;
    }

    //
    public void setSenderOption(
            int index) {
        senderOption = index;
    }

    //
    public int getSenderOption() {
        return senderOption;
    }

    // @see: sender
    public String getSender() {
        return sender;
    }

    // @see: sender
    public void setSender(
            String sender) {
        this.sender = sender;
    }

    //
    public void setAddresseeOption(
            int option) {
        addresseeOption = option;
    }

    //
    public int getAddresseeOption() {
        return addresseeOption;
    }

    //
    public void setAddressee(
            String addressee) {
        this.addressee = addressee;
    }

    //
    public String getAddressee() {
        return addressee;
    }

    //
    public void setTopicOption(
            int option) {
        topicOption = option;
    }

    //
    public int getTopicOption() {
        return topicOption;
    }

    // @see: topicWord
    public String getTopicWord() {
        return topicWord;
    }

    // @see: topicWord
    public void setTopicWord(
            String word) {
        topicWord = word;
    }

    //
    public void setTextOption(
            int option) {
        this.textOption = option;
    }

    //
    public int getTextOption() {
        return textOption;
    }

    // @see: textWord
    public String getTextWord() {
        return textWord;
    }

    // @see: textWord
    public void setTextWord(
            String word) {
        textWord = word;
    }

    //
    public void setTopicAndTextOption(
            int option) {
        topicAndTextOption = option;
    }

    //
    public int getTopicAndTextOption() {
        return topicAndTextOption;
    }

    // @see: topicAndTextWord
    public String getTopicAndTextWord() {
        return topicAndTextWord;
    }

    // @see: topicAndTextWord
    public void setTopicAndTextWord(
            String word) {
        topicAndTextWord = word;
    }

    //
    public void setActionOption(
            int option) {
        actionOption = option;
    }

    //
    public int getActionOption() {
        return actionOption;
    }

    //
    public void setActionInex(
            int index) {
        actionIndex = index;
    }

    //
    public int getActionIndex() {
        return actionIndex;
    }

    //
    public void setImportantOption(
            int option) {
        importantOption = option;
    }

    //
    public int getImportantOption() {
        return importantOption;
    }

    // @see: importantIndex
    public int getImportantIndex() {
        return importantIndex;
    }

    // @see: importantIndex
    public void setImportantIndex(
            int index) {
        importantIndex = index;
    }

    //
    public void setSenseOption(
            int option) {
        senseOption = option;
    }

    //
    public int getSenseOption() {
        return senseOption;
    }

    // @see: senseIndex
    public int getSenseIndex() {
        return senseIndex;
    }

    // @see: senseIndex
    public void setSenseIndex(
            int index) {
        senseIndex = index;
    }

    //
    public void setCategoryOption(
            int option) {
        categoryOption = option;
    }

    //
    public int getCategoryOption() {
        return categoryOption;
    }

    //
    public void setCategory(
            String cate) {
        category = cate;
    }

    //
    public String getCategory() {
        return category;
    }

    //
    public void setAttachOption(
            int option) {
        attachOption = option;
    }

    //
    public int getAttachOption() {
        return attachOption;
    }

    //
    public void setSizeOption(
            int option) {
        sizeOption = option;
    }

    //
    public int getSizeOption() {
        return sizeOption;
    }

    //
    public void setSize(
            int size) {
        this.size = size;
    }

    //
    public int getSize() {
        return size;
    }

    //
    public void setTimeOption(
            int option) {
        timeOption = option;
    }

    //
    public int getTimeOption() {
        return timeOption;
    }

    //
    public void setTime(
            Date date) {
        time = date;
    }

    //
    public Date getTime() {
        return time;
    }

    //
    public void setDirectSendToMe(
            boolean b) {
        directSendToMe = b;
    }

    //
    public boolean isDirectSendToMe() {
        return directSendToMe;
    }

    //
    public void setOnlySendToMe(
            boolean b) {
        onlySendToMe = b;
    }

    // @see: onlySendToMe
    public boolean isOnlySendToMe() {
        return onlySendToMe;
    }

    //
    public void setMoveOption(
            int option) {
        moveOption = option;
    }

    //
    public int getMoveOption() {
        return moveOption;
    }

    //
    public void setMovePlaceID(
            int prmPlaceID) {
        movePlaceID = prmPlaceID;
    }

    //
    public int getMovePlaceID() {
        return movePlaceID;
    }

    //
    public void setMoveCopyOption(
            int option) {
        moveCopyOption = option;
    }

    //
    public int getMoveCopyOption() {
        return moveCopyOption;
    }

    //
    public void setMoveCopyPlace(
            int prmPlaceID) {
        moveCopyPlaceID = prmPlaceID;
    }

    //
    public int getMoveCopyPlaceID() {
        return moveCopyPlaceID;
    }

    //
    public void setDeleteFlag(
            boolean b) {
        delete = b;
    }

    //
    public boolean isDelete() {
        return delete;
    }

    //
    public void setPermanentlyDelete(
            boolean b) {
        permanentlyDelete = b;
    }

    //
    public boolean isPermanentlyDelete() {
        return permanentlyDelete;
    }

    //
    public void setForwardOption(
            int option) {
        forwardOption = option;
    }

    //
    public int getForwardOption() {
        return forwardOption;
    }

    //
    public void setForwardPeople(
            String people) {
        forwardPeople = people;
    }

    //
    public String getForwardPeople() {
        return forwardPeople;
    }

    /**
     * Getter for property print.
     * 
     * @return Value of property print.
     */
    public boolean isPrint() {
        return this.print;
    }

    /**
     * Setter for property print.
     * 
     * @param print
     *            New value of property print.
     */
    public void setPrint(
            boolean print) {
        this.print = print;
    }

    /**
     * Getter for property specOption.
     * 
     * @return Value of property specOption.
     */
    public int getSpecOption() {
        return this.specOption;
    }

    /**
     * Setter for property specOption.
     * 
     * @param specOption
     *            New value of property specOption.
     */
    public void setSpecOption(
            int specOption) {
        this.specOption = specOption;
    }

    /**
     * Getter for property specMessage.
     * 
     * @return Value of property specMessage.
     */
    public String getSpecMessage() {
        return this.specMessage;
    }

    /**
     * Setter for property specMessage.
     * 
     * @param specMessage
     *            New value of property specMessage.
     */
    public void setSpecMessage(
            String specMessage) {
        this.specMessage = specMessage;
    }

    /**
     * Getter for property clearMark.
     * 
     * @return Value of property clearMark.
     */
    public boolean isClearMark() {
        return this.clearMark;
    }

    /**
     * Setter for property clearMark.
     * 
     * @param clearMark
     *            New value of property clearMark.
     */
    public void setClearMark(
            boolean clearMark) {
        this.clearMark = clearMark;
    }

    /**
     * Getter for property notifyWhenRead.
     * 
     * @return Value of property notifyWhenRead.
     */
    public boolean isNotifyWhenRead() {
        return this.notifyWhenRead;
    }

    /**
     * Setter for property notifyWhenRead.
     * 
     * @param notifyWhenRead
     *            New value of property notifyWhenRead.
     */
    public void setNotifyWhenRead(
            boolean notifyWhenRead) {
        this.notifyWhenRead = notifyWhenRead;
    }

    /**
     * Getter for property notifyWhenArrive.
     * 
     * @return Value of property notifyWhenArrive.
     */
    public boolean isNotifyWhenArrive() {
        return this.notifyWhenArrive;
    }

    /**
     * Setter for property notifyWhenArrive.
     * 
     * @param notifyWhenArrive
     *            New value of property notifyWhenArrive.
     */
    public void setNotifyWhenArrive(
            boolean notifyWhenArrive) {
        this.notifyWhenArrive = notifyWhenArrive;
    }

    /**
     * Getter for property copyOption.
     * 
     * @return Value of property copyOption.
     */
    public int getCopyOption() {
        return this.copyOption;
    }

    /**
     * Setter for property copyOption.
     * 
     * @param copyOption
     *            New value of property copyOption.
     */
    public void setCopyOption(
            int copyOption) {
        this.copyOption = copyOption;
    }

    /**
     * Getter for property copyPeople.
     * 
     * @return Value of property copyPeople.
     */
    public String getCopyPeople() {
        return this.copyPeople;
    }

    /**
     * Setter for property copyPeople.
     * 
     * @param copyPeople
     *            New value of property copyPeople.
     */
    public void setCopyPeople(
            String copyPeople) {
        this.copyPeople = copyPeople;
    }

    /**
     * Getter for property delayOption.
     * 
     * @return Value of property delayOption.
     */
    public int getDelayOption() {
        return this.delayOption;
    }

    /**
     * Setter for property delayOption.
     * 
     * @param delayOption
     *            New value of property delayOption.
     */
    public void setDelayOption(
            int delayOption) {
        this.delayOption = delayOption;
    }

    /**
     * Getter for property delayTime.
     * 
     * @return Value of property delayTime.
     */
    public String getDelayTime() {
        return this.delayTime;
    }

    /**
     * Setter for property delayTime.
     * 
     * @param delayTime
     *            New value of property delayTime.
     */
    public void setDelayTime(
            String delayTime) {
        this.delayTime = delayTime;
    }

    /**
     * Getter for property ruleName.
     * 
     * @return Value of property ruleName.
     */
    public String getRuleName() {
        return this.ruleName;
    }

    /**
     * Setter for property ruleName.
     * 
     * @param ruleName
     *            New value of property ruleName.
     */
    public void setRuleName(
            String ruleName) {
        this.ruleName = ruleName;
    }

    //
    public String toString() {
        Field[] fields = getClass().getDeclaredFields();
        int fieldCount = fields.length;
        StringBuffer sb = new StringBuffer();
        try {
            for (int i = 0; i < fieldCount; i++) {
                String type = fields[i].getType().getName();
                String fieldName = fields[i].getName();
                sb.append(fieldName).append(" = ");
                if (type.equalsIgnoreCase("int")) {
                    sb.append(fields[i].getInt(this));
                } else if (type.equalsIgnoreCase("boolean")) {
                    sb.append(fields[i].getBoolean(this));
                } else if (type.equalsIgnoreCase("java.lang.String")) {
                    sb.append((String) fields[i].get(this));
                } else if (type.equalsIgnoreCase("java.util.Date")) {
                    sb.append((Date) fields[i].get(this));
                }
                sb.append("; ");
            }
        } catch (IllegalAccessException e) {
            // e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * Getter for property isStopOtherRule.
     * 
     * @return Value of property isStopOtherRule.
     */
    public boolean isStopOtherRule() {
        return isStopOtherRule;
    }

    /**
     * Setter for property isStopOtherRule.
     * 
     * @param isStopOtherRule
     *            New value of property isStopOtherRule.
     */
    public void setIsStopOtherRule(
            boolean isStopOtherRule) {
        this.isStopOtherRule = isStopOtherRule;
    }

    // //////////////////////////////////////////////////////////////////////
    // NOTE: 变量的声明顺序不可变；
    // 库中记录的id号
    // private int id = -1;
    // 邮件规则的序列号：0、1、2、3、4。。。
    // private int series;
    // 应用的种类：0：发送；1：接收 时应用这样的规则
    // private boolean isSender;
    // type : 邮件规则中有三种动作可以被定制：0 ：条件规则； 1 : 例外规则； 2 ：动作规则
    // private int ruleType;
    /**
     * 以下的变量是在检测何种条件是使用的
     */
    // 接收帐户的名称
    private int accountOption = -1;
    private String accounts;
    // 我的姓名在不在收件人框中、抄送框中、收件人或抄送框中
    private int myNameOption = -1;
    private int myNameIndex;
    // 发件人为个人或通讯组列表
    private int senderOption = -1;
    private String sender;
    // 收件人
    private int addresseeOption = -1;
    private String addressee;
    // 主题中包含特定词语
    private int topicOption = -1;
    private String topicWord;
    // 正文包含特定词语
    private int textOption = -1;
    private String textWord;
    // 主题或正文包含特定词语
    private int topicAndTextOption = -1;
    private String topicAndTextWord;
    // 动作标记
    private int actionOption = -1;
    private int actionIndex;
    // 重要性标记
    private int importantOption = -1;
    private int importantIndex;
    // 敏感度标记
    private int senseOption = -1;
    private int senseIndex;
    // 类别是。。。
    private int categoryOption = -1;
    private String category;
    // 带有附件
    private int attachOption = -1;
    // 邮件大小在。。。之中
    private int sizeOption = -1;
    private int size;
    // 接收时间早于、晚于。。。
    private int timeOption = -1;
    private Date time;
    /**
     * 以下为从接收邮件时例外选项中的于接收邮件时条件选项的不同的项
     */
    // 直接发送给我
    private boolean directSendToMe;
    // 只发送给我
    private boolean onlySendToMe;
    /**
     * 以下为从接收邮件时动作选项中的与接收邮件时条件选项和例外选项都不同的项
     */
    // 将他移动到
    private int moveOption = -1;
    private int movePlaceID;
    // 将副本移动到
    private int moveCopyOption = -1;
    private int moveCopyPlaceID;
    // 删除
    private boolean delete;
    // 永久删除
    private boolean permanentlyDelete;
    // 转寄
    private int forwardOption = -1;
    private String forwardPeople;
    // 打印
    private boolean print;
    // 特定消息通知
    private int specOption = -1;
    private String specMessage;
    // 清除邮件标记
    private boolean clearMark;
    /**
     * 以下为发送邮件时动作选项与以上选项不同的
     */
    // 被读取时通知我
    private boolean notifyWhenRead;
    // 送达时通知我
    private boolean notifyWhenArrive;
    // 抄送给
    private int copyOption = -1;
    private String copyPeople;
    // 推迟传递时间
    private int delayOption = -1;
    private String delayTime;
    // 邮件规则的名字
    private String ruleName;
    /**
     * 以下为做此功能时添加的、原来缺少的字段值
     */
    // 停止处理其他规则
    private boolean isStopOtherRule;
}
