package bg.credihub.config;

import bg.credihub.model.entities.LoanProduct;
import bg.credihub.repository.LoanProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class LoanProductInitConfig {

    @Bean
    public CommandLineRunner productInit(LoanProductRepository loanProductRepository) {
        System.out.println("Loading loan products...");
        return args -> {
            if (loanProductRepository.count() > 0) {
                return;
            }

            LoanProduct personalLoan = new LoanProduct();
            personalLoan.setName("Personal Loan");
            personalLoan.setDescription("Personal loan for everyday needs");
            personalLoan.setMinAmount(BigDecimal.valueOf(1000));
            personalLoan.setMaxAmount(BigDecimal.valueOf(50000));
            personalLoan.setMinPeriodMonths(6);
            personalLoan.setMaxPeriodMonths(60);
            personalLoan.setBaseInterestRate(BigDecimal.valueOf(5));
            personalLoan.setMonthlyInterestIncrease(BigDecimal.valueOf(0.1));
            personalLoan.setActive(true);

            LoanProduct businessLoan = new LoanProduct();
            businessLoan.setName("Business Loan");
            businessLoan.setDescription("Financing for business growth");
            businessLoan.setMinAmount(BigDecimal.valueOf(5000));
            businessLoan.setMaxAmount(BigDecimal.valueOf(100000));
            businessLoan.setMinPeriodMonths(12);
            businessLoan.setMaxPeriodMonths(120);
            businessLoan.setBaseInterestRate(BigDecimal.valueOf(4));
            businessLoan.setMonthlyInterestIncrease(BigDecimal.valueOf(0.08));
            businessLoan.setActive(true);

            loanProductRepository.save(personalLoan);
            loanProductRepository.save(businessLoan);
        };
    }
}
