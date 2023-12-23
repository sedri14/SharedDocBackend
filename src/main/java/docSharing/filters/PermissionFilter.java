package docSharing.filters;

import docSharing.fileSystem.INode;
import docSharing.user.User;
import docSharing.user.UserRole;
import docSharing.fileSystem.FileSystemService;
import docSharing.service.SharedRoleService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import javax.servlet.FilterConfig;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@Component
@RequiredArgsConstructor
public class PermissionFilter implements Filter {
    public static final Logger logger = LogManager.getLogger(PermissionFilter.class);

    private final FileSystemService fileSystemService;

    private final SharedRoleService sharedRoleService;

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
            User user = (User) request.getAttribute("user");
            boolean isOwner = isOwner(user, inode);

            if (servletPath.startsWith("/doc/getDoc")) {
                UserRole roleForDoc = getRole(inode, user);
                if (!isOwner && null == roleForDoc) {
                    returnBadResponse(response);
                } else {
                    request.setAttribute("userRole", isOwner ? UserRole.OWNER : roleForDoc);
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            if (!isOwner) {
                returnBadResponse(response);
            }

            request.setAttribute("inode", inode);
        }
        filterChain.doFilter(request, response);
    }

    private boolean isOwner(User user, INode inode) {
        return user.getId().equals(inode.getOwner().getId());
    }

    private UserRole getRole(INode inode, User user) {
        return sharedRoleService.getRole(inode, user);
    }

    private void returnBadResponse(HttpServletResponse res) throws IOException {
        res.sendError(401, "No Access");
    }
}
