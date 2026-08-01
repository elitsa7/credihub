package bg.credihub.service;

import bg.credihub.client.PaymentClient;
import bg.credihub.model.dtos.payment.CheckoutSessionResponse;
import bg.credihub.model.dtos.payment.InstallmentResponse;
import bg.credihub.model.dtos.payment.LoanAccountResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PaymentService {
    private final PaymentClient paymentClient;

    public PaymentService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public List<InstallmentResponse> getLoanInstallments(UUID loanAccountId) {
        return paymentClient.getLoanInstallments(loanAccountId);
    }

    public List<LoanAccountResponse> getUserLoans(UUID userId) {
        return paymentClient.getUserLoans(userId);
    }

    public CheckoutSessionResponse createCheckoutSession(UUID installmentId) {
        CheckoutSessionResponse response = paymentClient.createCheckoutSession(installmentId);
        log.info("Created checkout session for installment {}", installmentId);
        return response;
    }
}
