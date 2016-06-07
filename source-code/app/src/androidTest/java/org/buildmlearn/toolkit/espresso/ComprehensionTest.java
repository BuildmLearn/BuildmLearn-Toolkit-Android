package org.buildmlearn.toolkit.espresso;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.constant.Constants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
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
        onView(withId(R.id.meta_title)).perform(typeText(passTitle));
        onView(withId(R.id.meta_passage)).perform(typeText(passage));
        closeSoftKeyboard();
        onView(withId(R.id.meta_timer)).perform(typeText(timer));
        closeSoftKeyboard();
        onView(withResourceName("buttonDefaultPositive")).perform(click());

        onView(withText(passTitle)).check(matches(isDisplayed()));
        onView(withText(passage)).check(matches(isDisplayed()));
        onView(withText(timer + " sec")).check(matches(isDisplayed()));

    }
}
