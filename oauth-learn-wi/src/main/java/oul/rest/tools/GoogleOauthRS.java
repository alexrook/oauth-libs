package oul.rest.tools;

import java.net.URI;
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
                    .setClientId("xxx")
                    //.setScope(uri)
                    .setRedirectURI("http://localhost:8080/oul/rest/ggl/callback")
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
            OAuthAuthzResponse oar = OAuthAuthzResponse.oAuthCodeAndTokenAuthzResponse(request);
            String code = oar.getCode();

            OAuthClientRequest oautrequest = OAuthClientRequest
                    .tokenProvider(OAuthProviderType.FACEBOOK)
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId("xxxxxx")
                    .setClientSecret("xxxxx")
                    .setRedirectURI("http://localhost:8080/oul/rest/ggl/callback")
                    .setCode(code)
                    .buildQueryMessage();

            OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

            OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oautrequest);

            String accessToken = oAuthResponse.getAccessToken();
            Long expiresIn = oAuthResponse.getExpiresIn();

            Response result = Response.seeOther(URI.create("http://localhost:8080/oul/index.html"))
                    .cookie(new NewCookie("accessToken", accessToken), 
                            new NewCookie("expiresIn", Long.toString(expiresIn))).build();

            return result;

        } catch (Exception ex) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(ex).build();
        }

    }

}
