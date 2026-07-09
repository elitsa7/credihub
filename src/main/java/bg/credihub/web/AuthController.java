package bg.credihub.web;

import bg.credihub.model.dtos.user.UserRegisterDTO;
import bg.credihub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        return new ModelAndView("login");
    }

    @GetMapping("/register")
    public ModelAndView getRegisterPage() {
        ModelAndView mav = new ModelAndView("register");
        mav.addObject("userRegisterDTO", new UserRegisterDTO());
        return mav;
    }

    @PostMapping("/register")
    public ModelAndView register(@Valid @ModelAttribute UserRegisterDTO userRegisterDTO,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("userRegisterDTO", userRegisterDTO);
            return mav;
        }

        try {
            userService.register(userRegisterDTO);
            return new ModelAndView("redirect:/login");

        } catch (RuntimeException e) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("userRegisterDTO", userRegisterDTO);
            mav.addObject("registerError", e.getMessage());
            return mav;
        }
    }
}
