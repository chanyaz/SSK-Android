package base.app.data

import base.app.data.content.tv.MediaModel.mapper
import base.app.data.content.wall.*
import base.app.data.user.User
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.gamesparks.sdk.api.autogen.GSResponseBuilder
import java.util.*

object TypeConverter {

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
        Social;
    }

    @JvmStatic
    val cache = HashMap<String, FeedItem>()

    @JvmStatic
    fun clear() {
        cache.clear()
    }

    @JvmStatic
    fun <T : FeedItem> postFactory(wallItem: Any, mapper: ObjectMapper, putInCache: Boolean): T? {
        val node = mapper.valueToTree<JsonNode>(wallItem)
        if (node.has("type")) {
            lateinit var typeReference: TypeReference<*>

            val typeValue = node.get("type").intValue()
            val type = ItemType.values()[typeValue - 1]

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
            var item = mapper.convertValue<T>(wallItem, typeReference)
            if (node.get("_id").has("\$oid")) {
                item.id = node.get("_id").get("\$oid").asText()
            } else {
                item.id = node.get("_id").asText()
            }

            // TODO @Filip - Fix me - preventing cache of non-wall items
            if (putInCache && item.id.isNotEmpty()) {
                val cachedItem = cache[item.id]
                if (cachedItem != null) {
                    item = cachedItem as T
                } else {
                    cache[item.id] = item
                }
            }

            return item
        }
        return null
    }
}

fun GSResponseBuilder.AccountDetailsResponse.toUser(): User {
    val scriptData = scriptData

    val user: User
    user = if (scriptData != null) {
        val data = scriptData.baseData
        mapper.convertValue(data, User::class.java)
    } else {
        mapper.convertValue(this, User::class.java)
    }
    return user
}