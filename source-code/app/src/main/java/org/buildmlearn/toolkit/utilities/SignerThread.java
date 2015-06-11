package org.buildmlearn.toolkit.utilities;

import android.content.Context;
import android.util.Log;

import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.model.KeyStoreDetails;

import java.io.File;
import java.io.IOException;
import java.security.UnrecoverableKeyException;

import kellinwood.security.zipsigner.AutoKeyException;
import kellinwood.security.zipsigner.ZipSigner;
import kellinwood.security.zipsigner.optional.CustomKeySigner;

/**
 * Created by Abhishek on 10-06-2015.
 */
public class SignerThread extends Thread {
    private static final String TAG = "SignerThread";
    private static final String TEMP_FOLDER = "hcjb";

    private ToolkitApplication toolkit;
    ZipSigner zipSigner = null;

    private Context context;
    private String assetsApk;
    private String finalApk;
    private KeyStoreDetails keyDetails;
    String signatureAlgorithm = "SHA1withRSA";

    public SignerThread(Context context, String assetsApk, String finalApk, KeyStoreDetails keyDetails) {
        this.context = context;
        this.assetsApk = assetsApk;
        this.finalApk = finalApk;
        this.keyDetails = keyDetails;
        this.toolkit = (ToolkitApplication)context;
    }

    public void run() {

        FileUtils.copyAssets(context, assetsApk, toolkit.getApkDir());
        FileUtils.copyAssets(context, keyDetails.getAssetsPath(), toolkit.getApkDir());

        try {
            FileUtils.unZip(toolkit.getApkDir() + assetsApk, toolkit.getUnZipDir() + TEMP_FOLDER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            FileUtils.zipFolder(toolkit.getUnZipDir() + TEMP_FOLDER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String inputFile = toolkit.getUnZipDir() + TEMP_FOLDER + ".zip";
        try {
            if (inputFile == null)
                throw new IllegalArgumentException("Parameter inputFile is null");
            if (finalApk == null)
                throw new IllegalArgumentException("Parameter outputFile is null");

            zipSigner = new ZipSigner();
            zipSigner.setResourceAdapter(new ZipSignerAppResourceAdapter(context.getResources()));

            File keystoreFile = new File(toolkit.getApkDir() + keyDetails.getAssetsPath());

            char[] keyPass = keyDetails.getPassword().toCharArray();
            char[] aliasPass = keyDetails.getAliasPassword().toCharArray();

            CustomKeySigner.signZip(zipSigner, keystoreFile.getAbsolutePath(), keyPass,
                    keyDetails.getAlias(), aliasPass, signatureAlgorithm, inputFile, finalApk);


            if (zipSigner.isCanceled())
                Log.d(TAG, "Signing cancelled");
            else {
                Log.d(TAG, "Signing Complete");
            }

        } catch (AutoKeyException x) {
            Log.d(TAG, "Exception: " + x.getMessage());
        } catch (UnrecoverableKeyException x) {
            Log.d(TAG, "Exception: " + x.getMessage());
        } catch (Throwable t) {

            String tname = t.getClass().getName();
            int pos = tname.lastIndexOf('.');
            if (pos >= 0) {
                tname = tname.substring(pos + 1);
            }
            Log.d(TAG, "Exception: " + tname + ": " + t.getMessage());
        }
    }

}

