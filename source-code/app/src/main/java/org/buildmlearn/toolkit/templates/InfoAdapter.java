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
 * @brief Adapter for displaying Info Template Editor data.
 * <p/>
 * Created by abhishek on 17/06/15 at 9:48 PM.
 */
public class InfoAdapter extends BaseAdapter implements TemplateAdapterInterface {

    private Context mContext;
    //Contains all Data
    private ArrayList<InfoModel> data;
    //Contains filtered Data(Search Purpose). In normal case (mDataFiltered == data)
    private ArrayList<InfoModel> mDataFiltered;

    private String searchQuery;


    public InfoAdapter(Context mContext, ArrayList<InfoModel> data) {
        this.mContext = mContext;
        this.data = data;
        mDataFiltered = data;
    }

    @Override
    public int getCount() {
        return mDataFiltered.size();
    }

    @Override
    public InfoModel getItem(int position) {
        return mDataFiltered.get(position);
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

        final InfoModel info = getItem(position);

        holder.meaning.setText(info.getInfoDescription());
        holder.word.setText(info.getInfoObject());
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
                        deleteItem(info);
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
                final MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                        .title(R.string.info_add_new_title)
                        .customView(R.layout.info_dialog_add_edit_data, true)
                        .positiveText(R.string.info_template_add)
                        .negativeText(R.string.info_template_delete)
                        .build();

                final InfoModel data = getItem(position);

                final EditText word = (EditText) dialog.findViewById(R.id.info_word);
                final EditText meaning = (EditText) dialog.findViewById(R.id.info_meaning);
                word.setText(data.getInfoObject());
                meaning.setText(data.getInfoDescription());

                dialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (InfoTemplate.validated(mContext, word, meaning)) {
                            String wordText = word.getText().toString();
                            String meaningText = meaning.getText().toString();

                            data.setWord(wordText);
                            data.setInfoDescription(meaningText);

                            searchFilter();
                            dialog.dismiss();
                        }

                    }
                });

                dialog.show();
            }
        });


        return convertView;
    }


    /**
     * Delete `infoModel` from `data`
     * @param infoModel
     * @return whether operation is done Successfully
     */
    public boolean deleteItem(InfoModel infoModel) {
        int index = data.indexOf(infoModel);
        if (index>=0) {
            data.remove(index);
            searchFilter();
            return true;
        }
        return false;
    }

    /**
     * Add Item to `data`
     * @param infoModel
     */
    public void addItem(InfoModel infoModel) {
        data.add(infoModel);
        searchFilter();
    }

    /**
     * Refresh List according to `searchQuery`
     */
    public void searchFilter() {
        if (searchQuery == null) {
            mDataFiltered = data;
        } else {
            mDataFiltered = new ArrayList<>();
            for (InfoModel model : data) {
                if (model.contains(searchQuery)) {
                    mDataFiltered.add(model);
                }
            }
        }
        notifyDataSetChanged();
    }

    /**
     * Set `searchQuery`
     * @param query
     */
    @Override
    public void searchFilter(String query) {
        if(query.trim().isEmpty())
            searchQuery = null;
        else
            searchQuery = query.trim();
        searchFilter();
    }

    public class InfoTemplateHolder {
        public TextViewPlus word;
        public TextViewPlus meaning;
        public ImageView editButton;
        public ImageView deleteButton;
    }
}
