package com.example.caei.Class;

public class Message {
    private int senderId;
    private String senderName;
    private String recipientName;
    private String timestamp;
    private String jour;
    private String message;

    public Message(int senderId, String senderName, String message, String timestamp, String jour) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.timestamp = timestamp;
        this.jour = jour;
        this.message = message;
    }

    public int getSenderId() {
        return senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getJour() {
        return jour;
    }

    public String getMessage() {
        return message;
    }
}
