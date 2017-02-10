package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.activity.DeepLinkerActivity;
import org.buildmlearn.toolkit.utilities.NetworkUtils;
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
    private Toast mToast;

    private static float deleteDirectory(File file, float size) {
        if (file.exists()) {
            File[] listFiles = file.listFiles();
            if (listFiles == null) return 0;

            for (File listFile : listFiles) {
                if (listFile.isDirectory()) {
                    size += deleteDirectory(listFile, 0);
                } else {
                    size += listFile.length();
                    listFile.delete();
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
        mToast=Toast.makeText(getActivity()," ",Toast.LENGTH_SHORT);

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

        Preference ratePreference=findPreference(getString(R.string.pref_rate_key));
        ratePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(NetworkUtils.isNetworkAvailable(getActivity()))
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                else {
                    mToast.setText(R.string.settings_network_unavailable);
                    mToast.show();
                }
                return true;
            }
        });

        Preference tell_friend=findPreference(getString(R.string.pref_tell_key));
        tell_friend.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,getString(R.string.pref_tell_message)+" http://play.google.com/store/apps/details?id=" + getActivity().getPackageName());
                shareIntent.setType("text/plain");
                startActivity(shareIntent);
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

        Preference checkUpdate = findPreference(getString(R.string.check_update));
        checkUpdate.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(NetworkUtils.isNetworkAvailable(getActivity()))
                    startActivity(new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
                else {
                    AlertDialog dialog = new AlertDialog.Builder(getActivity())
                            .setMessage(getString(R.string.settings_network_unavailable))
                            .setPositiveButton(getString(R.string.quiz_ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();

                }
                return true;
            }
        });
        prefUsername.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if ("".equals(newValue)) {
                    Toast.makeText(getActivity(), R.string.enter, Toast.LENGTH_LONG).show();
                    return false;
                } else if (newValue != null && !Character.isLetterOrDigit(((String) newValue).charAt(0))) {
                    Toast.makeText(getActivity(), R.string.name_valid_msg, Toast.LENGTH_LONG).show();
                    return false;
                }
                prefUsername.setSummary((String) newValue);
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

    private void resetUserName() {

        View dialogView = View.inflate(getActivity(),R.layout.dialog_settings_your_name, null);
        final EditText editInput = (EditText) dialogView.findViewById(R.id.et_dialog_settings_your_name);
        editInput.setText(prefUsername.getSummary());

        final AlertDialog dialog =
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.title_user_name)
                        .setView(dialogView)
                        .setNegativeButton(R.string.dialog_no, null)
                        .setPositiveButton(R.string.dialog_yes, null).create();

        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(editInput)) {
                    String enteredName = editInput.getText().toString();
                    prefUsername.getEditor().putString(getString(R.string.key_user_name), enteredName).commit();
                    prefUsername.setSummary(editInput.getText().toString());
                    dialog.dismiss();
                }

            }
        });
    }

    private boolean validated(EditText editInput) {
        if (editInput == null) {
            return false;
        }

        String authorText = editInput.getText().toString().trim();
        Context mContext = getActivity();

        if ("".equals(authorText)) {
            editInput.setError(mContext.getString(R.string.valid_msg_name));
            return false;
        } else if (!Character.isLetterOrDigit(authorText.charAt(0))) {
            editInput.setError(mContext.getString(R.string.title_valid));
            return false;
        }
        return true;
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
                            Handler mHandler =new Handler(Looper.getMainLooper());
                            @Override
                            public void
                            onSuccess(final File assetFile) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        processDialog.dismiss();
                                        Intent intentProject = new Intent(getActivity(), DeepLinkerActivity.class);
                                        intentProject.setData(Uri.fromFile(assetFile));
                                        getActivity().startActivity(intentProject);
                                    }
                                });
                            }

                            @Override
                            public void onFail() {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
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
                            }

                            @Override
                            public void onFail(Exception e) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
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

    private class AsyncTaskRunner extends AsyncTask<String, Void, Float> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(getActivity());
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
                mToast.setText("No Temp Files Found!");
                mToast.show();
            }
        }
    }
}
