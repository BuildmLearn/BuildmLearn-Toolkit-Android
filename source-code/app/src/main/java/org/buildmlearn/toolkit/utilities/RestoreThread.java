package org.buildmlearn.toolkit.utilities;

/**
 * Created by scopeinfinity on 14/3/16.
 */


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;

import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
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
    private final MaterialDialog processDialog;
    private OnRestoreComplete listener;

    public RestoreThread(Context context, InputStream zipInputStream, MaterialDialog processDialog) {
        this.context = context;
        this.zipInputStream = zipInputStream;
        this.processDialog = processDialog;
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
                if (listener != null){
                    Handler mHandler=new Handler(Looper.getMainLooper());
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            processDialog.dismiss();
                            final AlertDialog dialog = new AlertDialog.Builder(context)
                                    .setTitle(R.string.dialog_restore_title)
                                    .setMessage(R.string.dialog_restore_fileerror)
                                    .setPositiveButton(R.string.info_template_ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                    });
                }
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
