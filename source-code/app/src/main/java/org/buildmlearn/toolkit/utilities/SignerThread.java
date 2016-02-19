package org.buildmlearn.toolkit.utilities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.model.KeyStoreDetails;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.UnrecoverableKeyException;

import kellinwood.security.zipsigner.AutoKeyException;
import kellinwood.security.zipsigner.ZipSigner;
import kellinwood.security.zipsigner.optional.CustomKeySigner;

/**
 * Created by Abhishek on 10-06-2015.
 */

/**
 * @brief Class for signing a unsigned apk file using a given keystore and credentials.
 */
public class SignerThread extends Thread {
    private static final String TAG = "SignerThread";
    private static final String TEMP_FOLDER = "hcjb";
    ZipSigner zipSigner = null;
    String signatureAlgorithm = "SHA1withRSA";
    private ToolkitApplication toolkit;
    private Context context;
    private String assetsApk;
    private String finalApk;
    private String assetFileName;
    private String assetFilePath;
    private String projectFile;
    private KeyStoreDetails keyDetails;
    private OnSignComplete listener;

    public SignerThread(Context context, String assetsApk, String finalApk, KeyStoreDetails keyDetails, String assetFilePath, String assetFileName) {
        this.projectFile = finalApk;
        this.context = context;
        this.assetsApk = assetsApk;
        this.finalApk = finalApk.replaceAll("buildmlearn", "apk");
        this.keyDetails = keyDetails;
        this.toolkit = (ToolkitApplication) context;
        this.assetFileName = assetFileName;
        this.assetFilePath = assetFilePath;
    }

    public void setSignerThreadListener(OnSignComplete listener) {
        this.listener = listener;
    }

    public void run() {

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck==-1){
            listener.onFail(new FileNotFoundException());
            return;
        }
        FileUtils.copyAssets(context, assetsApk, toolkit.getApkDir());
        FileUtils.copyAssets(context, keyDetails.getAssetsPath(), toolkit.getApkDir());

        try {
            FileUtils.unZip(toolkit.getApkDir() + assetsApk, toolkit.getUnZipDir() + TEMP_FOLDER);
        } catch (IOException e) {
            if (listener != null) {
                listener.onFail(e);
            }
            e.printStackTrace();
        }

        File folder = new File(toolkit.getUnZipDir() + TEMP_FOLDER + "/" + assetFilePath);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        File src = new File(projectFile);
        File dest = new File(toolkit.getUnZipDir() + TEMP_FOLDER + "/" + assetFilePath + assetFileName);

        try {
            FileWriter fileWriter = new FileWriter(dest.getAbsoluteFile(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            FileUtils.copy(src, dest);
        } catch (IOException e) {
            if (listener != null) {
                listener.onFail(e);
            }
            e.printStackTrace();
        }

        try {
            FileUtils.zipFolder(toolkit.getUnZipDir() + TEMP_FOLDER);
        } catch (IOException e) {
            if (listener != null) {
                listener.onFail(e);
            }
            e.printStackTrace();
        }

        String inputFile = toolkit.getUnZipDir() + TEMP_FOLDER + ".zip";
        try {
            if (finalApk == null) {
                throw new IllegalArgumentException("Parameter outputFile is null");
            }

            zipSigner = new ZipSigner();
            zipSigner.setResourceAdapter(new ZipSignerAppResourceAdapter(context.getResources()));

            File keystoreFile;
            keystoreFile = new File(toolkit.getApkDir() + keyDetails.getAssetsPath());

            char[] keyPass = keyDetails.getPassword().toCharArray();
            char[] aliasPass = keyDetails.getAliasPassword().toCharArray();

            if (toolkit.checkExternalStorage()) {
                finalApk = toolkit.getDownloadDirectory() + "/" + finalApk.substring(toolkit.getSavedDir().length());
                Log.d(TAG, "Final APK: " + finalApk);
            }

            CustomKeySigner.signZip(zipSigner, keystoreFile.getAbsolutePath(), keyPass,
                    keyDetails.getAlias(), aliasPass, signatureAlgorithm, inputFile, finalApk);


            if (zipSigner.isCanceled()) {
                Log.d(TAG, "Signing cancelled");
                Toast.makeText(toolkit, "APK file not generated", Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Signing Complete");
                listener.onSuccess(finalApk);

                if (toolkit.isExternalStorageAvailable()) {
                    showNotification("APK file saved in Downloads folder");
                } else {
                    showNotification("SD card not found. APK file saved in internal storage.");
                }
            }

        } catch (AutoKeyException | UnrecoverableKeyException x) {
            Log.d(TAG, "Exception: " + x.getMessage());
            if (listener != null) {
                listener.onFail(x);
            }
        } catch (Throwable t) {

            String tName = t.getClass().getName();
            int pos = tName.lastIndexOf('.');
            if (pos >= 0) {
                tName = tName.substring(pos + 1);
            }
            Log.d(TAG, "Exception: " + tName + ": " + t.getMessage());
            if (listener != null) {
                listener.onFail(null);
            }
        }
    }

    private void showNotification(String description) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(toolkit)
                        .setSmallIcon(R.drawable.ic_stat_toggle_check_box)
                        .setContentTitle("APK Generated")
                        .setContentText(description)
                        .setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) toolkit.getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
        mNotificationManager.notify(23, mBuilder.build());
    }

    public interface OnSignComplete {
        void onSuccess(String path);

        void onFail(Exception e);
    }

}

