package base.app.data

import android.util.Log
import base.app.data.wall.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

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

object TypeMapper {

    private val TAG = "WALL BASE"

    @JvmStatic
    val cache = HashMap<String, WallBase>()

    @JvmStatic
    fun clear() {
        cache.clear()
    }

    @JvmStatic
    fun postFactory(wallItem: Any, mapper: ObjectMapper, putInCache: Boolean): WallBase? {
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
                PostType.Post, PostType.Comment, PostType.Social -> typeReference = object : TypeReference<Post>() {

                }
                PostType.NewsShare -> typeReference = object : TypeReference<NewsShare>() {

                }
                PostType.Betting -> typeReference = object : TypeReference<WallBetting>() {

                }
                PostType.Stats -> typeReference = object : TypeReference<Stats>() {

                }
                PostType.StoreOffer -> typeReference = object : TypeReference<StoreOffer>() {

                }
                PostType.NewsOfficial, PostType.Rumour -> typeReference = object : TypeReference<News>() {

                }
                else -> Log.e(TAG, "ERROR ----- unsupported post type " + node.get("type").textValue() + "\n\n" + node)
            }

            var item = mapper.convertValue<WallBase>(wallItem, typeReference!!)
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