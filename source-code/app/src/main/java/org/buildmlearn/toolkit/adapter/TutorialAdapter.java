package org.buildmlearn.toolkit.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.model.Tutorial;

/**
 * Created by abhishek on 03/08/15 at 10:52 PM.
 */
public class TutorialAdapter extends PagerAdapter {

    private Context mContext;
    private Tutorial[] mTutorials;

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

        View convertView = inflater.inflate(R.layout.tutorial_layout, null);
        ImageView deviceImage = (ImageView) convertView
                .findViewById(R.id.device_image);
        TextView title = (TextView) convertView
                .findViewById(R.id.tutorial_title);
        TextView description = (TextView) convertView
                .findViewById(R.id.tutorial_desc);

        Tutorial tutorial = getItem(position);

        deviceImage.setImageResource(tutorial.getImage());
        title.setText(tutorial.getTitle());
        description.setText(tutorial.getDescription());

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
}
