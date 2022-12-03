package docSharing.filter;

import docSharing.controller.DocController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class TokenFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(TokenFilter.class.getName());
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
    logger.info("In TokenFilter...");
    if (servletRequest instanceof HttpServletRequest) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        logger.info("Token is: {}", httpServletRequest.getHeader("token"));


    }
    filterChain.doFilter(servletRequest,servletResponse);
    }
}
