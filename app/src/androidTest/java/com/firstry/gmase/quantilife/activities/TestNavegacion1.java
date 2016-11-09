package com.firstry.gmase.quantilife.activities;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import com.firstry.gmase.quantilife.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestNavegacion1 {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testNavegacion1() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.go_questions),
                        withParent(allOf(withId(R.id.rightLayout),
                                withParent(withId(R.id.toolbar)))),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.go_stats),
                        withParent(allOf(withId(R.id.rightLayout),
                                withParent(withId(R.id.toolbarQ)))),
                        isDisplayed()));
        appCompatImageButton2.perform(click());

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.go_main),
                        withParent(allOf(withId(R.id.rightLayout),
                                withParent(withId(R.id.toolbarS)))),
                        isDisplayed()));
        appCompatImageButton3.perform(click());

    }

}
