package oul.web.tools.oauth.profile;

import java.io.IOException;

/**
 * @author moroz
 */
public interface IAuthEntryStorage {

    Profile.AuthzEntry register(Profile.AuthzEntry entry, Profile profile)
            throws IOException;

    Profile.AuthzEntry get(String authzEntryId) throws IOException,AuthzEntryNotFoundException;

    Profile getProfile(String authzEntryId) throws IOException,AuthzEntryNotFoundException;

    boolean check(String authzEntryId) throws IOException;

    boolean unregister(String authzEntryId) throws IOException;

    void setProfileStorage(IProfileStorage profileStorage);

    IProfileStorage getProfileStorage();

}
