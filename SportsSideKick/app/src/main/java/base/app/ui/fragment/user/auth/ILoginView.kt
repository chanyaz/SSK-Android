package base.app.ui.fragment.user.auth

import base.app.data.user.User

interface ILoginView {
    fun showLoading(loading: Boolean)
    fun navigateToFeed(user: User)
}