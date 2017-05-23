package base.app.model.club;


import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;

import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Playlist;
import com.google.api.services.youtube.model.PlaylistItem;
import com.google.api.services.youtube.model.PlaylistListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import base.app.Constant;

/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public abstract class GetChannelPlaylistsAsyncTask extends AsyncTask<String, Void, Pair<String, List<Playlist>>> {
    private static final String TAG = "GetPlaylistAsyncTask";
    private static final Long YOUTUBE_PLAYLIST_MAX_RESULTS = 50L;

    //see: https://developers.google.com/youtube/v3/docs/playlists/list
    private static final String YOUTUBE_CHANNEL_PART = "contentDetails,snippet";
    private static final String YOUTUBE_CHANNEL_FIELDS = "pageInfo,nextPageToken,items(id,snippet(title,publishedAt,thumbnails/high),contentDetails)";

    private YouTube mYouTubeDataApi;

    public GetChannelPlaylistsAsyncTask(YouTube api) {
        mYouTubeDataApi = api;
    }

    @Override
    protected Pair<String, List<Playlist>> doInBackground(String... params) {
        final String channelId = params[0];
        final String nextPageToken;

        if (params.length == 2) {
            nextPageToken = params[1];
        } else {
            nextPageToken = null;
        }

        PlaylistListResponse playlistListResponse;
        try {
            playlistListResponse = mYouTubeDataApi.playlists()
                    .list(YOUTUBE_CHANNEL_PART)
                    .setChannelId(channelId)
                    .setPageToken(nextPageToken)
                    .setFields(YOUTUBE_CHANNEL_FIELDS)
                    .setMaxResults(YOUTUBE_PLAYLIST_MAX_RESULTS)
                    .setKey(Constant.YOUTUBE_API_KEY)
                    .execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        if (playlistListResponse == null) {
            Log.e(TAG, "Failed to get playlist");
            return null;
        }

//        List<String> videoIds = new ArrayList();
//
//        // pull out the video id's from the playlist page
//        for (PlaylistItem item : playlistItemListResponse.getItems()) {
//            videoIds.add(item.getSnippet().getResourceId().getVideoId());
//        }
//
//        // get details of the videos on this playlist page
//        VideoListResponse videoListResponse = null;
//        try {
//            videoListResponse = mYouTubeDataApi.videos()
//                    .list(YOUTUBE_VIDEOS_PART)
//                    .setFields(YOUTUBE_VIDEOS_FIELDS)
//                    .setKey(Constant.YOUTUBE_API_KEY)
//                    .setId(TextUtils.join(",", videoIds)).execute();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        return new Pair(playlistListResponse.getNextPageToken(), playlistListResponse.getItems());
    }
}