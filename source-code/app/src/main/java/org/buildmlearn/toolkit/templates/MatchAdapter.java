package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.adapter.TemplateAdapter;
import org.buildmlearn.toolkit.views.TextViewPlus;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperAdapter;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperViewHolder;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Match Template Editor data.
 * <p/>
 * Created by Anupam (opticod) on 16/7/16.
 */
abstract class MatchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final ArrayList<MatchModel> matchData;

    protected abstract boolean onLongItemClick(int position, View view, boolean isLongPress);

    protected abstract String getTitle();

    protected abstract void restoreToolbarColorSchema();

    protected abstract String getAuthorName();

    protected abstract void setAuthorName(String authorName);

    protected abstract void setTitle(String title);

    protected abstract void populateMetaList(ListView listView);

    MatchAdapter(ArrayList<MatchModel> matchData) {
        this.matchData = matchData;
    }


    public MatchModel getItem(int position) {
        return matchData.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false);
            return new MatchAdapterHolder(view);
        } else if (viewType == TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.listview_header_template, parent, false);
            return new HeaderHolder(view);
        }
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
        if (viewHolder instanceof MatchAdapterHolder) {
            MatchAdapterHolder holder = (MatchAdapterHolder) viewHolder;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onLongItemClick(viewHolder.getLayoutPosition(), v, true);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onLongItemClick(viewHolder.getLayoutPosition(), v, false);
                }
            });
            MatchModel data = getItem(viewHolder.getLayoutPosition() - 1);
            holder.matchA.setText(data.getMatchA());
            holder.matchB.setText(data.getMatchB());
        } else if (viewHolder instanceof HeaderHolder) {
            final HeaderHolder headerHolder = (HeaderHolder) viewHolder;
            try {
                headerHolder.authorEditText.setText(getAuthorName());
                headerHolder.titleEditText.setText(getTitle());
                populateMetaList(headerHolder.listView);
                if (headerHolder.listView.getAdapter().getCount() < 1)
                    headerHolder.shadowMeta.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && headerHolder.listView.getAdapter().getCount() > 0) {
                    headerHolder.shadowMeta.setVisibility(View.VISIBLE);
                }
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
        return matchData.size() + 1;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (toPosition == 0)
            return false;
        try {
            MatchModel prev = matchData.remove(fromPosition - 1);
            matchData.add(toPosition > fromPosition ? toPosition - 1 : toPosition - 1, prev);
            notifyItemMoved(fromPosition, toPosition);
            restoreToolbarColorSchema();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {
        private View shadowMeta;
        private EditText authorEditText, titleEditText;
        private ListView listView;

        HeaderHolder(View itemView) {
            super(itemView);
            Context context = itemView.getContext();
            authorEditText = (EditText) itemView.findViewById(R.id.author_name);
            titleEditText = (EditText) itemView.findViewById(R.id.template_title);
            listView = (ListView) itemView.findViewById(R.id.template_meta_listview);
            shadowMeta = itemView.findViewById(R.id.shadow_meta);

            LinearLayout headerLayout = (LinearLayout) itemView.findViewById(R.id.header_layout);
            headerLayout.setBackgroundColor(TemplateAdapter.ListColor.values()[7].getColor());
            headerLayout.invalidate();
            ((TemplateEditor)context).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(TemplateAdapter.ListColor.values()[7].getColor()));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ((TemplateEditor)context).getWindow().setStatusBarColor(TemplateAdapter.ListDarkColor.values()[7].getColor());
                ((TemplateEditor)context).getWindow().setNavigationBarColor(TemplateAdapter.ListColor.values()[7].getColor());
            }
        }
    }

    private static class MatchAdapterHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        private TextViewPlus matchA, matchB;

        MatchAdapterHolder(View itemView) {
            super(itemView);
            matchA = (TextViewPlus) itemView.findViewById(R.id.matchA);
            matchB = (TextViewPlus) itemView.findViewById(R.id.matchB);
        }

        @Override
        public void onItemSelected() {
            Log.e(getClass().getName(), "Item Selected to drag");
        }
    }
}
