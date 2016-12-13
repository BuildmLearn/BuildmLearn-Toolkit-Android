package org.buildmlearn.toolkit.utilities;

import android.content.Context;
import android.content.res.AssetManager;

import org.w3c.dom.Document;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * @brief Helper functions related to String manipulation.
 * <p/>
 * Created by Abhishek on 23-05-2015.
 */
public class FileUtils {

    private final static int BUFFER_SIZE = 2048;


    /**
     * @param zipFilePath       Path of the source zip file
     * @param destinationFolder Destination folder for stroing the uncompresses files.
     * @throws IOException Exception thrown in case of some error.
     * @brief Unzips a compressed file (.zip, .apk)
     */
    public static void unZip(String zipFilePath, String destinationFolder) throws IOException {
        InputStream zipInputStream = new FileInputStream(zipFilePath);
        unZip(zipInputStream, destinationFolder);
    }

    /**
     * @param zipInputStream    InputStream of Zip file
     * @param destinationFolder Destination folder for stroing the uncompresses files.
     * @throws IOException Exception thrown in case of some error.
     * @brief Unzips a compressed file (.zip, .apk)
     */
    public static void unZip(InputStream zipInputStream, String destinationFolder) {
        int size;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            if (!destinationFolder.endsWith("/")) {
                destinationFolder += "/";
            }
            File f = new File(destinationFolder);
            if (!f.isDirectory()) {
                f.mkdirs();
            }

            ZipInputStream zin = null;
            try {
                zin = new ZipInputStream(new BufferedInputStream(zipInputStream, BUFFER_SIZE));
                ZipEntry ze;
                while ((ze = zin.getNextEntry()) != null) {
                    String path = destinationFolder + ze.getName();
                    File unzipFile = new File(path);

                    if (ze.isDirectory()) {
                        if (!unzipFile.isDirectory()) {
                            unzipFile.mkdirs();
                        }
                    } else {

                        File parentDir = unzipFile.getParentFile();
                        if (null != parentDir && !parentDir.isDirectory()) {
                            parentDir.mkdirs();
                        }

                        FileOutputStream out = new FileOutputStream(unzipFile, false);
                        BufferedOutputStream fout = new BufferedOutputStream(out, BUFFER_SIZE);
                        try {
                            while ((size = zin.read(buffer, 0, BUFFER_SIZE)) != -1) {
                                fout.write(buffer, 0, size);
                            }

                            zin.closeEntry();
                        } finally {
                            fout.flush();
                            fout.close();
                        }
                    }
                }
            } finally {
                if (zin != null) {
                    zin.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context              Application context
     * @param assetFileName        Name of the file stored in assets
     * @param destinationDirectory Destination folder for saving the file
     * @brief Copies a file from assets folder to a folder on device memory
     */
    public static void copyAssets(Context context, String assetFileName, String destinationDirectory) {
        AssetManager assetManager = context.getAssets();
        InputStream in;
        OutputStream out;
        try {
            in = assetManager.open(assetFileName);
            File f = new File(destinationDirectory);
            if (!f.isDirectory()) {
                f.mkdirs();
            }

            File outFile = new File(destinationDirectory, assetFileName);
            out = new FileOutputStream(outFile);
            copyFile(in, out);
            in.close();
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[BUFFER_SIZE];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }

    //Compare to File for Equal Contents
    public static boolean equalContent(File file1, File file2) {
        //Indenitifier with 1 suffix corresponds for file1
        byte[] buffer1 = new byte[BUFFER_SIZE];
        byte[] buffer2 = new byte[BUFFER_SIZE];
        int read1;
        int read2;
        InputStream is1 = null;
        InputStream is2 = null;
        try {
            is1 = new FileInputStream(file1);
            is2 = new FileInputStream(file2);

            while ((read1 = is1.read(buffer1)) != -1) {
                read2 = is2.read(buffer2);
                if (read1 != read2)
                    return false;   //Different Buffer Length

                if (!Arrays.equals(buffer1, buffer2))
                    return false;
            }
            //Final Read
            read2 = is2.read(buffer2);
            if (read2 != -1)
                return false;   //File2 closed
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {

            try {
                if (is1 != null)
                    is1.close();
                if (is2 != null)
                    is2.close();
            } catch (IOException ignored) {

            }

        }
        return true;
    }


    /**
     * @param destinationFolder Destination folder for saving the file
     * @param fileName          Destination file name
     * @param doc               Document object to be converted to xml formatted file
     * @return Returns true if successfully converted
     * @brief Converts a given Document object to xml format file
     */
    public static boolean saveXmlFile(String destinationFolder, String fileName, Document doc) {

        File f = new File(destinationFolder);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            File newTemplateFile=new File(destinationFolder + fileName);
            if(newTemplateFile.exists())
                return false;
            transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(newTemplateFile);
            transformer.transform(source, result);

        } catch (TransformerException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @param directoryToZipPath Source folder to be converted.
     * @throws IOException
     * @brief Archives a folder into .zip compressed file
     */
    public static void zipFolder(String directoryToZipPath) throws IOException {
        File directoryToZip = new File(directoryToZipPath);

        List<File> fileList = new ArrayList<>();
        System.out.println("---Getting references to all files in: " + directoryToZip.getCanonicalPath());
        getAllFiles(directoryToZip, fileList);
        System.out.println("---Creating zip file");
        writeZipFile(directoryToZip, fileList);
        System.out.println("---Done");
    }

    /**
     * @param dir      Source directory
     * @param fileList Referenced list. Files are added to this list
     * @brief Add all the files in a given folder into a list
     */
    private static void getAllFiles(File dir, List<File> fileList) {
        try {
            File[] files = dir.listFiles();
            for (File file : files) {
                fileList.add(file);
                if (file.isDirectory()) {
                    System.out.println("directory:" + file.getCanonicalPath());
                    getAllFiles(file, fileList);
                } else {
                    System.out.println("     file:" + file.getCanonicalPath());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeZipFile(File directoryToZip, List<File> fileList) {

        try {
            FileOutputStream fos = new FileOutputStream(directoryToZip.getAbsolutePath() + ".zip");
            ZipOutputStream zos = new ZipOutputStream(fos);

            for (File file : fileList) {
                if (!file.isDirectory()) { // we only zip files, not directories
                    addToZip(directoryToZip, file, zos);
                }
            }

            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addToZip(File directoryToZip, File file, ZipOutputStream zos) throws
            IOException {

        FileInputStream fis = new FileInputStream(file);

        // we want the zipEntry's path to be a relative path that is relative
        // to the directory being zipped, so chop off the rest of the path
        String zipFilePath = file.getCanonicalPath().substring(directoryToZip.getCanonicalPath().length() + 1,
                file.getCanonicalPath().length());
        System.out.println("Writing '" + zipFilePath + "' to zip file");
        ZipEntry zipEntry = new ZipEntry(zipFilePath);
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }

        zos.closeEntry();
        fis.close();
    }

    /**
     * @param src Source file
     * @param dst Destination file
     * @throws IOException Exception thrown in case of error
     * @brief Copies the content from one file to another
     */
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}

