public class Shipment {
    private int id;
    private String trackingNumber;
    private int orderId;
    private String orderNumber;
    private String carrier;
    private String origin;
    private String destination;
    private String currentLocation;
    private String status;
    private String estimatedDelivery;

    public Shipment() {}

    public Shipment(int id, String trackingNumber, int orderId, String carrier, String origin, String destination, String currentLocation, String status, String estimatedDelivery) {
        this.id = id;
        this.trackingNumber = trackingNumber;
        this.orderId = orderId;
        this.carrier = carrier;
        this.origin = origin;
        this.destination = destination;
        this.currentLocation = currentLocation;
        this.status = status;
        this.estimatedDelivery = estimatedDelivery;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTrackingNumber() { return trackingNumber; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getCarrier() { return carrier; }
    public void setCarrier(String carrier) { this.carrier = carrier; }

    public String getOrigin() { return origin; }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getDestination() { return destination; }
    public void setDestination(String destination) { this.destination = destination; }

    public String getCurrentLocation() { return currentLocation; }
    public void setCurrentLocation(String currentLocation) { this.currentLocation = currentLocation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEstimatedDelivery() { return estimatedDelivery; }
    public void setEstimatedDelivery(String estimatedDelivery) { this.estimatedDelivery = estimatedDelivery; }
}
