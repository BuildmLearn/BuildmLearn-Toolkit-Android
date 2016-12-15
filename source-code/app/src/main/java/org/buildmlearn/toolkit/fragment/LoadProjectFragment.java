package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.MenuInflater;
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
import org.buildmlearn.toolkit.activity.TemplateActivity;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.adapter.SavedProjectAdapter;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.model.SavedProject;
import org.buildmlearn.toolkit.model.Template;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @brief Fragment used for loading existing projects into a list.
 */
public class LoadProjectFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = "Load Project Fragment";
    private AbsListView mListView;

    private boolean showTemplateSelectedMenu;
    private SavedProjectAdapter mAdapter;
    private ToolkitApplication mToolkit;
    private Activity activity;
    private ArrayList<SavedProject> savedProjects, allsavedProjects;
    private View selectedView = null;

    private boolean isSearchOpened = false;
    private EditText editSearch;

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mToolkit = (ToolkitApplication) getActivity().getApplicationContext();
        activity = getActivity();
        savedProjects = new ArrayList<>();
        allsavedProjects = new ArrayList<>();

        String path = mToolkit.getSavedDir();
        Log.d("Files", "Path: " + path);


        File f = new File(path);
        File file[] = f.listFiles();
        if (file == null) {
            return;
        }

        Log.d("Files", "Size: " + file.length);
        for (File aFile : file) {

            Log.d(TAG, aFile.getAbsolutePath());
            File fXmlFile = new File(aFile.getAbsolutePath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                Log.d("Files", "Root element :" + doc.getDocumentElement().getAttribute("type"));
                savedProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
                allsavedProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
            } catch (ParserConfigurationException | DOMException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(savedProjects, new Comparator<SavedProject>() {
            public int compare(SavedProject f1, SavedProject f2) {
                return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
            }
        });

        Collections.sort(allsavedProjects, new Comparator<SavedProject>() {
            public int compare(SavedProject f1, SavedProject f2) {
                return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
            }
        });

        Collections.reverse(savedProjects);
        Collections.reverse(allsavedProjects);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadproject, container, false);
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mAdapter = new SavedProjectAdapter(getActivity(), savedProjects);
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
                    view.setBackgroundColor(ContextCompat.getColor(mToolkit, R.color.color_divider));
                    mAdapter.putSelectedPosition(position);
                    Log.d(TAG, "Position: " + position);
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

        if(mAdapter.selectedPositionsSize() > 0)
        {
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

        SavedProject project = savedProjects.get(position);
        Template[] templates = Template.values();
        for (int i = 0; i < templates.length; i++) {
            if (project.getType().equals(getString(templates[i].getType()))) {
                Intent intent = new Intent(getActivity(), TemplateEditor.class);
                intent.putExtra(Constants.TEMPLATE_ID, i);
                intent.putExtra(Constants.PROJECT_FILE_PATH, project.getFullPath());
                startActivity(intent);
                return;
            }
        }
        Toast.makeText(getActivity(), "Invalid project file", Toast.LENGTH_SHORT).show();
    }

    private void setAdapter(SavedProjectAdapter adapter) {
        mListView.setAdapter(adapter);
        setEmptyText();
    }

    private void setEmptyText() {
        getView().findViewById(R.id.no_saved_drafts).setVisibility(View.GONE);
        getView().findViewById(R.id.no_saved_apks).setVisibility(View.GONE);
        if (mListView.getAdapter().getCount() == 0) {
            getView().findViewById(R.id.no_saved_project).setVisibility(View.VISIBLE);
            View view= getView().findViewById(R.id.newProject);
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getActivity(),TemplateActivity.class));
                }
            });
        } else {
            getView().findViewById(R.id.newProject).setVisibility(View.GONE);
            getView().findViewById(R.id.no_saved_project).setVisibility(View.GONE);
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

            savedProjects.clear();
            allsavedProjects.clear();

            String path = mToolkit.getSavedDir();
            Log.d("Files", "Path: " + path);


            File f = new File(path);
            File file[] = f.listFiles();
            if (file == null) {
                return;
            }


            Log.d("Files", "Size: " + file.length);
            for (File aFile : file) {
                File fXmlFile = new File(aFile.getAbsolutePath());
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder;
                try {
                    dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(fXmlFile);
                    doc.getDocumentElement().normalize();
                    Log.d("Files", "Root element :" + doc.getDocumentElement().getAttribute("type"));
                    if (fXmlFile.getName().contains(specificApis))
                        savedProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
                    allsavedProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
                } catch (ParserConfigurationException | DOMException | IOException | SAXException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(savedProjects, new Comparator<SavedProject>() {
                public int compare(SavedProject f1, SavedProject f2) {
                    return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
                }
            });

            Collections.sort(allsavedProjects, new Comparator<SavedProject>() {
                public int compare(SavedProject f1, SavedProject f2) {
                    return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
                }
            });

            Collections.reverse(savedProjects);
            Collections.reverse(allsavedProjects);
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
        int primaryColor = ContextCompat.getColor(mToolkit, R.color.color_primary);
        int primaryColorDark = ContextCompat.getColor(mToolkit, R.color.color_primary_dark);
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
        int primaryColor = ContextCompat.getColor(mToolkit, R.color.color_primary_dark);
        int primaryColorDark = ContextCompat.getColor(mToolkit, R.color.color_selected_dark);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (showTemplateSelectedMenu) {
            activity.getMenuInflater().inflate(R.menu.menu_project_selected, menu);
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
                    SavedProject project = savedProjects.get(selectedPosition);
                    File file = new File(project.getFile().getPath());

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
                editSearch.setHint("Enter name of Project");
                editSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        // This method is intentionally left blank
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        // This method is intentionally left blank
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        String text = s.toString().trim();
                        savedProjects.clear();
                        for (int i = 0; i < allsavedProjects.size(); i++) {
                            if (allsavedProjects.get(i).getName().toLowerCase().contains(text.toLowerCase())) {
                               savedProjects.add(allsavedProjects.get(i));
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
                                savedProjects.clear();
                                for (int i = 0; i < allsavedProjects.size(); i++) {
                                    savedProjects.add(allsavedProjects.get(i));
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
                        savedProjects.get(i).setSelected(true);
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

    private void unselectAll() {
        for (int i = 0; i < mAdapter.getCount(); i++)
            if (mAdapter.isPositionSelected(i)) {
                savedProjects.get(i).setSelected(false);
                mAdapter.removeSelectedPosition(i);
            }
        restoreColorScheme();
        mAdapter.notifyDataSetChanged();
    }

    /**
     * @brief Removes selected project item
     */

    private void deleteItems() {
        ArrayList<Integer> selectedPositions = mAdapter.getSelectedPositions();
        boolean deleted = false;

        for(int selectedPosition : selectedPositions) {
            SavedProject project = savedProjects.get(selectedPosition);
            File file = new File(project.getFile().getPath());
            deleted = file.delete();

            if (deleted) {
                int selectedPos = -1;
                for (int i = 0; i < allsavedProjects.size(); i++) {
                    SavedProject sProject = allsavedProjects.get(i);
                    if (sProject.getName().equals(project.getName())) {
                        selectedPos = i;
                        break;
                    }
                }
                if (selectedPos != -1) {
                    allsavedProjects.remove(selectedPos);
                }
                savedProjects.remove(selectedPosition);
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
