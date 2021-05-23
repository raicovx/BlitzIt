package au.com.blitzit

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class Converters
{
    //LIST
    @TypeConverter
    fun fromList(value: List<String>?): String
    {
        return if(value == null) {
            val emptyList: List<String> = emptyList()
            Json.encodeToString(emptyList)
        } else
            Json.encodeToString(value)
    }

    @TypeConverter
    fun toList(value: String?): List<String>?
    {
        return if (value == null)
            null
        else
            Json.decodeFromString(value)
    }

    //MAP<String, Double>
    @TypeConverter
    fun fromMap(value: Map<String, Double>) = Json.encodeToString(value)
    @TypeConverter
    fun toMap(value: String) = Json.decodeFromString<Map<String, Double>>(value)
}