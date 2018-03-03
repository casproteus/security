package org.cas.client.platform.foregrounding.dialog;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import org.cas.client.platform.cascustomize.CustOpts;

public class ForegroundingGeneralPanel extends JPanel implements ComponentListener {

    public ForegroundingGeneralPanel() {
        initConponent();
    }

    /** Invoked when the component's size changes. */
    public void componentResized(
            ComponentEvent e) {
        salePane.setBounds(CustOpts.HOR_GAP, CustOpts.VER_GAP, getWidth() - CustOpts.HOR_GAP * 2,
                salePane.getPreferredSize().height);
        pAddService.setBounds(salePane.getX(), salePane.getY() + salePane.getHeight() + CustOpts.VER_GAP,
                salePane.getWidth(), pAddService.getPreferredSize().height);
        pCalculate.setBounds(pAddService.getX(), pAddService.getY() + pAddService.getHeight() + CustOpts.VER_GAP,
                pAddService.getWidth(), getHeight() - pAddService.getY() - pAddService.getHeight() - CustOpts.VER_GAP
                        * 2);
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
        salePane = new SalePane();
        pAddService = new AddServicePanel();
        pCalculate = new CalculatePanel();

        // properties
        setLayout(null);
        setOpaque(false);

        // built
        add(salePane);
        add(pAddService);
        add(pCalculate);

        // listeners
        addComponentListener(this);
    }

    public static SalePane salePane;
    public static AddServicePanel pAddService;
    public static CalculatePanel pCalculate;
}
