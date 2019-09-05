package com.stgo.security.monitor.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.StreamingOutput;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.opentext.otsp.server.OTMuleServer;
import com.stgo.security.monitor.ProtectionOperator;

public class FileUtil {

    // ========================== sending file to client =========================
    /**
     * write the zip file to StreamingOutput and return it to client side.
     * 
     * @param zipFilePath
     *            Path of zi[p file to convert this file to streaming output.
     * @return Streaming output.
     */
    public static StreamingOutput convertFileToStreammingOutput(
            String tempFolderPath,
            String fileName) {
        File tmpFolder = new File(tempFolderPath);
        if (!tmpFolder.exists()) {
            return null;
        }

        final File zipFile = new File(tmpFolder, fileName);

        final StreamingOutput streamingOutput = new StreamingOutput() {

            /**
             * @see javax.ws.rs.core.StreamingOutput#write(java.io.OutputStream)
             */
            @Override
            public void write(
                    final OutputStream outputStream) throws IOException, WebApplicationException {
                java.nio.file.Path path = null;
                try {
                    path = Paths.get(zipFile.getPath());

                    byte[] data = Files.readAllBytes(path);
                    outputStream.write(data);
                    outputStream.flush();
                } catch (Exception e) {
                    NetUtil.writeLog(NetUtil.KEY_ALARM_PRF,
                            "Unexpected exception when writing file into client socket. ");
                    throw new WebApplicationException();
                } finally {
                    // do not delete zip file from server. other client also need to down load it.
                    // FileUtils.deleteQuietly(zipFile);
                }
            }
        };
        return streamingOutput;
    }

    // =========================== when client got file from server side ============================
    /**
     * Save inputStream in output path.
     * 
     * @param inputStream
     *            The inputStream which is received from model exported service.
     * @param outputPath
     *            The inputStream will be saved in this path.
     * @return If inputStream is saved successfully, method will return 'true'.
     * @throws IOException
     */
    public static boolean saveDownloadedFile(
            final InputStream inputStream) throws IOException {
        InputStream nonNullInputStream = checkStreamIsNotEmpty(inputStream);
        if (nonNullInputStream == null) {
            return false;
        }

        File folder = new File(ProtectionOperator.tempFolderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }

        final OutputStream outputStream =
                new FileOutputStream(ProtectionOperator.tempFolderPath + "system_security_monitor.zip");
        IOUtils.copy(nonNullInputStream, outputStream);
        if (nonNullInputStream != null) {
            nonNullInputStream.close();
        }
        outputStream.close();
        return true;
    }

    /**
     * Check the input stream and throw exception if it is null or empty.
     * 
     * @param inputStream
     *            Value of input stream to validate.
     * @return It is same as the input stream which is in input parameter of the method..
     * @throws IOException
     * @throws EmptyInputStreamException
     */
    private static InputStream checkStreamIsNotEmpty(
            InputStream inputStream) {

        if (inputStream == null) {
            return null;
        }
        PushbackInputStream pushbackInputStream = new PushbackInputStream(inputStream);
        int b;
        try {
            b = pushbackInputStream.read();
            if (b == -1) {
                System.out.println("the content in inputstream is empty.");
                return null;
            }
            pushbackInputStream.unread(b);
        } catch (Exception e) {
            return null;
        }
        return pushbackInputStream;
    }

    // --------------------------------upgrade------------------------------------------
    public static void upgrade() {// already know that down loaded file system_security_monitor.zip is under temporal
                                  // folder.
        ZipInputStream zipInputStream = null; // so, create zipinputStream for it before unzip it.
        try {
            final InputStream inputStream =
                    new FileInputStream(ProtectionOperator.tempFolderPath + "system_security_monitor.zip");
            zipInputStream = new ZipInputStream(inputStream);
        } catch (FileNotFoundException e) {
            NetUtil.writeLog(NetUtil.KEY_ALARM_PRF, "Error occur when creating zip file in temp folder." + e.toString());
            throw new NotAcceptableException("Non-exist file for upgrading.");
        }

        System.out.println("unzipping file.");
        try {
            FileUtil.cleanUnzipFolder(ProtectionOperator.unzipFolderPath); // unzip the zipStream to cache folder.
            FileUtil.unzipZipInputStreamToLocation(ProtectionOperator.unzipFolderPath, zipInputStream);
        } catch (Exception e) {
            NetUtil.writeLog(NetUtil.KEY_ALARM_PRF, "Error occured when unziping" + e.getClass().getName());
            throw new NotAcceptableException("Non-exist file for upgrading.");
        }

        DBUtil.setVersion(String.valueOf(new Date().getTime()));// DBUtil.getVersion(); //update the new version with
                                                                // current time long value
        // restart the service.
        OTMuleServer.instance.setIsRestartRequired(true); // tell mule to restart.
    }

    /**
     * make sure check the folder is exist and be cleaned.
     */
    private static void cleanUnzipFolder(
            String unzipFolderPath) {

        if (unzipFolderPath == null) {
            System.out.println("None valid vairible unzipFolderPath detected!");
        }

        File directory = new File(unzipFolderPath);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        try {
            FileUtils.cleanDirectory(directory);
        } catch (IOException e) {
            // LOG.error("the path for unzipping the file is not valid, a writable existing directory is expected.");
            System.out
                    .println("the path for unzipping the file is not valid, a writable existing directory is expected.");
        }
    }

    /**
     * unzip the file onto the local disk
     *
     * @param zipInputStream
     * @throws IOException
     */
    private static void unzipZipInputStreamToLocation(
            String unzipFolderPath,
            final ZipInputStream zipInputStream) throws IOException {

        // buffer to read/write data to file
        byte[] buffer = new byte[2048];

        ZipEntry entry = zipInputStream.getNextEntry();

        while (entry != null) {
            String folderPath = unzipFolderPath;
            String entryName = entry.getName();
            int p = entryName.indexOf("/");
            if (p >= 0) {
                folderPath = unzipFolderPath + entryName.substring(0, p);
                File folder = new File(folderPath);
                if (!folder.exists()) {
                    folder.mkdir();
                }
                entryName = entryName.substring(p + 1);
            }
            File file = new File(folderPath, entryName);
            System.out.println("Unzipping file " + entryName + " to " + file.getAbsolutePath());

            // create the directories of each entry
            if (entry.isDirectory()) {
                File newDir = new File(file.getAbsolutePath());
                if (!newDir.exists()) {
                    boolean success = newDir.mkdirs();
                    if (success == false) {
                        System.out.println("can not create the directory: " + file.getAbsolutePath());
                        throw new InternalServerErrorException("error occurred when creating folder:"
                                + file.getAbsolutePath());
                    }
                }
            } else {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                int count = 0;
                while ((count = zipInputStream.read(buffer)) > 0) {
                    // write 'count' bytes to the file output stream
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
            }
            // close ZipEntry and take the next one
            zipInputStream.closeEntry();
            entry = zipInputStream.getNextEntry();
        }

        // close the last ZipEntry
        zipInputStream.closeEntry();
        zipInputStream.close();
    }

}
