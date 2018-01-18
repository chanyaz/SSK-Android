package base.app.util.events.comment

import base.app.data.wall.Comment
import base.app.data.wall.BaseItem

class CommentDeleteEvent(var post: BaseItem, var comment: Comment)
