package base.app.fragment.instance;


import android.util.Base64;

import org.greenrobot.eventbus.Subscribe;

import java.io.UnsupportedEncodingException;

import base.app.R;
import base.app.fragment.BaseFragment;
import base.app.model.Model;
import base.app.model.user.UserEvent;
import base.app.model.user.UserInfo;

import static base.app.model.user.UserEvent.Type.onDetailsUpdated;
import static base.app.model.user.UserEvent.Type.onLogin;

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
        UserInfo userInfo = Model.getInstance().getUserInfo();
        String userId = userInfo.getUserId();
        String firstName = userInfo.getFirstName();
        String lastName = userInfo.getFirstName();
        String nick = userInfo.getNicName();

        try {
            if (userId != null && firstName!=null && lastName!=null && nick!=null)
            {
                String base64userId = Base64.encodeToString(userId.getBytes("UTF-8"), Base64.DEFAULT);
                String base64firstName = Base64.encodeToString(firstName.getBytes("UTF-8"), Base64.DEFAULT);
                String base64lastName = Base64.encodeToString(lastName.getBytes("UTF-8"), Base64.DEFAULT);
                String base64nick = Base64.encodeToString(nick.getBytes("UTF-8"), Base64.DEFAULT);

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
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        withNavigation = false;
    }

    @Subscribe
    public void onUserLogin(UserEvent event)
    {
        if (event.getType()==onDetailsUpdated || event.getType()== onLogin)
        {
            setupFragment();
        }
    }


}