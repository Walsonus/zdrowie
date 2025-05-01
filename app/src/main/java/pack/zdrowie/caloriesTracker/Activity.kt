package pack.zdrowie.caloriesTracker

data class Activity(
    val name: String,
    val MET: Float, //MET jest to współczynnik intensywności aktywności fizycznej np. dla biegania 8.
    val durationInMinutes: Float,
    val weightInKg: Float
) {
    val kcalBurned: Float
        get() = (MET * 3.5f * weightInKg / 200f) * durationInMinutes

}
