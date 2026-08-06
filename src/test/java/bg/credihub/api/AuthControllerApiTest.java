package bg.credihub.api;

import bg.credihub.model.entities.User;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class AuthControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldRegisterUserSuccessfully() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "Ivan")
                        .param("lastName", "Ivanov")
                        .param("phoneNumber", "0888123456")
                        .param("identificationNumber", "1234567890")
                        .param("email", "ivan@test.com")
                        .param("password", "Password123!")
                        .param("confirmPassword", "Password123!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        User user = userRepository.findByEmail("ivan@test.com").orElseThrow();

        assertEquals("ivan@test.com", user.getEmail());
        assertEquals(Role.USER, user.getRole());
    }

    @Test
    void shouldReturnRegisterPageWhenValidationFails() throws Exception {
        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "")
                        .param("lastName", "")
                        .param("phoneNumber", "")
                        .param("identificationNumber", "")
                        .param("email", "")
                        .param("password", "")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("userRegisterDTO"));
    }

    @Test
    void shouldReturnRegisterErrorWhenEmailAlreadyExists() throws Exception {
        userRepository.save(createUser());

        mockMvc.perform(post("/register")
                        .with(csrf())
                        .param("firstName", "Ivan")
                        .param("lastName", "Ivanov")
                        .param("phoneNumber", "0888999999")
                        .param("identificationNumber", "9999999999")
                        .param("email", "ivan@test.com")
                        .param("password", "Password123!")
                        .param("confirmPassword", "Password123!"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("registerError"));
    }

    private User createUser() {
        User user = new User();

        user.setFirstName("Ivan");
        user.setLastName("Ivanov");
        user.setPhoneNumber("0888123456");
        user.setIdentificationNumber("1234567890");
        user.setEmail("ivan@test.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        return user;
    }
}