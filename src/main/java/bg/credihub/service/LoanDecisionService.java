package bg.credihub.service;

import bg.credihub.exception.InvalidLoanApplicationException;
import bg.credihub.exception.UnauthorizedActionException;
import bg.credihub.model.dtos.LoanDecisionDTO;
import bg.credihub.model.entities.LoanApplication;
import bg.credihub.model.entities.LoanDecision;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.ApplicationStatus;
import bg.credihub.model.enums.DecisionStatus;
import bg.credihub.model.enums.Role;
import bg.credihub.repository.LoanDecisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LoanDecisionService {
    private final LoanDecisionRepository loanDecisionRepository;
    private final UserService userService;
    private final LoanApplicationService loanApplicationService;
    private final LoanDecisionDTO loanDecisionDTO;

    @Autowired
    public LoanDecisionService(LoanDecisionRepository loanDecisionRepository, UserService userService, LoanApplicationService loanApplicationService, LoanDecisionDTO loanDecisionDTO) {
        this.loanDecisionRepository = loanDecisionRepository;
        this.userService = userService;
        this.loanApplicationService = loanApplicationService;
        this.loanDecisionDTO = loanDecisionDTO;
    }

    public void reviewApplication(UUID applicationId, UUID adminId, LoanDecisionDTO loanDecisionDTO) {
        User admin = userService.getById(adminId);
        validateAdmin(admin);

        LoanApplication loanApplication = loanApplicationService.getById(applicationId);

        validatePendingApplication(loanApplication);
        validateDecisionNotExists(loanApplication);

        LoanDecision loanDecision = new LoanDecision();
        loanDecision.setLoanApplication(loanApplication);
        loanDecision.setAdmin(admin);
        loanDecision.setStatus(loanDecisionDTO.getStatus());
        loanDecision.setComment(loanDecisionDTO.getComment());
        loanDecision.setCreatedAt(LocalDateTime.now());

        if (loanDecisionDTO.getStatus() == DecisionStatus.APPROVED) {
            loanApplication.setStatus(ApplicationStatus.APPROVED);
        } else {
            loanApplication.setStatus(ApplicationStatus.REJECTED);
        }
        loanDecisionRepository.save(loanDecision);
    }

    public LoanDecision getByApplicationId(UUID applicationId) {
        return loanDecisionRepository.findByLoanApplicationId(applicationId).orElse(null);
    }

    private void validatePendingApplication(LoanApplication loanApplication) {
        if (loanApplication.getStatus() != ApplicationStatus.PENDING) {
            throw new InvalidLoanApplicationException("Only pending application can be reviewed.");
        }
    }

    private void validateDecisionNotExists(LoanApplication loanApplication) {
        if (loanDecisionRepository.existsByLoanApplication(loanApplication)) {
            throw new InvalidLoanApplicationException("The application has already been reviewed.");
        }
    }

    private void validateAdmin(User user) {
        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedActionException("You are not allowed to approve this application.");
        }
    }
}
