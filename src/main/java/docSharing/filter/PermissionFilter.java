package docSharing.filter;

import docSharing.enums.UserRole;
import docSharing.service.AuthService;
import docSharing.service.FileSystemService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PermissionFilter implements Filter {
    public static final Logger logger = LogManager.getLogger(PermissionFilter.class);

    private final FileSystemService fileSystemService;
    private final AuthService authService;
    private final Map<UserRole, List<String>> actionByRole;

    //TODO: create the hard coded map with Properties class.
    public PermissionFilter(FileSystemService fileSystemService, AuthService authService) {
        actionByRole = new HashMap<>();
        //initPermissionsMap() //from permission_map.properties file

        this.fileSystemService = fileSystemService;
        this.authService = authService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }
//
//    public UserRole getUserRole(MutableHttpServletRequest request) {
//        Long eventId = Long.valueOf((String) request.getAttribute("eventId"));
//        User user = (User) request.getAttribute("user");
//        if (eventId == null || user == null) return null;
//        return eventService.getUserRole(user, eventId);
//    }

//    public void updateFilter(MutableHttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
//        Long eventId = Long.valueOf((String) request.getAttribute("eventId"));
//        String title = request.getHeader("title");
//        Integer duration = Integer.valueOf(request.getHeader("duration"));
//        String date = request.getHeader("time");
//        Event event = eventService.fetchEventById(eventId);
//        DateTimeFormatter format = DateTimeFormatter.ofPattern("HH:mm:ss");
//        if (!title.equals(event.getTitle()) || duration != event.getDuration() || !date.equals(event.getDateTime().format(format))) {
//            returnBadResponse(response, "Admin can't edit event's title, duration or event's date");
//        } else {
//            filterChain.doFilter(request, response);
//        }
//    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("In Permission Filter doFilter");
        MutableHttpServletRequest req = new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletResponse res = (HttpServletResponse) servletResponse;
//        String path = ((HttpServletRequest) servletRequest).getServletPath();
//        if (path.startsWith("/event/")) {
//            String operation = path.split("/")[2];
//            List<UserRole> roles = permissionsMap.get(operation);
//            if (roles != null) {
//                if (operation.equals("update") && getUserRole(req).equals(UserRole.ADMIN)) {
//                    updateFilter(req, res, filterChain);
//                } else if (!roles.contains(getUserRole(req))) {
//                    returnBadResponse(res, "The user have no permissions to do this operation");
//                } else filterChain.doFilter(req, res);
//            } else filterChain.doFilter(req, res);
//        } else filterChain.doFilter(req, res);
        filterChain.doFilter(req, res);
    }

    private void returnBadResponse(HttpServletResponse res, String errorMessage) throws IOException {
        res.sendError(401, errorMessage);
    }
}
