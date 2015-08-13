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
    START(R.drawable.app_splash_screen_framed, R.string.screen_1_title, R.string.screen_1_desc),
    FIRST(R.drawable.main_screen_framed, R.string.screen_2_title, R.string.screen_2_desc),
    SECOND(R.drawable.template_list_framed, R.string.screen_3_title, R.string.screen_3_desc),
    THIRD(R.drawable.quiz_template_framed, R.string.screen_4_title, R.string.screen_4_desc),
    FOURTH(R.drawable.simulator_without_any_template_framed, R.string.screen_5_title, R.string.screen_5_desc),
    FIFTH(R.drawable.load_saved_projects_framed, R.string.screen_6_title, R.string.screen_6_desc),
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
