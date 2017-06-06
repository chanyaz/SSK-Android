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
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */
public class Constant {

//    KEVIN-SSK
//    public static final String GS_API_KEY = "G310534qEf4N";
//    public static final String GS_API_SECRET = "iUeAGN7Sp8rka4lmBT5UNdfv2JBDVhSz";

    public static final String GS_API_KEY = "Z306867fEYlY";
    public static final String GS_API_SECRET = "Rx34NFmkNwSah80VZFGPmTXmCesdK5Sy";

    public static final int LOGIN_TEXT_TIME= 3000;
    public static final String YOUTUBE_API_KEY = "AIzaSyAVYoyvyouNeFJBvlLG9yQMfIuQ3EaNadY";
    public static final String VIDEO_CHAT_TOKEN_URL = "https://ssk-vc-tokengen-production.herokuapp.com";
    public static final String RADIO_VOLUME = "RADIO_VOLUME";

    public static final String TWITTER_KEY = "dTTwpATNlNQm57u4hnBbwrqr8";
    public static final String TWITTER_SECRET = "w07PsNvp94YRx3XTnyRTetki7LyXFRNTFVBE8UfSNycBmWTNGE";


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
    public static final String NOTIFICATION_MESSAGE = "message";
    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_BODY = "body";
    public static final String NOTIFICATION_TYPE = "notificationType";

    public static final List<Class> CLASS_LIST = Collections.unmodifiableList(
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
                // etc
    }});




}
