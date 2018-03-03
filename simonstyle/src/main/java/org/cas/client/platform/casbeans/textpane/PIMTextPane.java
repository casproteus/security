package org.cas.client.platform.casbeans.textpane;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Hashtable;

import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.undo.UndoManager;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.ModelDBCons;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimmodel.PIMRecord;
import org.cas.client.resource.international.PaneConsts;

//import client.platform.mail.MailDefaultViews;
//import client.platform.mail.MailUtility;
//import client.platform.mail.dialog.MailFrame;

public class PIMTextPane extends JEditorPane implements HyperlinkListener, Releasable, FocusListener,
        UndoableEditListener, KeyListener, MouseListener {
    /**
     * 构造器
     * 
     * @param prmRecord
     *            邮件记录
     */
    public PIMTextPane(PIMRecord prmRecord, boolean prmIsEncypted, boolean canEdit) {
        setEditorKit(htmlEditorkit);
        setEditable(canEdit);
        if (canEdit) {
            setDocument(htmlDocument);
            // 先设置字体属性，使显示宋体。TODO:该写法是为了临时应付863版本，将来需要从从总控取字体，以适应多语言版本。
            MutableAttributeSet attr = new SimpleAttributeSet();
            StyleConstants.setFontFamily(attr, PaneConsts.SONG);
            htmlEditorkit.getInputAttributes().addAttributes(attr);
        }
        setCaret(caret);
        setSelectedTextColor(Color.white);

        addHyperlinkListener(this); // 实现超链接的监听
        addFocusListener(this); // 焦点监听器,当PIMTextPane获得焦点时,向其的一个静态实例赋值
        addKeyListener(this); // 键盘监听器,当Alt键按下时，PIMTextPane将不可编辑,此时可以激活hyperlink

        if (prmIsEncypted) {
            if (prmRecord != null) {
                setContents(prmRecord);
            } else {
                setText("<BODY>" + "<FONT size=3>对邮件解密时出错</FONT><BR><BR>" + "您不能阅读这封邮件。<BR><BR>"
                        + "<HR align=\"left\" width=\"450\" size=1 color=\"blue\"><BR><BR>" + "这可能是因为：<BR>" + "<UL>"
                        + "<LI>您可能丢失或删除了对此邮件进行加密的数字ID。" + "<LI>您可能将用来对此邮件进行加密的数字 ID 安装在了另一台计算机上。"
                        + "<LI>发件人可能是想把这封邮件发给别人。" + "<LI>这台计算机上没有安装必要的安全包。" + "</UL>" + "</BODY>");
            }
        } else if (prmRecord != null) // 因为setContents不允许参数为null,顾在此判断.
        {
            setContents(prmRecord);
        }
        // //===============暂时留着=============
        // sourceCode = sourceCode.replaceAll("Tahoma","宋体");
        // sourceCode = sourceCode.replaceAll("tahoma","宋体");
        // sourceCode = sourceCode.replaceAll("utf-8","gb2312");
        // sourceCode = sourceCode.replaceAll("Arial","宋体");
        // sourceCode = sourceCode.replaceAll("arial","宋体");
        // sourceCode = sourceCode.replaceAll("Comic Sans MS","宋体");
        // //============================
    }

    /**
     * 更新PIMTextPane显示新的Record的内容
     * 
     * @note:prmRecord 不可以为null!
     * @for: PIMPreviewPane;
     */
    public boolean setContents(
            PIMRecord prmRecord) {
        if (prmRecord == null) {
            sourceCode =
                    "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\"><HTML><HEAD></HEAD><BODY></BODY></HTML>";
        } else {
            Object tmpValue = prmRecord.getFieldValue(PIMPool.pool.getKey(ModelDBCons.COMMENT));
            if (tmpValue == null) {
                sourceCode = CASUtility.EMPTYSTR;
            } else {
                sourceCode = tmpValue.toString();
            }
            sourceCode = sourceCode.replaceFirst("<style>body", "<STYLE>");
            sourceCode = sourceCode.replaceFirst("<Style>Body", "<STYLE>");
            sourceCode = sourceCode.replaceFirst("<STYLE>BODY", "<STYLE>");// TODO 初步解决预览问题，以后要修正。
            // 内容太长就作预览处理 if(sourceCode.length() < 45000)
            // {
            // PIMPreviewPane.getInstance().showPatientNotice(false);
            // showTheContent();
            // return true;
            // }
            // else
            // {
            // PIMPreviewPane.getInstance().showPatientNotice(true);
            // return false;
            // }
        }
        showTheContent();

        // Canvas canvas = new Canvas();
        // Display display = Display.getDefault();
        // Shell shell = org.eclipse.swt.awt.SWT_AWT.new_Shell (display, canvas);
        // add(canvas);
        // // Shell shell = display.getActiveShell();
        // // Control c = shell.getDisplay().getFocusControl();
        // // shell.setLayout (new org.eclipse.swt.layout.FillLayout ());
        // display.asyncExec(new Runnable(){
        //
        // public void run() {
        // // TODO Auto-generated method stub
        // try {
        // File tmpFile = new File("d:\\a.htm");
        // if (tmpFile.exists())
        // {
        // tmpFile.delete();
        // }
        // RandomAccessFile file = new RandomAccessFile("d:\\a.htm","rw");
        // file.write(sourceCode.getBytes());
        // file.close();
        // String f = new File("d:\\a.htm").toURI().toString();
        // IBrowser b = BrowserManager.getInstance().createBrowser(false);
        // b.displayURL(f);
        //
        // } catch (FileNotFoundException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (IOException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // } catch (Exception e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        // }});
        return true;
    }

    // 用户选择一个大邮件时,会显示一个提示信息(其实是一个提示面板被设为可见),当用户说"我要看这个预览"时,
    // 调本方法,使内容显示出来.
    private void showTheContent() {
        setText(sourceCode);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setCaretPosition(0);
            }
        });
    }

    /**
     * 插入背景图片的方法
     * 
     * @param file
     *            图片的路径
     */
    public void insertBackground(
            File file) {
        String path = file.getAbsolutePath();
        path = path.replace('\\', CustOpts.BIAS);
        String text = getText();
        String newTag = "<body background=\"".concat("file:///").concat(path).concat("\"").concat(">");
        text = text.replaceFirst(oldTag, newTag);
        if (htmlDocument.getLength() <= 1) {
            String mark = CASUtility.getEndMark(text, "<body");
            text = text.replaceFirst(mark, mark.concat("<P></P>"));
        }
        setText(text);
        setBodyTag(newTag);
    }

    /**
     * 插入背景色的方法
     * 
     * @param color
     *            背景色
     */
    public void insertBKColor(
            Color color) {
        String text = getText();
        String bkColor = Integer.toHexString(color.getRGB());
        bkColor = bkColor.replaceFirst("ff", "");
        String newTag = "<body bgcolor=\"".concat("#").concat(bkColor).concat("\"").concat(">");
        text = text.replaceFirst(oldTag, newTag);
        if (htmlDocument.getLength() <= 1) {
            String mark = CASUtility.getEndMark(text, "<body");
            text = text.replaceFirst(mark, mark.concat("<P></P>"));
        }
        setText(text);
        setBodyTag(newTag);
    }

    /**
     * 插入图片的方法
     * 
     * @param file
     *            图片的路径
     */
    public void insertIcon(
            File file) {
        MutableAttributeSet inputAttributes = getInputAttributes();
        inputAttributes.removeAttributes(inputAttributes);
        // inputAttributes.addAttribute(StyleConstants.NameAttribute,HTML.Tag.IMG);
        inputAttributes.addAttribute(StyleConstants.NameAttribute, "IMG");
        String url = "file:///".concat(file.getAbsolutePath());
        inputAttributes.addAttribute(HTML.Attribute.SRC, url);
        replaceSelection(CASUtility.SPACE, inputAttributes.copyAttributes());
        inputAttributes.removeAttributes(inputAttributes);
    }

    /**
     * 插入超链接的方法
     * 
     * @param url
     *            超链接地址
     */
    public void insertLink(
            String url) {
        MutableAttributeSet inputAttributes = (MutableAttributeSet) getInputAttributes().copyAttributes();
        inputAttributes.removeAttributes(inputAttributes);
        inputAttributes.addAttribute(HTML.Attribute.HREF, url);
        inputAttributes.addAttribute(HTML.Tag.A, inputAttributes);
        StyleConstants.setForeground(inputAttributes, java.awt.Color.blue);
        StyleConstants.setUnderline(inputAttributes, true);
        setCharacterAttributes(this, inputAttributes.copyAttributes(), false);
        addMouseListener(this);
    }

    /**
     * 插入水平线方法
     */
    public void insertLine() {
        MutableAttributeSet inputAttributes = getInputAttributes();
        inputAttributes.removeAttributes(inputAttributes);
        inputAttributes.addAttribute(StyleConstants.NameAttribute, HTML.Tag.HR);
        replaceSelection(CASUtility.SPACE, inputAttributes);
    }

    /**
     * 返回当前的属性集
     * 
     * @return getInputAttributes()
     */
    public MutableAttributeSet getInputAttributes() {
        return getHTMLEditorKit().getInputAttributes();
    }

    /**
     * 返回编辑器工具包
     * 
     * @return htmlEditorkit
     */
    public HTMLEditorKit getHTMLEditorKit() {
        return htmlEditorkit;
    }

    /**
     * 返回当前的文档
     * 
     * @return htmlDocument
     */
    public HTMLDocument getHTMLDocument() {
        return htmlDocument;
    }

    /**
     * 替换选中文字的方法
     * 
     * @param content
     *            用来替换的文字
     */
    public void replaceSelection(
            String content) {
        replaceSelection(content, null);
    }

    /**
     * 设置背景标识
     */
    public void setBodyTag(
            String newTag) {
        this.oldTag = newTag;
    }

    /**
     * 替换选中文字的方法
     * 
     * @param content
     *            用来替换的文字
     * @param attr
     *            用来替换的属性
     */
    public void replaceSelection(
            String content,
            AttributeSet attr) {
        if (!isEditable()) {
            UIManager.getLookAndFeel().provideErrorFeedback(PIMTextPane.this);
            return;
        }
        if (htmlDocument != null) {
            try {
                Caret caret = getCaret();
                int p0 = Math.min(caret.getDot(), caret.getMark());
                int p1 = Math.max(caret.getDot(), caret.getMark());
                if (p0 != p1) {
                    htmlDocument.remove(p0, p1 - p0);
                }
                if (content != null && content.length() > 0) {
                    htmlDocument.insertString(p0, content, attr != null ? attr : getInputAttributes());
                }
            } catch (BadLocationException e) {
                UIManager.getLookAndFeel().provideErrorFeedback(PIMTextPane.this);
            }
        }
    }

    /**
     * 设置文字属性
     * 
     * @param editor
     *            PIMTextPane
     * @param attr
     *            AttributeSet
     * @param replace
     *            boolean 使否要替换
     */
    protected final void setCharacterAttributes(
            PIMTextPane editor,
            AttributeSet attr,
            boolean replace) {
        int p0 = editor.getSelectionStart();
        int p1 = editor.getSelectionEnd();

        if (p0 != p1) {
            HTMLDocument doc = getHTMLDocument();
            doc.setCharacterAttributes(p0, p1 - p0, attr, replace);
        }

        MutableAttributeSet inputAttributes = getInputAttributes();
        if (replace) {
            inputAttributes.removeAttributes(inputAttributes);
        }
        inputAttributes.addAttributes(attr);

    }

    /**
     * Fetch the parser to use for reading HTML streams. This can be reimplemented to provide a different parser. The
     * default implementation is loaded dynamically to avoid the overhead of loading the default parser if it's not
     * used. The default parser is the HotJava parser using an HTML 3.2 DTD.
     */
    protected HTMLEditorKit.Parser getParser() {
        if (defaultParser == null) {
            try {
                Class c = Class.forName("javax.swing.text.html.parser.ParserDelegator");
                defaultParser = (HTMLEditorKit.Parser) c.newInstance();
            } catch (Throwable e) {
            }
        }
        return defaultParser;
    }

    /**
     * Called when a hypertext link is updated. 如果超连接监听器存在，则将地址交由监听器处理。
     * 
     * @param e
     *            the event responsible for the update
     */
    public void hyperlinkUpdate(
            HyperlinkEvent e) {
        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            String tmpUrl = e.getURL().toString().trim();
            if (tmpUrl.startsWith("mailto:")) { // 点击邮件链接
                if (hasMailListner)
                    CASControl.ctrl.getMainPane().getMailListner().setMailAddress("\"\"<" + tmpUrl.substring(7) + '>');

            }
            // else // 取得当前超链接的地址 并调用当前的操作系统 的Web浏览器显示
            // execHttp(tmpUrl);
        }
    }

    /**
     * Returns the PIMTextPane that most recently had focus. The returned value may currently have focus.
     */
    public static final PIMTextPane getFocusedComponent() {
        return focusedComponent;
    }

    /**
     * @return undoManager UndoManager
     */
    public UndoManager getUndoManager() {
        return undoManager;
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        removeHyperlinkListener(this);
        removeFocusListener(this);
        removeKeyListener(this);
        htmlEditorkit = null;
        htmlDocument = null;
    }

    /**
     * Invoked when a component gains the keyboard focus.
     */
    public void focusGained(
            FocusEvent fe) {
        focusedComponent = (PIMTextPane) fe.getSource();
        focusedComponent.getHTMLDocument().addUndoableEditListener(this);
    }

    /**
     * Invoked when a component loses the keyboard focus.
     */
    public void focusLost(
            FocusEvent fe) {
        focusedComponent = (PIMTextPane) fe.getSource();
    }

    /**
     * An undoable edit happened
     */
    public void undoableEditHappened(
            UndoableEditEvent e) {
        undoManager.addEdit(e.getEdit());
    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
     * pressed event.
     */
    public void keyPressed(
            KeyEvent e) {
        if (e.isAltDown()) {
            setEditable(false);
        } else if (e.getKeyCode() == KeyEvent.VK_TAB) {
            try {
                if (getCaretPosition() == htmlDocument.getLength()) {
                    Element element = htmlDocument.getCharacterElement(getCaretPosition());
                    // HTMLDocument.RunElement elem = new
                    // HTMLDocument.RunElement(element.getParentElement(),getInputAttributes(),getCaretPosition(),getCaretPosition()+1);
                    htmlDocument.setParser(getParser());
                    htmlDocument.setOuterHTML(element,
                            "&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;");
                }
            } catch (BadLocationException be) {
                be.printStackTrace();
            } catch (java.io.IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
     * released event.
     */
    public void keyReleased(
            KeyEvent e) {
        setEditable(true);
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
     * event.
     */
    public void keyTyped(
            KeyEvent e) {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on a component.
     */
    public void mouseClicked(
            MouseEvent e) {
        if (e.getClickCount() <= 1) {
            MutableAttributeSet inputAttributes = getInputAttributes();
            inputAttributes.removeAttribute(HTML.Tag.DIV);
            inputAttributes.removeAttribute(HTML.Tag.A);
            inputAttributes.removeAttribute(StyleConstants.Underline);
            inputAttributes.removeAttribute(StyleConstants.Foreground);
        }
    }

    /**
     * Invoked when the mouse enters a component.
     */
    public void mouseEntered(
            MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     */
    public void mouseExited(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    public void mousePressed(
            MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     */
    public void mouseReleased(
            MouseEvent e) {
    }

    private String sourceCode;
    // 因为日记等应用中有可能同时出现两个以上的PIMTextPane,所以有必要知道任意时刻的公共的
    // 工具条按钮操作事件应该作用到哪一个PIMTextPane上，比如设置字体/Undo/Redo等。
    private static PIMTextPane focusedComponent;
    private UndoManager undoManager = new UndoManager();
    private HTMLEditorKit htmlEditorkit = new HTMLEditorKit();
    private HTMLDocument htmlDocument = new HTMLDocument();
    private BoxHighlightingCaret caret = new BoxHighlightingCaret();
    private static HTMLEditorKit.Parser defaultParser = null;
    private String oldTag = "<body>";
    private boolean hasMailListner;

    public void setMailListener(
            boolean prmHasMailListner) {
        hasMailListner = prmHasMailListner;
    }
}
