package bg.credihub.model.dtos.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanProductDTO {
    @NotBlank(message = "Product name is required.")
    private String name;
    @NotBlank(message = "Description is required.")
    private String description;
    @NotNull(message = "Minimum amount is required.")
    @Positive(message = "Minimum amount must be greater than 0.")
    private BigDecimal minAmount;
    @NotNull(message = "Maximum amount is required.")
    @Positive(message = "Maximum amount must be greater than 0.")
    private BigDecimal maxAmount;
    @NotNull(message = "Minimum period is required.")
    @Positive(message = "Minimum period must be greater than 0.")
    private Integer minPeriodMonths;
    @NotNull(message = "Maximum period is required.")
    @Positive(message = "Maximum period must be greater than 0.")
    private Integer maxPeriodMonths;
    @NotNull(message = "Base interest rate is required.")
    @Positive(message = "Base interest must be greater than 0.")
    private BigDecimal baseInterestRate;
    @NotNull(message = "Monthly interest increase is required.")
    @Positive(message = "Monthly interest increase must be greater than 0.")
    private BigDecimal monthlyInterestIncrease;
    private boolean active = true;
}
