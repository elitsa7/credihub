package bg.credihub.web;

import bg.credihub.model.dtos.user.UserLoginDTO;
import bg.credihub.model.dtos.user.UserRegisterDTO;
import bg.credihub.model.entities.User;
import bg.credihub.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public ModelAndView getLoginPage() {
        ModelAndView mav = new ModelAndView("login");
        mav.addObject("userLoginDTO", new UserLoginDTO());
        return mav;
    }

    @PostMapping("/login")
    public ModelAndView login(@Valid @ModelAttribute UserLoginDTO userLoginDTO,
                              BindingResult bindingResult,
                              HttpSession session) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("userLoginDTO", userLoginDTO);
            return mav;
        }

        try {
            User user = userService.login(userLoginDTO);
            session.setAttribute("user_id", user.getId());
            return new ModelAndView("redirect:/");

        } catch (RuntimeException e) {
            ModelAndView mav = new ModelAndView("login");
            mav.addObject("userLoginDTO", userLoginDTO);
            mav.addObject("loginError", "Invalid username or password");
            return mav;
        }
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
            return new ModelAndView("redirect:/auth/login");

        } catch (RuntimeException e) {
            ModelAndView mav = new ModelAndView("register");
            mav.addObject("userRegisterDTO", userRegisterDTO);
            mav.addObject("registerError", e.getMessage());
            return mav;
        }
    }

    @GetMapping("/logout")
    public ModelAndView getLogoutPage(HttpSession session) {
        session.invalidate();
        return new ModelAndView("redirect:/");
    }
}
