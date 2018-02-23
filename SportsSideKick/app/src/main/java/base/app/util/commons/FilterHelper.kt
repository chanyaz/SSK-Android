package base.app.util.commons

import base.app.data.GSConstants
import base.app.data.im.ImsMessage

fun filterMessages(list: ArrayList<ImsMessage>): List<ImsMessage> {
    return list.filterNot { it.type == GSConstants.UPLOAD_TYPE_IMAGE && it.imageUrl == null }
            .filterNot { it.type == GSConstants.UPLOAD_TYPE_VIDEO && it.vidUrl == null }
            .filterNot { it.type == GSConstants.UPLOAD_TYPE_TEXT && (it.text == null || it.text.isBlank()) }
}