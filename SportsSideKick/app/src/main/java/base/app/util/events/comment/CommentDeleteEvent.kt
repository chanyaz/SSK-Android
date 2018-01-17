package base.app.util.events.comment

import base.app.data.wall.Comment
import base.app.data.wall.WallItem

class CommentDeleteEvent(var post: WallItem, var comment: Comment)
