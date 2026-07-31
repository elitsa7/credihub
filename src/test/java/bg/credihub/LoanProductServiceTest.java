package bg.credihub;

import bg.credihub.exception.LoanProductNotFoundException;
import bg.credihub.mapper.LoanProductMapper;
import bg.credihub.model.dtos.product.LoanProductDTO;
import bg.credihub.model.dtos.product.LoanProductViewDTO;
import bg.credihub.model.entities.LoanProduct;
import bg.credihub.repository.LoanProductRepository;
import bg.credihub.service.LoanProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanProductServiceTest {
    @Mock
    private LoanProductRepository loanProductRepository;

    @Mock
    private LoanProductMapper loanProductMapper;

    @Mock
    private ModelMapper modelMapper;

    private LoanProductService loanProductService;

    @BeforeEach
    public void setUp() {
        loanProductService = new LoanProductService(loanProductRepository, loanProductMapper, modelMapper);
    }

    @Test
    void shouldUpdateLoanProductSuccessfully() {
        LoanProduct loanProduct = createLoanProduct();
        LoanProductDTO loanProductDTO = createLoanProductDTO();

        when(loanProductRepository.findById(loanProduct.getId())).thenReturn(Optional.of(loanProduct));

        loanProductService.update(loanProduct.getId(), loanProductDTO);

        verify(modelMapper).map(loanProductDTO, loanProduct);
        verify(loanProductRepository).save(loanProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingLoanProduct() {
        UUID id = UUID.randomUUID();
        LoanProductDTO loanProductDTO = createLoanProductDTO();

        when(loanProductRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LoanProductNotFoundException.class,
                () -> loanProductService.update(id, loanProductDTO));

        verify(modelMapper, never()).map(any(), any());
        verify(loanProductRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllLoanProductsSuccessfully() {
        LoanProduct loanProduct1 = createLoanProduct();
        LoanProduct loanProduct2 = createLoanProduct();

        when(loanProductRepository.findAll()).thenReturn(List.of(loanProduct1, loanProduct2));
        when(loanProductMapper.toViewDto(any(LoanProduct.class))).thenReturn(new LoanProductViewDTO());

        List<LoanProductViewDTO> products = loanProductService.getAllView();

        assertEquals(2, products.size());
        verify(loanProductRepository).findAll();
    }

    @Test
    void shouldReturnEmptyLoanProductsList() {
        when(loanProductRepository.findAll()).thenReturn(Collections.emptyList());

        List<LoanProductViewDTO> products = loanProductService.getAllView();

        assertTrue(products.isEmpty());
        verify(loanProductRepository).findAll();
    }

    @Test
    void shouldReturnLoanProductEditDTOSuccessfully() {
        LoanProduct loanProduct = createLoanProduct();
        LoanProductDTO loanProductDTO = createLoanProductDTO();

        when(loanProductRepository.findById(loanProduct.getId())).thenReturn(Optional.of(loanProduct));
        when(loanProductMapper.toEditDto(loanProduct)).thenReturn(loanProductDTO);

        LoanProductDTO result = loanProductService.getEditDto(loanProduct.getId());
        assertEquals(loanProductDTO, result);
        verify(loanProductMapper).toEditDto(loanProduct);
    }

    @Test
    void shouldThrowExceptionWhenLoanProductForEditNotFound() {
        UUID id = UUID.randomUUID();

        when(loanProductRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LoanProductNotFoundException.class, () -> loanProductService.getEditDto(id));

        verify(loanProductMapper, never()).toEditDto(any());
    }

    @Test
    void shouldReturnLoanProductById() {
        LoanProduct loanProduct = createLoanProduct();

        when(loanProductRepository.findById(loanProduct.getId())).thenReturn(Optional.of(loanProduct));

        LoanProduct result = loanProductService.getById(loanProduct.getId());

        assertEquals(loanProduct, result);
    }

    @Test
    void shouldThrowExceptionWhenLoanProductNotFound() {
        UUID id = UUID.randomUUID();

        when(loanProductRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(LoanProductNotFoundException.class, () -> loanProductService.getById(id));
    }

    private LoanProduct createLoanProduct() {
        LoanProduct loanProduct = new LoanProduct();
        loanProduct.setId(UUID.randomUUID());
        loanProduct.setName("Old Product");
        loanProduct.setDescription("Old Description");
        loanProduct.setActive(true);
        loanProduct.setMinAmount(BigDecimal.valueOf(1000));
        loanProduct.setMaxAmount(BigDecimal.valueOf(10000));
        loanProduct.setMinPeriodMonths(6);
        loanProduct.setMaxPeriodMonths(60);
        loanProduct.setMinimumIncome(BigDecimal.valueOf(1000));
        loanProduct.setBaseInterestRate(BigDecimal.valueOf(5));
        loanProduct.setMonthlyInterestIncrease(BigDecimal.valueOf(0.2));

        return loanProduct;
    }

    private LoanProductDTO createLoanProductDTO() {
        LoanProductDTO loanProductDTO = new LoanProductDTO();
        loanProductDTO.setName("Personal Loan");
        loanProductDTO.setDescription("Personal loan description");
        loanProductDTO.setMinAmount(BigDecimal.valueOf(1000));
        loanProductDTO.setMaxAmount(BigDecimal.valueOf(10000));
        loanProductDTO.setMinPeriodMonths(6);
        loanProductDTO.setMaxPeriodMonths(60);
        loanProductDTO.setBaseInterestRate(BigDecimal.valueOf(5));
        loanProductDTO.setMonthlyInterestIncrease(BigDecimal.valueOf(0.2));
        loanProductDTO.setActive(true);

        return loanProductDTO;
    }

}
