//package docSharing.filters;
//
//import docSharing.controller.UserController;
//import docSharing.service.UserService;
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
//public class AuthenticationFilter extends GenericFilterBean {
//    @Autowired
//    UserController userController;
//
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        System.out.println("in authentication");
//        chain.doFilter(request, response);
//    }
//}
