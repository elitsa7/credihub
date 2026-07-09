package bg.credihub.web;

import bg.credihub.model.dtos.product.LoanProductDTO;
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
        mav.addObject("products", loanProductService.getAllView());
        return mav;
    }

    @GetMapping("/{id}/edit")
    public ModelAndView edit(@PathVariable UUID id) {
        ModelAndView mav = new ModelAndView("product-edit");

        mav.addObject("loanProductDTO", loanProductService.getEditDto(id));
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
