package org.cas.client.platform.casbeans;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;

import org.cas.client.platform.CASControl;
import org.cas.client.platform.ICASFrame;
import org.cas.client.platform.cascustomize.CustOpts;
import org.cas.client.platform.casutil.JMM;
import org.cas.client.platform.casutil.PIMPool;
import org.cas.client.resource.international.PaneConsts;

public class PIMBarOptimize extends JToolBar implements ActionListener, MouseListener {
    // 构造方法
    public PIMBarOptimize() {
        init();
    }

    private void init() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 2, 0));
        setFloatable(false);
        // 初始化------------------------
        dragLabel = new JLabel();
        Icon gcIcon = new ImageIcon(PIMPool.pool.getImage(PaneConsts.IAMGE_PATH.concat("garbageCollect.gif")));

        memoryProgressBar = new PIMProgressBar();
        progressModel = new DefaultBoundedRangeModel();
        gcBtn = new JButton(gcIcon);// TODO: 更换提示信息和Icon

        // 属性---------------------------------
        dragLabel.setIcon(CustOpts.custOps.getHorBarIcon());
        gcBtn.setToolTipText("内存回收");
        gcBtn.setOpaque(false);
        gcBtn.setBorder(null);

        progressModel.setMaximum(100);
        memoryProgressBar.setModel(progressModel);
        Runtime tmpRuntime = Runtime.getRuntime();
        initTotalMem = tmpRuntime.totalMemory();
        long tmpFreeMemory = tmpRuntime.freeMemory();
        memoryProgressBar.setValue((int) ((((double) (initTotalMem - tmpFreeMemory)) / (double) initTotalMem) * 100));
        String progressTip =
                "(已使用内存)" + (((double) (initTotalMem - tmpFreeMemory)) / (1024 * 1024)) + "M/(被分配内存)"
                        + ((double) initTotalMem / (1024 * 1024)) + 'M';
        memoryProgressBar.setToolTipText(progressTip);

        // 搭建----------------------------
        add(dragLabel);
        add(memoryProgressBar);
        add(gcBtn);

        // 布局----------------------------
        initMemBarWidth = memoryProgressBar.getPreferredSize().width / 4;
        memoryProgressBar.setPreferredSize(new Dimension(initMemBarWidth, CustOpts.BTN_HEIGHT - 6));
        gcBtn.setPreferredSize(new Dimension(CustOpts.BTN_HEIGHT - 2, CustOpts.BTN_HEIGHT - 2));
        resetAllButton();

        // 监听----------------------------
        gcBtn.addActionListener(this);
        addMouseListener(this);
    }

    /** Invoked when an action occurs. */
    @Override
    public void actionPerformed(
            ActionEvent e) {
        Object source = e.getSource();
        if (source == gcBtn) {
            // for (int i = 9; i-- >= 0;){
            // System.gc();
            // System.runFinalization();
            // }
            // refreshMemoryInfo();
            JMM.main(null);
        }
    }

    /** Invoked when the mouse button has been clicked (pressed and released) on a component. */
    @Override
    public void mouseClicked(
            MouseEvent e) {
    }

    /** Invoked when the mouse enters a component. */
    @Override
    public void mouseEntered(
            MouseEvent e) {
    }

    /** Invoked when the mouse exits a component. */
    @Override
    public void mouseExited(
            MouseEvent e) {
    }

    /** Invoked when a mouse button has been pressed on a component. */
    @Override
    public void mousePressed(
            MouseEvent e) {
    }

    /** Invoked when a mouse button has been released on a component. */
    @Override
    public void mouseReleased(
            MouseEvent e) {
    }

    /**
     * 刷新memory工具条的信息，在gc之后，给用户一个形象的内存印象。 1、随着申请内存的增加，progressbar的长度也随之增加 2、已用内存与整个内存的比例随时调整
     */
    public void refreshMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory(); // 分配内容
        long freeMemory = runtime.freeMemory(); // 空闲内存
        int value = (int) ((((double) (totalMemory - freeMemory)) / (double) totalMemory) * 100);// 已用内存
        memoryProgressBar.setValue(value);
        memoryProgressBar.setToolTipText("(已使用内存)".concat(
                (double) (totalMemory - freeMemory) / (1024 * 1024) + "M/(被分配内存)").concat(
                (double) totalMemory / (1024 * 1024) + "M"));

        Dimension tmpDimen = memoryProgressBar.getPreferredSize();
        double tmpScale = (double) totalMemory / (double) initTotalMem;
        int tmpW = (int) (initMemBarWidth * tmpScale);
        memoryProgressBar.setPreferredSize(new Dimension(tmpW, tmpDimen.height));
        resetAllButton();

        ((ICASFrame) CASControl.ctrl.getMainFrame()).reLayout(); // 通知父窗体重排工具条.
    }

    private void resetAllButton() // this method can be reused
    {
        Component[] tmpComps = getComponents();
        int space = 2;
        boolean isSeparator = true;
        int bw = 0;
        this.removeAll();
        this.revalidate();

        // Add component to toolbar
        for (int i = 0; i < tmpComps.length; i++) {
            Component comp = tmpComps[i];
            if (comp instanceof JButton)
                if (!comp.isVisible())
                    continue;
            int w = comp.getPreferredSize().width;
            int h = comp.getPreferredSize().height;
            if (h > this.getHeight())
                h = this.getHeight();
            if (comp instanceof JSeparator) {
                if (isSeparator)
                    continue;
                isSeparator = true;
                comp.setBounds(bw, space, w, 25 - 4);
            } else {
                isSeparator = false;
                comp.setBounds(bw, (this.getHeight() - h) / 2, w, h);
            }
            add(comp);
            bw += w;
        }
        setPreferredSize(new Dimension(bw + 10, CustOpts.BAR_HEIGHT));
    }

    private JLabel dragLabel;
    private JButton gcBtn;
    private DefaultBoundedRangeModel progressModel;
    private PIMProgressBar memoryProgressBar;
    private int initMemBarWidth;
    private long initTotalMem;
}
