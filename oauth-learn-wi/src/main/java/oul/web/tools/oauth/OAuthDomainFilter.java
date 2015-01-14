package oul.web.tools.oauth;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oul.web.tools.oauth.profile.IProfileStorage;
import oul.web.tools.oauth.profile.Profile;

/**
 * @author moroz
 */
public class OAuthDomainFilter extends AbstractSessionFilter {

    private static final String INIT_PARAM_ALLOWED_DOMAINS = "allowed_domains";

    private String[] allowedDomains;

    @Inject
    IProfileStorage profiles;

    public OAuthDomainFilter() {
    }

    @Override
    protected void doBeforeProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        super.doBeforeProcessing(request, response);

        Object obj = request.getAttribute(AUTHZ_ENTRY_ATTRIBUTE);
        if (obj != null) { //filter do nothing in case AuthzEntry not found in request
            try {
                Profile.AuthzEntry authzEntry = (Profile.AuthzEntry) obj;
                Profile profile = profiles.get(authzEntry.profileId);
                boolean pass = false;
                for (String domain : allowedDomains) {
                    if (profile.getDomain().endsWith(domain)) {
                        pass = true;
                        break;
                    }
                }
                if (!pass) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized domain");
                } else {
                    request.setAttribute(PRIFILE_ATTRIBUTE, profile);
                }
            } catch (ClassCastException e) {
                throw new ServletException("misconfigured request in domain session filter");
            }
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);

        String domains = getFilterConfig().getInitParameter(INIT_PARAM_ALLOWED_DOMAINS);

        if (domains != null) {
            setAllowedDomains(domains);
        } else {
            throw new ServletException("misconfigured domain session filter");
        }
    }

    private void setAllowedDomains(String domains) {
        this.allowedDomains = domains.split(",");
        for (int i = 0; i < allowedDomains.length; i++) {
            allowedDomains[i] = allowedDomains[i].trim().toLowerCase();
        }
    }

}
