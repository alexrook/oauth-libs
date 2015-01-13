package oul.web.tools.oauth.profile.impl;

import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import oul.web.tools.oauth.profile.AuthzEntryNotFoundException;
import oul.web.tools.oauth.profile.IAuthzEntryMapper;
import oul.web.tools.oauth.profile.Profile;

/**
 * @author moroz
 */
public class SimpleAuthzEntryMapper implements IAuthzEntryMapper {

    public static String AUTH_COOKIE_NAME = "AUTH_ID";

    @Override
    public Cookie[] map(Profile.AuthzEntry authzEntry) {

        Cookie authCookie = new Cookie(AUTH_COOKIE_NAME, authzEntry.authzId);
        authCookie.setMaxAge(authzEntry.ttl - 1);
        authCookie.setPath("/");

        Cookie[] result = {authCookie};

        return result;
    }

    @Override
    public String unmap(Cookie[] cookies) throws AuthzEntryNotFoundException {
        if (cookies == null) {
            throw new AuthzEntryNotFoundException("AuthzEntry not found in request");
        }
        for (Cookie cookie : cookies) {
            if (cookie.getName().equalsIgnoreCase(AUTH_COOKIE_NAME)) {
                return cookie.getValue();
            }
        }
        throw new AuthzEntryNotFoundException("AuthzEntry not found in request");
    }

    @Override
    public Cookie[] deleteAuthzEntry() {
        Cookie authCookie = new Cookie(AUTH_COOKIE_NAME, "");
        authCookie.setMaxAge(0);
        authCookie.setPath("/");
        Cookie[] result = {authCookie};
        return result;
    }

    @Override
    public String generateAuthzId(HttpServletRequest request) throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            throw new IOException("illegal state while generating AuthzEntry ID");
        }

        return session.getId();
    }

}
