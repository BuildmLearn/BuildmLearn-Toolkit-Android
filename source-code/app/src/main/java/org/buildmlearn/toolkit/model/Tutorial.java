package org.buildmlearn.toolkit.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.buildmlearn.toolkit.R;

/**
 * Created by abhishek on 03/08/15 at 10:53 PM.
 */
public enum Tutorial {
    START(R.drawable.splash_stretch_framed, R.string.screen_1_title, R.string.screen_1_desc),
    FIRST(R.drawable.splash_stretch_framed, R.string.screen_1_title, R.string.screen_1_desc);


    private @DrawableRes int image;
    private @StringRes int title;
    private @StringRes int description;

    Tutorial(int image, int title, int description) {
        this.image = image;
        this.title = title;
        this.description = description;
    }

    public int getImage() {
        return image;
    }

    public int getTitle() {
        return title;
    }

    public int getDescription() {
        return description;
    }
}
