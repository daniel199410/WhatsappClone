package com.example.whatsappclone.model;

import java.util.ArrayList;

public class Message {
    private String messageId, message, senderId;
    private ArrayList<String> mediaUrls;

    public Message(String messageId, String message, String senderId, ArrayList<String> mediaUrls) {
        this.messageId = messageId;
        this.message = message;
        this.senderId = senderId;
        this.mediaUrls = mediaUrls;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public ArrayList<String> getMediaUrls() {
        return mediaUrls;
    }

    public void setMediaUrls(ArrayList<String> mediaUrls) {
        this.mediaUrls = mediaUrls;
    }
}
