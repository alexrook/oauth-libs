package oul.web.tools.oauth;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author moroz
 */
public class SessionFilter implements Filter, IConst {

    private static boolean debug = false;
    private static long counter = 0;

    private FilterConfig filterConfig = null;

    @Inject
    private IUserStorageEx storage;

    public SessionFilter() {
    }

    private boolean doBeforeProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        if (debug) {
            log("SessionFilter:DoBeforeProcessing");
        }

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equalsIgnoreCase(AUTH_COOKIE_NAME)) {
                    if (storage.check(cookie)) {
                        return true;
                    }
                }
            }

        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unathorized");
        return false;
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

        boolean before = doBeforeProcessing(httpReq, httpRes);

        if (before) {
            chain.doFilter(request, response);
        }

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
