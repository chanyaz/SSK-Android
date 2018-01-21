package base.app.ui.fragment.popup

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import base.app.BuildConfig
import base.app.R
import base.app.data.user.LoginStateReceiver
import base.app.data.user.LoginStateReceiver.LoginListener
import base.app.data.user.PasswordResetReceiver
import base.app.data.user.PasswordResetReceiver.PasswordResetListener
import base.app.data.user.User
import base.app.util.commons.UserRepository
import base.app.util.commons.Utility
import base.app.util.events.FragmentEvent
import base.app.util.ui.AlertDialogManager
import base.app.util.ui.BaseFragment
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import kotlinx.android.synthetic.main.fragment_login.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.support.v4.toast
import org.json.JSONException
import java.util.*

class LoginFragment : BaseFragment(R.layout.fragment_login),
        LoginListener, PasswordResetListener {

    private var callbackManager: CallbackManager? = null
    private var loginStateReceiver: LoginStateReceiver? = null
    private var passwordResetReceiver: PasswordResetReceiver? = null

    override fun onViewCreated(view: View, state: Bundle?) {
        autoPopulateOnDebug()

        loginStateReceiver = LoginStateReceiver(this)
        passwordResetReceiver = PasswordResetReceiver(this)
        initFacebook()

        titleText.text = Html.fromHtml(getString(R.string.slogan))

        cancelRestoreButton.setOnClickListener {
            fieldsContainer.visibility = View.VISIBLE
            if (submitButton != null) {
                submitButton.visibility = View.VISIBLE
            }
            submitButtonLabel.text = getString(R.string.sign_in)
            cancelRestoreButton.visibility = View.GONE
            passwordField.visibility = View.VISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    private fun autoPopulateOnDebug() {
        if (BuildConfig.DEBUG) {
            emailField.setText("alexsheikodev3@gmail.com")
            passwordField.setText("temppass")
        }
    }

    @OnClick(R.id.submitButton)
    fun loginOnClick() {
        val email = emailField.text.toString()
        val password = passwordField.text.toString()
        if (email.isBlank() || password.isBlank()) {
            toast(R.string.required_credentials)
            return
        }
        UserRepository.getInstance().login(email, password)
        submitButtonLabel.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
    }

    @Optional
    @OnClick(R.id.reset_text)
    fun forgotPasswordOnClick() {
        val email = emailField.text.toString()
        UserRepository.getInstance().resetPassword(email)
        activity?.onBackPressed()
    }

    @Optional
    @OnClick(R.id.forgot_button)
    fun forgotOnClick() {
        if (Utility.isTablet(activity)) {
            if (submitButton != null) {
                submitButton.visibility = View.GONE
            }
            fieldsContainer.visibility = View.GONE
        } else {
            fieldsContainer.visibility = View.INVISIBLE
            if (submitButton != null) {
                submitButton.visibility = View.INVISIBLE
            }
        }
        submitButtonLabel.text = getString(R.string.password_reset)
        cancelRestoreButton.visibility = View.VISIBLE
        passwordField.visibility = View.GONE
    }

    fun initFacebook() {
        if (facebookButton != null) {
            facebookButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_photos"))
            callbackManager = CallbackManager.Factory.create()
            facebookButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                    ) { `object`, _ ->
                        try {
                            emailField.setText(`object`.getString("email"))
                            LoginManager.getInstance().logOut()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                    val parameters = Bundle()
                    parameters.putString("fields", "id, name, first_name, last_name, picture.type(large), email, friends, birthday, age_range, location, gender")
                    request.parameters = parameters
                    request.executeAsync()
                }

                override fun onCancel() {
                }

                override fun onError(error: FacebookException) {
                    Log.e(TAG, "Facebook login error - error is:" + error.localizedMessage)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager?.onActivityResult(requestCode, resultCode, data)
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(loginStateReceiver)
        EventBus.getDefault().unregister(passwordResetReceiver)
    }

    override fun onLogout() {}

    override fun onLoginAnonymously() {}

    override fun onLogin(user: User) {
        progressBar.visibility = View.GONE
        submitButtonLabel.visibility = View.VISIBLE
        EventBus.getDefault().post(UserRepository.getInstance().user)
        Utility.hideKeyboard(activity)
        activity?.onBackPressed()
    }

    override fun onLoginError(error: Error) {
        progressBar.visibility = View.GONE
        submitButtonLabel.visibility = View.VISIBLE
        AlertDialogManager.getInstance().showAlertDialog(
                context!!.resources.getString(R.string.login_failed),
                context!!.resources.getString(R.string.password_try_again), null,
                View.OnClickListener {
                    activity?.onBackPressed()
                    EventBus.getDefault().post(FragmentEvent(LoginFragment::class.java))
                }
        )
    }

    override fun onPasswordResetRequest() {
        AlertDialogManager.getInstance().showAlertDialog(
                context!!.resources.getString(R.string.password_reset),
                context!!.resources.getString(R.string.reset_check_email), null, View.OnClickListener // Confirm
        {
            activity?.onBackPressed()
        })
    }

    override fun onPasswordResetRequestError(error: Error) {
        AlertDialogManager.getInstance().showAlertDialog(
                context!!.resources.getString(R.string.error),
                context!!.resources.getString(R.string.email_enter_valid), null, View.OnClickListener // Confirm
        {
            activity?.onBackPressed()
            forgotOnClick()
        })
    }

    @Optional
    @OnClick(R.id.facebook_button)
    fun setLoginFacebook() {
        facebookButton.performClick()
    }

    companion object {
        private const val TAG = "LOGIN FRAGMENT"
    }
}