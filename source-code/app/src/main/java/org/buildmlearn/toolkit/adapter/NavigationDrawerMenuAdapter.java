package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.Section;

/**
 * @brief Adapter used for showing menus in the side panel
 *
 * Created by Abhishek on 21/04/15.
 */
public class NavigationDrawerMenuAdapter extends BaseAdapter {

    private final Section[] sections = Section.values();
    private final LayoutInflater inflater;
    private final Context context;
    private int currentSectionForegroundColor;
    private int currentSectionBackgroundColor;

    public NavigationDrawerMenuAdapter(Context context, LayoutInflater inflater) {
        this.inflater = inflater;
        this.context = context;
        // Select the primary color to tint the current section
        TypedArray a = context.getTheme().obtainStyledAttributes(new int[]{R.attr.colorPrimary});
        try {
            currentSectionForegroundColor = a.getColor(0, ContextCompat.getColor(context, R.color.color_primary));
        } finally {
            a.recycle();
        }
        currentSectionBackgroundColor = ContextCompat.getColor(context, R.color.translucent_grey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return sections.length;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Section getItem(int position) {
        return sections[position];
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Section menu = getItem(position);
        if (menu.getType() == Section.ACTIVITY || menu.getType() == Section.FRAGMENT) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_main_menu, parent, false);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.section_text);
            SpannableString sectionTitle = new SpannableString(context.getString(menu.getTitleResId()));
            Drawable sectionIcon;
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                sectionIcon = context.getDrawable(menu.getIconResId());
            } else {
                sectionIcon = ContextCompat.getDrawable(context, menu.getIconResId());
            }
            int backgroundColor;
            if (menu.isSelected()) {
                // Special color for the current section
//            sectionTitle.setSpan(new StyleSpan(Typeface.BOLD), 0, sectionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                sectionTitle.setSpan(new ForegroundColorSpan(currentSectionForegroundColor), 0, sectionTitle.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                // We need to mutate the drawable before applying the ColorFilter, or else all the similar drawable instances will be tinted.
                assert sectionIcon != null;
                sectionIcon.mutate().setColorFilter(currentSectionForegroundColor, PorterDuff.Mode.SRC_IN);
                backgroundColor = currentSectionBackgroundColor;
            } else {
                backgroundColor = Color.TRANSPARENT;
            }
            tv.setText(sectionTitle);
            tv.setCompoundDrawablesWithIntrinsicBounds(sectionIcon, null, null, null);
            tv.setBackgroundColor(backgroundColor);
        } else if (menu.getType() == Section.SECTION_DIVIDER) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_section_divider, parent, false);
            }
        }
        return convertView;
    }
}

