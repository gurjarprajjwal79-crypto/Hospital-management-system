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

@WebServlet("/CreatePatient")
public class CreatePatient extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private static final String JDBC_URL  = "jdbc:mysql://localhost:3306/hospital";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASS = "prajjwal18";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Redirect GET to form
        response.sendRedirect("addpatient.html");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 1) Read parameters
        String name        = request.getParameter("name");
        String email       = request.getParameter("email");
        String phone       = request.getParameter("phone");
        String ageStr      = request.getParameter("age");
        String gender      = request.getParameter("gender");
        String blood       = request.getParameter("blood");
        String symptom     = request.getParameter("symptom");
        String disease     = request.getParameter("disease");
        String doctorIdStr = request.getParameter("doctor_id");

        // 2) Validate numeric fields
        if (ageStr == null || doctorIdStr == null || ageStr.isEmpty() || doctorIdStr.isEmpty()) {
            out.println("<h3 style='color:red;'>Age and Doctor ID are required.</h3>");
            return;
        }

        int age, doctorId;
        try {
            age      = Integer.parseInt(ageStr.trim());
            doctorId = Integer.parseInt(doctorIdStr.trim());
        } catch (NumberFormatException nfe) {
            out.println("<h3 style='color:red;'>Invalid input. Age and Doctor ID must be numbers.</h3>");
            return;
        }

        try {
            // 3) Load Driver & Connect
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS)) {

                // 4) Verify doctor exists
                PreparedStatement chk = conn.prepareStatement(
                    "SELECT 1 FROM doctor WHERE id = ?");
                chk.setInt(1, doctorId);
                try (ResultSet rs = chk.executeQuery()) {
                    if (!rs.next()) {
                        out.println("<h3 style='color:red;'>Invalid doctor ID.</h3>");
                        return;
                    }
                }

                // 5) Insert patient
                PreparedStatement ins = conn.prepareStatement(
                    "INSERT INTO patient(name,email,phone,age,gender,blood,symptom,disease,doctor_id) " +
                    "VALUES(?,?,?,?,?,?,?,?,?)");
                ins.setString(1, name);
                ins.setString(2, email);
                ins.setString(3, phone);
                ins.setInt(4, age);
                ins.setString(5, gender);
                ins.setString(6, blood);
                ins.setString(7, symptom);
                ins.setString(8, disease);
                ins.setInt(9, doctorId);
                ins.executeUpdate();

                // 6) Get new patient_id
                PreparedStatement getPid = conn.prepareStatement(
                    "SELECT patient_id FROM patient WHERE email = ?");
                getPid.setString(1, email);
                String newPid;
                try (ResultSet rs2 = getPid.executeQuery()) {
                    rs2.next();
                    newPid = rs2.getString("patient_id");
                }

                // 7) Update doctor's patient list
                PreparedStatement fetch = conn.prepareStatement(
                    "SELECT patients FROM doctor WHERE id = ?");
                fetch.setInt(1, doctorId);
                String existing;
                try (ResultSet rs3 = fetch.executeQuery()) {
                    rs3.next();
                    existing = rs3.getString("patients");
                }

                String updated = (existing == null || existing.isEmpty())
                               ? newPid
                               : existing + "," + newPid;

                PreparedStatement upd = conn.prepareStatement(
                    "UPDATE doctor SET patients = ? WHERE id = ?");
                upd.setString(1, updated);
                upd.setInt(2, doctorId);
                upd.executeUpdate();

                out.println("<h2 style='color:green;'>Patient registered successfully!</h2>");
            }

        } catch (Exception e) {
            e.printStackTrace(out);
            out.println("<h2 style='color:red;'>Error while creating patient. Please try again.</h2>");
        }
    }
}
