package tv.sportssidekick.sportssidekick.fragment.instance;


import android.util.Base64;

import java.io.UnsupportedEncodingException;

import tv.sportssidekick.sportssidekick.R;
import tv.sportssidekick.sportssidekick.fragment.BaseFragment;

/**
 * Created by Filip on 12/6/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 *
 * A simple {@link BaseFragment} subclass.
 */

public class QuizFragment extends StoreFragment {

    public QuizFragment() {
        // Required empty public constructor
    }

    @Override
    protected void setupFragment(){
        // TODO Fill with proper data when User data is ready
        String userId = "123456789";
        String firstName = "123456789";
        String lastName = "123456789";
        String nick = "123456789";

        try {
            String base64userId = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);
            String base64firstName = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);
            String base64lastName = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);
            String base64nick = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);

            String urlTemp = getResources().getString(R.string.quiz_url);

            StringBuilder urlBuilder = new StringBuilder(urlTemp);
            urlBuilder
                    .append("?userid=")
                    .append(base64userId)
                    .append("&fname=")
                    .append(base64firstName)
                    .append("&lname=")
                    .append(base64lastName)
                    .append("&nick=")
                    .append(base64nick);

            url = urlBuilder.toString();

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        withNavigation = false;
    }

}
