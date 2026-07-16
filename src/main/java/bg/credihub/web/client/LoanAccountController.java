package bg.credihub.web.client;

import bg.credihub.security.CustomUserDetails;
import bg.credihub.service.PaymentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/loans")
public class LoanAccountController {
    private final PaymentService paymentService;

    public LoanAccountController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ModelAndView getLoans(@AuthenticationPrincipal CustomUserDetails currentUser) {
        ModelAndView mav = new ModelAndView("loans");
        mav.addObject("loans", paymentService.getUserLoans(currentUser.getId()));
        return mav;
    }

    @GetMapping("/{loanAccountId}/installments")
    public ModelAndView getInstallments(@PathVariable UUID loanAccountId) {
        ModelAndView mav = new ModelAndView("repayment-schedule");
        mav.addObject("installments", paymentService.getLoanInstallments(loanAccountId));
        return mav;
    }
}
