package org.cas.client.platform.casbeans.textfield;

import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

public class LimitedIntTextField extends JTextField implements KeyListener, FocusListener {
    /** @called by IpInputDig; */
    public LimitedIntTextField() {
        super();
        addKeyListener(this);
        addFocusListener(this);
        ((LimitIntTextDocument) getDocument()).setJCompFacade(this);
        init();
    }

    /** @called by CollaborationOption; */
    public LimitedIntTextField(int prmLimit) {
        super();
        addKeyListener(this);
        addFocusListener(this);
        ((LimitIntTextDocument) getDocument()).setJCompFacade(this);
        ((LimitIntTextDocument) getDocument()).setLimit(prmLimit);
        init();
    }

    /**
     * if the length of the string == 3 or the length is 2 but the number is bigger than 25 or the key last stroked is
     * ".", the focus go to next field. if not the key is derection.
     */
    public void keyTyped(
            KeyEvent e) {
    }

    /** if the key pressed is derections->or <-,then move the focus. */
    public void keyPressed(
            KeyEvent e) {
        int temKeyCode = e.getKeyCode();
        if (temKeyCode == KeyEvent.VK_LEFT) {
            if (getCaretPosition() == 0) {
                setSelectAllFlag(false);
                if (lastJComponent != null)
                    lastJComponent.grabFocus();
            }
        } else if (temKeyCode == KeyEvent.VK_RIGHT) {
            if (getCaretPosition() == getDocument().getLength()) {
                setSelectAllFlag(false);
                nextJComponent.grabFocus();
            }
        } else if (temKeyCode == KeyEvent.VK_BACK_SPACE) {
            if (getCaretPosition() == 0) {
                setSelectAllFlag(true);
                if (lastJComponent != null) {
                    lastJComponent.grabFocus();
                }
            }
        }
    }

    public void keyReleased(
            KeyEvent e) {
    }

    // access about focus--------------------------------------------------------
    public void focusGained(
            FocusEvent e) {
        selectAll();
    }

    public void focusLost(
            FocusEvent e) {
    }

    /**
     * redefine this method to change the condition of redraw. to make sure the input can be parse into a int.
     */
    public boolean isValid() {
        try {
            Integer.parseInt(getText());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // access about key----------------------------------------------------------
    public void replaceSelection(
            String prmStr) {
        if (prmStr != null) {
            if (prmStr.equals(".") || prmStr.equals("。")) {
                if (getText() == null || getText().length() < 1)
                    Toolkit.getDefaultToolkit().beep();
                else if (getNextJComponent() != null) {
                    try {
                        getDocument().insertString(getText().length(), ".", null);
                    } catch (Exception eBLE) {
                        // SUtility.instance().writeErrorLog(eBLE.toString());
                    }
                    getNextJComponent().grabFocus();
                }
            } else {
                try {
                    // int temInputNumber = Integer.parseInt(prmStr);
                    super.replaceSelection(prmStr);
                } catch (NumberFormatException e) {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }

    public void selectAll() {
        if (selectAllFlag)
            return;
        super.selectAll();
    }

    /** redefine this method to return a special Model. */
    protected Document createDefaultModel() {
        return new LimitIntTextDocument();
    }

    /*----------------variables-----------------*/
    JComponent lastJComponent;
    JComponent nextJComponent;
    Container container;
    private boolean selectAllFlag;

    public int getValue() {
        try {
            return Integer.parseInt(getText());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setLastJComponent(
            JComponent prmJComponent) {
        lastJComponent = prmJComponent;
    }

    public void setNextJComponent(
            JComponent prmJComponent) {
        nextJComponent = prmJComponent;
    }

    public void setContainer(
            Container prmContainer) {
        container = prmContainer;
    }

    public JComponent getLastJComponent() {
        return lastJComponent;
    }

    public JComponent getNextJComponent() {
        return nextJComponent;
    }

    public void setSelectAllFlag(
            boolean prmB) {
        setSelectAllFlagForward(prmB);
        setSelectAllFlagBackward(prmB);
    }

    public void setSelectAllFlagForward(
            boolean prmB) {
        selectAllFlag = prmB;
        if (getNextJComponent() != null && getNextJComponent() instanceof LimitedIntTextField)
            ((LimitedIntTextField) getNextJComponent()).setSelectAllFlagForward(prmB);
    }

    public void setSelectAllFlagBackward(
            boolean prmB) {
        selectAllFlag = prmB;
        if (getLastJComponent() != null && getLastJComponent() instanceof LimitedIntTextField)
            ((LimitedIntTextField) getLastJComponent()).setSelectAllFlagBackward(prmB);
    }

    private void init() {
        lastJComponent = null;
        nextJComponent = null;
    }
}

class LimitIntTextDocument extends PlainDocument {

    public void insertString(
            int offs,
            String str,
            AttributeSet a) throws BadLocationException {
        if (str == null)
            return;

        int value = str.charAt(0); // 输入的字符的Unicode
        if (!str.equals(".") && (value > 57 || value < 48)) // 判断如果输入的字符不是0-9的话就直接返回（参照微软）b92507
            return;

        if (str.equals(".")) {
            ((LimitedIntTextField) jCompFacade).getNextJComponent().grabFocus();
            return;
        }

        String oldStr = getText(0, getLength());
        String newStr = oldStr.substring(0, offs) + str + oldStr.substring(offs);
        try {
            int temInputNum = Integer.parseInt(newStr);
            if (temInputNum < limit && newStr.length() <= numberCount)
                super.insertString(offs, str, a);
            else {
                JComponent temComp = ((LimitedIntTextField) jCompFacade).getNextJComponent();
                int temKeep = Integer.parseInt(newStr.substring(0, numberCount));
                if (temKeep < limit) {
                    ((LimitedIntTextField) jCompFacade).setText(newStr.substring(0, numberCount));
                    if (temComp == null)
                        return;
                    else if (temComp instanceof LimitedIntTextField) {
                        ((LimitedIntTextField) temComp).setSelectAllFlag(true);
                        ((LimitedIntTextField) temComp).setText(newStr.substring(numberCount, newStr.length()));
                    }
                } else {
                    ((LimitedIntTextField) jCompFacade).setText(newStr.substring(0, numberCount - 1));
                    if (temComp == null)
                        return;
                    else if (temComp instanceof LimitedIntTextField) {
                        ((LimitedIntTextField) temComp).setSelectAllFlag(true);
                        ((LimitedIntTextField) temComp).setText(newStr.substring(numberCount - 1, newStr.length()));
                    }
                }
                temComp.grabFocus();
            }
        } catch (NumberFormatException e) {
            Toolkit.getDefaultToolkit().beep();
        }
    }

    /*--------variables------------*/
    private JComponent jCompFacade;
    private int limit = 256;
    private int numberCount = 3;

    void setJCompFacade(
            JComponent prmJComp) {
        jCompFacade = prmJComp;
    }

    void setLimit(
            int prmLimit) {
        limit = prmLimit;
        numberCount = Integer.toString(prmLimit).length();
    }
}
