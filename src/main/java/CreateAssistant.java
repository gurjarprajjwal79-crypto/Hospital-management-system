import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/CreateAssistant")
public class CreateAssistant extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hospital?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "prajjwal18"; // Update if needed

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect to assistant registration page if accessed via GET
        response.sendRedirect("newAssistant.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Read form parameters
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String pwd = request.getParameter("pwd");
        String joindate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        try {
            // Load JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Establish DB connection
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            // Insert assistant record
            String sql = "INSERT INTO assistant (name, email, phone, joindate, password) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, phone);
            stmt.setString(4, joindate);
            stmt.setString(5, pwd);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                // Redirect based on email domain
                if (email.endsWith("@admin.com")) {
                    response.sendRedirect("welcome.html");
                } else if (email.endsWith("@doctor.com")) {
                    response.sendRedirect("doctorpage.html");
                } else if (email.endsWith("@reception.com")) {
                    response.sendRedirect("reception.html");
                } else if (email.endsWith("@medicalstore.com")) {
                    response.sendRedirect("medicalstore.html");
                } else {
                    response.sendRedirect("patientpage.html");
                }
            } else {
                out.println("<h2>Registration failed. Redirecting back...</h2>");
                out.println("<script>setTimeout(() => { window.location = 'newAssistant.html'; }, 5000);</script>");
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace(out);
            out.println("<h2>Error occurred. Redirecting back...</h2>");
            out.println("<script>setTimeout(() => { window.location = 'newAssistant.html'; }, 5000);</script>");
        }
    }
}
