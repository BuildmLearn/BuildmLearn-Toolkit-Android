package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.activity.DeepLinkerActivity;
import org.buildmlearn.toolkit.utilities.RestoreThread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by abhishek on 21/06/15 at 9:51 PM.
 */
public class SettingsFragment extends PreferenceFragment {

    private static final int REQUEST_PICK_APK = 9985;
    private Preference prefUsername;

    public static float deleteDirectory(File file, float size) {
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null) return 0;

            for (int i = 0; i < listFiles.length; i++) {
                if (listFiles[i].isDirectory()) {
                    size += deleteDirectory(listFiles[i], 0);
                } else {
                    size += listFiles[i].length();
                    listFiles[i].delete();
                }
            }
        }
        file.delete();
        return (size);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.fragment_settings);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        Preference deleteTempFiles = findPreference(getString(R.string.key_delete_temporary_files));
        deleteTempFiles.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                String path = ToolkitApplication.getUnZipDir();
                AsyncTaskRunner asynctaskrunner = new AsyncTaskRunner();
                asynctaskrunner.execute(path);
                return true;
            }
        });

        Preference restoreProject = findPreference(getString(R.string.key_restore_project));
        restoreProject.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                initRestoreProjectDialog();
                return true;
            }
        });

        prefUsername = findPreference(getString(R.string.key_user_name));
        prefUsername.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                resetUserName();
                return true;
            }
        });
        prefUsername.setSummary(preferences.getString(getString(R.string.key_user_name), ""));

    }

    public void initRestoreProjectDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        startActivityForResult(intent, REQUEST_PICK_APK);
    }

    public void resetUserName(){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_settings_your_name, null);
        final EditText editInput = (EditText) dialogView.findViewById(R.id.et_dialog_settings_your_name);
        editInput.setText(prefUsername.getSummary());
        final AlertDialog dialog =
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_user_name)
                        .setView(dialogView)
                        .setNegativeButton(R.string.dialog_no, null)
                        .setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String enteredName = editInput.getText().toString();
                                if (!TextUtils.isEmpty(enteredName)){
                                    prefUsername.getEditor().putString(getString(R.string.key_user_name), enteredName).commit();
                                    prefUsername.setSummary(editInput.getText().toString());
                                }
                                dialog.dismiss();
                            }
                        }).create();
        dialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_PICK_APK:

                if (resultCode == Activity.RESULT_OK) {

                    try {
                        final ProgressDialog processDialog = new ProgressDialog(getActivity(), R.style.AppDialogTheme);
                        processDialog.setTitle(R.string.restore_progress_dialog);
                        processDialog.setMessage(getActivity().getString(R.string.restore_msg));
                        processDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        processDialog.setCancelable(false);
                        processDialog.setProgress(0);
                        processDialog.show();

                        InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                        RestoreThread restore = new RestoreThread(getActivity(), inputStream);

                        restore.setRestoreListener(new RestoreThread.OnRestoreComplete() {
                            @Override
                            public void onSuccess(File assetFile) {
                                processDialog.dismiss();
                                Intent intentProject = new Intent(getActivity(), DeepLinkerActivity.class);
                                intentProject.setData(Uri.fromFile(assetFile));
                                getActivity().startActivity(intentProject);
                            }

                            @Override
                            public void onFail() {
                                processDialog.dismiss();
                                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                                .setTitle(R.string.dialog_restore_title)
                                                .setMessage(R.string.dialog_restore_failed)
                                                .setPositiveButton(R.string.info_template_ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create();
                                dialog.show();
                            }

                            @Override
                            public void onFail(Exception e) {
                                processDialog.dismiss();
                                final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                                .setTitle(R.string.dialog_restore_title)
                                                .setMessage(R.string.dialog_restore_failed)
                                                .setPositiveButton(R.string.info_template_ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).create();
                                dialog.show();
                            }
                        });

                        restore.start();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();

                        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                                        .setTitle(R.string.dialog_restore_title)
                                        .setMessage(R.string.dialog_restore_fileerror)
                                        .setPositiveButton(R.string.info_template_ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        dialog.show();
                    }


                }

                break;
            default: //do nothing
                break;
        }

    }
    private class AsyncTaskRunner extends AsyncTask<String,Void,Float> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog=new ProgressDialog(getActivity());
            progressDialog.setTitle("Deleting...");
            progressDialog.setMessage("Deleting Temporary file");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected Float doInBackground(String... params) {
            float size = deleteDirectory(new File(params[0]), 0);
            size = (float) ((float) Math.round((size / 1048576) * 100d) / 100d);
            return size;
        }

        @Override
        protected void onPostExecute(Float size) {
            progressDialog.dismiss();
            if (size != 0) {
                Toast.makeText(getActivity(), "Deleted " + size + " MB.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "No Temp Files Found!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
