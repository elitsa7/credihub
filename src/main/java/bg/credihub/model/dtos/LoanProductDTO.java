package bg.credihub.model.dtos;

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
    @DecimalMin(value = "100.00", message = "Minimum amount must be at least 100.")
    private BigDecimal minAmount;
    @NotNull(message = "Maximum amount is required.")
    @DecimalMin(value = "100.00", message = "Maximum amount must be at least 100.")
    private BigDecimal maxAmount;
    @NotNull(message = "Minimum period is required.")
    @Min(value = 1, message = "Minimum period must be at least 1 month.")
    @Max(value = 120, message = "Minimum period cannot exceed 120 months.")
    private Integer minPeriodMonths;
    @NotNull(message = "Maximum period is required.")
    @Min(value = 1, message = "Maximum period must be at least 1 month.")
    @Max(value = 120, message = "Maximum period cannot exceed 120 months.")
    private Integer maxPeriodMonths;
    @NotNull(message = "Base interest rate is required.")
    @DecimalMin(value = "0.01", message = "Base interest rate must be positive.")
    @DecimalMax(value = "100.00", message = "Base interest rate cannot exceed 100%.")
    private BigDecimal baseInterestRate;
    @NotNull(message = "Monthly interest increase is required.")
    @DecimalMin(value = "0.00", message = "Monthly interest increase cannot be negative.")
    @DecimalMax(value = "10.00", message = "Monthly interest increase is too high.")
    private BigDecimal monthlyInterestIncrease;
    private boolean active = true;
}
