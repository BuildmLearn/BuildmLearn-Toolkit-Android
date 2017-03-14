package org.buildmlearn.toolkit.holder;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.adapter.TemplateAdapter;

/**
 * Created by Ankur Mittal on 12-03-2017.
 *
 * Header Holder class for template adapters, except for template id 5 & 7.
 * Contains Author EditText and Title EditText.
 *
 */
public class HeaderHolder extends RecyclerView.ViewHolder {
    final public EditText authorEditText, titleEditText;

    public HeaderHolder(View itemView, Context mContext, int templateId) {
        super(itemView);
        authorEditText = (EditText) itemView.findViewById(R.id.author_name);
        titleEditText = (EditText) itemView.findViewById(R.id.template_title);

        LinearLayout headerLayout = (LinearLayout) itemView.findViewById(R.id.header_layout);
        headerLayout.setBackgroundColor(TemplateAdapter.ListColor.values()[templateId].getColor());
        headerLayout.invalidate();
        ((TemplateEditor)mContext).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(TemplateAdapter.ListColor.values()[templateId].getColor()));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ((TemplateEditor)mContext).getWindow().setStatusBarColor(TemplateAdapter.ListDarkColor.values()[templateId].getColor());
            ((TemplateEditor)mContext).getWindow().setNavigationBarColor(TemplateAdapter.ListColor.values()[templateId].getColor());
        }
    }

}
