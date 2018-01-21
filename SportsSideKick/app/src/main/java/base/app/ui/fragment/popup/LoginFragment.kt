package base.app.ui.fragment.popup

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import base.app.BuildConfig
import base.app.R
import base.app.data.user.LoginStateReceiver
import base.app.data.user.LoginStateReceiver.LoginListener
import base.app.data.user.PasswordResetReceiver
import base.app.data.user.PasswordResetReceiver.PasswordResetListener
import base.app.data.user.User
import base.app.ui.adapter.profile.AccountCreatingAdapter
import base.app.util.commons.Connection
import base.app.util.commons.UserRepository
import base.app.util.commons.Utility
import base.app.util.events.FragmentEvent
import base.app.util.ui.AlertDialogManager
import base.app.util.ui.BaseFragment
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import kotlinx.android.synthetic.main.fragment_login.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import java.util.*

class LoginFragment : BaseFragment(R.layout.fragment_login),
        LoginListener, PasswordResetListener {

    private var callbackManager: CallbackManager? = null
    private var loginStateReceiver: LoginStateReceiver? = null
    private var passwordResetReceiver: PasswordResetReceiver? = null
    @BindView(R.id.login_text)
    internal var loginText: TextView? = null
    @BindView(R.id.forgot_password_container)
    internal var forgotPasswordContainer: RelativeLayout? = null
    @BindView(R.id.content_container)
    internal var loginContainer: RelativeLayout? = null
    @BindView(R.id.forgot_password_back)
    internal var forgotPasswordBack: ImageView? = null
    @BindView(R.id.image_logo)
    internal var imageLogo: ImageView? = null
    @BindView(R.id.logo_fq_image)
    internal var logoFqImage: ImageView? = null
    @BindView(R.id.image_player)
    internal var imagePlayer: ImageView? = null
    @BindView(R.id.forgot_button)
    internal var forgotButton: TextView? = null
    @BindView(R.id.title_text)
    internal var titleText: TextView? = null
    @BindView(R.id.bottom_buttons_container_reset)
    internal var resetButtonContainer: RelativeLayout? = null
    @BindView(R.id.bottom_buttons_container_login)
    internal var loginButtonContainer: RelativeLayout? = null
    @BindView(R.id.login_forgot_pass_email)
    internal var emailForgotPassword: EditText? = null
    @BindView(R.id.sign_up_facebook)
    internal var loginButton: LoginButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (UserRepository.getInstance().isRealUser && Utility.isPhone(activity)!!) {
            activity!!.onBackPressed()
        }
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (BuildConfig.DEBUG) {
            emailField.setText("alexsheikodev3@gmail.com")
            passwordField.setText("temppass")
        }
        ButterKnife.bind(this, view)
        this.loginStateReceiver = LoginStateReceiver(this)
        this.passwordResetReceiver = PasswordResetReceiver(this)
        initFacebook()

        if (titleText != null) {
            titleText!!.text = Html.fromHtml(getString(R.string.slogan))
        }

        forgotPasswordBack!!.setOnClickListener {
            loginContainer!!.visibility = View.VISIBLE
            if (loginButtonContainer != null) {
                loginButtonContainer!!.visibility = View.VISIBLE
            }
            resetButtonContainer!!.visibility = View.INVISIBLE
            forgotPasswordContainer!!.visibility = View.GONE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
    }

    fun initFacebook() {
        if (loginButton != null) {
            loginButton!!.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_birthday", "user_photos"))
            callbackManager = CallbackManager.Factory.create()
            loginButton!!.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    // App code
                    val request = GraphRequest.newMeRequest(
                            loginResult.accessToken
                    ) { `object`, response ->
                        // Application code
                        try {
                            emailField!!.setText(`object`.getString("email"))
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
                    Log.d(TAG, "Facebook login canceled!")
                }

                override fun onError(error: FacebookException) {
                    Log.d(TAG, "Facebook login error - error is:" + error.localizedMessage)
                }
            })
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(loginStateReceiver)
        EventBus.getDefault().unregister(passwordResetReceiver)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    @OnClick(R.id.bottom_buttons_container_login)
    fun loginOnClick() {
        val email = emailField!!.text.toString()
        val password = passwordField!!.text.toString()
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(context, context!!.resources.getString(R.string.required_credentials), Toast.LENGTH_SHORT).show()
            return
        }
        if (!Connection.getInstance().alertIfNotReachable(activity
                ) { activity!!.onBackPressed() }) {
            return
        }
        UserRepository.getInstance().login(email, password)
        loginText!!.visibility = View.INVISIBLE
        progressBar!!.visibility = View.VISIBLE
    }

    @Optional
    @OnClick(R.id.reset_text)
    fun forgotPasswordOnClick() {
        val email = emailForgotPassword!!.text.toString()
        UserRepository.getInstance().resetPassword(email)
        activity!!.onBackPressed()
    }

    @Optional
    @OnClick(R.id.forgot_button)
    fun forgotOnClick() {
        if (Utility.isTablet(activity)) {
            if (loginButtonContainer != null) {
                loginButtonContainer!!.visibility = View.GONE
            }
            loginContainer!!.visibility = View.GONE
        } else {
            loginContainer!!.visibility = View.INVISIBLE
            if (loginButtonContainer != null) {
                loginButtonContainer!!.visibility = View.INVISIBLE
            }
        }
        resetButtonContainer!!.visibility = View.VISIBLE
        forgotPasswordContainer!!.visibility = View.VISIBLE
    }

    override fun onLogout() {}

    override fun onLoginAnonymously() {}

    override fun onLogin(user: User) {
        progressBar!!.visibility = View.GONE
        loginText!!.visibility = View.VISIBLE
        EventBus.getDefault().post(UserRepository.getInstance().user) //catch in Lounge Activity
        Utility.hideKeyboard(activity)
        if (Utility.isTablet(activity)) {
            activity!!.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)
            EventBus.getDefault().post(FragmentEvent(AccountCreatingAdapter::class.java))
        } else {
            activity!!.onBackPressed()
            //EventBus.getDefault().savePost(new FragmentEvent(WallFragment.class));
        }
    }

    override fun onLoginError(error: Error) {
        progressBar!!.visibility = View.GONE
        loginText!!.visibility = View.VISIBLE
        AlertDialogManager.getInstance().showAlertDialog(
                context!!.resources.getString(R.string.login_failed),
                context!!.resources.getString(R.string.password_try_again), null,
                View.OnClickListener {
                    activity!!.onBackPressed()
                    EventBus.getDefault().post(FragmentEvent(LoginFragment::class.java))
                }
        )
    }

    override fun onPasswordResetRequest() {
        AlertDialogManager.getInstance().showAlertDialog(
                context!!.resources.getString(R.string.password_reset),
                context!!.resources.getString(R.string.reset_check_email), null, View.OnClickListener // Confirm
        {
            activity!!.onBackPressed()
        })
    }

    override fun onPasswordResetRequestError(error: Error) {
        AlertDialogManager.getInstance().showAlertDialog(
                context!!.resources.getString(R.string.error),
                context!!.resources.getString(R.string.email_enter_valid), null, View.OnClickListener // Confirm
        {
            activity!!.onBackPressed()
            forgotOnClick()
        })
    }

    @Optional
    @OnClick(R.id.facebook_button)
    fun setLoginFacebook() {
        if (loginButton != null) {
            loginButton!!.performClick()
        }
    }

    companion object {
        private const val TAG = "LOGIN FRAGMENT"
    }
}
