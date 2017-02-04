package org.buildmlearn.toolkit.views.dragdroprecyclerview;

import android.support.v7.widget.RecyclerView;

/**
 * Interface to notify a {@link RecyclerView.Adapter} of moving event from a {@link
 * android.support.v7.widget.helper.ItemTouchHelper.Callback}.
 */
public interface ItemTouchHelperAdapter {

    /**
     * Called when an item has been dragged far enough to trigger a move. This is called every time
     * an item is shifted, and not at the end of a "drop" event.
     *
     * @param fromPosition The start position of the moved item.
     * @param toPosition   Then end position of the moved item.
     * @see RecyclerView#getAdapterPositionFor(RecyclerView.ViewHolder)
     * @see RecyclerView.ViewHolder#getAdapterPosition()
     */
    boolean onItemMove(int fromPosition, int toPosition);
}
