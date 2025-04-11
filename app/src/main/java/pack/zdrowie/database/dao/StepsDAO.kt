package pack.zdrowie.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import pack.zdrowie.database.entities.Steps
import java.time.LocalDate
import java.util.Date

/** Data Access Object (DAO) for managing steps-related database operations. */
@Dao
interface StepsDAO {

    /**
     * Inserts a new step record into the database.
     *
     * @param steps The [Steps] entity to be inserted.
     */
    @Insert
    suspend fun insert(steps: Steps)

    /**
     * Retrieves all step records for a specific user.
     *
     * @param userId The unique ID of the user.
     * @return A list of [Steps] entities associated with the given user.
     */
    @Query("SELECT * FROM steps WHERE stepsUserId = :userId")
    suspend fun getStepsByUser(userId: Int): List<Steps>

    /**
     * Retrieves a step record by its unique ID.
     *
     * @param stepsId The unique ID of the step record.
     * @return The ID of the steps record.
     */
    @Query("SELECT * FROM steps WHERE stepsId = :stepsId")
    suspend fun getStepsById(stepsId: Int): Steps

    /**
     * Retrieves step records by a specific date.
     *
     * @param stepsDate The date for which steps should be retrieved.
     * @return A list of [Steps] entities associated with the given date.
     */
    @Query("SELECT * FROM steps WHERE stepsDate = :stepsDate")
    suspend fun getStepsByDate(stepsDate: LocalDate): List<Steps>
}