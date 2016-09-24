package org.buildmlearn.toolkit.utilities;

import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * @brief Helper functions used to hide the soft keyboard.
 * <p/>
 * User : opticod(Anupam Das)
 * Date : 24/2/16.
 */
public class KeyboardHelper {
    public static void hideKeyboard(final Activity activity, View view) {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                View view = activity.getCurrentFocus();
                if (view != null) {
                    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });
    }
}

