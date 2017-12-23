package base.app.helper

import base.app.activity.BaseActivity
import base.app.fragment.FragmentEvent
import base.app.fragment.instance.*
import base.app.model.notifications.ExternalNotificationEvent
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@Subscribe(threadMode = ThreadMode.MAIN)
fun BaseActivity.handleNotificationEvent(event: ExternalNotificationEvent) {
    val notificationData = event.data
    if (event.isFromBackground) {
        if (notificationData.containsKey("chatId")) {
            EventBus.getDefault().post(FragmentEvent(ChatFragment::class.java))
        } else if (notificationData.containsKey("wallId") && notificationData.containsKey("postId")) {
            val postId = notificationData["postId"]
            val wallId = notificationData["wallId"]
            val wallItemFragmentEvent = FragmentEvent(WallItemFragment::class.java)
            wallItemFragmentEvent.id = "$postId$$$$wallId"
            // TODO - Load wall item before displaying it ( or this is handled in fragment? )
            EventBus.getDefault().post(wallItemFragmentEvent)
        } else if (notificationData.containsKey("newsItem") && notificationData.containsKey("newsType")) {
            if ("official" == notificationData["newsType"]) {
                EventBus.getDefault().post(NewsFragment::class.java)
            } else {
                EventBus.getDefault().post(RumoursFragment::class.java)
            }
            if ("-1" != notificationData["newsItem"]) {
                val fe = FragmentEvent(NewsItemFragment::class.java)
                val id = notificationData["newsItem"]
                if ("official" == notificationData["newsType"]) {
                    fe.id = "UNOFFICIAL$$$$id"
                } else {
                    fe.id = id
                }
                // TODO - Load news item before displaying it
                //EventBus.getDefault().post(fe);
            }

        } else if (notificationData.containsKey("statsItem")) {
            EventBus.getDefault().post(StatisticsFragment::class.java)
        } else if (notificationData.containsKey("conferenceId")) {
            val conferenceId = notificationData["conferenceId"]
            val fe = FragmentEvent(VideoChatFragment::class.java)
            fe.id = conferenceId
            EventBus.getDefault().post(fe)
        }
    } else {
        // TODO Handle Push notifications while app is active
    }
}