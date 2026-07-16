package bg.credihub.web.client;

import bg.credihub.service.PaymentService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;


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
}
