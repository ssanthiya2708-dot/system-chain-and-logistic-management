import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/addProduct")
public class AddProductServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("productName");
        String category = request.getParameter("category");
        int quantity = 0;
        double price = 0.0;
        int minStock = 10;
        int supplierId = 0;

        try {
            if (request.getParameter("quantity") != null) quantity = Integer.parseInt(request.getParameter("quantity"));
            if (request.getParameter("price") != null) price = Double.parseDouble(request.getParameter("price"));
            if (request.getParameter("minStockLevel") != null) minStock = Integer.parseInt(request.getParameter("minStockLevel"));
            if (request.getParameter("supplierId") != null) supplierId = Integer.parseInt(request.getParameter("supplierId"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        Product product = new Product(name, category != null ? category : "General", quantity, price, minStock, supplierId);
        boolean success = ProductDAO.addProduct(product);

        String accept = request.getHeader("Accept");
        if (accept != null && accept.contains("application/json")) {
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print("{\"success\": " + success + ", \"message\": \"" + (success ? "Product added" : "Failed to add product") + "\"}");
        } else {
            response.sendRedirect("products.html?status=" + (success ? "success" : "error"));
        }
    }
}
