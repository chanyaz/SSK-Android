package base.app.data.wall

import base.app.data.PostType
import base.app.data.user.UserInfo
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class WallBase : Serializable {

    @JsonIgnore // NOTE: we set Post type in factory method, not trough automatic JSON parsing!
    var type = PostType.Post

    var timestamp = 0.0
    var wallId: String = ""
    var postId = ""
    @JsonProperty("likedByUser")
    var isLikedByUser: Boolean = false
    var likeCount = 0
    var shareCount = 0
    var commentsCount = 0
    var subTitle = ""
    var title = ""
    var bodyText = ""
    var coverImageUrl: String? = null
    var coverAspectRatio: Float? = null
    var referencedItemClub: String? = null
    var referencedItemId: String? = null
    val sharedComment: String? = null
    private var translatedTo: String? = null
    var url: String? = null
    var vidUrl: String? = null

    var poster: UserInfo? = null

    val typeAsInt: Int
        @JsonProperty("type")
        get() = type.ordinal + 1

    val isNotTranslated: Boolean
        get() = translatedTo == null

    fun hasSharedComment(): Boolean {
        return sharedComment != null && sharedComment.isNotBlank()
    }

    fun toggleLike() {
        isLikedByUser = !isLikedByUser
        if (isLikedByUser) {
            likeCount++
        } else {
            likeCount--
        }
        WallModel.getInstance().setLikeCount(this, isLikedByUser)
    }
}
