package bg.credihub.web;

import bg.credihub.model.dtos.LoanApplicationDTO;
import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.LoanProductService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public ModelAndView getCreateApplicationPage(){
        ModelAndView mav = new ModelAndView("application-create");
        mav.addObject("loanApplicationDTO", new LoanApplicationDTO());
        mav.addObject("loanProducts", loanProductService.getAll());
        return mav;
    }

    @PostMapping("/create")
    public ModelAndView createApplication(@Valid @ModelAttribute LoanApplicationDTO loanApplicationDTO,
                                          BindingResult bindingResult,
                                          HttpSession session){
        if(bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("application-create");
            mav.addObject("loanProducts", loanProductService.getAll());
            return mav;
        }

        UUID userId = (UUID)session.getAttribute("user_id");
        loanApplicationService.createLoanApplication(userId, loanApplicationDTO);

        return new ModelAndView("redirect:/applications");
    }
}
