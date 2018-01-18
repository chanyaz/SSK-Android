package base.app.data

import base.app.data.wall.*
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.util.*



object TypeMapper {

    // TODO Alex Sheiko: make private
    enum class ItemType {
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
    fun <T : BaseItem> postFactory(wallItem: Any, mapper: ObjectMapper, putInCache: Boolean): T? {
        val node = mapper.valueToTree<JsonNode>(wallItem)
        if (node.has("type")) {
            var typeReference: TypeReference<*>? = null
            val type: ItemType

            if (node.get("type").canConvertToInt()) {
                val typeValue = node.get("type").intValue()
                type = ItemType.values()[typeValue - 1]
            } else {
                val objectType = node.get("type").textValue()
                type = ItemType.valueOf(objectType)
            }
            when (type) {
                ItemType.Post,
                ItemType.Social -> typeReference = object : TypeReference<Post>() {
                }
                ItemType.NewsOfficial,
                ItemType.NewsUnofficial,
                ItemType.Rumour -> typeReference = object : TypeReference<News>() {
                }
                ItemType.NewsShare,
                ItemType.RumourShare,
                ItemType.SocialShare -> typeReference = object : TypeReference<Pin>() {
                }
                ItemType.Betting -> typeReference = object : TypeReference<Betting>() {
                }
                ItemType.Stats -> typeReference = object : TypeReference<Stats>() {
                }
                ItemType.StoreOffer -> typeReference = object : TypeReference<StoreOffer>() {
                }
                else -> throw IllegalStateException("Unsupported item type: ${node.get("type")}")
            }

            var item = mapper.convertValue<BaseItem>(wallItem, typeReference)

            // TODO @Filip - Fix me - preventing cache of non-wall items
            if (putInCache) {
                val cachedItem = cache[item.postId]
                if (cachedItem != null) {
                    item = cachedItem
                } else {
                    cache[item.postId] = item
                }
            }

            return item as T?
        }
        return null
    }
}