package org.buildmlearn.toolkit.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;

/**
 * Created by Abhishek on 21/04/15.
 */
public class NavigationDrawerMenu implements NavigationMenuItem {

    public static final int FRAGMENT = 0;
    public static final int ACTIVITY = 1;
    public static final int ACTION = 2;


    private Class<? extends Fragment> fragmentClass;
    private
    @StringRes
    int titleResourceId;
    private
    @DrawableRes
    int iconResId;
    private boolean keep;
    private int actionType;
    private boolean isSelected;

    public NavigationDrawerMenu(int actionType, Class<? extends Fragment> fragmentClass, int iconResId, boolean keep, int titleResourceId) {
        this.actionType = actionType;
        this.fragmentClass = fragmentClass;
        this.iconResId = iconResId;
        this.keep = keep;
        this.titleResourceId = titleResourceId;
        this.isSelected = false;
    }

    public Class<? extends Fragment> getFragmentClass() {
        return fragmentClass;
    }

    public void setFragmentClass(Class<? extends Fragment> fragmentClass) {
        this.fragmentClass = fragmentClass;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isKeep() {
        return keep;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public int getTitleResourceId() {
        return titleResourceId;
    }

    public void setTitleResourceId(int titleResourceId) {
        this.titleResourceId = titleResourceId;
    }

    public int getActionType() {
        return actionType;
    }

    public void setActionType(int actionType) {
        this.actionType = actionType;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    @Override
    public int menuType() {
        return NavigationMenuItem.PROJECT_MENU;
    }
}
