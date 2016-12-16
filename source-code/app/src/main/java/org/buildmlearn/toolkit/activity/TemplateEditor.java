package org.buildmlearn.toolkit.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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

    private static final String TAG = "TEMPLATE EDITOR";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 100;
    private final Handler handlerToast = new Handler() {
        public void handleMessage(Message message) {
            if (message.arg1 == -1) {
                Toast.makeText(TemplateEditor.this, R.string.build_unsuccessfull, Toast.LENGTH_SHORT).show();
            }
        }
    };
    private ListView templateEdtiorList;
    private ListView templateMetaList;
    private int templateId;
    private Template template;
    private TemplateInterface selectedTemplate;
    private int selectedPosition = -1;
    private boolean showTemplateSelectedMenu;
    private View selectedView;
    private EditText titleEditText;
    private EditText authorEditText;
    private ToolkitApplication toolkit;
    private String oldFileName;
    private ProgressDialog mApkGenerationDialog;
    private String lastActivity;
    private String lastFragment;


    public void openBottomSheet (View v) {

        View view = getLayoutInflater ().inflate (R.layout.bottom_sheet_view, null);
        TextView txt_save_apk = (TextView)view.findViewById( R.id.txt_save_apk);
        TextView txt_save_project = (TextView)view.findViewById( R.id.txt_save_project);
        TextView txt_share_apk = (TextView)view.findViewById( R.id.txt_share_apk);
        final TextView txt_shareProject = (TextView)view.findViewById( R.id.txt_share_project);

        final Dialog mBottomSheetDialog = new Dialog (TemplateEditor.this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView (view);
        mBottomSheetDialog.setCancelable (true);
        mBottomSheetDialog.getWindow ().setLayout (LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow ().setGravity (Gravity.BOTTOM);
        mBottomSheetDialog.show ();


//        save project
        txt_save_project.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ReturnValidDetails obj = detailsValid("saveProject");
                if(obj.result)
                    saveProject(obj);
                mBottomSheetDialog.dismiss();
            }
        });

        //share project
        txt_shareProject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                shareProject();
                mBottomSheetDialog.dismiss();
            }
        });

        txt_share_apk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                share_apk();
                mBottomSheetDialog.dismiss();
            }
        });

        txt_save_apk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                save_apk();
                mBottomSheetDialog.dismiss();
            }
        });
    }

    private void save_apk() {

        String savedFilePath = generateAPK("saveAPK");
        if(("empty file".equals(savedFilePath))){
            Toast.makeText(getApplicationContext(), R.string.failed_apk_generation + "\n" +
                    R.string.insert_meta_data, Toast.LENGTH_SHORT).show();
            return;
        } else if ("insufficient".equals(savedFilePath)){
            Toast.makeText(getApplicationContext(), R.string.failed_apk_generation + "\n" +
                    R.string.enter_one_question, Toast.LENGTH_SHORT).show();
            return;
        } else if ("not valid".equals(savedFilePath)){
            return;
        } else if ("".equals(savedFilePath)){
            Toast.makeText(getApplicationContext(), R.string.failed_apk_generation, Toast.LENGTH_SHORT).show();
            return;
        }

        String keyPassword = getString(R.string.key_password);
        String aliasName = getString(R.string.alias_name);
        String aliaspassword = getString(R.string.alias_password);
        KeyStoreDetails keyStoreDetails = new KeyStoreDetails(keyPassword, aliasName, aliaspassword);
        SignerThread signer = new SignerThread(getApplicationContext(), selectedTemplate.getApkFilePath(), generateAPK("saveAPK"), keyStoreDetails, selectedTemplate.getAssetsFilePath(), selectedTemplate.getAssetsFileName(TemplateEditor.this));

        mApkGenerationDialog = new ProgressDialog(TemplateEditor.this, R.style.AppDialogTheme);
        mApkGenerationDialog.setTitle(R.string.apk_progress_dialog);
        mApkGenerationDialog.setMessage(getString(R.string.apk_msg));
        mApkGenerationDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mApkGenerationDialog.setCancelable(false);
        mApkGenerationDialog.setProgress(0);
        mApkGenerationDialog.show();

        signer.setSignerThreadListener(new SignerThread.OnSignComplete() {
            @Override
            public void onSuccess(final String path) {
                Log.d(TAG, "APK generated");
                mApkGenerationDialog.dismiss();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog dialog = new AlertDialog.Builder(TemplateEditor.this)
                                .setTitle("Apk Generated")
                                .setMessage("Apk file saved at " + path)
                                .setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                });


            }

            @Override
            public void onFail(Exception e) {
                if (e != null) {
                    e.printStackTrace();
                    mApkGenerationDialog.dismiss();
                    Message message = handlerToast.obtainMessage();
                    message.arg1 = -1;
                    handlerToast.sendMessage(message);
                }
            }
        });

        signer.start();
    }

    private void share_apk() {

        String savedFilePath = generateAPK("shareAPK");

        if ("empty file".equals(savedFilePath)) {
            Toast.makeText(getApplicationContext(), R.string.failed_apk_generation + "\n" +
                    R.string.insert_meta_data, Toast.LENGTH_SHORT).show();
            return;
        } else if ("insufficient".equals(savedFilePath)){
            Toast.makeText(getApplicationContext(), R.string.failed_apk_generation + "\n" +
                    R.string.enter_one_question, Toast.LENGTH_SHORT).show();
            return;
        } else if ("not valid".equals(savedFilePath)){
            return;
        } else if ("".equals(savedFilePath)) {
            Toast.makeText(getApplicationContext(),  R.string.failed_apk_generation, Toast.LENGTH_SHORT).show();
            return;
        }

        String keyPassword = getString(R.string.key_password);
        String aliasName = getString(R.string.alias_name);
        String aliaspassword = getString(R.string.alias_password);
        KeyStoreDetails keyStoreDetails = new KeyStoreDetails(keyPassword, aliasName, aliaspassword);
        SignerThread signer = new SignerThread(getApplicationContext(), selectedTemplate.getApkFilePath(), generateAPK("saveAPK"), keyStoreDetails, selectedTemplate.getAssetsFilePath(), selectedTemplate.getAssetsFileName(TemplateEditor.this));

        mApkGenerationDialog = new ProgressDialog(TemplateEditor.this, R.style.AppDialogTheme);
        mApkGenerationDialog.setTitle(R.string.apk_progress_dialog);
        mApkGenerationDialog.setMessage(getString(R.string.apk_msg));
        mApkGenerationDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mApkGenerationDialog.setCancelable(false);
        mApkGenerationDialog.setProgress(0);
        mApkGenerationDialog.show();

        signer.setSignerThreadListener(new SignerThread.OnSignComplete() {
            @Override
            public void onSuccess(final String path) {
                Log.d(TAG, "APK generated");
                mApkGenerationDialog.dismiss();

                Uri fileUri = Uri.fromFile(new File(path));
                try {
                    ArrayList<Uri> uris = new ArrayList<>();
                    Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
                    sendIntent.setType("application/vnd.android.package-archive");
                    uris.add(fileUri);
                    sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
                    startActivity(Intent.createChooser(sendIntent, null));


                } catch (Exception e) {

                    ArrayList<Uri> uris = new ArrayList<>();
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
                    Message message = handlerToast.obtainMessage();
                    message.arg1 = -1;
                    handlerToast.sendMessage(message);
                }
            }
        });

        signer.start();
    }

    private void shareProject() {

        String savedFilePath = generateAPK("shareProject");

        if ("empty file".equals(savedFilePath)) {
            Toast.makeText(getApplicationContext(), R.string.share_empty_project, Toast.LENGTH_SHORT).show();
            return;
        } else if ("insufficient".equals(savedFilePath)){
            Toast.makeText(getApplicationContext(), R.string.enter_one_question, Toast.LENGTH_SHORT).show();
            return;
        }
        else if ("not valid".equals(savedFilePath)){
            return;
        } else if ("".equals(savedFilePath)) {
            Toast.makeText(getApplicationContext(), R.string.failed_share_project, Toast.LENGTH_SHORT).show();
            return;
        }

        Uri fileUri = Uri.fromFile(new File(savedFilePath));
        ArrayList<Uri> uris = new ArrayList<>();
        Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        sendIntent.setType("application/zip");
        uris.add(fileUri);
        sendIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        startActivity(Intent.createChooser(sendIntent, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        oldFileName = null;
        setContentView(R.layout.activity_template_editor);
        KeyboardHelper.hideKeyboard(this, findViewById(R.id.toolbar));
        KeyboardHelper.hideKeyboard(this, findViewById(R.id.template_editor_listview));
        KeyboardHelper.hideKeyboard(this, findViewById(R.id.empty));
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        toolkit = (ToolkitApplication) getApplicationContext();
        templateId = getIntent().getIntExtra(Constants.TEMPLATE_ID, -1);
        lastFragment = getIntent().getStringExtra("lastFragment");
        lastActivity = getIntent().getStringExtra("lastActivity");
        if (templateId == -1) {
            Toast.makeText(this, R.string.invalid_template_id_closing, Toast.LENGTH_LONG).show();
            finish();
        }

        titleEditText = (EditText) findViewById(R.id.template_title);
        authorEditText = (EditText) findViewById(R.id.author_name);

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
                if ((templateId == 5 || templateId == 7) && selectedTemplate.currentMetaEditorAdapter().isEmpty()) {
                    selectedTemplate.addMetaData(TemplateEditor.this);
                } else {
                    selectedTemplate.addItem(TemplateEditor.this);
                }
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    ToolkitApplication mToolkitApplication = new ToolkitApplication();
                    mToolkitApplication.storagePathsValidate();
                }
                return;
            }

        }
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
     * @param adapter Adapter containing template meta data
     * @brief Populates meta ListView item by setting adapter to ListView.
     */
    private void populateMetaView(final BaseAdapter adapter) {
        if (templateMetaList == null) {
            templateMetaList = (ListView) findViewById(R.id.template_meta_listview);
        }

        setAdapterMeta(adapter);

        templateMetaList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e(getClass().getName(), " " + position);

                if (selectedPosition == -2) {
                    selectedPosition = -1;
                    if (view instanceof CardView) {
                        ((CardView) view).setCardBackgroundColor(Color.WHITE);
                    } else {
                        view.setBackgroundResource(0);
                    }
                    restoreColorScheme();
                } else {
                    if (selectedView != null) {
                        if (selectedView instanceof CardView) {
                            ((CardView) selectedView).setCardBackgroundColor(Color.WHITE);
                        } else {
                            selectedView.setBackgroundResource(0);
                        }
                    }
                    selectedView = view;
                    selectedPosition = -2;
                    Log.d(TAG, "Position: " + selectedPosition);

                    if (view instanceof CardView) {
                        ((CardView) view).setCardBackgroundColor(Color.LTGRAY);
                    } else {
                        view.setBackgroundColor(ContextCompat.getColor(toolkit, R.color.color_divider));
                    }
                    changeColorScheme();
                }
                return true;
            }
        });
    }

    /**
     * @param adapter Adapter containing template data
     * @brief Populates ListView item by setting adapter to ListView. Also inflates Header View.
     * <p/>
     * Header view contains the editable author name and template title fields.
     */
    private void populateListView(final BaseAdapter adapter) {
        if (templateEdtiorList == null) {
            templateEdtiorList = (ListView) findViewById(R.id.template_editor_listview);
        }
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View templateHeader = inflater.inflate(R.layout.listview_header_template, templateEdtiorList, false);
        templateEdtiorList.addHeaderView(templateHeader, null, false);

        EditText authorEditText = (EditText) findViewById(R.id.author_name);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        assert authorEditText != null;
        authorEditText.setText(preferences.getString(getString(R.string.key_user_name), ""));

        setAdapter(adapter);

        templateEdtiorList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e(getClass().getName(), " " + position);
                if (position == 0) {
                    return false;
                }

                if (selectedPosition == position - 1) {
                    selectedPosition = -1;
                    if (view instanceof CardView) {
                        ((CardView) view).setCardBackgroundColor(Color.WHITE);
                    } else {
                        view.setBackgroundResource(0);
                    }
                    restoreColorScheme();
                } else {
                    if (selectedView != null) {
                        if (selectedView instanceof CardView) {
                            ((CardView) selectedView).setCardBackgroundColor(Color.WHITE);
                        } else {
                            selectedView.setBackgroundResource(0);
                        }
                    }
                    selectedView = view;
                    selectedPosition = position - 1;
                    Log.d(TAG, "Position: " + selectedPosition);
                    if (view instanceof CardView) {
                        ((CardView) view).setCardBackgroundColor(Color.LTGRAY);
                    } else {
                        view.setBackgroundColor(ContextCompat.getColor(toolkit, R.color.color_divider));
                    }
                    changeColorScheme();
                }
                return true;
            }
        });

    }

    /**
     * @brief Initialization function for setting up action bar
     */
    private void setUpActionBar() {
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
    private void setUpTemplateEditor() {
        Template[] templates = Template.values();
        template = templates[templateId];

        Class templateClass = template.getTemplateClass();
        try {
            Object templateObject = templateClass.newInstance();
            selectedTemplate = (TemplateInterface) templateObject;
            selectedTemplate.setTemplateId(templateId);
            populateListView(selectedTemplate.newTemplateEditorAdapter(this));
            if (templateId == 5 || templateId == 7) {
                populateMetaView(selectedTemplate.newMetaEditorAdapter(this));
            }
            setUpActionBar();

        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        catch ( IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @brief Initialization function when the Temlpate Editor is restored.
     */
    private void restoreTemplateEditor(Bundle savedInstanceState) {
        selectedTemplate = (TemplateInterface) savedInstanceState.getSerializable(Constants.TEMPLATE_OBJECT);
        oldFileName = savedInstanceState.getString(Constants.PROJECT_FILE_PATH);
        templateId = savedInstanceState.getInt(Constants.TEMPLATE_ID);
        Template[] templates = Template.values();
        template = templates[templateId];
        if (selectedTemplate == null) {
            finish();
        } else {
            populateListView(selectedTemplate.currentTemplateEditorAdapter());
            if (templateId == 5 || templateId == 7) {
                populateMetaView(selectedTemplate.currentMetaEditorAdapter());
            }
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

                final AlertDialog dialog = new AlertDialog.Builder(this)
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
                        selectedTemplate.deleteItem(TemplateEditor.this, selectedPosition);
                        selectedPosition = -1;
                        restoreSelectedView();
                    }
                });

                break;
            case R.id.action_edit:
                selectedTemplate.editItem(this, selectedPosition);
                selectedPosition = -1;
                restoreSelectedView();
                break;
            case R.id.action_save:
                openBottomSheet(LayoutInflater.from(TemplateEditor.this).inflate(R.layout.bottom_sheet_view, null));
                break;
            case R.id.action_simulate:
                startSimulator();
                break;
            case android.R.id.home:
                onBackPressed();
                break;
            default: //do nothing
                break;
        }
        return true;
    }

    /**
     * @brief Removes selected color from the selected ListView item when switching from edit mode to normal mode
     */
    public void restoreSelectedView() {
        if (selectedView != null) {
            if (selectedView instanceof CardView) {
                ((CardView) selectedView).setCardBackgroundColor(Color.WHITE);
            } else {
                selectedView.setBackgroundResource(0);
            }
        }

        restoreColorScheme();
    }

    /**
     * @brief Changes the color scheme when switching from normal mode to edit mode.
     * <p/>
     * Edit mode is triggered, when the list item is long pressed.
     */
    private void changeColorScheme() {
        int primaryColor = ContextCompat.getColor(toolkit, R.color.color_primary_dark);
        int primaryColorDark = ContextCompat.getColor(toolkit, R.color.color_selected_dark);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
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
    private void restoreColorScheme() {
        int primaryColor = ContextCompat.getColor(toolkit, R.color.color_primary);
        int primaryColorDark = ContextCompat.getColor(toolkit, R.color.color_primary_dark);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));

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
    private String saveProject(ReturnValidDetails obj) {

        Document doc = obj.doc;
        Element dataElement = obj.element;

        for (Element item : selectedTemplate.getItems(doc)) {
            dataElement.appendChild(item);
        }

        if (oldFileName != null) {
            File tempFile = new File(oldFileName);
            tempFile.delete();
            oldFileName = null;
        }
        String saveFileName = obj.title + " by " + obj.author + ".buildmlearn";
        saveFileName = saveFileName.replaceAll(" ", "-");

        boolean isSaved=FileUtils.saveXmlFile(toolkit.getSavedDir(), saveFileName, doc);

        if(isSaved) {
            oldFileName = toolkit.getSavedDir() + saveFileName;
            Toast.makeText(this, "Project Successfully Saved!", Toast.LENGTH_SHORT).show();
            return oldFileName;
        }
        else {
            titleEditText = (EditText)findViewById(R.id.template_title);
            titleEditText.requestFocus();
            titleEditText.setError("File Already exists");
            return "File already exists";
        }
    }

    @Override
    public void onBackPressed() {

        String title = "Test";
        String author = "Teacher";

        boolean docEmpty = false;
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
                docEmpty = true;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        ReturnValidDetails obj;
        if (docEmpty && "templateActivity".equals(lastActivity)) {
            super.onBackPressed();
        }
        else if ("projectFragment".equals(lastFragment)){
            obj = detailsValid("saveProject");
            if ((selectedTemplate.getItems(obj.doc).size() == 0) || selectedTemplate.getItems(obj.doc).size() < 2 && (templateId == 5 || templateId == 7)){
                deleteProject();
            }
            else if(obj.result) {
                saveProject(obj);
                super.onBackPressed();
            }
        }

        else if ("draftsFragment".equals(lastFragment)){
            obj = detailsValid("savedDraft");
            if (selectedTemplate.getItems(obj.doc).size() == 0 || selectedTemplate.getItems(obj.doc).size() < 2 && (templateId == 5 || templateId == 7)){
                deleteDraft();
            }
            else if(obj.result) {
                saveDraft(obj);
                super.onBackPressed();
            }
        }

        else {
            whatToDo();
        }
    }

    /**
     * Converts the current TemplateInterface object into a xml file. Xml file is saved in DRAFT
     * Directory (defined in constants). File name is of the format: draft<0-X>.buildmlearn
     *
     * @return Absolute path of the saved file. Null if there is some error.
     * @brief Saves the current project into a .buildmlearn file.
     */
    private String saveDraft(ReturnValidDetails obj) {

        Document doc = obj.doc;
        Element dataElement = obj.element;

        for (Element item : selectedTemplate.getItems(doc)) {
            dataElement.appendChild(item);
        }

        int draftFileIndex = 0;
        File draftDir = new File(toolkit.getDraftDir());
        String probableFileName = "draft" + draftFileIndex + ".buildmlearn";
        File probableFile = new File(draftDir, probableFileName);

        while (probableFile.exists()) {
            draftFileIndex++;
            probableFileName = "draft" + draftFileIndex + ".buildmlearn";
            probableFile = new File(draftDir, probableFileName);
        }

        File tempFile = new File(toolkit.getDraftDir(), ".temp");
        File oldFile = null;
        if (oldFileName != null)
            oldFile = new File(oldFileName);

        FileUtils.saveXmlFile(toolkit.getDraftDir(), ".temp", doc);
        if (oldFile == null || !FileUtils.equalContent(tempFile, oldFile)) {
            tempFile.renameTo(probableFile);
            Toast.makeText(getApplicationContext(), R.string.draft_saved, Toast.LENGTH_SHORT).show();
            finish();
            return toolkit.getDraftDir() + probableFileName;
        } else {
            File newFile = new File(toolkit.getDraftDir(), ".temp");
            newFile.delete();
        }
        return null;
    }


    /**
     * @brief Start the simulator activity
     * <p/>
     * Start the simulator with the fragment returned by the selected template. Simulator is started as a new activity.
     * String message contains file response which will be filepath if successfully saved and otherwise error message.
     */
    private void startSimulator() {

        String message = generateAPK("startSimulator");

        if ("empty file".equals(message)){
            Toast.makeText(getApplicationContext(), getString(R.string.failed_emulator) +
                    getString(R.string.enter_meta_data), Toast.LENGTH_SHORT).show();
            return;
        } else if ("insufficient".equals(message)){
            Toast.makeText(getApplicationContext(), getString(R.string.failed_emulator)+ "\n" +
                    R.string.enter_one_question, Toast.LENGTH_SHORT).show();
            return;
        } else if ("".equals(message)) {
            Toast.makeText(this, getString(R.string.failed_emulator), Toast.LENGTH_SHORT).show();
            return;
        } else if ("not valid".equals(message)){
            return;
        }
        Intent simulatorIntent = new Intent(getApplicationContext(), Simulator.class);
        simulatorIntent.putExtra(Constants.TEMPLATE_ID, templateId);
        simulatorIntent.putExtra(Constants.SIMULATOR_FILE_PATH, message);
        startActivity(simulatorIntent);

    }

    /**
     * @param path Path of the existing .buildmlearn file
     * @brief Converts an existing .buildmlearn file to TemplateInterface Object
     * <p/>
     * This function is used in loading existing files to editor. Reads file at a given path, parse the
     * file and convert into and convert it into TemplateInterface object.
     */
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
            selectedTemplate.setTemplateId(templateId);
            populateListView(selectedTemplate.loadProjectTemplateEditor(this, items));
            if (templateId == 5 || templateId == 7) {
                populateMetaView(selectedTemplate.loadProjectMetaEditor(this, doc));
            }
            File draftDir = new File(toolkit.getDraftDir());
            if (fXmlFile.getParentFile().compareTo(draftDir) == 0) {
                //If Draft File
//                fXmlFile.delete();
            }
            setUpActionBar();
            updateHeaderDetails(name, title);


        } catch (SAXException | IOException | ParserConfigurationException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }


    }

    /**
     * @param name  Name of the author
     * @param title Title of the template app
     * @brief Updates the title and author name in the header view.
     */
    private void updateHeaderDetails(String name, String title) {
        EditText authorEditText = (EditText) findViewById(R.id.author_name);
        titleEditText = (EditText) findViewById(R.id.template_title);
        assert authorEditText != null;
        authorEditText.setText(name);
        assert titleEditText != null;
        titleEditText.setText(title);
    }

    /**
     * @param adapter
     * @brief Sets the adapter to the ListView
     */
    private void setAdapter(BaseAdapter adapter) {
        templateEdtiorList.setAdapter(adapter);
    }

    /**
     * @param adapter
     * @brief Sets the adapter to the ListView
     */
    private void setAdapterMeta(BaseAdapter adapter) {
        templateMetaList.setAdapter(adapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mApkGenerationDialog != null) {
            mApkGenerationDialog.dismiss();
            mApkGenerationDialog = null;
        }
    }

    /**
     * Asks the user if the current data is to be saved as a project or draft. Depending on the user input it calls the intended function.
     */

    private void whatToDo() {
        AlertDialog.Builder builderNew = new AlertDialog.Builder(this);

        builderNew.setTitle(getString(R.string.confirm_action));
        builderNew.setMessage(getString(R.string.save_draft_project));

        builderNew.setPositiveButton(getString(R.string.dialog_project), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                ReturnValidDetails obj = detailsValid("saveProject");
                if(obj.result) {
                    saveProject(obj);
                    finish();
                }
            }
        });

        builderNew.setNegativeButton(getString(R.string.dialog_draft), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReturnValidDetails obj = detailsValid("saveDraft");
                if(obj.result){
                    saveDraft(obj);
                    finish();
                }
            }
        });

        builderNew.setNeutralButton(getString(R.string.dialog_discard), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });

        AlertDialog alert = builderNew.create();
        alert.show();
    }

    class ReturnValidDetails {

        private String title;
        private String author;
        private Document doc = null;
        private Element element = null;
        private boolean result = true;
    }

    /**
     * @brief Checks title, author name and document data.
     * @return object of ReturnValidDetails which  doc, element and a boolean variable
     */
    private ReturnValidDetails detailsValid ( String callingFunction) {

        ReturnValidDetails obj = new ReturnValidDetails();

        EditText titleEditText = (EditText) findViewById(R.id.template_title);
        assert titleEditText != null;
        String title = titleEditText.getText().toString().trim();
        if("".equals(title)){
            titleEditText.requestFocus();
            titleEditText.setError(getString(R.string.enter_project_title));
            obj.result = false;
            return obj;
        }
        if(!Character.isLetterOrDigit(title.charAt(0))) {
            titleEditText.requestFocus();
            titleEditText.setError(getString(R.string.title_valid));
            obj.result = false;
            return obj;
        }
        obj.title = title;

        EditText authorEditText = (EditText) findViewById(R.id.author_name);
        assert authorEditText != null;
        String author = authorEditText.getText().toString().trim();
        if("".equals(author)){
            authorEditText.requestFocus();
            authorEditText.setError(getString(R.string.enter_author_name));
            obj.result = false;
            return obj;
        }
        if(!Character.isLetter(author.charAt(0))) {
            authorEditText.requestFocus();
            authorEditText.setError(getString(R.string.valid_msg_name));
            obj.result = false;
            return obj;
        }
        obj.author = author;

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;

        try{
            docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Attr attr = doc.createAttribute("type");
            attr.setValue(getResources().getString(template.getType()));

            Element rootElement = doc.createElement("buildmlearn_application");
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

            obj.doc = doc;
            obj.element = dataElement;

            if("saveProject".equals(callingFunction)) {
                if (selectedTemplate.getItems(doc).size() == 0) {
                    Toast.makeText(this, R.string.insert_meta_data, Toast.LENGTH_SHORT).show();
                    obj.result = false;
                    return obj;
                }
                if (selectedTemplate.getItems(doc).size() < 2 && (templateId == 5 || templateId == 7)){
                    Toast.makeText(this, R.string.enter_one_question, Toast.LENGTH_SHORT).show();
                    obj.result = false;
                    return obj;
                }
            }
            else if("saveDraft".equals(callingFunction)) {
                if (selectedTemplate.getItems(doc).size() == 0) {
                    Toast.makeText(this, "Cannot save an empty draft.", Toast.LENGTH_SHORT).show();
                    obj.result = false;
                    return obj;
                }
                if (selectedTemplate.getItems(doc).size() < 2 && (templateId == 5 || templateId == 7)){
                    Toast.makeText(this, R.string.enter_one_question, Toast.LENGTH_SHORT).show();
                    obj.result = false;
                    return obj;
                }
            }
            else if("saveProject".equals(callingFunction)){
                obj.result = false;
                return obj;
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return obj;
    }

    private String generateAPK(String callingFunction) {

        EditText titleEditText = (EditText) findViewById(R.id.template_title);
        EditText authorEditText = (EditText) findViewById(R.id.author_name);

        String title = titleEditText.getText().toString().trim();
        String author = authorEditText.getText().toString().trim();

        if ("".equals(title)) {
            titleEditText.requestFocus();
            titleEditText.setError(getString(R.string.enter_project_title));
            return "not valid";
        }
        if  ("".equals(author)) {
            authorEditText.requestFocus();
            authorEditText.setError(getString(R.string.enter_author_name));
            return "not valid";
        }

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

            if (selectedTemplate.getItems(doc).size() == 0 ) {
                return "empty file";
            } else if ((selectedTemplate.getItems(doc).size() < 2 && (templateId == 5 || templateId == 7))){
                return "insufficient";
            }

            for (Element item : selectedTemplate.getItems(doc)) {
                dataElement.appendChild(item);
            }

            if (oldFileName != null) {
                File tempFile = new File(oldFileName);
                tempFile.delete();
                oldFileName = null;
            }

            String saveFileName = title + " by " + author + ".buildmlearn";
            saveFileName = saveFileName.replaceAll(" ", "-");

            boolean isSaved = FileUtils.saveXmlFile(toolkit.getTempDir(), saveFileName, doc);

            if(isSaved) {
                oldFileName = toolkit.getTempDir() + saveFileName;
                if ("shareAPK".equals(callingFunction) || "saveAPK".equals(callingFunction)){
                    Toast.makeText(this, "APK Generated", Toast.LENGTH_SHORT).show();
                }
                return oldFileName;
            }
            return null;

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void deleteDraft() {
        AlertDialog.Builder builderNew = new AlertDialog.Builder(this);

        builderNew.setTitle(getString(R.string.confirm_action));
        builderNew.setMessage("Cannot save an empty draft.");

        builderNew.setPositiveButton("Discard\ndraft", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                File thisFile = new File(oldFileName);
                thisFile.delete();
                Toast.makeText(getApplicationContext(), "Draft deleted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builderNew.setNegativeButton("Discard\nchanges", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builderNew.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog alert = builderNew.create();
        alert.show();
    }

    private void deleteProject() {
        AlertDialog.Builder builderNew = new AlertDialog.Builder(this);

        builderNew.setTitle(getString(R.string.confirm_action));
        builderNew.setMessage("Cannot save an empty project.");

        builderNew.setPositiveButton("Discard\nproject", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                File thisFile = new File(oldFileName);
                thisFile.delete();
                Toast.makeText(getApplicationContext(), "Project deleted.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builderNew.setNegativeButton("Discard\nchanges", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Changes discarded.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        builderNew.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            }
        });

        AlertDialog alert = builderNew.create();
        alert.show();
    }

}

