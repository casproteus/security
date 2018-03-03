package org.cas.client.platform.casbeans.textpane;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.View;

class BoxHighlighterPainter extends DefaultHighlighter.DefaultHighlightPainter {

    /** Creates a new instance of BoxHighlighterPainter */
    BoxHighlighterPainter(Color color) {
        super(color);
        this.color = color;
    }

    /**
     * 重载paintLayer方法 绘制Highlight
     * 
     * @return rect
     */
    public java.awt.Shape paintLayer(
            Graphics g,
            int p0,
            int p1,
            Shape shape,
            JTextComponent comp,
            View view) {
        Rectangle rect = shape.getBounds();
        try {
            Color color = getColor();
            if (color == null) {
                g.setColor(comp.getSelectionColor());
            } else {
                g.setColor(getColor(comp));
            }
            Rectangle r1 = comp.modelToView(p0);
            Rectangle r2 = comp.modelToView(p1);
            int height = Math.max((int) r1.getHeight(), (int) r2.getHeight());
            if (r1.y == r2.y) {
                // the same line
                g.drawRect(r1.x, r1.y, r2.x - r1.x - 1, height - 1);
                g.fillRect(r1.x, r1.y, r2.x - r1.x - 1, height - 1);
            } else {
                // different lines
                int p0ToMarginWidth = rect.x + rect.width - r1.x;
                g.fillRect(r1.x, r1.y, p0ToMarginWidth, height - 1);
                g.fillRect(rect.x, r2.y, (r2.x - rect.x), height - 1);

            }
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        }
        return rect;
    }

    private Color getColor(
            JTextComponent comp) {
        return color != null ? color : comp.getSelectionColor();
    }

    private Color color;

}
