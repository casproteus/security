package org.cas.client.platform.badminton;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.badminton.dialog.BadmintonGeneralPanel;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.contact.ContactDefaultViews;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.platform.pimview.PicturePane;

public class PlatLable extends PicturePane implements MouseListener {
    public static int WIDTH = 50;
    public static int HEIGHT = 80;

    public PlatLable(int pID, String pSubject, Object pTime) {
        super(PIMPool.pool.getImage(pTime == null ? "/org/cas/client/platform/badminton/img/Plat1.gif"
                : "/org/cas/client/platform/badminton/img/Plat2.gif"));
        id = pID;
        subject = pSubject;
        time = pTime;
        lable = new JLabel(subject);
        lable.setHorizontalAlignment(SwingConstants.CENTER);
        lable.setBounds(0, HEIGHT - CustOpts.LBL_HEIGHT, WIDTH, CustOpts.LBL_HEIGHT);
        setSize(WIDTH, HEIGHT);
        setLayout(null);
        // setBorder(new LineBorder(Color.WHITE));
        setToolTipText("开台时间:".concat(String.valueOf(time)));
        add(lable);
        addMouseListener(this);
    }

    /** Invoked when the mouse button has been clicked (pressed and released) on a component. */
    public void mouseClicked(
            MouseEvent e) {
        if (e.getClickCount() > 1 && time == null) {
            BadmintonGeneralPanel.pOpenBox.openBox(subject);
            // first init the comboBoxes.
            BadmintonGeneralPanel.pOpenBox.initBoxNumbers(null, null);
            BadmintonGeneralPanel.pAddService.initBoxNumbers();
            BadmintonGeneralPanel.pCalculate.initBoxNumbers();
            // second add service record.
            PIMRecord tRec =
                    CASControl.ctrl.getModel().selectRecord(CustOpts.custOps.APPNameVec.indexOf("Contact"), id, 100);
            if (tRec.getFieldValue(ContactDefaultViews.CATEGORY).toString().indexOf("桑拿") > -1)
                BadmintonGeneralPanel.pAddService.addService("", "桑拿", subject, 1);
        }
    }

    /** Invoked when a mouse button has been pressed on a component. */
    public void mousePressed(
            MouseEvent e) {
    }

    /** Invoked when a mouse button has been released on a component. */
    public void mouseReleased(
            MouseEvent e) {
    }

    /** Invoked when the mouse enters a component. */
    public void mouseEntered(
            MouseEvent e) {
    }

    /** Invoked when the mouse exits a component. */
    public void mouseExited(
            MouseEvent e) {
    }

    public boolean resetState(
            String pSubject,
            Object pTime) {
        if (pSubject.equals(subject)) {
            time = pTime;
            preparePicture(PIMPool.pool.getImage(pTime == null ? "/org/cas/client/platform/badminton/img/Bath1.gif"
                    : "/org/cas/client/platform/badminton/img/Bath2.gif"));
            setToolTipText("开台时间:".concat(String.valueOf(time)));
            repaint();
            return true;
        }
        return false;
    }

    private int id;
    private JLabel lable;
    private String subject;
    private Object time;
}
