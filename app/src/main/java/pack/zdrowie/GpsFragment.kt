package pack.zdrowie

 ZDR-19
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.util.Log

import android.os.Bundle
 master
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
 ZDR-19
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.Granularity
import com.google.android.gms.location.Priority
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import pack.zdrowie.database.dao.LocationDao
import pack.zdrowie.database.DatabaseProvider
import pack.zdrowie.database.entities.LocationEntity
import com.google.android.gms.location.LocationRequest


class GpsFragment : Fragment() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("GPS_PERMISSION", "Uprawnienie przyznane przez użytkownika.")
                startLocationUpdates()
            } else {
                Log.d("GPS_PERMISSION", "Uprawnienie odrzucone przez użytkownika.")
                Toast.makeText(
                    requireContext(),
                    "Uprawnienie do lokalizacji jest wymagane do działania tej funkcji!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }


    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationDao: LocationDao



class GpsFragment : Fragment() {
 master
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
 ZDR-19

        val applicationContext = requireContext().applicationContext
        locationDao = DatabaseProvider.getDatabase(applicationContext).locationDao()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)


                locationResult.lastLocation?.let { currentLocation ->
                    Log.d(
                        "GPS_CALLBACK",
                        "Otrzymano lokalizację: Lat: ${currentLocation.latitude}, Lon: ${currentLocation.longitude}"
                    )

                    val locationEntityToSave = LocationEntity(
                        latitude = currentLocation.latitude,
                        longitude = currentLocation.longitude,
                        timestamp = currentLocation.time,
                        accuracy = if (currentLocation.hasAccuracy()) currentLocation.accuracy else null
                    )

                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            locationDao.insertLocation(locationEntityToSave)
                            Log.d(
                                "GPS_DATABASE",
                                "Zapisano lokalizację do bazy: $locationEntityToSave"
                            )
                        } catch (e: Exception) {
                            Log.e("GPS_DATABASE", "Błąd zapisu lokalizacji do bazy", e)
                        }
                    }
                }
            }
        }



        return inflater.inflate(R.layout.fragment_gps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestLocationPermissionIfNeeded()
    }

    private fun requestLocationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(
                "GPS_PERMISSION",
                "Uprawnienie do lokalizacji nie jest przyznane. Proszę o nie..."
            )
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("GPS_PERMISSION", "Uprawnienie do lokalizacji jest już przyznane.")
            startLocationUpdates()
        }
    }

    private fun startLocationUpdates() {
        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).apply {
                setMinUpdateIntervalMillis(5000L)

                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()


        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            try {
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
                Log.d("GPS_UPDATES", "Uruchomiono nasłuchiwanie aktualizacji lokalizacji.")
            } catch (e: SecurityException) {
                Log.e(
                    "GPS_UPDATES",
                    "Błąd bezpieczeństwa (SecurityException) przy uruchamianiu aktualizacji.",
                    e
                )
            }
        } else {
            Log.w(
                "GPS_UPDATES",
                "Próba uruchomienia aktualizacji lokalizacji bez uprawnień (wewnątrz startLocationUpdates). To nie powinno się zdarzyć."
            )
        }
    }

    private fun stopLocationUpdates() {
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                Log.d("GPS_UPDATES", "Zatrzymano nasłuchiwanie aktualizacji lokalizacji.")
            } catch (e: Exception) {
                Log.e("GPS_UPDATES", "Błąd podczas zatrzymywania aktualizacji lokalizacji.", e)
            }
        } else {
            Log.d(
                "GPS_UPDATES",
                "Nie można zatrzymać aktualizacji: fusedLocationClient lub locationCallback nie są jeszcze zainicjalizowane."
            )
        }
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        Log.d("GPS_LIFECYCLE", "GpsFragment: onPause() - Zatrzymano aktualizacje lokalizacji.")
    }

        return inflater.inflate(R.layout.fragment_gps, container, false)
    }
 master
}