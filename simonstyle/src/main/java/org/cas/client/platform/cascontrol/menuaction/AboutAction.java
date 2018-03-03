package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.cascontrol.frame.CASMainFrame;
import org.cas.client.platform.magicbath.dialog.AboutDlg;

public class AboutAction extends SAction {
    public void actionPerformed(
            ActionEvent e) {
        AboutDlg.getInstance(CASMainFrame.mainFrame).setVisible(true);
    }
}
