package base.app.data.wall

import android.util.Log
import base.app.data.user.UserInfo
import base.app.data.wall.WallItem.PostType.Post
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.Serializable
import java.util.*

@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
open class WallItem : Serializable {

    @JsonIgnore // NOTE: we set Post type in factory method, not trough automatic JSON parsing!
    var type = Post

    var timestamp: Double = 0.0
    var wallId: String? = null
    var postId = ""
    var likeCount = 0
    @JsonProperty("likedByUser")
    var isLikedByUser: Boolean = false
    var commentsCount = 0
    var shareCount = 0
    var subTitle = ""
    var title: String = ""
    var bodyText: String = ""
    var coverImageUrl: String? = ""
    var coverAspectRatio: Float? = null
    var referencedItemClub: String? = null
    var referencedItemId: String? = null
    var sharedComment: String? = null
    private var translatedTo: String? = null
    var url: String? = null
    var vidUrl: String? = null

    var poster: UserInfo? = null

    val typeAsInt: Int
        @JsonProperty("type")
        get() = type.ordinal + 1

    val isNotTranslated: Boolean
        get() = translatedTo == null

    enum class PostType {
        Post,
        NewsShare,
        Betting,
        Stats,
        Rumour,
        StoreOffer,
        NewsOfficial,
        NewsUnofficial,
        Comment,
        RumourShare,
        PostShare,
        Social,
        SocialShare
    }

    fun hasSharedComment(): Boolean {
        return sharedComment != null && !sharedComment!!.replace(" ".toRegex(), "").isEmpty()
    }

    fun toggleLike() {
        isLikedByUser = !isLikedByUser
        if (isLikedByUser) {
            likeCount += 1
        } else {
            likeCount -= 1
        }
        WallModel.getInstance().setLikeCount(this, isLikedByUser).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.i(TAG, "Like set to value " + isLikedByUser)
            }
        }
    }

    companion object {

        private val TAG = "WALL BASE"

        @JvmStatic
        val cache = HashMap<String, WallItem>()

        @JvmStatic
        fun clear() {
            cache.clear()
        }

        @JvmStatic
        fun postFactory(wallItem: Any, mapper: ObjectMapper, putInCache: Boolean): WallItem? {
            val node = mapper.valueToTree<JsonNode>(wallItem)
            if (node.has("type")) {
                var typeReference: TypeReference<*>? = null
                val type: PostType

                if (node.get("type").canConvertToInt()) {
                    val typeValue = node.get("type").intValue()
                    type = PostType.values()[typeValue - 1]
                } else {
                    val objectType = node.get("type").textValue()
                    type = PostType.valueOf(objectType)
                }
                when (type) {
                    Post, WallItem.PostType.Comment, WallItem.PostType.Social -> typeReference = object : TypeReference<base.app.data.wall.Post>() {

                    }
                    WallItem.PostType.NewsShare -> typeReference = object : TypeReference<NewsShare>() {

                    }
                    WallItem.PostType.Betting -> typeReference = object : TypeReference<WallBetting>() {

                    }
                    WallItem.PostType.Stats -> typeReference = object : TypeReference<Stats>() {

                    }
                    WallItem.PostType.StoreOffer -> typeReference = object : TypeReference<StoreOffer>() {

                    }
                    WallItem.PostType.NewsOfficial, WallItem.PostType.Rumour -> typeReference = object : TypeReference<News>() {

                    }
                    else -> Log.e(TAG, "ERROR ----- unsupported post type " + node.get("type").textValue() + "\n\n" + node)
                }

                var item = mapper.convertValue<WallItem>(wallItem, typeReference!!)
                item.type = type

                // TODO @Filip - Fix me - preventing cache of non-wall items
                if (putInCache) {
                    val cachedItem = cache[item.postId]
                    if (cachedItem != null) {
                        item = cachedItem
                    } else {
                        cache[item.postId] = item
                    }
                }

                return item
            }
            return null
        }
    }
}
