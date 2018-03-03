package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import org.cas.client.platform.casbeans.calendar.DateSeleAreaPane;
import org.cas.client.platform.casutil.SOptionPane;
import org.cas.client.resource.international.PaneConsts;

public class GotoTodayAction extends SAction {
    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        try {
            DateSeleAreaPane.getInstance().setToday();
        } catch (Exception e) {
            // javax.swing.JOptionPane.showMessageDialog(null,
            // "Have a exception in class of PaneManager,I will need some time to deal with it !");
            // ErrorDialog.showErrorDialog(PIMControl.ctrl.getMainFrame(),
            // "Have a exception in class of PaneManager,I will need some time to deal with it !", ErrorDialog.OK_ONLY,
            // PaneConstant.TITLE);
            SOptionPane.showErrorDialog(PaneConsts.TITLE, SOptionPane.WARNING_MESSAGE, SOptionPane.STYLE_OK,
                    PaneConsts.GOTOTODAYAN);
        }
    }
}
