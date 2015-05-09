package org.buildmlearn.toolkit.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.buildmlearn.toolkit.R;

/**
 * Created by Abhishek on 08-05-2015.
 */
public enum Section {

    HOME(null, R.string.menu_home, R.drawable.menu_home, Section.FRAGMENT),
    OPEN_PROJECT(null, R.string.menu_load_project, R.drawable.menu_open, Section.FRAGMENT),
    STORE(null, R.string.menu_buildmlearn_store, R.drawable.menu_store, Section.FRAGMENT),
    UPLOAD(null, R.string.menu_my_uploads, R.drawable.menu_upload, Section.FRAGMENT),
    DRAFTS(null, R.string.menu_drafts, R.drawable.menu_drafts, Section.FRAGMENT),
    DIVIDER(),
    SETTINGS(null, R.string.menu_settings, R.drawable.menu_settings, Section.ACTIVITY),
    HOW_TO(null, R.string.menu_how_to, R.drawable.menu_how_to, Section.ACTIVITY),
    ABOUT_US(null, R.string.menu_about_us, R.drawable.menu_info, Section.ACTIVITY),
    PRIVACY_POLICY(null, R.string.menu_privacy_policy, R.drawable.menu_privacy_policy, Section.ACTIVITY);



    public final static int ACTIVITY = 0;
    public final static int FRAGMENT = 1;
    public final static int SECTION_DIVIDER = 2;

    private final Class<?> viewClass;

    private final int titleResId;
    private final int iconResId;
    private final int type;
    private boolean isSelected;


    Section(Class<?> fragmentClass, @StringRes int titleResId,
            @DrawableRes int iconResId, int type) {
        this.viewClass = fragmentClass;
        this.titleResId = titleResId;
        this.iconResId = iconResId;
        this.type = type;
        isSelected = false;
    }

    Section() {
        this(null, 0, 0, SECTION_DIVIDER);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public Class<?> getFragmentClassName() {
        return viewClass;
    }

    public int getTitleResId() {
        return titleResId;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int getType() {
        return type;
    }
}
