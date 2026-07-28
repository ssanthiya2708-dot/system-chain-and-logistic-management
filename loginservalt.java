import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Note: If using Tomcat 9 or older Java EE, change 'jakarta.servlet' imports to 'javax.servlet'

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // If parameters are null, attempt reading from JSON request body
        if (username == null || password == null) {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String body = sb.toString();
            if (body != null && !body.isEmpty()) {
                username = extractJsonField(body, "username");
                password = extractJsonField(body, "password");
            }
        }

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            out.print("{\"success\": false, \"message\": \"Username and password required\"}");
            return;
        }

        User user = UserDAO.validateUser(username.trim(), password.trim());

        if (user != null) {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);
            out.print("{\"success\": true, \"message\": \"Login successful\", \"role\": \"" + escapeJson(user.getRole()) + "\", \"fullName\": \"" + escapeJson(user.getFullName()) + "\"}");
        } else {
            out.print("{\"success\": false, \"message\": \"Invalid Username or Password\"}");
        }
    }

    private String extractJsonField(String json, String field) {
        String key = "\"" + field + "\"";
        int keyIndex = json.indexOf(key);
        if (keyIndex == -1) return null;
        int colonIndex = json.indexOf(":", keyIndex);
        if (colonIndex == -1) return null;
        int startQuote = json.indexOf("\"", colonIndex);
        if (startQuote == -1) return null;
        int endQuote = json.indexOf("\"", startQuote + 1);
        if (endQuote == -1) return null;
        return json.substring(startQuote + 1, endQuote);
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
