package org.buildmlearn.toolkit;

import android.app.Application;
import android.os.Environment;
import android.widget.Toast;

import org.buildmlearn.toolkit.constant.Constants;

import java.io.File;

/**
 * Created by Abhishek on 31-05-2015.
 */
public class ToolkitApplication extends Application {

    private String projectDir;
    private String toolkitDir;
    private String apkDir;

    private String dir;
    private boolean isExternalStorageAvailable = false;


    @Override
    public void onCreate() {
        super.onCreate();
        if (checkExternalStorage()) {
            Toast.makeText(this, "External storage available", Toast.LENGTH_SHORT).show();
            isExternalStorageAvailable = true;
            dir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            Toast.makeText(this, "Not able to read external storage. Phone memory will be used", Toast.LENGTH_SHORT).show();
            dir = getFilesDir().getAbsolutePath();
        }


    }

    public String getProjectDir() {
        return dir + Constants.BUILD_M_LEARN_PATH;

    }

    public String getSavedDir() {
        return dir + Constants.SAVED_DIR;
    }

    public String getUnZipDir() {
        return dir + Constants.UNZIP;
    }

    public String getApkDir() {
        return dir + Constants.APK_DIR;
    }


    private boolean checkExternalStorage() {

        boolean result = false;
        File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/BuildmLearn/");
        if (!f.isDirectory()) {
            result = f.mkdirs();
            f.delete();
        }
        return result;
    }


}
