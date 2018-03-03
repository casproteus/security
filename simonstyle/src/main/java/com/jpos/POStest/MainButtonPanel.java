//------------------------------------------------------------------------------
//
// This software is provided "AS IS".  360Commerce MAKES NO
// REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE SOFTWARE,
// EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
// NON-INFRINGEMENT. 360Commerce shall not be liable for
// any damages suffered as a result of using, modifying or distributing this
// software or its derivatives. Permission to use, copy, modify, and distribute
// the software and its documentation for any purpose is hereby granted.
//
// MainButtonPanel.java - A common panel used by the other panels to hold buttons.
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import jpos.*;

public class MainButtonPanel extends JPanel {

    private JComboBox logicalNameComboBox;
    private JTextField logicalNameTextField;
    private JLabel currentStatus;
    private JButton openButton;
    private JButton claimButton;
    private JButton releaseButton;
    private JButton closeButton;

    public MainButtonPanel(ActionListener actionListener, String defaultSelection)
    {
        JLabel logicalNameLabel = new JLabel("Logical name: ");
        add(logicalNameLabel);

        logicalNameTextField = new JTextField(15);
        logicalNameTextField.setText(defaultSelection);
        add(logicalNameTextField);

        currentStatus = new JLabel("unknown");
        add(currentStatus);

        openButton = new JButton("Open");
        openButton.setActionCommand("open");
        openButton.addActionListener(actionListener);
        add(openButton);

        claimButton = new JButton("Claim");
        claimButton.setActionCommand("claim");
        claimButton.addActionListener(actionListener);
        add(claimButton);

        releaseButton = new JButton("Release");
        releaseButton.setActionCommand("release");
        releaseButton.addActionListener(actionListener);
        add(releaseButton);

        closeButton = new JButton("Close");
        closeButton.setActionCommand("close");
        closeButton.addActionListener(actionListener);
        add(closeButton);

        setMaximumSize(new Dimension(Short.MAX_VALUE, 30)); 


    }

    public void action(ActionEvent ae) {
        if(ae.getActionCommand().equals("open")){
            currentStatus.setText("Open");
            logicalNameTextField.setEnabled(false);
        }
        if(ae.getActionCommand().equals("claim")){
            currentStatus.setText("Claim");
        }
        if(ae.getActionCommand().equals("release")){
            currentStatus.setText("Release");
        }
        if(ae.getActionCommand().equals("close")){
            currentStatus.setText("Close");
            logicalNameTextField.setEnabled(true);
        }
    }

    public String getLogicalName()
    {
        String logicalName = (String)logicalNameTextField.getText();
        return logicalName;
    }

}

 
