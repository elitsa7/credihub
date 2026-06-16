package bg.credihub.model.dtos;

import bg.credihub.model.entities.LoanDecision;
import bg.credihub.model.enums.LoanPurpose;
import jakarta.persistence.OneToOne;
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
public class LoanApplicationDTO {
    @NotNull(message = "Loan product is required.")
    private UUID loanProductId;
    @NotNull(message = "Requested amount is required.")
    @DecimalMin(value = "100.00", message = "Requested amount must be at least 100.")
    private BigDecimal requestedAmount;
    @NotNull(message = "Period is required.")
    @Min(value = 1, message = "Period must be at least 1 month.")
    @Max(value = 120, message = "Period cannot exceed 120 months.")
    private Integer periodMonths;
    @NotNull(message = "Monthly income is required.")
    @DecimalMin(value = "1.00", message = "Monthly income must be positive.")
    private BigDecimal monthlyIncome;
    @NotNull(message = "Purpose is required.")
    private LoanPurpose purpose;
    @OneToOne
    private LoanDecision loanDecision;
}
