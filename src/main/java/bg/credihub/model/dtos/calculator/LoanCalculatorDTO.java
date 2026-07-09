package bg.credihub.model.dtos.calculator;

import jakarta.validation.constraints.*;
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
    @Positive(message = "Amount must be greater than 0.")
    private BigDecimal requestedAmount;
    @NotNull(message = "Period is required.")
    @Positive(message = "Period must be greater than 0.")
    private Integer periodMonths;
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private BigDecimal totalRepaymentAmount;
}
