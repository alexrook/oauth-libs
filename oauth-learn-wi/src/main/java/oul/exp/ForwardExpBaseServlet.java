package oul.exp;

import java.io.IOException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author moroz
 */
public class ForwardExpBaseServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispather = request.getRequestDispatcher("restricted/oauth-secret.html");

        response.addCookie(new Cookie("x-forwarded-from", "ForwardExpBase"));
        dispather.forward(request, response);
    }

}
