import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ShipmentDAO {

    public static boolean createShipment(Shipment shipment) {
        String sql = "INSERT INTO shipments (tracking_number, order_id, carrier, origin, destination, current_location, status, estimated_delivery) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setString(1, shipment.getTrackingNumber());
            ps.setInt(2, shipment.getOrderId());
            ps.setString(3, shipment.getCarrier());
            ps.setString(4, shipment.getOrigin());
            ps.setString(5, shipment.getDestination());
            ps.setString(6, shipment.getCurrentLocation());
            ps.setString(7, shipment.getStatus() != null ? shipment.getStatus() : "Preparing");
            ps.setString(8, shipment.getEstimatedDelivery());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Shipment> getAllShipments() {
        List<Shipment> list = new ArrayList<>();
        String sql = "SELECT s.*, o.order_number FROM shipments s LEFT JOIN orders o ON s.order_id = o.id ORDER BY s.id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (con == null) return list;

            while (rs.next()) {
                Shipment sh = new Shipment(
                    rs.getInt("id"),
                    rs.getString("tracking_number"),
                    rs.getInt("order_id"),
                    rs.getString("carrier"),
                    rs.getString("origin"),
                    rs.getString("destination"),
                    rs.getString("current_location"),
                    rs.getString("status"),
                    rs.getString("estimated_delivery")
                );
                sh.setOrderNumber(rs.getString("order_number"));
                list.add(sh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean updateShipmentStatus(int id, String currentLocation, String status) {
        String sql = "UPDATE shipments SET current_location = ?, status = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setString(1, currentLocation);
            ps.setString(2, status);
            ps.setInt(3, id);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
