package bg.credihub.model.dtos.payment;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;
@Getter
@Builder
public class LoanAccountResponse {
    private UUID loanAccountId;
}
