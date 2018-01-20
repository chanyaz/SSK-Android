package base.app.data.content.wall

open class News(
        var content: String = "",
        var image: String? = null
) : FeedItem()