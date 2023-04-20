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

}