package oul.web.tools.oauth;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oul.web.tools.oauth.profile.AuthzEntryNotFoundException;
import oul.web.tools.oauth.profile.IAuthEntryStorage;
import oul.web.tools.oauth.profile.IAuthzEntryMapper;
import oul.web.tools.oauth.profile.Profile;

/**
 * @author moroz
 */
public class OAuthSessionFilter extends AbstractSessionFilter {

    @Inject
    private IAuthEntryStorage storage;

    @Inject
    private IAuthzEntryMapper authzEntryMapper;

    public OAuthSessionFilter() {
    }

    @Override
    protected void doBeforeProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

       super.doBeforeProcessing(request, response);

        try {
            String authzEntryId = authzEntryMapper.unmap(request);
            
            Profile.AuthzEntry authzEntry=storage.get(authzEntryId);
            
            if (!storage.check(authzEntry.authzId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            }

            request.setAttribute(AUTHZ_ENTRY_ATTRIBUTE, authzEntry);

        } catch (AuthzEntryNotFoundException ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }

    }

}
