package oul.web.tools.oauth;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import javax.inject.Singleton;
import javax.servlet.http.Cookie;

/**
 * @author moroz
 */
@Singleton
public class MemoryUserStorage implements IUserStorageEx, IConst {

    private class ProfileEntry {

        Date dateReg;
        int ttl = 3600;
        Profile profile;

        public ProfileEntry(Profile profile, Date dateReg) {

            this.dateReg = dateReg;
            this.profile = profile;

        }
    }

    private final Map<String, ProfileEntry> storage = new TreeMap<String, ProfileEntry>();

    @Override
    public Cookie registerEx(Profile profile) throws IOException {

        String key = register(profile);

        Cookie result = new Cookie(AUTH_COOKIE_NAME, key);
        result.setMaxAge(3600);
        result.setPath("/");

        return result;
    }

    @Override
    public Profile get(Cookie cookie) throws IOException {
        return get(cookie.getValue());
    }

    private ProfileEntry getProfileEntry(String id) {
        return storage.get(id);
    }

    @Override
    public boolean check(Cookie cookie) throws IOException {
        return check(cookie.getValue());
    }

    @Override
    public boolean check(String id) throws IOException {
        ProfileEntry buf = getProfileEntry(id);
        if (buf == null) {
            return false;
        }
        int t = new Date().compareTo(buf.dateReg);
        if ((t > 0) && (t < buf.ttl)) {
            return true;
        } else {
            unregister(id);
            return false;
        }
    }

    @Override
    public boolean unregister(Cookie cookie) throws IOException {
        return unregister(cookie.getValue());
    }

    @Override
    public boolean unregister(String id) throws IOException {
        storage.remove(id);
        return true;
    }

    @Override
    public String register(Profile profile) throws IOException {
        
        String key = calcKey(profile);
        ProfileEntry p = getProfileEntry(key);
        
        if (p != null) {
            p.dateReg = new Date();
        } else {
            storage.put(key, new ProfileEntry(profile, new Date()));
        }

        return key;
        
    }

    @Override
    public Profile get(String id) throws IOException {
        Profile result = getProfileEntry(id).profile;
        if (result == null) {
            throw new IOException("profile not found");
        }
        return result;
    }

    public String calcKey(Profile profile) throws IOException {
        try {
            byte[] buf = profile.getId().getBytes();
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            return toHex(md5.digest(buf));
        } catch (Exception e) {
            throw new IOException("could not create key");
        }

    }
    /*
     * http://stackoverflow.com/questions/332079/in-java-how-do-i-convert-a-byte-array-to-a-string-of-hex-digits-while-keeping-l
     */

    public static String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

}
