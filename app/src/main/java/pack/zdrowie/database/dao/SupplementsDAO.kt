package pack.zdrowie.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import pack.zdrowie.database.entities.Supplements

/**
 * Data Access Object (DAO) for managing supplement-related database operations.
 */
@Dao
interface SupplementDAO {

    /**
     * Inserts a new supplement into the database.
     *
     * @param supplements The [Supplements] entity to be inserted.
     */
    @Insert
    suspend fun insert(supplements: Supplements)

    /**
     * Deletes a supplement from the database.
     *
     * @param supplements The [Supplements] entity to be deleted.
     */
    @Delete
    suspend fun delete(supplements: Supplements)

    /**
     * Retrieves a supplement by its unique ID.
     *
     * @param suppId The unique ID of the supplement.
     * @return The corresponding [Supplements] entity.
     */
    @Query("SELECT * FROM supplements WHERE suppId = :suppId")
    suspend fun getSuppById(suppId: Int): Supplements

    /**
     * Retrieves all supplements associated with a specific user.
     *
     * @param suppUserId The unique ID of the user.
     * @return A list of [Supplements] entities associated with the given user.
     */
    @Query("SELECT * FROM supplements WHERE suppUserId = :suppUserId")
    suspend fun getSuppByUser(suppUserId: Int): List<Supplements>

    /**
     * Retrieves supplements by their name.
     *
     * @param suppName The name of the supplement.
     * @return A list of [Supplements] entities that match the given name.
     */
    @Query("SELECT * FROM supplements WHERE suppName = :suppName")
    suspend fun getSuppByName(suppName: String): List<Supplements>
}