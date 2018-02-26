package base.app.util.commons

import android.annotation.SuppressLint
import android.os.Build
import android.os.SystemClock
import base.app.Application.getDefaultTracker
import base.app.data.Model
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import java.util.*

/**
 * Implementation of analytics interface
 */
private fun sendEvent(action: String,
                      params: Map<String, String> = emptyMap()) {
    val eventBuilder = EventBuilder().apply {
        setAction(action)
        setAll(params)
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
@Suppress("DEPRECATION")
fun trackAppOpened() {
    sendEventWithSession("AppStart", mapOf(
            "BuildID" to Build.SERIAL,
            "DeviceID" to Model.getInstance().deviceId))
    launchTime = SystemClock.elapsedRealtime()
}

var launchTime: Long = 0

fun trackDeepLinkOpened() {
    sendEventWithSession("OpenedFromLink", mapOf(
            // TODO: Supply 'SocialPostID'
    ))
}

fun trackAppClosed() {
    val exitTime = SystemClock.elapsedRealtime()
    val delta = exitTime - launchTime
    val sessionDuration = delta / 1000

    sendEventWithSession("LeaveApp", mapOf(
            "TimeID" to sessionDuration.toString()
    ))
}


fun trackUserRegistered() {
    sendEventWithSession("RegistrationConfirm", mapOf(
            // TODO: Supply 'typeID' - user registers using email, or fb etc
    ))
}

fun trackUserLoggedIn() {
    sendEventWithSession("LoginConfirm")
}


fun trackHomeScreenDisplayed() {
    sendEventWithSession("MainScreenDisplay")
}

fun trackSettingsChanged(settingName: String) {
    sendEventWithSession("SettingsChange", mapOf(
            "SettingID" to settingName
    ))
}

fun trackWallDisplayed() {
    sendEventWithSession("WallSelected", mapOf(

    ))
}


fun trackPostViewed() {
    sendEventWithSession("WallPostOpen", mapOf(

    ))
}

fun trackPostComposing() {
    sendEventWithSession("WallPostStarted", mapOf(

    ))
}

fun trackPostSubmitted() {
    sendEventWithSession("WallPostSent", mapOf(

    ))
}

fun trackPostCommentSubmitted() {
    sendEventWithSession("WallComment", mapOf(

    ))
}

fun trackPostLiked() {
    sendEventWithSession("WallPostLiked", mapOf(

    ))
}

fun trackPostShared() {
    sendEventWithSession("WallPostShared", mapOf(

    ))
}


fun trackChatOpened() {
    sendEventWithSession("IMSSelected", mapOf(

    ))
}

fun trackChatCreated() {
    sendEventWithSession("IMSChatCreated", mapOf(

    ))
}

fun trackChatJoined() {
    sendEventWithSession("IMSChatJoined", mapOf(

    ))
}

fun trackMessageSent() {
    sendEventWithSession("IMSmessage", mapOf(

    ))
}


fun trackTvOpened() {
    sendEventWithSession("TVSelected", mapOf(

    ))
}

fun trackTvShowStarted() {
    sendEventWithSession("TVShowStarted", mapOf(

    ))
}

fun trackTvShowEnded() {
    sendEventWithSession("TVShowEnds", mapOf(

    ))
}


fun trackNewsSectionOpened() {
    sendEventWithSession("NewsSelected", mapOf(

    ))
}

fun trackNewsItemOpened() {
    sendEventWithSession("NewsItemSelected", mapOf(

    ))
}

fun trackNewsCommentSubmitted() {
    sendEventWithSession("NewsItemComment", mapOf(

    ))
}

fun trackNewsShared() {
    sendEventWithSession("NewsItemShared", mapOf(

    ))
}


fun trackSocialSectionOpened() {
    sendEvent("SocialSelected", mapOf(

    ))
}

fun trackSocialItemOpened() {
    sendEvent("SocialPostOpen")
}

fun trackSocialItemPinned() {
    sendEvent("SocialPostSent")
}

fun trackSocialLiked() {
    sendEvent("SocialPostLiked")
}

fun trackSocialShared() {
    sendEvent("SocialPostShared")
}

fun trackSocialCommentSubmitted() {
    sendEvent("SocialPostComment")
}


fun trackStoreOpened() {
    sendEventWithSession("ShopSelected", mapOf(

    ))
}

fun trackProductViewed() {
    sendEvent("ShopPageSelected")
}

fun trackStatsOpened() {
    sendEventWithSession("StatsSelected", mapOf(

    ))
}


fun trackFriendsViewed() {
    sendEventWithSession("FriendSelected", mapOf(

    ))
}

fun trackFriendRequestSent() {
    sendEventWithSession("FriendAdded")
}

fun trackFriendRequestAccepted() {
    sendEvent("FriendApproved")
}

fun trackFriendRequestRejected() {
    sendEvent("FriendRejected")
}

fun trackUnfollowed() {
    sendEvent("FriendUnfollow")
}

fun trackUnfriended() {
    sendEvent("FriendRemoved")
}


fun trackWebviewDisplayed() {
    sendEvent("<WebView>Selected")
}

fun trackWebviewPageSelected() {
    sendEvent("<WebView>PageSelected")
}
