package bg.credihub.mapper;

import bg.credihub.model.dtos.application.AdminLoanApplicationViewDTO;
import bg.credihub.model.dtos.application.LoanApplicationDTO;
import bg.credihub.model.dtos.application.LoanApplicationViewDTO;
import bg.credihub.model.dtos.calculator.LoanCalculatorDTO;
import bg.credihub.model.dtos.payment.LoanAccountRequest;
import bg.credihub.model.entities.LoanApplication;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class LoanApplicationMapper {

    public LoanApplicationDTO toEditDto(LoanApplication application) {
        return new LoanApplicationDTO(
                application.getLoanProduct().getId(),
                application.getRequestedAmount(),
                application.getPeriodMonths(),
                application.getMonthlyIncome(),
                application.getLoanPurpose()
        );
    }

    public LoanApplicationViewDTO toViewDto(LoanApplication application) {
        return new LoanApplicationViewDTO(
                application.getId(),
                application.getLoanProduct().getName(),
                application.getRequestedAmount(),
                application.getPeriodMonths(),
                application.getMonthlyIncome(),
                application.getLoanPurpose(),
                application.getStatus(),
                application.getInterestRate(),
                application.getMonthlyPayment(),
                application.getTotalRepaymentAmount(),
                application.getCreatedAt()
        );
    }

    public AdminLoanApplicationViewDTO toAdminViewDto(LoanApplication application) {
        return new AdminLoanApplicationViewDTO(
                application.getId(),
                application.getUser().getFirstName() + " " + application.getUser().getLastName(),
                application.getUser().getEmail(),
                application.getUser().getIdentificationNumber(),
                application.getLoanProduct().getName(),
                application.getRequestedAmount(),
                application.getPeriodMonths(),
                application.getMonthlyIncome(),
                application.getStatus(),
                application.getCreatedAt()
        );
    }

    public LoanCalculatorDTO toCalculatorDto(LoanApplication application) {
        LoanCalculatorDTO dto = new LoanCalculatorDTO();
        dto.setRequestedAmount(application.getRequestedAmount());
        dto.setPeriodMonths(application.getPeriodMonths());
        dto.setInterestRate(application.getInterestRate());
        dto.setMonthlyPayment(application.getMonthlyPayment());
        dto.setTotalRepaymentAmount(application.getTotalRepaymentAmount());
        return dto;
    }

    public LoanAccountRequest toLoanAccountRequest(LoanApplication application) {
        return LoanAccountRequest.builder()
                .applicationId(application.getId())
                .userId(application.getUser().getId())
                .principalAmount(application.getRequestedAmount())
                .annualInterestRate(application.getInterestRate())
                .monthlyPayment(application.getMonthlyPayment())
                .periodMonths(application.getPeriodMonths())
                .startDate(LocalDate.now())
                .build();
    }
}
