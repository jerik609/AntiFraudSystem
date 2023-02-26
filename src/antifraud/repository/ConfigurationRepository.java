package antifraud.repository;

import antifraud.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    Optional<Configuration> findByKey(String key);

}
