package org.buildmlearn.toolkit.flashcardtemplate.animations;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Ashish Goel on 1/30/2016.
 */
public class FlipPageTransformer implements ViewPager.PageTransformer {

    @Override
    public void transformPage(View page, float position) {
        float percentage = 1 - Math.abs(position);
        setVisibility(page, position);
        setTranslation(page);
        setSize(page, position, percentage);
        setRotation(page, position, percentage);
    }

    private void setVisibility(View page, float position) {
        if (position < 0.5 && position > -0.5) {
            page.setVisibility(View.VISIBLE);
        } else {
            page.setVisibility(View.INVISIBLE);
        }
    }

    private void setTranslation(View page) {
        ViewPager viewPager = (ViewPager) page.getParent();
        int scroll = viewPager.getScrollX() - page.getLeft();
        page.setTranslationX(scroll);
    }

    private void setSize(View page, float position, float percentage) {
        if (percentage < 0.8)
            percentage = 0.8f;
        page.setScaleX((position != 0 && position != 1) ? percentage : 1);
        page.setScaleY((position != 0 && position != 1) ? percentage : 1);
    }

    private void setRotation(View page, float position, float percentage) {
        if (position > 0) {
            page.setRotationY(-180 * (percentage + 1));
        } else {
            page.setRotationY(180 * (percentage + 1));
        }
    }
}
