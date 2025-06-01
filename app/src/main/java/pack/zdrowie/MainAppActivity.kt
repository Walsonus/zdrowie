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
 * HomeFragment displays a welcome message to the user, step count,
 * and a Google AdMob banner advertisement.
 */
class HomeFragment : Fragment() {
    private var userId: Int = -1
    private var currentSteps: Int = 0
    private lateinit var adView: AdView
    private lateinit var stepsCountTextView: TextView
    private lateinit var welcomeTextView: TextView

    /**
     * Called to have the fragment instantiate its user interface view.
     * Retrieves userId and currentSteps from arguments bundle.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous state.
     * @return The View for the fragment's UI, or null.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let {
            userId = it.getInt("UserID", -1)
            currentSteps = it.getInt("currentSteps", 0)
        }
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    /**
     * Called immediately after onCreateView. Initializes UI elements,
     * displays welcome message, sets current step count, and loads ads.
     *
     * @param view The View returned by onCreateView.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous state.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomeTextView = view.findViewById(R.id.welcomeText)
        stepsCountTextView = view.findViewById(R.id.stepsCount)

        updateSteps(currentSteps)

        Toast.makeText(requireContext(), "UserID: $userId", Toast.LENGTH_SHORT).show()

        if (userId != -1) {
            val appDatabase = DatabaseProvider.getDatabase(requireContext())
            val userDAO = appDatabase.userDao()

            viewLifecycleOwner.lifecycleScope.launch {
                val user = userDAO.getUserById(userId)
                if (user != null) {
                    val welcomeMessage = getString(R.string.welcome, user.userMail)
                    welcomeTextView.text = welcomeMessage
                } else {
                    welcomeTextView.text = getString(R.string.welcome, "User1")
                }
            }
        } else {
            welcomeTextView.text = getString(R.string.welcome, "User")
        }

        adView = view.findViewById(R.id.adView)

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Ad successfully loaded
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                // Handle the failure
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    /**
     * Updates the displayed number of steps.
     * This method can be called from outside, e.g., by an activity.
     *
     * @param steps The new step count to be displayed.
     */
    fun updateSteps(steps: Int) {
        currentSteps = steps
        if (::stepsCountTextView.isInitialized) {
            stepsCountTextView.text = currentSteps.toString()
        }
    }

    /**
     * Called when the Fragment is no longer resumed.
     * Pauses the ad view to save resources.
     */
    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /**
     * Called when the Fragment is visible and resumed again.
     * Resumes the ad view.
     */
    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /**
     * Called when the Fragment is being destroyed.
     * Destroys the ad view to free resources.
     */
    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}
