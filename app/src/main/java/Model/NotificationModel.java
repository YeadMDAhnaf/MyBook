package Model;

public class NotificationModel {
    private String bunbuni;
    private String chuni;

    public NotificationModel() {
        // Default constructor required for Firebase
    }

    public NotificationModel(String bunbuni, String chuni) {
        this.bunbuni = bunbuni;
        this.chuni = chuni;
    }

    public String getBunbuni() {
        return bunbuni;
    }

    public void setBunbuni(String bunbuni) {
        this.bunbuni = bunbuni;
    }

    public String getChuni() {
        return chuni;
    }

    public void setChuni(String chuni) {
        this.chuni = chuni;
    }
}
