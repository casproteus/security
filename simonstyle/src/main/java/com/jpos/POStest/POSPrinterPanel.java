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
// POSPrinterPanel.java - The POSPrinter panel for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;

import jpos.*;

public class POSPrinterPanel extends Component {

    protected MainButtonPanel mainButtonPanel;

    private POSPrinter posPrinter;

    private String defaultLogicalName = "defaultPrinter";

    private JTextArea posPrinterOutputArea;


    private JButton printNormalButton;
    private JButton printBarCodeButton;
    private JButton cutPaperButton;
    private JCheckBox deviceEnabledCB;
    private JCheckBox freezeEventsCB;
    
    private int station;
    private int bcSymbology;
    private int bcHeight;
    private int bcWidth; 
    private int bcAlignment;
    private int bcTextPosition;

    boolean deviceEnabled;
    boolean freezeEvents;

    boolean updateDevice = true;



    public POSPrinterPanel() 
    {
    }

    public Component make() {
 //       this.posPrinter = posPrinter;
        if(posPrinter == null)
        {
            posPrinter = new POSPrinter();
        }
        
        JPanel mainPanel = new JPanel(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        MethodListener methodListener = new MethodListener();

        mainButtonPanel = new MainButtonPanel(methodListener,defaultLogicalName);
        mainPanel.add(mainButtonPanel);

        JPanel buttonPanel = new JPanel();
        printNormalButton = new JButton("Print Normal");
        printNormalButton.setActionCommand("printNormal");
        printNormalButton.addActionListener(methodListener);
        buttonPanel.add(printNormalButton);
        printBarCodeButton = new JButton("Print Bar Code");
        printBarCodeButton.setActionCommand("printBarCode");
        printBarCodeButton.addActionListener(methodListener);
//        buttonPanel.add(printBarCodeButton);
        cutPaperButton = new JButton("Cut Paper");
        cutPaperButton.setActionCommand("cutPaper");
        cutPaperButton.addActionListener(methodListener);
        buttonPanel.add(cutPaperButton);

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
       

        CheckBoxListener cbListener = new CheckBoxListener();
        deviceEnabledCB.addItemListener(cbListener);
        freezeEventsCB.addItemListener(cbListener);

        StationListener stationListener = new StationListener();

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel("Station: ");
        radioPanel.add(label);
        JRadioButton rButton = new JRadioButton("Receipt");
        rButton.setActionCommand("receipt");
        rButton.addActionListener(stationListener);
        rButton.setSelected(true);
        JRadioButton jButton = new JRadioButton("Journal");
        jButton.setActionCommand("journal");
        jButton.addActionListener(stationListener);
        JRadioButton sButton = new JRadioButton("Slip");
        sButton.setActionCommand("slip");
        sButton.addActionListener(stationListener);

        station = POSPrinterConst.PTR_S_RECEIPT;

        // Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(rButton);
        group.add(jButton);
        group.add(sButton);

        radioPanel.add(rButton);
        radioPanel.add(jButton);
        radioPanel.add(sButton);
        radioPanel.add(Box.createVerticalGlue());

        subPanel.add(radioPanel);

        JPanel printerOutputPanel = new JPanel();
        printerOutputPanel.setLayout(new BoxLayout(printerOutputPanel, BoxLayout.Y_AXIS));
        label = new JLabel("Send to printer: ");
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        printerOutputPanel.add(label);

        posPrinterOutputArea = new JTextArea(10,10);
        JScrollPane scrollPane = new JScrollPane(posPrinterOutputArea);
        printerOutputPanel.add(scrollPane);

//        JTextField bcSymTF = new JTextField(8);
//        printerOutputPanel.add(bcSymTF);

        subPanel.add(printerOutputPanel);

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

                     posPrinter.open(logicalName);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("claim")){
                 try{
                     posPrinter.claim(0);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("release")){
                 try{
                     posPrinter.release();

                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }
             if(ae.getActionCommand().equals("close")){
                 try{
                     posPrinter.close();

                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }


             if(ae.getActionCommand().equals("printNormal")){
                 try{
                     posPrinter.printNormal(station, posPrinterOutputArea.getText());
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }

                 
             }

             if(ae.getActionCommand().equals("cutPaper")){
                 try{
                     posPrinter.cutPaper(90);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }

             if(ae.getActionCommand().equals("printBarCode")){
                 try{
                     posPrinter.printBarCode(station, posPrinterOutputArea.getText(),0,0,0,0,0);
                 }catch(JposException e){
                     System.err.println("Jpos exception " + e);
                 }
             }

             try
             {
                 updateDevice = false;
                 deviceEnabledCB.setSelected(posPrinter.getDeviceEnabled());
                 freezeEventsCB.setSelected(posPrinter.getFreezeEvents());
                 updateDevice = true;
             }
             catch(JposException je)
             {
                 System.err.println("POSPrinterPanel: ActionPerformedmethod received JposException: "+ je);
             }
        }
    }


    class CheckBoxListener implements ItemListener 
    {
        public void itemStateChanged(ItemEvent e) 
        {
            if(updateDevice)
            {

                Object source = e.getItemSelectable();

                if (source == deviceEnabledCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            posPrinter.setDeviceEnabled(false);
                        }else{
                            posPrinter.setDeviceEnabled(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("POSPrinterPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }
                else if (source == freezeEventsCB)
                {
                    try
                    {
                        if (e.getStateChange() == ItemEvent.DESELECTED){
                            posPrinter.setFreezeEvents(false);
                        }else{
                            posPrinter.setFreezeEvents(true);
                        }
                    }
                    catch(JposException je)
                    {
                        System.err.println("POSPrinterPanel: CheckBoxListener: Jpos Exception: " + je + "\nSource: " + source);
                    }
                }

            }
         }
     }    
    


   /** Listens to the station radio buttons. */
    class StationListener implements ActionListener { 
        public void actionPerformed(ActionEvent e) {
             if(e.getActionCommand().equals("receipt")){station = POSPrinterConst.PTR_S_RECEIPT;}
             if(e.getActionCommand().equals("journal")){station = POSPrinterConst.PTR_S_JOURNAL;}
             if(e.getActionCommand().equals("slip")){station = POSPrinterConst.PTR_S_SLIP;}
        }
    }

}
