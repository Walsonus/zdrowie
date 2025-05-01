package pack.zdrowie.caloriesTracker

data class Meal(
    val name: String,
    val weightInGrams: Float,
    val kcalPer100g: Float
) {
    val totalKcal: Float
        get() = (weightInGrams / 100f) * kcalPer100g
}
