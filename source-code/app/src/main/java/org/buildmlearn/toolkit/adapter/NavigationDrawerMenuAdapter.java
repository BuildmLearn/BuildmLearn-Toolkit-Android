package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.NavigationAuxMenuItem;
import org.buildmlearn.toolkit.model.NavigationDrawerMenu;
import org.buildmlearn.toolkit.model.NavigationMenuItem;

import java.util.ArrayList;

/**
 * Created by Abhishek on 21/04/15.
 */
public class NavigationDrawerMenuAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private int currentSectionForegroundColor;
    private int currentSectionBackgroundColor;
    private ArrayList<NavigationMenuItem> menus;
    private Context context;

    public NavigationDrawerMenuAdapter(Context context, LayoutInflater inflater, ArrayList<NavigationMenuItem> menus) {
        this.inflater = inflater;
        this.context = context;
        // Select the primary color to tint the current section
        this.menus = menus;
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
        try {
            currentSectionForegroundColor = a.getColor(0, context.getResources().getColor(R.color.color_primary));
        } finally {
            a.recycle();
        }
        currentSectionBackgroundColor = context.getResources().getColor(R.color.translucent_grey);
    }

    @Override
    public int getCount() {
        return menus.size();
    }

    @Override
    public NavigationMenuItem getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NavigationMenuItem menu = getItem(position);
        if (menu.menuType() == NavigationMenuItem.PROJECT_MENU) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_main_menu, parent, false);
            }
            NavigationDrawerMenu projectMenu = (NavigationDrawerMenu) menu;
            TextView tv = (TextView) convertView.findViewById(R.id.section_text);
            SpannableString sectionTitle = new SpannableString(context.getString(projectMenu.getTitleResourceId()));
            Drawable sectionIcon;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sectionIcon = context.getDrawable(projectMenu.getIconResId());
            } else {
                sectionIcon = context.getResources().getDrawable(projectMenu.getIconResId());
            }
            int backgroundColor;
            if (projectMenu.isSelected()) {
                // Special color for the current section
//            sectionTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, sectionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sectionTitle.setSpan(new ForegroundColorSpan(currentSectionForegroundColor), 0, sectionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // We need to mutate the drawable before applying the ColorFilter, or else all the similar drawable instances will be tinted.
                sectionIcon.mutate().setColorFilter(currentSectionForegroundColor, PorterDuff.Mode.SRC_IN);
                backgroundColor = currentSectionBackgroundColor;
            } else {
                backgroundColor = Color.TRANSPARENT;
            }
            tv.setText(sectionTitle);
            tv.setCompoundDrawablesWithIntrinsicBounds(sectionIcon, null, null, null);
            tv.setBackgroundColor(backgroundColor);
        } else if (menu.menuType() == NavigationMenuItem.SECTION) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_section_divider, parent, false);
            }
        } else {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_aux_menu, parent, false);
            }
            NavigationAuxMenuItem projectMenu = (NavigationAuxMenuItem) menu;
            TextView tv = (TextView) convertView.findViewById(R.id.section_text);
            SpannableString sectionTitle = new SpannableString(context.getString(projectMenu.getTitle()));
            Drawable sectionIcon;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sectionIcon = context.getDrawable(projectMenu.getIconResId());
            } else {
                sectionIcon = context.getResources().getDrawable(projectMenu.getIconResId());
            }
            int backgroundColor = Color.TRANSPARENT;
            tv.setText(sectionTitle);
            tv.setCompoundDrawablesWithIntrinsicBounds(sectionIcon, null, null, null);
            tv.setBackgroundColor(backgroundColor);
        }

        return convertView;
    }
}

