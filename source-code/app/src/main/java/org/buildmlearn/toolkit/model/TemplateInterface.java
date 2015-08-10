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
     * @brief Called from Template Editor when template editor is started for creating a new template project.
     * @param context Application context
     * @return BaseAdapter inherited Object
     */
    BaseAdapter newTemplateEditorAdapter(Context context);

    /**
     * @brief This function is used to get the adapter (containing template data) for a existing/current template project.
     * @return BaseAdapter inherited Object
     */
    BaseAdapter currentTemplateEditorAdapter();

    BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data);

    /**
     * @brief Called from TemplateEditor whenever a template is attached to TemplateEditor
     * @return Custom string
     */
    String onAttach();

    /**
     * @brief Used to get the title of the templaye. Mainly used to update ActionBar in Template Editor
     * @return Title as a string
     */
    String getTitle();

    /**
     * @brief Add an item to template data
     * @param activity Current Activity
     */
    void addItem(Activity activity);

    /**
     * @brief Called to edit an item in the template data
     * @param activity Current activity
     * @param position Position of the item in the template data list
     */
    void editItem(Activity activity, int position);

    /**
     * @bried Remove an item form template data list
     * @param position Position of the item to be removed
     */
    void deleteItem(int position);

    
    ArrayList<Element> getItems(Document doc);

    /**
     * @bried Returns a fragment required for the Simulator Activity.
     *
     * Returns a fragment required for the Simulator Activity.
     * **Dev Note: File Path should be used to populate data from actual .buildmlearn file in the Simulator.
     * @param filePathWithName Path of the generated .buildmlearn file
     * @return
     */
    Fragment getSimulatorFragment(String filePathWithName);

    /**
     * @brief Name of the xml file congaing template data in the assets folders in the build apk.
     * @return Asset file name
     */
    String getAssetsFileName();

    /**
     * @brief Folder path in which the apk is stored in the build APK
     * @return Assets folder path
     */
    String getAssetsFilePath();

    /**
     * Path of the apk stored in assets
     * @return Apk file path
     */
    String getApkFilePath();

    /**
     * @bried Called whenever onActivityResult is called in Template Editor. Can be used to perform action related to intent and callbacks.
     * @param context
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    void onActivityResult(Context context, int requestCode, int resultCode, Intent intent);

}
