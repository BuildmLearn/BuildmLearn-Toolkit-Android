package org.buildmlearn.toolkit;

import android.app.Application;
import android.os.Environment;

import org.buildmlearn.toolkit.constant.Constants;

import java.io.File;
import java.util.ArrayList;

/**
 * @brief Extended Application class
 * <p/>
 * <p/>
 * Created by Abhishek on 31-05-2015.
 */
public class ToolkitApplication extends Application {

    private static String dir;

    private static boolean isExternalStorageAvailable = false;

    /**
     * @return Folder path
     * @brief Returns folder path for unzipped apks
     */
    public static String getUnZipDir() {
        return dir + Constants.UNZIP;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        storagePathsValidate();
    }

    public void storagePathsValidate() {
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
     * @return folder file
     * @brief Returns external storage directory.
     */
    public File getDir() {
        return Environment.getExternalStorageDirectory();
    }

    /**
     * @return folder path
     * @brief Returns directory for BuildmLearn toolkit  manually created files.
     */
    public String getProjectDir() {
        return dir + Constants.BUILD_M_LEARN_PATH;

    }

    /**
     * @return Folder path
     * @brief Returns folder path for saved projects
     */
    public String getSavedDir() {
        return dir + Constants.SAVED_DIR;
    }

    /**
     * @return Folder path
     * @brief Returns folder path for saved projects
     */
    public String getDraftDir() {
        return dir + Constants.DRAFT_DIR;
    }

    /**
     * @return Folder path
     * @brief Returns folder path for storing generated apks
     */
    public String getApkDir() {
        return dir + Constants.APK_DIR;
    }

    /**
     * @return true if external storage is present, else false
     * @brief Checks if external storage is present for storing data
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
     * @return Folder path
     * @brief Returns folder path for Download directory
     */
    public String getDownloadDirectory() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }
}
