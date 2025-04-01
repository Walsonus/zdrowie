package pack.zdrowie.database

import androidx.room.TypeConverter
import java.util.Date

/**
 * A class containing type converters for Room database.
 *
 * Room does not support storing custom objects like [Date] or [UnitEnum] directly,
 * so these converters handle their serialization and deserialization.
 */
class Converters {

    /**
     * Converts a timestamp (Long) to a [Date] object.
     *
     * @param value The timestamp value in milliseconds.
     * @return A [Date] object or null if the input is null.
     */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    /**
     * Converts a [Date] object to a timestamp (Long).
     *
     * @param date The [Date] object.
     * @return The timestamp in milliseconds or null if the input is null.
     */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
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
