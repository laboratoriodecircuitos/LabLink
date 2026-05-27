package br.com.laboratoriodecircuitos.lablink.core.controls

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object ControlStorage {
    private const val PREFS_NAME = "lablink_controls"
    private const val KEY_CONTROLS = "configured_controls"

    fun loadControls(context: Context): List<LabLinkControl> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val rawJson = prefs.getString(KEY_CONTROLS, null)

        if (rawJson.isNullOrBlank()) {
            return listOf(DefaultControls.pin13DigitalOutput)
        }

        return runCatching {
            val array = JSONArray(rawJson)

            buildList {
                for (index in 0 until array.length()) {
                    val item = array.getJSONObject(index)

                    add(
                        LabLinkControl(
                            id = item.optString("id"),
                            type = ControlType.valueOf(
                                item.optString("type", ControlType.DigitalToggle.name),
                            ),
                            name = item.optString("name", "Controle ${index + 1}"),
                            pin = item.optString("pin", "13"),
                            minValue = item.optNullableInt("minValue"),
                            maxValue = item.optNullableInt("maxValue"),
                            currentValue = item.optNullableInt("currentValue"),
                            isOn = item.optBoolean("isOn", false),
                        ),
                    )
                }
            }.ifEmpty {
                listOf(DefaultControls.pin13DigitalOutput)
            }
        }.getOrElse {
            listOf(DefaultControls.pin13DigitalOutput)
        }
    }

    fun saveControls(
        context: Context,
        controls: List<LabLinkControl>,
    ) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val array = JSONArray()

        controls.forEach { control ->
            array.put(
                JSONObject()
                    .put("id", control.id)
                    .put("type", control.type.name)
                    .put("name", control.name)
                    .put("pin", control.pin)
                    .put("minValue", control.minValue)
                    .put("maxValue", control.maxValue)
                    .put("currentValue", control.currentValue)
                    .put("isOn", control.isOn),
            )
        }

        prefs.edit()
            .putString(KEY_CONTROLS, array.toString())
            .apply()
    }

    private fun JSONObject.optNullableInt(key: String): Int? {
        return if (has(key) && !isNull(key)) {
            optInt(key)
        } else {
            null
        }
    }
}
