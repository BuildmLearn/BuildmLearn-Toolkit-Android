package org.buildmlearn.toolkit.activity;

import android.view.View;
import android.widget.ListView;

/**
 * Created by vishwesh3 on 7/1/17.
 */
public interface TemplateEditorInterface {
    /**
     * Called on long click of item
     *
     * @param position on which long click is applied
     * @param view     view of the item
     * @return onLongClick
     */
    boolean onItemLongClick(int position, View view);

    /**
     * Get title of the project
     * If project is restored from Drafts or Saved
     *
     * @return project title
     */
    String getProjectTitle();

    /**
     * Get author name
     * If project is restored from Drafts or Saved
     *
     * @return author name
     */
    String getAuthorName();

    /**
     * Updates project title in the activity
     *
     * @param title updated title of the project
     */
    void setProjectTitle(String title);

    /**
     * Updates author name in the activity
     *
     * @param title updated author nam of the project
     * @param name
     */
    void setAuthorName(String name);

    /**
     * On rearrange complete restore previous view
     */
    void restoreColorSchema();

    /**
     * To populate listview of meta items
     *
     * @param listView to be populated
     */
    void populateMetaList(ListView listView);
}
