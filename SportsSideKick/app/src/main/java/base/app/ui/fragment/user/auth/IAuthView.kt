package base.app.ui.fragment.user.auth

interface IAuthView {
    fun showLoading(loading: Boolean)
    fun navigateToFeed(it: AuthViewModel.Session)
}