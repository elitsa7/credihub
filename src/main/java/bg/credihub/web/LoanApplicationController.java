package bg.credihub.web;

import bg.credihub.model.dtos.application.LoanApplicationDTO;
import bg.credihub.security.CustomUserDetails;
import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/applications")
public class LoanApplicationController {
    private final LoanApplicationService loanApplicationService;
    private final LoanProductService loanProductService;

    public LoanApplicationController(LoanApplicationService loanApplicationService, LoanProductService loanProductService) {
        this.loanApplicationService = loanApplicationService;
        this.loanProductService = loanProductService;
    }

    @GetMapping("/create")
    public ModelAndView getCreateApplicationPage() {
        ModelAndView mav = new ModelAndView("application-create");
        mav.addObject("loanApplicationDTO", new LoanApplicationDTO());
        mav.addObject("loanProducts", loanProductService.getAllView());
        return mav;
    }

    @PostMapping("/create")
    public ModelAndView createApplication(@Valid @ModelAttribute LoanApplicationDTO loanApplicationDTO,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal CustomUserDetails currentUser) {
        ModelAndView mav = new ModelAndView("application-create");

        if (bindingResult.hasErrors()) {
            mav.addObject("loanProducts", loanProductService.getAllView());
            return mav;
        }
        try {
            loanApplicationService.createLoanApplication(currentUser.getId(), loanApplicationDTO);
            return new ModelAndView("redirect:/applications");
        } catch (RuntimeException e) {
            mav.addObject("applicationError", e.getMessage());
            mav.addObject("loanProducts", loanProductService.getAllView());
            return mav;
        }
    }

    @GetMapping
    public ModelAndView getMyApplicationPage(@AuthenticationPrincipal CustomUserDetails currentUser) {
        ModelAndView mav = new ModelAndView("applications");
        mav.addObject("applications", loanApplicationService.getAllByUser(currentUser.getId()));
        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView getApplicationDetails(@PathVariable UUID id,
                                              @AuthenticationPrincipal CustomUserDetails currentUser) {
        ModelAndView mav = new ModelAndView("application-details");
        mav.addObject("loanApplication", loanApplicationService.getApplicationDetails(id, currentUser.getId()));
        return mav;
    }

    @PostMapping("{id}/cancel")
    public ModelAndView cancelApplication(@PathVariable UUID id,
                                          @AuthenticationPrincipal CustomUserDetails currentUser) {
        loanApplicationService.cancelLoanApplication(id, currentUser.getId());
        return new ModelAndView("redirect:/applications");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView getEditApplicationPage(@PathVariable UUID id,
                                               @AuthenticationPrincipal CustomUserDetails currentUser) {
        ModelAndView mav = new ModelAndView("application-edit");

        mav.addObject("loanApplicationDTO", loanApplicationService.getApplicationForEdit(id, currentUser.getId()));
        mav.addObject("applicationId", id);
        mav.addObject("loanProducts", loanProductService.getAllView());

        return mav;
    }

    @PutMapping("/{id}")
    public ModelAndView updateApplication(@PathVariable UUID id,
                                          @Valid @ModelAttribute LoanApplicationDTO loanApplicationDTO,
                                          BindingResult bindingResult,
                                          @AuthenticationPrincipal CustomUserDetails currentUser) {

        ModelAndView mav = new ModelAndView("application-edit");

        if (bindingResult.hasErrors()) {
            mav.addObject("applicationId", id);
            mav.addObject("loanProducts", loanProductService.getAllView());
            return mav;
        }

        try {
            loanApplicationService.updateLoanApplication(id, currentUser.getId(), loanApplicationDTO);
            return new ModelAndView("redirect:/applications/" + id);

        } catch (RuntimeException e) {
            mav.addObject("applicationError", e.getMessage());
            mav.addObject("applicationId", id);
            mav.addObject("loanProducts", loanProductService.getAllView());
            return mav;
        }
    }
}
