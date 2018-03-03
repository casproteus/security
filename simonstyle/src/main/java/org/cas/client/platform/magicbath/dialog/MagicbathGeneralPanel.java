package org.cas.client.platform.magicbath.dialog;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import org.cas.client.platform.cascustomize.CustOpts;

public class MagicbathGeneralPanel extends JPanel implements ComponentListener {

    public MagicbathGeneralPanel() {
        initConponent();
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        pOpenBox.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - CustOpts.HOR_GAP * 2,
                pOpenBox.getPreferredSize().height);
        pAddService.setBounds(pOpenBox.getX(), pOpenBox.getY() + pOpenBox.getHeight() + CustOpts.VER_GAP,
                pOpenBox.getWidth(), pAddService.getPreferredSize().height);
        pCleanSalary.setBounds(pAddService.getX(), getHeight() - CustOpts.VER_GAP
                - pCleanSalary.getPreferredSize().height, pAddService.getWidth(),
                pCleanSalary.getPreferredSize().height);
        pCalculate.setBounds(pCleanSalary.getX(), pAddService.getY() + pAddService.getHeight() + CustOpts.VER_GAP,
                pCleanSalary.getWidth(), pCleanSalary.getY() - pAddService.getY() - pAddService.getHeight()
                        - CustOpts.VER_GAP * 2);

        if (pCalculate.getHeight() < 160) {
            pCleanSalary.setBounds(pAddService.getX(), getHeight() - CustOpts.VER_GAP
                    - pCleanSalary.getPreferredSize().height, pAddService.getWidth(), 0);
            pCalculate.setBounds(pCleanSalary.getX(), pAddService.getY() + pAddService.getHeight() + CustOpts.VER_GAP,
                    pCleanSalary.getWidth(), getHeight() - pAddService.getY() - pAddService.getHeight()
                            - CustOpts.VER_GAP * 2);
        }
    }

    /** Invoked when the component's position changes. */
    public void componentMoved(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made visible. */
    public void componentShown(
            ComponentEvent e) {
    }

    /** Invoked when the component has been made invisible. */
    public void componentHidden(
            ComponentEvent e) {
    }

    private void initConponent() {
        // init----------
        pOpenBox = new OpenBoxPanel();
        pAddService = new AddServicePanel();
        pCalculate = new CalculatePanel();
        pCleanSalary = new CleanSalaryPanel();

        // properties
        setLayout(null);
        setOpaque(false);

        // built
        add(pOpenBox);
        add(pAddService);
        add(pCalculate);
        add(pCleanSalary);

        // listeners
        addComponentListener(this);
    }

    public static OpenBoxPanel pOpenBox;
    public static AddServicePanel pAddService;
    public static CalculatePanel pCalculate;
    static CleanSalaryPanel pCleanSalary;
}
