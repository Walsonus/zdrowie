package pack.zdrowie.stepCounter

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import android.content.SharedPreferences

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
            Toast.makeText(context, "Sensor kroków nie jest dostępny na tym urządzeniu.", Toast.LENGTH_LONG).show()
        }
        loadPreviousTotalSteps()
    }

    fun startListening() {
        stepCounterSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
        savePreviousTotalSteps()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            if (it.sensor.type == Sensor.TYPE_STEP_COUNTER) {
                // Sensor TYPE_STEP_COUNTER zwraca całkowitą liczbę kroków od momentu ostatniego restartu urządzenia
                val currentSteps = it.values[0]

                if (previousTotalSteps == 0f) {
                    // Jeśli to pierwsze odczytywanie, ustaw previousTotalSteps
                    previousTotalSteps = currentSteps
                }

                totalSteps = (currentSteps - previousTotalSteps).toInt()
                // Tutaj możesz aktualizować UI, np. TextView
                // przykładowo: updateStepsUI(totalSteps)
                println("Kroki: $totalSteps") // Do celów debugowania

                // Możesz chcieć zapisać currentSteps do SharedPreferences,
                // żeby obliczyć kroki od ostatniego uruchomienia aplikacji.
                // Na razie skupimy się na krokach od ostatniego uruchomienia licznika.
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Ta metoda jest wywoływana, gdy zmienia się dokładność sensora.
        // W przypadku sensora kroków zazwyczaj nie musimy jej obsługiwać.
    }

    private fun loadPreviousTotalSteps() {
        previousTotalSteps = sharedPreferences.getFloat(KEY_PREVIOUS_TOTAL_STEPS, 0f)
    }

    private fun savePreviousTotalSteps() {
        val editor = sharedPreferences.edit()
        editor.putFloat(KEY_PREVIOUS_TOTAL_STEPS, previousTotalSteps)
        editor.apply()
    }

    fun getSteps(): Int {
        return totalSteps
    }

    fun resetSteps() {
        // Aby zresetować kroki, musimy zresetować previousTotalSteps
        // do aktualnej wartości sensora, wtedy kolejne odczyty będą zaczynać się od zera.
        // Niestety, nie możemy bezpośrednio "zresetować" sensora kroków systemowo.
        // To, co robimy, to "resetujemy" naszą wewnętrzną reprezentację.
        if (stepCounterSensor != null) {
            // Ponieważ sensor TYPE_STEP_COUNTER liczy kroki od restartu urządzenia,
            // efektywny reset polega na ustawieniu previousTotalSteps na aktualną wartość sensora.
            // Wtedy kolejne kroki będą liczone od tego momentu.
            // W bardziej zaawansowanych aplikacjach możesz chcieć przechowywać datę ostatniego resetu.
            // Na potrzeby tego przykładu, po prostu resetujemy wewnętrzną wartość.
            previousTotalSteps = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
                // Musimy poczekać na nowe zdarzenie sensora, aby uzyskać aktualną wartość.
                // Na razie ustawiamy na 0, i przy pierwszym odczycie ustawimy na obecną wartość sensora.
                0f // Ustawiamy na 0, a faktyczny reset nastąpi przy pierwszym odczycie.
            } ?: 0f // Jeśli sensor niedostępny, poprzednie kroki to 0.

            // Dodatkowo, aby mieć pewność, że to zadziała, możesz wymusić ponowne odczytanie sensora.
            // Najprostsze jest ponowne zarejestrowanie sensora po zresetowaniu previousTotalSteps.
            stopListening()
            savePreviousTotalSteps()
            totalSteps = 0
            startListening()
        }
    }
}