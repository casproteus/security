package org.cas.client.platform.pimview.pimcard;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.menuaction.DeleteAction;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.Releasable;
import org.cas.client.platform.pimmodel.PIMViewInfo;
import org.cas.client.platform.pimview.IView;
import org.cas.client.platform.pimview.pimscrollpane.PIMScrollPane;

public class CardViewPanel extends JComponent implements HierarchyBoundsListener, ListSelectionListener, Releasable {
    /**
     * 静态初始化块,载入UI
     */
    static {
        UIManager.getDefaults().put("CardViewPanelUI", "org.cas.client.platform.pimview.pimcard.BasicCardViewPanelUI");
        // UIManager.getDefaults().put("PIMTable.selectionBackground", CustomOptions.custOps.getSelectedBackColor());
    }

    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "CardViewPanelUI";

    /**
     * Creates a new instance of CardViewPanel
     * 
     * @param width
     *            卡片面板宽度
     * @param height
     *            卡片面板高度
     * @param contents
     *            数据内容
     * @param cardInfo
     *            视图信息
     */
    public CardViewPanel() {
    }

    /**
     * Creates a new instance of CardViewPanel
     * 
     * @param width
     *            卡片面板宽度
     * @param height
     *            卡片面板高度
     * @param contents
     *            数据内容
     * @param cardInfo
     *            视图信息
     */
    public CardViewPanel(int width, int height, Object[][] contents, CardInfo cardInfo) {
        this.width = width;
        this.height = height;
        this.contents = contents;
        this.cardInfo = cardInfo;

        init();
        layoutCardView();
    }

    /**
     * Creates a new instance of CardViewPanel
     * 
     * @param width
     *            卡片面板宽度
     * @param height
     *            卡片面板高度
     * @param view
     *            视图接口
     */
    public CardViewPanel(int width, int height, IView view) {
        this.width = width;
        this.height = height;
        this.view = view;

        // contents = view.getApplication ().getViewContents ();
        // viewInfo = view.getApplication ().getActiveViewInfo ();

        init();
        layoutCardView();
    }

    /**
     * Resets the UI property with a value from the current look and feel.
     *
     * @see JComponent#updateUI
     */
    public void updateUI() {
        setUI((CardViewPanelUI) UIManager.getUI(this));
    }

    /**
     * Returns the look and feel (L&F) object that renders this component.
     *
     * @return the PanelUI object that renders this component
     * @since 1.4
     */
    public CardViewPanelUI getUI() {
        return (CardViewPanelUI) ui;
    }

    /**
     * Sets the look and feel (L&F) object that renders this component.
     *
     * @param ui
     *            the PanelUI L&F object
     * @see UIDefaults#getUI
     * @since 1.4
     * @beaninfo bound: true hidden: true attribute: visualUpdate true description: The UI object that implements the
     *           Component's LookAndFeel.
     */
    public void setUI(
            CardViewPanelUI ui) {
        super.setUI(ui);
    }

    /**
     * Returns a string that specifies the name of the L&F class that renders this component.
     *
     * @return "PanelUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     * @beaninfo expert: true description: A string that specifies the name of the L&F class.
     */
    public String getUIClassID() {
        return uiClassID;
    }

    /**
     * See readObject() and writeObject() in JComponent for more information about serialization in Swing.
     */
    private void writeObject(
            ObjectOutputStream s) throws IOException {
        // s.defaultWriteObject();
        // if (getUIClassID().equals(uiClassID)) {
        // byte count = JComponent.getWriteObjCounter(this);
        // JComponent.setWriteObjCounter(this, --count);
        // if (count == 0 && ui != null) {
        // ui.installUI(this);
        // }
        // }
    }

    /**
     * Returns a string representation of this JPanel. This method is intended to be used only for debugging purposes,
     * and the content and format of the returned string may vary between implementations. The returned string may be
     * empty but may not be <code>null</code>.
     *
     * @return a string representation of this JPanel.
     */
    protected String paramString() {
        return super.paramString();
    }

    /**
     * 得到卡片视图的总列数,每一个是最下一张卡片的索引
     * 
     * @return 列数组
     */
    public Vector getRowVector() {
        return rowsVector;
    }

    /**
     *
     */
    private void init() {
        setLayout(null);
        setDoubleBuffered(true);
        setOpaque(true);
        updateUI();
        setBackground(Color.white);

        setFocusTraversalKeysEnabled(false);

        rowsVector = new Vector();
        rowsIndexes = new int[0];
        cardsVector = new Vector();
        selectedCards = new Vector();
        copyVector = new Vector();
        selectionModel = new DefaultListSelectionModel();
        selectionModel.addListSelectionListener(this);
        // 处理键盘事件和鼠标事件
        keyHandler = new CardViewPanelKeyHandler(this);
        addKeyListener(keyHandler);
        mouseHandler = new CardViewPanelMouseHandler(this);
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
        addHierarchyBoundsListener(this);
    }

    /**
     * 重载
     * 
     * @return boolean
     */
    public boolean isFocusTraversable() {
        return true;
    }

    /**
     * 布局视图
     */
    public void layoutCardView() {
        initCardComponents(contents, cardInfo);

        calculateRowsAndColumns();

        layoutCardComponents();
    }

    /**
     * 布局
     */
    public void layoutCardComponents() {
        int dx = CustOpts.HOR_GAP;
        int dy = CustOpts.VER_GAP;
        int columns = rowsVector.size();
        int temp = 0;
        CardViewComponent cardComponent = null;

        for (int i = 0; i < columns; i++) {
            // 实际上是第一列
            int rows = Integer.parseInt((String) rowsVector.elementAt(i));
            for (int j = temp; j <= rows; j++) {
                cardComponent = (CardViewComponent) cardsVector.elementAt(j);
                cardComponent.setBounds(dx, dy, cardComponent.getCardWidth(), cardComponent.getCardHeight());
                dy = dy + cardComponent.getCardHeight() + CustOpts.VER_GAP;
            }
            // 暂时，将来卡片的宽度有视图信息确定
            CardViewComponent tempComponent = (CardViewComponent) cardsVector.elementAt(0);
            // 每一列处理完,X坐标加偏移,Y坐标恢复
            dx = dx + tempComponent.getCardWidth() + CustOpts.HOR_GAP;
            dy = CustOpts.VER_GAP;
            temp = rows + 1;
        }

        // 计算本组件的首选尺寸(X方向上的宽度)
        int length = cardsVector.size();
        if (length > 0) {
            CardViewComponent tempComponent = (CardViewComponent) cardsVector.elementAt(length - 1);
            preferredSize = new Dimension(tempComponent.getX() + tempComponent.getCardWidth(), height);
            setPreferredSize(preferredSize);
        }

        // 去原来本面板上的卡片,把我们的放上去
        removeAll();
        for (int i = 0; i < length; i++) {
            add((CardViewComponent) cardsVector.elementAt(i));
        }
    }

    /**
     * 初始化卡片组件,有多少个记录便于工作有多少张卡片,放入一个Vector
     * 
     * @param contents
     *            数据内容
     * @param info
     *            视图信息
     */
    public void initCardComponents(
            Object[][] contents,
            CardInfo info) {
        int length = contents.length;
        CardViewComponent cardComponent = null;
        cardsVector.removeAllElements();
        // 为每一张卡片分配所需显示的字符串信息
        for (int i = 0; i < length; i++) {
            Object[] record = contents[i];
            cardComponent = new CardViewComponent(info, record);
            cardComponent.setCardIndex(i);

            cardsVector.addElement(cardComponent);
        }
    }

    /**
     * 计算行数和列数 这个方法实际只计算每列有几行,rowsVector中的元素为每列的最下面一个卡片的索引
     */
    public void calculateRowsAndColumns() {
        int rows = 0;
        int columns = 0;
        int length = cardsVector.size();
        int columnHeight = CustOpts.VER_GAP;

        rowsVector.removeAllElements();
        CardViewComponent cardViewComponent = null;
        for (int i = 0; i < length; i++) {
            cardViewComponent = (CardViewComponent) cardsVector.elementAt(i);
            columnHeight += cardViewComponent.getCardHeight() + CustOpts.VER_GAP;
            if (columnHeight - CustOpts.VER_GAP == height) {
                columns++;
                rows = i;
                rowsVector.addElement(Integer.toString(rows));
                columnHeight = CustOpts.VER_GAP;
            } else if (columnHeight - CustOpts.VER_GAP > height) {
                columns++;
                rows = i - 1;
                if (i < 1) {
                    rows = i;
                }
                rowsVector.addElement(Integer.toString(rows));
                columnHeight = cardViewComponent.getCardHeight() + CustOpts.VER_GAP * 2;
            }
            if (i == length - 1) {
                rowsVector.addElement(Integer.toString(length - 1));
            }
        }
        int rowsSize = rowsVector.size();
        rowsIndexes = new int[rowsSize];
        for (int i = 0; i < rowsSize; i++) {
            rowsIndexes[i] = Integer.parseInt(rowsVector.get(i).toString());
        }
    }

    /**
     * 获得当前选中的索引
     * 
     * @return int
     */
    public int getCurrentSelectedIndex() {
        return currentSelectedIndex;
    }

    /**
     * 设置当前选中的索引
     * 
     * @param currentSelectedIndex
     *            选中的索引
     */
    public void setCurrentSelectedIndex(
            int currentSelectedIndex) {
        this.currentSelectedIndex = currentSelectedIndex;
    }

    /**
     * 返回初始大小
     * 
     * @return Dimension
     */
    public Dimension getPreferredSize() {
        return preferredSize;
    }

    /**
     * 获得视图
     * 
     * @return IView
     */
    public IView getView() {
        return view;
    }

    /**
     * 设置视图
     * 
     * @param view
     *            视图接口
     */
    public void setView(
            IView view) {
        this.view = view;
    }

    /**
     * 获得所有卡片组件
     * 
     * @return Vector
     */
    public Vector getCardComponents() {
        return cardsVector;
    }

    /**
     * 获得选中卡片
     * 
     * @return Vector
     */
    public Vector getSelectedCards() {
        return selectedCards;
    }

    /**
     * 设置选中卡片
     * 
     * @param v
     *            选中卡片的数组
     */
    public void setSelectedCards(
            Vector v) {
        this.selectedCards = v;
    }

    /**
     * 得到所有选中的卡片
     * 
     * @return 选中卡片的数组
     */
    public Vector getSelectedCardComponents() {
        Vector selectedCardsComponents = new Vector();
        int[] indexes = null;
        if (selectedCards != null && selectedCards.size() > 0) {
            int length = selectedCards.size();
            indexes = new int[length];
            for (int i = 0; i < length; i++) {
                indexes[i] = ((CardViewComponent) selectedCards.elementAt(i)).getCardIndex();
            }
        }

        Arrays.sort(indexes);

        if (indexes != null) {
            for (int i = 0; i < indexes.length; i++) {
                selectedCardsComponents.addElement(cardsVector.elementAt(indexes[i]));
            }
        }

        return selectedCardsComponents;
    }

    /**
     * 设置宽高
     * 
     * @param w
     *            宽度
     * @param h
     *            高度
     */
    public void setWidthAndHeight(
            int w,
            int h) {
        if (this.width != w || this.height != h) {
            this.width = w;
            this.height = h;

            refresh();
        }
    }

    /**
     * 选中所有
     */
    public void selectAll() {
        selectionModel.setSelectionInterval(0, cardsVector.size() - 1);
    }

    /**
     * 判断是否有卡被选中
     * 
     * @return boolean
     */
    public boolean hasSelectedCard() {
        if (selectedCards != null && selectedCards.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * 返回拷贝的所有记录
     * 
     * @return Vector
     */
    public Vector getCopyVector() {
        return copyVector;
    }

    /**
     * 设置拷贝
     * 
     * @param cv
     *            可拷贝的卡片的数组
     */
    public void setCopyVector(
            Vector cv) {
        this.copyVector = cv;
    }

    /**
     * 拷贝选中的卡片
     */
    public void copySelectedCards() {
        if (selectedCards != null && selectedCards.size() > 0) {
            // 清空拷贝
            copyVector.removeAllElements();

            int length = selectedCards.size();
            CardViewComponent cardViewComponent = null;
            for (int i = 0; i < length; i++) {
                cardViewComponent = (CardViewComponent) selectedCards.elementAt(i);
                CardViewComponent newCardView = new CardViewComponent(cardInfo, cardViewComponent.getCardRecord());
                copyVector.addElement(newCardView);
            }
        }
    }

    /**
     * 返回卡片的宽度
     * 
     * @return int
     */
    public int getCardWidth() {
        FontMetrics titleFontMetrics = getFontMetrics(cardInfo.getTitleFont());
        int titleCharWidth = titleFontMetrics.charWidth('W');

        FontMetrics textFontMetrics = getFontMetrics(cardInfo.getTextFont());
        int textCharWidth = textFontMetrics.charWidth('W');

        int maxCharWidth = titleCharWidth > textCharWidth ? titleCharWidth : textCharWidth;
        int cardWidth = cardInfo.getCardWidth() * maxCharWidth;

        return cardWidth;
    }

    /**
     * 绘制 重载的目的是绘制分隔线
     * 
     * @param g
     *            图形设备
     */
    public void paintComponent(
            Graphics g) {
        super.paintComponent(g);

        int x = CustOpts.HOR_GAP;
        int cardWidth = getCardWidth();
        int w = getPreferredSize().width;
        int gColumns = w / cardWidth;
        g.setColor(Color.lightGray);
        // 绘制分隔线
        for (int i = 0; i < gColumns; i++) {
            x += cardWidth;
            if (i == 0) {
                x += CustOpts.HOR_GAP / 2;
            } else {
                x += CustOpts.HOR_GAP;
            }
            // 画线
            g.fillRect(x - 1, CustOpts.VER_GAP, 2, height);
            // g.drawLine (x, CustOpts.VER_GAP, x, height);
        }
    }

    /**
     * 设置选中一张卡片,初始化时调用
     * 
     * @param comp
     *            卡片组件
     */
    public void selectCard(
            CardViewComponent comp) {
        int index = comp.getCardIndex();
        changeSelectionModel(index, true, false);
        // selectionModel.setAnchorSelectionIndex(index);
        // selectedCards.addElement(comp);
        // comp.setSelected(true);
        // setLastAnchorCard(comp);
    }

    /**
     * 根据作标点和选取模式选取card
     * 
     * @param p
     *            鼠标点
     * @param toggle
     *            ctrl
     * @param extend
     *            shift
     */
    public void selectCard(
            Point p,
            boolean toggle,
            boolean extend) {
        // 根据点获取图标组件
        Object source = getComponentAt(p);
        if (source instanceof CardViewComponent) {
            CardViewComponent comp = (CardViewComponent) source;
            int cardIndex = comp.getCardIndex();
            changeSelectionModel(cardIndex, toggle, extend);
        } else {
            clearSelection();
        }
    }

    // public void selectCard (Point p, int mode)
    // {
    // //根据点获取图标组件
    // Object source = getComponentAt (p);
    // if (source instanceof CardViewComponent)
    // {
    // CardViewComponent comp = (CardViewComponent)source;
    // //选取模式
    // switch (mode)
    // {
    // case CardConstants.SINGLE_SELECT: //处理单选模式
    // clearSelection ();
    // selectedCards.addElement (comp);
    // comp.setSelected (true);
    // setLastAnchorCard(comp);
    // break;
    //
    // case CardConstants.MULTI_SELECT: //处理多选模式
    // if (selectedCards.contains (comp))
    // {
    // selectedCards.remove (comp);
    // comp.setSelected (false);
    // }
    // else
    // {
    // selectedCards.addElement (comp);
    // comp.setSelected (true);
    // setLastAnchorCard(comp);
    // }
    // break;
    // case CardConstants.SINGLE_INTERVAL_SELECT: //处理shift多选模式
    // CardViewComponent lastAnchorCard = getLastAnchorCard();
    // if(lastAnchorCard == null)
    // {
    // return ;
    // }
    // int lastIndex = lastAnchorCard.getCardIndex();
    // for(int i = lastIndex + 1 ; i <= comp.getCardIndex(); i++)
    // {
    // CardViewComponent selectCard = (CardViewComponent)getCardComponents().get(i);
    // if (selectedCards.contains(selectCard))
    // {
    // setLastAnchorCard(selectCard);
    // }
    // else
    // {
    // selectedCards.addElement(selectCard);
    // selectCard.setSelected(true);
    // setLastAnchorCard(selectCard);
    // }
    // }
    // break;
    // default:
    // break;
    // }
    // }
    // else
    // {
    // //清空选中
    // if (mode == CardConstants.SINGLE_SELECT)
    // {
    // clearSelection ();
    // }
    // }
    //
    // //重绘
    // revalidate ();
    // repaint ();
    // }

    /**
     * 改变选择模型中的选中状态
     * 
     * @param index
     *            索引值
     * @param toggle
     *            是否为ctrl
     * @param extend
     *            是否shift
     * @param selected
     *            是否选中
     */
    private void changeSelectionModel(
            int index,
            boolean toggle,
            boolean extend) {
        // 扩展
        if (extend) {
            // 反转
            // if (toggle)
            // {
            // selectionModel.setAnchorSelectionIndex(index);
            // }
            // 不反转
            // else
            {
                selectionModel.setLeadSelectionIndex(index);
            }
        }
        // 不扩展
        else {
            // 反转
            if (toggle) {
                // 有选中
                if (selectionModel.isSelectedIndex(index)) {
                    selectionModel.removeSelectionInterval(index, index);
                }
                // 没有选中
                else {
                    selectionModel.addSelectionInterval(index, index);
                }
            }
            // 不反转
            else {
                selectionModel.setSelectionInterval(index, index);
            }
        }
    }

    /**
     * 得到所选中的第一行,-1表示没有选中的 Returns the index of the first selected row, -1 if no row is selected.
     * 
     * @return the index of the first selected row
     */
    public int getSelectedCardIndex() {
        return selectionModel.getMinSelectionIndex();
    }

    /**
     * 得到所有选中的卡片的索引值
     *
     * @return an array of integers containing the indices of all selected rows, or an empty array if no row is selected
     * @see #getSelectedRow
     */
    public int[] getSelectedCardIndexes() {
        // 得到选择模型中的最小行号和最大行号
        int iMin = selectionModel.getMinSelectionIndex();
        int iMax = selectionModel.getMaxSelectionIndex();

        // 没有就造个空的数组返回
        if ((iMin == -1) || (iMax == -1)) {
            return new int[0];
        }

        // 造个数组容纳所有的选中行(中间可能有没有选中的,间隔选的)
        int[] rvTmp = new int[1 + (iMax - iMin)];
        int n = 0;
        // 遍历,有就放进去
        for (int i = iMin; i <= iMax; i++) {
            if (selectionModel.isSelectedIndex(i)) {
                rvTmp[n++] = i;
            }
        }
        // 这个数组正合适,复制一下
        int[] rv = new int[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }

    /**
     * 得到选择模型
     * 
     * @return 选择模型
     */
    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    /**
     * 清除选取
     */
    public void clearSelection() {
        selectionModel.clearSelection();
    }

    /**
     * 清除选取卡片数组
     */
    public void clearSelectionCardVector() {
        // 选中不为空
        if (selectedCards != null && selectedCards.size() != 0) {
            for (int i = 0; i < selectedCards.size(); i++) {
                // 设置选中状态
                CardViewComponent cardComp = (CardViewComponent) selectedCards.elementAt(i);
                cardComp.setSelected(false);
            }
        }
        // 清空选中
        selectedCards.clear();
        setLastAnchorCard(null);
    }

    /**
     * 处理鼠标双击动作
     */
    public void processDoubleClickedAction() {
        // 选中的个数
        int length = selectedCards.size();
        // 判断当前长度是否为1
        if (length == 1) {
            CardViewComponent comp = (CardViewComponent) selectedCards.elementAt(0);
            int recordID = comp.getRecordID();
            // 处理打开记录
            // TODO
            if (view != null) {
                view.getApplication().processMouseDoubleClickAction(this, 0, 0, recordID);
            }
        }
    }

    /**
     * Called when an ancestor of the source is moved.
     * 
     * @param e
     *            a HierarchyEvent
     */
    public void ancestorMoved(
            HierarchyEvent e) {
    }

    /**
     * Called when an ancestor of the source is resized.
     * 
     * @param e
     *            a HierarchyEvent
     */
    public void ancestorResized(
            HierarchyEvent e) {
        Container parent = e.getChangedParent();
        if (parent != null && parent instanceof PIMScrollPane) {
            PIMScrollPane scrollPane = (PIMScrollPane) parent;
            Rectangle bounds = scrollPane.getViewport().getBounds();
            width = bounds.width;
            height = bounds.height - CustOpts.VER_GAP;
            // 刷新视图
            refresh();
        }
    }

    /**
     * 处理视图刷新
     */
    private void refresh() {
        // 清空缓冲
        rowsVector.clear();
        rowsIndexes = null;
        // 重新计算和布局
        calculateRowsAndColumns();
        layoutCardComponents();

        // 重绘
        revalidate();
        repaint();
    }

    /**
     * 删除选取的图标
     */
    public void removeSelected() {
        // 判断选中是否为空
        if (selectedCards.size() > 0 && selectedCards.size() != cardsVector.size()) {
            // Vector deleteCards = new Vector(selectedCards.size ());
            // IModel model = PIMControl.ctrl.getModel();
            // int[] ids = new int[selectedCards.size()];
            // for (int i = 0; i < selectedCards.size (); i ++)
            // {
            // //删除选中的附件
            // CardViewComponent viewComp = (CardViewComponent)selectedCards.elementAt (i);
            // int recordID = viewComp.getRecordID();
            // ids[i] = recordID;
            // }

            clearSelection(); // 清空选中

            // removeSelectedRecord (deleteCards);

            new DeleteAction().actionPerformed(null);

            // refresh (); //刷新
        } else if (selectedCards.size() > 0 && selectedCards.size() == cardsVector.size()) {
            // 删除所有
            removeAllCards();
        }
    }

    /**
     * 从数据库中删除记录
     * 
     * @param records
     *            记录的数组
     */
    public void removeSelectedRecord(
            List records) {
        CASControl.ctrl.getModel().deleteRecords(records);
    }

    /**
     * 删除所有卡片
     */
    public void removeAllCards() {
        // 判断选中数是否等于图标总数
        if (selectedCards.size() == cardsVector.size()) {
            // 清空选中
            clearSelection();
            selectedCards.clear();

            // 清空所有
            cardsVector.clear();
            removeAll();

            // 刷新
            refresh();
        }
    }

    /**
     * 添加一条记录，卡片
     * 
     * @param record
     *            一条记录
     */
    public void addCard(
            Vector record) {
    }

    /**
     * 添加多条记录，卡片
     * 
     * @param records
     *            记录数组
     */
    public void addCards(
            Vector records) {
    }

    /**
     * 返回键盘处理事件
     * 
     * @return CardViewPanelKeyHandler
     */
    public CardViewPanelKeyHandler getKeyAdapter() {
        return keyHandler;
    }

    /**
     * 获取CardInfo
     * 
     * @return cardInfo
     */
    public CardInfo getCardInfo() {
        return cardInfo;
    }

    /**
     * 设置CardInfo
     * 
     * @param info
     *            卡片信息
     */
    public void setCardInfo(
            CardInfo info) {
        this.cardInfo = info;

        // 组件重绘
        if (cardsVector != null && cardsVector.size() > 0) {
            for (int i = 0; i < cardsVector.size(); i++) {
                CardViewComponent cardComp = (CardViewComponent) cardsVector.elementAt(i);
                cardComp.setCardInfo(info);
            }
        }

        // 面板刷新
        refresh();
    }

    /**
     * 返回所有记录Vector
     * 
     * @return 所有记录
     */
    public Object[][] getDataVector() {
        return contents;
    }

    /**
     * 设置最后一张选择的卡片
     * 
     * @param prmLastAnchorCard
     *            一张卡片
     */
    public void setLastAnchorCard(
            CardViewComponent prmLastAnchorCard) {
        lastAnchorCard = prmLastAnchorCard;
    }

    /**
     * 得到最后一张选择的卡片
     * 
     * @return 最后选择的卡片
     */
    public CardViewComponent getLastAnchorCard() {
        int tmpAnchorIndex = selectionModel.getAnchorSelectionIndex();
        if (tmpAnchorIndex < 0) {
            return null;
        } else {
            return (CardViewComponent) getCardComponents().get(tmpAnchorIndex);
        }
    }

    /**
     * 处理键盘事件
     * 
     * @param e
     *            键盘事件
     */
    public void processKeyEvent(
            java.awt.event.KeyEvent e) {
        super.processKeyEvent(e);
    }

    /**
     * 因为不超掉本事件会通知到滚动面板
     */
    protected boolean processKeyBinding(
            javax.swing.KeyStroke ks,
            java.awt.event.KeyEvent e,
            int condition,
            boolean pressed) {
        return true;
    }

    /**
     * 得到所有行的最下一张卡片的索引数组
     * 
     * @return 最下一张卡片的索引数组
     */
    public int[] getRowIndexes() {
        return rowsIndexes;
    }

    /**
     * Called whenever the value of the selection changes.
     * 
     * @param e
     *            the event that characterizes the change.
     */
    public void valueChanged(
            ListSelectionEvent e) {
        // TODO: 重设卡片的选中状态
        selectedCards.removeAllElements();
        for (int i = 0; i < cardsVector.size(); i++) {
            CardViewComponent selectCard = (CardViewComponent) getCardComponents().get(i);
            if (selectionModel.isSelectedIndex(i)) {
                selectedCards.addElement(selectCard);
                selectCard.setSelected(true);
            } else {
                selectCard.setSelected(false);
            }
        }
        revalidate();
        repaint();
    }

    /**
     * 进行清除对象引用关系、打断连接的动作，为gc的垃圾回收做准备 原则：1、谁使用该对象谁释放，既调用此方法。若用Optimizeit可找到此对象的root引用， 应该在root一级做这个动作
     * 2、后创建的先释放。例如：A->B->C,则调用关系是a.release()->b.release()->c.release()，
     * 一路调用下去实际执行时的顺序是c.release()、b.release()、a.release(); 注意：1、调用顺序出现错误，若先创建的先被释放，会产生空指针或数组越界之类的异常。特别注意调用顺序
     * 2、此方法中要特别注意监听器的移除、Hashtable、Hashmap、Vector等结构中数据的移除和释放、 视图中UI的卸载等
     *
     */
    public void release() {
        if (getUI() instanceof BasicCardViewPanelUI) {
            ((BasicCardViewPanelUI) getUI()).uninstallUI(this);
        }
        removeKeyListener(keyHandler);
        removeMouseListener(mouseHandler);
        removeMouseMotionListener(mouseHandler);
        removeHierarchyBoundsListener(this);
        selectionModel.removeListSelectionListener(this);
    }

    private CardViewComponent lastAnchorCard;
    // 处理刷新
    private int width;
    private int height;
    private int currentSelectedIndex = -1;

    // 初始大小
    private Dimension preferredSize = new Dimension(0, 0);

    // private CardViewPanel viewPanel = this;
    // 视图
    private IView view;
    // 卡片视图信息
    private PIMViewInfo viewInfo;
    private CardInfo cardInfo;
    // 行
    private Vector rowsVector;

    private int[] rowsIndexes;
    // 所有数据库的记录
    private Object[][] contents;

    // 所有卡片
    private Vector cardsVector;
    // 选中卡片
    private Vector selectedCards;
    // 拷贝卡片
    private Vector copyVector;

    // 键盘处理事件
    private CardViewPanelKeyHandler keyHandler;
    // 鼠标处理事件
    private CardViewPanelMouseHandler mouseHandler;

    private ListSelectionModel selectionModel;
}
