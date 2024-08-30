package com.example.caei.Class;

public class MessageGroup {
    private int idMes;
    private int senderId;
    private int recipientId;
    private String timestamp;
    private String jour;
    private String message;

    // Constructor
    public MessageGroup(int idMes, String timestamp, String jour, String message) {
        this.idMes = idMes;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.jour = jour;
        this.message = message;
    }

    // Getters
    public int getIdMes() {
        return idMes;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getRecipientId() {
        return recipientId;
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
