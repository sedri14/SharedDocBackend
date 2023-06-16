package docSharing.filter;

import docSharing.entities.User;
import docSharing.service.AuthService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class TokenFilter implements Filter {
    public static final Logger logger = LogManager.getLogger(TokenFilter.class);
    private final AuthService authService;

    public TokenFilter(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("In Token Filter doFilter");

        MutableHttpServletRequest request = new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //Verify token for each endpoint except Auth and Ws.
        if (!((HttpServletRequest) servletRequest).getServletPath().startsWith("/auth/") && !((HttpServletRequest) servletRequest).getServletPath().startsWith("/ws")) {
            String token = request.getHeader("token");
            if (null != token) {
                User user = authService.getCachedUser(token);
                if (null != user) {
                    request.setAttribute("token", token);
                    request.setAttribute("user", user);
                    filterChain.doFilter(request, response);
                } else returnBadResponse(response);
            } else returnBadResponse(response);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void returnBadResponse(HttpServletResponse res) throws IOException {
        res.sendError(401, "Unauthorized");
    }

    public void destroy() {
        Filter.super.destroy();
    }
}