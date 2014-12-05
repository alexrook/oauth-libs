package oul.rest.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * @author moroz
 */
@Path("/proxy")
public class ProxyRS {

    private static final String X_PROXY_HEADER = "X-Proxy-This";

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response proxy(@Context HttpHeaders headers, @QueryParam("uri") String uri) {
        int responseCode = 0;
        try {
            URL url = new URL(uri);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");

            Map<String, String> proxyHeaders = getProxyHeaders(headers);
            for (String k : proxyHeaders.keySet()) {
                httpConn.setRequestProperty(k, proxyHeaders.get(k));
            }
            responseCode = httpConn.getResponseCode();
            if (responseCode > 400) {
                throw new IOException(httpConn.getResponseMessage());
            }

            StringBuilder buf = new StringBuilder();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                buf.append(line);
                buf.append("\n");
                line = reader.readLine();
            }
            buf.deleteCharAt(buf.length() - 1);
            httpConn.disconnect();

            return Response.ok(buf.toString()).build();

        } catch (Exception e) {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(responseCode + ":" + e.getMessage()).build();
        }

    }

    private Map<String, String> getProxyHeaders(HttpHeaders headers) throws IOException {

        Map<String, String> map = new HashMap<String, String>();

        for (String key : headers.getRequestHeaders().keySet()) {
            if (key.toLowerCase().startsWith(X_PROXY_HEADER.toLowerCase())) {
                StringBuilder buf = new StringBuilder();
                String hk = "";
                int i = 0;
                for (String v : headers.getRequestHeader(key)) {
                    if (i == 0) {
                        int k = v.indexOf("=");
                        if (k < 0) {
                            throw new IOException("malformed X-Proxy-This header");
                        }
                        hk = v.substring(0, k);
                        buf.append(v.substring(k + 1));
                    } else {
                        buf.append(v);
                    }
                    i++;

                }
                map.put(hk, buf.toString());
            }

        }
        return map;
    }
}
