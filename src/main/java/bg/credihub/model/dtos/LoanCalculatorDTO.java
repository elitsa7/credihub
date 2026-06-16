package bg.credihub.model.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class LoanCalculatorDTO {
    @NotNull(message = "Loan product is required.")
    private UUID loanProductId;
    @NotNull(message = "Requested amount is required.")
    @DecimalMin(value = "100.00")
    private BigDecimal requestedAmount;
    @NotNull(message = "Period is required.")
    @Min(1)
    @Max(120)
    private Integer periodMonths;
}
