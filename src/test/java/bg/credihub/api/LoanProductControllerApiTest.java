package bg.credihub.api;

import bg.credihub.model.dtos.product.LoanProductDTO;
import bg.credihub.service.LoanProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@WithMockUser(roles = "ADMIN")
public class LoanProductControllerApiTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanProductService loanProductService;

    @Test
    void shouldReturnProductsPage() throws Exception {
        when(loanProductService.getAllView()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-products"));

        verify(loanProductService).getAllView();
    }

    @Test
    void shouldReturnEditPage() throws Exception {
        UUID id = UUID.randomUUID();

        when(loanProductService.getEditDto(id)).thenReturn(createLoanProductDTO());

        mockMvc.perform(get("/admin/products/{id}/edit", id))
                .andExpect(status().isOk())
                .andExpect(view().name("product-edit"))
                .andExpect(model().attributeExists("loanProductDTO"))
                .andExpect(model().attributeExists("productId"));

        verify(loanProductService).getEditDto(id);
    }

    @Test
    void shouldUpdateLoanProductSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();

        mockMvc.perform(put("/admin/products/{id}", id)
                        .with(csrf())
                        .flashAttr("loanProductDTO", createLoanProductDTO()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/products"));

        verify(loanProductService).update(eq(id), any(LoanProductDTO.class));
    }

    private LoanProductDTO createLoanProductDTO() {
        LoanProductDTO dto = new LoanProductDTO();

        dto.setName("Personal Loan");
        dto.setDescription("Description");
        dto.setMinAmount(BigDecimal.valueOf(1000));
        dto.setMaxAmount(BigDecimal.valueOf(20000));
        dto.setMinPeriodMonths(12);
        dto.setMaxPeriodMonths(60);
        dto.setBaseInterestRate(BigDecimal.valueOf(7.5));
        dto.setMonthlyInterestIncrease(BigDecimal.valueOf(0.1));
        dto.setActive(true);

        return dto;
    }
}
