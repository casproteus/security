package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JFileChooser;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.PaneConsts;

public class SaveAttachAllAction extends SAction {

    /**
     * Creates a new instance of SaveAttachAll 保存所有附件构建器
     * 
     * @param action
     *            标记
     */
    public SaveAttachAllAction(int flag) {
        super(flag);
    }

    /**
     * Creates a new instance of SaveAttachAll 保存所有附件构建器
     * 
     * @param prmRecord
     *            : 带有附件的记录
     */
    public SaveAttachAllAction(PIMRecord prmRecord) {
        record = prmRecord;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        if (record == null) {
            Vector vector = CASControl.ctrl.getSelectRecords(); // getPIMRecord();
            // 只处理选中一条记录的情况
            if (vector != null && vector.size() == 1) {
                record = (PIMRecord) vector.get(0);
            }
        }
        if (record == null) {
            return;
        }

        // 取得记录中所有附件的原始路径
        String[] attachPaths = CASUtility.getAttachMentName(record, true);
        if (attachPaths == null) {
            return;
        }
        String[] actualAttachName = CASUtility.getAttachMentName(record, false);
        if (actualAttachName == null || attachPaths.length != actualAttachName.length) {
            return;
        }

        // 取得PIM系统自行保存附件的目录,设置到保存对话盒中
        String filename = CASUtility.EMPTYSTR;
        try {
            JFileChooser tmpFileChooser = new JFileChooser();
            tmpFileChooser.setDialogType(1);
            tmpFileChooser.setAcceptAllFileFilterUsed(true);
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("pet", PaneConsts.TEMPLATE_FILE));
            tmpFileChooser.setCurrentDirectory(new File(CASUtility.getPIMMailDirPath()));
            if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
                return;
            File f = tmpFileChooser.getSelectedFile();
            if (f == null)
                return;
            filename = f.getPath();// 取得重命名后的文件名
        } catch (Exception ex) {
            // javax.swing.JOptionPane.showMessageDialog(null,"保存对话盒内部出错!");
            SOptionPane.showErrorDialog(CASControl.ctrl.getMainFrame(), "保存对话盒内部出错", SOptionPane.OK_ONLY,
                    PaneConsts.TITLE);
            return;
        }
        FileOutputStream os = null;
        FileInputStream is = null;
        BufferedInputStream bi = null;
        BufferedOutputStream bo = null;
        // 开始保存文件到硬盘
        try {
            for (int i = 0; i < attachPaths.length; i++) {
                File file1 = new File(attachPaths[i]);
                File file2 = new File(filename + File.separator + actualAttachName[i]);
                is = new FileInputStream(file1);
                bi = new BufferedInputStream(is);
                os = new FileOutputStream(file2);
                bo = new BufferedOutputStream(os);
                // 文件长度不会超过磁盘扇区大小
                byte[] buf = new byte[512];
                int size;
                while ((size = bi.read(buf)) != -1) {
                    bo.write(buf, 0, size);
                }
            }
            if (os != null) {
                os.close();
            }
            if (bo != null) {
                bo.close();
            }
            if (is != null) {
                is.close();
            }
            if (bi != null) {
                bi.close();
            }
        } catch (IOException ex) {
            ErrorUtil.write("00014", new Exception("Error Ocurred in save one of attach !"));
            // ex.printStackTrace();
        }
    }

    private PIMRecord record;
}
