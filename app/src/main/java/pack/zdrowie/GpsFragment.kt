package pack.zdrowie

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
 * Fragment handling GPS location tracking, permission management, and Google Maps integration.
 *
 * Key responsibilities:
 * - Manages location permissions
 * - Tracks device location using FusedLocationProvider
 * - Stores location history in Room database
 * - Displays real-time location and historical route on Google Maps
 * - Handles proper lifecycle management for location updates
 */
class GpsFragment : Fragment(), OnMapReadyCallback {

    /**
     * Permission launcher for requesting location access.
     * Handles both granted and denied permission scenarios.
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Log.d("GPS_PERMISSION", "Location permission granted by user")
                startLocationUpdates()
                enableMyLocationOnMap()
            } else {
                Log.d("GPS_PERMISSION", "Location permission denied by user")
                Toast.makeText(
                    requireContext(),
                    "Location permission is required for this feature!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationDao: LocationDao
    private var gMap: GoogleMap? = null
    private var currentPositionMarker: Marker? = null

    /**
     * Initializes fragment UI and location tracking components.
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
        Log.d("GPS_LIFECYCLE", "GpsFragment: onCreateView")

        // Initialize database access
        val applicationContext = requireContext().applicationContext
        locationDao = DatabaseProvider.getDatabase(applicationContext).locationDao()

        // Initialize location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // Configure location callback
        locationCallback = object : LocationCallback() {
            /**
             * Handles incoming location updates.
             * @param locationResult Contains the new Location data
             */
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationResult.lastLocation?.let { currentLocation ->
                    Log.d(
                        "GPS_CALLBACK",
                        "New location: Lat: ${currentLocation.latitude}, Lon: ${currentLocation.longitude}"
                    )

                    // Create database entity
                    val locationEntityToSave = LocationEntity(
                        latitude = currentLocation.latitude,
                        longitude = currentLocation.longitude,
                        timestamp = currentLocation.time,
                        accuracy = if (currentLocation.hasAccuracy()) currentLocation.accuracy else null
                    )

                    // Save to database in background
                    lifecycleScope.launch(Dispatchers.IO) {
                        try {
                            locationDao.insertLocation(locationEntityToSave)
                            Log.d(
                                "GPS_DATABASE",
                                "Saved location to database: $locationEntityToSave"
                            )
                        } catch (e: Exception) {
                            Log.e("GPS_DATABASE", "Error saving location to database", e)
                        }
                    }

                    // Update map UI
                    val newLatLng = LatLng(currentLocation.latitude, currentLocation.longitude)
                    updateMapWithLocation(newLatLng)
                }
            }
        }
        return inflater.inflate(R.layout.fragment_gps, container, false)
    }

    /**
     * Called after view creation. Initializes the map fragment and checks permissions.
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("GPS_LIFECYCLE", "GpsFragment: onViewCreated")

        // Initialize map
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment_container) as? SupportMapFragment
        mapFragment?.getMapAsync(this)

        // Check location permissions
        requestLocationPermissionIfNeeded()
    }

    /**
     * Called when GoogleMap is ready to be used.
     * Configures basic map settings and initial view.
     *
     * @param googleMap The GoogleMap instance
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        this.gMap = googleMap
        Log.d("GPS_MAP", "Map is ready")

        // Configure map UI
        gMap?.uiSettings?.isZoomControlsEnabled = true
        enableMyLocationOnMap()

        // Set initial view to Poland
        val polandCenter = LatLng(52.0, 19.0)
        gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(polandCenter, 6f))
    }

    /**
     * Checks and requests location permission if not already granted.
     */
    private fun requestLocationPermissionIfNeeded() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("GPS_PERMISSION", "Location permission not granted. Requesting...")
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            Log.d("GPS_PERMISSION", "Location permission already granted")
        }
    }

    /**
     * Starts receiving location updates with high accuracy settings.
     * Requires location permission to be granted first.
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.w("GPS_UPDATES", "Attempt to startLocationUpdates without permission. Aborting.")
            return
        }

        // Configure location request
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
            Log.d("GPS_UPDATES", "Started location updates listening.")
        } catch (e: SecurityException) {
            Log.e("GPS_UPDATES", "SecurityException when starting location updates.", e)
        }
    }

    /**
     * Stops receiving location updates to conserve battery.
     * Should be called when the fragment is no longer active.
     */
    private fun stopLocationUpdates() {
        if (::fusedLocationClient.isInitialized && ::locationCallback.isInitialized) {
            try {
                fusedLocationClient.removeLocationUpdates(locationCallback)
                Log.d("GPS_UPDATES", "Stopped location updates.")
            } catch (e: Exception) {
                Log.e("GPS_UPDATES", "Error stopping location updates.", e)
            }
        } else {
            Log.d("GPS_UPDATES", "Cannot stop updates: components not initialized.")
        }
    }

    /**
     * Updates the map with new location coordinates.
     * @param latLng The new location coordinates
     */
    private fun updateMapWithLocation(latLng: LatLng) {
        if (gMap != null) {
            // Update or create marker
            if (currentPositionMarker == null) {
                currentPositionMarker = gMap?.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title("My Current Position")
                )
            } else {
                currentPositionMarker?.position = latLng
            }

            // Move camera to new position
            gMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        }
    }

    /**
     * Enables "My Location" layer on the map if permissions are granted.
     */
    @SuppressLint("MissingPermission")
    private fun enableMyLocationOnMap() {
        if (gMap == null) {
            Log.w("GPS_MAP", "Attempt to enable MyLocationLayer before map is ready.")
            return
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap?.isMyLocationEnabled = true
            gMap?.uiSettings?.isMyLocationButtonEnabled = true
            Log.d("GPS_MAP", "Enabled My Location layer.")
        } else {
            Log.w("GPS_MAP", "Cannot enable My Location layer - missing permissions.")
        }
    }

    /**
     * Called when fragment is paused. Stops location updates to conserve battery.
     */
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
        Log.d("GPS_LIFECYCLE", "GpsFragment: onPause() - Stopped location updates.")
    }
}