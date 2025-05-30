package pack.zdrowie.caloriesTracker

/**
 * Represents a food meal with nutritional information.
 *
 * @property name The name/description of the meal (e.g., "Chicken Salad")
 * @property weightInGrams Total weight of the meal in grams. Must be positive.
 * @property kcalPer100g Calories per 100 grams of the food. Must be non-negative.
 */
data class Meal(
    val name: String,
    val weightInGrams: Float,
    val kcalPer100g: Float
) {
    /**
     * Calculates the total calories in the meal based on its weight.
     *
     * The calculation follows the formula:
     * `(weightInGrams / 100) * kcalPer100g`
     *
     * @return Total calories in the meal as a floating-point value.
     * @throws IllegalStateException if the calculated calories are negative,
     *         which could happen due to floating-point precision issues.
     */
    val totalKcal: Float
        get() {
            val calories = (weightInGrams / 100f) * kcalPer100g
            require(calories >= 0) { "Calculated calories cannot be negative" }
            return calories
        }
}