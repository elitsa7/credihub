package bg.credihub.web;

import bg.credihub.model.dtos.payment.CheckoutSessionResponse;
import bg.credihub.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/success")
    public String success(@RequestParam("session_id") String sessionId) {
        return "redirect:/loans";
    }

    @GetMapping("/cancel")
    public String cancel() {
        return "redirect:/loans";
    }

    @PostMapping("/installments/{installmentId}/pay")
    public String pay(@PathVariable UUID installmentId) throws Exception {
        CheckoutSessionResponse response = paymentService.createCheckoutSession(installmentId);

        return "redirect:" + response.getCheckoutUrl();
    }
}
