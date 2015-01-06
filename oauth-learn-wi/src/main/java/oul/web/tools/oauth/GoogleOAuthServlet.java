package oul.web.tools.oauth;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import oul.web.tools.oauth.profile.IAuthEntryStorage;
import oul.web.tools.oauth.profile.IProfileStorage;
import oul.web.tools.oauth.profile.Profile;

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

    /**
     * URI_SUFFIX_xxx strings must be exactly like the ends of the
     * paths(url-pattern) that is mapped to this servlet
     *
     * see web.xml
     */
    public static final String URI_SUFFIX_LOGIN = "login",
            URI_SUFFIX_LOGOUT = "logout",
            URI_SUFFIX_CALLBACK = "callback",
            URI_SUFFIX_PROFILE = "profile",
            /*uri to redirect after succesfully callback*/
            PARAM_SUCCESS_REDIRECT_URI = "success-redirect-uri";

    private String successRedirectURI;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath().toLowerCase();

        if (servletPath.endsWith(URI_SUFFIX_LOGIN.toLowerCase())) {
            doLogin(request, response);
        } else if (servletPath.endsWith(URI_SUFFIX_LOGOUT.toLowerCase())) {
            doLogout(request, response);
        } else if (servletPath.endsWith(URI_SUFFIX_CALLBACK.toLowerCase())) {
            doCallback(request, response);
        } else if (servletPath.endsWith(URI_SUFFIX_PROFILE.toLowerCase())) {
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
            response.sendRedirect(loginInfo.uri.toString());

            /*response.setContentType("text/plain");
             response.getWriter().write("\ndoLogin: " + loginInfo.uri.toString());*/
        } catch (OAuthSystemException ex) {
            throw new IOException("the error occured while request to google", ex);
        }

    }

    private void doCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new ServletException("illegal state while doing callback");
        }

        try {
            googleOAuthBase.callback(request, session.getId());

            response.sendRedirect(successRedirectURI);

        } catch (OAuthSystemException ex) {
            throw new IOException("the error occured while request to google", ex);
        } catch (OAuthProblemException ex) {
            throw new IOException("the error occured while request to google", ex);
        }

    }

    private void doProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new ServletException("illegal state while get profile");
        }

        Profile profile = profileSorage.get(session.getId());

        response.setContentType("text/plain");
        response.getWriter().write("\ndoProfile:\n " + profile.toString());
    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new ServletException("illegal state while logout");
        }

        googleOAuthBase.unregister(session.getId());

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        authStorage.setProfileStorage(profileSorage);
        googleOAuthBase.setAuthStorage(authStorage);

        successRedirectURI = config.getInitParameter(PARAM_SUCCESS_REDIRECT_URI);

        if ((successRedirectURI == null) || (successRedirectURI.isEmpty())) {
            throw new ServletException("misconfigured servlet");
        }

    }

}
