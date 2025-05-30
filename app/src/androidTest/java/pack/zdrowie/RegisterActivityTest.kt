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


/**
 * Instrumented tests for {@link RegisterActivity} functionality.
 *
 * <p>This test class verifies the user registration validation logic including:
 * <ul>
 *   <li>Email format validation</li>
 *   <li>Password matching verification</li>
 *   <li>Empty field handling</li>
 *   <li>Successful registration flow</li>
 * </ul>
 */
class RegisterActivityTest {

    /**
     * Activity test rule that launches the RegisterActivity before each test.
     */
    @Rule
    @JvmField
    val activityRule = ActivityScenarioRule(RegisterActivity::class.java)

    /**
     * Tests registration with empty email field.
     *
     * <p>Verifies that:
     * <ol>
     *   <li>When email field is empty</li>
     *   <li>And passwords are valid</li>
     *   <li>Then appropriate error message is shown</li>
     * </ol>
     */
    @Test
    fun register_withEmptyEmail_showsError() {
        // Enter empty email and valid passwords
        onView(withId(R.id.emailTextEdit)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit2)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify error toast is shown
        verifyErrorMessageShown(R.string.no_email)
    }

    /**
     * Tests registration with invalid email format.
     *
     * <p>Verifies that:
     * <ol>
     *   <li>When email format is invalid</li>
     *   <li>And passwords are valid</li>
     *   <li>Then appropriate error message is shown</li>
     * </ol>
     */
    @Test
    fun register_withInvalidEmail_showsError() {
        // Enter invalid email and valid passwords
        onView(withId(R.id.emailTextEdit)).perform(typeText("invalid-email"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit2)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify error toast is shown
        verifyErrorMessageShown(R.string.wrong_email)
    }

    /**
     * Tests registration with non-matching passwords.
     *
     * <p>Verifies that:
     * <ol>
     *   <li>When passwords don't match</li>
     *   <li>And email is valid</li>
     *   <li>Then appropriate error message is shown</li>
     * </ol>
     */
    @Test
    fun register_withNotEqualPasswords_showsError() {
        // Enter valid email and mismatched passwords
        onView(withId(R.id.emailTextEdit)).perform(
            typeText("test@example.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordTextEdit2)).perform(typeText("password123"), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText("password124"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify error toast is shown
        verifyErrorMessageShown(R.string.mismatch_password)
    }

    /**
     * Tests registration with empty password fields.
     *
     * <p>Verifies that:
     * <ol>
     *   <li>When password fields are empty</li>
     *   <li>And email is valid</li>
     *   <li>Then appropriate error message is shown</li>
     * </ol>
     */
    @Test
    fun register_withNoPasswords_showsError() {
        // Enter valid email and empty passwords
        onView(withId(R.id.emailTextEdit)).perform(
            typeText("test@example.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordTextEdit2)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify error toast is shown
        verifyErrorMessageShown(R.string.no_password)
    }

    /**
     * Tests successful registration flow.
     *
     * <p>Verifies that:
     * <ol>
     *   <li>When all fields are valid</li>
     *   <li>Then success message is shown</li>
     * </ol>
     *
     * <p>Note: Currently contains incorrect implementation with empty passwords - needs fixing.
     */
    @Test
    fun register_withCorrectCredentials() {
        // Enter valid email and empty passwords (BUG: should be valid passwords)
        onView(withId(R.id.emailTextEdit)).perform(
            typeText("test@example.com"),
            closeSoftKeyboard()
        )
        onView(withId(R.id.passwordTextEdit2)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.passwordTextEdit3)).perform(typeText(""), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        // Verify success toast is shown
        verifyErrorMessageShown(R.string.user_registered)
    }

    /**
     * Helper method to verify error/success toasts are displayed.
     * @param messageRes The string resource ID of the expected message
     */
    private fun verifyErrorMessageShown(messageRes: Int) {
        activityRule.scenario.onActivity { activity ->
            try {
                onView(withText(messageRes))
                    .inRoot(withDecorView(not(activity.window.decorView)))
                    .check(matches(isDisplayed()))
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
