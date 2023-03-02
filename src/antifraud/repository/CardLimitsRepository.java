package antifraud.repository;

import antifraud.model.CardLimits;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CardLimitsRepository extends JpaRepository<CardLimits, Long> {

    Optional<CardLimits> getByNumber(String number);

}
