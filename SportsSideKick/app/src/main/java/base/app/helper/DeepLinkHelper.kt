package base.app.helper

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import base.app.Constant
import base.app.activity.BaseActivity
import base.app.fragment.FragmentEvent
import base.app.fragment.instance.NewsDetailFragment
import base.app.fragment.instance.WallItemFragment
import base.app.model.notifications.ExternalNotificationEvent
import base.app.model.sharing.SharingManager
import base.app.util.Utility
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pixplicity.easyprefs.library.Prefs
import org.greenrobot.eventbus.EventBus
import java.io.IOException

fun BaseActivity.handleStartingIntent(intent: Intent) {
    val isFistTimeStartingApp = Prefs.getBoolean(Constant.IS_FIRST_TIME, true)
    val extras = intent.extras
    if (isFistTimeStartingApp) {
        // in case we are starting this app for first time, ignore intent's data
        Prefs.putBoolean(Constant.IS_FIRST_TIME, false)
    } else if (extras != null && !extras.isEmpty) {
        var savedIntentData = Bundle()
        // make sure we are not handling the same intent
        if (!Utility.checkIfBundlesAreEqual(savedIntentData, extras)) {
            val mapper = ObjectMapper()
            val notificationData = extras.getString(Constant.NOTIFICATION_DATA, "")
            try {
                val dataMap = mapper.readValue<Map<String, String>>(
                        notificationData, object : TypeReference<Map<String, String>>() {

                })
                handleNotificationEvent(ExternalNotificationEvent(dataMap, true))
                savedIntentData = extras
            } catch (e: IOException) {
                Log.e(BaseActivity.TAG, "Error parsing notification data!")
                e.printStackTrace()
            }

        }
    }
    val action = intent.action
    if (Intent.ACTION_VIEW == action) {
        val deeplink = intent.data
        Log.d(BaseActivity.TAG, "deeplink : " + deeplink!!.toString())
        handleDeepLink(deeplink)
    }
}

fun BaseActivity.handleDeepLink(uri: Uri?) {
    if (uri != null) {
        val lastPathSegment = uri.lastPathSegment
        val parts = lastPathSegment.split(":")
        if (parts != null && parts!!.size == 3) {
            val clubId = parts!![2]
            val postType = parts!![1]
            val postId = parts!![0] // WallPost ?
            Log.d(BaseActivity.TAG, "Post id is : " + postId)
            if (SharingManager.ItemType.WallPost.name == postType) {
                val wallItemFragmentEvent = FragmentEvent(WallItemFragment::class.java)
                wallItemFragmentEvent.id = postId + "$$$"
                EventBus.getDefault().post(wallItemFragmentEvent)
            } else if (SharingManager.ItemType.News.name == postType) {
                val newsItemFragmentEvent = FragmentEvent(NewsDetailFragment::class.java)
                newsItemFragmentEvent.id = postId
                EventBus.getDefault().post(newsItemFragmentEvent)
            }
        }
    }
}