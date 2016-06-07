package org.buildmlearn.toolkit.espresso;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.annotation.UiThreadTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.WindowManager;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.constant.Constants;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Created by anupam (opticod) on 7/6/16.
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class ComprehensionTest {
    @Rule
    public ActivityTestRule<TemplateEditor> mActivityRule =
            new ActivityTestRule<TemplateEditor>(TemplateEditor.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = InstrumentationRegistry.getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, TemplateEditor.class);
                    result.putExtra(Constants.TEMPLATE_ID, 5);
                    return result;
                }
            };

    @UiThreadTest
    @Before
    public void setUp() throws Throwable {
        final Activity activity = mActivityRule.getActivity();
        mActivityRule.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                KeyguardManager mKG = (KeyguardManager) activity.getSystemService(Context.KEYGUARD_SERVICE);
                KeyguardManager.KeyguardLock mLock = mKG.newKeyguardLock(Context.KEYGUARD_SERVICE);
                mLock.disableKeyguard();

                activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        | WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
            }
        });
    }

    @Test
    public void toolbarTitle() {
        String title = "Comprehension Template";
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(title)));
    }

    @Test
    public void addMetaDetails() {
        String passTitle = "PassageTitle";
        String passage = "Short Passage.";
        String timer = "180";

        onView(withId(R.id.button_add_item)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.meta_title)).perform(typeText(passTitle));
        closeSoftKeyboard();
        onView(withId(R.id.meta_passage)).perform(typeText(passage));
        closeSoftKeyboard();
        sleep();
        onView(withId(R.id.meta_timer)).perform(scrollTo()).perform(click()).perform(typeText(timer));
        closeSoftKeyboard();
        sleep();

    }

    public void sleep() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
