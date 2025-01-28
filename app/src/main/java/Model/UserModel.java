package Model;

public class UserModel {
    private String userId;
    private String userName;
    private String bkashNumber;

    public UserModel() {}

    public UserModel(String userId, String userName, String bkashNumber) {
        this.userId = userId;
        this.userName = userName;
        this.bkashNumber = bkashNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBkashNumber() {
        return bkashNumber;
    }

    public void setBkashNumber(String bkashNumber) {
        this.bkashNumber = bkashNumber;
    }
}
