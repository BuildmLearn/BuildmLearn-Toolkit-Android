package org.buildmlearn.toolkit.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.internal.ThemeSingleton;
import com.cocosw.bottomsheet.BottomSheet;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.ToolkitApplication;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.model.KeyStoreDetails;
import org.buildmlearn.toolkit.model.Template;
import org.buildmlearn.toolkit.model.TemplateInterface;
import org.buildmlearn.toolkit.simulator.Simulator;
import org.buildmlearn.toolkit.utilities.FileUtils;
import org.buildmlearn.toolkit.utilities.KeyboardHelper;
import org.buildmlearn.toolkit.utilities.SignerThread;
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

/**
 * @brief Placeholder activity for all the templates
 * <p/>
 * A placeholder activty in which all the templates are loaded and allows the user to enter respective template
 * data, generate and save projects, APKs and sharing options.
 */

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
    private String oldFileName;
    private MaterialDialog mApkGenerationDialog;


    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldFileName = null;
        setContentView(R.layout.activity_template_editor);
        KeyboardHelper.hideKeyboard(this, findViewById(R.id.toolbar));
        KeyboardHelper.hideKeyboard(this,findViewById(R.id.template_editor_listview));
        KeyboardHelper.hideKeyboard(this,findViewById(R.id.empty));
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
                oldFileName = path;
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

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(Constants.TEMPLATE_OBJECT, selectedTemplate);
        outState.putInt(Constants.TEMPLATE_ID, templateId);
        outState.putString(Constants.PROJECT_FILE_PATH, oldFileName);
        super.onSaveInstanceState(outState);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "On activity result");
        selectedTemplate.onActivityResult(this, requestCode, resultCode, intent);
        super.onActivityResult(requestCode, resultCode, intent);
    }


    /**
     * @param adapter Adapter containing template data
     * @brief Populates ListView item by setting adapter to ListView. Also inflates Header View.
     * <p/>
     * Header view contains the editable author name and template title fields.
     */
    protected void populateListView(final BaseAdapter adapter) {
        if (templateEdtiorList == null) {
            templateEdtiorList = (ListView) findViewById(R.id.template_editor_listview);
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View templateHeader = inflater.inflate(R.layout.listview_header_template, templateEdtiorList, false);
        templateEdtiorList.addHeaderView(templateHeader, null, false);

        EditText authorEditText = (EditText) findViewById(R.id.author_name);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        authorEditText.setText(preferences.getString(getString(R.string.key_user_name), ""));
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

    /**
     * @brief Initialization function for setting up action bar
     */
    protected void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        templateEdtiorList = (ListView) findViewById(R.id.template_editor_listview);
        if (actionBar == null) {
            throw new AssertionError();
        }
        actionBar.setTitle(selectedTemplate.getTitle());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    /**
     * @brief Initialization function when the Temlpate Editor is created.
     */
    protected void setUpTemplateEditor() {
        Template[] templates = Template.values();
        template = templates[templateId];

        Class templateClass = template.getTemplateClass();
        try {
            Object templateObject = templateClass.newInstance();
            selectedTemplate = (TemplateInterface) templateObject;
            populateListView(selectedTemplate.newTemplateEditorAdapter(this));
            setUpActionBar();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief Initialization function when the Temlpate Editor is restored.
     */
    protected void restoreTemplateEditor(Bundle savedInstanceState) {
        selectedTemplate = (TemplateInterface) savedInstanceState.getSerializable(Constants.TEMPLATE_OBJECT);
        oldFileName = savedInstanceState.getString(Constants.PROJECT_FILE_PATH);
        templateId = savedInstanceState.getInt(Constants.TEMPLATE_ID);
        Template[] templates = Template.values();
        template = templates[templateId];
        if (selectedTemplate == null) {
            finish();
        } else {
            populateListView(selectedTemplate.currentTemplateEditorAdapter());
            setUpActionBar();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d(TAG, "onPrepareOptionsMenu");
        menu.clear();
        if (showTemplateSelectedMenu) {
            getMenuInflater().inflate(R.menu.menu_template_item_selected, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_template_editor, menu);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_delete:

                final MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.dialog_delete_title)
                        .content(R.string.dialog_delete_msg)
                        .positiveText(R.string.dialog_yes)
                        .negativeText(R.string.dialog_no)
                        .build();

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        selectedTemplate.deleteItem(selectedPosition);
                        restoreSelectedView();
                    }
                });

                dialog.show();

                break;
            case R.id.action_edit:
                selectedTemplate.editItem(this, selectedPosition);
                restoreSelectedView();
                break;
            case R.id.action_save:
                new BottomSheet.Builder(this).sheet(R.menu.bottom_sheet_template).listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String savedFilePath;
                        switch (id) {
                            case R.id.save_project:
                                saveProject();
                                break;
                            case R.id.share_project:
                                savedFilePath = saveProject();
                                if (savedFilePath == null || savedFilePath.length() == 0) {
                                    return;
                                }

                                Uri fileUri = Uri.fromFile(new File(savedFilePath));
                                ArrayList<Uri> uris = new ArrayList<Uri>();
                                Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                sendIntent.setType("application/zip");
                                uris.add(fileUri);
                                sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                                startActivity(Intent.createChooser(sendIntent, null));

                                break;
                            case R.id.share_apk:

                                savedFilePath = saveProject();
                                if (savedFilePath == null || savedFilePath.length() == 0) {
                                    return;
                                }
                                String keyPassword = getString(R.string.key_password);
                                String aliasName = getString(R.string.alias_name);
                                String aliaspassword = getString(R.string.alias_password);
                                KeyStoreDetails keyStoreDetails = new KeyStoreDetails("TestKeyStore.jks", keyPassword, aliasName, aliaspassword);
                                SignerThread signer = new SignerThread(getApplicationContext(), selectedTemplate.getApkFilePath(), saveProject(), keyStoreDetails, selectedTemplate.getAssetsFilePath(), selectedTemplate.getAssetsFileName());

                                mApkGenerationDialog = new MaterialDialog.Builder(TemplateEditor.this)
                                        .title(R.string.apk_progress_dialog)
                                        .content(R.string.apk_msg)
                                        .cancelable(false)
                                        .progress(true, 0)
                                        .show();

                                signer.setSignerThreadListener(new SignerThread.OnSignComplete() {
                                    @Override
                                    public void onSuccess(final String path) {
                                        Log.d(TAG, "APK generated");
                                        mApkGenerationDialog.dismiss();

                                        Uri fileUri = Uri.fromFile(new File(path));
                                        try {
                                            ArrayList<Uri> uris = new ArrayList<Uri>();
                                            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                            sendIntent.setType("application/vnd.android.package-archive");
                                            uris.add(fileUri);
                                            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                                            startActivity(Intent.createChooser(sendIntent, null));


                                        } catch (Exception e) {

                                            ArrayList<Uri> uris = new ArrayList<Uri>();
                                            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                                            sendIntent.setType("application/zip");
                                            uris.add(fileUri);
                                            sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                                            startActivity(Intent.createChooser(sendIntent, null));
                                        }

                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        if (e != null) {
                                            e.printStackTrace();
                                            mApkGenerationDialog.dismiss();
                                            Toast.makeText(TemplateEditor.this, "Build unsuccessful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                signer.start();

                                break;
                            case R.id.save_apk:
                                savedFilePath = saveProject();
                                if (savedFilePath == null || savedFilePath.length() == 0) {
                                    return;
                                }
                                keyPassword = getString(R.string.key_password);
                                aliasName = getString(R.string.alias_name);
                                aliaspassword = getString(R.string.alias_password);
                                keyStoreDetails = new KeyStoreDetails("TestKeyStore.jks", keyPassword, aliasName, aliaspassword);
                                signer = new SignerThread(getApplicationContext(), selectedTemplate.getApkFilePath(), saveProject(), keyStoreDetails, selectedTemplate.getAssetsFilePath(), selectedTemplate.getAssetsFileName());

                                mApkGenerationDialog = new MaterialDialog.Builder(TemplateEditor.this)
                                        .title(R.string.apk_progress_dialog)
                                        .content(R.string.apk_msg)
                                        .cancelable(false)
                                        .progress(true, 0)
                                        .show();

                                signer.setSignerThreadListener(new SignerThread.OnSignComplete() {
                                    @Override
                                    public void onSuccess(final String path) {
                                        Log.d(TAG, "APK generated");
                                        mApkGenerationDialog.dismiss();

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mApkGenerationDialog = new MaterialDialog.Builder(TemplateEditor.this)
                                                        .title("Apk Generated")
                                                        .content("Apk file saved at " + path)
                                                        .positiveText("okay")
                                                        .show();
                                            }
                                        });


                                    }

                                    @Override
                                    public void onFail(Exception e) {
                                        if (e != null) {
                                            e.printStackTrace();
                                            mApkGenerationDialog.dismiss();
                                            Toast.makeText(TemplateEditor.this, "Build unsuccessful", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                signer.start();
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
        return true;
    }

    /**
     * @brief Removes selected color from the selected ListView item when switching from edit mode to normal mode
     */
    public void restoreSelectedView() {
        if (selectedView != null) {
            selectedView.setBackgroundResource(0);
        }

        restoreColorScheme();
    }

    /**
     * @brief Changes the color scheme when switching from normal mode to edit mode.
     * <p/>
     * Edit mode is triggered, when the list item is long pressed.
     */
    public void changeColorScheme() {
        int primaryColor = getResources().getColor(R.color.color_primary_dark);
        int primaryColorDark = getResources().getColor(R.color.color_selected_dark);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().neutralColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().negativeColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(primaryColorDark);
            getWindow().setNavigationBarColor(primaryColor);
        }

        showTemplateSelectedMenu = true;
        invalidateOptionsMenu();
    }

    /**
     * @brief Restores the color scheme when switching from edit mode to normal mode.
     * <p/>
     * Edit mode is triggered, when the list item is long pressed.
     */
    public void restoreColorScheme() {
        int primaryColor = getResources().getColor(R.color.color_primary);
        int primaryColorDark = getResources().getColor(R.color.color_primary_dark);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().neutralColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().negativeColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(primaryColorDark);
            getWindow().setNavigationBarColor(primaryColor);
        }
        showTemplateSelectedMenu = false;
        invalidateOptionsMenu();
    }

    /**
     * Converts the current TemplateInterface object into a xml file. Xml file is saved in SAVE
     * Directory (defined in constants). File name is of the format: <title>_by_<author>.buildmlearn
     *
     * @return Absolute path of the saved file. Null if there is some error.
     * @brief Saves the current project into a .buildmlearn file.
     */
    protected String saveProject() {

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
                if (oldFileName != null) {
                    File tempFile = new File(oldFileName);
                    tempFile.delete();
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

    /**
     * @brief Start the simulator activity
     * <p/>
     * Start the simulator with the fragment returned by the selected template. Simulator is started as a new activity.
     */
    protected void startSimulator() {
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

    /**
     * @param path Path of the existing .buildmlearn file
     * @brief Converts an existing .buildmlearn file to TemplateInterface Object
     * <p/>
     * This function is used in loading existing files to editor. Reads file at a given path, parse the
     * file and convert into and convert it into TemplateInterface object.
     */
    protected void parseSavedFile(String path) {

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
            setUpActionBar();
            updateHeaderDetails(name, title);


        } catch (SAXException | IOException | ParserConfigurationException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }


    }

    /**
     * @param name  Name of the author
     * @param title Title of the template app
     * @brief Updates the title and author name in the header view.
     */
    protected void updateHeaderDetails(String name, String title) {
        EditText authorEditText = ((EditText) findViewById(R.id.author_name));
        EditText titleEditText = ((EditText) findViewById(R.id.template_title));
        authorEditText.setText(name);
        titleEditText.setText(title);
    }

    /**
     * @param adapter
     * @brief Sets the adapter to the ListView
     */
    protected void setAdapter(BaseAdapter adapter) {
        templateEdtiorList.setAdapter(adapter);
        setEmptyView();
    }

    /**
     * @brief Toggles the visibility of empty text if adapter has zero elements
     */
    protected void setEmptyView() {

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
