package base.app.data.wall

import base.app.data.Id
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class PostComment {

    @JsonProperty("_id")
    lateinit var id: Id
    lateinit var comment: String
    var posterId: Id = Id()
    lateinit var wallId: String
    lateinit var postId: String
    var timestamp: Double = 0.0
}