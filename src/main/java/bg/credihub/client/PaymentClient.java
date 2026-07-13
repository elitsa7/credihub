package bg.credihub.client;

import bg.credihub.model.dtos.payment.LoanAccountRequest;
import bg.credihub.model.dtos.payment.LoanAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "payment-service",
        url = "${payment.service.url}")
public interface PaymentClient {

    @PostMapping("/api/v1/loan-accounts")
    LoanAccountResponse createLoanAccount(@RequestBody LoanAccountRequest loanAccountRequest);

}
