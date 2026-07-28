import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public static boolean createOrder(Order order) {
        String sqlOrder = "INSERT INTO orders (order_number, customer_name, customer_email, shipping_address, status, total_amount) VALUES (?, ?, ?, ?, ?, ?)";
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            if (con == null) return false;

            con.setAutoCommit(false);

            PreparedStatement ps = con.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, order.getOrderNumber());
            ps.setString(2, order.getCustomerName());
            ps.setString(3, order.getCustomerEmail());
            ps.setString(4, order.getShippingAddress());
            ps.setString(5, order.getStatus() != null ? order.getStatus() : "Pending");
            ps.setDouble(6, order.getTotalAmount());

            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int orderId = 0;
            if (rs.next()) {
                orderId = rs.getInt(1);
            }

            if (order.getItems() != null && !order.getItems().isEmpty() && orderId > 0) {
                String sqlItem = "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
                PreparedStatement psItem = con.prepareStatement(sqlItem);
                for (OrderItem item : order.getItems()) {
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getProductId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setDouble(4, item.getUnitPrice());
                    psItem.addBatch();

                    // Deduct stock
                    String sqlStock = "UPDATE products SET quantity = quantity - ? WHERE id = ?";
                    PreparedStatement psStock = con.prepareStatement(sqlStock);
                    psStock.setInt(1, item.getQuantity());
                    psStock.setInt(2, item.getProductId());
                    psStock.executeUpdate();
                    psStock.close();
                }
                psItem.executeBatch();
                psItem.close();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }

    public static List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (con == null) return list;

            while (rs.next()) {
                list.add(new Order(
                    rs.getInt("id"),
                    rs.getString("order_number"),
                    rs.getString("customer_name"),
                    rs.getString("customer_email"),
                    rs.getString("shipping_address"),
                    rs.getString("order_date"),
                    rs.getString("status"),
                    rs.getDouble("total_amount")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
