package org.cas.client.platform.cascontrol.frame;

/**PIM单独运行时的菜单，由PIMBarManager.java调用，设置
 * 本类增加了缓冲，使尽量少、晚地实例化JMenu、JMenuItem、及其Action等对象。没有完全采用即时实例化
 * 临时变量地机制，主要是为防止垃圾回收机制跟不上内存开辟地速度。后来者可以根据高版本虚拟机的改进做进一步优化。（user008）
 */
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.border.EmptyBorder;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.cascontrol.IStatCons;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.CASUtility;
import org.cas.client.platform.casutil.ErrorUtil;
import org.cas.client.platform.casutil.PIMPool;

class CASMenuBar extends JMenuBar implements MenuListener, ActionListener {
    /**
     * Creates a new instance of PIMMenuBar 菜单栏
     * 
     * @param prmParent
     *            : 菜单的父窗体
     */
    CASMenuBar(Frame prmParent) {
        setLayout(new FlowLayout(FlowLayout.LEFT, 0, 2));
        JLabel tmpDragLabel = new JLabel();
        tmpDragLabel.setIcon(CustOpts.custOps.getHorBarIcon());
        tmpDragLabel.setBorder(new EmptyBorder(0, 3, 0, 2));
        add(tmpDragLabel);

        String tmpMenuString = (String) CustOpts.custOps.hash2.get("Menus");
        String tmpMnemonicStr = (String) CustOpts.custOps.hash2.get("Mnemonic");
        Vector tmpMenuStrs = CASUtility.parserStrToVec(tmpMenuString, CustOpts.BIAS, tmpMenuString.length());
        Vector tmpMnemonicStrs = CASUtility.parserStrToVec(tmpMnemonicStr, CustOpts.BIAS, tmpMnemonicStr.length());

        int tmpLength = tmpMenuStrs.size();
        if (tmpLength != tmpMnemonicStrs.size())// 小心起见，对Config2中的内容有效性做个除错处理。
        {
            ErrorUtil
                    .write("Please check the Config2.txt, the size of menus is not match with the size of mnemonic: TopMenu");
        } else {
            JMenu tmpMenu;
            for (int i = 0; i < tmpLength; i++) {
                String tmpCaption = (String) tmpMenuStrs.get(i);
                tmpMenu = new JMenu(tmpCaption);
                tmpMenu.setMnemonic(((String) tmpMnemonicStrs.get(i)).trim().charAt(0));
                tmpMenu.addMenuListener(this);
                add(tmpMenu);
            }
        }
    }

    /**
     * Invoked when a menu is selected. 当用户选中某个菜单时，增加这个菜单下的相应的MenuItem项。
     * 
     * @param e
     *            a MenuEvent object
     */
    public void menuSelected(
            MenuEvent e) {
        JMenu tmpActiveMenu = (JMenu) e.getSource(); // 用户所选即为活动菜单，得到其上的文字，
        String tmpText = tmpActiveMenu.getText();
        Vector tmpTextVec = CustOpts.custOps.getMenuText(tmpText); // 解析存于vector中。
        Vector tmpMnemVec = CustOpts.custOps.getMenuMnem(tmpText);
        Vector tmpActionNameVec = CustOpts.custOps.getActionName(tmpText);

        int tmpLength = tmpTextVec.size();
        if (tmpTextVec == null || tmpMnemVec == null || tmpActionNameVec == null
                || tmpMnemVec.size() != tmpActionNameVec.size()) // 小心起见，对Config2中的内容有效性做个除错处理。
        {
            ErrorUtil.write("Please check the Config2.txt, the size of menus is not match with the size of mnemonic:"
                    + tmpText);
            return;
        }

        // 开始组装。
        for (int i = 0, j = 0; i < tmpLength;) // 遍历每个元素，并分别根据其是不是同时是Config2中的key来判断这个文字对应的应该是
        { // 一个Menu还是对应着一个MenuItem。
            String tmpCaption = (String) tmpTextVec.get(i);
            if (tmpCaption.indexOf('-') == 0) {
                tmpActiveMenu.addSeparator();
                i++;
            } else if (CustOpts.custOps.getMenuText(tmpCaption) != null) // 为Menu的情况。
            {
                JMenu tmpMenu = PIMPool.pool.getAMenu();
                tmpMenu.setText(tmpCaption);
                tmpMenu.setFont(tmpActiveMenu.getFont()); // 不加该句代码,字体显示为难看的灰色.
                tmpMenu.setMnemonic(((String) tmpMnemVec.get(j)).trim().charAt(0));
                tmpMenu.addMenuListener(this); // 在此构成嵌套结构，该子菜单在被选择时，也将调用到本方法，并显示子菜单和子菜单项致无限复杂。
                tmpActiveMenu.add(tmpMenu);
                i++;
                j++;
            } else // 为MenuItem的情况。
            {
                JMenuItem tmpMenuItem = PIMPool.pool.getAMenuItem();
                tmpMenuItem.setText(tmpCaption);
                tmpMenuItem.setFont(tmpActiveMenu.getFont());// 不加该句代码,字体显示为难看的灰色.
                tmpMenuItem.setMnemonic(((String) tmpMnemVec.get(j)).trim().charAt(0));
                int tmpMenuStatus = CASControl.ctrl.getStatus(tmpActionNameVec.get(j));
                if ((tmpMenuStatus & IStatCons.NEED_ALL_MATCH) > 0)// 如果定制的状态值为要求的多各位中任意位被置即有效的话.
                    tmpMenuItem.setEnabled((CASControl.ctrl.getSystemStatus() & tmpMenuStatus) == tmpMenuStatus);
                else
                    // 否则表示菜单定制的被设成置位状态的各位必须在同时满足时才有效.
                    tmpMenuItem.setEnabled((CASControl.ctrl.getSystemStatus() & tmpMenuStatus) > 0);
                tmpMenuItem.addActionListener(this);
                tmpMenuItem.getAccessibleContext().setAccessibleParent(tmpActiveMenu);
                tmpActiveMenu.add(tmpMenuItem);
                i++;
                j++;
            }
        }
        System.gc();
    }

    /**
     * Invoked when the menu is deselected.
     * 
     * @param e
     *            a MenuEvent object
     */
    public void menuDeselected(
            MenuEvent e) {
        JMenu tmpMenu = (JMenu) e.getSource();
        tmpMenu.removeAll();
        for (int i = 0; i < tmpMenu.getComponentCount(); i++) {
            Object tmpObj = tmpMenu.getComponent(i);
            if (tmpObj.getClass() == JMenu.class)
                PIMPool.pool.keepTheJMenu((JMenu) tmpObj);
            else
                PIMPool.pool.keepTheJMenuItem((JMenuItem) tmpObj);
        }
    }

    /**
     * Invoked when the menu is canceled.
     * 
     * @param e
     *            a MenuEvent object
     */
    public void menuCanceled(
            MenuEvent e) {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(
            ActionEvent e) {
        String tmpMenuText =
                ((JMenu) ((JMenuItem) e.getSource()).getAccessibleContext().getAccessibleParent()).getText();
        String tmpMnemonic = (String) CustOpts.custOps.hash2.get(tmpMenuText.concat("_Mnemonic")); // 得到Config2中的对应的助记符字串。
        int tmpIndex = tmpMnemonic.indexOf(((JMenuItem) e.getSource()).getMnemonic()) / 2;
        String tmpActionName = (String) CustOpts.custOps.hash2.get(tmpMenuText.concat("_ActionID")); // 得到Config2中的对应的助记符字串。
        Vector tmpVec = CASUtility.parserStrToVec(tmpActionName, CustOpts.BIAS, tmpActionName.length());
        tmpActionName = (String) tmpVec.get(tmpIndex);

        CASControl.ctrl.getAction(tmpActionName).actionPerformed(null);
    }
}
