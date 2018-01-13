package base.app.util.events.comment

import base.app.data.wall.PostComment
import base.app.data.wall.WallItem

class CommentDeleteEvent(var post: WallItem, var comment: PostComment)
