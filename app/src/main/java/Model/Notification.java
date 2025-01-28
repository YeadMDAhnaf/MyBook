package Model;

public class Notification {
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

    private String bunbuni,chuni;

    public Notification() {
        // Default constructor required for calls to DataSnapshot.getValue(TransactionModel.class)
    }

    public Notification(String bunbuni, String chuni) {
        this.bunbuni = bunbuni;
        this.chuni = chuni;
    }

    // Getters and setters



}
