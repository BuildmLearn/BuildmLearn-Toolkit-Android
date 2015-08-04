package org.buildmlearn.toolkit.model;

import android.app.Activity;
import android.app.Fragment;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TutorialActivity;
import org.buildmlearn.toolkit.fragment.HomeFragment;
import org.buildmlearn.toolkit.fragment.LoadProjectFragment;
import org.buildmlearn.toolkit.fragment.SettingsFragment;

/**
 * Created by Abhishek on 08-05-2015.
 */
public enum Section {

    HOME(HomeFragment.class, R.string.menu_home, R.drawable.menu_home, false),
    OPEN_PROJECT(LoadProjectFragment.class, R.string.menu_load_project, R.drawable.menu_open, false),
    //    UPLOAD(TestFragment.class, R.string.menu_my_uploads, R.drawable.menu_upload, false),
//    DRAFTS(TestFragment.class, R.string.menu_drafts, R.drawable.menu_drafts, false),
    DIVIDER(),
    SETTINGS(SettingsFragment.class, R.string.menu_settings, R.drawable.menu_settings, false),
    HOW_TO(TutorialActivity.class, R.string.menu_how_to, R.drawable.menu_how_to);
//    ABOUT_US(TestActivity.class, R.string.menu_about_us, R.drawable.menu_info),
//    PRIVACY_POLICY(TestActivity.class, R.string.menu_privacy_policy, R.drawable.menu_privacy_policy);


    public final static int ACTIVITY = 0;
    public final static int FRAGMENT = 1;
    public final static int SECTION_DIVIDER = 2;

    private final String viewName;
    private final int titleResId;
    private final int iconResId;
    private final int type;
    private final boolean keep;
    private boolean isSelected;


    Section(Class<? extends Fragment> fragmentClass, @StringRes int titleResId,
            @DrawableRes int iconResId, boolean keep) {
        this.viewName = fragmentClass.getName();
        this.titleResId = titleResId;
        this.iconResId = iconResId;
        this.type = FRAGMENT;
        this.keep = keep;
        this.isSelected = false;
    }

    Section(Class<? extends Activity> activityClass, @StringRes int titleResId,
            @DrawableRes int iconResId) {
        this.viewName = activityClass.getName();
        this.titleResId = titleResId;
        this.iconResId = iconResId;
        this.type = ACTIVITY;
        this.keep = false;
        this.isSelected = false;
    }

    Section() {
        this.viewName = null;
        this.titleResId = 0;
        this.iconResId = 0;
        this.type = SECTION_DIVIDER;
        this.keep = false;
        this.isSelected = false;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        if (type == FRAGMENT) {
            this.isSelected = isSelected;
        }
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

    public boolean isKeep() {
        return keep;
    }

    public String getViewName() {
        return viewName;
    }
}
