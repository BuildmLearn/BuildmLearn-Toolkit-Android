package org.buildmlearn.toolkit.model;

import android.app.Activity;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

/**
 * Created by Abhishek on 22/04/15.
 */
public class NavigationAuxMenuItem implements NavigationMenuItem {

    private
    @StringRes
    int title;
    private Class<? extends Activity> activityClass;
    private
    @DrawableRes
    int iconResId;

    public NavigationAuxMenuItem(Class<? extends Activity> activityClass, int iconResId, int title) {
        this.activityClass = activityClass;
        this.iconResId = iconResId;
        this.title = title;
    }

    @Override
    public int menuType() {
        return NavigationMenuItem.AUX_MENU;
    }

    public Class<? extends Activity> getActivityClass() {
        return activityClass;
    }

    public void setActivityClass(Class<? extends Activity> activityClass) {
        this.activityClass = activityClass;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}
