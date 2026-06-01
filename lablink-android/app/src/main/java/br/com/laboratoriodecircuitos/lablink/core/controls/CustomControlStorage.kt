package br.com.laboratoriodecircuitos.lablink.core.controls

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

object CustomControlStorage {
    private const val PREFS_NAME = "lablink_custom_controls"
    private const val KEY_CONTROLS = "controls"

    fun loadControls(context: Context): List<CustomControl> {
        val rawJson = context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_CONTROLS, null)

        if (rawJson.isNullOrBlank()) {
            return emptyList()
        }

        return runCatching {
            val array = JSONArray(rawJson)

            buildList {
                for (index in 0 until array.length()) {
                    add(array.getJSONObject(index).toCustomControl())
                }
            }
        }.getOrElse {
            emptyList()
        }
    }

    fun saveControls(
        context: Context,
        controls: List<CustomControl>,
    ) {
        val array = JSONArray()

        controls.forEach { control ->
            array.put(control.toJson())
        }

        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_CONTROLS, array.toString())
            .commit()
    }

    private fun JSONObject.toCustomControl(): CustomControl {
        val widgetsArray = optJSONArray("widgets") ?: JSONArray()

        return CustomControl(
            id = optString("id"),
            name = optString("name", "Controle"),
            isSaved = optBoolean("isSaved", true),
            createdAt = optLong("createdAt", System.currentTimeMillis()),
            widgets = buildList {
                for (index in 0 until widgetsArray.length()) {
                    add(widgetsArray.getJSONObject(index).toLabLinkControl(index))
                }
            },
        )
    }

    private fun CustomControl.toJson(): JSONObject {
        val widgetsArray = JSONArray()
        widgets.forEach { widget ->
            widgetsArray.put(widget.toJson())
        }

        return JSONObject()
            .put("id", id)
            .put("name", name)
            .put("isSaved", isSaved)
            .put("createdAt", createdAt)
            .put("widgets", widgetsArray)
    }

    private fun JSONObject.toLabLinkControl(index: Int): LabLinkControl {
        return LabLinkControl(
            id = optString("id", "widget_$index"),
            type = ControlType.valueOf(optString("type", ControlType.DigitalToggle.name)),
            name = optString("name", "Widget ${index + 1}"),
            pin = optString("pin", ""),
            minValue = optNullableInt("minValue"),
            maxValue = optNullableInt("maxValue"),
            currentValue = optNullableInt("currentValue"),
            isOn = optBoolean("isOn", false),
            widthUnits = optInt("widthUnits", 1),
            heightUnits = optInt("heightUnits", 1),
            gridX = optInt("gridX", index % 3),
            gridY = optInt("gridY", index / 3),
            mode = optNullableString("mode"),
            durationMs = optNullableInt("durationMs"),
        )
    }

    private fun LabLinkControl.toJson(): JSONObject {
        return JSONObject()
            .put("id", id)
            .put("type", type.name)
            .put("name", name)
            .put("pin", pin)
            .put("minValue", minValue)
            .put("maxValue", maxValue)
            .put("currentValue", currentValue)
            .put("isOn", isOn)
            .put("widthUnits", widthUnits)
            .put("heightUnits", heightUnits)
            .put("gridX", gridX)
            .put("gridY", gridY)
            .put("mode", mode)
            .put("durationMs", durationMs)
    }

    private fun JSONObject.optNullableInt(key: String): Int? {
        return if (has(key) && !isNull(key)) {
            optInt(key)
        } else {
            null
        }
    }

    private fun JSONObject.optNullableString(key: String): String? {
        return if (has(key) && !isNull(key)) {
            optString(key)
        } else {
            null
        }
    }
}
