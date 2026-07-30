package bg.credihub;

import bg.credihub.client.PaymentClient;
import bg.credihub.exception.InvalidLoanApplicationException;
import bg.credihub.exception.InvalidLoanProductException;
import bg.credihub.exception.LoanApplicationNotFoundException;
import bg.credihub.exception.UnauthorizedActionException;
import bg.credihub.mapper.LoanApplicationMapper;
import bg.credihub.model.dtos.application.LoanApplicationDTO;
import bg.credihub.model.dtos.application.LoanApplicationViewDTO;
import bg.credihub.model.dtos.calculator.LoanCalculatorDTO;
import bg.credihub.model.entities.LoanApplication;
import bg.credihub.model.entities.LoanProduct;
import bg.credihub.model.entities.User;
import bg.credihub.model.enums.ApplicationStatus;
import bg.credihub.model.enums.LoanPurpose;
import bg.credihub.repository.LoanApplicationRepository;
import bg.credihub.service.LoanApplicationService;
import bg.credihub.service.LoanProductService;
import bg.credihub.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanApplicationServiceTest {
    @Mock
    private LoanApplicationRepository loanApplicationRepository;
    @Mock
    private UserService userService;
    @Mock
    private LoanProductService loanProductService;
    @Mock
    private PaymentClient paymentClient;

    private LoanApplicationMapper loanApplicationMapper;

    private LoanApplicationService loanApplicationService;

    @BeforeEach
    void setUp() {
        loanApplicationMapper = new LoanApplicationMapper();

        loanApplicationService = new LoanApplicationService(loanApplicationRepository, userService,
                loanProductService, loanApplicationMapper, paymentClient);
    }

    //Create
    @Test
    void shouldCreateLoanApplicationSuccessfully() {
        User user = createUser();
        LoanProduct loanProduct = createLoanProduct();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanProduct.getId());

        when(userService.getById(user.getId())).thenReturn(user);
        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);
        when(loanApplicationRepository.existsByUserAndLoanProductAndStatus(
                user, loanProduct, ApplicationStatus.PENDING)).thenReturn(false);

        loanApplicationService.createLoanApplication(user.getId(), loanApplicationDTO);

        verify(loanApplicationRepository).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenPendingApplicationExists() {
        User user = createUser();
        LoanProduct loanProduct = createLoanProduct();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanProduct.getId());

        when(userService.getById(user.getId())).thenReturn(user);
        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);
        when(loanApplicationRepository.existsByUserAndLoanProductAndStatus(
                user, loanProduct, ApplicationStatus.PENDING)).thenReturn(true);

        assertThrows(
                InvalidLoanApplicationException.class,
                () -> loanApplicationService.createLoanApplication(user.getId(), loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));

    }

    @Test
    void shouldThrowExceptionWhenLoanProductIsInactive() {
        User user = createUser();
        LoanProduct loanProduct = createLoanProduct();
        loanProduct.setActive(false);

        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanProduct.getId());

        when(userService.getById(user.getId())).thenReturn(user);
        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);
        when(loanApplicationRepository.existsByUserAndLoanProductAndStatus(
                user, loanProduct, ApplicationStatus.PENDING)).thenReturn(false);

        assertThrows(InvalidLoanProductException.class,
                () -> loanApplicationService.createLoanApplication(user.getId(), loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenRequestedAmountIsBelowMinimum() {
        User user = createUser();
        LoanProduct loanProduct = createLoanProduct();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanProduct.getId());
        loanApplicationDTO.setRequestedAmount(BigDecimal.valueOf(500));

        when(userService.getById(user.getId())).thenReturn(user);
        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);
        when(loanApplicationRepository.existsByUserAndLoanProductAndStatus(
                user, loanProduct, ApplicationStatus.PENDING)).thenReturn(false);

        assertThrows(InvalidLoanApplicationException.class,
                () -> loanApplicationService.createLoanApplication(user.getId(), loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenPeriodIsBelowMinimum() {
        User user = createUser();
        LoanProduct loanProduct = createLoanProduct();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanProduct.getId());
        loanApplicationDTO.setPeriodMonths(3);

        when(userService.getById(user.getId())).thenReturn(user);
        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);
        when(loanApplicationRepository.existsByUserAndLoanProductAndStatus(
                user, loanProduct, ApplicationStatus.PENDING)).thenReturn(false);

        assertThrows(InvalidLoanApplicationException.class,
                () -> loanApplicationService.createLoanApplication(user.getId(), loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenMonthlyIncomeIsBelowMinimum() {
        User user = createUser();
        LoanProduct loanProduct = createLoanProduct();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanProduct.getId());
        loanApplicationDTO.setMonthlyIncome(BigDecimal.valueOf(500));

        when(userService.getById(user.getId())).thenReturn(user);
        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);
        when(loanApplicationRepository.existsByUserAndLoanProductAndStatus(
                user, loanProduct, ApplicationStatus.PENDING)).thenReturn(false);

        assertThrows(InvalidLoanApplicationException.class,
                () -> loanApplicationService.createLoanApplication(user.getId(), loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    //Update
    @Test
    void shouldUpdateLoanApplicationSuccessfully() {
        LoanApplication loanApplication = createLoanApplication();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanApplication.getLoanProduct().getId());

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));
        when(loanProductService.getById(loanApplication.getLoanProduct().getId())).thenReturn(loanApplication.getLoanProduct());

        loanApplicationService.updateLoanApplication(loanApplication.getId(),
                loanApplication.getUser().getId(), loanApplicationDTO);

        verify(loanApplicationRepository).save(any(LoanApplication.class));

        assertEquals(loanApplicationDTO.getRequestedAmount(), loanApplication.getRequestedAmount());
        assertEquals(loanApplicationDTO.getPeriodMonths(), loanApplication.getPeriodMonths());
        assertEquals(loanApplicationDTO.getMonthlyIncome(), loanApplication.getMonthlyIncome());
        assertEquals(loanApplicationDTO.getPurpose(), loanApplication.getLoanPurpose());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        LoanApplication loanApplication = createLoanApplication();
        UUID anotherUserId = UUID.randomUUID();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanApplication.getLoanProduct().getId());

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(UnauthorizedActionException.class,
                () -> loanApplicationService.updateLoanApplication(loanApplication.getId(),
                        anotherUserId, loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonPendingLoanApplication() {
        LoanApplication loanApplication = createLoanApplication();
        loanApplication.setStatus(ApplicationStatus.APPROVED);
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(loanApplication.getLoanProduct().getId());

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(InvalidLoanApplicationException.class,
                () -> loanApplicationService.updateLoanApplication(loanApplication.getId(),
                        loanApplication.getUser().getId(), loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionWhenLoanApplicationNotFound() {
        UUID id = UUID.randomUUID();
        LoanApplicationDTO loanApplicationDTO = createLoanApplicationDTO(createLoanProduct().getId());

        when(loanApplicationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LoanApplicationNotFoundException.class,
                () -> loanApplicationService.updateLoanApplication(id, UUID.randomUUID(), loanApplicationDTO));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    //Approve
    @Test
    void shouldApproveLoanApplicationSuccessfully() {
        LoanApplication loanApplication = createLoanApplication();

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        loanApplicationService.approve(loanApplication.getId());

        verify(loanApplicationRepository).save(loanApplication);
        verify(paymentClient).createLoanAccount(any());

        assertEquals(ApplicationStatus.APPROVED, loanApplication.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenApprovingNonPendingLoanApplication() {
        LoanApplication loanApplication = createLoanApplication();
        loanApplication.setStatus(ApplicationStatus.APPROVED);

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(InvalidLoanApplicationException.class, () -> loanApplicationService.approve(loanApplication.getId()));

        verify(loanApplicationRepository, never()).save(any());
        verify(paymentClient, never()).createLoanAccount(any());
    }

    @Test
    void shouldThrowExceptionWhenApprovingLoanApplicationDoesNotExists() {
        LoanApplication loanApplication = createLoanApplication();

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.empty());

        assertThrows(LoanApplicationNotFoundException.class, () -> loanApplicationService.approve(loanApplication.getId()));

        verify(paymentClient, never()).createLoanAccount(any());
        verify(loanApplicationRepository, never()).save(any());
    }

    //Reject
    @Test
    void shouldRejectLoanApplicationSuccessfully() {
        LoanApplication loanApplication = createLoanApplication();

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        loanApplicationService.reject(loanApplication.getId());

        verify(loanApplicationRepository).save(loanApplication);

        assertEquals(ApplicationStatus.REJECTED, loanApplication.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenRejectingNonExistingLoanApplication() {
        UUID id = UUID.randomUUID();

        when(loanApplicationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LoanApplicationNotFoundException.class, () -> loanApplicationService.reject(id));

        verify(loanApplicationRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRejectingNonPendingLoanApplication() {
        LoanApplication loanApplication = createLoanApplication();
        loanApplication.setStatus(ApplicationStatus.REJECTED);

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(InvalidLoanApplicationException.class, () -> loanApplicationService.reject(loanApplication.getId()));

        verify(loanApplicationRepository, never()).save(any());
    }

    //Cancel
    @Test
    void shouldCancelLoanApplicationSuccessfully() {
        LoanApplication loanApplication = createLoanApplication();

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        loanApplicationService.cancelLoanApplication(loanApplication.getId(), loanApplication.getUser().getId());

        verify(loanApplicationRepository).save(loanApplication);
        assertEquals(ApplicationStatus.CANCELLED, loanApplication.getStatus());
    }

    @Test
    void shouldThrowExceptionWhenCancelingLoanApplicationOfAnotherUser() {
        LoanApplication loanApplication = createLoanApplication();
        UUID anotherUserId = UUID.randomUUID();

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(UnauthorizedActionException.class,
                () -> loanApplicationService.cancelLoanApplication(loanApplication.getId(), anotherUserId));

        verify(loanApplicationRepository, never()).save(any(LoanApplication.class));
    }

    @Test
    void shouldThrowExceptionCancelingNonPendingLoanApplication() {
        LoanApplication loanApplication = createLoanApplication();
        loanApplication.setStatus(ApplicationStatus.APPROVED);

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(InvalidLoanApplicationException.class,
                () -> loanApplicationService.cancelLoanApplication
                        (loanApplication.getId(), loanApplication.getUser().getId()));

        verify(loanApplicationRepository, never()).save(any());
    }

    //ApplicationDetails
    @Test
    void shouldGetApplicationDetailsSuccessfully() {
        LoanApplication loanApplication = createLoanApplication();
        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        LoanApplicationViewDTO loanApplicationViewDTO = loanApplicationService.getApplicationDetails
                (loanApplication.getId(), loanApplication.getUser().getId());

        assertNotNull(loanApplicationViewDTO);
    }

    @Test
    void shouldThrowExceptionWhenGettingApplicationDetailsOfAnotherUser() {
        LoanApplication loanApplication = createLoanApplication();
        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(UnauthorizedActionException.class,
                () -> loanApplicationService.getApplicationDetails(loanApplication.getId(), UUID.randomUUID()));
    }

    //ApplicationForEdit
    @Test
    void shouldReturnLoanApplicationForEditSuccessfully() {
        LoanApplication loanApplication = createLoanApplication();
        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        LoanApplicationDTO loanApplicationDTO = loanApplicationService.getApplicationForEdit
                (loanApplication.getId(), loanApplication.getUser().getId());

        assertNotNull(loanApplicationDTO);
    }

    @Test
    void shouldThrowExceptionWhenGettingLoanApplicationForEditOfAnotherUser() {
        LoanApplication loanApplication = createLoanApplication();
        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(UnauthorizedActionException.class,
                () -> loanApplicationService.getApplicationForEdit(loanApplication.getId(), UUID.randomUUID()));
    }

    @Test
    void shouldThrowExceptionWhenGettingApplicationForEditIfNotPending() {
        LoanApplication loanApplication = createLoanApplication();
        loanApplication.setStatus(ApplicationStatus.APPROVED);

        when(loanApplicationRepository.findById(loanApplication.getId())).thenReturn(Optional.of(loanApplication));

        assertThrows(InvalidLoanApplicationException.class,
                () -> loanApplicationService.getApplicationForEdit(loanApplication.getId(), loanApplication.getUser().getId()));
    }

    //Calculate
    @Test
    void shouldCalculateLoanSuccessfully() {
        LoanProduct loanProduct = createLoanProduct();
        LoanCalculatorDTO loanCalculatorDTO = createLoanCalculatorDTO(loanProduct.getId());

        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);

        LoanCalculatorDTO result = loanApplicationService.calculate(loanCalculatorDTO);
        assertNotNull(result);
        assertNotNull(result.getInterestRate());
        assertNotNull(result.getMonthlyPayment());
        assertNotNull(result.getTotalRepaymentAmount());
    }

    @Test
    void shouldThrowExceptionWhenCalculatingWithInactiveLoanProduct() {
        LoanProduct loanProduct = createLoanProduct();
        loanProduct.setActive(false);

        LoanCalculatorDTO loanCalculatorDTO = createLoanCalculatorDTO(loanProduct.getId());

        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);

        assertThrows(InvalidLoanProductException.class, () -> loanApplicationService.calculate(loanCalculatorDTO));
    }

    @Test
    void shouldThrowExceptionWhenCalculatingWithInvalidAmount() {
        LoanProduct loanProduct = createLoanProduct();

        LoanCalculatorDTO loanCalculatorDTO = createLoanCalculatorDTO(loanProduct.getId());
        loanCalculatorDTO.setRequestedAmount(BigDecimal.valueOf(500));

        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);

        assertThrows(InvalidLoanApplicationException.class, () -> loanApplicationService.calculate(loanCalculatorDTO));
    }

    @Test
    void shouldThrowExceptionWhenCalculatingWithInvalidPeriod() {
        LoanProduct loanProduct = createLoanProduct();

        LoanCalculatorDTO loanCalculatorDTO = createLoanCalculatorDTO(loanProduct.getId());
        loanCalculatorDTO.setPeriodMonths(3);

        when(loanProductService.getById(loanProduct.getId())).thenReturn(loanProduct);

        assertThrows(InvalidLoanApplicationException.class, () -> loanApplicationService.calculate(loanCalculatorDTO));
    }

    private User createUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        return user;
    }

    private LoanProduct createLoanProduct() {
        LoanProduct product = new LoanProduct();
        product.setId(UUID.randomUUID());
        product.setActive(true);
        product.setMinAmount(BigDecimal.valueOf(1000));
        product.setMaxAmount(BigDecimal.valueOf(10000));
        product.setMinPeriodMonths(6);
        product.setMaxPeriodMonths(60);
        product.setMinimumIncome(BigDecimal.valueOf(1000));
        product.setBaseInterestRate(BigDecimal.valueOf(5));
        product.setMonthlyInterestIncrease(BigDecimal.valueOf(0.2));

        return product;
    }

    private LoanApplicationDTO createLoanApplicationDTO(UUID loanProductId) {
        LoanApplicationDTO dto = new LoanApplicationDTO();
        dto.setLoanProductId(loanProductId);
        dto.setRequestedAmount(BigDecimal.valueOf(5000));
        dto.setPeriodMonths(24);
        dto.setMonthlyIncome(BigDecimal.valueOf(2500));
        dto.setPurpose(LoanPurpose.REPAYMENT_OF_ANOTHER_OBLIGATION);

        return dto;
    }

    private LoanApplication createLoanApplication() {
        LoanApplication loanApplication = new LoanApplication();
        loanApplication.setId(UUID.randomUUID());
        loanApplication.setStatus(ApplicationStatus.PENDING);
        loanApplication.setRequestedAmount(BigDecimal.valueOf(5000));
        loanApplication.setPeriodMonths(24);
        loanApplication.setMonthlyIncome(BigDecimal.valueOf(2500));
        loanApplication.setInterestRate(BigDecimal.valueOf(9.8));
        loanApplication.setMonthlyPayment(BigDecimal.valueOf(228.75));
        loanApplication.setTotalRepaymentAmount(BigDecimal.valueOf(5490));
        loanApplication.setLoanPurpose(LoanPurpose.REPAYMENT_OF_ANOTHER_OBLIGATION);

        User user = createUser();
        LoanProduct product = createLoanProduct();

        loanApplication.setUser(user);
        loanApplication.setLoanProduct(product);

        return loanApplication;
    }

    private LoanCalculatorDTO createLoanCalculatorDTO(UUID loanProductId) {
        LoanCalculatorDTO loanCalculatorDTO = new LoanCalculatorDTO();
        loanCalculatorDTO.setLoanProductId(loanProductId);
        loanCalculatorDTO.setRequestedAmount(BigDecimal.valueOf(5000));
        loanCalculatorDTO.setPeriodMonths(24);
        return loanCalculatorDTO;
    }
}
