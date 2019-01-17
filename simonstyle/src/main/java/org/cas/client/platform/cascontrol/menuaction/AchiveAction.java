package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.swing.JFileChooser;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.FileSystemUtil;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.PaneConsts;

public class AchiveAction extends SAction {
    /**
     * Invoked when an action occurs.
     */
    @Override
	public void actionPerformed(
            ActionEvent e) {
        String mailPath = CASUtility.getPIMDirPath();
        try {
            JFileChooser tmpFileChooser = new JFileChooser();
            tmpFileChooser.setDialogType(1);
            tmpFileChooser.setAcceptAllFileFilterUsed(false);
            tmpFileChooser.addChoosableFileFilter(new CASFileFilter("zip"));
            tmpFileChooser.setCurrentDirectory(new File(mailPath));
            if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
                return;
            File f = tmpFileChooser.getSelectedFile();
            if (f == null)
                return;
            String filename = f.getName();
            if (!filename.endsWith(".zip"))
                filename = filename.concat(".zip");
            String savePath =
                    tmpFileChooser.getCurrentDirectory().toString().concat(System.getProperty("file.separator"))
                            .concat(filename);
            // /*****/A.s("savePath "+savePath);
            saveData(savePath);
        } catch (java.lang.Exception ex) {
            // javax.swing.JOptionPane.showMessageDialog(PIMControl.ctrl.getMainFrame(),
            // "保存对话盒内部出错,请及时联系SS组有关负责人");
            // ErrorDialog.showErrorDialog(PIMControl.ctrl.getMainFrame(), "保存对话盒内部出错,请及时联系SS组有关负责人",
            // ErrorDialog.OK_ONLY, PaneConstant.TITLE);
            SOptionPane.showErrorDialog(PaneConsts.TITLE, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                    PaneConsts.ACHIVEAN);
        }
    }

    /**
     * 根据文本路径写文件
     * 
     * @param savePath
     *            :保存文件得路径
     */
    private void saveData(
            String savePath) {
        CASControl.ctrl.closePIMAPP();// 关闭数据库链接先。

        String path = CASUtility.getPIMDirPath();
        int length = path.length();
        File read = new File(path);
        File[] files = read.listFiles();
        ArrayList<String> v = new ArrayList<String>();
        FileSystemUtil.getAllFilesIntoArray(files, v, length);

        try {
            String write = savePath;// "C:\\Documents and Settings\\Administrator\\桌面\\文件与磁盘操作.zip";
            FileInputStream in = null;
            FileOutputStream out = null;
            ZipOutputStream zipOut = null;
            // 创建文件输出流对象
            out = new FileOutputStream(write);
            // 创建JAR数据输出流对象
            zipOut = new ZipOutputStream(out);
            for (int i = 0; i < v.size(); i++) {
                File file = new File(path + System.getProperty("file.separator") + v.get(i));
                // 创建文件输入流对象
                // 创建指向压缩原始文件的入口
                in = new FileInputStream(file);
                ZipEntry entry = new ZipEntry(v.get(i));
                zipOut.putNextEntry(entry);
                // 向压缩文件中输出数据
                int nNumber;
                byte[] buffer = new byte[512];
                while ((nNumber = in.read(buffer)) != -1)
                    zipOut.write(buffer, 0, nNumber);
            }

            // 关闭创建的流对象
            zipOut.close();
            out.close();
            in.close();
        } catch (IOException e) {
            ErrorUtil.write(e);
        }

        // 重新开启数据库链接。
        CASControl.ctrl.initModel();
    }
}

/**
 * 根据文本路径写文件
 * 
 * @param savePath
 *            :保存文件得路径
 *
 *            public void saveData(String savePath) { try { File file = new File(savePath); FileOutputStream fileOutput
 *            = new FileOutputStream(file); ZipOutputStream zipOutput = new ZipOutputStream(fileOutput); String path =
 *            PIMUtility.getPIMDirPath(); File parentFile = new File(path); File [] files = parentFile.listFiles();
 *            //压缩文件条目 entryFiles(zipOutput,files); zipOutput.closeEntry(); zipOutput.finish(); zipOutput.close();
 *            fileOutput.close(); } catch (IOException e) { //ErrorUtil.writeErrorLog("0005",new
 *            Exception("Error Ocurred in saveDate Method in DefaultConfigInfoWriter"));
 * 
 *            } }
 * 
 * 
 *            private void entryFiles(ZipOutputStream zipOutput,File [] files) { try { for (int i = 0;
 *            i<files.length;i++) { if (!files[i].isDirectory()) { ZipEntry entry = new ZipEntry(files[i].getName());
 *            zipOutput.putNextEntry(entry); } else { //prmPath = prmPath + System.getProperty("file.separator") +
 *            files[i].getName(); entryFiles(zipOutput,files[i].listFiles()); } } } catch (IOException ex) {
 *            ErrorUtil.writeErrorLog("0006",new Exception("Execption in write zip files")); } }
 */
