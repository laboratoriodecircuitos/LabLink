package br.com.laboratoriodecircuitos.lablink.core.controls

data class CustomControl(
    val id: String,
    val name: String,
    val widgets: List<LabLinkControl>,
    val isSaved: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
)
