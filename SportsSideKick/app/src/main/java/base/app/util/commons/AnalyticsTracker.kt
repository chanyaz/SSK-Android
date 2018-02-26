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
}

fun trackDeepLinkOpened() {
    sendEventWithSession("OpenedFromLink", mapOf(
            // TODO: Supply 'SocialPostID'
    ))
}

fun trackAppClosed() {
    sendEventWithSession("LeaveApp", mapOf(
            // TODO: Supply 'TimeID', time spent in app
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

fun trackSettingsChanged() {
    sendEventWithSession("SettingsChange")
}

fun trackWallDisplayed() {
    sendEventWithSession("WallSelected")
}


fun trackPostViewed() {
    sendEventWithSession("WallPostOpen")
}

fun trackPostComposing() {
    sendEventWithSession("WallPostStarted")
}

fun trackPostSubmitted() {
    sendEventWithSession("WallPostSent")
}

fun trackPostCommentSubmitted() {
    sendEventWithSession("WallComment")
}

fun trackPostLiked() {
    sendEventWithSession("WallPostLiked")
}

fun trackPostShared() {
    sendEventWithSession("WallPostShared")
}


fun trackChatOpened() {
    sendEventWithSession("IMSSelected")
}

fun trackChatCreated() {
    sendEventWithSession("IMSChatCreated")
}

fun trackChatJoined() {
    sendEventWithSession("IMSChatJoined")
}

fun trackMessageSent() {
    sendEventWithSession("IMSmessage")
}


fun trackTvOpened() {
    sendEventWithSession("TVSelected")
}

fun trackTvShowStarted() {
    sendEventWithSession("TVShowStarted")
}

fun trackTvShowEnded() {
    sendEventWithSession("TVShowEnds")
}


fun trackNewsSectionOpened() {
    sendEventWithSession("NewsSelected")
}

fun trackNewsItemOpened() {
    sendEventWithSession("NewsItemSelected")
}

fun trackNewsCommentSubmitted() {
    sendEventWithSession("NewsItemComment")
}

fun trackNewsShared() {
    sendEventWithSession("NewsItemShared")
}


fun trackSocialSectionOpened() {
    sendEvent("SocialSelected")
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


fun trackShopOpened() {
    sendEventWithSession("ShopSelected")
}

fun trackProductViewed() {
    sendEvent("ShopPageSelected")
}

fun trackStatsOpened() {
    sendEventWithSession("StatsSelected")
}


fun trackFriendsViewed() {
    sendEventWithSession("FriendSelected")
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
