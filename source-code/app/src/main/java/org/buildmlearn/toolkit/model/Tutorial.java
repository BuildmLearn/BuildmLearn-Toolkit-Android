package org.buildmlearn.toolkit.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import org.buildmlearn.toolkit.R;

/**
 * @brief Enum used as model for holding tutorial data.
 *
 * Created by abhishek on 03/08/15 at 10:53 PM.
 */
public enum Tutorial {
    START(R.drawable.splash_stretch_framed, R.string.screen_1_title, R.string.screen_1_desc),
    FIRST(R.drawable.splash_stretch_framed, R.string.screen_1_title, R.string.screen_1_desc),
    SECOND(R.drawable.splash_stretch_framed, R.string.screen_1_title, R.string.screen_1_desc),
    THIRD(R.drawable.splash_stretch_framed, R.string.screen_1_title, R.string.screen_1_desc),
    FOURTH(R.drawable.splash_stretch_framed, R.string.screen_1_title, R.string.screen_1_desc),
    LAST(true);


    private
    @DrawableRes
    int image;
    private
    @StringRes
    int title;
    private
    @StringRes
    int description;
    private boolean isLastScreen;

    Tutorial(int image, int title, int description) {
        this.image = image;
        this.title = title;
        this.description = description;
        this.isLastScreen = false;
    }

    Tutorial(boolean isLastScreen) {
        this.isLastScreen = true;
        this.image = 0;
        this.title = 0;
        this.description = 0;
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

    public boolean isLastScreen() {
        return isLastScreen;
    }
}
