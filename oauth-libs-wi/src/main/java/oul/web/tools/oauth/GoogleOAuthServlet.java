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
import oul.web.tools.oauth.profile.AuthzEntryNotFoundException;
import oul.web.tools.oauth.profile.IAuthEntryStorage;
import oul.web.tools.oauth.profile.IAuthzEntryMapper;
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

    @Inject
    IAuthzEntryMapper authMapper;

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

        if (isLogIn(request)) {
            response.sendRedirect(successRedirectURI);

        } else {
            try {
                String loginUri = googleOAuthBase.getOAuthLoginURI(session.getId());
                response.sendRedirect(loginUri);

            } catch (OAuthSystemException ex) {
                throw new IOException("the error occured while request to google", ex);
            }
        }

    }

    private boolean isLogIn(HttpServletRequest request) {
        try {
            String authzId = authMapper.unmap(request);
            return authStorage.check(authzId);
        } catch (AuthzEntryNotFoundException ex) {
            return false;
        } catch (IOException ex) {
            return false;
        }
    }

    private void doCallback(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new ServletException("illegal state while doing callback");
        }

        try {

            Profile.AuthzEntry authEntry = googleOAuthBase.callback(request);

            authMapper.map(authEntry, response);

            response.sendRedirect(successRedirectURI);

        } catch (OAuthSystemException ex) {
            throw new IOException("the error occured while request to google", ex);
        } catch (OAuthProblemException ex) {
            throw new IOException("the error occured while request to google", ex);
        }

    }

    private void doProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            String authzEntryId = authMapper.unmap(request);
            Profile profile = authStorage.getProfile(authzEntryId);

            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");
            response.getWriter().write(profile.toJsonString());

        } catch (AuthzEntryNotFoundException ex) {

            authMapper.deleteAuthzEntry(response);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");

        }

    }

    private void doLogout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            googleOAuthBase.unregister(authMapper.unmap(request));
            response.setStatus(200);
        } catch (AuthzEntryNotFoundException e) {
            //http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html
            response.setStatus(205);//205-Reset Content
        } finally {
            HttpSession session = request.getSession(false);
            authMapper.deleteAuthzEntry(response);
            if (session != null) {
                session.invalidate();
            }
         
        }

    }

    @Override
    public void init(ServletConfig config) throws ServletException {

        authStorage.setProfileStorage(profileSorage);
        googleOAuthBase.setAuthStorage(authStorage);
        googleOAuthBase.setAuthzEntryMapper(authMapper);

        successRedirectURI = config.getInitParameter(PARAM_SUCCESS_REDIRECT_URI);

        if ((successRedirectURI == null) || (successRedirectURI.isEmpty())) {
            throw new ServletException("misconfigured servlet");
        }

        if (!successRedirectURI.toLowerCase().startsWith("http")) {
            successRedirectURI = config.getServletContext().getContextPath() + "/" + successRedirectURI;
        }

    }

}
