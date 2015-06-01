package org.buildmlearn.toolkit.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.ThemeSingleton;
import com.cocosw.bottomsheet.BottomSheet;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.simulator.Simulator;
import org.buildmlearn.toolkit.utilities.FileUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class TemplateEditor extends AppCompatActivity {

    private final static String TAG = "TEMPLATE EDITOR";

    private ListView templateEdtiorList;
    private int templateId;
    private Template template;
    private TemplateInterface selectedTemplate;
    private int selectedPosition = -1;
    private boolean showTemplateSelectedMenu;
    private View selectedView = null;
    private ToolkitApplication toolkit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_editor);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        toolkit = (ToolkitApplication) getApplicationContext();
        templateId = getIntent().getIntExtra(Constants.TEMPLATE_ID, -1);
        if (templateId == -1) {
            Toast.makeText(this, "Invalid template ID, closing Template Editor activity", Toast.LENGTH_LONG).show();
            finish();
        }


        if (savedInstanceState == null) {
            String path = getIntent().getStringExtra(Constants.PROJECT_FILE_PATH);
            if (path == null) {
                setUpTemplateEditor();
            } else {
                parseSavedFile(path);
            }
        } else {
            restoreTemplateEditor(savedInstanceState);
        }

        findViewById(R.id.button_add_item).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedTemplate.addItem(TemplateEditor.this);
                hideEmptyView();
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.TEMPLATE_OBJECT, selectedTemplate);
        outState.putInt(Constants.TEMPLATE_ID, templateId);
        super.onSaveInstanceState(outState);
    }

    private void populateListView(final BaseAdapter adapter) {
        if (templateEdtiorList == null) {
            templateEdtiorList = (ListView) findViewById(R.id.template_editor_listview);
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View templateHeader = inflater.inflate(R.layout.listview_header_template, templateEdtiorList, false);
        templateEdtiorList.addHeaderView(templateHeader, null, false);
        setAdapter(adapter);

        templateEdtiorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    return false;
                }

                if (selectedPosition == position - 1) {
                    selectedPosition = -1;
                    view.setBackgroundResource(0);
                    restoreColorScheme();
                } else {
                    if (selectedView != null) {
                        selectedView.setBackgroundResource(0);
                    }
                    selectedView = view;
                    selectedPosition = position - 1;
                    Log.d(TAG, "Position: " + selectedPosition);
                    view.setBackgroundColor(getResources().getColor(R.color.color_divider));
                    changeColorScheme();
                }
                return true;
            }
        });

    }

    private void setUpActionBar(String title) {
        ActionBar actionBar = getSupportActionBar();
        templateEdtiorList = (ListView) findViewById(R.id.template_editor_listview);
        if (actionBar == null) {
            throw new AssertionError();
        }
        actionBar.setTitle(selectedTemplate.getTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setUpTemplateEditor() {
        Log.d(TAG, "Activity Created");
        Template[] templates = Template.values();
        template = templates[templateId];
        Class templateClass = template.getTemplateClass();
        try {
            Object templateObject = templateClass.newInstance();
            selectedTemplate = (TemplateInterface) templateObject;
            populateListView(selectedTemplate.newTemplateEditorAdapter(this));
            setUpActionBar(selectedTemplate.getTitle());
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void restoreTemplateEditor(Bundle savedInstanceState) {
        Log.d(TAG, "Activity Restored");
        selectedTemplate = (TemplateInterface) savedInstanceState.getSerializable(Constants.TEMPLATE_OBJECT);
        templateId = savedInstanceState.getInt(Constants.TEMPLATE_ID);
        Template[] templates = Template.values();
        template = templates[templateId];
        if (selectedTemplate == null) {
            Toast.makeText(this, "Unable to restore Activity state, finishing Template Editor activity", Toast.LENGTH_LONG).show();
            finish();
        } else {
            Toast.makeText(this, selectedTemplate.onAttach(), Toast.LENGTH_LONG).show();
            populateListView(selectedTemplate.currentTemplateEditorAdapter());
            setUpActionBar(selectedTemplate.getTitle());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        if (showTemplateSelectedMenu) {
            getMenuInflater().inflate(R.menu.menu_template_item_selected, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_template_editor, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:
                selectedTemplate.deleteItem(selectedPosition);
                restoreSelectedView();
                break;
            case R.id.action_edit:
                selectedTemplate.editItem(this, selectedPosition);
                restoreSelectedView();
                break;
            case R.id.action_save:
                new BottomSheet.Builder(this).sheet(R.menu.bottom_sheet_template).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        switch (id) {
                            case R.id.save_project:
                                saveProject();
                                break;
                            case R.id.share_apk:
                                Uri fileUri = Uri.fromFile(new File(toolkit.getApkDir() + "FlashCardTemplateApp_v2.0.apk"));
                                Intent shareIntent = new Intent();
                                shareIntent.setAction(Intent.ACTION_SEND);
                                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                                shareIntent.setType("application/vnd.android.package-archive");
                                startActivity(Intent.createChooser(shareIntent, getString(R.string.bottom_sheet_share_apk)));
                                break;
                        }
                    }
                }).show();
                break;
            case R.id.action_simulate:
                startSimulator();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void restoreSelectedView() {
        if (selectedView != null) {
            selectedView.setBackgroundResource(0);
        }

        restoreColorScheme();
    }

    public void changeColorScheme() {
        int primaryColor = getResources().getColor(R.color.color_primary_dark);
        int primaryColorDark = getResources().getColor(R.color.color_selected_dark);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = primaryColor;
        ThemeSingleton.get().neutralColor = primaryColor;
        ThemeSingleton.get().negativeColor = primaryColor;
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(primaryColorDark);
            getWindow().setNavigationBarColor(primaryColor);
        }

        showTemplateSelectedMenu = true;
        invalidateOptionsMenu();
    }

    public void restoreColorScheme() {
        int primaryColor = getResources().getColor(R.color.color_primary);
        int primaryColorDark = getResources().getColor(R.color.color_primary_dark);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = primaryColor;
        ThemeSingleton.get().neutralColor = primaryColor;
        ThemeSingleton.get().negativeColor = primaryColor;
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(primaryColorDark);
            getWindow().setNavigationBarColor(primaryColor);
        }
        showTemplateSelectedMenu = false;
        invalidateOptionsMenu();
    }

    private String saveProject() {

        EditText authorEditText = ((EditText) findViewById(R.id.author_name));
        EditText titleEditText = ((EditText) findViewById(R.id.template_title));
        String author = ((EditText) findViewById(R.id.author_name)).getText().toString();
        String title = ((EditText) findViewById(R.id.template_title)).getText().toString();
        if (author.equals("")) {
            authorEditText.setError("Author name is required");
        } else if (title.equals("")) {
            titleEditText.setError("Title is required");
        } else {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder;
            try {

                docBuilder = docFactory.newDocumentBuilder();
                Document doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("buildmlearn_application");
                Attr attr = doc.createAttribute("type");
                attr.setValue(getResources().getString(template.getType()));
                rootElement.setAttributeNode(attr);

                Element authorElement = doc.createElement("author");
                rootElement.appendChild(authorElement);

                Element nameElement = doc.createElement("name");
                nameElement.appendChild(doc.createTextNode(author));

                authorElement.appendChild(nameElement);

                Element titleElement = doc.createElement("title");
                titleElement.appendChild(doc.createTextNode(title));
                rootElement.appendChild(titleElement);

                doc.appendChild(rootElement);
                Element dataElement = doc.createElement("data");
                rootElement.appendChild(dataElement);
                if (selectedTemplate.getItems(doc).size() == 0) {
                    Toast.makeText(this, "Unable to perform action: No Data", Toast.LENGTH_SHORT).show();
                    return null;
                }
                for (Element item : selectedTemplate.getItems(doc)) {
                    dataElement.appendChild(item);
                }
                String saveFileName = title + " by " + author + ".buildmlearn";
                saveFileName = saveFileName.replaceAll(" ", "-");
                FileUtils.saveXmlFile(toolkit.getSavedDir(), saveFileName, doc);
                return toolkit.getSavedDir() + saveFileName;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private void startSimulator() {
        String filePath = saveProject();
        if (filePath == null || filePath.equals("")) {
            Toast.makeText(this, "Build unsuccessful", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent simulatorIntent = new Intent(getApplicationContext(), Simulator.class);
        simulatorIntent.putExtra(Constants.TEMPLATE_ID, templateId);
        simulatorIntent.putExtra(Constants.SIMULATOR_FILE_PATH, filePath);
        startActivity(simulatorIntent);

    }

    private void parseSavedFile(String path) {

        try {
            File fXmlFile = new File(path);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc;
            doc = dBuilder.parse(fXmlFile);
            doc.getDocumentElement().normalize();
            Element authorElement = (Element) doc.getElementsByTagName("author").item(0);
            Element nameElement = (Element) authorElement.getElementsByTagName("name").item(0);
            String name = nameElement.getTextContent();
            String title = doc.getElementsByTagName("title").item(0).getTextContent();
            NodeList nList = doc.getElementsByTagName("item");
            ArrayList<Element> items = new ArrayList<>();
            for (int i = 0; i < nList.getLength(); i++) {
                Node nodeItem = nList.item(i);
                if (nodeItem.getNodeType() == Node.ELEMENT_NODE) {
                    items.add((Element) nodeItem);
                }
            }

            Log.d(TAG, "Activity Created");
            Template[] templates = Template.values();
            template = templates[templateId];
            Class templateClass = template.getTemplateClass();

            Object templateObject = templateClass.newInstance();
            selectedTemplate = (TemplateInterface) templateObject;
            populateListView(selectedTemplate.loadProjectTemplateEditor(this, items));
            setUpActionBar(selectedTemplate.getTitle());
            updateHeaderDetails(name, title);


        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }


    }

    private void updateHeaderDetails(String name, String title) {
        EditText authorEditText = ((EditText) findViewById(R.id.author_name));
        EditText titleEditText = ((EditText) findViewById(R.id.template_title));
        authorEditText.setText(name);
        titleEditText.setText(title);
    }

    private void setAdapter(BaseAdapter adapter) {
        templateEdtiorList.setAdapter(adapter);
        setEmptyView();
    }

    private void setEmptyView() {

        if (templateEdtiorList.getAdapter().getCount() == 1) {
            findViewById(R.id.empty).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.empty).setVisibility(View.GONE);
        }
    }


    private void hideEmptyView() {
        findViewById(R.id.empty).setVisibility(View.GONE);
    }
}
