package base.app.data.news

import base.app.data.Model
import base.app.data.wall.WallBase
import base.app.data.wall.WallModel
import base.app.data.wall.WallPost
import base.app.util.commons.Utility
import io.reactivex.Observable
import io.reactivex.Observable.just
import java.io.File

class PostsRepository {

    fun composePost(
            title: String,
            bodyText: String,
            imageUrl: String?
    ): Observable<WallPost> {
        val post = WallPost()
        post.title = title
        post.bodyText = bodyText
        post.type = WallBase.PostType.post
        post.timestamp = Utility.getCurrentTime().toDouble()
        post.coverImageUrl = imageUrl
        return just(post)
    }

    fun savePost(post: WallPost): Observable<WallPost> {
        return WallModel.getInstance().createPostAndObserve(post)
    }

    fun uploadImage(image: File?): Observable<String> {
        return if (image != null) {
            Model.getInstance().uploadImage(image)
        } else {
            just("")
        }
    }
}