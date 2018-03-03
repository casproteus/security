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
// POStest.java - The main class for POStest
//
//------------------------------------------------------------------------------
package com.jpos.POStest;


import jpos.*;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class POStest extends JPanel{


    public static void main(String[] args) {

        POStestGUI gui = new POStestGUI();
        JFrame frame = new JFrame("POStest");
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
        });
         
        frame.getContentPane().add(gui,BorderLayout.CENTER);

        frame.setSize(700, 500);
        frame.setVisible(true);

    }

}


    

