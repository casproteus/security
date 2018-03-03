package org.cas.client.platform.pimview.pimcard;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.cas.client.platform.cascustomize.CustOpts;

public class CardMatchingPanel extends JPanel implements ActionListener {

    /** Creates a new instance of CardMatchingPanel */
    public CardMatchingPanel() {
        setLayout(null);

        initComponents();
    }

    private void initComponents() {
        JButton[] buttons = new JButton[CardConstants.MATCHING_LABEL.length];
        int x = CustOpts.HOR_GAP;
        int y = CustOpts.VER_GAP;
        for (int i = 0; i < CardConstants.MATCHING_LABEL.length; i++) {
            buttons[i] = new JButton(CardConstants.MATCHING_LABEL[i]);
            buttons[i].setBounds(x, y, CardConstants.BTN_WIDTH, CardConstants.BTN_HEIGHT);
            y += (CardConstants.BTN_HEIGHT + CustOpts.VER_GAP);
            buttons[i].addActionListener(this);
            add(buttons[i]);
        }
    }

    /**
     * Invoked when an action occurs.
     * 
     * @param e
     *            动作事件
     */
    public void actionPerformed(
            ActionEvent e) {
    }

}
