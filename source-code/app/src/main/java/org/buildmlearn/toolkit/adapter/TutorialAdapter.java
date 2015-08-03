package org.buildmlearn.toolkit.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;

/**
 * Created by abhishek on 03/08/15 at 10:52 PM.
 */
public class TutorialAdapter extends PagerAdapter {
    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return false;
    }
}
