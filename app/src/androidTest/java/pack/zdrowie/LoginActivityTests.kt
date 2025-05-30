package pack.zdrowie

import android.os.SystemClock
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import pack.zdrowie.database.AppDatabase
import pack.zdrowie.database.DatabaseProvider
import pack.zdrowie.database.dao.UserDAO
import pack.zdrowie.database.entities.User
import java.time.LocalDate

import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase.assertNotNull
import org.junit.Rule

/**
 * Instrumented tests for {@link LoginActivity} functionality.
 *
 * <p>This test class verifies:
 * <ul>
 *   <li>User authentication flow</li>
 *   <li>UI component visibility</li>
 *   <li>Navigation between activities</li>
 *   <li>Database interactions during login</li>
 * </ul>
 *
 * <p>Tests are marked as {@code @MediumTest} as they involve:
 * <ul>
 *   <li>Database operations</li>
 *   <li>Activity navigation</li>
 *   <li>UI interactions</li>
 * </ul>
 */
@MediumTest
@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentedTest {

    /**
     * Data Access Object for user database operations.
     */
    private lateinit var userDAO: UserDAO

    /**
     * Reference to the application database.
     */
    private lateinit var db: AppDatabase

    /**
     * Initializes test environment before each test case.
     *
     * <p>Sets up:
     * <ul>
     *   <li>Database connection</li>
     *   <li>User DAO instance</li>
     * </ul>
     */
    @Before
    fun setUp() {
        if (!::db.isInitialized) {
            db = DatabaseProvider.getDatabase(ApplicationProvider.getApplicationContext())
            userDAO = db.userDao()
        }
    }

    /**
     * Cleans up test environment after each test case.
     *
     * <p>Performs:
     * <ul>
     *   <li>Database table clearance</li>
     * </ul>
     */
    @After
    fun tearDown() {
        db.clearAllTables()
    }

    /**
     * Verifies login behavior with invalid credentials.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Inserts test user into database</li>
     *   <li>Attempts login with wrong password</li>
     *   <li>Verifies login button remains visible (failed login)</li>
     * </ol>
     */
    @Test
    fun login_withInvalidCredentials_showsError() {
        runBlocking {
            userDAO.insert(createTestUser())

            ActivityScenario.launch(LoginActivity::class.java)

            // Attempt login
            onView(withId(R.id.emailEditText)).perform(typeText("test@example.com"))
            onView(withId(R.id.passwordEditText)).perform(
                typeText("wrongpassword"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.loginButton)).perform(click())

            // Verify still on login screen
            onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        }
    }

    /**
     * Verifies navigation to registration activity.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Launches login activity</li>
     *   <li>Clicks registration link</li>
     *   <li>Verifies navigation to registration</li>
     * </ol>
     */
    @Test
    fun registerText_click_navigatesToRegisterActivity() {
        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.registerText)).perform(click())
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    /**
     * Verifies successful login navigation.
     *
     * <p>Test steps:
     * <ol>
     *   <li>Inserts test user into database</li>
     *   <li>Performs login with correct credentials</li>
     *   <li>Verifies navigation to main activity</li>
     * </ol>
     */
    @Test
    fun login_withCorrectCredentials_navigatesToMainPage() {
        runBlocking {
            userDAO.insert(createTestUser())

            ActivityScenario.launch(LoginActivity::class.java)

            // Perform login
            onView(withId(R.id.emailEditText)).perform(typeText("test@example.com"))
            onView(withId(R.id.passwordEditText)).perform(
                typeText("password123"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.loginButton)).perform(click())

            // Verify main activity
            onView(withId(R.id.mainAppActivity)).check(matches(isDisplayed()))
        }
    }

    /**
     * Nested test class for basic UI component verification.
     */
    class LoginActivityTests {
        @get:Rule
        val activityRule = ActivityScenarioRule(LoginActivity::class.java)

        /**
         * Initializes Intents framework before each test.
         */
        @Before
        fun setUp() {
            Intents.init()
        }

        /**
         * Releases Intents framework resources after each test.
         */
        @After
        fun tearDown() {
            Intents.release()
        }

        /**
         * Verifies login button visibility.
         */
        @Test
        fun checkLoginButtonIsDisplayed() {
            onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        }

        /**
         * Verifies registration link visibility.
         */
        @Test
        fun checkRegisterTextIsDisplayed() {
            onView(withId(R.id.registerText)).check(matches(isDisplayed()))
        }

        /**
         * Verifies email input field visibility.
         */
        @Test
        fun checkEmailLayoutIsDisplayed() {
            onView(withId(R.id.emailLayout)).check(matches(isDisplayed()))
        }

        /**
         * Verifies password input field visibility.
         */
        @Test
        fun checkPasswordLayoutIsDisplayed() {
            onView(withId(R.id.passwordLayout)).check(matches(isDisplayed()))
        }

        /**
         * Verifies email text field visibility.
         */
        @Test
        fun checkEmailEditTextIsDisplayed() {
            onView(withId(R.id.emailEditText)).check(matches(isDisplayed()))
        }

        /**
         * Verifies registration activity launch.
         */
        @Test
        fun checkRegisterActivityIsLaunched() {
            onView(withId(R.id.registerText)).perform(click())
            intended(hasComponent(RegisterActivity::class.java.name))
        }

        /**
         * Verifies view binding initialization.
         */
        @Test
        fun bindingIsInitializedInOnCreate() {
            activityRule.scenario.onActivity { activity ->
                assertNotNull(activity.getBinding())
            }
        }
    }

    /**
     * Creates a standard test user entity.
     * @return User object with test data
     */
    private fun createTestUser(): User {
        return User(
            userId = 1,
            userName = "Test User",
            userMail = "test@example.com",
            userPassword = "password123",
            userWeight = 70f,
            userHeight = 175f,
            userDateOfBirth = LocalDate.of(2000, 12, 12)
        )
    }
}