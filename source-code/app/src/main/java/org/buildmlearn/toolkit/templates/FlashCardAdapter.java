package org.buildmlearn.toolkit.templates;

import android.content.Context;

import android.support.design.widget.Snackbar;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperAdapter;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperViewHolder;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Flash Card Template Editor data.
 * <p/>
 * Created by abhishek on 12/07/15 at 11:56 PM.
 */
abstract class FlashCardAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context mContext;
    private final ArrayList<FlashCardModel> mData;

    protected abstract boolean onLongItemClick(int position, View view);

    protected abstract String getTitle();

    protected abstract void restoreToolbarColorSchema();

    protected abstract String getAuthorName();

    protected abstract void setAuthorName(String authorName);

    protected abstract void setTitle(String title);

    FlashCardAdapter(Context context, ArrayList<FlashCardModel> data) {
        mContext = context;
        mData = data;
    }

    public FlashCardModel getItem(int position) {
        return mData.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.flash_template_item, parent, false);
            return new FlashCardAdapterHolder(view);
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
        if (viewHolder instanceof FlashCardAdapterHolder) {
            FlashCardAdapterHolder flashCardAdapterHolder = (FlashCardAdapterHolder) viewHolder;
            final FlashCardModel data = getItem(viewHolder.getLayoutPosition() - 1);
            flashCardAdapterHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onLongItemClick(viewHolder.getLayoutPosition(), v);
                }
            });
            flashCardAdapterHolder.answer.setText(data.getAnswer());
            flashCardAdapterHolder.image.setImageBitmap(data.getImageBitmap());
            flashCardAdapterHolder.hint.setText(data.getHint());
            flashCardAdapterHolder.question.setText(data.getQuestion());

            flashCardAdapterHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Long press to edit this item", Toast.LENGTH_SHORT).show();
                }
            });

            flashCardAdapterHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FlashCardModel flashCardModel = mData.get(viewHolder.getLayoutPosition() - 1);
                    mData.remove(viewHolder.getLayoutPosition() - 1);
                    notifyDataSetChanged();
                    notifyDataSetChanged();
                    Snackbar.make(v, R.string.snackbar_deleted_message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mData.add(viewHolder.getLayoutPosition(), flashCardModel);
                                    notifyDataSetChanged();
                                    Snackbar.make(v, R.string.snackbar_restored_message, Snackbar.LENGTH_LONG).show();
                                }
                            }).show();


                    ((TemplateEditor) mContext).restoreSelectedView();
                }
            });

            flashCardAdapterHolder.edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, R.string.LongPress_toedit, Toast.LENGTH_SHORT).show();
                }
            });

            flashCardAdapterHolder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final FlashCardModel flashCardModel = mData.get(viewHolder.getLayoutPosition() - 1);
                    mData.remove(viewHolder.getLayoutPosition() - 1);
                    notifyDataSetChanged();
                    notifyDataSetChanged();
                    Snackbar.make(v, R.string.snackbar_deleted_message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (viewHolder.getLayoutPosition() - 1 >= 0) {
                                        mData.add(viewHolder.getLayoutPosition() - 1, flashCardModel);
                                    } else {
                                        mData.add(flashCardModel);
                                    }
                                    notifyDataSetChanged();
                                    Snackbar.make(v, R.string.snackbar_restored_message, Snackbar.LENGTH_LONG).show();
                                }
                            }).show();


                    ((TemplateEditor) mContext).restoreSelectedView();
                }
            });

        } else if (viewHolder instanceof FlashCardAdapter.HeaderHolder) {
            final FlashCardAdapter.HeaderHolder headerHolder = (FlashCardAdapter.HeaderHolder) viewHolder;
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
        return mData.size() + 1;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (toPosition == 0)
            return false;
        try {
            FlashCardModel prev = mData.remove(fromPosition - 1);
            mData.add(toPosition > fromPosition ? toPosition - 1 : toPosition - 1, prev);
            notifyItemMoved(fromPosition, toPosition);
            restoreToolbarColorSchema();
            return true;
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static class HeaderHolder extends RecyclerView.ViewHolder {
        private EditText authorEditText, titleEditText;

        HeaderHolder(View itemView) {
            super(itemView);
            authorEditText = (EditText) itemView.findViewById(R.id.author_name);
            titleEditText = (EditText) itemView.findViewById(R.id.template_title);
        }
    }

    private static class FlashCardAdapterHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        private View view;
        public TextView question;
        public TextView answer;
        public TextView hint;
        public ImageView image;
        public ImageView edit;
        public ImageView delete;

        FlashCardAdapterHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            question = (TextViewPlus) itemView.findViewById(R.id.flash_item_question);
            answer = (TextViewPlus) itemView.findViewById(R.id.flash_item_answer);
            hint = (TextViewPlus) itemView.findViewById(R.id.flash_item_hint);
            image = (ImageView) itemView.findViewById(R.id.flash_item_image);
            delete = (ImageView) itemView.findViewById(R.id.flash_template_delete);
            edit = (ImageView) itemView.findViewById(R.id.flash_item_edit);
        }

        @Override
        public void onItemSelected() {
            Log.e(getClass().getName(), "Item Selected to drag");
        }
    }
}
