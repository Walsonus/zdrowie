package pack.zdrowie

import android.content.pm.ActivityInfo
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class ProfileFragmentInstrumentedTest {

    private lateinit var activityScenario: ActivityScenario<MainAppActivity>

    @Before
    fun setUp() {
        val config = androidx.test.platform.app.InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .resources
            .configuration

        config.setLocale(Locale.ENGLISH)
        androidx.test.platform.app.InstrumentationRegistry
            .getInstrumentation()
            .targetContext
            .resources
            .updateConfiguration(config, null)

        activityScenario = ActivityScenario.launch(MainAppActivity::class.java)

        onView(withId(R.id.nav_profile)).perform(click())
    }

    @After
    fun tearDown() {
        activityScenario.close()
    }

    @Test
    fun themeToggleButton_shouldChangeTextAndTheme() {
        onView(withId(R.id.themeToggleButton))
            .check(matches(withText("Zmień tryb na ciemny")))

        onView(withId(R.id.themeToggleButton))
            .perform(click())

        Thread.sleep(1000) // pauza 1 sekunda

        onView(withId(R.id.themeToggleButton))
            .check(matches(withText("Zmień tryb na jasny")))


        onView(withId(R.id.themeToggleButton))
            .perform(click())

        Thread.sleep(1000) // pauza 1 sekunda

        onView(withId(R.id.themeToggleButton))
            .check(matches(withText("Zmień tryb na ciemny")))

    }

}


