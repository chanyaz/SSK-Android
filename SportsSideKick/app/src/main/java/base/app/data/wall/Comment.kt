package base.app.data.wall

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
class Comment {

    @JsonProperty("_id")
    lateinit var id: String
    lateinit var comment: String
    lateinit var posterId: String
    lateinit var wallId: String
    lateinit var postId: String
    var timestamp: Double = 0.0
}