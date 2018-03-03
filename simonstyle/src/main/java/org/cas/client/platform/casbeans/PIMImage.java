package org.cas.client.platform.casbeans;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.Serializable;

import javax.swing.JButton;

import org.cas.client.platform.casutil.ErrorUtil;

public class PIMImage extends JButton implements Serializable {
    /**
     * 构建器
     * 
     * @param text
     *            表示文件的字符串
     */
    public PIMImage(String text) {
        super(text);
        initComponents();
    }

    private void initComponents() {
        setDoubleBuffered(true);
    }

    /**
     * 由文件名创建图片的方法
     * 
     * @called by: ContactGeneralPanel
     * @param picture
     *            表示文件的字符串
     * @param passed
     *            图片引用
     * @return 图片对象
     */
    public Image recreateImage(
            String picture,
            Image passed) {
        this.picture = picture;
        recalcScaleRect(passed);
        image = passed.getScaledInstance(scaleRect.width, scaleRect.height, Image.SCALE_DEFAULT);
        return image;
    }

    private void recalcScaleRect(
            Image passed) {
        if (scaleRect == null) {
            int width = getWidth();
            int height = getHeight();
            int imageWidth = passed.getWidth(this);
            int imageHeight = passed.getHeight(this);
            scaleRect = new Rectangle(imageWidth, imageHeight);
            double imageRatio = (double) (imageWidth / (double) imageHeight);
            if (height < imageHeight) {
                if (width < imageWidth) {
                    int h = (int) (width / imageRatio);
                    scaleRect.width = width;
                    scaleRect.height = h;
                } else {
                    int w = (int) (height * imageRatio);
                    scaleRect.width = w;
                    scaleRect.height = height;
                }
            } else {
                if (width < imageWidth) {
                    int h = (int) (width / imageRatio);
                    scaleRect.width = width;
                    scaleRect.height = h;
                }
            }
        }
    }

    /**
     * 绘制(重载)
     * 
     * @param g
     *            图形设备
     */
    public void paintComponent(
            Graphics g) {
        super.paintComponent(g);

        if (picture != null && picture.length() != 0) {
            int width = getWidth();
            int height = getHeight();
            if (image == null) {
                image = Toolkit.getDefaultToolkit().getImage(picture);

                MediaTracker tracker = new MediaTracker(this);
                tracker.addImage(image, 0);
                try {
                    tracker.waitForID(0);
                } catch (InterruptedException ex) {
                    ErrorUtil.write(ex);
                }
            }
            int imageWidth = image.getWidth(this);
            int imageHeight = image.getHeight(this);

            Color old = g.getColor();
            g.drawImage(image, (width - imageWidth) / 2, (height - imageHeight) / 2, imageWidth, imageHeight, this);
            g.setColor(Color.black);
            g.drawRect((width - imageWidth) / 2, (height - imageHeight) / 2, imageWidth, imageHeight);
            g.setColor(old);
        }
    }

    /**
     * 设置图片
     * 
     * @called by: ContactGenralPanel
     * @param pic
     *            表示文件的字符串
     */
    public void setPicture(
            String pic) {
        String oldValue = getPicture();
        if (oldValue != null && oldValue.equals(pic)) {
            return;
        }

        picture = pic;
        repaint();
    }

    /**
     * 得到图片
     * 
     * @return 图片名
     */
    String getPicture() {
        return picture;
    }

    private Image image;
    private String picture;
    private Rectangle scaleRect;
}
