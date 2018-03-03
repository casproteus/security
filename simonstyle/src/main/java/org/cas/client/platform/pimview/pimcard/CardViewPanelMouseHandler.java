package org.cas.client.platform.pimview.pimcard;

import java.awt.Cursor;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

public class CardViewPanelMouseHandler implements MouseInputListener {

    /**
     * Creates a new instance of CardViewPanelMouseListener
     * 
     * @param cardPane
     *            卡片面板
     */
    public CardViewPanelMouseHandler(CardViewPanel cardPane) {
        this.cardPane = cardPane;
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseClicked(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button is pressed on a component and then dragged. <code>MOUSE_DRAGGED</code> events will
     * continue to be delivered to the component where the drag originated until the mouse button is released
     * (regardless of whether the mouse position is within the bounds of the component).
     * <p>
     * Due to platform-dependent Drag&Drop implementations, <code>MOUSE_DRAGGED</code> events may not be delivered
     * during a native Drag&Drop operation.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseDragged(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse enters a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse cursor has been moved onto a component but no buttons have been pushed.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseMoved(
            MouseEvent e) {
        int x = e.getX();
        int cardWidth = cardPane.getCardWidth();
        int dx = x - 10;
        int columns = dx / cardWidth;
        if (dx > columns * cardWidth + 10 * (columns - 1) && dx < (columns * cardWidth + 10 * columns)) {
            cardPane.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
        } else {
            cardPane.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mousePressed(
            MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            return;
        }
        if ((e.getModifiers() & MouseEvent.BUTTON1_MASK) != 0) {
            cardPane.selectCard(e.getPoint(), e.isControlDown(), e.isShiftDown());

            if (e.getClickCount() >= 2) {
                cardPane.processDoubleClickedAction();
            }
        }
    }

    /**
     * Invoked when a mouse button has been released on a component.
     * 
     * @param e
     *            鼠标事件源
     */
    public void mouseReleased(
            MouseEvent e) {
        if (cardPane.getView() == null) {
            return;
        }
        if (SwingUtilities.isRightMouseButton(e)) {
            cardPane.getView().getApplication().showPopupMenu(cardPane, e.getX(), e.getY());
        }
    }

    private CardViewPanel cardPane;
}
