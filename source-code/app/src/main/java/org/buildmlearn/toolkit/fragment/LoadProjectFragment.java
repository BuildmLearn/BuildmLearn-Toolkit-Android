package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
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
    private ArrayList<SavedProject> savedProjects;
    private View selectedView = null;

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
            } catch (ParserConfigurationException | DOMException | IOException | SAXException e) {
                e.printStackTrace();
            }
        }

        Collections.sort(savedProjects, new Comparator<SavedProject>() {
            public int compare(SavedProject f1, SavedProject f2) {
                return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
            }
        });

        Collections.reverse(savedProjects);
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
        mAdapter = new SavedProjectAdapter(getActivity(), savedProjects);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mAdapter.getSelectedPosition() == position) {
                    mAdapter.setSelectedPosition(-1);
                    view.setBackgroundResource(0);
                    restoreColorScheme();
                } else {
                    if (selectedView != null) {
                        selectedView.setBackgroundResource(0);
                    }
                    selectedView = view;
                    view.setBackgroundColor(ContextCompat.getColor(mToolkit, R.color.color_divider));
                    mAdapter.setSelectedPosition(position);
                    Log.d(TAG, "Position: " + position);
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

            savedProjects.clear();

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
                    savedProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
                } catch (ParserConfigurationException | DOMException | IOException | SAXException e) {
                    e.printStackTrace();
                }
            }

            Collections.sort(savedProjects, new Comparator<SavedProject>() {
                public int compare(SavedProject f1, SavedProject f2) {
                    return Long.valueOf(f1.getFile().lastModified()).compareTo(f2.getFile().lastModified());
                }
            });

            Collections.reverse(savedProjects);
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
        int primaryColor = ContextCompat.getColor(mToolkit, R.color.color_primary_dark);
        int primaryColorDark = ContextCompat.getColor(mToolkit, R.color.color_selected_dark);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (showTemplateSelectedMenu) {
            activity.getMenuInflater().inflate(R.menu.menu_project_selected, menu);
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
                        deleteItem(mAdapter.getSelectedPosition());
                        restoreSelectedView();
                    }
                });
                dialog.show();
                break;
            default: //do nothing
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * @brief Removes selected project item
     */
    private void deleteItem(int selectedPosition) {
        SavedProject project = savedProjects.get(selectedPosition);
        File file = new File(project.getFile().getPath());
        boolean deleted = file.delete();
        if (deleted) {
            savedProjects.remove(selectedPosition);
            mAdapter.setSelectedPosition(-1);
            mAdapter.notifyDataSetChanged();
            setEmptyText();
            Toast.makeText(activity, "Project Successfully Deleted!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Project Deletion Failed!", Toast.LENGTH_SHORT).show();
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
}
