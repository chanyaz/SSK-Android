package base.app.util.events

class NotificationEvent(
        val closeTime: Int,
        val title: String, val description: String,
        val type: Type) {

    enum class Type { FRIEND_REQUESTS, FOLLOWERS, LIKES }
}