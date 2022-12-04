//package docSharing.filter;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import docSharing.controller.DocController;
//import docSharing.service.AuthService;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Component;
//
//import javax.servlet.*;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.util.Enumeration;
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class TokenFilter implements Filter {
//
//    @Autowired
//    private AuthService authService;
//
//    private static final Logger logger = LogManager.getLogger(TokenFilter.class.getName());
//
//    private final ObjectMapper mapper = new ObjectMapper();
//
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//        logger.info("In TokenFilter...");
//        if (servletRequest instanceof HttpServletRequest) {
//            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
//
//            //test code
//            Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
//
//            if (headerNames != null) {
//                while (headerNames.hasMoreElements()) {
//                    logger.info("Header: " + httpServletRequest.getHeader(headerNames.nextElement()));
//                }
//            }
//
//            //end of test code
//            String token = httpServletRequest.getHeader("user-token");
//            String userId = httpServletRequest.getHeader("user-id");
//            logger.debug("User id is: {}, Token is: {}", userId, token);
//
//            if (authService.isValidToken(userId, token)) {
//                filterChain.doFilter(servletRequest, servletResponse);
//
//            } else {
//                HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
//
//                Map<String, Object> errorDetails = new HashMap<>();
//                errorDetails.put("message", "Invalid token");
//                httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
//                httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
//                mapper.writeValue(httpServletResponse.getWriter(), errorDetails);
//            }
//        }
//    }
//}
