package base.app.model.videoChat;

import base.app.events.BusEvent;

/**
 * Created by Filip on 3/31/2017.
 */

public class VideoChatEvent extends BusEvent{

    VideoChatItem item;

    public enum Type {
        onUserInvited,
        onUserInvitationRejected,
        onSelfInvited,
        onInvitationRevoked,
        onChatClosed
    }


    private Type type;

    public VideoChatEvent(String id) {
        super(id);
    }

    public VideoChatEvent(Type type, String id) {
        super(id);
        this.type = type;
    }

    public VideoChatEvent(Type type, String id, VideoChatItem item) {
        super(id);
        this.type = type;
        this.item = item;
    }

    public VideoChatItem getItem() {
        return item;
    }

    public Type getType() {
        return type;
    }


}
