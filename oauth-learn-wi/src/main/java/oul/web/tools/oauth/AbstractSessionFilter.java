package oul.web.tools.oauth;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author moroz
 */
public abstract class AbstractSessionFilter implements Filter {

    public static final String AUTHZ_ENTRY_ATTRIBUTE = "web.tools.oauth.profile.authz_entry",
            PRIFILE_ATTRIBUTE = "web.tools.oauth.profile",
            INIT_PARAM_EXCLUDE = "exclude";

    private boolean debug = false;
    private long counter = 0;
    private String filterName = "";
    private String excludeRegex = "";

    private FilterConfig filterConfig = null;

    public AbstractSessionFilter() {
    }

    protected void doBeforeProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log(filterName + ":DoBeforeProcessing");
        }

    }

    protected void doAfterProcessing(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (debug) {
            log(filterName + ":DoAfterProcessing");
        }

    }

    @Override
    public final void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        if (debug) {
            counter++;
            log(filterName + ":doFilter()=" + counter);
        }

        HttpServletRequest httpReq = (HttpServletRequest) request;
        HttpServletResponse httpRes = (HttpServletResponse) response;

        if ((excludeRegex != null) && (!excludeRegex.isEmpty())) {
            if (httpReq.getServletPath().toLowerCase().matches(excludeRegex)) {
                chain.doFilter(request, response);
                return;
            }
        }

        doBeforeProcessing(httpReq, httpRes);
       
        if (!response.isCommitted()) {
            chain.doFilter(request, response);
        }

        doAfterProcessing(httpReq, httpRes);

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        setFilterConfig(filterConfig);
        if (getFilterConfig() != null) {
            String isDebug = getFilterConfig().getInitParameter("debug");
            if ((isDebug != null) && (!isDebug.equalsIgnoreCase("false"))) {
                setDebug(true);
            }
            this.filterName = filterConfig.getFilterName();
            this.excludeRegex = filterConfig.getInitParameter(INIT_PARAM_EXCLUDE);
        }

        if (isDebug()) {
            log(filterName + ":Initializing filter");
        }
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

    public void log(String msg) {
        filterConfig.getServletContext().log(msg);
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

}
