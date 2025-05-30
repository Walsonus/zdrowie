package pack.zdrowie

import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import pack.zdrowie.databinding.ActivityMainAppBinding
import pack.zdrowie.stepCounter.StepCounterManager

/**
 * The main activity of the application, responsible for managing fragment navigation,
 * step counter initialization, and permission handling.
 */
class MainAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainAppBinding
    private var previousNavItemId = R.id.nav_home
    private var userId: Int = -1

    /**
     * Instance of [StepCounterManager], responsible for the step counting logic.
     */
    lateinit var stepCounterManager: StepCounterManager
    private val ACTIVITY_RECOGNITION_PERMISSION_CODE = 100 // Code for handling permission requests

    private val handler = Handler(Looper.getMainLooper())
    private val updateStepsRunnable = object : Runnable {
        override fun run() {
            // This method updates the steps in the currently displayed fragment.
            updateCurrentFragmentSteps()
            // Schedules the next execution after 1 second.
            handler.postDelayed(this, 1000)
        }
    }

    /**
     * Called when the activity is first created. Initializes views,
     * manages navigation, the step sensor, and requests permissions.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     * previously being shut down then this Bundle contains the data it most
     * recently supplied in `onSaveInstanceState(Bundle)`. Otherwise, it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieves UserID from the Intent.
        userId = intent.getIntExtra("UserID", -1)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Initializes the step counter manager.
        stepCounterManager = StepCounterManager(this)

        // Sets the listener for the bottom navigation.
        bottomNavigationView.setOnItemSelectedListener { item ->
            val direction = when {
                item.itemId > previousNavItemId -> 1 // Slide left
                item.itemId < previousNavItemId -> -1 // Slide right
                else -> 0 // No change
            }

            if (direction != 0) {
                loadFragment(getFragmentForItem(item.itemId), direction)
                previousNavItemId = item.itemId
            }
            true
        }

        // Loads the initial fragment (HomeFragment) on first launch.
        if (savedInstanceState == null) {
            loadFragment(createHomeFragment(stepCounterManager.getSteps()), 0)
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        // Initializes the Mobile Ads SDK.
        MobileAds.initialize(this) { initializationStatus ->
            // You can add logic after ad initialization.
        }

        // Checks and requests permission for physical activity recognition.
        checkAndRequestActivityRecognitionPermission()
    }

    /**
     * Called when the activity becomes visible to the user.
     * Starts listening for step sensor events and periodically updating the UI.
     */
    override fun onResume() {
        super.onResume()
        stepCounterManager.startListening()
        // Starts periodically updating the step counter UI.
        handler.post(updateStepsRunnable)
    }

    /**
     * Called when the activity is no longer in the foreground.
     * Stops listening for step sensor events and stops UI updates.
     */
    override fun onPause() {
        super.onPause()
        // Stops listening to the step sensor when the activity is paused.
        stepCounterManager.stopListening()
        // Stops periodically updating the step counter UI.
        handler.removeCallbacks(updateStepsRunnable)
    }

    /**
     * Returns a fragment instance based on the selected navigation item.
     *
     * @param itemId The ID of the selected navigation item.
     * @return An instance of the corresponding fragment.
     */
    private fun getFragmentForItem(itemId: Int): Fragment {
        return when (itemId) {
            R.id.nav_home -> createHomeFragment(stepCounterManager.getSteps())
            R.id.nav_gps -> GpsFragment()
            R.id.nav_supplements -> SupplementsFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> createHomeFragment(stepCounterManager.getSteps())
        }
    }

    /**
     * Creates an instance of [HomeFragment] and passes the UserID and initial step count to it.
     * @param initialSteps The initial number of steps to display in HomeFragment.
     * @return A new instance of HomeFragment.
     */
    private fun createHomeFragment(initialSteps: Int): HomeFragment {
        return HomeFragment().apply {
            arguments = Bundle().apply {
                putInt("UserID", userId)
                putInt("currentSteps", initialSteps)
            }
        }
    }

    /**
     * Loads the given fragment into the activity's container with animations.
     * @param fragment The fragment to load.
     * @param direction The animation direction (1 for left, -1 for right, 0 for no animation).
     */
    private fun loadFragment(fragment: Fragment, direction: Int) {
        val enterAnim = when (direction) {
            1 -> R.anim.slide_in_left
            -1 -> R.anim.slide_in_right
            else -> 0
        }
        val exitAnim = when (direction) {
            1 -> R.anim.slide_out_right
            -1 -> R.anim.slide_out_left
            else -> 0
        }

        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(enterAnim, exitAnim)
            replace(R.id.mainAppActivity, fragment)
            commit()
        }
    }

    /**
     * Checks if the application has the `ACTIVITY_RECOGNITION` permission,
     * and requests it if necessary.
     */
    private fun checkAndRequestActivityRecognitionPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION),
                ACTIVITY_RECOGNITION_PERMISSION_CODE)
        }
    }

    /**
     * Handles the result of a permission request.
     * Called after the user has responded to a permission request.
     *
     * @param requestCode The request code passed in `requestPermissions`.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     * which is either [PackageManager.PERMISSION_GRANTED] or [PackageManager.PERMISSION_DENIED]. Never null.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Activity permission granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Activity permission denied. Step counter may not work.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Updates the step count in the currently displayed fragment,
     * if it is a [HomeFragment].
     */
    private fun updateCurrentFragmentSteps() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainAppActivity)
        if (currentFragment is HomeFragment) {
            currentFragment.updateSteps(stepCounterManager.getSteps())
        }
    }
}