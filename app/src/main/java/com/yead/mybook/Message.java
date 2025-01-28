package com.yead.mybook;

public class Message {
    private String senderEmail;
    private String messageContent;
    private String timestamp;
    private String messageId;// Add message ID

    public Message(String senderEmail, String messageContent, String timestamp, String messageId) {
        this.senderEmail = senderEmail;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
        this.messageId = messageId;
    }

    public String getSenderEmail() {
        return senderEmail;
    }

    public String getMessageContent() {
        return messageContent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}