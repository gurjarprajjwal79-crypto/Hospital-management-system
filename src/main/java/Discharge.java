import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Discharge")
public class Discharge extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Discharge() {
        super();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Get parameters
        String pidStr     = request.getParameter("pid");
        String daysStr    = request.getParameter("days");
        String daycostStr = request.getParameter("daycost");
        String mc         = request.getParameter("mc");

        // Validate inputs
        if (pidStr == null || daysStr == null || daycostStr == null || mc == null ||
            pidStr.trim().isEmpty() || daysStr.trim().isEmpty() ||
            daycostStr.trim().isEmpty() || mc.trim().isEmpty()) {
            out.println("<h3 style='color:red;'>All fields (Patient ID, Days, Day Cost, Medicines) are required.</h3>");
            return;
        }

        int pid, days, daycost;
        try {
            pid     = Integer.parseInt(pidStr.trim());
            days    = Integer.parseInt(daysStr.trim());
            daycost = Integer.parseInt(daycostStr.trim());
        } catch (NumberFormatException nfe) {
            out.println("<h3 style='color:red;'>Patient ID, Days and Day Cost must be valid integers.</h3>");
            return;
        }

        // Medicine format: "1,2;3,4" means id=1 qty=2 and id=3 qty=4
        String[] entries = mc.split(";");
        if (entries.length == 0) {
            out.println("<h3 style='color:red;'>Medicine data format is invalid.</h3>");
            return;
        }

        double total = 0.0;

        try (Connection conn = GetConnection.getConnection()) {
            // 🔧 Use correct patient column: patient_id
            conn.createStatement().executeUpdate("DELETE FROM patient WHERE patient_id = " + pid);

            for (String entry : entries) {
                String[] parts = entry.split(",");
                if (parts.length != 2) continue;

                int mid   = Integer.parseInt(parts[0].trim());
                int count = Integer.parseInt(parts[1].trim());

                // 🔧 Use correct medicine column: id
                ResultSet rs = conn.createStatement()
                        .executeQuery("SELECT price FROM medicine WHERE id = " + mid);
                if (rs.next()) {
                    double price = rs.getDouble("price");
                    total += price * count;
                }
            }

            // Add room cost
            total += days * daycost;

            // Output total
            out.println("<h1 align='center'>TOTAL MONEY TO PAY IS:</h1>");
            out.println("<h2 align='center'>" + total + "</h2>");

        } catch (SQLException e) {
            out.println("<h3 style='color:red;'>An error occurred during discharge. Please try again.</h3>");
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.sendRedirect("discharge.html");
    }
}
