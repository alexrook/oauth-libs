package oul.web.tools.oauth.profile;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
     * @param response
     * @return 
     * @throws IOException
     */
    HttpServletResponse map(Profile.AuthzEntry authzEntry,HttpServletResponse response) throws IOException;

    /**
     *
     * 
     * @param request
     * @return auhtzEntry id from request cookies;
     * @throws AuthzEntryNotFoundException
     */
    String unmap(HttpServletRequest request) throws AuthzEntryNotFoundException;

    /**
     *
     * @param response
     * @return 
     */
    HttpServletResponse deleteAuthzEntry(HttpServletResponse response);

}
