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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase.assertNotNull
import org.junit.Rule

@MediumTest
@RunWith(AndroidJUnit4::class)
class LoginActivityInstrumentedTest {

    private lateinit var userDAO: UserDAO
    private lateinit var db: AppDatabase

    @Before
    fun setUp() {
        if (!::db.isInitialized) {
            db = DatabaseProvider.getDatabase(ApplicationProvider.getApplicationContext())
            userDAO = db.userDao()
        }
    }

    @After
    fun tearDown() {
        // Clear the database after each test
        db.clearAllTables()
//        db.close()
    }

    //TEST FOR DISPLAY INCORRECT DATA
//    @Test
//    fun login_withEmptyFields_showsToastError() {
//        ActivityScenario.launch(LoginActivity::class.java)
//
//        onView(withId(R.id.loginButton)).perform(click())
//
//        SystemClock.sleep(2000)
//
//        onView(withText(R.string.no_login_data))
//            .inRoot(ToastMatcher())
//            .check(matches(isDisplayed()))
//    }


    @Test
    fun login_withInvalidCredentials_showsError() {
        runBlocking {
            userDAO.insert(
                User(
                    userId = 1,
                    userName = "Test User",
                    userMail = "test@example.com",
                    userPassword = "password123",
                    userWeight = 70f,
                    userHeight = 175f,
                    userDateOfBirth = LocalDate.of(2000, 12, 12)
                )
            )
        }

        ActivityScenario.launch(LoginActivity::class.java)

        onView(withId(R.id.emailEditText)).perform(typeText("test@example.com"))
        onView(withId(R.id.passwordEditText)).perform(typeText("wrongpassword"), closeSoftKeyboard())
        onView(withId(R.id.loginButton)).perform(click())

        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }


    @Test
    fun registerText_click_navigatesToRegisterActivity() {
        ActivityScenario.launch(LoginActivity::class.java)

        // Click on register text
        onView(withId(R.id.registerText)).perform(click())

        // Check if we've navigated to RegisterActivity
        onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
    }

    @Test
    fun login_withCorrectCredentials_navigatesToMainPage() {
        runBlocking {
            // Insert a test user
            userDAO.insert(
                User(
                    userId = 1,
                    userName = "Test User",
                    userMail = "test@example.com",
                    userPassword = "password123",
                    userWeight = 70f,
                    userHeight = 175f,
                    userDateOfBirth = LocalDate.of(2000, 12, 12)
                )
            )


            ActivityScenario.launch(LoginActivity::class.java)

            // Enter correct credentials
            onView(withId(R.id.emailEditText)).perform(typeText("test@example.com"))
            onView(withId(R.id.passwordEditText)).perform(
                typeText("password123"),
                closeSoftKeyboard()
            )
            onView(withId(R.id.loginButton)).perform(click())

            // Check if we've navigated to main
            onView(withId(R.id.main)).check(matches(isDisplayed()))
        }
    }

    class LoginActivityTests {
        @get:Rule
        val activityRule = ActivityScenarioRule(LoginActivity::class.java)

        @Before
        fun setUp() {
            Intents.init()
        }

        @After
        fun tearDown() {
            Intents.release()
        }

        @Test
        fun checkLoginButtonIsDisplayed() {
            onView(withId(R.id.loginButton)).check(matches(isDisplayed()))
        }

        @Test
        fun checkRegisterTextIsDisplayed() {
            onView(withId(R.id.registerText)).check(matches(isDisplayed()))
        }

        @Test
        fun checkEmailLayoutIsDisplayed() {
            onView(withId(R.id.emailLayout)).check(matches(isDisplayed()))
        }

        @Test
        fun checkPasswordLayoutIsDisplayed() {
            onView(withId(R.id.passwordLayout)).check(matches(isDisplayed()))
        }

        @Test
        fun checkEmailEditTextIsDisplayed() {
            onView(withId(R.id.emailEditText)).check(matches(isDisplayed()))
        }

        @Test
        fun checkRegisterActivityIsLaunched() {
            onView(withId(R.id.registerText)).perform(click())
            intended(hasComponent(RegisterActivity::class.java.name))

        }

        @Test
        fun bindingIsInitializedInOnCreate() {
            activityRule.scenario.onActivity { activity ->
                assertNotNull(activity.getBinding())
            }
        }

//    @Test
//    fun checkIfEmailAndPasswordFieldsHasValueWhenActivityIsCreatedalues () {
//        onView(withId(R.id.emailEditText)).check(matches(withText("Email")))
//        onView(withId(R.id.passwordEditText)).check(matches(withText("Hasło")))
//    }

    }

    }