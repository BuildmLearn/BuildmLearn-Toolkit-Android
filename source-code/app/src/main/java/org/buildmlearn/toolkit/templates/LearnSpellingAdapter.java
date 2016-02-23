package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Learn Spelling Template Editor data.
 *
 * Created by abhishek on 17/06/15 at 9:48 PM.
 */
public class LearnSpellingAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<LearnSpellingModel> data;
    private LearnSpellingTemplate template;

    public LearnSpellingAdapter(Context mContext, ArrayList<LearnSpellingModel> data, LearnSpellingTemplate template) {
        this.mContext = mContext;
        this.data = data;
        this.template = template;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public LearnSpellingModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LearnSpellingHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.info_template_item, parent, false);
            holder = new LearnSpellingHolder();
        } else {
            holder = (LearnSpellingHolder) convertView.getTag();
        }

        holder.word = (TextViewPlus) convertView.findViewById(R.id.info_object);
        holder.meaning = (TextViewPlus) convertView.findViewById(R.id.info_description);

        LearnSpellingModel info = getItem(position);

        holder.meaning.setText(info.getMeaning());
        holder.word.setText(info.getWord());
        convertView.setTag(holder);

        holder.deleteButton = (ImageView) convertView.findViewById(R.id.info_template_delete);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title(R.string.info_template_delete)
                        .content(R.string.info_delete_item_content)
                        .positiveText(R.string.dialog_yes)
                        .negativeText(R.string.dialog_no)
                        .build();

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        template.deleteItem(position);
                        dialog.dismiss();

                        ((TemplateEditor) mContext).restoreSelectedView();
                    }
                });

                dialog.show();

            }
        });

        holder.editButton = (ImageView) convertView.findViewById(R.id.info_template_edit);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                template.editItem((TemplateEditor) mContext, position);
            }
        });


        return convertView;
    }

    public class LearnSpellingHolder {
        public TextViewPlus word;
        public TextViewPlus meaning;
        public ImageView editButton;
        public ImageView deleteButton;
    }
}
