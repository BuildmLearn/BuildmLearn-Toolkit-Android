package org.buildmlearn.toolkit.templates;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
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
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public class Holder {
        TextView question;
        TextView answer;
        TextView hint;
        ImageView image;
        Button edit;
        Button delete;

    }
}
