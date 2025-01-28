package Model;

public class TransactionModel {
    private String productId;
    private String productName;
    private String productBkash;
    private double amountPaid;

    public TransactionModel() {
        // Default constructor required for calls to DataSnapshot.getValue(TransactionModel.class)
    }

    public TransactionModel(String productId, String productName, String productBkash, double amountPaid) {
        this.productId = productId;
        this.productName = productName;
        this.productBkash = productBkash;
        this.amountPaid = amountPaid;
    }

    // Getters and setters
    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductBkash() { return productBkash; }
    public void setProductBkash(String productBkash) { this.productBkash = productBkash; }

    public double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(double amountPaid) { this.amountPaid = amountPaid; }
}
