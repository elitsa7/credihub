package bg.credihub.web;

import bg.credihub.model.dtos.LoanProductDTO;
import bg.credihub.model.entities.LoanProduct;
import bg.credihub.service.LoanProductService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
@RequestMapping("/admin/products")
public class LoanProductController {

    private final LoanProductService loanProductService;

    public LoanProductController(LoanProductService loanProductService) {
        this.loanProductService = loanProductService;
    }

    @GetMapping
    public ModelAndView getProducts() {
        ModelAndView mav = new ModelAndView("admin-products");
        mav.addObject("products", loanProductService.getAll());
        return mav;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable UUID id) {
        LoanProduct product = loanProductService.getById(id);

        LoanProductDTO loanProductDTO = new LoanProductDTO();

        loanProductDTO.setName(product.getName());
        loanProductDTO.setDescription(product.getDescription());
        loanProductDTO.setMinAmount(product.getMinAmount());
        loanProductDTO.setMaxAmount(product.getMaxAmount());
        loanProductDTO.setMinPeriodMonths(product.getMinPeriodMonths());
        loanProductDTO.setMaxPeriodMonths(product.getMaxPeriodMonths());
        loanProductDTO.setBaseInterestRate(product.getBaseInterestRate());
        loanProductDTO.setMonthlyInterestIncrease(product.getMonthlyInterestIncrease());
        loanProductDTO.setActive(product.isActive());

        ModelAndView mav = new ModelAndView("product-edit");

        mav.addObject("loanProductDTO", loanProductDTO);
        mav.addObject("productId", id);

        return mav;
    }

    @PutMapping("/{id}")
    public ModelAndView editLoanProduct(@PathVariable UUID id,
                                        @Valid @ModelAttribute LoanProductDTO loanProductDTO,
                                        BindingResult bindingResult) {
        ModelAndView mav = new ModelAndView("product-edit");

        if (bindingResult.hasErrors()) {
            mav.addObject("productId", id);
            mav.addObject("loanProductDTO", loanProductDTO);
            return mav;
        }

        try {
            loanProductService.update(id, loanProductDTO);
            return new ModelAndView("redirect:/admin/products");

        } catch (RuntimeException e) {
            mav.addObject("productError", e.getMessage());
            mav.addObject("productId", id);
            return mav;
        }
    }
}
