
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CreateMedicine")
public class CreateMedicine extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "prajjwal18"; // Update if needed

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to form if accessed via GET
        response.sendRedirect("addmedicine.html"); // Adjust the form page name if different
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set encoding and content type
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        PrintWriter out = response.getWriter();

        // Get parameters
        String name = request.getParameter("name");
        String price = request.getParameter("price");
        String count = request.getParameter("count");

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO medicine (name, price, count) VALUES (?, ?, ?)")) {

            // Set parameters
            stmt.setString(1, name);
            stmt.setString(2, price);
            stmt.setString(3, count);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                out.println("<h2>Medicine added successfully!</h2>");
            } else {
                out.println("<h2>Failed to add medicine. Please try again.</h2>");
            }

        } catch (Exception e) {
            e.printStackTrace(out);
            out.println("<h2>Error occurred while adding medicine.</h2>");
        }
    }
}
