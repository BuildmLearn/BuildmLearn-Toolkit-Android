package org.buildmlearn.toolkit.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.TypedValue;

import android.widget.LinearLayout;
import android.widget.TextView;


import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.adapter.TutorialAdapter;
import org.buildmlearn.toolkit.constant.Constants;
import org.buildmlearn.toolkit.model.Tutorial;

/**
 * @brief Shows the tutorial related to BuildmLearn toolkit usage.
 */
public class TutorialActivity extends AppCompatActivity {


    private LinearLayout indicatingDotsContainer;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        TutorialAdapter mAdapter = new TutorialAdapter(this, getIntent().getBooleanExtra(Constants.START_ACTIVITY, false));

        ViewPager mPager = (ViewPager) findViewById(R.id.pager);
        //  mPager.setAdapter(mAdapter);
        indicatingDotsContainer = (LinearLayout) findViewById(R.id.layoutDots);
        addBottomDots(0); //adds indicating dots to given slide i.e current slide

        assert mPager != null;
        mPager.setAdapter(mAdapter);
        mPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void addBottomDots(int current_slide) {
        TextView[] dots = new TextView[Tutorial.values().length];
        int dot_colorActive = getResources().getColor(R.color.selected_dot);

        indicatingDotsContainer.removeAllViews();

        //number of dots added to container equals number of slides
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#9675;"));
            dots[i].setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
            indicatingDotsContainer.addView(dots[i]);
        }

        //dot corresponding to current slide is given active color i.e white color
        if (dots.length > 0) {
            dots[current_slide].setTextColor(dot_colorActive);
            dots[current_slide].setText(Html.fromHtml("&#8226;"));
            dots[current_slide].setTextSize(TypedValue.COMPLEX_UNIT_SP,32);
        }
    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {


        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //do nothing

        }

        //invoked when slide is changed
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

            //do nothing
        }


    };
}