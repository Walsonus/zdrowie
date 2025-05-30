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

/**
 * Instrumented test class for {@link ProfileFragment} functionality.
 *
 * <p>This class tests the user interface and behavior of the profile fragment,
 * including theme switching functionality.
 *
 * <p>Tests include:
 * <ul>
 *   <li>Theme toggle button text changes</li>
 *   <li>Theme switching behavior</li>
 * </ul>
 *
 * <p>Note: Tests require device/emulator with English locale setting.
 */
@RunWith(AndroidJUnit4::class)
class ProfileFragmentInstrumentedTest {

    /**
     * Activity scenario used to launch and control the main activity during tests.
     */
    private lateinit var activityScenario: ActivityScenario<MainAppActivity>

    /**
     * Prepares the test environment before each test case.
     *
     * <p>This method:
     * <ul>
     *   <li>Sets the device locale to English</li>
     *   <li>Launches the main activity</li>
     *   <li>Navigates to the profile fragment</li>
     * </ul>
     */
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

    /**
     * Cleans up test resources after each test case.
     *
     * <p>Closes the activity scenario to prevent memory leaks
     * and ensure test isolation.
     */
    @After
    fun tearDown() {
        activityScenario.close()
    }

    /**
     * Verifies the theme toggle button functionality.
     *
     * <p>This test:
     * <ul>
     *   <li>Checks initial button text</li>
     *   <li>Simulates button click</li>
     *   <li>Verifies text changes after click</li>
     *   <li>Tests toggle back to original state</li>
     * </ul>
     *
     * <p>Note: Includes 1-second delays to allow theme transitions to complete.
     *
     * @throws InterruptedException if thread sleep is interrupted
     */
    @Test
    fun themeToggleButton_shouldChangeTextAndTheme() {
        // Verify initial state
        onView(withId(R.id.themeToggleButton))
            .check(matches(withText("Zmień tryb na ciemny")))

        // Test dark mode activation
        onView(withId(R.id.themeToggleButton))
            .perform(click())

        Thread.sleep(1000) // Allow theme transition to complete

        // Verify dark mode state
        onView(withId(R.id.themeToggleButton))
            .check(matches(withText("Zmień tryb na jasny")))

        // Test light mode reactivation
        onView(withId(R.id.themeToggleButton))
            .perform(click())

        Thread.sleep(1000) // Allow theme transition to complete

        // Verify back to initial state
        onView(withId(R.id.themeToggleButton))
            .check(matches(withText("Zmień tryb na ciemny")))
    }
}


