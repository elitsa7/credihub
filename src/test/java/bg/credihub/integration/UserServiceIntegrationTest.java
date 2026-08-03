package bg.credihub.integration;

import bg.credihub.model.dtos.user.UserRegisterDTO;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.UserRepository;
import bg.credihub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class UserServiceIntegrationTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully() {
        UserRegisterDTO userRegisterDTO = createUserRegisterDTO();

        userService.register(userRegisterDTO);

        User savedUser = userRepository.findByEmail(userRegisterDTO.getEmail()).orElseThrow();

        assertNotNull(savedUser);
        assertEquals(userRegisterDTO.getEmail(), savedUser.getEmail());
        assertEquals(Role.USER, savedUser.getRole());
        assertNotEquals(userRegisterDTO.getPassword(), savedUser.getPassword());
    }

    private UserRegisterDTO createUserRegisterDTO() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();

        userRegisterDTO.setFirstName("Ivan");
        userRegisterDTO.setLastName("Ivanov");
        userRegisterDTO.setPhoneNumber("0888123456");
        userRegisterDTO.setIdentificationNumber("1234567890");
        userRegisterDTO.setEmail("ivan@test.com");
        userRegisterDTO.setPassword("Password123!");
        userRegisterDTO.setConfirmPassword("Password123!");

        return userRegisterDTO;
    }


}
