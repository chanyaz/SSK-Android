package base.app.util.events.comment

import base.app.data.wall.Comment

class GetCommentsCompleteEvent(
        val commentList: List<Comment> = ArrayList()
)