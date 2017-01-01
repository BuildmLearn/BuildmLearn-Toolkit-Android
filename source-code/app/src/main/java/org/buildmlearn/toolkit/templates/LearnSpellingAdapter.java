package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.views.TextViewPlus;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperAdapter;
import org.buildmlearn.toolkit.views.dragdroprecyclerview.ItemTouchHelperViewHolder;

import java.util.ArrayList;

/**
 * @brief Adapter for displaying Learn Spelling Template Editor data.
 * <p/>
 * Created by abhishek on 17/06/15 at 9:48 PM.
 */
abstract class LearnSpellingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements ItemTouchHelperAdapter {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final Context mContext;
    private final ArrayList<LearnSpellingModel> data;

    protected abstract boolean onLongItemClick(int position, View view);
    protected abstract String getTitle();
    protected abstract void restoreToolbarColorSchema();
    protected abstract String getAuthorName();
    protected abstract void setAuthorName(String authorName);
    protected abstract void setTitle(String title);

    LearnSpellingAdapter(Context mContext, ArrayList<LearnSpellingModel> data) {
        this.mContext = mContext;
        this.data = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_template_item, parent, false);
            return new LearnSpellingHolder(view);
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
        if (viewHolder instanceof LearnSpellingHolder) {
            LearnSpellingHolder holder = (LearnSpellingHolder) viewHolder;
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onLongItemClick(viewHolder.getLayoutPosition(), v);
                }
            });
            LearnSpellingModel info = getItem(position - 1);
            holder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final LearnSpellingModel learnSpellingModel = data.get(viewHolder.getLayoutPosition() - 1);
                    data.remove(viewHolder.getLayoutPosition() - 1);
                    notifyDataSetChanged();
                    Snackbar.make(v, R.string.snackbar_deleted_message, Snackbar.LENGTH_LONG)
                            .setAction(R.string.snackbar_undo, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (viewHolder.getLayoutPosition() - 1 >= 0) {
                                        data.add(viewHolder.getLayoutPosition() - 1, learnSpellingModel);
                                    } else {
                                        data.add(learnSpellingModel);
                                    }
                                    notifyDataSetChanged();
                                    Snackbar.make(v, R.string.snackbar_restored_message, Snackbar.LENGTH_LONG).show();
                                }
                            }).show();

                    ((TemplateEditor) mContext).restoreSelectedView();
                }
            });
            holder.meaning.setText(info.getMeaning());
            holder.word.setText(info.getWord());

            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    final View dialogView = inflater.inflate(R.layout.info_dialog_add_edit_data, null);
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

                    final LearnSpellingModel data = getItem(viewHolder.getLayoutPosition() - 1);

                    final EditText word = (EditText) dialogView.findViewById(R.id.info_word);
                    final EditText meaning = (EditText) dialogView.findViewById(R.id.info_meaning);
                    word.setText(data.getWord());
                    meaning.setText(data.getMeaning());

                    dialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (InfoTemplate.validated(mContext, word, meaning)) {
                                String wordText = word.getText().toString();
                                String meaningText = meaning.getText().toString();

                                data.setWord(wordText);
                                data.setMeaning(meaningText);

                                notifyDataSetChanged();
                                dialog.dismiss();
                            }

                        }
                    });
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
    private LearnSpellingModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return data.size() + 1;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (toPosition == 0)
            return false;
        try {
            LearnSpellingModel prev = data.remove(fromPosition - 1);
            data.add(toPosition > fromPosition ? toPosition - 1 : toPosition - 1, prev);
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

    private static class LearnSpellingHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {
        private TextViewPlus word;
        private TextViewPlus meaning;
        private ImageView editButton;
        private ImageView deleteButton;

        LearnSpellingHolder(View itemView) {
            super(itemView);
            editButton = (ImageView) itemView.findViewById(R.id.info_template_edit);
            deleteButton = (ImageView) itemView.findViewById(R.id.info_template_delete);
            word = (TextViewPlus) itemView.findViewById(R.id.info_object);
            meaning = (TextViewPlus) itemView.findViewById(R.id.info_description);
        }

        @Override
        public void onItemSelected() {
            Log.e(getClass().getName(), "Item Selected to drag");
        }
    }
}
