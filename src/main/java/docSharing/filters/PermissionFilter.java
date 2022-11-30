//package docSharing.filters;
//
//import docSharing.controller.UserController;
//import docSharing.service.PermissionService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.GenericFilterBean;
//
//import javax.servlet.FilterChain;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import java.io.IOException;
//
//@Component
//public class PermissionFilter extends GenericFilterBean {
//    @Autowired
//    PermissionService permissionService;
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        System.out.println("in permission");
//        chain.doFilter(request, response);
//    }
//}
