package br.com.android.chatapp.data.util


sealed class UiIntent<out T> {
    object Loading: UiIntent<Nothing>()
    data class Success<out T>(val data: T): UiIntent<T>()
    data class Failure(val error: String?): UiIntent<Nothing>()
}
