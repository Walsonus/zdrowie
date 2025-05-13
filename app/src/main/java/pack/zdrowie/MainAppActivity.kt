package pack.zdrowie

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import pack.zdrowie.databinding.ActivityMainAppBinding
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainAppActivity : AppCompatActivity() {

    private var previousNavItemId = R.id.nav_home // default menu selection

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_app)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        bottomNavigationView.setOnItemSelectedListener { item ->
            val direction = when {
                item.itemId > previousNavItemId -> 1 // right transition
                item.itemId < previousNavItemId -> -1 // left transition
                else -> 0 // no transition when same item selected
            }

            if (direction != 0) {
                loadFragment(getFragmentForItem(item.itemId), direction)
                previousNavItemId = item.itemId
            }
            true
        }

        //default fragment with default menu selection
        if (savedInstanceState == null) {
            loadFragment(HomeFragment(), 0)
            bottomNavigationView.selectedItemId = R.id.nav_home
        }
    }

    private fun getFragmentForItem(itemId: Int): Fragment {
        return when (itemId) {
            R.id.nav_home -> HomeFragment()
            R.id.nav_gps -> GpsFragment()
            //R.id.nav_supplements -> SupplementsFragment()
            //R.id.nav_profile -> ProfileFragment()
            else -> HomeFragment()
        }
    }


    //animation picker for correct transition
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

        val popEnterAnim = when (direction) {
            1 -> R.anim.slide_in_left
            -1 -> R.anim.slide_in_right
            else -> 0
        }

        val popExitAnim = when (direction) {
            1 -> R.anim.slide_out_right
            -1 -> R.anim.slide_out_left
            else -> 0
        }

        supportFragmentManager.beginTransaction().apply {
            setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
            replace(R.id.mainAppActivity, fragment)
            addToBackStack(null)
            commit()
        }
    }
}