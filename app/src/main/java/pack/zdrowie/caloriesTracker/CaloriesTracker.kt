package pack.zdrowie.caloriesTracker

/**
 * Tracks calorie intake from meals and calorie expenditure from physical activities.
 * Provides calculations for total calories consumed, burned, and net balance.
 */
class CaloriesTracker {
    private val meals = mutableListOf<Meal>()
    private val activities = mutableListOf<Activity>()

    /**
     * Adds a meal to the tracker.
     * @param meal The [Meal] to be added to the calorie intake log.
     */
    fun addMeal(meal: Meal) {
        meals.add(meal)
    }

    /**
     * Records a physical activity in the tracker.
     * @param activity The [Activity] to be added to the calorie expenditure log.
     */
    fun addActivity(activity: Activity) {
        activities.add(activity)
    }

    /**
     * Calculates the sum of all calories consumed from recorded meals.
     * @return Total calories eaten (in kcal) as a floating-point value.
     */
    fun totalCaloriesEaten(): Float {
        return meals.sumOf { it.totalKcal.toDouble() }.toFloat()
    }

    /**
     * Calculates the sum of all calories burned from recorded activities.
     * @return Total calories burned (in kcal) as a floating-point value.
     */
    fun totalCaloriesBurned(): Float {
        return activities.sumOf { it.kcalBurned.toDouble() }.toFloat()
    }

    /**
     * Calculates the net calorie balance (intake - expenditure).
     * @return Positive value indicates calorie surplus, negative indicates deficit.
     */
    fun netCalories(): Float {
        return totalCaloriesEaten() - totalCaloriesBurned()
    }
}