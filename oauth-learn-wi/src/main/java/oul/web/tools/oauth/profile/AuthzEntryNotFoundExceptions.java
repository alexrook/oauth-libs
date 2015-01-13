package oul.web.tools.oauth.profile;

/**
 * @author moroz
 */
public class AuthzEntryNotFoundExceptions extends Exception {

    public AuthzEntryNotFoundExceptions(String msg) {
        super(msg);
    }

    public AuthzEntryNotFoundExceptions(String msg, Throwable clause) {
        super(msg, clause);
    }
}
