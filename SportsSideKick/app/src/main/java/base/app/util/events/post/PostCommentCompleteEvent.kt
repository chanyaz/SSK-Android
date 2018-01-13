package base.app.util.events.post

import base.app.data.wall.PostComment
import base.app.data.wall.WallItem

class PostCommentCompleteEvent(
        val comment: PostComment,
        val post: WallItem
)
