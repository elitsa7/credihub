package bg.credihub.service;

import bg.credihub.exception.LoanProductNotFoundException;
import bg.credihub.mapper.LoanProductMapper;
import bg.credihub.model.dtos.product.LoanProductDTO;
import bg.credihub.model.dtos.product.LoanProductViewDTO;
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
    private final LoanProductMapper loanProductMapper;
    private final ModelMapper modelMapper;

    @Autowired
    public LoanProductService(LoanProductRepository loanProductRepository, LoanProductMapper loanProductMapper, ModelMapper modelMapper) {
        this.loanProductRepository = loanProductRepository;
        this.loanProductMapper = loanProductMapper;
        this.modelMapper = modelMapper;
    }

    public void update(UUID id, LoanProductDTO loanProductDTO) {
        LoanProduct loanProduct = getById(id);

        modelMapper.map(loanProductDTO, loanProduct);

        loanProductRepository.save(loanProduct);
    }

    public List<LoanProduct> getAll() {
        return loanProductRepository.findAll();
    }

    public List<LoanProductViewDTO> getAllView() {

        return loanProductRepository.findAll()
                .stream()
                .map(loanProductMapper::toViewDto)
                .toList();
    }

    public LoanProductDTO getEditDto(UUID id) {
        return loanProductMapper.toEditDto(getById(id));
    }

    public LoanProduct getById(UUID id) {
        return loanProductRepository.findById(id)
                .orElseThrow(() -> new LoanProductNotFoundException("Loan product not found."));
    }
}
