package base.app.util.commons;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import base.app.ui.fragment.other.ChatFragment;
import base.app.ui.fragment.stream.ClubRadioFragment;
import base.app.ui.fragment.stream.ClubTVFragment;
import base.app.ui.fragment.content.news.NewsListFragment;
import base.app.ui.fragment.content.RumoursFragment;
import base.app.ui.fragment.other.StatisticsFragment;
import base.app.ui.fragment.content.StoreFragment;
import base.app.ui.fragment.stream.VideoChatFragment;
import base.app.ui.fragment.content.WallFragment;

/**
 * Created by Djordje Krutil on 6.12.2016..
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
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
            add(NewsListFragment.class);
            add(StatisticsFragment.class);
            add(RumoursFragment.class);
            add(ClubRadioFragment.class);
            add(StoreFragment.class);
            add(ClubTVFragment.class);
            add(VideoChatFragment.class);
    }});




}
