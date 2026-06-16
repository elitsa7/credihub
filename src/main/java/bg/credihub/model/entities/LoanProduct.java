package bg.credihub.model.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_products")
public class LoanProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, unique = true)
    private String name;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private BigDecimal minAmount;
    @Column(nullable = false)
    private BigDecimal maxAmount;
    @Column(nullable = false)
    private Integer minPeriodMonths;
    @Column(nullable = false)
    private Integer maxPeriodMonths;
    @Column(nullable = false)
    private BigDecimal baseInterestRate;
    @Column(nullable = false)
    private BigDecimal monthlyInterestIncrease;
    @Column(nullable = false)
    private boolean active;
}
