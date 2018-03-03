package org.cas.client.platform.pimview.pimscratch;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.cas.client.platform.casbeans.textpane.PIMTextPane;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;

public class NotePaper extends JPanel implements MouseMotionListener, LayoutManager {
    /***/
    public NotePaper() {
        setMaximumSize(new Dimension(200, 150));
        setMinimumSize(new Dimension(200, 150));
        setPreferredSize(new Dimension(200, 150));
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        setBackground(Color.ORANGE);
        textPane = new PIMTextPane(null, false, true);
        textScrollPane = new PIMScrollPane(textPane);
        setLayout(this);
        add(textScrollPane, -1);
        addMouseMotionListener(this);
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseDragged(java.awt.event.MouseEvent)
     */
    public void mouseDragged(
            MouseEvent e) {
        // TODO Auto-generated method stub
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        setLocation(e.getComponent().getLocation().x + e.getX() - 100, e.getComponent().getLocation().y + e.getY());
    }

    /*
     * (non-Javadoc)
     * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
     */
    public void mouseMoved(
            MouseEvent e) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     * @see java.awt.LayoutManager#addLayoutComponent(java.lang.String, java.awt.Component)
     */
    public void addLayoutComponent(
            String name,
            Component comp) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * @see java.awt.LayoutManager#layoutContainer(java.awt.Container)
     */
    public void layoutContainer(
            Container parent) {
        // TODO Auto-generated method stub
        java.awt.Rectangle tmpBounds = getBounds();
        // 设置滚动面板的Bounds
        if (textScrollPane != null) {
            textScrollPane.setBounds(0, 10, tmpBounds.width, tmpBounds.height - 10);
        }

    }

    /*
     * (non-Javadoc)
     * @see java.awt.LayoutManager#minimumLayoutSize(java.awt.Container)
     */
    public Dimension minimumLayoutSize(
            Container parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.LayoutManager#preferredLayoutSize(java.awt.Container)
     */
    public Dimension preferredLayoutSize(
            Container parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * @see java.awt.LayoutManager#removeLayoutComponent(java.awt.Component)
     */
    public void removeLayoutComponent(
            Component comp) {
        // TODO Auto-generated method stub

    }

    private PIMTextPane textPane;
    private PIMScrollPane textScrollPane;

    public static void main(
            String[] args) {
        JFrame mainFrame = new JFrame();
        NotePaper notePaper = new NotePaper();
        JPanel tempPanel = new JPanel();
        tempPanel.add(notePaper);
        mainFrame.getContentPane().add(tempPanel);
        mainFrame.setSize(800, 600);
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

}
