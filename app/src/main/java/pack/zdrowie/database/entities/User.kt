package pack.zdrowie.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.util.Date

/**
 * Represents a user entity in the database.
 *
 * This table stores user-related data such as login credentials, personal details, and physical attributes.
 *
 * @property userId The unique identifier for the user (auto-generated).
 * @property userName The username of the user.
 * @property userPassword The user's password (should be securely stored, ideally hashed).
 * @property userWeight The user's weight in kilograms.
 * @property userHeight The user's height in centimeters.
 * @property userDateOfBirth The user's date of birth.
 */
@Entity(tableName = "user")
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int,
    val userName: String,
    val userMail: String,
    val userPassword: String,
    val userWeight: Float,
    val userHeight: Float,
    val userDateOfBirth: LocalDate
)
