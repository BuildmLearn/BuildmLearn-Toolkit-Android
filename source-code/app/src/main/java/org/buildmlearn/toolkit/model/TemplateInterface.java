package org.buildmlearn.toolkit.model;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @brief Interface containing methods for a implementing a template.
 *
 * Each template must implement this interface. For using the template, a new enum in Template Enum is required.
 *
 * Created by abhishek on 27/5/15.
 */
public interface TemplateInterface extends Serializable {

    /**
     * @param context Application context
     * @return BaseAdapter inherited Object
     * @brief Called from Template Editor when template editor is started for creating a new template project.
     */
    BaseAdapter newTemplateEditorAdapter(Context context);

    /**
     * @return BaseAdapter inherited Object
     * @brief This function is used to get the adapter (containing template data) for a existing/current template project.
     */
    BaseAdapter currentTemplateEditorAdapter();

    BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data);

    /**
     * @return Custom string
     * @brief Called from TemplateEditor whenever a template is attached to TemplateEditor
     */
    String onAttach();

    /**
     * @brief Set templateId,that can be used to get Info about current template from enum Template
     * @param templateId
     */
    void setTemplateId(int templateId);
    /**
     * @return Title as a string
     * @brief Used to get the title of the templaye. Mainly used to update ActionBar in Template Editor
     */
    String getTitle();

    /**
     * @param activity Current Activity
     * @brief Add an item to template data
     */
    void addItem(Activity activity);

    /**
     * @param activity Current activity
     * @param position Position of the item in the template data list
     * @brief Called to edit an item in the template data
     */
    void editItem(Activity activity, int position);

    /**
     * @param position Position of the item to be removed
     * @brief Remove an item form template data list
     */
    void deleteItem(int position);


    ArrayList<Element> getItems(Document doc);

    /**
     * @param filePathWithName Path of the generated .buildmlearn file
     * @return
     * @brief Returns a fragment required for the Simulator Activity.
     *
     * Returns a fragment required for the Simulator Activity.
     * **Dev Note: File Path should be used to populate data from actual .buildmlearn file in the Simulator.
     */
    Fragment getSimulatorFragment(String filePathWithName);

    /**
     * @param context For obtaining String from StringRes
     * @return Asset file name
     * @brief Name of the xml file congaing template data in the assets folders in the build apk.
     */
     String getAssetsFileName(Context context);

    /**
     * @return Assets folder path
     * @brief Folder path in which the apk is stored in the build APK
     */
    String getAssetsFilePath();

    /**
     * Path of the apk stored in assets
     *
     * @return Apk file path
     */
    String getApkFilePath();

    /**
     * @param context
     * @param requestCode
     * @param resultCode
     * @param intent
     * @brief Called whenever onActivityResult is called in Template Editor. Can be used to perform action related to intent and callbacks.
     */
    void onActivityResult(Context context, int requestCode, int resultCode, Intent intent);

}
