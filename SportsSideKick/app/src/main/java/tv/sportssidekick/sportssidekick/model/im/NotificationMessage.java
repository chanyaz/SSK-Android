package tv.sportssidekick.sportssidekick.model.im;

/**
 * Created by Filip on 12/13/2016.
 * Copyright by Hypercube d.o.o.
 * www.hypercubesoft.com
 */

public class NotificationMessage {

    private String chatId;
    private boolean newIM;
    private String senderId;
    private String senderName;
    private String message;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public boolean isNewIM() {
        return newIM;
    }

    public void setNewIM(boolean newIM) {
        this.newIM = newIM;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationMessage() {

    }
}
