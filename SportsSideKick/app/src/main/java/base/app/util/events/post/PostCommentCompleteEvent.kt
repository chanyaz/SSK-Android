package base.app.util.events.post

import base.app.data.wall.Comment
import base.app.data.wall.Post

class PostCommentCompleteEvent(
        val comment: Comment,
        val post: Post
)
