

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;


@WebServlet("/Logout")
public class Logout extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public Logout() {
        super();
    }

    // Handle logout on GET request
    @Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession sess = request.getSession(false); // false to avoid creating session if none
        if (sess != null) {
            sess.invalidate();
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<br><br><br><h1 align=center><font color=\"green\">YOU ARE LOGGED OUT OF THE SYSTEM<br>REDIRECTING YOU TO<br><br>HOME-PAGE</font></h1>");
        out.println("<script type=\"text/javascript\">");
        out.println("redirectURL = \"index.html\";setTimeout(function() { location.href = redirectURL; }, 5000);");
        out.println("</script>");
    }

    // Call doGet for POST requests too (optional)
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
