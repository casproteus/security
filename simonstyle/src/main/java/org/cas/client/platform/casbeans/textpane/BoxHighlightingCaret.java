package org.cas.client.platform.casbeans.textpane;

import java.awt.Color;

import javax.swing.text.DefaultCaret;
import javax.swing.text.Highlighter;

class BoxHighlightingCaret extends DefaultCaret {

    /** Creates a new instance of BoxHighlightingCaret */
    BoxHighlightingCaret() {
        // 设置光标频率
        setBlinkRate(500);
        setVisible(true);

    }

    private static BoxHighlighterPainter painter = new BoxHighlighterPainter(Color.black);

    /**
     * 重载getSelectionPainter()方法
     * 
     * @return painter BoxHighlighterPainter
     */
    public Highlighter.HighlightPainter getSelectionPainter() {
        return painter;
    }
}
