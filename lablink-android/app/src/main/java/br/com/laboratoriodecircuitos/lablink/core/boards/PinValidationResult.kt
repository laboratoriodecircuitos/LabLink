package br.com.laboratoriodecircuitos.lablink.core.boards

sealed interface PinValidationResult {
    data object Valid : PinValidationResult

    data object EmptyPin : PinValidationResult

    data class PinNotFound(
        val pin: String,
        val boardName: String,
    ) : PinValidationResult

    data class PinAlreadyUsed(
        val pin: String,
    ) : PinValidationResult

    data class UnsupportedFunction(
        val pin: String,
        val functionName: String,
    ) : PinValidationResult
}
