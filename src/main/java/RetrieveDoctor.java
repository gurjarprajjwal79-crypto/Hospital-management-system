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

@WebServlet("/RetrieveDoctor")
public class RetrieveDoctor extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RetrieveDoctor() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Set CORS headers if needed
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try (Connection c = GetConnection.getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM doctor");
             ResultSet rs = ps.executeQuery()) {

            ResultSetMetaData meta = rs.getMetaData();

            out.println("<html><head><title>Doctor List</title>");
            out.println("<style>");
            out.println("table { border-collapse: collapse; width: 90%; margin: 20px auto; }");
            out.println("th, td { border: 1px solid #ccc; padding: 8px; text-align: center; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style></head><body>");
            out.println("<h2 align='center'>Doctor Information</h2>");

            out.println("<table>");
            out.println("<tr>");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                out.println("<th>" + escapeHtml(meta.getColumnName(i)) + "</th>");
            }
            out.println("<th>Patients Under</th>");
            out.println("</tr>");

            while (rs.next()) {
                out.println("<tr>");
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    out.println("<td>" + escapeHtml(rs.getString(i)) + "</td>");
                }
                out.println("<td>");
                out.println("<form method='GET' action='RetrievePatientsDID'>");
                out.println("<input type='hidden' name='did' value='" + escapeHtml(rs.getString("id")) + "'/>");
                out.println("<input type='submit' value='View Patients'/>");
                out.println("</form>");
                out.println("</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

        } catch (SQLException e) {
            out.println("<h3 align='center' style='color:red;'>Error retrieving doctors. Please try again.</h3>");
            e.printStackTrace(out); // Consider logging instead of printing to response
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    // Utility method to escape HTML to prevent XSS
    private String escapeHtml(String str) {
        if (str == null) return null;
        return str.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\"", "&quot;")
                  .replace("'", "&#39;");
    }
}
