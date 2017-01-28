package tv.sportssidekick.sportssidekick.model.club;

import android.util.Pair;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tv.sportssidekick.sportssidekick.Constant;
import tv.sportssidekick.sportssidekick.service.ClubTVEvent;


/**
 * Created by Filip on 1/25/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ClubTVModel {

    private static ClubTVModel instance;

    public static ClubTVModel getInstance(){
        if(instance==null){
            instance = new ClubTVModel();
        }
        return instance;
    }
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final GsonFactory jsonFactory = new GsonFactory();


    private List<Playlist> playlists;
    private List<Video> videos;
    private HashMap<String,List<Video>> videosHashMap;
    private  YouTube youtubeDataApi;
    private ClubTVModel(){
        playlists = new ArrayList<>();
        videos = new ArrayList<>();
        videosHashMap = new HashMap<>();
        youtubeDataApi = new YouTube.Builder(transport, jsonFactory, null).setApplicationName("tv.sportssidekick.sportssidekick").build();
    }

    public void requestAllPlaylists() {
        if (playlists.size()>0) {
            EventBus.getDefault().post(new ClubTVEvent(null, ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED));
        } else {
            new GetChannelPlaylistsAsyncTask(youtubeDataApi) {
                @Override
                protected void onPostExecute(Pair<String, List<Playlist>> stringListPair) {
                    super.onPostExecute(stringListPair);
                    playlists.addAll(stringListPair.second);
                    EventBus.getDefault().post(new ClubTVEvent(null, ClubTVEvent.Type.CHANNEL_PLAYLISTS_DOWNLOADED));
                }
            }.execute(Constant.YOUTUBE_CHANNEL_ID);
        }
    }

    public void requestPlaylist(final String playlistId){
        if(videosHashMap.containsKey(playlistId)){
            EventBus.getDefault().post(new ClubTVEvent(playlistId, ClubTVEvent.Type.PLAYLIST_DOWNLOADED));
        } else {
            new GetPlaylistAsyncTask(youtubeDataApi) {
                @Override
                protected void onPostExecute(Pair<String, List<Video>> stringListPair) {
                    super.onPostExecute(stringListPair);
                    List<Video> receivedVideos = stringListPair.second;
                    videosHashMap.put(playlistId,receivedVideos);
                    videos.addAll(receivedVideos);
                    EventBus.getDefault().post(new ClubTVEvent(playlistId, ClubTVEvent.Type.PLAYLIST_DOWNLOADED));
                }
            }.execute(playlistId);
        }

    }

    public Playlist getPlaylistById(String id){
        for(Playlist playlist : playlists){
            if(id.equals(playlist.getId())){
                return playlist;
            }
        }
        return null;
    }
    public Video getVideoById(String id){
        for(Video video : videos){
            if(id.equals(video.getId())){
                return video;
            }
        }
        return null;
    }

    public String getPlaylistId(Video video){
        for(Map.Entry<String,List<Video>> entry : videosHashMap.entrySet()){
            if(entry.getValue().contains(video)){
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

}
