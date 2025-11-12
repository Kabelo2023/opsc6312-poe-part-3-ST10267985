package com.example.smartplanner

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smartplanner.ui.home.HomeActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId

@RunWith(AndroidJUnit4::class)
class HomeActivityTest {

    @get:Rule
    val rule = ActivityScenarioRule(HomeActivity::class.java)

    @Test
    fun showsWeekList() {
        onView(withId(R.id.weekList)).check(matches(isDisplayed()))
    }
}
