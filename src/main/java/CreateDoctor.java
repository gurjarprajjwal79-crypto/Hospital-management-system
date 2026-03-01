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

@WebServlet("/CreateDoctor")
public class CreateDoctor extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "prajjwal18"; // <-- update this with your actual password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Option 1: Show error message
        // response.setContentType("text/html");
        // PrintWriter out = response.getWriter();
        // out.println("<h3>This servlet only supports POST requests.</h3>");

        // Option 2: Redirect to form page
        response.sendRedirect("adddoctor.html"); // adjust path as needed
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String ageStr = request.getParameter("age");
        String salStr = request.getParameter("sal");
        String spec = request.getParameter("spec");

        int age = Integer.parseInt(ageStr);
        double salary = Double.parseDouble(salStr);

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            String sql = "INSERT INTO doctor (name, email, phone, age, salary, specialist) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setInt(4, age);
            stmt.setDouble(5, salary);
            stmt.setString(6, spec);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                out.println("<h2>Doctor added successfully!</h2>");
            } else {
                out.println("<h2>Error adding doctor.</h2>");
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace(out);
        }
    }
}
