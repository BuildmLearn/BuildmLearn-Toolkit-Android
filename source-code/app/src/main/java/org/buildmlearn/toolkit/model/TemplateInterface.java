package org.buildmlearn.toolkit.model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @brief Interface containing methods for a implementing a template.
 * <p/>
 * Each template must implement this interface. For using the template, a new enum in Template Enum is required.
 * <p/>
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
     * @param context Application context
     * @return BaseAdapter inherited Object
     * @brief Called from Template Editor when template editor is started for creating a new meta details of template project.
     */
    BaseAdapter newMetaEditorAdapter(Context context);

    /**
     * @return BaseAdapter inherited Object
     * @brief This function is used to get the adapter (containing template data) for a existing/current template project.
     */
    BaseAdapter currentTemplateEditorAdapter();

    /**
     * @return BaseAdapter inherited Object
     * @brief This function is used to get the meta adapter (containing template meta details) for a existing/current template project.
     */
    BaseAdapter currentMetaEditorAdapter();

    BaseAdapter loadProjectMetaEditor(Context context, Document doc);

    BaseAdapter loadProjectTemplateEditor(Context context, ArrayList<Element> data);

    /**
     * @param templateId
     * @brief Set templateId,that can be used to get Info about current template from enum Template
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
     * @param activity Current Activity
     * @brief Add MetaData to template data
     */
    void addMetaData(Activity activity);

    /**
     * @param activity Current activity
     * @param position Position of the item in the template data list
     * @brief Called to edit an item in the template data
     */
    void editItem(Activity activity, int position);

    /**
     * @param activity Current Activity
     * @param position Position of the item to be removed
     * @brief Remove an item form template data list
     */
    Object deleteItem(Activity activity, int position);

    void restoreItem(Activity activity, int position,Object object);


    ArrayList<Element> getItems(Document doc);

    /**
     * @param filePathWithName Path of the generated .buildmlearn file
     * @return
     * @brief Returns a fragment required for the Simulator Activity.
     * <p/>
     * Returns a fragment required for the Simulator Activity.
     * **Dev Note: File Path should be used to populate data from actual .buildmlearn file in the Simulator.
     */
    android.support.v4.app.Fragment getSimulatorFragment(String filePathWithName);

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
