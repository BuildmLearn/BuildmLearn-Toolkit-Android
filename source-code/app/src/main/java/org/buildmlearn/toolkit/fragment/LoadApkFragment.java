package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.adapter.SavedApiAdapter;
import org.buildmlearn.toolkit.model.SavedApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by opticod (Anupam Das) on 29/2/16.
 *
 * @brief Fragment used for loading existing APKs into a list.
 */

public class LoadApkFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = "Load API Fragment";
    private AbsListView mListView;

    private boolean showTemplateSelectedMenu;
    private SavedApiAdapter mAdapter;
    private ToolkitApplication mToolkit;
    private Activity activity;
    private ArrayList<SavedApi> savedApis, allsavedApis;
    private View selectedView = null;
    private EditText editSearch;

    private boolean isSearchOpened = false;
    private int selectedPosition = -1;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mToolkit = (ToolkitApplication) getActivity().getApplicationContext();
        activity = getActivity();
        savedApis = new ArrayList<>();
        allsavedApis = new ArrayList<>();

        String path = mToolkit.getSavedDir();
        if (mToolkit.checkExternalStorage()) {
            path = mToolkit.getDownloadDirectory();
        }

        Log.d("Files", "Path: " + path);
        File f = new File(path);
        File file[] = f.listFiles();
        if (file == null) {
            return;
        }

        Log.d("Files", "Size: " + file.length);
        for (File aFile : file) {
            Log.d(TAG, aFile.getAbsolutePath());
            File apkFile = new File(aFile.getAbsolutePath());
            PackageInfo info = getActivity().getPackageManager().getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
            if (info != null && info.packageName != null && info.packageName.startsWith("org.buildmlearn.")) {
                savedApis.add(new SavedApi(apkFile, apkFile.getName(), apkFile.lastModified()));
                allsavedApis.add(new SavedApi(apkFile, apkFile.getName(), apkFile.lastModified()));
            }
        }

        Collections.sort(savedApis, new Comparator<SavedApi>() {
            public int compare(SavedApi f1, SavedApi f2) {
                return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
            }
        });

        Collections.sort(allsavedApis, new Comparator<SavedApi>() {
            public int compare(SavedApi f1, SavedApi f2) {
                return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
            }
        });

        Collections.reverse(savedApis);
        Collections.reverse(allsavedApis);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_loadproject, container, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SavedApiAdapter(getActivity(), savedApis);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedPosition == position) {
                    selectedPosition = -1;
                    view.setBackgroundResource(0);
                    restoreColorScheme();
                } else {
                    if (selectedView != null) {
                        selectedView.setBackgroundResource(0);
                    }
                    selectedView = view;
                    selectedPosition = position;
                    Log.d(TAG, "Position: " + selectedPosition);
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_divider));
                    changeColorScheme();
                }
                return true;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        SavedApi apk = savedApis.get(position);
        File file = new File(apk.getFile().getPath());
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void setAdapter(SavedApiAdapter adapter) {
        mListView.setAdapter(adapter);
        setEmptyText();
    }

    private void setEmptyText() {
        getView().findViewById(R.id.newProject).setVisibility(View.GONE);
        if (mListView.getAdapter().getCount() == 0) {
            getView().findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        if (mAdapter != null) {

            String specificApis = "";
            if (isSearchOpened)
                specificApis = editSearch.getText().toString();
            savedApis.clear();
            allsavedApis.clear();
            String path = mToolkit.getApkDir();

            if (mToolkit.checkExternalStorage()) {
                path = mToolkit.getDownloadDirectory();
            }
            Log.d("Files", "Path: " + path);

            File f = new File(path);
            File file[] = f.listFiles();
            if (file == null) {
                return;
            }

            Log.d("Files", "Size: " + file.length);
            for (File aFile : file) {
                if (aFile.getName().contains(".apk")) {
                    File apkFile = new File(aFile.getAbsolutePath());
                    PackageInfo info = getActivity().getPackageManager().getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
                    if (info != null && info.packageName != null && info.packageName.startsWith("org.buildmlearn.")) {
                        if (apkFile.getName().startsWith(specificApis))
                            savedApis.add(new SavedApi(apkFile, apkFile.getName(), apkFile.lastModified()));
                        allsavedApis.add(new SavedApi(apkFile, apkFile.getName(), apkFile.lastModified()));
                    }
                }
            }

            Collections.sort(savedApis, new Comparator<SavedApi>() {
                public int compare(SavedApi f1, SavedApi f2) {
                    return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
                }
            });

            Collections.sort(allsavedApis, new Comparator<SavedApi>() {
                public int compare(SavedApi f1, SavedApi f2) {
                    return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
                }
            });

            Collections.reverse(savedApis);
            Collections.reverse(allsavedApis);
            mAdapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    /**
     * @brief Restores the color scheme when switching from edit mode to normal mode.
     * <p/>
     * Edit mode is triggered, when the list item is long pressed.
     */
    private void restoreColorScheme() {
        int primaryColor = ContextCompat.getColor(getActivity(), R.color.color_primary);
        int primaryColorDark = ContextCompat.getColor(getActivity(), R.color.color_primary_dark);
        ((AppCompatActivity) activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().neutralColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().negativeColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(primaryColorDark);
            activity.getWindow().setNavigationBarColor(primaryColor);
        }
        showTemplateSelectedMenu = false;
        activity.invalidateOptionsMenu();
    }

    /**
     * @brief Changes the color scheme when switching from normal mode to edit mode.
     * <p/>
     * Edit mode is triggered, when the list item is long pressed.
     */
    private void changeColorScheme() {
        int primaryColor = ContextCompat.getColor(getActivity(), R.color.color_primary_dark);
        int primaryColorDark = ContextCompat.getColor(getActivity(), R.color.color_selected_dark);
        ((AppCompatActivity) activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().neutralColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().negativeColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(primaryColorDark);
            activity.getWindow().setNavigationBarColor(primaryColor);
        }

        showTemplateSelectedMenu = true;
        activity.invalidateOptionsMenu();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (showTemplateSelectedMenu) {
            activity.getMenuInflater().inflate(R.menu.menu_apk_selected, menu);
        } else {
            activity.getMenuInflater().inflate(R.menu.menu_apk_not_selected, menu);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:

                final MaterialDialog dialog = new MaterialDialog.Builder(activity)
                        .title(R.string.dialog_delete_title)
                        .content(R.string.dialog_delete_msg)
                        .positiveText(R.string.dialog_yes)
                        .negativeText(R.string.dialog_no)
                        .build();

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        deleteItem(selectedPosition);
                        restoreSelectedView();
                    }
                });
                dialog.show();
                break;
            case R.id.action_share:

                SavedApi apk = savedApis.get(selectedPosition);
                File file = new File(apk.getFile().getPath());
                Uri fileUri = Uri.fromFile(file);
                Intent sendIntent = new Intent(Intent.ACTION_SEND);
                sendIntent.setType("application/vnd.android.package-archive");
                sendIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(Intent.createChooser(sendIntent, null));
                break;
            case R.id.action_search:

                isSearchOpened = true;
                ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
                actionBar.setDisplayShowCustomEnabled(true);
                item.setVisible(false);
                actionBar.setCustomView(R.layout.search_bar);
                actionBar.setDisplayShowTitleEnabled(false);
                editSearch = (EditText) actionBar.getCustomView().findViewById(R.id.editSearch);
                editSearch.setHint("Enter name of Apk");
                editSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = s.toString().trim();
                        savedApis.clear();
                        SavedApi tempApi;
                        for (int i = 0; i < allsavedApis.size(); i++) {
                            if (allsavedApis.get(i).getName().startsWith(text)) {
                                tempApi = new SavedApi(allsavedApis.get(i).getFile(), allsavedApis.get(i).getName(), allsavedApis.get(i).getUnformattedDate());
                                savedApis.add(tempApi);
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                        setEmptyText();
                    }
                });
                editSearch.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            editSearch.onKeyPreIme(keyCode, event);
                            if (isSearchOpened) {
                                closeSearch();
                            }
                            return true;
                        }
                        return false;
                    }
                });
                editSearch.requestFocus();
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editSearch, InputMethodManager.SHOW_IMPLICIT);
                break;
            default: //do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @brief Removes selected apk item
     */
    private void deleteItem(int selectedPosition) {
        SavedApi apk = savedApis.get(selectedPosition);
        File file = new File(apk.getFile().getPath());
        boolean deleted = file.delete();
        if (deleted) {
            savedApis.remove(selectedPosition);
            mAdapter.notifyDataSetChanged();
            setEmptyText();
            Toast.makeText(activity, "APK Successfully Deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "APK Deletion Failed!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * @brief Removes selected color from the selected ListView item when switching from edit mode to normal mode
     */
    private void restoreSelectedView() {
        if (selectedView != null) {
            selectedView.setBackgroundResource(0);
        }
        restoreColorScheme();
    }

    public void closeSearch() {
        if (isSearchOpened) {
            editSearch.setText("");
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            restoreColorScheme();
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);
            isSearchOpened = false;
            actionBar.setDisplayShowHomeEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }

    }

}

