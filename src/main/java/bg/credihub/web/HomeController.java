package bg.credihub.web;

import bg.credihub.model.dtos.calculator.LoanCalculatorDTO;
import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
    private final LoanApplicationService loanApplicationService;
    private final LoanProductService loanProductService;

    public HomeController(LoanApplicationService loanApplicationService, LoanProductService loanProductService) {
        this.loanApplicationService = loanApplicationService;
        this.loanProductService = loanProductService;
    }

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView mav = new ModelAndView("home");
        mav.addObject("loanCalculatorDTO", new LoanCalculatorDTO());
        mav.addObject("loanProducts", loanProductService.getAllView());
        return mav;
    }

    @PostMapping("/calculate")
    public ModelAndView calculate(@Valid @ModelAttribute LoanCalculatorDTO loanCalculatorDTO,
                                  BindingResult  bindingResult) {
        ModelAndView mav = new ModelAndView("home");

        if (bindingResult.hasErrors()) {
            mav.addObject("loanCalculatorDTO", loanCalculatorDTO);
            mav.addObject("loanProducts", loanProductService.getAllView());
            return mav;
        }

        try {
            mav.addObject("result", loanApplicationService.calculate(loanCalculatorDTO));

        } catch (RuntimeException e) {
            mav.addObject("calculateError", e.getMessage());
        }

        mav.addObject("loanCalculatorDTO", loanCalculatorDTO);
        mav.addObject("loanProducts", loanProductService.getAllView());

        return mav;
    }
}
