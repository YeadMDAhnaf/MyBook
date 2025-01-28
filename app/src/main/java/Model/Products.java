package Model;

public class Products{
    private String PNAME, PRICE, DESCRIPTION, IMAGE,DATE,TIME,PID;

    public Products(){

    }

    public Products(String PNAME, String PRICE, String DESCRIPTION, String IMAGE, String DATE, String TIME, String PID) {
        this.PNAME = PNAME;
        this.PRICE = PRICE;
        this.DESCRIPTION = DESCRIPTION;
        this.IMAGE = IMAGE;
        this.DATE = DATE;
        this.TIME = TIME;
        this.PID = PID;

    }


    public String getPNAME() {
        return PNAME;
    }

    public void setPNAME(String PNAME) {
        this.PNAME = PNAME;
    }

    public String getPRICE() {
        return PRICE;
    }

    public void setPRICE(String PRICE) {
        this.PRICE = PRICE;
    }

    public String getDESCRIPTION() {
        return DESCRIPTION;
    }

    public void setDESCRIPTION(String DESCRIPTION) {
        this.DESCRIPTION = DESCRIPTION;
    }

    public String getIMAGE() {
        return IMAGE;
    }

    public void setIMAGE(String IMAGE) {
        this.IMAGE = IMAGE;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getTIME() {
        return TIME;
    }

    public void setTIME(String TIME) {
        this.TIME = TIME;
    }

    public String getPID() {
        return PID;
    }

    public void setPID(String PID) {
        this.PID = PID;
    }






}