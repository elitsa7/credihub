package bg.credihub.model.dtos.application;

import bg.credihub.model.enums.ApplicationStatus;
import bg.credihub.model.enums.LoanPurpose;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanApplicationViewDTO {
    private UUID id;
    private String loanProductName;
    private BigDecimal requestedAmount;
    private Integer periodMonths;
    private BigDecimal monthlyIncome;
    private LoanPurpose loanPurpose;
    private ApplicationStatus status;
    private BigDecimal interestRate;
    private BigDecimal monthlyPayment;
    private BigDecimal totalRepaymentAmount;
    private LocalDateTime createdAt;
}
