public class Product {
    private int id;
    private String productName;
    private String category;
    private int quantity;
    private double price;
    private int minStockLevel;
    private int supplierId;
    private String supplierName;

    public Product() {}

    public Product(String productName, int quantity, double price) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.category = "General";
        this.minStockLevel = 10;
    }

    public Product(int id, String productName, String category, int quantity, double price, int minStockLevel, int supplierId) {
        this.id = id;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.minStockLevel = minStockLevel;
        this.supplierId = supplierId;
    }

    public Product(String productName, String category, int quantity, double price, int minStockLevel, int supplierId) {
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
        this.minStockLevel = minStockLevel;
        this.supplierId = supplierId;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public int getSupplierId() { return supplierId; }
    public void setSupplierId(int supplierId) { this.supplierId = supplierId; }

    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}
