package bg.credihub.client;

import bg.credihub.model.dtos.payment.InstallmentResponse;
import bg.credihub.model.dtos.payment.LoanAccountRequest;
import bg.credihub.model.dtos.payment.LoanAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "payment-service",
        url = "${payment.service.url}")
public interface PaymentClient {

    @PostMapping("/api/v1/loan-accounts")
    LoanAccountResponse createLoanAccount(@RequestBody LoanAccountRequest loanAccountRequest);

    @GetMapping("/api/v1/installments/loan/{loanAccountId}")
    List<InstallmentResponse> getLoanInstallments(@PathVariable UUID loanAccountId);

    @GetMapping("/api/v1/loan-accounts/user/{userId}")
    List<LoanAccountResponse> getUserLoans(@PathVariable UUID userId);

}
