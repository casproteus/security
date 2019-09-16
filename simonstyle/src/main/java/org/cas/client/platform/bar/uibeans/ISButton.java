package org.cas.client.platform.bar.uibeans;

import java.awt.event.ActionListener;

import javax.swing.JComponent;

public interface ISButton {
   JComponent clone();
   void addActionListener(ActionListener actionListener);
   
   //for satisfy JToogleButton interfaces
   void setSelected(boolean b);
   boolean isSelected();
   
   //for satisfy JButton
   String getText();
   void setText(String text);
}
