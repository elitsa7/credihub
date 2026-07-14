package bg.credihub.service;

import bg.credihub.client.PaymentClient;
import bg.credihub.exception.InvalidLoanApplicationException;
import bg.credihub.exception.InvalidLoanProductException;
import bg.credihub.exception.LoanApplicationNotFoundException;
import bg.credihub.exception.UnauthorizedActionException;
import bg.credihub.mapper.LoanApplicationMapper;
import bg.credihub.model.dtos.application.AdminLoanApplicationViewDTO;
import bg.credihub.model.dtos.application.LoanApplicationDTO;
import bg.credihub.model.dtos.application.LoanApplicationViewDTO;
import bg.credihub.model.dtos.calculator.LoanCalculatorDTO;
import bg.credihub.model.entities.LoanApplication;
import bg.credihub.model.entities.LoanProduct;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.ApplicationStatus;
import bg.credihub.repository.LoanApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class LoanApplicationService {
    private final LoanApplicationRepository loanApplicationRepository;
    private final UserService userService;
    private final LoanProductService loanProductService;
    private final LoanApplicationMapper loanApplicationMapper;
    private final PaymentClient paymentClient;

    @Autowired
    public LoanApplicationService(LoanApplicationRepository loanApplicationRepository,
                                  UserService userService,
                                  LoanProductService loanProductService,
                                  LoanApplicationMapper loanApplicationMapper,
                                  PaymentClient paymentClient) {
        this.loanApplicationRepository = loanApplicationRepository;
        this.userService = userService;
        this.loanProductService = loanProductService;
        this.loanApplicationMapper = loanApplicationMapper;
        this.paymentClient = paymentClient;
    }

    public void createLoanApplication(UUID userId, LoanApplicationDTO loanApplicationDTO) {
        User user = userService.getById(userId);
        LoanProduct loanProduct = loanProductService.getById(loanApplicationDTO.getLoanProductId());

        if (loanApplicationRepository.existsByUserAndLoanProductAndStatus(user, loanProduct, ApplicationStatus.PENDING)) {
            throw new InvalidLoanApplicationException("You already have an application for this loan product");
        }

        validateLoanProductIsActive(loanProduct);
        validateAmountAndPeriod(loanApplicationDTO.getRequestedAmount(), loanApplicationDTO.getPeriodMonths(), loanProduct);
        validateMonthlyIncome(loanApplicationDTO, loanProduct);

        LoanApplication loanApplication = new LoanApplication();

        loanApplication.setRequestedAmount(loanApplicationDTO.getRequestedAmount());
        loanApplication.setPeriodMonths(loanApplicationDTO.getPeriodMonths());
        loanApplication.setMonthlyIncome(loanApplicationDTO.getMonthlyIncome());
        loanApplication.setLoanPurpose(loanApplicationDTO.getPurpose());
        loanApplication.setLoanProduct(loanProduct);
        loanApplication.setUser(user);
        loanApplication.setStatus(ApplicationStatus.PENDING);
        loanApplication.setCreatedAt(LocalDateTime.now());
        applyCalculatedValues(loanApplication, loanProduct);

        loanApplicationRepository.save(loanApplication);
    }

    public void updateLoanApplication(UUID id, UUID userId, LoanApplicationDTO loanApplicationDTO) {
        LoanApplication loanApplication = getById(id);
        validateOwner(loanApplication, userId);
        validatePendingStatus(loanApplication);

        LoanProduct loanProduct = loanProductService.getById(loanApplicationDTO.getLoanProductId());
        validateLoanProductIsActive(loanProduct);
        validateAmountAndPeriod(loanApplicationDTO.getRequestedAmount(), loanApplicationDTO.getPeriodMonths(), loanProduct);
        validateMonthlyIncome(loanApplicationDTO, loanProduct);

        loanApplication.setRequestedAmount(loanApplicationDTO.getRequestedAmount());
        loanApplication.setPeriodMonths(loanApplicationDTO.getPeriodMonths());
        loanApplication.setMonthlyIncome(loanApplicationDTO.getMonthlyIncome());
        loanApplication.setLoanPurpose(loanApplicationDTO.getPurpose());
        loanApplication.setLoanProduct(loanProduct);

        applyCalculatedValues(loanApplication, loanProduct);

        loanApplicationRepository.save(loanApplication);
    }

    public void cancelLoanApplication(UUID applicationId, UUID userId) {
        LoanApplication loanApplication = getById(applicationId);

        validateOwner(loanApplication, userId);
        validatePendingStatus(loanApplication);

        loanApplication.setStatus(ApplicationStatus.CANCELLED);
        loanApplicationRepository.save(loanApplication);
    }

    public LoanApplicationViewDTO getApplicationDetails(UUID applicationId, UUID userId) {
        LoanApplication loanApplication = getById(applicationId);
        validateOwner(loanApplication, userId);
        return loanApplicationMapper.toViewDto(loanApplication);
    }

    public LoanApplicationDTO getApplicationForEdit(UUID applicationId, UUID userId) {
        LoanApplication loanApplication = getById(applicationId);
        validateOwner(loanApplication, userId);
        validatePendingStatus(loanApplication);
        return loanApplicationMapper.toEditDto(loanApplication);
    }

    public LoanCalculatorDTO calculate(LoanCalculatorDTO loanCalculatorDTO) {
        LoanProduct loanProduct = loanProductService.getById(loanCalculatorDTO.getLoanProductId());

        validateLoanProductIsActive(loanProduct);
        validateAmountAndPeriod(loanCalculatorDTO.getRequestedAmount(), loanCalculatorDTO.getPeriodMonths(), loanProduct);

        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setRequestedAmount(loanCalculatorDTO.getRequestedAmount());
        loanApplication.setPeriodMonths(loanCalculatorDTO.getPeriodMonths());

        applyCalculatedValues(loanApplication, loanProduct);

        return loanApplicationMapper.toCalculatorDto(loanApplication);
    }

    @Transactional
    public void approve(UUID id) {
        LoanApplication loanApplication = getById(id);
        validatePendingStatus(loanApplication);
        loanApplication.setStatus(ApplicationStatus.APPROVED);
        loanApplicationRepository.save(loanApplication);
        paymentClient.createLoanAccount(loanApplicationMapper.toLoanAccountRequest(loanApplication));
    }

    public void reject(UUID id) {
        LoanApplication loanApplication = getById(id);
        validatePendingStatus(loanApplication);
        loanApplication.setStatus(ApplicationStatus.REJECTED);
        loanApplicationRepository.save(loanApplication);
    }

    public LoanApplication getById(UUID id) {
        return loanApplicationRepository.findById(id)
                .orElseThrow(() -> new LoanApplicationNotFoundException("LoanApplication with id " + id + " not found."));
    }

    public List<LoanApplicationViewDTO> getAllByUser(UUID userId) {
        User user = userService.getById(userId);
        return loanApplicationRepository.findAllByUser(user)
                .stream()
                .map(loanApplicationMapper::toViewDto)
                .toList();
    }

    public List<AdminLoanApplicationViewDTO> getAllForAdmin() {
        return loanApplicationRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(loanApplicationMapper::toAdminViewDto)
                .toList();
    }

    private void validateLoanProductIsActive(LoanProduct loanProduct) {
        if (!loanProduct.isActive()) {
            throw new InvalidLoanProductException("Loan product is inactive.");
        }
    }

    private void validateMonthlyIncome(LoanApplicationDTO loanApplicationDTO, LoanProduct loanProduct) {
        if (loanApplicationDTO.getMonthlyIncome().compareTo(loanProduct.getMinimumIncome()) < 0) {
            throw new InvalidLoanApplicationException(
                    "Minimum monthly income for " + loanProduct.getName() + " is " + loanProduct.getMinimumIncome() + " EUR.");
        }
    }

    private void validateOwner(LoanApplication loanApplication, UUID userId) {
        if (!loanApplication.getUser().getId().equals(userId)) {
            throw new UnauthorizedActionException("You can update only your applications.");
        }
    }

    private void validatePendingStatus(LoanApplication loanApplication) {
        if (loanApplication.getStatus() != (ApplicationStatus.PENDING)) {
            throw new InvalidLoanApplicationException("Only pending applications can be modified.");
        }
    }

    private void validateAmountAndPeriod(BigDecimal requestedAmount, Integer periodMonths, LoanProduct loanProduct) {
        if (requestedAmount.compareTo(loanProduct.getMinAmount()) < 0
                || requestedAmount.compareTo(loanProduct.getMaxAmount()) > 0) {
            throw new InvalidLoanApplicationException(
                    "Amount must be between " + loanProduct.getMinAmount() + " and " + loanProduct.getMaxAmount() + " EUR.");
        }

        if (periodMonths < loanProduct.getMinPeriodMonths()
                || periodMonths > loanProduct.getMaxPeriodMonths()) {
            throw new InvalidLoanApplicationException(
                    "Period must be between " + loanProduct.getMinPeriodMonths() + " and " + loanProduct.getMaxPeriodMonths() + " months.");
        }
    }

    private void applyCalculatedValues(LoanApplication loanApplication, LoanProduct loanProduct) {
        BigDecimal interestRate = loanProduct.getBaseInterestRate()
                .add(loanProduct.getMonthlyInterestIncrease()
                        .multiply(BigDecimal.valueOf(loanApplication.getPeriodMonths())));

        BigDecimal interestAmount = loanApplication.getRequestedAmount()
                .multiply(interestRate)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal totalRepaymentAmount = loanApplication.getRequestedAmount().add(interestAmount);

        BigDecimal monthlyPayment = totalRepaymentAmount
                .divide(BigDecimal.valueOf(loanApplication.getPeriodMonths()), 2, RoundingMode.HALF_UP);

        loanApplication.setInterestRate(interestRate);
        loanApplication.setTotalRepaymentAmount(totalRepaymentAmount);
        loanApplication.setMonthlyPayment(monthlyPayment);
    }
}
