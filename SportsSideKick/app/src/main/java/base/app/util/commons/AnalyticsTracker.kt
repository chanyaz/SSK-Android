package base.app.util.commons

import base.app.Application.getDefaultTracker
import base.app.data.Model
import com.google.android.gms.analytics.HitBuilders
import java.util.*

/**
 * Implementation of analytics
 */

fun sendSessionEvent(action: String) {
    Model.getInstance().userInfo
    Date()

    sendEventWithParams(action)
}

fun sendGenericEvent(action: String) {
    sendEventWithParams(action)
}

private fun sendEventWithParams(action: String) {
    getDefaultTracker()
            .send(HitBuilders.EventBuilder()
                    .setAction(action)
                    .build())
}

/**
 * Interface for analytics
 * */

fun trackAppOpened() {}

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
