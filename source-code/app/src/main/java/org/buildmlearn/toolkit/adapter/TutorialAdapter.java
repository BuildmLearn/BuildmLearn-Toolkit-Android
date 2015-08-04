package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorRes;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.HomeActivity;
import org.buildmlearn.toolkit.model.Tutorial;

/**
 * Created by abhishek on 03/08/15 at 10:52 PM.
 */
public class TutorialAdapter extends PagerAdapter {

    private Context mContext;
    private Tutorial[] mTutorials;
    private ListColor[] colors = ListColor.values();

    public TutorialAdapter(Context context) {
        mContext = context;
        mTutorials = Tutorial.values();
    }

    @Override
    public int getCount() {
        return mTutorials.length;
    }


    public Tutorial getItem(int position) {
        return mTutorials[position];
    }

    public long getItemId(int position) {
        return position;
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Tutorial tutorial = getItem(position);


        View convertView;
        if (tutorial.isLastScreen()) {
            convertView = inflater.inflate(R.layout.tutorial_layout_finish, null);

            convertView.findViewById(R.id.finish_tutorial_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, HomeActivity.class));
                }
            });
        } else {
            convertView = inflater.inflate(R.layout.tutorial_layout, null);
            ImageView deviceImage = (ImageView) convertView
                    .findViewById(R.id.device_image);
            TextView title = (TextView) convertView
                    .findViewById(R.id.tutorial_title);
            TextView description = (TextView) convertView
                    .findViewById(R.id.tutorial_desc);

            int color = colors[position % colors.length].getColor();

            convertView.findViewById(R.id.tutorial_layout).setBackgroundColor(color);

            deviceImage.setImageResource(tutorial.getImage());
            title.setText(tutorial.getTitle());
            description.setText(tutorial.getDescription());
        }
        container.addView(convertView, 0);

        return convertView;
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ViewGroup) object);
    }

    private enum ListColor {
        BLUE("#29A6D4"),
        GREEN("#1C7D6C"),
        ORANGE("#F77400"),
        RED("#F53B3C");

        private
        @ColorRes
        int color;

        ListColor(String colorCode) {
            this.color = Color.parseColor(colorCode);
        }

        public int getColor() {
            return color;
        }
    }

}
