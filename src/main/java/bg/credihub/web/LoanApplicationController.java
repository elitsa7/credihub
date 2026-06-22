package bg.credihub.web;

import bg.credihub.model.dtos.LoanApplicationDTO;
import bg.credihub.model.entities.LoanApplication;
import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.LoanProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
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
        mav.addObject("loanProducts", loanProductService.getAll());
        return mav;
    }

    @PostMapping("/create")
    public ModelAndView createApplication(@Valid @ModelAttribute LoanApplicationDTO loanApplicationDTO,
                                          BindingResult bindingResult,
                                          HttpSession session) {
        ModelAndView mav = new ModelAndView("application-create");

        if (bindingResult.hasErrors()) {
            mav.addObject("loanProducts", loanProductService.getAll());
            return mav;
        }
        try {
            UUID userId = (UUID) session.getAttribute("user_id");
            loanApplicationService.createLoanApplication(userId, loanApplicationDTO);
            return new ModelAndView("redirect:/applications");
        } catch (RuntimeException e) {
            mav.addObject("applicationError", e.getMessage());
            mav.addObject("loanProducts", loanProductService.getAll());
            return mav;
        }
    }

    @GetMapping
    public ModelAndView getMyApplicationPage(HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        ModelAndView mav = new ModelAndView("applications");
        mav.addObject("applications", loanApplicationService.getAllByUser(userId));
        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView getApplicationDetails(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        ModelAndView mav = new ModelAndView("application-details");
        mav.addObject("loanApplication", loanApplicationService.getApplicationDetails(id, userId));
        return mav;
    }

    @PostMapping("{id}/cancel")
    public ModelAndView cancelApplication(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");
        loanApplicationService.cancelLoanApplication(id, userId);
        return new ModelAndView("redirect:/applications");
    }

    @GetMapping("/{id}/edit")
    public ModelAndView getEditApplicationPage(@PathVariable UUID id, HttpSession session) {
        UUID userId = (UUID) session.getAttribute("user_id");

        LoanApplication application = loanApplicationService.getApplicationDetails(id, userId);

        LoanApplicationDTO dto = new LoanApplicationDTO();

        dto.setLoanProductId(application.getLoanProduct().getId());
        dto.setRequestedAmount(application.getRequestedAmount());
        dto.setPeriodMonths(application.getPeriodMonths());
        dto.setMonthlyIncome(application.getMonthlyIncome());
        dto.setPurpose(application.getLoanPurpose());

        ModelAndView mav = new ModelAndView("application-edit");

        mav.addObject("loanApplicationDTO", dto);
        mav.addObject("applicationId", id);
        mav.addObject("loanProducts", loanProductService.getAll());

        return mav;
    }

    @PutMapping("/{id}")
    public ModelAndView updateApplication(@PathVariable UUID id,
                                          @Valid @ModelAttribute LoanApplicationDTO loanApplicationDTO,
                                          BindingResult bindingResult, HttpSession session) {

        ModelAndView mav = new ModelAndView("application-edit");

        if (bindingResult.hasErrors()) {
            mav.addObject("applicationId", id);
            mav.addObject("loanProducts", loanProductService.getAll());
            return mav;
        }

        try {
            UUID userId = (UUID) session.getAttribute("user_id");
            loanApplicationService.updateLoanApplication(id, userId, loanApplicationDTO);
            return new ModelAndView("redirect:/applications/" + id);

        } catch (RuntimeException e) {
            mav.addObject("applicationError", e.getMessage());
            mav.addObject("applicationId", id);
            mav.addObject("loanProducts", loanProductService.getAll());
            return mav;
        }
    }
}
