package oul.web.tools.oauth.profile.impl;

import java.io.IOException;
import java.util.Date;
import oul.web.tools.oauth.profile.AuthzEntryNotFoundExceptions;
import oul.web.tools.oauth.profile.IAuthEntryStorage;
import oul.web.tools.oauth.profile.Profile;

/**
 * @author moroz
 */
public abstract class AbstractAuthEntryStorage implements IAuthEntryStorage {

    @Override
    public boolean check(String authzEntryId) throws IOException {
        Profile.AuthzEntry buf;
        try {
            buf = get(authzEntryId);
        } catch (AuthzEntryNotFoundExceptions ex) {
            return false;
        }
        if (check(buf)) {
            return true;
        } else {
            unregister(authzEntryId);
            return false;
        }
    }

    public boolean check(Profile.AuthzEntry entry) {
        if (entry == null) {
            return false;
        }
        int t = new Date().compareTo(entry.dateReg);
        return (t > 0) && (t < entry.ttl);

    }

}
