package oul.web.tools.oauth;

import oul.web.tools.oauth.profile.Profile;
import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import oul.web.tools.oauth.profile.IAuthEntryStorage;

/**
 * @author moroz
 */
@Singleton
public class GoogleOAuthBase {

    private String propertiesName = "local.properties";

    private IAuthEntryStorage authStorage;

    private final Map<String, String> states = new HashMap<String, String>(15);

    public static class AuthLoginInfo {

        public URI uri;
        public String state;

        public AuthLoginInfo(URI uri, String state) {
            this.uri = uri;
            this.state = state;
        }
    }

    public AuthLoginInfo getLoginInfo(String sessionId) throws IOException, OAuthSystemException {

        UUID uuid = UUID.randomUUID();
        String state = uuid.toString().replaceAll("-", "");

        states.put(sessionId, state);

        OAuthClientRequest request = OAuthClientRequest
                .authorizationProvider(OAuthProviderType.GOOGLE)
                .setClientId(getGoogleClientId())
                .setScope(getGoogleClientScope())
                .setRedirectURI(getGoogleClientRedirectURI())
                .setResponseType(getGoogleClientResponseType())
                .setState(state)
                .buildQueryMessage();

        String location = request.getLocationUri();

        return new AuthLoginInfo(URI.create(location), uuid.toString());

    }

    public Profile.AuthzEntry callback(HttpServletRequest request, String sessionId)
            throws IOException, OAuthSystemException, OAuthProblemException {

        OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);

        String code = oar.getCode(),
                state = oar.getState();

        OAuthClientRequest oautrequest = OAuthClientRequest
                .tokenProvider(OAuthProviderType.GOOGLE)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(getGoogleClientId())
                .setClientSecret(getGoogleClientSecret())
                .setRedirectURI(getGoogleClientRedirectURI())
                .setCode(code)
                .buildBodyMessage();

        String rstate = states.get(sessionId);

        if ((rstate != null) && (rstate.equals(state))) {
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

            OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oautrequest);

            Profile profile = queryProfile(oAuthResponse);

            Profile.AuthzEntry ret = authStorage.register(
                    new Profile.AuthzEntry(new Date(), profile.getId(),
                            sessionId, oAuthResponse.getAccessToken()),
                    profile);

            states.remove(sessionId);//remove state after succesfully getting profile

            return ret;
        } else {
            throw new OAuthSystemException("unregistered state");
        }

    }

    public Profile queryProfile(OAuthJSONAccessTokenResponse oAuthResponse) throws
            IOException, OAuthSystemException, OAuthProblemException {

        OAuthClientRequest bearerClientRequest
                = new OAuthBearerClientRequest(getGoogleApiProfileURI())
                .setAccessToken(oAuthResponse.getAccessToken()).buildQueryMessage();

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

        OAuthResourceResponse resourceResponse = oAuthClient
                .resource(bearerClientRequest,
                        OAuth.HttpMethod.GET, OAuthResourceResponse.class);

        if (resourceResponse.getResponseCode() == 200) {

            Profile ret = new Profile(resourceResponse.getBody());

            return ret;

        } else {
            throw new IOException("could not query profile");
        }

    }

    public boolean unregister(String sessionId) throws IOException {

        try {

            Profile.AuthzEntry authzEntry = authStorage.get(sessionId);

            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation(getGoogleClientRevokeURI())
                    .setParameter("token", authzEntry.accessToken)
                    .buildQueryMessage();
            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());
            OAuthResourceResponse resourceResponse = oAuthClient
                    .resource(request,
                            OAuth.HttpMethod.GET, OAuthResourceResponse.class);

            if (resourceResponse.getResponseCode() == 200) {
                return authStorage.unregister(sessionId);
            } else {
                throw new IOException(resourceResponse.getBody());
            }

        } catch (Exception ex) {
            throw new IOException("could not unregister profile for sessionId=" + sessionId, ex);
        }

    }

    public String getPropertiesName() {
        return propertiesName;
    }

    public void setPropertiesName(String propertiesName) {
        this.propertiesName = propertiesName;
    }

    public IAuthEntryStorage getAuthStorage() {
        return authStorage;
    }

    public void setAuthStorage(IAuthEntryStorage authStorage) {
        this.authStorage = authStorage;
    }

    private Properties getLocalProperties() throws IOException {
        Properties res = new Properties();
        res.load(GoogleOAuthBase.class.getClassLoader().getResourceAsStream(getPropertiesName()));
        return res;
    }

    private String getGoogleClientId() throws IOException {
        return getLocalProperties().getProperty("google.oauth.clientId");
    }

    private String getGoogleClientSecret() throws IOException {
        return getLocalProperties().getProperty("google.oauth.clientSecret");

    }

    private String getGoogleClientRedirectURI() throws IOException {
        return getLocalProperties().getProperty("google.oauth.redirectUri");
    }

    private String getGoogleClientScope() throws IOException {
        return getLocalProperties().getProperty("google.oauth.scope");
    }

    private String getGoogleClientResponseType() throws IOException {
        return getLocalProperties().getProperty("google.oauth.responseType");
    }

    private String getGoogleClientRevokeURI() throws IOException {
        return getLocalProperties().getProperty("google.oauth.revokeUri");
    }

    private String getGoogleApiProfileURI() throws IOException {
        return getLocalProperties().getProperty("google.api.profileUri");
    }

}
