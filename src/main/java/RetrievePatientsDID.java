import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RetrievePatientsDID")
public class RetrievePatientsDID extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RetrievePatientsDID() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String doctor_id = request.getParameter("did"); // HTML still sends `did`
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        if (doctor_id == null || doctor_id.trim().isEmpty()) {
            out.println("<h3 style='color:red'>Doctor ID is missing.</h3>");
            return;
        }

        try (Connection conn = GetConnection.getConnection()) {
            // ✅ Use correct column name `id` in the doctor table
            String doctorQuery = "SELECT name, patients FROM doctor WHERE id = ?";
            PreparedStatement doctorStmt = conn.prepareStatement(doctorQuery);
            doctorStmt.setString(1, doctor_id);
            ResultSet docResult = doctorStmt.executeQuery();

            if (!docResult.next()) {
                out.println("<h3 style='color:red'>No doctor found with ID: " + doctor_id + "</h3>");
                return;
            }

            String doctorName = docResult.getString("name");
            String patientsRaw = docResult.getString("patients");

            if (patientsRaw == null || patientsRaw.trim().isEmpty()) {
                out.println("<h3>No patients assigned to Dr. " + doctorName + ".</h3>");
                return;
            }

            String[] patientIds = patientsRaw.split(",");

            // HTML structure
            out.println("<html><head><title>Patients Under Doctor</title>");
            out.println("<style>");
            out.println("table { width: 95%; border-collapse: collapse; margin: 20px auto; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: center; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style></head><body>");
            out.println("<h2 align='center'>Patients under Dr. " + doctorName + "</h2>");
            out.println("<table>");

            String patientQuery = "SELECT * FROM patient WHERE patient_id = ?";
            PreparedStatement patientStmt = conn.prepareStatement(patientQuery);
            boolean headerPrinted = false;

            for (String pid : patientIds) {
                pid = pid.trim();
                if (pid.isEmpty()) continue;

                patientStmt.setString(1, pid);
                ResultSet patientResult = patientStmt.executeQuery();

                if (patientResult.next()) {
                    if (!headerPrinted) {
                        ResultSetMetaData meta = patientResult.getMetaData();
                        out.println("<tr>");
                        for (int i = 1; i <= meta.getColumnCount(); i++) {
                            out.println("<th>" + meta.getColumnName(i) + "</th>");
                        }
                        out.println("</tr>");
                        headerPrinted = true;
                    }

                    out.println("<tr>");
                    for (int i = 1; i <= patientResult.getMetaData().getColumnCount(); i++) {
                        out.println("<td>" + patientResult.getString(i) + "</td>");
                    }
                    out.println("</tr>");
                }
            }

            out.println("</table></body></html>");

        } catch (SQLException e) {
            out.println("<h3 style='color:red'>An error occurred while retrieving patients.</h3>");
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
