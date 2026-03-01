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

@WebServlet("/LoginDetails")
public class LoginDetails extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public LoginDetails() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try (Connection c = GetConnection.getConnection()) {
            String sql = "SELECT * FROM assistant";
            PreparedStatement ps = c.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();

            out.println("<html><head><title>Login Details List</title>");
            out.println("<style>");
            out.println("table { width: 80%; border-collapse: collapse; margin: 20px auto; }");
            out.println("th, td { border: 1px solid #ccc; padding: 10px; text-align: center; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style></head><body>");
            out.println("<h2 align='center'>Login Details</h2>");
            out.println("<table>");
            out.println("<tr>");
            for (int i = 1; i <= meta.getColumnCount(); i++) {
                out.println("<th>" + meta.getColumnName(i) + "</th>");
            }
            out.println("</tr>");

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
            out.println("<h3 align='center' style='color:red;'>Failed to retrieve Login data.</h3>");
            e.printStackTrace(out);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
