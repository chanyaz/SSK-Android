package base.app.util.commons

import base.app.Application.getDefaultTracker
import base.app.data.Model
import com.google.android.gms.analytics.HitBuilders.EventBuilder
import java.util.*

/**
 * Implementation of analytics interface
 */

private fun sendEvent(action: String,
                      params: Map<String, String> = emptyMap()) {
    val builder = EventBuilder().apply {
        setAction(action)
        params.forEach { set(it.key, it.value) }
    }
    getDefaultTracker().send(builder.build())
}

fun sendEventWithSession(action: String) {
    val user = Model.getInstance().userInfo

    val params = mapOf(
            "Date/Time" to Date().toString(),
            "anonymousUserID" to user.userId,
            "SessionID" to user.sessionId
    )

    sendEvent(action, params)
}

/**
 * Interface for analytics
 * */

fun trackAppOpened() {
    sendEventWithSession("AppStart")
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
