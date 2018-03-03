package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.Timer;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.MainPane;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.MessageCons;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;

public class OpenAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        if (seleRecs == null && (seleRecs = CASControl.ctrl.getSelectRecords()) == null || seleRecs.size() == 0) {
            seleRecs = null;// 因为if语句中有可能会给seleRecs赋了值，所以这里要清空。
            return;
        }// 除错：如果本对象没有被赋予记录，而且当前应用也没有记录被选中，则直接返回-------------

        if (seleRecs.size() > 1) {// 如果记录数大于1，则需要将选中的记录扔到专门的线程中做打开操作.（线程将每200毫秒开一个）。
            if (seleRecs.size() >= CustOpts.MAX_FOR_SHOWING_DIALOG)// action被触发的第一次时需要检查，如果选中项目较多时报错。
                if (SOptionPane.showErrorDialog(MessageCons.W20493) == 2)// 该操作需要较多的内存，要继续吗？
                    return;
            new OpenThread().start();
        } else { // 如果只有一条记录，则调打开单个记录的方法打开该记录。
            openSingleRecord((PIMRecord) seleRecs.get(0));
            seleRecs = null; // NOTE:每次actionperformed之后清空seleRecs对象,是为了防止此类的实例被多次使用的情况,如:菜单中的打开选中项菜单
        } // 就是实例化本类一次,但是会使用实例方法多次的"目前所知的唯一特殊情况".故此清空,防止第二次打开的是第一次抓到的Record.
    }

    public void addRecord(
            PIMRecord prmRecord) {
        seleRecs.add(prmRecord);
    }

    public void setRecords(
            Vector prmSeleRecs) {
        seleRecs = prmSeleRecs;
    }

    /**
     * 根据记录的不同类型调相应的对话盒显示一条记录。 called by: OpenThread;
     */
    private void openSingleRecord(
            PIMRecord prmRecord) {
        /**
         * 如当前应用是IMAP文件夹时，如此IMAP文件夹仅同步了邮件 标题，则在点击时应更新此文件夹视图并下载所选中信件的信件体 及其它相关内容（标记）。在这种情况（选中记录只同步了标题时）时， 有个特殊情况，如下：
         * 当所选中的记录已在服务器上删除更新后本地的记录也会删除， 此时分为两种情况处理： 1.当所选记录为最后一条记录时 则更新完文件夹后自动选中此时的最后一条记录 （1）本记录只同步了标题，则下载信件体
         * （2）已下载信件体，则直接从model中获取 2.当所选记录处在中间时或是第一条时 则仍选更新后相同位置的记录 （1）本记录只同步了标题，则下载信件体 （2）已下载信件体，则直接从model中获取
         * 当有其它记录已被删除时，更新完后重新找到此记录并选中 （此时有个缺陷，即更新文件夹时，本来已下载的信件体也都被 删除了，现在没时间解，以后再说） 如果已下载了信件体，则直接从model中获取，同时也不更新文件夹
         * 当同步了标题后，如设置了点击时不自动下载信件体，则此时对照上 述规则，只更新信件标记。同时在预览面板上显示一警告网页 ================================================
         */
        // 此段用于IMAP协议，目前IMAP协议代码被注释掉，还用不到此处代码
        /*
         * PIMViewInfo currentViewInfo = PIMControl.ctrl.getCurrentViewInfo(); if (currentViewInfo != null) { int
         * appType = currentViewInfo.getAppType(); String folderPath = currentViewInfo.getFolderPath(); if (appType ==
         * ModelConstants.INBOX_APP) { Object object =
         * prmRecord.getFieldValues().get(Pool.getInstance().getIntegerKey(ModelDBConstants.HASDOWN)); boolean hasDown =
         * (object == null) ? false : ((Boolean)object).booleanValue(); if (!hasDown) //如还没下载 { Vector recordVec =
         * PIMControl.ctrl.getModel().getAllRecord(currentViewInfo);
         * MailFacade.getInstance(PIMMailContext.getInstance())
         * .downLoadMessage(PIMControl.ctrl.getModel().getDefaultAccount(), "[PIM, 资讯管理, 收件箱]", recordVec, prmRecord); }
         * } }
         */

        // File[] attachFiles = null;
        Object tmpValue = prmRecord.getFieldValue(ModelDBCons.NEEDTOBESEND);

        boolean tmpIsRequest =
                (tmpValue != null) && (tmpValue instanceof Boolean) && ((Boolean) tmpValue).booleanValue();
        MainPane.getApp((String) CustOpts.custOps.APPNameVec.get(prmRecord.getAppIndex())).showDialog(
                CASControl.ctrl.getMainFrame(), new UpdateContactAction(), prmRecord, tmpIsRequest, false);

        // if (prmRecord.getAppIndex() == ModelConstants.DIARY_APP)
        // {
        // int tmpSeleID = ((PIMRecord)seleRecs.get(0)).getRecordID();
        // if (tmpSeleID != -1)
        // {
        // EDate tmpDate = PIMUtility.IDToDate(tmpSeleID);
        // EDate[] tmpDates = new EDate[] { tmpDate };
        // EDaySet tmpDayset = new EDaySet(tmpDates); //newDateSet
        // DateSeleAreaPane dateSeleAreaPane = ((PIMSplitPane)
        // ((PIMMainFrame)PIMControl.ctrl.getPIMFrame()).getSplitPane()).getDateSelectAreaPane();
        // int[] date = dateSeleAreaPane.getLastDate();
        // EDate oldDate = new EDate(date[0], date[1], date[2]);
        // EDate[] oldDates = new EDate[] { oldDate };
        // EDaySet oldDayset = new EDaySet(oldDates); //oldDateSet
        // PIMControl.ctrl.changeApplication(ModelConstants.DIARY_APP, ModelConstants.DIARY_BY_PREVIEW,
        // CustomOptions.custOps.getActiveFolderPath());
        // //转到日记视图
        // if (oldDayset != tmpDayset)
        // {
        // dateSeleAreaPane.setDaySet(tmpDayset); //设置选中日期
        // }
        // }
        // else //什么情况下传入的prmID会等于－1呢？
        // {
        // PIMControl.ctrl.changeApplication(ModelConstants.DIARY_APP, ModelConstants.DIARY_BY_PREVIEW,
        // CustomOptions.custOps.getActiveFolderPath());
        // }
        // }
        // else if (
        // prmRecord.getAppIndex() == ModelConstants.INBOX_APP
        // || prmRecord.getAppIndex() == ModelConstants.OUTBOX_APP
        // || prmRecord.getAppIndex() == ModelConstants.SENDED_APP
        // || prmRecord.getAppIndex() == ModelConstants.DRAFT_APP
        // || prmRecord.getAppIndex() == ModelConstants.DELETED_ITEM_APP)
        // {
        // int tmpIcon = ((Byte)prmRecord.getFieldValue(ModelDBConstants.ICON)).intValue();
        // if (tmpIcon == 13 && prmRecord.getAppIndex() == ModelConstants.INBOX_APP)
        // //TODO:当任务状态报告在发件箱时(此时还应判断是否在任务发送者的收件箱中)
        // {
        // MailFrame tmpFrame = MailFrame.showMailFrame(prmRecord, PIMMailContext.getInstance(), "open", null);
        // tmpFrame.getMailBarManager().resetEmail();
        // tmpFrame.setEditable(false);
        //
        // String tmpFileContent =
        // (prmRecord.getFieldValue(ModelDBConstants.ATTACH) == null)
        // ? PIMUtility.EMPTYSTR
        // : (String)prmRecord.getFieldValue(ModelDBConstants.ATTACH);
        //
        // Vector tmpFiles = PIMUtility.parseString(tmpFileContent);
        //
        // Vector tmpVec = new Vector();
        // for (int i = tmpFiles.size() - 1; i >= 0; i--)
        // {
        // String tmpName = (String)tmpFiles.get(i);
        // if (tmpName.indexOf("eiomail") != -1)
        // {
        // tmpVec.add(tmpName);
        // }
        // }
        //
        // for (int i = tmpVec.size() - 1; i >= 0; i--)
        // {
        // String tmpFilePath = (String)tmpVec.get(i);
        // try
        // {
        // FileInputStream tmpInput = new FileInputStream(tmpFilePath);
        //
        // byte[] buffer = new byte[tmpInput.available()];
        // //建立任务文件对象。
        // tmpInput.read(buffer);
        // tmpInput.close();
        // tmpFileContent = new String(buffer); //将任务文件信息读入字符串。
        // }
        // catch (IOException e)
        // {
        // PIMErrorUtility.writeErrorLog(e);
        // e.printStackTrace();
        // }
        // tmpFiles.clear();
        // tmpFiles = PIMUtility.parseString(tmpFileContent);
        // if (tmpFiles.get(tmpFiles.size() - 1).toString().trim().length() == 2
        // && tmpFiles.size() == 12) //判断，说明这个是任务状态文件
        // {
        // break;
        // }
        // }
        //
        // int tmpSize = tmpFiles.size();
        // int tmpID = Integer.valueOf(tmpFiles.get(tmpSize - 1).toString().trim()).intValue();
        // //原任务记录的ID号
        // if (tmpID == -1)
        // {
        // // javax.swing.JOptionPane.showMessageDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理。");
        // //ErrorDialog.showErrorDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理", ErrorDialog.OK_ONLY,
        // PaneConstant.TITLE);
        // ErrorDialog.showErrorDialog(PaneConstant.TITLE,ErrorDialog.WARNING_MESSAGE,MessageCons.STYLE_OK,PaneConstant.OPENAN);
        // return;
        // }
        //
        // //String tmpVersionNum = tmpFiles.get(tmpSize-2).toString().trim(); //版本号
        // String tmpKeySequence = tmpFiles.get(tmpSize - 3).toString().trim();
        // //键序号列
        //
        // int tmpApp = ModelConstants.TASK_APP;
        // int tmpIndex = tmpKeySequence.length() / 2; //序号的数量
        // if (tmpIndex != tmpFiles.size() - 3) //去掉序号列和版本号的长度2
        // {
        // // javax.swing.JOptionPane.showMessageDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理。");
        // ErrorDialog.showErrorDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理", ErrorDialog.OK_ONLY,
        // PaneConstant.TITLE);
        // return;
        // }
        //
        // /**
        // * +------------------------------------------------------
        // * |数据列的格式(以文件形式保存，跟任务，约会的发送类似）：
        // * |开始日期;截止日期;完成率;完成日期;总工作量;实际工作时间;里程;记帐信息;备注信息;
        // * +------------------------------------------------------
        // */
        // while (tmpKeySequence.length() > 0) //序号列的构造为:两位为一个序号,依次累加
        // {
        // String tmpKey = tmpKeySequence.substring(tmpKeySequence.length() - 2);
        // //序号值
        // tmpKeySequence = tmpKeySequence.substring(0, tmpKeySequence.length() - 2);
        // int tempKey = Integer.valueOf(tmpKey).intValue();
        //
        // String tmpTypeStr = (String)CustomOptions.custOps.hash2.get("Task_Types");
        // int tmpLength = PIMUtility.parserStrToVec(tmpTypeStr, CustOpts.BIAS, tmpTypeStr.length()).size();
        //
        // if (tempKey >= tmpLength)
        // {
        // tmpIndex--;
        // }
        // else
        // {
        // if (PIMUtility.constructHashtable(tmpFiles, tmpIndex, tempKey, tmpApp)) //根据信息,构建Hashtable
        // {
        // tmpIndex--;
        // }
        // else
        // {
        // // javax.swing.JOptionPane.showMessageDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理。");
        // ErrorDialog.showErrorDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理", ErrorDialog.OK_ONLY,
        // PaneConstant.TITLE);
        // return;
        // }
        // }
        // }
        //
        // PIMRecord tmpRecord =
        // PIMControl.ctrl.getModel().selectRecord(tmpApp, tmpID, PIMUtility.getTreePath(tmpApp));
        // //根据ID号,找出原任务记录
        //
        // Object tmpObj = tmpRecord.getFieldValue(ModelDBConstants.OWNER);
        // //任务的拥有者(用于完成率,工作时间等统计时的分个累加)
        // String tmpTaskOwner = (tmpObj == null) ? PIMUtility.EMPTYSTR : (String)tmpObj;
        // if (tmpTaskOwner.length() == 0)
        // {
        // // javax.swing.JOptionPane.showMessageDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理。");
        // ErrorDialog.showErrorDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理", ErrorDialog.OK_ONLY,
        // PaneConstant.TITLE);
        // return;
        // }
        //
        // tmpTaskOwner = (tmpTaskOwner.indexOf(",") == -1) ? tmpTaskOwner : tmpTaskOwner.replaceAll(",", ";");
        // tmpVec = PIMUtility.parseString(tmpTaskOwner);
        // tmpSize = tmpVec.size();
        //
        // Pool tmpPool = Pool.getInstance();
        // tmpObj = hashtable.get(tmpPool.getIntegerKey(ModelDBConstants.COMPLETED));
        // String tmpNewCompleted = (tmpObj == null) ? "0" : (String)tmpObj;
        //
        // tmpObj = prmRecord.getFieldValue(ModelDBConstants.ADDRESSER);
        // String tmpSender = (tmpObj == null) ? PIMUtility.EMPTYSTR : (String)tmpObj;
        // if (tmpSender == null || tmpSender.length() == 0)
        // {
        // javax.swing.JOptionPane.showMessageDialog(PIMControl.ctrl.getMainFrame(), "报告已损坏，无法处理。");
        // return;
        // }
        // String tmpAddress = tmpSender.substring(tmpSender.indexOf("<") + 1, tmpSender.indexOf(">"));
        //
        // tmpObj = tmpRecord.getFieldValue(ModelDBConstants.COMPLETED);
        // String tmpCompleted = (tmpObj == null) ? PIMUtility.EMPTYSTR : (String)tmpObj;
        //
        // int index = -1;
        // for (int i = 0; i < tmpSize; i++) //确定自已在所有任务拥有者中的位置(序号)
        // {
        // String tmpOwner = (String)tmpVec.get(i);
        // tmpOwner = tmpOwner.substring(tmpOwner.indexOf("<") + 1, tmpOwner.indexOf(">"));
        // if (tmpAddress.equalsIgnoreCase(tmpOwner))
        // {
        // index = i;
        // break;
        // }
        // }
        //
        // String tmpResult = PIMUtility.EMPTYSTR;
        // if (tmpCompleted.indexOf(";") == -1)
        // //替换原有数据(如有几个拥有者,则表示为格式:"...;...;")---完成率累加时的算法,同工作时间的算法
        // {
        // for (int i = 0; i < tmpSize; i++)
        // {
        // tmpResult = (i == index) ? tmpResult + tmpNewCompleted + ';' : tmpResult.concat("0;");
        // }
        // }
        // else
        // {
        // tmpVec = PIMUtility.parseString(tmpCompleted);
        // for (int i = 0; i < tmpVec.size(); i++)
        // {
        // tmpResult = (i == index) ? tmpResult + tmpNewCompleted + ';' : tmpResult.concat((String)tmpVec.get(i));
        // }
        // }
        // tmpRecord.setFieldValue(ModelDBConstants.COMPLETED, tmpResult);
        //
        // tmpVec = PIMUtility.parseString(tmpResult);
        // tmpSize = tmpVec.size();
        // int tmpCompletedValue = 0;
        // for (int i = 0; i < tmpSize; i++)
        // {
        // String tmpStr = (String)tmpVec.get(i);
        // tmpCompletedValue = tmpCompletedValue + (Integer.valueOf(tmpStr).intValue()) / tmpSize;
        // }
        //
        // if (tmpCompletedValue == 100) //如果完成率为100%,则改其状态为"已完成"
        // {
        // tmpRecord.setFieldValue(ModelDBConstants.STATUS, tmpPool.getIntegerKey(2));
        // }
        //
        // if (tmpCompletedValue > 0) //如果介于0~100之间,则其状态为"进行中"
        // {
        // tmpRecord.setFieldValue(ModelDBConstants.STATUS, tmpPool.getIntegerKey(1));
        // }
        //
        // tmpObj = tmpRecord.getFieldValue(ModelDBConstants.TASK_GROSS);
        // String tmpTaskGross = (String)tmpObj;
        // tmpObj = hashtable.get(tmpPool.getIntegerKey(ModelDBConstants.TASK_GROSS));
        // if (tmpTaskGross.indexOf(";") == -1) //拥有任务者只有一个时，直接替换其值。
        // {
        // tmpTaskGross = PIMUtility.EMPTYSTR;
        // for (int i = 0; i < tmpSize; i++)
        // {
        // tmpTaskGross =
        // (i == index)
        // ? tmpTaskGross + (String)tmpObj + ';'
        // : tmpTaskGross + "0 " + TaskDialogConstant.HOUR + ';';
        // }
        // }
        // else //当此任务有几个拥有者时，则分别替换其相对应的部分。（格式为："...;...;")
        // {
        // tmpVec = PIMUtility.parseString(tmpTaskGross);
        // tmpTaskGross = PIMUtility.EMPTYSTR;
        // for (int i = 0; i < tmpVec.size(); i++)
        // {
        // tmpTaskGross =
        // (i == index) ? tmpTaskGross + (String)tmpObj + ';' : tmpTaskGross.concat((String)tmpVec.get(i));
        // }
        // }
        // tmpRecord.setFieldValue(ModelDBConstants.TASK_GROSS, tmpTaskGross);
        //
        // tmpObj = tmpRecord.getFieldValue(ModelDBConstants.REALLY_TASK);
        // String tmpTaskReally = (String)tmpObj;
        // tmpObj = hashtable.get(tmpPool.getIntegerKey(ModelDBConstants.REALLY_TASK));
        // if (tmpTaskReally.indexOf(";") == -1) //拥有任务者只有一个时，直接替换其值。
        // {
        // tmpTaskReally = PIMUtility.EMPTYSTR;
        // for (int i = 0; i < tmpSize; i++)
        // {
        // tmpTaskReally =
        // (i == index)
        // ? tmpTaskReally + (String)tmpObj + ';'
        // : tmpTaskReally + "0 " + TaskDialogConstant.HOUR + ';';
        // }
        // }
        // else //当此任务有几个拥有者时，则分别替换其相对应的部分。（格式为："...;...;")
        // {
        // tmpVec = PIMUtility.parseString(tmpTaskReally);
        // tmpTaskReally = PIMUtility.EMPTYSTR;
        // for (int i = 0; i < tmpVec.size(); i++)
        // {
        // tmpTaskReally =
        // (i == index) ? tmpTaskReally + (String)tmpObj + ';' : tmpTaskReally.concat((String)tmpVec.get(i));
        // }
        // }
        // tmpRecord.setFieldValue(ModelDBConstants.TASK_GROSS, tmpTaskReally);
        //
        // tmpObj = hashtable.get(tmpPool.getIntegerKey(ModelDBConstants.MILESTONE));
        // //TODO:里程(此处需用上面的累加算法)
        // if (tmpObj != null && tmpObj.toString().trim().length() > 0)
        // {
        // int tmpNewMilestone = Integer.valueOf(((String)tmpObj).trim()).intValue();
        // tmpObj = tmpRecord.getFieldValue(ModelDBConstants.MILESTONE);
        // int tmpMilestone =
        // (tmpObj == null || tmpObj.toString().trim().length() == 0)
        // ? tmpNewMilestone
        // : Integer.valueOf(((String)tmpObj).trim()).intValue() + tmpNewMilestone;
        // tmpRecord.setFieldValue(ModelDBConstants.MILESTONE, String.valueOf(tmpMilestone));
        // }
        //
        // tmpObj = hashtable.get(tmpPool.getIntegerKey(ModelDBConstants.TALLY_INFO));
        // //TODO:记账信息(同上)
        // if (tmpObj != null && tmpObj.toString().trim().length() > 0)
        // {
        // int tmpNewTally = Integer.valueOf(((String)tmpObj).trim()).intValue();
        // tmpObj = tmpRecord.getFieldValue(ModelDBConstants.TALLY_INFO);
        // int tmpTally =
        // (tmpObj == null || tmpObj.toString().trim().length() == 0)
        // ? tmpNewTally
        // : Integer.valueOf(((String)tmpObj).trim()).intValue() + tmpNewTally;
        // tmpRecord.setFieldValue(ModelDBConstants.TALLY_INFO, String.valueOf(tmpTally));
        // }
        //
        // PIMControl.ctrl.getModel().updateRecord(tmpRecord, true);
        // //更新原任务记录,使上面的改动生效
        // PIMControl.ctrl.getModel().permanentlyDeleteRecord(prmRecord, true, true);
        // //彻底删除任务状态报告
        // }
        // else if (prmRecord.getAppIndex() == ModelConstants.DRAFT_APP)
        // {
        // String tmpFileNames = (String)prmRecord.getFieldValue(ModelDBConstants.ATTACH);
        // String tmpActualFileNames = (String)prmRecord.getFieldValue(ModelDBConstants.ATTACHMENT);
        // Vector tmpFileVec = PIMUtility.parseString(tmpFileNames);
        // Vector tmpActualVec = PIMUtility.parseString(tmpActualFileNames);
        // int size = tmpActualVec.size();
        // attachFiles = new File[size];
        // String tmpFilePath;
        // for (int i = size - 1; i >= 0; i--)
        // {
        // tmpFilePath = (String)tmpFileVec.get(i);
        // attachFiles[i] = new File(tmpFilePath);
        // }
        // Object obj = prmRecord.getFieldValue(ModelDBConstants.ISEDIT);
        // boolean isEdit = obj == null ? false : ((Boolean)obj).booleanValue();
        // MailFrame tmpFrame;
        // if (isEdit)
        // {
        // tmpFrame = MailFrame.showMailFrame(prmRecord, PIMMailContext.getInstance(), MailUtility.OPEN_NEW, null);
        // }
        // else
        // {
        // tmpFrame = MailFrame.showMailFrame(prmRecord, PIMMailContext.getInstance(), MailUtility.OPEN, null);
        // tmpFrame.getMailBarManager().resetEmail();
        // }
        //
        // if (!tmpFrame.isDoubleOpen())
        // {
        // if (attachFiles != null && attachFiles.length > 0)
        // {
        // tmpFrame.addAttach(attachFiles, tmpActualVec);
        // }
        // }
        // }
        // else
        // {
        // String tmpFileNames = (String)prmRecord.getFieldValue(ModelDBConstants.ATTACH);
        // String tmpActualFileNames = (String)prmRecord.getFieldValue(ModelDBConstants.ATTACHMENT);
        // Vector tmpFileVec = PIMUtility.parseString(tmpFileNames);
        // Vector tmpActualVec = PIMUtility.parseString(tmpActualFileNames);
        // int size = tmpActualVec.size();
        // attachFiles = new File[size];
        // String tmpFilePath;
        // for (int i = size - 1; i >= 0; i--)
        // {
        // tmpFilePath = (String)tmpFileVec.get(i);
        // attachFiles[i] = new File(tmpFilePath);
        // }
        // MailFrame tmpFrame = MailFrame.showMailFrame(prmRecord, PIMMailContext.getInstance(), "open_2", null);
        // tmpFrame.getMailBarManager().resetEmail();
        // if (prmRecord.getAppIndex() == ModelConstants.OUTBOX_APP)
        // {
        // tmpFrame.getMailBarManager().setMailOperatorStatus();
        // }
        //
        // if(!tmpFrame.isDoubleOpen())
        // {
        // if (attachFiles != null && attachFiles.length > 0)
        // {
        // tmpFrame.addAttach(attachFiles, tmpActualVec);
        // }
        // }
        // }
        // //TODO:OutBox中的邮件需要特殊对待,即把Record从数据库中的记录.
        // }
    }

    // =============================================================================================
    /*
     * 专门用于连续打开多条记录的类。因为EDialog对话框有一个180毫秒的限制，所以专门写此类用于在每个打开动作（openSingleRecord）
     * 之间插入200（CustomOptionsConstants.DIALOG_DELAY_TIME）毫秒的间隔。
     */
    private class OpenThread extends Timer implements ActionListener {
        OpenThread() {
            super(CustOpts.DIALOG_DELAY_TIME, null);
            addActionListener(this);
            i = 0;
        }

        public void actionPerformed(
                ActionEvent e) {
            if (i < seleRecs.size()) {
                openSingleRecord((PIMRecord) seleRecs.get(i));
                i++;
            } else {
                stop();
                seleRecs = null;
                removeActionListener(this);
            }
        }

        int i = 0;
    }

    Vector seleRecs = null; // 用于存放选中的记录。
}
