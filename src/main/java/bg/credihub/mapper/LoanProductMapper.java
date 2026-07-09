package bg.credihub.mapper;

import bg.credihub.model.dtos.product.LoanProductDTO;
import bg.credihub.model.dtos.product.LoanProductViewDTO;
import bg.credihub.model.entities.LoanProduct;
import org.springframework.stereotype.Component;

@Component
public class LoanProductMapper {

    public LoanProductDTO toEditDto(LoanProduct product) {
        LoanProductDTO dto = new LoanProductDTO();
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setMinAmount(product.getMinAmount());
        dto.setMaxAmount(product.getMaxAmount());
        dto.setMinPeriodMonths(product.getMinPeriodMonths());
        dto.setMaxPeriodMonths(product.getMaxPeriodMonths());
        dto.setBaseInterestRate(product.getBaseInterestRate());
        dto.setMonthlyInterestIncrease(product.getMonthlyInterestIncrease());
        dto.setActive(product.isActive());
        return dto;
    }

    public LoanProductViewDTO toViewDto(LoanProduct product) {
        return new LoanProductViewDTO(
                product.getId(),
                product.getName(),
                product.getMinAmount(),
                product.getMaxAmount(),
                product.getMinPeriodMonths(),
                product.getMaxPeriodMonths(),
                product.isActive()
        );
    }
}
