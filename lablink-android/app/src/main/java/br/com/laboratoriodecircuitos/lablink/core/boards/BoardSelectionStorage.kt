package br.com.laboratoriodecircuitos.lablink.core.boards

import android.content.Context

object BoardSelectionStorage {
    private const val PREFS_NAME = "lablink_board_selection"
    private const val KEY_SELECTED_BOARD_TYPE = "selected_board_type"

    fun loadBoard(context: Context): BoardProfile? {
        val savedType = context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_SELECTED_BOARD_TYPE, null)
            ?: return null

        val boardType = runCatching {
            BoardType.valueOf(savedType)
        }.getOrNull() ?: return null

        return BoardProfiles.findByType(boardType)
    }

    fun saveBoard(
        context: Context,
        board: BoardProfile,
    ) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_SELECTED_BOARD_TYPE, board.type.name)
            .apply()
    }

    fun clearBoard(context: Context) {
        context
            .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .remove(KEY_SELECTED_BOARD_TYPE)
            .apply()
    }
}
