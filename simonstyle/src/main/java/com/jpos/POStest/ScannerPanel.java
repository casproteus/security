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
// ScannerPanel.java - The Scanner panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;
import jpos.events.*;

public class ScannerPanel extends Component implements DataListener, ErrorListener, ActionListener
{

    protected MainButtonPanel mainButtonPanel;

    private Scanner scanner = null;
    private int status = 0;

    private String defaultLogicalName = "defaultScanner";


    byte[] scanData;
    byte[] scanDataLabel;
    int    scanDataType;
    boolean autoDisable;
    boolean dataEventEnabled;
    boolean deviceEnabled;
    boolean freezeEvents;
    boolean decodeData;

    boolean updateDevice = true;


    private JTextField scanDataTextField;
    private JTextField scanDataLabelTextField;
    private JTextField scanDataTypeTextField;

    private JCheckBox autoDisableCB;
    private JCheckBox dataEventEnabledCB;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    private JCheckBox decodeDataCB;

    Runnable doUpdateGUI;

    public ScannerPanel() {}



    public Component make() {
        
        if(scanner == null)
        {
            scanner = new Scanner();
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
        propPanel.add(Box.createVerticalGlue());
        subPanel.add(propPanel);
       
        autoDisableCB.setEnabled(true);
        dataEventEnabledCB.setEnabled(true);
        deviceEnabledCB.setEnabled(true);
        freezeEventsCB.setEnabled(true);
        decodeDataCB.setEnabled(true);

        CheckBoxListener cbListener = new CheckBoxListener();
        autoDisableCB.addItemListener(cbListener);
        dataEventEnabledCB.addItemListener(cbListener);
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);
        decodeDataCB.addItemListener(cbListener);

        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));

        JLabel scanDataLabel = new JLabel("Scan Data: ");
        scanDataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(scanDataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        scanDataLabel = new JLabel("Scan Data Type: ");
        scanDataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(scanDataLabel);
        labelPanel.add(Box.createRigidArea(new Dimension(0,4)));
        scanDataLabel = new JLabel("Scan Data Label: ");
        scanDataLabel.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        labelPanel.add(scanDataLabel);
        labelPanel.add(Box.createVerticalGlue());


        subPanel.add(labelPanel);

        JPanel tfPanel = new JPanel();
        tfPanel.setLayout(new BoxLayout(tfPanel, BoxLayout.Y_AXIS));

        scanDataTextField = new JTextField(30);
        scanDataTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(scanDataTextField);
        scanDataTypeTextField = new JTextField(30);
        scanDataTypeTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(scanDataTypeTextField);
        scanDataLabelTextField = new JTextField(30);
        scanDataLabelTextField.setMaximumSize(new Dimension(Short.MAX_VALUE, 25)); 
        tfPanel.add(scanDataLabelTextField);

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

                     scanner.open(logicalName);
                     scanner.addErrorListener(this);
                     scanner.addDataListener(this);
                 }catch(JposException e){
                     scanner = null;
                     System.err.println("Jpos exception on open " + e);
                 }
             }
             if(ae.getActionCommand().equals("claim")){
                 try{
                     scanner.claim(0);
                 }catch(JposException e){
                     System.err.println("Jpos exception on claim " + e);
                 }
             }
             if(ae.getActionCommand().equals("release")){
                 try{
                     scanner.release();
                 }catch(JposException e){
                     System.err.println("Jpos exception on release " + e);
                 }
             }
             if(ae.getActionCommand().equals("close")){
                 try{
                     scanner.close();
                 }catch(JposException e){
                     System.err.println("Jpos exception on close" + e);
                 }
             }
             try
             {
                 updateDevice = false;
                 autoDisableCB.setSelected(scanner.getAutoDisable());
                 dataEventEnabledCB.setSelected(scanner.getDataEventEnabled());
                 deviceEnabledCB.setSelected(scanner.getDeviceEnabled());
                 freezeEventsCB.setSelected(scanner.getFreezeEvents());
                 decodeDataCB.setSelected(scanner.getDecodeData());
                 updateDevice = true;
             }
             catch(JposException je)
             {
                 System.err.println("ScannerPanel: ActionPerformed method received JposException: "+ je);
             }
        }


    public void dataOccurred(DataEvent de)
    {
        try
        {
            scanData = scanner.getScanData();
            scanDataLabel = scanner.getScanDataLabel();
            scanDataType = scanner.getScanDataType();
            autoDisable = scanner.getAutoDisable();
            dataEventEnabled = scanner.getDataEventEnabled();
            deviceEnabled = scanner.getDeviceEnabled();
            freezeEvents = scanner.getFreezeEvents();
            decodeData = scanner.getDecodeData();
        }
        catch(JposException je)
        {
            System.err.println("Scanner: dataOccurred: Jpos Exception" + je);
        }

        updateDevice = false;

        try{
            SwingUtilities.invokeLater(doUpdateGUI);
        }catch(Exception e){
            System.err.println("InvokeLater exception.");
        } 
        updateDevice = true;

    }


    public void errorOccurred(ErrorEvent ee)
    {
            System.out.println("Error Occurred");
    }

    public void updateGUI()
    {
            scanDataTextField.setText(new String(scanData));
            scanDataLabelTextField.setText(new String(scanDataLabel));
            Integer i = new Integer(scanDataType);
            scanDataTypeTextField.setText(i.toString());
            autoDisableCB.setSelected(autoDisable);
            dataEventEnabledCB.setSelected(dataEventEnabled);
            deviceEnabledCB.setSelected(deviceEnabled);
            freezeEventsCB.setSelected(freezeEvents);
            decodeDataCB.setSelected(decodeData);
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
                            scanner.setAutoDisable(false);
                        }else{
                            scanner.setAutoDisable(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }

                }
                else if (source == dataEventEnabledCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setDataEventEnabled(false);
                        }else{
                            scanner.setDataEventEnabled(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }

                }
                else if (source == deviceEnabledCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setDeviceEnabled(false);
                        }else{
                            scanner.setDeviceEnabled(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == freezeEventsCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setFreezeEvents(false);
                        }else{
                            scanner.setFreezeEvents(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == decodeDataCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            scanner.setDecodeData(false);
                        }else{
                            scanner.setDecodeData(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("ScannerPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                try
                {
                    updateDevice = false;
                    autoDisableCB.setSelected(scanner.getAutoDisable());
                    dataEventEnabledCB.setSelected(scanner.getDataEventEnabled());
                    deviceEnabledCB.setSelected(scanner.getDeviceEnabled());
                    freezeEventsCB.setSelected(scanner.getFreezeEvents());
                    decodeDataCB.setSelected(scanner.getDecodeData());
                    updateDevice = true;
                }
                catch(JposException je)
                {
                    System.err.println("ScannerPanel: CheckBoxListener method received JposException: "+ je);
                }
             }


         }
     }    
    


}
