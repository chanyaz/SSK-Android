package base.app.model.club;

import android.support.annotation.Nullable;
import android.util.Pair;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gamesparks.sdk.GSEventConsumer;
import com.gamesparks.sdk.api.autogen.GSResponseBuilder;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import base.app.BuildConfig;
import base.app.events.ClubTVEvent;
import base.app.model.GSConstants;
import base.app.util.Utility;

import static base.app.ClubConfig.CLUB_ID;
import static base.app.model.GSConstants.CLUB_ID_TAG;
import static base.app.model.Model.createRequest;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * Created by Filip on 1/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ClubModel {

    private static ClubModel instance;
    private final ObjectMapper mapper; // jackson's object mapper
    private List<Station> stations;

    public static ClubModel getInstance() {
        if (instance == null) {
            instance = new ClubModel();
        }
        return instance;
    }

    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final GsonFactory jsonFactory = new GsonFactory();


    private List<Playlist> playlists;
    private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    private HashMap<String, List<Video>> videosHashMap;
    private YouTube youtubeDataApi;

    private ClubModel() {
        mapper = new ObjectMapper();
        playlists = new ArrayList<>();
        videos = new ArrayList<>();
        videosHashMap = new HashMap<>();
        youtubeDataApi = new YouTube.Builder(transport, jsonFactory, null).setApplicationName(BuildConfig.APPLICATION_ID).build();
    }

    public void requestAllPlaylists(String channelId) {
        if (playlists.size() > 0) {
            EventBus.getDefault().post(new ClubTVEvent(null, ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED));
        } else {
            new GetChannelPlaylistsAsyncTask(youtubeDataApi) {
                @Override
                protected void onPostExecute(Pair<String, List<Playlist>> stringListPair) {
                    super.onPostExecute(stringListPair);
                    if (stringListPair != null) {
                        if (stringListPair.second != null) {
                            playlists.addAll(stringListPair.second);
                        }
                        if (Utility.isPhone(getApplicationContext())) { // if on phone, download first playlist immediately
                            ClubModel.getInstance().requestPlaylist(playlists.get(0).getId(), true);
                        }
                        EventBus.getDefault().post(new ClubTVEvent(null, ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED));
                    }
                }
            }.execute(channelId);
        }
    }

    public void requestPlaylist(final String playlistId, final boolean firstTime) {
        if (videosHashMap.containsKey(playlistId)) {
            EventBus.getDefault().post(new ClubTVEvent(playlistId, ClubTVEvent.Type.PLAYLIST_DOWNLOADED));
        } else {
            new GetPlaylistAsyncTask(youtubeDataApi) {
                @Override
                protected void onPostExecute(Pair<String, List<Video>> stringListPair) {
                    super.onPostExecute(stringListPair);
                    List<Video> receivedVideos = stringListPair.second;
                    videosHashMap.put(playlistId, receivedVideos);
                    videos.addAll(receivedVideos);
                    EventBus.getDefault().post(new ClubTVEvent(playlistId, ClubTVEvent.Type.PLAYLIST_DOWNLOADED));
                    if(firstTime){
                        EventBus.getDefault().post(new ClubTVEvent(videos.get(0).getId(), ClubTVEvent.Type.FIRST_VIDEO_DATA_DOWNLOADED));
                    }
                }
            }.execute(playlistId);
        }

    }

    public Playlist getPlaylistById(@Nullable String id) {
        if(id==null){
            return null;
        }
        for (Playlist playlist : playlists) {
            if (id.equals(playlist.getId())) {
                return playlist;
            }
        }
        return null;
    }

    public Video getVideoById(String id) {
        if(id==null){
            return null;
        }
        for (Video video : videos) {
            if (id.equals(video.getId())) {
                return video;
            }
        }
        return null;
    }

    public String getPlaylistId(Video video) {
        for (Map.Entry<String, List<Video>> entry : videosHashMap.entrySet()) {
            if (entry.getValue().contains(video)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<Video> getPlaylistsVideos(String id) {
        return videosHashMap.get(id);
    }

    public List<Playlist> getPlaylists() {
        return playlists;
    }

    public Task<List<Station>> getStations() {
        final TaskCompletionSource<List<Station>> source = new TaskCompletionSource<>();
        GSEventConsumer<GSResponseBuilder.LogEventResponse> consumer = new GSEventConsumer<GSResponseBuilder.LogEventResponse>() {
            @Override
            public void onEvent(GSResponseBuilder.LogEventResponse response) {
                if (!response.hasErrors()) {
                    Object object = response.getScriptData().getBaseData().get(GSConstants.ITEMS);
                    List<Station> receivedStations = mapper.convertValue(object, new TypeReference<List<Station>>() {
                    });
                    while (receivedStations.contains(null)) {
                        receivedStations.remove(null);
                    }
                    stations = receivedStations;
                    source.setResult(receivedStations);
                } else {
                    source.setException(new Exception("There was an error while trying to get stations."));
                }
            }
        };
        createRequest("clubRadioGetStations")
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer);
        return source.getTask();
    }

    public Station getStationByName(String name) {
        if (stations != null) {
            for (Station station : stations) {
                if (station.getName().equals(name)) {
                    return station;
                }
            }
        }
        return null;
    }


}
