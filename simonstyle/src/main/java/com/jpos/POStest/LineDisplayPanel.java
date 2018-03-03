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
// LineDisplayPanel.java - The Line Display panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;

public class LineDisplayPanel extends Component 
{

    protected MainButtonPanel mainButtonPanel;

    private LineDisplay lineDisplay;

    private String defaultLogicalName = "defaultLineDisplay";

    private JTextField row;
    private JTextField column;
    private JTextField lineDisplayData;
    private JTextField attribute;


    private JButton displayTextAtButton;
    private JButton displayTextButton;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JLabel label;

    public LineDisplayPanel() 
    {
        lineDisplay = null;
    }

    public Component make() {
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        MethodListener methodListener = new MethodListener();

        mainButtonPanel = new MainButtonPanel(methodListener,defaultLogicalName);
        mainPanel.add(mainButtonPanel);

        JPanel buttonPanel = new JPanel();
        displayTextAtButton = new JButton("Dispay Text At");
        displayTextAtButton.setActionCommand("displayTextAt");
        displayTextAtButton.addActionListener(methodListener);
        displayTextAtButton.setEnabled(false);
        buttonPanel.add(displayTextAtButton);

        displayTextButton = new JButton("Dispay Text");
        displayTextButton.setActionCommand("displayText");
        displayTextButton.addActionListener(methodListener);
        displayTextButton.setEnabled(false);
        buttonPanel.add(displayTextButton);

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


        JPanel lineDisplayOutputPanel = new JPanel();
        lineDisplayOutputPanel.setLayout(new BoxLayout(lineDisplayOutputPanel, BoxLayout.Y_AXIS));

        label = new JLabel("Row: ");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        lineDisplayOutputPanel.add(label);
        row = new JTextField(30);
        lineDisplayOutputPanel.add(row);

        label = new JLabel("Column: ");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        lineDisplayOutputPanel.add(label);
        column = new JTextField(30);
        lineDisplayOutputPanel.add(column);

        label = new JLabel("Send to line display: ");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        lineDisplayOutputPanel.add(label);
        lineDisplayData = new JTextField(30);
        lineDisplayOutputPanel.add(lineDisplayData);

        label = new JLabel("Attribute: ");
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        lineDisplayOutputPanel.add(label);
        attribute = new JTextField(30);
        lineDisplayOutputPanel.add(attribute);


        subPanel.add(lineDisplayOutputPanel);

        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());

        return mainPanel;
    }


   /** Listens to the method buttons. */

    class MethodListener implements ActionListener { 
        public void actionPerformed(ActionEvent ae) {
             mainButtonPanel.action(ae);
             if(ae.getActionCommand().equals("open")){
                 try{
                     String logicalName = mainButtonPanel.getLogicalName();
                     if(logicalName.equals("")){
                         logicalName = defaultLogicalName;
                     }
                     if(lineDisplay == null)
                     {
                         lineDisplay = new LineDisplay();
                     }
                     lineDisplay.open(logicalName);
                     deviceEnabledCB.setEnabled(false);
                     freezeEventsCB.setEnabled(true);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("claim")){
                 try{
                     lineDisplay.claim(0);
                     displayTextAtButton.setEnabled(true);
                     displayTextButton.setEnabled(true);
                     deviceEnabledCB.setEnabled(true);
                     freezeEventsCB.setEnabled(true);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("release")){
                 try{
                     lineDisplay.release();
                     displayTextAtButton.setEnabled(false);
                     displayTextButton.setEnabled(false);
                     deviceEnabledCB.setEnabled(false);
                     freezeEventsCB.setEnabled(true);

                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("close")){
                 try{
                     lineDisplay.close();
                     deviceEnabledCB.setEnabled(false);
                     freezeEventsCB.setEnabled(false);

                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }


             if(ae.getActionCommand().equals("displayTextAt")){
                 try{
                     lineDisplay.displayTextAt(Integer.parseInt(row.getText()),Integer.parseInt(column.getText()),lineDisplayData.getText(),Integer.parseInt(attribute.getText()));
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }

                 
             }
             if(ae.getActionCommand().equals("displayText")){
                 try{
                     lineDisplay.setCursorRow(Integer.parseInt(row.getText()));
                     lineDisplay.setCursorColumn(Integer.parseInt(column.getText()));
                     lineDisplay.displayText(lineDisplayData.getText(),Integer.parseInt(attribute.getText()));
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }

                 
             }

            try
            {
                if(deviceEnabledCB.isEnabled()){
                    deviceEnabledCB.setSelected(lineDisplay.getDeviceEnabled());
                }
                if(freezeEventsCB.isEnabled()){
                    freezeEventsCB.setSelected(lineDisplay.getFreezeEvents());
                }
            }
            catch(JposException je)
            {
                System.err.println("LineDisplayPanel: MethodListener: JposException");
            }
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
                        lineDisplay.setDeviceEnabled(false);
                    }else{
                        lineDisplay.setDeviceEnabled(true);
                    }
                }else if (source == freezeEventsCB){
                    if (e.getStateChange() == ItemEvent.DESELECTED){
                        lineDisplay.setFreezeEvents(false);
                    }else{
                        lineDisplay.setFreezeEvents(true);
                    }
                }
            }
            catch(JposException je)
            {
                System.err.println("LineDisplayPanel: CheckBoxListener: Jpos Exception" + source);
            }
         }
     }    
    


}
