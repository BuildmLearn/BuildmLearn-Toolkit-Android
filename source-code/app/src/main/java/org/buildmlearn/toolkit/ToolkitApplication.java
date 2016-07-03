package org.buildmlearn.toolkit;

import android.app.Application;
import android.os.Environment;

import org.buildmlearn.toolkit.constant.Constants;

import java.io.File;
import java.util.ArrayList;

/**
 * @brief Extended Application class
 *
 *
 * Created by Abhishek on 31-05-2015.
 */
public class ToolkitApplication extends Application {

    private static String dir;

    private boolean isExternalStorageAvailable = false;


    @Override
    public void onCreate() {
        super.onCreate();
        if (checkExternalStorage()) {
            isExternalStorageAvailable = true;
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            dir = getFilesDir().getAbsolutePath();
        }

        ArrayList<String> paths = new ArrayList<>();
        paths.add(getProjectDir());
        paths.add(getApkDir());
        paths.add(getSavedDir());
        paths.add(getUnZipDir());

        for (String path : paths) {
            File f = new File(path);
            if (!f.isDirectory()) {
                f.mkdirs();
            }
        }


    }

    /**
     * @brief Returns external storage directory.
     * @return folder file
     */
    public File getDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * @brief Returns directory for BuildmLearn toolkit  manually created files.
     * @return folder path
     */
    public String getProjectDir() {
        return dir + Constants.BUILD_M_LEARN_PATH;

    }

    /**
     * @brief Returns folder path for saved projects
     * @return Folder path
     */
    public String getSavedDir() {
        return dir + Constants.SAVED_DIR;
    }

    /**
     * @brief Returns folder path for saved projects
     * @return Folder path
     */
    public String getDraftDir() {
        return dir + Constants.DRAFT_DIR;
    }


    /**
     * @brief Returns folder path for unzipped apks
     * @return Folder path
     */
    public static String getUnZipDir() {
        return dir + Constants.UNZIP;
    }

    /**
     * @brief Returns folder path for storing generated apks
     * @return Folder path
     */
    public String getApkDir() {
        return dir + Constants.APK_DIR;
    }

    /**
     * @brief Checks if external storage is present for storing data
     * @return true if external storage is present, else false
     */
    public boolean checkExternalStorage() {

        boolean result = false;
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BuildmLearn123/");
        if (!f.isDirectory()) {
            result = f.mkdirs();
            f.delete();
        }
        return result;
    }

    public boolean isExternalStorageAvailable() {
        return isExternalStorageAvailable;
    }

    /**
     * @brief Returns folder path for Download directory
     * @return Folder path
     */
    public String getDownloadDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }
}
