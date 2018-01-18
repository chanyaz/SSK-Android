package base.app.data.wall

import java.io.Serializable

abstract class BaseItem : Serializable {

    lateinit var postId: String
    lateinit var wallId: String
    var likeCount = 0
    var shareCount = 0
    var commentsCount = 0
    var timestamp = 0.0
    var likedByUser = false
    var url: String? = null
    var vidUrl: String? = null
}