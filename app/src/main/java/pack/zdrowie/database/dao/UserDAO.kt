package pack.zdrowie.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import pack.zdrowie.database.entities.User

/**
 * Data Access Object (DAO) for managing user-related database operations.
 */
@Dao
interface UserDAO {

    /**
     * Inserts a new user into the database.
     *
     * @param user The [User] entity to be inserted.
     */
    @Insert
    suspend fun insert(user: User)

    /**
     * Deletes a user from the database.
     *
     * @param user The [User] entity to be deleted.
     */
    @Delete
    suspend fun delete(user: User)

    /**
     * Retrieves a user by their unique ID.
     *
     * @param userId The unique ID of the user.
     * @return The corresponding [User] entity.
     */
    @Query("SELECT * FROM user WHERE userId = :userId ")
    suspend fun getUserById(userId: Int): User

    /**
     * Retrieves a user by their username.
     *
     * @param userName The username of the user.
     * @return The corresponding [User] entity.
     */
    @Query("SELECT * FROM user WHERE userName = :userName ")
    suspend fun getUserByName(userName: String): User
}