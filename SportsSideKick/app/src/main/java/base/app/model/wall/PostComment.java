

package base.app.model.wall;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true,value={"type"})
public class PostComment {

    @JsonProperty("_id")
    private String id;
    @JsonProperty("comment")
    private String comment;
    @JsonProperty("posterId")
    private String posterId;
    @JsonProperty("wallId")
    private String wallId;
    @JsonProperty("postId")
    private String postId;
    @JsonProperty("timestamp")
    private Double timestamp;

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    @JsonProperty("_id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("comment")
    public String getComment() {
        return comment;
    }

    @JsonProperty("comment")
    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("posterId")
    public String getPosterId() {
        return posterId;
    }

    @JsonProperty("posterId")
    public void setPosterId(String posterId) {
        this.posterId = posterId;
    }

    @JsonProperty("wallId")
    public String getWallId() {
        return wallId;
    }

    @JsonProperty("wallId")
    public void setWallId(String wallId) {
        this.wallId = wallId;
    }

    @JsonProperty("postId")
    public String getPostId() {
        return postId;
    }

    @JsonProperty("postId")
    public void setPostId(String postId) {
        this.postId = postId;
    }

    @JsonProperty("timestamp")
    public Double getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Double timestamp) {
        this.timestamp = timestamp;
    }
}

