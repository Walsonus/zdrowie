package pack.zdrowie.caloriesTracker

class CaloriesTracker {
    private val meals = mutableListOf<Meal>()
    private val activities = mutableListOf<Activity>()

    fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    fun totalCaloriesEaten(): Float {
        return meals.sumOf { it.totalKcal.toDouble() }.toFloat()
    }

    fun totalCaloriesBurned(): Float {
        return activities.sumOf { it.kcalBurned.toDouble() }.toFloat()
    }

    fun netCalories(): Float {
        return totalCaloriesEaten() - totalCaloriesBurned()
    }
}