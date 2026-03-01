import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Login")
public class Login extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "prajjwal18";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("pwd");

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            String sql = "SELECT * FROM assistant WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Determine user role based on email domain
                if (email.endsWith("@admin.com")) {
                    response.sendRedirect("welcome.html");
                } else if (email.endsWith("@doctor.com")) {
                    response.sendRedirect("doctorpage.html");
                } else if (email.endsWith("@reception.com")) {
                    response.sendRedirect("reception.html");
                } else if (email.endsWith("@medicalstore.com")) {
                    response.sendRedirect("medicalstore.html");
                } else {
//                    out.println("<h3>Unknown user role. Access denied.</h3>");
                	response.sendRedirect("patientpage.html");
                }
            } else {
                out.println("<h3>Invalid email or password.</h3>");
            }

            rs.close();
            stmt.close();
            conn.close();

        } catch (Exception e) {
            out.println("<h3>Error: " + e.getMessage() + "</h3>");
            e.printStackTrace(out);
        }
    }
}
