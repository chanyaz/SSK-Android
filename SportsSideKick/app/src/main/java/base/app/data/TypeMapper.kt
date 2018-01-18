package base.app.data

import base.app.data.wall.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*

object TypeMapper {

    private enum class PostType {
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
        SocialShare,
        Social
    }

    @JvmStatic
    val cache = HashMap<String, BaseItem>()

    @JvmStatic
    fun clear() {
        cache.clear()
    }

    @JvmStatic
    fun postFactory(wallItem: Any, mapper: ObjectMapper, putInCache: Boolean): BaseItem? {
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
                PostType.NewsShare -> typeReference = object : TypeReference<Pin>() {

                }
                PostType.Betting -> typeReference = object : TypeReference<Betting>() {

                }
                PostType.Stats -> typeReference = object : TypeReference<Stats>() {

                }
                PostType.StoreOffer -> typeReference = object : TypeReference<StoreOffer>() {

                }
                PostType.NewsOfficial, PostType.Rumour -> typeReference = object : TypeReference<News>() {

                }
                else -> throw IllegalStateException("Unsupported item type: ${node.get("type")}")
            }

            var item = mapper.convertValue<BaseItem>(wallItem, typeReference)
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