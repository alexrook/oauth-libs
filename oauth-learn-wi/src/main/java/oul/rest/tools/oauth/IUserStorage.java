package oul.rest.tools.oauth;

import java.io.IOException;
import javax.servlet.http.Cookie;

/**
 * @author moroz
 */
public interface IUserStorage {

    boolean check(Cookie cookie) throws IOException, UnsupportedOperationException;

    boolean check(String id) throws IOException;

    boolean register(Profile profile) throws IOException;

    Profile get(Cookie cookie) throws IOException, UnsupportedOperationException;

    Profile get(String id) throws IOException;

    boolean unregister(Cookie cookie) throws IOException, UnsupportedOperationException;

    boolean unregister(String id) throws IOException;
}
