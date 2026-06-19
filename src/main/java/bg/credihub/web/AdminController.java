package bg.credihub.web;

import bg.credihub.service.LoanApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final LoanApplicationService loanApplicationService;

    public AdminController(LoanApplicationService loanApplicationService) {
        this.loanApplicationService = loanApplicationService;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        ModelAndView mav = new ModelAndView("admin-dashboard");
        mav.addObject("applications", loanApplicationService.getAll());
        return mav;
    }

    @PostMapping("/applications/{id}/approve")
    public ModelAndView approve(@PathVariable UUID id) {
        loanApplicationService.approve(id);
        return new ModelAndView("redirect:/admin/dashboard");
    }

    @PostMapping("/applications/{id}/reject")
    public ModelAndView reject(@PathVariable UUID id) {
        loanApplicationService.reject(id);
        return new ModelAndView("redirect:/admin/dashboard");
    }

}
