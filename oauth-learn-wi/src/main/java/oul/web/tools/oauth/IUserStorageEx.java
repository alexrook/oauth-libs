package oul.web.tools.oauth;

import java.io.IOException;
import javax.servlet.http.Cookie;

/**
 * @author moroz
 */
public interface IUserStorageEx extends IUserStorage {

    Cookie registerEx(Profile profile)
            throws IOException;

    Profile get(Cookie cookie) throws IOException;

    boolean check(Cookie cookie) throws IOException;

    boolean unregister(Cookie cookie) throws IOException;

}
