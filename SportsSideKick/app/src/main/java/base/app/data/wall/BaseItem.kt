package base.app.data.wall

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import java.io.Serializable

@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
abstract class BaseItem : Serializable {

    open var id: String = ""
    var wallId: String = ""
    var title: String = ""
    var likeCount = 0
    var shareCount = 0
    var commentsCount = 0
    var timestamp = 0.0
    var likedByUser = false
    var url: String? = null
}