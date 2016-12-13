package org.buildmlearn.toolkit.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
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
 * @brief Adapter used for showing tutorial
 * <p/>
 * Created by abhishek on 03/08/15 at 10:52 PM.
 */
public class TutorialAdapter extends PagerAdapter {

    private final Activity mActivity;
    private final Tutorial[] mTutorials;
    private final ListColor[] colors = ListColor.values();
    private final boolean mStartActivity;

    public TutorialAdapter(Activity activity, boolean startActivity) {
        mActivity = activity;
        mTutorials = Tutorial.values();
        this.mStartActivity = startActivity;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getCount() {
        return mTutorials.length;
    }


    private Tutorial getItem(int position) {
        return mTutorials[position];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mActivity);
        boolean SkipTutorial = prefs.getBoolean("SkipTutorial",false);

        LayoutInflater inflater = (LayoutInflater) container.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Tutorial tutorial = getItem(position);


        View convertView;
        if (tutorial.isLastScreen()) {
            convertView = inflater.inflate(R.layout.tutorial_layout_finish, null);

            convertView.findViewById(R.id.finish_tutorial_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mStartActivity) {
                        mActivity.startActivity(new Intent(mActivity, HomeActivity.class));
                    }
                    mActivity.finish();
                }
            });
        } else {
            convertView = inflater.inflate(R.layout.tutorial_layout, null);
            View skip_button = convertView.findViewById(R.id.skip_button);
            skip_button.setVisibility(View.GONE);
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
            if(!SkipTutorial) {
                skip_button.setVisibility(View.VISIBLE);
            }
            convertView.findViewById(R.id.skip_button).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mStartActivity) {
                        mActivity.startActivity(new Intent(mActivity, HomeActivity.class));
                    }
                    mActivity.finish();
                }
            });
        }
        container.addView(convertView, 0);

        return convertView;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ViewGroup) object);
    }

    private enum ListColor {
        BLUE("#29A6D4"),
        GREEN("#1C7D6C"),
        ORANGE("#F77400"),
        RED("#F53B3C"),
        GRAYISH("#78909C"),
        PURPLE("#AB47BC"),
        YELLOW("#F9A01E");

        private
        @ColorRes
        final
        int color;

        ListColor(String colorCode) {
            this.color = Color.parseColor(colorCode);
        }

        public int getColor() {
            return color;
        }
    }

}
