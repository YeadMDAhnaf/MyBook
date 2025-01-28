package Model;

public class Message {
    private String text;
    private String sender;
    private String timestamp;

    // Constructor
    public Message(String text, String sender, String timestamp) {
        this.text = text;
        this.sender = sender;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
