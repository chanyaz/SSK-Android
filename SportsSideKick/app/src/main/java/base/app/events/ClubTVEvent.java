package base.app.events;

public class ClubTVEvent extends BusEvent {

    public enum Type {
        FIRST_VIDEO_DATA_DOWNLOADED,
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
