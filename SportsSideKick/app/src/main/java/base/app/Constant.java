package base.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.app.fragment.instance.ChatFragment;
import base.app.fragment.instance.ClubRadioFragment;
import base.app.fragment.instance.ClubTVFragment;
import base.app.fragment.instance.NewsFragment;
import base.app.fragment.instance.RumoursFragment;
import base.app.fragment.instance.StatisticsFragment;
import base.app.fragment.instance.StoreFragment;
import base.app.fragment.instance.VideoChatFragment;
import base.app.fragment.instance.WallFragment;

/**
 * Created by Djordje Krutil on 6.12.2016..
 */
public class Constant {

    public static final int LOGIN_TEXT_TIME= 3000;
    public static final String RADIO_VOLUME = "RADIO_VOLUME";

    public static final int REQUEST_CODE_CHAT_IMAGE_CAPTURE = 101;
    public static final int REQUEST_CODE_CHAT_IMAGE_PICK = 102;
    public static final int REQUEST_CODE_CHAT_VIDEO_CAPTURE = 103;

    public static final int REQUEST_CODE_EDIT_PROFILE_IMAGE_CAPTURE = 104;
    public static final int REQUEST_CODE_EDIT_PROFILE_IMAGE_PICK = 105;

    public static final int REQUEST_CODE_POST_IMAGE_CAPTURE = 106;
    public static final int REQUEST_CODE_POST_IMAGE_PICK = 107;
    public static final int REQUEST_CODE_POST_VIDEO_CAPTURE = 108;

    public static final int REQUEST_CODE_CHAT_CREATE_IMAGE_CAPTURE = 109;
    public static final int REQUEST_CODE_CHAT_CREATE_IMAGE_PICK = 111;

    public static final int REQUEST_CODE_CHAT_EDIT_IMAGE_CAPTURE = 109;
    public static final int REQUEST_CODE_CHAT_EDIT_IMAGE_PICK = 111;

    public static final String IS_FIRST_TIME = "IS_FIRST_TIME";

    public static final String NOTIFICATION_DATA = "SSK_PUSH_NOTIFICATION_DATA";

    public static final List<Class> PHONE_MENU_OPTIONS = Collections.unmodifiableList(
        new ArrayList<Class>() {{
            add(WallFragment.class);
            add(ChatFragment.class);
            add(NewsFragment.class);
            add(StatisticsFragment.class);
            add(RumoursFragment.class);
            add(ClubRadioFragment.class);
            add(StoreFragment.class);
            add(ClubTVFragment.class);
            add(VideoChatFragment.class);
    }});
}
