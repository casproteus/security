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
// CashDrawerPanel.java - The cash drawer panel of POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;


public class CashDrawerPanel extends Component implements StatusUpdateListener, ActionListener {

    protected MainButtonPanel mainButtonPanel;
    
    private CashDrawer cashDrawer;

    private String defaultLogicalName = "defaultCashDrawer";


    private JButton openCashDrawerButton;
    private JButton getDrawerOpenedButton;
    private JButton waitForDrawerCloseButton;

    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private JTextArea statusTextArea;

    public CashDrawerPanel() 
    {
        cashDrawer = null;
    }

    public Component make() {
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

//        MethodListener methodListener = new MethodListener();

        mainButtonPanel = new MainButtonPanel(this,defaultLogicalName);
        mainPanel.add(mainButtonPanel);

        JPanel buttonPanel = new JPanel();
        openCashDrawerButton = new JButton("Open Cash Drawer");
        openCashDrawerButton.setActionCommand("openCashDrawer");
        openCashDrawerButton.addActionListener(this);
        openCashDrawerButton.setEnabled(false);
        buttonPanel.add(openCashDrawerButton);

        getDrawerOpenedButton = new JButton("Get Drawer Opened");
        getDrawerOpenedButton.setActionCommand("getDrawerOpened");
        getDrawerOpenedButton.addActionListener(this);
        getDrawerOpenedButton.setEnabled(false);
        buttonPanel.add(getDrawerOpenedButton);

        waitForDrawerCloseButton = new JButton("Wait For Drawer Close");
        waitForDrawerCloseButton.setActionCommand("waitForDrawerClose");
        waitForDrawerCloseButton.addActionListener(this);
        waitForDrawerCloseButton.setEnabled(false);
        buttonPanel.add(waitForDrawerCloseButton);

        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30)); 

        mainPanel.add(buttonPanel);


        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));

        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));


        deviceEnabledCB = new JCheckBox("Device enabled");
        propPanel.add(deviceEnabledCB);
        freezeEventsCB = new JCheckBox("Freeze events");
        propPanel.add(freezeEventsCB);
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
       
        deviceEnabledCB.setEnabled(false);
        freezeEventsCB.setEnabled(false);

        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);



        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Action log: ");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusPanel.add(label);

        statusTextArea = new JTextArea(10,10);
        JScrollPane scrollPane = new JScrollPane(statusTextArea);
        statusPanel.add(scrollPane);


        subPanel.add(statusPanel);

        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }

    public void statusUpdateOccurred(StatusUpdateEvent sue)
    {
        System.out.println("Cash drawer received status update event.");
    }


   /** Listens to the method buttons. */

        public void actionPerformed(ActionEvent ae) {
             mainButtonPanel.action(ae);
             if(ae.getActionCommand().equals("open")){
                 try{
                     String logicalName = mainButtonPanel.getLogicalName();
                     if(logicalName.equals("")){
                         logicalName = defaultLogicalName;
                     }
                     if(cashDrawer == null)
                     {
                         cashDrawer = new CashDrawer();
                         cashDrawer.addStatusUpdateListener(this);
                     }

                     cashDrawer.open(logicalName);
                     deviceEnabledCB.setEnabled(false);
                     freezeEventsCB.setEnabled(true);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("claim")){
                 try{
                     cashDrawer.claim(0);
                     openCashDrawerButton.setEnabled(true);
                     getDrawerOpenedButton.setEnabled(true);
                     waitForDrawerCloseButton.setEnabled(true);

                     deviceEnabledCB.setEnabled(true);
                     freezeEventsCB.setEnabled(true);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("release")){
                 try{
                     cashDrawer.release();
                     openCashDrawerButton.setEnabled(false);
                     getDrawerOpenedButton.setEnabled(false);
                     waitForDrawerCloseButton.setEnabled(false);

                     deviceEnabledCB.setEnabled(false);
                     freezeEventsCB.setEnabled(true);

                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("close")){
                 try{
                     cashDrawer.close();
                     deviceEnabledCB.setEnabled(false);
                     freezeEventsCB.setEnabled(false);

                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }


             if(ae.getActionCommand().equals("openCashDrawer")){
                 statusTextArea.append("Open cash drawer.\n");
                 try{
                     cashDrawer.openDrawer();
                 }catch(JposException e){
                     statusTextArea.append("Jpos exception " + e + "\n");
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("getDrawerOpened")){
                 try{
                     if(cashDrawer.getDrawerOpened())
                     {
                         statusTextArea.append("Cash drawer is open.\n");
                     }
                     else
                     {
                         statusTextArea.append("Cash drawer is closed.\n");
                     }
                 }catch(JposException e){
                     statusTextArea.append("Jpos exception " + e + "\n");
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("waitForDrawerClose")){
                 try{
//                     statusTextArea.append("Wait for drawer close...\n");
                     cashDrawer.waitForDrawerClose(0,0,0,0);
                     statusTextArea.append("Cash drawer closed.\n");
                 }catch(JposException e){
                     statusTextArea.append("Jpos exception " + e + "\n");
                     System.err.println("Jpos exception " + e);
                 }
             }

            try
            {
                deviceEnabledCB.setSelected(cashDrawer.getDeviceEnabled());
                freezeEventsCB.setSelected(cashDrawer.getFreezeEvents());
            }
            catch(JposException je)
            {
                System.err.println("POSPrinterPanel: MethodListener: JposException");
            }
        }


    class CheckBoxListener implements ItemListener 
    {
        public void itemStateChanged(ItemEvent e) 
        {
            Object source = e.getItemSelectable();
            try
            {
                if (source == deviceEnabledCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        cashDrawer.setDeviceEnabled(false);
                    }else{
                        cashDrawer.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        cashDrawer.setFreezeEvents(false);
                    }else{
                        cashDrawer.setFreezeEvents(true);
                    }
                }
            }
            catch(JposException je)
            {
                System.err.println("CashDrawerPanel: CheckBoxListener: Jpos Exception" + e);
            }
         }
     }    
    



}
