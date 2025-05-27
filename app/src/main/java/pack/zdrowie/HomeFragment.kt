package pack.zdrowie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

class HomeFragment : Fragment() {
    private lateinit var adView: AdView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Znajdź widok AdView z layoutu
        adView = view.findViewById(R.id.adView)

        // Opcjonalnie ustaw listener do monitorowania zdarzeń reklamowych
        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Reklama została poprawnie załadowana
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                // Obsłuż sytuację, gdy reklama nie załadowała się – możesz np. zapisać loga
            }
        }

        // Utwórz żądanie reklamy i załaduj reklamę
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    // Zarządzanie cyklem życia AdView – ważne dla wydajności i poprawnego działania reklamy.
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