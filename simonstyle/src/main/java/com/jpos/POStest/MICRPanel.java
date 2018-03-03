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
// MICRPanel.java - The MICR panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class MICRPanel extends Component implements DataListener, ActionListener
{

    protected MainButtonPanel mainButtonPanel;

    private MICR micr = null;

    private String defaultLogicalName = "defaultMICR";

    String rawData;
    String accountNumber;
    String bankNumber;
    String serialNumber;

    boolean autoDisable;
    boolean dataEventEnabled;
    boolean deviceEnabled;
    boolean freezeEvents;

    protected JTextField accountNumberTextField;
    protected JTextField amountTextField;
    protected JTextField bankNumberTextField;
    protected JTextField checkTypeTextField;
    protected JTextField countryCodeTextField;
    protected JTextField epcTextField;
    protected JTextField rawDataTextField;
    protected JTextField serialNumberTextField;
    protected JTextField transitNumberTextField;

    private JCheckBox autoDisableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;

    boolean updateDevice = true;

    private JButton beginInsertionButton;
    private JButton endInsertionButton;
    private JButton beginRemovalButton;
    private JButton endRemovalButton;

    Runnable doUpdateGUI;


    public MICRPanel() {}


    public Component make() {

        if(micr == null)
        {
            micr = new MICR();
        }

        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

//        MethodListener methodListener = new MethodListener();

        mainButtonPanel = new MainButtonPanel(this,defaultLogicalName);
        mainPanel.add(mainButtonPanel);

        JPanel buttonPanel = new JPanel();
        beginInsertionButton = new JButton("Begin Insertion");
        beginInsertionButton.setActionCommand("beginInsertion");
        beginInsertionButton.addActionListener(this);
        beginInsertionButton.setEnabled(false);
        buttonPanel.add(beginInsertionButton);

        endInsertionButton = new JButton("End Insertion");
        endInsertionButton.setActionCommand("endInsertion");
        endInsertionButton.addActionListener(this);
        endInsertionButton.setEnabled(false);
        buttonPanel.add(endInsertionButton);

        beginRemovalButton = new JButton("Begin Removal");
        beginRemovalButton.setActionCommand("beginRemoval");
        beginRemovalButton.addActionListener(this);
        beginRemovalButton.setEnabled(false);
        buttonPanel.add(beginRemovalButton);

        endRemovalButton = new JButton("End Removal");
        endRemovalButton.setActionCommand("endRemoval");
        endRemovalButton.addActionListener(this);
        endRemovalButton.setEnabled(false);
        buttonPanel.add(endRemovalButton);

        buttonPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 30)); 

        mainPanel.add(buttonPanel);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.X_AXIS));

        JPanel propPanel = new JPanel();
        propPanel.setLayout(new BoxLayout(propPanel, BoxLayout.Y_AXIS));
        autoDisableCB = new JCheckBox("Auto disable");
        Font f = autoDisableCB.getFont();
        Font newf = new Font(f.getName(),Font.PLAIN,f.getSize());
        autoDisableCB.setFont(newf);
        propPanel.add(autoDisableCB);
        dataEventEnabledCB = new JCheckBox("Data event enabled");
        dataEventEnabledCB.setFont(newf);
        propPanel.add(dataEventEnabledCB);
        deviceEnabledCB = new JCheckBox("Device enabled");
        deviceEnabledCB.setFont(newf);
        propPanel.add(deviceEnabledCB);
        freezeEventsCB = new JCheckBox("Freeze events");
        freezeEventsCB.setFont(newf);
        propPanel.add(freezeEventsCB);
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
       
        autoDisableCB.setEnabled(true);
        dataEventEnabledCB.setEnabled(true);
        deviceEnabledCB.setEnabled(true);
        freezeEventsCB.setEnabled(true);

        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel("Account number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("Amount:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("Bank number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("Check type:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("Country code:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("EPC:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("Raw data:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("Serial number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
      
        label = new JLabel("Transit number:");
        label.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(label);      
        labelPanel.add(Box.createRigidArea(new Dimension(0,6)));
        labelPanel.add(Box.createVerticalGlue());


        subPanel.add(labelPanel);


        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        accountNumberTextField = new JTextField();
        accountNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(accountNumberTextField);

        amountTextField = new JTextField();
        amountTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(amountTextField);

        bankNumberTextField = new JTextField();
        bankNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(bankNumberTextField);

        checkTypeTextField = new JTextField();
        checkTypeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(checkTypeTextField);

        countryCodeTextField = new JTextField();
        countryCodeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(countryCodeTextField);

        epcTextField = new JTextField();
        epcTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(epcTextField);

        rawDataTextField = new JTextField();
        rawDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(rawDataTextField);

        serialNumberTextField = new JTextField();
        serialNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(serialNumberTextField);

        transitNumberTextField = new JTextField();
        transitNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 40)); 
        fieldPanel.add(transitNumberTextField);
        fieldPanel.add(Box.createVerticalGlue());

        subPanel.add(fieldPanel);
        subPanel.add(Box.createHorizontalGlue());


        mainPanel.add(subPanel);
        mainPanel.add(Box.createVerticalGlue());

        doUpdateGUI = new Runnable() {
            public void run() {
                updateGUI();
            }
        };

        return mainPanel;
    }

        public void actionPerformed(ActionEvent ae) {
             mainButtonPanel.action(ae);
             if(ae.getActionCommand().equals("open")){
                 try{
                     String logicalName = mainButtonPanel.getLogicalName();
                     if(logicalName.equals("")){
                         logicalName = defaultLogicalName;
                     }
                     micr.open(logicalName);
                     micr.addDataListener(this);
                 }catch(JposException e){
                     micr = null;
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("claim")){
                 try{
                     micr.claim(0);
                     beginInsertionButton.setEnabled(true);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("release")){
                 try{
                     micr.release();
                     beginInsertionButton.setEnabled(false);
                     endInsertionButton.setEnabled(false);
                     beginRemovalButton.setEnabled(false);
                     endRemovalButton.setEnabled(false);

                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("close")){
                 try{
                     micr.close();
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("beginInsertion")){
                 try{
                     micr.beginInsertion(-1);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
                 beginInsertionButton.setEnabled(false);
                 endInsertionButton.setEnabled(true);
                 beginRemovalButton.setEnabled(false);
                 endRemovalButton.setEnabled(false);
             }
             if(ae.getActionCommand().equals("endInsertion")){
                 try{
                     micr.endInsertion();
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
                 beginInsertionButton.setEnabled(false);
                 endInsertionButton.setEnabled(false);
                 beginRemovalButton.setEnabled(true);
                 endRemovalButton.setEnabled(false);
             }
             if(ae.getActionCommand().equals("beginRemoval")){
                 try{
                     micr.beginRemoval(-1);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
                 beginInsertionButton.setEnabled(false);
                 endInsertionButton.setEnabled(false);
                 beginRemovalButton.setEnabled(false);
                 endRemovalButton.setEnabled(true);
             }
             if(ae.getActionCommand().equals("endRemoval")){
                 try{
                     micr.endRemoval();
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
                 beginInsertionButton.setEnabled(true);
                 endInsertionButton.setEnabled(false);
                 beginRemovalButton.setEnabled(false);
                 endRemovalButton.setEnabled(false);
             }

            try
            {
                 updateDevice = false;
                 autoDisableCB.setSelected(micr.getAutoDisable());
                 dataEventEnabledCB.setSelected(micr.getDataEventEnabled());
                 deviceEnabledCB.setSelected(micr.getDeviceEnabled());
                 freezeEventsCB.setSelected(micr.getFreezeEvents());
                 updateDevice = true;
            }
            catch(JposException je)
            {
                System.err.println("MICRPanel: MethodListener: JposException");
            }
        }



    public void dataOccurred(DataEvent dataEvent)
    {
        try
        {
            rawData = micr.getRawData();
            accountNumber = micr.getAccountNumber();
            bankNumber = micr.getBankNumber();
            serialNumber = micr.getSerialNumber();
            
            autoDisable = micr.getAutoDisable();
            dataEventEnabled = micr.getDataEventEnabled();
            deviceEnabled = micr.getDeviceEnabled();
            freezeEvents = micr.getFreezeEvents();
        }
        catch(JposException je)
        {
            System.err.println("MICR: dataOccurred: Jpos Exception " + je);
        }
   
        updateDevice = false;

        try
        {
            SwingUtilities.invokeLater(doUpdateGUI);
        }
        catch(Exception e)
        {
            System.err.println("InvokeLater exception.");
        } 

        updateDevice = true;

    }


    public void updateGUI()
    {
        rawDataTextField.setText(new String(rawData));
        accountNumberTextField.setText(new String(accountNumber));
        bankNumberTextField.setText(new String(bankNumber));
        serialNumberTextField.setText(new String(serialNumber));

        autoDisableCB.setSelected(autoDisable);
        dataEventEnabledCB.setSelected(dataEventEnabled);
        deviceEnabledCB.setSelected(deviceEnabled);
        freezeEventsCB.setSelected(freezeEvents);
    }


    class CheckBoxListener implements ItemListener 
    {
        public void itemStateChanged(ItemEvent e) 
        {
            if(updateDevice)
            {
                Object source = e.getItemSelectable();
                if (source == autoDisableCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setAutoDisable(false);
                        }else{
                            micr.setAutoDisable(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MICRPanel: CheckBoxListener: autoDisable Jpos Exception: " + je + "\nSource: " + source);
                    }

                }
                else if (source == dataEventEnabledCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setDataEventEnabled(false);
                        }else{
                            micr.setDataEventEnabled(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MICRPanel: CheckBoxListener: dataEventEnable Jpos Exception: " + je + "\nSource: " + source);
                    }

                }
                else if (source == deviceEnabledCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setDeviceEnabled(false);
                        }else{
                            micr.setDeviceEnabled(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MICRPanel: CheckBoxListener: deviceEnable Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == freezeEventsCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            micr.setFreezeEvents(false);
                        }else{
                            micr.setFreezeEvents(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MICRPanel: CheckBoxListener: freezeEvents Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                try
                {
                    updateDevice = false;
                    autoDisableCB.setSelected(micr.getAutoDisable());
                    dataEventEnabledCB.setSelected(micr.getDataEventEnabled());
                    deviceEnabledCB.setSelected(micr.getDeviceEnabled());
                    freezeEventsCB.setSelected(micr.getFreezeEvents());
                    updateDevice = true;
                }
                catch(JposException je)
                {
                    System.err.println("MICRPanel: CheckBoxListener method received JposException: "+ je);
                }

            }
         }
     }    
    


}
