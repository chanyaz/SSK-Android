package base.app.data.tv

import base.app.BuildConfig.APPLICATION_ID
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.api.client.extensions.android.http.AndroidHttp.newCompatibleTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Video
import java.util.*

object MediaModel {

    val mapper: ObjectMapper = ObjectMapper()
    var stations: List<Station>? = null

    @JvmStatic
    val videos: MutableList<Video> = ArrayList()

    val videosHashMap: HashMap<String, List<Video>> = HashMap()
    val youtubeDataApi: YouTube = YouTube.Builder(
            newCompatibleTransport(),
            GsonFactory(),
            null
    ).setApplicationName(APPLICATION_ID).build()

    @JvmStatic
    fun getVideoById(id: String?): Video? {
        if (id == null) {
            return null
        }
        return videos.firstOrNull { id == it.id }
    }

    @JvmStatic
    fun getPlaylistId(video: Video): String? {
        for ((key, value) in videosHashMap) {
            if (value.contains(video)) {
                return key
            }
        }
        return null
    }

    /*
    fun getPlaylistsVideos(id: String): List<Video> {
        return videosHashMap[id]
    }

    fun getStations(): Task<List<Station>> {
        val source = TaskCompletionSource<List<Station>>()
        val consumer = GSEventConsumer<GSResponseBuilder.LogEventResponse> { response ->
            if (!response.hasErrors()) {
                val `object` = response.scriptData.baseData[GSConstants.ITEMS]
                val receivedStations = mapper.convertValue<List<Station>>(`object`, object : TypeReference<List<Station>>() {

                })
                while (receivedStations.contains(null)) {
                    receivedStations.remove(null)
                }
                stations = receivedStations
                source.setResult(receivedStations)
            } else {
                source.setException(Exception("There was an error while trying to get stations."))
            }
        }
        createRequest("clubRadioGetStations")
                .setEventAttribute(CLUB_ID_TAG, CLUB_ID)
                .send(consumer)
        return source.task
    }

    fun getStationByName(name: String): Station? {
        if (stations != null) {
            for (station in stations) {
                if (station.name == name) {
                    return station
                }
            }
        }
        return null
    }
    */
}