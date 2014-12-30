package oul.web.tools.oauth;

import java.io.IOException;

/**
 * @author moroz
 */
public interface IUserStorage {

    boolean check(String id) throws IOException;

    String register(Profile profile) throws IOException;

    Profile get(String id) throws IOException;

    boolean unregister(String id) throws IOException;

}
