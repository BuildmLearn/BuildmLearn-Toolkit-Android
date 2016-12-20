package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
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
                if (mAdapter.isPositionSelected(position)) {
                    mAdapter.removeSelectedPosition(position);
                    view.setBackgroundResource(0);
                    if(mAdapter.selectedPositionsSize()==0)
                        restoreColorScheme();
                } else {
                    view.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.color_divider));
                    mAdapter.putSelectedPosition(position);
                    changeColorScheme();
                }
                return true;
            }
        });

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (mAdapter.selectedPositionsSize() > 0) {
                        unselectAll();
                        return true;
                    }
                }
                return false;
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(mAdapter.selectedPositionsSize() > 0) {
            if(mAdapter.isPositionSelected(position)) {
                mAdapter.removeSelectedPosition(position);
                view.setBackgroundResource(0);
                if(mAdapter.selectedPositionsSize() == 0)
                    restoreColorScheme();
            }
            else{
                view.setBackgroundColor(ContextCompat.getColor(mToolkit, R.color.color_divider));
                mAdapter.putSelectedPosition(position);
                Log.d(TAG, "Position: " + position);
                changeColorScheme();
            }
            return ;
        }
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
        getView().findViewById(R.id.no_saved_project).setVisibility(View.GONE);
        getView().findViewById(R.id.no_saved_drafts).setVisibility(View.GONE);
        if (mListView.getAdapter().getCount() == 0) {
            getView().findViewById(R.id.no_saved_apks).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.no_saved_apks).setVisibility(View.GONE);
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
                        if (apkFile.getName().contains(specificApis))
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

                final AlertDialog dialog = new AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.dialog_yes, null)
                        .setNegativeButton(R.string.dialog_no, null)
                        .create();
                dialog.show();

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        deleteItems();
                        restoreSelectedView();
                    }
                });
                break;
            case R.id.action_share:

                ArrayList<Integer> selectedPositions = mAdapter.getSelectedPositions();
                ArrayList<Uri> uris = new ArrayList<>();
                Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                sendIntent.setType("application/zip");
                for(int selectedPosition : selectedPositions) {
                    SavedApi apk = savedApis.get(selectedPosition);
                    File file = new File(apk.getFile().getPath());
                    Uri fileUri = Uri.fromFile(file);
                    uris.add(fileUri);
                }
                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
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
                editSearch.setHint("Enter Apk name");
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
                        for (int i = 0; i < allsavedApis.size(); i++) {
                            if (allsavedApis.get(i).getName().toLowerCase().contains(text.toLowerCase())) {
                                savedApis.add(allsavedApis.get(i));
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
                                savedApis.clear();
                                for (int i = 0; i < allsavedApis.size(); i++) {
                                    savedApis.add(allsavedApis.get(i));
                                }
                                mAdapter.notifyDataSetChanged();
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

            case R.id.action_select_all:
                for(int i=0;i<mAdapter.getCount();i++) {
                    if (!mAdapter.isPositionSelected(i))
                    {
                        savedApis.get(i).setSelected(true);
                        mAdapter.putSelectedPosition(i);
                        changeColorScheme();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.action_unselect_all:
                unselectAll();
                break;

            default: //do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void unselectAll() {
        for (int i = 0; i < mAdapter.getCount(); i++)
            if (mAdapter.isPositionSelected(i)) {
                savedApis.get(i).setSelected(false);
                mAdapter.removeSelectedPosition(i);
            }
        restoreColorScheme();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * @brief Removes selected apk item
     */
    private void deleteItems() {
        ArrayList<Integer> selectedPositions = mAdapter.getSelectedPositions();
        boolean deleted = false;

        for(int selectedPosition : selectedPositions) {
            SavedApi apk = savedApis.get(selectedPosition);
            File file = new File(apk.getFile().getPath());
            deleted = file.delete();
            if (deleted) {
                int selectedPos = -1;
                for (int i = 0; i < allsavedApis.size(); i++) {
                    SavedApi sApi = allsavedApis.get(i);
                    if (sApi.getName().equals(apk.getName())) {
                        selectedPos = i;
                        break;
                    }
                }
                if (selectedPos != -1) {
                    allsavedApis.remove(selectedPos);
                }
                savedApis.remove(selectedPosition);
                mAdapter.removeSelectedPosition(selectedPosition);
                mAdapter.notifyDataSetChanged();
                setEmptyText();
            }
        }
        if(deleted)
            if(selectedPositions.size()==1)
                Toast.makeText(activity,"Project Successfully Deleted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(activity,selectedPositions.size()+" Projects Successfully Deleted", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(activity, "Project Deletion Failed!", Toast.LENGTH_SHORT).show();
    }

    /**
     * @brief Removes selected color from the selected ListView item when switching from edit mode to normal mode
     */
    private void restoreSelectedView() {
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
            actionBar.setDisplayShowCustomEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
        }

    }

}

