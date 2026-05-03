package za.co.quikle.lexio.util

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(
        val message: String,
        val retryable: Boolean = true,
        val errorType: ErrorType = ErrorType.UNKNOWN
    ) : ApiResult<Nothing>()
    data object Loading : ApiResult<Nothing>()
}

enum class ErrorType {
    NO_INTERNET,
    SERVER_ERROR,
    TIMEOUT,
    RATE_LIMITED,
    UNKNOWN
}

fun getErrorMessage(errorType: ErrorType): String {
    return when (errorType) {
        ErrorType.NO_INTERNET -> "No internet connection. The Rights Library is available offline."
        ErrorType.SERVER_ERROR -> "Something went wrong. Please try again."
        ErrorType.TIMEOUT -> "The server took too long to respond. Try a shorter question."
        ErrorType.RATE_LIMITED -> "You've reached the daily limit. Upgrade to Pro Citizen for unlimited access."
        ErrorType.UNKNOWN -> "Something went wrong. Please try again."
    }
}
