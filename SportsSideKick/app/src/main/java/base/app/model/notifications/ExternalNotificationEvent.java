package base.app.model.notifications;

import java.util.Map;

/**
 * Created by Filip on 4/20/2017.
 */

public class ExternalNotificationEvent {

    public enum Type {
        ImsMessage ("1"),
        ImsUpdateChatInfo  ("2"),
        NewWallPost  ("3");

        private final String code;

        Type(String code){
            this.code = code;
        }

        public String getCode() { return  code; }
    }

    private Map<String, String> data;
    private boolean fromBackground;

    public ExternalNotificationEvent(Map<String, String> data, boolean isFromBackground) {
        this.fromBackground = isFromBackground;
        this.data = data;
    }

    public boolean isFromBackground() {
        return fromBackground;
    }

    public Map<String, String> getData() {
        return data;
    }

}
