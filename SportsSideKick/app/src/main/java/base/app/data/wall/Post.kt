package base.app.data.wall

import base.app.data.user.UserInfo

open class Post(
        var title: String = "",
        var subTitle: String = "",
        var bodyText: String = "",
        var poster: UserInfo? = null,
        var coverImageUrl: String? = null,
        var coverAspectRatio: Float = 1.6F,
        var url: String? = null,
        val vidUrl: String? = null,
        val translatedTo: String? = null
) : BaseItem()