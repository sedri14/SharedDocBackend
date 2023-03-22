package docSharing.filter;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;


public class CorsFilter implements Filter {
    private Set<String> origins = new HashSet<>(Set.of("http://localhost:3001"));

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response=((HttpServletResponse)servletResponse);
        String header = request.getHeader("origin");
        if(origins.contains(header)){
            response.addHeader("Access-Control-Allow-Origin", header);
            response.addHeader("Access-Control-Allow-Headers", "*");
            response.addHeader("Access-Control-Allow-Methods",
                    "GET, OPTIONS, HEAD, PUT, POST, DELETE");
            response.addHeader("Access-Control-Allow-Credentials", "true");

            if (request.getMethod().equals("OPTIONS")) {
                response.setStatus(HttpServletResponse.SC_ACCEPTED);
                return;
            }
        }
        filterChain.doFilter(servletRequest, response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
