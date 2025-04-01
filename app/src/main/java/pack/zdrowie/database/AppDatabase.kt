package pack.zdrowie.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pack.zdrowie.database.dao.StepsDAO
import pack.zdrowie.database.dao.SupplementDAO
import pack.zdrowie.database.dao.UserDAO
import pack.zdrowie.database.entities.Steps
import pack.zdrowie.database.entities.Supplements
import pack.zdrowie.database.entities.User

/**
 * Main application database using Room.
 *
 * This database stores information about users, steps, and supplements.
 * It also includes type converters for custom data types.
 *
 * @property userDao Provides access to user-related database operations.
 * @property stepsDao Provides access to steps-related database operations.
 * @property supplementDAO Provides access to supplements-related database operations.
 *
 * @constructor Creates an instance of the database.
 */
@Database(entities = [User::class, Steps::class, Supplements::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Retrieves the DAO for user operations.
     *
     * @return Instance of [UserDAO].
     */
    abstract fun userDao(): UserDAO

    /**
     * Retrieves the DAO for steps operations.
     *
     * @return Instance of [StepsDAO].
     */
    abstract fun stepsDao(): StepsDAO

    /**
     * Retrieves the DAO for supplement operations.
     *
     * @return Instance of [SupplementDAO].
     */
    abstract fun supplementDAO(): SupplementDAO
}
