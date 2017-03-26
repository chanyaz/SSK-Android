package tv.sportssidekick.sportssidekick.model.achievements;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;

import java.util.List;

import tv.sportssidekick.sportssidekick.model.GSConstants;

import static tv.sportssidekick.sportssidekick.model.Model.createRequest;

/**
 * Created by Filip on 3/26/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class AchievementManager {


    /**
     * this function gets the list of user achievements names and the list of all the
     * system achievements info
     *
     **/

    private static AchievementManager instance;
    private final ObjectMapper mapper; // jackson's object mapper
    private List<Achievement> systemAchievements;

    public static AchievementManager getInstance(){
        if(instance==null){
            instance = new AchievementManager();
        }
        return instance;
    }

    private AchievementManager(){
        mapper = new ObjectMapper();
    }

    public Task<List<Achievement>> getUserAchievements(){
        final TaskCompletionSource<List<Achievement>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object objectAchievemnts = response.getScriptData().getBaseData().get(GSConstants.ACHIEVEMENTS);
                    systemAchievements = mapper.convertValue(objectAchievemnts, new TypeReference<List<Achievement>>(){});
                    Object objectUserAchievements = response.getScriptData().getBaseData().get(GSConstants.USER_ACHIEVEMENTS);
                    List<Achievement> userAchievements = mapper.convertValue(objectUserAchievements, new TypeReference<List<Achievement>>(){});
                    source.setResult(userAchievements);
                }  else {
                    source.setException(new Exception());
                }
            }
        };
        createRequest("achievementGetUserAchievements")
                .send(consumer);
        return source.getTask();
    }

    public List<Achievement> getSystemAchievements() {
        return systemAchievements;
    }
}
