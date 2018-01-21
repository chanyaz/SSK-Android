package base.app.data.user.friends;

import android.text.TextUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.ArrayList;
import java.util.List;

import base.app.data.user.User;
import base.app.util.commons.GSConstants;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.util.commons.GSConstants.CLUB_ID_TAG;
import static base.app.ui.fragment.user.login.LoginApi.createRequest;

/**
 * Created by Filip on 12/27/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class PeopleSearchManager {

    private static PeopleSearchManager instance;
    private final ObjectMapper mapper;

    public static PeopleSearchManager getInstance(){
        if(instance==null){
            instance = new PeopleSearchManager();
        }
        return instance;
    }

    private PeopleSearchManager(){
        mapper  = new ObjectMapper();
    }

    /**
     * SearchPeople - get a matching list of SSK users that matches the search string
     * @param  searchString search string
     *
     */

    public Task<List<User>> searchPeople(String searchString, int firstUserIndex){
        final TaskCompletionSource<List<User>> source = new TaskCompletionSource<>();
        final List<User> usersInfo = new ArrayList<>();

        if(TextUtils.isEmpty(searchString)){
            source.setResult(usersInfo);
            return source.getTask();
        }

        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.PLAYER);
                    List<User> friends = mapper.convertValue(object, new TypeReference<List<User>>(){});
                    while(friends.contains(null)){
                        friends.remove(null);
                    }
                    source.setResult(friends);
                }  else {
                    source.setException(new Exception("There was an error while trying to search users."));
                }
            }
        };
        createRequest("searchUsers")
                .setEventAttribute(GSConstants.ENTRY_COUNT,"50")
                .setEventAttribute(GSConstants.OFFSET,firstUserIndex)
                .setEventAttribute(GSConstants.SEARCH_PATTERN,searchString)
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }
}
