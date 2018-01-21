package base.app.util.commons

import base.app.ui.fragment.content.StoreFragment
import base.app.ui.fragment.content.news.NewsFragment
import base.app.ui.fragment.content.news.RumoursFragment
import base.app.ui.fragment.content.tv.TvFragment
import base.app.ui.fragment.content.wall.WallFragment
import base.app.ui.fragment.other.ChatFragment
import base.app.ui.fragment.other.StatisticsFragment
import base.app.ui.fragment.stream.RadioFragment
import base.app.ui.fragment.stream.VideoChatFragment

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
object Constants {

    const val LOGIN_TEXT_TIME = 3000
    const val RADIO_VOLUME = "RADIO_VOLUME"

    const val REQUEST_CODE_CHAT_IMAGE_CAPTURE = 101
    const val REQUEST_CODE_CHAT_IMAGE_PICK = 102
    const val REQUEST_CODE_CHAT_VIDEO_CAPTURE = 103

    const val REQUEST_CODE_EDIT_PROFILE_IMAGE_CAPTURE = 104
    const val REQUEST_CODE_EDIT_PROFILE_IMAGE_PICK = 105

    const val REQUEST_CODE_CHAT_CREATE_IMAGE_CAPTURE = 109
    const val REQUEST_CODE_CHAT_CREATE_IMAGE_PICK = 111

    const val REQUEST_CODE_CHAT_EDIT_IMAGE_CAPTURE = 109
    const val REQUEST_CODE_CHAT_EDIT_IMAGE_PICK = 111

    const val IS_FIRST_TIME = "IS_FIRST_TIME"

    const val NOTIFICATION_DATA = "SSK_PUSH_NOTIFICATION_DATA"
}
