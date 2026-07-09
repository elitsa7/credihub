package bg.credihub.model.dtos.application;

import bg.credihub.model.enums.LoanPurpose;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive(message = "Amount must be greater than 0.")
    private BigDecimal requestedAmount;
    @NotNull(message = "Period is required.")
    @Positive(message = "Period must be greater than 0.")
    private Integer periodMonths;
    @NotNull(message = "Monthly income is required.")
    @Positive(message = "Monthly income must be greater than 0.")
    private BigDecimal monthlyIncome;
    @NotNull(message = "Purpose is required.")
    private LoanPurpose purpose;
}
