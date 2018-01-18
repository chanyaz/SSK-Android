package base.app.data.wall

import base.app.data.user.UserInfo
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import java.io.Serializable

@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class WallBase : Serializable {

    var postId = ""
    var wallId = ""
    var title = ""
    var subTitle = ""
    var bodyText = ""
    var timestamp = 0.0
    var likeCount = 0
    var shareCount = 0
    var commentsCount = 0
    var likedByUser: Boolean = false
    var coverImageUrl: String? = null
    var coverAspectRatio: Float? = null
    var referencedItemClub: String? = null
    var referencedItemId: String? = null
    val sharedComment: String? = null
    val translatedTo: String? = null
    var url: String? = null
    var vidUrl: String? = null
    var poster: UserInfo? = null

    fun toggleLike() {
        likedByUser = !likedByUser
        if (likedByUser) {
            likeCount++
        } else {
            likeCount--
        }
        WallModel.getInstance().setLikeCount(this, likedByUser)
    }
}