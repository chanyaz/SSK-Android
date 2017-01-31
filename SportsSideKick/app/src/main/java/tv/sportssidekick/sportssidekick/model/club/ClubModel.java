package tv.sportssidekick.sportssidekick.model.club;

import android.util.Pair;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.Video;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.Type;
import java.util.ArrayList;
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

public class ClubModel {

    private static ClubModel instance;

    public static ClubModel getInstance(){
        if(instance==null){
            instance = new ClubModel();
        }
        return instance;
    }
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final GsonFactory jsonFactory = new GsonFactory();
    List<Station> stations;


    private List<Playlist> playlists;
    private List<Video> videos;
    private HashMap<String,List<Video>> videosHashMap;
    private  YouTube youtubeDataApi;

    private ClubModel(){
        playlists = new ArrayList<>();
        videos = new ArrayList<>();
        videosHashMap = new HashMap<>();
        youtubeDataApi = new YouTube.Builder(transport, jsonFactory, null).setApplicationName("tv.sportssidekick.sportssidekick").build();
        stations = createDummyStations();
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

    String dummyStationsJson = "[{\n\t\"coverImageUrl\": \"https://firebasestorage.googleapis.com/v0/b/sportssidekick-5418a.appspot.com/o/static%2Fimages%2F5LiveLogo.jpg?alt=media&token=ea67cee0-1d8d-4b2b-b265-469d04b3bf80\",\n\t\"isPodcast\": false,\n\t\"name\": \"BBC Radio 5 Live\",\n\t\"url\": \"http://bbcmedia.ic.llnwd.net/stream/bbcmedia_radio5live_mf_p\"\n}, {\n\t\"coverImageUrl\": \"https://firebasestorage.googleapis.com/v0/b/sportssidekick-5418a.appspot.com/o/static%2Fimages%2FtalkSport.jpg?alt=media&token=3b1a9d5c-1fb1-464d-9952-fd748b237535\",\n\t\"isPodcast\": false,\n\t\"name\": \"TalkSport Radio\",\n\t\"url\": \"http://www.radiofeeds.co.uk/talksportstreammobile.m3u\"\n}, {\n\t\"coverImageUrl\": \"https://firebasestorage.googleapis.com/v0/b/sportssidekick-5418a.appspot.com/o/static%2Fimages%2Fantena1.jpg?alt=media&token=93b5187e-ca25-431f-866c-82d634bacd56\",\n\t\"isPodcast\": false,\n\t\"name\": \"ANTENA 1\",\n\t\"url\": \"http://uk1.antennaradio.co.uk:8024/listen.pls?sid=1\"\n}, {\n\t\"coverImageUrl\": \"https://firebasestorage.googleapis.com/v0/b/sportssidekick-5418a.appspot.com/o/static%2Fimages%2Fbenedita.jpg?alt=media&token=5771fbc1-01e1-4751-adc7-e9ed306b9462\",\n\t\"isPodcast\": false,\n\t\"name\": \"Bendita\",\n\t\"url\": \"http://www.listenlive.eu/bbcradio1.m3u\"\n}, {\n\t\"coverImageUrl\": \"https://ichef.bbci.co.uk/images/ic/480x270/p04l1dv1.jpg\",\n\t\"isPodcast\": true,\n\t\"name\": \"Kilbane: Bournemouth hangover affected Liverpool performance\",\n\t\"url\": \"http://www.radiofeeds.co.uk/talksportstream.m3u\"\n}]";
    private List<Station> createDummyStations() {
        Gson gson = new Gson();
        Type listType = new TypeToken<List<Station>>(){}.getType();
       return (List<Station>) gson.fromJson(dummyStationsJson, listType);
    }

    public List<Station> getStations() {
        return stations;
    }

    public Station getStationByName(String name){
        for(Station station : stations){
            if(station.getName().equals(name)){
                return station;
            }
        }
        return null;
    }


}
