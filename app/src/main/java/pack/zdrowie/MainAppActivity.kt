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

class MainAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainAppBinding
    private var previousNavItemId = R.id.nav_home
    private var userId: Int = -1 // Dodane pole do przechowywania UserID

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

    private fun getFragmentForItem(itemId: Int): Fragment {
        return when (itemId) {
            R.id.nav_home -> createHomeFragment()
            R.id.nav_gps -> GpsFragment() // Możesz też dodać przekazywanie UserID do innych fragmentów
            //R.id.nav_supplements -> SupplementsFragment()
            //R.id.nav_profile -> ProfileFragment()
            else -> createHomeFragment()
        }
    }

    // Tworzy HomeFragment z przekazanym UserID
    private fun createHomeFragment(): Fragment {
        return HomeFragment().apply {
            arguments = Bundle().apply {
                putInt("UserID", userId)
            }
        }
    }

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