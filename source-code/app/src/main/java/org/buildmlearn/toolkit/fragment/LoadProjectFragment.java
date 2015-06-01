package org.buildmlearn.toolkit.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.adapter.SavedProjectAdapter;
import org.buildmlearn.toolkit.model.SavedProject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class LoadProjectFragment extends Fragment implements AbsListView.OnItemClickListener {

    private AbsListView mListView;

    private SavedProjectAdapter mAdapter;
    private ToolkitApplication mToolkit;
    private ArrayList<SavedProject> savedProjects;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mToolkit = (ToolkitApplication) getActivity().getApplicationContext();
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
            File fXmlFile = new File(file[i].getAbsolutePath());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = null;
            try {
                dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(fXmlFile);
                doc.getDocumentElement().normalize();
                Log.d("Files", "Root element :" + doc.getDocumentElement().getAttribute("type"));
                savedProjects.add(new SavedProject(fXmlFile.getName(), fXmlFile.lastModified(), doc.getDocumentElement().getAttribute("type")));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loadproject, container, false);
        mAdapter = new SavedProjectAdapter(getActivity(), savedProjects);
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        setEmptyText("No saved projects found");
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEmptyText("No saved projects found");
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }


}
