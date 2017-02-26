package org.buildmlearn.toolkit.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperAdapter;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.SimpleItemTouchHelperCallback;
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

public class TemplateEditor extends AppCompatActivity implements TemplateEditorInterface {

    private static final String TAG = "TEMPLATE EDITOR";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE_RESULT = 100;
    private final Handler handlerToast = new Handler() {
        public void handleMessage(Message message) {
            if (message.arg1 == -1) {
                Toast.makeText(TemplateEditor.this, "Build unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    };
    private RecyclerView templateEdtiorList;
    private ListView templateMetaList;
    private int templateId;
    private Template template;
    private TemplateInterface selectedTemplate;
    private int selectedPosition = -1;
    private boolean showTemplateSelectedMenu;
    private View selectedView;
    private EditText titleEditText;
    private ToolkitApplication toolkit;
    private String oldFileName;
    private ProgressDialog mApkGenerationDialog;
    private TemplateEditorInterface templateEditorInterface;
    private String authorName, title;
    private BaseAdapter metaAdapter;


    public void openBottomSheet(View v) {

        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_view, null);

        TextView txtSaveApk = (TextView) view.findViewById(R.id.txt_save_apk);
        TextView txtSaveProject = (TextView) view.findViewById(R.id.txt_save_project);
        TextView txtShareApk = (TextView) view.findViewById(R.id.txt_share_apk);
        final TextView txtShareProject = (TextView) view.findViewById(R.id.txt_share_project);

        final Dialog mBottomSheetDialog = new Dialog(TemplateEditor.this,
                R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();


        //save project
        txtSaveProject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveProject();
                mBottomSheetDialog.dismiss();
            }
        });

        //share project
        txtShareProject.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                shareProject();
                mBottomSheetDialog.dismiss();
            }
        });

        txtShareApk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                shareApk();
                mBottomSheetDialog.dismiss();
            }
        });

        txtSaveApk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveApk();
                mBottomSheetDialog.dismiss();
            }
        });
    }

    private void saveApk() {
        String savedFilePath;
        savedFilePath = saveProject();
        if (savedFilePath == null || savedFilePath.length() == 0) {
            return;
        }
        String keyPassword = getString(R.string.key_password);
        String aliasName = getString(R.string.alias_name);
        String aliaspassword = getString(R.string.alias_password);
        KeyStoreDetails keyStoreDetails = new KeyStoreDetails(keyPassword, aliasName, aliaspassword);
        if (saveProject().equals("File already exists")) {
            return;
        }
        SignerThread signer = new SignerThread(getApplicationContext(), selectedTemplate.getApkFilePath(), saveProject(), keyStoreDetails, selectedTemplate.getAssetsFilePath(), selectedTemplate.getAssetsFileName(TemplateEditor.this));

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
                                .setMessage("Apk file saved at " + path + "\nFind it any time under Saved APKs\nInstall now?")
                                .setNegativeButton("later", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).setPositiveButton("install", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent();
                                        intent.setAction(Intent.ACTION_VIEW);
                                        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                                        startActivity(intent);
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

    private void shareApk() {
        String savedFilePath;
        savedFilePath = saveProject();
        if (savedFilePath == null || savedFilePath.length() == 0) {
            return;
        }
        String keyPassword = getString(R.string.key_password);
        String aliasName = getString(R.string.alias_name);
        String aliaspassword = getString(R.string.alias_password);
        KeyStoreDetails keyStoreDetails = new KeyStoreDetails(keyPassword, aliasName, aliaspassword);
        SignerThread signer = new SignerThread(getApplicationContext(), selectedTemplate.getApkFilePath(), saveProject(), keyStoreDetails, selectedTemplate.getAssetsFilePath(), selectedTemplate.getAssetsFileName(TemplateEditor.this));

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
        String savedFilePath;
        savedFilePath = saveProject();
        if (savedFilePath == null || savedFilePath.length() == 0) {
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
        templateEditorInterface = this;
        KeyboardHelper.hideKeyboard(this, findViewById(R.id.toolbar));
        KeyboardHelper.hideKeyboard(this, findViewById(R.id.template_editor_recyclerView));
        KeyboardHelper.hideKeyboard(this, findViewById(R.id.empty));
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
    private void populateMetaView(final BaseAdapter adapter, ListView templateMetaList) {
        this.templateMetaList = templateMetaList;
        if (templateMetaList == null) {
            View view = templateEdtiorList.getChildAt(0);
            templateMetaList = (ListView) view.findViewById(R.id.template_meta_listview);
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
                    restoreToolbarColorScheme();
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
    private void populateListView(final Object adapter) {
        if (templateEdtiorList == null) {
            initializeRecyclerView((ItemTouchHelperAdapter) adapter);
        }
        setAdapter(adapter);
    }

    private void initializeRecyclerView(ItemTouchHelperAdapter adapter) {
        templateEdtiorList = (RecyclerView) findViewById(R.id.template_editor_recyclerView);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        templateEdtiorList.setLayoutManager(mLayoutManager);
        templateEdtiorList.setItemAnimator(new DefaultItemAnimator());
        if (adapter == null)
            return;
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        ItemTouchHelper mItemTouchHelper;
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(templateEdtiorList);
    }

    private boolean changeItemSchema(int position, View view) {
        if (view == null)
            return false;
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
            restoreToolbarColorScheme();
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

    /**
     * @brief Initialization function for setting up action bar
     */
    private void setUpActionBar() {
        ActionBar actionBar = getSupportActionBar();
        initializeRecyclerView(null);
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
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            setAuthorName(preferences.getString(this.getString(R.string.key_user_name), ""));
            populateListView(selectedTemplate.newTemplateEditorAdapter(this, templateEditorInterface));
            if (templateId == 5 || templateId == 7) {
                setMetaAdapter(selectedTemplate.newMetaEditorAdapter(this));
            }
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
    private void restoreTemplateEditor(Bundle savedInstanceState) {
        selectedTemplate = (TemplateInterface) savedInstanceState.getSerializable(Constants.TEMPLATE_OBJECT);
        oldFileName = savedInstanceState.getString(Constants.PROJECT_FILE_PATH);
        templateId = savedInstanceState.getInt(Constants.TEMPLATE_ID);
        Template[] templates = Template.values();
        template = templates[templateId];
        if (selectedTemplate == null) {
            finish();
        } else {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
            setAuthorName(preferences.getString(this.getString(R.string.key_user_name), ""));
            populateListView(selectedTemplate.currentTemplateEditorAdapter());
            if (templateId == 5 || templateId == 7) {
                setMetaAdapter(selectedTemplate.currentMetaEditorAdapter());
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
        boolean isRearrangeOccurs;
        switch (id) {
            case R.id.action_delete:
                final int restorePosition = selectedPosition;
                final Object object = selectedTemplate.deleteItem(TemplateEditor.this, selectedPosition);
                selectedPosition = -1;
                restoreSelectedView();
                Snackbar.make(findViewById(R.id.relative_layout),
                        R.string.snackbar_deleted_message, Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                selectedTemplate.restoreItem(TemplateEditor.this, restorePosition, object);
                                Snackbar.make(v, R.string.snackbar_restored_message, Snackbar.LENGTH_LONG).show();
                            }
                        }).show();
                restoreColorSchema();
                break;
            case R.id.action_edit:
                selectedTemplate.editItem(this, selectedPosition);
                restoreColorSchema();
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
            case R.id.action_move_up:
                isRearrangeOccurs = selectedTemplate.moveUp(this, selectedPosition);
                if (isRearrangeOccurs) {
                    restoreSelectedView();
                    selectedView = null;
                    View view = templateEdtiorList.getChildAt(selectedPosition);
                    changeItemSchema(selectedPosition, view);
                } else {
                    Toast.makeText(this, R.string.already_at_top, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_move_down:
                isRearrangeOccurs = selectedTemplate.moveDown(this, selectedPosition);
                if (isRearrangeOccurs) {
                    restoreSelectedView();
                    selectedView = null;
                    View view;
                    view = templateEdtiorList.getChildAt(selectedPosition + 2);
                    changeItemSchema(selectedPosition + 2, view);
                } else {
                    Toast.makeText(this, R.string.already_at_bottom, Toast.LENGTH_SHORT).show();
                }
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

        restoreToolbarColorScheme();
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
    private void restoreToolbarColorScheme() {
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
    private String saveProject() {
        try {
            View view = templateEdtiorList.getChildAt(0);
            EditText authorEditText = (EditText) view.findViewById(R.id.author_name);
            titleEditText = (EditText) view.findViewById(R.id.template_title);
            String author = authorEditText.getText().toString();
            String title = titleEditText.getText().toString();
            if ("".equals(author)) {
                assert authorEditText != null;
                authorEditText.setError("Author name is required");
            } else if ("".equals(title)) {
                assert titleEditText != null;
                titleEditText.setError(getResources().getString(R.string.title_error));
            } else if (!Character.isLetterOrDigit(author.charAt(0))) {
                assert authorEditText != null;
                authorEditText.setError(getResources().getString(R.string.valid_msg));
            } else if (!Character.isLetterOrDigit(title.charAt(0))) {
                assert titleEditText != null;
                titleEditText.setError(getString(R.string.title_valid));
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
                    if (selectedTemplate.getItems(doc).size() == 0 || (selectedTemplate.getItems(doc).size() < 2 && (templateId == 5 || templateId == 7))) {
                        Toast.makeText(this, "Unable to perform action: No Data", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                    if (selectedTemplate.getItems(doc).get(0).getTagName().equals("item") && (templateId == 5 || templateId == 7)) {
                        Toast.makeText(this, "Unable to perform action: Add Meta Details", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                    if (templateId == 7 && selectedTemplate.getItems(doc).size() == 2) {
                        Toast.makeText(this, "Please enter atleast 2 items", Toast.LENGTH_SHORT).show();
                        return null;
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

                    boolean isSaved = FileUtils.saveXmlFile(toolkit.getSavedDir(), saveFileName, doc);
                    if (isSaved) {
                        oldFileName = toolkit.getSavedDir() + saveFileName;
                        Toast.makeText(this, "Project Successfully Saved!", Toast.LENGTH_SHORT).show();
                        return oldFileName;
                    } else {
                        titleEditText.setError("File Already exists");
                        return "File already exists";
                    }
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();

                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        if (selectedView == null) {
            super.onBackPressed();
            if (saveDraft() != null)
                Toast.makeText(getApplicationContext(), "Saved in Draft!", Toast.LENGTH_SHORT).show();
        } else {
            restoreColorSchema();
        }

    }

    /**
     * Converts the current TemplateInterface object into a xml file. Xml file is saved in DRAFT
     * Directory (defined in constants). File name is of the format: draft<0-X>.buildmlearn
     *
     * @return Absolute path of the saved file. Null if there is some error.
     * @brief Saves the current project into a .buildmlearn file.
     */
    private String saveDraft() {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        try {
            View view = templateEdtiorList.getChildAt(0);
            EditText etAuthorName = ((EditText) view.findViewById(R.id.author_name));
            String author = etAuthorName.getText().toString();
            EditText etTemplateTitle = ((EditText) findViewById(R.id.template_title));
            String title = etTemplateTitle.getText().toString();

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
            if (selectedTemplate.getItems(doc).get(0).getTagName().equals("item") && (templateId == 5 || templateId == 7)) {
                Toast.makeText(this, "Unable to perform action: No Meta Details", Toast.LENGTH_SHORT).show();
                return null;
            }
            if (templateId == 7 && selectedTemplate.getItems(doc).size() == 2) {
                Toast.makeText(this, "Please enter atleast 2 items", Toast.LENGTH_SHORT).show();
                return null;
            }
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

            //Create temporary File, if that file content matches with OldContent
            // Then Don't make Draft
            File tempFile = new File(toolkit.getDraftDir(), ".temp");
            File oldFile = null;
            if (oldFileName != null)
                oldFile = new File(oldFileName);

            FileUtils.saveXmlFile(toolkit.getDraftDir(), ".temp", doc);
            if (oldFile == null || !FileUtils.equalContent(tempFile, oldFile)) {
                tempFile.renameTo(probableFile);
                return toolkit.getDraftDir() + probableFileName;
            } else {
                File newFile = new File(toolkit.getDraftDir(), ".temp");
                newFile.delete();
            }
            return null;

        } catch (ParserConfigurationException | NullPointerException e) {
            e.printStackTrace();
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
        String message = saveProject();
        if (message == null || message.equals("")) {
            Toast.makeText(this, "Build unsuccessful", Toast.LENGTH_SHORT).show();
            return;
        } else if ("File already exists".equals(message)) {
            titleEditText.setError("Template Already exists");
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
            updateHeaderDetails(name, title);
            populateListView(selectedTemplate.loadProjectTemplateEditor(this, items, templateEditorInterface));
            if (templateId == 5 || templateId == 7) {
                setMetaAdapter(selectedTemplate.loadProjectMetaEditor(this, doc));
            }
            File draftDir = new File(toolkit.getDraftDir());
            if (fXmlFile.getParentFile().compareTo(draftDir) == 0) {
                //If Draft File
                fXmlFile.delete();
            }
            setUpActionBar();


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
        setAuthorName(name);
        setTitle(title);
    }

    /**
     * @param adapter
     * @brief Sets the adapter to the ListView
     */
    private void setAdapter(Object adapter) {
        templateEdtiorList.setAdapter((RecyclerView.Adapter) adapter);
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

    @Override
    public boolean onItemLongClick(int position, View view) {
        return !(position == 0 || view == null) && changeItemSchema(position, view);
    }

    @Override
    public String getProjectTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getAuthorName() {
        return authorName;
    }

    @Override
    public void setProjectTitle(String title) {
        this.title=title;
    }

    @Override
    public void restoreColorSchema() {
        restoreSelectedView();
        restoreToolbarColorScheme();
        selectedView = null;
        selectedPosition = -1;
    }

    @Override
    public void populateMetaList(ListView listView) {
        if (templateId == 5 || templateId == 7) {
            populateMetaView(getMetaAdapter(), listView);
        }
    }
    @Override
    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public BaseAdapter getMetaAdapter() {
        return metaAdapter;
    }

    public void setMetaAdapter(BaseAdapter metaAdapter) {
        this.metaAdapter = metaAdapter;
    }
}

