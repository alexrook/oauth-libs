package oul.web.tools.oauth.profile;

import java.io.IOException;

/**
 * @author moroz
 */
public interface IAuthEntryStorage {

    Profile.AuthzEntry register(Profile.AuthzEntry entry, Profile profile)
            throws IOException;

    Profile.AuthzEntry get(String sessionId) throws IOException;

    Profile getProfile(String sessionId) throws IOException;

    boolean check(String sessionId) throws IOException;

    boolean unregister(String sessionId) throws IOException;

    void setProfileStorage(IProfileStorage profileStorage);

    IProfileStorage getProfileStorage();

}
