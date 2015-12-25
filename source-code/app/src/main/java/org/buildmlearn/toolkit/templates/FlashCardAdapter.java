package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Flash Card Template Editor data.
 *
 * Created by abhishek on 12/07/15 at 11:56 PM.
 */
public class FlashCardAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<FlashCardModel> mData;

    public FlashCardAdapter(Context context, ArrayList<FlashCardModel> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public FlashCardModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {


        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater;
        mInflater = LayoutInflater.from(mContext);
        Holder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.flash_template_item, parent, false);
            holder = new Holder();

            holder.question = (TextViewPlus) convertView.findViewById(R.id.flash_item_question);
            holder.answer = (TextViewPlus) convertView.findViewById(R.id.flash_item_answer);
            holder.hint = (TextViewPlus) convertView.findViewById(R.id.flash_item_hint);
            holder.image = (ImageView) convertView.findViewById(R.id.flash_item_image);

            holder.delete = (ImageView) convertView.findViewById(R.id.flash_template_delete);
            holder.edit = (ImageView) convertView.findViewById(R.id.flash_item_edit);

        } else {
            holder = (Holder) convertView.getTag();
        }

        final FlashCardModel data = getItem(position);
        holder.answer.setText(data.getAnswer());
        holder.image.setImageBitmap(data.getImageBitmap());
        holder.hint.setText(data.getHint());
        holder.question.setText(data.getQuestion());

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext, "Long press to edit this item", Toast.LENGTH_SHORT).show();
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
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
                        mData.remove(position);
                        notifyDataSetChanged();
                        dialog.dismiss();

                        ((TemplateEditor) mContext).restoreSelectedView();
                    }
                });

                dialog.show();

            }
        });


        convertView.setTag(holder);

        return convertView;
    }

    public class Holder {
        TextView question;
        TextView answer;
        TextView hint;
        ImageView image;
        ImageView edit;
        ImageView delete;

    }
}
