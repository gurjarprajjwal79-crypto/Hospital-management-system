import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/RetrievePatients")
public class RetrievePatients extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RetrievePatients() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection c = GetConnection.getConnection()) {
            String sql = "SELECT * FROM patient";
            Statement stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            ResultSetMetaData meta = rs.getMetaData();

            out.println("<html><head><title>Patient Records</title>");
            out.println("<style>");
            out.println("table { width: 95%; margin: 20px auto; border-collapse: collapse; }");
            out.println("th, td { padding: 8px; text-align: center; border: 1px solid #ddd; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style></head><body>");
            out.println("<h2 align='center'>All Patients</h2>");
            out.println("<table>");

            // Header Row
            out.println("<tr>");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                out.println("<th>" + meta.getColumnName(i) + "</th>");
            }
            out.println("</tr>");

            // Data Rows
            while (rs.next()) {
                out.println("<tr>");
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    out.println("<td>" + rs.getString(i) + "</td>");
                }
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

        } catch (SQLException e) {
            out.println("<h3 align='center' style='color:red;'>Error retrieving patient records. Try again.</h3>");
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
