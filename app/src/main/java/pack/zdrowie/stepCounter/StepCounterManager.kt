package pack.zdrowie.stepCounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import android.content.SharedPreferences

/**
 * Manages step counting using the TYPE_STEP_COUNTER sensor available on Android devices.
 * This class implements [SensorEventListener] to receive sensor events.
 * The step count is stored locally in [SharedPreferences].
 *
 * @param context The application context, required to access system services and SharedPreferences.
 */
class StepCounterManager(private val context: Context) : SensorEventListener {

    private var sensorManager: SensorManager? = null
    private var stepCounterSensor: Sensor? = null
    private var totalSteps = 0
    private var previousTotalSteps = 0f
    private val PREFS_NAME = "step_counter_prefs"
    private val KEY_PREVIOUS_TOTAL_STEPS = "previous_total_steps"

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    init {
        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCounterSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        if (stepCounterSensor == null) {
            Toast.makeText(context, "Step counter sensor is not available on this device.", Toast.LENGTH_LONG).show()
        }
        loadPreviousTotalSteps()
    }

    /**
     * Starts listening for step sensor events.
     * Registers the [SensorEventListener] for the TYPE_STEP_COUNTER sensor.
     */
    fun startListening() {
        stepCounterSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /**
     * Stops listening for step sensor events.
     * Unregisters the [SensorEventListener] and saves the current previousTotalSteps value.
     */
    fun stopListening() {
        sensorManager?.unregisterListener(this)
        savePreviousTotalSteps()
    }

    /**
     * Called when sensor values change.
     * Calculates the number of steps since the last counter reset and updates [totalSteps].
     *
     * @param event The sensor event containing the new data.
     */
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                // The TYPE_STEP_COUNTER sensor returns the total number of steps since the last device reboot.
                val currentSteps = it.values[0]

                if (previousTotalSteps == 0f) {
                    // If this is the first reading, set previousTotalSteps.
                    previousTotalSteps = currentSteps
                }

                totalSteps = (currentSteps - previousTotalSteps).toInt()
                // You can update UI here, e.g., TextView
                // For example: updateStepsUI(totalSteps)
                println("Steps: $totalSteps") // For debugging purposes

                // You might want to save currentSteps to SharedPreferences
                // to calculate steps since the last app launch.
                // For now, we focus on steps since the last counter start.
            }
        }
    }

    /**
     * Called when the accuracy of a sensor changes.
     * For the step counter sensor, this usually doesn't require specific handling.
     *
     * @param sensor The sensor whose accuracy changed.
     * @param accuracy The new accuracy of the sensor.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // This method is called when the accuracy of the sensor changes.
        // For the step counter sensor, we usually don't need to handle it.
    }

    /**
     * Loads the previous total steps from [SharedPreferences].
     * Used to calculate steps since the last counter launch.
     */
    private fun loadPreviousTotalSteps() {
        previousTotalSteps = sharedPreferences.getFloat(KEY_PREVIOUS_TOTAL_STEPS, 0f)
    }

    /**
     * Saves the current previousTotalSteps value to [SharedPreferences].
     */
    private fun savePreviousTotalSteps() {
        val editor = sharedPreferences.edit()
        editor.putFloat(KEY_PREVIOUS_TOTAL_STEPS, previousTotalSteps)
        editor.apply()
    }

    /**
     * Returns the currently counted number of steps.
     *
     * @return The total number of steps since the last "reset" of the counter.
     */
    fun getSteps(): Int {
        return totalSteps
    }

    /**
     * Resets the internal step counter.
     * Sets [previousTotalSteps] to the current sensor value, effectively "zeroing" the counter.
     * Note that the system sensor still counts steps since the device reboot.
     */
    fun resetSteps() {
        // To reset steps, we need to reset previousTotalSteps
        // to the current sensor value, so subsequent readings will start from zero.
        // Unfortunately, we cannot directly "reset" the system step counter sensor.
        // What we do is "reset" our internal representation.
        if (stepCounterSensor != null) {
            // Since TYPE_STEP_COUNTER counts steps from device reboot,
            // an effective reset involves setting previousTotalSteps to the sensor's current value.
            // Then, subsequent steps will be counted from that point.
            // In more advanced applications, you might want to store the date of the last reset.
            // For this example, we simply reset the internal value.
            previousTotalSteps = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
                // We need to wait for a new sensor event to get the current value.
                // For now, we set it to 0, and the actual reset will occur on the first reading.
                0f // Set to 0, and the actual reset will happen on the first reading.
            } ?: 0f // If sensor is not available, previous steps are 0.

            // Additionally, to ensure this works, you might want to force a re-read of the sensor.
            // The simplest way is to re-register the sensor after resetting previousTotalSteps.
            stopListening()
            savePreviousTotalSteps()
            totalSteps = 0
            startListening()
        }
    }
}