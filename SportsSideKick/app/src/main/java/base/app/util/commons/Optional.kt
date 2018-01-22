package base.app.util.commons

sealed class Optional<out T> {
    class With<out T>(val element: T): Optional<T>()
    object None: Optional<Nothing>()
}