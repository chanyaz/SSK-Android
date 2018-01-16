package base.app.ui.fragment.popup.post

import java.io.File

interface IPostCreateView {
    fun showUser()
    fun showPostImage(image: File)
    fun clearPostImage()
    fun showLoading(loading: Boolean)
    fun exit()
}