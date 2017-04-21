package tv.sportssidekick.sportssidekick.model.notifications;

/**
 * Created by Filip on 4/20/2017.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class ExternalNotificationEvent {

    private boolean fromBackground;

    public ExternalNotificationEvent(boolean isFromBackground) {
        fromBackground = isFromBackground;
    }

    public boolean isFromBackground() {
        return fromBackground;
    }

    public void setFromBackground(boolean fromBackground) {
        this.fromBackground = fromBackground;
    }

}
