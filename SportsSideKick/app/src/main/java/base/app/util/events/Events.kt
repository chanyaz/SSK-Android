package base.app.util.events

import android.content.Intent
import base.app.data.chat.ChatInfo
import base.app.data.chat.ChatMessage
import base.app.data.content.wall.BaseItem
import base.app.data.content.wall.Comment
import base.app.data.content.wall.News
import base.app.data.content.wall.Post
import base.app.data.user.UserInfo
import java.util.*

class AddFriendsEvent(val userInfo: UserInfo, val isRemove: Boolean)
class FriendsListChangedEvent
class AddUsersToCallEvent(val users: List<UserInfo>)
class ChatsInfoUpdatesEvent(val chats: List<ChatInfo>)

class ChatUpdateEvent(val chatInfo: ChatInfo)

class ClubTVEvent(var eventType: Type, var id: String) {
    enum class Type { FIRST_VIDEO_DATA_DOWNLOADED, VIDEOS_DOWNLOADED, PLAYLISTS_DOWNLOADED }
}

class CommentDeleteEvent(var post: BaseItem, var comment: Comment)
class CommentSelectedEvent(var selectedComment: Comment)
class CommentUpdatedEvent(val wallItem: BaseItem, val comment: Comment)
class CommentUpdateEvent(val wallItem: BaseItem, val comment: Comment)

class CreateNewChatSuccessEvent(var chatInfo: ChatInfo)

class MessageUpdateEvent(var message: ChatMessage)

class UserIsTypingEvent(var chatId: String, var users: List<UserInfo>)

class FragmentEvent(var type: Class<*>?) {
    var itemId: String? = null
    var secondaryId: String? = null
    var stringArrayList: ArrayList<String>? = null
    var item: BaseItem? = null
    var initiatorFragment: Class<*>? = null
}

class FullScreenImageEvent(var imageUrl: String)

class MessageSelectedEvent(val selectedMessage: ChatMessage)

class OpenChatEvent(var chatInfo: ChatInfo)

class GetCommentsCompleteEvent(val commentList: List<Comment> )
class GetPostByIdEvent(val post: Post)
class ItemUpdateEvent(val item: BaseItem)

class NativeShareEvent(val intent: Intent)

class NewsPageEvent(var values: List<News>)

class NextMatchUpdateEvent

class PostDeletedEvent(val post: Post)

class WallLikeUpdateEvent(var wallId: String, var postId: String, var count: Int)

class PostCommentCompleteEvent(val comment: Comment, val post: Post)

class PlayVideoEvent(var videoUrl: String)

class StartCallEvent(var users: List<UserInfo>)