/*
 *
 * Reference :: https://github.com/sjitech/ApkRename/tree/master/lib/AndroidManifestBinaryXml_ChangePackageName
 *
 * Library Name :: axml.jar
 *
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 SJI Research Center for Advanced Technology
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.buildmlearn.toolkit.utilities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.model.KeyStoreDetails;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.security.UnrecoverableKeyException;
import java.util.Calendar;

import kellinwood.security.zipsigner.AutoKeyException;
import kellinwood.security.zipsigner.ZipSigner;
import kellinwood.security.zipsigner.optional.CustomKeySigner;
import pxb.android.axml.AxmlReader;
import pxb.android.axml.AxmlVisitor;
import pxb.android.axml.AxmlWriter;
import pxb.android.axml.NodeVisitor;

/**
 * Created by Abhishek on 10-06-2015.
 * Modified by Anupam (opticod) on 18-05-2016
 */

/**
 * @brief Class for signing a unsigned apk file using a given keystore and credentials.
 */
public class SignerThread extends Thread {
    private static final String TAG = "SignerThread";
    private static final String TEMP_FOLDER = "hcjb";
    private static final String NS = "http://schemas.android.com/apk/res/android";
    private static boolean needRemoveConflict;
    private static boolean needRemoveLib;
    private static String newPackageFullName;
    private static boolean changed;
    private final ToolkitApplication toolkit;
    private final Context context;
    private final String assetsApk;
    private final String assetFileName;
    private final String assetFilePath;
    private final String projectFile;
    private final KeyStoreDetails keyDetails;
    private String finalApk;
    private OnSignComplete listener;

    public SignerThread(Context context, String assetsApk, String finalApk, KeyStoreDetails keyDetails, String assetFilePath, String assetFileName) {
        this.projectFile = finalApk;
        this.context = context;
        this.assetsApk = assetsApk;
        this.finalApk = getFinalApkPath(finalApk);
        this.keyDetails = keyDetails;
        this.toolkit = (ToolkitApplication) context;
        this.assetFileName = assetFileName;
        this.assetFilePath = assetFilePath;
    }

    private static void modifyManifest(final String[] args) {
        try {
            String androidManifestBinXml = args[0];
            needRemoveConflict = args[1].contains("!");
            needRemoveLib = args[1].contains("%");
            newPackageFullName = args[1].replace("!", "").replace("%", "");
            InputStream is = new FileInputStream(androidManifestBinXml);
            byte[] xml = new byte[is.available()];
            is.read(xml);
            is.close();
            AxmlReader ar = new AxmlReader(xml);
            AxmlWriter aw = new AxmlWriter();
            ar.accept(new AxmlVisitor(aw) {
                @Override
                public NodeVisitor child(String ns, String name) {
                    return new MyNodeVisitor(super.child(ns, name), name);
                }
            });

            if (changed) {
                byte[] modified = aw.toByteArray();
                FileOutputStream fos = new FileOutputStream(androidManifestBinXml);
                fos.write(modified);
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getFinalApkPath(String buildmlearnPath) {
        int index = buildmlearnPath.lastIndexOf("buildmlearn");
        return buildmlearnPath.substring(0, index) + "apk";
    }

    public void setSignerThreadListener(OnSignComplete listener) {
        this.listener = listener;
    }

    @SuppressLint("HardwareIds")
    public void run() {

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck == -1) {
            listener.onFail(new FileNotFoundException());
            return;
        }

        FileUtils.copyAssets(context, assetsApk, toolkit.getApkDir());
        FileUtils.copyAssets(context, keyDetails.getAssetsPath(), toolkit.getApkDir());

        try {
            FileUtils.unZip(toolkit.getApkDir() + assetsApk, ToolkitApplication.getUnZipDir() + TEMP_FOLDER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String packageName = "org.buildmlearn.app";
            packageName += Secure.getString(context.getContentResolver(),
                    Secure.ANDROID_ID);
            Calendar rightNow = Calendar.getInstance();
            packageName += String.valueOf(rightNow.getTimeInMillis());

            modifyManifest(new String[]{ToolkitApplication.getUnZipDir() + TEMP_FOLDER + "/AndroidManifest.xml", packageName});
        } catch (Exception e) {
            if (listener != null) {
                listener.onFail(e);
            }
            e.printStackTrace();
        }


        File folder = new File(ToolkitApplication.getUnZipDir() + TEMP_FOLDER + "/" + assetFilePath);

        if (!folder.exists()) {
            folder.mkdir();
        } else {
            //Empty the folder, clean previous assets
            File[] oldAssets = folder.listFiles();
            for (File file : oldAssets)
                file.delete();
        }

        File src = new File(projectFile);
        File dest = new File(ToolkitApplication.getUnZipDir() + TEMP_FOLDER + "/" + assetFilePath + assetFileName);

        try {
            new FileWriter(dest.getAbsoluteFile(), true);
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
            FileUtils.zipFolder(ToolkitApplication.getUnZipDir() + TEMP_FOLDER);
        } catch (IOException e) {
            if (listener != null) {
                listener.onFail(e);
            }
            e.printStackTrace();
        }

        String inputFile = ToolkitApplication.getUnZipDir() + TEMP_FOLDER + ".zip";
        try {
            if (finalApk == null) {
                throw new IllegalArgumentException("Parameter outputFile is null");
            }

            ZipSigner zipSigner = new ZipSigner();
            zipSigner.setResourceAdapter(new ZipSignerAppResourceAdapter(context.getResources()));

            File keystoreFile;
            keystoreFile = new File(toolkit.getApkDir() + keyDetails.getAssetsPath());

            char[] keyPass = keyDetails.getPassword().toCharArray();
            char[] aliasPass = keyDetails.getAliasPassword().toCharArray();

            if (toolkit.checkExternalStorage()) {
                finalApk = toolkit.getDownloadDirectory() + "/" + finalApk.substring(toolkit.getSavedDir().length());
                Log.d(TAG, "Final APK: " + finalApk);
            }

            String signatureAlgorithm = "SHA1withRSA";
            CustomKeySigner.signZip(zipSigner, keystoreFile.getAbsolutePath(), keyPass,
                    keyDetails.getAlias(), aliasPass, signatureAlgorithm, inputFile, finalApk);


            if (zipSigner.isCanceled()) {
                Log.d(TAG, "Signing cancelled");
                Toast.makeText(toolkit, R.string.apk_not_generated, Toast.LENGTH_SHORT).show();
            } else {
                Log.d(TAG, "Signing Complete");
                listener.onSuccess(finalApk);
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

    public interface OnSignComplete {
        void onSuccess(String path);

        void onFail(Exception e);
    }

    public static class MyNodeVisitor extends NodeVisitor {
        public static String level = "";
        public static String oldPackageName;
        public boolean didLogNodeName = false;
        public String nodeName = "";

        MyNodeVisitor(NodeVisitor nv, String nodeName) {
            super(nv);
            this.nodeName = nodeName;
        }

        @Override
        public NodeVisitor child(String ns, String name) {
            if (needRemoveConflict && ("original-package".equals(name) || "provider".equals(name)) && ns == null) {
                changed = true;
                return null;
            } else if (needRemoveLib && ("uses-library".equals(name)) && ns == null) {
                changed = true;
                return null;
            }
            level += "    ";
            return new MyNodeVisitor(super.child(ns, name), name);
        }

        @Override
        public void attr(String ns, String name, int resourceId, int type, Object val) {
            String oldName = name;
            Object oldVal = val;
            if (ns == null && "package".equals(name) && "manifest".equals(nodeName) && type == NodeVisitor.TYPE_STRING && level.length() == 0) {
                oldPackageName = (String) val;
                if (!newPackageFullName.equals(val)) {
                    val = newPackageFullName;
                }
            } else if (type == NodeVisitor.TYPE_STRING && ("name".equals(name) || "backupAgent".equals(name) || "manageSpaceActivity".equals(name) || "targetActivity".equals(name)) && NS.equals(ns) && val != null && val instanceof String) {
                if (((String) val).startsWith(".")) {
                    val = oldPackageName + val;
                } else if (!((String) val).contains(".") && ((String) val).length() > 0) {
                    val = oldPackageName + "." + val;
                }
            } else if (type == NodeVisitor.TYPE_STRING && "value".equals(name) && NS.equals(ns) && val != null && val instanceof String) {
                if (((String) val).startsWith(".")) {
                    val = oldPackageName + val;
                }
            } else if (needRemoveConflict && ("protectionLevel".equals(name) || "process".equals(name) || "sharedUserId".equals(name)) && NS.equals(ns)) {
                name = null;
            } else if (needRemoveConflict && ("coreApp".equals(name)) && ns == null) {
                name = null;
            }

            if ((!((name == null && oldName == null) || (name != null && name.equals(oldName)))) || val != oldVal) {
                changed = true;
                if (!didLogNodeName) {
                    didLogNodeName = true;
                }
                if (name == null) {
                    return;
                }
            }

            super.attr(ns, name, resourceId, type, val);
        }

        @Override
        public void end() {
            level = level.length() > 4 ? level.substring(4) : "";
            super.end();
        }
    }
}
