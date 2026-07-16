package bg.credihub.web.client;

import bg.credihub.model.dtos.payment.CheckoutSessionResponse;
import bg.credihub.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Controller
@RequestMapping("installments")
public class InstallmentController {
    private final PaymentService paymentService;

    public InstallmentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/{installmentId}/pay")
    public String pay(@PathVariable UUID installmentId) throws Exception {
        CheckoutSessionResponse response = paymentService.createCheckoutSession(installmentId);
        return "redirect:" + response.getCheckoutUrl();
    }
}
