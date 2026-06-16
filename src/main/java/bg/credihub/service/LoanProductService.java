package bg.credihub.service;

import bg.credihub.exception.InvalidLoanProductException;
import bg.credihub.exception.LoanProductAlreadyExists;
import bg.credihub.exception.LoanProductNotFoundException;
import bg.credihub.model.dtos.LoanProductDTO;
import bg.credihub.model.entities.LoanProduct;
import bg.credihub.repository.LoanProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LoanProductService {
    private final LoanProductRepository loanProductRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LoanProductService(LoanProductRepository loanProductRepository, ModelMapper modelMapper) {
        this.loanProductRepository = loanProductRepository;
        this.modelMapper = modelMapper;
    }

    public void createLoanProduct(LoanProductDTO loanProductDTO) {
        validateAmountAndPeriods(loanProductDTO);

        if (loanProductRepository.existsByName(loanProductDTO.getName())) {
            throw new LoanProductAlreadyExists("Loan product with this name already exists.");
        }

        LoanProduct loanProduct = modelMapper.map(loanProductDTO, LoanProduct.class);

        loanProductRepository.save(loanProduct);
    }

    public void updateLoanProduct(UUID id, LoanProductDTO loanProductDTO) {
        validateAmountAndPeriods(loanProductDTO);
        LoanProduct loanProduct = getById(id);
        modelMapper.map(loanProductDTO, loanProduct);
        loanProductRepository.save(loanProduct);
    }

    public void deactivateLoanProduct(UUID id) {
        LoanProduct loanProduct = getById(id);
        if (!loanProduct.isActive()) {
            throw new InvalidLoanProductException("Loan product is already inactive.");
        }
        loanProduct.setActive(false);
        loanProductRepository.save(loanProduct);
    }

    public void activateLoanProduct(UUID id) {
        LoanProduct loanProduct = getById(id);
        loanProduct.setActive(true);
        loanProductRepository.save(loanProduct);
    }

    public List<LoanProduct> getAll() {
        return loanProductRepository.findAll();
    }

    public List<LoanProduct> getAllActive() {
        return loanProductRepository.findAllByActiveTrue();
    }

    public LoanProduct getById(UUID id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new LoanProductNotFoundException("Loan product not found."));
    }

    private void validateAmountAndPeriods(LoanProductDTO loanProductDTO) {
        if (loanProductDTO.getMinAmount().compareTo(loanProductDTO.getMaxAmount()) > 0) {
            throw new InvalidLoanProductException("Minimum amount cannot be greater than maximum amount.");
        }

        if (loanProductDTO.getMinPeriodMonths().compareTo(loanProductDTO.getMaxPeriodMonths()) > 0) {
            throw new InvalidLoanProductException("Minimum period cannot be greater than maximum period.");
        }
    }
}
