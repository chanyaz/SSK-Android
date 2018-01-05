package base.app.util.events.post

import base.app.data.wall.PostComment
import base.app.data.wall.WallBase

class PostCommentCompleteEvent(
        val comment: PostComment,
        val post: WallBase
)
