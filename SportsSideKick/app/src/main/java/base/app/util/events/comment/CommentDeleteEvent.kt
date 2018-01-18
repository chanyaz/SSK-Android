package base.app.util.events.comment

import base.app.data.wall.Comment
import base.app.data.wall.WallBase

class CommentDeleteEvent(var post: WallBase, var comment: Comment)
