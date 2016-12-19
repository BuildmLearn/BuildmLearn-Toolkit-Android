package org.buildmlearn.toolkit.templates;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.views.TextViewPlus;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Meta Details of Match The Following Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 16/7/16.
 */
class MatchMetaAdapter extends BaseAdapter {

    private final Context mContext;
    private final ArrayList<MatchMetaModel> data;
    transient private MatchAdapter adapter;
    private int expandedPostion = -1;


    public MatchMetaAdapter(Context mContext, ArrayList<MatchMetaModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public MatchMetaModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final MatchMetaHolder holder;
        final int parPosition = position;

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.match_meta_item, parent, false);
            holder = new MatchMetaHolder();
            convertView.setTag(holder);
        } else {
            holder = (MatchMetaHolder) convertView.getTag();
        }

        holder.title = (TextViewPlus) convertView.findViewById(R.id.meta_title);
        holder.first_list_title = (TextViewPlus) convertView.findViewById(R.id.first_list_title);
        holder.second_list_title = (TextViewPlus) convertView.findViewById(R.id.second_list_title);
        holder.emptyView = (TextViewPlus) convertView.findViewById(R.id.empty_view_text);
        holder.questionIcon = (ImageView) convertView.findViewById(R.id.question_icon);
        holder.quizOptionsBox = (LinearLayout) convertView.findViewById(R.id.quiz_options_box);
        holder.addMatchData = (Button) convertView.findViewById(R.id.button_add_match_data);
        holder.delMatchData = (Button) convertView.findViewById(R.id.delete_match_data);
        holder.editMatchData = (Button) convertView.findViewById(R.id.edit_match_data);
        holder.list_view_match_models = (ListView) convertView.findViewById(R.id.list_view_match_data);
        adapter = new MatchAdapter(mContext,data.get(position).getMatchModels());
        holder.list_view_match_models.setAdapter(adapter);
        holder.list_view_match_models.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                    default:
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });
        holder.list_view_match_models.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(parPosition,position,holder);
                setAddorDel(parPosition,holder);
                return true;
            }
        });

        if (getItem(position).isSelected()) {
            holder.questionIcon.setImageResource(R.drawable.collapse);
            holder.quizOptionsBox.setVisibility(View.VISIBLE);
        } else {
            holder.questionIcon.setImageResource(R.drawable.expand);
            holder.quizOptionsBox.setVisibility(View.GONE);
        }

        holder.questionIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandedPostion >= 0 && expandedPostion != position && getItem(expandedPostion) != null) {
                    getItem(expandedPostion).setIsSelected(false);
                    holder.list_view_match_models.getParent().requestDisallowInterceptTouchEvent(false);
                }
                if (getItem(position).isSelected()) {
                    getItem(position).setIsSelected(false);
                    expandedPostion = -1;
                    holder.list_view_match_models.getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getItem(position).setIsSelected(true);
                    expandedPostion = position;
                    adapter.selectItem(-1);
                }
                notifyDataSetChanged();
            }
        });

        holder.addMatchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItem(parPosition, holder);
            }
        });
        holder.delMatchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelectedPosition() != -1) {
                    deleteItems(parPosition, adapter.getSelectedPosition(), holder);
                    adapter.selectItem(-1);
                    adapter.notifyDataSetChanged();
                    holder.list_view_match_models.invalidateViews();
                }
            }
        });
        holder.editMatchData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelectedPosition() != -1) {
                    editItem(parPosition, adapter.getSelectedPosition());
                    adapter.selectItem(-1);
                    adapter.notifyDataSetChanged();
                    holder.list_view_match_models.invalidateViews();
                }
            }
        });

        MatchMetaModel meta = getItem(position);

        holder.title.setText(Html.fromHtml("<b>" + "Title :  " + "</b> " + meta.getTitle()));
        holder.first_list_title.setText(Html.fromHtml("<b>" + "First List Title :  " + "</b> " + meta.getFirstListTitle()));
        holder.second_list_title.setText(Html.fromHtml("<b>" + "Second List Title :  " + "</b> " + meta.getSecondListTitle()));
        setEmptyView(position,holder);
        setAddorDel(parPosition,holder);
        return convertView;
    }

    public class MatchMetaHolder {
        public TextViewPlus title;
        public TextViewPlus first_list_title;
        public TextViewPlus second_list_title;
        public TextViewPlus emptyView;
        public ImageView questionIcon;
        public LinearLayout quizOptionsBox;
        public Button addMatchData;
        public Button delMatchData;
        public Button editMatchData;
        public ListView list_view_match_models;
    }

    private void addItem(final int position,final MatchMetaHolder holder) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.match_dialog_add_edit, null);
        Activity activity = (Activity) mContext;
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.match_dialog_add_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.quiz_add, null)
                .setNegativeButton(R.string.quiz_cancel, null)
                .create();
        dialog.show();

        final EditText first_list_item = (EditText) dialogView.findViewById(R.id.first_list_item);
        final EditText second_list_item = (EditText) dialogView.findViewById(R.id.second_list_item);

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(mContext, first_list_item, second_list_item)) {
                    String first_list_itemText = first_list_item.getText().toString().trim();
                    String second_list_itemText = second_list_item.getText().toString().trim();

                    MatchModel temp = new MatchModel(first_list_itemText, second_list_itemText);
                    data.get(position).addMatchModel(temp);
                    adapter.notifyDataSetChanged();
                    setEmptyView(position,holder);
                    dialog.dismiss();
                }

            }
        });
    }

    public void deleteItems(final int parPosition, final int position, final MatchMetaHolder holder) {
        adapter.remove(position);
        adapter.selectItem(-1);
        holder.list_view_match_models.invalidateViews();
        setAddorDel(parPosition,holder);
    }

    private void selectItem(final int parPosition, final int position, final MatchMetaHolder holder) {
        adapter.selectItem(position);
        holder.list_view_match_models.invalidateViews();
        setAddorDel(parPosition,holder);
    }

    private void editItem(final int parPosition, final int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View dialogView = inflater.inflate(R.layout.match_dialog_add_edit, null);
        Activity activity = (Activity) mContext;
        final AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.match_dialog_add_title)
                .setView(dialogView,
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_left),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_top),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_right),
                        activity.getResources().getDimensionPixelSize(R.dimen.spacing_bottom))
                .setPositiveButton(R.string.quiz_ok, null)
                .setNegativeButton(R.string.quiz_cancel, null)
                .create();
        dialog.show();

        final EditText first_list_item = (EditText) dialogView.findViewById(R.id.first_list_item);
        final EditText second_list_item = (EditText) dialogView.findViewById(R.id.second_list_item);

        first_list_item.setText(data.get(parPosition).getMatchModels().get(position).getMatchA());
        second_list_item.setText(data.get(parPosition).getMatchModels().get(position).getMatchB());

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (validated(mContext, first_list_item, second_list_item)) {
                    String first_list_itemText = first_list_item.getText().toString().trim();
                    String second_list_itemText = second_list_item.getText().toString().trim();

                    adapter.getItem(position).setMatchA(first_list_itemText);
                    adapter.getItem(position).setMatchB(second_list_itemText);
                    adapter.selectItem(-1);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }
        });
    }

    private void setAddorDel(final int parPosition,final MatchMetaHolder holder) {
        if(adapter.getSelectedPosition() == -1) {
            holder.addMatchData.setVisibility(View.VISIBLE);
            holder.delMatchData.setVisibility(View.GONE);
            holder.editMatchData.setVisibility(View.GONE);
        } else {
            holder.addMatchData.setVisibility(View.GONE);
            holder.delMatchData.setVisibility(View.VISIBLE);
            holder.editMatchData.setVisibility(View.VISIBLE);
        }
        setEmptyView(parPosition,holder);
    }

    private void setEmptyView(final int position,final MatchMetaHolder holder) {
        if(data.get(position).getMatchModels().size() > 0) {
            holder.emptyView.setVisibility(View.GONE);
        } else {
            holder.emptyView.setVisibility(View.VISIBLE);
        }

    }

    private static boolean validated(Context context, EditText first_list_title, EditText second_list_title) {
        if (first_list_title == null || second_list_title == null) {
            return false;
        }

        String first_list_titleText = first_list_title.getText().toString().trim();
        String second_list_titleText = second_list_title.getText().toString().trim();

        if (("").equals(first_list_titleText)) {
            first_list_title.hasFocus();
            first_list_title.setError(context.getString(R.string.match_first_list_title));
            return false;
        } else if (("").equals(second_list_titleText)) {
            second_list_title.hasFocus();
            second_list_title.setError(context.getString(R.string.match_second_list_title));
            return false;
        }

        return true;
    }

}

