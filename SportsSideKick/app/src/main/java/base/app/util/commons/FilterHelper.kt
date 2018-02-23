package base.app.util.commons

import base.app.data.GSConstants
import base.app.data.im.ImsMessage

fun filterMessages(list: ArrayList<ImsMessage>): List<ImsMessage> {
    return list.filterNot { it.type == GSConstants.UPLOAD_TYPE_IMAGE && it.imageUrl == null }
            .filterNot { it.type == GSConstants.UPLOAD_TYPE_VIDEO && it.vidUrl == null }
            .filterNot { it.type == GSConstants.UPLOAD_TYPE_TEXT && (it.text == null || it.text.isBlank()) }
            .filterNot {
                // old broken messages stuck in loading mode
                it.id == "59e48ffac5b51e04d4e2534a"
                        || it.id == "59f6fa57c5b51e04d4b790fe"
                        || it.id == "59f6fbebcc302a04d34bd5bd"
            }
}