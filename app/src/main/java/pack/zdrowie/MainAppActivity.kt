package pack.zdrowie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pack.zdrowie.databinding.ActivityMainAppBinding
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
/**
 * Main application activity handling navigation between core features.
 *
 * <p>Responsible for:
 * <ul>
 *   <li>Managing bottom navigation menu</li>
 *   <li>Switching between main app fragments</li>
 *   <li>Passing user ID to fragments</li>
 *   <li>Initializing AdMob ads</li>
 *   <li>Handling fragment transition animations</li>
 * </ul>
 */
class MainAppActivity : AppCompatActivity() {

    /** View binding instance for activity_main_app layout */
    private lateinit var binding: ActivityMainAppBinding

    /** Stores previously selected nav item ID for animation direction */
    private var previousNavItemId = R.id.nav_home

    /** Current user ID received from LoginActivity */
    private var userId: Int = -1 // Dodane pole do przechowywania UserID

    /**
     * Initializes activity components.
     * @param savedInstanceState Saved state from configuration changes
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicjalizacja view binding
        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Pobranie UserID z Intentu
        userId = intent.getIntExtra("UserID", -1)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            val direction = when {
                item.itemId > previousNavItemId -> 1
                item.itemId < previousNavItemId -> -1
                else -> 0
            }

            if (direction != 0) {
                loadFragment(getFragmentForItem(item.itemId), direction)
                previousNavItemId = item.itemId
            }
            true
        }

        // Domyślny fragment z przekazaniem UserID
        if (savedInstanceState == null) {
            loadFragment(createHomeFragment(), 0)
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        MobileAds.initialize(this) { initializationStatus -> }
    }

    /**
     * Creates fragment instance for given navigation item.
     * @param itemId Selected bottom navigation item ID
     * @return Corresponding fragment instance
     */
    private fun getFragmentForItem(itemId: Int): Fragment {
        return when (itemId) {
            R.id.nav_home -> createHomeFragment()
            R.id.nav_gps -> GpsFragment() // Możesz też dodać przekazywanie UserID do innych fragmentów
            R.id.nav_supplements -> SupplementsFragment()
            R.id.nav_profile -> ProfileFragment()
            else -> createHomeFragment()
        }
    }

    /**
     * Creates HomeFragment with user ID argument.
     * @return Configured HomeFragment instance
     */
    private fun createHomeFragment(): Fragment {
        return HomeFragment().apply {
            arguments = Bundle().apply {
                putInt("UserID", userId)
            }
        }
    }
    /**
     * Loads fragment with directional slide animation.
     * @param fragment Fragment to display
     * @param direction Animation direction (1=right, -1=left, 0=none)
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
            // Usunięto addToBackStack aby uniknąć nagromadzenia fragmentów
            commit()
        }
    }
}