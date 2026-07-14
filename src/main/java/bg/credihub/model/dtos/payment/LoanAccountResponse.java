package bg.credihub.model.dtos.payment;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanAccountResponse {
    private UUID id;
    private BigDecimal principalAmount;
    private BigDecimal remainingBalance;
    private BigDecimal monthlyPayment;
    private Integer paidInstallments;
    private Integer periodMonths;
    private String status;
    private LocalDate startDate;
    private LocalDate endDate;
}
