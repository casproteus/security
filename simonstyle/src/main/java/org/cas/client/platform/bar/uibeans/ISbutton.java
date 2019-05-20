package org.cas.client.platform.bar.uibeans;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

public interface ISbutton {
   JComponent clone();
   void addActionListener(ActionListener actionListener);
}
