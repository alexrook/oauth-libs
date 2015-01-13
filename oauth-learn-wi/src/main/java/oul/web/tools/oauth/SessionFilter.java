package oul.web.tools.oauth;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import oul.web.tools.oauth.profile.AuthzEntryNotFoundExceptions;
import oul.web.tools.oauth.profile.IAuthEntryStorage;
import oul.web.tools.oauth.profile.IAuthzEntryMapper;

/**
 * @author moroz
 */
public class SessionFilter implements Filter {

    private static boolean debug = false;
    private static long counter = 0;

    private FilterConfig filterConfig = null;

    @Inject
    private IAuthEntryStorage storage;

    @Inject
    private IAuthzEntryMapper authzEntryMapper;

    public SessionFilter() {
    }

    private void doBeforeProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (debug) {
            log("SessionFilter:DoBeforeProcessing");
        }

        try {
            String authzEntryId = authzEntryMapper.unmap(request.getCookies());
            if (!storage.check(authzEntryId)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unathorized");
            }
        } catch (AuthzEntryNotFoundExceptions ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unathorized");
        }
     
    }

    private void doAfterProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log("SessionFilter:DoAfterProcessing");
        }

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {
        counter++;
        if (debug) {
            log("SessionFilter:doFilter()=" + counter);
        }

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        doBeforeProcessing(httpReq, httpRes);
       
        chain.doFilter(request, response);

        doAfterProcessing(httpReq, httpRes);

    }

    public FilterConfig getFilterConfig() {
        return (this.filterConfig);
    }

    public void setFilterConfig(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void init(FilterConfig filterConfig) {
        this.filterConfig = filterConfig;
        if (filterConfig != null) {
            String isDebug = filterConfig.getInitParameter("debug");
            if ((isDebug != null) && (!isDebug.equalsIgnoreCase("false"))) {
                debug = true;
            }
            if (debug) {
                log("SessionFilter:Initializing filter");
            }
        }
    }

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

}
