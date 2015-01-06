package oul.web.tools.oauth;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import oul.web.tools.oauth.GoogleOAuthBase.AuthLoginInfo;
import oul.web.tools.oauth.profile.IAuthEntryStorage;
import oul.web.tools.oauth.profile.IProfileStorage;

/**
 * @author moroz
 */
public class GoogleOAuthServlet extends HttpServlet {

    @Inject
    GoogleOAuthBase googleOAuthBase;

    @Inject
    IProfileStorage profileSorage;

    @Inject
    IAuthEntryStorage authStorage;

    public static final String LOGIN_URI_SUFFIX = "login",
            CALLBACK_URI_SUFFIX = "callback",
            PROFILE_URI_SUFFIX = "profile",
            PARAM_SUCCESS_REDIRECT_URI = "success-redirect-uri";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath().toLowerCase();

        if (servletPath.endsWith(LOGIN_URI_SUFFIX.toLowerCase())) {
            doLogin(request, response);
        } else if (servletPath.endsWith(CALLBACK_URI_SUFFIX.toLowerCase())) {
            doCallback(request, response);
        } else if (servletPath.endsWith(PROFILE_URI_SUFFIX.toLowerCase())) {
            doProfile(request, response);
        } else {
            throw new UnavailableException("requested resource is unknown");
        }
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);

        try {
            GoogleOAuthBase.AuthLoginInfo loginInfo = googleOAuthBase.getLoginInfo(session.getId());
            response.setContentType("text/plain");
            response.getWriter().write("\ndoLogin: " + loginInfo.uri.toString());

        } catch (OAuthSystemException ex) {
            throw new IOException("the error occured while request to google", ex);
        }

    }

    private void doCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.getWriter().write("\ndoCallback: " + request.getServletPath());
    }

    private void doProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain");
        response.getWriter().write("\ndoProfile: " + request.getServletPath());
    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        authStorage.setProfileStorage(profileSorage);
        googleOAuthBase.setAuthStorage(authStorage);

    }

}
