package org.cas.client.platform.casutil;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.Week;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

/**
 * An example of a time series chart. For the most part, default settings are used, except that the renderer is modified
 * to show filled shapes (as well as lines) at each data point.
 */
public class JMM extends JFrame implements ActionListener, WindowListener {
    public static void main(
            String[] args) {
        JMM demo = new JMM("CASTool");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
        new Thread(new Runnable() {
            public void run() {
                Runtime tRuntime = Runtime.getRuntime();
                int i = 8;
                int j = 1;
                isAlive = true;
                while (isAlive) {
                    try {
                        Thread.sleep(sleeptime);
                        if (pause) {
                            s1.add(new Week(i % 52 + 1, 2002 + j), tRuntime.totalMemory());
                            s2.add(new Week(i % 52 + 1, 2002 + j), tRuntime.freeMemory());
                            if (i % 52 == 0)
                                j++;
                            i++;
                            if (s1.getItemCount() > pointCount) {
                                s1.delete(0, 0);
                                s2.delete(0, 0);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("exception occured!");
                    }
                }
            }
        }).start();
    }

    public JMM(String title) {
        super(title);
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 280));
        chartPanel.setMouseZoomable(true, false);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(chartPanel);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        tPause = new JToggleButton("暂停");
        JLabel tLabel = new JLabel("  采样频率设置");
        tComboBox = new JComboBox();
        JLabel tLabel5 = new JLabel("  采样数量设置");
        tComboBox3 = new JComboBox();
        JLabel tLabel3 = new JLabel("  连续执行");
        tComboBox2 = new JComboBox();
        JLabel tLabel4 = new JLabel("次");
        btnGC = new JButton("垃圾收集");

        String[] spanAry =
                new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "20", "40", "60", "80", "100", "300",
                        "500", "700", "900", "1000", "2000", "3000", "4000", "5000", "6000", "7000", "8000", "9000" };
        String[] timeAry = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
        String[] pointLenAry = new String[] { "500", "750", "1000", "1250", "1500", "1750", "2000" };

        tComboBox2.setModel(new DefaultComboBoxModel(timeAry));
        tComboBox.setModel(new DefaultComboBoxModel(spanAry));
        tComboBox3.setModel(new DefaultComboBoxModel(pointLenAry));
        tComboBox2.setSelectedIndex(0);
        tComboBox.setSelectedIndex(14);
        tComboBox3.setSelectedIndex(0);
        tPause.setFont(new Font("Dialog", 0, 12));
        tLabel.setFont(tPause.getFont());
        tComboBox.setFont(tPause.getFont());
        tLabel3.setFont(tPause.getFont());
        tComboBox2.setFont(tPause.getFont());
        tLabel4.setFont(tPause.getFont());
        btnGC.setFont(tPause.getFont());
        tLabel5.setFont(tPause.getFont());
        tComboBox3.setFont(tPause.getFont());
        tPause.setPreferredSize(new Dimension(tPause.getPreferredSize().width, 20));
        tLabel.setPreferredSize(new Dimension(tLabel.getPreferredSize().width, 20));
        tComboBox.setPreferredSize(new Dimension(tComboBox.getPreferredSize().width, 20));
        btnGC.setPreferredSize(new Dimension(btnGC.getPreferredSize().width, 20));
        tLabel3.setPreferredSize(new Dimension(tLabel3.getPreferredSize().width, 20));
        tComboBox2.setPreferredSize(new Dimension(tComboBox2.getPreferredSize().width, 20));
        tLabel4.setPreferredSize(new Dimension(tLabel4.getPreferredSize().width, 20));
        tLabel5.setPreferredSize(new Dimension(tLabel5.getPreferredSize().width, 20));
        tComboBox3.setPreferredSize(new Dimension(tComboBox3.getPreferredSize().width, 20));

        controlPanel.add(tPause);
        controlPanel.add(tLabel);
        controlPanel.add(tComboBox);
        controlPanel.add(tLabel5);
        controlPanel.add(tComboBox3);
        controlPanel.add(tLabel3);
        controlPanel.add(tComboBox2);
        controlPanel.add(tLabel4);
        controlPanel.add(btnGC);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        tPause.addActionListener(this);
        tComboBox.addActionListener(this);
        tComboBox3.addActionListener(this);
        btnGC.addActionListener(this);
        addWindowListener(this);
    }

    public void actionPerformed(
            ActionEvent e) {
        Object o = e.getSource();
        if (o == tPause) {
            if (tPause.isSelected())
                tPause.setText("开始");
            else
                tPause.setText("暂停");
            setPause();
        } else if (o == tComboBox) {
            String tTime = (String) tComboBox.getSelectedItem();
            sleeptime = Integer.parseInt(tTime);
        } else if (o == tComboBox3) {
            String tCount = (String) tComboBox3.getSelectedItem();
            int tPC = Integer.parseInt(tCount);
            if (tPC >= pointCount)
                pointCount = Integer.parseInt(tCount);
            else {
                s1.delete(0, pointCount - tPC);
                s2.delete(0, pointCount - tPC);
            }
        } else if (o == btnGC) {
            String times = (String) tComboBox2.getSelectedItem();
            int tCount = Integer.parseInt(times);
            for (int i = 0; i < tCount; i++)
                System.gc();
        }
    }

    public void windowOpened(
            WindowEvent e) {
    }

    public void windowClosing(
            WindowEvent e) {
        isAlive = false;
        sleeptime = 100;
        pause = true;
        s1 = null;
        s2 = null;
        tPause = null;
        tComboBox = null;
        tComboBox2 = null;
        btnGC = null;
        dispose();
    }

    public void windowClosed(
            WindowEvent e) {
    }

    public void windowIconified(
            WindowEvent e) {
    }

    public void windowDeiconified(
            WindowEvent e) {
    }

    public void windowActivated(
            WindowEvent e) {
    }

    public void windowDeactivated(
            WindowEvent e) {
    }

    private static JFreeChart createChart(
            XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart("虚拟机内存使用情况监视工具", // title
                "时间", // x-axis label
                "内存(b)", // y-axis label
                dataset, // data
                true, // create legend?
                false, // generate tooltips?
                false // generate URLs?
                );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof XYLineAndShapeRenderer) {
            XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) r;
            renderer.setBaseShapesVisible(false);
            renderer.setBaseShapesFilled(false);
        }
        return chart;
    }

    private static XYDataset createDataset() {
        s1 = new TimeSeries("申请内存总量", Week.class);
        s2 = new TimeSeries("申请空间内尚未使用的内存总量", Week.class);
        TimeSeriesCollection dataset = new TimeSeriesCollection();
        dataset.addSeries(s1);
        dataset.addSeries(s2);
        dataset.setDomainIsPointsInTime(true);
        return dataset;
    }

    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        return new ChartPanel(chart);
    }

    public static void setPause() {
        pause = !pause;
    }

    static boolean isAlive = true;
    static TimeSeries s1;
    static TimeSeries s2;
    static int sleeptime = 100;
    static int pointCount = 500;
    static boolean pause = true;
    static JToggleButton tPause;
    static JComboBox tComboBox;
    static JComboBox tComboBox2;
    static JComboBox tComboBox3;
    static JButton btnGC;
}
