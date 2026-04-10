package health.kokoro.infrastructure.converter

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class MapConverter : AttributeConverter<Map<String, String>, String> {
    companion object {
        private val gson: Gson = GsonBuilder().create()
        private val mapType = object : TypeToken<Map<String, String>>() {}.type
    }

    override fun convertToDatabaseColumn(map: Map<String, String>?): String {
        return gson.toJson(map)
    }

    override fun convertToEntityAttribute(str: String?): Map<String, String>? {
        return if (str.isNullOrEmpty()) null else gson.fromJson(str, mapType)
    }
}
