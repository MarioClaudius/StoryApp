package android.marc.com.storyapp.activity.login

import android.marc.com.storyapp.JsonConverter
import android.marc.com.storyapp.helper.EspressoIdlingResource
import android.marc.com.storyapp.R
import android.marc.com.storyapp.activity.main.MainActivity
import android.marc.com.storyapp.api.ApiConfig
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest {

    private val testEmail = "testforuitest@test.com"
    private val testPassword = "testforuitest"
    private val wrongTestPassword = "adsaadsdsadas"

    private val mockWebServer = MockWebServer()

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp() {
        mockWebServer.start(8080)
        ApiConfig().BASE_URL = "http://127.0.0.1:8080/"
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun login_failed() {
        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_email)).perform(typeText(testEmail))
        onView(withId(R.id.ed_login_password)).perform(typeText(wrongTestPassword))
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())

        val mockErrorResponse = MockResponse()
            .setResponseCode(401)
            .setBody(JsonConverter.readStringFromFile("error_login_response.json"))
        mockWebServer.enqueue(mockErrorResponse)

        onView(withText(R.string.error_dialog_title))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
        onView(withId(android.R.id.button1)).perform(click())
    }

    @Test
    fun login_and_logout_success() {
        Intents.init()

        onView(withId(R.id.ed_login_email)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_password)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_login_email)).perform(typeText(testEmail))
        onView(withId(R.id.ed_login_password)).perform(typeText(testPassword))
        closeSoftKeyboard()
        onView(withId(R.id.btn_login)).perform(click())

        val mockSuccessResponse = MockResponse()
            .setResponseCode(200)
            .setBody(JsonConverter.readStringFromFile("success_login_response.json"))
        mockWebServer.enqueue(mockSuccessResponse)

        onView(withText(R.string.success_dialog_title))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(android.R.id.button1)).perform(click())

        intended(hasComponent(MainActivity::class.java.name))
        onView(withId(R.id.logout)).check(matches(isDisplayed())).perform(click())

        intended(hasComponent(LoginActivity::class.java.name))
        onView(withId(R.id.img_camera_logo_login)).check(matches(isDisplayed()))

        Intents.release()
    }
}