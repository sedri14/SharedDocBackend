package docSharing.filter;

import docSharing.entities.INode;
import docSharing.entities.User;
import docSharing.enums.UserRole;
import docSharing.service.AuthService;
import docSharing.service.FileSystemService;
import docSharing.service.SharedRoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class PermissionFilter implements Filter {
    public static final Logger logger = LogManager.getLogger(PermissionFilter.class);

    private final FileSystemService fileSystemService;

    private final SharedRoleService sharedRoleService;

    public PermissionFilter(FileSystemService fileSystemService, SharedRoleService sharedRoleService) {
        this.fileSystemService = fileSystemService;
        this.sharedRoleService = sharedRoleService;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        logger.info("In Permission Filter doFilter");
        MutableHttpServletRequest request = new MutableHttpServletRequest((HttpServletRequest) servletRequest);
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        logger.info("url: {}", request.getRequestURL().toString());

        List<String> ownerPermissionEndpoints = Arrays.asList("/fs/rename", "/fs/level", "/fs/move", "/fs/delete", "/fs/changeUserRole", "/doc/getDoc/", "/doc/changeUserRole", "/doc/roles");
        String servletPath = request.getServletPath();
        logger.info("servlet path {}", servletPath);
        boolean isIncluded = ownerPermissionEndpoints.stream().anyMatch(servletPath::startsWith);

        if (isIncluded) {
            Long inodeId = Long.parseLong(request.getHeader("inodeId"));
            INode inode = fileSystemService.fetchINodeById(inodeId);
            User user = (User)request.getAttribute("user");
            boolean isOwner = isOwner(user, inode);

            if (servletPath.startsWith("/doc/getDoc")){
                if (!isOwner && !hasRole(user, inode)) {
                    returnBadResponse(response);
                }
            } else {
                if(!isOwner) {
                    returnBadResponse(response);
                }
            }
            request.setAttribute("inode", inode);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isOwner(User user, INode inode) {
        return user.getId().equals(inode.getOwner().getId());
    }

    private boolean hasRole(User user, INode inode) {
       return sharedRoleService.hasRole(inode, user);
    }

    private void returnBadResponse(HttpServletResponse res) throws IOException {
        res.sendError(401, "No Access");
    }
}
