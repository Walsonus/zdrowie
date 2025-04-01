package pack.zdrowie.database

/**
 * Enum representing measurement units for supplements.
 *
 * This enumeration defines commonly used units for storing supplement capacities.
 */
enum class UnitEnum {
    /** Centiliters (cl) - 1 cl = 10 ml */
    Centilitre,

    /** Milliliters (ml) - 1 ml = 0.001 l */
    Milliliter,

    /** Liters (l) - 1 l = 1000 ml */
    Liter,

    /** Grams (g) - 1 g = 0.001 kg */
    Gram,

    /** Kilograms (kg) - 1 kg = 1000 g */
    Kilogram
}
