package pack.zdrowie.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import pack.zdrowie.database.UnitEnum
import java.time.LocalDate

/**
 * Represents a supplement record in the database.
 *
 * Each supplement entry is associated with a specific [User]. If a user is deleted,
 * their supplement records will also be removed due to the `CASCADE` delete rule.
 *
 * @property suppId The unique identifier for the supplement record (auto-generated).
 * @property suppName The name of the supplement.
 * @property suppCapacity The capacity of the supplement (e.g., number of tablets or milliliters).
 * @property suppUnit The unit of measurement for the supplement, stored as an [UnitEnum].
 * @property suppPic A string representing the path or URL to the supplement's image.
 * @property suppExpDate The expiration date of the supplement.
 * @property suppUserId The ID of the user to whom this supplement record belongs.
 */
@Entity(
    tableName = "supplements",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["suppUserId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Supplements(
    @PrimaryKey(autoGenerate = true) val suppId: Int,
    val suppName: String,
    val suppCapacity: Int,
    val suppUnit: UnitEnum,
    val suppPic: String,
    val suppExpDate: LocalDate,
    val suppUserId: Int
)
