package org.cas.client.platform.cascontrol.dialog.customizeview;

import javax.swing.JPanel;

/**
 * Invoked when an item has been selected or deselected. The code written for this method performs the operations that
 * need to occur when an item is selected (or deselected). paint all font attributes here The logic thought is very
 * complicated .It contains 21 attributes.
 */

class PreviewPanel extends JPanel// implements FontConstants
{
    // //the width of the preview panel
    // //private final int rectWidth = 371;
    // private int rectWidth = 371;
    // //the height of the preview panel
    // private int rectHeight = 61;
    // //private final int rectHeight = 61;
    // //distance the x start point to the mostleft of the preview panel
    // private final int RECT_X_LIMIT = 1; //0 = 0;
    // //distance the y start point to the mostbottom of the preview panel
    // //private final int rectYLimit = 50; //rectHeight = 61;
    // private int rectYLimit = rectHeight - 11; //rectHeight = 61;
    // //the ratio of changing character spacing; 18 pixels per 1 pt/13
    // private final float SPACING_SCALE = 65/100f;
    // //the ratio of changing character POSITION;
    // private final float POSITION_SLOPE = 12/10f;
    // //client selects small caps item
    // private final float SMALL_SCALE = 8 / 10f;
    //
    // /** Creates a previewPanel with font attributes */
    // public PreviewPanel()
    // {
    // setOpaque(true);
    // setBorder(new javax.swing.border.LineBorder(Color.black));
    // setBackground(Color.white);
    // isPresentation = false;
    // initText();
    // }
    //
    // /** Creates a previewPanel for Presentation
    // * @param type 类型
    // * @param scheme 颜色表
    // */
    // public PreviewPanel(int type, ColorScheme scheme)
    // {
    // this.type = type;
    // this.scheme = scheme;
    // setOpaque(true);
    // setBorder(new javax.swing.border.LineBorder(Color.black));
    // setBackground(Color.white);
    // isPresentation = true;
    // initText();
    // }
    //
    // /**
    // * @called by: SetFormatDialog
    // * @param fe 字体属性
    // */
    // public void setFontAttribute(FontAttribute fe)
    // {
    // fa = fe;
    // }
    //
    // /** for the size of Panel.
    // * @param width 宽度
    // * @param height 高度
    // */
    // public void setPanelSize(int width, int height)
    // {
    // rectWidth = width;
    // rectHeight = height;
    // }
    //
    // /*
    // * Fetch different text via version
    // */
    // private void initText()
    // {
    // strLength = PREVIEW_TEXT.length();
    // }
    //
    // /** main paint method
    // * Note: understand logic thought
    // * paint strikethrough & double strikethrough & underline & superscript
    // * Paint superscript font & subscript font
    // * paint shadow & outline & emboss & engrave
    // * @param g 图形设备
    // */
    // public void paint(Graphics g)
    // {
    // super.paint(g);
    // initFontAttribute();
    // /* 为了解决调整“字符间距”中的缩放比例时预览面板能在1.4中正确显示的问题
    // * 为解决这个问题，绘制时先把 g 在 x 方向拉伸；这个类中很多地方都用了“ / charScale”来调整 x 的坐标
    // * king, 2002-6-18
    // * -------------------- added begin ---------------
    // */
    // if (charScale < 0.2f) //调整缩放比例，和 MS 一样
    // {
    // charScale = 0.2f;
    // }
    // Graphics2D g2d = (Graphics2D)g;
    // g2d.scale(charScale, 1);
    //
    // //用于这个坐标系统的定位是写死的,而我不知这位仁兄的算法
    // g2d.translate(-65,-10);
    // //added over --------------------------------------
    // if (FontPanel.version == 0)
    // {
    // mainPaint(g);
    // }
    // else
    // {
    // paintcn(g);
    // }
    // //Fix Bug#14020 at Feb-28-2002
    // g.setColor(Color.black);
    // /*
    // *把这 2 句注释掉了，否则，要用“ / charScale”调整 x 的坐标，但转化成 int 时有时差一个象素，很难看
    // *king, 2002-6-18
    // */
    // g2d.scale(1 / charScale, 1);
    // g2d.drawLine(0, 0, rectWidth, 0);
    // g2d.drawLine(rectWidth - 1, 0, rectWidth - 1, rectHeight -1);
    // //注释结束----------------------
    // }
    //
    // /**
    // * draw single strikethrough & double strikethrough & underline &
    // * superscript & subscript & shadow & outline & emboss & engrave
    // * draw small caps & all caps & character scale & spacing & position
    // */
    // private void mainPaint(Graphics g)
    // {
    // //Graphics2D g2 = (Graphics2D) g;
    // isWhite = fontColor.equals(Color.white) ? true : false;
    // g.setColor(fontColor = isWhite ? Color.black : fontColor);
    // frc = new FontRenderContext(null, false, false);
    // //must define a middle variable, to save
    // int roundSize = Math.round(fontSize * 7 / 10.0f);
    // int midFontSize = !(superscript || subscript) ? fontSize : roundSize;
    // //font = new Font(fontName, fontStyle, midFontSize);
    // font = FontManager.getFont(fontName, fontStyle, midFontSize);
    // fm = getFontMetrics(font);
    // float scale = fm.getAscent() / 12f;
    //
    // float y1 = (rectHeight + fm.getAscent()) / 2.0f;
    // //decide top limit, calculate string height
    // //when process character position & superscript or subscript
    //
    // //float y = getStringHeight(scale, y1);//king commented, 2002-6-15
    // //king added begin -------- 2002-6-15,原因见CharacterPanel.adjuestPosition的注释
    // float y;
    // if (! CharacterPanel.adjuestPosition && lastY != 50)
    // {
    // y = lastY;
    // }
    // else
    // {
    // y = getStringHeight(scale, y1);
    // lastY = y;
    // }
    // //king added end ----- 2002-6-15----------------------------------------------
    //
    // //calculate string width
    // //when process spacing or small caps or all caps or character scale
    // float x1 = getStringWidth(g, midFontSize);
    //
    // float x = (rectWidth - x1) / 2.0f;
    // x /= charScale; //king, 2002-6-18，原因见“paint”方法中的注释
    // float increment = 0;
    // String temp = null;
    // for (int i = 0; i < strLength ; i++)
    // {
    // //for tradional Chinese version at Jan-10-2002
    // //temp = previewText.substring(i, i + 1);
    // temp = PREVIEW_TEXT.substring(i, i + 1);
    // float tempSize = midFontSize;
    // if (smallcaps || allcaps)
    // {
    // temp = temp.toUpperCase();
    // //for tradional Chinese version at Jan-10-2002
    // //if (smallcaps && previewText.charAt(i) >= 'a' && previewText.charAt(i) <= 'z')
    // if (smallcaps && PREVIEW_TEXT.charAt(i) >= 'a' && PREVIEW_TEXT.charAt(i) <= 'z')
    // {
    // tempSize = midFontSize * SMALL_SCALE;
    // }
    // }
    // //JDK1.4.0解决了FontStyle的Bug。
    // font = font.deriveFont(fontStyle, tempSize);
    // //font = font.deriveFont(fontStyle, tempSize + random.nextFloat() / 1000);
    // //Get the preview text
    // AttributedString ats = new AttributedString(temp);
    // ats.addAttribute(TextAttribute.FONT, font);
    // AttributedCharacterIterator iter = ats.getIterator();
    //
    // frc = getCharScale(); //PROCESS character scale
    //
    // layout = new TextLayout(iter, frc);
    // fm = getFontMetrics(font);
    // x += increment; //PROCESS character scale
    // //Note :Last increment don't add to x
    // //process character spacing
    // increment = (fm.stringWidth(temp) + getMidSpacing()) * charScale;
    // increment /= charScale; //king, 2002-6-18，原因见“paint”方法中的注释
    // //get max value
    // x = Math.max(RECT_X_LIMIT, x);
    //
    // //process word only underline
    // wordOnlyUnderline(g, x, y, scale, i, increment);
    //
    // //process shadow & outline & emboss & engrave & outline == false
    // layoutDraw(layout, g, x, y, scale);
    // }
    //
    // // get the start point of drawing Line
    // x = (rectWidth - x1) / 2;
    // x = Math.max(RECT_X_LIMIT, x);
    //
    // //draw underline & singel & double strikethrough
    // paintThreeLines(g, (int)x, (int)y, scale);
    //
    // if (x > RECT_X_LIMIT)
    // {
    // //float y1 = (rectHeight + fm.getAscent()) / 2.0f;
    // g.setColor(Color.black);
    // /*
    // *king commented, 2002-6-18，原因见“paint”方法中的注释
    // */
    // //draw left line1
    // //g.drawLine(0, (int)y1, (int)(x / 2), (int)y1);
    // //draw right line
    // //g.drawLine((int)(rectWidth - x / 2), (int)y1, rectWidth, (int)y1);
    // //注释结束 --------- king
    // //king added ---- begin ----, 2002-6-18，原因见“paint”方法中的注释
    // g.drawLine(0, (int)y1, (int)(x / 2 / charScale), (int)y1);
    // g.drawLine((int)((rectWidth - x / 2) / charScale) , (int)y1, (int)(rectWidth / charScale), (int)y1);
    // //king added over --------------
    // return;
    // }
    // }
    //
    // /**
    // * Chinese preview
    // */
    // private void paintcn(Graphics g)
    // {
    // isWhite = fontColor.equals(Color.white) ? true : false;
    // g.setColor(fontColor = isWhite ? Color.black : fontColor);
    // frc = new FontRenderContext(null, false, false);
    // int roundSize = Math.round(fontSize * 7 / 10.0f);
    // int midFontSize = !(superscript || subscript) ? fontSize : roundSize;
    // //for the optimization of Font at Feb-21-2002
    // //font = new Font(fontName, fontStyle, midFontSize);
    // //fontcn = new Font(asianName, fontStyle, midFontSize);
    // font = FontManager.getFont(fontName, fontStyle, midFontSize);
    // fontcn = FontManager.getFont(asianName, fontStyle, midFontSize);
    //
    // fm = getFontMetrics(font);
    // fmcn = getFontMetrics(fontcn);
    // int ascent = fm.getAscent();
    // int ascentcn = fmcn.getAscent();
    // float y1 = (rectHeight + ascent) / 2.0f;
    // float scale = ascent / 12f;
    // if (ascent < ascentcn)
    // {
    // y1 = (rectHeight + ascentcn) / 2.0f;
    // scale = ascentcn / 12f;
    // }
    // //float y = getStringHeight(scale, y1); //commented by king, 2002-6-15
    // //king added begin -------- 2002-6-15,原因见CharacterPanel.adjuestPosition的注释
    // float y;
    // if (! CharacterPanel.adjuestPosition && lastY != 50)
    // {
    // y = lastY;
    // }
    // else
    // {
    // y = getStringHeight(scale, y1);
    // lastY = y;
    // }
    // //king added end ----- 2002-6-15----------------------------------------------
    //
    // float x1 = getStringWidthcn(g, midFontSize);
    // float x = (rectWidth - x1) / 2.0f;
    // x /= charScale; //king, 2002-6-18，原因见“paint”方法中的注释
    // float increment = 0;
    // String temp = null;
    // AttributedString ats = null;
    // AttributedCharacterIterator iter = null;
    // for (int i = 0; i < strLength ; i++)
    // {
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //temp = previewText.substring(i, i + 1);
    // temp = PREVIEW_TEXT.substring(i, i + 1);
    // float tempSize = midFontSize;
    // //if (fa.isAsia(previewText.charAt(i)))
    // if (FontAttribute.isAsia(PREVIEW_TEXT.charAt(i)))
    // {
    // fontcn = fontcn.deriveFont(fontStyle, tempSize);
    // //fontcn = fontcn.deriveFont(fontStyle, tempSize + random.nextFloat() / 1000);
    // //Get the preview text
    // ats = new AttributedString(temp);
    // ats.addAttribute(TextAttribute.FONT, fontcn);
    // iter = ats.getIterator();
    // frc = getCharScale(); //PROCESS character scale
    // layout = new TextLayout(iter, frc);
    // fmcn = getFontMetrics(fontcn);
    // x += increment; //PROCESS character scale
    // //Note :Last increment don't add to x
    // //process character spacing
    // increment = (fmcn.stringWidth(temp) + getMidSpacingcn()) * charScale;
    // }
    // else
    // {
    // if (smallcaps || allcaps)
    // {
    // temp = temp.toUpperCase();
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //if (smallcaps && previewText.charAt(i) >= 'a' && previewText.charAt(i) <= 'z')
    // if (smallcaps && PREVIEW_TEXT.charAt(i) >= 'a' && PREVIEW_TEXT.charAt(i) <= 'z')
    // {
    // tempSize = midFontSize * SMALL_SCALE;
    // }
    // }
    // font = font.deriveFont(fontStyle, tempSize);
    // //font = font.deriveFont(fontStyle, tempSize + random.nextFloat() / 1000);
    // //Get the preview text
    // ats = new AttributedString(temp);
    // ats.addAttribute(TextAttribute.FONT, font);
    // iter = ats.getIterator();
    //
    // frc = getCharScale(); //PROCESS character scale
    //
    // layout = new TextLayout(iter, frc);
    // fm = getFontMetrics(font);
    // x += increment; //PROCESS character scale
    // increment = (fm.stringWidth(temp) + getMidSpacingcn()) * charScale;
    // }
    // //get max value
    // x = Math.max(RECT_X_LIMIT, x);
    // increment /= charScale; //king, 2002-6-18，原因见“paint”方法中的注释
    // //process word only underline
    // wordOnlyUnderline(g, x, y, scale, i, increment);
    // //process shadow & outline & emboss & engrave & outline == false
    // layoutDraw(layout, g, x, y, scale);
    // }
    //
    // // get the start point of drawing Line
    // x = (rectWidth - x1) / 2;
    // x = Math.max(RECT_X_LIMIT, x);
    //
    // //draw underline & singel & double strikethrough
    // paintThreeLines(g, (int)x, (int)y, scale);
    //
    // if (x > RECT_X_LIMIT)
    // {
    // //float y1 = (rectHeight + fm.getAscent()) / 2.0f;
    // g.setColor(Color.black);
    // /*
    // *king commented, 2002-6-18，原因见“paint”方法中的注释
    // */
    // //draw left line1
    // //g.drawLine(0, (int)y1, (int)(x / 2), (int)y1);
    // //draw right line
    // //g.drawLine((int)(rectWidth - x / 2), (int)y1, rectWidth, (int)y1);
    // //king 注释结束 ---------------
    // //king added --- begin ----, 2002-6-18，原因见“paint”方法中的注释
    // g.drawLine(0, (int)y1, (int)(x / 2 / charScale), (int)y1);
    // g.drawLine((int)((rectWidth - x / 2) / charScale), (int)y1, (int)(rectWidth / charScale), (int)y1);
    // //king added over ---------
    // return;
    // }
    // }
    // /**
    // * process shadow & outline & emboss & engrave & outline == false
    // * @param layout, the object of TextLayout
    // * @param g2, the object of Graphics2D
    // * @param x, the start point
    // * @param y, the start point coordinate y
    // * @param scale, changing scale equals getStrikethroughThickness()
    // */
    // private void layoutDraw(TextLayout layout, Graphics g, float x, float y,
    // float scale)
    // {
    // Graphics2D g2 = (Graphics2D)g;
    // if (shadow)
    // {
    // Color oldColor = g2.getColor();
    // g2.setColor(Color.lightGray);
    // //have a difference when the number is put ahead and behind
    // //float midScale = 9 / 10f * scale;
    // float midScale = scale * .9f < 1f ? 1f : scale * 0.9f;
    // layout.draw(g2, x + midScale, y + midScale);
    // g.setColor(oldColor);
    // }
    //
    // //if (!outline)
    // //{
    // //g2.setColor(!(emboss || engrave) ? fontColor : Color.lightGray);
    // Color oldColor = g2.getColor();
    // g2.setColor(emboss || engrave ? Color.lightGray : fontColor);
    // layout.draw(g2, x, y);
    // g2.setColor(oldColor);
    // //}
    // if (emboss || engrave) // && !shadow && !outline)
    // {
    // //g2.setColor(fontColor);
    // //BasicStroke bs = (BasicStroke)g2.getStroke();
    // //float lw = bs.getLineWidth();
    // //g2.setColor((fontColor == Color.black && isTitle) ? Color.white : fontColor);
    // //float midLw = emboss ? -lw : lw; //draw emboss
    // Color haha = g2.getColor();
    // if (isWhite)
    // {
    // g2.setColor(Color.white);
    // }
    // float midLw = emboss ? -1 : 1;
    // layout.draw(g2, (float)(x + midLw), (float)(y + midLw));
    // g2.setColor(haha);
    // }
    // /*if (outline)
    // {
    // Shape shape = layout.getOutline(AffineTransform.getTranslateInstance(x, y));
    // g2.setColor(Color.white);
    // layout.draw(g2, x, y);
    // g2.setColor(fontColor);
    // g2.draw(shape);
    // }*/
    // }
    //
    // /**
    // * get the start point, coordinate y
    // * @param y, the start point
    // * @param scale, changing scale equals getStrikethroughThickness()
    // */
    // private float getStringHeight(float scale, float y1)
    // {
    // //float y1 = (rectHeight + fm.getAscent()) / 2.0f;
    // float y = Math.min(rectYLimit, y1);
    // //process character position
    // y = y - position * POSITION_SLOPE;
    //
    // //determine if sup or sub is true or false
    // if (superscript || subscript)
    // {
    // float midScale = scale * 3.0f;
    // //draw supscript & subscript
    // y = superscript ? y - midScale : y + midScale;
    // }
    //
    // return y;
    // }
    //
    // /**
    // * draw word only underline
    // * it is different that word only compares to other style underline.
    // * @param g2, Graphics2D
    // * @parm x, the start point
    // * @param y, the start point
    // * @param scale, changing scale equals getStrikethroughThickness
    // * @parm i, 'i' is the sequence variable
    // * @parm increment, one character width
    // */
    // private void wordOnlyUnderline(Graphics g, float x, float y, float scale,
    // int i, float increment)
    // {
    // if (underlineType == WORD_UNDERLINE)
    // {
    // //stroke = new BasicStroke(scale * 8 / 10);
    // Graphics2D g2 = (Graphics2D)g;
    // //g2.setStroke(stroke);
    // Stroke olds = g2.getStroke();
    // Color oldColor = g2.getColor();
    // //float h = y + 2.0f * scale;
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //boolean isEquals = previewText.substring(i, i + 1).equals(PIMUtility.SPACE);
    // boolean isEquals = PREVIEW_TEXT.substring(i, i + 1).equals(PIMUtility.SPACE);
    // g2.setColor(isEquals ? Color.white : underlineColor);
    // //int thickness = Math.round(scale * 8 / 10f);
    // /*int thickness = scale >= 1.0f ? Math.round(scale) : 1;
    // for (int k = 0; k < thickness; k++)
    // {
    // g.drawLine((int)x, (int)h + k, (int)(x + increment), (int)h + k);
    // }
    // g.setColor(oldColor);*/
    // //resetLineWidth(g);
    // float singleWidth = scale * 0.6F;
    // stroke = new BasicStroke(singleWidth);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * scale);
    // g2.drawLine((int)x, (int)y, (int)(x + increment), (int)y);
    // //g2.drawLine((int)x, (int)h + k, (int)(x + increment), (int)h + k);
    // g.setColor(oldColor);
    // g2.setStroke(olds);
    // }
    // }
    //
    // /**
    // * draw underline
    // * There are 17 styles underline totally
    // * 1f is width, CAP_BUTT is line shape
    // * JOIN_MITER 10f is , BasicStroke(float width, int cap, int join,
    // * float milterlimit, float []dash, float-phase);
    // * float [] dash, float - phase, 2f is opaque, 2f is not opque, 5f is opaque,
    // * 2f is not opque, 2f is opaque, 2f is not opaque
    // * @param g2,the object of Graphics2D
    // * @param x, the start point
    // * @param y, the start point
    // * @param thick, changing scale
    // */
    // private void drawUnderline(Graphics g, int x, int y, float thick)
    // {
    // GeneralPath gp = new GeneralPath();
    // Graphics2D g2 = (Graphics2D)g;
    // Stroke oldStroke = g2.getStroke();
    // Color oldColor = g2.getColor();
    // g2.setColor(underlineColor);
    // float singleWidth = thick * 0.6F;
    // /*if (singleWidth < 1F)
    // {
    // singleWidth = 1F;
    // }*/
    // float doubleWidth = 2 * 0.58F * thick;
    // if (doubleWidth < 1.5F)
    // {
    // doubleWidth = 1.5F;
    // }
    // switch (underlineType)
    // {
    // /**
    // * case 1: is put in the main method
    // * It is the very difficult in styles of underline
    // */
    // case 2:
    // //stroke = new BasicStroke(thick * 8 / 10);
    // stroke = new BasicStroke(singleWidth);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case DOUBLE_UNDERLINE: //3
    // stroke = new BasicStroke(singleWidth);
    // g2.setStroke(stroke);
    // //down up line
    // g2.drawLine(x, y + (int)thick, rectWidth - x, y + (int)thick);
    // //paint down line
    // y = y + (int)(2 * thick + 1);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case THICK_UNDERLINE: //4
    // stroke = new BasicStroke(doubleWidth);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case 5:
    // stroke = new BasicStroke(singleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{2f, 2f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case DOTTED_HEAVY_UNDERLINE: //6
    // stroke = new BasicStroke(doubleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{27f/10, 27/10f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case 7:
    // stroke = new BasicStroke(singleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{8f, 4f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case DASHED_HEAVY_UNDERLINE: //8:
    // stroke = new BasicStroke(doubleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{8f, 4f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case 9:
    // stroke = new BasicStroke(singleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{15f, 8f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case DASHED_LONG_HEAVY_UNDERLINE: //10:
    // stroke = new BasicStroke(doubleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{15f, 8f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case 11:
    // stroke = new BasicStroke(singleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{2f, 2f, 8f, 2f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case DOT_DASH_HEAVY_UNDERLINE: //12:
    // stroke = new BasicStroke(doubleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{2f, 2f, 8f, 2f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case 13:
    // stroke = new BasicStroke(singleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{2f, 2f, 2f, 2f, 7f, 2f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case DOT_DOT_DASH_HEAVY_UNDERLINE: //14:
    // stroke = new BasicStroke(doubleWidth, BasicStroke.CAP_BUTT,
    // BasicStroke.JOIN_MITER, 10f, new float[]{2f, 2f, 2f, 2f, 7f, 2f}, 0f);
    // g2.setStroke(stroke);
    // y = y + (int)(2 * thick);
    // g2.drawLine(x, y, rectWidth - x, y);
    // break;
    //
    // case 15:
    // //x+fm.stringWidth() is end point == rectWidth - x
    // stroke = new BasicStroke(1F);
    // g2.setStroke(stroke);
    // //g2.setStroke(1F);
    // float x15 = rectWidth - x - thick;
    // gp.moveTo(x, y + 2 * thick); //paint start point
    // while (x < x15)
    // {
    // gp.lineTo(x + 3, y + thick);
    // gp.lineTo(x + 6, y + 2 * thick);
    // x = x + 6;
    // }
    // g2.draw(gp);
    // break;
    //
    // case 16:
    // stroke = new BasicStroke(2F);
    // g2.setStroke(stroke);
    // //g2.setStroke(2F);
    // //x+fm.stringWidth() is end point == rectWidth - x
    // float x16 = rectWidth - x - thick;
    // int add = 3;
    // float ah0 = thick;
    // float ah1 = 2 * ah0;
    // if (ah0 <= 1F)
    // {
    // ah0 = 1F;
    // ah1 = 3F;
    // }
    // gp.moveTo(x, y + ah1); //paint start point
    // while (x < x16)
    // {
    // gp.lineTo(x + add, y + ah0);
    // if (x + add >= x16)
    // {
    // break;
    // }
    // gp.lineTo(x + 2 * add, y + ah1);
    // x += 2 * add;
    // }
    // g2.draw(gp);
    // break;
    //
    // case 17:
    // stroke = new BasicStroke(1F);
    // g2.setStroke(stroke);
    // float skip = 2f;
    // float dskip = 2 * skip;
    // float ay = thick; // <= 0.5f ? 0.5f : thick;
    // float day = 2 * ay;
    // float tay = fontSize >= 8 ? 3 * ay : 2f; //font size equals 8 that I test it
    // float x0 = x;
    // //x+fm.stringWidth() is end point == rectWidth - x
    // //Because of mistake, subtract thick
    // float x17 = rectWidth - x - thick;
    // //gp.moveTo(x0, y + 2 * thick); //paint start point
    // gp.moveTo(x0, y + day); //paint start point
    // while (x0 < x17)
    // {
    // //gp.lineTo(x0 + skip, y + thick);
    // gp.lineTo(x0 + skip, y + ay);
    // if (x0 + skip >= x17)
    // {
    // break;
    // }
    // //gp.lineTo(x0 + dskip, y + 2 * thick);
    // gp.lineTo(x0 + dskip, y + day);
    // x0 = x0 + dskip;
    // }
    // //g2.draw(gp);
    //
    // //paint down line
    // //x+fm.stringWidth() is end point == rectWidth - x
    // float dx = x;
    // float x18 = rectWidth - x - thick;
    // float tempy = y + skip;
    // //gp.moveTo(dx, y + 3 * thick); //paint start point
    // gp.moveTo(dx, tempy + tay); //paint start point
    // while (dx < x18)
    // {
    // //gp.lineTo(dx + skip, y + 2 * thick);
    // gp.lineTo(dx + skip, tempy + day);
    // if (dx + skip >= x18)
    // {
    // break;
    // }
    // //gp.lineTo(dx + dskip, y + 3 * thick);
    // gp.lineTo(dx + dskip, tempy + tay);
    // dx = dx + dskip;
    // }
    // g2.draw(gp);
    // break;
    //
    // default:
    // break;
    // }
    // g2.setColor(oldColor);
    // //resetLineWidth(g2);
    // g2.setStroke(oldStroke);
    // }
    //
    // /**
    // * getStrikethroughOffset() == -(getAscent() / 2.0F)
    // * @param g2,the object of Graphics2D
    // * @param x, the start point
    // * @param y, the start point
    // * @param scale, changing scale
    // */
    // private void drawStrikethrough(Graphics g, int x, int y, float scale)
    // {
    // //g.setColor(fontColor);
    // //stroke = new BasicStroke(scale);
    // //g2.setStroke(stroke);
    // int h = (int)(y - scale * 3.5f);
    // int thickness = scale >= 1.0f ? Math.round(scale) : 1;
    // for (int i = 0; i < thickness; i++)
    // {
    // g.drawLine(x, h + i, rectWidth - x, h + i);
    // }
    //
    // //resetLineWidth(g2);
    // }
    //
    // /**
    // * scale = lm.getStrikethroughThickness() == (getAscent() / 12f)
    // * y - fm.getAscent() / 2 == y - scale * 12f / 2
    // * @param g2,the object of Graphics2D
    // * @param x, the start point
    // * @param y, the start point
    // * @param scale, changing scale
    // */
    // private void drawDoubleStrikethrough(Graphics g, int x, int y, float scale)
    // {
    // //g.setColor(fontColor);
    // //stroke = new BasicStroke(scale);
    // //g.setStroke(stroke);
    // int h1 = (int)(y - scale * 3f);
    // int h2 = (int)(y - scale * 5f);
    // int thickness = scale >= 1.0f ? Math.round(scale) : 1;
    // for (int i = 0; i < thickness; i++)
    // {
    // //down line
    // g.drawLine(x, h1 + i, rectWidth - x, h1 + i);
    // //up line
    // g.drawLine(x, h2 + i, rectWidth - x, h2 + i);
    // }
    //
    // //resetLineWidth(g2);
    // }
    //
    // /**
    // * simplify main method
    // * @param g2, the object of Graphics2D
    // * @param x, the start point of drawing string
    // * @param y, the start point of drawing string
    // * @param scale, allocating scale equals getStrikethroughThickness()
    // */
    // private void paintThreeLines(Graphics g, int x, int y, float scale)
    // {
    // //king added --- begin ---, 2002-6-18，原因见“paint”方法中的注释
    // //由于下面画线的算法中的 x 坐标都是由 参数 x 和 rectWidth 决定的，因此直接缩放 x 和 rectWidth
    // //然后再恢复 rectWidth 的值（x 的值没有必要恢复）
    // x /= charScale;
    // int w = rectWidth;
    // rectWidth /= charScale;
    // //king added over --------------------------------------
    // if (underlineType > 1 || strikethrough || doubleStrikethrough)
    // {
    // //process underline
    // if (underlineType > 1)
    // {
    // drawUnderline(g, x, y, scale);
    // }
    // // process single strikethrough
    // if (strikethrough)
    // {
    // drawStrikethrough(g, x, y, scale);
    // }
    // //process double strikethrough
    // if (doubleStrikethrough)
    // {
    // drawDoubleStrikethrough(g, x, y, scale);
    // }
    // }
    // rectWidth = w; //king, 2002-6-18，恢复rectWidth的值
    // }
    //
    // /**
    // * draw Small caps or All caps of font
    // * calculateWidth()
    // * @param g2, the object of Graphics2D
    // * @return string width, float
    // * It saves the entireWidth & lastWidth
    // */
    // private float getCapsWidth(Graphics g, int midFontSize)
    // {
    // //calculate string width
    // //the other method is BufferedImage by clipt(not used here)
    // int entireWidth = 0;
    // //int lastWidth = 0;
    //
    // for (int i = 0; i < strLength ; i++)
    // {
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //String temp = previewText.substring(i, i + 1).toUpperCase();
    // String temp = PREVIEW_TEXT.substring(i, i + 1).toUpperCase();
    // int changeSize = midFontSize;
    // /*if (i == strLength - 1)
    // {
    // lastWidth = fm.stringWidth(temp);
    // } */
    // //if (smallcaps && previewText.charAt(i) >= 'a' && previewText.charAt(i) <= 'z')
    // if (smallcaps && PREVIEW_TEXT.charAt(i) >= 'a' && PREVIEW_TEXT.charAt(i) <= 'z')
    // {
    // //if (i % 3 != 0) //to determine if lower case
    // //{
    // changeSize = Math.round(midFontSize * SMALL_SCALE);
    // if (i == strLength - 1) //There is a question
    // {
    // //String lastStr = previewText.substring(strLength - 1, strLength);
    // //lastWidth = fm.stringWidth(lastStr);
    // }
    // //}
    // }
    // //modified it for the optimization of Font at Feb-21-2002
    // //font = new Font(fontName, fontStyle, changeSize);
    // font = FontManager.getFont(fontName, fontStyle, changeSize);
    //
    // fm = getFontMetrics(font);
    // entireWidth = entireWidth + fm.stringWidth(temp);
    // }
    //
    // return entireWidth;
    // }
    //
    // private float getCapsWidthcn(Graphics g, int midFontSize)
    // {
    // int entireWidth = 0;
    // String temp = null;
    // int leng = END_EN.length();
    // for (int i = 0; i < leng; i++)
    // {
    // temp = END_EN.substring(i, i + 1).toUpperCase();
    // int changeSize = midFontSize;
    // if (smallcaps && END_EN.charAt(i) >= 'a' && END_EN.charAt(i) <= 'z')
    // {
    // changeSize = Math.round(midFontSize * SMALL_SCALE);
    // // if (i == leng - 1) //There is a question
    // // {
    // // String lastStr = END_EN.substring(leng - 1, leng);
    // // }
    // }
    // font = font.deriveFont(changeSize); //new Font(fontName, fontStyle, changeSize);
    // fm = getFontMetrics(font);
    // entireWidth = entireWidth + fm.stringWidth(temp);
    // }
    // return entireWidth;
    // }
    //
    // /**
    // * draw charactere scale
    // * process character scale
    // * @param frc, the object of FontRenderContext
    // */
    // private FontRenderContext getCharScale()
    // {
    // /*
    // * king commented, 2002-6-18，原因见“paint”方法中的注释
    // */
    // //AffineTransform at = new AffineTransform();
    // //at.scale(charScale, 1);
    // //frc = new FontRenderContext(at, false, false);
    // //注释结束 －－－－－－－－－－－
    // frc = new FontRenderContext(null, false, false); //king, 2002-6-18
    // return frc;
    // }
    //
    // //get middle spacing
    // //because does not change spacing
    // private float getMidSpacing()
    // {
    // //process spacing comboBox
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //float width = fm.stringWidth(previewText) / 10.0f;
    // float width = fm.stringWidth(PREVIEW_TEXT) / 10.0f;
    // float midSpacing = (-spacing <= width) ? spacing : -width;
    // return midSpacing * SPACING_SCALE;
    // }
    //
    // private float getMidSpacingcn()
    // {
    // //process spacing comboBox
    // float width = (fm.stringWidth(END_EN) + fmcn.stringWidth(START_CH))/ 10f;
    // float midSpacing = (-spacing <= width) ? spacing : -width;
    // return midSpacing * SPACING_SCALE;
    // }
    // /**
    // * get character width
    // * @param g2, the object of Graphics2D
    // * @param x, the width of all string
    // * @return x1, the width of all string which are processed
    // * draw character spacing
    // * process character spacing
    // * @param g2, the object of Graphics2D
    // * @return float x1, the width of string after character spacing
    // */
    // private float getStringWidth(Graphics g, int midFontSize)
    // {
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //float x1 = fm.stringWidth(previewText);
    // float x1 = fm.stringWidth(PREVIEW_TEXT);
    // //process spacing comboBox
    // //float midSpacing = getMidSpacing();
    // //calculate total character spacing
    // //total value equals (str.length-1)*spacing
    // float totalSpacing = (strLength - 1) * getMidSpacing();
    //
    // //after Small Caps or All Caps
    // if (smallcaps || allcaps)
    // {
    // x1 = getCapsWidth(g, midFontSize);
    // }
    //
    // //left limit left limit
    // //x1 = x1 + totalSpacing;
    //
    // //after character scale
    // x1 = charScale * (x1 + totalSpacing);
    //
    // //It has limited width when condensed
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //float limitWidth = (fm.stringWidth(previewText) / 7.0f) * charScale;
    // float limitWidth = (fm.stringWidth(PREVIEW_TEXT) / 7.0f) * charScale;
    //
    // if (x1 <= limitWidth)
    // {
    // x1 = limitWidth;
    // }
    //
    // return x1;
    // }
    //
    // /**
    // * For Chinese
    // */
    // private float getStringWidthcn(Graphics g, int midFontSize)
    // {
    // int widthen = fm.stringWidth(END_EN);
    // int widthcn = fmcn.stringWidth(START_CH);
    // float x1 = widthen + widthcn;
    // float totalSpacing = (strLength - 1) * getMidSpacingcn();
    // //after Small Caps or All Caps
    // if (smallcaps || allcaps)
    // {
    // x1 = getCapsWidthcn(g, midFontSize) + widthcn;
    // }
    // //after character scale
    // x1 = charScale * (x1 + totalSpacing);
    // //It has limited width when condensed
    // float limitWidth = (widthen + widthcn / 7.0f) * charScale;
    // if (x1 <= limitWidth)
    // {
    // x1 = limitWidth;
    // }
    // return x1;
    // }
    // //Fetch Font attributes
    // private void initFontAttribute()
    // {
    // if (fa != null)
    // {
    // fontName = fa.getFontName();
    // if (fontName == null)
    // {
    // fontName = FontAttribute.DEFAULT_LATIN;
    // }
    // asianName = fa.getAsianName();
    // if (asianName == null)
    // {
    // asianName = FontAttribute.DEFAULT_ASIAN;
    // }
    // fontStyle = getLineAndStyle(fa.getFontStyle());
    //
    // int size = fa.getFontSize();
    // fontSize = size == -1 ? FontAttribute.getEIOSize(FontAttribute.DEFAULT_SIZE) : FontAttribute.getEIOSize(size);
    //
    // //if (type != PRESENTATION)
    // /*{
    // //isTitle = fa.isAutoColor();
    // fontColor = fa.getFontColor();
    // if (fontColor == null)
    // {
    // fontColor = Color.black;
    // }
    //
    // //isLineTitle = fa.isUnderlineAuto();
    // underlineColor = fa.getUnderlineColor();
    // if (underlineColor == null)
    // {
    // underlineColor = Color.black;
    // }
    // }*/
    // //else
    // if (isPresentation)
    // {
    // int fontSchemeIndex = fa.getFontSchemIndex();
    // if (fontSchemeIndex == -1)
    // {
    // fontColor = fa.getFontColor();
    // //isTitle = fa.isAutoColor();
    // //if (isTitle || fontColor == null)
    // if (fontColor == null)
    // {
    // fontColor = Color.black;
    // }
    // }
    // else
    // {
    // fontColor = scheme.getSchemeValue(fontSchemeIndex);
    // }
    // int lineSchemeIndex = fa.getUnderlineScheme();
    // if (lineSchemeIndex == -1)
    // {
    // underlineColor = fa.getUnderlineColor();
    // if (underlineColor == null)
    // {
    // underlineColor = Color.black;
    // }
    // }
    // else
    // {
    // underlineColor = scheme.getSchemeValue(lineSchemeIndex);
    // }
    // }
    // else
    // {
    // fontColor = fa.getFontColor();
    // if (fontColor == null)
    // {
    // fontColor = Color.black;
    // }
    //
    // underlineColor = fa.getUnderlineColor();
    // if (underlineColor == null)
    // {
    // underlineColor = Color.black;
    // }
    // }
    //
    // underlineType = getLineAndStyle(fa.getUnderlineType());
    // strikethrough = isFontEffect(fa.getStrikethrough());
    // doubleStrikethrough = isFontEffect(fa.getDoubleStrikethrough());
    // superscript = isFontEffect(fa.getSuperscript());
    // subscript = isFontEffect(fa.getSubscript());
    // shadow = isFontEffect(fa.getShadow());
    // //outline = isFontEffect(fa.getOutline());
    // emboss = isFontEffect(fa.getEmboss());
    // engrave = isFontEffect(fa.getEngrave());
    // smallcaps = isFontEffect(fa.getSmallcaps());
    // allcaps = isFontEffect(fa.getAllcaps());
    // hidden = isFontEffect(fa.getHidden());
    //
    // charScale = fa.getScale();
    // //Hide a bug about the scale of character, modified at Jan-30-2002
    // //if (charScale == -1.0f)
    // if (charScale == -1.0F || charScale == 0F)
    // {
    // charScale = 1.0f;
    // }
    // //Modified Jan - 03 - 2002
    // //spacing = getValue(fa.getSpacing()) * POINT_TO_PIXEL;
    // //position = getValue(fa.getPosition()) * POINT_TO_PIXEL;
    // spacing = fa.getSpacing() * POINT_TO_PIXEL;
    // position = fa.getPosition() * POINT_TO_PIXEL;
    // return;
    // }
    // else
    // {
    // fontName = FontAttribute.DEFAULT_LATIN; //"Dialog";//"Serif"; //should modify
    // asianName = FontAttribute.DEFAULT_ASIAN; //"宋体";
    // fontStyle = 0;
    // fontSize = FontAttribute.getEIOSize(FontAttribute.DEFAULT_SIZE);
    // fontColor = Color.black;
    // //isTitle = false;
    // underlineType = 0;
    // underlineColor = Color.black;
    // strikethrough = false;
    // doubleStrikethrough = false;
    // superscript = false;
    // subscript = false;
    // shadow = false;
    // //outline = false;
    // emboss = false;
    // engrave = false;
    // smallcaps = false;
    // allcaps = false;
    // hidden = false;
    // charScale = 1.0f;
    // spacing = 0.0f;
    // position = 0.0f;
    // return;
    // }
    // }
    // /**solute underline and font style*/
    // private int getLineAndStyle(int n){
    // if (n == -1)
    // n = 0;
    // return n;
    // }
    // /**solute 11 type font effect*/
    // private boolean isFontEffect(int n){
    // if (n == 0 || n == 1)
    // return false;
    // else if (n == 2)
    // return true;
    // return false;
    // }
    //
    // /**
    // * king added this method for disposing
    // * 2002-5-10
    // */
    // public void clearMem()
    // {
    // scheme = null;
    // fa = null;
    // //random = null;
    //
    // removeAll();
    // }
    //
    // /**--------------variable---------------------*/
    //
    // private int strLength;
    // //Modified,for tradional Chinese version at Jan-10-2002
    // //private String previewText;
    // private FontMetrics fm;
    // private FontMetrics fmcn;
    // private Font font;
    // private Font fontcn;
    // private FontRenderContext frc;
    // private TextLayout layout;
    // private BasicStroke stroke;
    // private FontAttribute fa;
    // private int type;
    // private ColorScheme scheme;
    //
    // private String fontName;
    // private String asianName;
    // private int fontStyle;
    // private int fontSize;
    // private Color fontColor;
    // private int underlineType;
    // private Color underlineColor;
    // private boolean strikethrough;
    // private boolean doubleStrikethrough;
    // private boolean superscript;
    // private boolean subscript;
    // private boolean shadow;
    // //private boolean outline;
    // private boolean emboss;
    // private boolean engrave;
    // private boolean smallcaps;
    // private boolean allcaps;
    // private boolean hidden;
    // private float charScale;
    // private float spacing;
    // private float position;
    // //for fontColor == Color.white
    // private boolean isWhite;
    // //Fix a bug of Java about Font Style
    // //private Random random = new Random();
    // //For Presentation
    // private boolean isPresentation;
    //
    // /* king added this field, 2002-6-15,原因见CharacterPanel.adjuestPosition的注释
    // * 因为当 y 为 50 时，早已经超出面板视图范围，因此用 50 初始化，不会有问题
    // */
    // private float lastY = 50;

}
