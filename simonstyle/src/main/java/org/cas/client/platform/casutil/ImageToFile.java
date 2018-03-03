package org.cas.client.platform.casutil;

import java.awt.Image;
import java.awt.image.renderable.ParameterBlock;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.media.jai.JAI;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedOp;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;

public class ImageToFile {
    /*
     * ImageToFile()
     * @param: Image is object
     * @param: fileName
     */
    public static void imageToFile(
            Image image,
            String fileName) {
        // long threadTime = 5 ;
        // debug 66485 , 2004 / 3 /15 ,解决存盘大小写问题.
        if (fileName.toLowerCase().endsWith(".bmp")) {
            imageToBMP(image, fileName);
        }
        if (fileName.toLowerCase().endsWith(".png")) {
            imageToPNG(image, fileName);
        }
        if (fileName.toLowerCase().endsWith(".jpg")) {
            imageToJPG(image, fileName);
        }
        if (fileName.toLowerCase().endsWith(".tiff") || fileName.toLowerCase().endsWith(".tif")) {
            imageToTIFF(image, fileName);
        }
        /*
         * try { Thread.sleep(threadTime); } catch(Exception e) { SOptionPane.showErrorDialog("w10418"); return; }
         */
    }

    private static void imageToPNG(
            Image image,
            String PNGFilePath) {
        try {
            ParameterBlock pb = new ParameterBlock();
            pb.add(image);
            PlanarImage tPlanarImage = (PlanarImage) JAI.create("awtImage", pb, null);
            OutputStream output = new FileOutputStream(PNGFilePath);
            ImageEncoder tEncode = ImageCodec.createImageEncoder("png", output, null);
            tEncode.encode(tPlanarImage);
            output.close();
            image = null;
            tPlanarImage = null;
        } catch (Exception e) {
            SOptionPane.showErrorDialog("w10418");
            return;
        }
    }

    private static void imageToTIFF(
            Image image,
            String TIFFilePath) {
        try {
            String path = CASUtility.getPIMDirPath().concat(File.separator).concat("Temp").concat(File.separator);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String outPutPath = path + "convert.png";
            ParameterBlock pb = new ParameterBlock();
            pb.add(image);
            PlanarImage tPlanarImage = (PlanarImage) JAI.create("awtImage", pb, null);
            JAI.create("filestore", tPlanarImage, outPutPath, "png", null);
            RenderedOp drc = JAI.create("fileload", outPutPath);
            JAI.create("filestore", drc, TIFFilePath, "tiff", null);
            image = null;
            tPlanarImage = null;
        } catch (Exception e) {
            SOptionPane.showErrorDialog("w10418");
            return;
        }
    }

    /**
     * 转换为JPEG文件
     * 
     * @param image
     *            BufferImage，用于保存图片
     * @param JPGFilePath
     *            保存图片的文件路径
     * @exception java.io.IOException
     *                , java.io.FileNotFoundException
     */
    public static void imageToJPGForWP(
            Image image,
            String JPGFilePath) throws java.io.IOException, java.io.FileNotFoundException {
        String path = CASUtility.getPIMDirPath().concat(File.separator).concat("Temp").concat(File.separator);
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        ParameterBlock pb = new ParameterBlock();
        pb.add(image);
        PlanarImage tPlanarImage = (PlanarImage) JAI.create("awtImage", pb, null);
        String outPutPath = path + "convert.png";
        OutputStream output = new FileOutputStream(outPutPath);
        ImageEncoder tEncode = ImageCodec.createImageEncoder("png", output, null);
        tEncode.encode(tPlanarImage);
        output.close();
        RenderedOp drc = JAI.create("fileload", outPutPath);
        JAI.create("filestore", drc, JPGFilePath, "jpeg", null);
        image = null;
        tPlanarImage = null;
    }

    private static void imageToJPG(
            Image image,
            String JPGFilePath) {
        try {
            imageToJPGForWP(image, JPGFilePath);
        } catch (Exception e) {
            SOptionPane.showErrorDialog("w10418");
            return;
        }
    }

    private static void imageToBMP(
            Image image,
            String BMPFilePath) {
        try {
            String path = CASUtility.getPIMDirPath().concat(File.separator).concat("Temp").concat(File.separator);
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String outPutPath = path + "convert.png";
            ParameterBlock pb = new ParameterBlock();
            pb.add(image);
            PlanarImage tPlanarImage = (PlanarImage) JAI.create("awtImage", pb, null);
            JAI.create("filestore", tPlanarImage, outPutPath, "png", null);
            RenderedOp drc = JAI.create("fileload", outPutPath);
            JAI.create("filestore", drc, BMPFilePath, "bmp", null);
            image = null;
            tPlanarImage = null;
        } catch (Exception e) {
            SOptionPane.showErrorDialog("w10418");
            return;
        }
        /*
         * int w = image.getWidth(null); int h = image.getHeight(null); int[] pixels = new int[w*h]; PixelGrabber pg;
         * int[] rgbArray = new int[w*h*3]; try { pg = new PixelGrabber(image,0,0,w,h,pixels,0,w); pg.grabPixels(); }
         * catch(InterruptedException e) { SOptionPane.showErrorDialog("w10418"); return; } java.awt.image.ColorModel
         * defaultRGB= java.awt.image.ColorModel.getRGBdefault(); int z = 0; for (int i = 0; i<w*h;i++) { rgbArray[z] =
         * defaultRGB.getRed(pixels[i]); rgbArray[z+1] = defaultRGB.getGreen(pixels[i]); rgbArray[z+2] =
         * defaultRGB.getBlue(pixels[i]); z += 3; } PreviewtoBmp previewToBmp = new PreviewtoBmp();
         * previewToBmp.covertImageToBmp(rgbArray,h,w,BMPFilePath);
         */
    }
}
