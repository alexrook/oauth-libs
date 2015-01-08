package oul.web.tools.oauth.profile.impl;

import oul.web.tools.oauth.profile.Profile;
import oul.web.tools.oauth.profile.IAuthEntryStorage;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Singleton;
import oul.web.tools.oauth.IConst;
import oul.web.tools.oauth.profile.IProfileStorage;
import oul.web.tools.oauth.profile.Profile.AuthzEntry;

/**
 * @author moroz
 */
@Singleton
public class MemoryAuthEntryStorage implements IAuthEntryStorage, IConst {

    private final Map<String, Profile.AuthzEntry> entrys
            = new TreeMap<String, Profile.AuthzEntry>();

    private IProfileStorage profileStorage;

    @Override
    public Profile.AuthzEntry register(AuthzEntry entry, Profile profile) throws IOException {

        entry.profileId = profileStorage.register(profile);

        entrys.put(entry.sessionId, entry);

        return entry;
    }

    @Override
    public Profile getProfile(String sessionId) throws IOException {

        AuthzEntry entr = get(sessionId);

        Profile ret = profileStorage.get(entr.profileId);

        return ret;

    }

    @Override
    public AuthzEntry get(String sessionId) throws IOException {

        AuthzEntry ret = entrys.get(sessionId);

        if (ret == null) {
            throw new IOException("could not find registered profile");
        }

        return ret;

    }

    @Override
    public boolean check(String sessionId) throws IOException {
        AuthzEntry buf = get(sessionId);

        if (check(buf)) {
            return true;
        } else {
            unregister(sessionId);
            return false;
        }
    }

    public boolean check(AuthzEntry entry) {
        if (entry == null) {
            return false;
        }
        int t = new Date().compareTo(entry.dateReg);
        return (t > 0) && (t < entry.ttl);

    }

    @Override
    public boolean unregister(String sessionId) throws IOException {
        return (entrys.remove(sessionId)) != null;
    }

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
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
