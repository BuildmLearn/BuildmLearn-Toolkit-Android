package org.buildmlearn.toolkit.espresso;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.test.annotation.UiThreadTest;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.v7.widget.AppCompatTextView;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.buildmlearn.toolkit.R;
import org.buildmlearn.toolkit.activity.TemplateEditor;
import org.buildmlearn.toolkit.constant.Constants;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.closeSoftKeyboard;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.longClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withResourceName;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsAnything.anything;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * Espresso test designed to test all the functionalities of Comprehension template
 * Created by anupam (opticod) on 7/6/16.
 */

@Ignore
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ComprehensionTest {
    @Rule
    public final ActivityTestRule<TemplateEditor> mActivityRule =
            new ActivityTestRule<TemplateEditor>(TemplateEditor.class) {
                @Override
                protected Intent getActivityIntent() {
                    Context targetContext = getInstrumentation()
                            .getTargetContext();
                    Intent result = new Intent(targetContext, TemplateEditor.class);
                    result.putExtra(Constants.TEMPLATE_ID, 5);
                    return result;
                }
            };

    public static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void allowPermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= 23) {
            UiDevice device = UiDevice.getInstance(getInstrumentation());
            UiObject allowPermissions = device.findObject(new UiSelector().text("Allow"));
            if (allowPermissions.exists()) {
                try {
                    allowPermissions.click();
                } catch (UiObjectNotFoundException e) {
                }
            }
        }
    }

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

    public void toolbarTitle() {
        String title = "Comprehension Template";
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar))))
                .check(matches(withText(title)));
    }

    public void addMetaDetails() {
        String passTitle = "PassageTitle";
        String passage = "Short Passage.";
        String timer = "180";

        onView(withId(R.id.button_add_item)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.meta_title)).perform(typeText(passTitle));
        closeSoftKeyboard();
        onView(withId(R.id.meta_passage)).perform(typeText(passage), ViewActions.closeSoftKeyboard());
        sleep();
        onView(withId(R.id.meta_timer)).perform(scrollTo());
        sleep();
        onView(withId(R.id.meta_timer)).perform(click()).perform(typeText(timer), ViewActions.closeSoftKeyboard());
        sleep();
        onView(withResourceName("buttonDefaultPositive")).perform(click());

    }

    public void editMetaDetails() {
        String passTitle = "EditedPassageTitle";
        String passage = "EditedShort Passage.";

        onView(withId(R.id.template_meta_listview)).perform(longClick());
        onView(withId(R.id.action_edit)).perform(click());
        closeSoftKeyboard();
        onView(withId(R.id.meta_title)).perform(replaceText(passTitle));
        closeSoftKeyboard();
        onView(withId(R.id.meta_passage)).perform(replaceText(passage), ViewActions.closeSoftKeyboard());
        sleep();
        onView(withResourceName("buttonDefaultPositive")).perform(click());

    }

    public void addQuestions() {
        String question = "This is just a silly question whose answer is (b).";

        onView(withId(R.id.button_add_item)).perform(click());
        onView(withId(R.id.quiz_question)).perform(typeText(question));
        closeSoftKeyboard();
        sleep();
        onView(withId(R.id.quiz_option_1)).perform(scrollTo());
        onView(withId(R.id.quiz_option_1)).perform(typeText("A"));
        closeSoftKeyboard();
        sleep();
        onView(withId(R.id.quiz_option_2)).perform(scrollTo());
        onView(withId(R.id.quiz_option_2)).perform(typeText("B"));
        closeSoftKeyboard();
        sleep();
        onView(withId(R.id.quiz_radio_2)).perform(scrollTo()).perform(click());
        closeSoftKeyboard();
        sleep();
        onView(withResourceName("buttonDefaultPositive")).perform(click());

    }

    public void editQuestions() {
        String question = "This is just a silly question whose answer changed to (a).";

        onData(anything()).inAdapterView(withId(R.id.template_editor_listview)).atPosition(1).perform(longClick());
        onView(withId(R.id.action_edit)).perform(click());
        onView(withId(R.id.quiz_question)).perform(replaceText(question));
        closeSoftKeyboard();
        onView(withId(R.id.quiz_radio_1)).perform(scrollTo()).perform(click());
        closeSoftKeyboard();
        onView(withResourceName("buttonDefaultPositive")).perform(click());

    }

    public void addTemplate() {

        onView(withId(R.id.author_name)).perform(replaceText("Anupam"));
        onView(withId(R.id.template_title)).perform(replaceText("Testing template"));
    }

    public void saveAPK() {

        onView(withId(R.id.action_save)).perform(click());
        onView(withText("Save APK")).perform(click());

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        String finalApk = getText(allOf(withResourceName("content"), is(instanceOf(AppCompatTextView.class))));
        finalApk = finalApk.substring(18, finalApk.length());
        File file = new File(finalApk);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        mActivityRule.getActivity().startActivity(intent);
    }

    private String getText(final Matcher<View> matcher) {
        final String[] stringHolder = {null};
        onView(matcher).perform(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public String getDescription() {
                return "getting text from a TextView";
            }

            @Override
            public void perform(UiController uiController, View view) {
                TextView tv = (TextView) view;
                stringHolder[0] = tv.getText().toString();
            }
        });
        return stringHolder[0];
    }

    public void checkSimulator() {
        onView(withId(R.id.action_simulate)).perform(click());
        onView(withText("Testing template")).check(matches(isDisplayed()));
        onView(withText("Anupam")).check(matches(isDisplayed()));
        onView(withId(R.id.enter)).perform(click());
        String title = "EditedPassageTitle";
        String passage = "EditedShort Passage.";
        onView(allOf(instanceOf(TextView.class), withParent(withId(R.id.toolbar)), withText(title)))
                .check(matches(isDisplayed()));

        onView(withId(R.id.passage)).check(matches(withText(passage))).check(matches(isDisplayed()));

        onView(withId(R.id.go_to_ques)).perform(scrollTo()).perform(click());

        onView(withId(R.id.radioButton1)).perform(scrollTo()).perform(click());
        onView(withId(R.id.next)).perform(scrollTo()).perform(click());
        onView(withId(R.id.correct)).check(matches(withText("Total Correct : 1"))).check(matches(isDisplayed()));
        onView(withId(R.id.wrong)).check(matches(withText("Total Wrong : 0"))).check(matches(isDisplayed()));
        onView(withId(R.id.un_answered)).check(matches(withText("Total Unanswered : 0"))).check(matches(isDisplayed()));

        onView(withId(R.id.exit)).perform(click());

    }

    @Test
    public void testAllSerially() {
        allowPermissionsIfNeeded();
        toolbarTitle();
        addTemplate();
        addMetaDetails();
        addQuestions();
        editMetaDetails();
        editQuestions();
        checkSimulator();
        saveAPK();
    }
}