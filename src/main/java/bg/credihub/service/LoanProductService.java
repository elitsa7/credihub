package bg.credihub.service;

import bg.credihub.exception.LoanProductNotFoundException;
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

    public List<LoanProduct> getAll() {
        return loanProductRepository.findAll();
    }

    public LoanProduct getById(UUID id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new LoanProductNotFoundException("Loan product not found."));
    }
}
