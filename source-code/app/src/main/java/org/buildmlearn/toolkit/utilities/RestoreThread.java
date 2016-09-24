package org.buildmlearn.toolkit.utilities;

/**
 * Created by scopeinfinity on 14/3/16.
 */


import android.content.Context;

import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.model.Template;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * @brief Class for extracting .buildmlearn file from apk.
 */
public class RestoreThread extends Thread {
    private static final String TEMP_FOLDER = "rtf";
    private final Context context;
    private final InputStream zipInputStream;

    private OnRestoreComplete listener;

    public RestoreThread(Context context, InputStream zipInputStream) {
        this.context = context;
        this.zipInputStream = zipInputStream;
    }

    public void setRestoreListener(OnRestoreComplete listener) {
        this.listener = listener;
    }

    @Override
    public void run() {
        try {
            String assetDirectory = "assets";
            File assetDir = new File(ToolkitApplication.getUnZipDir() + TEMP_FOLDER + "/" + assetDirectory);
            assetDir.mkdirs();
            //Deleting Previous Files if Exists
            File[] templateAssets = assetDir.listFiles();
            for (File data : templateAssets) {
                data.delete();
            }

            //Unzipping
            FileUtils.unZip(zipInputStream, ToolkitApplication.getUnZipDir() + TEMP_FOLDER);
            String files[] = assetDir.list();
            File data = null;
            Template[] templates = Template.values();

            //Search if asset have required file
            for (String file : files) {
                boolean matchFound = false;
                for (Template template : templates)

                    if (context.getString(template.getAssetsName()).equals(file)) {
                        data = new File(assetDir, file); //*_content.xml File
                        matchFound = true;
                        break;
                    }
                if (matchFound)
                    break;
            }

            if (data == null) {
                if (listener != null)
                    listener.onFail();
                return;
            }

            if (listener != null)
                listener.onSuccess(data);


        } finally {

            try {
                zipInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public interface OnRestoreComplete {
        void onSuccess(File assetFile);

        void onFail();

        void onFail(Exception e);
    }
}
