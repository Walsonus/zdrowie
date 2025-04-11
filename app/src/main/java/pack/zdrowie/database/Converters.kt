package pack.zdrowie.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date

/**
 * A class containing type converters for Room database.
 *
 * Room does not support storing custom objects like [Date] or [UnitEnum] directly,
 * so these converters handle their serialization and deserialization.
 */
class Converters {

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    /**
     * Converts a String to a LocalDate object.
     *
     * @param value The string representing a date.
     * @return A LocalDate object or null if the input is null.
     */
    @TypeConverter
    fun fromStringToLocalDate(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it, formatter) }
    }

    /**
     * Converts a LocalDate object to a String.
     *
     * @param date The LocalDate object.
     * @return A string representation of the date or null if the input is null.
     */
    @TypeConverter
    fun fromLocalDateToString(date: LocalDate?): String? {
        return date?.format(formatter)
    }

    /**
     * Converts a [UnitEnum] value to a [String].
     *
     * @param value The [UnitEnum] value.
     * @return The string representation of the enum.
     */
    @TypeConverter
    fun fromUnitEnum(value: UnitEnum): String {
        return value.name
    }
    
    /**
     * Converts a [String] back to a [UnitEnum] value.
     *
     * @param value The string representation of the enum.
     * @return The corresponding [UnitEnum] value.
     * @throws IllegalArgumentException if the provided string does not match any [UnitEnum] value.
     */
    @TypeConverter
    fun toUnitEnum(value: String): UnitEnum {
        return UnitEnum.valueOf(value)
    }
}
