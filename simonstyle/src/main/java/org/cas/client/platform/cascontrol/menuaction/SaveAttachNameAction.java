package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.resource.international.PaneConsts;

public class SaveAttachNameAction extends SAction {
    /**
     * Creates a new instance of SaveAttachNameActino 保存附件
     * 
     * @param flag
     *            : 设置内部标志
     */
    public SaveAttachNameAction(int flag) {
        super(flag);
    }

    /**
     * Creates a new instance of SaveAttachNameActino
     * 
     * @param 保存对应路径的文件
     */
    public SaveAttachNameAction(String prmAttachPath) {
        attachPath = prmAttachPath;
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        if (attachPath == null || attachPath.length() == 0) {
            return;
        }
        File file1 = new File(attachPath);
        // 取得PIM系统默认路径
        String mailPath = CASUtility.getPIMMailDirPath();
        JFileChooser tmpFileChooser = new JFileChooser();
        tmpFileChooser.setDialogType(1);
        tmpFileChooser.setAcceptAllFileFilterUsed(true);
        tmpFileChooser.addChoosableFileFilter(new CASFileFilter("pet", PaneConsts.TEMPLATE_FILE));
        tmpFileChooser.setCurrentDirectory(new File(mailPath));
        if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
            return;

        File file = tmpFileChooser.getSelectedFile();// 取得保存文件名
        if (file == null)
            return;
        String savePath = file.getName();
        try {
            File file2 = new File(savePath + System.getProperty("file.separator") + savePath);// 开始写入磁盘
            FileInputStream is = new FileInputStream(file1);
            FileOutputStream os = new FileOutputStream(file2);
            BufferedInputStream bi = new BufferedInputStream(is);
            BufferedOutputStream bo = new BufferedOutputStream(os);

            // 文件长度不会超过磁盘扇区大小
            long length = file1.length();
            byte[] buf = new byte[(int) length];
            int size = bi.read(buf);
            while (size != -1) {
                bo.write(buf, 0, size);
                size = bi.read(buf);
            }
            os.close();
            bo.close();
            bi.close();
            bo.close();
        } catch (IOException ex) {
            ErrorUtil.write("00013", new Exception("Error Ocurred in save only one attach !"));
            // ex.printStackTrace();
        }
    }

    private String attachPath;

}
