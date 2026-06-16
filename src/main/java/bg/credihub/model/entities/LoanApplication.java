package bg.credihub.model.entities;

import bg.credihub.model.enums.ApplicationStatus;
import bg.credihub.model.enums.LoanPurpose;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_applications")
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @ManyToOne
    @JoinColumn(name = "loan_product_id", nullable = false)
    private LoanProduct loanProduct;
    @Column(nullable = false)
    private BigDecimal requestedAmount;
    @Column(nullable = false)
    private Integer periodMonths;
    @Column(nullable = false)
    private Integer monthlyIncome;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanPurpose loanPurpose;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;
    @Column(nullable = false)
    private BigDecimal interestRate;
    @Column(nullable = false)
    private BigDecimal monthlyPayment;
    @Column(nullable = false)
    private BigDecimal totalRepaymentAmount;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
