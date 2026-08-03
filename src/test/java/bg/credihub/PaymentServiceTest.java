package bg.credihub;

import bg.credihub.model.dtos.payment.CheckoutSessionResponse;
import bg.credihub.model.dtos.payment.InstallmentResponse;
import bg.credihub.model.dtos.payment.LoanAccountResponse;
import bg.credihub.service.client.PaymentClient;
import bg.credihub.service.client.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private PaymentClient paymentClient;

    private PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        paymentService = new PaymentService(paymentClient);
    }

    @Test
    void shouldReturnLoanInstallmentsSuccessfully() {
        UUID loanAccountId = UUID.randomUUID();
        List<InstallmentResponse> installments = List.of(createInstallmentResponse());

        when(paymentClient.getLoanInstallments(loanAccountId)).thenReturn(installments);

        List<InstallmentResponse> result = paymentService.getLoanInstallments(loanAccountId);

        assertEquals(1, result.size());
        verify(paymentClient).getLoanInstallments(loanAccountId);
    }

    @Test
    void shouldReturnEmptyLoanInstallments() {
        UUID loanAccountId = UUID.randomUUID();

        when(paymentClient.getLoanInstallments(loanAccountId)).thenReturn(Collections.emptyList());

        List<InstallmentResponse> result = paymentService.getLoanInstallments(loanAccountId);

        assertTrue(result.isEmpty());
        verify(paymentClient).getLoanInstallments(loanAccountId);
    }

    @Test
    void shouldReturnUserLoansSuccessfully() {
        UUID userId = UUID.randomUUID();

        List<LoanAccountResponse> loans = List.of(createLoanAccountResponse());

        when(paymentClient.getUserLoans(userId)).thenReturn(loans);

        List<LoanAccountResponse> result = paymentService.getUserLoans(userId);

        assertEquals(1, result.size());
        verify(paymentClient).getUserLoans(userId);
    }

    @Test
    void shouldReturnEmptyUserLoans() {
        UUID userId = UUID.randomUUID();

        when(paymentClient.getUserLoans(userId)).thenReturn(Collections.emptyList());

        List<LoanAccountResponse> result = paymentService.getUserLoans(userId);

        assertTrue(result.isEmpty());
        verify(paymentClient).getLoanInstallments(userId);
    }

    @Test
    void shouldCreateCheckoutSessionSuccessfully() {
        UUID installmentId = UUID.randomUUID();
        CheckoutSessionResponse checkoutSessionResponse = createCheckoutSessionResponse();

        when(paymentClient.createCheckoutSession(installmentId)).thenReturn(checkoutSessionResponse);

        CheckoutSessionResponse result = paymentService.createCheckoutSession(installmentId);

        assertEquals(checkoutSessionResponse, result);
        verify(paymentClient).createCheckoutSession(installmentId);
    }

    private InstallmentResponse createInstallmentResponse() {
        return new InstallmentResponse();
    }

    private LoanAccountResponse createLoanAccountResponse() {
        return new LoanAccountResponse();
    }

    private CheckoutSessionResponse createCheckoutSessionResponse() {
        return new CheckoutSessionResponse();
    }
}
