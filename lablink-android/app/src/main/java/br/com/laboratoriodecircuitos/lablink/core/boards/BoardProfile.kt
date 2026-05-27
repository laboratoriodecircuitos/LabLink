package br.com.laboratoriodecircuitos.lablink.core.boards

data class BoardProfile(
    val type: BoardType,
    val displayName: String = type.displayName,
    val description: String,
    val pins: List<BoardPin>,
)
