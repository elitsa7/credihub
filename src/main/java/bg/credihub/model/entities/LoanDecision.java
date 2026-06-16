package bg.credihub.model.entities;

import bg.credihub.model.enums.DecisionStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loan_decisions")
public class LoanDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;
    @ManyToOne
    @JoinColumn(name = "admin_id", nullable = false)
    private User admin;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisionStatus status;
    @Column(nullable = false)
    private String comment;
    @Column(nullable = false)
    private LocalDateTime createdAt;
}
