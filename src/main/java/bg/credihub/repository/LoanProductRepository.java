package bg.credihub.repository;

import bg.credihub.model.entities.LoanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LoanProductRepository extends JpaRepository<LoanProduct, UUID> {
    boolean existsByName(String name);

    List<LoanProduct> findAllByActiveTrue();
}
