package antifraud.repository;

import antifraud.enums.TransactionValidationResult;
import antifraud.model.LimitsConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LimitsConfigRepository extends JpaRepository<LimitsConfig, Long> {

    Optional<LimitsConfig> findByValidityAndFeedback(TransactionValidationResult validity, TransactionValidationResult feedback);

}
