package oul.web.tools.oauth.profile.impl;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import oul.web.tools.oauth.profile.Profile;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Singleton;
import oul.web.tools.oauth.profile.IProfileStorage;

/**
 *
 * @author moroz
 */
@Singleton
public class MemoryProfileStorage implements IProfileStorage {

    private final Map<String, Profile> storage = new HashMap<String, Profile>(125);

    @Override
    public boolean check(String id) throws IOException {
        return storage.containsKey(id);
    }

    @Override
    public String register(Profile profile) throws IOException {
        return storage.put(profile.getId(), profile).getId();
    }

    @Override
    public Profile get(String id) throws IOException {
      
        Profile ret = storage.get(id);

        if (ret != null) {
            return ret;
        } else {
            throw new IOException("could not find profile");
        }
    }

    @Override
    public boolean unregister(String id) throws IOException {
        if (check(id)) {
            storage.remove(id);
            return true;
        } else {
            return false;
        }
    }

}
