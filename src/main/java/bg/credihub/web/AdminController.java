package bg.credihub.web;

import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.UserService;
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
    private final UserService userService;

    public AdminController(LoanApplicationService loanApplicationService, UserService userService) {
        this.loanApplicationService = loanApplicationService;
        this.userService = userService;
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        ModelAndView mav = new ModelAndView("admin-dashboard");
        mav.addObject("applications", loanApplicationService.getAllForAdmin());
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

    @GetMapping("/users")
    public ModelAndView users() {
        ModelAndView mav = new ModelAndView("admin-users");
        mav.addObject("users", userService.getAllWithoutAdminView());
        return mav;
    }

    @PostMapping("/users/{id}/make-moderator")
    public ModelAndView makeModerator(@PathVariable UUID id) {
        userService.makeModerator(id);
        return new ModelAndView("redirect:/admin/users");
    }

    @PostMapping("/users/{id}/remove-moderator")
    public ModelAndView removeModerator(@PathVariable UUID id) {
        userService.removeModerator(id);
        return new ModelAndView("redirect:/admin/users");
    }
}
