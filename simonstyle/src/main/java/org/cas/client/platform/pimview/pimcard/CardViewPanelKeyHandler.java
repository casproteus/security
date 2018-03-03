package org.cas.client.platform.pimview.pimcard;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.cas.client.platform.cascustomize.CustOpts;

public class CardViewPanelKeyHandler implements KeyListener {

    /**
     * Creates a new instance of CardViewPanelKeyListener
     * 
     * @param cardPane
     *            卡片面板
     */
    public CardViewPanelKeyHandler(CardViewPanel cardPane) {
        this.cardPane = cardPane;
    }

    /**
     * Invoked when a key has been pressed. See the class description for {@link KeyEvent} for a definition of a key
     * pressed event.
     * 
     * @param e
     *            键盘事件
     */
    public void keyPressed(
            KeyEvent e) {
        int keyCode = e.getKeyCode();
        int modifiers = e.getModifiers();
        Object[][] dataV = cardPane.getDataVector();
        if (dataV == null || dataV.length == 0) {
            return;
        }
        if (e.isControlDown() && keyCode == KeyEvent.VK_A) {
            cardPane.selectAll();
            return;
        }

        if (keyCode == KeyEvent.VK_DELETE) {
            cardPane.removeSelected();
            return;
        }

        if (keyCode == KeyEvent.VK_TAB) {
            // return ;
        }

        if (e.isControlDown() && keyCode == KeyEvent.VK_C) {
            cardPane.copySelectedCards();
            return;
        }
        // 处理跳到开始.
        if (keyCode == KeyEvent.VK_ESCAPE) {
            if (cardPane.getCardComponents().size() == 0) {
                return;
            }
            CardViewComponent card = (CardViewComponent) cardPane.getCardComponents().get(0);
            cardPane.clearSelection();
            cardPane.getSelectedCards().addElement(card);
            card.setSelected(true);
            cardPane.scrollRectToVisible(card.getBounds());
            return;
        } else // if (isNavigateKey(e))
        {
            cardPane.requestFocusInWindow();
            // int mode = e.isControlDown() ? CardConstants.MULTI_SELECT :
            // (e.isShiftDown() ? CardConstants.SINGLE_INTERVAL_SELECT : CardConstants.SINGLE_SELECT);

            CardViewComponent anchorCard = cardPane.getLastAnchorCard();
            if (anchorCard == null) {
                return;
            }
            int anchorIndex = anchorCard.getCardIndex();

            CardViewComponent nextCard;
            Point nextLocation;

            switch (keyCode) {
                case KeyEvent.VK_HOME:
                    int homeIndex = 0;
                    nextCard = (CardViewComponent) cardPane.getCardComponents().get(homeIndex);
                    nextLocation = nextCard.getLocation();
                    cardPane.scrollRectToVisible(nextCard.getBounds());
                    cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                            e.isShiftDown());
                    break;

                case KeyEvent.VK_END:
                    int endIndex = dataV.length - 1;
                    nextCard = (CardViewComponent) cardPane.getCardComponents().get(endIndex);
                    nextLocation = nextCard.getLocation();
                    cardPane.scrollRectToVisible(nextCard.getBounds());
                    cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                            e.isShiftDown());
                    break;

                case KeyEvent.VK_PAGE_UP:
                    pageUp(e, anchorCard);
                    break;

                case KeyEvent.VK_PAGE_DOWN:
                    pageDown(e, anchorCard);
                    break;

                case KeyEvent.VK_LEFT:
                    navigateLeft(e, anchorIndex);
                    break;

                case KeyEvent.VK_RIGHT:
                    navigateRight(e, anchorIndex);
                    break;

                case KeyEvent.VK_TAB:
                    // 目前这一句根本执行不到,系统挡掉了,
                    if (e.isShiftDown()) {
                        if (anchorIndex == 0) {
                            return;
                        }
                        nextCard = (CardViewComponent) cardPane.getCardComponents().get(anchorIndex - 1);
                        nextLocation = nextCard.getLocation();
                        cardPane.scrollRectToVisible(nextCard.getBounds());
                        cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(), false);
                    } else if (modifiers == 0) {
                        if (anchorIndex == cardPane.getDataVector().length - 1) {
                            break;
                        }
                        nextCard = (CardViewComponent) cardPane.getCardComponents().get(anchorIndex + 1);
                        nextLocation = nextCard.getLocation();
                        cardPane.scrollRectToVisible(nextCard.getBounds());
                        cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                                e.isShiftDown());
                    }
                    break;

                case KeyEvent.VK_UP:
                    if (anchorIndex == 0) {
                        return;
                    }
                    nextCard = (CardViewComponent) cardPane.getCardComponents().get(anchorIndex - 1);
                    nextLocation = nextCard.getLocation();
                    cardPane.scrollRectToVisible(nextCard.getBounds());
                    cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                            e.isShiftDown());
                    break;

                case KeyEvent.VK_DOWN:
                    if (anchorIndex == cardPane.getDataVector().length - 1) {
                        break;
                    }
                    nextCard = (CardViewComponent) cardPane.getCardComponents().get(anchorIndex + 1);
                    nextLocation = nextCard.getLocation();
                    cardPane.scrollRectToVisible(nextCard.getBounds());
                    cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                            e.isShiftDown());
                    break;

                default:
                    break;
            }
            cardPane.requestFocusInWindow();
        }
    }

    /**
     * Invoked when a key has been released. See the class description for {@link KeyEvent} for a definition of a key
     * released event.
     * 
     * @param e
     *            键盘事件
     */
    public void keyReleased(
            KeyEvent e) {
    }

    /**
     * Invoked when a key has been typed. See the class description for {@link KeyEvent} for a definition of a key typed
     * event.
     * 
     * @param e
     *            键盘事件
     */
    public void keyTyped(
            KeyEvent e) {
    }

    /**
     * 向上翻页
     * 
     * @param e
     *            键盘事件
     * @param anchorCard
     *            上次选中的卡片
     */
    private void pageUp(
            KeyEvent e,
            CardViewComponent anchorCard) {
        // TODO:先计算视口宽度,再除以卡片+CAP的宽度,得到这最上面的一个卡片索引
        int cardWidth = anchorCard.getCardWidth();
        CardViewComponent nextCard;
        Point nextLocation;
        int scrollWidth = cardPane.getParent().getBounds().width + cardPane.getLocation().x;
        if (scrollWidth > 0) {
            // 到第一个
            nextCard = (CardViewComponent) cardPane.getCardComponents().get(0);
            nextLocation = nextCard.getLocation();
            cardPane.scrollRectToVisible(nextCard.getBounds());
            cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(), e.isShiftDown());
            return;
        }
        int range = (-scrollWidth) / (cardWidth + CustOpts.HOR_GAP) - 1;
        int[] columns = cardPane.getRowIndexes();
        int pageUpIndex = -1;
        if (range <= 0) {
            pageUpIndex = 0;
        } else {
            pageUpIndex = columns[range] + 1;
        }
        nextCard = (CardViewComponent) cardPane.getCardComponents().get(pageUpIndex);
        nextLocation = nextCard.getLocation();
        cardPane.scrollRectToVisible(nextCard.getBounds());
        cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(), e.isShiftDown());
    }

    /**
     * 向下翻页
     * 
     * @param e
     *            键盘事件
     * @param anchorCard
     *            上次选中的卡片
     */
    private void pageDown(
            KeyEvent e,
            CardViewComponent anchorCard) {
        if (anchorCard.getCardIndex() == cardPane.getCardComponents().size() - 1) {
            return;
        }
        // TODO:先计算视口宽度,再除以卡片+CAP的宽度,得到这最上面的一个卡片索引
        int cardWidth = anchorCard.getCardWidth();
        int scrollWidth = cardPane.getParent().getBounds().width - cardPane.getLocation().x;
        int range = scrollWidth / (cardWidth + CustOpts.HOR_GAP) - 1;
        int viewRange = cardPane.getParent().getBounds().width / (cardWidth + CustOpts.HOR_GAP);
        int[] columns = cardPane.getRowIndexes();
        int pageDownIndex = -1;
        // 表示将要锁定的列是最后一列,就要到最后一个卡片
        if (range + 1 >= columns.length - 1) {
            pageDownIndex = columns[columns.length - 1];
            CardViewComponent nextCard = (CardViewComponent) cardPane.getCardComponents().get(pageDownIndex);
            Point nextLocation = nextCard.getLocation();
            cardPane.scrollRectToVisible(nextCard.getBounds());
            cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(), e.isShiftDown());
            return; // ok
        } else {
            pageDownIndex = columns[range] + 1;
            // 这里就复杂了,处理以下这种情况,
            // TODO: 设视图可显示三列,现在视图的首列是倒数第四列,应选中倒数第三列首卡片
            if ((range + viewRange) > columns.length - 1) {
                int forward = columns.length - viewRange;
                int delta = forward - range;
                // pageDownIndex = columns[columns.length - viewRange ] + 1;
                pageDownIndex = columns[delta] + 1;
            }
            // 因为PAGEDOWN动作使得选中索引只增不减,这里保证一下
            if (pageDownIndex < 0 || pageDownIndex <= anchorCard.getCardIndex()) {
                pageDownIndex = columns[columns.length - 1];
                CardViewComponent nextCard = (CardViewComponent) cardPane.getCardComponents().get(pageDownIndex);
                Point nextLocation = nextCard.getLocation();
                cardPane.scrollRectToVisible(nextCard.getBounds());
                cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                        e.isShiftDown());
                return;
            }
        }
        CardViewComponent nextCard = (CardViewComponent) cardPane.getCardComponents().get(pageDownIndex);
        Point nextLocation = nextCard.getLocation();
        // TODO:要滚动到使这个卡片在可视的第一列
        // TODO:得求出这个可视卡片是否到最后
        if (true) {
            cardPane.scrollRectToVisible(new Rectangle(new Point(nextCard.getBounds().x, 0), cardPane.getParent()
                    .getBounds().getSize()));
        }
        cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(), e.isShiftDown());
    }

    /**
     * 左方向键
     * 
     * @param e
     *            键盘事件
     * @param anchorIndex
     *            上次选中的卡片索引
     */
    private void navigateRight(
            KeyEvent e,
            int anchorIndex) {
        int[] tmpRowArr = cardPane.getRowIndexes();
        int tmpColumnNum = tmpRowArr.length;
        // 只有一行就不做
        if (tmpColumnNum <= 1) {
            return;
        }
        int lastIndexOfColumn = -1;
        for (int i = 0; i < tmpColumnNum; i++) {
            lastIndexOfColumn = tmpRowArr[i];
            if (anchorIndex <= lastIndexOfColumn) // 到当前列了
            {
                int headerDown = -1;
                if (i != 0) {
                    headerDown = tmpRowArr[i - 1];
                }
                int delta = anchorIndex - headerDown;

                int nextAnchorIndex = lastIndexOfColumn + delta;
                if (nextAnchorIndex > tmpRowArr[tmpRowArr.length - 1]) {
                    nextAnchorIndex = tmpRowArr[tmpRowArr.length - 1];
                }
                CardViewComponent nextCard = (CardViewComponent) cardPane.getCardComponents().get(nextAnchorIndex);
                Point nextLocation = nextCard.getLocation();
                cardPane.scrollRectToVisible(nextCard.getBounds());
                cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                        e.isShiftDown());
                break;
            }
        }
    }

    /**
     * 右方向键
     * 
     * @param e
     *            键盘事件
     * @param anchorIndex
     *            上次选中的卡片索引
     */
    private void navigateLeft(
            KeyEvent e,
            int anchorIndex) {
        int[] tmpRowArr = cardPane.getRowIndexes();
        int tmpColumnNum = tmpRowArr.length;
        // 只有一行就不做
        if (tmpColumnNum <= 1) {
            return;
        }
        if (anchorIndex <= tmpRowArr[0]) {
            return;
        }
        int lastIndexOfColumn = -1;
        for (int i = 0; i < tmpColumnNum; i++) {
            lastIndexOfColumn = tmpRowArr[i];
            if (anchorIndex <= lastIndexOfColumn) // 到当前列了
            {
                int headerDown = -1;
                if (i != 0) {
                    headerDown = tmpRowArr[i - 1];
                }
                // int delta = anchorIndex - headerDown;

                int nextAnchorIndex = anchorIndex - (lastIndexOfColumn - headerDown);
                if (nextAnchorIndex < 0) {
                    nextAnchorIndex = 0;
                }
                CardViewComponent nextCard = (CardViewComponent) cardPane.getCardComponents().get(nextAnchorIndex);
                Point nextLocation = nextCard.getLocation();
                cardPane.scrollRectToVisible(nextCard.getBounds());
                cardPane.selectCard(new Point(nextLocation.x + 5, nextLocation.y + 5), e.isControlDown(),
                        e.isShiftDown());
                break;
            }
        }
    }

    // /** 判断是否是操作键
    // * @return 是否是操作键
    // * @param e 键盘事件 */
    // private boolean isNavigateKey(KeyEvent e)
    // {
    // boolean result = false;
    // int keyCode = e.getKeyCode();
    // if (keyCode == KeyEvent.VK_UP
    // || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT
    // || keyCode == KeyEvent.VK_RIGHT || keyCode == KeyEvent.VK_HOME
    // || keyCode == KeyEvent.VK_END)
    // {
    // result = true;
    // }
    // return result;
    // }

    /**
     * 卡片面板的引用
     */
    private CardViewPanel cardPane;
}
