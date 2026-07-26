package bg.credihub.scheduler;

import bg.credihub.model.enums.ApplicationStatus;
import bg.credihub.repository.LoanApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LoanApplicationScheduler {
    private final LoanApplicationRepository loanApplicationRepository;

    public LoanApplicationScheduler(LoanApplicationRepository loanApplicationRepository) {
        this.loanApplicationRepository = loanApplicationRepository;
    }

    @Scheduled(fixedRate = 3600000)
    public void logPendingLoanApplications() {
      long pendingApplications = loanApplicationRepository.countByStatus(ApplicationStatus.PENDING);
      log.info("Current pending applications: {}", pendingApplications);
    }
}
