package Model;

public class MessageModel {
    private String messageText;
    private String senderId;

    public MessageModel() {}

    public MessageModel(String messageText, String senderId) {
        this.messageText = messageText;
        this.senderId = senderId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
