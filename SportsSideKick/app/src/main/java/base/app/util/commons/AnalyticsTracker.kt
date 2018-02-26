package base.app.util.commons

import android.annotation.SuppressLint
import android.os.Build
import base.app.Application.getDefaultTracker
import base.app.data.Model
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import java.util.*

/**
 * Implementation of analytics interface
 */

private fun sendEvent(action: String,
                      sessionData: Map<String, String> = emptyMap()) {
    val eventBuilder = EventBuilder().apply {
        setAction(action)
        sessionData.forEach { set(it.key, it.value) }
    }
    getDefaultTracker().send(eventBuilder.build())
}

fun sendEventWithSession(action: String,
                         eventDetails: Map<String, String> = emptyMap()) {
    val user = Model.getInstance().userInfo

    val sessionData = mutableMapOf(
            "Date/Time" to Date().toString(),
            "anonymousUserID" to user.userId,
            "SessionID" to user.sessionId
    )
    val params = mutableMapOf<String, String>()
    params.putAll(sessionData)
    params.putAll(eventDetails)

    sendEvent(action, params)
}

/**
 * Interface for analytics
 */

@SuppressLint("HardwareIds")
fun trackAppOpened() {
    sendEventWithSession("AppStart", mapOf(
            "BuildID" to Build.SERIAL,
            "DeviceID" to Model.getInstance().deviceId))
}

fun trackDeepLinkOpened() {}

fun trackAppClosed() {}


fun trackUserRegistered() {}

fun trackUserLoggedIn() {}


fun trackWallDisplayed() {}

fun trackSettingsChanged() {}

fun trackSectionOpened() {}


fun trackPostViewed() {}

fun trackPostComposing() {}

fun trackPostSubmitted() {}

fun trackPostCommentSubmitted() {}

fun trackPostLiked() {}

fun trackPostShared() {}


fun trackChatOpened() {}

fun trackChatCreated() {}

fun trackChatJoined() {}

fun trackMessageSent() {}


fun trackTvOpened() {}

fun trackTvShowStarted() {}

fun trackTvShowEnded() {}


fun trackNewsSectionOpened() {}

fun trackNewsItemOpened() {}

fun trackNewsCommentSubmitted() {}

fun trackNewsShared() {}


fun trackSocialSectionOpened() {}

fun trackSocialItemOpened() {}

fun trackSocialItemPinned() {}

fun trackSocialLiked() {}

fun trackSocialShared() {}


fun trackShopOpened() {}

fun trackProductViewed() {}

fun trackStatsOpened() {}


fun trackFriendsViewed() {}

fun trackFriendRequestSent() {}

fun trackFriendRequestAccepted() {}

fun trackFriendRequestRejected() {}

fun trackUnfriended() {}

fun trackUnfollowed() {}


fun trackWebviewDisplayed() {}

fun trackWebviewPageSelected() {}
