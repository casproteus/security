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
// MSRPanel.java - The MSR panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class MSRPanel extends Component implements DataListener, ActionListener
{
    protected MSR msr;

    protected MainButtonPanel mainButtonPanel;

    private String defaultLogicalName = "defaultMSR";

    byte[] track1Data;
    byte[] track2Data;
    byte[] track3Data;
    String accountNumber;
    String expirationDate;
    String title;
    String firstName;
    String middleInitial;
    String surname;
    String suffix;
    String serviceCode;
    byte[] t1DiscData;
    byte[] t2DiscData;

    boolean autoDisable;
    boolean dataEventEnabled;
    boolean deviceEnabled;
    boolean freezeEvents;
    boolean decodeData;
    boolean parseDecodeData;

    boolean updateDevice = true;

    private JTextField track1DataTextField;
    private JTextField track2DataTextField;
    private JTextField track3DataTextField;

    private JTextField accountNumberTextField;
    private JTextField expirationDateTextField;
    private JTextField titleTextField;
    private JTextField firstNameTextField;
    private JTextField middleInitialTextField;
    private JTextField surnameTextField;
    private JTextField suffixTextField;
    private JTextField serviceCodeTextField;
    private JTextField t1DiscDataTextField;
    private JTextField t2DiscDataTextField;

    private JCheckBox autoDisableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox decodeDataCB;
    private JCheckBox parseDecodeDataCB;

    Runnable doUpdateGUI;

    public MSRPanel() {
        msr = null;
    }

    public MSR getMSR()
    {
        return msr;
    }


    public Component make() {
        if(msr == null)
        {
            msr = new MSR();
        }
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));


        mainButtonPanel = new MainButtonPanel(this,defaultLogicalName);
        mainPanel.add(mainButtonPanel);

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
        decodeDataCB = new JCheckBox("Decode data");
        decodeDataCB.setFont(newf);
        propPanel.add(decodeDataCB);
        parseDecodeDataCB = new JCheckBox("Parse decode data");
        parseDecodeDataCB.setFont(newf);
        propPanel.add(parseDecodeDataCB);
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
       
        autoDisableCB.setEnabled(true);
        dataEventEnabledCB.setEnabled(true);
        deviceEnabledCB.setEnabled(true);
        freezeEventsCB.setEnabled(true);
        decodeDataCB.setEnabled(true);
        parseDecodeDataCB.setEnabled(true);

        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        decodeDataCB.addItemListener(cbListener);
        parseDecodeDataCB.addItemListener(cbListener);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        JLabel dataLabel = new JLabel("Track 1 Data: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Track 2 Data: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Track 3 Data: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);

        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Account number: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Expiration date: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Title: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("First name: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Middle initial: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Surname: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Suffix: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Service Code: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Track 1 discretionary data: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        dataLabel = new JLabel("Track 2 discretionary data: ");
        dataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(dataLabel);

        labelPanel.add(Box.createVerticalGlue());


        subPanel.add(labelPanel);

        JPanel tfPanel = new JPanel();
        tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.Y_AXIS));

        track1DataTextField = new JTextField(50);
        track1DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(track1DataTextField);
        track2DataTextField = new JTextField(50);
        track2DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(track2DataTextField);
        track3DataTextField = new JTextField(50);
        track3DataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(track3DataTextField);


        accountNumberTextField = new JTextField(50);
        accountNumberTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(accountNumberTextField);
        expirationDateTextField = new JTextField(50);
        expirationDateTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(expirationDateTextField);
        titleTextField = new JTextField(50);
        titleTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(titleTextField);
        firstNameTextField = new JTextField(50);
        firstNameTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(firstNameTextField);
        middleInitialTextField = new JTextField(50);
        middleInitialTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(middleInitialTextField);
        surnameTextField = new JTextField(50);
        surnameTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(surnameTextField);
        suffixTextField = new JTextField(50);
        suffixTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(suffixTextField);
        serviceCodeTextField = new JTextField(50);
        serviceCodeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(serviceCodeTextField);
        t1DiscDataTextField = new JTextField(50);
        t1DiscDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(t1DiscDataTextField);
        t2DiscDataTextField = new JTextField(50);
        t2DiscDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(t2DiscDataTextField);

        tfPanel.add(Box.createVerticalGlue());

        subPanel.add(tfPanel);

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
                     msr.open(logicalName);
                     msr.addDataListener(this);
                     msr.setTracksToRead(MSRConst.MSR_TR_1_2);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("claim")){
                 try{
                     msr.claim(0);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("release")){
                 try{
                     msr.release();
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("close")){
                 try{
                     msr.close();
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             try
             {
                 updateDevice = false;
                 autoDisableCB.setSelected(msr.getAutoDisable());
                 dataEventEnabledCB.setSelected(msr.getDataEventEnabled());
                 deviceEnabledCB.setSelected(msr.getDeviceEnabled());
                 freezeEventsCB.setSelected(msr.getFreezeEvents());
                 decodeDataCB.setSelected(msr.getDecodeData());
                 parseDecodeDataCB.setSelected(msr.getParseDecodeData());
                 updateDevice = true;
             }
             catch(JposException je)
             {
                 System.err.println("MSRPanel: JposException" + je);
             }
        }


    public void dataOccurred(DataEvent de)
    {
        try
        {
                track1Data = msr.getTrack1Data();
                track2Data = msr.getTrack2Data();
                track3Data = msr.getTrack3Data();
                accountNumber = msr.getAccountNumber();
                expirationDate = msr.getExpirationDate();
                title = msr.getTitle();
                firstName = msr.getFirstName();
                middleInitial = msr.getMiddleInitial();
                surname = msr.getSurname();
                suffix = msr.getSuffix();
                serviceCode = msr.getServiceCode();
                t1DiscData = msr.getTrack1DiscretionaryData();
                t2DiscData = msr.getTrack2DiscretionaryData();
            autoDisable = msr.getAutoDisable();
            dataEventEnabled = msr.getDataEventEnabled();
            deviceEnabled = msr.getDeviceEnabled();
            freezeEvents = msr.getFreezeEvents();
            decodeData = msr.getDecodeData();
            parseDecodeData = msr.getParseDecodeData();

    //        msr.setDataEventEnabled(true);
        }
        catch(JposException je)
        {
            System.err.println("MSR: Jpos Exception");
        }

        updateDevice = false;
        try{
            SwingUtilities.invokeLater(doUpdateGUI);
        }catch(Exception e){
            System.err.println("InvokeLater exception.");
        } 
        updateDevice = true;


    }

    public void updateGUI()
    {
                track1DataTextField.setText(new String(track1Data));
                track2DataTextField.setText(new String(track2Data));
                track3DataTextField.setText(new String(track3Data));
                accountNumberTextField.setText(new String(accountNumber));
                expirationDateTextField.setText(new String(expirationDate));
                titleTextField.setText(new String(title));
                firstNameTextField.setText(new String(firstName));
                middleInitialTextField.setText(new String(middleInitial));
                surnameTextField.setText(new String(surname));
                suffixTextField.setText(new String(suffix));
                serviceCodeTextField.setText(new String(serviceCode));
                t1DiscDataTextField.setText(new String(t1DiscData));
                t2DiscDataTextField.setText(new String(t2DiscData));

            autoDisableCB.setSelected(autoDisable);
            dataEventEnabledCB.setSelected(dataEventEnabled);
            deviceEnabledCB.setSelected(deviceEnabled);
            freezeEventsCB.setSelected(freezeEvents);
            decodeDataCB.setSelected(decodeData); 
            parseDecodeDataCB.setSelected(parseDecodeData);

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
                        if (e.getStateChange() == ItemEvent.DESELECTED)
                        {
                            msr.setAutoDisable(false);
                        }
                        else
                        {
                            msr.setAutoDisable(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MSRPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }

                }
                else if (source == dataEventEnabledCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED)
                        {
                            msr.setDataEventEnabled(false);
                        }
                        else
                        {
                            msr.setDataEventEnabled(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MSRPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == deviceEnabledCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED)
                        {
                            msr.setDeviceEnabled(false);
                        }
                        else
                        {
                            msr.setDeviceEnabled(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MSRPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == freezeEventsCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED)
                        {
                            msr.setFreezeEvents(false);
                        }
                        else
                        {
                            msr.setFreezeEvents(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MSRPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == decodeDataCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED)
                        {
                            msr.setDecodeData(false);
                        }
                        else
                        {
                            msr.setDecodeData(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MSRPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == parseDecodeDataCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED)
                        {
                            msr.setParseDecodeData(false);
                        }
                        else
                        {
                            msr.setParseDecodeData(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("MSRPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                try
                {
                    updateDevice = false;
                    autoDisableCB.setSelected(msr.getAutoDisable());
                    dataEventEnabledCB.setSelected(msr.getDataEventEnabled());
                    deviceEnabledCB.setSelected(msr.getDeviceEnabled());
                    freezeEventsCB.setSelected(msr.getFreezeEvents());
                    decodeDataCB.setSelected(msr.getDecodeData());
                    parseDecodeDataCB.setSelected(msr.getParseDecodeData());
                    updateDevice = true;
                }
                catch(JposException je)
                {
                    System.err.println("MSRPanel: CheckBoxListener method received JposException: "+ je);
                }
             }

         }
     }    
    


}
