package base.app.data.wall

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.io.Serializable

@JsonIgnoreProperties(ignoreUnknown = true)
abstract class BaseItem : Serializable {

    lateinit var postId: String
    lateinit var wallId: String
    var likeCount = 0
    var shareCount = 0
    var commentsCount = 0
    var timestamp = 0.0
    var likedByUser = false
}