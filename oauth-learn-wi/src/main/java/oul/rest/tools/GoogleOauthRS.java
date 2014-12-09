package oul.rest.tools;

import java.io.IOException;
import java.net.URI;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.OAuthProviderType;
import org.apache.oltu.oauth2.common.message.types.GrantType;

/**
 * @author moroz
 */
@Path("/ggl")
public class GoogleOauthRS {

    @GET
    @Path("first")
    public Response firstPhase() {
        try {

            OAuthClientRequest request = OAuthClientRequest
                    .authorizationProvider(OAuthProviderType.GOOGLE)
                    .setClientId(getGoogleClientId())
                    .setScope(getGoogleClientScope())
                    .setRedirectURI(getGoogleClientRedirectURI())
                    .setResponseType(getGoogleClientResponseType())
                    .buildQueryMessage();

            String location = request.getLocationUri();

            Response result = Response.temporaryRedirect(URI.create(location)).build();
            return result;

        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
        }
    }

    @GET
    @Path("callback")
    public Response callback(@Context HttpServletRequest request) {
        try {

            OAuthAuthzResponse oar = OAuthAuthzResponse.oauthCodeAuthzResponse(request);
            String code = oar.getCode();

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

            String accessToken = oAuthResponse.getAccessToken();
            Long expiresIn = oAuthResponse.getExpiresIn();

            Response result = Response.seeOther(URI.create("../#/google-oauth"))
                    .cookie(new NewCookie("accessToken", accessToken),
                            new NewCookie("expiresIn", Long.toString(expiresIn))).build();

            return result;

        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
        }

    }

    private Properties getLocalProperties() throws IOException {
        Properties res = new Properties();
        res.load(GoogleOauthRS.class.getClassLoader().getResourceAsStream("local.properties"));
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
}
