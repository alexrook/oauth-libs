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
     * @param request with AuthzEntry
     * @return New AuthzEntry ID based on request information
     * @throws IOException
     */
    String generateAuthzId(HttpServletRequest request) throws IOException;

    /**
     *
     * @param authzEntry
     * @param response
     * @return response with mapped AuthzEntry
     * @throws IOException
     */
    HttpServletResponse map(Profile.AuthzEntry authzEntry,HttpServletResponse response) throws IOException;

    /**
     *
     * 
     * @param request with AuthzEntry
     * @return auhtzEntry id from request cookies;
     * @throws AuthzEntryNotFoundException
     */
    String unmap(HttpServletRequest request) throws AuthzEntryNotFoundException;

    /**
     *
     * @param response
     * @return response with deleted mapped AuthzEntry
     */
    HttpServletResponse deleteAuthzEntry(HttpServletResponse response);

}
