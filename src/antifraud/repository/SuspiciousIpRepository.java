package antifraud.repository;

import antifraud.model.SuspiciousIp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SuspiciousIpRepository extends JpaRepository<SuspiciousIp, Long> {

}
