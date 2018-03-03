package org.cas.client.platform;

import javax.swing.JPanel;

import org.cas.client.platform.cascontrol.navigation.CASNavigationPane;

public interface ICASFrame {
    void updateActionStatus();

    CASNavigationPane getFolderPane();

    JPanel[] getToolBarPanes();

    public void reLayout();
}
