package au.com.blitzit

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters
{
    //LIST
    @TypeConverter
    fun fromList(value: List<String>) = Json.encodeToString(value)
    @TypeConverter
    fun toList(value: String) = Json.decodeFromString<List<String>>(value)

    //MAP<String, Double>
    @TypeConverter
    fun fromMap(value: Map<String, Double>) = Json.encodeToString(value)
    @TypeConverter
    fun toMap(value: String) = Json.decodeFromString<Map<String, Double>>(value)
}