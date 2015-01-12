##Prerequisites

### Create file 'local.properties'

To run this project on Jboss 7.1.1 you need create 'local.properties' file
with next data:

    google.oauth.clientId=<your-google-app-clientID>
    google.oauth.clientSecret=<your-google-app-clientSedret>
    google.oauth.redirectUri=http://localhost:8080/oul/oauth/google/callback
    google.oauth.revokeUri=https://accounts.google.com/o/oauth2/revoke
    google.oauth.scope=profile email
    google.oauth.responseType=code
    google.api.profileUri=https://www.googleapis.com/plus/v1/people/me'


###Setup http BASIC auth on JBoss
To work with 'restricted area'   with BASIC Auth [rfc2617](https://www.ietf.org/rfc/rfc2617.txt)  on jboss 7.1.1 run script:

    <JBOSS_HOME>/bin/add_user.sh

Add 'Application User' to 'ApplicationRealm'  with role 'users'.
Now try to get url  http://localhost:8080/oul/restricted/secret.html

###Setup http DIGEST auth on JBoss
To access to 'restricted area' with DIGEST Auth (rfc2617)  on jboss 7.1.1

* Add security domain to server config:
            
        <security-domain name="appdom" cache-type="default">
            <authentication>
              <login-module code="UsersRoles" flag="required">
                <module-option name="hashAlgorithm" value="MD5"/>
                <module-option name="hashEncoding" value="RFC2617"/>
                <module-option name="hashUserPassword" value="false"/>
                <module-option name="hashStorePassword" value="true"/>
                <module-option name="passwordIsA1Hash" value="true"/>
                <module-option name="storeDigestCallback" 
                         value="org.jboss.security.auth.callback.RFC2617Digest"/>
              </login-module>
           </authentication>
        </security-domain>

* Add users.properties to application classpath with content:

        <username>=<hashed-password>

* To hash user password run:

        java -cp ${JBOSS_HOME}/modules/<...>/picketbox-4.0.7.Final.jar \
          org.jboss.security.auth.callback.RFC2617Digest <username> appdom password>

* Add roles.properties to application classpath with content:

        <username>=users

See more: 

* [Digest authentication with JBoss AS7](https://developer.jboss.org/message/744521)
* [UsersRolesLoginModule wiki](https://developer.jboss.org/wiki/UsersRolesLoginModule)
* [picketbox.jboss.org](http://picketbox.jboss.org/)





