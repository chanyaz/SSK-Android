package base.app.util.events.comment

import base.app.data.wall.PostComment
import base.app.data.wall.WallBase

class CommentDeleteEvent(var post: WallBase, var comment: PostComment)
