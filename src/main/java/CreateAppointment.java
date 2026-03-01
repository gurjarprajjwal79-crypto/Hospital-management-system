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

@WebServlet("/CreateAppointment")
public class CreateAppointment extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Database credentials
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/hospital";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "prajjwal18"; // Replace with your actual password

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET requests to appointment form page
        response.sendRedirect("getAppointment.html"); // Adjust this filename/path as needed
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set encoding and content type
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html");

        // Read form parameters
        String patient_name = request.getParameter("patientName");
        String email = request.getParameter("email");
        String phone_no = request.getParameter("phone");
        String doctor_name = request.getParameter("doctor");
        String appointment_date = request.getParameter("date");
        String appointment_time = request.getParameter("time");

        PrintWriter out = response.getWriter();

        try {
            // Load MySQL JDBC Driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to DB
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);

            // Insert appointment into DB
            String sql = "INSERT INTO appointment (patient_name, email, phone_no, doctor_name, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, patient_name);
            stmt.setString(2, email);
            stmt.setString(3, phone_no);
            stmt.setString(4, doctor_name);
            stmt.setString(5, appointment_date);
            stmt.setString(6, appointment_time);

            int rows = stmt.executeUpdate();

            if (rows > 0) {
                out.println("<!DOCTYPE html>");
                out.println("<html><head><title>Appointment Confirmation</title></head><body>");
                out.println("<h1>Appointment Booked Successfully!</h1>");
                out.println("<p><strong>Patient:</strong> " + patient_name + "</p>");
                out.println("<p><strong>Email:</strong> " + email + "</p>");
                out.println("<p><strong>Phone:</strong> " + phone_no + "</p>");
                out.println("<p><strong>Doctor:</strong> " + doctor_name + "</p>");
                out.println("<p><strong>Date:</strong> " + appointment_date + "</p>");
                out.println("<p><strong>Time:</strong> " + appointment_time + "</p>");
                out.println("</body></html>");
            } else {
                out.println("<h2>Error booking appointment.</h2>");
            }

            stmt.close();
            conn.close();

        } catch (Exception e) {
            out.println("<h2>Error: " + e.getMessage() + "</h2>");
            e.printStackTrace(out);
        }
    }
}
