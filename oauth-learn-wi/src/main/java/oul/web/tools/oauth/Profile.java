package oul.web.tools.oauth;

import java.net.URI;
import java.util.List;

/**
 * @author moroz
 * 
 * User profile mainly based
 * on get https://www.googleapis.com/plus/v1/people/me
 * 
 */
public class Profile {

    public static class Name {

        private String firstName, lastName, displayName;

        public Name() {
        }

        public Name(String firstName, String lastName, String displayName) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.displayName = displayName;
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
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

    }

    private String id,domain;

    private Name name;

    private URI imgURI;
    
    private List<URI> emails;

    public Profile() {

    }

    public Profile(String id,Name name,String domain,URI imgURI,List<URI> emails){
            this.id=id;
            this.name=name;
            this.domain=domain;
            this.imgURI=imgURI;
            this.emails=emails;
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
