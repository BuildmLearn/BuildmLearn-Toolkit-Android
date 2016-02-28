package org.buildmlearn.toolkit.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

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
    File file[];
    private SavedProjectAdapter mAdapter;
    private ToolkitApplication mToolkit;
    private ArrayList<SavedProject> savedProjects;

    /**
     * {@inheritDoc}
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToolkit = (ToolkitApplication) getActivity().getApplicationContext();
        savedProjects = new ArrayList<>();
        String path = mToolkit.getSavedDir();
        Log.d("Files", "Path: " + path);


        File f = new File(path);
        file = f.listFiles();
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
        registerForContextMenu(mListView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == android.R.id.list) {
            MenuInflater inflater = getActivity().getMenuInflater();
            inflater.inflate(R.menu.menu_savedprojects_list, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.delete:
                int position = menuInfo.position;
                savedProjects.remove(position);
                if (file[position].delete()) {
                    Toast.makeText(getActivity(), "Deleted!", Toast.LENGTH_SHORT).show();
                    mAdapter.notifyDataSetChanged();
                    setEmptyText();
                } else
                    Toast.makeText(getActivity(), "Cannot delete!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
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
            Toast.makeText(getActivity(), "Invalid project file", Toast.LENGTH_SHORT).show();
        }
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
}
