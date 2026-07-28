// Legacy model wrapper matching filename products.java
public class products {
    private String productName;
    private int quantity;
    private double price;

    public products() {}

    public products(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
    }

    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
}
