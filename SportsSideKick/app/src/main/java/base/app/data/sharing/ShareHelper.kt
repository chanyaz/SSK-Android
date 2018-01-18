package base.app.data.sharing

import android.content.Intent
import android.content.Intent.*
import base.app.data.wall.WallBase
import org.greenrobot.eventbus.EventBus

/**
 * Created by Filip on 4/4/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
object ShareHelper {

    @JvmStatic
    fun share(item: WallBase) {
        val intent = Intent(ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(EXTRA_TEXT, item.url)
        intent.putExtra(EXTRA_SUBJECT, item.title)
        EventBus.getDefault().post(NativeShareEvent(intent))
    }
}