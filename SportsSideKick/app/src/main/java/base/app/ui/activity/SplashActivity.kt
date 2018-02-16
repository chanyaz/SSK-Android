package base.app.ui.activity

import android.R.anim
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        goHome()
    }

    private fun goHome() {
        startActivity<MainActivity>()
        overridePendingTransition(
                anim.fade_in,
                anim.fade_out
        )
        finish()
    }
}
