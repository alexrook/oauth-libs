package oul.web.tools.oauth.profile;

import java.io.IOException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @author moroz
 */
public interface IAuthzEntryMapper {

    /**
     *
     * @param request
     * @return New AuthzEntry ID based on request information
     * @throws IOException
     */
    String generateAuthzId(HttpServletRequest request) throws IOException;

    /**
     *
     * @param authzEntry
     * @return array of cookies for mapping an AuthzEntry
     * @throws IOException
     */
    Cookie[] map(Profile.AuthzEntry authzEntry) throws IOException;

    /**
     *
     * @param cookies an request cookies
     * @return auhtzEntry id from request cookies;
     * @throws AuthzEntryNotFoundException
     */
    String unmap(Cookie[] cookies) throws AuthzEntryNotFoundException;

    /**
     *
     * @return 0-time-live cookies for deleting authEntry
     */
    Cookie[] deleteAuthzEntry();

}
