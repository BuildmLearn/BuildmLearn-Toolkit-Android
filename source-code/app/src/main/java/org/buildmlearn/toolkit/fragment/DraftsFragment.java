package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.adapter.DraftProjectAdapter;
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
 * Created by scopeinfinity on 10/3/16.
 */

/**
 * @brief Fragment used to save drafts.
 */
public class DraftsFragment extends Fragment implements AbsListView.OnItemClickListener {

    private static final String TAG = "Draft Project Fragment";
    private AbsListView mListView;

    private boolean showTemplateSelectedMenu;

    private DraftProjectAdapter mAdapter;
    private ToolkitApplication mToolkit;
    private Activity activity;
    private ArrayList<SavedProject> draftProjects;
    private View selectedView = null;

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
        draftProjects = new ArrayList<>();

        String path = mToolkit.getDraftDir();
        Log.d("Files", "Path: " + path);


        reloadContent();
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
        mAdapter = new DraftProjectAdapter(getActivity(), draftProjects);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter.isPositionSelected(position)) {
                    mAdapter.removeSelectedPosition(position);
                    view.setBackgroundResource(0);
                    if (mAdapter.selectedPositionsSize() == 0)
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
        SavedProject project = draftProjects.get(position);
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

    private void setAdapter(DraftProjectAdapter adapter) {
        mListView.setAdapter(adapter);
        setEmptyText();
    }

    private void setEmptyText() {
        getView().findViewById(R.id.newProject).setVisibility(View.GONE);
        getView().findViewById(R.id.no_saved_project).setVisibility(View.GONE);
        getView().findViewById(R.id.no_saved_apks).setVisibility(View.GONE);
        if (mListView.getAdapter().getCount() == 0) {
            getView().findViewById(R.id.no_saved_drafts).setVisibility(View.VISIBLE);
        } else {
            getView().findViewById(R.id.no_saved_drafts).setVisibility(View.GONE);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        if (mAdapter != null) {
            reloadContent();
        }
        setEmptyText();
        super.onResume();
    }

    private void reloadContent() {

        draftProjects.clear();

        String path = mToolkit.getDraftDir();
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
                draftProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
            } catch (ParserConfigurationException | DOMException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(draftProjects, new Comparator<SavedProject>() {
            public int compare(SavedProject f1, SavedProject f2) {
                //In Decreasing order of last modified
                return (Long.valueOf(f2.getFile().lastModified()).compareTo(f1.getFile().lastModified()));
            }
        });

        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
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
        menu.clear();
        if (showTemplateSelectedMenu) {
            activity.getMenuInflater().inflate(R.menu.menu_project_selected, menu);
            menu.findItem(R.id.action_share).setVisible(false);
        } else if (mAdapter.getCount() > 0) {
            activity.getMenuInflater().inflate(R.menu.menu_draft, menu);
        }
    }

    /**
     * {@inheritDoc}
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:

                final AlertDialog dialogDelete = new AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.dialog_yes, null)
                        .setNegativeButton(R.string.dialog_no, null)
                        .create();
                dialogDelete.show();

                dialogDelete.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDelete.dismiss();
                        deleteItems();
                        restoreSelectedView();
                    }
                });
                break;

            case R.id.action_select_all:
                for(int i=0;i<mAdapter.getCount();i++) {
                    if (!mAdapter.isPositionSelected(i))
                    {
                        draftProjects.get(i).setSelected(true);
                        mAdapter.putSelectedPosition(i);
                        changeColorScheme();
                    }
                }
                mAdapter.notifyDataSetChanged();
                break;

            case R.id.action_unselect_all:
                unselectAll();
                break;

            case R.id.action_delete_all:

                final AlertDialog dialogDeleteAll = new AlertDialog.Builder(activity)
                        .setTitle(R.string.dialog_delete_all_title)
                        .setMessage(R.string.dialog_delete_all_msg)
                        .setPositiveButton(R.string.dialog_yes, null)
                        .setNegativeButton(R.string.dialog_no, null)
                        .create();
                dialogDeleteAll.show();

                dialogDeleteAll.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogDeleteAll.dismiss();
                        deleteAllDrafts();
                        restoreSelectedView();
                    }
                });
                break;
            default: //do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void unselectAll() {
        for (int i = 0; i < mAdapter.getCount(); i++)
            if (mAdapter.isPositionSelected(i)) {
                draftProjects.get(i).setSelected(false);
                mAdapter.removeSelectedPosition(i);
            }
        mAdapter.notifyDataSetChanged();
        restoreColorScheme();
    }

    /**
     * @brief Removes selected project item
     */
    private void deleteItems() {
        ArrayList<Integer> selectedPositions = mAdapter.getSelectedPositions();
        boolean deleted = false;
        for(int selectedPosition : selectedPositions) {
            SavedProject project = draftProjects.get(selectedPosition);
            File file = new File(project.getFile().getPath());
            deleted = file.delete();
            if (deleted) {
                draftProjects.remove(selectedPosition);
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
     * @brief Removes All Drafts
     */
    private void deleteAllDrafts() {
        File draftDir = new File(mToolkit.getDraftDir());
        File[] allDrafts = draftDir.listFiles();
        boolean allDeleted = true;
        for (File draft : allDrafts) {
            if (!draft.delete())
                allDeleted = false;
        }
        if (allDeleted)
            Toast.makeText(activity, getResources().getString(R.string.draft_deleted), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(activity, getResources().getString(R.string.draft_deleted_failed), Toast.LENGTH_SHORT).show();
        reloadContent();
        setEmptyText();

    }

    /**
     * @brief Removes selected color from the selected ListView item when switching from edit mode to normal mode
     */
    private void restoreSelectedView() {
        restoreColorScheme();
    }
}
