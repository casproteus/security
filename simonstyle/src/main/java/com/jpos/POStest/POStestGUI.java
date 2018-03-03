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
// POStestGUI.java - The overall GUI container
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

import jpos.*;

public class POStestGUI extends JPanel  implements  ActionListener{

    public POStestGUI()  {


        JTabbedPane tabbedPane = new JTabbedPane();


        POSPrinterPanel posPrinterPanel = new POSPrinterPanel();
        tabbedPane.addTab("POSPrinter", null, posPrinterPanel.make(), "POSPrinter");

        ScannerPanel scannerPanel = new ScannerPanel();
        tabbedPane.addTab("Scanner", null, scannerPanel.make(), "Scanner");

        MSRPanel msrPanel = new MSRPanel();
        tabbedPane.addTab("MSR", null, msrPanel.make(), "MSR");

        CashDrawerPanel cashDrawerPanel1 = new CashDrawerPanel();
        tabbedPane.addTab("Cash Drawer 1", null, cashDrawerPanel1.make(), "Cash Drawer 1");

        CashDrawerPanel cashDrawerPanel2 = new CashDrawerPanel();
        tabbedPane.addTab("Cash Drawer 2", null, cashDrawerPanel2.make(), "Cash Drawer 2");

        MICRPanel micrPanel = new MICRPanel();
        tabbedPane.addTab("MICR", null, micrPanel.make(), "MICR");

        SigpadPanel sigpadPanel = new SigpadPanel();
        tabbedPane.addTab("SigCap", null, sigpadPanel.make(), "SigCap");

        LineDisplayPanel lineDisplayPanel = new LineDisplayPanel();
        tabbedPane.addTab("Line Display", null, lineDisplayPanel.make(), "Line Display");

        PinpadPanel pinpadPanel = new PinpadPanel();
        tabbedPane.addTab("Pinpad", null, pinpadPanel.make(), "Pinpad");

        tabbedPane.setSelectedIndex(1);


        //Add the tabbed pane to this panel.
        setLayout(new GridLayout(1, 1)); 
        add(tabbedPane);

        char k = 12;
        KeyStroke ks = KeyStroke.getKeyStroke(k);
        this.registerKeyboardAction(this,ks,JComponent.WHEN_IN_FOCUSED_WINDOW);
       
    }

    public void actionPerformed(ActionEvent ae)
    {
    }


}
