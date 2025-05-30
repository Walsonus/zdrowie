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

class HomeFragment : Fragment() {
    private var userId: Int = -1
    private var currentSteps: Int = 0
    private lateinit var adView: AdView
    private lateinit var stepsCountTextView: TextView
    private lateinit var welcomeTextView: TextView

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        welcomeTextView = view.findViewById(R.id.welcomeText)
        stepsCountTextView = view.findViewById(R.id.stepsCount)

        updateSteps(currentSteps)

        val toast = Toast.makeText(requireContext(), "UserID: $userId", Toast.LENGTH_SHORT)
        toast.show()

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
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    /**
     * Publiczna metoda do aktualizacji wyświetlanej liczby kroków.
     * Wywoływana przez MainAppActivity, gdy liczba kroków się zmienia.
     */
    fun updateSteps(steps: Int) {
        currentSteps = steps
        if (::stepsCountTextView.isInitialized) {
            stepsCountTextView.text = currentSteps.toString()
        }
    }

    override fun onPause() {
        adView.pause()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }
}