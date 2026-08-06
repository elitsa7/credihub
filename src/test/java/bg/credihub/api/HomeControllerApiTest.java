package bg.credihub.api;

import bg.credihub.exception.InvalidLoanApplicationException;
import bg.credihub.model.dtos.calculator.LoanCalculatorDTO;
import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.LoanProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class HomeControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoanApplicationService loanApplicationService;

    @MockitoBean
    private LoanProductService loanProductService;

    @Test
    void shouldReturnHomePage() throws Exception {
        when(loanProductService.getAllView()).thenReturn(List.of());

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("loanCalculatorDTO"))
                .andExpect(model().attributeExists("loanProducts"));

        verify(loanProductService).getAllView();
    }

    @Test
    void shouldCalculateLoanSuccessfully() throws Exception {
        LoanCalculatorDTO calculated = createCalculatedLoan();

        when(loanApplicationService.calculate(any())).thenReturn(calculated);
        when(loanProductService.getAllView()).thenReturn(List.of());

        mockMvc.perform(post("/calculate")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("loanCalculatorDTO", createLoanCalculatorDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("result"))
                .andExpect(model().attributeExists("loanProducts"));

        verify(loanApplicationService).calculate(any());
    }

    @Test
    void shouldReturnHomePageWhenValidationFails() throws Exception {
        LoanCalculatorDTO loanCalculatorDTO = new LoanCalculatorDTO();

        when(loanProductService.getAllView()).thenReturn(List.of());

        mockMvc.perform(post("/calculate")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("loanCalculatorDTO", loanCalculatorDTO))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("loanCalculatorDTO"))
                .andExpect(model().attributeExists("loanProducts"));
    }

    @Test
    void shouldReturnCalculationError() throws Exception {
        when(loanApplicationService.calculate(any())).thenThrow(new InvalidLoanApplicationException("Calculation error"));
        when(loanProductService.getAllView()).thenReturn(List.of());

        mockMvc.perform(post("/calculate")
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .flashAttr("loanCalculatorDTO", createLoanCalculatorDTO()))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("calculateError"))
                .andExpect(model().attributeExists("loanProducts"));

        verify(loanApplicationService).calculate(any());
    }

    private LoanCalculatorDTO createLoanCalculatorDTO() {
        LoanCalculatorDTO dto = new LoanCalculatorDTO();
        dto.setLoanProductId(UUID.randomUUID());
        dto.setRequestedAmount(BigDecimal.valueOf(10000));
        dto.setPeriodMonths(24);
        return dto;
    }

    private LoanCalculatorDTO createCalculatedLoan() {
        LoanCalculatorDTO dto = createLoanCalculatorDTO();
        dto.setInterestRate(BigDecimal.valueOf(7.50));
        dto.setMonthlyPayment(BigDecimal.valueOf(449.55));
        dto.setTotalRepaymentAmount(BigDecimal.valueOf(10789.20));
        return dto;
    }
}