package bg.credihub.model.dtos.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanProductViewDTO {
    private UUID id;
    private String name;
    private BigDecimal minAmount;
    private BigDecimal maxAmount;
    private Integer minPeriodMonths;
    private Integer maxPeriodMonths;
    private boolean active;
}
