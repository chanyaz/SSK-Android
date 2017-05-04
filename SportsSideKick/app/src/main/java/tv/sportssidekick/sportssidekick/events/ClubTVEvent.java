package tv.sportssidekick.sportssidekick.events;

/**
 * Created by v3 on 1/28/2017.
 */

public class ClubTVEvent extends BusEvent {

    public enum Type {
        PLAYLIST_DOWNLOADED,
        CHANNEL_PLAYLISTS_DOWNLOADED
    }
    private Type eventType;
    public Type getEventType() {
        return eventType;
    }

    public ClubTVEvent(String id, Type eventType) {
        super(id);
        this.eventType = eventType;
    }

}
