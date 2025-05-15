package pack.zdrowie.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import pack.zdrowie.database.entities.LocationEntity

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationEntity)

    @Query ("SELECT * FROM location_history ORDER BY timestamp DESC")
    suspend fun getAllLocations(): List<LocationEntity>




}