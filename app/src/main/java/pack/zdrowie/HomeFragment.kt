package pack.zdrowie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import pack.zdrowie.database.DatabaseProvider
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * The main home screen fragment displaying user welcome message and advertisements.
 *
 * Key Features:
 * - Displays personalized welcome message for logged-in users
 * - Manages AdMob banner ad lifecycle
 * - Handles user data retrieval from Room database
 */
class HomeFragment : Fragment() {
    private var userId: Int = -1
    private lateinit var adView: AdView

    /**
     * Inflates the fragment layout and processes incoming arguments.
     *
     * @param inflater The LayoutInflater object
     * @param container The parent view group
     * @param savedInstanceState Saved instance state bundle
     * @return The inflated view hierarchy
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Retrieve user ID from fragment arguments
        arguments?.let {
            userId = it.getInt("UserID", -1)
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Configures the fragment UI after view creation.
     *
     * Responsibilities:
     * - Displays welcome message with user email
     * - Initializes and loads AdMob banner ad
     * - Sets up ad event listeners
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize welcome message TextView
        val welcomeTextView = view.findViewById<TextView>(R.id.welcomeText)

        // Debug toast showing user ID (remove in production)
        Toast.makeText(requireContext(), "User ID: $userId", Toast.LENGTH_SHORT).show()

        if (userId != -1) {
            // Fetch user data from database
            val appDatabase = DatabaseProvider.getDatabase(requireContext())
            val userDAO = appDatabase.userDao()

            viewLifecycleOwner.lifecycleScope.launch {
                val user = userDAO.getUserById(userId)
                welcomeTextView.text = if (user != null) {
                    getString(R.string.welcome, user.userMail) // Personalized welcome
                } else {
                    getString(R.string.welcome, "Guest") // Fallback
                }
            }
        } else {
            welcomeTextView.text = getString(R.string.welcome, "Guest")
        }

        // Initialize AdMob banner ad
        initializeAdView(view)
    }

    /**
     * Initializes and configures the AdMob banner ad.
     *
     * @param view The parent view containing the adView
     */
    private fun initializeAdView(view: View) {
        adView = view.findViewById(R.id.adView)

        // Ad event listener
        adView.adListener = object : AdListener() {
            /**
             * Called when an ad is successfully loaded.
             */
            override fun onAdLoaded() {
                // Ad successfully loaded and visible
            }

            /**
             * Called when an ad request fails.
             * @param loadAdError The error object containing failure details
             */
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle ad loading failure (e.g., log error)
            }
        }

        // Load the ad
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    /**
     * Pauses the ad view when fragment is paused.
     */
    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /**
     * Resumes the ad view when fragment is resumed.
     */
    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /**
     * Destroys the ad view when fragment is destroyed.
     */
    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}