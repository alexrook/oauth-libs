package oul.web.tools.oauth;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import java.util.UUID;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import oul.rest.tools.GoogleOauthRS;

/**
 * @author moroz
 */
public class GoogleOAuthBase {

    private String propertiesName = "local.properties";

    private IUserStorage userStorage;

    public static class AuthLoginInfo {

        public URI uri;
        public String state;

        public AuthLoginInfo(URI uri, String state) {
            this.uri = uri;
            this.state = state;
        }
    }

    public AuthLoginInfo getLoginInfo(String sessionId) throws IOException, OAuthSystemException {

        OAuthClientRequest request = OAuthClientRequest
                .authorizationProvider(OAuthProviderType.GOOGLE)
                .setClientId(getGoogleClientId())
                .setScope(getGoogleClientScope())
                .setRedirectURI(getGoogleClientRedirectURI())
                .setResponseType(getGoogleClientResponseType())
                .buildQueryMessage();

        String location = request.getLocationUri();

        UUID uuid = UUID.randomUUID();//todo: move uuid&state to temporary storage

        return new AuthLoginInfo(URI.create(location), uuid.toString());

    }

    public Profile callback(String code, String state) throws IOException, OAuthSystemException, OAuthProblemException {

        OAuthClientRequest oautrequest = OAuthClientRequest
                .tokenProvider(OAuthProviderType.GOOGLE)
                .setGrantType(GrantType.AUTHORIZATION_CODE)
                .setClientId(getGoogleClientId())
                .setClientSecret(getGoogleClientSecret())
                .setRedirectURI(getGoogleClientRedirectURI())
                .setCode(code)
                .buildBodyMessage();

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

        OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oautrequest);

        Profile ret = queryProfile(oAuthResponse);

        userStorage.register(ret);

        return queryProfile(oAuthResponse);

    }

    public Profile queryProfile(OAuthJSONAccessTokenResponse oAuthResponse) throws IOException, OAuthSystemException, OAuthProblemException {
        OAuthClientRequest bearerClientRequest
                = new OAuthBearerClientRequest(getGoogleApiProfileURI())
                .setAccessToken(oAuthResponse.getAccessToken()).buildQueryMessage();

        OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

        OAuthResourceResponse resourceResponse = oAuthClient
                .resource(bearerClientRequest,
                        OAuth.HttpMethod.GET, OAuthResourceResponse.class);

        if (resourceResponse.getResponseCode() == 200) {

            userStorage.register(null);//todo
        }

        return null; //todo

    }

    public String getPropertiesName() {
        return propertiesName;
    }

    public void setPropertiesName(String propertiesName) {
        this.propertiesName = propertiesName;
    }

    public IUserStorage getUserStorage() {
        return userStorage;
    }

    public void setUserStorage(IUserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private Properties getLocalProperties() throws IOException {
        Properties res = new Properties();
        res.load(GoogleOauthRS.class.getClassLoader().getResourceAsStream(getPropertiesName()));
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
