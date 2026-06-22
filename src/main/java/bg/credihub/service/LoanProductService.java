package bg.credihub.service;

import bg.credihub.exception.LoanProductNotFoundException;
import bg.credihub.model.dtos.LoanProductDTO;
import bg.credihub.model.entities.LoanProduct;
import bg.credihub.repository.LoanProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoanProductService {
    private final LoanProductRepository loanProductRepository;

    @Autowired
    public LoanProductService(LoanProductRepository loanProductRepository) {
        this.loanProductRepository = loanProductRepository;
    }

    public void update(UUID id, LoanProductDTO loanProductDTO) {
        LoanProduct loanProduct = getById(id);

        loanProduct.setName(loanProductDTO.getName());
        loanProduct.setDescription(loanProductDTO.getDescription());
        loanProduct.setMinAmount(loanProductDTO.getMinAmount());
        loanProduct.setMaxAmount(loanProductDTO.getMaxAmount());
        loanProduct.setMinPeriodMonths(loanProductDTO.getMinPeriodMonths());
        loanProduct.setMaxPeriodMonths(loanProductDTO.getMaxPeriodMonths());
        loanProduct.setBaseInterestRate(loanProductDTO.getBaseInterestRate());
        loanProduct.setMonthlyInterestIncrease(loanProductDTO.getMonthlyInterestIncrease());
        loanProduct.setActive(loanProductDTO.isActive());

        loanProductRepository.save(loanProduct);
    }

    public List<LoanProduct> getAll() {
        return loanProductRepository.findAll();
    }

    public LoanProduct getById(UUID id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new LoanProductNotFoundException("Loan product not found."));
    }
}
