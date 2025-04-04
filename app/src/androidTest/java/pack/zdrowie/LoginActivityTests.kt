package pack.zdrowie

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase.assertNotNull
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test



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