package oul.web.tools.oauth.profile;

/**
 * @author moroz
 */
public class AuthzEntryNotFoundException extends Exception {

    public AuthzEntryNotFoundException(String msg) {
        super(msg);
    }

    public AuthzEntryNotFoundException(String msg, Throwable clause) {
        super(msg, clause);
    }
}
