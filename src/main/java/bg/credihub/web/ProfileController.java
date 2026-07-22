package bg.credihub.web;

import bg.credihub.exception.EmailAlreadyExistsException;
import bg.credihub.exception.IdentificationNumberAlreadyExistsException;
import bg.credihub.exception.PhoneNumberAlreadyExistsException;
import bg.credihub.model.dtos.user.UpdateUserProfileRequest;
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
        mav.addObject("updateUserProfileRequest", userService.getProfileForEdit(currentUser.getId()));
        return mav;
    }

    @PutMapping
    public ModelAndView updateProfile(@AuthenticationPrincipal CustomUserDetails currentUser,
            @Valid @ModelAttribute("user") UpdateUserProfileRequest request,
            BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("profile-edit");

        if (bindingResult.hasErrors()) {
            mav.addObject("updateUserProfileRequest", request);
            return mav;
        }

        try {
            userService.updateProfile(currentUser.getId(), request);
            return new ModelAndView("redirect:/profile");
        } catch (PhoneNumberAlreadyExistsException | EmailAlreadyExistsException |
                 IdentificationNumberAlreadyExistsException e) {
            mav.addObject("updateUserProfileRequest", request);
            mav.addObject("profileError", e.getMessage());
            return mav;
        }
    }
}
