package pack.zdrowie.caloriesTracker

/**
 * Represents a physical activity with data needed to calculate burned calories.
 *
 * @property name Name of the activity (e.g., "Running", "Swimming").
 * @property MET Metabolic Equivalent of Task (intensity factor of the activity).
 *              Example values:
 *              - 8.0f for running,
 *              - 4.0f for brisk walking,
 *              - 2.5f for light walking.
 * @property durationInMinutes Duration of the activity in minutes. Must be positive.
 * @property weightInKg User's weight in kilograms. Must be positive.
 */
data class Activity(
    val name: String,
    val MET: Float,
    val durationInMinutes: Float,
    val weightInKg: Float
) {
    /**
     * Calculates the estimated number of calories burned during the activity.
     * Formula: `(MET * 3.5 * weightInKg / 200) * durationInMinutes`.
     *
     * @return The approximate calories burned (in kcal).
     */
    val kcalBurned: Float
        get() = (MET * 3.5f * weightInKg / 200f) * durationInMinutes
}