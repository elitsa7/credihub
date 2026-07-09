package bg.credihub.web;

import bg.credihub.model.dtos.user.UserProfileDTO;
import bg.credihub.security.CustomUserDetails;
import bg.credihub.service.UserService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ModelAndView getProfilePage(@AuthenticationPrincipal CustomUserDetails currentUser) {
        ModelAndView mav = new ModelAndView("profile");
        mav.addObject("profile", userService.getProfile(currentUser.getId()));
        return mav;
    }

    @GetMapping("/edit")
    public ModelAndView editProfilePage(@AuthenticationPrincipal CustomUserDetails currentUser) {
        ModelAndView mav = new ModelAndView("profile-edit");
        mav.addObject("userProfileDTO", userService.getProfile(currentUser.getId()));
        return mav;
    }

    @PutMapping
    public ModelAndView updateProfile(@AuthenticationPrincipal CustomUserDetails currentUser,
                                      @Valid @ModelAttribute UserProfileDTO userProfileDTO,
                                      BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("profile-edit");

        if (bindingResult.hasErrors()) {
            return mav;
        }

        try {
            userService.updateProfile(currentUser.getId(), userProfileDTO);
            return new ModelAndView("redirect:/profile");
        } catch (Exception e) {
            mav.addObject("userProfileDTO", userProfileDTO);
            mav.addObject("profileError", e.getMessage());
            return mav;
        }
    }
}
