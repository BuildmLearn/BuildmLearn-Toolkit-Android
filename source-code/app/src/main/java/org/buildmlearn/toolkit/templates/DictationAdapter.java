package org.buildmlearn.toolkit.templates;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.holder.HeaderHolder;
import org.buildmlearn.toolkit.views.TextViewPlus;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperAdapter;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperViewHolder;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Dictation Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 4/7/16.
 */
abstract class DictationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context mContext;
    private final ArrayList<DictationModel> data;

    protected abstract boolean onLongItemClick(int position, View view);
    protected abstract String getTitle();
    protected abstract void restoreToolbarColorSchema();
    protected abstract String getAuthorName();
    protected abstract void setAuthorName(String authorName);
    protected abstract void setTitle(String title);

    DictationAdapter(Context mContext, ArrayList<DictationModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    public DictationModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dictation_template_item, parent, false);
            return new DictationHolder(view);
        } else if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_header_template, parent, false);
            return new HeaderHolder(view,mContext,6);
        } else
            return null;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;

        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        if (viewHolder instanceof DictationHolder) {
            final DictationHolder holder = (DictationHolder) viewHolder;
            holder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onLongItemClick(viewHolder.getLayoutPosition(), v);
                }
            });
            final DictationModel dictation = getItem(position - 1);
            holder.passage.setText(Html.fromHtml("<b>" + "Passage :  " + "</b> " + dictation.getPassage()));
            holder.title.setText(Html.fromHtml("<b>" + "Title :  " + "</b> " + dictation.getTitle()));

            if (dictation.isExpanded()) {
                holder.expandButton.setVisibility(View.INVISIBLE);
                holder.collapseButton.setVisibility(View.VISIBLE);

            } else {
                holder.expandButton.setVisibility(View.VISIBLE);
                holder.collapseButton.setVisibility(View.INVISIBLE);
            }
            holder.collapseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    collapseTextView(holder.passage);
                    dictation.setExpanded(false);
                    holder.expandButton.setVisibility(View.VISIBLE);
                    holder.collapseButton.setVisibility(View.INVISIBLE);
                }
            });
            holder.expandButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    expandTextView(holder.passage);
                    dictation.setExpanded(true);
                    holder.expandButton.setVisibility(View.INVISIBLE);
                    holder.collapseButton.setVisibility(View.VISIBLE);
                }
            });
        } else if (viewHolder instanceof HeaderHolder) {
            final HeaderHolder headerHolder = (HeaderHolder) viewHolder;
            try {
                headerHolder.authorEditText.setText(getAuthorName());
                headerHolder.titleEditText.setText(getTitle());
                handleTextChange(headerHolder.authorEditText, headerHolder.titleEditText);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleTextChange(final EditText authorEditText, final EditText titleEditText) {
        authorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(getClass().getName(), "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setAuthorName(authorEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(getClass().getName(), "afterTextChanged");
            }
        });
        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.e(getClass().getName(), "beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setTitle(titleEditText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.e(getClass().getName(), "afterTextChanged");
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    private void expandTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", tv.getLineCount());
        animation.setDuration(tv.getLineCount() * 10).start();
    }

    private void collapseTextView(TextView tv) {
        ObjectAnimator animation = ObjectAnimator.ofInt(tv, "maxLines", 5);
        animation.setDuration(tv.getLineCount() * 10).start();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (toPosition == 0)
            return false;
        try {
            DictationModel prev = data.remove(fromPosition - 1);
            data.add(toPosition > fromPosition ? toPosition - 1 : toPosition - 1, prev);
            notifyItemMoved(fromPosition, toPosition);
            restoreToolbarColorSchema();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class DictationHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        private View view;
        private TextViewPlus title;
        private TextViewPlus passage;
        private ImageButton expandButton;
        private ImageButton collapseButton;

        DictationHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            title = (TextViewPlus) itemView.findViewById(R.id.dict_title);
            passage = (TextViewPlus) itemView.findViewById(R.id.dict_passage);
            expandButton = (ImageButton) itemView.findViewById(R.id.toogle_expand);
            collapseButton = (ImageButton) itemView.findViewById(R.id.toogle_collapse);
        }

        @Override
        public void onItemSelected() {
            Log.e(getClass().getName(), "Item Selected to drag");
        }
    }
}
