package bg.credihub.config;

import bg.credihub.model.entities.User;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.UserRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AdminInitConfig {
    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public CommandLineRunner adminInit(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if(userRepository.findByEmail("admin@credihub.com").isEmpty()) {
                User admin = new User();

                admin.setFirstName("Admin");
                admin.setLastName("Admin");
                admin.setEmail("admin@credihub.com");
                admin.setPhoneNumber("0888888888");
                admin.setIdentificationNumber("0000000000");
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);
            }
        };
    }
}
