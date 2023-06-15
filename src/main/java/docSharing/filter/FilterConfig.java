package docSharing.filter;

import docSharing.service.AuthService;
import docSharing.service.FileSystemService;
import docSharing.service.SharedRoleService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    public static final Logger logger = LogManager.getLogger(FilterConfig.class);
    private final AuthService authService;
    private final FileSystemService fileSystemService;

    private final SharedRoleService sharedRoleService;


    @Autowired
    public FilterConfig(AuthService authService, FileSystemService fileSystemService, SharedRoleService sharedRoleService) {
        this.authService = authService;
        this.fileSystemService = fileSystemService;
        this.sharedRoleService = sharedRoleService;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter> corsFilterBean() {
        logger.info("CorsFilterBean has been created");
        FilterRegistrationBean<CorsFilter> registrationBean = new FilterRegistrationBean<>();
        CorsFilter corsFilter = new CorsFilter();
        registrationBean.setFilter(corsFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<TokenFilter> tokenFilterBean() {
        logger.info("TokenFilterBean has been created");
        FilterRegistrationBean<TokenFilter> registrationBean = new FilterRegistrationBean<>();
        TokenFilter customURLFilter = new TokenFilter(authService);
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<PermissionFilter> permissionFilterBean() {
        logger.info("PermissionFilterBean has been created");
        FilterRegistrationBean<PermissionFilter> registrationBean = new FilterRegistrationBean<>();
        PermissionFilter customURLFilter = new PermissionFilter(fileSystemService, sharedRoleService);
        registrationBean.setFilter(customURLFilter);
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);
        return registrationBean;
    }
}
