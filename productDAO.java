import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public static boolean addProduct(Product product) {
        String sql = "INSERT INTO products (product_name, category, quantity, price, min_stock_level, supplier_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setString(1, product.getProductName());
            ps.setString(2, product.getCategory() != null ? product.getCategory() : "General");
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getMinStockLevel() > 0 ? product.getMinStockLevel() : 10);
            
            if (product.getSupplierId() > 0) {
                ps.setInt(6, product.getSupplierId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, s.supplier_name FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id ORDER BY p.id DESC";
        try (Connection con = DBConnection.getConnection();
             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (con == null) return list;

            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getInt("min_stock_level"),
                    rs.getInt("supplier_id")
                );
                p.setSupplierName(rs.getString("supplier_name"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Product getProductById(int id) {
        String sql = "SELECT p.*, s.supplier_name FROM products p LEFT JOIN suppliers s ON p.supplier_id = s.id WHERE p.id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return null;

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product(
                    rs.getInt("id"),
                    rs.getString("product_name"),
                    rs.getString("category"),
                    rs.getInt("quantity"),
                    rs.getDouble("price"),
                    rs.getInt("min_stock_level"),
                    rs.getInt("supplier_id")
                );
                p.setSupplierName(rs.getString("supplier_name"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean updateProduct(Product product) {
        String sql = "UPDATE products SET product_name=?, category=?, quantity=?, price=?, min_stock_level=?, supplier_id=? WHERE id=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setString(1, product.getProductName());
            ps.setString(2, product.getCategory());
            ps.setInt(3, product.getQuantity());
            ps.setDouble(4, product.getPrice());
            ps.setInt(5, product.getMinStockLevel());
            if (product.getSupplierId() > 0) {
                ps.setInt(6, product.getSupplierId());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.setInt(7, product.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            if (con == null) return false;

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
