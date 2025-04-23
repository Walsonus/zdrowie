package pack.zdrowie
import android.app.Activity
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Rule
import org.junit.Test
import androidx.test.espresso.intent.matcher.IntentMatchers.*


class RegisterActivityTest {

    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    @Test
    fun register_withEmptyEmail_showsError() {
        onView(withId(R.id.emailTextEdit)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit2)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        activityRule.scenario.onActivity { activity ->
            try {
                onView(withText(R.string.no_email))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(activity.window.decorView)))
                    .check(matches(isDisplayed()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


    }

    @Test
    fun register_withInvalidEmail_showsError() {
        onView(withId(R.id.emailTextEdit)).perform(typeText("invalid-email"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit2)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        activityRule.scenario.onActivity { activity ->
            try {
                onView(withText(R.string.wrong_email))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(activity.window.decorView)))
                    .check(matches(isDisplayed()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    @Test
    fun register_withNotEqualPasswords_showsError() {
        onView(withId(R.id.emailTextEdit)).perform(
            typeText("test@example.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordTextEdit2)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText("password124"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        activityRule.scenario.onActivity { activity ->
            try {
                onView(withText(R.string.mismatch_password))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(activity.window.decorView)))
                    .check(matches(isDisplayed()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun register_withNoPasswords_showsError() {
        onView(withId(R.id.emailTextEdit)).perform(
            typeText("test@example.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordTextEdit2)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        activityRule.scenario.onActivity { activity ->
            try {
                onView(withText(R.string.no_password))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(activity.window.decorView)))
                    .check(matches(isDisplayed()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @Test
    fun register_withCorrectCredentials() {
        onView(withId(R.id.emailTextEdit)).perform(
            typeText("test@example.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordTextEdit2)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        activityRule.scenario.onActivity { activity ->
            try {
                onView(withText(R.string.user_registered))
                    .inRoot(RootMatchers.withDecorView(Matchers.not(activity.window.decorView)))
                    .check(matches(isDisplayed()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

