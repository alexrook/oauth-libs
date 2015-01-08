package oul.web.tools.oauth.profile;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;

/**
 * @author moroz
 *
 * User profile mainly based on get https://www.googleapis.com/plus/v1/people/me
 *
 * see src/test/resources/example-google-apps-user.json &
 * example-non-google-apps-user.json
 */
public class Profile {

    //profile metadata
    public static class AuthzEntry {

        public Date dateReg;
        public int ttl = 3600;
        public String sessionId, accessToken, profileId;

        public AuthzEntry(Date dateReg,
                String profileId,
                String sessionID,
                String accessToken) {
            this.dateReg = dateReg;
            this.sessionId = sessionID;
            this.profileId = profileId;
            this.accessToken = accessToken;
        }
    }

    public static class Name {

        private String firstName, lastName, displayName;

        public Name() {
        }

        public Name(String firstName, String lastName, String displayName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
        }

        @Override
        public String toString() {
            StringBuilder ret = new StringBuilder("Name [");
            ret.append("firstName=").append(firstName).append(",")
                    .append("lastName=").append(lastName).append(",")
                    .append("displayName").append(displayName)
                    .append("]");

            return ret.toString();
        }

        public JsonObject toJsonObject() {

            JsonObject ret = Json.createObjectBuilder()
                    .add("firstName", firstName)
                    .add("lastName", lastName)
                    .add("displayName", displayName)
                    .build();

            return ret;
        }

        public String toJsonString() {
            StringWriter sw = new StringWriter(93);//~calc 

            JsonWriter jsw = Json.createWriter(sw);

            jsw.writeObject(toJsonObject());

            return sw.toString();

        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getDisplayName() {
            return (displayName != null && displayName.length() > 0) ? displayName : firstName + ' ' + lastName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

    }

    private String id, domain;

    private Name name;

    private URI imgURI;

    private List<URI> emails;

    public Profile() {

    }

    public Profile(String id, Name name, String domain, URI imgURI, List<URI> emails) {
        this.id = id;
        this.name = name;
        this.domain = domain;
        this.imgURI = imgURI;
        this.emails = emails;
    }

    public Profile(String jsonStr) throws IOException {
        JsonReader reader = null;
        try {
            reader = Json.createReader(new StringReader(jsonStr));
            JsonObject json = reader.readObject();
            this.id = json.getString("id");
            this.name = new Name(json.getJsonObject("name").getString("givenName"),
                    json.getJsonObject("name").getString("familyName"),
                    getOptField("displayName", json.getJsonObject("name")));

            this.domain = getOptField("domain", json);

            String uri = getOptField("url", json.getJsonObject("image"));
            this.imgURI = uri.length() > 0 ? new URI(uri) : null;

            JsonArray jsemails = json.getJsonArray("emails");
            this.emails = new ArrayList<URI>(5);
            for (JsonValue email : jsemails) {
                this.emails.add(new URI(
                        ((JsonObject) email).getString("value")));
            }

            if (this.emails.isEmpty()) {
                throw new IOException("non filled json string");
            }

        } catch (URISyntaxException e) {
            throw new IOException("malformed profile JSON exception");
        } catch (IOException e) {
            throw new IOException("malformed profile JSON exception");
        } finally {
            if (reader != null) {
                reader.close();
            }

        }
    }

    private String getOptField(String name, JsonObject json) {
        try {
            return json.getString(name);
        } catch (Exception e) {
            return "";
        }
    }

    public String toJsonString() {

        StringWriter sw = new StringWriter(700);//~calc 

        JsonWriter jsw = Json.createWriter(sw);

        jsw.writeObject(toJsonObject());

        return sw.toString();
    }

    public JsonObject toJsonObject() {

        JsonArrayBuilder emailsBuilder = Json.createArrayBuilder();

        for (URI uri : getEmails()) {
            emailsBuilder.add(uri.toString());
        }

        JsonObject ret = Json.createObjectBuilder()
                .add("id", id)
                .add("domain", domain)
                .add("imgUri", imgURI.toString())
                .add("emails", emailsBuilder)
                .add("name", name.toJsonObject())
                .build();

        return ret;

    }

    @Override
    public String toString() {

        StringBuilder ret = new StringBuilder("Profile [");

        ret.append("id=").append(id).append(",")
                .append("domain=").append(domain).append(",")
                .append(name.toString()).append(",")
                .append("Emails ").append(Arrays.toString(emails.toArray()))
                .append("]");

        return ret.toString();

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public URI getImgURI() {
        return imgURI;
    }

    public void setImgURI(URI imgURI) {
        this.imgURI = imgURI;
    }

    public List<URI> getEmails() {
        return emails;
    }

    public void setEmails(List<URI> emails) {
        this.emails = emails;
    }

}
