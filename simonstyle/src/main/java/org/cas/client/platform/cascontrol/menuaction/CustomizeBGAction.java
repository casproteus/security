package org.cas.client.platform.cascontrol.menuaction;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.dialog.CASFileFilter;
import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.cascustomize.CustOptsConsts;

public class CustomizeBGAction extends SAction {
    public void actionPerformed(
            ActionEvent e) {
        JFileChooser tmpFileChooser = new JFileChooser();
        tmpFileChooser.setAcceptAllFileFilterUsed(true);
        tmpFileChooser.addChoosableFileFilter(new CASFileFilter("png"));
        tmpFileChooser.addChoosableFileFilter(new CASFileFilter("gif"));
        tmpFileChooser.addChoosableFileFilter(new CASFileFilter("jpg"));
        if (tmpFileChooser.showOpenDialog(CASControl.ctrl.getMainFrame()) != JFileChooser.APPROVE_OPTION)
            return;

        File file = tmpFileChooser.getSelectedFile();
        Image tImg = null;
        try {
            tImg = Toolkit.getDefaultToolkit().getImage(file.getPath());
            CASControl.ctrl.getMainPane().preparePicture(tImg);
            CASControl.ctrl.getMainPane().repaint();
            CustOpts.custOps.setKeyAndValue(CustOptsConsts.MainBGImgPath, file.getPath());
        } catch (Exception exp) {
            JOptionPane.showMessageDialog(CASMainFrame.mainFrame,
                    "选中的文件无效,可能是因为内容不完全符合格式或发生过不明目的的改变。\n为安全起见，请重新选择其他图片。");
        }
    }
}
