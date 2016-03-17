package org.buildmlearn.toolkit.utilities;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.internal.ThemeSingleton;

import org.buildmlearn.toolkit.R;

/**
 * Created by anupam(opticod) on 17/3/16.
 */
public class Selection {
    /**
     * @brief Restores the color scheme when switching from edit mode to normal mode.
     * <p/>
     * Edit mode is triggered, when the list item is long pressed.
     */
    public static void restoreColorScheme(Activity activity, Resources resources) {

        int primaryColor = resources.getColor(R.color.color_primary);
        int primaryColorDark = resources.getColor(R.color.color_primary_dark);
        ((AppCompatActivity) activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().neutralColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().negativeColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(primaryColorDark);
            activity.getWindow().setNavigationBarColor(primaryColor);
        }
        activity.invalidateOptionsMenu();
    }

    /**
     * @brief Changes the color scheme when switching from normal mode to edit mode.
     * <p/>
     * Edit mode is triggered, when the list item is long pressed.
     */
    public static void changeColorScheme(Activity activity, Resources resources) {
        int primaryColor = resources.getColor(R.color.color_primary_dark);
        int primaryColorDark = resources.getColor(R.color.color_selected_dark);
        ((AppCompatActivity) activity).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(primaryColor));
        ThemeSingleton.get().positiveColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().neutralColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().negativeColor = ColorStateList.valueOf(primaryColor);
        ThemeSingleton.get().widgetColor = primaryColor;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(primaryColorDark);
            activity.getWindow().setNavigationBarColor(primaryColor);
        }
        activity.invalidateOptionsMenu();
    }
}
