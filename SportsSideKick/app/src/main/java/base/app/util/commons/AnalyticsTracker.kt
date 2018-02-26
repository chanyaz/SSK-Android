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
            "FeatureID" to "Wall"
    ))
}


fun trackPostViewed(postId: String) {
    sendEventWithSession("WallPostOpen", mapOf(
            "PostID" to postId
    ))
}

fun trackPostComposing() {
    sendEventWithSession("WallPostStarted", mapOf(
            "TypeID" to "Creating new post"
    ))
}

fun trackPostSubmitted(source: String) {
    sendEventWithSession("WallPostSent", mapOf(
            "TypeID" to source
    ))
}

fun trackPostCommentSubmitted(postId: String) {
    sendEventWithSession("WallComment", mapOf(
            "PostID" to postId
    ))
}

fun trackPostLiked(postId: String) {
    sendEventWithSession("WallPostLiked", mapOf(
            "PostID" to postId
    ))
}

fun trackPostShared(postId: String) {
    sendEventWithSession("WallPostShared", mapOf(
            "PostID" to postId
    ))
}


fun trackChatOpened() {
    sendEventWithSession("IMSSelected", mapOf(
            "FeatureID" to "IMS"
    ))
}

fun trackChatCreated() {
    sendEventWithSession("IMSChatCreated", mapOf(
            "TypeID" to "Private group" // Only private chats are currently allowed
    ))
}

fun trackChatJoined() {
    sendEventWithSession("IMSChatJoined", mapOf(
            "TypeID" to "Private group" // Only private chats are currently allowed
    ))
}

fun trackMessageSent(chatId: String) {
    sendEventWithSession("IMSmessage", mapOf(
            "TypeID" to "Private group", // Only private chats are currently allowed
            "chatID" to chatId
    ))
}


fun trackTvOpened() {
    sendEventWithSession("TVSelected", mapOf(
            "FeatureID" to "TV"
    ))
}

fun trackVideoStarted(videoId: String) {
    sendEventWithSession("TVShowStarted", mapOf(
            "showID" to videoId
    ))
}

fun trackVideoEnded(videoId: String) {
    sendEventWithSession("TVShowEnds", mapOf(
            "showID" to videoId
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

fun trackNewsCommentSubmitted(postId: String) {
    sendEventWithSession("NewsItemComment", mapOf(

    ))
}

fun trackNewsShared(postId: String) {
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

fun trackSocialLiked(postId: String) {
    sendEvent("SocialPostLiked")
}

fun trackSocialShared(postId: String) {
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
