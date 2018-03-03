package org.cas.client.platform.casbeans;

import java.awt.Dimension;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import org.cas.client.platform.cascustomize.CustOpts;

public class PIMSeparator extends JLabel {
    /**
     * Creates new PIMTitle with text and width.
     * 
     * @param text
     *            title text string
     * @param length
     *            the length from text to separator end
     */
    public PIMSeparator(String text) {
        super(text);
        separator = new JSeparator(JSeparator.HORIZONTAL);
        separator.setOpaque(false);
        add(separator);
    }

    /**
     * Defines the single line of text this component will display. If the value of text is null or empty string,
     * nothing is displayed.
     * 
     * @param text
     *            title text string.
     */
    public void setText(
            String text) {
        super.setText(text);
        setLength(getLength());
    }

    /**
     * Sets the font of this component.
     * 
     * @param font
     *            The font to become this component's font.
     */
    public void setFont(
            Font font) {
        super.setFont(font);
        setPreferredSize(getPreferredSize());
    }

    /**
     * Sets the preferred size of the receiving component.
     * 
     * @param size
     *            the preferred size.
     */
    public void setPreferredSize(
            Dimension size) {
        super.setPreferredSize(size);
        int length = getFontMetrics(CustOpts.custOps.getFontOfDefault()).stringWidth(getText());
        if (length != 0) {
            length += 5;
        }
        offset = length;
        if (separator != null) {
            separator.setBounds(length, size.height >> 1, size.width - length, 2);
        }
    }

    /**
     * Moves and resizes this component. The new location of the top-left corner is specified by <code>x</code> and
     * <code>y</code>, and the new size is specified by <code>width</code> and <code>height</code>.
     * 
     * @param x
     *            The new <i>x</i>-coordinate of this component.
     * @param y
     *            The new <i>y</i>-coordinate of this component.
     * @param width
     *            The new <code>width</code> of this component.
     * @param height
     *            The new <code>height</code> of this component.
     */
    public void setBounds(
            int x,
            int y,
            int width,
            int height) {
        super.setBounds(x, y, width, height);
        separator.setBounds(offset, height >> 1, width - offset, 2);
    }

    private void setLength(
            int length) {
        setPreferredSize(new Dimension(length, CustOpts.LBL_HEIGHT));
    }

    private int getLength() {
        return getPreferredSize().width;
    }

    private int offset;
    private JSeparator separator;
}
