package bg.credihub.api;

import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanApplicationService loanApplicationService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnReviewPage() throws Exception {
        when(loanApplicationService.getAllForAdmin()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/review"))
                .andExpect(status().isOk())
                .andExpect(view().name("review-applications"))
                .andExpect(model().attributeExists("applications"));

        verify(loanApplicationService).getAllForAdmin();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldApproveApplicationSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/admin/applications/{id}/approve", id)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/review"));

        verify(loanApplicationService).approve(id);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRejectApplicationSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/admin/applications/{id}/reject", id)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/review"));

        verify(loanApplicationService).reject(id);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnUsersPage() throws Exception {
        when(userService.getAllWithoutAdminView()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"))
                .andExpect(model().attributeExists("users"));

        verify(userService).getAllWithoutAdminView();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldMakeModeratorSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/admin/users/{id}/make-moderator", id)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).makeModerator(id);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRemoveModeratorSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(post("/admin/users/{id}/remove-moderator", id)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/users"));

        verify(userService).removeModerator(id);
    }
}