package base.app.data.content.news

import base.app.ui.fragment.user.login.LoginApi
import base.app.data.content.wall.Post
import base.app.data.content.wall.WallModel
import base.app.util.commons.Utility
import io.reactivex.Observable
import io.reactivex.Observable.just
import java.io.File

class PostsRepository {

    fun composePost(
            title: String,
            bodyText: String,
            imageUrl: String?
    ): Observable<Post> {
        val post = Post()
        post.title = title
        post.bodyText = bodyText
        post.timestamp = Utility.getCurrentTime().toDouble()
        post.coverImageUrl = imageUrl
        return just(post)
    }

    fun savePost(post: Post): Observable<Post> {
        return WallModel.getInstance().createPost(post)
    }

    fun uploadImage(image: File?): Observable<String> {
        return if (image != null) {
            LoginApi.getInstance().uploadImage(image)
        } else {
            just("")
        }
    }
}