package org.buildmlearn.toolkit.views.dragdroprecyclerview;

import android.support.v7.widget.helper.ItemTouchHelper;

/**
 * Interface to notify an item ViewHolder of relevant callbacks from {@link
 * android.support.v7.widget.helper.ItemTouchHelper.Callback}.
 */
public interface ItemTouchHelperViewHolder {

    /**
     * Called when the {@link ItemTouchHelper} first registers an item as being moved.
     * Implementations should update the item view to indicate it's active state.
     */
    void onItemSelected();
}
