package pack.zdrowie.database.entities
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "location_history")
data class LocationEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val accuracy: Float?


    )
