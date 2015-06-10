package org.buildmlearn.toolkit.utilities;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.security.UnrecoverableKeyException;

import kellinwood.security.zipsigner.AutoKeyException;
import kellinwood.security.zipsigner.ZipSigner;
import kellinwood.security.zipsigner.optional.CustomKeySigner;

/**
 * Created by Abhishek on 10-06-2015.
 */
public class SignerThread extends Thread{
    private static final String TAG = "SignerThread";
    ZipSigner zipSigner = null;

    private String inputFile;
    private String outputFile;
    private Context context;
    private String keyStoreFilePath;
    private String keyStorePassword;
    private String alias;
    private String aliasPassword;
    String signatureAlgorithm = "SHA1withRSA";


    public SignerThread(Context context, String inputFile, String outputFile,  String keyStoreFilePath, String keyStorePassword, String alias, String aliasPassword) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        this.context = context;
        this.keyStoreFilePath = keyStoreFilePath;
        this.keyStorePassword = keyStorePassword;
        this.alias = alias;
        this.aliasPassword = aliasPassword;
    }

    public void run() {

        try {
            if (inputFile == null)
                throw new IllegalArgumentException("Parameter inputFile is null");
            if (outputFile == null)
                throw new IllegalArgumentException("Parameter outputFile is null");

            zipSigner = new ZipSigner();
            zipSigner.setResourceAdapter(new ZipSignerAppResourceAdapter(context.getResources()));

            File keystoreFile = new File(keyStoreFilePath);

            char[] keyPass = keyStorePassword.toCharArray();
            char[] aliasPass = aliasPassword.toCharArray();

            CustomKeySigner.signZip(zipSigner, keystoreFile.getAbsolutePath(), keyPass,
                    alias, aliasPass, signatureAlgorithm, inputFile, outputFile);


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

