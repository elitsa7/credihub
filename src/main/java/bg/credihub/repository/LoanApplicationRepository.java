package bg.credihub.repository;

import bg.credihub.model.entities.LoanApplication;
import bg.credihub.model.entities.LoanProduct;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanApplicationRepository extends JpaRepository<LoanApplication, UUID> {

    boolean existsByUserAndLoanProductAndStatus(User user, LoanProduct loanProduct, ApplicationStatus applicationStatus);

    List<LoanApplication> findAllByUser(User user);
}
