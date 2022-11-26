package docSharing.filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfigurerAdapter {

    @Autowired
    AuthenticationFilter authenticationFilter;
    @Autowired
    PermissionFilter permissionFilter;


    @Bean
    public DefaultSecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.addFilterAfter(authenticationFilter, BasicAuthenticationFilter.class);
        http.addFilterAfter(permissionFilter, BasicAuthenticationFilter.class);

        return http.build();
    }
}
