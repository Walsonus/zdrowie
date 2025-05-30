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


class MainAppActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainAppBinding
    private var previousNavItemId = R.id.nav_home
    private var userId: Int = -1

    // Instancja StepCounterManager, odpowiedzialna za logikę licznika kroków
    lateinit var stepCounterManager: StepCounterManager
    private val ACTIVITY_RECOGNITION_PERMISSION_CODE = 100 // Kod do obsługi prośby o uprawnienia


    private val handler = Handler(Looper.getMainLooper())
    private val updateStepsRunnable = object : Runnable {
        override fun run() {
            // metoda aktualizuje kroki w aktualnie wyświetlanym fragmencie
            updateCurrentFragmentSteps()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("UserID", -1)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        stepCounterManager = StepCounterManager(this)

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

        if (savedInstanceState == null) {
            loadFragment(createHomeFragment(stepCounterManager.getSteps()), 0)
            bottomNavigationView.selectedItemId = R.id.nav_home
        }

        MobileAds.initialize(this) { initializationStatus ->
        }

        checkAndRequestActivityRecognitionPermission()
    }

    override fun onResume() {
        super.onResume()
        stepCounterManager.startListening()
        // Rozpocznij cykliczne aktualizowanie UI licznika kroków.
        handler.post(updateStepsRunnable)
    }

    override fun onPause() {
        super.onPause()
        // Konczy nasłuchiwanie sensora kroków, gdy aktywność jest wstrzymana.
        stepCounterManager.stopListening()
        // Zatrzymuje cykliczne aktualizowanie UI licznika kroków.
        handler.removeCallbacks(updateStepsRunnable)
    }


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
     * Tworzy instancję HomeFragment i przekazuje do niej UserID oraz początkową liczbę kroków.
     * @param initialSteps Początkowa liczba kroków do wyświetlenia w HomeFragment.
     * @return Nowa instancja HomeFragment.
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
     * Ładuje podany fragment do kontenera w aktywności.
     * @param fragment Fragment do załadowania.
     * @param direction Kierunek animacji (1 dla w lewo, -1 dla w prawo, 0 bez animacji).
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
     * Sprawdza, czy aplikacja ma uprawnienie ACTIVITY_RECOGNITION,
     * i prosi o nie, jeśli jest potrzebne.
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
     * Obsługa wyniku prośby o uprawnienia.
     */
    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACTIVITY_RECOGNITION_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Uprawnienie do aktywności przyznane!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Uprawnienie do aktywności odrzucone. Licznik kroków może nie działać.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Aktualizuje liczbę kroków w aktualnie wyświetlanym fragmencie,
     * jeśli jest to HomeFragment.
     */
    private fun updateCurrentFragmentSteps() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.mainAppActivity)
        if (currentFragment is HomeFragment) {
            currentFragment.updateSteps(stepCounterManager.getSteps())
        }

    }
}