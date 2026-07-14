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
public class InstallmentResponse {
    private UUID id;
    private Integer installmentNumber;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String status;
    private boolean payable;
}
