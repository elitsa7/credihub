package bg.credihub.repository;

import bg.credihub.model.entities.LoanApplication;
import bg.credihub.model.entities.LoanDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LoanDecisionRepository extends JpaRepository<LoanDecision, UUID> {
    boolean existsByLoanApplication(LoanApplication loanApplication);

    Optional<LoanDecision> findByLoanApplicationId(UUID applicationId);
}
