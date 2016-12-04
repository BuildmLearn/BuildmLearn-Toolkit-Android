package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Info Template Editor data.
 * <p/>
 * Created by abhishek on 17/06/15 at 9:48 PM.
 */
class InfoAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<InfoModel> data;

    public InfoAdapter(Context mContext, ArrayList<InfoModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public InfoModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        InfoTemplateHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.info_template_item, parent, false);
            holder = new InfoTemplateHolder();
        } else {
            holder = (InfoTemplateHolder) convertView.getTag();
        }

        holder.word = (TextViewPlus) convertView.findViewById(R.id.info_object);
        holder.meaning = (TextViewPlus) convertView.findViewById(R.id.info_description);

        InfoModel info = getItem(position);

        holder.meaning.setText(info.getInfoDescription());
        holder.word.setText(info.getInfoObject());
        convertView.setTag(holder);

        holder.deleteButton = (ImageView) convertView.findViewById(R.id.info_template_delete);
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final InfoModel infoModel = data.get(position);
                data.remove(position);
                notifyDataSetChanged();
                Snackbar.make(v,R.string.snackbar_deleted_message,Snackbar.LENGTH_LONG)
                        .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                data.add(position,infoModel);
                                notifyDataSetChanged();
                                Snackbar.make(v,R.string.snackbar_restored_message,Snackbar.LENGTH_LONG).show();
                            }
                        }).show();




                ((TemplateEditor) mContext).restoreSelectedView();
            }
        });

        holder.editButton = (ImageView) convertView.findViewById(R.id.info_template_edit);
        holder.editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(mContext);
                View dialogView = inflater.inflate(R.layout.info_dialog_add_edit_data, null);
                final AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle(R.string.info_edit_title)
                        .setView(dialogView,
                                mContext.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                                mContext.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                                mContext.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                                mContext.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                        .setPositiveButton(R.string.info_template_add, null)
                        .setNegativeButton(R.string.info_template_cancel, null)
                        .create();
                dialog.show();

                final InfoModel data = getItem(position);

                final EditText word = (EditText) dialogView.findViewById(R.id.info_word);
                final EditText meaning = (EditText) dialogView.findViewById(R.id.info_meaning);
                word.setText(data.getInfoObject());
                meaning.setText(data.getInfoDescription());

                dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (InfoTemplate.validated(mContext, word, meaning)) {
                            String wordText = word.getText().toString();
                            String meaningText = meaning.getText().toString();

                            data.setWord(wordText);
                            data.setInfoDescription(meaningText);

                            notifyDataSetChanged();
                            dialog.dismiss();
                        }

                    }
                });
            }
        });


        return convertView;
    }

    public class InfoTemplateHolder {
        public TextViewPlus word;
        public TextViewPlus meaning;
        public ImageView editButton;
        public ImageView deleteButton;
    }
}
