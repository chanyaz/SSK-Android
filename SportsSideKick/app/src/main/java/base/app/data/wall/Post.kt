package base.app.data.wall

import base.app.data.user.UserInfo
import com.fasterxml.jackson.annotation.JsonProperty

open class Post(
        @JsonProperty("postId")
        override var id: String = "",
        var bodyText: String = "",
        var poster: UserInfo? = null,
        var coverImageUrl: String? = null,
        var coverAspectRatio: Float = 1.6F,
        val vidUrl: String? = null,
        val translatedTo: String? = null
) : BaseItem()