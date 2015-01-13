package oul.web.tools.oauth.profile.impl;

import oul.web.tools.oauth.profile.Profile;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Singleton;
import oul.web.tools.oauth.profile.AuthzEntryNotFoundExceptions;
import oul.web.tools.oauth.profile.IProfileStorage;
import oul.web.tools.oauth.profile.Profile.AuthzEntry;

/**
 * @author moroz
 */
@Singleton
public class MemoryAuthEntryStorage extends AbstractAuthEntryStorage  {

    private final Map<String, Profile.AuthzEntry> entrys
            = new TreeMap<String, Profile.AuthzEntry>();

    private IProfileStorage profileStorage;

    @Override
    public Profile.AuthzEntry register(AuthzEntry entry, Profile profile) throws IOException {

        entry.profileId = profileStorage.register(profile);

        entrys.put(entry.authzId, entry);

        return entry;
    }

    @Override
    public Profile getProfile(String authzEntryId) throws IOException,AuthzEntryNotFoundExceptions {

        AuthzEntry entr = get(authzEntryId);

        Profile ret = profileStorage.get(entr.profileId);

        return ret;

    }

    @Override
    public AuthzEntry get(String authzEntryId) throws IOException, AuthzEntryNotFoundExceptions {

        AuthzEntry ret = entrys.get(authzEntryId);

        if (ret == null) {
            throw new AuthzEntryNotFoundExceptions("could not find AuthzEntry");
        }

        return ret;

    }

    @Override
    public boolean unregister(String authzEntryId) throws IOException {
        return (entrys.remove(authzEntryId)) != null;
    }

    @Override
    public IProfileStorage getProfileStorage() {
        return profileStorage;
    }

    @Override
    public void setProfileStorage(IProfileStorage profileStorage) {
        this.profileStorage = profileStorage;
    }

}
