package org.buildmlearn.toolkit.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.adapter.SavedProjectAdapter;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.model.SavedProject;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.utilities.Selection;
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
    public static boolean showTemplateSelectedMenu;
    public static View selectedView = null;
    public static int selectedPosition = -1;
    private AbsListView mListView;
    private SavedProjectAdapter mAdapter;
    private ToolkitApplication mToolkit;
    private Activity activity;
    private ArrayList<SavedProject> savedProjects;

    public void onDestroy() {
        super.onDestroy();
        selectedView = null;
        selectedPosition = -1;

    }

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
        for (int i = 0; i < file.length; i++) {

            Log.d(TAG, file[i].getAbsolutePath());
            File fXmlFile = new File(file[i].getAbsolutePath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                Log.d("Files", "Root element :" + doc.getDocumentElement().getAttribute("type"));
                savedProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (DOMException e) {
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
                if (selectedPosition == position) {
                    selectedPosition = -1;
                    view.setBackgroundResource(0);
                    Selection.restoreColorScheme(activity, getResources());
                    showTemplateSelectedMenu = false;
                } else {
                    if (selectedView != null) {
                        selectedView.setBackgroundResource(0);
                    }
                    selectedView = view;
                    selectedPosition = position;
                    Log.d(TAG, "Position: " + selectedPosition);
                    view.setBackgroundColor(getResources().getColor(R.color.color_divider));
                    Selection.changeColorScheme(activity, getResources());
                    showTemplateSelectedMenu = true;
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
            for (int i = 0; i < file.length; i++) {
                File fXmlFile = new File(file[i].getAbsolutePath());
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder;
                try {
                    dBuilder = dbFactory.newDocumentBuilder();
                    Document doc = dBuilder.parse(fXmlFile);
                    doc.getDocumentElement().normalize();
                    Log.d("Files", "Root element :" + doc.getDocumentElement().getAttribute("type"));
                    savedProjects.add(new SavedProject(fXmlFile, fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type"), fXmlFile.getAbsolutePath()));
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (DOMException e) {
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
     * {@inheritDoc}
     */
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
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
                        deleteItem(selectedPosition);
                        restoreSelectedView();
                    }
                });
                dialog.show();
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
    public void restoreSelectedView() {
        if (selectedView != null) {
            selectedPosition = -1;
            selectedView.setBackgroundResource(0);
        }
        Selection.restoreColorScheme(activity, getResources());
        showTemplateSelectedMenu = false;
    }
}
