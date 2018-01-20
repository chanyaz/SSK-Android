package base.app.data.content.wall

import base.app.data.user.User

open class Post(
        var bodyText: String = "",
        var poster: User? = null,
        var coverImageUrl: String? = null,
        var coverAspectRatio: Float = 1.6F,
        val vidUrl: String? = null,
        val translatedTo: String? = null
) : FeedItem()