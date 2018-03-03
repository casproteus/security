package org.cas.client.platform.cascontrol.menuaction;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/***/
public class ViewGotoAction extends AbstractAction {
    /**
     * Creates new ViewGotoAction
     * 
     * @param 视图名
     */
    public ViewGotoAction(String desName) {
        this.desName = desName;
    }

    /* action is performed */
    public void actionPerformed(
            ActionEvent evt) {
        // MainPane.getInstance().getBaseBookPane().setPane(0, 0);
    }

    private String desName;
}
