package base.app.util.events.comment

import base.app.data.wall.PostComment

class CommentDeleteEvent(var comment: PostComment, val newCommentCount: Int)
