package docSharing;

import docSharing.entities.INode;
import docSharing.entities.User;
import docSharing.enums.INodeType;
//import docSharing.filter.TokenFilter;
import docSharing.repository.FileSystemRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class SpringApp {
    public static void main(String[] args) {
        SpringApplication.run(SpringApp.class, args);
    }

    @Bean
    public CommandLineRunner initData(FileSystemRepository fsRepository) {
        return args -> {
            if (!fsRepository.existsById(1L)) {
                fsRepository.save(new INode("root", INodeType.DIR, LocalDateTime.now(), null, null, null));
            }
        };
    }

//    @Bean
//    FilterRegistrationBean<TokenFilter> tokenFilterFilterRegistrationBean() {
//        final FilterRegistrationBean<TokenFilter> filterRegistrationBean = new FilterRegistrationBean<>();
//        filterRegistrationBean.setFilter(new TokenFilter());
//        filterRegistrationBean.addUrlPatterns("/fs/*");
//        filterRegistrationBean.addUrlPatterns("/doc/*");
//        filterRegistrationBean.addUrlPatterns("/user/*");
//
//        return filterRegistrationBean;
//    }
}