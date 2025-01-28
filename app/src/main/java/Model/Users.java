package Model;

public class Users {
    private String Name,Number,Email,Password, image, address , Bkash;
    public Users(){

    }

    public Users(String name, String number, String email, String password, String image, String address, String Bkash) {
        Name = name;
        Number = number;
        Email = email;
        Password = password;
        this.image = image;
        this.address = address;
        this.Bkash  = Bkash;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNumber() {
        return Number;
    }
    public String getBkash() {
        return Bkash;}

    public void setNumber(String number) {
        Number = number;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public void setBkash(String Bkash) {
        this.Bkash = Bkash;
    }
}
