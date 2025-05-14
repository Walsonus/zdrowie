package pack.zdrowie // Upewnij się, że nazwa pakietu jest poprawna

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
import kotlinx.coroutines.withContext
import pack.zdrowie.database.dao.LocationDao
import pack.zdrowie.database.DatabaseProvider
import pack.zdrowie.database.entities.LocationEntity
import com.google.android.gms.location.LocationRequest
// Importy dla Google Maps
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.LatLngBounds
import android.graphics.Color

/**
 * Fragment odpowiedzialny za obsługę GPS, proszenie o uprawnienia,
 * pobieranie lokalizacji, zapisywanie jej do lokalnej bazy danych Room
 * oraz wyświetlanie mapy Google z aktualną pozycją i historią trasy.
 */
class GpsFragment : Fragment(), OnMapReadyCallback {

    // ... (bez zmian: requestPermissionLauncher, fusedLocationClient, locationCallback, locationDao, gMap, currentPositionMarker) ...
    /**
     * Launcher do obsługi prośby o uprawnienie do lokalizacji.
     * Wynik (przyznanie lub odmowa) jest obsługiwany w callbacku.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("GPS_PERMISSION", "Uprawnienie przyznane przez użytkownika.")
                startLocationUpdates()
                enableMyLocationOnMap()
            } else {
                Log.d("GPS_PERMISSION", "Uprawnienie odrzucone przez użytkownika.")
                Toast.makeText(
                    requireContext(),
                    "Uprawnienie do lokalizacji jest wymagane do działania tej funkcji!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    /** Klient usług lokalizacyjnych Google Play do pobierania danych GPS. */
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    /** Callback do odbierania aktualizacji lokalizacji. */
    private lateinit var locationCallback: LocationCallback
    /** DAO do interakcji z tabelą lokalizacji w bazie Room. */
    private lateinit var locationDao: LocationDao
    /** Obiekt mapy Google, gdy jest już gotowa. */
    private var gMap: GoogleMap? = null
    /** Marker wskazujący aktualną pozycję użytkownika na mapie. */
    private var currentPositionMarker: Marker? = null


    /**
     * Wywoływane przy tworzeniu widoku fragmentu.
     * Inicjalizuje DAO, klienta lokalizacji oraz callback lokalizacji.
     * Zwraca napompowany layout dla tego fragmentu.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("GPS_LIFECYCLE", "GpsFragment: onCreateView")

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

                    val nowyLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    updateMapWithLocation(nowyLatLng)
                }
            }
        }
        return inflater.inflate(R.layout.fragment_gps, container, false)
    }

    /**
     * Wywoływane tuż po tym, jak onCreateView() zakończy działanie.
     * To dobre miejsce na inicjalizację komponentów UI, ładowanie mapy
     * oraz pierwsze sprawdzenie uprawnień.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GPS_LIFECYCLE", "GpsFragment: onViewCreated")

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_container) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        requestLocationPermissionIfNeeded()
    }

    /**
     * Wywoływana, gdy mapa jest w pełni załadowana i gotowa do użycia.
     * @param googleMap Instancja [GoogleMap], która jest gotowa.
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.gMap = googleMap
        Log.d("GPS_MAP", "Mapa jest gotowa.")

        gMap?.uiSettings?.isZoomControlsEnabled = true
        enableMyLocationOnMap()

        val polandCenter = LatLng(52.0, 19.0)
        gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(polandCenter, 6f))

        // -------- 👇 NOWY/ZMODYFIKOWANY KOD: Wyświetlanie historii trasy 👇 -----------
        Log.d("GPS_MAP_HISTORY", "Próba załadowania historii trasy...")
        lifecycleScope.launch {
            val locationHistoryList = withContext(Dispatchers.IO) {
                locationDao.getAllLocations() // Pobiera listę posortowaną od najnowszej do najstarszej
            }

            val chronologicalPath = locationHistoryList.reversed() // Odwracamy, aby rysować od najstarszej
            Log.d("GPS_MAP_HISTORY", "Pobrano ${chronologicalPath.size} punktów historii.")

            if (chronologicalPath.size > 1) {
                val polylineOptions = PolylineOptions()
                    .color(Color.BLUE) // Użyj android.graphics.Color.BLUE
                    .width(10f)
                    .clickable(true) // Opcjonalnie

                val boundsBuilder = LatLngBounds.Builder()

                for (locationEntity in chronologicalPath) {
                    val point = LatLng(locationEntity.latitude, locationEntity.longitude)
                    polylineOptions.add(point)
                    boundsBuilder.include(point)
                }

                withContext(Dispatchers.Main) {
                    gMap?.addPolyline(polylineOptions)
                    Log.d("GPS_MAP_HISTORY", "Narysowano Polyline na mapie.")

                    try {
                        val bounds = boundsBuilder.build()
                        gMap?.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100)) // 100 to padding
                        Log.d("GPS_MAP_HISTORY", "Kamera dopasowana do granic trasy.")
                    } catch (e: IllegalStateException) {
                        Log.e("GPS_MAP_HISTORY", "Nie można zbudować granic dla kamery: ${e.message}")
                        chronologicalPath.firstOrNull()?.let { firstPoint ->
                            gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(firstPoint.latitude, firstPoint.longitude), 16f))
                        }
                    }
                }
            } else {
                Log.d("GPS_MAP_HISTORY", "Nie znaleziono wystarczającej liczby punktów (${chronologicalPath.size}) do narysowania historii trasy.")
            }
        }
        // -------- 👆 KONIEC NOWEGO/ZMODYFIKOWANEGO KODU 👆 -----------
    }

    /**
     * Sprawdza, czy uprawnienie do lokalizacji (ACCESS_FINE_LOCATION) jest przyznane.
     * Jeśli nie, prosi o nie użytkownika za pomocą [requestPermissionLauncher].
     * Jeśli tak, uruchamia aktualizacje lokalizacji i włącza warstwę "Moja Lokalizacja" na mapie.
     */
    private fun requestLocationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("GPS_PERMISSION", "Uprawnienie do lokalizacji nie jest przyznane. Proszę o nie...")
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("GPS_PERMISSION", "Uprawnienie do lokalizacji jest już przyznane.")
            startLocationUpdates()
            enableMyLocationOnMap()
        }
    }

    /**
     * Rozpoczyna proces nasłuchiwania na aktualizacje lokalizacji od [FusedLocationProviderClient].
     * Ta funkcja powinna być wywoływana tylko po upewnieniu się, że uprawnienia są przyznane.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("GPS_UPDATES", "Próba uruchomienia startLocationUpdates bez uprawnień. Przerywam.")
            return
        }

        val locationRequest =
            LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L).apply {
                setMinUpdateIntervalMillis(5000L)
                setGranularity(Granularity.GRANULARITY_PERMISSION_LEVEL)
                setWaitForAccurateLocation(true)
            }.build()
        try {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
            Log.d("GPS_UPDATES", "Uruchomiono nasłuchiwanie aktualizacji lokalizacji.")
        } catch (e: SecurityException) {
            Log.e("GPS_UPDATES", "Błąd bezpieczeństwa (SecurityException) przy uruchamianiu aktualizacji.", e)
        }
    }

    /**
     * Zatrzymuje nasłuchiwanie na aktualizacje lokalizacji.
     * Ważne, aby wywołać tę metodę, gdy fragment nie jest już aktywny, w celu oszczędzania baterii.
     */
    private fun stopLocationUpdates() {
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                Log.d("GPS_UPDATES", "Zatrzymano nasłuchiwanie aktualizacji lokalizacji.")
            } catch (e: Exception) {
                Log.e("GPS_UPDATES", "Błąd podczas zatrzymywania aktualizacji lokalizacji.", e)
            }
        } else {
            Log.d("GPS_UPDATES", "Nie można zatrzymać aktualizacji: komponenty nie zainicjalizowane.")
        }
    }

    /**
     * Wywoływane, gdy fragment jest pauzowany. Zatrzymujemy aktualizacje lokalizacji.
     */
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        Log.d("GPS_LIFECYCLE", "GpsFragment: onPause() - Zatrzymano aktualizacje lokalizacji.")
    }

    /**
     * Aktualizuje mapę o nową pozycję: przesuwa kamerę i aktualizuje/dodaje marker.
     * @param latLng Nowe współrzędne [LatLng].
     */
    private fun updateMapWithLocation(latLng: LatLng) {
        if (gMap != null) {
            if (currentPositionMarker == null) {
                currentPositionMarker = gMap?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("Moja Aktualna Pozycja")
                )
            } else {
                currentPositionMarker?.position = latLng
            }
            gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    /**
     * Sprawdza uprawnienia i jeśli są przyznane, włącza warstwę "Moja Lokalizacja" na mapie
     * oraz przycisk do centrowania na tej lokalizacji.
     * Powinna być wywoływana po tym, jak mapa (`gMap`) jest gotowa.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocationOnMap() {
        if (gMap == null) {
            Log.w("GPS_MAP", "Próba włączenia MyLocationLayer, ale mapa (gMap) nie jest jeszcze gotowa.")
            return
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap?.isMyLocationEnabled = true
            gMap?.uiSettings?.isMyLocationButtonEnabled = true
            Log.d("GPS_MAP", "Warstwa 'Moja Lokalizacja' włączona.")
        } else {
            Log.w("GPS_MAP", "Nie można włączyć warstwy 'Moja Lokalizacja' - brak uprawnień.")
        }
    }
}

