package pack.zdrowie.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDate

/**
 * Represents a step-tracking record for a user.
 *
 * Each entry corresponds to the number of steps taken on a given date.
 * It is linked to a specific [User] entity. If a user is deleted, their
 * associated step records are also removed due to the `CASCADE` delete rule.
 *
 * @property stepsId The unique identifier for the step record (auto-generated).
 * @property stepsDate The date when the steps were recorded.
 * @property stepsCount The total number of steps taken on the given date.
 * @property stepsUserId The ID of the user who owns this step record.
 */
@Entity(
    tableName = "steps",
    foreignKeys = [ForeignKey(
        entity = User::class,
        parentColumns = ["userId"],
        childColumns = ["stepsUserId"],
        onDelete = ForeignKey.CASCADE
    )]
)
data class Steps(
    @PrimaryKey(autoGenerate = true) val stepsId: Int,
    val stepsDate: LocalDate,
    val stepsCount: Int,
    val stepsUserId: Int
)
