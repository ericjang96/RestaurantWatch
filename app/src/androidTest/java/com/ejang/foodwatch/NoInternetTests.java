package com.ejang.foodwatch;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.ejang.foodwatch.Activities.BrowseActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by eric_ on 2017-07-05.
 */
@RunWith(AndroidJUnit4.class)
public class NoInternetTests {

    @Rule
    public ActivityTestRule<BrowseActivity> mActivityTestRule = new ActivityTestRule<>(BrowseActivity.class);

    @Before
    public void setExampleLocation() {
        BrowseActivity.setUserLat(49.191461);
        BrowseActivity.setUserLong(-122.849329);
        BrowseActivity.locationSet.set(true);
    }

    @Test
    public void testVolleyError() {
    }

    @After
    public void after() throws InterruptedException {
        Thread.sleep(3000);
    }
}
